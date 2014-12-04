package de.hochschuleTrier.fmv.model.impl;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BoundedRangeModel;

import prefuse.Visualization;
import de.hochschuleTrier.fmv.model.interfaces.listener.IUISettingsListener;
import de.hochschuleTrier.fmv.stategies.mousewheel.IMouseWheelStrategy;
import de.hochschuleTrier.fmv.stategies.mousewheel.MouseWheelZoomStrategy;
import de.hochschuleTrier.fmv.util.UIConstants;

public class UISettings {
	private static UISettings instance;

	private final List<IUISettingsListener> listener;

	private IMouseWheelStrategy mouseWheelStrategy;
	private Color selectedColor;
	private boolean centerSelectedNode;
	private boolean fisheyeZoomEnabled;
	private BoundedRangeModel fisheyeDistanceModel;
	private boolean showToolTip;
	private boolean showComplexConstraintIconInFeatureDiagram;

	private int constraintFocusDistance;

	public synchronized static UISettings getInstance() {
		if (UISettings.instance == null) {
			UISettings.instance = new UISettings();
		}
		return UISettings.instance;
	}

	private UISettings() {
		this.listener = new ArrayList<IUISettingsListener>();
		this.showToolTip = UIConstants.DEFAULT_SHOW_TOOLTIP;
		this.setMouseWheelStrategy(new MouseWheelZoomStrategy());
	}

	public synchronized IMouseWheelStrategy getMouseWheelStrategy() {
		return this.mouseWheelStrategy;
	}

	public void setMouseWheelStrategy(final IMouseWheelStrategy mouseWheelStrategy) {
		this.mouseWheelStrategy = mouseWheelStrategy;
	}

	public synchronized boolean isCenterSelectedNode() {
		return this.centerSelectedNode;
	}

	public synchronized void setCenterSelectedNode(final boolean centerSelectedNode) {
		this.centerSelectedNode = centerSelectedNode;
	}

	public synchronized Color getSelectedColor() {
		return this.selectedColor;
	}

	public synchronized void setSelectedColor(final Color selectedColor) {
		this.selectedColor = selectedColor;
	}

	public synchronized boolean isFisheyeZoomEnabled() {
		return this.fisheyeZoomEnabled;
	}

	public synchronized void setFisheyeZoomEnabled(final boolean fisheyeZoomEnabled) {
		this.fisheyeZoomEnabled = fisheyeZoomEnabled;
	}

	public synchronized void setFisheyeDistanceModel(final BoundedRangeModel model) {
		this.fisheyeDistanceModel = model;
	}

	public synchronized int getFisheyeDistance() {
		return this.fisheyeDistanceModel.getValue();
	}

	public synchronized void setFisheyeDistance(final int distance) {
		this.fisheyeDistanceModel.setValue(distance);
		this.fireFisheyeDistanceChanged();
	}

	public synchronized void incrementFisheyeDistance() {
		this.setFisheyeDistance(this.getFisheyeDistance() + 1);
	}

	public synchronized void decrementFisheyeDistance() {
		this.setFisheyeDistance(this.getFisheyeDistance() - 1);
	}

	public void addListener(final IUISettingsListener l) {
		this.listener.add(l);
	}

	public void removeListener(final IUISettingsListener l) {
		this.listener.remove(l);
	}

	private void fireFisheyeDistanceChanged() {
		final int distance = this.getFisheyeDistance();
		for (final IUISettingsListener l : this.listener) {
			l.fisheyeDistanceChanged(distance);
		}
	}

	private void fireConstraintFocusDistanceChanged() {
		for (final IUISettingsListener l : this.listener) {
			l.constraintFocusDistanceChanged(this.constraintFocusDistance);
		}
	}

	public void setShowToolTip(final boolean enabled) {
		this.showToolTip = enabled;
	}

	public boolean isShowToolTip() {
		return this.showToolTip;
	}

	public synchronized void setConstraintFocusDistance(final int newDistance) {
		if (newDistance <= UIConstants.MAX_CONSTRAINT_FOCUS_DISTANCE && newDistance >= UIConstants.MIN_CONSTRAINT_FOCUS_DISTANCE) {
			this.constraintFocusDistance = newDistance;
		}
		this.fireConstraintFocusDistanceChanged();
	}

	public synchronized void decrementConstraintFocusDistance() {
		if (this.constraintFocusDistance > UIConstants.MIN_CONSTRAINT_FOCUS_DISTANCE) {
			this.constraintFocusDistance--;
		}
		this.fireConstraintFocusDistanceChanged();
	}

	public synchronized void incrementConstraintFocusDistance() {
		if (this.constraintFocusDistance < UIConstants.MAX_CONSTRAINT_FOCUS_DISTANCE) {
			this.constraintFocusDistance++;
		}
		this.fireConstraintFocusDistanceChanged();
	}

	public boolean isShowComplexConstraintIconInFeatureDiagram() {
		return this.showComplexConstraintIconInFeatureDiagram;
	}

	public void setShowComplexConstraintIconInFeatureDiagram(final boolean showComplexConstraintIconInFeatureDiagram) {
		this.showComplexConstraintIconInFeatureDiagram = showComplexConstraintIconInFeatureDiagram;
		final Visualization vis = ApplicationModel.getInstance().getCurrentFeatureTreeModel().getDisplay().getVisualization();
		vis.run("iconLayout");
		vis.run("repaint");
	}

}
