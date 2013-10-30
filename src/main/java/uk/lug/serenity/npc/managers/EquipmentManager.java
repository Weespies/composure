/**
 * 
 */
package uk.lug.serenity.npc.managers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;

import uk.lug.serenity.npc.model.equipment.Armor;
import uk.lug.serenity.npc.model.equipment.Equipment;
import uk.lug.serenity.npc.model.equipment.Explosive;
import uk.lug.serenity.npc.model.equipment.MeleeWeapon;
import uk.lug.serenity.npc.model.equipment.RangedWeapon;
import uk.lug.serenity.npc.model.skills.SkillSheet;

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
public class EquipmentManager {
	private static final String EXPLOSIVES_MAPPING = "mapping/explosivesMapping.xml";
	private static final String ARMOR_MAPPING = "mapping/armorMapping.xml";
	private static final String MELEE_WEAPON_MAPPING = "mapping/meleeWeaponsMapping.xml";
	private static final String EQUIPMENT_MAPPING = "mapping/equipmentMapping.xml";
	private static final String RANGED_WEAPON_MAPPING = "mapping/rangedWeaponsMapping.xml";
	private static final String ARMOR_RESOURCE = "data/armor.xml";
	private static final String MELEE_WEAPON_RESOURCE = "data/meleeweapons.xml";
	private static final String RANGED_WEAPON_RESOURCE = "data/rangedweapons.xml";
	private static final String EXPLOSIVES_RESOURCE = "data/explosives.xml";
	private static final String EQUIPMENT_RESOURCE = "data/equipment.xml";
	private static ArrayList<Armor> armorList = new ArrayList<Armor>();
	private static ArrayList<MeleeWeapon> meleeList = new ArrayList<MeleeWeapon>();
	private static ArrayList<RangedWeapon> rangedList = new ArrayList<RangedWeapon>();
	private static ArrayList<Explosive> explosivesList = new ArrayList<Explosive>();
	private static ArrayList<Equipment> equipmentList = new ArrayList<Equipment>();

	static {
		try {
			loadArmor();
			loadMeleeWeapons();
			loadRangedWeapons();
			loadExplosives();
			loadEquipment();
		} catch (UnmarshallingException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Read armor list from local file. If local file does not exist, extract
	 * from resource.
	 * 
	 * @throws UnmarshallingException
	 * @throws JDOMException
	 * @throws IOException
	 */
	@SuppressWarnings("unchecked")
	private static void loadArmor() throws UnmarshallingException {
		List<Armor> armor = (List<Armor>) LocalFileController.getInstance().unmarshalResource(ARMOR_RESOURCE, ARMOR_MAPPING);
		armorList.clear();
		armorList.addAll(armor);
	}

	/**
	 * Read melee weapon list from local file. If local file does not exist,
	 * extract from resource.
	 * 
	 * @throws UnmarshallingException
	 * @throws JDOMException
	 * @throws IOException
	 */
	@SuppressWarnings("unchecked")
	private static void loadMeleeWeapons() throws UnmarshallingException {
		List<MeleeWeapon> melee = (List<MeleeWeapon>) LocalFileController.getInstance().unmarshalResource(MELEE_WEAPON_RESOURCE, MELEE_WEAPON_MAPPING);
		meleeList.clear();
		meleeList.addAll(melee);
	}

	/**
	 * Read ranged weapon list from local file. If local file does not exist,
	 * extract from resource.
	 * 
	 * @throws JDOMException
	 * @throws IOException
	 * @throws UnmarshallingException
	 */
	@SuppressWarnings("unchecked")
	private static void loadRangedWeapons() throws UnmarshallingException {
		rangedList.clear();
		List<RangedWeapon> unmarshalled = (List<RangedWeapon>) LocalFileController.getInstance().unmarshalResource(RANGED_WEAPON_RESOURCE,
				RANGED_WEAPON_MAPPING);
		rangedList.addAll(unmarshalled);

	}

	/**
	 * Read explosives list from local file. If local file does not exist,
	 * extract from resource.
	 * 
	 * @throws JDOMException
	 * @throws IOException
	 * @throws UnmarshallingException
	 */
	@SuppressWarnings("unchecked")
	private static void loadExplosives() throws UnmarshallingException {
		List<Explosive> explosives = (List<Explosive>) LocalFileController.getInstance().unmarshalResource(EXPLOSIVES_RESOURCE, EXPLOSIVES_MAPPING);
		explosivesList.clear();
		explosivesList.addAll(explosives);
	}

	/**
	 * Read equipment list from local file. If local file does not exist,
	 * extract from resource.
	 * 
	 * @throws JDOMException
	 * @throws IOException
	 * @throws UnmarshallingException
	 */
	@SuppressWarnings("unchecked")
	private static void loadEquipment() throws UnmarshallingException {
		List<Equipment> equip = (List<Equipment>) LocalFileController.getInstance().unmarshalResource(EQUIPMENT_RESOURCE, EQUIPMENT_MAPPING);
		equipmentList.clear();
		equipmentList.addAll(equip);

	}

	/**
	 * Load an xml document and return the root element.
	 * 
	 * @param resource
	 *            name of the data resource.
	 * @return
	 * @throws IOException
	 * @throws JDOMException
	 */
	private static Element loadFromFile(String resource) throws JDOMException, IOException {
		Document doc = LocalFileController.getInstance().getDocumentResource(resource, true);
		return doc.getRootElement();
	}

	public static void main(String[] args) {

	}

	/**
	 * @return the armorList
	 */
	public static List<Armor> getArmorList() {
		return armorList;
	}

	/**
	 * @return the equipmentList
	 */
	public static List<Equipment> getEquipmentList() {
		return equipmentList;
	}

	/**
	 * @return the explosivesList
	 */
	public static List<Explosive> getExplosivesList() {
		return explosivesList;
	}

	/**
	 * @return the meleeList
	 */
	public static List<MeleeWeapon> getMeleeWeaponList() {
		return meleeList;
	}

	/**
	 * @return the rangedList
	 */
	public static List<RangedWeapon> getRangedWeaponList() {
		return rangedList;
	}

	/**
	 * @return the meleeList
	 */
	public static List<MeleeWeapon> listMeleeWeaponsForSkills(SkillSheet skillSheet) {
		List<MeleeWeapon> ret = new ArrayList<MeleeWeapon>();
		for (MeleeWeapon mw : getMeleeWeaponList()) {
			for (String name : StringUtils.split(mw.getSkill(), ",")) {
				if (name.equals("*") || skillSheet.hasSkill(name)) {
					ret.add(mw);
				}
			}
		}
		return ret;
	}

	public static List<RangedWeapon> listRangedWeaponsForSkills(SkillSheet skillSheet) {
		List<RangedWeapon> ret = new ArrayList<RangedWeapon>();
		for (RangedWeapon rw : getRangedWeaponList()) {
			for (String name : StringUtils.split(rw.getSkill(), ",")) {
				if (name.equals("*") || skillSheet.hasSkill(name)) {
					ret.add(rw);
				}
			}
		}
		return ret;
	}

	public static List<Explosive> listExplosivesForSkills(SkillSheet skillSheet) {
		List<Explosive> ret = new ArrayList<Explosive>();
		for (Explosive ex : getExplosivesList()) {
			for (String name : StringUtils.split(ex.getSkill(), ",")) {
				if (name.equals("*") || skillSheet.hasSkill(name)) {
					ret.add(ex);
				}
			}
		}
		return ret;
	}
}
