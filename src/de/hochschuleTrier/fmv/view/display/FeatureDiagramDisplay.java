package de.hochschuleTrier.fmv.view.display;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import prefuse.Constants;
import prefuse.Display;
import prefuse.Visualization;
import prefuse.action.Action;
import prefuse.action.ActionList;
import prefuse.action.ItemAction;
import prefuse.action.RepaintAction;
import prefuse.action.animate.ColorAnimator;
import prefuse.action.animate.LocationAnimator;
import prefuse.action.animate.QualityControlAnimator;
import prefuse.action.animate.VisibilityAnimator;
import prefuse.action.assignment.ColorAction;
import prefuse.action.layout.CollapsedSubtreeLayout;
import prefuse.action.layout.graph.NodeLinkTreeLayout;
import prefuse.activity.SlowInSlowOutPacer;
import prefuse.controls.Control;
import prefuse.controls.FocusControl;
import prefuse.controls.PanControl;
import prefuse.controls.ZoomToFitControl;
import prefuse.data.Schema;
import prefuse.data.Tree;
import prefuse.data.Tuple;
import prefuse.data.event.TupleSetListener;
import prefuse.data.search.PrefixSearchTupleSet;
import prefuse.data.tuple.TupleSet;
import prefuse.render.AbstractShapeRenderer;
import prefuse.render.DefaultRendererFactory;
import prefuse.render.EdgeRenderer;
import prefuse.render.LabelRenderer;
import prefuse.util.ColorLib;
import prefuse.util.PrefuseLib;
import prefuse.visual.NodeItem;
import prefuse.visual.VisualItem;
import prefuse.visual.expression.InGroupPredicate;
import prefuse.visual.sort.TreeDepthItemSorter;
import prefuse.visual.tuple.TableNodeItem;
import de.hochschuleTrier.fmv.controls.featureDiagram.FeatureDiagramAutoPanControl;
import de.hochschuleTrier.fmv.controls.featureDiagram.FeatureDiagramClickControl;
import de.hochschuleTrier.fmv.controls.featureDiagram.FeatureDiagramMouseWheelControl;
import de.hochschuleTrier.fmv.controls.featureDiagram.FeatureDiagramNodeColorControl;
import de.hochschuleTrier.fmv.controls.featureDiagram.FeatureDiagramNodeStrokeColorControl;
import de.hochschuleTrier.fmv.controls.featureDiagram.FeatureDiagramRightClickControl;
import de.hochschuleTrier.fmv.controls.featureDiagram.FeatureDiagramToolTipControl;
import de.hochschuleTrier.fmv.filter.featureDiagram.FeatureDiagramInspectNodeTreeFilter;
import de.hochschuleTrier.fmv.filter.featureDiagram.FisheyeTreeFilter;
import de.hochschuleTrier.fmv.filter.featureDiagram.ShowSelectedNodeFilter;
import de.hochschuleTrier.fmv.model.impl.ApplicationModel;
import de.hochschuleTrier.fmv.model.impl.FeatureSchema;
import de.hochschuleTrier.fmv.model.impl.UISettings;
import de.hochschuleTrier.fmv.model.impl.featureDiagram.FeatureDiagramConstraintDecoratorSchema;
import de.hochschuleTrier.fmv.model.interfaces.featureDiagram.IFeatureDiagramModel;
import de.hochschuleTrier.fmv.model.interfaces.listener.IApplicationModelListener;
import de.hochschuleTrier.fmv.model.interfaces.listener.IUISettingsListener;
import de.hochschuleTrier.fmv.util.NodeLib;
import de.hochschuleTrier.fmv.util.TreeDisplayEnum;
import de.hochschuleTrier.fmv.view.layout.FeatureDiagramComplexConstraintIconLayout;
import de.hochschuleTrier.fmv.view.layout.FeatureDiagramConstraintEdgeLayout;
import de.hochschuleTrier.fmv.view.render.ConstraintIconRenderer;
import de.hochschuleTrier.fmv.view.render.FeatureDiagramConstraintEdgeRenderer;
import de.hochschuleTrier.fmv.view.render.FeatureDiagramEdgeRenderer;
import de.hochschuleTrier.fmv.view.render.FeatureDiagramNodeLabelRenderer;

public class FeatureDiagramDisplay extends Display implements IApplicationModelListener, IUISettingsListener {

	private LabelRenderer nodeRenderer;
	private EdgeRenderer edgeRenderer;

	private final List<TableNodeItem> searchedItems;
	private int currentSearchItemIndex;

	public static final String CONSTRAINT_EDGE_DECORATORS = "constraintEdgeDecorator";
	public static final String COMPLEX_CONSTRAINT_DECORATORS = "complexConstraintDeco";

	private static final Schema COMPLEX_CONSTRAINT_DECORATOR_SCHEMA = PrefuseLib.getVisualItemSchema();
	static {
		COMPLEX_CONSTRAINT_DECORATOR_SCHEMA.setDefault(VisualItem.INTERACTIVE, true);
	}

