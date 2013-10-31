package uk.lug.fight;

import java.util.Random;

import uk.lug.util.RandomFactory;

public class Dice {
	private static final Random random = RandomFactory.getRandom();
	public static final Dice D2 = Dice.create("D2", 2);
	public static final Dice D4 = Dice.create("D4", 4);
	public static final Dice D6 = Dice.create("D6", 6);
	public static final Dice D8 = Dice.create("D8", 8);
	public static final Dice D10 = Dice.create("D10", 10);
	public static final Dice D12 = Dice.create("D12", 12);
	public static final Dice D20 = Dice.create("D20", 20);

	private String name;
	private Integer value;

	@Override
	public String toString() {
		return name;
	}
	
	public static Dice getForValue(Integer v) {
		if (v == 2) {
			return D2;
		} else if (v==4 ) {
			return D4;
		} else if (v==6 ) {
			return D6;
		} else if (v==8 ) {
			return D8;
		} else if (v==10 ) {
			return D10;
		} else if (v==12 ) {
			return D12;
		} else if (v==20 ) {
			return D20;
		} else {
			 throw new IllegalStateException("Cannot find dice for "+v);
		}
	}

	public static Dice create(String name, Integer value) {
		Dice ret = new Dice();
		ret.setName(name);
		ret.setValue(value);
		return ret;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getValue() {
		return value;
	}

	public void setValue(Integer value) {
		this.value = value;
	}

	public int roll() {
		return random.nextInt(value)+1;
	}

}
