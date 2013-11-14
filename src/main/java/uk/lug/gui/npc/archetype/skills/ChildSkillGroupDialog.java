/**
 * 
 */
package uk.lug.gui.npc.archetype.skills;

import java.awt.BorderLayout;
import java.awt.Container;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.apache.commons.lang.StringUtils;

import uk.lug.gui.npc.selector.SelectorDialog;
import uk.lug.gui.npc.selector.SelectorModel;

/**
 * @author Luggy
 *
 */
public class ChildSkillGroupDialog extends SelectorDialog<String,String> {
	private JPanel namePanel;
	private JTextField nameField;
	private JLabel nameLabel;
	
	/**
	 * @param owner
	 * @param title
	 * @param selectorModel
	 */
	public ChildSkillGroupDialog(Container owner, String title,
			SelectorModel selectorModel) {
		super(owner, title, selectorModel);
	}

	/**
	 * 
	 */
	@Override
	protected void buildAdditional() {
		super.buildAdditional();
		namePanel = new JPanel(new BorderLayout());
		nameLabel = new JLabel("Group Name :");
		namePanel.setBorder( BorderFactory.createEmptyBorder(4,2,4,2));
		nameField= new JTextField(30);
		
		namePanel.add( nameLabel, BorderLayout.WEST);
		namePanel.add( nameField, BorderLayout.CENTER );
		
		nameLabel.setLabelFor( nameField );
		nameLabel.setDisplayedMnemonic('n');
		add( namePanel, BorderLayout.NORTH );
		
		nameField.getDocument().addDocumentListener( new DocumentListener() {

			public void changedUpdate(DocumentEvent e) {
				nameChanged();
			}

			public void insertUpdate(DocumentEvent e) {
				nameChanged();
			}

			public void removeUpdate(DocumentEvent e) {
				nameChanged();
			}});
	}
	
	/**
	 * 
	 */
	protected void nameChanged() {
		super.setOKButton();
	}

	/**
	 * @return the contents of the name field.
	 */
	public String getGroupName() {
		return nameField.getText();
	}
	
	/**
	 * Set the contents of the group name field.
	 * @param str name of the group.
	 */
	public void setGroupName(String str) {
		nameField.setText( str );
	}
	
	/* (non-Javadoc)
	 * @see luggy.gui.selector.SelectorDialog#isAdditionalOK()
	 */
	@Override
	protected boolean isAdditionalOK() {
		return !StringUtils.isEmpty( nameField.getText() ) ;
	}
	

}
