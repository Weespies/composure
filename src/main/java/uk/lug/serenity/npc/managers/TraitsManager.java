/**
 * 
 */
package uk.lug.serenity.npc.managers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import uk.lug.serenity.npc.model.Person;
import uk.lug.serenity.npc.model.traits.Trait;
import uk.lug.serenity.npc.model.traits.TraitData;
import uk.lug.serenity.npc.model.traits.TraitType;

/**
 * $Id$
 * @version $Revision$
 * @author $Author$
 * <p>
 * 
 */
/**
 * @author Luggy
 *
 */
public class TraitsManager {
	private static final String ASSETS_ROOT = "assets";
	private static final String ASSETS_MAPPING_FILE = "mapping/assetMapping.xml";
	private static final String ASSETS_SCHEMA="asset.xsd";
	private static final String ASSETS_RESOURCE="data/assets.xml";
	private static final String COMPLICATIONS_ROOT = "complications";
	private static final String COMPLICATIONS_MAPPING_FILE = "mapping/complicationMapping.xml";
	private static final String COMPLICATIONS_SCHEMA="complications.xsd";
	private static final String COMPLICATIONS_RESOURCE="data/complications.xml";
	private static Trait[] ASSETS;
	private static Trait[] COMPLICATIONS;
	
	static {
		readAssets();
		readComplications();
	}
	
