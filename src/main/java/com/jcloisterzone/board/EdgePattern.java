package com.jcloisterzone.board;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Queue;

import com.jcloisterzone.feature.City;
import com.jcloisterzone.feature.Feature;
import com.jcloisterzone.feature.Road;

public class EdgePattern {

	private char[] code = new char[4];

	private EdgePattern() { }
	private EdgePattern(char[] code) { this.code = code; }

	private static char getTileEdgePattern(Tile tile, Location loc) {
		Feature f = tile.getFeaturePartOf(loc, Road.class, City.class);
		if (f == null) {
			return 'F';
		}
		if (f instanceof Road) {
			return 'R';
		}
		return 'C';
	}

	public static EdgePattern forTile(Tile tile) {
		EdgePattern pattern = new EdgePattern();
		for(Location loc : Location.sides()) {
			pattern.code[loc.ordinal] = getTileEdgePattern(tile, loc);
		}
		return pattern;
	}

	public static EdgePattern forEmptyTile(Board board, Position pos) {
		EdgePattern pattern = new EdgePattern();
		for(Location loc : Location.sides()) {
			Tile t = board.get(pos.add(loc));
			if (t == null) {
				pattern.code[loc.ordinal] = '?';
			} else {
				pattern.code[loc.ordinal] = getTileEdgePattern(t, loc.rev());
			}
		}
		return pattern;
	}

	public char at(Location loc) {
		return code[loc.ordinal()];
	}

	public char at(Location loc, Rotation rotation) {
		return at(loc.rotateCCW(rotation));
	}

	public int wildcardSize() {
		int size = 0;
		for(int i = 0; i < code.length; i++) {
			if (code[i] == '?') size++;
		}
		return size;
	}

	private EdgePattern switchEdge(int i, char ch) {
		char[] switched = Arrays.copyOf(code, code.length);
		switched[i] = ch;
		return new EdgePattern(switched);
	}

	public Collection<EdgePattern> fill() {
		//TODO better impl
		if (wildcardSize() == 0) return Collections.singleton(this);
		Queue<EdgePattern> q = new LinkedList<EdgePattern>();
		q.add(this);
		while(q.peek().wildcardSize() > 0) {
			EdgePattern p = q.poll();
			int i = 0;
			while(p.code[i] != '?') i++;
			q.add(switchEdge(i, 'R'));
			q.add(switchEdge(i, 'C'));
			q.add(switchEdge(i, 'F'));
		}
		return q;
	}

	private char[] shift(int shift) {
		char[] result = new char[4];
		for(int i = 0; i < code.length; i++) {
			result[i] = code[(i+shift)%code.length];
		}
		return result;
	}

	private char[] canonize() {
		char[] result = code;
		shiftLoop:
		for(int shift = 1; shift < code.length; shift++) {
			char[] c = shift(shift);
			for(int i = 0; i < code.length; i++) {
				if (c[i] < result[i]) {
					result = c;
					continue shiftLoop;
				}
				if (c[i] > result[i]) {
					break;
				}
			}
		}
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (! (obj instanceof EdgePattern)) return false;
		EdgePattern that = (EdgePattern) obj;
		return Arrays.equals(that.canonize(), canonize());
	}

	@Override
	public int hashCode() {
		char[] c = canonize();
		int hash = 0;
		for(int i = 0; i < c.length; i++) {
			hash = hash * 91 + c[i];
		}
		return hash;
	}


	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for(int i = 0; i < code.length; i++) {
			sb.append(code[i]);
		}
		return sb.toString();
	}

}
