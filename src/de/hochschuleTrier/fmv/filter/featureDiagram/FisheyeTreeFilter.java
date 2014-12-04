package de.hochschuleTrier.fmv.filter.featureDiagram;

import java.util.Iterator;

import prefuse.Constants;
import prefuse.Visualization;
import prefuse.action.GroupAction;
import prefuse.data.Graph;
import prefuse.data.Tree;
import prefuse.data.expression.Predicate;
import prefuse.util.PrefuseLib;
import prefuse.visual.EdgeItem;
import prefuse.visual.NodeItem;
import prefuse.visual.VisualItem;
import prefuse.visual.expression.InGroupPredicate;
import de.hochschuleTrier.fmv.model.impl.ApplicationModel;
import de.hochschuleTrier.fmv.model.impl.UISettings;
import de.hochschuleTrier.fmv.model.interfaces.featureDiagram.IFeatureDiagramModel;

/**
 * <p>
 * Filter Action that computes a fisheye degree-of-interest function over a tree structure (or the spanning tree of a graph structure). Visibility and DOI (degree-of-interest) values are set for the
 * nodes in the structure. This function includes current focus nodes, and includes neighbors only in a limited window around these foci. The size of this window is determined by the distance value
 * set for this action. All ancestors of a focus up to the root of the tree are considered foci as well. By convention, DOI values start at zero for focus nodes, with decreasing negative numbers for
 * each hop away from a focus.
 * </p>
 * 
 * <p>
 * This form of filtering was described by George Furnas as early as 1981. For more information about Furnas' fisheye view calculation and DOI values, take a look at G.W. Furnas,
 * "The FISHEYE View: A New Look at Structured Files," Bell Laboratories Tech. Report, Murray Hill, New Jersey, 1981. Available online at <a href="http://citeseer.nj.nec.com/furnas81fisheye.html">
 * http://citeseer.nj.nec.com/furnas81fisheye.html</a>.
 * </p>
 * 
 * @author <a href="http://jheer.org">jeffrey heer</a>
 * @author <a href="mailto:ommenj@hochschule-trier.de">Jürgen Ommen</a> (extended and customized)
 */
public class FisheyeTreeFilter extends GroupAction {

	private String m_sources;
	private final Predicate m_groupP;

	private int m_threshold;

	private NodeItem m_root;
	private double m_divisor;

	/**
	 * Create a new FisheyeTreeFilter that processes the given group.
	 * 
	 * @param group
	 *            the data group to process. This should resolve to a Graph instance, otherwise exceptions will result when this Action is run.
	 */
	public FisheyeTreeFilter(final String group) {
		this(group, 1);
	}

	/**
	 * Create a new FisheyeTreeFilter that processes the given group.
	 * 
	 * @param group
	 *            the data group to process. This should resolve to a Graph instance, otherwise exceptions will result when this Action is run.
	 * @param distance
	 *            the graph distance threshold from high-interest nodes past which nodes will not be visible nor expanded.
	 */
	public FisheyeTreeFilter(final String group, final int distance) {
		this(group, Visualization.FOCUS_ITEMS, distance);
	}

	/**
	 * Create a new FisheyeTreeFilter that processes the given group.
	 * 
	 * @param group
	 *            the data group to process. This should resolve to a Graph instance, otherwise exceptions will result when this Action is run.
	 * @param sources
	 *            the group to use as source nodes, representing the nodes of highest degree-of-interest.
	 * @param distance
	 *            the graph distance threshold from high-interest nodes past which nodes will not be visible nor expanded.
	 */
	public FisheyeTreeFilter(final String group, final String sources, final int distance) {
		super(group);
		this.m_sources = sources;
		this.m_threshold = -distance;
		this.m_groupP = new InGroupPredicate(PrefuseLib.getGroupName(group, Graph.NODES));
	}

	/**
	 * Get the graph distance threshold used by this filter. This is the threshold for high-interest nodes, past which nodes will not be visible nor expanded.
	 * 
	 * @return the graph distance threshold
	 */
	public int getDistance() {
		return -this.m_threshold;
	}

