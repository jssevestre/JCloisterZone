package com.jcloisterzone.ui.panel;

import static com.jcloisterzone.ui.I18nUtils._;

import java.awt.Component;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Map;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

import net.miginfocom.swing.MigLayout;

import com.google.common.collect.Maps;
import com.jcloisterzone.Expansion;
import com.jcloisterzone.game.CustomRule;
import com.jcloisterzone.game.PlayerSlot;
import com.jcloisterzone.game.PlayerSlot.SlotType;
import com.jcloisterzone.ui.Client;


public class CreateGamePanel extends JPanel {

	private static final long serialVersionUID = -8993000662700228625L;

	static Font FONT_RULE_SECTION = new Font(null, Font.ITALIC, 13);

	private final Client client;
	private boolean mutableSlots;

	private JPanel playersPanel;
	//private JLabel helpText;
	private JButton startGameButton;
	private JPanel expansionPanel;
	private JPanel rulesPanel;
	private JPanel panel;

	private Map<Expansion, JCheckBox> expansionCheckboxes = Maps.newHashMap();
	private Map<CustomRule, JCheckBox> ruleCheckboxes = Maps.newHashMap();

	/**
	 * Create the panel.
	 */
	public CreateGamePanel(final Client client, boolean mutableSlots) {
		this.client = client;
		this.mutableSlots = mutableSlots;
		NameProvider nameProvider = new NameProvider(client.getConfig());

		setLayout(new MigLayout("", "[][grow][grow]", "[][grow]"));

		panel = new JPanel();
		add(panel, "cell 0 0 3 1,grow");
		panel.setLayout(new MigLayout("", "[grow][]", "[]"));

		//helpText = new JLabel("[TODO HELP TEXT]");
		//panel.add(helpText, "growx");
		//helpText.setText(_("The game has been created. Remote clients can connect now."));

		startGameButton = new JButton(_("Start game"));
		startGameButton.setIcon(new ImageIcon(CreateGamePanel.class.getResource("/sysimages/endTurn.png")));
		panel.add(startGameButton, "width 100, h 40, east");

		startGameButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				client.getServer().startGame();
			}
		});
		startGameButton.setEnabled(false);

		playersPanel = new JPanel();
		playersPanel.setBorder(new TitledBorder(null, _("Players"), TitledBorder.LEADING, TitledBorder.TOP, null, null));
		playersPanel.setLayout(new MigLayout("", "[grow]", ""));

		//not nice code
		int slotCount = client.getGame().getAllPlayers() == null ? PlayerSlot.COUNT : client.getGame().getAllPlayers().length;
		for(int i = 0; i < slotCount; i++) {
			playersPanel.add(new CreateGamePlayerPanel(client, mutableSlots, i, nameProvider), "wrap");
		}
		add(playersPanel, "cell 0 1,grow");

		expansionPanel = new JPanel();
		expansionPanel.setBorder(new TitledBorder(null, _("Expansions"), TitledBorder.LEADING, TitledBorder.TOP, null, null));
		expansionPanel.setLayout(new MigLayout("", "[]", "[]"));
		for(Expansion exp: Expansion.values()) {
			if (! exp.isEnabled()) continue;
			//if (exp == Expansion.WHEEL_OF_FORTUNE) continue;
			JCheckBox chbox = createExpansionCheckbox(exp, mutableSlots);
			if (exp == Expansion.KING_AND_SCOUT || exp == Expansion.INNS_AND_CATHEDRALS) {
				expansionPanel.add(chbox, "wrap,gaptop 10");
			} else {
				expansionPanel.add(chbox, "wrap");
			}
			expansionCheckboxes.put(exp, chbox);
		}
		add(expansionPanel, "cell 1 1,grow");

		rulesPanel = new JPanel();
		rulesPanel.setBorder(new TitledBorder(null, _("Rules"), TitledBorder.LEADING, TitledBorder.TOP, null, null));
		rulesPanel.setLayout(new MigLayout("", "[]", "[]"));
		add(rulesPanel, "cell 2 1,grow");

		Expansion prev = Expansion.BASIC;
		for(CustomRule rule: CustomRule.values()) {
			if (prev != rule.getExpansion()) {
				prev = rule.getExpansion();
				JLabel label = new JLabel(prev.toString());
				label.setFont(FONT_RULE_SECTION);
				rulesPanel.add(label, "wrap, growx, gaptop 10, gapbottom 7");
			}
			JCheckBox chbox = createRuleCheckbox(rule, mutableSlots);
			rulesPanel.add(chbox, "wrap");
			ruleCheckboxes.put(rule, chbox);
		}
	}

	public void disposePanel() {
		for(Component comp : playersPanel.getComponents()) {
			if (comp instanceof CreateGamePlayerPanel) {
				((CreateGamePlayerPanel) comp).disposePanel();
			}
		}
	}

	private JCheckBox createRuleCheckbox(final CustomRule rule, boolean mutableSlots) {
		JCheckBox chbox = new JCheckBox(rule.getLabel());
		if (mutableSlots) {
			chbox.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					JCheckBox chbox = (JCheckBox) e.getSource();
					client.getServer().updateCustomRule(rule, chbox.isSelected());
				}
			});
		} else {
			chbox.setEnabled(false);
		}
		return chbox;
	}

	private JCheckBox createExpansionCheckbox(final Expansion exp, boolean mutableSlots) {
		JCheckBox chbox = new JCheckBox(exp.toString());
		if (! exp.isEnabled() || ! mutableSlots) chbox.setEnabled(false);
		if (exp == Expansion.BASIC) {
			//chbox.setSelected(true);
			chbox.setEnabled(false);
		}
		if (chbox.isEnabled()) {
			chbox.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					JCheckBox chbox = (JCheckBox) e.getSource();
					client.getServer().updateExpansion(exp, chbox.isSelected());
				}
			});
		}
		return chbox;
	}

	public void updateCustomRule(CustomRule rule, Boolean enabled) {
		ruleCheckboxes.get(rule).setSelected(enabled);
	}

	public void updateExpansion(Expansion expansion, Boolean enabled) {
		expansionCheckboxes.get(expansion).setSelected(enabled);
	}

	public void updateSupportedExpansions(EnumSet<Expansion> expansions) {
		if (expansions == null) {
			expansions = EnumSet.allOf(Expansion.class);
		}
		for(Expansion exp : Expansion.values()) {
			if (exp.isEnabled()) {
				boolean isSupported = expansions.contains(exp);
				JCheckBox chbox = expansionCheckboxes.get(exp);
				//chbox.setEnabled(isSupported);
				chbox.setVisible(isSupported);
				//chbox.setForeground(isSupported ? null : Color.RED);
			}
		}
	}

	private CreateGamePlayerPanel getPlayerPanel(int number) {
		return (CreateGamePlayerPanel) playersPanel.getComponent(number);
	}


	public void updateSlot(PlayerSlot slot) {
		getPlayerPanel(slot.getNumber()).updateSlot(slot);

		boolean anyPlayerAssigned = false;
		boolean allPlayersAssigned = true;
		ArrayList<Integer> serials = new ArrayList<Integer>();

		for(Component c : playersPanel.getComponents()) {
			CreateGamePlayerPanel playerPanel = (CreateGamePlayerPanel) c;
			PlayerSlot ps = playerPanel.getSlot();
			if (ps == null || ps.getType() == SlotType.OPEN) {
				allPlayersAssigned = false;
			} else {
				anyPlayerAssigned = true;
			}
			if (ps != null && ps.getSerial() != null) {
				serials.add(ps.getSerial());
			} else {
				playerPanel.serSerialText("");
			}
		}
		if (mutableSlots && ! serials.isEmpty()) {
			Collections.sort(serials);
			for(Component c : playersPanel.getComponents()) {
				CreateGamePlayerPanel playerPanel = (CreateGamePlayerPanel) c;
				PlayerSlot ps = playerPanel.getSlot();
				if (ps != null && ps.getSerial() != null) {
					playerPanel.serSerialText("" + (1 + serials.indexOf(ps.getSerial())));
				}
			}
		}

		if (mutableSlots) {
			startGameButton.setEnabled(anyPlayerAssigned);
		} else {
			startGameButton.setEnabled(allPlayersAssigned);
		}
	}

}
