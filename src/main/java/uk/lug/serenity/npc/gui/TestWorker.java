package uk.lug.serenity.npc.gui;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.reflect.InvocationTargetException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

import org.apache.commons.lang.StringUtils;

public class TestWorker extends JPanel {
	public TestWorker() throws InvocationTargetException, InterruptedException {
		super();
		SwingUtilities.invokeAndWait(new Runnable() {
			
			public void run() {
				buildUI();
			}
		});
	}
	
	private JLabel label;

	protected void buildUI() {
		setLayout(new GridLayout(2,1));
		label = new JLabel("0");
		JButton button = new JButton("Go");
		add(label);
		add(button);
		button.addActionListener( new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
				SwingWorker worker = new SwingWorker<String,Integer>(){

					@Override
					protected String doInBackground() throws Exception {
						this.setProgress(0);
						for ( int i=0;i<10;i++) {
							
							Thread.sleep(1000);
							this.setProgress(i);
						}
						return "FIN";
						
					}};
					worker.addPropertyChangeListener(new PropertyChangeListener() {
						
						public void propertyChange(PropertyChangeEvent evt) {
							if ( StringUtils.equals("progress",evt.getPropertyName()) ) {
								label.setText(Integer.toString((Integer)evt.getNewValue()));
							}
						}
					});
					worker.execute();
			}
		});
	}
	
	public static void main(String[] args) {
		try {
			JFrame win = new JFrame("Test");
			win.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			win.add(new TestWorker());
			win.pack();
			win.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	

}
