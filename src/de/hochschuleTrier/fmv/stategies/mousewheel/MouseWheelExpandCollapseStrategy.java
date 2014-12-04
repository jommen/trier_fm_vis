package de.hochschuleTrier.fmv.stategies.mousewheel;

import de.hochschuleTrier.fmv.exceptions.StrategyActionFailedException;
import de.hochschuleTrier.fmv.model.impl.ApplicationModel;

public class MouseWheelExpandCollapseStrategy implements IMouseWheelStrategy {

	@Override
	public void performMouseWheelMoved(final int wheelRotation) throws StrategyActionFailedException {
		final ApplicationModel model = ApplicationModel.getInstance();
		if (model.isAnyNodeSelected()) {
			if (wheelRotation < 0) {
				model.expandTree();
			}
			else {
				model.collapseTree();
			}
			model.updateCollapsedAndExpandedNodes();
		}
	}

}
