package uk.lug.fight;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.apache.commons.lang.StringUtils;

import uk.lug.serenity.npc.model.Person;
import uk.lug.serenity.npc.model.stats.DerivedStat;
import uk.lug.serenity.npc.model.stats.NamedStat;
import uk.lug.util.RandomFactory;

public class InititativeDice implements Serializable {
	private static final long serialVersionUID = 1L;
	private List<Dice> diceList;

	@Override
	public String toString() {
		if (diceList == null || diceList.isEmpty()) {
			return "";
		}
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < diceList.size(); i++) {
			sb.append(diceList.get(i));
			if (i < diceList.size() - 1) {
				sb.append("+");
			}
		}
		return sb.toString();
	}

	public List<Dice> getDiceList() {
		return diceList;
	}

	public void setDiceList(List<Dice> diceList) {
		this.diceList = diceList;
	}

	public static InititativeDice createEmpty() {
		InititativeDice ret = new InititativeDice();
		List<Dice> diceList = new ArrayList<Dice>();
		ret.setDiceList(diceList);
		return ret;
	}

	public static InititativeDice createFrom(Person person) {
		InititativeDice ret = new InititativeDice();
		List<Dice> diceList = new ArrayList<Dice>();
		DerivedStat dstat = person.getInitiative();
		for (NamedStat ns : dstat.getComponentStats()) {
			diceList.addAll(ns.getDiceList());
		}
		ret.setDiceList(diceList);
		return ret;
	}

	public Integer getValue() {
		if (diceList == null || diceList.isEmpty()) {
			return 0;
		}
		Integer ret = 0;
		for (Dice dice : diceList) {
			ret += dice.getValue();
		}
		return ret;
	}

	public Integer roll() {
		int ret = 0;
		for (Dice die : diceList) {
			ret = ret + die.roll();
		}
		return ret;

	}

	public void setFrom(String data) {
		if (diceList == null) {
			diceList = new ArrayList<Dice>();
		} else {
			diceList.clear();
		}
		if (StringUtils.isEmpty(data)) {
			return;
		}
		for (String str : StringUtils.split(data, "+")) {
			Integer value = Integer.parseInt(StringUtils.substringAfter(str.toUpperCase(), "D"));
			diceList.add(Dice.getForValue(value));
		}
	}
}
