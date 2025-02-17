package com.jcloisterzone.server;

import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.util.EnumSet;
import java.util.List;
import java.util.Random;

import org.ini4j.Ini;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jcloisterzone.Application;
import com.jcloisterzone.Expansion;
import com.jcloisterzone.Player;
import com.jcloisterzone.board.Location;
import com.jcloisterzone.board.Position;
import com.jcloisterzone.board.Rotation;
import com.jcloisterzone.figure.Follower;
import com.jcloisterzone.figure.Meeple;
import com.jcloisterzone.game.CustomRule;
import com.jcloisterzone.game.GameSettings;
import com.jcloisterzone.game.PlayerSlot;
import com.jcloisterzone.game.PlayerSlot.SlotType;
import com.jcloisterzone.game.Snapshot;
import com.jcloisterzone.rmi.ClientIF;
import com.jcloisterzone.rmi.ServerIF;
import com.jcloisterzone.rmi.mina.ServerStub;


public class Server extends GameSettings implements ServerIF {

	protected final transient Logger logger = LoggerFactory.getLogger(getClass());

	//TODO
//	/** maximum length of player's nick, longer nicks are trimmed */
//	public static int MAX_NICK_LENGTH = 16;

	//private final Ini config;

	private boolean gameStarted;

	protected final PlayerSlot[] slots;
	protected EnumSet<Expansion>[] slotSupportedExpansions;
	protected int slotSerial;
	private Snapshot snapshot;

	/** server stub for sending and receiving messages */
	private ClientIF stub;

	private Random random = new Random();


	@SuppressWarnings("unchecked")
	public Server(Ini config)  {
		slots = new PlayerSlot[PlayerSlot.COUNT];
		slotSupportedExpansions = new EnumSet[slots.length];
		for(int i = 0; i < slots.length; i++) {
			slots[i] = new PlayerSlot(i);
		}
		getExpansions().add(Expansion.BASIC);
		for(Expansion exp: Expansion.values()) {
			if (exp.isEnabled() && config.get("game-default-expansions", exp.name(), boolean.class)) {
				getExpansions().add(exp);
			}
		}
		for(CustomRule rule : CustomRule.values()) {
			if (config.get("game-default-rules", rule.name(), boolean.class)) {
				getCustomRules().add(rule);
			}
		}
	}

	@SuppressWarnings("unchecked")
	public Server(Snapshot snapshot) {
		List<Player> players = snapshot.getPlayers();
		slots = new PlayerSlot[players.size()];
		slotSupportedExpansions = new EnumSet[slots.length]; //in fact not used during load
		for(int i = 0; i < slots.length; i++) {
			PlayerSlot slot = new PlayerSlot(i);
			for(Player player : players) {
				if (player.getSlot().getNumber() == i) {
					slot.setNick(player.getNick());
					slot.setType(player.getSlot().getType());
					slot.setAiClassName(player.getSlot().getAiClassName());
					break;
				}
			}
			slots[i] = slot;
		}
		getExpansions().addAll(snapshot.getExpansions());
		getCustomRules().addAll(snapshot.getCustomRules());
		this.snapshot = snapshot;
	}

	public void engageSlots(long clientId) {
		for(int i = 0; i < slots.length; i++) {
			if (slots[i].getType() != SlotType.OPEN) {
				slots[i].setOwner(clientId);
			}
		}
	}

	public void start(int port) throws IOException {
		InvocationHandler handler = new ServerStub(this, port);
		stub = (ClientIF) Proxy.newProxyInstance(ClientIF.class.getClassLoader(), new Class[] { ClientIF.class }, handler);
	}

	public void stop() {
		((ServerStub)Proxy.getInvocationHandler(stub)).stop();
	}

	public PlayerSlot[] getSlots() {
		return slots;
	}

	public Snapshot getSnapshot() {
		return snapshot;
	}

	private EnumSet<Expansion> mergeSupportedExpansions() {
		EnumSet<Expansion> merged = null;
		for(int i = 0; i < slotSupportedExpansions.length; i++) {
			EnumSet<Expansion> supported = slotSupportedExpansions[i];
			if (supported == null) continue;
			if (merged == null) {
				merged = EnumSet.allOf(Expansion.class);
			}
			merged.retainAll(supported);
		}
		return merged;
	}

