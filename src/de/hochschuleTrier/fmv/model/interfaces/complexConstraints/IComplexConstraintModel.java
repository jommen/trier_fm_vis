package de.hochschuleTrier.fmv.model.interfaces.complexConstraints;

import java.util.List;

import prefuse.data.Graph;

public interface IComplexConstraintModel {
	Graph getConstraintGraph();

	List<IComplexConstraintGroup> getComplexConstraints();
}
