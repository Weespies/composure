package uk.lug.fight;

import java.io.IOException;
import java.io.Serializable;

import org.apache.commons.lang.StringUtils;
import org.jdom.JDOMException;

import uk.lug.dao.records.PersonRecord;
import uk.lug.data.PersonUtils;
import uk.lug.gui.fight.PlayerInfo;
import uk.lug.serenity.npc.model.Person;

public class RoundRow implements Serializable {
	private static final long serialVersionUID = 1L;
	private String name;
	private String player;
	private String identifier;
	private InititativeDice initiativeStats;
	private Integer initiativeRoll;
	private Integer lifeTotal;
	private Integer stun;
	private Integer wounds;

	public String toString() {
		StringBuilder sb = new StringBuilder();
		appendString(sb, "name", name);
		sb.append(",");
		appendString(sb, "player", player);
		sb.append(",");
		appendString(sb, "identifier", identifier);
		sb.append(",");
		appendInteger(sb, "initiativeRoll", initiativeRoll);
		sb.append(",");
		if ( initiativeStats!=null ) {
			appendString(sb, "initiativeDice", initiativeStats.toString());
			sb.append(",");	
		}
		
		appendInteger(sb, "lifeTotal", lifeTotal);
		sb.append(",");
		appendInteger(sb, "stun", stun);
		sb.append(",");
		appendInteger(sb, "wounds", wounds);

		return sb.toString();
	}

	public void setFromString(String str) {
		String fieldName;
		String data;
		for (String field : StringUtils.split(str, ",")) {
			fieldName = StringUtils.substringBefore(field, "[");
			data = StringUtils.substringBetween(field, "[", "]");
			if (StringUtils.equals(fieldName, "name")) {
				name = nullString(data);
			} else if (StringUtils.equals(fieldName, "player")) {
				player = nullString(data);
			} else if (StringUtils.equals(fieldName, "identifier")) {
				identifier = nullString(data);
			} else if (StringUtils.equals(fieldName, "lifeTotal")) {
				lifeTotal = nullInteger(data);
			} else if (StringUtils.equals(fieldName, "stun")) {
				stun = nullInteger(data);
			} else if (StringUtils.equals(fieldName, "wounds")) {
				wounds = nullInteger(data);
			} else if (StringUtils.equals(fieldName, "initiativeRoll")) {
				initiativeRoll = nullInteger(data);
			} else if ( StringUtils.equals(fieldName,"initiativeDice")){
				initiativeStats= new InititativeDice();
				initiativeStats.setFrom(data);
			}
		}
	}

	private Integer nullInteger(String data) {
		if (StringUtils.isEmpty(data) || !StringUtils.isNumeric(data) || StringUtils.equals("null",data) ) {
			return null;
		}
		return Integer.parseInt(data);
	}

	private String nullString(String data) {
		if (StringUtils.isEmpty(data) || StringUtils.equals("null",data) ) {
			return null;
		} else {
			return data;
		}
	}

	private void appendInteger(StringBuilder sb, String field, Integer data) {
		sb.append(field);
		sb.append("[");
		sb.append(data);
		sb.append("]");
	}

	private void appendString(StringBuilder sb, String field, String data) {
		sb.append(field);
		sb.append("[");
		sb.append(data);
		sb.append("]");
	}

	public static RoundRow fromRecord(PersonRecord pr) throws JDOMException, IOException {
		RoundRow ret = new RoundRow();
		ret.setPlayer(pr.getPlayerName());
		Person person = PersonUtils.decode(pr.getData());
		ret.setLifeTotal(person.getLife().getValue());
		ret.setStun(0);
		ret.setWounds(0);
		ret.setInitiativeStats(InititativeDice.createFrom(person));
		ret.setName(person.getName());
		ret.setIdentifier("");
		return ret;
	}

	public static RoundRow createForPlayer(PlayerInfo info) {
		RoundRow ret = new RoundRow();
		ret.setPlayer(info.getPlayer());
		ret.setLifeTotal(0);
		ret.setStun(0);
		ret.setWounds(0);
		ret.setInitiativeStats(InititativeDice.createEmpty());
		ret.setName(info.getName());
		ret.setIdentifier("");
		return ret;
	}

	public RoundRow() {
		super();
		// TODO Auto-generated constructor stub
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public InititativeDice getInitiativeStats() {
		return initiativeStats;
	}

	public void setInitiativeStats(InititativeDice initiativeStats) {
		this.initiativeStats = initiativeStats;
	}

	public Integer getInitiativeRoll() {
		return initiativeRoll;
	}

	public void setInitiativeRoll(Integer initiativeRoll) {
		this.initiativeRoll = initiativeRoll;
	}

	public Integer getLifeTotal() {
		return lifeTotal;
	}

	public void setLifeTotal(Integer lifeTotal) {
		this.lifeTotal = lifeTotal;
	}

	public Integer getStun() {
		return stun;
	}

	public void setStun(Integer stun) {
		this.stun = stun;
	}

	public Integer getWounds() {
		return wounds;
	}

	public void setWounds(Integer wounds) {
		this.wounds = wounds;
	}

	public String getPlayer() {
		return player;
	}

	public void setPlayer(String player) {
		this.player = player;
	}

	public String getIdentifier() {
		return identifier;
	}

	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}

}
