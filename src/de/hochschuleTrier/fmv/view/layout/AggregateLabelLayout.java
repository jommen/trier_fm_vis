package de.hochschuleTrier.fmv.view.layout;

import java.awt.geom.Rectangle2D;
import java.util.Iterator;

import prefuse.action.layout.Layout;
import prefuse.visual.DecoratorItem;
import prefuse.visual.VisualItem;

/**
 * Set label positions. Labels are assumed to be DecoratorItem instances, decorating their respective nodes. The layout simply gets the bounds of the decorated node and assigns the label coordinates
 * to the center of those bounds.
 */
public class AggregateLabelLayout extends Layout {
	public AggregateLabelLayout(final String group) {
		super(group);
	}

	@Override
	public void run(final double frac) {
		final Iterator iter = this.m_vis.items(this.m_group);
		while (iter.hasNext()) {
			final DecoratorItem decorator = (DecoratorItem) iter.next();
			final VisualItem decoratedItem = decorator.getDecoratedItem();
			final Rectangle2D bounds = decoratedItem.getBounds();

			final double x = bounds.getCenterX();
			final double y = bounds.getCenterY();

			this.setX(decorator, null, x);
			this.setY(decorator, null, y);
		}
	}
} // end of inner class LabelLayout