package uk.lug.fight;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import uk.lug.serenity.npc.model.Person;
import uk.lug.serenity.npc.model.stats.DerivedStat;
import uk.lug.serenity.npc.model.stats.NamedStat;

public class InititativeDice extends ListOfDice implements Serializable {
	private static final long serialVersionUID = 1L;

	public static ListOfDice createFrom(Person person) {
		ListOfDice ret = new InititativeDice();
		List<Dice> diceList = new ArrayList<Dice>();
		DerivedStat dstat = person.getInitiative();
		for (NamedStat ns : dstat.getComponentStats()) {
			diceList.addAll(ns.getDiceList());
		}
		ret.setDiceList(diceList);
		return ret;
	}
}
