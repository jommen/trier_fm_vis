package de.hochschuleTrier.fmv.model.impl.complexConstraints;

public enum ComplexConstraintType {
	IMPLIES("implies"), EXCLUDES("excludes"), AND("and"), OR("or");

	private final String xmlTag;

	ComplexConstraintType(final String xmlTag) {
		this.xmlTag = xmlTag;
	}

	public String getXmlTag() {
		return this.xmlTag;
	}

	public static ComplexConstraintType getTypeByTag(final String xmlTag) {
		for (final ComplexConstraintType constraint : ComplexConstraintType.values()) {
			if (constraint.getXmlTag().equals(xmlTag)) {
				return constraint;
			}
		}

		return null;
	}
}
