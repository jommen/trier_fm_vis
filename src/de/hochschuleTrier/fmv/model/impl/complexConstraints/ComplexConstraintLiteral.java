package de.hochschuleTrier.fmv.model.impl.complexConstraints;

import de.hochschuleTrier.fmv.model.interfaces.complexConstraints.IComplexConstraintLiteral;

public class ComplexConstraintLiteral extends AbstractComplexConstraint implements IComplexConstraintLiteral {
	private String name;
	private final int id;

	public ComplexConstraintLiteral(final int id) {
		this.id = id;
	}

	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public void setName(final String name) {
		this.name = name;
	}

	@Override
	public int getId() {
		return this.id;
	}

}
