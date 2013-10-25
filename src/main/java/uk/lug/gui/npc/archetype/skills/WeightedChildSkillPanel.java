/**
 * 
 */
package uk.lug.gui.npc.archetype.skills;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragGestureListener;
import java.awt.dnd.DragSource;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import uk.lug.gui.util.CachedImageLoader;
import uk.lug.gui.util.ColorUtils;
import uk.lug.serenity.npc.gui.controls.JButtonRow;
import uk.lug.serenity.npc.random.archetype.skills.ExclusiveChildSkillGroup;
import uk.lug.serenity.npc.random.archetype.skills.WeightedChildSkill;

/**
 * @author Luggy
 *
 */
public class WeightedChildSkillPanel extends JPanel implements DragGestureListener {
	private static final float DRAG_DARKEN_AMOUNT = 0.15f;
	public static final Dimension SLIDER_MIN_SIZE = new Dimension(120,14);
	private static final Color TOP_TITLE = Color.white;
	private static final Color BOTTOM_TITLE = new Color( 0xd8d7a8 );
	private static final Color DRAG_TOP_TITLE = ColorUtils.darken( Color.white, DRAG_DARKEN_AMOUNT );
	private static final Color DRAG_BOTTOM_TITLE = ColorUtils.darken( new Color( 0xd8d7a8 ),DRAG_DARKEN_AMOUNT);
	private static final Insets BUTTON_INSETS = new Insets(0,1,0,1);
	private static final Icon DELETE_ICON = CachedImageLoader.getCachedIcon("images/delete.png");
	private static final Icon DELETE_ICON_CLICKED = CachedImageLoader.getCachedIcon("images/delete_clicked.png");
	public static final String LABEL_NAME_PREFIX = "label_";
	protected static final int SLIDER_MAX = 7;
	
	protected WeightedChildSkill dataModel;
	private JPanel titlePanel;	
	protected JButtonRow slider;
	
	private List<DeleteListener> deleteListeners = new ArrayList<DeleteListener>();
	
	private Action deleteGroupAction = new AbstractAction("",DELETE_ICON) {
		public void actionPerformed(ActionEvent event) {
			performDelete();
		}
	};
	private JButton deleteGroupButton;
	private JLabel titleLabel;
	private GradientPanelUI gradientPanelUI;
	
	/**
	 * Construct a child skil group panel
	 * @param wcs
	 */
	public WeightedChildSkillPanel(WeightedChildSkill wcs) {
		super();
		this.dataModel = wcs;
		build();
		setPreferredSize( new Dimension(calculatePreferredWidth(),38) );
		DragSource ds = new DragSource();
		ds.createDefaultDragGestureRecognizer(this, DnDConstants.ACTION_MOVE, this);
	}

	/**
	 * @return
	 */
	private int calculatePreferredWidth() {
		if ( titleLabel.getPreferredSize().width>200 ) {
			return 25+titleLabel.getPreferredSize().width;
		}
		return 225;
	}

	/**
	 * 
	 */
	private void build() {
		setLayout( new GridBagLayout() ) ;
		buildTitlePanel();
		add(titlePanel, new GridBagConstraints(0, 0, 3, 1, 1, 1,
				GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
				new Insets(0, 0, 0, 0), 0, 0));
	}

	/**
	 * 
	 */
	private void buildTitlePanel() {
		titlePanel = new JPanel();
		titlePanel.setLayout( new GridBagLayout() );
		
		titleLabel = new JLabel( dataModel.getSkillName() );
		titleLabel.setForeground( getTitleTextColor() );
		titleLabel.setHorizontalAlignment( SwingConstants.LEFT );
		titlePanel.add( titleLabel, new GridBagConstraints(0,0, 1,1, 1,1, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0,2,0,2),0,0) );
		gradientPanelUI = new GradientPanelUI( TOP_TITLE, BOTTOM_TITLE );
		titlePanel.setUI( gradientPanelUI );
		titlePanel.setPreferredSize( new Dimension(100,38) );
		
