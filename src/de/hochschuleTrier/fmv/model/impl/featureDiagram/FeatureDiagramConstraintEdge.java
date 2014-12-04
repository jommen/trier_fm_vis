package de.hochschuleTrier.fmv.model.impl.featureDiagram;

import prefuse.visual.NodeItem;
import de.hochschuleTrier.fmv.model.interfaces.featureDiagram.IFeatureDiagramConstraintEdge;

public class FeatureDiagramConstraintEdge implements IFeatureDiagramConstraintEdge {
	private final NodeItem sourceNode;
	private final NodeItem targetNode;
	private final String edgeType;

	public FeatureDiagramConstraintEdge(final NodeItem sourceNode, final NodeItem targetNode, final String edgeType) {
		this.sourceNode = sourceNode;
		this.targetNode = targetNode;
		this.edgeType = edgeType;
	}

	@Override
	public NodeItem getSourceNode() {
		return this.sourceNode;
	}

	@Override
	public NodeItem getTargetNode() {
		return this.targetNode;
	}

	@Override
	public String getEdgeType() {
		return this.edgeType;
	}

}
