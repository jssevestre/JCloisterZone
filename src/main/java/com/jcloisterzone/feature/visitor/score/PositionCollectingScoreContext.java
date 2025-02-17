package com.jcloisterzone.feature.visitor.score;

import java.util.Set;

import com.google.common.collect.Sets;
import com.jcloisterzone.board.Position;
import com.jcloisterzone.feature.Completable;
import com.jcloisterzone.feature.Feature;
import com.jcloisterzone.game.Game;

public abstract class PositionCollectingScoreContext extends AbstractScoreContext implements CompletableScoreContext {

	private Set<Position> positions = Sets.newHashSet();
	private boolean isCompleted = true;

	public PositionCollectingScoreContext(Game game) {
		super(game);
	}

	public abstract int getPoints(boolean completed);

	public Completable getMasterFeature() {
		return (Completable) super.getMasterFeature();
	}

	public int getSize() {
		return positions.size();
	}

	public Set<Position> getPositions() {
		return positions;
	}

	public boolean isCompleted() {
		return isCompleted;
	}

	@Override
	public boolean visit(Feature feature) {
		positions.add(feature.getTile().getPosition());
		if (! ((Completable)feature).isPieceCompleted()) {
			isCompleted = false;
		}
		return super.visit(feature);
	}

}
