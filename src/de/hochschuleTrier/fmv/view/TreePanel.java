package de.hochschuleTrier.fmv.view;

import java.awt.Color;

import javax.swing.JTree;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import prefuse.Display;
import prefuse.visual.NodeItem;
import de.hochschuleTrier.fmv.controls.overview.TreePanelMouseListener;
import de.hochschuleTrier.fmv.model.impl.ApplicationModel;
import de.hochschuleTrier.fmv.model.impl.TreeOverviewModel;
import de.hochschuleTrier.fmv.model.interfaces.complexConstraints.IComplexConstraintModel;
import de.hochschuleTrier.fmv.model.interfaces.constraints.IConstraintModel;
import de.hochschuleTrier.fmv.model.interfaces.featureDiagram.IFeatureDiagramModel;
import de.hochschuleTrier.fmv.model.interfaces.listener.IApplicationModelListener;
import de.hochschuleTrier.fmv.model.interfaces.listener.IFeatureModelClosedListener;
import de.hochschuleTrier.fmv.model.interfaces.listener.INewFeatureModelListener;
import de.hochschuleTrier.fmv.util.JTreeUtil;
import de.hochschuleTrier.fmv.view.render.TreePanelCellRenderer;

public class TreePanel extends JTree implements IApplicationModelListener, INewFeatureModelListener, IFeatureModelClosedListener {

	public TreePanel() {
		ApplicationModel.getInstance().addListener(this);
		ApplicationModel.getInstance().addNewFeatureModelListener(this);
		ApplicationModel.getInstance().addFeatureModelClosedListener(this);
		this.addMouseListener(new TreePanelMouseListener(this));
		this.setCellRenderer(new TreePanelCellRenderer());
		this.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		this.setExpandsSelectedPaths(true);
		this.setModel(new TreeOverviewModel(null));
	}

	@Override
	public void currentFeatureTreeModelHasChanged(final IFeatureDiagramModel newFeatureTreeModel) {
		try {
			final TreeModel treeModel = new TreeOverviewModel(newFeatureTreeModel.getTree());
			this.setModel(treeModel);
		}
		catch (final Exception e) {
			// nothing to do..
		}
	}

	@Override
	public void nodeSelected(final NodeItem oldNode, final NodeItem newNode) {
		JTreeUtil.expandAll(this, false);
		final TreePath path = ((TreeOverviewModel) this.getModel()).getPathToRoot(newNode);
		this.setSelectionPath(path);
		this.setExpandedState(path, true);
	}

	@Override
	public void nodePainted(final NodeItem node, final Color color) {

	}

	@Override
	public void activeDisplayChanged(final Display display) {
	}

	@Override
	public void newFeatueTreeModelCreated(final IFeatureDiagramModel newFeatureTreeModel, final IConstraintModel newConstraintModel, final String featureModelName) {
		this.currentFeatureTreeModelHasChanged(newFeatureTreeModel);
	}

	@Override
	public void newSubtreeCreated(final IFeatureDiagramModel featureTreeModel) {
		this.currentFeatureTreeModelHasChanged(featureTreeModel);
	}

	@Override
	public void newComplexConstraintView(final IComplexConstraintModel model) {
		// nothing to do..
	}

	@Override
	public void featureModelClosed() {
		this.setModel(new TreeOverviewModel(null));
	}

}
