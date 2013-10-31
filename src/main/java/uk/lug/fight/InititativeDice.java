package uk.lug.fight;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import uk.lug.serenity.npc.model.Person;
import uk.lug.serenity.npc.model.stats.DerivedStat;
import uk.lug.serenity.npc.model.stats.NamedStat;
import uk.lug.util.RandomFactory;

public class InititativeDice {
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
		int ret =0;
		for ( Dice die : diceList) {
			ret=ret+die.roll();
		}
		return ret;

	}
}
