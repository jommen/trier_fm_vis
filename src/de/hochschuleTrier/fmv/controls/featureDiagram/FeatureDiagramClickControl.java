package de.hochschuleTrier.fmv.controls.featureDiagram;

import java.awt.event.MouseEvent;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import prefuse.controls.Control;
import prefuse.controls.ControlAdapter;
import prefuse.visual.NodeItem;
import prefuse.visual.VisualItem;
import prefuse.visual.tuple.TableNodeItem;
import de.hochschuleTrier.fmv.exceptions.NodeNotFoundException;
import de.hochschuleTrier.fmv.model.impl.ApplicationModel;
import de.hochschuleTrier.fmv.model.interfaces.featureDiagram.IFeatureDiagramModel;

public class FeatureDiagramClickControl extends ControlAdapter {

	/**
	 * @see Control#itemClicked(VisualItem, MouseEvent)
	 */
	@Override
	public void mouseClicked(final MouseEvent e) {
		if (e.getClickCount() == 2) {
			try {
				ApplicationModel.getInstance().getCurrentFeatureTreeModel().setInspectConstraintNodes(null);
			}
			catch (final NodeNotFoundException e1) {
				JOptionPane.showMessageDialog(null, "Inspect mode failed", "Error", JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	/**
	 * @see prefuse.controls.Control#itemClicked(prefuse.visual.VisualItem, java.awt.event.MouseEvent)
	 */
	@Override
	public void itemClicked(final VisualItem item, final MouseEvent e) {
		if (item instanceof TableNodeItem && SwingUtilities.isLeftMouseButton(e)) {
			if (e.getClickCount() >= 2) {
				this.performDoubleClick(item);
			}
			else {
				this.performSingleClick(item);
			}
		}
	}

	private void performSingleClick(final VisualItem item) {
		final TableNodeItem node = (TableNodeItem) item;
		final ApplicationModel model = ApplicationModel.getInstance();
		model.setSelectedNode(node);
	}

	private void performDoubleClick(final VisualItem item) {
		if (item instanceof NodeItem) {
			final IFeatureDiagramModel model = ApplicationModel.getInstance().getCurrentFeatureTreeModel();
			try {
				model.setInspectConstraintNodes((NodeItem) item);
			}
			catch (final NodeNotFoundException e) {
				JOptionPane.showMessageDialog(null, "Inspect mode failed", "Error", JOptionPane.ERROR_MESSAGE);
			}
		}
	}
}