package de.hochschuleTrier.fmv.stategies.mousewheel;

import de.hochschuleTrier.fmv.exceptions.StrategyActionFailedException;
import de.hochschuleTrier.fmv.model.impl.UISettings;

public class MouseWheelFisheyeStrategy implements IMouseWheelStrategy {

	@Override
	public void performMouseWheelMoved(final int wheelRotation) throws StrategyActionFailedException {
		if (wheelRotation > 0) {
			UISettings.getInstance().incrementFisheyeDistance();
		}
		else {
			UISettings.getInstance().decrementFisheyeDistance();
		}
	}

}
