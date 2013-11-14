package uk.lug.gui.fight;

public interface EditingListener<T> {

	public void edited(T bean,boolean isValid);
}
