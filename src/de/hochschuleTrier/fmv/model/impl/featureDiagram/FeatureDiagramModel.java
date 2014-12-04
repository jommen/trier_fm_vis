package de.hochschuleTrier.fmv.model.impl.featureDiagram;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import prefuse.data.Edge;
import prefuse.data.Graph;
import prefuse.data.Node;
import prefuse.data.Tree;
import prefuse.data.io.TreeMLReader;
import prefuse.data.tuple.TableNode;
import prefuse.visual.NodeItem;
import de.hochschuleTrier.fmv.exceptions.NodeNotFoundException;
import de.hochschuleTrier.fmv.exceptions.ReadMLDataFileFailedExceptions;
import de.hochschuleTrier.fmv.model.impl.ApplicationModel;
import de.hochschuleTrier.fmv.model.interfaces.featureDiagram.IFeatureDiagramConstraintEdge;
import de.hochschuleTrier.fmv.model.interfaces.featureDiagram.IFeatureDiagramModel;
import de.hochschuleTrier.fmv.model.interfaces.listener.IFeatureDiagramModelListener;
import de.hochschuleTrier.fmv.util.EdgeLib;
import de.hochschuleTrier.fmv.util.GraphLib;
import de.hochschuleTrier.fmv.util.NodeLib;
import de.hochschuleTrier.fmv.util.TreeDisplayEnum;
import de.hochschuleTrier.fmv.view.display.FeatureDiagramDisplay;

public class FeatureDiagramModel implements IFeatureDiagramModel {

	private Tree tree;
	private FeatureDiagramDisplay display;
	private final List<IFeatureDiagramModelListener> listener;

	private NodeItem inspectConstraintNode;
	private final List<IFeatureDiagramConstraintEdge> inspectConstraintNodeEdges = new ArrayList<IFeatureDiagramConstraintEdge>();

	public FeatureDiagramModel() {
		this((Tree) null);
	}

	public FeatureDiagramModel(final Tree tree) {
		this.tree = tree;
		this.listener = new ArrayList<IFeatureDiagramModelListener>();
	}

	public FeatureDiagramModel(final String treeMLdatafile) throws ReadMLDataFileFailedExceptions {
		this((Tree) null);
		try {
			this.tree = (Tree) new TreeMLReader().readGraph(treeMLdatafile);
			this.tree.addColumns(new FeatureDiagramSchema());
		}
		catch (final Exception e) {
			throw new ReadMLDataFileFailedExceptions(e);
		}
	}

	@Override
	public void setTree(final Tree tree) {
		this.tree = tree;
		this.notifyTreeListener();
	}

	@Override
	public Tree getTree() {
		return this.tree;
	}

	@Override
	public void addTreeListener(final IFeatureDiagramModelListener l) {
		this.listener.add(l);
	}

	@Override
	public void removeTreeListener(final IFeatureDiagramModelListener l) {
		this.listener.remove(l);
	}

	private void notifyTreeListener() {
		for (final IFeatureDiagramModelListener l : this.listener) {
			l.treeHasChanged(this.tree);
		}
	}

	@Override
	public FeatureDiagramDisplay getDisplay() {
		return this.display;
	}

	@Override
	public void setDisplay(final FeatureDiagramDisplay display) {
		this.display = display;
	}

	@Override
	public boolean isInInspectingNodeMode() {
		return this.inspectConstraintNode != null;
	}

	@Override
	public NodeItem getInspectConstraintNode() {
		return this.inspectConstraintNode;
	}

	@Override
	public void setInspectConstraintNodes(final NodeItem inspectConstraintNode) throws NodeNotFoundException {
		System.out.println(String.valueOf(null == null));
		if (this.inspectConstraintNode != inspectConstraintNode) {
			this.inspectConstraintNode = inspectConstraintNode;
			this.updateConstraintEdges();
			this.display.getVisualization().run("filter");
		}
		else {
			this.inspectConstraintNode = null;
		}
	}

	private void updateConstraintEdges() throws NodeNotFoundException {
		this.inspectConstraintNodeEdges.clear();
		if (this.isInInspectingNodeMode()) {
			final Graph constraintGraph = ApplicationModel.getInstance().getConstraintModel().getConstraintGraph();
			final Node node = GraphLib.findNode(constraintGraph, NodeLib.getName(this.inspectConstraintNode));
			final Iterator<TableNode> neighbors = node.neighbors();
			while (neighbors.hasNext()) {
				final TableNode neighbor = neighbors.next();
				Edge edge = constraintGraph.getEdge(node, neighbor);
				if (edge == null) {
					edge = constraintGraph.getEdge(neighbor, node);
				}
				if (edge != null && EdgeLib.isConstraintEdge(edge)) {
					final NodeItem sourceNode = this.findNode(edge.getSourceNode());
					final NodeItem targetNode = this.findNode(edge.getTargetNode());
					final IFeatureDiagramConstraintEdge IFeatureTreeConstraintEdge = new FeatureDiagramConstraintEdge(sourceNode, targetNode, EdgeLib.getConstraintType(edge));
					this.inspectConstraintNodeEdges.add(IFeatureTreeConstraintEdge);
				}
			}
		}
	}

	private NodeItem findNode(final Node nodeToFind) {
		final Iterator<NodeItem> nodes = this.display.getVisualization().items(TreeDisplayEnum.NODES.toString());
		while (nodes.hasNext()) {
			final NodeItem node = nodes.next();
			if (node.getString("name").equals(nodeToFind.getString("name"))) {
				return node;
			}
		}
		return null;
	}

	@Override
	public List<IFeatureDiagramConstraintEdge> getInspectConstraintNodeEdges() {
		return this.inspectConstraintNodeEdges;
	}

}
