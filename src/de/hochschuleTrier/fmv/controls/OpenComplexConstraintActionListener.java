package de.hochschuleTrier.fmv.controls;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import de.hochschuleTrier.fmv.model.impl.ApplicationModel;
import de.hochschuleTrier.fmv.model.impl.complexConstraints.ComplexConstraintModel;
import de.hochschuleTrier.fmv.model.interfaces.complexConstraints.IComplexConstraintGroup;
import de.hochschuleTrier.fmv.model.interfaces.complexConstraints.IComplexConstraintModel;

public class OpenComplexConstraintActionListener implements ActionListener {

	private final List<IComplexConstraintGroup> complexConstraints;

	public OpenComplexConstraintActionListener(final List<IComplexConstraintGroup> complexConstraints) {
		this.complexConstraints = complexConstraints;
	}

	@Override
	public void actionPerformed(final ActionEvent e) {
		final ApplicationModel appVisModel = ApplicationModel.getInstance();
		final IComplexConstraintModel model = new ComplexConstraintModel(this.complexConstraints);
		appVisModel.openComplexConstraintModel(model);
	}

}
