package de.hochschuleTrier.fmv.filter.constraints;

import java.util.Iterator;

import prefuse.Visualization;
import prefuse.action.GroupAction;
import prefuse.data.Graph;
import prefuse.data.Node;
import prefuse.util.PrefuseLib;
import prefuse.visual.EdgeItem;
import prefuse.visual.NodeItem;
import de.hochschuleTrier.fmv.model.impl.constraints.ConstraintNodesToInspectInspectListModel;
import de.hochschuleTrier.fmv.util.InspectingConstraintNodesIterator;
import de.hochschuleTrier.fmv.util.NodeLib;
import de.hochschuleTrier.fmv.util.TreeDisplayEnum;

public class ConstraintInspectingFilter extends GroupAction {

	final ConstraintNodesToInspectInspectListModel model;
	final Visualization visualization;
	private final Graph graph;

	public ConstraintInspectingFilter(final ConstraintNodesToInspectInspectListModel model, final Graph graph, final Visualization vis) {
		this.model = model;
		this.visualization = vis;
		this.graph = graph;
	}

	@Override
	public void run(final double frac) {
		if (!this.model.getList().isEmpty()) {
			this.hideEdges();
			this.hideNodes();

			final InspectingConstraintNodesIterator iter = new InspectingConstraintNodesIterator(this.graph, this.model.getList());
			while (iter.hasNext()) {
				final Node node = iter.next();
				final NodeItem visualNode = (NodeItem) this.visualization.getVisualItem(TreeDisplayEnum.NODES.toString(), node);
				// TODO: Highlight inspected nodes
				//				final boolean isInspectedNode = this.model.getList().contains(NodeLib.getName(visualNode));
				PrefuseLib.updateVisible(visualNode, true);
				final Iterator<NodeItem> neighbors = visualNode.neighbors();
				while (neighbors.hasNext()) {
					final NodeItem neighbor = neighbors.next();
					if (neighbor.isVisible()) {
						final EdgeItem edge = (EdgeItem) NodeLib.getConstraintEdgeBetween(visualNode, neighbor);
						if (edge != null) {
							PrefuseLib.updateVisible(edge, true);
						}
					}
				}
			}
		}
	}

	private void hideNodes() {
		final Iterator<NodeItem> visibleNodes = this.visualization.visibleItems(TreeDisplayEnum.NODES.toString());
		while (visibleNodes.hasNext()) {
			final NodeItem visibleNode = visibleNodes.next();
			PrefuseLib.updateVisible(visibleNode, false);
		}
	}

	private void hideEdges() {
		final Iterator<EdgeItem> edgeItems = this.visualization.visibleItems(TreeDisplayEnum.EDGES.toString());
		while (edgeItems.hasNext()) {
			final EdgeItem visibleEdge = edgeItems.next();
			PrefuseLib.updateVisible(visibleEdge, false);
		}
	}

}
