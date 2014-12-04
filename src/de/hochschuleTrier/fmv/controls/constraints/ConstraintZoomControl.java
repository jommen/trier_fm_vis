package de.hochschuleTrier.fmv.controls.constraints;

import java.awt.event.MouseWheelEvent;

import prefuse.Display;
import prefuse.Visualization;
import de.hochschuleTrier.fmv.controls.DefaultWheelZoomControl;
import de.hochschuleTrier.fmv.model.impl.UISettings;

public class ConstraintZoomControl extends DefaultWheelZoomControl {

	// prüfen, ob Knoten selektiert und entsprechende Aktion ausführen
	@Override
	public void mouseWheelMoved(final MouseWheelEvent e) {
		final Display display = (Display) e.getComponent();
		if (display.getVisualization().getFocusGroup(Visualization.FOCUS_ITEMS).getTupleCount() > 0 && e.isControlDown()) {
			if (e.getWheelRotation() < 0) {
				UISettings.getInstance().decrementConstraintFocusDistance();
			}
			else {
				UISettings.getInstance().incrementConstraintFocusDistance();
			}
		}
		else {
			super.mouseWheelMoved(e);
		}
	}

}
