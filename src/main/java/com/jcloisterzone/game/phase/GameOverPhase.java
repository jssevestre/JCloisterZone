package com.jcloisterzone.game.phase;

import com.jcloisterzone.Player;
import com.jcloisterzone.feature.Completable;
import com.jcloisterzone.feature.Farm;
import com.jcloisterzone.feature.score.ScoreAllCallback;
import com.jcloisterzone.feature.score.ScoreAllFeatureFinder;
import com.jcloisterzone.feature.visitor.score.CompletableScoreContext;
import com.jcloisterzone.feature.visitor.score.FarmScoreContext;
import com.jcloisterzone.figure.Barn;
import com.jcloisterzone.game.Game;


public class GameOverPhase extends Phase implements ScoreAllCallback {

	public GameOverPhase(Game game) {
		super(game);
	}

	@Override
	public void enter() {
		if (game.getPrincessAndDragonGame() != null) {
			//erase position to not affect final scoring
			game.getPrincessAndDragonGame().setFairyPosition(null);
		}

		ScoreAllFeatureFinder scoreAll = new ScoreAllFeatureFinder();
		scoreAll.scoreAll(game, this);

		game.expansionDelegate().finalScoring();
		game.fireGameEvent().gameOver();
	}

	@Override
	public void scoreFarm(FarmScoreContext ctx, Player p) {
		int points = ctx.getPoints(p);
		game.scoreFeature(points, ctx, p);
	}

	@Override
	public void scoreBarn(FarmScoreContext ctx, Barn meeple) {
		int points = ctx.getBarnPoints();
		meeple.getPlayer().addPoints(points, ctx.getMasterFeature().getPointCategory());
		game.fireGameEvent().scored(meeple.getFeature(), points, points+"", meeple, true);
	}


	@Override
	public void scoreCompletableFeature(CompletableScoreContext ctx) {
		game.scoreCompletableFeature(ctx);
	}

	@Override
	public CompletableScoreContext getCompletableScoreContext(Completable completable) {
		return completable.getScoreContext();
	}

	@Override
	public FarmScoreContext getFarmScoreContext(Farm farm) {
		return farm.getScoreContext();
	}



}
