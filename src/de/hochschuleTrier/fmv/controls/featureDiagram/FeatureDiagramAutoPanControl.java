package de.hochschuleTrier.fmv.controls.featureDiagram;

import java.awt.geom.Point2D;

import prefuse.Display;
import prefuse.Visualization;
import prefuse.action.Action;
import prefuse.data.tuple.TupleSet;
import prefuse.visual.VisualItem;
import de.hochschuleTrier.fmv.model.impl.UISettings;

public class FeatureDiagramAutoPanControl extends Action {

	private final Point2D m_start = new Point2D.Double();
	private final Point2D m_end = new Point2D.Double();
	private final Point2D m_cur = new Point2D.Double();
	private final int m_bias = 0;
	private final Display display;
	private boolean firstCall = true;

	public FeatureDiagramAutoPanControl(final Display display) {
		this.display = display;
	}

	@Override
	public void run(final double frac) {

		if (UISettings.getInstance().isCenterSelectedNode()) {
			final double scale = 1;// m_vis.getDisplay(0).getScale();

			final TupleSet ts = this.m_vis.getFocusGroup(Visualization.FOCUS_ITEMS);
			if (ts.getTupleCount() == 0) {
				return;
			}

			// Hier darf er nur das erste Mal hineinspringen
			if (this.firstCall) {
				this.firstCall = false;
				int xbias = 0;
				final int ybias = 0;
				xbias = this.m_bias;

				final VisualItem vi = (VisualItem) ts.tuples().next();
				this.m_cur.setLocation((this.display.getWidth() / 2) / scale, (this.display.getHeight() / 2) / scale);
				this.display.getAbsoluteCoordinate(this.m_cur, this.m_start);
				this.m_end.setLocation(vi.getX() + xbias, vi.getY() + ybias);
			}
			else {
				this.m_cur.setLocation(this.m_start.getX() + frac * (this.m_end.getX() - this.m_start.getX()), this.m_start.getY() + frac * (this.m_end.getY() - this.m_start.getY()));
				this.display.panToAbs(this.m_cur);
			}
			// frac == 1 ist der letzte Aufruf der Methode
			if (frac == 1.0) {
				this.firstCall = true;
			}
		}

	}
}
