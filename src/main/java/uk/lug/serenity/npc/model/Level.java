package uk.lug.serenity.npc.model;

public enum Level {
	GREENHORN(1),VETERAN(2),BIG_DAMN_HERO(3);
	private Integer value;

	private Level(Integer value) {
		this.value = value;
	}

	public Integer getValue() {
		return value;
	}
}
