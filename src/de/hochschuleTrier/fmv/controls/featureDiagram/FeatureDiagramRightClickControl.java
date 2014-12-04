package de.hochschuleTrier.fmv.controls.featureDiagram;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.util.Iterator;
import java.util.List;

import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;

import prefuse.controls.ControlAdapter;
import prefuse.data.Node;
import prefuse.data.Tree;
import prefuse.visual.NodeItem;
import prefuse.visual.VisualItem;
import prefuse.visual.tuple.TableNodeItem;
import de.hochschuleTrier.fmv.controls.AddToOrRemoveFromConstraintInspectListControl;
import de.hochschuleTrier.fmv.controls.OpenComplexConstraintActionListener;
import de.hochschuleTrier.fmv.exceptions.NodeNotFoundException;
import de.hochschuleTrier.fmv.model.impl.ApplicationModel;
import de.hochschuleTrier.fmv.model.impl.FeatureSchema;
import de.hochschuleTrier.fmv.model.impl.constraints.ConstraintNodesToInspectInspectListModel;
import de.hochschuleTrier.fmv.model.impl.featureDiagram.FeatureModel;
import de.hochschuleTrier.fmv.model.interfaces.complexConstraints.IComplexConstraint;
import de.hochschuleTrier.fmv.model.interfaces.complexConstraints.IComplexConstraintGroup;
import de.hochschuleTrier.fmv.model.interfaces.complexConstraints.IComplexConstraintLiteral;
import de.hochschuleTrier.fmv.model.interfaces.constraints.IConstraintModel;
import de.hochschuleTrier.fmv.model.interfaces.featureDiagram.IFeatureModel;
import de.hochschuleTrier.fmv.util.GraphLib;
import de.hochschuleTrier.fmv.util.NodeLib;
import de.hochschuleTrier.fmv.util.TreeDisplayEnum;
import de.hochschuleTrier.fmv.view.NewFeaturePopupForm;

public class FeatureDiagramRightClickControl extends ControlAdapter {

	private final Tree tree;
	private final IConstraintModel constraintModel;

	private NodeItem currentItem;

	public FeatureDiagramRightClickControl(final Tree tree) {
		this.tree = tree;
		this.constraintModel = ApplicationModel.getInstance().getConstraintModel();
	}

	@Override
	public void itemClicked(final VisualItem item, final MouseEvent e) {
		if (SwingUtilities.isRightMouseButton(e) && item instanceof NodeItem) {
			this.currentItem = (NodeItem) item;

			final JPopupMenu contextMenu = new JPopupMenu("Context menu");

			final JMenuItem editText = new JMenuItem("Edit Feature", 'e');
			final JMenuItem addNode = new JMenuItem("Add Sub-Feature", 'n');

			contextMenu.add(editText);
			contextMenu.add(addNode);

			addNode.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(final ActionEvent e) {
					FeatureDiagramRightClickControl.this.addNewNode(null);
				}
			});

