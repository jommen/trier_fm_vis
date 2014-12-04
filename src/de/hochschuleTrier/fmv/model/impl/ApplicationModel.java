package de.hochschuleTrier.fmv.model.impl;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import prefuse.Display;
import prefuse.Visualization;
import prefuse.data.Node;
import prefuse.data.Tree;
import prefuse.data.tuple.TableNode;
import prefuse.util.PrefuseLib;
import prefuse.visual.EdgeItem;
import prefuse.visual.NodeItem;
import de.hochschuleTrier.fmv.model.impl.featureDiagram.FeatureDiagramModel;
import de.hochschuleTrier.fmv.model.interfaces.complexConstraints.IComplexConstraintModel;
import de.hochschuleTrier.fmv.model.interfaces.complexConstraints.IComplexConstraints;
import de.hochschuleTrier.fmv.model.interfaces.constraints.IConstraintModel;
import de.hochschuleTrier.fmv.model.interfaces.featureDiagram.IFeatureDiagramModel;
import de.hochschuleTrier.fmv.model.interfaces.listener.IApplicationModelListener;
import de.hochschuleTrier.fmv.model.interfaces.listener.IFeatureModelClosedListener;
import de.hochschuleTrier.fmv.model.interfaces.listener.INewFeatureModelListener;
import de.hochschuleTrier.fmv.util.NodeLib;
import de.hochschuleTrier.fmv.util.Toolbox;
import de.hochschuleTrier.fmv.util.TreeLib;

public class ApplicationModel {

	// ////////////////////////////////////////////////////////
	// Attributes
	// ////////////////////////////////////////////////////////

	private static ApplicationModel instance;

	private final List<IApplicationModelListener> listener;
	private final List<INewFeatureModelListener> newFeatureModelListener;
	private final List<IFeatureModelClosedListener> featureModelClosedListener;

	private IFeatureDiagramModel currentFeatureTreeModel;
	private IConstraintModel constraintModel;
	private IComplexConstraints complexConstraints;

	private Display activeDisplay;

	private NodeItem currentSelectedNode;
	private final List<Node> pathToSelectedNode;
	private final Deque<NodeItem> expandedNodes;
	private final Deque<NodeItem> collapsedNodes;

	// ////////////////////////////////////////////////////////
	// Methods
	// ////////////////////////////////////////////////////////

	public synchronized static ApplicationModel getInstance() {
		if (ApplicationModel.instance == null) {
			ApplicationModel.instance = new ApplicationModel();
		}
		return ApplicationModel.instance;
	}

	private ApplicationModel() {
		this.listener = new ArrayList<IApplicationModelListener>();
		this.newFeatureModelListener = new ArrayList<>();
		this.featureModelClosedListener = new ArrayList<>();
		this.collapsedNodes = new LinkedList<NodeItem>();
		this.expandedNodes = new LinkedList<NodeItem>();
		this.pathToSelectedNode = new ArrayList<Node>();
	}

	// ////////////////////////////////////////////////////////
	// Getter and Setter
	// ////////////////////////////////////////////////////////

	public IComplexConstraints getComplexConstraints() {
		return this.complexConstraints;
	}

	public void setComplexConstraints(final IComplexConstraints complexConstraints) {
		this.complexConstraints = complexConstraints;
	}

	public IConstraintModel getConstraintModel() {
		return this.constraintModel;
	}

	public void setConstraintModel(final IConstraintModel constraintModel) {
		this.constraintModel = constraintModel;
	}

	public void setActiveDisplay(final Display display) {
		this.activeDisplay = display;
		for (final IApplicationModelListener l : this.listener) {
			l.activeDisplayChanged(this.activeDisplay);
		}
	}

	public Display getActiveDisplay() {
		return this.activeDisplay;
	}

	public IFeatureDiagramModel getCurrentFeatureTreeModel() {
		return this.currentFeatureTreeModel;
	}

