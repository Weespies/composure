/* 
 * Copyright (c) 2004/5 Covalent Software Ltd
 * All rights reserved, on this planet and others.
 */
/**
 * 
 */
package uk.lug.gui.models;


/**
 * $Id$
 * @version $Revision$
 * @author $Author$
 * <p>Basic parent class for ObjectFilters.  All this really does is
 * handle the getting and setting of the criteria string and the case 
 * sensitivity flag.  By default, the case sensitivity flag is off. </p>
 * 
 * @author paul.loveridge
 *
 */
public abstract class AbstractObjectFilter<FilterElement extends Object> 
	implements IObjectFilter<FilterElement> {
	protected String criteria = null; 
	
	protected boolean caseSensitive = false;
	
	/**
	 * This is here in case we add parent class functionality later.
	 */
	public AbstractObjectFilter() {	
	}
	
	/**
	 * Construct with case sensitivity option.
	 * @param sensitive TRUE if filtering should be case sensitive.
	 */
	public AbstractObjectFilter( boolean sensitive ) {
		caseSensitive = sensitive;
	}

	/**
	 * @return Returns TRUE if the filtering is case sensitive.
	 */
	public boolean isCaseSensitive() {
		return caseSensitive;
	}

	/**
	 * @param caseSensitive TRUE if the filtering is to be case sensitive , false otherwise.
	 */
	public void setCaseSensitive(boolean caseSensitive) {
		this.caseSensitive = caseSensitive;
	}
	
	/* (non-Javadoc)
	 * @see com.covalent.core.client.beans.objectfilters.IObjectFilter#getCriteria()
	 */
	public String getCriteria() {
		return criteria;
	}

	/* (non-Javadoc)
	 * @see com.covalent.core.client.beans.objectfilters.IObjectFilter#setCriteria(java.lang.String)
	 */
	public void setCriteria(String s) {
		criteria = s;
	}
	
	/**
	 * Takes the given string and attempts to match it against the given criteria.
	 * Will automatically return true if the criteria is null or zero length
	 * @param test string to test.
	 * @return FALSE if the given string matches the criteria
	 */
	protected boolean doesCriteriaMatch(String test) {
		if ( test==null ) {
			return false;
		}
		if ( criteria==null || criteria.length()==0 ) {
			return true;
		}
		
		//Handle case sensitivity by actually compare two temp strings.
		String tmpCriteria = ( isCaseSensitive() ? criteria : criteria.toLowerCase() );
		String tmpTest = ( isCaseSensitive() ? test : test.toLowerCase () );
	
		boolean wildcardBefore = false;
		while ( tmpCriteria.startsWith("*") ) {
			wildcardBefore=true;
			tmpCriteria = tmpCriteria.substring( 1, tmpCriteria.length() );
		}
		boolean wildcardAfter = false;
		while ( tmpCriteria.endsWith("*") ) {
			wildcardAfter=true;
			tmpCriteria = tmpCriteria.substring( 0, tmpCriteria.length()-1 );
		}
		
		if ( tmpCriteria.length()==0 ) {
			return true;
		}
		
		//Test
		boolean match = false;
		if ( wildcardBefore && wildcardAfter ) {
			match = ( tmpTest.indexOf( tmpCriteria) !=-1 );
		} else if ( wildcardBefore ) {
			match = tmpTest.endsWith( tmpCriteria );
		} else {
			match = tmpTest.startsWith( tmpCriteria );		
		}
		return match;
	}
}
