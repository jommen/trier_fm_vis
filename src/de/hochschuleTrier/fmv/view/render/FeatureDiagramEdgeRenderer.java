package de.hochschuleTrier.fmv.view.render;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.CubicCurve2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Ellipse2D.Double;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;

import prefuse.Constants;
import prefuse.render.EdgeRenderer;
import prefuse.util.ColorLib;
import prefuse.util.GraphicsLib;
import prefuse.visual.EdgeItem;
import prefuse.visual.VisualItem;
import prefuse.visual.tuple.TableEdgeItem;
import prefuse.visual.tuple.TableNodeItem;
import de.hochschuleTrier.fmv.model.impl.FeatureSchema;
import de.hochschuleTrier.fmv.model.impl.featureDiagram.FeatureDiagramSchema;
import de.hochschuleTrier.fmv.util.NodeLib;

public class FeatureDiagramEdgeRenderer extends EdgeRenderer {
	protected int m_circleSize = 8;
	protected Ellipse2D.Double m_circle = this.updateCircle();
	protected AffineTransform m_circleTrans = new AffineTransform();
	protected Shape m_curCircle;
	protected Polygon m_groupPolygon; // for alternative and or group edges
	protected Point2D m_ctrlPointsBackup[] = new Point2D[2];
	// private boolean m_drawGroupDecoration = false;

	// neu:
	private final Point2D[] edgeMarkPoints = new Point2D[4];

	public FeatureDiagramEdgeRenderer() {
		super();
	}

	public FeatureDiagramEdgeRenderer(final int edgeType) {
		super(edgeType);
	}

	private Double updateCircle() {
		if (this.m_circle == null) {
			this.m_circle = new Ellipse2D.Double(-this.m_circleSize, -this.m_circleSize / 2, this.m_circleSize, this.m_circleSize);
		}
		this.m_circle.height = this.m_circleSize;
		this.m_circle.width = this.m_circleSize;

		return this.m_circle;
	}

	@Override
	protected Shape getRawShape(final VisualItem item) {
		final EdgeItem edge = (EdgeItem) item;
		final VisualItem item1 = edge.getSourceItem();
		final VisualItem item2 = edge.getTargetItem();

		final int type = this.m_edgeType;

		EdgeRenderer.getAlignedPoint(this.m_tmpPoints[0], item1.getBounds(), this.m_xAlign1, this.m_yAlign1);
		EdgeRenderer.getAlignedPoint(this.m_tmpPoints[1], item2.getBounds(), this.m_xAlign2, this.m_yAlign2);
		this.m_curWidth = (float) (this.m_width * this.getLineWidth(item));

		// get starting and ending edge endpoints

		Point2D start = null, end = null;
		start = this.m_tmpPoints[0];
		end = this.m_tmpPoints[1];

		// compute the intersection with the target bounding box
		final VisualItem dest = edge.getTargetItem();
		final int i = GraphicsLib.intersectLineRectangle(start, end, dest.getBounds(), this.m_isctPoints);
		if (i > 0) {
			end = this.m_isctPoints[0];
		}

		// create circle shape for mandatory and optional elements
		final AffineTransform at = this.getCircleTrans(start, end, this.m_curWidth);
		this.m_curCircle = at.createTransformedShape(this.m_circle);

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

				// neu
				this.edgeMarkPoints[0] = new Point2D.Double(n1x, n1y); // start
				this.edgeMarkPoints[1] = new Point2D.Double(this.m_ctrlPoints[0].getX(), this.m_ctrlPoints[0].getY()); // c1
				this.edgeMarkPoints[2] = new Point2D.Double(this.m_ctrlPoints[1].getX(), this.m_ctrlPoints[1].getY()); // c2
				this.edgeMarkPoints[3] = new Point2D.Double(n2x, n2y);// end

				shape = this.m_cubic;
				break;
			default:
				throw new IllegalStateException("Unknown edge type");
		}

