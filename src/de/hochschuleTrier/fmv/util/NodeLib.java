package de.hochschuleTrier.fmv.util;

import java.awt.Color;

import prefuse.data.Edge;
import prefuse.data.Graph;
import prefuse.data.Node;
import de.hochschuleTrier.fmv.model.impl.FeatureSchema;
import de.hochschuleTrier.fmv.model.impl.featureDiagram.FeatureDiagramSchema;

public class NodeLib {

	public static boolean isFeatureGroup(final Node node) {
		return node.getString(FeatureSchema.NODETYPE).equals(FeatureDiagramSchema.NODETYPE_FEATUREGROUP);
	}

	public static boolean isOptional(final Node nodeItem) {
		return nodeItem.getBoolean(FeatureSchema.OPTIONAL);
	}

	public static String getGroupType(final Node node) {
		if (NodeLib.isFeatureGroup(node)) {
			return node.getString(FeatureSchema.GROUPTYPE);
		}
		return FeatureDiagramSchema.GROUPTYPE_NONE;
	}

	public static String getNodeType(final Node node) {
		return node.getString(FeatureSchema.NODETYPE);
	}

	public static String getName(final Node node) {
		return node.getString(FeatureSchema.NAME);
	}

	public static Color getColor(final Node node) {
		return new Color(node.getInt(FeatureSchema.COLOR));
	}

	public static void setColor(final Node node, final Color color) {
		if (color == null) {
			NodeLib.setColor(node, 0xFFFFFF);
		}
		else {
			NodeLib.setColor(node, color.getRGB());
		}
	}

	public static void setColor(final Node node, final int color) {
		node.set(FeatureSchema.COLOR, color);
	}

	public static boolean hasCrossTreeEdge(final Node node) {
		return node.getBoolean(FeatureSchema.HAS_CROSS_TREE_EDGE);
	}

	public static void setHasCrossTreeEdge(final Node node, final boolean hasCrossTreeEdge) {
		node.setBoolean(FeatureSchema.HAS_CROSS_TREE_EDGE, hasCrossTreeEdge);
	}

	public static boolean isFullExpanded(final Node node) {
		return node.getBoolean(FeatureSchema.IS_FULL_EXPANDED);
	}

	public static void setFullExpanded(final Node node, final boolean isFullExpanded) {
		node.setBoolean(FeatureSchema.IS_FULL_EXPANDED, isFullExpanded);
	}

	public static boolean isEqual(final Node node1, final Node node2) {
		return NodeLib.getName(node1).equals(NodeLib.getName(node2));
	}

	public static Edge getConstraintEdgeBetween(final Node node1, final Node node2) {
		final Graph graph = node1.getGraph();
		Edge edge = graph.getEdge(node1, node2);
		if (edge == null) {
			edge = graph.getEdge(node2, node1);
		}
		return (edge == null || !EdgeLib.isConstraintEdge(edge) ? null : edge);
	}

	public static boolean existsConstraintEdgeBetween(final Node node1, final Node node2) {
		return NodeLib.getConstraintEdgeBetween(node1, node2) != null;
	}

}
