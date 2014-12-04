package de.hochschuleTrier.fmv.util;

import prefuse.data.Edge;
import de.hochschuleTrier.fmv.model.impl.constraints.ConstraintEdgeSchema;

public class EdgeLib {

	public static boolean isConstraintEdge(final Edge edge) {
		return edge.getString(ConstraintEdgeSchema.EDGE_TYPE).equals(ConstraintEdgeSchema.EDGE_TYPE_CONSTRAINT);
	}

	public static String getConstraintType(final Edge edge) {
		return edge.getString(ConstraintEdgeSchema.CONSTRAINT_TYPE);
	}

}
