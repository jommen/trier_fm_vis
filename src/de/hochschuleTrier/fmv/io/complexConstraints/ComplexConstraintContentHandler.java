package de.hochschuleTrier.fmv.io.complexConstraints;

import java.util.ArrayList;
import java.util.List;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import de.hochschuleTrier.fmv.model.impl.complexConstraints.ComplexConstraintGroup;
import de.hochschuleTrier.fmv.model.impl.complexConstraints.ComplexConstraintLiteral;
import de.hochschuleTrier.fmv.model.impl.complexConstraints.ComplexConstraintType;
import de.hochschuleTrier.fmv.model.interfaces.complexConstraints.IComplexConstraint;
import de.hochschuleTrier.fmv.model.interfaces.complexConstraints.IComplexConstraintGroup;
import de.hochschuleTrier.fmv.model.interfaces.complexConstraints.IComplexConstraintLiteral;

public class ComplexConstraintContentHandler extends DefaultHandler {

	private String currentValue;
	private IComplexConstraintGroup currentComplexConstraint;
	private IComplexConstraintLiteral currentConstraintNode;
	private final List<IComplexConstraintGroup> allConstraints;

	private int constraintNodeIdCounter;

	public ComplexConstraintContentHandler() {
		this.allConstraints = new ArrayList<IComplexConstraintGroup>();
	}

	@Override
	public void characters(final char[] ch, final int start, final int length) throws SAXException {
		this.currentValue = new String(ch, start, length);
	}

	@Override
	public void startElement(final String uri, final String localName, final String qName, final Attributes attributes) throws SAXException {

		if (localName.equals("node") && this.currentComplexConstraint != null) {
			this.currentConstraintNode = new ComplexConstraintLiteral(this.constraintNodeIdCounter++);
			this.setNegateIfNecessary(this.currentConstraintNode, attributes);
		}
		else {
			final ComplexConstraintType type = ComplexConstraintType.getTypeByTag(localName);
			if (type == null) {
				return;
			}

			switch (type) {
				case EXCLUDES:
				case IMPLIES:
					if (this.currentComplexConstraint != null) {
						this.allConstraints.add(this.currentComplexConstraint);
					}
					this.currentComplexConstraint = new ComplexConstraintGroup(type);
					this.allConstraints.add(this.currentComplexConstraint);
					break;
				case OR:
				case AND:
					final ComplexConstraintGroup constraint = new ComplexConstraintGroup(type, this.currentComplexConstraint);
					this.currentComplexConstraint.addChild(constraint);
					this.currentComplexConstraint = constraint;
					break;
			}

			this.setNegateIfNecessary(this.currentComplexConstraint, attributes);
		}
	}

	private void setNegateIfNecessary(final IComplexConstraint constraint, final Attributes attributes) {
		final boolean negate = Boolean.parseBoolean(attributes.getValue("negate"));
		constraint.setNegated(negate);
	}

	@Override
	public void endElement(final String uri, final String localName, final String qName) throws SAXException {
		if (this.currentComplexConstraint == null) {
			return;
		}
		if (localName.equals("node")) {
			this.currentConstraintNode.setName(this.currentValue);
			this.currentComplexConstraint.addChild(this.currentConstraintNode);
		}
		else {
			this.currentComplexConstraint = this.currentComplexConstraint.getParent();
		}
	}

	public List<IComplexConstraintGroup> getAllConstraints() {
		return this.allConstraints;
	}
}
