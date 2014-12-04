package de.hochschuleTrier.fmv.stategies.mousewheel;

import prefuse.Display;
import de.hochschuleTrier.fmv.controls.DefaultWheelZoomControl;
import de.hochschuleTrier.fmv.exceptions.StrategyActionFailedException;
import de.hochschuleTrier.fmv.model.impl.ApplicationModel;

public class MouseWheelZoomStrategy extends DefaultWheelZoomControl implements IMouseWheelStrategy {

	@Override
	public void performMouseWheelMoved(final int wheelRotation) throws StrategyActionFailedException {
		double zoomScale;
		if (wheelRotation < 0) {
			zoomScale = 1.1;
		}
		else {
			zoomScale = 0.9;
		}
		final Display display = ApplicationModel.getInstance().getCurrentFeatureTreeModel().getDisplay();
		super.zoom(display, zoomScale);
	}

}
