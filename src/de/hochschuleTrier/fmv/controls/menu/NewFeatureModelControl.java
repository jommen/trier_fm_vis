package de.hochschuleTrier.fmv.controls.menu;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JOptionPane;

import prefuse.data.Graph;
import prefuse.data.Node;
import prefuse.data.Tree;
import de.hochschuleTrier.fmv.model.impl.ApplicationModel;
import de.hochschuleTrier.fmv.model.impl.FeatureSchema;
import de.hochschuleTrier.fmv.model.impl.complexConstraints.ComplexConstraints;
import de.hochschuleTrier.fmv.model.impl.constraints.ConstraintEdgeSchema;
import de.hochschuleTrier.fmv.model.impl.constraints.ConstraintModel;
import de.hochschuleTrier.fmv.model.impl.constraints.ConstraintNodeSchema;
import de.hochschuleTrier.fmv.model.impl.featureDiagram.FeatureDiagramModel;
import de.hochschuleTrier.fmv.model.impl.featureDiagram.FeatureDiagramSchema;

public class NewFeatureModelControl implements ActionListener {

	@Override
	public void actionPerformed(final ActionEvent arg0) {
		final String message = "Please insert the title of the feature model.";
		final String title = "Create new feature model";

		final String featureModelName = JOptionPane.showInputDialog(null, message, title, JOptionPane.PLAIN_MESSAGE);
		if (featureModelName != null && !featureModelName.isEmpty()) {
			final FeatureDiagramModel featureDiagramModel = this.createFeatureDiagramModel(featureModelName);
			final ConstraintModel constraintModel = this.createConstraintModel(featureModelName);
			final ComplexConstraints complexConstraints = new ComplexConstraints(null);

			final ApplicationModel applicationModel = ApplicationModel.getInstance();
			applicationModel.setComplexConstraints(complexConstraints);
			applicationModel.openNewFeatureTreeModel(featureDiagramModel, constraintModel, featureModelName);
		}
	}

	private FeatureDiagramModel createFeatureDiagramModel(final String rootName) {
		final Tree tree = new Tree();
		tree.addColumns(new FeatureDiagramSchema());

		final Node root = tree.addRoot();
		this.setAttributes(root, rootName);

		return new FeatureDiagramModel(tree);
	}

	private ConstraintModel createConstraintModel(final String nodeName) {
		final Graph graph = new Graph();

		graph.getNodeTable().addColumns(new ConstraintNodeSchema());
		graph.getEdgeTable().addColumns(new ConstraintEdgeSchema());

		final Node node = graph.addNode();
		this.setAttributes(node, nodeName);

		return new ConstraintModel(graph);
	}

	private void setAttributes(final Node node, final String nodeName) {
		node.setString(FeatureSchema.NAME, nodeName);
		node.setString(FeatureSchema.NODETYPE, FeatureDiagramSchema.NODETYPE_FEATURE);
		node.setBoolean(FeatureSchema.OPTIONAL, false);
		node.setString(FeatureSchema.GROUPTYPE, FeatureDiagramSchema.GROUPTYPE_NONE);
	}
}