	@Override
	public void updateSlot(PlayerSlot slot, EnumSet<Expansion> supportedExpansions) {
		if (gameStarted) {
			logger.error(Application.ILLEGAL_STATE_MSG, "updateSlot");
			return;
		}
		if (snapshot == null) {
			//TODO check rights, maybe copy only
			if (slots[slot.getNumber()].getType() == SlotType.OPEN) { //old type
				slot.setSerial(++slotSerial);
			}
			if (slot.getType() == SlotType.OPEN) { //new type
				slot.setNick(null);
				slot.setSerial(null);
			}
			if (slot.getType() != SlotType.AI) { //new type
				slot.setAiClassName(null);
			}
		}
		slots[slot.getNumber()] = slot;
		slotSupportedExpansions[slot.getNumber()] = supportedExpansions;
		stub.updateSlot(slot);
		//TODO
		stub.updateSupportedExpansions(mergeSupportedExpansions());
	}

	@Override
	public void updateExpansion(Expansion expansion, Boolean enabled) {
		if (gameStarted || ! expansion.isEnabled()) {
			logger.error(Application.ILLEGAL_STATE_MSG, "updateExpansion");
			return;
		}
		if (enabled) {
			getExpansions().add(expansion);
		} else {
			getExpansions().remove(expansion);
		}
		//stub.updateGameSettings(slots, getExpansions(), getCustomRules());
		stub.updateExpansion(expansion, enabled);
	}

	@Override
	public void updateCustomRule(CustomRule rule, Boolean enabled) {
		if (gameStarted) {
			logger.error(Application.ILLEGAL_STATE_MSG, "updateCustomRule");
			return;
		}
		if (enabled) {
			getCustomRules().add(rule);
		} else {
			getCustomRules().remove(rule);
		}
		//stub.updateGameSettings(slots, getExpansions(), getCustomRules());
		stub.updateCustomRule(rule, enabled);
	}

	@Override
	public void startGame() {
		((ServerStub)Proxy.getInvocationHandler(stub)).closeAccepting();
		gameStarted = true;
		EnumSet<Expansion> supported = mergeSupportedExpansions();
		if (supported != null) {
			for(Expansion exp : Expansion.values()) {
				if (exp.isEnabled() && ! supported.contains(exp)) {
					stub.updateExpansion(exp, false);
				}
			}
		}
		stub.startGame();
	}

	@Override
	public void placeNoFigure() {
		stub.placeNoFigure();
	}

	@Override
	public void placeNoTile() {
		stub.placeNoTile();
	}

	@Override
	public void placeTile(Rotation tileRotation, Position tilePosition) {
		stub.placeTile(tileRotation, tilePosition);
	}

	@Override
	public void deployMeeple(Position p, Location loc, Class<? extends Meeple> meepleType) {
		stub.deployMeeple(p, loc, meepleType);

	}

	@Override
	public void moveDragon(Position p) {
		stub.moveDragon(p);
	}

	@Override
	public void payRansom(Integer playerIndexToPay, Class<? extends Follower> meepleType) {
		stub.payRansom(playerIndexToPay, meepleType);

	}

	@Override
	public void selectTile(Integer tiles) {
		//generate random tile
		stub.nextTile(random.nextInt(tiles));
	}

	@Override
	public void moveFairy(Position p) {
		stub.moveFairy(p);
	}

	@Override
	public void placeTowerPiece(Position p) {
		stub.placeTowerPiece(p);
	}

	@Override
	public void placeTunnelPiece(Position p, Location d, boolean isSecondPiece) {
		stub.placeTunnelPiece(p, d, isSecondPiece);
	}

	@Override
	public void captureFigure(Position p, Location d) {
		stub.captureFigure(p, d);

	}

	@Override
	public void escapeFromCity(Position p, Location d) {
		stub.escapeFromCity(p, d);
	}

	@Override
	public void removeKnightWithPrincess(Position p, Location d) {
		stub.removeKnightWithPrincess(p, d);
	}

}
