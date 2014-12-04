package de.hochschuleTrier.fmv.io.proofer;

import prefuse.data.Graph;
import prefuse.data.Tree;
import de.hochschuleTrier.fmv.model.interfaces.complexConstraints.IComplexConstraints;

public abstract class AbstractProofExporter implements IProofExporter {

	private Tree featureModel;
	private Graph crossTreeConstraintGraph;
	private IComplexConstraints complexConstraints;

	public Tree getFeatureModel() {
		return this.featureModel;
	}

	public void setFeatureModel(final Tree featureModel) {
		this.featureModel = featureModel;
	}

	public Graph getCrossTreeConstraintGraph() {
		return this.crossTreeConstraintGraph;
	}

	public void setCrossTreeConstraintGraph(final Graph crossTreeConstraintGraph) {
		this.crossTreeConstraintGraph = crossTreeConstraintGraph;
	}

	public IComplexConstraints getComplexConstraints() {
		return this.complexConstraints;
	}

	public void setComplexConstraints(final IComplexConstraints complexConstraints) {
		this.complexConstraints = complexConstraints;
	}

}
