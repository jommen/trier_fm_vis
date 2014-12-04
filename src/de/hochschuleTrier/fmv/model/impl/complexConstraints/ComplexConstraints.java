package de.hochschuleTrier.fmv.model.impl.complexConstraints;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hochschuleTrier.fmv.io.complexConstraints.ComplexConstraintReader;
import de.hochschuleTrier.fmv.model.interfaces.complexConstraints.IComplexConstraint;
import de.hochschuleTrier.fmv.model.interfaces.complexConstraints.IComplexConstraintGroup;
import de.hochschuleTrier.fmv.model.interfaces.complexConstraints.IComplexConstraints;

/**
 * Container for all complex constraints
 * 
 */
public class ComplexConstraints implements IComplexConstraints {

	/**
	 * Maps the complex constraints belonging to a node: Map<NodeName, ComplexConstraints>
	 */
	private final Map<String, List<IComplexConstraintGroup>> constraintTable;

	/**
	 * Simple list of all complex constraints as they are read in by the {@link ComplexConstraintReader}
	 */
	private final List<IComplexConstraintGroup> constraintList;

	public ComplexConstraints() {
		this(null);
	}

	public ComplexConstraints(final List<IComplexConstraintGroup> complexConstraints) {
		this.constraintList = new ArrayList<>();
		this.constraintTable = new HashMap<>();

		if (complexConstraints != null) {
			this.constraintList.addAll(complexConstraints);

			for (final IComplexConstraintGroup complexConstraint : complexConstraints) {
				this.parseComplexConstraint(complexConstraint, complexConstraint);
			}
		}

	}

	/**
	 * Returns all complex constraints in a list
	 * 
	 * @return
	 */
	@Override
	public List<IComplexConstraintGroup> getConstraintList() {
		return this.constraintList;
	}

	/**
	 * Returns the complex constraints belonging to the given node
	 * 
	 * @param feature
	 *            Name of the node for which you need the complex constraints
	 * @return
	 */
	@Override
	public List<IComplexConstraintGroup> get(final String feature) {
		if (this.constraintTable.containsKey(feature)) {
			return this.constraintTable.get(feature);
		}
		return Collections.emptyList();
	}

	/**
	 * Parses the complex constraint list recursive to map the constraint to the related nodes
	 * 
	 * @param complexConstraint
	 * @param currentConstraint
	 */
	private void parseComplexConstraint(final IComplexConstraintGroup complexConstraint, final IComplexConstraint currentConstraint) {
		// if the complex constraint is a simple node --> recursive anker
		if (currentConstraint instanceof ComplexConstraintLiteral) {
			final ComplexConstraintLiteral node = (ComplexConstraintLiteral) currentConstraint;
			this.parseComplexConstraintNode(complexConstraint, node);
		}
		// else parse recursive
		else {
			for (final IComplexConstraint child : ((IComplexConstraintGroup) currentConstraint).getChildren()) {
				this.parseComplexConstraint(complexConstraint, child);
			}

		}
	}

	private void parseComplexConstraintNode(final IComplexConstraintGroup complexConstraint, final ComplexConstraintLiteral node) {
		if (this.constraintTable.containsKey(node.getName())) {
			if (!this.constraintTable.get(node.getName()).contains(complexConstraint)) {
				this.constraintTable.get(node.getName()).add(complexConstraint);
			}
		}
		else {
			final List<IComplexConstraintGroup> complexConstraints = new ArrayList<IComplexConstraintGroup>();
			complexConstraints.add(complexConstraint);
			this.constraintTable.put(node.getName(), complexConstraints);
		}
	}
}
