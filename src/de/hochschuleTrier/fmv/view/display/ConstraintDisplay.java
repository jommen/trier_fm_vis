package de.hochschuleTrier.fmv.view.display;

import java.awt.Color;
import java.awt.Font;
import java.util.Map;

import prefuse.Constants;
import prefuse.Display;
import prefuse.Visualization;
import prefuse.action.ActionList;
import prefuse.action.ItemAction;
import prefuse.action.RepaintAction;
import prefuse.action.animate.ColorAnimator;
import prefuse.action.assignment.ColorAction;
import prefuse.action.assignment.FontAction;
import prefuse.action.assignment.StrokeAction;
import prefuse.action.layout.RandomLayout;
import prefuse.action.layout.graph.ForceDirectedLayout;
import prefuse.activity.Activity;
import prefuse.controls.DragControl;
import prefuse.controls.PanControl;
import prefuse.data.Graph;
import prefuse.data.Schema;
import prefuse.data.Tuple;
import prefuse.data.event.TupleSetListener;
import prefuse.data.expression.Predicate;
import prefuse.data.expression.parser.ExpressionParser;
import prefuse.data.search.PrefixSearchTupleSet;
import prefuse.data.search.SearchTupleSet;
import prefuse.data.tuple.TupleSet;
import prefuse.render.AbstractShapeRenderer;
import prefuse.render.DefaultRendererFactory;
import prefuse.render.EdgeRenderer;
import prefuse.render.LabelRenderer;
import prefuse.util.ColorLib;
import prefuse.util.FontLib;
import prefuse.util.PrefuseLib;
import prefuse.util.StrokeLib;
import prefuse.util.force.DragForce;
import prefuse.util.force.ForceSimulator;
import prefuse.util.force.NBodyForce;
import prefuse.util.force.SpringForce;
import prefuse.visual.VisualItem;
import prefuse.visual.expression.InGroupPredicate;
import de.hochschuleTrier.fmv.controls.HighlightItemMouseover;
import de.hochschuleTrier.fmv.controls.constraints.ConstraintFocusControl;
import de.hochschuleTrier.fmv.controls.constraints.ConstraintNodeFillColorControl;
import de.hochschuleTrier.fmv.controls.constraints.ConstraintNodeStrokeColorAction;
import de.hochschuleTrier.fmv.controls.constraints.ConstraintNodeStrokeControl;
import de.hochschuleTrier.fmv.controls.constraints.ConstraintRightClickControl;
import de.hochschuleTrier.fmv.controls.constraints.ConstraintZoomControl;
import de.hochschuleTrier.fmv.controls.constraints.ConstraintZoomToFitControl;
import de.hochschuleTrier.fmv.controls.constraints.ExtendedNeighborHighlightControl;
import de.hochschuleTrier.fmv.filter.constraints.ConstraintEdgeFilter;
import de.hochschuleTrier.fmv.filter.constraints.ConstraintFocusFilter;
import de.hochschuleTrier.fmv.filter.constraints.ConstraintInspectingFilter;
import de.hochschuleTrier.fmv.filter.constraints.ConstraintNodeFilter;
import de.hochschuleTrier.fmv.model.impl.ApplicationModel;
import de.hochschuleTrier.fmv.model.impl.FeatureSchema;
import de.hochschuleTrier.fmv.model.impl.UISettings;
import de.hochschuleTrier.fmv.model.impl.constraints.ConstraintEdgeSchema;
import de.hochschuleTrier.fmv.model.interfaces.listener.IUISettingsListener;
import de.hochschuleTrier.fmv.util.TreeDisplayEnum;
import de.hochschuleTrier.fmv.util.UIConstants;
import de.hochschuleTrier.fmv.view.layout.ConstraintIconLayout;
import de.hochschuleTrier.fmv.view.render.ConstraintEdgeRenderer;
import de.hochschuleTrier.fmv.view.render.ConstraintIconRenderer;

/**
 * Feature Model Constraint View
 * 
 */
public class ConstraintDisplay extends Display implements IUISettingsListener {

	private LabelRenderer labelRenderer;
	private EdgeRenderer edgeRenderer;

	private ExtendedNeighborHighlightControl enhancedNeighborHighlightControl;
	private ConstraintFocusFilter constraintFocusFilter;

	public static final String COMPLEX_CONSTRAINT_DECORATORS = "complexConstraintDeco";

	private final Graph graph;
	private final Map<String, Color> colorMap;

