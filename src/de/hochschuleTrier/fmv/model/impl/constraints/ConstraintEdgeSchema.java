package de.hochschuleTrier.fmv.model.impl.constraints;

import prefuse.data.Schema;

public class ConstraintEdgeSchema extends Schema {
	public final static String EDGE_TYPE = "edgetype";
	public final static String CONSTRAINT_TYPE = "constrainttype";

	public static final String REQUIRES_CONSTRAINT = "requires";
	public static final String EXCLUDES_CONSTRAINT = "excludes";

	public static final String EDGE_TYPE_CONSTRAINT = "constraint";

	/**
	 * Default constructor
	 */
	public ConstraintEdgeSchema() {
		this.addColumn(EDGE_TYPE, String.class);
		this.addColumn(CONSTRAINT_TYPE, String.class);
	}

}
