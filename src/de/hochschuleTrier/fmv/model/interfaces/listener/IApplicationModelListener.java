package de.hochschuleTrier.fmv.model.interfaces.listener;

import java.awt.Color;

import prefuse.Display;
import prefuse.visual.NodeItem;
import de.hochschuleTrier.fmv.model.interfaces.featureDiagram.IFeatureDiagramModel;

public interface IApplicationModelListener {

	public void currentFeatureTreeModelHasChanged(IFeatureDiagramModel currentFeatureTreeModel);

	public void nodeSelected(NodeItem oldNode, NodeItem newNode);

	public void nodePainted(NodeItem node, Color color);

	public void activeDisplayChanged(Display display);

}
