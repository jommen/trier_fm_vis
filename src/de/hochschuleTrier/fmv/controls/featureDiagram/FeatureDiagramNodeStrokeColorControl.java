package de.hochschuleTrier.fmv.controls.featureDiagram;

import java.awt.Color;

import prefuse.action.assignment.ColorAction;
import prefuse.visual.NodeItem;
import prefuse.visual.VisualItem;
import de.hochschuleTrier.fmv.model.impl.ApplicationModel;
import de.hochschuleTrier.fmv.model.interfaces.constraints.IConstraintModel;
import de.hochschuleTrier.fmv.util.NodeLib;

public class FeatureDiagramNodeStrokeColorControl extends ColorAction {

	public FeatureDiagramNodeStrokeColorControl(final String group, final String field, final int color) {
		super(group, field, color);
	}

	@Override
	public int getColor(final VisualItem item) {
		final NodeItem fmNodeItem = (NodeItem) item;

		final IConstraintModel model = ApplicationModel.getInstance().getConstraintModel();
		if (model.isInspectFilter() && model.getNodesToInspectListModel().getList().contains(NodeLib.getName(fmNodeItem))) {
			return Color.RED.getRGB();
		}
		return super.getColor(item);
	}
}
