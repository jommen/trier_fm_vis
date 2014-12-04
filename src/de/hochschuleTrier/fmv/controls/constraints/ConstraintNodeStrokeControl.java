package de.hochschuleTrier.fmv.controls.constraints;

import java.awt.BasicStroke;

import prefuse.action.assignment.StrokeAction;
import prefuse.util.StrokeLib;
import prefuse.visual.NodeItem;
import prefuse.visual.VisualItem;
import de.hochschuleTrier.fmv.util.NodeLib;

public class ConstraintNodeStrokeControl extends StrokeAction {

	public ConstraintNodeStrokeControl(final String group, final BasicStroke stroke) {
		super(group, stroke);
	}

	@Override
	public BasicStroke getStroke(final VisualItem item) {
		if (item instanceof NodeItem) {
			final NodeItem fmNodeItem = (NodeItem) item;
			if (!NodeLib.isFullExpanded(fmNodeItem)) {
				return StrokeLib.getStroke(1, StrokeLib.DOTS);
			}
			return StrokeLib.getStroke(1);
		}
		return super.getStroke(item);
	}

}
