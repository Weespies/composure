/**
 * 
 */
package uk.lug.serenity.npc.model;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.IOException;
import java.io.Serializable;
import java.io.StringWriter;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.HTMLWriter;

import org.apache.commons.lang.StringUtils;
import org.jdom.Element;

import uk.lug.EquipmentList;
import uk.lug.MutableList;
import uk.lug.serenity.npc.model.equipment.Armor;
import uk.lug.serenity.npc.model.equipment.Equipment;
import uk.lug.serenity.npc.model.equipment.Explosive;
import uk.lug.serenity.npc.model.equipment.MeleeWeapon;
import uk.lug.serenity.npc.model.equipment.RangedWeapon;
import uk.lug.serenity.npc.model.event.SkillChangeListener;
import uk.lug.serenity.npc.model.skills.SkillSheet;
import uk.lug.serenity.npc.model.stats.CharacterStats;
import uk.lug.serenity.npc.model.stats.DerivedStat;
import uk.lug.serenity.npc.model.stats.MainStat;
import uk.lug.serenity.npc.model.stats.NamedStat;
import uk.lug.serenity.npc.model.stats.StepStat;
import uk.lug.serenity.npc.model.traits.TraitData;
import uk.lug.serenity.npc.model.traits.TraitList;
import uk.lug.serenity.npc.model.traits.TraitType;
import uk.lug.serenity.npc.random.RandomNamer;
import uk.lug.util.JDOMUtils;
import uk.lug.util.RandomFactory;

/**
 * $Id$
 * @version $Revision$
 * @author $Author$
 * <p>Base data model for holding character data.</p>
 * 
 */
/**
 * @author Luggy
 * 
 */
public class Person implements ListDataListener, DocumentListener, Serializable {
	private static final long serialVersionUID = 1l;
	private static final String INFO_PROPERTY = "Info";
	public static final String PROPERTY_STAT_POINTS = "statpoints";
	public static final String PROPERTY_SKILL_POINTS = "skillpoints";
	public static final String PROPERTY_STAT_PROFILE = "statprofile";
	public static final String PROPERTY_LEVEL = "level";
	public static final String PROPERTY_NAME = "name";
	public static final String PROPERTY_GENDER = "gender";
	public static final String PROPERTY_STARTING_MONEY = "startingmoney";
	public static final String PROPERTY_CURRENT_MONEY = "currentmoney";
	public static final String PROPERTY_PERSONALITY = "currentmoney";
	public static final String PROPERTY_ARCHETYPE = "archetype";

	public static final int LEVEL_GREENHORN = 1;
	public static final int LEVEL_VETERAN = 2;
	public static final int LEVEL_BIG_DAM_HERO = 3;

	public static final int ATTRIBUTE_POINTS_GREENHORN = 42;
	public static final int ATTRIBUTE_POINTS_VETERAN = 48;
	public static final int ATTRIBUTE_POINTS_BIG_DAM_HERO = 54;

	public static final int STAT_MAX_GREENHORN = 12;
	public static final int STAT_MAX_VETERAN = 14;
	public static final int STAT_MAX_BIG_DAM_HERO = 16;

	public static final int GENERAL_SKILLS_LOWER = 6;
	public static final int GENERAL_SKILLS_UPPER = 16;

	public static final int TRAIT_LIST_MAX_SIZE = 5;

	private int level = LEVEL_GREENHORN;
	private boolean male = true;
	private String femaleName;
	private String maleName;
	private String personalityTemplate;
	private CharacterStats mainStats;
	private DerivedStat initiative;
	private DerivedStat life;
	private TraitList assets;
	private TraitList complications;
	private String archetypeName;
	private int startingPoints = ATTRIBUTE_POINTS_GREENHORN;
	private int currentPoints = ATTRIBUTE_POINTS_GREENHORN;
	private SkillSheet skillSheet;
	private HTMLDocument info = new HTMLDocument();

	private double currentMoney = 750d;
	private double startingMoney = 750d;
	private boolean moneyedIndividual = false;
	private boolean deadBroke = false;
	private EquipmentList<Equipment> equipment;
	private EquipmentList<Armor> armor;
	private EquipmentList<MeleeWeapon> meleeWeapons;
	private EquipmentList<RangedWeapon> rangedWeapons;
	private EquipmentList<Explosive> explosives;
	private PropertyChangeSupport propertySupport = new PropertyChangeSupport(this);

