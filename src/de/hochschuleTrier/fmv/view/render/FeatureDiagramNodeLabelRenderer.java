package de.hochschuleTrier.fmv.view.render;

import java.awt.Image;

import prefuse.Constants;
import prefuse.data.tuple.TableNode;
import prefuse.render.ImageFactory;
import prefuse.render.LabelRenderer;
import prefuse.visual.VisualItem;
import prefuse.visual.tuple.TableNodeItem;
import de.hochschuleTrier.fmv.model.impl.FeatureSchema;
import de.hochschuleTrier.fmv.model.impl.featureDiagram.FeatureDiagramSchema;

public class FeatureDiagramNodeLabelRenderer extends LabelRenderer {

	public FeatureDiagramNodeLabelRenderer() {
		super();
		this.initImageFactory();
	}

	public FeatureDiagramNodeLabelRenderer(final String textField) {
		super(textField);
		this.initImageFactory();
	}

	private void initImageFactory() {
		this.m_images = new ImageFactory();
		this.setImagePosition(Constants.RIGHT);
	}

	@Override
	protected Image getImage(final VisualItem item) {
		final TableNodeItem fmItem = (TableNodeItem) item;
		final TableNode nodeItem = (TableNode) item.getSourceTuple();
		final String folder = "/images/";

		if (nodeItem.children().hasNext() && !fmItem.isExpanded()) {
			final String type = fmItem.getString(FeatureSchema.GROUPTYPE);
			if (type != null && type.equals(FeatureDiagramSchema.GROUPTYPE_ALTERNATIVE)) {
				return this.m_images.getImage(folder + "alternative_horizontal.png");
			}
			else if (type != null && type.equals(FeatureDiagramSchema.GROUPTYPE_OR)) {
				return this.m_images.getImage(folder + "or_horizontal.png");
			}
			else {
				return this.m_images.getImage(folder + "featureGroup_horizontal.png");
			}
		}
		return null;
	}

}
