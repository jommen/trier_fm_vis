package de.hochschuleTrier.fmv.model.interfaces.listener;

import de.hochschuleTrier.fmv.model.interfaces.complexConstraints.IComplexConstraintModel;
import de.hochschuleTrier.fmv.model.interfaces.constraints.IConstraintModel;
import de.hochschuleTrier.fmv.model.interfaces.featureDiagram.IFeatureDiagramModel;

public interface INewFeatureModelListener {
	void newFeatueTreeModelCreated(IFeatureDiagramModel currentFeatureTreeModel, IConstraintModel newConstraintModel, String featureModelName);

	void newSubtreeCreated(IFeatureDiagramModel featureTreeModel);

	void newComplexConstraintView(IComplexConstraintModel model);
}
