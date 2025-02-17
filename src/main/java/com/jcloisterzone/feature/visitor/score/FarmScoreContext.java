package com.jcloisterzone.feature.visitor.score;

import java.util.Map;
import java.util.Set;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.jcloisterzone.Expansion;
import com.jcloisterzone.Player;
import com.jcloisterzone.feature.City;
import com.jcloisterzone.feature.Farm;
import com.jcloisterzone.feature.Feature;
import com.jcloisterzone.figure.Pig;
import com.jcloisterzone.game.Game;

public class FarmScoreContext extends AbstractScoreContext {

	private Map<City, CityScoreContext> adjoiningCompletedCities = Maps.newHashMap();
	private Set<Player> pigs = Sets.newHashSet();
	private int pigHerds = 0;

	private Map<City, CityScoreContext> cityCache;
	private Map<Player, Set<City>> scoredCities;

	public FarmScoreContext(Game game) {
		super(game);
	}

	public Map<City, CityScoreContext> getCityCache() {
		return cityCache;
	}

	public void setCityCache(Map<City, CityScoreContext> cityCache) {
		this.cityCache = cityCache;
	}

	public Map<Player, Set<City>> getScoredCities() {
		return scoredCities;
	}

	public void setScoredCities(Map<Player, Set<City>> scoredCities) {
		this.scoredCities = scoredCities;
	}

	private void addAdjoiningCompletedCities(City[] adjoiningCities) {
		for(City c : adjoiningCities) {
			CityScoreContext ctx = cityCache.get(c);
			if (ctx == null) {
				ctx = c.getScoreContext();
				ctx.setCityCache(cityCache);
				c.walk(ctx);
			}
			if (ctx.isCompleted()) {
				adjoiningCompletedCities.put((City) ctx.getMasterFeature(), ctx);
			}
		}
	}


	@Override
	public boolean visit(Feature feature) {
		Farm farm = (Farm) feature;
		if (farm.getAdjoiningCities() != null) {
			addAdjoiningCompletedCities(farm.getAdjoiningCities());
		}
		if (farm.getMeeple() instanceof Pig) {
			pigs.add(farm.getMeeple().getPlayer());
		}
		if (farm.isPigHerd()) {
			pigHerds += 1;
		}
		return super.visit(feature);
	}

	private int getPointsPerCity(Player player, int basePoints) {
		int pointsPerCity = basePoints + pigHerds;
		if (pigs.contains(player)) pointsPerCity += 1;
		return pointsPerCity;
	}

	public int getPointsPerCity(Player player) {
		return getPointsPerCity(player, 3);
	}

	public int getPoints(Player player) {
		return getPlayerPoints(player, getPointsPerCity(player));
	}

	public int getPointsWhenBarnIsConnected(Player player) {
		return getPlayerPoints(player, getPointsPerCity(player, 1));
	}

	private int getPlayerPoints(Player player, int pointsPerCity) {
		//optimalization
		if (scoredCities == null && ! getGame().hasExpansion(Expansion.CATHARS)) {
			return pointsPerCity * adjoiningCompletedCities.size();
		}

		int points = 0;
		for(CityScoreContext ctx : adjoiningCompletedCities.values()) {
			if (scoredCities != null) {
				if (scoredCities.get(player).contains(ctx.getMasterFeature())) {
					continue;
				}
				scoredCities.get(player).add((City) ctx.getMasterFeature());
			}
			points += pointsPerCity;
			if (ctx.isBesieged()) { //count city twice
				points += pointsPerCity;
			}
		}
		return points;
	}



	public int getBarnPoints() {
		if (getGame().hasExpansion(Expansion.CATHARS)) {
			int points = 0;
			for(CityScoreContext ctx : adjoiningCompletedCities.values()) {
				points += 4;
				if (ctx.isBesieged()) { //count city twice
					points += 4;
				}
			}
			return points;
		} else {
			return adjoiningCompletedCities.size() * 4;
		}
	}

}
