package com.jcloisterzone.ui;

import static com.jcloisterzone.ui.I18nUtils._;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jcloisterzone.ui.grid.layer.PlacementHistory;

@SuppressWarnings("serial")
public class MenuBar extends JMenuBar {

	protected final transient Logger logger = LoggerFactory.getLogger(getClass());

	private final Client client;

	private JMenuItem create, connect, close, showDiscard, save, load;
	private JMenuItem zoomIn, zoomOut;
	private JMenuItem history;

	public MenuBar(Client client2) {
		this.client = client2;

		JMenu menu;
		JMenuItem menuItem;

		//menu.getAccessibleContext().setAccessibleDescription(
		//"The only menu in this program that has menu items");

		menu = new JMenu(_("Game"));
		menu.setMnemonic(KeyEvent.VK_G);
		create = new JMenuItem(_("New game"));
		create.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F5, 0));
		create.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				client.createGame();
			}
		});
		menu.add(create);

		connect = new JMenuItem(_("Connect"));
		connect.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F6, 0));
		connect.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				client.showConnectGamePanel();
			}
		});
		menu.add(connect);

		close = new JMenuItem(_("Close game"));
		close.setEnabled(false);
		close.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				client.closeGame();
			}
		});
		menu.add(close);


		menu.addSeparator();

		save = new JMenuItem(_("Save"));
		save.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F2, 0));
		save.setEnabled(false);
		save.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				client.save();
			}
		});
		menu.add(save);

		load = new JMenuItem(_("Load"));
		load.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F3, 0));
		load.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				client.load();
			}
		});
		menu.add(load);

		menu.addSeparator();

		menuItem = new JMenuItem(_("Quit"));
		menuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (client.closeGame() == true) {
					System.exit(0);
				}
			}
		});
		menu.add(menuItem);

		this.add(menu);


		menu = new JMenu(_("Window"));
		zoomIn = new JMenuItem(_("Zoom in"));
		zoomIn.setAccelerator(KeyStroke.getKeyStroke('+'));
		zoomIn.setEnabled(false);
		zoomIn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				client.getGridPanel().zoom(2);
			}
		});
		menu.add(zoomIn);
		zoomOut = new JMenuItem(_("Zoom out"));
		zoomOut.setAccelerator(KeyStroke.getKeyStroke('-'));
		zoomOut.setEnabled(false);
		zoomOut.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				client.getGridPanel().zoom(-2);
			}
		});
		menu.add(zoomOut);

//		menuItem = new JMenuItem(_("FullScreen"));
//		menuItem.addActionListener(new ActionListener() {
//			public void actionPerformed(ActionEvent e) {
//				//fullScreen();
//				throw new UnsupportedOperationException();
//			}
//		});
//		/*if (! GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().isFullScreenSupported()) {
//			menuItem.setEnabled(false);
//		}*/
//		menuItem.setEnabled(false);
//		menu.add(menuItem);

		menu.addSeparator();
		history = new JCheckBoxMenuItem(_("Show last placements"));
		history.setAccelerator(KeyStroke.getKeyStroke('x'));
		history.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JCheckBoxMenuItem ch = (JCheckBoxMenuItem) e.getSource();
				client.getSettings().setShowHistory(ch.isSelected());
				if (ch.isSelected()) {
					client.getGridPanel().showRecentHistory();
				} else {
					client.getGridPanel().removeLayer(PlacementHistory.class);
				}
			}
		});
		menu.add(history);


		showDiscard = new JMenuItem(_("Show discarded tiles"));
		showDiscard.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F9, 0));
		showDiscard.setEnabled(false);
		showDiscard.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				client.getDiscardedTilesDialog().setVisible(true);
			}
		});
		menu.add(showDiscard);


		this.add(menu);

		menu = new JMenu(_("Settings"));
		menuItem = new JCheckBoxMenuItem(_("Beep alert at player turn"));
		menuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				boolean state = ((JCheckBoxMenuItem) e.getSource()).getState();
				client.getSettings().setPlayBeep(state);
			}
		});
		((JCheckBoxMenuItem)menuItem).setSelected(client.getSettings().isPlayBeep());
		menu.add(menuItem);

		menu.addSeparator();

		menuItem = new JCheckBoxMenuItem(_("Confirm placement on a farm"));
		menuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				boolean state = ((JCheckBoxMenuItem) e.getSource()).getState();
				client.getSettings().setConfirmFarmPlacement(state);
			}
		});
		((JCheckBoxMenuItem)menuItem).setSelected(client.getSettings().isConfirmFarmPlacement());
		menu.add(menuItem);
		menuItem = new JCheckBoxMenuItem(_("Confirm placement on a tower"));
		menuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				boolean state = ((JCheckBoxMenuItem) e.getSource()).getState();
				client.getSettings().setConfirmTowerPlacement(state);
			}
		});
		((JCheckBoxMenuItem)menuItem).setSelected(client.getSettings().isConfirmTowerPlacement());
		menu.add(menuItem);

		menu.addSeparator();

		menuItem = new JCheckBoxMenuItem(_("Confirm game close"));
		menuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				boolean state = ((JCheckBoxMenuItem) e.getSource()).getState();
				client.getSettings().setConfirmGameClose(state);
			}
		});
		((JCheckBoxMenuItem)menuItem).setSelected(client.getSettings().isConfirmGameClose());
		menu.add(menuItem);

//		menuItem = new JMenuItem(_("Settings_ClearPrefs"));
//		menuItem.addActionListener(new ActionListener() {
//			public void actionPerformed(ActionEvent e) {
//				Preferences prefs = Preferences.userNodeForPackage(Client.class);
//				try {
//					prefs.clear();
//				} catch (BackingStoreException e1) {
//					e1.printStackTrace();
//					JOptionPane.showMessageDialog(client,e1.getMessage(),"BackingStoreException",JOptionPane.ERROR_MESSAGE);
//					return;
//				}
//				JOptionPane.showMessageDialog(client,"OK", _("Settings_ClearPrefs"),JOptionPane.INFORMATION_MESSAGE);
//			}
//		});
//		menu.add(menuItem);

		this.add(menu);

		menu = new JMenu(_("Help"));

//		menuItem = new JMenuItem(_("Rules"));
//		menuItem.addActionListener(new ActionListener() {
//			public void actionPerformed(ActionEvent e) {
//				//emty for now
//			}
//		});
//		menuItem.setEnabled(false);
//		menu.add(menuItem);

		menuItem = new JMenuItem(_("About"));
		menuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new AboutDialog();
			}
		});
		menu.add(menuItem);

		this.add(menu);
	}

	//legacy methods - TODO refactor

	public void setZoomInEnabled(boolean state) {
		zoomIn.setEnabled(state);
	}
	public void setZoomOutEnabled(boolean state) {
		zoomOut.setEnabled(state);
	}

	public void setIsGameRunning(boolean isGameRunning) {
		create.setEnabled(! isGameRunning);
		connect.setEnabled(! isGameRunning);
		close.setEnabled(isGameRunning);
		if (! isGameRunning) {
			showDiscard.setEnabled(false);
		}
		save.setEnabled(isGameRunning);
	}

	public void setShowDiscardedEnabled(boolean enabled) {
		showDiscard.setEnabled(enabled);
	}




}
