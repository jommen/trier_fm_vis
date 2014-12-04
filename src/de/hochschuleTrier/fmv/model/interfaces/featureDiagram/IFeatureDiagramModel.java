package de.hochschuleTrier.fmv.model.interfaces.featureDiagram;

import java.util.List;

import prefuse.data.Tree;
import prefuse.visual.NodeItem;
import de.hochschuleTrier.fmv.exceptions.NodeNotFoundException;
import de.hochschuleTrier.fmv.model.interfaces.listener.IFeatureDiagramModelListener;
import de.hochschuleTrier.fmv.view.display.FeatureDiagramDisplay;

public interface IFeatureDiagramModel {
	public void setTree(final Tree tree);

	public Tree getTree();

	public void addTreeListener(final IFeatureDiagramModelListener l);

	public void removeTreeListener(final IFeatureDiagramModelListener l);

	public FeatureDiagramDisplay getDisplay();

	public void setDisplay(final FeatureDiagramDisplay display);

	public boolean isInInspectingNodeMode();

	public NodeItem getInspectConstraintNode();

	public void setInspectConstraintNodes(final NodeItem inspectConstraintNode) throws NodeNotFoundException;

	public List<IFeatureDiagramConstraintEdge> getInspectConstraintNodeEdges();
}
