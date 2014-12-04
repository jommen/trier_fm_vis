package de.hochschuleTrier.fmv.util;

import java.util.Iterator;

import prefuse.Visualization;
import prefuse.data.Graph;
import prefuse.data.Node;
import prefuse.visual.NodeItem;
import de.hochschuleTrier.fmv.exceptions.NodeNotFoundException;

public class GraphLib {
	private GraphLib() {

	}

	public static Node findNode(final Graph graph, final String name) throws NodeNotFoundException {
		final Iterator<Node> nodes = graph.nodes();
		while (nodes.hasNext()) {
			final Node node = nodes.next();
			if (NodeLib.getName(node).equals(name)) {
				return node;
			}
		}
		throw new NodeNotFoundException("Node not found");
	}

	public static NodeItem findVisualNode(final Visualization visualization, final String name) throws NodeNotFoundException {
		final Iterator<NodeItem> nodes = visualization.items(TreeDisplayEnum.NODES.toString());
		while (nodes.hasNext()) {
			final NodeItem node = nodes.next();
			if (NodeLib.getName(node).equals(name)) {
				return node;
			}
		}
		throw new NodeNotFoundException("Node not found");
	}
}
