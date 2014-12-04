package de.hochschuleTrier.fmv.controls.featureDiagram;

import java.awt.event.MouseWheelEvent;

import prefuse.controls.WheelZoomControl;
import de.hochschuleTrier.fmv.exceptions.StrategyActionFailedException;
import de.hochschuleTrier.fmv.model.impl.UISettings;
import de.hochschuleTrier.fmv.stategies.mousewheel.IMouseWheelStrategy;

public class FeatureDiagramMouseWheelControl extends WheelZoomControl {

	@Override
	public void mouseWheelMoved(final MouseWheelEvent e) {
		try {
			final int wheelRotation = e.getWheelRotation();
			final IMouseWheelStrategy strategy = UISettings.getInstance().getMouseWheelStrategy();
			strategy.performMouseWheelMoved(wheelRotation);
		}
		catch (final StrategyActionFailedException e1) {
			e1.printStackTrace();
		}

	}
}
