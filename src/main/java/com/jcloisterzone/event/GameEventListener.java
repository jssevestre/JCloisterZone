package com.jcloisterzone.event;

import java.util.EnumSet;
import java.util.EventListener;

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

public interface GameEventListener extends EventListener {

	void updateSlot(PlayerSlot slot);
	void updateExpansion(Expansion expansion, Boolean enabled);
	void updateCustomRule(CustomRule rule, Boolean enabled);
	void updateSupportedExpansions(EnumSet<Expansion> expansions);

	void started(Snapshot snapshot);

	void playerActivated(Player turnPlayer, Player activePlayer);

	//void scoreAssigned(int score, PlacedFigure pf); //TODO not used, revise use or delete
	void ransomPaid(Player from, Player to, Follower meeple);
	//void scoreAssigned(int score, Tile tile, Player player); //non-feature score (fairy?)

	void tileDrawn(Tile tile);
	void tileDiscarded(String tileId);
	void tilePlaced(Tile tile);

	void dragonMoved(Position p);
	void fairyMoved(Position p);
	void towerIncreased(Position p, Integer height);

	void tunnelPiecePlaced(Player player, Position p, Location d, boolean isSecondPiece);

	void gameOver();

	//feature events
	void completed(Completable feature, CompletableScoreContext ctx);
	void scored(Feature feature, int points, String label, Meeple meeple, boolean isFinal);
	void scored(Position position, Player player, int points, String label, boolean isFinal);

	//meeple events
	void deployed(Meeple meeple);
	void undeployed(Meeple meeple);

}
