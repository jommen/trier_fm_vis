package de.hochschuleTrier.fmv.view.display;

import java.awt.Color;
import java.awt.Font;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import prefuse.Constants;
import prefuse.Display;
import prefuse.Visualization;
import prefuse.action.ActionList;
import prefuse.action.ItemAction;
import prefuse.action.RepaintAction;
import prefuse.action.assignment.ColorAction;
import prefuse.action.assignment.StrokeAction;
import prefuse.action.layout.graph.ForceDirectedLayout;
import prefuse.activity.Activity;
import prefuse.controls.PanControl;
import prefuse.data.Graph;
import prefuse.data.Schema;
import prefuse.render.AbstractShapeRenderer;
import prefuse.render.DefaultRendererFactory;
import prefuse.render.LabelRenderer;
import prefuse.util.ColorLib;
import prefuse.util.FontLib;
import prefuse.util.PrefuseLib;
import prefuse.util.StrokeLib;
import prefuse.util.force.DragForce;
import prefuse.util.force.ForceSimulator;
import prefuse.util.force.NBodyForce;
import prefuse.util.force.SpringForce;
import prefuse.visual.AggregateItem;
import prefuse.visual.AggregateTable;
import prefuse.visual.VisualGraph;
import prefuse.visual.VisualItem;
import prefuse.visual.expression.HoverPredicate;
import prefuse.visual.expression.InGroupPredicate;
import prefuse.visual.tuple.TableNodeItem;
import de.hochschuleTrier.fmv.controls.DefaultWheelZoomControl;
import de.hochschuleTrier.fmv.controls.complexConstraints.AggregateDragControl;
import de.hochschuleTrier.fmv.model.impl.complexConstraints.ComplexConstraintSchema;
import de.hochschuleTrier.fmv.model.impl.complexConstraints.ComplexConstraintType;
import de.hochschuleTrier.fmv.model.interfaces.complexConstraints.IComplexConstraint;
import de.hochschuleTrier.fmv.model.interfaces.complexConstraints.IComplexConstraintGroup;
import de.hochschuleTrier.fmv.model.interfaces.complexConstraints.IComplexConstraintLiteral;
import de.hochschuleTrier.fmv.model.interfaces.complexConstraints.IComplexConstraintModel;
import de.hochschuleTrier.fmv.view.layout.AggregateLabelLayout;
import de.hochschuleTrier.fmv.view.layout.AggregateLayout;
import de.hochschuleTrier.fmv.view.render.ComplexConstraintAggregateRenderer;
import de.hochschuleTrier.fmv.view.render.ComplexConstraintDefaultEdgeRenderer;

/**
 * Displays complex constraint views
 * 
 * @version 1.0
 */
public class ComplexConstraintDisplay extends Display {

	public static final String GRAPH = "graph";
	public static final String NODES = "graph.nodes";
	public static final String EDGES = "graph.edges";
	public static final String AGGR = "aggregates";
	public static final String AGGR_DECORATORS = "aggrDeco";

	private final Graph graph;
	private final IComplexConstraintModel model;

	private static final Schema DECORATOR_SCHEMA = PrefuseLib.getVisualItemSchema();
	static {
		DECORATOR_SCHEMA.setDefault(VisualItem.INTERACTIVE, false);
		DECORATOR_SCHEMA.setDefault(VisualItem.TEXTCOLOR, ColorLib.gray(128));
		DECORATOR_SCHEMA.setDefault(VisualItem.FONT, FontLib.getFont("Tahoma", 16));
	}

	public ComplexConstraintDisplay(final IComplexConstraintModel constraintModel) {
		super(new Visualization());
		this.graph = constraintModel.getConstraintGraph();
		this.model = constraintModel;

		this.setUpVisualization();
		this.setUpRenderers();
		this.setUpActions();
		this.setUpDisplay();
	}

