/**
 * 
 * 
 */
package uk.lug.serenity.npc.gui;

import java.awt.BorderLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.LinkedList;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

import uk.lug.gui.util.CachedImageLoader;
import uk.lug.serenity.npc.model.Person;
import uk.lug.serenity.npc.model.traits.TraitData;
import uk.lug.serenity.npc.model.traits.TraitList;

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
public class TraitListPanel extends JPanel implements PropertyChangeListener {
	private static final long serialVersionUID = 1L;
	private static final Icon DELETE_ICON = CachedImageLoader.getCachedIcon("images/delete.png");
	private static final String NO_ASSETS_MESSAGE = "Character has no assets.";
	private static final String NO_COMPLICATIONS_MESSAGE = "Character has no complications.";
	private TraitList traits ; 
	private LinkedList<TraitPanel> traitPanels; 
	private LinkedList<JButton> delButtons;
	private boolean assets;
	private Person dataModel ;
	private ArrayList<SwapListener<TraitData>> swapListeners = new ArrayList<SwapListener<TraitData>>();
	
	public TraitListPanel( boolean asAssets ) {
		super();
		assets=asAssets;
	}
	

	public TraitListPanel( Person person,boolean asAssets) {
		super();
		dataModel = person;
		assets = asAssets;
		setData( (assets ? person.getAssets() : person.getComplications() ) );
		layoutPanels();
	}
	
	/**
	 * @return the assets
	 */
	public boolean isDisplayedAsAssets() {
		return assets;
	}

	/**
	 * @param assets the assets to set
	 */
	public void setDisplayedAsAssets(boolean assets) {
		this.assets = assets;
	}

	/**
	 * Changes to asset or complication mode.
	 * @param b
	 */	
	public void setPerson( Person p ) {
		if ( dataModel!=null ) {
			dataModel.removePropertyChangeListener( this );
		}
		dataModel = p;
		dataModel.addPropertyChangeListener( this );
		setData( (assets ? dataModel.getAssets() : dataModel.getComplications() ) );
		layoutPanels();
	}
	
	private Action traitSwapAction = new AbstractAction() {
		public void actionPerformed(ActionEvent ae) {
			if ( ae.getSource() instanceof TraitData ) {
				for ( SwapListener<TraitData> traitSwapper : swapListeners ) {
					traitSwapper.swapFor( (TraitData)ae.getSource() );
				}
			}
		}
	};
	
	public void layoutPanels() {
		this.removeAll();
		if ( traitPanels!=null ) {
			traitPanels.clear();
		} else {
			traitPanels = new LinkedList<TraitPanel>();
		}
		if ( traits.size()==0 ) {
			JPanel tmpPanel =new JPanel( new BorderLayout() );
			JLabel noTraitsLabel = new JLabel();
			noTraitsLabel.setText( isDisplayedAsAssets() ? NO_ASSETS_MESSAGE : NO_COMPLICATIONS_MESSAGE );
			tmpPanel.add( noTraitsLabel, BorderLayout.CENTER );
			add( tmpPanel );
		}
		setLayout( new BoxLayout( this, BoxLayout.Y_AXIS ) );
		delButtons = new LinkedList<JButton>();
		for ( final TraitData tdata : traits ) {
			JPanel tmpPanel = new JPanel( new BorderLayout() );
			//panel
			TraitPanel panel = new TraitPanel();
			panel.setName( getPanelName(tdata) );
			traitPanels.add(panel);
			panel.setTraitData( tdata , assets);
			tmpPanel.add( panel, BorderLayout.CENTER );
			panel.setSwapAction( traitSwapAction );
			
			
			//delete button
			JButton delButton = new JButton(DELETE_ICON);
			delButton.setName( panel.getName()+".delete");
			delButton.setMargin( new Insets(0,0,0,0));
			delButton.setBackground( this.getBackground() );
			delButton.setOpaque( true );
			tmpPanel.add( delButton, BorderLayout.EAST );
			
			delButton.addActionListener( new ActionListener() {

				public void actionPerformed(ActionEvent e) {
					deleteTrait( tdata );
				}});
			delButtons.add( delButton );
			add( tmpPanel );
		}
		enableButtons();
		revalidate();
		repaint();
	}
	
	public static final String getPanelName(TraitData tdata ) {
		StringBuilder sb = new StringBuilder(64);
		sb.append(tdata.getName());
		sb.append( tdata.isMajor() ? "Major" : "Minor" );
		sb.append( "Panel" );
		return sb.toString();
	}
	
	/**
	 * Turn off delete buttons if trait count <2 .
	 *
	 */
	protected void enableButtons() {
		for ( JButton jb : delButtons ) {
			jb.setEnabled( traits.size()>1 );
		}
	}
	
	/**
	 * @param tdata
	 */
	protected void deleteTrait(TraitData tdata) {
		traits.remove( tdata );
		int pts = ( tdata.isMajor() ? 4 : 2 );
		if ( assets ) {
			dataModel.setCurrentPoints( dataModel.getCurrentStatPoints() + pts );
		} else {
			dataModel.setCurrentPoints( dataModel.getCurrentStatPoints() - pts );
		}
		layoutPanels();
		revalidate();
		repaint();
		
	}

	/**
	 * @param data
	 */
	private void setData(TraitList data) {
		traits = data;
		if ( assets ) {
			dataModel.getAssets().addListDataListener( new ListDataListener() {

				public void intervalAdded(ListDataEvent arg0) {
					layoutPanels();
				}

				public void intervalRemoved(ListDataEvent arg0) {
					layoutPanels();
				}

				public void contentsChanged(ListDataEvent arg0) {
					layoutPanels();
				}});
		} else {
			dataModel.getComplications().addListDataListener( new ListDataListener() {

				public void intervalAdded(ListDataEvent arg0) {
					layoutPanels();
				}

				public void intervalRemoved(ListDataEvent arg0) {
					layoutPanels();
				}

				public void contentsChanged(ListDataEvent arg0) {
					layoutPanels();
				}});
		}
	}

	/* (non-Javadoc)
	 * @see java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)
	 */
	public void propertyChange(PropertyChangeEvent evt) {
		if ( evt.getPropertyName().equals( Person.PROPERTY_STAT_POINTS ) ) {
			//Disable delete buttons for complications if not enough points
			if ( !assets ) {
				for ( int i=0; i<traits.size();i++ ) {
					int amount = ( traits.get(i).isMajor() ? 4 : 2);
					delButtons.get(i).setEnabled( traits.size()>1 && dataModel.getCurrentStatPoints() >= amount ) ;
				}
			}
		}
	}
	
	public void addSwapListener(SwapListener<TraitData> swapper) {
		swapListeners.add( swapper );
	}
	
	public void removeSwapListener(SwapListener<TraitData> swapper) {
		swapListeners.remove( swapper );
	}
}
