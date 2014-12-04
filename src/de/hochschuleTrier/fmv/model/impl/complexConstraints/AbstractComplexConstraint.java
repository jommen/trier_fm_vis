package de.hochschuleTrier.fmv.model.impl.complexConstraints;

import de.hochschuleTrier.fmv.model.interfaces.complexConstraints.IComplexConstraint;

public abstract class AbstractComplexConstraint implements IComplexConstraint {
	private boolean negated;

	@Override
	public boolean isNegated() {
		return this.negated;
	}

	@Override
	public void setNegated(final boolean negated) {
		this.negated = negated;
	}
}
