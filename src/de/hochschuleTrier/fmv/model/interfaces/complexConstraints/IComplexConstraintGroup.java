package de.hochschuleTrier.fmv.model.interfaces.complexConstraints;

import java.util.Iterator;
import java.util.List;

import de.hochschuleTrier.fmv.model.impl.complexConstraints.ComplexConstraintType;

public interface IComplexConstraintGroup extends IComplexConstraint {

	public void setType(ComplexConstraintType type);

	public ComplexConstraintType getType();

	public boolean isTopLevelConstraint();

	public List<IComplexConstraint> getChildren();

	public Iterator<IComplexConstraint> childrenIterator();

	public void addChild(IComplexConstraint child);

	public IComplexConstraintGroup getParent();
}
