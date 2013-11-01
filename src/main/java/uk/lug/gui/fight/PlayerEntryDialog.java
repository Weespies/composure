package uk.lug.gui.fight;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

import uk.lug.util.SwingHelper;

public class PlayerEntryDialog extends AbstractEditingDialog<PlayerInfo> {

	public PlayerEntryDialog(Component owner, String title, PlayerInfo playerInfo) {
		super(SwingHelper.getOwnerFrame(owner), title, playerInfo);
		buildUI();
	}

	private void buildUI() {

	}

	@Override
	protected void buildEditingPanel() {
		editingPanel = new PlayerEntryPanel(editBean);
	}

	public static void main(String[] args) {
		try {
			final PlayerInfo info = new PlayerInfo();
			
			JFrame win = new JFrame("Test");
			final JButton button = new JButton("Test");
			JPanel panel = new JPanel(new BorderLayout());
			panel.add(button,BorderLayout.CENTER);
			
			win.getContentPane().add(panel);
			
			button.addActionListener(new ActionListener() {
				
				public void actionPerformed(ActionEvent e) {
					PlayerEntryDialog dialog = new PlayerEntryDialog(button, "test dialog", info);
					dialog.setVisible(true);
					if ( dialog.isOk() ) {
						System.out.println("Info : "+info.toString());						
					} else {
						System.out.println("Cancelled.");
					}
				}
			});
			
			win.pack();
			win.setVisible(true);
			win.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
