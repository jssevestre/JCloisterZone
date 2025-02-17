package com.jcloisterzone.feature.visitor.score;

import java.util.List;
import java.util.Set;

import com.jcloisterzone.Player;
import com.jcloisterzone.feature.Scoreable;
import com.jcloisterzone.feature.visitor.FeatureVisitor;
import com.jcloisterzone.figure.Follower;
import com.jcloisterzone.figure.Meeple;
import com.jcloisterzone.figure.Special;

public interface ScoreContext extends FeatureVisitor {

	Scoreable getMasterFeature();

	Follower getSampleFollower(Player player);
	Set<Player> getMajorOwners();

	List<Follower> getFollowers();
	List<Special> getSpecialMeeples();
	Iterable<Meeple> getMeeples();



}
