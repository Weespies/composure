/**
 * 
 */
package uk.lug.serenity.npc.gui;

import java.text.DecimalFormat;

/**
 * Copyright 18 Jun 2007
 * @author Paul Loveridge
 * <p>
 * 
 */
/**
 * 
 */
public class CreditFormat extends DecimalFormat {
	private static final long serialVersionUID = 1L;
	private static CreditFormat singleton;
	
	/**
	 * 
	 */
	public CreditFormat() {
		super("#.#cr");
	}
	
	public static final CreditFormat get() {
		if ( singleton==null ) {
			singleton = new CreditFormat();
		}
		return singleton;
	}

}
