package de.hochschuleTrier.fmv.model.interfaces.constraints;

import prefuse.data.Graph;
import de.hochschuleTrier.fmv.model.impl.constraints.ConstraintNodesToInspectInspectListModel;
import de.hochschuleTrier.fmv.view.display.ConstraintDisplay;

public interface IConstraintModel {
	public Graph getConstraintGraph();

	public ConstraintDisplay getDisplay();

	public void setDisplay(final ConstraintDisplay display);

	public ConstraintNodesToInspectInspectListModel getNodesToInspectListModel();

	public void setNodesToInspectListModel(final ConstraintNodesToInspectInspectListModel nodesToInspectListModel);

	public void setInspectFilter(boolean b);

	public boolean isInspectFilter();
}
