package de.hochschuleTrier.fmv.model.interfaces.complexConstraints;

import java.util.List;

public interface IComplexConstraints {

	/**
	 * Returns all complex constraints in a list
	 * 
	 * @return
	 */
	public List<IComplexConstraintGroup> getConstraintList();

	/**
	 * Returns the complex constraints belonging to the given node
	 * 
	 * @param node
	 *            Name of the node for which you need the complex constraints
	 * @return
	 */
	public List<IComplexConstraintGroup> get(final String node);

}
