package de.hochschuleTrier.fmv.controls.overview;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Shape;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;

import prefuse.Display;
import prefuse.controls.Control;
import prefuse.controls.ControlAdapter;
import prefuse.util.ColorLib;
import prefuse.util.display.PaintListener;
import prefuse.util.ui.UILib;

/**
 * Quelle: http://deept.googlecode.com/svn/trunk/prefuse/demos/prefuse/demos2/decorators/OverviewControl.java (Zuletzt abgerufen am 26.04.2013)
 * 
 */
public class OverviewControl extends ControlAdapter implements PaintListener {

	private Point pointClicked;
	private int m_button = Control.LEFT_MOUSE_BUTTON;
	private boolean buttonPressed = false;
	private Display display, overview;
	private Insets insets;

	public OverviewControl(final Display display, final Display overview) {
		this(display, overview, Control.LEFT_MOUSE_BUTTON);
	}

	public OverviewControl(final Display display, final Display overview, final int button) {
		super();

		this.setDisplayAndOverview(display, overview);
		this.m_button = button;
	}

	public void setDisplayAndOverview(final Display display, final Display overview) {
		this.display = display;
		this.overview = overview;
		this.insets = overview.getInsets();

		this.pointClicked = new Point();
	}

	@Override
	public void mousePressed(final MouseEvent e) {
		if (UILib.isButtonPressed(e, this.m_button)) {
			this.buttonPressed = true;

			this.pointClicked = new Point(e.getX(), e.getY());

			this.panDisplayTo(this.pointClicked);
		}
	}

	@Override
	public void mouseReleased(final MouseEvent e) {
		if (e.getButton() == this.m_button) {
			this.buttonPressed = false;

			this.pointClicked = new Point(e.getX(), e.getY());

			this.panDisplayTo(this.pointClicked);
		}
	}

	@Override
	public void mouseDragged(final MouseEvent e) {
		if (this.buttonPressed) {
			this.pointClicked = new Point(e.getX(), e.getY());

			this.panDisplayTo(this.pointClicked);
		}
	}

	private void panDisplayTo(final Point point) {
		final Point to = this.overview2display(point);

		this.display.panTo(to);
		this.display.repaint();
	}

	private Point overview2display(final Point point) {
		try {
			final AffineTransform displayT = this.display.getTransform();
			final AffineTransform overviewT = this.overview.getTransform();

			final Point tPoint = new Point();
			final Point displayPoint = new Point();

			overviewT.inverseTransform(point, tPoint); // Overview -> 1:1
			displayT.transform(tPoint, displayPoint); // 1:1 -> Display

			return displayPoint;
		}
		catch (final NoninvertibleTransformException nitex) {
		}
		return null;
	}

	private Point display2overview(final Point point) {
		Point overviewPoint = null;
		try {
			final AffineTransform displayT = this.display.getTransform();
			final AffineTransform overviewT = this.overview.getTransform();

			final Point tPoint = new Point();
			overviewPoint = new Point();

			displayT.inverseTransform(point, tPoint); // Display -> 1:1
			overviewT.transform(tPoint, overviewPoint); // 1:1 -> Overview
		}
		catch (final NoninvertibleTransformException nitex) {
		}
		return overviewPoint;
	}

	/*
	 * private void fixBounds(Point point) { Point boundsStart = new Point(insets.left, insets.top); Point boundsEnd = new Point(overview.getWidth() - insets.right, overview.getHeight() -
	 * insets.bottom);
	 * 
	 * // Gets rectangle coordinates. Point rectStart = new Point(); Point rectEnd = new Point(); getRectangleCoordinates(display, rectStart, rectEnd);
	 * 
	 * int dx = (rectEnd.x - rectStart.x) / 2; int dy = (rectEnd.y - rectStart.y) / 2;
	 * 
	 * // Check bounds. int fixedX = point.x; int fixedY = point.y;
	 * 
	 * if (rectStart.x < boundsStart.x) { fixedX = boundsStart.x + dx; } if (rectStart.y < boundsStart.y) { fixedY = boundsStart.y + dy; } if (rectEnd.x > boundsEnd.x) { fixedX = boundsEnd.x - dx; }
	 * if (rectEnd.y > boundsEnd.y) { fixedY = boundsEnd.y - dy; }
	 * 
	 * // System.out.println(" >> (" + point.x + "," + point.y + ") ->> (" + fixedX + "," + fixedY + ")");
	 * 
	 * point.setLocation(fixedX, fixedY); }
	 */

	@Override
	public void postPaint(final Display d, final Graphics2D g) {
		this.drawClearRectangle(g);
	}

	@Override
	public void prePaint(final Display d, final Graphics2D g) {
		this.overview.repaint();
	}

	private void getRectangleCoordinates(final Display display, final Point rectangleStart, final Point rectangleEnd) {
		// Gets visible rect...
		final Point tmpStart = display.getVisibleRect().getLocation();

		final Point tmpEnd = display.getVisibleRect().getLocation();
		tmpEnd.translate(display.getVisibleRect().width, display.getVisibleRect().height);

		// ... converts it to the overview coordinates system...
		rectangleStart.setLocation(this.display2overview(tmpStart));
		rectangleEnd.setLocation(this.display2overview(tmpEnd));
	}

	private void drawClearRectangle(final Graphics2D g) {
		// Updates rectangle coordinates...
		final Point rectangleStart = new Point();
		final Point rectangleEnd = new Point();
		this.getRectangleCoordinates(this.display, rectangleStart, rectangleEnd);

		// Prepares drawing area.
		final Color before = g.getColor();
		final Shape clip = g.getClip();

		g.setClip(this.insets.left, this.insets.top, this.overview.getWidth() - this.insets.left - this.insets.right, this.overview.getHeight() - this.insets.bottom - this.insets.top);
		g.setColor(new Color(ColorLib.hex("#4682B47A"), true));

		// ...and draws it:
		// Top.
		g.fillRect(this.insets.left, this.insets.top, this.overview.getWidth() - this.insets.left - this.insets.right, rectangleStart.y - this.insets.top);

		// Bottom.
		g.fillRect(this.insets.left, rectangleEnd.y, this.overview.getWidth() - this.insets.left - this.insets.right, this.overview.getHeight() - this.insets.bottom - rectangleEnd.y);

		// Left.
		g.fillRect(this.insets.left, rectangleStart.y, rectangleStart.x - this.insets.left, rectangleEnd.y - rectangleStart.y);

		// Right.
		g.fillRect(rectangleEnd.x, rectangleStart.y, this.overview.getWidth() - rectangleEnd.x - this.insets.right, rectangleEnd.y - rectangleStart.y);

		// Draws the rectangle.
		g.setColor(new Color(ColorLib.hex("#A6A8FA"), true));
		g.drawRect(rectangleStart.x, rectangleStart.y, rectangleEnd.x - rectangleStart.x, rectangleEnd.y - rectangleStart.y);

		// ... and a cross.
		//		this.drawCross(g, rectangleStart, rectangleEnd);

		// Restore drawing area.
		g.setColor(before);
		g.setClip(clip);
	}

	//	private void drawCross(final Graphics g, final Point start, final Point end) {
	//		final Color anterior = g.getColor();
	//		g.setColor(Color.RED);
	//
	//		g.drawLine(start.x, start.y, end.x, end.y);
	//		g.drawLine(start.x, end.y, end.x, start.y);
	//
	//		g.setColor(anterior);
	//	}

}
