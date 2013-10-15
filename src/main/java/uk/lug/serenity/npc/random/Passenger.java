/**
 * 
 */
package uk.lug.serenity.npc.random;

/**
 * $Id: This will be filled in on CVS commit $
 * @version $Revision: This will be filled in on CVS commit $
 * @author $Author: This will be filled in on CVS commit $
 * <p>
 * 
 */
/**
 * @author Luggy
 *
 */
public class Passenger {
	public int lowRoll=0;
	public int highRoll=1;
	public int likeLow=-1;
	public int likeHigh=-1;
	public String description;
	
	public void parseFromDataFile( String dataLine ) {
		//Get high and low roll.
		if ( dataLine.charAt(3)=='-')  {
			setLowRoll( Integer.parseInt( dataLine.substring(0,3) ) );
			setHighRoll( Integer.parseInt( dataLine.substring(4,7) ) );
		} else {
			setLowRoll( Integer.parseInt( dataLine.substring(0,3) ) );
			setHighRoll( Integer.parseInt( dataLine.substring(0,3) ) );
		}
		//Likews
		int lptr1 = dataLine.indexOf("[");
		int lptr2 = dataLine.indexOf("]");
		if ( lptr1!=-1 && lptr2!=-1 ) {
			String like = dataLine.substring(lptr1+1, lptr2) ;
			if ( like.indexOf("-")==-1 ) {
				setLikeHigh( Integer.parseInt(like));
				setLikeLow( Integer.parseInt(like));
			} else {
				setLikeHigh( Integer.parseInt(like.substring(4,7)));
				setLikeLow( Integer.parseInt(like.substring(0,3)));
			}
			description = dataLine.substring(lptr2+1,dataLine.length()).trim();
		} else {
			if ( lowRoll==highRoll) {
				description = dataLine.substring(4,dataLine.length() ).trim();
			} else {
				description = dataLine.substring(7,dataLine.length() ).trim();
			}
		}
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder(256);
		sb.append("(");
		sb.append(lowRoll);
		sb.append("-");
		sb.append(highRoll);
		sb.append(")");
		if ( likeLow!=-1 ) {
			sb.append(" like ");
			if ( likeHigh==-1 ) {
				sb.append( likeLow );
			} else {
				sb.append( "[" );
				sb.append( likeLow );
				sb.append( "-" );
				sb.append( likeHigh ) ;
				sb.append( "]");
			}		
		}
		sb.append(" ");
		sb.append( description );
		return sb.toString();
	}
	
	
	/**
	 * @return the description of this passenger.
	 */
	public String getDescription() {
		return description;
	}
	/**
	 * @param description description of this passenger.
	 */
	public void setDescription(String description) {
		this.description = description;
	}
	/**
	 * @return the highest roll that will result in this passenger type.
	 */
	public int getHighRoll() {
		return highRoll;
	}
	/**
	 * @param highRoll highest roll that will result in this passenger type.
	 */
	public void setHighRoll(int highRoll) {
		this.highRoll = highRoll;
	}
	
	/**
	 * @return the lowest roll that will result in this passenger type.
	 */
	public int getLowRoll() {
		return lowRoll;
	}
	
	/**
	 * @param lowRoll highest roll that will result in this passenger type.
	 */
	public void setLowRoll(int lowRoll) {
		this.lowRoll = lowRoll;
	}
	/**
	 * @return the highest roll in the "like xxx-yyy" roll.
	 */
	public int getLikeHigh() {
		return likeHigh;
	}
	/**
	 * @param likeHigh the highest roll in the "like xxx-yyy" roll.
	 */
	public void setLikeHigh(int likeHigh) {
		this.likeHigh = likeHigh;
	}
	
	/**
	 * @return the the lowest roll in the "like xxx-yyy" roll.
	 */
	public int getLikeLow() {
		return likeLow;
	}
	/**
	 * @param likeLow the lowest roll in the "like xxx-yyy" roll.
	 */
	public void setLikeLow(int likeLow) {
		this.likeLow = likeLow;
	}
	
}
