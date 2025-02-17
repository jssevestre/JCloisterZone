package com.jcloisterzone.event;

import java.util.EnumSet;
import java.util.EventListener;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.jcloisterzone.Expansion;
import com.jcloisterzone.Player;
import com.jcloisterzone.UserInterface;
import com.jcloisterzone.action.CaptureAction;
import com.jcloisterzone.action.PlayerAction;
import com.jcloisterzone.board.Location;
import com.jcloisterzone.board.Position;
import com.jcloisterzone.board.Rotation;
import com.jcloisterzone.board.Tile;
import com.jcloisterzone.feature.Completable;
import com.jcloisterzone.feature.Feature;
import com.jcloisterzone.feature.visitor.score.CompletableScoreContext;
import com.jcloisterzone.figure.Follower;
import com.jcloisterzone.figure.Meeple;
import com.jcloisterzone.game.CustomRule;
import com.jcloisterzone.game.PlayerSlot;
import com.jcloisterzone.game.Snapshot;


public class EventMulticaster implements GameEventListener, UserInterface {

	protected final EventListener a, b;

	protected EventMulticaster(EventListener a, EventListener b) {
		this.b = b;
		this.a = a;
	}

	public static EventListener addListener(EventListener a, EventListener b) {
		if (a == null)  return b;
		if (b == null)  return a;
		return new EventMulticaster(a, b);
	}

	@Override
	public void updateCustomRule(CustomRule rule, Boolean enabled) {
		((GameEventListener)a).updateCustomRule(rule, enabled);
		((GameEventListener)b).updateCustomRule(rule, enabled);

	}

	@Override
	public void updateExpansion(Expansion expansion, Boolean enabled) {
		((GameEventListener)a).updateExpansion(expansion, enabled);
		((GameEventListener)b).updateExpansion(expansion, enabled);
	}

	@Override
	public void updateSlot(PlayerSlot slot) {
		((GameEventListener)a).updateSlot(slot);
		((GameEventListener)b).updateSlot(slot);
	}

	@Override
	public void updateSupportedExpansions(EnumSet<Expansion> expansions) {
		((GameEventListener)a).updateSupportedExpansions(expansions);
		((GameEventListener)b).updateSupportedExpansions(expansions);
	}

	@Override
	public void started(Snapshot snapshot) {
		((GameEventListener)a).started(snapshot);
		((GameEventListener)b).started(snapshot);
	}

	@Override
	public void playerActivated(Player p, Player p2) {
		((GameEventListener)a).playerActivated(p, p2);
		((GameEventListener)b).playerActivated(p, p2);
	}

//	@Override
//	public void scoreAssigned(int score, PlacedFigure pf) {
//		((GameEventListener)a).scoreAssigned(score, pf);
//		((GameEventListener)b).scoreAssigned(score, pf);
//	}

	@Override
	public void tileDrawn(Tile tile) {
		((GameEventListener)a).tileDrawn(tile);
		((GameEventListener)b).tileDrawn(tile);
	}

	@Override
	public void tileDiscarded(String tileId) {
		((GameEventListener)a).tileDiscarded(tileId);
		((GameEventListener)b).tileDiscarded(tileId);
	}

	@Override
	public void tilePlaced(Tile tile) {
		((GameEventListener)a).tilePlaced(tile);
		((GameEventListener)b).tilePlaced(tile);
	}

	@Override
	public void dragonMoved(Position p) {
		((GameEventListener)a).dragonMoved(p);
		((GameEventListener)b).dragonMoved(p);

	}

	@Override
	public void fairyMoved(Position p) {
		((GameEventListener)a).fairyMoved(p);
		((GameEventListener)b).fairyMoved(p);
	}

	@Override
	public void towerIncreased(Position p, Integer height) {
		((GameEventListener)a).towerIncreased(p, height);
		((GameEventListener)b).towerIncreased(p, height);
	}

	@Override
	public void selectAbbeyPlacement(Set<Position> positions) {
		((UserInterface)a).selectAbbeyPlacement(positions);
		((UserInterface)b).selectAbbeyPlacement(positions);
	}

	@Override
	public void selectTilePlacement(Map<Position, Set<Rotation>> positions) {
		((UserInterface)a).selectTilePlacement(positions);
		((UserInterface)b).selectTilePlacement(positions);
	}

	@Override
	public void selectAction(List<PlayerAction> actions) {
		((UserInterface)a).selectAction(actions);
		((UserInterface)b).selectAction(actions);
	}

	@Override
	public void selectTowerCapture(CaptureAction action) {
		((UserInterface)a).selectTowerCapture(action);
		((UserInterface)b).selectTowerCapture(action);
	}

	@Override
	public void selectDragonMove(Set<Position> positions, int movesLeft) {
		((UserInterface)a).selectDragonMove(positions, movesLeft);
		((UserInterface)b).selectDragonMove(positions, movesLeft);
	}

	@Override
	public void gameOver() {
		((GameEventListener)a).gameOver();
		((GameEventListener)b).gameOver();
	}

	@Override
	public void ransomPaid(Player from, Player to, Follower ft) {
		((GameEventListener)a).ransomPaid(from, to, ft);
		((GameEventListener)b).ransomPaid(from, to, ft);
	}

	@Override
	public void tunnelPiecePlaced(Player player, Position p, Location d, boolean isSecondPiece) {
		((GameEventListener)a).tunnelPiecePlaced(player, p, d, isSecondPiece);
		((GameEventListener)b).tunnelPiecePlaced(player, p, d, isSecondPiece);
	}

	@Override
	public void deployed(Meeple m) {
		((GameEventListener)a).deployed(m);
		((GameEventListener)b).deployed(m);
	}


	@Override
	public void undeployed(Meeple m) {
		((GameEventListener)a).undeployed(m);
		((GameEventListener)b).undeployed(m);
	}

	@Override
	public void completed(Completable feature, CompletableScoreContext ctx) {
		((GameEventListener)a).completed(feature, ctx);
		((GameEventListener)b).completed(feature, ctx);
	}

	@Override
	public void scored(Feature feature, int points, String label, Meeple meeple, boolean isFinal) {
		((GameEventListener)a).scored(feature, points, label, meeple, isFinal);
		((GameEventListener)b).scored(feature, points, label, meeple, isFinal);
	}

	@Override
	public void scored(Position position, Player player, int points, String label, boolean isFinal) {
		((GameEventListener)a).scored(position, player, points, label, isFinal);
		((GameEventListener)b).scored(position, player, points, label, isFinal);
	}



}
