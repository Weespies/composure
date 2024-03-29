/**
 * 
 */
package uk.lug.serenity.npc.random;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import org.apache.commons.lang.StringUtils;

import uk.lug.serenity.npc.managers.ArchetypesManager;
import uk.lug.serenity.npc.managers.EquipmentManager;
import uk.lug.serenity.npc.managers.SkillsManager;
import uk.lug.serenity.npc.managers.TraitsManager;
import uk.lug.serenity.npc.model.Person;
import uk.lug.serenity.npc.model.equipment.Armor;
import uk.lug.serenity.npc.model.equipment.Explosive;
import uk.lug.serenity.npc.model.equipment.MeleeWeapon;
import uk.lug.serenity.npc.model.equipment.RangedWeapon;
import uk.lug.serenity.npc.model.skills.GeneralSkill;
import uk.lug.serenity.npc.model.skills.SkillData;
import uk.lug.serenity.npc.model.stats.MainStat;
import uk.lug.serenity.npc.model.stats.NamedStat;
import uk.lug.serenity.npc.model.traits.Trait;
import uk.lug.serenity.npc.model.traits.TraitData;
import uk.lug.serenity.npc.model.traits.TraitType;
import uk.lug.serenity.npc.random.archetype.Archetype;
import uk.lug.serenity.npc.random.archetype.skills.GeneralSkillBias;
import uk.lug.util.RandomFactory;

/**
 * $Id$
 * @version $Revision$
 * @author $Author$
 * <p>This class actually does the job of randomizing a character.</p>
 * 
 */
/**
 * @author Luggy
 * 
 */
public class Generator {
	private static final int MAX_MELEE = 2;
	private static final int MAX_RANGED = 2;
	private static Random random = RandomFactory.getRandom();
	private Person person;
	private Archetype archetype = null;

	/**
	 * Generator using an existing person. If this person has no profile then a
	 * random profile is assigned.
	 * 
	 * @param person
	 */
	public Generator(Person person, Archetype archetype) {
		super();
		this.person = person;
		this.archetype = archetype;
		person.setCurrentPoints(person.getStartingPoints());
	}

	/**
	 * Randomize the stats
	 * 
	 * @return
	 */
	public void randomizeStats() {
		int pts = person.getCurrentStatPoints();
		person.setCurrentPoints(person.getCurrentStatPoints());
		while (pts > 0) {
			MainStat stat = archetype.getRandomStat(RandomFactory.getRandom());
			NamedStat nstat = person.getMainStats().getStat(stat.getKey());
			if (nstat.getValue() < person.getStatMax()) {
				nstat.setValue(nstat.getValue() + 2);
				pts = pts - 2;
				person.setCurrentPoints(person.getCurrentStatPoints() - 2);
			}
		}
	}

	/**
	 * Randomize the traits.
	 */
	public void randomizeTraits() {
		int assets = 1;
		int complications = 1;
		assets = assets + random.nextInt(3);
		complications = complications + random.nextInt(3);
		getRandomAssets(assets);
		getRandomComplications(complications);
		if (person.hasTrait("Moneyed Individual")) {
			person.setCurrentMoney(person.getCurrentMoney() * 1.5d);
			person.setStartingMoney(person.getStartingMoney() * 1.5d);
		}
		if (person.hasTrait("Dead Broke")) {
			person.setCurrentMoney(person.getCurrentMoney() * 0.5d);
			person.setStartingMoney(person.getStartingMoney() * 0.5d);
		}
	}

	/**
	 * Give the person the specified number of assets.
	 * 
	 * @param number
	 */
	private void getRandomAssets(int number) {
		Trait[] assets = TraitsManager.getAssets();
		for (int i = 0; i < number; i++) {
			Trait trait;
			// Keep going until we have one that we don't already have
			do {
				trait = assets[random.nextInt(assets.length)];
			} while (person.hasTrait(trait.getName()));
			// Major or minor trait ?
			boolean major = false;
			if (trait.isMajor() && trait.isMinor()) {
				major = random.nextBoolean();
			} else if (trait.isMajor()) {
				major = true;
			}
			// Focus
			String focus = null;
			if (trait.getFocusType() == Trait.FOCUS_LIST) {
				focus = trait.getFocusList()[random.nextInt(trait.getFocusList().length)];
			}
			if (trait.getFocusType() == Trait.SKILL_LIST) {
				focus = trait.getSkillsList()[random.nextInt(trait.getSkillsList().length)];
			}

			TraitData tdata = new TraitData();
			tdata.setType(TraitType.ASSET);
			tdata.setName(trait.getName());
			tdata.setMajor(major);
			if (focus != null) {
				tdata.setFocus(focus);
			}
			person.setCurrentPoints(person.getCurrentStatPoints() - (tdata.isMajor() ? 4 : 2));
			person.getAssets().add(tdata);
		}
	}