	/**
	 * Return a plain ASCII output of this character.
	 */
	public StringBuilder getAsText() {
		StringBuilder sb = new StringBuilder(1024 * 5);

		// Name, profile and level
		String name = (isMale() ? maleName : femaleName);
		sb.append(name);
		sb.append("\n");
		for (int i = 0; i < name.length(); i++) {
			sb.append("=");
		}
		sb.append("\n");
		switch (level) {
		case LEVEL_GREENHORN:
			sb.append("Greenhorn");
			break;
		case LEVEL_VETERAN:
			sb.append("Veteran");
			break;
		case LEVEL_BIG_DAM_HERO:
			sb.append("Big Damn Hero");
			break;
		}

		// personality
		if (!StringUtils.isEmpty(personalityTemplate)) {
			sb.append("Personality : ");
			sb.append(personalityTemplate);
			sb.append("\n");
		}

		// Main stats
		int len = 0;
		for (String s : MainStat.getNames()) {
			len = (s.length() > len ? s.length() : len);
		}
		for (int i = 0; i < MainStat.count(); i++) {
			String key = MainStat.getForIndex(i).getKey();
			String longname = MainStat.getNames().get(i);
			while (longname.length() < len) {
				longname = longname + " ";
			}
			sb.append(longname);
			sb.append(" : ");
			sb.append(mainStats.getStat(key).getDice());
			sb.append("\n");
		}

		// Derived Stats
		sb.append("\n\nInitiative : ");
		sb.append(mainStats.getStat("AGL").getDice());
		sb.append("+");
		sb.append(mainStats.getStat("ALT").getDice());
		sb.append("\nLife Points : ");
		int lifeTotal = mainStats.getStat("VIT").getValue() + mainStats.getStat("WIL").getValue();
		sb.append(lifeTotal);

		// Assets
		sb.append("\n\nAssets\n-----\n");
		for (TraitData tdata : assets) {
			sb.append(tdata.getName());
			sb.append(" (");
			sb.append((tdata.isMajor() ? "Major)\n" : "Minor)\n"));
		}

		// Complications
		sb.append("\nComplications\n-------------\n");
		for (TraitData tdata : complications) {
			sb.append(tdata.getName());
			sb.append(" (");
			sb.append((tdata.isMajor() ? "Major)\n" : "Minor)\n"));
		}

		// Skills
		sb.append("\n\nSkills\n------\n");
		sb.append(skillSheet.getAsText());

		// Armor
		sb.append("\n\nArmor\n-----\n");
		for (Armor arm : armor) {
			sb.append(arm.toString());
			sb.append("\n");
		}

		// Weapons
		sb.append("\n\nWeapons\n-------\n");
		for (MeleeWeapon mw : meleeWeapons) {
			sb.append(mw.toString());
			sb.append("( ");
			sb.append(getToHitFor(mw.getSkill()));
			sb.append(" )");
			sb.append("\n");
		}
		for (RangedWeapon rw : rangedWeapons) {
			sb.append(rw.toString());
			sb.append("( ");
			sb.append(getToHitFor(rw.getSkill()));
			sb.append(" )");
			sb.append("\n");
		}
		for (Explosive ex : explosives) {
			sb.append(ex.toString());
			sb.append("\n");
		}

		// Equipment
		sb.append("\n\nEquipment\n---------\n");
		for (Equipment eq : equipment) {
			sb.append(eq.toString());
			sb.append("\n");
		}

		// Info
		sb.append("\n\nInfo");
		sb.append("\n----\n");
		try {
			sb.append(info.getText(0, info.getLength()));
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
		return sb;
	}

	/**
	 * return person as an XML element.
	 */
	public Element getXML() {
		Element xml = new Element("serenityCharacter");
		xml.setAttribute("maleName", maleName);
		xml.setAttribute("femaleName", femaleName);
		xml.setAttribute("archetype", archetypeName);
		xml.addContent(mainStats.getXML());

		xml.addContent(getTraitXML("assets", assets));
		xml.addContent(getTraitXML("complications", complications));
		xml.setAttribute("gender", (isMale() ? "M" : "F"));

		xml.addContent(skillSheet.getXML());
		Element infoXML = new Element("info");
		try {
			infoXML.setText(getHTMLBytes(info));
		} catch (BadLocationException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		xml.addContent(getEquipXML("equipmentList", equipment));
		xml.addContent(getEquipXML("armorList", armor));
		xml.addContent(getEquipXML("meleeList", meleeWeapons));
		xml.addContent(getEquipXML("rangedList", rangedWeapons));
		xml.addContent(getEquipXML("explosiveList", explosives));

		xml.addContent(infoXML);
		if (!StringUtils.isEmpty(personalityTemplate)) {
			Element e = new Element("personality");
			e.setText(personalityTemplate);
			xml.addContent(e);
		}
		return xml;
	}

	/**
	 * Convert a document to a string.
	 * 
	 * @param doc
	 * @return
	 * @throws IOException
	 * @throws BadLocationException
	 */
	public static String getHTMLBytes(HTMLDocument doc) throws IOException, BadLocationException {
		if (doc == null) {
			return "";
		}
		StringWriter swriter = new StringWriter(1024 * 10);
		HTMLWriter hwriter = new HTMLWriter(swriter, doc);
		hwriter.write();
		swriter.flush();
		swriter.close();
		return swriter.getBuffer().toString();
	}

	/**
	 * Build a HTMLDocument from a string.
	 * 
	 * @param txt
	 * @return
	 */
	public static HTMLDocument getHTML(String txt) {
		HTMLEditorKit kit = new HTMLEditorKit();
		HTMLDocument doc = (HTMLDocument) kit.createDefaultDocument();
		try {
			java.io.StringReader in = new java.io.StringReader(txt);
			kit.read(in, doc, 0);
		} catch (Exception e) {
		}
		return doc;
	}

	/**
	 * Build from an element
	 * 
	 * @param xml
	 */
	public void setXML(Element xml) {
		setMale(!StringUtils.equalsIgnoreCase("f", xml.getAttributeValue("gender")));
		String name = xml.getAttributeValue("name");
		if (StringUtils.isEmpty(name)) {
			// New dual name system
			femaleName = xml.getAttributeValue("femaleName");
			maleName = xml.getAttributeValue("maleName");
			archetypeName = xml.getAttributeValue("archetype", "");
		} else {
			// Old system, use the gender name and make up the other one.
			if (isMale()) {
				maleName = xml.getAttributeValue("name");
				femaleName = RandomNamer.getRandomName(false);
			} else {
				femaleName = xml.getAttributeValue("name");
				maleName = RandomNamer.getRandomName(true);
			}
		}

		mainStats.setXML(xml.getChild(CharacterStats.XML_KEY));

		setTraitXML(xml.getChild("assets"), assets);
		setTraitXML(xml.getChild("complications"), complications);
		skillSheet.setXML(xml.getChild("skillDataMap"));
		setMale(!StringUtils.equalsIgnoreCase("f", xml.getAttributeValue("gender")));

		setEquipmentXML(xml.getChild("equipmentList"));
		setArmorXML(xml.getChild("armorList"));
		setExplosiveXML(xml.getChild("explosiveList"));
		setMeleeXML(xml.getChild("meleeList"));
		setRangedXML(xml.getChild("rangedList"));
		if (xml.getChild("personality") != null) {
			personalityTemplate = xml.getChild("personality").getText();
		}
		Element e = xml.getChild("info");
		if (e != null) {
			String s = e.getText();
			info = new HTMLDocument();
			if (!StringUtils.isEmpty(s)) {
				info = getHTML(e.getText());
			}
			info.addDocumentListener(this);
		}
	}

	/**
	 * Populate equipment list from xml element.
	 * 
	 * @param xml
	 */
	private void setEquipmentXML(Element xml) {
		equipment.clear();
		if (xml == null) {
			return;
		}
		Element[] elems = JDOMUtils.getChildArray(xml, "equipment");
		for (Element e : elems) {
			equipment.add(new Equipment(e));
		}
	}

	/**
	 * Populate armor list from xml element.
	 * 
	 * @param xml
	 */
	private void setArmorXML(Element xml) {
		armor.clear();
		if (xml == null) {
			return;
		}

		Element[] elems = JDOMUtils.getChildArray(xml, "armor");
		for (Element e : elems) {
			armor.add(new Armor(e));
		}
	}

	/**
	 * Populate armor list from xml element.
	 * 
	 * @param xml
	 */
	private void setExplosiveXML(Element xml) {
		explosives.clear();
		if (xml == null) {
			return;
		}
		Element[] elems = JDOMUtils.getChildArray(xml, "weapon");

		for (Element e : elems) {
			explosives.add(new Explosive(e));
		}
	}

	/**
	 * Populate melee list from xml element.
	 * 
	 * @param xml
	 */
	private void setMeleeXML(Element xml) {
		meleeWeapons.clear();
		if (xml == null) {
			return;
		}
		Element[] elems = JDOMUtils.getChildArray(xml, "weapon");

		for (Element e : elems) {
			meleeWeapons.add(new MeleeWeapon(e));
		}
	}

	/**
	 * Populate ranged weapons list from xml element.
	 * 
	 * @param xml
	 */
	private void setRangedXML(Element xml) {
		rangedWeapons.clear();
		if (xml == null) {
			return;
		}
		Element[] elems = JDOMUtils.getChildArray(xml, "weapon");

		for (Element e : elems) {
			rangedWeapons.add(new RangedWeapon(e));
		}
	}

	/**
	 * Compile a trait list into an xml element.
	 * 
	 * @param name
	 * @return
	 */
	private Element getTraitXML(String name, MutableList<TraitData> list) {
		Element xml = new Element(name);
		for (TraitData td : list) {
			xml.addContent(td.getXML());
		}
		return xml;
	}

	/**
	 * Compile a trait list into an xml element.
	 * 
	 * @param name
	 * @return
	 */
	private Element getEquipXML(String name, MutableList<? extends Equipment> list) {
		Element xml = new Element(name);
		for (Equipment equip : list) {
			xml.addContent(equip.getXML());
		}
		return xml;
	}

	/**
	 * Retreive a trait list from the given xml
	 * 
	 * @param xml
	 * @param list
	 */
	private void setTraitXML(Element xml, MutableList<TraitData> list) {
		list.clear();
		Element[] elems = JDOMUtils.getChildArray(xml, "traitData");
		TraitType type = (xml.getName().equals("assets") ? TraitType.ASSET : TraitType.COMPLICATION);
		for (Element e : elems) {
			TraitData tdata = TraitData.createTraitData(e);
			tdata.setType(type);
			list.add(tdata);

		}
	}

	/**
	 * Blank this character except for the name
	 * 
	 */
	public void clearExceptName() {
		mainStats = new CharacterStats();
		NamedStat[] initStats = { mainStats.getStat("AGL"), mainStats.getStat("ALT") };
		initiative = new DerivedStat("Initiative", initStats);
		NamedStat[] lifeStats = { mainStats.getStat("VIT"), mainStats.getStat("WIL") };
		life = new DerivedStat("Life", lifeStats);
		// Traits
		if (assets != null) {
			assets.clear();
		} else {
			assets = new TraitList(TraitType.ASSET);
			assets.addListDataListener(new MoneyChecker());
		}
		if (complications != null) {
			complications.clear();
		} else {
			complications = new TraitList(TraitType.COMPLICATION);
		}
		// Skills
		skillSheet = (skillSheet == null ? new SkillSheet() : skillSheet);
		skillSheet.init();
		// Equipment
		if (equipment != null) {
			equipment.clear();
		} else {
			equipment = new EquipmentList<Equipment>();
		}
		if (armor != null) {
			armor.clear();
		} else {
			armor = new EquipmentList<Armor>();
		}
		if (meleeWeapons != null) {
			meleeWeapons.clear();
		} else {
			meleeWeapons = new EquipmentList<MeleeWeapon>();
		}
		if (rangedWeapons != null) {
			rangedWeapons.clear();
		} else {
			rangedWeapons = new EquipmentList<RangedWeapon>();
		}
		if (explosives != null) {
			explosives.clear();
		} else {
			explosives = new EquipmentList<Explosive>();
		}
	}

	/**
	 * @return Returns the assets.
	 */
	public TraitList getAssets() {
		return assets;
	}

	/**
	 * Tests this person for the presence of the given asset.
	 * 
	 * @param asset
	 * @return true if the asset is present. Major/Minor is not checked.
	 */
	public boolean hasAsset(TraitData asset) {
		for (TraitData trait : assets) {
			if (trait.getName().equals(asset.getName())) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Tests this person for the presence of the given complication.
	 * 
	 * @param complication
	 * @return true if the asset is present. Major/Minor is not checked.
	 */
	public boolean hasComplication(TraitData complication) {
		for (TraitData trait : complications) {
			if (trait.getName().equals(complication.getName())) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Adds an asset to this character.
	 * 
	 * @param asset
	 *            asset to add
	 * @throws IllegalArgumentException
	 *             if this character already has this asset.
	 */
	public void addAsset(TraitData asset) {
		if (hasAsset(asset)) {
			throw new IllegalArgumentException("Character already has asset \"" + asset.getName() + "\" .");
		}
		assets.add(asset);
	}

	/**
	 * Adds a Complication to this character.
	 * 
	 * @param complication
	 *            Complication to add
	 * @throws IllegalArgumentException
	 *             if this character already has this asset.
	 */
	public void addComplication(TraitData complication) {
		if (hasComplication(complication)) {
			throw new IllegalArgumentException("Character already has complication \"" + complication.getName() + "\" .");
		}
		complications.add(complication);
	}

	/**
	 * Remove the given complication from this character.
	 * 
	 * @param complication
	 * @return true if the complication has been removed, false if not (probably
	 *         because it's not present).
	 */
	public boolean removeComplication(TraitData complication) {
		return complications.remove(complication);
	}

	/**
	 * Remove the given asset from this character.
	 * 
	 * @param asset
	 * @return true if the asset has been removed, false if not (probably
	 *         because it's not present).
	 */
	public boolean removeAsset(TraitData complication) {
		return assets.remove(complication);
	}

	/**
	 * @param newAssets
	 *            The assets to set.
	 */
	public void setAssets(MutableList<TraitData> newAssets) {
		assets.clear();
		assets.addAll(newAssets);
	}

	/**
	 * @return Returns the complications.
	 */
	public TraitList getComplications() {
		return complications;
	}

	/**
	 * @param newComps
	 *            The complications to set.
	 */
	public void setComplications(MutableList<TraitData> newComps) {
		complications.clear();
		complications.addAll(newComps);
	}

	/**
	 * @return Returns the the number of free stat/trait points.
	 */
	public int getCurrentStatPoints() {

		return currentPoints;
	}

	/**
	 * @param currentPoints
	 *            The currentPoints to set.
	 */
	public void setCurrentPoints(int currentPoints) {
		int old = this.currentPoints;
		this.currentPoints = currentPoints;
		propertySupport.firePropertyChange(PROPERTY_STAT_POINTS, old, currentPoints);
	}

	/**
	 * Recomputes the amount of points spent on stats/traits.
	 */
	public void recomputeCurrentStatPoints() {
		currentPoints = startingPoints;
		currentPoints = currentPoints - mainStats.getTotalPoints();
		for (TraitData tdata : assets) {
			currentPoints = currentPoints - tdata.getCost();
		}
		for (TraitData tdata : complications) {
			currentPoints = currentPoints + tdata.getCost();
		}
	}

	/**
	 * @return Returns the initiative.
	 */
	public DerivedStat getInitiative() {
		return initiative;
	}

	/**
	 * @param initiative
	 *            The initiative to set.
	 */
	public void setInitiative(DerivedStat initiative) {
		this.initiative = initiative;
	}

	/**
	 * @return Returns the life.
	 */
	public DerivedStat getLife() {
		return life;
	}

	/**
	 * @param life
	 *            The life to set.
	 */
	public void setLife(DerivedStat life) {
		this.life = life;
	}

	/**
	 * @return Returns the mainStats.
	 */
	public CharacterStats getMainStats() {
		return mainStats;
	}

	/**
	 * @param mainStats
	 *            The mainStats to set.
	 */
	public void setMainStats(CharacterStats mainStats) {
		this.mainStats = mainStats;
	}

	/**
	 * @return Returns the male.
	 */
	public boolean isMale() {
		return male;
	}

	/**
	 * @param male
	 *            The male to set.
	 */
	public void setMale(boolean male) {
		boolean old = this.male;
		this.male = male;
		propertySupport.firePropertyChange(PROPERTY_GENDER, old, male);
	}

	/**
	 * @return Returns the name.
	 */
	public String getName() {
		return (isMale() ? maleName : femaleName);
	}

	/**
	 * @param name
	 *            The name to set.
	 */
	// public void setName(String name) {
	// String old = this.name;
	// this.name = name;
	// propertySupport.firePropertyChange( PROPERTY_NAME, old, this.name );
	// }

	/**
	 * @return Returns the startingPoints.
	 */
	public int getStartingPoints() {
		return startingPoints;
	}

	/**
	 * @param startingPoints
	 *            The startingPoints to set.
	 */
	public void setStartingPoints(int startingPoints) {
		int old = this.startingPoints;
		this.startingPoints = startingPoints;

		propertySupport.firePropertyChange(PROPERTY_STAT_POINTS, old, this.startingPoints);

	}

	/**
	 * Creates an empty person.
	 */
	public Person() {
		male = RandomFactory.getRandom().nextBoolean();
		maleName = RandomNamer.getRandomName(true);
		femaleName = RandomNamer.getRandomName(false);
		mainStats = new CharacterStats();
		mainStats.addPropertyChangeListener(new PropertyChangeListener() {

			public void propertyChange(PropertyChangeEvent evt) {
				if (evt.getPropertyName().equals(CharacterStats.properties.TOTAL_PROPERTY)) {
					respondToStatChange(evt);
				}
			}
		});
		NamedStat[] initStats = { mainStats.getStat("AGL"), mainStats.getStat("ALT") };
		initiative = new DerivedStat("Initiative", initStats);
		NamedStat[] lifeStats = { mainStats.getStat("VIT"), mainStats.getStat("WIL") };
		life = new DerivedStat("Life", lifeStats);
		assets = new TraitList(TraitType.ASSET);
		assets.addListDataListener(new MoneyChecker());
		complications = new TraitList(TraitType.COMPLICATION);
		complications.addListDataListener(new MoneyChecker());
		skillSheet = new SkillSheet();
		skillSheet.init();
		equipment = new EquipmentList<Equipment>();
		armor = new EquipmentList<Armor>();
		meleeWeapons = new EquipmentList<MeleeWeapon>();
		rangedWeapons = new EquipmentList<RangedWeapon>();
		explosives = new EquipmentList<Explosive>();

		assets.addListDataListener(this);
		complications.addListDataListener(this);

		ListDataListener moneyListener = new ListDataListener() {

			public void contentsChanged(ListDataEvent e) {
				recalculateEquipmentCosts();
			}

			public void intervalAdded(ListDataEvent e) {
				recalculateEquipmentCosts();
			}

			public void intervalRemoved(ListDataEvent e) {
				recalculateEquipmentCosts();
			}
		};
		equipment.addListDataListener(moneyListener);
		armor.addListDataListener(moneyListener);
		meleeWeapons.addListDataListener(moneyListener);
		rangedWeapons.addListDataListener(moneyListener);
		explosives.addListDataListener(moneyListener);
		info.addDocumentListener(this);
	}

	/**
	 * 
	 */
	protected void recalculateEquipmentCosts() {
		double money = getStartingMoney();
		money -= equipment.getCostTotal();
		money -= explosives.getCostTotal();
		money -= armor.getCostTotal();
		money -= rangedWeapons.getCostTotal();
		money -= meleeWeapons.getCostTotal();
		setCurrentMoney(money);

	}

	/**
	 * @param evt
	 */
	private void respondToStatChange(PropertyChangeEvent evt) {
		int oldValue = ((Integer) evt.getOldValue()).intValue();
		int newValue = ((Integer) evt.getNewValue()).intValue();
		setCurrentPoints(getCurrentStatPoints() - (newValue - oldValue));
	}

	/**
	 * @return Returns the skillSheet.
	 */
	public SkillSheet getSkillSheet() {
		return skillSheet;
	}

	/**
	 * @return Returns the level.
	 */
	public int getLevel() {
		return level;
	}

	/**
	 * @param level
	 *            The level to set.
	 */
	public void setLevel(int level) {
		int old = this.level;
		this.level = level;

		switch (level) {
		default:
			setStartingPoints(ATTRIBUTE_POINTS_GREENHORN);
			break;
		case LEVEL_VETERAN:
			setStartingPoints(ATTRIBUTE_POINTS_VETERAN);
			break;
		case LEVEL_BIG_DAM_HERO:
			setStartingPoints(ATTRIBUTE_POINTS_BIG_DAM_HERO);
			break;
		}
		propertySupport.firePropertyChange(PROPERTY_LEVEL, old, this.level);
	}

	/**
	 * returns the maximum stat points for the current level
	 * 
	 * @return
	 */
	public int getStatMax() {
		switch (level) {
		default:
			return 12;
		case LEVEL_GREENHORN:
			return STAT_MAX_GREENHORN;
		case LEVEL_VETERAN:
			return STAT_MAX_VETERAN;
		case LEVEL_BIG_DAM_HERO:
			return STAT_MAX_BIG_DAM_HERO;
		}
	}

	/**
	 * returns the maximum skill points for the current level
	 * 
	 * @return
	 */
	public int getSkillMax() {
		return 16;
	}

	/**
	 * Checks this person for the named asset or complication.
	 * 
	 * @param name
	 * @return
	 */
	public boolean hasTrait(String name) {
		for (TraitData tdata : assets) {
			if (tdata.getName().equals(name)) {
				return true;
			}
		}
		for (TraitData tdata : complications) {
			if (tdata.getName().equals(name)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Return the skill point allocation, i.e. the number of skill points this
	 * character starts with.
	 */
	public int getStartingSkillPoints() {
		return startingPoints + 20;
	}

	/**
	 * @return the number of free skill points available.
	 */
	public int getAvailableSkillPoints() {
		return 20 + startingPoints - skillSheet.getTotalPoints();
	}

	/**
	 * Adds a listener to be notified of property changes to this person.
	 * 
	 * @param pcl
	 */
	public void addPropertyChangeListener(PropertyChangeListener pcl) {
		propertySupport.addPropertyChangeListener(pcl);
	}

	/**
	 * remove a listener from property change notifications.
	 * 
	 * @param pcl
	 */
	public void removePropertyChangeListener(PropertyChangeListener pcl) {
		propertySupport.removePropertyChangeListener(pcl);
	}

	/**
	 * Adds a listener to be notified of any skill changes. This listener is
	 * just a direct pass through to the SkillSheet.
	 * 
	 * @param cl
	 */
	public void addSkillChangeListener(SkillChangeListener cl) {
		getSkillSheet().addSkillChangeListener(cl);
	}

	/**
	 * Removes a listener from the skill change notification list. This listener
	 * is just a direct pass through to the SkillSheet.
	 * 
	 * @param cl
	 */
	public void removeSkillSkillChangeListener(SkillChangeListener cl) {
		getSkillSheet().removeSkillChangeListener(cl);
	}

	/**
	 * @return the info
	 */
	public HTMLDocument getInfo() {
		return info;
	}

	/**
	 * @param info
	 *            the info to set
	 */
	public void setInfo(HTMLDocument info) {
		HTMLDocument oldInfo = this.info;
		this.info = info;
		propertySupport.firePropertyChange(INFO_PROPERTY, oldInfo, info);
	}

	/**
	 * @return the money
	 */
	public double getCurrentMoney() {
		return currentMoney;
	}

	/**
	 * @param money
	 *            the money to set
	 */
	public void setCurrentMoney(double money) {
		double old = this.currentMoney;
		this.currentMoney = money;
		propertySupport.firePropertyChange(PROPERTY_CURRENT_MONEY, old, currentMoney);
	}

	/**
	 * @return the startingMoney
	 */
	public double getStartingMoney() {
		return startingMoney;
	}

	/**
	 * @param startingMoney
	 *            the startingMoney to set
	 */
	public void setStartingMoney(double startingMoney) {
		double old = this.startingMoney;
		this.startingMoney = startingMoney;
		propertySupport.firePropertyChange(PROPERTY_STARTING_MONEY, old, startingMoney);
	}

	/**
	 * Listener for watching the asset lists and modifiying the money whenever
	 * "Moneyed Inidividual" is added or removed from the asset list.
	 * 
	 * @author Luggy
	 * 
	 */
	class MoneyChecker implements ListDataListener {
		private static final String MONEYED_INDIVIDUAL_TRAIT_NAME = "Moneyed Individual";
		private static final String DEAD_BROKE_TRAIT_NAME = "Dead Broke";

		/**
		 * Check to see if trait has been added.
		 */
		private void checkAdded() {
			if (Person.this.hasTrait(DEAD_BROKE_TRAIT_NAME) && !deadBroke) {
				setStartingMoney(getStartingMoney() * 0.5d);
				setCurrentMoney(getCurrentMoney() * 0.5d);
				deadBroke = true;
			}
			if (Person.this.hasTrait(MONEYED_INDIVIDUAL_TRAIT_NAME) && !moneyedIndividual) {
				// Moneyed Individual added
				setStartingMoney(getStartingMoney() * 1.5d);
				setCurrentMoney(getCurrentMoney() * 1.5d);
				moneyedIndividual = true;
			}
		}

		/**
		 * Check to see if trait has been removed.
		 */
		private void checkRemoved() {
			if (moneyedIndividual && !Person.this.hasTrait(MONEYED_INDIVIDUAL_TRAIT_NAME)) {
				// Moneyed individual removed
				setStartingMoney(getStartingMoney() / 1.5d);
				setCurrentMoney(getCurrentMoney() / 1.5d);
				moneyedIndividual = false;
			}
			if (deadBroke && !Person.this.hasTrait(DEAD_BROKE_TRAIT_NAME)) {
				setStartingMoney(getStartingMoney() / 0.5d);
				setCurrentMoney(getCurrentMoney() / 0.5d);
				deadBroke = false;
			}
		}

		public void intervalAdded(ListDataEvent e) {
			checkAdded();
		}

		public void intervalRemoved(ListDataEvent e) {
			checkRemoved();
		}

		public void contentsChanged(ListDataEvent e) {
			checkAdded();
			checkRemoved();
		}
	}

	/**
	 * @return the armor
	 */
	public EquipmentList<Armor> getArmor() {
		return armor;
	}

	/**
	 * @return the equipment
	 */
	public EquipmentList<Equipment> getEquipment() {
		return equipment;
	}

	/**
	 * @return the explosives
	 */
	public EquipmentList<Explosive> getExplosives() {
		return explosives;
	}

	/**
	 * @return the meleeWeapons
	 */
	public EquipmentList<MeleeWeapon> getMeleeWeapons() {
		return meleeWeapons;
	}

	/**
	 * @return the rangedWeapons
	 */
	public EquipmentList<RangedWeapon> getRangedWeapons() {
		return rangedWeapons;
	}

	/**
	 * Return a to hit roll for the given skill
	 * 
	 * @param skill
	 * @return
	 */
	public String getToHitFor(String skill) {
		int stat = mainStats.getStat("AGL").getValue();
		if (!StringUtils.isEmpty(skill)) {
			if (skill != null) {
				stat = stat + skillSheet.getPointsIn(skill);
			}
		}
		return StepStat.getDiceFor(stat);
	}

	/**
	 * Generic description of a persons personality.
	 * 
	 * @return the personalityTemplate
	 */
	public String getPersonalityTemplate() {
		return personalityTemplate;
	}

	/**
	 * @param personalityTemplate
	 *            the personalityTemplate to set
	 */
	public void setPersonalityTemplate(String personalityTemplate) {
		String old = this.personalityTemplate;
		this.personalityTemplate = personalityTemplate;
		propertySupport.firePropertyChange(PROPERTY_PERSONALITY, old, this.personalityTemplate);
	}

	/**
	 * Perform equality test on the person.
	 */
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Person)) {
			return false;
		}
		Person person = (Person) obj;
		if (!person.getMaleName().equalsIgnoreCase(this.getMaleName())) {
			return false;
		}
		if (!person.getFemaleName().equalsIgnoreCase(this.getFemaleName())) {
			return false;
		}

		// Stats
		if (!person.getMainStats().equals(this.getMainStats())) {
			return false;
		}

		// Traits
		if (!assets.contentsEqual(person.getAssets())) {
			return false;
		}
		if (!complications.contentsEqual(person.getComplications())) {
			return false;
		}

		// Skills
		if (!this.getSkillSheet().equals(person.getSkillSheet())) {
			return false;
		}
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * javax.swing.event.ListDataListener#contentsChanged(javax.swing.event.
	 * ListDataEvent)
	 */
	public void contentsChanged(ListDataEvent e) {
		recomputeTraitPoints(e);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.event.ListDataListener#intervalAdded(javax.swing.event.
	 * ListDataEvent)
	 */
	public void intervalAdded(ListDataEvent e) {
		recomputeTraitPoints(e);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * javax.swing.event.ListDataListener#intervalRemoved(javax.swing.event.
	 * ListDataEvent)
	 */
	public void intervalRemoved(ListDataEvent e) {
		recomputeTraitPoints(e);
	}

	private void recomputeTraitPoints(@SuppressWarnings("unused") ListDataEvent e) {
		recomputeCurrentStatPoints();
	}

	/**
	 * Checks this person for a given piece of equipment.
	 * 
	 * @param equip
	 * @return the trueif it was found.
	 */
	public boolean hasEquipment(Equipment equip) {
		if (getArmor().contains(equip)) {
			return true;
		}
		if (getEquipment().contains(equip)) {
			return true;
		}
		if (getExplosives().contains(equip)) {
			return true;
		}
		if (getMeleeWeapons().contains(equip)) {
			return true;
		}
		if (getRangedWeapons().contains(equip)) {
			return true;
		}
		return false;
	}

	/**
	 * @return the name used while this character is female.
	 */
	public String getFemaleName() {
		return femaleName;
	}

	/**
	 * @param femaleName
	 *            the name used while this character is female.
	 */
	public void setFemaleName(String femaleName) {
		this.femaleName = femaleName;
	}

	/**
	 * @return the name used while this character is male.
	 */
	public String getMaleName() {
		return maleName;
	}

	/**
	 * @param maleName
	 *            the name used while this character is male.
	 */
	public void setMaleName(String maleName) {
		this.maleName = maleName;
	}

	/**
	 * Changes the naem of the person. If the person is male then the male name
	 * changes, if female then the female name changes.
	 * 
	 * @param newName
	 *            the new name of the character.
	 */
	public void changeNameTo(String newName) {
		if (isMale()) {
			setMaleName(newName);
		} else {
			setFemaleName(newName);
		}
	}

	/**
	 * @return true if the current data model allows a new asset to be added.
	 */
	public boolean canAddNewAsset() {
		return getCurrentStatPoints() > 1 && getAssets().size() < TRAIT_LIST_MAX_SIZE;
	}

	/**
	 * @return true if the current data model allows a new asset to be added.
	 */
	public boolean canAddNewComplication() {
		return getComplications().size() < TRAIT_LIST_MAX_SIZE;
	}

	/**
	 * @return total weight of all equipment this character has.
	 */
	public double getCarriedWeight() {
		double totalWeight = 0;
		totalWeight = totalWeight + equipment.getWeightTotal();
		totalWeight = totalWeight + explosives.getWeightTotal();
		totalWeight = totalWeight + armor.getWeightTotal();
		totalWeight = totalWeight + meleeWeapons.getWeightTotal();
		totalWeight = totalWeight + rangedWeapons.getWeightTotal();
		return totalWeight;
	}

	public int getEquipmentCount() {
		return equipment.size() + explosives.size() + meleeWeapons.size() + armor.size() + rangedWeapons.size();
	}

	/**
	 * @return total cost of all equipment this character has.
	 */
	public double getEquipmentCost() {
		double totalCost = 0;
		totalCost = totalCost + equipment.getCostTotal();
		totalCost = totalCost + explosives.getCostTotal();
		totalCost = totalCost + armor.getCostTotal();
		totalCost = totalCost + meleeWeapons.getCostTotal();
		totalCost = totalCost + rangedWeapons.getCostTotal();
		return totalCost;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.event.DocumentListener#changedUpdate(javax.swing.event.
	 * DocumentEvent)
	 */
	public void changedUpdate(DocumentEvent e) {
		propertySupport.firePropertyChange(INFO_PROPERTY, "", info);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.event.DocumentListener#insertUpdate(javax.swing.event.
	 * DocumentEvent)
	 */
	public void insertUpdate(DocumentEvent e) {
		propertySupport.firePropertyChange(INFO_PROPERTY, "", info);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.event.DocumentListener#removeUpdate(javax.swing.event.
	 * DocumentEvent)
	 */
	public void removeUpdate(DocumentEvent e) {
		propertySupport.firePropertyChange(INFO_PROPERTY, "", info);
	}

	public String getArchetypeName() {
		return archetypeName;
	}

	public void setArchetypeName(String archetypeName) {
		String oldArchetype = this.archetypeName;
		this.archetypeName = archetypeName;
		propertySupport.firePropertyChange(archetypeName, oldArchetype, this.archetypeName);
	}

	public Integer count(MeleeWeapon meleeWeapon) {
		return meleeWeapons.countOf(meleeWeapon);
	}

	public Integer count(RangedWeapon rangedWeapon) {
		return rangedWeapons.countOf(rangedWeapon);
	}
}
