package de.hochschuleTrier.fmv.view.render;

import java.awt.Component;

import javax.swing.ImageIcon;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;

import prefuse.data.Node;
import de.hochschuleTrier.fmv.model.impl.FeatureSchema;
import de.hochschuleTrier.fmv.model.impl.featureDiagram.FeatureDiagramSchema;
import de.hochschuleTrier.fmv.util.NodeLib;

public class TreePanelCellRenderer extends DefaultTreeCellRenderer {

	@Override
	public Component getTreeCellRendererComponent(final JTree tree, final Object value, final boolean selected, final boolean expanded, final boolean leaf, final int row, final boolean hasFocus) {
		final Component component = super.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);

		if (value instanceof Node) {
			final Node node = (Node) value;
			this.setText(node.getString(0));
			this.setIcon(this.getImageIcon(node, leaf));
		}

		return component;
	}

	private ImageIcon getImageIcon(final Node node, final boolean leaf) {
		final String folder = "/images/";

		if (leaf) {
			final boolean isOptional = NodeLib.isOptional(node);
			if (isOptional) {
				return new ImageIcon(this.getClass().getResource(folder + "circle_unfilled.png"));
			}
			else {
				return new ImageIcon(this.getClass().getResource(folder + "circle_filled.png"));
			}
		}
		else {
			final String type = node.getString(FeatureSchema.GROUPTYPE);
			if (type != null && type.equals(FeatureDiagramSchema.GROUPTYPE_ALTERNATIVE)) {
				return new ImageIcon(this.getClass().getResource(folder + "alternative.png"));
			}
			else if (type != null && type.equals(FeatureDiagramSchema.GROUPTYPE_OR)) {
				return new ImageIcon(this.getClass().getResource(folder + "or.png"));
			}
			else {
				return new ImageIcon(this.getClass().getResource(folder + "featureGroup.png"));
			}
		}
	}
}
