package de.hochschuleTrier.fmv.view.layout;

import java.awt.geom.Rectangle2D;
import java.util.Iterator;
import java.util.List;

import prefuse.action.layout.Layout;
import prefuse.util.PrefuseLib;
import prefuse.visual.DecoratorItem;
import prefuse.visual.VisualItem;
import de.hochschuleTrier.fmv.model.impl.ApplicationModel;
import de.hochschuleTrier.fmv.model.impl.UISettings;
import de.hochschuleTrier.fmv.model.interfaces.complexConstraints.IComplexConstraintGroup;

public class FeatureDiagramComplexConstraintIconLayout extends Layout {
	public FeatureDiagramComplexConstraintIconLayout(final String group) {
		super(group);
	}

	@Override
	public void run(final double frac) {
		final Iterator iter = this.m_vis.items(this.getGroup());
		while (iter.hasNext()) {
			final DecoratorItem decorator = (DecoratorItem) iter.next();
			final VisualItem decoratedItem = decorator.getDecoratedItem();

			final List<IComplexConstraintGroup> complexConstraints = ApplicationModel.getInstance().getComplexConstraints().get(decoratedItem.getString("name"));
			if (UISettings.getInstance().isShowComplexConstraintIconInFeatureDiagram() && !complexConstraints.isEmpty() && decoratedItem.isVisible()) {
				PrefuseLib.updateVisible(decorator, true);
				final Rectangle2D bounds = decoratedItem.getBounds();

				final double x = decoratedItem.getX() + bounds.getWidth();
				final double y = decoratedItem.getY() - (bounds.getHeight() / 2.0);

				this.setX(decorator, null, x);
				this.setY(decorator, null, y);
			}
			else {
				PrefuseLib.updateVisible(decorator, false);
			}
		}
	}
}
