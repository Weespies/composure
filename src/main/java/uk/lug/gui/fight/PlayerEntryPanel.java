package uk.lug.gui.fight;

import java.awt.BorderLayout;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.apache.commons.lang3.StringUtils;

import uk.lug.serenity.npc.gui.controls.FormPanel;

public class PlayerEntryPanel extends EditingPanel<PlayerInfo> {
	private PlayerInfo info;
	private JTextField playerField;
	private JTextField nameField;

	public PlayerEntryPanel(PlayerInfo info) {
		super();
		this.info = info;
		buildUI();
	}

	private void buildUI() {
		setLayout( new BorderLayout() );
		playerField = new JTextField(info.getPlayer());
		nameField = new JTextField(info.getName());
		FormPanel form = new FormPanel();
		form.append("Player",playerField);
		form.append("Name", nameField);
		KeyListener keyListener = new KeyAdapter(){
			@Override
			public void keyTyped(KeyEvent e) {
				fireKeyListeners();
			}
		};
		playerField.addKeyListener(keyListener);
		nameField.addKeyListener(keyListener);
		add(form,BorderLayout.CENTER);
		setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
	}

	protected void fireKeyListeners() {
		info.setPlayer(playerField.getText());
		info.setName(nameField.getText());
		boolean valid = !(StringUtils.isEmpty(info.getPlayer()) || StringUtils.isEmpty(info.getName()));
		fireEditingListener(info, valid);
	}
	
}
