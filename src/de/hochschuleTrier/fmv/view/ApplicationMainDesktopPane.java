package de.hochschuleTrier.fmv.view;

import java.beans.PropertyVetoException;

import javax.swing.JDesktopPane;

import de.hochschuleTrier.fmv.model.impl.ApplicationModel;
import de.hochschuleTrier.fmv.model.interfaces.complexConstraints.IComplexConstraintModel;
import de.hochschuleTrier.fmv.model.interfaces.constraints.IConstraintModel;
import de.hochschuleTrier.fmv.model.interfaces.featureDiagram.IFeatureDiagramModel;
import de.hochschuleTrier.fmv.model.interfaces.listener.INewFeatureModelListener;

public class ApplicationMainDesktopPane extends JDesktopPane implements INewFeatureModelListener {

	public ApplicationMainDesktopPane() {
		this.setDragMode(JDesktopPane.OUTLINE_DRAG_MODE);
		ApplicationModel.getInstance().addNewFeatureModelListener(this);
	}

	@Override
	public void newFeatueTreeModelCreated(final IFeatureDiagramModel newFeatureTreeModel, final IConstraintModel newConstraintModel, final String featureModelName) {
		final ConstraintInternalFrame constraintDiagram = new ConstraintInternalFrame(newConstraintModel);
		constraintDiagram.setTitle(featureModelName + " - Constraints");
		constraintDiagram.setVisible(true);
		this.add(constraintDiagram);

		constraintDiagram.invalidate();
		constraintDiagram.repaint();

		final FeatureDiagramInternalFrame featureDiagram = this.getNewFDInternalFrame(newFeatureTreeModel, featureModelName);
		this.add(featureDiagram);
		try {
			featureDiagram.setSelected(true);
		}
		catch (final PropertyVetoException e) {
		}
	}

	@Override
	public void newSubtreeCreated(final IFeatureDiagramModel featureTreeModel) {
		final FeatureDiagramInternalFrame featureDiagram = this.getNewFDInternalFrame(featureTreeModel, "Teilbaum", false);
		this.add(featureDiagram);
		try {
			featureDiagram.setSelected(true);
		}
		catch (final PropertyVetoException e) {
		}
	}

	private FeatureDiagramInternalFrame getNewFDInternalFrame(final IFeatureDiagramModel featureTreeModel, final String title) {
		return this.getNewFDInternalFrame(featureTreeModel, title, true);
	}

	private FeatureDiagramInternalFrame getNewFDInternalFrame(final IFeatureDiagramModel featureTreeModel, final String title, final boolean mainFrame) {
		final FeatureDiagramInternalFrame featureDiagram = new FeatureDiagramInternalFrame(featureTreeModel, mainFrame);
		featureDiagram.setSize(800, 500);
		featureDiagram.setTitle(title);
		featureDiagram.setLocation(25, 25);
		featureDiagram.setVisible(true);
		return featureDiagram;
	}

	@Override
	public void newComplexConstraintView(final IComplexConstraintModel model) {
		final ComplexConstraintInternalFrame frame = new ComplexConstraintInternalFrame(model);
		frame.setSize(400, 400);
		frame.setTitle("Complex constraint view");
		frame.setLocation(75, 75);
		frame.setVisible(true);
		this.add(frame);
		try {
			frame.setSelected(true);
		}
		catch (final PropertyVetoException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
