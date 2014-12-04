package de.hochschuleTrier.fmv.model.impl;

import prefuse.data.Schema;

public class FeatureSchema extends Schema {
	public final static String NAME = "name";
	public final static String NODETYPE = "nodetype";
	public final static String OPTIONAL = "optional";
	public final static String GROUPTYPE = "grouptype";
	public final static String IS_FULL_EXPANDED = "isFullExpanded";
	public final static String HAS_CROSS_TREE_EDGE = "hasCrossTreeEdge";
	public final static String COLOR = "color";

	/**
	 * Default constructor
	 */
	public FeatureSchema() {
		this.addColumn(NAME, String.class);
		this.addColumn(NODETYPE, String.class);
		this.addColumn(OPTIONAL, boolean.class);
		this.addColumn(GROUPTYPE, String.class);
		this.addColumn(IS_FULL_EXPANDED, boolean.class, false);
		this.addColumn(HAS_CROSS_TREE_EDGE, boolean.class, false);
		this.addColumn(COLOR, int.class, -1);
	}
}
