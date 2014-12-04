package de.hochschuleTrier.fmv.controls;

import java.awt.event.MouseEvent;

import prefuse.Visualization;
import prefuse.controls.ControlAdapter;
import prefuse.visual.NodeItem;
import prefuse.visual.VisualItem;
import de.hochschuleTrier.fmv.exceptions.NodeNotFoundException;
import de.hochschuleTrier.fmv.model.impl.ApplicationModel;
import de.hochschuleTrier.fmv.util.GraphLib;
import de.hochschuleTrier.fmv.util.NodeLib;
import de.hochschuleTrier.fmv.view.display.ConstraintDisplay;

public class HighlightItemMouseover extends ControlAdapter {

	@Override
	public void itemEntered(final VisualItem item, final MouseEvent e) {
		if (e.getComponent() instanceof ConstraintDisplay && item instanceof NodeItem) {
			this.highlightInFDTreeview((NodeItem) item, true);
		}
	}

	@Override
	public void itemExited(final VisualItem item, final MouseEvent e) {
		if (e.getComponent() instanceof ConstraintDisplay && item instanceof NodeItem) {
			this.highlightInFDTreeview((NodeItem) item, false);
		}
	}

	private void highlightInFDTreeview(final NodeItem item, final boolean highlight) {
		try {
			final Visualization visualization = ApplicationModel.getInstance().getCurrentFeatureTreeModel().getDisplay().getVisualization();
			final NodeItem fmNode = GraphLib.findVisualNode(visualization, NodeLib.getName(item));
			fmNode.setHighlighted(highlight);
			visualization.run("fullPaint");
			visualization.run("animatePaint");
		}
		catch (final NodeNotFoundException e) {
			// nothing to do if there is no node
		}

	}

}