	public synchronized void setCurrentFeatureTreeModel(final IFeatureDiagramModel currentFeatureTreeModel) {
		if (this.currentFeatureTreeModel != currentFeatureTreeModel) {
			this.currentFeatureTreeModel = currentFeatureTreeModel;
			this.notifyCurrentTreeModelChangedListener();
		}
	}

	public synchronized List<Node> getPathToSelectedNode() {
		return Collections.unmodifiableList(this.pathToSelectedNode);
	}

	public synchronized Deque<NodeItem> getExpandedNodes() {
		return this.expandedNodes;
	}

	public synchronized Deque<NodeItem> getCollapsedNodes() {
		return this.collapsedNodes;
	}

	public synchronized void setSelectedNode(final NodeItem node) {
		final NodeItem oldNode = this.currentSelectedNode;
		this.currentSelectedNode = node;
		this.pathToSelectedNode.clear();
		this.pathToSelectedNode.addAll(TreeLib.getRootNodeTrack(this.currentSelectedNode));
		this.updateCollapsedAndExpandedNodes();
		this.notifySelectedNodeChangedListener(oldNode, node);
		this.updateConstraintView();
	}

	public synchronized void updateCollapsedAndExpandedNodes() {
		this.expandedNodes.clear();
		this.collapsedNodes.clear();
		this.expandedNodes.addAll(Toolbox.getExpandNodes(this.currentSelectedNode));
		this.collapsedNodes.addAll(Toolbox.getCollapseNodes(this.currentSelectedNode));
	}

	public synchronized NodeItem getSelectedNode() {
		return this.currentSelectedNode;
	}

	public boolean isAnyNodeSelected() {
		return this.currentSelectedNode != null;
	}

	// ////////////////////////////////////////////////////////
	// Listener
	// ////////////////////////////////////////////////////////

	public synchronized void addListener(final IApplicationModelListener l) {
		this.listener.add(l);
	}

	public synchronized void removeListener(final IApplicationModelListener l) {
		this.listener.remove(l);
	}

	public synchronized void addNewFeatureModelListener(final INewFeatureModelListener l) {
		this.newFeatureModelListener.add(l);
	}

	public synchronized void removeNewFeatureModelListener(final INewFeatureModelListener l) {
		this.newFeatureModelListener.remove(l);
	}

	public synchronized void addFeatureModelClosedListener(final IFeatureModelClosedListener l) {
		this.featureModelClosedListener.add(l);
	}

	public synchronized void removeFeatureModelClosedListener(final IFeatureModelClosedListener l) {
		this.featureModelClosedListener.remove(l);
	}

	// ////////////////////////////////////////////////////////
	// Notifies
	// ////////////////////////////////////////////////////////

	private void notifyCurrentTreeModelChangedListener() {
		for (final IApplicationModelListener l : this.listener) {
			l.currentFeatureTreeModelHasChanged(this.currentFeatureTreeModel);
		}
	}

	private void notifyNewTreeModelListener(final String featureModelName) {
		for (final INewFeatureModelListener l : this.newFeatureModelListener) {
			l.newFeatueTreeModelCreated(this.currentFeatureTreeModel, this.constraintModel, featureModelName);
		}
	}

	private void notifySelectedNodeChangedListener(final NodeItem oldNodeItem, final NodeItem newNodeItem) {
		for (final IApplicationModelListener l : this.listener) {
			l.nodeSelected(oldNodeItem, newNodeItem);
		}
	}

	private void notifyPaintSelectedNodeListener(final Color color) {
		final NodeItem node = this.currentSelectedNode;
		for (final IApplicationModelListener l : this.listener) {
			l.nodePainted(node, color);
		}
	}

	private void notifyNewSubtreeCreated() {
		for (final INewFeatureModelListener l : this.newFeatureModelListener) {
			l.newSubtreeCreated(this.currentFeatureTreeModel);
		}
	}

