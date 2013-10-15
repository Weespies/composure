/**
 * 
 */
package uk.lug.serenity.npc.gui;

import java.awt.BorderLayout;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

import uk.lug.data.DataModel;
import uk.lug.data.DataModelListener;
import uk.lug.serenity.npc.model.Person;
import uk.lug.serenity.npc.model.event.SkillChangeEvent;
import uk.lug.serenity.npc.model.event.SkillChangeListener;

/**
 * Copyright 8 Jul 2007
 * @author Paul Loveridge
 * <p>
 * 
 */
/**
 * 
 */
public class SummaryArea extends JPanel implements DataModelListener<Person>, PropertyChangeListener, SkillChangeListener {
	private static final long serialVersionUID = 1L;
	private DataModel<Person> model;
	private JTextArea textArea = new JTextArea();
	private JScrollPane scrollPane;
	
	public SummaryArea(DataModel<Person> model2) {
		super();
		model = model2;
		model.addDataModelListener(this);
		model.getData().addPropertyChangeListener( this );
		model.getData().addSkillChangeListener( this );
		createGUI();
		refreshText();
	}
	
	/**
	 * 
	 */
	private void createGUI() {
		setLayout( new BorderLayout() );
		textArea = new JTextArea();
		textArea.setEditable(false);
		scrollPane = new JScrollPane( textArea );
		add( scrollPane, BorderLayout.CENTER );
	}

	/**
	 * Replace the existing text of this Area with the ascii from
	 * the datamodel.
	 */
	private void refreshText() {
		String txt = model.getData().getAsText().toString();
		final int vert = scrollPane.getVerticalScrollBar().getValue();
		final int horiz = scrollPane.getHorizontalScrollBar().getValue();
		textArea.setText( txt );
		SwingUtilities.invokeLater( new Runnable() {

			public void run() {
				scrollPane.getVerticalScrollBar().setValue( vert );
				scrollPane.getHorizontalScrollBar().setValue( horiz );
			}});
		
	}

	/* (non-Javadoc)
	 * @see lug.data.DataModelListener#dataChanged(java.lang.Object, java.lang.Object)
	 */
	public void dataChanged(Person oldData, Person newData) {
		oldData.removePropertyChangeListener( this );
		oldData.removeSkillSkillChangeListener( this );
		newData.addPropertyChangeListener( this );
		newData.addSkillChangeListener( this );
		refreshText();
	}

	/* (non-Javadoc)
	 * @see java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)
	 */
	public void propertyChange(PropertyChangeEvent arg0) {
		refreshText();
	}

	/* (non-Javadoc)
	 * @see lug.serenity.npc.model.event.SkillChangeListener#SkillChanged(lug.serenity.npc.model.event.SkillChangeEvent)
	 */
	public void SkillChanged(SkillChangeEvent evt) {
		refreshText();		
	}
}
