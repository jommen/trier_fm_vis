package de.hochschuleTrier.fmv.filter.featureDiagram;

import java.util.Iterator;

import prefuse.action.Action;
import prefuse.util.PrefuseLib;
import prefuse.visual.EdgeItem;
import prefuse.visual.NodeItem;
import prefuse.visual.VisualItem;
import de.hochschuleTrier.fmv.model.interfaces.featureDiagram.IFeatureDiagramConstraintEdge;
import de.hochschuleTrier.fmv.model.interfaces.featureDiagram.IFeatureDiagramModel;

/**
 * Filter is used when a single feature in the feature tree should be inspected.
 * All CTCs belonging to the currently focussed feature are displayed.
 * 
 */
public class FeatureDiagramInspectNodeTreeFilter extends Action {

	private final String group;
	private final IFeatureDiagramModel model;

	public FeatureDiagramInspectNodeTreeFilter(final String group, final IFeatureDiagramModel model) {
		this.group = group;
		this.model = model;
	}

	@Override
	public void run(final double frac) {
		if (this.model.isInInspectingNodeMode()) {
			this.doFilter();
		}
	}

	private void doFilter() {
		if (this.model.isInInspectingNodeMode()) {
			this.hideAllItems();
			this.showNeededItems();
		}
	}

	private void hideAllItems() {
		final Iterator items = this.m_vis.visibleItems(this.group);
		while (items.hasNext()) {
			final VisualItem item = (VisualItem) items.next();
			PrefuseLib.updateVisible(item, false);
			item.setExpanded(false);
		}
	}

	private void showNeededItems() {
		this.setVisible(this.model.getInspectConstraintNode());
		for (final IFeatureDiagramConstraintEdge edge : this.model.getInspectConstraintNodeEdges()) {
			this.setVisible(edge.getSourceNode());
			this.setVisible(edge.getTargetNode());
		}
	}

	private void setVisible(final NodeItem node) {
		PrefuseLib.updateVisible(node, true);

		this.expand(node);
		this.setParentsVisible(node);
	}

	private void setParentsVisible(final NodeItem node) {
		NodeItem parent = (NodeItem) node.getParent();
		NodeItem currentNode = node;
		while (parent != null) {
			PrefuseLib.updateVisible(parent, true);
			PrefuseLib.updateVisible((EdgeItem) currentNode.getParentEdge(), true);
			parent.setExpanded(true);
			this.expand(parent);

			currentNode = parent;
			parent = (NodeItem) currentNode.getParent();
		}
	}

	private void expand(final NodeItem node) {
		node.setExpanded(true);
		final Iterator<NodeItem> children = node.children();
		while (children.hasNext()) {
			final NodeItem child = children.next();
			PrefuseLib.updateVisible(child, true);
			PrefuseLib.updateVisible((EdgeItem) child.getParentEdge(), true);
		}
	}

}
