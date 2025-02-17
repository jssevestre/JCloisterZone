package com.jcloisterzone.game.expansion;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.jcloisterzone.Player;
import com.jcloisterzone.action.PlayerAction;
import com.jcloisterzone.action.TunnelAction;
import com.jcloisterzone.board.Location;
import com.jcloisterzone.board.Position;
import com.jcloisterzone.board.Tile;
import com.jcloisterzone.board.XmlUtils;
import com.jcloisterzone.collection.Sites;
import com.jcloisterzone.feature.Feature;
import com.jcloisterzone.feature.Road;
import com.jcloisterzone.game.ExpandedGame;


public final class TunnelGame extends ExpandedGame {

	private Road placedTunnelCurrentTurn;

	private Map<Player, Integer> tunnelTokensA = Maps.newHashMap();
	private Map<Player, Integer> tunnelTokensB = Maps.newHashMap();

	private List<Road> tunnels = Lists.newArrayList();

	@Override
	public void initPlayer(Player player) {
		tunnelTokensA.put(player, 2);
		tunnelTokensB.put(player, game.getAllPlayers().length <= 2 ? 2 : 0);
	}

	@Override
	public void initFeature(Tile tile, Feature feature, Element xml) {
		if (! (feature instanceof Road)) return;
		Road road = (Road) feature;
		if (road.isTunnelEnd()) {
			tunnels.add(road);
		}
	}

	public Collection<Road> getOpenTunnels() {
		return Collections2.filter(tunnels, new Predicate<Road>() {
			@Override
			public boolean apply(Road road) {
				if (road.getTile().getPosition() == null) return false;
				return road.isTunnelOpen();
			}
		});
	}

	public int getTunnelTokens(Player player, boolean isB) {
		Map<Player, Integer> map = isB ? tunnelTokensB : tunnelTokensA;
		return map.get(player);
	}

	public void decreaseTunnelTokens(Player player, boolean isB) {
		Map<Player, Integer> map = isB ? tunnelTokensB : tunnelTokensA;
		int tokens = map.get(player);
		if (tokens == 0) throw new IllegalStateException("Player has no tunnel token");
		map.put(player, tokens-1);
	}

	@Override
	public void prepareActions(List<PlayerAction> actions, Sites commonSites) {
		if (isTunnelUsedThisTurn()) return;
		//TODO double iteration over tunnels
		if (getOpenTunnels().isEmpty()) return;
		TunnelAction tunnelAction = null;
		Sites sites = new Sites();
		if (getTunnelTokens(game.getActivePlayer(), false) > 0) {
			tunnelAction = new TunnelAction(false, sites);
			actions.add(tunnelAction);
		}
		if (getTunnelTokens(game.getActivePlayer(), true) > 0) {
			tunnelAction = new TunnelAction(true, sites);
			actions.add(tunnelAction);
		}
		//tunnel actions share sites object
		if (tunnelAction != null) {
			for (Road tunnelEnd : getOpenTunnels()) {
				tunnelAction.getOrCreate(tunnelEnd.getTile().getPosition()).add(tunnelEnd.getLocation());
			}
		}
	}

	public boolean isTunnelUsedThisTurn() {
		return placedTunnelCurrentTurn != null;
	}
	public Road getPlacedTunnel() {
		return placedTunnelCurrentTurn;
	}


	@Override
	public void turnCleanUp() {
		placedTunnelCurrentTurn = null;
	}

	private int getTunnelId(Player p, boolean isB) {
		return p.getIndex() + (isB ? 100 : 0);
	}

	public void placeTunnelPiece(Position p, Location d, boolean isB) {
		Road road = (Road) getBoard().get(p).getFeature(d);
		if (! road.isTunnelOpen()) {
			throw new IllegalStateException("No open tunnel here.");
		}
		Player player = game.getActivePlayer();
		int connectionId = getTunnelId(player, isB);
		decreaseTunnelTokens(player, isB);
		for(Road r : tunnels) {
			if (r.getTunnelEnd() == connectionId) {
				r.setTunnelEdge(road);
				road.setTunnelEdge(r);
				break;
			}
		}
		road.setTunnelEnd(connectionId);
		placedTunnelCurrentTurn = road;
		game.fireGameEvent().tunnelPiecePlaced(player, p, d, isB);
	}

	@Override
	public void saveToSnapshot(Document doc, Element node) {
		for(Road tunnel : tunnels) {
			if (tunnel.getTile().getPosition() != null && tunnel.getTunnelEnd() != Road.OPEN_TUNNEL) {
				Element el = doc.createElement("tunnel");
				node.appendChild(el);
				XmlUtils.injectPosition(el, tunnel.getTile().getPosition());
				el.setAttribute("location", tunnel.getLocation().toString());
				el.setAttribute("player", "" + (tunnel.getTunnelEnd() % 100));
				el.setAttribute("b", tunnel.getTunnelEnd() > 100 ? "yes" : "no");
			}
		}
	}

	@Override
	public void loadFromSnapshot(Document doc, Element node) {
		NodeList nl = node.getElementsByTagName("tunnel");
		for(int i = 0; i < nl.getLength(); i++) {
			Element el = (Element) nl.item(i);
			Position pos = XmlUtils.extractPosition(el);
			Location loc = Location.valueOf(el.getAttribute("location"));
			Road road = (Road) getBoard().get(pos).getFeature(loc);
			if (! road.isTunnelEnd()) {
				logger.error("Tunnel end does not exist.");
				continue;
			}
			Player player = game.getPlayer(Integer.parseInt(el.getAttribute("player")));
			boolean isB = "yes".equals(el.getAttribute("b"));
			road.setTunnelEnd(getTunnelId(player, isB));
			game.fireGameEvent().tunnelPiecePlaced(player, pos, loc, isB);
		}
	}

}
