/**
 * 
 */
package uk.lug.serenity.npc.model.skills;

/**
 * @author Luggy
 * Placeholder class for unmarshalling child skills because castors
 * xml mapping is so fucking retarded it splits strings.
 *
 */
public class Specialty {
	private String name;
	
	/**
	 * 
	 */
	public Specialty() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param name
	 */
	public Specialty(String name) {
		super();
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
