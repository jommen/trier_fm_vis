package de.hochschuleTrier.fmv.view.render;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;

import prefuse.render.AbstractShapeRenderer;
import prefuse.visual.DecoratorItem;
import prefuse.visual.NodeItem;
import prefuse.visual.VisualItem;
import de.hochschuleTrier.fmv.model.impl.constraints.ConstraintEdgeSchema;
import de.hochschuleTrier.fmv.model.impl.featureDiagram.FeatureDiagramConstraintDecoratorSchema;

public class FeatureDiagramConstraintEdgeRenderer extends AbstractShapeRenderer {

	private static final int ARROW_SIZE = 6;

	@Override
	public void render(final Graphics2D g, final VisualItem item) {
		final DecoratorItem decorator = (DecoratorItem) item;

		if (this.hasConstraintEdge(decorator)) {
			final NodeItem fromNode = (NodeItem) decorator.get(FeatureDiagramConstraintDecoratorSchema.FROM_NODE);
			final NodeItem toNode = (NodeItem) decorator.get(FeatureDiagramConstraintDecoratorSchema.TO_NODE);

			final Line2D line = this.calcLine(fromNode, toNode);

			if (this.isRequiredConstraint(decorator)) {
				g.setColor(Color.GREEN);
				this.drawArrow(g, line.getX1(), line.getY1(), line.getX2(), line.getY2());
			}
			else {
				g.setColor(Color.RED);
				this.drawArrow(g, line.getX1(), line.getY1(), line.getX2(), line.getY2());
				this.drawArrow(g, line.getX2(), line.getY2(), line.getX1(), line.getY1());
			}
		}
	}

	private Line2D calcLine(final NodeItem fromNode, final NodeItem toNode) {
		final Line2D line = new Line2D.Float();

		int startX = (int) fromNode.getX();
		int startY = (int) fromNode.getY();
		int endX = (int) toNode.getX();
		int endY = (int) toNode.getY();

		if (startX > endX) {
			endX += toNode.getBounds().getWidth();
		}
		else if (startX < endX) {
			startX += fromNode.getBounds().getWidth();
		}
		else {
			startX += fromNode.getBounds().getWidth() / 2;
			endX += toNode.getBounds().getWidth() / 2;
			if (startY > endY) {
				startY -= fromNode.getBounds().getHeight() / 2;
				endY += toNode.getBounds().getHeight() / 2;
			}
			else {
				startY += fromNode.getBounds().getHeight() / 2;
				endY -= toNode.getBounds().getHeight() / 2;
			}
		}

		line.setLine(startX, startY, endX, endY);
		return line;
	}

	private boolean hasConstraintEdge(final DecoratorItem decorator) {
		return decorator.canGet(FeatureDiagramConstraintDecoratorSchema.FROM_NODE, NodeItem.class) && decorator.canGet(FeatureDiagramConstraintDecoratorSchema.TO_NODE, NodeItem.class)
				&& decorator.get(FeatureDiagramConstraintDecoratorSchema.FROM_NODE) != null && decorator.get(FeatureDiagramConstraintDecoratorSchema.TO_NODE) != null;
	}

	private boolean isRequiredConstraint(final DecoratorItem decorator) {
		return decorator.getString(FeatureDiagramConstraintDecoratorSchema.CONSTRAINT_TYPE).equals(ConstraintEdgeSchema.REQUIRES_CONSTRAINT);
	}

	private void drawArrow(final Graphics2D g1, final double x1, final double y1, final double x2, final double y2) {
		final Graphics2D g = (Graphics2D) g1.create();
		final double dx = x2 - x1, dy = y2 - y1;
		final double angle = Math.atan2(dy, dx);
		final int len = (int) Math.sqrt(dx * dx + dy * dy);
		final AffineTransform at = AffineTransform.getTranslateInstance(x1, y1);
		at.concatenate(AffineTransform.getRotateInstance(angle));
		g.transform(at);

		final int arrowSize = FeatureDiagramConstraintEdgeRenderer.ARROW_SIZE;
		g.drawLine(0, 0, len, 0);
		g.fillPolygon(new int[] { len, len - arrowSize, len - arrowSize, len }, new int[] { 0, -arrowSize, arrowSize, 0 }, 4);
	}

	@Override
	protected Shape getRawShape(final VisualItem item) {
		final DecoratorItem decorator = (DecoratorItem) item;
		if (decorator != null && this.hasConstraintEdge(decorator)) {
			final NodeItem fromNode = (NodeItem) decorator.get(FeatureDiagramConstraintDecoratorSchema.FROM_NODE);
			final NodeItem toNode = (NodeItem) decorator.get(FeatureDiagramConstraintDecoratorSchema.TO_NODE);

			final int startX = (int) fromNode.getX();
			final int startY = (int) fromNode.getY();
			final int endX = (int) toNode.getX();
			final int endY = (int) toNode.getY();

			final Line2D line = new Line2D.Float();
			line.setLine(startX, startY, endX, endY);

			return line;
		}
		return null;
	}

}
