package com.jcloisterzone.action;

import java.util.Set;

import com.jcloisterzone.board.Location;
import com.jcloisterzone.board.Position;
import com.jcloisterzone.collection.Sites;
import com.jcloisterzone.figure.BigFollower;
import com.jcloisterzone.figure.Builder;
import com.jcloisterzone.figure.Mayor;
import com.jcloisterzone.figure.Meeple;
import com.jcloisterzone.figure.Pig;
import com.jcloisterzone.figure.SmallFollower;
import com.jcloisterzone.figure.Wagon;
import com.jcloisterzone.rmi.Client2ClientIF;

public class MeepleAction extends FeatureAction {

	private final Class<? extends Meeple> meepleType;

	public MeepleAction(Class<? extends Meeple> meepleType) {
		this.meepleType = meepleType;
	}

	public MeepleAction(Class<? extends Meeple> meepleType, Sites sites) {
		super(sites);
		this.meepleType = meepleType;
	}

	public MeepleAction(Class<? extends Meeple> meepleType, Position p, Set<Location> locations) {
		super(p, locations);
		this.meepleType = meepleType;
	}

	public Class<? extends Meeple> getMeepleType() {
		return meepleType;
	}

	@Override
	public void perform(Client2ClientIF server, Position p, Location d) {
		server.deployMeeple(p, d, meepleType);
	}

	@Override
	public String getName() {
		return getMeepleType().getSimpleName().toLowerCase();
	}

	@Override
	protected int getSortOrder() {
		//TODO bad code
		if (meepleType.equals(SmallFollower.class)) return 10;
		if (meepleType.equals(BigFollower.class)) return 11;
		if (meepleType.equals(Wagon.class)) return 12;
		if (meepleType.equals(Mayor.class)) return 13;
		if (meepleType.equals(Builder.class)) return 14;
		if (meepleType.equals(Pig.class)) return 15;
		return 19;
	}

}
