/**
 * 
 */
package uk.lug.serenity.npc.model.equipment;

import javax.swing.ListCellRenderer;

import org.apache.commons.lang.StringUtils;
import org.jdom.Element;

import uk.lug.serenity.npc.gui.equipment.EquipmentCellRenderer;

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
public class Equipment {
	protected String name;
	protected EquipmentType type;
	protected double cost;
	protected double weight;
	protected Availability availability =Availability.EVERYWHERE;
	
	/**
	 * @return the cost
	 */
	public double getCost() {
		return cost;
	}
	
	/**
	 * @param cost the cost to set
	 */
	public void setCost(double cost) {
		this.cost = cost;
	}
	
	/**
	 * @return the weight
	 */
	public double getWeight() {
		return weight;
	}
	
	/**
	 * @param weight the weight to set
	 */
	public void setWeight(double weight) {
		this.weight = weight;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	/**
	 * Return object as an XML element
	 */
	public Element getXML() {
		Element ret = new Element("equipment");
		ret.setAttribute("name",name);
		ret.setAttribute("cost", Double.toString(cost) );
		ret.setAttribute("weight", Double.toString(weight) );
		if ( availability!=null ) {
			ret.setAttribute("availability", availability.getCode() );
		}
		if ( type!=null ) {
			ret.setAttribute("type", type.getName() );
		}
		return ret;
	}
	
	/**
	 * Set data from an XML element.
	 */
	public void setXML(Element xml ) {
		String nstr= xml.getAttributeValue("name");
		String cstr= xml.getAttributeValue("cost");
		String wstr= xml.getAttributeValue("weight");
		String astr= xml.getAttributeValue("availability");
		String tstr= xml.getAttributeValue("type");
		name = (StringUtils.isEmpty(nstr) ? null : nstr );
		cost = (StringUtils.isEmpty(cstr) ? 0d : Double.parseDouble( cstr ) );
		weight = (StringUtils.isEmpty(wstr) ? 0d : Double.parseDouble( wstr ) );
		type = EquipmentType.getForName( tstr );
		availability=null;
		if ( !StringUtils.isEmpty(astr) ) {
			availability = Availability.getForCode( astr );
		}
		if ( availability==null ) {
			availability= Availability.EVERYWHERE;
		}
		
	}

	/**
	 * @return the availability
	 */
	public Availability getAvailability() {
		return availability;
	}

	/**
	 * @param availability the availability to set
	 */
	public void setAvailability(Availability availability) {
		this.availability = availability;
	}

	/**
	 * @param name
	 * @param cost
	 * @param weight
	 * @param availability
	 */
	public Equipment(String name, double cost, double weight, Availability availability) {
		super();
		this.name = name;
		this.cost = cost;
		this.weight = weight;
		this.availability = availability;
	}
	
	public Equipment(Element xml ) {
		setXML( xml );
	}

	/**
	 * Empty constructor
	 */
	public Equipment() {
	}
	
	@Override
	public String toString() {
		StringBuilder sb= new StringBuilder(256);
		sb.append( name ) ;
		sb.append( " (");
		sb.append( availability.getDescription() );
		sb.append( ")");
		return sb.toString();
	}
	
	/**
	 * Returns the renderer for displaying the equipment in a list.
	 * @return
	 */
	public static ListCellRenderer getCellRenderer() {
		return new EquipmentCellRenderer();
	}

	/**
	 * @return the type
	 */
	public EquipmentType getType() {
		return type;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(EquipmentType type) {
		this.type = type;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if ( !(obj instanceof Equipment) ) {
			return false;
		}
		Equipment eq = (Equipment)obj;
		if ( !StringUtils.equalsIgnoreCase( eq.getName(), getName() ) ) {
			return false;
		}
		if ( eq.getAvailability()!=getAvailability() ) {
			return false;
		}
		if ( eq.getCost()!=getCost() ) {
			return false;
		}
		if ( eq.getType()!=getType() ) {
			return false;
		}
		if ( eq.getWeight()!=getWeight() ) {
			return false;
		}
		return true;
	}
	
//	/**
//	 * @return Equipment type's name.
//	 */
//	public String getTypeName() {
//		return type.getName();
//	}
//	
//	/**
//	 * Sets equipment type by name 
//	 */
//	public void setTypeName(String str){
//		type = EquipmentType.getForName(str);
//	}
//	
//	public String getAvailabilityCode() {
//		return availability.getCode();
//	}
//	
//	public void setAvailabilityCode(String str) {
//		availability = Availability.valueOf(str);
//	}
}