		//Bias slider
		slider = new JButtonRow(1, SLIDER_MAX);
		slider.setOpaque(false);
		slider.setMinimumSize( SLIDER_MIN_SIZE );
		slider.setPreferredSize( SLIDER_MIN_SIZE );
		slider.setValue( dataModel.getWeighting().getValue() );
		titlePanel.add( slider, new GridBagConstraints(0,1, 1,1, 0,0, GridBagConstraints.WEST, GridBagConstraints.NONE, BUTTON_INSETS,0,0) );
		
		slider.addChangeListener( new ChangeListener() {

			public void stateChanged(ChangeEvent e) {
				dataModel.getWeighting().setValue( slider.getValue() );
			}});
		
		//Buttons
		deleteGroupButton = new EmptyButton( deleteGroupAction );
		deleteGroupButton.setPressedIcon(DELETE_ICON_CLICKED );
		titlePanel.add( deleteGroupButton, new GridBagConstraints(3,1, 1,1, 0,0, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0,8,0,1),0,0));
	}
		
	/**
	 * Determine if the title color should be black or white.
	 * @return
	 */
	private Color getTitleTextColor() {
		int greyTop = ColorUtils.getGreyValue( TOP_TITLE ) ;
		int greyBottom = ColorUtils.getGreyValue( BOTTOM_TITLE ) ;
		int greyAvg = (greyTop+greyBottom)/2;
		return ( greyAvg>128 ? Color.black : Color.white );
	}
	
	/**
	 * Wrap the current weighted child skill into a transferable object.
	 * @return
	 */
	private Transferable makeTransferable() {
		SkillTransferable ret = new SkillTransferable(dataModel);
		return ret;
	}
	
	/* (non-Javadoc)
	 * @see java.awt.dnd.DragGestureListener#dragGestureRecognized(java.awt.dnd.DragGestureEvent)
	 */
	public void dragGestureRecognized(DragGestureEvent dge) {
		Cursor cursor=null;
		if ( dge.getDragAction()==DnDConstants.ACTION_MOVE) {
			cursor=DragSource.DefaultMoveDrop;
		}
		dge.startDrag(cursor,makeTransferable() );
		gradientPanelUI.setBottomColor(DRAG_BOTTOM_TITLE);
		gradientPanelUI.setTopColor(DRAG_TOP_TITLE);
		repaint();
	}
	
	/**
	 * Restore the colour when the drop is completed.
	 */
	public void dropFailed() {
		gradientPanelUI.setBottomColor(BOTTOM_TITLE);
		gradientPanelUI.setTopColor(TOP_TITLE);
		repaint();
	}

	/* (non-Javadoc)
	 * @see java.awt.datatransfer.Transferable#getTransferData(java.awt.datatransfer.DataFlavor)
	 */
	public Object getTransferData(DataFlavor flavor)
			throws UnsupportedFlavorException, IOException {
		return WeightedChildSkillFlavor.get();
	}

	/* (non-Javadoc)
	 * @see java.awt.datatransfer.Transferable#getTransferDataFlavors()
	 */
	public DataFlavor[] getTransferDataFlavors() {
		return new DataFlavor[0];
	}

	/* (non-Javadoc)
	 * @see java.awt.datatransfer.Transferable#isDataFlavorSupported(java.awt.datatransfer.DataFlavor)
	 */
	public boolean isDataFlavorSupported(DataFlavor flavor) {
		return false;
	}
	
	/**
	 * Add a listener to be notified when the delete button is pressed.
	 */
	public void addDeleteListener( DeleteListener listener ) {
		deleteListeners.add( listener );
	}
	
	/**
	 * Remove a listener to no longer be notified when the delete button is pressed.
	 */
	public void removeDeleteListener( DeleteListener listener ) {
		deleteListeners.remove( listener );
	}


	private void performDelete() {
		for ( DeleteListener dl : deleteListeners ) {
			dl.delete( dataModel.getSkillName() );
		}
	}
}
