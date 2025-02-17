package com.jcloisterzone.event;

import java.util.EnumSet;

import com.jcloisterzone.Expansion;
import com.jcloisterzone.Player;
import com.jcloisterzone.board.Location;
import com.jcloisterzone.board.Position;
import com.jcloisterzone.board.Tile;
import com.jcloisterzone.feature.Completable;
import com.jcloisterzone.feature.Feature;
import com.jcloisterzone.feature.visitor.score.CompletableScoreContext;
import com.jcloisterzone.figure.Follower;
import com.jcloisterzone.figure.Meeple;
import com.jcloisterzone.game.CustomRule;
import com.jcloisterzone.game.PlayerSlot;
import com.jcloisterzone.game.Snapshot;

public class GameEventAdapter implements GameEventListener {

	@Override
	public void updateSlot(PlayerSlot slot) {
	}

	@Override
	public void updateExpansion(Expansion expansion, Boolean enabled) {
	}

	@Override
	public void updateCustomRule(CustomRule rule, Boolean enabled) {
	}

	@Override
	public void updateSupportedExpansions(EnumSet<Expansion> expansions) {
	}

	@Override
	public void started(Snapshot snapshot) {
	}

	@Override
	public void playerActivated(Player turnPlayer, Player activePlayer) {
	}

	@Override
	public void ransomPaid(Player from, Player to, Follower meeple) {
	}

	@Override
	public void tileDrawn(Tile tile) {
	}

	@Override
	public void tileDiscarded(String tileId) {
	}

	@Override
	public void tilePlaced(Tile tile) {
	}

	@Override
	public void dragonMoved(Position p) {
	}

	@Override
	public void fairyMoved(Position p) {
	}

	@Override
	public void towerIncreased(Position p, Integer height) {
	}

	@Override
	public void tunnelPiecePlaced(Player player, Position p, Location d, boolean isSecondPiece) {
	}

	@Override
	public void gameOver() {
	}

	@Override
	public void completed(Completable feature, CompletableScoreContext ctx) {
	}

	@Override
	public void scored(Feature feature, int points, String label, Meeple meeple, boolean isFinal) {
	}

	@Override
	public void scored(Position position, Player player, int points, String label, boolean isFinal) {
	}

	@Override
	public void deployed(Meeple meeple) {
	}

	@Override
	public void undeployed(Meeple meeple) {
	}

}
