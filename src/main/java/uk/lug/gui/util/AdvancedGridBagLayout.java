/**
 * 
 */
package uk.lug.gui.util;

import java.awt.Component;
import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * @author Luggy
 *
 */
public class AdvancedGridBagLayout extends GridBagLayout {
	
	/**
	 * Examine the layout constraints for a containers components and remove any 
	 * empty gridy rows  
	 * @param container container to compress.
	 */
	public void compressGridY(Container container) {
		List<Integer> yCoords = new ArrayList<Integer>();
		for (Component component : container.getComponents() ) {
			int gridY = getConstraints(component).gridy;
			int gridHeight = getConstraints(component).gridheight;
			for (int i=0; i<gridHeight; i++ ){
				if ( gridY>0 && !yCoords.contains(gridY+i) ) {
					yCoords.add(gridY+i);
				}
			}
		}
		HashMap<Integer,Integer> shuffled = new HashMap<Integer,Integer>( yCoords.size() );
		Collections.sort( yCoords );
		int newGridY=1;
		for ( int gridy : yCoords ) {
			shuffled.put(gridy,newGridY++);
		}
		for ( Component component : container.getComponents() ) {
			GridBagConstraints gbc = getConstraints( component );
			int gridy = gbc.gridy;
			
			if ( shuffled.containsKey(gridy) ) {
				gbc.gridy = shuffled.get(gridy);
				setConstraints(component, gbc);
			}
		}
	}
	

	
	/**
	 * Return the earliest empty row within the grid bag layout.
	 * @return
	 */
	public int firstEmptyRow(Container container) {
		int y=1;
		while ( hasComponentAtRow(container, y) ) {
			y++;
		}
		return y;
	}
	
	/**
	 * Insert a new grid row at the given row.  Any existing 
	 * components at that row or below are shuffled y+1.
	 * @param container
	 * @param row
	 */
	public void insertAtRow(Container container, int row ) {
		for ( Component component : container.getComponents() ) {
			GridBagConstraints gbc = getConstraints( component );
			if ( gbc.gridy>=row ) {
				gbc.gridy++;
				setConstraints(component, gbc);
			}
		}
	}
	
	/**
	 * True if any gridbagconstraint exists within the given row.
	 * @param y
	 * @return
	 */
	public boolean hasComponentAtRow(Container container, int y) {
		int gridy=0;
		int gridHeight=0;
		for ( Component component : container.getComponents() ) {
			GridBagConstraints gbc = getConstraints( component );
			gridy = gbc.gridy;
			gridHeight = gbc.gridheight;
			if ( gridy>=y && gridy+gridHeight>y ) {
				return true;
			}
		}
		return false;
	}
}
