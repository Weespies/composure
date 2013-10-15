/**
 * 
 */
package uk.lug.serenity.npc.gui;

import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.JPanel;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.StyleSheet;

import uk.lug.data.DataModel;
import uk.lug.data.DataModelListener;
import uk.lug.gui.HTMLEditorPanel;
import uk.lug.serenity.npc.model.Person;

/**
 * $Id$
 * @version $Revision$
 * @author $Author$
 * <p>
 * 
 */
/**
 * @author Luggy
 *
 */
public class InfoEditorPanel extends JPanel {
	private static final long serialVersionUID = 1L;
	private HTMLEditorPanel editorPanel;
	private Person dataModel;
	private DataModel<Person> model;
	
	public InfoEditorPanel(DataModel<Person> dataModel) {
		editorPanel = new HTMLEditorPanel( new StyleSheet() );
		setLayout(new BorderLayout() );
		add( editorPanel, BorderLayout.CENTER );
		editorPanel.setBackground( Color.WHITE );
		model = dataModel;
		model.addDataModelListener( new DataModelListener<Person>() {

			public void dataChanged(Person oldData, Person newData) {
				doPersonChanged();
			}});	
	}

	/**
	 * @param newData
	 */
	protected void doPersonChanged() {
		editorPanel.setHTMLDocument( model.getData().getInfo() ) ;
	}

	/**
	 * @return the person
	 */
	public Person getPerson() {
		return dataModel;
	}

	/**
	 * @param person the person to set
	 */
	public void setPerson(Person person) {
		dataModel = person;
		if ( dataModel.getInfo()==null ) {
			dataModel.setInfo( new HTMLDocument() );
		}
		editorPanel.setHTMLDocument( dataModel.getInfo() );
	}
		
}

