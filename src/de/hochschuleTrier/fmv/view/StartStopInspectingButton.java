package de.hochschuleTrier.fmv.view;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JToggleButton;
import javax.swing.ListModel;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

import prefuse.Visualization;
import de.hochschuleTrier.fmv.model.impl.ApplicationModel;
import de.hochschuleTrier.fmv.model.interfaces.constraints.IConstraintModel;

public class StartStopInspectingButton extends JToggleButton implements ItemListener, ListDataListener {

	private final static String START_LABEL = "Start inspecting..";
	private final static String STOP_LABEL = "Stop inspecting..";

	private final IConstraintModel constraintModel;
	private final ListModel<String> listModel;

	public StartStopInspectingButton(final IConstraintModel constraintModel, final ListModel<String> listModel) {
		super(START_LABEL);
		this.constraintModel = constraintModel;
		this.listModel = listModel;
		this.listModel.addListDataListener(this);
		this.addItemListener(this);

		this.setEnabled(false);
	}

	@Override
	public void itemStateChanged(final ItemEvent event) {
		final Visualization visualization = this.constraintModel.getDisplay().getVisualization();
		final Visualization featureTreeVisualization = ApplicationModel.getInstance().getCurrentFeatureTreeModel().getDisplay().getVisualization();
		if (event.getStateChange() == ItemEvent.SELECTED) {
			this.constraintModel.setInspectFilter(true);
			this.setText(STOP_LABEL);
			visualization.run("inspectFilter");
		}
		else {
			this.constraintModel.setInspectFilter(false);
			this.setText(START_LABEL);
			visualization.run("filter");
		}
		visualization.run("recolor");
		featureTreeVisualization.run("filter");
	}

	@Override
	public void contentsChanged(final ListDataEvent event) {
		this.enableOrDisable();
	}

	@Override
	public void intervalAdded(final ListDataEvent arg0) {
		this.enableOrDisable();
	}

	@Override
	public void intervalRemoved(final ListDataEvent arg0) {
		this.enableOrDisable();
	}

	private void enableOrDisable() {
		if (this.listModel.getSize() > 1) {
			this.setEnabled(true);
		}
		else {
			this.setEnabled(false);
		}
	}

}
