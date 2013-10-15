package uk.lug.gui;

/*
 * JNumberField.java
 *
 * Created on December 15, 2002, 9:13 PM
 */

import javax.swing.JTextField;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.PlainDocument;

/**
 *
 * @author  dtrusty
 */
public class JNumberField extends JTextField {
	private boolean integersOnly = false;
	private boolean allowNegatives = true;
    
    /**
	 * @return Returns true if negative numbers are allowed .
	 */
	public boolean isAllowNegatives() {
		return allowNegatives;
	}

	/**
	 * @param allowNegatives true if negative numbers are allowed .
	 */
	public void setAllowNegatives(boolean allowNegatives) {
		this.allowNegatives = allowNegatives;
	}

	/**
	 * @return Returns true if only integer numbers are allowed.
	 */
	public boolean isIntegersOnly() {
		return integersOnly;
	}

	/**
	 * @param integersOnly true if only integer numbers are allowed.
	 */
	public void setIntegersOnly(boolean integersOnly) {
		this.integersOnly = integersOnly;
	}

	/** Creates a new instance of JNumberField */
    public JNumberField(int cols) {
         super(cols);
     }
 
     @Override
	protected Document createDefaultModel() {
 	      return new NumberDocument();
     }
 
     class NumberDocument extends PlainDocument {

		@Override
		public void insertString(int offs, String str, AttributeSet a)
				throws BadLocationException {
			if (str == null) {
				return;
			}
			StringBuffer sb = new StringBuffer(str);
			StringBuffer sb2 = new StringBuffer();
			int length = sb.length();
			for (int i = 0; i < length; i++) {
				char c = sb.charAt(i);
				//Allow numberical figures
				if (c >= '0' && c <= '9') {
					sb2.append(c);
				}
				if ( !integersOnly && c=='.' ) {
					//Decimal point
					sb2.append(c);
				}
				if ( allowNegatives && c=='-' && i==0 ) {
					//allow negatives at start
					sb2.append(c);
				}
			}
			super.insertString(offs, sb2.toString(), a);
		}
	}
     
     public int intValue() {
    	 return Integer.parseInt(getText());
     }
     
     public void setText(int n) {
    	 setText( Integer.toString(n) );
     }

    
}
