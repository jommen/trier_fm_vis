package de.hochschuleTrier.fmv.view;

import java.awt.AWTEvent;
import java.awt.Component;
import java.awt.EventQueue;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;

import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

import de.hochschuleTrier.fmv.model.impl.ApplicationModel;
import de.hochschuleTrier.fmv.model.impl.constraints.ConstraintNodesToInspectInspectListModel;

public class ConstraintNodesToInspectList extends JList<String> {

	private JMenuItem remove;

	public ConstraintNodesToInspectList() {
		final ConstraintNodesToInspectInspectListModel model = new ConstraintNodesToInspectInspectListModel();
		this.setModel(model);
		this.setComponentPopupMenu(this.createPopupMenu());
	}

	private JPopupMenu createPopupMenu() {
		final JPopupMenu menu = new JPopupMenu();

		menu.addPopupMenuListener(new ListPopupMenuAdapter());

		this.remove = new JMenuItem("Remove Feature", 'r');

		menu.add(this.remove);
		this.remove.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				final ConstraintNodesToInspectInspectListModel model = (ConstraintNodesToInspectInspectListModel) ConstraintNodesToInspectList.this.getModel();
				model.remove(ConstraintNodesToInspectList.this.getSelectedIndex());
			}
		});
		return menu;
	}

	private class ListPopupMenuAdapter implements PopupMenuListener {
		private void maybeUpdateSelection(final PopupMenuEvent e) {
			final AWTEvent awtEvent = EventQueue.getCurrentEvent();
			final MouseEvent me;
			if (!(awtEvent instanceof MouseEvent) || !(me = (MouseEvent) awtEvent).isPopupTrigger()) {
				return;
			}
			final JPopupMenu menu = (JPopupMenu) e.getSource();
			final Component invoker = menu.getInvoker();

			if (!(invoker instanceof JList)) {
				return;
			}
			final JList list = (JList) invoker;
			final Point p = me.getPoint();
			final int row = list.locationToIndex(p);
			if (row == -1) {
				return;
			}

			list.clearSelection();
			list.setSelectedIndex(row);
		}

		@Override
		public void popupMenuWillBecomeVisible(final PopupMenuEvent e) {
			ConstraintNodesToInspectList.this.remove.setEnabled(true);
			if (ApplicationModel.getInstance().getConstraintModel().isInspectFilter()) {
				ConstraintNodesToInspectList.this.remove.setEnabled(false);
			}
			this.maybeUpdateSelection(e);
		}

		@Override
		public void popupMenuWillBecomeInvisible(final PopupMenuEvent e) {
			this.maybeUpdateSelection(e);
		}

		@Override
		public void popupMenuCanceled(final PopupMenuEvent e) {
			this.maybeUpdateSelection(e);
		}
	}

}
