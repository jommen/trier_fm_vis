package de.hochschuleTrier.fmv.model.impl.featureDiagram;

import de.hochschuleTrier.fmv.model.interfaces.featureDiagram.IFeatureModel;

public class FeatureModel implements IFeatureModel {
	private String name;
	private boolean optional;
	private String nodeType;
	private String groupType;

	@Override
	public boolean isOptional() {
		return this.optional;
	}

	@Override
	public void setOptional(final boolean optional) {
		this.optional = optional;
	}

	@Override
	public String getOptionalAsString() {
		return String.valueOf(this.optional);
	}

	@Override
	public String getNodeType() {
		return this.nodeType;
	}

	@Override
	public void setNodeType(final String nodeType) {
		this.nodeType = nodeType;
	}

	@Override
	public String getGroupType() {
		return this.groupType;
	}

	@Override
	public void setGroupType(final String groupType) {
		this.groupType = groupType;
	}

	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public void setName(final String name) {
		this.name = name;
	}
}