	private static final Schema DECORATOR_SCHEMA = PrefuseLib.getVisualItemSchema();
	static {
		DECORATOR_SCHEMA.setDefault(VisualItem.INTERACTIVE, true);
	}

	public ConstraintDisplay(final Graph graph, final Map<String, Color> colorMap) {
		super(new Visualization());
		this.graph = graph;
		this.colorMap = colorMap;
		UISettings.getInstance().addListener(this);

		this.setUpVisualization();
		this.setUpRenderers();
		this.setUpActions();
		this.setUpDisplay();
	}

	private void setUpVisualization() {
		this.m_vis.addGraph(TreeDisplayEnum.TREE.toString(), this.graph);
		this.m_vis.setInteractive(TreeDisplayEnum.EDGES.toString(), null, false);
	}

	private void setUpRenderers() {
		this.m_vis.addDecorators(COMPLEX_CONSTRAINT_DECORATORS, TreeDisplayEnum.NODES.toString(), DECORATOR_SCHEMA);

		this.labelRenderer = new LabelRenderer(FeatureSchema.NAME);
		this.labelRenderer.setRenderType(AbstractShapeRenderer.RENDER_TYPE_DRAW_AND_FILL);
		this.labelRenderer.setHorizontalAlignment(Constants.CENTER);
		this.labelRenderer.setRoundedCorner(12, 12);
		this.labelRenderer.setHorizontalPadding(5);
		this.labelRenderer.setVerticalPadding(2);
		this.edgeRenderer = new ConstraintEdgeRenderer();

		final DefaultRendererFactory rendererFactory = new DefaultRendererFactory(this.labelRenderer);
		rendererFactory.add(new InGroupPredicate(TreeDisplayEnum.EDGES.toString()), this.edgeRenderer);
		rendererFactory.add(new InGroupPredicate(COMPLEX_CONSTRAINT_DECORATORS), new ConstraintIconRenderer());
		this.m_vis.setRendererFactory(rendererFactory);
	}

