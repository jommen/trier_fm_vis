package de.hochschuleTrier.fmv.model.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import javax.swing.event.TreeModelListener;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import prefuse.data.Node;
import prefuse.data.Tree;
import prefuse.visual.NodeItem;

public class TreeOverviewModel extends DefaultTreeModel {

	private final Tree tree;
	private final List<TreeModelListener> listener;

	public TreeOverviewModel(final Tree tree) {
		super(null);
		this.tree = tree;
		this.listener = new ArrayList<TreeModelListener>();
	}

	@Override
	public Object getRoot() {
		if (this.tree == null) {
			return null;
		}
		final Node root = this.tree.getRoot();
		return root;
	}

	@Override
	public Object getChild(final Object parent, final int index) {
		return ((Node) parent).getChild(index);
	}

	@Override
	public int getChildCount(final Object parent) {
		return ((Node) parent).getChildCount();
	}

	@Override
	public boolean isLeaf(final Object node) {
		return this.getChildCount(node) == 0;
	}

	@Override
	public void valueForPathChanged(final TreePath path, final Object newValue) {
		// nothing to do
	}

	@Override
	public int getIndexOfChild(final Object parent, final Object child) {
		return ((Node) parent).getChildIndex((Node) child);
	}

	@Override
	public void addTreeModelListener(final TreeModelListener l) {
		this.listener.add(l);
	}

	@Override
	public void removeTreeModelListener(final TreeModelListener l) {
		this.listener.add(l);
	}

	public TreePath getPathToRoot(NodeItem aNode) {
		// Walk along the path
		final LinkedList<Object> nodeItems = new LinkedList<Object>();
		nodeItems.add(aNode);
		while (aNode.getParent() != null) {
			aNode = (NodeItem) aNode.getParent();
			nodeItems.add(aNode.getVisualization().getSourceTuple(aNode));
		}

		// And reverse it for selection
		Collections.reverse(nodeItems);
		return new TreePath(nodeItems.toArray());
	}
}
