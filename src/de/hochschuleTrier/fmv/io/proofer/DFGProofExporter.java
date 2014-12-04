package de.hochschuleTrier.fmv.io.proofer;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import prefuse.data.Edge;
import prefuse.data.Node;
import de.hochschuleTrier.fmv.model.impl.ApplicationModel;
import de.hochschuleTrier.fmv.model.impl.complexConstraints.ComplexConstraintType;
import de.hochschuleTrier.fmv.model.impl.constraints.ConstraintEdgeSchema;
import de.hochschuleTrier.fmv.model.impl.featureDiagram.FeatureDiagramSchema;
import de.hochschuleTrier.fmv.model.interfaces.complexConstraints.IComplexConstraint;
import de.hochschuleTrier.fmv.model.interfaces.complexConstraints.IComplexConstraintGroup;
import de.hochschuleTrier.fmv.model.interfaces.complexConstraints.IComplexConstraintLiteral;
import de.hochschuleTrier.fmv.util.EdgeLib;
import de.hochschuleTrier.fmv.util.NodeLib;

public class DFGProofExporter extends AbstractProofExporter {

	@Override
	public void exportProof(final File toFile) {
		final ApplicationModel appModel = ApplicationModel.getInstance();
		this.setFeatureModel(appModel.getCurrentFeatureTreeModel().getTree());
		this.setCrossTreeConstraintGraph(appModel.getConstraintModel().getConstraintGraph());
		this.setComplexConstraints(appModel.getComplexConstraints());

		try (final BufferedWriter writer = new BufferedWriter(new FileWriter(toFile))) {
			this.writeArray(writer, this.getHeader());
			this.writeArray(writer, this.getSymbols());
			this.writeArray(writer, this.getAxioms());
			this.writeArray(writer, this.getFooter());

			writer.flush();
		}
		catch (final Exception e) {

		}
	}

	private void writeArray(final BufferedWriter writer, final List<String> content) throws IOException {
		for (final String c : content) {
			writer.write(c);
			writer.newLine();
		}
		writer.newLine();
	}

	protected List<String> getHeader() {
		final List<String> header = new ArrayList<>();
		header.add("begin_problem(Problem).");
		header.add("list_of_descriptions.");
		header.add("name({* None *}).");
		header.add("author({* None *}).");
		header.add("status(unknown).");
		header.add("description({* None *}).");
		header.add("end_of_list.");
		return header;
	}

	protected List<String> getFooter() {
		final List<String> footer = new ArrayList<>();
		footer.add("end_problem.");
		return footer;
	}

	protected List<String> getSymbols() throws IOException {
		final List<String> symbols = new ArrayList<>();

		final StringBuffer predicates = new StringBuffer();
		final Iterator<Node> nodes = this.getFeatureModel().nodes();
		while (nodes.hasNext()) {
			final Node node = nodes.next();
			predicates.append("(" + NodeLib.getName(node) + ",0),");
		}
		predicates.deleteCharAt(predicates.length() - 1);

		symbols.add("list_of_symbols.");
		symbols.add("predicates[" + predicates.toString() + "].");
		symbols.add("end_of_list.");

		return symbols;
	}

	protected List<String> getAxioms() {
		final List<String> axioms = new ArrayList<>();
		axioms.add("list_of_formulae(axioms).");
		axioms.addAll(this.buildFormulae());
		axioms.add("end_of_list.");
		return axioms;
	}

	protected List<String> buildFormulae() {
		final List<String> formulae = new ArrayList<>();

		formulae.addAll(this.buildCrossTreeConstraintFormulae());
		formulae.addAll(this.buildComplexConstraintFormulae());
		formulae.add(String.format("formula(%s).", NodeLib.getName(this.getFeatureModel().getRoot())));
		this.buildTreeFormula(this.getFeatureModel().getRoot(), formulae);

		return formulae;
	}

