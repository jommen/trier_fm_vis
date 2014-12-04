package de.hochschuleTrier.fmv.util;

public enum TreeDisplayEnum {
	TREE("tree"), NODES("tree.nodes"), EDGES("tree.edges"), LINEAR("linear");

	private final String text;

	private TreeDisplayEnum(final String text) {
		this.text = text;
	}

	@Override
	public String toString() {
		return this.text;
	}
}
