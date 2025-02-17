package com.jcloisterzone.game.phase;

import java.util.List;

import org.ini4j.Profile.Section;

import com.jcloisterzone.Expansion;
import com.jcloisterzone.board.DefaultTilePack;
import com.jcloisterzone.board.Tile;
import com.jcloisterzone.board.TilePack;
import com.jcloisterzone.game.Game;
import com.jcloisterzone.game.expansion.RiverGame;
import com.jcloisterzone.game.expansion.RiverIIGame;
import com.jcloisterzone.rmi.ServerIF;


public class DrawPhase extends ServerAwarePhase {


	private List<String> debugTiles;

	public DrawPhase(Game game, ServerIF server) {
		super(game, server);
		Section debugSection = game.getConfig().get("debug");
		if (debugSection != null) {
			debugTiles = debugSection.getAll("draw");
		}
	}

	private boolean makeDebugDraw() {
		if (debugTiles != null && debugTiles.size() > 0) { //for debug purposes only
			String tileId = debugTiles.remove(0);
			if (tileId.equals("!")) {
				next(GameOverPhase.class);
				return true;
			}
			TilePack tilePack = getTilePack();
			Tile tile = tilePack.drawTile(tileId);
			if (tile == null) {
				logger.warn("Invalid debug draw id: " + tileId);
			} else {
				if (tile.getRiver() == null && (tilePack.isGroupActive("river-start") || tilePack.isGroupActive("river"))) {
					//helper code for better behavior when debug draw is "river-invalid"
					//river II must be checke first!
					if (game.hasExpansion(Expansion.RIVER_II)) {
						((RiverIIGame)game.getExpandedGame(Expansion.RIVER_II)).activateNonRiverTiles();
					} else if (game.hasExpansion(Expansion.RIVER)) {
						((RiverGame)game.getExpandedGame(Expansion.RIVER)).activateNonRiverTiles();
					}
					tilePack.deactivateGroup("river-start");
					((DefaultTilePack)tilePack).setCurrentTile(tile); //recovery from lake placement
				}
				nextTile(tile);
				return true;
			}
		}
		return false;
	}

	@Override
	public void enter() {
		if (getTilePack().isEmpty()) {
			next(GameOverPhase.class);
			return;
		}
		if (makeDebugDraw()) {
			return;
		}
		if (isLocalPlayer(game.getActivePlayer())) {
			//call only from one client (from the active one)
			getServer().selectTile(getTilePack().size());
		}
	}



	@Override
	public void nextTile(Integer tileIndex) {
		Tile tile = getTilePack().drawTile(tileIndex);
		nextTile(tile);
	}

	private void nextTile(Tile tile) {
		getBoard().checkMoves(tile);
		if (getBoard().getAvailablePlacementPositions().isEmpty()) {
			getBoard().discardTile(tile.getId());
			next(DrawPhase.class);
			return;
		}
		game.fireGameEvent().tileDrawn(tile);
		next();
	}

}
