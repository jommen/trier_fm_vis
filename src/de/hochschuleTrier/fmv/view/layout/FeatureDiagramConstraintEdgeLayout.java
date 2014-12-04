package de.hochschuleTrier.fmv.view.layout;

import java.util.Iterator;

import prefuse.action.layout.Layout;
import prefuse.util.PrefuseLib;
import prefuse.visual.DecoratorItem;
import prefuse.visual.NodeItem;
import de.hochschuleTrier.fmv.model.impl.featureDiagram.FeatureDiagramConstraintDecoratorSchema;
import de.hochschuleTrier.fmv.model.interfaces.featureDiagram.IFeatureDiagramConstraintEdge;
import de.hochschuleTrier.fmv.model.interfaces.featureDiagram.IFeatureDiagramModel;
import de.hochschuleTrier.fmv.util.NodeLib;

public class FeatureDiagramConstraintEdgeLayout extends Layout {

	private final IFeatureDiagramModel model;

	public FeatureDiagramConstraintEdgeLayout(final String group, final IFeatureDiagramModel model) {
		super(group);
		this.model = model;
	}

	@Override
	public void run(final double frac) {
		final Iterator iter = this.m_vis.items(this.getGroup());
		while (iter.hasNext()) {
			final DecoratorItem decorator = (DecoratorItem) iter.next();
			final NodeItem decoratedItem = (NodeItem) decorator.getDecoratedItem();

			PrefuseLib.updateVisible(decorator, false);
			if (this.model.isInInspectingNodeMode() && !NodeLib.isEqual(decoratedItem, this.model.getInspectConstraintNode())) {
				for (final IFeatureDiagramConstraintEdge edge : this.model.getInspectConstraintNodeEdges()) {
					if (NodeLib.isEqual(edge.getSourceNode(), decoratedItem) || NodeLib.isEqual(edge.getTargetNode(), decoratedItem)) {
						PrefuseLib.updateVisible(decorator, true);

						decorator.set(FeatureDiagramConstraintDecoratorSchema.FROM_NODE, edge.getSourceNode());
						decorator.set(FeatureDiagramConstraintDecoratorSchema.TO_NODE, edge.getTargetNode());
						decorator.setString(FeatureDiagramConstraintDecoratorSchema.CONSTRAINT_TYPE, edge.getEdgeType());
					}
				}
			}
		}
	}
}
