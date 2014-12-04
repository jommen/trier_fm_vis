package de.hochschuleTrier.fmv.controls.featureDiagram;

import java.awt.Color;

import prefuse.Visualization;
import prefuse.action.assignment.ColorAction;
import prefuse.util.ColorLib;
import prefuse.visual.NodeItem;
import prefuse.visual.VisualItem;
import de.hochschuleTrier.fmv.model.impl.ApplicationModel;
import de.hochschuleTrier.fmv.model.interfaces.constraints.IConstraintModel;
import de.hochschuleTrier.fmv.util.NodeLib;

public class FeatureDiagramNodeColorControl extends ColorAction {

	private final static int PATH_TO_NODE_COLOR = ColorLib.rgb(164, 193, 193);

	public FeatureDiagramNodeColorControl(final String group, final String field) {
		super(group, field);
	}

	@Override
	public int getColor(final VisualItem item) {
		final NodeItem nodeItem = (NodeItem) item;

		final IConstraintModel model = ApplicationModel.getInstance().getConstraintModel();
		if (model.isInspectFilter() && model.getNodesToInspectListModel().getList().contains(NodeLib.getName(nodeItem))) {
			return Color.RED.getRGB();
		}
		else if (ApplicationModel.getInstance().getPathToSelectedNode().contains(nodeItem)) {
			return FeatureDiagramNodeColorControl.PATH_TO_NODE_COLOR;
		}
		else if (!NodeLib.getColor(nodeItem).equals(Color.WHITE)) {
			return NodeLib.getColor(nodeItem).getRGB();
		}
		else if (this.m_vis.isInGroup(item, Visualization.FOCUS_ITEMS)) {
			return ColorLib.rgb(198, 229, 229);
		}
		else if (this.m_vis.isInGroup(item, Visualization.SEARCH_ITEMS)) {
			return ColorLib.rgb(255, 255, 0);
		}
		else if (nodeItem.isHighlighted()) {
			return Color.YELLOW.getRGB();
		}
		else if (nodeItem.getParent() == null) {
			return FeatureDiagramNodeColorControl.PATH_TO_NODE_COLOR;
		}
		else {
			return Color.WHITE.getRGB();
		}

	}

}
