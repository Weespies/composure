/**
 * 
 */
package uk.lug.serenity.npc.random;

import java.util.LinkedList;
import java.util.Random;

import uk.lug.serenity.npc.managers.SkillsManager;
import uk.lug.serenity.npc.model.skills.Skill;
import uk.lug.util.RandomFactory;

/**
 * $Id$
 * @version $Revision$
 * @author $Author$
 * <p>Holds a list of biases for skills.</p>
 * <p>Also performs the action of getting a random skill.</p>
 */
/**
 * @author Luggy
 *
 */
public class SkillProfile {
	private String profileName;
	private LinkedList<SkillBias> biases = new LinkedList<SkillBias>();
	private Random random = RandomFactory.getRandom();
	
	/**
	 * @param profileName
	 */
	public SkillProfile(String profileName) {
		super();
		this.profileName = profileName;
	}
	
	/**
	 * @return the biases
	 */
	public LinkedList<SkillBias> getBiases() {
		return biases;
	}
	
	/**
	 * @param biases the biases to set
	 */
	public void setBiases(LinkedList<SkillBias> biases) {
		this.biases = biases;
	}
	
	/**
	 * @return the profileName
	 */
	public String getProfileName() {
		return profileName;
	}
	
	/**
	 * @param profileName the profileName to set
	 */
	public void setProfileName(String profileName) {
		this.profileName = profileName;
	}
	
	/**
	 * Add a skill bias
	 */
	public void addBias( SkillBias bias ) {
		biases.add(bias);
	}

	/**
	 * Return this profile as a human readable string. ( it will be multiline )/
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder(1024);
		sb.append( getProfileName() ) ;
		for ( SkillBias bias : biases ) {
			sb.append("\n  +-- ");
			sb.append( bias.getSkillName() );
			sb.append(" ");
			sb.append( bias.getBias() );
			sb.append("%");
		}
		return sb.toString();
	}
	
	/**
	 * @return a skill at complete random
	 */
	public Skill getUnbiasedSkill() {
		int sk = random.nextInt( SkillsManager.getSkills().length );
		return SkillsManager.getSkills()[sk];
	}
	
	/**
	 * Return a random skill biased towards an entry in the profile.
	 * @return
	 */
	public Skill getBiasedSkill()  {
		int dice = random.nextInt(100);
		for ( SkillBias bias : biases ) {
			dice = dice-(int)bias.getBias();
			if ( dice<=0 ) {
				return SkillsManager.getNamedSkill( bias.getSkillName() );
			}
		}
		return getUnbiasedSkill();
	}
}
