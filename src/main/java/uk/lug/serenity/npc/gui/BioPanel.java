/**
 * 
 */
package uk.lug.serenity.npc.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.InputStream;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ButtonGroup;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.border.LineBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.apache.commons.lang.StringUtils;

import uk.lug.data.DataModel;
import uk.lug.data.DataModelListener;
import uk.lug.gui.archetype.skills.FontUtils;
import uk.lug.gui.gridbag.GridBagException;
import uk.lug.gui.gridbag.GridBagLayoutXML;
import uk.lug.gui.util.CachedImageLoader;
import uk.lug.serenity.npc.managers.ArchetypesManager;
import uk.lug.serenity.npc.model.Person;
import uk.lug.serenity.npc.model.event.SkillChangeEvent;
import uk.lug.serenity.npc.model.event.SkillChangeListener;
import uk.lug.serenity.npc.random.RandomNamer;
import uk.lug.serenity.npc.random.StatProfile;
import uk.lug.serenity.npc.random.archetype.Archetype;

/**
 * $Id$
 * @version $Revision$
 * @author $Author$
 * <p>Panel for controlling name level , profile and randomize button.<
 * 
 */
/**
 * @author Luggy
 *
 */
public class BioPanel extends JPanel 
	implements PropertyChangeListener,SkillChangeListener,DataModelListener<Person> {

	
	public static final String CLEAR_BUTTON = "clearButton";
	private static final Color MONEY_POSITIVE_COLOR = Color.BLACK;
	private static final Color MONEY_NEGATIVE_COLOR = Color.RED;
	public static final String PERSONALITY_FIELD = "personalityField";
	public static final String MONEY_FIELD = "moneyField";
	public static final String FEMALE_BUTTON = "femaleButton";
	public static final String MALE_BUTTON = "maleButton";
	public static final String RANDOMIZE_BUTTON = "randomizeButton";
	public static final String LEVEL_COMBO = "levelCombo";
	public static final String PROFILE_COMBO = "archetypeCombo";
	public static final String RANDOMIZE_NAME_BUTTON = "nameButton";
	public static final String NAME_FIELD = "nameField";
	private static final long serialVersionUID = 1L;
	private static final Icon DICE_ICON = CachedImageLoader.getCachedIcon("images/die_48.png");
	private static final Icon NAME_DICE_ICON = CachedImageLoader.getCachedIcon("images/die_16.png");
	private static final Icon MALE_ICON = CachedImageLoader.getCachedIcon("images/dude1.png");
	private static final Icon MALE_ICON_GHOST = CachedImageLoader.getCachedIcon("images/dude1_ghost.png");
	private static final Icon FEMALE_ICON = CachedImageLoader.getCachedIcon("images/woman3.png");
	private static final Icon FEMALE_ICON_GHOST = CachedImageLoader.getCachedIcon("images/woman3_ghost.png");
	private static final String[] LEVEL_STRINGS = {"Greenhorn","Veteran","Big Damn Hero"};
	private JLabel nameLabel;
	private JTextField nameField;
	private JPanel namePanel;
	private JButton nameButton;
	private JButton randomizeButton;
	private JButton clearButton;
	private JComboBox archetypeCombo;
	private JLabel profileLabel;
	private JLabel levelLabel;
	private JLabel moneyLabel;
	private JLabel moneyField;
	private JComboBox levelCombo;
	private Person dataModel;
	private ButtonGroup genderGroup;
	private JRadioButton maleButton;
	private JRadioButton femaleButton;
	private JPanel malePanel;
	private JPanel femalePanel;	
	private JPanel genderPanel;
	private JLabel statPointsLabel;
	private JLabel skillPointsLabel;
	private JLabel statPointsField;
	private JLabel skillPointsField;
	private JLabel personalityLabel;
	private JTextField personalityField;
	private boolean isAdjusting=false;
	private DataModel<Person> model;

	/**
	 * @param diceAction
	 * Sets the action attached to the dice button. 
	 */
	public void setDiceAction( Action diceAction ) {
		randomizeButton.setAction( diceAction );
		randomizeButton.setEnabled( diceAction!=null );
	}
	
	public void enableDiceAction( boolean b ) {
		randomizeButton.getAction().setEnabled(b);
		randomizeButton.setEnabled( b );
		randomizeButton.repaint();
	}
	
	/**
	 * Action for generating a random name.
	 */
	private Action randomNameAction = new AbstractAction("",NAME_DICE_ICON) {
		private static final long serialVersionUID = 1L;

		public void actionPerformed(ActionEvent ae) {
			nameField.setText( RandomNamer.getRandomName( dataModel.isMale() ) );
			dataModel.setMaleName( RandomNamer.getRandomName(true) );
			dataModel.setFemaleName( RandomNamer.getRandomName(false) );
			nameField.setText( dataModel.getName() );
		}
	};
	
	public BioPanel(DataModel<Person> dataModel ) {
		try {
			makeGUI();
		} catch (GridBagException e) {
			e.printStackTrace();
			System.exit(0);
		}
		model = dataModel;
		model.addDataModelListener( this );
	}
	
	/**
	 * @param newData
	 */
	protected void doPersonChanged(Person newData) {
		setPerson( newData );
	}

	/**
	 * Construct the user interface.
	 * @throws GridBagException 
	 *
	 */
	private void makeGUI() throws GridBagException {
		InputStream instream = this.getClass().getClassLoader()
		.getResourceAsStream("layout/BioPanel.xml");

		GridBagLayoutXML gridbag = new GridBagLayoutXML(instream); 
		setLayout(gridbag);
		
		nameLabel = new JLabel("Name : ");
		nameField = new JTextField(20);
		nameField.setName(NAME_FIELD);
		nameButton = new JButton ( randomNameAction );
		nameButton.setName(RANDOMIZE_NAME_BUTTON);
		nameButton.setMargin( new Insets(0,0,0,0) );
		nameButton.setBorder( new EmptyBorder( new Insets(0,0,0,0) ) ) ;
		namePanel = new JPanel( new BorderLayout() ) ;
		namePanel.add( nameField, BorderLayout.CENTER );
		nameField.getDocument().addDocumentListener( new DocumentListener() {

			public void insertUpdate(DocumentEvent e) {
				doUpdateName();
			}

			public void removeUpdate(DocumentEvent e) {
				doUpdateName();
			}

			public void changedUpdate(DocumentEvent e) {
				doUpdateName();
			}});
		namePanel.add( nameButton, BorderLayout.EAST );
		
		
		profileLabel = new JLabel("Archetype : ");
		archetypeCombo = new JComboBox(ArchetypesManager.getArchetypes().toArray());
		archetypeCombo.setRenderer( new ArchetypesCellRenderer() );		archetypeCombo.setName(PROFILE_COMBO);
		archetypeCombo.setEditable(false);
		
		levelLabel= new JLabel("Level : ");
		levelCombo = new JComboBox( LEVEL_STRINGS );
		levelCombo.setName(LEVEL_COMBO);
		levelCombo.setEditable(false);
		
		randomizeButton = new JButton( DICE_ICON );
		randomizeButton.setName(RANDOMIZE_BUTTON);
		randomizeButton.setHorizontalTextPosition( SwingConstants.CENTER );
		randomizeButton.setVerticalTextPosition( SwingConstants.BOTTOM );
		randomizeButton.setMargin( new Insets(2,2,2,2));
		randomizeButton.setEnabled(false);
		
		clearButton = new JButton("Clear");
		clearButton.setName( CLEAR_BUTTON );
		
		maleButton = new JRadioButton( "Male", MALE_ICON_GHOST );
		maleButton.setName(MALE_BUTTON);
		malePanel =new JPanel( new BorderLayout() );
		malePanel.setBorder( new EtchedBorder(EtchedBorder.LOWERED));
		malePanel.add( maleButton ) ;
		maleButton.addChangeListener( new ChangeListener() {
			public void stateChanged(ChangeEvent arg0) {
				updateGenderDisplay();
			}});
		femaleButton = new JRadioButton( "Female", FEMALE_ICON_GHOST );
		femaleButton.setName(FEMALE_BUTTON);
		femalePanel =new JPanel( new BorderLayout() );
		femalePanel.add( femaleButton ) ;
		femaleButton.addChangeListener( new ChangeListener() {
			public void stateChanged(ChangeEvent arg0) {
				updateGenderDisplay();
			}});

		genderPanel = new JPanel( new GridLayout(1,2) );
		genderGroup = new ButtonGroup();
		genderGroup.add( maleButton );
		genderGroup.add( femaleButton );
		genderPanel.add( malePanel );
		genderPanel.add( femalePanel );
		
		statPointsLabel = new JLabel("Stat Points : ");
		skillPointsLabel = new JLabel("Skill Points : ");
		statPointsField = new JLabel("0/0");
		skillPointsField = new JLabel("0/0");
		
		moneyLabel = new JLabel("Money : ");
		FontUtils.Embolden( moneyLabel );
		moneyField = new JLabel("0 cr");
		moneyField.setName(MONEY_FIELD);
		
		personalityLabel = new JLabel("Personality : ");
		personalityField = new JTextField(30);
		personalityField.setName(PERSONALITY_FIELD);
		
		add(nameLabel, gridbag.getConstraints("name.label") );
		add(namePanel, gridbag.getConstraints("name.field") );
		add(profileLabel, gridbag.getConstraints("profile.label") );
		add(archetypeCombo, gridbag.getConstraints("profile.combo") );
		add(levelLabel, gridbag.getConstraints("level.label") );
		add(levelCombo, gridbag.getConstraints("level.combo") );
		add(new JLabel("Gender : ") , gridbag.getConstraints("gender.label") );
		add(genderPanel, gridbag.getConstraints("gender.panel") );
		add(randomizeButton , gridbag.getConstraints("random.button") );
		add(clearButton, gridbag.getConstraints("clear.button") );
		add(statPointsLabel , gridbag.getConstraints("statpoints.label") );
		add(statPointsField , gridbag.getConstraints("statpoints.field") );
		add(skillPointsLabel , gridbag.getConstraints("skillpoints.label") );
		add(skillPointsField, gridbag.getConstraints("skillpoints.field") );
		add(moneyLabel, gridbag.getConstraints("money.label") );
		add(moneyField, gridbag.getConstraints("money.field") );
		add(personalityLabel, gridbag.getConstraints("personality.label"));
		add(personalityField, gridbag.getConstraints("personality.field"));
		
		levelCombo.addActionListener( new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				doLevelChange();
			}});
		
		personalityField.getDocument().addDocumentListener( new DocumentListener() {

			public void insertUpdate(DocumentEvent e) {
				doUpdatePersonality();
			}

			public void removeUpdate(DocumentEvent e) {
				doUpdatePersonality();
			}

			public void changedUpdate(DocumentEvent e) {
				doUpdatePersonality();
			}});
	}

	/**
	 * Set GUI to show the correct gender and name for that gender. 
	 */
	protected void updateGenderDisplay() {
		if ( maleButton.isSelected() ) {
			maleButton.setIcon( MALE_ICON );
			malePanel.setBorder( new LineBorder( new Color(50,50,50) , 1 ) );
			femaleButton.setIcon( FEMALE_ICON_GHOST );
			femalePanel.setBorder( new EmptyBorder(1, 1,1,1) );
			dataModel.setMale(true);
		} else {
			maleButton.setIcon( MALE_ICON_GHOST );
			malePanel.setBorder( new EmptyBorder(1, 1,1,1) );
			
			femaleButton.setIcon( FEMALE_ICON );
			femalePanel.setBorder( new LineBorder( new Color(50,50,50) , 1 ) );
			dataModel.setMale(false);
		}		
	}

	/**
	 * 
	 */
	protected void doUpdateName() {
		if ( !isAdjusting ) {
			isAdjusting = true;
			dataModel.changeNameTo( nameField.getText() );
			isAdjusting = false;
		}
		
	}

	/**
	 * 
	 */
	protected void doUpdatePersonality() {
		if ( !isAdjusting ) {
			isAdjusting = true;
			dataModel.setPersonalityTemplate( personalityField.getText() );
			isAdjusting = false;
		}
		
	}

	/**
	 * Action on gui level change
	 */
	protected void doLevelChange() {
		if ( dataModel==null ) {
			return;
		}
		switch ( levelCombo.getSelectedIndex() ) {
			case 0 :
				dataModel.setLevel( Person.LEVEL_GREENHORN );
				model.getData().setLevel( Person.LEVEL_GREENHORN );
				break;
			case 1 :
				dataModel.setLevel( Person.LEVEL_VETERAN);
				model.getData().setLevel( Person.LEVEL_VETERAN);
				break;
			case 2 :
				dataModel.setLevel( Person.LEVEL_BIG_DAM_HERO);
				model.getData().setLevel( Person.LEVEL_BIG_DAM_HERO );
				break;
		}
		
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
		isAdjusting=true;
		//Take care of listener issues
		if ( this.dataModel!=null ) {
			this.dataModel.removePropertyChangeListener( this );
			this.dataModel.removeSkillSkillChangeListener( this );
		}
		this.dataModel = person;
		this.dataModel.addPropertyChangeListener( this );
		this.dataModel.addSkillChangeListener( this );

		//name and profile
		nameField.setText( dataModel.getName() );
		nameField.getDocument().addDocumentListener( new DocumentListener() {

			public void insertUpdate(DocumentEvent e) {
				doUpdateName();
			}

			public void removeUpdate(DocumentEvent e) {
				doUpdateName();
			}

			public void changedUpdate(DocumentEvent e) {
				doUpdateName();
			}});
				
		//level
		switch ( person.getLevel() ) {
			default :  
				levelCombo.setSelectedIndex(0);
				break;
			case Person.LEVEL_VETERAN : 
				levelCombo.setSelectedIndex(1);
				break;
			case Person.LEVEL_BIG_DAM_HERO :
				levelCombo.setSelectedIndex(2);
				break;
		}
		
		//gender
		if ( dataModel.isMale() ) {
			maleButton.setSelected(true);
		} else {
			femaleButton.setSelected(true);
		}	
		refreshStatPoints();

		//skill points
		refreshSkillPoints();
		isAdjusting=false;
		
		refreshMoneyDisplay();
		
		if ( StringUtils.isEmpty( dataModel.getPersonalityTemplate() ) ) {
			personalityField.setText("");
		} else {
			personalityField.setText( dataModel.getPersonalityTemplate() );
		}
		personalityField.getDocument().addDocumentListener( new DocumentListener() {

			public void insertUpdate(DocumentEvent e) {
				doUpdatePersonality();
			}

			public void removeUpdate(DocumentEvent e) {
				doUpdatePersonality();
			}

			public void changedUpdate(DocumentEvent e) {
				doUpdatePersonality();
			}});
	}
	
	private void refreshMoneyDisplay() {
		moneyField.setText( getMoneyString() );
		moneyField.setForeground( dataModel.getCurrentMoney()<0  ? MONEY_NEGATIVE_COLOR : MONEY_POSITIVE_COLOR );
	}
	
	/**
	 * Return a string for the person money in the form "current / starting".
	 */
	private String getMoneyString() {
		StringBuilder sb = new StringBuilder(256);
		sb.append( CreditFormat.get().format( dataModel.getCurrentMoney() ) );
		sb.append( " / ");
		sb.append( CreditFormat.get().format( dataModel.getStartingMoney() ) );
		return sb.toString() ;
	}
	
	

	/**
	 * 
	 */
	private void refreshSkillPoints() {
		StringBuilder sb = new StringBuilder(200);
		int start = dataModel.getStartingSkillPoints();
		int used = dataModel.getSkillSheet().getTotalPoints();
		
		int cur = start-used;
		sb.append( cur );
		sb.append("/");
		sb.append( dataModel.getStartingPoints()+20 );
		skillPointsField.setText( sb.toString() );
	}

	/**
	 * 
	 */
	private void refreshStatPoints() {
		//stat points
		dataModel.recomputeCurrentStatPoints();
		StringBuilder sb = new StringBuilder(200);
		sb.append( dataModel.getCurrentStatPoints() );
		sb.append("/");
		sb.append( dataModel.getStartingPoints() );
		statPointsField.setText( sb.toString());
	}

	/**
	 * respond to changes to the datamodel.
	 */
	public void propertyChange(PropertyChangeEvent evt) {
		if ( isAdjusting ) {
			return;
		}
		String property = evt.getPropertyName();
		if ( property.equals( Person.PROPERTY_LEVEL ) ) {
			//Level changed
			int newLevel = ((Integer)evt.getNewValue()).intValue();
			switch ( newLevel ) {
				default :  
					levelCombo.setSelectedIndex(0);
					refreshStatPoints();
					refreshSkillPoints();
					break;
				case Person.LEVEL_VETERAN : 
					levelCombo.setSelectedIndex(1);
					refreshStatPoints();
					refreshSkillPoints();
					break;
				case Person.LEVEL_BIG_DAM_HERO :
					levelCombo.setSelectedIndex(2);
					refreshStatPoints();
					refreshSkillPoints();
					break;
			}
		} else if ( property.equals( Person.PROPERTY_NAME ) ) {
			//Name changed
			nameField.setText( (String) evt.getNewValue() ) ;
		} else if ( property.equals( Person.PROPERTY_STAT_PROFILE ) ) {
			StatProfile newProfile = (StatProfile)evt.getNewValue();
			archetypeCombo.setSelectedItem( newProfile.getName() );
		} else if ( property.equals( Person.PROPERTY_GENDER ) ) {
			if ( ((Boolean)evt.getNewValue()).booleanValue() ) {
				maleButton.setEnabled(true);
				nameField.setText( dataModel.getMaleName() );
			} else {
				femaleButton.setEnabled(true);
				nameField.setText( dataModel.getFemaleName() );
			}
		} else if ( property.equals( Person.PROPERTY_STAT_POINTS ) ) {
			//stat points
			refreshStatPoints();
		} else if ( property.equals( Person.PROPERTY_STAT_POINTS) ) {
			//skill points
			refreshSkillPoints();
		} else if ( property.equals( Person.PROPERTY_CURRENT_MONEY) ) {
			refreshMoneyDisplay();
		} else if ( property.equals( Person.PROPERTY_STARTING_MONEY) ) {
			refreshMoneyDisplay();
		} else if ( property.equals( Person.PROPERTY_PERSONALITY ) ) {
			if ( StringUtils.isEmpty( dataModel.getPersonalityTemplate() ) ) {
				personalityField.setText("");
			} else {
				personalityField.setText( dataModel.getPersonalityTemplate() );
			}
		}
	}

	/* (non-Javadoc)
	 * @see lug.serenity.event.SkillChangeListener#SkillChanged(lug.serenity.event.SkillChangeEvent)
	 */
	public void SkillChanged(SkillChangeEvent evt) {
		refreshSkillPoints();//We only 1 thing to do
	}
	
	/* (non-Javadoc)
	 * @see lug.data.DataModelListener#dataChanged(java.lang.Object, java.lang.Object)
	 */
	public void dataChanged(Person oldData, Person newData) {
		doPersonChanged( newData );
		
	}

	/**
	 * Sets the action attached to the clear button.
	 * @param clearAction
	 */
	public void setClearAction(Action clearAction) {
		clearButton.setAction( clearAction );
	}
	
	/**
	 * @return the currently selected archetype
	 */
	public Archetype getSelectedArchetype() {
		return (Archetype)archetypeCombo.getSelectedItem();
	}

	/**
	 * 
	 */
	public void refreshArchetypes() {
		ArchetypesManager.refreshArchetypes();
		archetypeCombo.removeAllItems();
		for ( Archetype archetype : ArchetypesManager.getArchetypes() ) {
			archetypeCombo.addItem( archetype );
		}
	}

	public int getLevel() {
		return levelCombo.getSelectedIndex();
	}
}