	private void setUpVisualization() {
		// add visual data groups
		final VisualGraph vg = this.m_vis.addGraph(ComplexConstraintDisplay.GRAPH, this.graph);
		this.m_vis.setInteractive(ComplexConstraintDisplay.EDGES, null, false);
		this.m_vis.setValue(ComplexConstraintDisplay.NODES, null, VisualItem.SHAPE, new Integer(Constants.SHAPE_ELLIPSE));

		final AggregateTable aggregateTable = this.m_vis.addAggregates(ComplexConstraintDisplay.AGGR);
		aggregateTable.addColumns(new ComplexConstraintSchema());

		for (final IComplexConstraintGroup constraint : this.model.getComplexConstraints()) {
			if (constraint.isTopLevelConstraint()) {
				final VisualItem fromItem = this.initAggregatesFromComplexConstraint(aggregateTable, constraint.getChildren().get(0));
				final AggregateItem toItem = (AggregateItem) this.initAggregatesFromComplexConstraint(aggregateTable, constraint.getChildren().get(1));
				if (constraint.getType().equals(ComplexConstraintType.IMPLIES)) {
					fromItem.set(ComplexConstraintSchema.REQUIRES, new AggregateItem[] { toItem });
				}
				else {
					fromItem.set(ComplexConstraintSchema.EXCLUDES, new AggregateItem[] { toItem });
				}
			}
		}
	}

	private VisualItem initAggregatesFromComplexConstraint(final AggregateTable aggregateTable, final IComplexConstraint constraint) {
		if (constraint instanceof IComplexConstraintGroup) {
			return this.initComplexConstraintGroup(aggregateTable, (IComplexConstraintGroup) constraint);
		}
		else {
			return this.initComplexConstraintNode(aggregateTable, (IComplexConstraintLiteral) constraint);
		}
	}

	private VisualItem initComplexConstraintGroup(final AggregateTable aggregateTable, final IComplexConstraintGroup constraint) {
		final AggregateItem complexItem = (AggregateItem) aggregateTable.addItem();

		final List<VisualItem> visualChildren = new ArrayList<VisualItem>();
		for (final IComplexConstraint child : constraint.getChildren()) {
			final VisualItem visualChild = this.initAggregatesFromComplexConstraint(aggregateTable, child);
			if (visualChild != null) {
				visualChildren.add(visualChild);
			}
		}

		complexItem.setBoolean(ComplexConstraintSchema.NEGATE, constraint.isNegated());
		switch (constraint.getType()) {
			case AND:
				complexItem.setString(ComplexConstraintSchema.TYPE, ComplexConstraintSchema.TYPE_AND);
				break;
			case OR:
				complexItem.setString(ComplexConstraintSchema.TYPE, ComplexConstraintSchema.TYPE_OR);
				break;
			default:
				break;

		}

		for (final VisualItem visualChild : visualChildren) {
			complexItem.addItem(visualChild);
		}

		return complexItem;
	}

	private VisualItem initComplexConstraintNode(final AggregateTable aggregateTable, final IComplexConstraintLiteral constraint) {
		final Iterator<VisualItem> visibleItems = this.m_vis.visibleItems();
		while (visibleItems.hasNext()) {
			final VisualItem item = visibleItems.next();
			if (item instanceof TableNodeItem) {
				if (item.getInt("id") == constraint.getId()) {
					return item;
				}
			}
		}
		return null;
	}

	private void setUpRenderers() {
		DECORATOR_SCHEMA.setDefault(VisualItem.TEXTCOLOR, ColorLib.gray(255, 128));
		DECORATOR_SCHEMA.setDefault(VisualItem.FONT, FontLib.getFont("Tahoma", Font.BOLD, 32));
		this.m_vis.addDecorators(AGGR_DECORATORS, AGGR, new HoverPredicate(), DECORATOR_SCHEMA);

		// draw the nodes as basic shapes
		final LabelRenderer nodeRenderer = new LabelRenderer("name");
		nodeRenderer.setRenderType(AbstractShapeRenderer.RENDER_TYPE_DRAW_AND_FILL);
		nodeRenderer.setHorizontalAlignment(Constants.CENTER);
		nodeRenderer.setRoundedCorner(12, 12);
		nodeRenderer.setHorizontalPadding(5);
		nodeRenderer.setVerticalPadding(2);

		// draw aggregates as polygons with curved edges
		final ComplexConstraintAggregateRenderer polyR = new ComplexConstraintAggregateRenderer(Constants.POLY_TYPE_CURVE);
		polyR.setCurveSlack(0.15f);

		final DefaultRendererFactory rendererFactory = new DefaultRendererFactory();
		//		rendererFactory.setDefaultRenderer(nodeRenderer);
		rendererFactory.add(new InGroupPredicate(NODES), nodeRenderer);
		rendererFactory.add(new InGroupPredicate(EDGES), new ComplexConstraintDefaultEdgeRenderer());
		rendererFactory.add("ingroup('aggregates')", polyR);
		rendererFactory.add(new InGroupPredicate(AGGR_DECORATORS), new LabelRenderer(ComplexConstraintSchema.TYPE));
		this.m_vis.setRendererFactory(rendererFactory);
	}

