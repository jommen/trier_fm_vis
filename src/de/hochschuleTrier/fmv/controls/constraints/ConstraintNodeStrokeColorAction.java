package de.hochschuleTrier.fmv.controls.constraints;

import java.awt.Color;

import prefuse.action.assignment.ColorAction;
import prefuse.util.ColorLib;
import prefuse.visual.NodeItem;
import prefuse.visual.VisualItem;
import de.hochschuleTrier.fmv.model.impl.ApplicationModel;
import de.hochschuleTrier.fmv.model.interfaces.constraints.IConstraintModel;
import de.hochschuleTrier.fmv.util.NodeLib;

public class ConstraintNodeStrokeColorAction extends ColorAction {

	public ConstraintNodeStrokeColorAction(final String group, final String field, final int color) {
		super(group, field, color);
	}

	@Override
	public int getColor(final VisualItem item) {
		if (item instanceof NodeItem) {
			final NodeItem fmNodeItem = (NodeItem) item;

			final IConstraintModel model = ApplicationModel.getInstance().getConstraintModel();
			if (model.isInspectFilter() && model.getNodesToInspectListModel().getList().contains(NodeLib.getName(fmNodeItem))) {
				return Color.RED.getRGB();
			}

			if (!NodeLib.isFullExpanded(fmNodeItem)) {
				return ColorLib.gray(175);
			}
		}
		return super.getColor(item);
	}

}