	private final Tree tree;

	public FeatureDiagramDisplay(final Tree tree) {
		super(new Visualization());
		this.tree = tree;

		ApplicationModel.getInstance().addListener(this);
		UISettings.getInstance().addListener(this);

		this.searchedItems = new ArrayList<TableNodeItem>();

		this.setUpVisualization();
		this.setUpRenderers();
		this.setUpActions();
		this.setUpDisplay();
	}

	private void setUpVisualization() {
		this.m_vis.add(TreeDisplayEnum.TREE.toString(), this.tree);
		this.m_vis.setInteractive(TreeDisplayEnum.EDGES.toString(), null, false);

		this.setName("mainView");
	}

	private void setUpRenderers() {
		this.m_vis.addDecorators(CONSTRAINT_EDGE_DECORATORS, TreeDisplayEnum.NODES.toString(), new FeatureDiagramConstraintDecoratorSchema());
		this.m_vis.addDecorators(COMPLEX_CONSTRAINT_DECORATORS, TreeDisplayEnum.NODES.toString(), COMPLEX_CONSTRAINT_DECORATOR_SCHEMA);

		this.nodeRenderer = new FeatureDiagramNodeLabelRenderer(FeatureSchema.NAME);
		this.nodeRenderer.setRenderType(AbstractShapeRenderer.RENDER_TYPE_FILL);
		this.nodeRenderer.setHorizontalAlignment(Constants.LEFT);
		this.nodeRenderer.setRoundedCorner(8, 8);

		this.edgeRenderer = new FeatureDiagramEdgeRenderer(Constants.EDGE_TYPE_CURVE);
		this.edgeRenderer.setHorizontalAlignment1(Constants.RIGHT);
		this.edgeRenderer.setHorizontalAlignment2(Constants.LEFT);
		this.edgeRenderer.setVerticalAlignment1(Constants.CENTER);
		this.edgeRenderer.setVerticalAlignment2(Constants.CENTER);

		final DefaultRendererFactory rendererFactory = new DefaultRendererFactory(this.nodeRenderer);
		rendererFactory.add(new InGroupPredicate(TreeDisplayEnum.EDGES.toString()), this.edgeRenderer);
		rendererFactory.add(new InGroupPredicate(CONSTRAINT_EDGE_DECORATORS), new FeatureDiagramConstraintEdgeRenderer());
		rendererFactory.add(new InGroupPredicate(COMPLEX_CONSTRAINT_DECORATORS), new ConstraintIconRenderer());
		this.m_vis.setRendererFactory(rendererFactory);
	}

	private void setUpActions() {
		// colors
		final ItemAction textColor = new ColorAction(TreeDisplayEnum.NODES.toString(), VisualItem.TEXTCOLOR, ColorLib.rgb(0, 0, 0));
		this.m_vis.putAction("textColor", textColor);
		final ItemAction edgeColor = new ColorAction(TreeDisplayEnum.EDGES.toString(), VisualItem.STROKECOLOR, ColorLib.rgb(200, 200, 200));

		final ItemAction selectedNodeColor = new FeatureDiagramNodeColorControl(TreeDisplayEnum.NODES.toString(), VisualItem.FILLCOLOR);
		final ColorAction nodeStrokeColor = new FeatureDiagramNodeStrokeColorControl(TreeDisplayEnum.NODES.toString(), VisualItem.STROKECOLOR, Color.WHITE.getRGB());

		// animate paint change
		final ActionList animatePaint = new ActionList(400); // 400
		animatePaint.add(new ColorAnimator(TreeDisplayEnum.NODES.toString()));
		animatePaint.add(new RepaintAction());
		this.m_vis.putAction("animatePaint", animatePaint);

		// full paint for
		final ActionList fullPaint = new ActionList();
		fullPaint.add(textColor);
		fullPaint.add(edgeColor);
		fullPaint.add(selectedNodeColor);
		fullPaint.add(nodeStrokeColor);
		this.m_vis.putAction("fullPaint", fullPaint);

		// create the tree layout action
		final NodeLinkTreeLayout treeLayout = new NodeLinkTreeLayout(TreeDisplayEnum.TREE.toString(), Constants.ORIENT_LEFT_RIGHT, 50, 0, 8);
		treeLayout.setLayoutAnchor(new Point2D.Double(25, 300));
		this.m_vis.putAction("treeLayout", treeLayout);

		final CollapsedSubtreeLayout subLayout = new CollapsedSubtreeLayout(TreeDisplayEnum.TREE.toString(), Constants.ORIENT_LEFT_RIGHT);
		this.m_vis.putAction("subLayout", subLayout);

		// create the filtering and layout
		final ActionList filter = new ActionList();
		filter.add(new ShowSelectedNodeFilter());
		filter.add(new FisheyeTreeFilter(TreeDisplayEnum.TREE.toString()));
		filter.add(new FeatureDiagramInspectNodeTreeFilter(TreeDisplayEnum.TREE.toString(), ApplicationModel.getInstance().getCurrentFeatureTreeModel()));
		filter.add(treeLayout);
		filter.add(subLayout);
		filter.add(fullPaint);
		this.m_vis.putAction("filter", filter);

		final Action complexConstraintIconLayout = new FeatureDiagramComplexConstraintIconLayout(COMPLEX_CONSTRAINT_DECORATORS);
		this.m_vis.putAction("iconLayout", complexConstraintIconLayout);

		// animated transition
		final ActionList animate = new ActionList(1000);
		animate.setPacingFunction(new SlowInSlowOutPacer());
		animate.add(new FeatureDiagramConstraintEdgeLayout(CONSTRAINT_EDGE_DECORATORS, ApplicationModel.getInstance().getCurrentFeatureTreeModel()));
		animate.add(new FeatureDiagramAutoPanControl(this));
		animate.add(new QualityControlAnimator());
		animate.add(new VisibilityAnimator(TreeDisplayEnum.TREE.toString()));
		animate.add(new LocationAnimator(TreeDisplayEnum.NODES.toString()));
		animate.add(new ColorAnimator(TreeDisplayEnum.NODES.toString()));
		animate.add(complexConstraintIconLayout);
		animate.add(new RepaintAction());
		this.m_vis.putAction("animate", animate);
		this.m_vis.alwaysRunAfter("filter", "animate");

		this.m_vis.putAction("repaint", new RepaintAction());
	}

