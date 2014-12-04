package de.hochschuleTrier.fmv.filter.constraints;

import java.util.Iterator;

import prefuse.Constants;
import prefuse.Visualization;
import prefuse.action.GroupAction;
import prefuse.util.PrefuseLib;
import prefuse.visual.EdgeItem;
import prefuse.visual.NodeItem;
import prefuse.visual.VisualItem;
import de.hochschuleTrier.fmv.util.TreeDisplayEnum;
import de.hochschuleTrier.fmv.util.VisualBreadthFirstIterator;

/**
 * Filter which focusses on one constraint node, show his neighbors and hide all other nodes
 * 
 */
public class ConstraintFocusFilter extends GroupAction {

	private final VisualBreadthFirstIterator visualBreadthFirstIterator;
	private int distance;

	public ConstraintFocusFilter(final int distance) {
		this.visualBreadthFirstIterator = new VisualBreadthFirstIterator();
		this.distance = distance;
	}

	@Override
	public void run(final double frac) {
		final Iterator<VisualItem> focusItems = this.m_vis.visibleItems(Visualization.FOCUS_ITEMS);
		while (focusItems.hasNext()) {
			final VisualItem visualItem = focusItems.next();
			if (visualItem instanceof NodeItem) {
				final NodeItem item = (NodeItem) visualItem;
				this.calcDistance(item);
				this.hideNodes(item);
				this.hideEdges(item);
			}
		}
	}

	private void calcDistance(final NodeItem item) {
		final Iterator<VisualItem> visibleItems = item.getVisualization().visibleItems(TreeDisplayEnum.TREE.toString());
		while (visibleItems.hasNext()) {
			final VisualItem visibleItem = visibleItems.next();
			visibleItem.setDOI(Constants.MINIMUM_DOI);
		}

		this.visualBreadthFirstIterator.init(item, this.distance, Constants.NODE_AND_EDGE_TRAVERSAL);
		this.visualBreadthFirstIterator.setExcludeInvisible(true);

		while (this.visualBreadthFirstIterator.hasNext()) {
			final VisualItem nextItem = (VisualItem) this.visualBreadthFirstIterator.next();
			final int depth = this.visualBreadthFirstIterator.getDepth(nextItem);
			nextItem.setDOI(-depth);
		}
	}

	private void hideNodes(final NodeItem item) {
		final Iterator<NodeItem> visibleNodes = item.getVisualization().visibleItems(TreeDisplayEnum.NODES.toString());
		while (visibleNodes.hasNext()) {
			final NodeItem visibleNode = visibleNodes.next();
			if (visibleNode.getDOI() < -this.distance) {
				PrefuseLib.updateVisible(visibleNode, false);
			}
		}
	}

	private void hideEdges(final NodeItem item) {
		final Iterator<EdgeItem> edgeItems = item.getVisualization().visibleItems(TreeDisplayEnum.EDGES.toString());
		while (edgeItems.hasNext()) {
			final EdgeItem visibleEdge = edgeItems.next();
			if (visibleEdge.getSourceItem().getDOI() < -this.distance || visibleEdge.getTargetItem().getDOI() < -this.distance) {
				PrefuseLib.updateVisible(visibleEdge, false);
			}
		}
	}

	public void setDistance(final int distance) {
		this.distance = distance;
	}

}
