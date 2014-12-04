package de.hochschuleTrier.fmv.view.display;

import java.awt.geom.Rectangle2D;

import prefuse.Display;
import prefuse.Visualization;
import prefuse.util.GraphicsLib;
import prefuse.util.display.DisplayLib;
import prefuse.util.display.ItemBoundsListener;
import de.hochschuleTrier.fmv.controls.overview.OverviewControl;

/**
 * 
 * Quelle: http://deept.googlecode.com/svn/trunk/prefuse/demos/prefuse/demos2/decorators/Overview.java
 * 
 */
public class Overview extends Display {

	public Overview(final Display display) {
		super(display.getVisualization());

		DisplayLib.fitViewToBounds(this, this.getVisualization().getBounds(Visualization.ALL_ITEMS), 0);
		this.addItemBoundsListener(new FitOverviewListener());

		final OverviewControl zoomToFitRectangleControl = new OverviewControl(display, this);
		this.addControlListener(zoomToFitRectangleControl);
		this.addPaintListener(zoomToFitRectangleControl);
	}

	public static class FitOverviewListener implements ItemBoundsListener {
		private final Rectangle2D m_bounds = new Rectangle2D.Double();
		private final Rectangle2D m_temp = new Rectangle2D.Double();
		private final double m_d = 15;

		@Override
		public void itemBoundsChanged(final Display d) {
			d.getItemBounds(this.m_temp);
			GraphicsLib.expand(this.m_temp, 25 / d.getScale());

			final double dd = this.m_d / d.getScale();
			final double xd = Math.abs(this.m_temp.getMinX() - this.m_bounds.getMinX());
			final double yd = Math.abs(this.m_temp.getMinY() - this.m_bounds.getMinY());
			final double wd = Math.abs(this.m_temp.getWidth() - this.m_bounds.getWidth());
			final double hd = Math.abs(this.m_temp.getHeight() - this.m_bounds.getHeight());
			if (xd > dd || yd > dd || wd > dd || hd > dd) {
				this.m_bounds.setFrame(this.m_temp);
				DisplayLib.fitViewToBounds(d, this.m_bounds, 0);
			}
		}
	}
}
