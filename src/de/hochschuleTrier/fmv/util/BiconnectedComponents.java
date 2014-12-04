package de.hochschuleTrier.fmv.util;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import prefuse.Visualization;
import prefuse.data.Graph;
import prefuse.data.Node;
import prefuse.visual.NodeItem;
import de.hochschuleTrier.fmv.exceptions.NodeNotFoundException;

/**
 * Provides a depth first search {@link Iterator} which searches for a list of {@link NodeItem}s in a given {@link Visualization} object
 * 
 * Running time: O(|V| + |E|) where |V| is the number of vertices and |E| is the number of edges
 * 
 * @see "Depth first search and linear graph algorithms by R. E. Tarjan (1972), SIAM J. Comp." (Link: http://ieeexplore.ieee.org/stamp/stamp.jsp?tp=&arnumber=4569669)
 * 
 * @author JuergenOmmen
 * 
 */
public class BiconnectedComponents {

	private final Deque<SearchBiconnectedComponentEdge> stack = new ArrayDeque<>();
	private Graph graph;
	private Set<Set<Node>> biconnectedComponents;

	private int counter = 0;
	private Map<Node, SearchBiconnectedComponentNode> biCompNodes;

	public BiconnectedComponents() {
		// do nothing, requires init call
	}

	public BiconnectedComponents(final Node startNode) {
		this.init(startNode);
	}

	public BiconnectedComponents(final Graph graph, final String startNodeName) throws NodeNotFoundException {
		this.init(graph, startNodeName);
	}

	public void init(final Graph graph, final String startNodeName) throws NodeNotFoundException {
		final Node startNode = GraphLib.findNode(graph, startNodeName);
		this.init(startNode);
	}

	public void init(final Node startNode) {
		this.graph = startNode.getGraph();
		this.biconnectedComponents = new HashSet<>();
		this.biCompNodes = new HashMap<>();
		this.stack.clear();

		// Init custom data structure
		final Iterator<Node> nodes = this.graph.nodes();
		while (nodes.hasNext()) {
			final Node node = nodes.next();
			this.biCompNodes.put(node, new SearchBiconnectedComponentNode(node));
		}

		// Do search
		this.findBiconnectedComponents(startNode);

		//		System.out.println("-------------------------------");
		//		System.out.println("Biconnected Components:");
		//		for (final Set<Node> component : this.biconnectedComponents) {
		//			for (final Node node : component) {
		//				System.out.print(NodeLib.getName(node) + ",");
		//			}
		//			System.out.println();
		//		}
		//		System.out.println("-------------------------------");
	}

