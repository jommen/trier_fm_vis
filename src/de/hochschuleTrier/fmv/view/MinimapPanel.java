package de.hochschuleTrier.fmv.view;

import java.awt.Color;

import javax.swing.JPanel;

import prefuse.Display;
import prefuse.visual.NodeItem;
import de.hochschuleTrier.fmv.model.impl.ApplicationModel;
import de.hochschuleTrier.fmv.model.interfaces.featureDiagram.IFeatureDiagramModel;
import de.hochschuleTrier.fmv.model.interfaces.listener.IApplicationModelListener;
import de.hochschuleTrier.fmv.model.interfaces.listener.IFeatureModelClosedListener;
import de.hochschuleTrier.fmv.view.display.Overview;

public class MinimapPanel extends JPanel implements IApplicationModelListener, IFeatureModelClosedListener {

	private Overview overview;

	public MinimapPanel() {
		ApplicationModel.getInstance().addListener(this);
		ApplicationModel.getInstance().addFeatureModelClosedListener(this);
	}

	@Override
	public void currentFeatureTreeModelHasChanged(final IFeatureDiagramModel currentFeatureTreeModel) {

	}

	@Override
	public void activeDisplayChanged(final Display display) {
		this.removeAll();
		this.overview = new Overview(display);
		this.overview.setSize((int) (this.getSize().getWidth() - 20), (int) (this.getSize().getHeight() - 20));
		this.overview.damageReport();
		this.add(this.overview);
		this.invalidate();
		this.repaint();
	}

	@Override
	public void nodeSelected(final NodeItem oldNode, final NodeItem newNode) {

	}

	@Override
	public void nodePainted(final NodeItem node, final Color color) {

	}

	@Override
	public void featureModelClosed() {
		this.removeAll();
	}

}
