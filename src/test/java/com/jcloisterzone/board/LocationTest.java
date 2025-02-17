package com.jcloisterzone.board;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

import org.junit.Test;

public class LocationTest {

	@Test
	public void isSpecialLocation() {
		assertTrue(Location.E.isSideLocation());
		assertTrue(Location.NW.isSideLocation());
		assertTrue(Location.create(135).isSideLocation());
		assertFalse(Location.CENTER.isSideLocation());
		assertFalse(Location.CLOISTER.isSideLocation());
		assertFalse(Location.TOWER.isSideLocation());
	}

	@Test
	public void isPartOf() {
		assertTrue(Location.E.isPartOf(Location.E));
		assertTrue(Location.N.isPartOf(Location.NW));
		assertFalse(Location.E.isPartOf(Location.NW));
		assertFalse(Location.CENTER.isPartOf(Location.N));
		assertTrue(Location._N.isPartOf(Location.ALL));
	}

	@Test
	public void union() {
		assertEquals(Location.SW, Location.S.union(Location.W));
		assertEquals(Location.SW, Location.W.union(Location.S));
		assertEquals(Location.ALL, Location.N.union(Location._N));

	}
	@Test(expected=IllegalArgumentException.class)
	public void unionExcept1() {
		Location.N.union(Location.CLOISTER);
	}
	@Test(expected=IllegalArgumentException.class)
	public void unionExcept2() {
		Location.N.union(Location.CENTER);
	}

	@Test
	public void substract() {
		assertEquals(Location.S, Location.SW.substract(Location.W));
		assertEquals(Location.W, Location.SW.substract(Location.S));
		assertEquals(Location.N, Location.ALL.substract(Location._N));
	}
	@Test(expected=IllegalArgumentException.class)
	public void substractExcept1() {
		Location.N.substract(Location.CLOISTER);
	}

	@Test
	public void rev() {
		assertEquals(Location.S, Location.N.rev());
		assertEquals(Location.NL, Location.SR.rev());
		assertEquals(Location.NW, Location.SE.rev());
		assertEquals(Location._N, Location._S.rev());
		assertEquals(Location.N.union(Location.EL), Location.S.union(Location.WR).rev());
	}

	@Test
	public void rotate() {
		assertEquals(Location.E, Location.N.rotateCW(Rotation.R90));
		assertEquals(Location.W, Location.E.rotateCW(Rotation.R180));
		assertEquals(Location.S, Location.S.rotateCCW(Rotation.R0));
	}

	@Test
	public void isRotationOf() {
		assertTrue(Location.E.isRotationOf(Location.E));
		assertTrue(Location.E.isRotationOf(Location.N));
		assertTrue(Location.N.isRotationOf(Location.E));
		assertTrue(Location._N.isRotationOf(Location._S));
		assertTrue(Location.NW.isRotationOf(Location.NE));
	}

	@Test
	public void intersect() {
		assertEquals(Location.E, Location.E.intersect(Location.E));
		assertNull(Location.E.intersect(Location.W));
		assertEquals(Location.E, Location.HORIZONTAL.intersect(Location.SE));
		assertEquals(Location.NR, Location.NW.intersect(Location.NR.union(Location.EL)));
	}


}


