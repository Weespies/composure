/**
 * 
 */
package uk.lug.serenity.npc.gui;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashMap;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;

import uk.lug.data.DataModel;
import uk.lug.data.DataModelListener;
import uk.lug.serenity.npc.model.Person;
import uk.lug.serenity.npc.model.event.SkillChangeEvent;
import uk.lug.serenity.npc.model.event.SkillChangeListener;
import uk.lug.serenity.npc.model.skills.GeneralSkill;
import uk.lug.serenity.npc.model.skills.SkillData;
import uk.lug.serenity.npc.model.skills.SkillSheet;
import uk.lug.serenity.npc.random.Generator;

/**
 * $Id$
 * @version $Revision$
 * @author $Author$
 * <p>
 * 
 */
/**
 * @author Luggy
 *3
 */
public class SkillSheetPanel extends JPanel 
	implements SkillChangeListener, PropertyChangeListener, DataModelListener<Person> {
	public static final String RIGHT_SCROLLER = "rightScroller";
	public static final String CHILD_SKILLS_PANEL = "childSkillsPanel";
	public static final String GENERAL_SKILLS_PANEL = "GeneralSkillsPanel";
	public static final String LEFT_SCROLLER = "leftScroller";
	public static final String PANEL_NAME="generalSkillPanel";
	private static final long serialVersionUID = 1L;
	private Person dataModel;
	private JPanel listPanel;
	private JScrollPane leftScroller;
	private ChildSkillsPanel childPanel;
	private JScrollPane rightScroller;
	private JSplitPane splitPane;
	private boolean initialDataLoaded =false;
	private HashMap<GeneralSkill, GeneralSkillPanel> parentPanels = null;
	private GeneralSkill lastSelected = null;
	private DataModel<Person> model;
	protected int pointsAvailable;
	
	protected Action blankAction = new AbstractAction("Blank Skills"){ 
		private static final long serialVersionUID = 1L;

		public void actionPerformed(ActionEvent ae) {
			model.getData().getSkillSheet().blank();
			setPointsAvailable( 62 );
			updateGUI();
			
		}
	};
	
	
	/**
	 * Construct the panel.
	 */
	public SkillSheetPanel(DataModel<Person> dataModel) {
		model = dataModel;
		model.addDataModelListener( this ); 
		setLayout( new BorderLayout() );
		createGUI();
	}
	
	/**
	 * @param newData
	 */
	protected void doPersonChanged(@SuppressWarnings("unused")
	Person newData) {
	}

	/**
	 * Construct the user interface
	 */
	private void createGUI() {
		setLayout( new BorderLayout() );
		splitPane = new JSplitPane();
		
		//Build left hand side gui
		listPanel = new JPanel();
		listPanel.setLayout( new GridBagLayout());
		leftScroller = new JScrollPane( listPanel );
		
		splitPane.setLeftComponent( leftScroller );
		
		//Build right hand side gui
		childPanel = new ChildSkillsPanel();
		childPanel.setName(CHILD_SKILLS_PANEL);
		rightScroller = new JScrollPane( childPanel );
		rightScroller.setName(RIGHT_SCROLLER);
		splitPane.setRightComponent( rightScroller );
		
		leftScroller.getVerticalScrollBar().setUnitIncrement( 5);
		rightScroller.getVerticalScrollBar().setUnitIncrement( 5);
		
		add( splitPane, BorderLayout.CENTER );
		splitPane.setDividerLocation( 260 );
		
	}

	/**
	 * Builds the user interface now that we have a datamodel.
	 *
	 */
	private void buildGUI() {
		if ( initialDataLoaded  || parentPanels!=null ) {
			updateGUI();
			return;
		}
	
		parentPanels = new HashMap<GeneralSkill, GeneralSkillPanel>();
	
		//Add parent skills to left hand side scroller.
		GridBagConstraints c = new GridBagConstraints(0, 0, 1, 1, 1, 0,
				GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
				new Insets(0, 0, 0, 0), 0, 2);
		for ( GeneralSkill skill : GeneralSkill.values() ) {
			SkillData skillData = dataModel.getSkillSheet().getFor( skill );
			GeneralSkillPanel gsp = new GeneralSkillPanel( skillData );
			gsp.setName( panelName(skill) );
			gsp.setAllowRaised( dataModel.getAvailableSkillPoints()>0 );
			parentPanels.put( skill, gsp );
			listPanel.add( gsp, c);
			c.gridy++;
		}
		listPanel.revalidate();
		listPanel.repaint();
		listPanel.setName(GENERAL_SKILLS_PANEL);
		leftScroller.addMouseListener( new MouseAdapter() {
			/* (non-Javadoc)
			 * @see java.awt.event.MouseAdapter#mousePressed(java.awt.event.MouseEvent)
			 */
			@Override
			public void mousePressed(MouseEvent e) {
				doClickOn( e.getPoint() );
				if ( e.getButton()==InputEvent.BUTTON1_MASK ) {
					
				}
			}
		});
		leftScroller.setName(LEFT_SCROLLER);
	}

	/**
	 * @param skill
	 * @return
	 */
	private String panelName(GeneralSkill skill) {
		return PANEL_NAME+"."+skill.getName();
	}
	
	/**
	 * Respond to mouse click.
	 * @param point Local coordinates of the click.
	 */
	protected void doClickOn(Point point) {
		Point p = leftScroller.getViewport().getViewPosition();
		point.x = point.x + p.x;
		point.y = point.y + p.y;
		if ( parentPanels==null || parentPanels.size()==0 ) {
			return;
		}
		for ( GeneralSkill skill : parentPanels.keySet() ) {
			GeneralSkillPanel gsp = parentPanels.get(skill);
			if ( gsp.getBounds().contains(point) ) {
				setSelected( skill );
				return;
			}
		}
	}

	/**
	 * Update existing gui with new data, rather than create from scratch.
	 */
	private void updateGUI() {
		//Refresh the general skills
		for ( GeneralSkill generalSkill : GeneralSkill.values() ) {
			SkillData data = dataModel.getSkillSheet().getFor( generalSkill );
			if ( parentPanels.get( panelName(generalSkill) )!=null ) {
				parentPanels.get( panelName(generalSkill) ).setSkillData( data);
			}
		}
		listPanel.revalidate();
		listPanel.repaint();
		
		//refresh the child skills if selected
		if ( lastSelected!=null ) {
			childPanel.setSkillData( dataModel.getSkillSheet().getFor( lastSelected ));
			int free = dataModel.getStartingSkillPoints()-dataModel.getSkillSheet().getTotalPoints();
			if ( free>0 ) {
				childPanel.setPointsAvailable( free );
			}
			enableChildAddButton();		
		}
	}

	/**
	 * @param name
	 */
	protected void setSelected(GeneralSkill skill) {
		if ( skill==null ) { 
			GeneralSkillPanel gsp = parentPanels.get( lastSelected ) ;
			if ( gsp!=null ) {
				gsp.setPanelSelected(false);
			}
		}
		if ( lastSelected!=null ) {
			parentPanels.get(lastSelected).setPanelSelected(false);
			parentPanels.get(lastSelected).repaint();
		} 
		GeneralSkillPanel gsp = parentPanels.get( skill ) ;
		if ( gsp!=null ) {
			gsp.setPanelSelected( true );
			gsp.repaint();
			lastSelected = skill;
		}
		childPanel.setSkillData( dataModel.getSkillSheet().getFor( skill ) );
		int free = dataModel.getStartingSkillPoints()-dataModel.getSkillSheet().getTotalPoints();
		if ( free>0 ) {
			childPanel.setPointsAvailable( free );
		}
		enableChildAddButton();
	}

	/**
	 * @return the sheet
	 */
	public SkillSheet getSheet() {
		return dataModel.getSkillSheet();
	}
	
	 
	
	/**
	 * @param dataModel the sheet to set
	 */
	public void setPerson(Person person) {
		//Stop listening to old and busted datamodel.
		if ( dataModel!=null ) {
			this.dataModel.getSkillSheet().removeSkillChangeListener( this );
			this.dataModel.removePropertyChangeListener( this );
		}
		this.dataModel = person;
		if ( parentPanels!=null ) {
			boolean raise = model.getData().getAvailableSkillPoints()>0;
			for ( GeneralSkill skill : parentPanels.keySet() ) {
				SkillData data = dataModel.getSkillSheet().getFor( skill );
				parentPanels.get( skill ).setSkillData( data );
				parentPanels.get( skill ).setAllowRaised( raise );
			}
		}
		
		//Change the model and listen to it.
		
		this.dataModel.getSkillSheet().addSkillChangeListener( this );
		this.dataModel.addPropertyChangeListener( this );
		
		//Update the gui
		buildGUI();
		repaint();

		
	}
	
	public static void main(String[] args) {
		try {
			JFrame win =  new JFrame("SkillPanel test frame");
			win.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
			
			DataModel<Person> model = new DataModel<Person>();
			SkillSheetPanel panel = new SkillSheetPanel(model);
			win.add( panel );
			win.setSize(640,480);
			win.setVisible(true);
			
			//Load test data
			Person p = Generator.getRandomPerson();
			
			model.setData( p );
			Thread.sleep(2000);
			
//			Thread.sleep(2000);
//			Person p2 = Generator.getRandomPerson();
//			panel.setPerson( p2 );
			
			
				
		} catch (Exception e ) {
			e.printStackTrace();
		}
	}
	


	/**
	 * Change availability of points to spend in skills.
	 */
	public void setPointsAvailable( int pts ){
		pointsAvailable = pts;
		for ( GeneralSkill generalSkill : GeneralSkill.values()  ) {
			GeneralSkillPanel gsp = parentPanels.get( generalSkill );
			gsp.setAllowRaised( pts>0 );
		}
		
		if ( lastSelected!=null ) {
			childPanel.setPointsAvailable( pts ) ;
		}
		
	}	

	/* (non-Javadoc)
	 * @see lug.serenity.event.SkillChangeListener#SkillChanged(lug.serenity.event.SkillChangeEvent)
	 */
	public void SkillChanged(SkillChangeEvent evt) {
		if ( evt.getType()==SkillChangeEvent.POINTS_CHANGED ) {
			
			setPointsAvailable( dataModel.getAvailableSkillPoints() );
		}
		if ( evt.getType()==SkillChangeEvent.CHILD_POINTS_CHANGED ) {
			int pts= dataModel.getStartingSkillPoints()-dataModel.getSkillSheet().getTotalPoints();
			setPointsAvailable( pts );
		}
		enableChildAddButton();
	}

	/**
	 * Enable or disable the add skill button inthe child panel as appropriate
	 */
	private void enableChildAddButton() {
		SkillData selected = getSelected();
		if ( childPanel==null || selected==null ) {
			return;
		}
		boolean enabled = selected.getPoints()==6;
		childPanel.setNewSkillActionEnabled( enabled );
		
	}

	/**
	 * @return currently selected skill or null if no skill is selected.
	 */
	private SkillData getSelected() {
		if ( lastSelected==null ) {
			return null;
		}
		return model.getData().getSkillSheet().getFor( lastSelected );
	}
	
	/* (non-Javadoc)
	 * @see java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)
	 */
	public void propertyChange(PropertyChangeEvent evt) {
		if ( parentPanels.containsKey( evt.getPropertyName() ) ) {
			SkillData sdata = dataModel.getSkillSheet().getNamed( evt.getPropertyName() ) ;
			sdata.setPoints( ((Integer)evt.getNewValue()).intValue() );
//			int pts= 20+dataModel.getStaringSkillPoints()-dataModel.getSkillSheet().getTotalPoints();
//			setPointsAvailable( pts );
		}
	}

	/* (non-Javadoc)
	 * @see lug.data.DataModelListener#dataChanged(java.lang.Object, java.lang.Object)
	 */
	public void dataChanged(Person oldData, Person newData) {
		setPerson( newData );
	}
}