			editText.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(final ActionEvent e) {
					final IFeatureModel model = new FeatureModel();
					final NodeItem nodeItem = FeatureDiagramRightClickControl.this.getCurrentItem();
					model.setName(NodeLib.getName(nodeItem));
					model.setOptional(NodeLib.isOptional(nodeItem));
					model.setGroupType(NodeLib.getGroupType(nodeItem));
					model.setNodeType(NodeLib.getNodeType(nodeItem));
					FeatureDiagramRightClickControl.this.editNode(model);
				}
			});

			// Open complex constraint view
			final ApplicationModel appModel = ApplicationModel.getInstance();
			final String itemName = item.getString("name");
			final List<IComplexConstraintGroup> complexConstraints = appModel.getComplexConstraints().get(item.getString("name"));

			if (!complexConstraints.isEmpty()) {
				final JMenuItem openComplexConstraint = new JMenuItem("Open complex constraint view");
				openComplexConstraint.addActionListener(new OpenComplexConstraintActionListener(complexConstraints));
				contextMenu.add(openComplexConstraint);
			}

			// Add to or remove from inspect list
			final JMenuItem addToOrRemoveFromFocusGroup = new JMenuItem("Add to focus list");
			final ConstraintNodesToInspectInspectListModel listModel = appModel.getConstraintModel().getNodesToInspectListModel();
			final boolean isInInspectGroup = listModel.contains(itemName);
			if (isInInspectGroup) {
				addToOrRemoveFromFocusGroup.setText("Remove from focus list");
			}
			if (appModel.getConstraintModel().isInspectFilter()) {
				addToOrRemoveFromFocusGroup.setEnabled(false);
			}
			else {
				addToOrRemoveFromFocusGroup.setEnabled(true);
			}

			addToOrRemoveFromFocusGroup.addActionListener(new AddToOrRemoveFromConstraintInspectListControl(itemName, listModel));
			contextMenu.add(addToOrRemoveFromFocusGroup);

			contextMenu.show(e.getComponent(), e.getX(), e.getY());
		}
	}

	private void addNewNode(final IFeatureModel inModel) {
		final NewFeaturePopupForm form = new NewFeaturePopupForm();
		final int response = form.showDialog(inModel);
		if (response == JOptionPane.OK_OPTION) {
			final IFeatureModel model = form.getNewFeatureNodeModel();

			final String errorMessage = this.getNewNodeErrorMessage(model);
			if (errorMessage == null) {
				// Add node in constraint diagram
				final Node constraintNode = this.constraintModel.getConstraintGraph().addNode();
				this.setNodeAttributes(constraintNode, model);

				// Add node in feature diagram
				final Node featureNode = this.tree.addChild((Node) this.currentItem.getSourceTuple());
				this.setNodeAttributes(featureNode, model);
				this.currentItem.getVisualization().run("filter");
			}
			else {
				JOptionPane.showMessageDialog((Component) null, errorMessage, "Error", JOptionPane.ERROR_MESSAGE);
				this.addNewNode(model);
			}
		}
	}

	private void editNode(final IFeatureModel inModel) {
		final NewFeaturePopupForm form = new NewFeaturePopupForm();
		final int response = form.showDialog(inModel);
		if (response == JOptionPane.OK_OPTION) {
			final IFeatureModel model = form.getNewFeatureNodeModel();

			final String errorMessage = this.getEditNodeErrorMessage(model);
			if (errorMessage == null) {
				// Edit Node in Complex-Constraint-View
				// TODO: Now Complex constraint view has to be reopened to show the new node name --> Fix it
				final List<IComplexConstraintGroup> complexConstraintsOfNode = ApplicationModel.getInstance().getComplexConstraints().get(NodeLib.getName(this.currentItem));
				for (final IComplexConstraintGroup complexConstraintGroup : complexConstraintsOfNode) {
					this.editInComplexConstraints(complexConstraintGroup, model);
				}

				// Edit Node in Constraint-View
				try {
					final Node constraintNode = GraphLib.findNode(this.constraintModel.getConstraintGraph(), NodeLib.getName(this.currentItem));
					this.setNodeAttributes(constraintNode, model);
					this.constraintModel.getDisplay().getVisualization().run("animatePaint");
				}
				catch (final NodeNotFoundException e) {
					// if there is no node we can go one like normal ..
				}

				// Edit Node in FM-View
				this.setNodeAttributes(this.currentItem, model);
				this.currentItem.getVisualization().run("animatePaint");
			}
			else {
				JOptionPane.showMessageDialog((Component) null, errorMessage, "Error", JOptionPane.ERROR_MESSAGE);
				this.addNewNode(model);
			}
		}
	}

	private void editInComplexConstraints(final IComplexConstraintGroup complexConstraintGroup, final IFeatureModel model) {
		for (final IComplexConstraint complexConstraint : complexConstraintGroup.getChildren()) {
			if (complexConstraint instanceof IComplexConstraintGroup) {
				this.editInComplexConstraints((IComplexConstraintGroup) complexConstraint, model);
			}
			else {
				this.editInComplexConstraints((IComplexConstraintLiteral) complexConstraint, model);
			}
		}
	}

	private void editInComplexConstraints(final IComplexConstraintLiteral complexConstraintNode, final IFeatureModel model) {
		if (complexConstraintNode.getName().equals(NodeLib.getName(this.currentItem))) {
			complexConstraintNode.setName(model.getName());
		}
	}

	private void setNodeAttributes(final Node node, final IFeatureModel model) {
		node.setString(FeatureSchema.NAME, model.getName());
		node.setBoolean(FeatureSchema.OPTIONAL, model.isOptional());
		node.setString(FeatureSchema.NODETYPE, model.getNodeType());
		node.setString(FeatureSchema.GROUPTYPE, model.getGroupType());
	}

	private String getNewNodeErrorMessage(final IFeatureModel model) {
		if (model.getName().equals("")) {
			return "Please insert a name.";
		}
		final Iterator<TableNodeItem> nodes = this.currentItem.getVisualization().items(TreeDisplayEnum.NODES.toString());
		while (nodes.hasNext()) {
			final TableNodeItem node = nodes.next();
			if (NodeLib.getName(node).equals(model.getName())) {
				return "The name must be unique.";
			}
		}
		return null;
	}

	private String getEditNodeErrorMessage(final IFeatureModel model) {
		if (model.getName().equals("")) {
			return "Please insert a name.";
		}
		return null;
	}

	private NodeItem getCurrentItem() {
		return this.currentItem;
	}
}
