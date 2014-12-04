package de.hochschuleTrier.fmv.view.render;

import java.awt.Shape;

import prefuse.render.EdgeRenderer;
import prefuse.visual.VisualItem;

public class ComplexConstraintDefaultEdgeRenderer extends EdgeRenderer {

	@Override
	public Shape getShape(final VisualItem item) {
		// Die Kanten sollen nicht gezeichnet, im Layout aber berücksichtigt werden
		return null;
	}
}
