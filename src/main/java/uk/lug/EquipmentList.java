/**
 * 
 */
package uk.lug;

import uk.lug.serenity.npc.model.equipment.Equipment;

/**
 * Copyright 18 Jun 2007
 * @author Paul Loveridge
 * <p>
 * 
 */
/**
 * 
 */
public class EquipmentList<T extends Equipment> extends MutableList<T> {
	
	/**
	 * @return total cost of all equipment in this list.
	 */
	public double getCostTotal() {
		double ret=0;
		for ( T t : this ) {
			ret += t.getCost();
		}
		return ret;
	}
	
	/**
	 * @return total weight of all equipment in this list.
	 */
	public double getWeightTotal() {
		double ret=0;
		for ( T t : this ) {
			ret += t.getWeight();
		}
		return ret;
	}

}
