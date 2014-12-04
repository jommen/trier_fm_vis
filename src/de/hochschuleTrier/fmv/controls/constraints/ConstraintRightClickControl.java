package de.hochschuleTrier.fmv.controls.constraints;

import java.awt.event.MouseEvent;
import java.util.List;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import prefuse.controls.Control;
import prefuse.controls.FocusControl;
import prefuse.util.ui.UILib;
import prefuse.visual.VisualItem;
import de.hochschuleTrier.fmv.controls.AddToOrRemoveFromConstraintInspectListControl;
import de.hochschuleTrier.fmv.controls.OpenComplexConstraintActionListener;
import de.hochschuleTrier.fmv.model.impl.ApplicationModel;
import de.hochschuleTrier.fmv.model.impl.constraints.ConstraintNodesToInspectInspectListModel;
import de.hochschuleTrier.fmv.model.interfaces.complexConstraints.IComplexConstraintGroup;

public class ConstraintRightClickControl extends FocusControl {

	public ConstraintRightClickControl() {
		super(1);
		this.button = Control.RIGHT_MOUSE_BUTTON;
	}

	@Override
	public void itemClicked(final VisualItem item, final MouseEvent e) {
		if (UILib.isButtonPressed(e, this.button) && e.getClickCount() == this.ccount) {
			final JPopupMenu contextMenu = new JPopupMenu("Context menu");

			// Open complex constraint view
			final ApplicationModel appModel = ApplicationModel.getInstance();
			final String itemName = item.getString("name");
			final List<IComplexConstraintGroup> complexConstraints = appModel.getComplexConstraints().get(item.getString("name"));
			if (!complexConstraints.isEmpty()) {
				final JMenuItem openComplexConstraint = new JMenuItem("Open complex constraint view");
				openComplexConstraint.addActionListener(new OpenComplexConstraintActionListener(complexConstraints));
				contextMenu.add(openComplexConstraint);
			}

			// Add to or remove from inspect list
			final JMenuItem addToOrRemoveFromFocusGroup = new JMenuItem("Add to focus list");
			final ConstraintNodesToInspectInspectListModel listModel = appModel.getConstraintModel().getNodesToInspectListModel();
			final boolean isInInspectGroup = listModel.contains(itemName);
			if (isInInspectGroup) {
				addToOrRemoveFromFocusGroup.setText("Remove from focus list");
			}
			if (appModel.getConstraintModel().isInspectFilter()) {
				addToOrRemoveFromFocusGroup.setEnabled(false);
			}
			else {
				addToOrRemoveFromFocusGroup.setEnabled(true);
			}

			addToOrRemoveFromFocusGroup.addActionListener(new AddToOrRemoveFromConstraintInspectListControl(itemName, listModel));
			contextMenu.add(addToOrRemoveFromFocusGroup);

			contextMenu.show(e.getComponent(), e.getX(), e.getY());
		}
	}

}
