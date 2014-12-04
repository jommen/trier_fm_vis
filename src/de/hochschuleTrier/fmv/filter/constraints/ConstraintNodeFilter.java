package de.hochschuleTrier.fmv.filter.constraints;

import java.util.ArrayList;
import java.util.Iterator;

import prefuse.Visualization;
import prefuse.action.GroupAction;
import prefuse.util.PrefuseLib;
import prefuse.visual.NodeItem;
import prefuse.visual.tuple.TableEdgeItem;
import prefuse.visual.tuple.TableNodeItem;
import de.hochschuleTrier.fmv.model.impl.ApplicationModel;
import de.hochschuleTrier.fmv.model.interfaces.constraints.IConstraintModel;
import de.hochschuleTrier.fmv.util.EdgeLib;
import de.hochschuleTrier.fmv.util.NodeLib;
import de.hochschuleTrier.fmv.util.TreeDisplayEnum;
import de.hochschuleTrier.fmv.view.display.FeatureDiagramDisplay;

public class ConstraintNodeFilter extends GroupAction {

	public ConstraintNodeFilter(final String edgeGroupName) {
		super(edgeGroupName);
	}

	@Override
	public void run(final double frac) {
		final IConstraintModel constraintModel = ApplicationModel.getInstance().getConstraintModel();
		if (constraintModel.isInspectFilter()) {
			return;
		}

		this.hideAllNodes();

		final Iterator edges = this.m_vis.items(this.m_group);
		final FeatureDiagramDisplay featureDiagramDisplay = ApplicationModel.getInstance().getCurrentFeatureTreeModel().getDisplay();
		if (featureDiagramDisplay != null) {
			final Visualization treeVis = featureDiagramDisplay.getVisualization();

			final ArrayList<String> visibleNodeNames = new ArrayList<String>();

			try {
				final Iterator visibleTreeNodes = treeVis.visibleItems(TreeDisplayEnum.NODES.toString());
				while (visibleTreeNodes.hasNext()) {
					final TableNodeItem node = (TableNodeItem) visibleTreeNodes.next();
					final String name = NodeLib.getName(node);
					visibleNodeNames.add(name);
				}

				while (edges.hasNext()) {
					final TableEdgeItem edge = (TableEdgeItem) edges.next();
					final TableNodeItem source = (TableNodeItem) edge.getSourceNode();
					final TableNodeItem target = (TableNodeItem) edge.getTargetNode();

					NodeLib.setFullExpanded(source, false);
					NodeLib.setFullExpanded(target, false);
					if (visibleNodeNames.contains(NodeLib.getName(source))) {
						NodeLib.setFullExpanded(source, true);
					}
					if (visibleNodeNames.contains(NodeLib.getName(target))) {
						NodeLib.setFullExpanded(target, true);
					}

					if (EdgeLib.isConstraintEdge(edge)) {
						NodeLib.setHasCrossTreeEdge(source, true);
						NodeLib.setHasCrossTreeEdge(target, true);
						if (visibleNodeNames.contains(source.get("name")) || visibleNodeNames.contains(target.get("name"))) {
							PrefuseLib.updateVisible(source, true);
							PrefuseLib.updateVisible(target, true);
						}
					}
					else {
						PrefuseLib.updateVisible(source, false);
						PrefuseLib.updateVisible(target, false);
					}
				}
			}
			catch (final Exception e) {
				// TODO: handle exception
				System.out.println("couldn't conncect to tree visualization.");
			}
		}
	}

	private void hideAllNodes() {
		final Iterator<NodeItem> nodeIterator = this.m_vis.items(TreeDisplayEnum.NODES.toString());
		while (nodeIterator.hasNext()) {
			final NodeItem node = nodeIterator.next();
			PrefuseLib.updateVisible(node, false);
		}
	}
}