	public void notifyFeatureModelClosed() {
		for (final IFeatureModelClosedListener l : this.featureModelClosedListener) {
			l.featureModelClosed();
		}
	}

	// ////////////////////////////////////////////////////////
	// Business Methods
	// ////////////////////////////////////////////////////////

	public synchronized void expandTree() {
		NodeItem expandNode;

		if (this.expandedNodes != null) {
			while (!this.expandedNodes.isEmpty()) {
				expandNode = this.expandedNodes.pop();
				this.expandNode(expandNode);
			}
		}
		this.currentSelectedNode.getVisualization().run("filter");
		this.updateConstraintView();
	}

	private void expandNode(final NodeItem node) {
		node.setExpanded(true);
		final Iterator childnodes = node.children();
		final Iterator childedges = node.childEdges();

		while (childnodes.hasNext()) {
			final NodeItem childnode = (NodeItem) childnodes.next();
			final EdgeItem childedge = (EdgeItem) childedges.next();

			PrefuseLib.updateVisible(childnode, true);
			PrefuseLib.updateVisible(childedge, true);
		}
	}

	public synchronized void collapseTree() {
		NodeItem node;

		if (this.collapsedNodes != null) {
			while (!this.collapsedNodes.isEmpty()) {
				node = this.collapsedNodes.pop();
				this.collapseNode(node);
			}
		}
		this.currentSelectedNode.getVisualization().run("filter");
		this.updateConstraintView();
	}

	private void collapseNode(final NodeItem node) {
		node.setExpanded(false);
		final Iterator childnodes = node.children();
		final Iterator childedges = node.childEdges();

		while (childnodes.hasNext()) {
			final NodeItem childnode = (NodeItem) childnodes.next();
			final EdgeItem childedge = (EdgeItem) childedges.next();

			this.collapseNode(childnode);

			PrefuseLib.updateVisible(childnode, false);
			PrefuseLib.updateVisible(childedge, false);
		}
	}

	public synchronized void paintSelectedSubtree(final Color color) {
		final LinkedList<NodeItem> subtree = Toolbox.getBreadthFirstIterator(this.currentSelectedNode);
		NodeItem currentNode = null;

		while (!subtree.isEmpty()) {
			currentNode = subtree.pop();
			NodeLib.setColor((TableNode) currentNode.getSourceTuple(), color);
		}
		this.notifyPaintSelectedNodeListener(color);
	}

	public synchronized void extractSubTree() {
		if (this.currentSelectedNode != null) {
			final Tree currentTree = this.currentFeatureTreeModel.getTree();
			final Node selectedNodeCopy = currentTree.getNode(this.currentSelectedNode.getRow());
			final Tree subTree = TreeLib.createCopyOfSubtree(selectedNodeCopy);

			this.currentFeatureTreeModel = new FeatureDiagramModel(subTree);
			this.notifyNewSubtreeCreated();
		}
	}

	public void updateConstraintView() {
		final Visualization constraintVis = this.getConstraintModel().getDisplay().getVisualization();
		constraintVis.run("random");
		constraintVis.runAfter("random", "filter");
		if (!constraintVis.getAction("layout").isRunning()) {
			constraintVis.run("layout");
		}
	}

	public synchronized void openNewFeatureTreeModel(final IFeatureDiagramModel newFeatureTreeModel, final IConstraintModel newConstraintModel, final String featureModelName) {
		this.currentFeatureTreeModel = newFeatureTreeModel;
		this.constraintModel = newConstraintModel;
		this.notifyFeatureModelClosed();
		this.notifyNewTreeModelListener(featureModelName);
	}

	public synchronized void openComplexConstraintModel(final IComplexConstraintModel model) {
		for (final INewFeatureModelListener l : this.newFeatureModelListener) {
			l.newComplexConstraintView(model);
		}
	}

	public synchronized void paintSelectedNode(final Color color) {
		this.notifyPaintSelectedNodeListener(color);
	}

}
