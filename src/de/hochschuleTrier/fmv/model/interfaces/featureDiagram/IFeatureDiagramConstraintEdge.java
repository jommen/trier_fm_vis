package de.hochschuleTrier.fmv.model.interfaces.featureDiagram;

import prefuse.visual.NodeItem;

public interface IFeatureDiagramConstraintEdge {
	public NodeItem getSourceNode();

	public NodeItem getTargetNode();

	public String getEdgeType();
}
