package uk.lug.fight;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

public class ListOfDice {

	private List<Dice> diceList;

	public static InititativeDice createEmpty() {
		InititativeDice ret = new InititativeDice();
		List<Dice> diceList = new ArrayList<Dice>();
		ret.setDiceList(diceList);
		return ret;
	}

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

	public ListOfDice() {
		super();
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