package com.jcloisterzone.board;

import static com.jcloisterzone.board.XmlUtils.attributeIntValue;
import static com.jcloisterzone.board.XmlUtils.attributeStringValue;
import static com.jcloisterzone.board.XmlUtils.getTileId;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.jcloisterzone.Expansion;
import com.jcloisterzone.game.CustomRule;
import com.jcloisterzone.game.Game;
import com.jcloisterzone.game.PlayerSlot;


public class TilePackFactory {

	protected final transient Logger logger = LoggerFactory.getLogger(getClass());

	public static final String DEFAULT_TILE_GROUP = "default";

	private final DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
	private final TileFactory tileFactory = new TileFactory();

	protected Game game;
	protected Map<Expansion, Element> defs;

	private Set<String> usedIds = Sets.newHashSet(); //for assertion only


	public void setGame(Game game) {
		this.game = game;
		tileFactory.setGame(game);
	}

	public void setExpansions(Set<Expansion> expansions) {
		defs = Maps.newLinkedHashMap();
		for(Expansion expansion : expansions) {
			defs.put(expansion, getExpansionDefinition(expansion));
		}
	}

	private InputStream getCardsConfig(Expansion expansion) {
		String fileName = game.getConfig().get("debug", "cards_"+expansion.name());
		if (fileName == null) {
			fileName = "tile-definitions/"+expansion.name().toLowerCase()+".xml";
		}
		return TilePackFactory.class.getClassLoader().getResourceAsStream(fileName);
	}

	protected Element getExpansionDefinition(Expansion expansion) {
		try {
			DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
			return docBuilder.parse(getCardsConfig(expansion)).getDocumentElement();
		} catch (Exception ex) {
			logger.error("Cannot load card definitions for expansion " + expansion, ex);
			System.exit(1);
			return null;
		}
	}

	protected Map<String, Integer> getDiscardTiles() {
		Map<String, Integer> discard = Maps.newHashMap();
		for(Element expansionDef: defs.values()) {
			NodeList nl = expansionDef.getElementsByTagName("discard");
			for(int i = 0; i < nl.getLength(); i++) {
				Element el = (Element) nl.item(i);
				String tileId = el.getAttribute("tile");
				if (discard.containsKey(tileId)) {
					discard.put(tileId, 1 + discard.get(tileId));
				} else {
					discard.put(tileId, 1);
				}
			}
		}
		return discard;
	}

	protected boolean isTunnelActive(Expansion expansion) {
		return expansion == Expansion.TUNNEL || (game.hasExpansion(Expansion.TUNNEL) && game.hasRule(CustomRule.TUNNELIZE_ALL_EXPANSIONS));
	}

	protected int getTileCount(Element card, String tileId) {
		if (Tile.ABBEY_TILE_ID.equals(tileId)) {
			return PlayerSlot.COUNT;
		} else {
			return attributeIntValue(card, "count", 1);
		}
	}

	protected String getTileGroup(Tile tile, Element card) {
		return attributeStringValue(card, "group", DEFAULT_TILE_GROUP);
	}

	public List<Tile> createTiles(Expansion expansion, String tileId, Element card) {
		if (usedIds.contains(tileId)) {
			throw new IllegalArgumentException("Multiple occurences of id " + tileId + " in tile definition xml.");
		}
		usedIds.add(tileId);

		Map<String, Integer> discardList = getDiscardTiles();
		if (discardList.containsKey(tileId)) {
			int n = discardList.get(tileId);
			if (n == 1) {
				discardList.remove(tileId);
			} else {
				discardList.put(tileId, n-1);
			}
			return Collections.emptyList();
		}

		int count = getTileCount(card, tileId);

		List<Tile> tiles = new ArrayList<Tile>(count);
		for(int j = 0; j < count; j++) {
			Tile tile = tileFactory.createTile(tileId, card, isTunnelActive(expansion));
			game.expansionDelegate().initTile(tile, card); //must be called before rotation!
			tiles.add(tile);
		}
		return tiles;
	}

	public LinkedList<Position> getPreplacedPositions(String tileId, Element card) {
		NodeList nl = card.getElementsByTagName("position");
		if (nl.getLength() == 0) return null;

		LinkedList<Position> result = new LinkedList<Position>();
		for(int i = 0; i < nl.getLength(); i++) {
			Element posEl = (Element) nl.item(i);
			result.add(new Position(attributeIntValue(posEl, "x"), attributeIntValue(posEl, "y")));
		}
		return result;
	}

	@Deprecated
	public Tile createTileForId(String id) {
		for(Entry<Expansion, Element> entry: defs.entrySet()) {
			Expansion expansion = entry.getKey();
			NodeList nl = entry.getValue().getElementsByTagName("card");
			for(int i = 0; i < nl.getLength(); i++) {
				Element tileElement = (Element) nl.item(i);
				String tileId = getTileId(expansion, tileElement);
				if (! tileId.equals(id)) continue;
				tileElement.setAttribute("count", "1");
				return createTiles(expansion, tileId, tileElement).get(0);
			}
		}
		return null;
	}

	public DefaultTilePack createTilePack() {
		DefaultTilePack tilePack = new DefaultTilePack();

		for(Entry<Expansion, Element> entry: defs.entrySet()) {
			Expansion expansion = entry.getKey();
			NodeList nl = entry.getValue().getElementsByTagName("card");
			for(int i = 0; i < nl.getLength(); i++) {
				Element tileElement = (Element) nl.item(i);
				String tileId = getTileId(expansion, tileElement);
				LinkedList<Position> positions = getPreplacedPositions(tileId, tileElement);
				for(Tile tile : createTiles(expansion, tileId, tileElement)) {
					if (positions != null && ! positions.isEmpty()) {
						tile.setPosition(positions.removeFirst());
					}
					tilePack.addTile(tile, getTileGroup(tile, tileElement));
				}
			}
		}
		return tilePack;
	}

	public Game getGame() {
		return game;
	}




}
