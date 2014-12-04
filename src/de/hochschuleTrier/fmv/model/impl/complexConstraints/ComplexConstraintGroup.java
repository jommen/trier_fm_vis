package de.hochschuleTrier.fmv.model.impl.complexConstraints;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import de.hochschuleTrier.fmv.model.interfaces.complexConstraints.IComplexConstraint;
import de.hochschuleTrier.fmv.model.interfaces.complexConstraints.IComplexConstraintGroup;

public class ComplexConstraintGroup extends AbstractComplexConstraint implements IComplexConstraintGroup {
	private ComplexConstraintType type;
	private final List<IComplexConstraint> children;
	private final IComplexConstraintGroup parent;

	public ComplexConstraintGroup(final ComplexConstraintType type) {
		this(type, null);
	}

	public ComplexConstraintGroup(final ComplexConstraintType type, final IComplexConstraintGroup parent) {
		this.children = new ArrayList<IComplexConstraint>();
		this.type = type;
		this.parent = parent;
	}

	@Override
	public void setType(final ComplexConstraintType type) {
		this.type = type;
	}

	@Override
	public ComplexConstraintType getType() {
		return this.type;
	}

	@Override
	public boolean isTopLevelConstraint() {
		return this.type.equals(ComplexConstraintType.IMPLIES) || this.type.equals(ComplexConstraintType.EXCLUDES);
	}

	@Override
	public List<IComplexConstraint> getChildren() {
		return this.children;
	}

	@Override
	public void addChild(final IComplexConstraint child) {
		if (this.isTopLevelConstraint()) {
			if (this.children.size() >= 2) {
				throw new IllegalStateException("Implies and Excludes type do not allow more than 2 children");
			}
		}
		this.children.add(child);
	}

	@Override
	public IComplexConstraintGroup getParent() {
		return this.parent;
	}

	@Override
	public Iterator<IComplexConstraint> childrenIterator() {
		return this.children.iterator();
	}
}
