/* 
 * Copyright (c) 2004/5 Covalent Software Ltd
 * All rights reserved, on this planet and others.
 */
package uk.lug.gui;

import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.text.JTextComponent;

/**
 * $Id$
 * @version $Revision$
 * @author $Author$
 * <p>Attach this listener to a JTextComponent and whenever that
 * component receives the focus the text within that component becomes
 * selected.</p>
 * 
 */
public class AutoSelectTextFocusListener implements FocusListener {

	/**
	 * Called when the component this is attached to gains focus. 
	 * This causes the focused component's text to become selected.
	 */
	public void focusGained(FocusEvent e) {
		Object source = e.getSource();
		if (source instanceof JTextComponent) {
			JTextComponent jtc = (JTextComponent)source;
			String txt = jtc.getText();
			if ( (txt!=null) || (txt.length()>0) ) {
				jtc.setSelectionStart(0);
				jtc.setSelectionEnd(txt.length());
			}
		}

	}

	/**
	 * Called when the component loses focus.  Does nothing.
	 */
	public void focusLost(FocusEvent e) {
		// TODO Auto-generated method stub

	}

}
