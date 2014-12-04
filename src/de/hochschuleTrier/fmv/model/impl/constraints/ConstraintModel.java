package de.hochschuleTrier.fmv.model.impl.constraints;

import prefuse.data.Graph;
import prefuse.data.io.DataIOException;
import prefuse.data.io.GraphMLReader;
import de.hochschuleTrier.fmv.exceptions.ReadMLDataFileFailedExceptions;
import de.hochschuleTrier.fmv.model.interfaces.constraints.IConstraintModel;
import de.hochschuleTrier.fmv.view.display.ConstraintDisplay;

public class ConstraintModel implements IConstraintModel {

	private Graph constraintGraph;
	private ConstraintDisplay display;
	private ConstraintNodesToInspectInspectListModel nodesToInspectListModel;
	private boolean inspectFilter;

	public ConstraintModel(final String graphMLDataFile) throws ReadMLDataFileFailedExceptions {
		try {
			this.constraintGraph = new GraphMLReader().readGraph(graphMLDataFile);
			this.constraintGraph.getNodeTable().addColumns(new ConstraintNodeSchema());
			this.constraintGraph.getEdgeTable().addColumns(new ConstraintEdgeSchema());
		}
		catch (final DataIOException e) {
			throw new ReadMLDataFileFailedExceptions(e);
		}
	}

	public ConstraintModel(final Graph graph) {
		this.constraintGraph = graph;
	}

	@Override
	public synchronized Graph getConstraintGraph() {
		return this.constraintGraph;
	}

	@Override
	public synchronized ConstraintDisplay getDisplay() {
		return this.display;
	}

	@Override
	public synchronized void setDisplay(final ConstraintDisplay display) {
		this.display = display;
	}

	@Override
	public ConstraintNodesToInspectInspectListModel getNodesToInspectListModel() {
		return this.nodesToInspectListModel;
	}

	@Override
	public void setNodesToInspectListModel(final ConstraintNodesToInspectInspectListModel nodesToInspectListModel) {
		this.nodesToInspectListModel = nodesToInspectListModel;
	}

	@Override
	public void setInspectFilter(final boolean inspectFilter) {
		this.inspectFilter = inspectFilter;
	}

	@Override
	public boolean isInspectFilter() {
		return this.inspectFilter;
	}

}
