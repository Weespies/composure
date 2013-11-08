package uk.lug.serenity.npc.gui;

import java.awt.Dimension;
import java.io.IOException;

import javax.swing.JFrame;
import javax.swing.RepaintManager;
import javax.swing.UIManager;

import org.jdom.JDOMException;

import uk.lug.control.MainControlPanel;
import uk.lug.dao.handlers.DatabaseSchema;
import uk.lug.gui.archetype.skills.JMemoryFrame;
import uk.lug.gui.pending.PleaseWaitPanel;
import uk.lug.serenity.npc.managers.ArchetypesManager;
import uk.lug.serenity.npc.managers.EquipmentManager;
import uk.lug.serenity.npc.managers.SkillsManager;
import uk.lug.util.SwingHelper;

public class AppLauncher {

	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
//			RepaintManager.setCurrentManager(new CheckThreadViolationRepaintManager());
			DatabaseSchema.init();
			new Thread(new Runnable() {

				public void run() {
					EquipmentManager.getArmorList();
					SkillsManager.getSkills();
					try {
						new ArchetypesManager();
					} catch (JDOMException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}).start();
			SwingHelper.runInEventThread(new Runnable(){

				public void run() {
					JMemoryFrame win = new JMemoryFrame("Composure - Serenity Control");
					win.setName("composure.mainwin");
					win.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
					win.recall(new Dimension(640, 480));
					win.setVisible(true);
					MainControlPanel mcp = new MainControlPanel();
					PleaseWaitPanel glass = new PleaseWaitPanel();
					glass.setVisible(false);
					win.setGlassPane(glass);
					win.getContentPane().add(mcp);
				}});
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
