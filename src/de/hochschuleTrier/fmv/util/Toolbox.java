package de.hochschuleTrier.fmv.util;

import java.util.LinkedList;

import prefuse.data.Node;
import prefuse.visual.NodeItem;

public class Toolbox {

	public static LinkedList<NodeItem> getExpandNodes(final Node root) {
		final LinkedList<NodeItem> allExpandNodes = new LinkedList<NodeItem>();

		final LinkedList<NodeItem> worklist = Toolbox.getBreadthFirstIterator((NodeItem) root);

		NodeItem testnode;

		final int length = worklist.size();

		for (int i = 0; i < length; i++) {

			testnode = worklist.get(i);

			if (!testnode.isVisible()) {

				final int depthOfExpandeNodes = testnode.getParent().getDepth();
				NodeItem rel; //

				while (!worklist.isEmpty() && worklist.getLast().getDepth() >= depthOfExpandeNodes) {
					rel = worklist.removeLast();
					if (rel.getDepth() == depthOfExpandeNodes) {
						allExpandNodes.add(rel);

					}
				}
				return allExpandNodes;
			}

		}
		return allExpandNodes;
	}

	public static LinkedList<NodeItem> getCollapseNodes(final Node root) {

		final LinkedList<NodeItem> allCollapseNodes = new LinkedList<NodeItem>();

		final LinkedList<NodeItem> worklist = Toolbox.getBreadthFirstIterator((NodeItem) root);

		NodeItem testnode;

		final int length = worklist.size();

		for (int i = length - 1; i >= 0; i--) {

			testnode = worklist.get(i);

			if (testnode.isVisible() && !testnode.equals(root)) {
				/*
				 * unterster sichbarer Knoten gefunden: Eltern + Onkel/Tanten
				 * ACHTUNG: Auch Cousinen und Großcousinen etc.... ... also alle
				 * Knoten auf den gleichen Level!!!! müssen eingeklappt werden
				 */
				final int depthOfCollapseNodes = testnode.getParent().getDepth();
				NodeItem rel;

				allCollapseNodes.add((NodeItem) testnode.getParent());

				while (!worklist.isEmpty() && worklist.getLast().getDepth() >= depthOfCollapseNodes) {
					rel = worklist.removeLast();
					if (rel.getDepth() == depthOfCollapseNodes) {
						allCollapseNodes.add(rel);
					}
				}
				return allCollapseNodes;
			}

		}
		return allCollapseNodes;

	}

	public static LinkedList<NodeItem> getBreadthFirstIterator(final NodeItem root) {
		final LinkedList<NodeItem> worklist = new LinkedList<NodeItem>();
		final LinkedList<NodeItem> iteratorlist = new LinkedList<NodeItem>();

		worklist.add(root);
		iteratorlist.add(root);

		NodeItem currentNode;

		while (!worklist.isEmpty()) {

			currentNode = worklist.pop();
			for (int i = 0; i < currentNode.getChildCount(); i++) {
				worklist.add((NodeItem) currentNode.getChild(i));
				iteratorlist.add((NodeItem) currentNode.getChild(i));
			}
		}

		return iteratorlist;
	}

	public static int getMaxDepth(final NodeItem root) {

		final LinkedList<NodeItem> iterator = Toolbox.getBreadthFirstIterator(root);
		return iterator.getLast().getDepth();
	}

}