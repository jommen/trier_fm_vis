package de.hochschuleTrier.fmv.controls.overview;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.SwingUtilities;

import prefuse.Visualization;
import prefuse.data.Node;
import prefuse.visual.NodeItem;
import de.hochschuleTrier.fmv.exceptions.NodeNotFoundException;
import de.hochschuleTrier.fmv.model.impl.ApplicationModel;
import de.hochschuleTrier.fmv.util.GraphLib;
import de.hochschuleTrier.fmv.util.NodeLib;
import de.hochschuleTrier.fmv.view.TreePanel;

public class TreePanelMouseListener extends MouseAdapter {

	private final TreePanel treeOverviewPanel;

	public TreePanelMouseListener(final TreePanel treeOverviewPanel) {
		this.treeOverviewPanel = treeOverviewPanel;
	}

	@Override
	public void mousePressed(final MouseEvent e) {
		final int selRow = this.treeOverviewPanel.getRowForLocation(e.getX(), e.getY());
		if (selRow != -1) {
			if (SwingUtilities.isLeftMouseButton(e) && e.getClickCount() == 2) {
				this.treeOverviewPanel.setSelectionRow(selRow);
				this.focusOnSelectedFeature();
			}
		}
	}

	private void focusOnSelectedFeature() {
		try {
			final ApplicationModel applicationModel = ApplicationModel.getInstance();
			final Visualization vis = applicationModel.getCurrentFeatureTreeModel().getDisplay().getVisualization();
			final String feature = NodeLib.getName((Node) TreePanelMouseListener.this.treeOverviewPanel.getSelectionModel().getSelectionPath().getLastPathComponent());
			final NodeItem node = GraphLib.findVisualNode(vis, feature);
			applicationModel.setSelectedNode(node);
		}
		catch (final NodeNotFoundException e1) {
			// nothing to do if the feature cannot be found
		}
	}
}
