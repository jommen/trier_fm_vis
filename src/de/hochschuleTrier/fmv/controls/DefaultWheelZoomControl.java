package de.hochschuleTrier.fmv.controls;

import java.awt.event.MouseWheelEvent;

import prefuse.Display;
import prefuse.controls.WheelZoomControl;
import de.hochschuleTrier.fmv.util.FMDisplayLib;

public class DefaultWheelZoomControl extends WheelZoomControl {
	@Override
	public void mouseWheelMoved(final MouseWheelEvent e) {
		final Display display = (Display) e.getComponent();
		double zoomScale = 0.9;
		if (e.getWheelRotation() < 0) {
			zoomScale = 1.1;
		}

		this.zoom(display, zoomScale);
	}

	protected void zoom(final Display display, final double zoomScale) {
		FMDisplayLib.zoom(display, zoomScale);
	}
}
