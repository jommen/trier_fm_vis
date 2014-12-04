package de.hochschuleTrier.fmv.stategies.mousewheel;

import de.hochschuleTrier.fmv.exceptions.StrategyActionFailedException;

public interface IMouseWheelStrategy {
	void performMouseWheelMoved(int wheelRotation) throws StrategyActionFailedException;
}
