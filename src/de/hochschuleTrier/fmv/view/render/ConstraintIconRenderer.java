package de.hochschuleTrier.fmv.view.render;

import java.awt.Image;

import prefuse.Constants;
import prefuse.render.ImageFactory;
import prefuse.render.LabelRenderer;
import prefuse.visual.VisualItem;

public class ConstraintIconRenderer extends LabelRenderer {

	public ConstraintIconRenderer() {
		this.initImageFactory();
	}

	private void initImageFactory() {
		this.m_images = new ImageFactory();
		this.setImagePosition(Constants.RIGHT);
	}

	@Override
	protected Image getImage(final VisualItem item) {
		return this.m_images.getImage("/images/open_complex_constraint.png");
	}

}
