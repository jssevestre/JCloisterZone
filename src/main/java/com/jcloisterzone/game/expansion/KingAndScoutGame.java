package com.jcloisterzone.game.expansion;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.jcloisterzone.Player;
import com.jcloisterzone.PointCategory;
import com.jcloisterzone.event.GameEventAdapter;
import com.jcloisterzone.feature.City;
import com.jcloisterzone.feature.Completable;
import com.jcloisterzone.feature.Road;
import com.jcloisterzone.feature.visitor.score.CompletableScoreContext;
import com.jcloisterzone.feature.visitor.score.PositionCollectingScoreContext;
import com.jcloisterzone.game.ExpandedGame;
import com.jcloisterzone.game.Game;
import com.jcloisterzone.game.SnapshotCorruptedException;

public final class KingAndScoutGame extends ExpandedGame {

	protected int completedCities, biggestCitySize;
	protected int completedRoads, longestRoadLength;

	private Player king, robberBaron;

	@Override
	public void setGame(Game game) {
		super.setGame(game);
		game.addGameListener(new GameEventAdapter() {
			@Override
			public void completed(Completable feature, CompletableScoreContext ctx) {
				if (feature instanceof City) {
					cityCompleted((City) feature, (PositionCollectingScoreContext) ctx);
				}
				if (feature instanceof Road) {
					roadCompleted((Road) feature, (PositionCollectingScoreContext) ctx);
				}
			}
		});
	}

	private void cityCompleted(City c, PositionCollectingScoreContext ctx) {
		completedCities++;
		int size = ctx.getSize();
		if (size > biggestCitySize) {
			biggestCitySize = size;
			king = game.getActivePlayer();
		}
	}

	private void roadCompleted(Road r, PositionCollectingScoreContext ctx) {
		completedRoads++;
		int size = ctx.getSize();
		if (size > longestRoadLength) {
			longestRoadLength = size;
			robberBaron = game.getActivePlayer();
		}
	}

	@Override
	public void finalScoring() {
		if (king != null) {
			king.addPoints(completedCities, PointCategory.BIGGEST_CITY);
		}
		if (robberBaron != null) {
			robberBaron.addPoints(completedRoads, PointCategory.LONGEST_ROAD);
		}
	}

	public int getCompletedCities() {
		return completedCities;
	}

	public int getBiggestCitySize() {
		return biggestCitySize;
	}

	public int getCompletedRoads() {
		return completedRoads;
	}

	public int getLongestRoadLength() {
		return longestRoadLength;
	}

	public Player getKing() {
		return king;
	}

	public Player getRobberBaron() {
		return robberBaron;
	}


	@Override
	public void saveToSnapshot(Document doc, Element node) {
		if (king != null) {
			node.setAttribute("king", king.getIndex() + "");
		}
		if (robberBaron != null) {
			node.setAttribute("robber", robberBaron.getIndex() + "");
		}
	}

	@Override
	public void loadFromSnapshot(Document doc, Element node) throws SnapshotCorruptedException {
		if (node.hasAttribute("king")) {
			king = game.getPlayer(Integer.parseInt(node.getAttribute("king")));
		}
		if (node.hasAttribute("robber")) {
			robberBaron = game.getPlayer(Integer.parseInt(node.getAttribute("robber")));
		}
	}


}
