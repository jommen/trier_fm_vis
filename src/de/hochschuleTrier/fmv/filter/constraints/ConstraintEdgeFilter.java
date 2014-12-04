package de.hochschuleTrier.fmv.filter.constraints;

import java.util.Iterator;

import prefuse.action.GroupAction;
import prefuse.util.PrefuseLib;
import prefuse.visual.tuple.TableEdgeItem;
import prefuse.visual.tuple.TableNodeItem;
import de.hochschuleTrier.fmv.util.EdgeLib;

public class ConstraintEdgeFilter extends GroupAction {

	public ConstraintEdgeFilter(final String edges) {
		super(edges);
	}

	@Override
	public void run(final double frac) {
		final Iterator edges = this.m_vis.items(this.m_group);

		while (edges.hasNext()) {
			final TableEdgeItem edge = (TableEdgeItem) edges.next();

			final TableNodeItem sourceNode = (TableNodeItem) edge.getSourceNode();
			final TableNodeItem targetNode = (TableNodeItem) edge.getTargetNode();
			if (!EdgeLib.isConstraintEdge(edge) || !sourceNode.isVisible() || !targetNode.isVisible()) {
				PrefuseLib.updateVisible(edge, false);
			}
			else {
				PrefuseLib.updateVisible(edge, true);
			}
		}
	}

}
