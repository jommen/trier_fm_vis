package de.hochschuleTrier.fmv.controls.constraints;

import java.awt.Color;

import prefuse.Visualization;
import prefuse.action.assignment.ColorAction;
import prefuse.util.ColorLib;
import prefuse.visual.NodeItem;
import prefuse.visual.VisualItem;
import de.hochschuleTrier.fmv.model.impl.ApplicationModel;
import de.hochschuleTrier.fmv.model.interfaces.constraints.IConstraintModel;
import de.hochschuleTrier.fmv.util.NodeLib;

public class ConstraintNodeFillColorControl extends ColorAction {

	final ExtendedNeighborHighlightControl enhancedNeighborHighlightControl;

	public ConstraintNodeFillColorControl(final String group, final ExtendedNeighborHighlightControl enhancedNeighborHighlightControl) {
		super(group, VisualItem.FILLCOLOR, ColorLib.rgb(0x9E, 0xCF, 0xFF));
		this.enhancedNeighborHighlightControl = enhancedNeighborHighlightControl;
	}

	@Override
	public int getColor(final VisualItem item) {
		final NodeItem nodeItem = (NodeItem) item;
		final IConstraintModel model = ApplicationModel.getInstance().getConstraintModel();

		if (this.m_vis.isInGroup(item, Visualization.SEARCH_ITEMS)) {
			return ColorLib.rgb(0xFF, 0xFE, 0x5C);
		}
		else if (model.isInspectFilter() && model.getNodesToInspectListModel().getList().contains(NodeLib.getName(nodeItem))) {
			return Color.RED.getRGB();
		}
		else if (this.m_vis.isInGroup(item, Visualization.FOCUS_ITEMS)) {
			return ColorLib.rgb(0xFF, 0x64, 0x64);
		}
		else if (item.isHighlighted()) {
			return this.enhancedNeighborHighlightControl.getHighlightColorAction().getColor(nodeItem);
		}
		// if (item instanceof TableFMNodeItem) {
		// final TableFMNodeItem fmNodeItem = (TableFMNodeItem) item;
		// if (fmNodeItem.isFullExpanded()) {
		// return ColorLib.gray(200);
		// }
		// }
		return super.getColor(item);
	}
}
