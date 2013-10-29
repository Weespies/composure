/**
 * 
 */
package uk.lug.data.filter;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Paul
 *
 */
public abstract class AbstractFilter<T> implements Filter<T> {

	public List<T> filter(List<T> originalList) {
		List<T> ret = new ArrayList<T>(originalList.size());
		for ( T object : originalList ) {
			if (matches(object)){
				ret.add(object);
			}			
		}
		return ret;
	}

}
