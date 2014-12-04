package de.hochschuleTrier.fmv.controls;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import de.hochschuleTrier.fmv.model.impl.constraints.ConstraintNodesToInspectInspectListModel;

public class AddToOrRemoveFromConstraintInspectListControl implements ActionListener {

	private String itemName;
	private final ConstraintNodesToInspectInspectListModel listModel;

	public AddToOrRemoveFromConstraintInspectListControl(final String itemName, final ConstraintNodesToInspectInspectListModel listModel) {
		this.itemName = itemName;
		this.listModel = listModel;
	}

	@Override
	public void actionPerformed(final ActionEvent e) {
		final boolean isInInspectingGroup = this.listModel.contains(this.itemName);
		if (isInInspectingGroup) {
			this.listModel.removeElement(this.itemName);
		}
		else {
			this.listModel.addElement(this.itemName);
		}
	}

	public void setItemName(final String itemName) {
		this.itemName = itemName;
	}

}