	private void setUpActions() {
		final ColorAction nodeStroke = new ColorAction(ComplexConstraintDisplay.NODES, VisualItem.STROKECOLOR);
		nodeStroke.setDefaultColor(ColorLib.gray(100));
		nodeStroke.add(ComplexConstraintSchema.NEGATE + " = true", Color.RED.getRGB());

		final ColorAction nodeFill = new ColorAction(ComplexConstraintDisplay.NODES, VisualItem.FILLCOLOR);
		nodeFill.setDefaultColor(ColorLib.gray(255));
		nodeFill.add("_hover", ColorLib.gray(200));

		final ColorAction nodeEdges = new ColorAction(ComplexConstraintDisplay.EDGES, VisualItem.STROKECOLOR);
		nodeEdges.setDefaultColor(ColorLib.gray(100));

		final ItemAction nodeText = new ColorAction(ComplexConstraintDisplay.NODES, VisualItem.TEXTCOLOR, Color.BLACK.getRGB());
		this.m_vis.putAction("textColor", nodeText);

		final ColorAction aggregateStrokeColor = new ColorAction(ComplexConstraintDisplay.AGGR, VisualItem.STROKECOLOR);
		aggregateStrokeColor.setDefaultColor(ColorLib.gray(50));
		aggregateStrokeColor.add(ComplexConstraintSchema.NEGATE + " = true", ColorLib.rgb(255, 100, 100));

		final StrokeAction aggregateStroke = new StrokeAction(ComplexConstraintDisplay.AGGR, StrokeLib.getStroke(1));
		aggregateStroke.add(ComplexConstraintSchema.TYPE + " = '" + ComplexConstraintSchema.TYPE_OR + "'", StrokeLib.getStroke(1.5f, StrokeLib.LONG_DASHES));

		final ColorAction aggregateFill = new ColorAction(ComplexConstraintDisplay.AGGR, VisualItem.FILLCOLOR);
		aggregateFill.setDefaultColor(ColorLib.rgba(200, 200, 255, 150));

		// bundle the color actions
		final ActionList colors = new ActionList();
		colors.add(nodeStroke);
		colors.add(nodeFill);
		colors.add(nodeEdges);
		colors.add(aggregateStroke);
		colors.add(aggregateStrokeColor);
		colors.add(nodeText);
		colors.add(aggregateFill);

		final ForceSimulator forceSim = new ForceSimulator();
		// default: -1.0f, -1.0f, 0.9f
		forceSim.addForce(new NBodyForce(-1.5f, 100f, 0.9f));
		// default: 1.0E-4f, 50.0f
		forceSim.addForce(new SpringForce(1.0E-4f, 50.0f));
		// default: 0.01f
		forceSim.addForce(new DragForce(0.01f));

		// now create the main layout routine
		final ActionList layout = new ActionList(Activity.INFINITY);
		layout.add(colors);
		layout.add(new ForceDirectedLayout(GRAPH, forceSim, false));
		layout.add(new AggregateLayout(AGGR));
		layout.add(new AggregateLabelLayout(AGGR_DECORATORS));
		layout.add(new RepaintAction());
		this.m_vis.putAction("layout", layout);
	}

	private void setUpDisplay() {
		this.setSize(500, 500);
		this.pan(250, 250);
		this.setHighQuality(true);
		this.addControlListener(new AggregateDragControl());
		this.addControlListener(new DefaultWheelZoomControl());
		this.addControlListener(new PanControl());

		// set things running
		this.m_vis.run("layout");
	}

}
