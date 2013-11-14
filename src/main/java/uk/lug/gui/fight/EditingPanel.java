package uk.lug.gui.fight;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;

import uk.lug.util.SwingHelper;

public class EditingPanel<T> extends JPanel {
	private List<EditingListener<T>> editingListeners = new ArrayList<EditingListener<T>>();
	
	public void addEditingListener(EditingListener<T> editingListener) {
		editingListeners.add(editingListener);
	}
	
	public void removeEditingListener(EditingListener<T> editingListener) {
		editingListeners.remove(editingListener);
	}
	
	public void fireEditingListener(final T bean, final Boolean isValid) {
		for ( final EditingListener<T> editingListener : editingListeners ) {
			SwingHelper.runOutsideEventThread(new Runnable(){

				public void run() {
					editingListener.edited(bean, isValid);
				}});			
		}
	}
}
