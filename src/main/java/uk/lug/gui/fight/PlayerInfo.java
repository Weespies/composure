package uk.lug.gui.fight;

public class PlayerInfo {
	private String player;
	private String name;
	
	public static PlayerInfo create(String player, String name) {
		PlayerInfo ret = new PlayerInfo();
		ret.setPlayer(player);
		ret.setName(name);
		return ret;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("(");
		sb.append(player == null ? "null" : player);
		sb.append("|");
		sb.append(name == null ? "null" : name);
		sb.append(")");
		return sb.toString();
	}

	public String getPlayer() {
		return player;
	}

	public void setPlayer(String player) {
		this.player = player;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