	/**
	 * Set the graph distance threshold used by this filter. This is the threshold for high-interest nodes, past which nodes will not be visible nor expanded.
	 * 
	 * @param distance
	 *            the graph distance threshold to use
	 */
	public void setDistance(final int distance) {
		this.m_threshold = -distance;
	}

	/**
	 * Get the name of the group to use as source nodes for measuring graph distance. These form the roots from which the graph distance is measured.
	 * 
	 * @return the source data group
	 */
	public String getSources() {
		return this.m_sources;
	}

	/**
	 * Set the name of the group to use as source nodes for measuring graph distance. These form the roots from which the graph distance is measured.
	 * 
	 * @param sources
	 *            the source data group
	 */
	public void setSources(final String sources) {
		this.m_sources = sources;
	}

	/**
	 * @see prefuse.action.GroupAction#run(double)
	 */
	@Override
	public void run(final double frac) {
		final IFeatureDiagramModel featureTreeModel = ApplicationModel.getInstance().getCurrentFeatureTreeModel();
		if (UISettings.getInstance().isFisheyeZoomEnabled() && !featureTreeModel.isInInspectingNodeMode()) {
			this.setDistance(UISettings.getInstance().getFisheyeDistance());
			this.doFilter();
			ApplicationModel.getInstance().updateConstraintView();
		}
	}

	private void doFilter() {
		final Tree tree = ((Graph) this.m_vis.getGroup(this.m_group)).getSpanningTree();
		this.m_divisor = tree.getNodeCount();
		this.m_root = (NodeItem) tree.getRoot();

		// mark the items
		Iterator items = this.m_vis.visibleItems(this.m_group);
		while (items.hasNext()) {
			final VisualItem item = (VisualItem) items.next();
			item.setDOI(Constants.MINIMUM_DOI);
			item.setExpanded(false);
		}

		// compute the fisheye over nodes
		final Iterator iter = this.m_vis.items(this.m_sources, this.m_groupP);
		boolean focussedItems = false;
		while (iter.hasNext()) {
			this.visitFocus((NodeItem) iter.next(), null, 0);
			focussedItems = true;
		}
		if (!focussedItems) {
			this.visitFocus(this.m_root, null, 0);
		}

		// mark unreached items
		items = this.m_vis.visibleItems(this.m_group);
		while (items.hasNext()) {
			final VisualItem item = (VisualItem) items.next();
			if (item.getDOI() == Constants.MINIMUM_DOI) {
				PrefuseLib.updateVisible(item, false);
			}
		}
	}

	/**
	 * Visit a focus node.
	 */
	private void visitFocus(final NodeItem node, final NodeItem child, final double doi) {
		if (node.getDOI() <= -1) {
			if (node.getDOI() < doi) {
				this.visit(node, child, doi);
				if (this.m_threshold < 0) {
					this.visitDescendants(node, child);
				}
			}
			this.visitAncestors(node);
		}
	}

	/**
	 * Visit a specific node and update its degree-of-interest.
	 */
	private void visit(final NodeItem node, final NodeItem child, final double doi) {
		PrefuseLib.updateVisible(node, true);
		node.setDOI(doi);

		if (child != null) {
			final EdgeItem edge = (EdgeItem) child.getParentEdge();
			edge.setDOI(child.getDOI());
			PrefuseLib.updateVisible(edge, true);
		}
	}

	/**
	 * Visit tree ancestors and their other descendants.
	 */
	private void visitAncestors(final NodeItem n) {
		if (n != this.m_root) {
			this.visitFocus((NodeItem) n.getParent(), n, n.getDOI() + ((n.getDOI() - 1) * .5));
		}
	}

	/**
	 * Traverse tree descendents.
	 */
	private void visitDescendants(final NodeItem parent, final NodeItem skip) {
		final Iterator children = parent.children();

		parent.setExpanded(children.hasNext());

		while (children.hasNext()) {
			final NodeItem child = (NodeItem) children.next();
			if (child == skip) {
				continue;
			}

			final double doi = parent.getDOI() - 1;
			if (child.getDOI() < doi) {
				this.visit(child, child, doi);
				if (doi > this.m_threshold) {
					this.visitDescendants(child, null);
				}
			}
		}
	}
}