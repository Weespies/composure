package uk.lug.gui.fight;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingWorker;

import uk.lug.MutableList;
import uk.lug.dao.handlers.DatabaseSchema;
import uk.lug.fight.RoundRow;
import uk.lug.serenity.npc.model.Person;
import uk.lug.serenity.npc.model.equipment.Armor;
import uk.lug.serenity.npc.model.equipment.Explosive;
import uk.lug.serenity.npc.model.equipment.MeleeWeapon;
import uk.lug.serenity.npc.model.equipment.RangedWeapon;
import uk.lug.util.SwingHelper;

public class DataPanel extends JPanel {
	private DataRowTableModel tableModel;
	private JTable table;
	private MutableList<DataRow> listModel;
	private JPanel armorPanel;

	public DataPanel() {
		super();
		buildUI();
	}

	private void buildUI() {
		listModel = new MutableList<DataRow>();
		setLayout(new BorderLayout());
		tableModel = new DataRowTableModel(listModel);
		table = new JTable(tableModel);
		setPreferredSize(new Dimension(300, 100));

		add(table, BorderLayout.CENTER);
		buildArmorPanel();
		add(armorPanel, BorderLayout.NORTH);
		setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
	}

	private void buildArmorPanel() {
		armorPanel = new JPanel(new FlowLayout(FlowLayout.LEADING));
		armorPanel.add(new JLabel("      "));
		armorPanel.setMinimumSize(new Dimension(50, 30));
		armorPanel.setPreferredSize(new Dimension(50, 30));
	}

	public void set(final RoundRow roundRow) {
		try {
			SwingHelper.runNowInEventThread(new Runnable() {

				public void run() {
					armorPanel.removeAll();
					armorPanel.repaint();
				}
			});
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		new SwingWorker<Void, DataRow>() {

			@Override
			protected Void doInBackground() throws Exception {
				List<DataRow> ret = new ArrayList<DataRow>();
				try {
					Person person = DatabaseSchema.getPersonDao().getForId(roundRow.getPersonId()).getPerson();
					
					for (RangedWeapon rw : person.getRangedWeapons()) {
						ret.add(DataRow.createFrom(person, rw));
					}
					for (MeleeWeapon mw : person.getMeleeWeapons()) {
						ret.add(DataRow.createFrom(person, mw));
					}
					for (Explosive ex : person.getExplosives()) {
						ret.add(DataRow.createFrom(person, ex));
					}
					publish(ret.toArray(new DataRow[ret.size()]));
				} catch (Throwable t) {
					t.printStackTrace();
				}
				return null;
			}

			protected void process(java.util.List<DataRow> chunks) {
				tableModel.replaceAll(chunks);
				table.repaint();
			};
		}.execute();
		new SwingWorker<Void, String>() {

			@Override
			protected Void doInBackground() throws Exception {
				Person person = DatabaseSchema.getPersonDao().getForId(roundRow.getPersonId()).getPerson();
				StringBuilder sb = new StringBuilder();
				DataRow dodge = DataRow.createDodge(person);
				sb.append("Dodge : ");
				sb.append(dodge.getRoll());
				sb.append(" ");
				publish(sb.toString());
				for (Armor a : person.getArmor()) {
					sb = new StringBuilder();
					sb.append(a.getName());
					sb.append(" (");
					sb.append(a.getRating());
					sb.append(" ");
					sb.append(a.getCovers());
					sb.append(")");
					publish(sb.toString());
				}
				return null;
			}

			protected void process(java.util.List<String> chunks) {
				for (String str : chunks) {
					JLabel label = new JLabel(str);
					SwingHelper.boldLabel(label);
					label.setBorder(BorderFactory.createEtchedBorder());
					armorPanel.add(label);
					armorPanel.revalidate();
				}

			};
		}.execute();
	}

	public void clear() {
		SwingHelper.runInEventThread(new Runnable() {

			public void run() {
				tableModel.clear();
				armorPanel.removeAll();
				armorPanel.repaint();
				DataPanel.this.revalidate();
			}
		});
	}
}
