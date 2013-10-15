/**
 * 
 */
package uk.lug.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.border.LineBorder;

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
public class FoldingPanel extends JPanel {
	public static final Icon UNFOLD_ICON = CachedImageLoader.getCachedIcon("images/arrow_right.png");
	public static final Icon FOLD_ICON = CachedImageLoader.getCachedIcon("images/arrow_down.png");
	private JComponent unfoldedView;
	private Component titleComponent;
	private JPanel titlePanel;
	private JButton foldButton;
	private Border titleBorder = null;
	private Border unfoldedBorder = null;
	private boolean locked = false;
	
	/**
	 * Action called to flip the folded/unfolded state.
	 */
	private Action foldAction = new AbstractAction("", UNFOLD_ICON ) {
		public void actionPerformed(ActionEvent arg0) {
			doFlip();
		}		
	};

	/**
	 * @return the current border around the title component ( not including the icon )
	 */
	public Border getTitleBorder() {
		return titleBorder;
	}

	/**
	 * @param titleBorder Set the border around the title component ( not including the icon )
	 */
	public void setTitleBorder(Border titleBorder) {
		this.titleBorder = titleBorder;
		titlePanel.setBorder( titleBorder );
		titlePanel.revalidate();
		titlePanel.repaint();
	}

	/**
	 * @return the border around the unfolded component
	 */
	public Border getUnfoldedBorder() {
		return unfoldedBorder;
	}

	/**
	 * sets border around the unfolded component
	 */
	public void setUnfoldedBorder(Border unfoldedBorder) {
		this.unfoldedBorder = unfoldedBorder;
		unfoldedView.setBorder( unfoldedBorder );
		unfoldedView.revalidate();
		unfoldedView.repaint();
	}

	/**
	 * Construct a folding panel, which starts out as folded.
	 * @param unfoldedView the view to show when the component is unfolded
	 * @param titleComponent the component to display next to the fold/unfold icon as the title.
	 */
	public FoldingPanel(Component titleComponent,JComponent unfoldedView) {
		this.unfoldedView = unfoldedView;
		this.titleComponent = titleComponent;
		createGUI();
		
		
	}

	/**
	 * Cause the icon to fold if unfolded or unfold if folded.
	 */
	public void doFlip() {
		setFolded( unfoldedView.isVisible() );
	}
	
	/**
	 * Set the folded state of the panel.
	 * @param folded TRUE to fold the panel, FALSE to unfold it.
	 */
	public void setFolded( boolean folded ) {
		if ( unfoldedView.isVisible()!=folded ) {
			return;
		}
		unfoldedView.setVisible( !folded) ;
		foldAction.putValue( Action.SMALL_ICON, ( !folded ? FOLD_ICON : UNFOLD_ICON ) );
		revalidate();
		repaint();
	}

	/**
	 * Construct the panel 
	 */
	private void createGUI() {
		titlePanel= new JPanel( new BorderLayout() );
		
		foldButton = new JButton( foldAction );
		foldButton.setOpaque(false);
		foldButton.setMargin( new Insets(0,0,0,0));
		foldButton.setBackground( null );
		foldButton.setContentAreaFilled(false);
		foldButton.setBorderPainted(false);
		
		titlePanel.add( titleComponent, BorderLayout.CENTER );
		titlePanel.add( foldButton , BorderLayout.WEST );
		
		setLayout( new BorderLayout() );
		add( titlePanel, BorderLayout.NORTH );
		add( unfoldedView,BorderLayout.CENTER);
		
		unfoldedView.setBorder( unfoldedBorder );
		titlePanel.setBorder( titleBorder );
		unfoldedView.setVisible(false);
		
	}

	/**
	 * Returns the panel containing the title component and icon .
	 */
	public JPanel getTitlePanel() {
		return titlePanel;
	}

	/**
	 * @param titlePanel the titlePanel to set,  If you do this, you'll
	 * need to provided your own control to flip/unflip the panel and call
	 * doFlip() or setFolded() as required.
	 */
	public void setTitlePanel(JPanel titlePanel) {
		this.titlePanel = titlePanel;
		revalidate();
		repaint();
	}

	/**
	 * @return the component shown when this panel is unfolded.
	 */
	public Component getUnfoldedView() {
		return unfoldedView;
	}

	/**
	 * sets the view to show when this panel is unfolded.
	 */
	public void setUnfoldedView(JComponent unfoldedView) {
		this.unfoldedView = unfoldedView;
		revalidate();
		repaint();
	}
	
	/**
	 * @return TRUE if this panels fold/unfold state cannot be changed by user interaction.
	 */
	public boolean isLocked() {
		return locked;
	}

	/**
	 * @param locked Locks or unlocks the folding process.
	 */
	public void setLocked(boolean locked) {
		this.locked = locked;
		foldAction.setEnabled( !this.locked );
	}
}
