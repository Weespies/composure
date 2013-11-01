package uk.lug.gui.fight;

import java.util.ArrayList;
import java.util.List;

import uk.lug.fight.Dice;
import uk.lug.serenity.npc.model.Person;
import uk.lug.serenity.npc.model.equipment.Explosive;
import uk.lug.serenity.npc.model.equipment.MeleeWeapon;
import uk.lug.serenity.npc.model.equipment.RangedWeapon;
import uk.lug.serenity.npc.model.stats.MainStat;

public class DataRow {
	private String name;
	private String roll;
	private String damage;

	public static DataRow createFrom(Person person, RangedWeapon rw) {
		DataRow ret = new DataRow();
		ret.setName("Ranged : "+rw.getName());
		List<Dice> skillRoll = new ArrayList<Dice>();
		int agility = person.getMainStats().getMainStats().get(MainStat.Agility.getKey()).getValue();
		skillRoll.addAll(Dice.makeList(agility));
		skillRoll.addAll(person.getSkillSheet().highestSkillOf(rw.getSkill()));
		ret.setRoll(writeList(skillRoll));
		ret.setDamage(rw.getDamage());
		return ret;
	}


	public static DataRow createFrom(Person person, Explosive ex) {
		DataRow ret = new DataRow();
		ret.setName("Explosive : "+ex.getName());
		List<Dice> skillRoll = new ArrayList<Dice>();
		int agility = person.getMainStats().getMainStats().get(MainStat.Agility.getKey()).getValue();
		skillRoll.addAll(Dice.makeList(agility));
		
		skillRoll.addAll(person.getSkillSheet().highestSkillOf(ex.getSkill()));
		ret.setRoll(writeList(skillRoll));
		ret.setDamage(ex.getDamage());
		return ret;
	}
	public static DataRow createFrom(Person person, MeleeWeapon mw) {
		DataRow ret = new DataRow();
		ret.setName("Melee : "+mw.getName());
		List<Dice> skillRoll = new ArrayList<Dice>();
		int agility = person.getMainStats().getMainStats().get(MainStat.Agility.getKey()).getValue();
		skillRoll.addAll(Dice.makeList(agility));
		skillRoll.addAll(person.getSkillSheet().highestSkillOf(mw.getSkill()));
		ret.setRoll(writeList(skillRoll));
		ret.setDamage(mw.getDamage());
		return ret;
	}

	private static String writeList(List<Dice> diceList) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < diceList.size(); i++) {
			sb.append(diceList.get(i).toString());
			if (i < diceList.size() - 1) {
				sb.append("+");
			}
		}
		return sb.toString();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDamage() {
		return damage;
	}

	public void setDamage(String damage) {
		this.damage = damage;
	}

	public String getRoll() {
		return roll;
	}

	public void setRoll(String roll) {
		this.roll = roll;
	}
}
