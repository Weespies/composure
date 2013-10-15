/**
 * 
 */
package uk.lug.gui.selector;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

import uk.lug.gui.CachedImageLoader;

/**
 * @author Luggy
 *
 */
public class SelectorDialog<L extends Comparable<L>, R extends Comparable<R>> extends JDialog implements ListDataListener {
	private static final String DIALOG_ESCAPE = "dialogEscape";
	private static final KeyStroke ESCAPE_KEYSTROKE = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0, false);
	private static final Dimension SELECTOR_PREF_SIZE = new Dimension(300,200);
	private SelectorModel<L,R> selectorModel;
	private Selector<L,R> selector;
	private Icon OK_ICON = CachedImageLoader.getCachedIcon("images/check.png");
	private Icon CANCEL_ICON = CachedImageLoader.getCachedIcon("images/delete.png");
	private boolean ok =false;
	private Container parentContainer= null;
	private JPanel dialogPanel;
	
	
	Action okAction = new AbstractAction("OK", OK_ICON) {
		public void actionPerformed(ActionEvent ae) {
			ok=true;
			setVisible(false);
		}
	};
	
	Action cancelAction = new AbstractAction("Cancel", CANCEL_ICON) {
		public void actionPerformed(ActionEvent ae) {
			ok=false;
			setVisible(false);
		}
	};
	
	private JPanel okCancelPanel;
	private JButton cancelButton;
	private JButton okButton;
	
	/**
	 * Construct a selector with a pre-defined selector model.
	 * @param selectorModel
	 */
	public SelectorDialog(SelectorModel<L, R> selectorModel) {
		this.selectorModel = selectorModel;
		buildGUI();
		setVisible(true);
	}	
	
	/**
	 * Construct a selector with a pre-defined selector model.
	 * @param selectorModel
	 */
	public SelectorDialog(String title, SelectorModel<L, R> selectorModel) {
		this.selectorModel = selectorModel;
		buildGUI();
		setTitle(title);
		setVisible(true);
	}	
	
	/**
	 * @param owner
	 * @param title
	 */
	public SelectorDialog(Dialog owner, String title,SelectorModel<L, R> selectorModel) {
		super(owner, title);
		parentContainer = owner;
		this.selectorModel = selectorModel;
		buildGUI();
		setTitle( title );
		setVisible(true);
	}
	
	/**
	 * @param owner
	 * @param title
	 */
	public SelectorDialog(Container owner, String title,SelectorModel<L, R> selectorModel) {
		super();
		parentContainer = owner;
		this.selectorModel = selectorModel;
		buildGUI();
		setTitle( title );
		setVisible(true);
	}

	/**
	 * @param owner
	 */
	public SelectorDialog(Dialog owner, SelectorModel<L, R> selectorModel) {
		super(owner);
		parentContainer = owner;
		this.selectorModel = selectorModel;
		buildGUI();
		setVisible(true);
	}
	
	/**
	 * @param owner
	 * @param title
	 */
	public SelectorDialog(Frame owner, String title, SelectorModel<L, R> selectorModel) {
		super(owner, title);
		parentContainer = owner;
		this.selectorModel = selectorModel;
		buildGUI();
		setTitle(title);
		setVisible(true);
	}

	/**
	 * @param owner
	 */
	public SelectorDialog(Frame owner, SelectorModel<L, R> selectorModel) {
		super(owner);
		parentContainer = owner;
		this.selectorModel = selectorModel;
		buildGUI();
		setVisible(true);
	}

	/**
	 * 
	 */
	private void buildGUI() {
		super.setModal(true);
		setLayout ( new BorderLayout() );
		
		okCancelPanel = new JPanel( new FlowLayout(FlowLayout.RIGHT, 10,4) );
		selector = new Selector<L,R>(selectorModel);
		selector.setPreferredSize( SELECTOR_PREF_SIZE ) ;
		add( selector, BorderLayout.CENTER );
		
		okButton = new JButton( okAction );
		cancelButton = new JButton( cancelAction );
		okCancelPanel.add( okButton  );
		okCancelPanel.add( cancelButton );
		okCancelPanel.setBorder( BorderFactory.createEmptyBorder(4,40,4,0));
		add( okCancelPanel, BorderLayout.SOUTH );
		
		buildAdditional();
		
		pack();
		center();
		
		getRootPane().getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(ESCAPE_KEYSTROKE,DIALOG_ESCAPE);
		getRootPane().getActionMap().put(DIALOG_ESCAPE, cancelAction);
		
		selectorModel.getSelectedList().addListDataListener( this );
		okAction.setEnabled( selectorModel.hasSelectionChanged() );
	}
	
	/**
	 * Override this to add additional editor dialog components.
	 */
	protected void buildAdditional() {
	}
	
	/**
	 * @return true if there are no additional components or they are in a fit state for OK.
	 */
	protected boolean  isAdditionalOK() {
		return true;
	}
	
	
	private void center() {
		int parentX=0;
		int parentY=0;
		Dimension parentSize = Toolkit.getDefaultToolkit().getScreenSize();
		if ( parentContainer!=null ) {
			parentSize = parentContainer.getSize();
			parentX = parentContainer.getX();
			
			parentY = parentContainer.getY();
		}
		
		int x = parentX+(parentSize.width/2)-(getWidth()/2);
		int y = parentY+(parentSize.height/2)-(getHeight()/2);
		setLocation(x,y);
	}
	
	/**
	 * @return the ok
	 */
	public boolean isOk() {
		return ok;
	}
	
	protected void setOKButton() {
		okAction.setEnabled( isAdditionalOK() && selectorModel.hasSelectionChanged() );
	}

	/* (non-Javadoc)
	 * @see javax.swing.event.ListDataListener#contentsChanged(javax.swing.event.ListDataEvent)
	 */
	public void contentsChanged(ListDataEvent e) {
		setOKButton();
	}

	/* (non-Javadoc)
	 * @see javax.swing.event.ListDataListener#intervalAdded(javax.swing.event.ListDataEvent)
	 */
	public void intervalAdded(ListDataEvent e) {
		setOKButton();
	}

	/* (non-Javadoc)
	 * @see javax.swing.event.ListDataListener#intervalRemoved(javax.swing.event.ListDataEvent)
	 */
	public void intervalRemoved(ListDataEvent e) {
		setOKButton();
	}

	/**
	 * @return the selector used in this dialog.
	 */
	protected Selector getSelectorComponent() {
		return selector;
	}
}
