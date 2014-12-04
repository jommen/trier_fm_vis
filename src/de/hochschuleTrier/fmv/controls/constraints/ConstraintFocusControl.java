package de.hochschuleTrier.fmv.controls.constraints;

import java.awt.event.MouseEvent;

import prefuse.Visualization;
import prefuse.controls.FocusControl;
import prefuse.data.tuple.TupleSet;
import prefuse.util.ui.UILib;
import prefuse.visual.VisualItem;

/**
 * Standard focus control with restriction to focus on only one item at the same
 * time
 * 
 */
public class ConstraintFocusControl extends FocusControl {

	private final String group = Visualization.FOCUS_ITEMS;
	private Visualization visualization;
	private String animateAction = "animatePaint";

	/**
	 * @see FocusControl#FocusControl()
	 * @param focusGroup
	 */
	public ConstraintFocusControl() {
		super();
	}

	/**
	 * @see FocusControl#FocusControl(int, String)
	 * @param focusGroup
	 */
	public ConstraintFocusControl(final int clicks, final String act) {
		super(clicks, act);
	}

	/**
	 * @see FocusControl#FocusControl(int)
	 * @param focusGroup
	 */
	public ConstraintFocusControl(final int clicks) {
		super(clicks);
	}

	/**
	 * @see FocusControl#FocusControl(String, int, String)
	 * @param focusGroup
	 */
	public ConstraintFocusControl(final String focusGroup, final int clicks, final String act) {
		super(focusGroup, clicks, act);
	}

	/**
	 * @see FocusControl#FocusControl(String, int)
	 * @param focusGroup
	 */
	public ConstraintFocusControl(final String focusGroup, final int clicks) {
		super(focusGroup, clicks);
	}

	/**
	 * @see FocusControl#FocusControl(String)
	 * @param focusGroup
	 */
	public ConstraintFocusControl(final String focusGroup) {
		super(focusGroup);
	}

	/**
	 * @see prefuse.controls.Control#itemClicked(prefuse.visual.VisualItem, java.awt.event.MouseEvent)
	 */
	@Override
	public void itemClicked(final VisualItem item, final MouseEvent e) {
		if (!this.filterCheck(item)) {
			return;
		}
		if (UILib.isButtonPressed(e, this.button) && e.getClickCount() == this.ccount) {
			final Visualization vis = item.getVisualization();
			this.visualization = vis;
			final TupleSet ts = vis.getFocusGroup(this.group);
			ts.clear();
			ts.addTuple(item);
			this.runActivity(vis);
		}
	}

	@Override
	public void mouseClicked(final MouseEvent e) {
		if (UILib.isButtonPressed(e, this.button) && e.getClickCount() == this.ccount && this.visualization != null) {
			final TupleSet ts = this.visualization.getFocusGroup(this.group);
			ts.clear();
			this.runActivity(this.visualization);
		}
	}

	private void runActivity(final Visualization vis) {
		if (this.activity != null) {
			vis.run(this.activity);
			vis.run(this.animateAction);
		}
	}

	public void setAnimateAction(final String animateAction) {
		if (animateAction != null && !animateAction.equals("")) {
			this.animateAction = animateAction;
		}
	}

}
