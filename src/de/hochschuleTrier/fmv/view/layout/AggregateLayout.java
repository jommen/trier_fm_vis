package de.hochschuleTrier.fmv.view.layout;

import java.awt.Point;
import java.awt.geom.Rectangle2D;
import java.util.Iterator;

import prefuse.action.layout.Layout;
import prefuse.util.GraphicsLib;
import prefuse.visual.AggregateItem;
import prefuse.visual.AggregateTable;
import prefuse.visual.VisualItem;
import de.hochschuleTrier.fmv.model.impl.complexConstraints.ComplexConstraintSchema;

/**
 * Layout algorithm that computes a convex hull surrounding aggregate items and saves it in the "_polygon" field.
 */
public class AggregateLayout extends Layout {

	private final int m_margin = 5; // convex hull pixel margin
	private double[] m_pts; // buffer for computing convex hulls
	private AggregateTable aggregateTable;

	public AggregateLayout(final String aggrGroup) {
		super(aggrGroup);
	}

	/**
	 * @see edu.berkeley.guir.prefuse.action.Action#run(edu.berkeley.guir.prefuse.ItemRegistry, double)
	 */
	@Override
	public void run(final double frac) {
		this.aggregateTable = (AggregateTable) this.m_vis.getGroup(this.m_group);

		// do we have any to process?
		if (this.aggregateTable.getTupleCount() == 0) {
			return;
		}

		this.updateBuffers();
		this.computeAndAssignConvexHulls();
		this.computeAndAssignComplexConstraints();
	}

	private void updateBuffers() {
		int maxsz = 0;
		for (final Iterator aggrs = this.aggregateTable.tuples(); aggrs.hasNext();) {
			maxsz = Math.max(maxsz, 4 * 2 * ((AggregateItem) aggrs.next()).getAggregateSize());
		}
		if (this.m_pts == null || maxsz > this.m_pts.length) {
			this.m_pts = new double[maxsz];
		}
	}

	private void computeAndAssignConvexHulls() {
		final Iterator aggregates = this.m_vis.visibleItems(this.m_group);
		while (aggregates.hasNext()) {
			final AggregateItem aitem = (AggregateItem) aggregates.next();

			int idx = 0;
			if (aitem.getAggregateSize() == 0) {
				continue;
			}
			VisualItem item = null;
			final Iterator iter = aitem.items();
			while (iter.hasNext()) {
				item = (VisualItem) iter.next();
				if (item.isVisible()) {
					AggregateLayout.addPoint(this.m_pts, idx, item, this.m_margin);
					idx += 2 * 4;
				}
			}
			// if no aggregates are visible, do nothing
			if (idx == 0) {
				continue;
			}

			// compute convex hull
			final double[] nhull = GraphicsLib.convexHull(this.m_pts, idx);

			// prepare viz attribute array
			float[] fhull = (float[]) aitem.get(VisualItem.POLYGON);
			if (fhull == null || fhull.length < nhull.length) {
				fhull = new float[nhull.length];
			}
			else if (fhull.length > nhull.length) {
				fhull[nhull.length] = Float.NaN;
			}

			// copy hull values
			for (int j = 0; j < nhull.length; j++) {
				fhull[j] = (float) nhull[j];
			}

			aitem.set(VisualItem.POLYGON, fhull);
			aitem.setValidated(false); // force invalidation
		}
	}

	private void computeAndAssignComplexConstraints() {
		final Iterator aggregates = this.m_vis.visibleItems(this.m_group);
		while (aggregates.hasNext()) {
			final AggregateItem aggregateItem = (AggregateItem) aggregates.next();
			this.computeLines(aggregateItem, ComplexConstraintSchema.REQUIRES);
			this.computeLines(aggregateItem, ComplexConstraintSchema.EXCLUDES);
		}
	}

	/**
	 * 
	 * @param aggregateItem
	 *            AggregateItem for which the lines should be computed
	 * @param type
	 *            Type of line to compute. Possible values are "requires" and "excludes".
	 */
	private void computeLines(final AggregateItem aggregateItem, final String type) {
		if (aggregateItem.canGet(type, AggregateItem[].class)) {
			final AggregateItem[] requiresItems = (AggregateItem[]) aggregateItem.get(type);
			if (requiresItems != null) {
				final float[][] lines = new float[requiresItems.length][];
				for (int itemIndex = 0; itemIndex < requiresItems.length; itemIndex++) {
					final AggregateItem constraintToItem = requiresItems[itemIndex];

					final float[] itemPolygon = (float[]) aggregateItem.get(VisualItem.POLYGON);
					final float[] constraintToPolygon = (float[]) constraintToItem.get(VisualItem.POLYGON);

					double minDistance = Double.MAX_VALUE;
					for (int i = 0; i < itemPolygon.length; i += 2) {
						// Wenn der Wert NaN ist, sind alle weiteren Werte nicht mehr relevant
						if (Float.isNaN(itemPolygon[i]) || Float.isNaN(itemPolygon[i + 1])) {
							break;
						}
						for (int j = 0; j < constraintToPolygon.length; j += 2) {
							// Wenn der Wert NaN ist, sind alle weiteren Werte nicht mehr relevant
							if (Float.isNaN(constraintToPolygon[j]) || Float.isNaN(constraintToPolygon[j + 1])) {
								break;
							}
							final Point point1 = new Point((int) itemPolygon[i], (int) itemPolygon[i + 1]);
							final Point point2 = new Point((int) constraintToPolygon[j], (int) constraintToPolygon[j + 1]);
							final double distance = point1.distance(point2);
							if (distance < minDistance) {
								minDistance = distance;
								lines[itemIndex] = new float[] { itemPolygon[i], itemPolygon[i + 1], constraintToPolygon[j], constraintToPolygon[j + 1] };
							}
						}
					}
				}
				aggregateItem.set(type + "Lines", lines);
			}
		}
	}

	private static void addPoint(final double[] pts, final int idx, final VisualItem item, final int growth) {
		final Rectangle2D b = item.getBounds();
		final double minX = (b.getMinX()) - growth, minY = (b.getMinY()) - growth;
		final double maxX = (b.getMaxX()) + growth, maxY = (b.getMaxY()) + growth;
		pts[idx] = minX;
		pts[idx + 1] = minY;
		pts[idx + 2] = minX;
		pts[idx + 3] = maxY;
		pts[idx + 4] = maxX;
		pts[idx + 5] = minY;
		pts[idx + 6] = maxX;
		pts[idx + 7] = maxY;
	}

} // end of class AggregateLayout