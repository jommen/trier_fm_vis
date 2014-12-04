package de.hochschuleTrier.fmv.model.impl.constraints;

import java.util.ArrayList;
import java.util.List;

import javax.swing.DefaultListModel;

public class ConstraintNodesToInspectInspectListModel extends DefaultListModel<String> {

	public List<String> getList() {
		final List<String> list = new ArrayList<>();
		for (int i = 0; i < this.getSize(); i++) {
			list.add(this.getElementAt(i));
		}
		return list;
	}

}
