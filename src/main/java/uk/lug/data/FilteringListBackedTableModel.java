package uk.lug.data;

import java.util.List;

import uk.lug.data.filter.Filter;
import uk.lug.util.SwingHelper;

public abstract class FilteringListBackedTableModel<T> extends AbstractListBackedTableModel<T> {
	private List<T> original;
	
	public FilteringListBackedTableModel(List<T> dataList) {
		super(dataList);
		doFilter();
	}

	protected void doFilter() {
		if ( getFilter()==null ) {
			return;
		}
		final List<T> filtered = getFilter().filter(original);
		SwingHelper.runInEventThread(new Runnable(){

			public void run() {
				FilteringListBackedTableModel.this.replaceAll( filtered );
			}});
		
	}
	
	protected abstract Filter<T> getFilter() ;


}
