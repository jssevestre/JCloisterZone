package com.jcloisterzone.game;

import static com.jcloisterzone.ui.I18nUtils._;

import java.util.List;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import org.apache.mina.core.session.IoSession;

import com.jcloisterzone.game.PlayerSlot.SlotType;
import com.jcloisterzone.rmi.ControllMessage;
import com.jcloisterzone.rmi.mina.ClientStub;
import com.jcloisterzone.ui.Client;


public class GuiClientStub extends ClientStub {

	private final Client client;

	public GuiClientStub(Client client) {
		this.client = client;
	}

	@Override
	protected Game createGame(ControllMessage msg) {
		Game game = super.createGame(msg);
		game.setConfig(client.getConfig());
		client.setGame(game);
		return game;
	}

	@Override
	protected void controllMessageReceived(final ControllMessage msg) {
		super.controllMessageReceived(msg);

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				client.showCreateGamePanel(msg.getSnapshot() == null);
			}
		});

		if (client.getConfig().get("debug", "autostart", boolean.class)) {
			final List<String> players = client.getConfig().get("debug").getAll("autostart_player");
			if (players.isEmpty()) {
				players.add("UNDEFINED");
			}
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					int i = 0;
					for(String name: players) {
						PlayerSlot slot;
						try {
							Class<?> clazz = Class.forName(name);
							slot = new PlayerSlot(i, SlotType.AI, clazz.getSimpleName(), getClientId());
							slot.setAiClassName(clazz.getName());
						} catch (ClassNotFoundException e) {
							slot = new PlayerSlot(i, SlotType.PLAYER, name, getClientId());
						}
						client.getServer().updateSlot(slot, null);
						i++;
					}
					client.getServer().startGame();
				}
			});
		}
	}

	protected void versionMismatch(int version) {
		super.versionMismatch(version);
		JOptionPane.showMessageDialog(client,
				_("Remote JCloisterZone is not compatible with local application. Please upgrade both applications to same version."),
				_("Incompatible versions"),
				JOptionPane.ERROR_MESSAGE
		);
	}

	@Override
	public void exceptionCaught(IoSession session, Throwable cause) {
		logger.error(cause.getLocalizedMessage(), cause);
		JOptionPane.showMessageDialog(client,
			cause.getLocalizedMessage(),
			_("Connection error"),
			JOptionPane.ERROR_MESSAGE
		);
		//TODO better handling
	}


}
