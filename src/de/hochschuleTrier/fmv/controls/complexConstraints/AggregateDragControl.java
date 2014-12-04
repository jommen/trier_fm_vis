package de.hochschuleTrier.fmv.controls.complexConstraints;

import java.awt.Cursor;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.util.Iterator;

import javax.swing.SwingUtilities;

import prefuse.Display;
import prefuse.controls.ControlAdapter;
import prefuse.visual.AggregateItem;
import prefuse.visual.VisualItem;

/**
 * Interactive drag control that is "aggregate-aware"
 */
public class AggregateDragControl extends ControlAdapter {

	private VisualItem activeItem;
	protected Point2D down = new Point2D.Double();
	protected Point2D temp = new Point2D.Double();
	protected boolean dragged;

	/**
	 * Creates a new drag control that issues repaint requests as an item is dragged.
	 */
	public AggregateDragControl() {
	}

	/**
	 * @see prefuse.controls.Control#itemEntered(prefuse.visual.VisualItem, java.awt.event.MouseEvent)
	 */
	@Override
	public void itemEntered(final VisualItem item, final MouseEvent e) {
		final Display d = (Display) e.getSource();
		d.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		this.activeItem = item;
		if (!(item instanceof AggregateItem)) {
			AggregateDragControl.setFixed(item, true);
		}
	}

	/**
	 * @see prefuse.controls.Control#itemExited(prefuse.visual.VisualItem, java.awt.event.MouseEvent)
	 */
	@Override
	public void itemExited(final VisualItem item, final MouseEvent e) {
		if (this.activeItem == item) {
			this.activeItem = null;
			AggregateDragControl.setFixed(item, false);
		}
		final Display d = (Display) e.getSource();
		d.setCursor(Cursor.getDefaultCursor());
	}

	/**
	 * @see prefuse.controls.Control#itemPressed(prefuse.visual.VisualItem, java.awt.event.MouseEvent)
	 */
	@Override
	public void itemPressed(final VisualItem item, final MouseEvent e) {
		if (!SwingUtilities.isLeftMouseButton(e)) {
			return;
		}
		this.dragged = false;
		final Display d = (Display) e.getComponent();
		d.getAbsoluteCoordinate(e.getPoint(), this.down);
		if (item instanceof AggregateItem) {
			AggregateDragControl.setFixed(item, true);
		}
	}

	/**
	 * @see prefuse.controls.Control#itemReleased(prefuse.visual.VisualItem, java.awt.event.MouseEvent)
	 */
	@Override
	public void itemReleased(final VisualItem item, final MouseEvent e) {
		if (!SwingUtilities.isLeftMouseButton(e)) {
			return;
		}
		if (this.dragged) {
			this.activeItem = null;
			AggregateDragControl.setFixed(item, false);
			this.dragged = false;
		}
	}

	/**
	 * @see prefuse.controls.Control#itemDragged(prefuse.visual.VisualItem, java.awt.event.MouseEvent)
	 */
	@Override
	public void itemDragged(final VisualItem item, final MouseEvent e) {
		if (!SwingUtilities.isLeftMouseButton(e)) {
			return;
		}
		this.dragged = true;
		final Display d = (Display) e.getComponent();
		d.getAbsoluteCoordinate(e.getPoint(), this.temp);
		final double dx = this.temp.getX() - this.down.getX();
		final double dy = this.temp.getY() - this.down.getY();

		AggregateDragControl.move(item, dx, dy);

		this.down.setLocation(this.temp);
	}

	protected static void setFixed(final VisualItem item, final boolean fixed) {
		if (item instanceof AggregateItem) {
			final Iterator items = ((AggregateItem) item).items();
			while (items.hasNext()) {
				AggregateDragControl.setFixed((VisualItem) items.next(), fixed);
			}
		}
		else {
			item.setFixed(fixed);
		}
	}

	protected static void move(final VisualItem item, final double dx, final double dy) {
		if (item instanceof AggregateItem) {
			final Iterator items = ((AggregateItem) item).items();
			while (items.hasNext()) {
				AggregateDragControl.move((VisualItem) items.next(), dx, dy);
			}
		}
		else {
			final double x = item.getX();
			final double y = item.getY();
			item.setStartX(x);
			item.setStartY(y);
			item.setX(x + dx);
			item.setY(y + dy);
			item.setEndX(x + dx);
			item.setEndY(y + dy);
		}
	}

}