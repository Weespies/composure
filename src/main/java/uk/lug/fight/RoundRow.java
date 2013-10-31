package uk.lug.fight;

import java.io.IOException;

import org.jdom.JDOMException;

import uk.lug.dao.records.PersonRecord;
import uk.lug.data.PersonUtils;
import uk.lug.serenity.npc.model.Person;

public class RoundRow {
	private String name;
	private InititativeDice initiativeStats;
	private Integer initiativeRoll;
	private Integer lifeTotal;
	private Integer stun;
	private Integer wounds;
	private String player;
	private String identifier;
	
	public static RoundRow fromRecord( PersonRecord pr) throws JDOMException, IOException {
		RoundRow ret = new RoundRow();
		ret.setPlayer(pr.getPlayerName());
		Person person = PersonUtils.decode(pr.getData());
		ret.setLifeTotal( person.getLife().getValue() );
		ret.setStun(0);
		ret.setWounds(0);
		ret.setInitiativeStats(InititativeDice.createFrom(person));
		ret.setName(person.getName());
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
