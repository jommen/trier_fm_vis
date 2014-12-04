package de.hochschuleTrier.fmv.model.impl.complexConstraints;

import java.util.ArrayList;
import java.util.List;

import prefuse.data.Graph;
import prefuse.data.Node;
import de.hochschuleTrier.fmv.model.interfaces.complexConstraints.IComplexConstraint;
import de.hochschuleTrier.fmv.model.interfaces.complexConstraints.IComplexConstraintGroup;
import de.hochschuleTrier.fmv.model.interfaces.complexConstraints.IComplexConstraintLiteral;
import de.hochschuleTrier.fmv.model.interfaces.complexConstraints.IComplexConstraintModel;

/**
 * Model for the visualization of the complex constraint view of a specific feature node
 * 
 */
public class ComplexConstraintModel implements IComplexConstraintModel {

	private final List<IComplexConstraintGroup> complexConstraints;
	private Graph constraintGraph;

	public ComplexConstraintModel(final List<IComplexConstraintGroup> complexConstraints) {
		this.complexConstraints = complexConstraints;
		this.initDataGroup();
	}

	/**
	 * Generates the needed Prefuse graph
	 */
	private void initDataGroup() {
		this.constraintGraph = new Graph();
		this.constraintGraph.addColumn("negate", boolean.class);
		this.constraintGraph.addColumn("name", String.class);
		this.constraintGraph.addColumn("id", int.class);
		for (final IComplexConstraintGroup constraint : this.complexConstraints) {
			this.initNodesFromComplexConstraint(constraint);
		}
	}

	/**
	 * 
	 * @param constraint
	 * @return
	 */
	private Node initNodesFromComplexConstraint(final IComplexConstraint constraint) {
		// Constraint is a single node --> recursion anker
		if (constraint instanceof IComplexConstraintLiteral) {
			final IComplexConstraintLiteral constraintNode = (IComplexConstraintLiteral) constraint;
			return this.processNode(constraintNode);
		}
		// We've got a complex constraint --> do recursive
		else {
			final IComplexConstraintGroup group = (IComplexConstraintGroup) constraint;
			this.processComplexConstraintGroup(group);
			return null;
		}
	}

	private Node processNode(final IComplexConstraintLiteral constraintNode) {
		final Node node = this.constraintGraph.addNode();
		node.setBoolean("negate", constraintNode.isNegated());
		node.setString("name", constraintNode.getName());
		node.setInt("id", constraintNode.getId());
		return node;
	}

	private void processComplexConstraintGroup(final IComplexConstraintGroup group) {
		// Collect all inner complex constraints
		final List<Node> nodes = new ArrayList<Node>();
		for (final IComplexConstraint child : group.getChildren()) {
			final Node node = this.initNodesFromComplexConstraint(child);
			if (node != null) {
				nodes.add(node);
			}
		}

		// And connect the nodes which belongs together
		for (int i = 0; i < nodes.size() - 1; i++) {
			for (int j = i + 1; j < nodes.size(); j++) {
				this.constraintGraph.addEdge(nodes.get(i), nodes.get(j));
			}
		}
	}

	@Override
	public synchronized Graph getConstraintGraph() {
		return this.constraintGraph;
	}

	@Override
	public List<IComplexConstraintGroup> getComplexConstraints() {
		return this.complexConstraints;
	}

}
