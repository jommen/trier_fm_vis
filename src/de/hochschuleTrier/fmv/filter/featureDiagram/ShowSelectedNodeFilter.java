package de.hochschuleTrier.fmv.filter.featureDiagram;

import java.util.Iterator;

import prefuse.Visualization;
import prefuse.action.GroupAction;
import prefuse.data.Node;
import prefuse.data.Tuple;
import prefuse.data.tuple.TupleSet;
import prefuse.util.PrefuseLib;
import prefuse.visual.EdgeItem;
import prefuse.visual.NodeItem;
import prefuse.visual.VisualItem;
import de.hochschuleTrier.fmv.util.TreeDisplayEnum;

public class ShowSelectedNodeFilter extends GroupAction {

	@Override
	public void run(final double frac) {
		final TupleSet tupleSet = this.getVisualization().getFocusGroup(Visualization.FOCUS_ITEMS);
		final Iterator<Tuple> iter = tupleSet.tuples();
		while (iter.hasNext()) {
			final Node selectedNode = (Node) iter.next();
			final VisualItem selectedNodeItem = this.getVisualization().getVisualItem(TreeDisplayEnum.NODES.toString(), selectedNode);

			if (!selectedNodeItem.isVisible()) {
				PrefuseLib.updateVisible(selectedNodeItem, true);
				this.visitAncestors(selectedNode);
			}
		}
	}

	private void visitAncestors(final Node node) {
		Node parent = node.getParent();
		while (parent != null) {
			final NodeItem parentItem = (NodeItem) this.getVisualization().getVisualItem(TreeDisplayEnum.NODES.toString(), parent);
			if (parentItem.isExpanded()) {
				break;
			}
			this.visit(parentItem);
			parent = parent.getParent();
		}
	}

	private void visit(final NodeItem nodeItem) {
		PrefuseLib.updateVisible(nodeItem, true);
		nodeItem.setExpanded(true);

		this.visitDescendants(nodeItem);
	}

	private void visitDescendants(final NodeItem nodeItem) {
		final Iterator<NodeItem> children = nodeItem.children();
		while (children.hasNext()) {
			final NodeItem child = children.next();
			final EdgeItem edge = (EdgeItem) child.getParentEdge();
			PrefuseLib.updateVisible(child, true);
			PrefuseLib.updateVisible(edge, true);
		}
	}

}