	/**
	 * Give the person the specified number of complications.
	 * 
	 * @param number
	 */
	private void getRandomComplications(int number) {
		Trait[] assets = TraitsManager.getComplications();
		for (int i = 0; i < number; i++) {
			Trait trait;
			// Keep going until we have one that we don't already have
			do {
				trait = assets[random.nextInt(assets.length)];
			} while (person.hasTrait(trait.getName()));

			// Major or minor trait ?
			boolean major = false;
			if (trait.isMajor() && trait.isMinor()) {
				major = random.nextBoolean();
			} else if (trait.isMajor()) {
				major = true;
			}

			// Focus
			String focus = null;
			if (trait.getFocusType() == Trait.FOCUS_LIST) {
				focus = trait.getFocusList()[random.nextInt(trait.getFocusList().length)];
			}
			if (trait.getFocusType() == Trait.SKILL_LIST) {
				focus = trait.getSkillsList()[random.nextInt(trait.getSkillsList().length)];
			}

			TraitData tdata = new TraitData();
			tdata.setType(TraitType.COMPLICATION);
			tdata.setName(trait.getName());
			tdata.setMajor(major);
			if (focus != null) {
				tdata.setFocus(focus);
			}
			person.setCurrentPoints(person.getCurrentStatPoints() + (tdata.isMajor() ? 4 : 2));
			person.getComplications().add(tdata);
		}
	}

	/**
	 * Generate a completely random person.
	 * 
	 * @return
	 */
	public static Person getRandomPerson() {
		Person p = new Person();
		p.setMale(RandomFactory.getRandom().nextBoolean());
		p.setMaleName(RandomNamer.getRandomName(true));
		p.setFemaleName(RandomNamer.getRandomName(false));
		Archetype archetype = ArchetypesManager.getRandom();
		Generator gen = new Generator(p, archetype);
		gen.randomizeTraits();
		gen.randomizeStats();
		gen.randomizeSkills();
		gen.randomizeWeapons();
		p.setArchetypeName(archetype.getName());
		return p;
	}

	/**
	 * Generate a random person with a given profile and at a given level
	 * 
	 * @param Archetype
	 *            archetype to use for generation.
	 * @param level
	 *            LEVEL_GREENHORN, LEVEL_VETERAN or LEVEL_BIG_DAMN_HERO
	 * @return
	 */
	public static Person getRandomPerson(Archetype archetype, int level) {
		Person p = new Person();
		p.setLevel(level);

		Generator gen = new Generator(p, archetype);
		gen.randomizeTraits();
		gen.randomizeStats();
		gen.randomizeSkills();
		gen.randomizeWeapons();
		p.setArchetypeName(archetype.getName());
		return p;
	}

	private void randomizeWeapons() {
		if (hasSkill(GeneralSkill.MELEE_WEAPONS)) {
			addRandomMeleeWeapons();
		}
		if (hasSkill(GeneralSkill.GUNS) || hasSkill(GeneralSkill.RANGED_WEAPONS)) {
			addRandomRangedWeapons();
		}
		boolean allowedExplosives = hasSkill(GeneralSkill.HEAVY_WEAPON) || hasSkill(GeneralSkill.RANGED_WEAPONS) || hasSkill(GeneralSkill.COVERT);
		if (random.nextInt(5) == 0 && allowedExplosives) {
			addRandomExplosives();
		}
		addRandomArmor();
	}

	private void addRandomArmor() {
		List<String> covered = new ArrayList<String>();
		for (Armor armor : EquipmentManager.getArmorList()) {
			if (armor.getChance() > 0 && randomPercentage()<armor.getChance() ) {
				if (!covered.contains(armor.getCovers()) ) {
					covered.add(armor.getCovers());
					person.getArmor().add(armor);
				}
			}
		}
	}