	/**
	 * Find biconnected components
	 * 
	 * @see "Depth first search and linear graph algorithms by R. E. Tarjan (1972), SIAM J. Comp." (Link: http://ieeexplore.ieee.org/stamp/stamp.jsp?tp=&arnumber=4569669)
	 * @see http://www.ics.uci.edu/~dan/class/161/notes/8/Bicomps.html - Good Implementation description
	 * @param current The currently selected node, equals "v" in the algorithm proposed by Tarjan
	 */
	private void findBiconnectedComponents(final Node current) {
		final SearchBiconnectedComponentNode currentNode = this.biCompNodes.get(current);
		currentNode.setNumber(this.counter);
		currentNode.setLow(this.counter);
		this.counter++;
		final Iterator<Node> children = current.neighbors();
		while (children.hasNext()) {
			final Node child = children.next();
			// if this is not a constraint edge --> select the next adjacent node
			if (!NodeLib.existsConstraintEdgeBetween(current, child)) {
				continue;
			}
			final SearchBiconnectedComponentNode childNode = this.biCompNodes.get(child);
			final SearchBiconnectedComponentEdge constraintEdgeBetweenCurrentAndChild = new SearchBiconnectedComponentEdge(currentNode, childNode);

			// if constraintEdgeBetweenCurrentAndChild is not yet on the stack
			// (v,w) is already on the stack if and only if (v,w) was a backedge w to v, for w descendant of v.
			// This can be expressed by:    
			// FATHER[v] = w  or (IS_NUMBERED[w] and NUMBER[v] < NUMBER[w]
			if (!((currentNode.hasFather() && NodeLib.isEqual(child, currentNode.getFather().getNode())) || (childNode.isNumbered() && currentNode.getNumber() < childNode.getNumber()))) {
				this.stack.push(new SearchBiconnectedComponentEdge(currentNode, childNode));
			}
			if (!childNode.isNumbered()) {
				childNode.setFather(currentNode);
				this.findBiconnectedComponents(child);

				// if LOW(childNode) >= NUMBER(currentNode) --> currentNode is an articulation point
				// and we can generate the biconnected component from the stack
				if (childNode.getLow() >= currentNode.getNumber()) {
					final Set<Node> bicomponent = new HashSet<>();
					SearchBiconnectedComponentEdge edge;
					do {
						edge = this.stack.pop();
						bicomponent.add(edge.getSource().getNode());
						bicomponent.add(edge.getTarget().getNode());
					} while (!edge.equals(constraintEdgeBetweenCurrentAndChild));
					this.biconnectedComponents.add(bicomponent);
				}
				currentNode.setLow(Math.min(currentNode.getLow(), childNode.getLow()));
			}
			else if (currentNode.getFather() != null && !NodeLib.isEqual(child, currentNode.getFather().getNode())) {
				currentNode.setLow(Math.min(currentNode.getLow(), childNode.getNumber()));
			}
		}
	}

	public Set<Set<Node>> get() {
		return this.biconnectedComponents;
	}

	private class SearchBiconnectedComponentEdge {
		private final SearchBiconnectedComponentNode source;
		private final SearchBiconnectedComponentNode target;

		public SearchBiconnectedComponentEdge(final SearchBiconnectedComponentNode source, final SearchBiconnectedComponentNode target) {
			this.source = source;
			this.target = target;
		}

		public SearchBiconnectedComponentNode getTarget() {
			return this.target;
		}

		public SearchBiconnectedComponentNode getSource() {
			return this.source;
		}

		@Override
		public String toString() {
			return NodeLib.getName(this.source.getNode()) + " --> " + NodeLib.getName(this.target.getNode());
		}

		@Override
		public boolean equals(final Object obj) {
			if (obj == null) {
				return false;
			}
			if (obj == this) {
				return true;
			}
			if (obj.getClass() == this.getClass()) {
				final SearchBiconnectedComponentEdge edge = (SearchBiconnectedComponentEdge) obj;
				if (NodeLib.isEqual(this.source.getNode(), edge.source.getNode()) && NodeLib.isEqual(this.target.getNode(), edge.target.getNode())) {
					return true;
				}
			}
			return false;
		}

		@Override
		public int hashCode() {
			int hash = 1;
			hash = hash * 17 + this.source.hashCode();
			hash = hash * 31 + this.target.hashCode();
			return hash;
		}
	}

	private class SearchBiconnectedComponentNode {
		private final Node node;
		private SearchBiconnectedComponentNode father;
		private int low;
		private int number;

		public SearchBiconnectedComponentNode(final Node node) {
			this.node = node;
			this.setLow(Integer.MIN_VALUE);
			this.setNumber(Integer.MIN_VALUE);
		}

		public int getLow() {
			return this.low;
		}

		public void setLow(final int low) {
			this.low = low;
		}

		public Node getNode() {
			return this.node;
		}

		public int getNumber() {
			return this.number;
		}

		public void setNumber(final int number) {
			this.number = number;
		}

		public boolean isNumbered() {
			return this.number > Integer.MIN_VALUE;
		}

		public SearchBiconnectedComponentNode getFather() {
			return this.father;
		}

		public boolean hasFather() {
			return this.father != null;
		}

		public void setFather(final SearchBiconnectedComponentNode father) {
			this.father = father;
		}

		@Override
		public String toString() {
			return NodeLib.getName(this.node) + ", Low: " + this.low + ", Number: " + this.number;
		}

	}

}
