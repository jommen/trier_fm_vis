package de.hochschuleTrier.fmv.view.render;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.AffineTransform;

import prefuse.Constants;
import prefuse.render.PolygonRenderer;
import prefuse.visual.AggregateItem;
import prefuse.visual.VisualItem;
import de.hochschuleTrier.fmv.model.impl.complexConstraints.ComplexConstraintSchema;

public class ComplexConstraintAggregateRenderer extends PolygonRenderer {

	private static final int ARROW_SIZE = 6;

	/**
	 * Create a new PolygonRenderer supporting straight lines.
	 */
	public ComplexConstraintAggregateRenderer() {
		this(Constants.EDGE_TYPE_LINE);
	}

	/**
	 * Create a new PolygonRenderer.
	 * 
	 * @param polyType
	 *            the polygon edge type, one of {@link prefuse.Constants#POLY_TYPE_LINE}, {@link prefuse.Constants#POLY_TYPE_CURVE}, or {@link prefuse.Constants#POLY_TYPE_STACK}).
	 */
	public ComplexConstraintAggregateRenderer(final int polyType) {
		super(polyType);
	}

	@Override
	public void render(final Graphics2D g, final VisualItem item) {
		final Shape shape = this.getShape(item);
		if (shape != null) {
			this.drawShape(g, item, shape);
		}

		if (item instanceof AggregateItem) {
			final AggregateItem aggregateItem = (AggregateItem) item;
			g.setColor(Color.GREEN);
			this.drawComplexConstraintArrows(ComplexConstraintSchema.REQUIRES, aggregateItem, g, false);
			g.setColor(Color.RED);
			this.drawComplexConstraintArrows(ComplexConstraintSchema.EXCLUDES, aggregateItem, g, true);
		}
	}

	private void drawComplexConstraintArrows(final String type, final AggregateItem aggregateItem, final Graphics2D g, final boolean drawTwoArrowHeads) {
		if (aggregateItem.canGet(type + "Lines", float[][].class)) {
			final float[][] requiresLines = (float[][]) aggregateItem.get(type + "Lines");
			if (requiresLines != null) {
				for (final float[] requiresLine : requiresLines) {
					if (requiresLine != null) {
						this.drawArrow(g, (int) requiresLine[0], (int) requiresLine[1], (int) requiresLine[2], (int) requiresLine[3]);
						if (drawTwoArrowHeads) {
							this.drawArrow(g, (int) requiresLine[2], (int) requiresLine[3], (int) requiresLine[0], (int) requiresLine[1]);
						}
					}
				}
			}
		}
	}

	private void drawArrow(final Graphics g1, final int x1, final int y1, final int x2, final int y2) {
		final Graphics2D g = (Graphics2D) g1.create();

		final double dx = x2 - x1, dy = y2 - y1;
		final double angle = Math.atan2(dy, dx);
		final int len = (int) Math.sqrt(dx * dx + dy * dy);
		final AffineTransform at = AffineTransform.getTranslateInstance(x1, y1);
		at.concatenate(AffineTransform.getRotateInstance(angle));
		g.transform(at);

		// Draw horizontal arrow starting in (0, 0)
		final int arrowSize = ARROW_SIZE;
		g.drawLine(0, 0, len, 0);
		g.fillPolygon(new int[] { len, len - arrowSize, len - arrowSize, len }, new int[] { 0, -arrowSize, arrowSize, 0 }, 4);
	}
}
