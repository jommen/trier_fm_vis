package de.hochschuleTrier.fmv.util;

import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import prefuse.Display;
import prefuse.Visualization;
import prefuse.util.GraphicsLib;
import prefuse.util.display.DisplayLib;

public class FMDisplayLib extends DisplayLib {

	public static void zoomToFit(final Display display) {
		if (!display.isTranformInProgress()) {
			final Rectangle2D bounds = display.getVisualization().getBounds(Visualization.ALL_ITEMS);
			GraphicsLib.expand(bounds, 10 + (int) (1 / display.getScale()));
			DisplayLib.fitViewToBounds(display, bounds, 1000);
			display.repaint();
		}
	}

	public static void zoom(final Display display, final double zoomScale) {
		final Point windowLocation = display.getLocationOnScreen();
		final Point mouseLocation = MouseInfo.getPointerInfo().getLocation();
		final double x = mouseLocation.getX() - windowLocation.getX();
		final double y = mouseLocation.getY() - windowLocation.getY();
		FMDisplayLib.zoom(display, zoomScale, new Point2D.Double(x, y));
	}

	public static void zoomCenter(final Display display, final double zoomScale) {
		final double x = (display.getWidth() / 2.0);
		final double y = (display.getHeight() / 2.0);
		FMDisplayLib.zoom(display, zoomScale, new Point2D.Double(x, y));
	}

	private static void zoom(final Display display, final double zoomScale, final Point2D center) {
		display.zoom(center, zoomScale);
		display.repaint();
	}
}