	/**
	 * Read the asset list.
	 */
	@SuppressWarnings("unchecked")
	private static void readAssets() {
		try {
			List<Trait> list = (List<Trait>) LocalFileController.getInstance()
					.unmarshalResource(ASSETS_RESOURCE, ASSETS_MAPPING_FILE);
			for ( Trait t: list ) {
				t.setTraitType(TraitType.ASSET);
			}
			ASSETS = list.toArray( new Trait[list.size()] ) ;
		} catch (UnmarshallingException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Write the asset list back into the local file
	 * @throws IOException 
	 * @throws MarshallingException 
	 */
	public static void writeAssets() throws MarshallingException {
		ArrayList<Trait> list = new ArrayList<Trait>();
		for (Trait t : ASSETS ) {
			list.add( t );
		}
		LocalFileController.getInstance().marshalResource(list,
				ASSETS_RESOURCE, ASSETS_MAPPING_FILE, ASSETS_ROOT,
				ASSETS_SCHEMA);
	}
	
	/**
	 * Write the complications list back into the local file
	 * @throws IOException 
	 * @throws MarshallingException 
	 */
	public static void writeComplications() throws IOException, MarshallingException {
		ArrayList<Trait> list = new ArrayList<Trait>();
		for (Trait t : COMPLICATIONS) {
			list.add( t );
		}
		LocalFileController.getInstance().marshalResource(list,
				COMPLICATIONS_RESOURCE, COMPLICATIONS_RESOURCE,
				COMPLICATIONS_ROOT, COMPLICATIONS_SCHEMA);
	}
	
	/**
	 * Read the complications list.
	 */
	@SuppressWarnings("unchecked")
	private static void readComplications() {
		try {
			List<Trait> list = (List<Trait>) LocalFileController.getInstance()
				.unmarshalResource(COMPLICATIONS_RESOURCE, COMPLICATIONS_MAPPING_FILE);
			for ( Trait t: list ) {
				t.setTraitType(TraitType.COMPLICATION);
			}
			COMPLICATIONS= list.toArray( new Trait[list.size()] ) ;
		} catch (UnmarshallingException e) {
			e.printStackTrace();
		}
	}

	/**
	 * @return Returns the assets.
	 */
	public static Trait[] getAssets() {
		return ASSETS;
	}
	
	/**
	 * @return Returns the complications.
	 */
	public static Trait[] getComplications() {
		return COMPLICATIONS;
	}
	
	/**
	 * Returns the named asset or null if the named asset cannot be found.
	 */
	public static Trait getAssetNamed( String name ) {
		for ( Trait asset : ASSETS ) {
			if ( asset.getName().equalsIgnoreCase(name) ) {
				return asset;
			}
		}
		return null;
	}
	
	/**
	 * Returns the named asset or null if the named asset cannot be found.
	 */
	public static Trait getComplicationNamed( String name ) {
		for ( Trait asset : COMPLICATIONS ) {
			if ( asset.getName().equalsIgnoreCase(name) ) {
				return asset;
			}
		}
		return null;
	}
	
	/**
	 * Returns the named complication or null if the named complication cannot be found.
	 */
	public static Trait getTraitNamed( String name ) {
		for ( Trait comp : COMPLICATIONS ) {
			if ( comp.getName().equalsIgnoreCase(name) ) {
				return comp;
			}
		}
		return getAssetNamed( name );
	}
	
	/**
	 * Return all major and minor asset choices.
	 * @return
	 */
	public static String[] getPickAssetData() {
		LinkedList<String> res= new LinkedList<String>();
		for ( Trait trait : ASSETS ) {
			if ( trait.isMajor() ) {
				res.add( trait.getName() + " (Major)");
			}
			if ( trait.isMinor() ) {
				res.add( trait.getName() + " (Minor)");
			}
		}			
		return res.toArray( new String[0] ) ;
	}
	
	/**
	 * Return all major and minor complication choices.
	 * @return
	 */
	public static String[] getPickComplicationData() {
		LinkedList<String> res= new LinkedList<String>();
		for ( Trait trait : COMPLICATIONS ) {
			if ( trait.isMajor() ) {
				res.add( trait.getName() + " (Major)");
			}
			if ( trait.isMinor() ) {
				res.add( trait.getName() + " (Minor)");
			}
		}			
		return res.toArray( new String[0] ) ;
	}	
	
	/**
	 * Get all asset choices that have not alraedy been taken.
	 * @param chosen
	 * @return
	 */
	public static TraitData[] getChoosableAssets( Person person ) {
		if ( person.getCurrentStatPoints()<2 ) {
			return new TraitData[0];
		}
		LinkedList<TraitData> res= new LinkedList<TraitData>();
		for ( Trait trait : ASSETS ) {
			boolean found = false;
			//Already picked ?
			for ( TraitData td : person.getAssets() ) {
				if ( td.getName().equalsIgnoreCase( trait.getName() ) ) {
					found = true;
					break;
				}
			}
			if ( !found ) {
				if ( trait.isMajor() && person.getCurrentStatPoints()>3 ) {
					TraitData td = TraitData.createMajorTrait( trait );
					res.add( td );
				}
				if ( trait.isMinor() && person.getCurrentStatPoints()>1 ) {
					TraitData td = TraitData.createMinorTrait( trait );
					res.add( td );
				}
			}
		}			
		return res.toArray( new TraitData[0] ) ;
	}	
	
	/**
	 * Get all asset choices that can replace a given trait.
	 * @param person whose trait will be swapped.
	 * @param swapTrait trait being swapped.
	 * @return
	 */
	public static TraitData[] getSwappableAssets( Person person , TraitData swapTrait) {
		LinkedList<TraitData> res= new LinkedList<TraitData>();
		for ( Trait trait : ASSETS ) {
			boolean found = false;
			//Already picked ?
			for ( TraitData td : person.getAssets() ) {
				if ( td.getName().equalsIgnoreCase( trait.getName() ) ) {
					found = true;
					break;
				}
			}
			if ( !found ) {
				if ( trait.isMajor() && person.getCurrentStatPoints()+swapTrait.getCost()>3 ) {
					TraitData td = TraitData.createMajorTrait( trait );
					res.add( td );
				}
				if ( trait.isMinor() && person.getCurrentStatPoints()+swapTrait.getCost()>1 ) {
					TraitData td = TraitData.createMinorTrait( trait );
					res.add( td );
				}
			}
		}			
		return res.toArray( new TraitData[0] ) ;
	}	
	
	/**
	 * Get all asset choices that can replace a given trait.
	 * @param person whose trait will be swapped.
	 * @param swapTrait trait being swapped.
	 * @return
	 */
	public static TraitData[] getSwappableComplications( Person person , TraitData swapTrait) {
		LinkedList<TraitData> res= new LinkedList<TraitData>();
		for ( Trait trait : COMPLICATIONS ) {
			boolean found = false;
			//Already picked ?
			for ( TraitData td : person.getComplications() ) {
				if ( td.getName().equalsIgnoreCase( trait.getName() ) ) {
					found = true;
					break;
				}
			}
			if ( !found ) {
				if ( trait.isMajor()  ) {
					TraitData td = TraitData.createMajorTrait( trait );
					res.add( td );
				}
				if ( trait.isMinor()  ) {
					TraitData td = TraitData.createMinorTrait( trait );
					res.add( td );
				}
			}
		}			
		return res.toArray( new TraitData[0] ) ;
	}
	
	
	/**
	 * Get all asset choices that have not alraedy been taken.
	 * @param chosen
	 * @return
	 */
	public static String[] getChoosableAssetsNames( Person person ) {
		if ( person.getCurrentStatPoints()<2 ) {
			return new String[0];
		}
		LinkedList<String> res= new LinkedList<String>();
		for ( Trait trait : ASSETS ) {
			boolean found = false;
			//Already picked ?
			for ( TraitData td : person.getAssets() ) {
				if ( td.getName().equalsIgnoreCase( trait.getName() ) ) {
					found = true;
					break;
				}
			}
			if ( !found ) {
				if ( trait.isMajor() && person.getCurrentStatPoints()>3 ) {
					res.add( trait.getName() + " (Major)");
				}
				if ( trait.isMinor() && person.getCurrentStatPoints()>1 ) {
					res.add( trait.getName() + " (Minor)");
				}
			}
		}			
		return res.toArray( new String[0] ) ;
	}
	
	/**
	 * Get all complication choices that have not alraedy been taken.
	 * @param chosen
	 * @return
	 */
	public static String[] getChoosableComplicationNames( Person person ) {
		
		LinkedList<String> res= new LinkedList<String>();
		for ( Trait trait : COMPLICATIONS ) {
			boolean found = false;
			//Already picked ?
			for ( TraitData td : person.getComplications() ) {
				if ( td.getName().equalsIgnoreCase( trait.getName() ) ) {
					found = true;
					break;
				}
			}
			if ( !found ) {
				if ( trait.isMajor()  ) {
					res.add( trait.getName() + " (Major)");
				}
				if ( trait.isMinor()  ) {
					res.add( trait.getName() + " (Minor)");
				}
			}
		}			
		return res.toArray( new String[0] ) ;
	}
	
	/**
	 * Get all complication choices that have not alraedy been taken.
	 * @param chosen
	 * @return
	 */
	public static TraitData[] getChoosableComplications( Person person ) {
		
		LinkedList<TraitData> res= new LinkedList<TraitData>();
		for ( Trait trait : COMPLICATIONS ) {
			boolean found = false;
			//Already picked ?
			for ( TraitData td : person.getComplications() ) {
				if ( td.getName().equalsIgnoreCase( trait.getName() ) ) {
					found = true;
					break;
				}
			}
			if ( !found ) {
				if ( trait.isMajor()  ) {
					res.add( TraitData.createMajorTrait(trait ) );
				}
				if ( trait.isMinor()  ) {
					res.add( TraitData.createMinorTrait(trait ) );
				}
			}
		}			
		return res.toArray( new TraitData[0] ) ;
	}

	
	
}