		return shape;
	}

	private AffineTransform getCircleTrans(final Point2D p1, final Point2D p2, final double width) {
		this.m_circleTrans.setToTranslation(p2.getX(), p2.getY());
		this.m_circleTrans.translate(-1, 0);
		if (width > 1) {
			final double scalar = width / 4;
			this.m_circleTrans.scale(scalar, scalar);
		}
		return this.m_circleTrans;
	}

	@Override
	public void render(final Graphics2D g, final VisualItem item) {

		// render the edge line
		final TableEdgeItem e = (TableEdgeItem) item;
		final TableNodeItem target = (TableNodeItem) e.getTargetNode();
		final TableNodeItem source = (TableNodeItem) e.getSourceNode();
		super.render(g, item);

		// render edge decorations
		if (this.m_curCircle != null) {
			g.setStroke(new BasicStroke(2));
			g.draw(this.m_curCircle);
			g.fill(this.m_curCircle);
			if (NodeLib.isOptional(target)) {
				g.setColor(Color.white);
				g.fill(this.m_curCircle);
			}
		}
		// render group decoration (Junk) - DOES NOT WORK!!!!
		g.setPaint(ColorLib.getColor(item.getStrokeColor()));

		// Blanke: render group decorations :-)
		if (this.m_edgeType == Constants.EDGE_TYPE_CURVE && NodeLib.isFeatureGroup(source) && target.equals(source.getFirstChild())) {
			final String grouptype = source.getString(FeatureSchema.GROUPTYPE);

			if (grouptype.equals(FeatureDiagramSchema.GROUPTYPE_ALTERNATIVE)) {
				this.drawEdgeMark(g, true);
			}
			else if (grouptype.equals(FeatureDiagramSchema.GROUPTYPE_OR)) {
				this.drawEdgeMark(g, false);
			}
		}

	}

	public void drawEdgeMark(final Graphics2D g, final boolean alternative) {

		final double[] start = { this.edgeMarkPoints[0].getX(), this.edgeMarkPoints[0].getY() };
		final double[] end = { this.edgeMarkPoints[3].getX(), this.edgeMarkPoints[3].getY() };
		final double[] c1 = { this.edgeMarkPoints[1].getX(), this.edgeMarkPoints[1].getY() };
		final double[] c2 = { this.edgeMarkPoints[2].getX(), this.edgeMarkPoints[2].getY() };

		final double[] srcPoints = { start[0], start[1], c1[0], c1[1], c2[0], c2[1], end[0], end[1] };

		final double[] leftPoints = new double[8];
		final double[] rightPoints = new double[8];

		CubicCurve2D.subdivide(srcPoints, 0, leftPoints, 0, rightPoints, 0);

		final CubicCurve2D.Double upperLeftCurve = new CubicCurve2D.Double();
		upperLeftCurve.setCurve(leftPoints, 0);

		/*
		 * y-Koordinaten von c1, c2 und end spiegeln, punkte umdrehen, damit Kurve von rechts nach links geht
		 */

		final double[] underCurvePoints = new double[8];

		underCurvePoints[0] = leftPoints[6];
		underCurvePoints[1] = leftPoints[7] + 2 * Math.abs(leftPoints[1] - leftPoints[7]);
		underCurvePoints[2] = leftPoints[4];
		underCurvePoints[3] = leftPoints[5] + 2 * Math.abs(leftPoints[1] - leftPoints[5]);
		underCurvePoints[4] = leftPoints[2];
		underCurvePoints[5] = leftPoints[3] + 2 * Math.abs(leftPoints[1] - leftPoints[3]);
		underCurvePoints[6] = leftPoints[0];
		underCurvePoints[7] = leftPoints[1];

		final CubicCurve2D.Double underLeftCurve = new CubicCurve2D.Double();
		underLeftCurve.setCurve(underCurvePoints, 0);

		final Path2D.Double path = new Path2D.Double(upperLeftCurve);
		path.append(underLeftCurve, true);

		g.setStroke(new BasicStroke());

		if (alternative) {
			g.draw(path);
		}
		else {
			g.draw(path);
			g.fill(path);
		}

	}

}