	private void buildTreeFormula(final Node node, final List<String> formulae) {
		final StringBuffer formula = new StringBuffer("formula(");

		switch (NodeLib.getGroupType(node)) {
			case FeatureDiagramSchema.GROUPTYPE_OR:
				formula.append(String.format("equiv(%s,or(", NodeLib.getName(node)));
				Iterator<Node> children = node.children();
				while (children.hasNext()) {
					final Node child = children.next();
					formula.append(NodeLib.getName(child) + ",");
				}
				formula.deleteCharAt(formula.length() - 1);
				formula.append("))).");
				break;

			case FeatureDiagramSchema.GROUPTYPE_ALTERNATIVE:
				formula.append("and(");
				children = node.children();
				while (children.hasNext()) {
					final Node child = children.next();
					formula.append(String.format("equiv(%s,and(", NodeLib.getName(child)));

					final Iterator<Node> innerChildren = node.children();
					while (innerChildren.hasNext()) {
						final Node innerChild = innerChildren.next();
						if (!NodeLib.isEqual(child, innerChild)) {
							formula.append(String.format("not(%s),", NodeLib.getName(innerChild)));
						}
					}
					formula.append(NodeLib.getName(node) + ")),");
				}
				formula.deleteCharAt(formula.length() - 1);
				formula.append(")).");
				break;

			case "NONE":
				formula.append("and(");
				children = node.children();
				while (children.hasNext()) {
					final Node child = children.next();
					if (NodeLib.isOptional(child)) {
						formula.append(String.format("implies(%s, %s),", NodeLib.getName(child), NodeLib.getName(node)));
					}
					else {
						formula.append(String.format("equiv(%s, %s),", NodeLib.getName(node), NodeLib.getName(child)));
					}
				}
				formula.deleteCharAt(formula.length() - 1);
				formula.append(")).");
				break;
		}

		formulae.add(formula.toString());

		final Iterator<Node> children = node.children();
		while (children.hasNext()) {
			final Node child = children.next();
			if (child.children().hasNext()) {
				this.buildTreeFormula(child, formulae);
			}
		}

	}

	private List<String> buildComplexConstraintFormulae() {
		final List<String> formula = new ArrayList<>();

		for (final IComplexConstraintGroup constraintGroup : this.getComplexConstraints().getConstraintList()) {
			formula.add(String.format("formula(%s).", this.buildComplexConstraintFormula(constraintGroup)));
		}

		return formula;
	}

	private String buildComplexConstraintFormula(final IComplexConstraint constraint) {
		// Constraint is a single node --> recursion anker
		if (constraint instanceof IComplexConstraintLiteral) {
			final IComplexConstraintLiteral constraintNode = (IComplexConstraintLiteral) constraint;
			return this.processNode(constraintNode);
		}
		// We've got a complex constraint --> do recursive
		else {
			final IComplexConstraintGroup group = (IComplexConstraintGroup) constraint;
			return this.processComplexConstraintGroup(group);
		}
	}

	private String processNode(final IComplexConstraintLiteral constraintNode) {
		if (constraintNode.isNegated()) {
			return String.format("not(%s)", constraintNode.getName());
		}
		return constraintNode.getName();
	}

	private String processComplexConstraintGroup(final IComplexConstraintGroup group) {
		final StringBuffer buffer = new StringBuffer();

		switch (group.getType()) {
			case IMPLIES:
				buffer.append("implies(");
				break;

			case EXCLUDES:
				buffer.append("not(and(");
				break;

			case AND:
				buffer.append("and(");
				break;

			case OR:
				buffer.append("or(");
				break;
		}

		// Collect all inner complex constraints
		final List<Node> nodes = new ArrayList<Node>();
		for (final IComplexConstraint child : group.getChildren()) {
			buffer.append(this.buildComplexConstraintFormula(child) + ",");
		}
		buffer.deleteCharAt(buffer.length() - 1);
		buffer.append(")");
		if (group.getType().equals(ComplexConstraintType.EXCLUDES)) {
			buffer.append(")");
		}
		return buffer.toString();
	}

	private List<String> buildCrossTreeConstraintFormulae() {
		final List<String> formula = new ArrayList<>();

		final Iterator<Edge> edges = this.getCrossTreeConstraintGraph().edges();
		while (edges.hasNext()) {
			final Edge edge = edges.next();
			if (EdgeLib.isConstraintEdge(edge)) {
				switch (EdgeLib.getConstraintType(edge)) {
					case ConstraintEdgeSchema.EXCLUDES_CONSTRAINT:
						formula.add(String.format("formula(not(and(%s,%s))).", NodeLib.getName(edge.getSourceNode()), NodeLib.getName(edge.getTargetNode())));
						break;

					case ConstraintEdgeSchema.REQUIRES_CONSTRAINT:
						formula.add(String.format("formula(implies(%s,%s)).", NodeLib.getName(edge.getSourceNode()), NodeLib.getName(edge.getTargetNode())));
						break;
				}
			}
		}

		return formula;
	}
}
