package de.hochschuleTrier.fmv.io.proofer;

import java.util.List;

import de.hochschuleTrier.fmv.model.impl.ApplicationModel;
import de.hochschuleTrier.fmv.util.NodeLib;

public class DeadFeatureDFGProofExporter extends DFGProofExporter {

	@Override
	protected List<String> buildFormulae() {
		final String featureName = NodeLib.getName(ApplicationModel.getInstance().getSelectedNode());
		final List<String> formula = super.buildFormulae();
		formula.add("formula(" + featureName + ").");
		return formula;
	}

}
