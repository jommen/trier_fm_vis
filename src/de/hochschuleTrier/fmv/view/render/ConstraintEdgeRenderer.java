package de.hochschuleTrier.fmv.view.render;

import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;

import prefuse.Constants;
import prefuse.render.EdgeRenderer;
import prefuse.util.GraphicsLib;
import prefuse.visual.VisualItem;
import prefuse.visual.tuple.TableEdgeItem;
import prefuse.visual.tuple.TableNodeItem;
import de.hochschuleTrier.fmv.model.impl.constraints.ConstraintEdgeSchema;
import de.hochschuleTrier.fmv.util.EdgeLib;

public class ConstraintEdgeRenderer extends EdgeRenderer {
	protected Shape m_constraintArrow;
	protected Shape m_constraintArrow2;
	protected Polygon m_arrowHead2 = this.updateArrowHead2(this.m_arrowWidth, this.m_arrowHeight);

	@Override
	protected Shape getRawShape(final VisualItem item) {
		final TableEdgeItem edge = (TableEdgeItem) item;
		final TableNodeItem sourcenode = (TableNodeItem) edge.getSourceNode();
		final TableNodeItem targetnode = (TableNodeItem) edge.getTargetNode();

		final int type = this.m_edgeType;

		EdgeRenderer.getAlignedPoint(this.m_tmpPoints[0], sourcenode.getBounds(), this.m_xAlign1, this.m_yAlign1);
		EdgeRenderer.getAlignedPoint(this.m_tmpPoints[1], targetnode.getBounds(), this.m_xAlign2, this.m_yAlign2);
		this.m_curWidth = (float) (this.m_width * this.getLineWidth(item));

		if (EdgeLib.isConstraintEdge(edge)) {
			// get starting and ending edge endpoints
			final boolean forward = (this.m_edgeArrow == Constants.EDGE_ARROW_FORWARD);
			Point2D start = null, end = null;
			start = this.m_tmpPoints[forward ? 0 : 1];
			end = this.m_tmpPoints[forward ? 1 : 0];

			// compute the intersection with the target bounding box
			final VisualItem dest = forward ? edge.getTargetItem() : edge.getSourceItem();
			final int i = GraphicsLib.intersectLineRectangle(start, end, dest.getBounds(), this.m_isctPoints);
			if (i > 0) {
				end = this.m_isctPoints[0];
			}

			// create the arrow head shape
			final AffineTransform at = this.getArrowTrans(start, end, this.m_curWidth);
			this.m_constraintArrow = at.createTransformedShape(this.m_arrowHead);

			// update the endpoints for the edge shape
			// need to bias this by arrow head size
			final Point2D lineEnd = this.m_tmpPoints[forward ? 1 : 0];
			lineEnd.setLocation(0, -this.m_arrowHeight);
			at.transform(lineEnd, lineEnd);
		}
		if (EdgeLib.isConstraintEdge(edge) && EdgeLib.getConstraintType(edge).equals(ConstraintEdgeSchema.EXCLUDES_CONSTRAINT)) {
			// get starting and ending edge endpoints
			final boolean forward = !(this.m_edgeArrow == Constants.EDGE_ARROW_FORWARD);
			Point2D start = null, end = null;
			start = this.m_tmpPoints[forward ? 0 : 1];
			end = this.m_tmpPoints[forward ? 1 : 0];

			// compute the intersection with the target bounding box
			final VisualItem dest = forward ? edge.getTargetItem() : edge.getSourceItem();
			final int i = GraphicsLib.intersectLineRectangle(start, end, dest.getBounds(), this.m_isctPoints);
			if (i > 0) {
				end = this.m_isctPoints[0];
			}

			// create the arrow head shape
			final AffineTransform at = this.getArrowTrans(start, end, this.m_curWidth);
			this.m_constraintArrow2 = at.createTransformedShape(this.m_arrowHead2);

			// update the endpoints for the edge shape
			// need to bias this by arrow head size
			final Point2D lineEnd = this.m_tmpPoints[forward ? 1 : 0];
			lineEnd.setLocation(0, -this.m_arrowHeight);
			at.transform(lineEnd, lineEnd);
		}
		else {
			this.m_constraintArrow2 = null;
		}
		if (!EdgeLib.isConstraintEdge(edge)) {
			this.m_constraintArrow = null;
			this.m_constraintArrow2 = null;
		}

		// create the edge shape
		Shape shape = null;
		final double n1x = this.m_tmpPoints[0].getX();
		final double n1y = this.m_tmpPoints[0].getY();
		final double n2x = this.m_tmpPoints[1].getX();
		final double n2y = this.m_tmpPoints[1].getY();
		switch (type) {
			case Constants.EDGE_TYPE_LINE:
				this.m_line.setLine(n1x, n1y, n2x, n2y);
				shape = this.m_line;
				break;
			case Constants.EDGE_TYPE_CURVE:
				this.getCurveControlPoints(edge, this.m_ctrlPoints, n1x, n1y, n2x, n2y);
				this.m_cubic.setCurve(n1x, n1y, this.m_ctrlPoints[0].getX(), this.m_ctrlPoints[0].getY(), this.m_ctrlPoints[1].getX(), this.m_ctrlPoints[1].getY(), n2x, n2y);
				shape = this.m_cubic;
				break;
			default:
				throw new IllegalStateException("Unknown edge type");
		}

		// return the edge shape
		return shape;
	}

	@Override
	public void render(final Graphics2D g, final VisualItem item) {
		// render the edge line
		super.render(g, item);
		// render the edge arrow head, if appropriate
		if (this.m_constraintArrow != null) {
			g.fill(this.m_constraintArrow);
		}
		if (this.m_constraintArrow2 != null) {
			g.fill(this.m_constraintArrow2);
		}
	}

	protected Polygon updateArrowHead2(final int w, final int h) {
		if (this.m_arrowHead2 == null) {
			this.m_arrowHead2 = new Polygon();
		}
		else {
			this.m_arrowHead2.reset();
		}
		this.m_arrowHead2.addPoint(0, 0);
		this.m_arrowHead2.addPoint(-w / 2, -h);
		this.m_arrowHead2.addPoint(w / 2, -h);
		this.m_arrowHead2.addPoint(0, 0);
		return this.m_arrowHead2;
	}
}
