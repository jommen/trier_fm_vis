package de.hochschuleTrier.fmv.model.impl.complexConstraints;

import prefuse.data.Schema;
import prefuse.visual.AggregateItem;
import prefuse.visual.VisualItem;

/**
 * Schema which describes the data for the complex constraints
 * 
 */
public class ComplexConstraintSchema extends Schema {

	public final static String ID = "id";
	public final static String TYPE = "type";
	public final static String NEGATE = "negate";
	public final static String REQUIRES = "requires";
	public final static String EXCLUDES = "excludes";
	public final static String REQUIRES_LINES = "requiresLines";
	public final static String EXCLUDES_LINES = "excludesLines";

	public final static String TYPE_OR = "or";
	public final static String TYPE_AND = "and";

	/**
	 * Default constructor
	 */
	public ComplexConstraintSchema() {
		this.addColumn(VisualItem.POLYGON, float[].class);
		this.addColumn(ComplexConstraintSchema.ID, int.class);
		this.addColumn(ComplexConstraintSchema.TYPE, String.class);
		this.addColumn(ComplexConstraintSchema.NEGATE, boolean.class);
		this.addColumn(ComplexConstraintSchema.REQUIRES, AggregateItem[].class);
		this.addColumn(ComplexConstraintSchema.EXCLUDES, AggregateItem[].class);
		this.addColumn(ComplexConstraintSchema.REQUIRES_LINES, float[][].class);
		this.addColumn(ComplexConstraintSchema.EXCLUDES_LINES, float[][].class);
	}

}
