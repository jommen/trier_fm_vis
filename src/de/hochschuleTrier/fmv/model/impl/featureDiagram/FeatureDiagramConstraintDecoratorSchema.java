package de.hochschuleTrier.fmv.model.impl.featureDiagram;

import java.awt.geom.Rectangle2D;

import prefuse.data.Schema;
import prefuse.visual.NodeItem;
import prefuse.visual.VisualItem;

public class FeatureDiagramConstraintDecoratorSchema extends Schema {

	public static final String FROM_NODE = "fromNode";
	public static final String TO_NODE = "toNode";
	public static final String CONSTRAINT_TYPE = "constraintType";

	public FeatureDiagramConstraintDecoratorSchema() {
		// booleans
		this.addColumn(VisualItem.VALIDATED, boolean.class, Boolean.FALSE);
		this.addColumn(VisualItem.VISIBLE, boolean.class, Boolean.TRUE);
		this.addColumn(VisualItem.STARTVISIBLE, boolean.class, Boolean.FALSE);
		this.addColumn(VisualItem.ENDVISIBLE, boolean.class, Boolean.TRUE);
		this.addColumn(VisualItem.INTERACTIVE, boolean.class, Boolean.TRUE);

		// bounding box
		this.addColumn(VisualItem.BOUNDS, Rectangle2D.class, new Rectangle2D.Double());

		// custom
		this.addColumn(FROM_NODE, NodeItem.class);
		this.addColumn(TO_NODE, NodeItem.class);
		this.addColumn(CONSTRAINT_TYPE, String.class);
	}
}