	private void addRandomRangedWeapons() {
		List<RangedWeapon> available = EquipmentManager.listRangedWeaponsForSkills(person.getSkillSheet());
		randomizeArray(available);
		if (available == null || available.isEmpty()) {
			return;
		}
		int count = 1 + random.nextInt(MAX_RANGED);
		List<RangedWeapon> toAdd = new ArrayList<RangedWeapon>();
		int attempt = 0;
		do {
			for (RangedWeapon rw : available) {
				int chance = rw.getChance();
				if (!hasASpeciality(StringUtils.split(rw.getSkill(), ","))) {
					chance = chance / 3;
				}

				if (chance > 0 && chance <= randomPercentage() && count(toAdd, rw) < rw.getMax()) {
					toAdd.add(rw);
					break;
				}
			}
		} while (toAdd.size() < count && attempt++ < 1000);
		for (RangedWeapon mw : toAdd) {
			person.getRangedWeapons().add(mw);
		}
	}

	/**
	 * Return true if the current person has a specialty skill in the sublist
	 * 
	 * @param rw
	 * @return
	 */
	private boolean hasASpeciality(String[] skillNames) {
		for (String name : skillNames) {
			try {
				GeneralSkill.getForName(name);
			} catch (IllegalArgumentException e) {
				if (person.getSkillSheet().hasSkill(name)) {
					return true;
				}
			}
		}
		return false;
	}

	private void randomizeArray(List list) {
		int count = list.size();
		int ptr;
		for (int i = 0; i < count * 2; i++) {
			ptr = random.nextInt(count);
			Object o = list.get(ptr);
			list.remove(ptr);
			list.add(o);
		}
	}

	private void addRandomExplosives() {
		List<Explosive> available = EquipmentManager.listExplosivesForSkills(person.getSkillSheet());
		randomizeArray(available);
		if (available == null || available.isEmpty()) {
			return;
		}
		int count = 1 + random.nextInt(MAX_MELEE);
		List<Explosive> toAdd = new ArrayList<Explosive>();
		int attempt = 0;
		do {
			for (Explosive ex : available) {
				Integer chance = ex.getChance();
				if (!hasASpeciality(StringUtils.split(ex.getSkill(), ","))) {
					chance = chance / 3;
				}

				if (chance > 0 && chance <= randomPercentage() && count(toAdd, ex) < ex.getMax()) {
					int c = random.nextInt(ex.getMax());
					for (int i = 0; i < c; i++) {
						toAdd.add(ex);
					}
					break;
				}
			}
		} while (toAdd.size() < count && attempt++ < 1000);
		for (Explosive ex : toAdd) {
			person.getExplosives().add(ex);
		}
	}

	private void addRandomMeleeWeapons() {
		List<MeleeWeapon> available = EquipmentManager.listMeleeWeaponsForSkills(person.getSkillSheet());
		randomizeArray(available);
		if (available == null || available.isEmpty()) {
			return;
		}
		int count = 1 + random.nextInt(MAX_MELEE);
		List<MeleeWeapon> toAdd = new ArrayList<MeleeWeapon>();
		int attempt = 0;
		do {
			for (MeleeWeapon mw : available) {
				Integer chance = mw.getChance();
				if (!hasASpeciality(StringUtils.split(mw.getSkill(), ","))) {
					chance = chance / 3;
				}

				if (chance <= randomPercentage() && count(toAdd, mw) < mw.getMax()) {
					toAdd.add(mw);
					break;
				}
			}
		} while (toAdd.size() < count && attempt++ < 1000);
		for (MeleeWeapon mw : toAdd) {
			person.getMeleeWeapons().add(mw);
		}
	}

	private Integer count(List<MeleeWeapon> toAdd, MeleeWeapon mw) {
		int ret = 0;
		for (MeleeWeapon item : toAdd) {
			if (item.equals(mw)) {
				ret++;
			}
		}
		return ret;
	}

	private Integer count(List<Explosive> toAdd, Explosive ex) {
		int ret = 0;
		for (Explosive item : toAdd) {
			if (item.equals(ex)) {
				ret++;
			}
		}
		return ret;
	}

