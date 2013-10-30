package uk.lug.serenity.npc.gui;

import java.util.List;

import javax.swing.JFrame;

import uk.lug.control.MainControlPanel;
import uk.lug.control.PersonTablePanel;
import uk.lug.dao.handlers.DatabaseSchema;
import uk.lug.dao.records.PersonRecord;
import uk.lug.gui.archetype.skills.JMemoryFrame;
import uk.lug.serenity.npc.managers.EquipmentManager;
import uk.lug.serenity.npc.managers.SkillsManager;

public class AppLauncher {

	public static void main(String[] args) {
		try {
			DatabaseSchema.init();
			new Thread(new Runnable() {

				public void run() {
					EquipmentManager.getArmorList();
					SkillsManager.getSkills();
				}
			}).start();
			JMemoryFrame win = new JMemoryFrame("Composure - Serenity Control");
			win.setName("composure.mainwin");
			win.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			win.setSize(640, 480);
			win.setVisible(true);
			MainControlPanel mcp = new MainControlPanel();
			win.getContentPane().add(mcp);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
