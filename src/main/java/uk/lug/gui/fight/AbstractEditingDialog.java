package uk.lug.gui.fight;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;

import uk.lug.gui.util.CachedImageLoader;
import uk.lug.util.SwingHelper;

public abstract class AbstractEditingDialog<T> extends JDialog implements EditingListener<T> {
	protected EditingPanel<T> editingPanel;
	private JPanel main;
	private JPanel okCancelPanel;
	private boolean ok = false;
	protected T editBean;

	public AbstractEditingDialog(Dialog owner, String title, T editBean) {
		super(owner, title);
		this.editBean = editBean;
		buildUI();
	}

	public AbstractEditingDialog(Frame owner, String title, T editBean) {
		super(owner, title);
		this.editBean = editBean;
		buildUI();

	}

	public AbstractEditingDialog(Window owner, String title, T editBean) {
		super(owner, title);
		this.editBean = editBean;
		buildUI();
	}

	private void buildUI() {
		setModal(true);
		main = new JPanel(new BorderLayout());
		buildEditingPanel();
		main.add(editingPanel, BorderLayout.CENTER);
		buildOKCancelPanel();
		main.add(okCancelPanel, BorderLayout.SOUTH);
		editingPanel.addEditingListener(this);
		getContentPane().add(main);
		pack();
	}

	protected abstract void buildEditingPanel();

	private void buildOKCancelPanel() {
		okCancelPanel = new JPanel(new GridBagLayout());
		okCancelPanel.add(new JButton(okAction), new GridBagConstraints(0, 0, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE,
				SwingHelper.DEFAULT_INSETS, 0, 0));
		okCancelPanel.add(new JButton(cancelAction), new GridBagConstraints(1, 0, 1, 1, 0, 0, GridBagConstraints.EAST, GridBagConstraints.NONE,
				SwingHelper.DEFAULT_INSETS, 0, 0));
	}

	protected Action okAction = new AbstractAction("OK", CachedImageLoader.CHECK_ICON) {

		public void actionPerformed(ActionEvent e) {
			doOk();
		}
	};

	private Action cancelAction = new AbstractAction("Cancel", CachedImageLoader.DELETE_ICON) {

		public void actionPerformed(ActionEvent e) {
			doCancel();
		}
	};

	public void edited(T bean, final boolean isValid) {
		this.editBean = bean;
		SwingHelper.runInEventThread(new Runnable(){

			public void run() {
				okAction.setEnabled(isValid);
			}});
		
	}

	protected void doCancel() {
		ok = false;
		setVisible(false);
	}

	protected void doOk() {
		ok = true;
		setVisible(false);

	}

	public boolean isOk() {
		return ok;
	}

	public T getEditBean() {
		return ok ? editBean : null;
	}

}