	private void setUpActions() {
		final int startColor = ColorLib.rgb(0x02, 0x97, 0x19);
		final int endColor = ColorLib.rgb(0xCC, 0xFF, 0x99);
		final String activity = "animatePaint";
		this.enhancedNeighborHighlightControl = new ExtendedNeighborHighlightControl(UIConstants.DEFAULT_CONSTRAINT_FOCUS_DISTANCE, startColor, endColor, activity);
		this.constraintFocusFilter = new ConstraintFocusFilter(UIConstants.DEFAULT_CONSTRAINT_FOCUS_DISTANCE);

		final ItemAction textColor = new ColorAction(TreeDisplayEnum.NODES.toString(), VisualItem.TEXTCOLOR, Color.BLACK.getRGB());
		this.m_vis.putAction("textColor", textColor);

		final FontAction fonts = new FontAction(TreeDisplayEnum.NODES.toString(), FontLib.getFont("Tahoma", 11));
		fonts.add("ingroup('" + Visualization.SEARCH_ITEMS + "')", FontLib.getFont("Tahoma", Font.BOLD, 13));
		fonts.add("ingroup('" + Visualization.FOCUS_ITEMS + "')", FontLib.getFont("Tahoma", Font.BOLD, 13));

		final ColorAction nodeBackgroundColor = new ConstraintNodeFillColorControl(TreeDisplayEnum.NODES.toString(), this.enhancedNeighborHighlightControl);
		nodeBackgroundColor.add(VisualItem.FIXED, ColorLib.rgb(0xFF, 0x64, 0x64));
		nodeBackgroundColor.setDefaultColor(ColorLib.gray(255));

		final ColorAction nodeStrokeColor = new ConstraintNodeStrokeColorAction(TreeDisplayEnum.NODES.toString(), VisualItem.STROKECOLOR, ColorLib.gray(50));
		final StrokeAction nodeStroke = new ConstraintNodeStrokeControl(TreeDisplayEnum.NODES.toString(), StrokeLib.getStroke(1));

		// define edge colors
		final Predicate excludesPredicate = ExpressionParser.predicate(ConstraintEdgeSchema.CONSTRAINT_TYPE + " = '" + ConstraintEdgeSchema.EXCLUDES_CONSTRAINT + "'");
		final Predicate requiresPredicate = ExpressionParser.predicate(ConstraintEdgeSchema.CONSTRAINT_TYPE + " = '" + ConstraintEdgeSchema.REQUIRES_CONSTRAINT + "'");

		final ItemAction excludesConstraintColor = new ColorAction(TreeDisplayEnum.EDGES.toString(), excludesPredicate, VisualItem.STROKECOLOR, this.colorMap.get("excludedEdgeColor").getRGB());
		final ItemAction requiresConstraintColor = new ColorAction(TreeDisplayEnum.EDGES.toString(), requiresPredicate, VisualItem.STROKECOLOR, this.colorMap.get("requiresEdgeColor").getRGB());

		final ActionList recolor = new ActionList();
		recolor.add(nodeStrokeColor);
		recolor.add(nodeStroke);
		recolor.add(textColor);
		recolor.add(nodeBackgroundColor);
		recolor.add(fonts);
		this.m_vis.putAction("recolor", recolor);

		final ActionList filter = new ActionList();
		filter.add(recolor);
		filter.add(excludesConstraintColor);
		filter.add(requiresConstraintColor);
		filter.add(new ConstraintNodeFilter(TreeDisplayEnum.EDGES.toString()));
		filter.add(new ConstraintEdgeFilter(TreeDisplayEnum.EDGES.toString()));
		filter.add(this.constraintFocusFilter);
		this.m_vis.putAction("filter", filter);

		final ActionList inspectFilter = new ActionList();
		inspectFilter.add(new ConstraintInspectingFilter(ApplicationModel.getInstance().getConstraintModel().getNodesToInspectListModel(), this.graph, this.getVisualization()));
		this.m_vis.putAction("inspectFilter", inspectFilter);

		final ForceSimulator forceSim = new ForceSimulator();
		// default: -1.0f, -1.0f, 0.9f
		forceSim.addForce(new NBodyForce(-1.5f, 250f, 0.9f));
		// default: 1.0E-4f, 50.0f
		forceSim.addForce(new SpringForce(1.0E-4f, 50.0f));
		// default: 0.01f
		forceSim.addForce(new DragForce(0.01f));

		final ActionList layout = new ActionList(Activity.INFINITY);
		layout.add(new ForceDirectedLayout(TreeDisplayEnum.TREE.toString(), forceSim, false));
		layout.add(new ConstraintIconLayout(COMPLEX_CONSTRAINT_DECORATORS));
		layout.add(recolor);
		layout.add(new RepaintAction());

		final ActionList random = new ActionList();
		random.add(new RandomLayout(TreeDisplayEnum.TREE.toString()));

		this.m_vis.putAction("layout", layout);
		this.m_vis.putAction("random", random);

		this.m_vis.runAfter("filter", "layout");
		this.m_vis.runAfter("inspectFilter", "layout");

		final ActionList animatePaint = new ActionList(400);
		animatePaint.add(new ColorAnimator(TreeDisplayEnum.NODES.toString()));
		animatePaint.add(recolor);
		animatePaint.add(new RepaintAction());
		this.m_vis.putAction("animatePaint", animatePaint);

	}

	private void setUpDisplay() {
		// initialize the display
		this.setSize(600, 600);

		this.addControlListener(new ConstraintFocusControl(2, "filter"));
		this.addControlListener(new ConstraintRightClickControl());
		this.addControlListener(new DragControl());
		this.addControlListener(new PanControl());
		this.addControlListener(new ConstraintZoomControl());
		this.addControlListener(new ConstraintZoomToFitControl());
		this.addControlListener(new HighlightItemMouseover());
		this.addControlListener(this.enhancedNeighborHighlightControl);

		// ------------------------------------------------

		// filter graph and perform layout
		this.m_vis.run("filter");

		final SearchTupleSet search = new PrefixSearchTupleSet();
		this.m_vis.addFocusGroup(Visualization.SEARCH_ITEMS, search);
		search.addTupleSetListener(new TupleSetListener() {
			@Override
			public void tupleSetChanged(final TupleSet t, final Tuple[] add, final Tuple[] rem) {
				ConstraintDisplay.this.m_vis.cancel("animatePaint");
				ConstraintDisplay.this.m_vis.run("recolor");
				ConstraintDisplay.this.m_vis.run("animatePaint");
			}
		});
	}

	// ------------------------------------------------------------------------

	@Override
	public void constraintFocusDistanceChanged(final int newDistance) {
		this.constraintFocusFilter.setDistance(newDistance);
		this.enhancedNeighborHighlightControl.setDistance(newDistance);
		this.m_vis.run("filter");
	}

	@Override
	public void fisheyeDistanceChanged(final int newDistance) {
		// nothing to do..
	}
}
