/**
 * 
 */
package uk.lug.serenity.npc.gui;

import java.awt.BorderLayout;

import uk.lug.data.DataModel;
import uk.lug.gui.archetype.skills.JMemoryFrame;
import uk.lug.serenity.npc.model.Person;
/**
 * Copyright 8 Jul 2007
 * @author Paul Loveridge
 * <p>
 * 
 */
/**
 * 
 */
public class SummaryFrame extends JMemoryFrame {
	private SummaryArea summaryArea ;
	private DataModel<Person> model;
	
	/**
	 * 
	 */
	public SummaryFrame(DataModel<Person> model2) {
		super();
		model = model2;
		setTitle("Character Preview");
		createGUI();
	}

	/**
	 * Construct the user interface.
	 */
	private void createGUI() {
		setLayout( new BorderLayout() );
		summaryArea = new SummaryArea(model);
		add( summaryArea, BorderLayout.CENTER );
		recall();
	}
	
	
	
}