	private Integer count(List<RangedWeapon> toAdd, RangedWeapon rw) {
		int ret = 0;
		for (RangedWeapon item : toAdd) {
			if (item.equals(rw)) {
				ret++;
			}
		}
		return ret;
	}

	private static Integer randomPercentage() {
		return random.nextInt(101);
	}

	private boolean hasSkill(GeneralSkill generalSkill) {
		SkillData data = person.getSkillSheet().getFor(generalSkill);
		return data.getTotalPoints() > 0;
	}

	/**
	 * Generate a random person with a given profile and at a given level Stat
	 * profile for the empty person is set to generic.
	 * 
	 * @param level
	 *            LEVEL_GREENHORN, LEVEL_VETERAN or LEVEL_BIG_DAMN_HERO
	 * @return
	 */
	public static Person getEmptyPerson(int level) {
		Person p = new Person();
		p.setLevel(level);
		p.setCurrentPoints(p.getStartingPoints());
		return p;
	}

	public static void main(String[] args) {
		try {
			//
			// Person p = new Person();
			// Generator gen = new Generator(p, ArchetypesManager.getGeneric());
			// gen.randomizeTraits();
			// gen.randomizeStats();
			// gen.randomizeGeneralSkills();
			// gen.randomizeSpecialtySkills();
			// gen.randomizeWeapons();
		} catch (Throwable e) {
			e.printStackTrace();
		}

	}

	/**
	 * @return Returns the person.
	 */
	public Person getPerson() {
		return person;
	}

	/**
	 * @param person
	 *            The person to set.
	 */
	public void setPerson(Person person) {
		this.person = person;
	}

	/**
	 * Allocate random skills
	 */
	public void randomizeSkills() {
		// randomizeGeneralSkills();
		// randomizeSpecialtySkills();
		int points = person.getStartingSkillPoints();
		while (points > 0) {
			GeneralSkill skill = archetype.getRandomGeneralSkill(RandomFactory.getRandom());
			SkillData data = person.getSkillSheet().getFor(skill);
			if (data.getPoints() < 6) {
				data.setPoints(data.getPoints() + 2);
			} else {
				GeneralSkillBias bias = archetype.getGeneralSkillBiases().get(skill);
				String childName = bias.getRandomChild(person.getSkillSheet(), random);
				data.addChildPoints(childName, 2);
			}
			points -= 2;
		}

	}

	/**
	 * Allocate the general skills in a random fashion.
	 */
	private void randomizeGeneralSkills() {
		int generalCount = random.nextInt(Person.GENERAL_SKILLS_UPPER - Person.GENERAL_SKILLS_LOWER) + Person.GENERAL_SKILLS_LOWER;
		int points = person.getStartingSkillPoints();

		for (int i = 0; i < generalCount; i++) {
			GeneralSkill skill;
			do {
				skill = archetype.getRandomGeneralSkill(RandomFactory.getRandom());
			} while (person.getSkillSheet().getNamed(skill.getName()).getPoints() > 0);
			SkillData data = SkillData.createSkillData(skill);
			data.setPoints(2);
			points = points - 2;
			person.getSkillSheet().getNamed(skill.getName()).setPoints(2);
		}
	}

	/**
	 * Return a random general skill in which the character has >0 points.
	 */
	private String getRandonGeneralSkillName() {
		LinkedList<String> list = new LinkedList<String>();
		for (String s : person.getSkillSheet().getSkillDataMap().keySet().toArray(new String[0])) {
			if (person.getSkillSheet().getNamed(s).getPoints() > 0) {
				list.add(s);
			}
		}
		return list.get(random.nextInt(list.size()));
	}

	/**
	 * Allocate the specialty skills in a random fashion.
	 */
	private void randomizeSpecialtySkills() {
		int points = person.getStartingSkillPoints() - person.getSkillSheet().getTotalPoints();

		while (points > 0) {
			String s = getRandonGeneralSkillName();
			SkillData data = person.getSkillSheet().getNamed(s);
			if (data.getPoints() < 6) {
				// Not maximized yet, so up it!
				data.setPoints(data.getPoints() + 2);
			} else {
				// Maxed out, up a child skill
				String[] childnames = SkillsManager.getChildrenFor(s);
				String child = childnames[random.nextInt(childnames.length)];
				data.addChildPoints(child, 2);

			}
			points -= 2;
		}
	}

}
