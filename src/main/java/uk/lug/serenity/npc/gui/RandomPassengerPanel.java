/**
 * 
 */
package uk.lug.serenity.npc.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ListCellRenderer;
import javax.swing.border.EmptyBorder;

import uk.lug.MutableList;
import uk.lug.gui.archetype.skills.JMemoryFrame;
import uk.lug.gui.util.CachedImageLoader;
import uk.lug.serenity.npc.random.RandomPassengerGenerator;

/**
 * $Id: This will be filled in on CVS commit $
 * @version $Revision: This will be filled in on CVS commit $
 * @author $Author: This will be filled in on CVS commit $
 * <p>
 * 
 */
/**
 * @author Luggy
 *
 */
public class RandomPassengerPanel extends JPanel {
	private static final long serialVersionUID = 1L;
	private static final Icon DICE_ICON = CachedImageLoader.getCachedIcon("images/die_48.png");
	private JList resultList;
	private JScrollPane resultScroll;
	private JButton newPassengerButton,clearButton;
	private MutableList<String> passengerList;
	
	/** Action for generating a random passenger.*/
	private Action randomAction = new AbstractAction("Random Passegner",DICE_ICON) {
		private static final long serialVersionUID = 1L;

		public void actionPerformed(ActionEvent ae) {
			passengerList.add( RandomPassengerGenerator.getRandomPassengerDescription() );
		}
	};
	
	/** Action for clearing the list.*/
	private Action clearListAction = new AbstractAction("Clear List") {
		private static final long serialVersionUID = 1L;

		public void actionPerformed(ActionEvent ae) {
			passengerList.clear();
		}
	};
	
	public RandomPassengerPanel() {
		makeGUI();
	}

	/**
	 * 
	 */
	private void makeGUI() {
		setLayout( new BorderLayout() );
		passengerList = new MutableList<String>();
		
		resultList = new JList( passengerList );
		resultList.setCellRenderer( new PassengerCellRenderer() );
		resultScroll = new JScrollPane(resultList);
		
		newPassengerButton = new JButton( randomAction ) ;
		clearButton = new JButton( clearListAction );
		JPanel buttonPanel = new JPanel( new GridLayout(1,2)) ;
		buttonPanel.add( newPassengerButton );
		buttonPanel.add( clearButton );
		
		add( buttonPanel, BorderLayout.NORTH );
		add( resultScroll, BorderLayout.CENTER );
	}
	
	class PassengerCellRenderer extends JPanel implements ListCellRenderer { 
		private static final long serialVersionUID = 1L;
		private JTextArea textArea;
		
		public PassengerCellRenderer() {
			setLayout( new BorderLayout() );
			textArea = new JTextArea();
			add( textArea, BorderLayout.CENTER );
			textArea.setLineWrap(true);
			textArea.setWrapStyleWord(true);
//			textArea.setOpaque(false);
			setBorder( new EmptyBorder(5,1,5,10));
		}

		/* (non-Javadoc)
		 * @see javax.swing.ListCellRenderer#getListCellRendererComponent(javax.swing.JList, java.lang.Object, int, boolean, boolean)
		 */
		public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
			Color defBG = ( ((index/2)*2)==index ? new Color(220,220,200) : Color.WHITE );
			Color bg = ( isSelected ? Color.YELLOW: defBG);
			Color fg = ( isSelected ? Color.BLACK : Color.WHITE );
			setForeground( fg );
			setBackground( bg );
			textArea.setBackground( bg );
			textArea.setText( (String)value );
			
			setForeground( Color.BLACK );
			
			super.doLayout();
			return this;
		}
		
	}
	
	public static void main(String[] args) {
		try {
			JFrame win = new JMemoryFrame("Random passenger list");
			win.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
			
			RandomPassengerPanel rpp = new RandomPassengerPanel();
			
			win.add( rpp );
			win.pack();
			win.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
