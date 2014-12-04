package de.hochschuleTrier.fmv.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import prefuse.data.Edge;
import prefuse.data.Graph;
import prefuse.data.Node;
import de.hochschuleTrier.fmv.exceptions.NodeNotFoundException;
import de.hochschuleTrier.fmv.model.impl.constraints.ConstraintEdgeSchema;

public class InspectingConstraintNodesIterator implements Iterator<Node> {

	private final Graph graph;
	private List<Node> nodes;
	private final List<Node> inspectNodes;
	private int index;

	public InspectingConstraintNodesIterator(final Graph graph, final List<String> inspectNodes) {
		this.graph = graph;
		this.inspectNodes = new ArrayList<>();
		for (final String nodeName : inspectNodes) {
			try {
				this.inspectNodes.add(GraphLib.findNode(graph, nodeName));
			}
			catch (final NodeNotFoundException e) {
				// nothing to do if there is no node
			}
		}
		this.init();
	}

	public InspectingConstraintNodesIterator(final List<Node> inspectNodes) {
		if (inspectNodes.size() <= 0) {
			throw new IllegalArgumentException("Number of inspecting nodes must be > 0");
		}
		this.inspectNodes = new ArrayList<>();
		Collections.copy(this.inspectNodes, inspectNodes);
		this.graph = this.inspectNodes.get(0).getGraph();
		this.init();
	}

	private void init() {
		this.nodes = new ArrayList<>();
		final List<Edge> tempEdges = new ArrayList<>();
		// Temporary connect all nodes
		for (int i = 0; i < this.inspectNodes.size(); i++) {
			for (int j = i + 1; j < this.inspectNodes.size(); j++) {
				final Node node1 = this.inspectNodes.get(i);
				final Node node2 = this.inspectNodes.get(j);
				if (!NodeLib.existsConstraintEdgeBetween(node1, node2)) {
					final Edge edge = this.graph.addEdge(node1, node2);
					edge.setString(ConstraintEdgeSchema.EDGE_TYPE, ConstraintEdgeSchema.EDGE_TYPE_CONSTRAINT);
					edge.setString(ConstraintEdgeSchema.CONSTRAINT_TYPE, ConstraintEdgeSchema.REQUIRES_CONSTRAINT);
					tempEdges.add(edge);
				}
			}
		}

		// Find biconnected components
		final BiconnectedComponents biconnectedComponents = new BiconnectedComponents(this.inspectNodes.get(0));
		final Set<Set<Node>> components = biconnectedComponents.get();
		for (final Set<Node> component : components) {
			if (component.containsAll(this.inspectNodes)) {
				this.nodes.addAll(component);
				break;
			}
		}

		// Remove temporary edge
		for (final Edge edge : tempEdges) {
			this.graph.removeEdge(edge);
		}
	}

	@Override
	public boolean hasNext() {
		return this.index < this.nodes.size();
	}

	@Override
	public Node next() {
		return this.nodes.get(this.index++);
	}

	@Override
	public void remove() {
		this.nodes.remove(this.index);
	}

	public void rewind() {
		this.index = 0;
	}

}