	private void setUpDisplay() {
		this.setItemSorter(new TreeDepthItemSorter());

		this.addControlListener(new FeatureDiagramRightClickControl(this.tree));
		this.addControlListener(new FeatureDiagramClickControl());
		this.addControlListener(new FeatureDiagramMouseWheelControl());
		this.addControlListener(new PanControl());
		this.addControlListener(new FocusControl(1, "filter"));
		this.addControlListener(new ZoomToFitControl(Control.MIDDLE_MOUSE_BUTTON));
		// this.addControlListener(new HighlightItemMouseover());
		this.addControlListener(new FeatureDiagramToolTipControl());

		// ------------------------------------------------

		this.m_vis.run("filter");

		final TupleSet search = new PrefixSearchTupleSet();
		this.m_vis.addFocusGroup(Visualization.SEARCH_ITEMS, search);
		search.addTupleSetListener(new TupleSetListener() {
			@Override
			public void tupleSetChanged(final TupleSet t, final Tuple[] add, final Tuple[] rem) {
				FeatureDiagramDisplay.this.searchedItems.clear();
				FeatureDiagramDisplay.this.currentSearchItemIndex = -1;

				final Iterator<Tuple> tuples = t.tuples();
				while (tuples.hasNext()) {
					final TableNodeItem node = (TableNodeItem) tuples.next();
					FeatureDiagramDisplay.this.searchedItems.add(node);
				}
				FeatureDiagramDisplay.this.selectNextSearchHit();
				FeatureDiagramDisplay.this.m_vis.cancel("animatePaint");
				FeatureDiagramDisplay.this.m_vis.run("fullPaint");
				FeatureDiagramDisplay.this.m_vis.run("animatePaint");
			}
		});
	}

	public void selectNextSearchHit() {
		this.currentSearchItemIndex++;
		this.selectSearchHit();
	}

	public void selectPreviousSearchHit() {
		this.currentSearchItemIndex--;
		this.selectSearchHit();
	}

	private void selectSearchHit() {
		if (!this.searchedItems.isEmpty()) {
			final ApplicationModel model = ApplicationModel.getInstance();
			final TableNodeItem node = this.searchedItems.get(this.currentSearchItemIndex % this.searchedItems.size());
			model.setSelectedNode(node);

			final TupleSet ts = this.getVisualization().getFocusGroup(Visualization.FOCUS_ITEMS);
			ts.clear();
			ts.addTuple(node);
			this.getVisualization().run("filter");
		}
	}

	@Override
	public void currentFeatureTreeModelHasChanged(final IFeatureDiagramModel currentFeatureTreeModel) {
		this.getVisualization().run("filter");
	}

	@Override
	public void nodeSelected(final NodeItem oldNode, final NodeItem newNode) {
		final TupleSet ts = this.getVisualization().getFocusGroup(Visualization.FOCUS_ITEMS);
		ts.clear();
		ts.addTuple(newNode);
		this.getVisualization().run("filter");
	}

	@Override
	public void nodePainted(final NodeItem node, final Color color) {
		if (node != null) {
			NodeLib.setColor(node, color);
			this.getVisualization().run("filter");
		}
	}

	@Override
	public void fisheyeDistanceChanged(final int newDistance) {
		this.getVisualization().run("filter");
	}

	@Override
	public void activeDisplayChanged(final Display display) {
	}

	@Override
	public void constraintFocusDistanceChanged(final int newDistance) {

	}

}
