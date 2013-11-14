package uk.lug.data.filter;

import java.util.List;

public interface Filter<T> {
	/**
	 * Return a copy of the original list containing all
	 * items that match the filter.
	 * @param originalList
	 * @return
	 */
	public List<T> filter(List<T> originalList);
	
	public boolean matches(T object);
}
