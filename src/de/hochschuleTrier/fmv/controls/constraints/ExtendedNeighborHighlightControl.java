package de.hochschuleTrier.fmv.controls.constraints;

import java.awt.event.MouseEvent;

import prefuse.Constants;
import prefuse.action.assignment.ColorAction;
import prefuse.controls.ControlAdapter;
import prefuse.data.Graph;
import prefuse.util.ColorLib;
import prefuse.visual.NodeItem;
import prefuse.visual.VisualItem;
import de.hochschuleTrier.fmv.util.VisualBreadthFirstIterator;

/**
 * Modification of the NeighborHighlightControl of the prefuse library. This
 * control enables the user to specify the distance in which neighbour nodes
 * should be highlighted.
 * 
 * The color action returned by the method getHighlightColorAction() of this
 * class must be used on the nodes for which the highlight flag is set
 * (VisualItem.HIGHLIGHT predicate) to change the node appearance as desired.
 * (Check the related demo coming along with the profusians library to get an
 * idea what that means)
 * 
 * The nodes can be either all highlighted in the same color or in dependence to
 * the distance from the node the mouse points to according to a given color
 * palette.
 * 
 * This color palette can be either specified by an explicit array of colors or
 * through a start and end color, which is interpolated according to the given
 * distance.
 * 
 * By default, invisible items are not taking into account, which includes that
 * nodes connect through invisible edges are not counted as neighbors. This
 * behavior can be changed through the setHighlightWithInvisibleEdge() method.
 * 
 * 
 * </p>
 * 
 * @author <a href="http://goosebumps4all.net">martin dudek</a>
 * @author <a href="http://jheer.org">jeffrey heer</a>
 */
public class ExtendedNeighborHighlightControl extends ControlAdapter {

	private String m_activity = null;

	protected VisualBreadthFirstIterator m_vbfs;

	protected int m_distance;

	private final HighlightColorAction m_colorAction;

	private boolean m_dynamicPalette = false;

	private int m_startColor, m_endColor;

	private boolean m_highlightWithInvisibleEdge = false;

	/**
	 * Creates a new highlight control with distance 1
	 */
	public ExtendedNeighborHighlightControl(final int color) {
		this(1, color, color, null);
	}

	/**
	 * Creates a new highlight control. All nodes within the given distance are
	 * highlighted in the specified color.
	 * 
	 * @param distance
	 *            the graph distance from the node the mouse points to in which
	 *            nodes should be highlighted
	 * @param color
	 *            the highlight color
	 */

	public ExtendedNeighborHighlightControl(final int distance, final int color) {
		this(distance, new int[] { color }, null);
	}

	/**
	 * Creates a new highlight control. A color palette if the size of the
	 * parameter distance is created by interpolating the given colors
	 * startColor and endColor,
	 * 
	 * All nodes within the given distance are highlighted according to this
	 * color palette, the directed neighbours in the startColor, the nodes at
	 * maximal distance in the endColor, all nodes inbetween according to other
	 * colors of the color palette.
	 * 
	 * If the distance of the control is changed later on, a new color palette
	 * is created using the same start and end color.
	 * 
	 * @param distance
	 *            the graph distance from the node the mouse points to in which
	 *            nodes should be highlighted
	 * @param startColor
	 *            the first color of the color palette
	 * @param endColor
	 *            the last color of the color palette
	 */

	public ExtendedNeighborHighlightControl(final int distance, final int startColor, final int endColor) {
		this(distance, startColor, endColor, null);
	}

	/**
	 * Creates a new highlight control that runs the given activity whenever the
	 * neighbor highlight changes.
	 * 
	 * A color palette if the size of the parameter distance is created by
	 * interpolating the given colors startColor and endColor,
	 * 
	 * All nodes within the given distance are highlighted according to this
	 * color palette, the directed neighbours in the startColor, the nodes at
	 * maximal distance in the endColor, all nodes inbetween according to other
	 * colors of the color palette.
	 * 
	 * If the distance of the control is changed later on, a new color palette
	 * is created using the same start and end color.
	 * 
	 * @param distance
	 *            the graph distance from the node the mouse points to in which
	 *            nodes should be highlighted
	 * @param startColor
	 *            the first color of the color palette
	 * @param endColor
	 *            the last color of the color palette
	 * @param activity
	 *            the update Activity to run
	 */

	public ExtendedNeighborHighlightControl(final int distance, final int startColor, final int endColor, final String activity) {

		this(distance, ColorLib.getInterpolatedPalette(distance, startColor, endColor), activity);
		this.m_startColor = startColor;
		this.m_endColor = endColor;
		this.m_dynamicPalette = true;
	}

	/**
	 * Creates a new highlight control.
	 * 
	 * All nodes within the given distance are highlighted according to the
	 * given color palette, the directed neighbours in the first color of the
	 * palette, the nodes with graph distance two in the second color and so on.
	 * 
	 * The last color of the given color palette is used for all neighbor nodes
	 * with a distance larger than the size of the color palette.
	 * 
	 * @param distance
	 *            the graph distance from the node the mouse points to in which
	 *            nodes should be highlighted
	 * @param colorPalette
	 *            the color palette to be used
	 */

	public ExtendedNeighborHighlightControl(final int distance, final int[] colorPalette) {
		this(distance, colorPalette, null);
	}

	/**
	 * Creates a new highlight control that runs the given activity whenever the
	 * neighbor highlight changes.
	 * 
	 * All nodes within the given distance are highlighted according to the
	 * given color palette, the directed neighbours in the first color of the
	 * palette, the nodes with graph distance two in the second color and so on.
	 * 
	 * The last color of the given color palette is used for all neighbor nodes
	 * with a distance bigger than the size of the color palette.
	 * 
	 * @param distance
	 *            the graph distance from the node the mouse points to in which
	 *            nodes should be highlighted
	 * @param colorPalette
	 *            the color palette to be used
	 * @param activity
	 *            the update Activity to run
	 */

	public ExtendedNeighborHighlightControl(final int distance, final int[] colorPalette, final String activity) {

		this.m_distance = distance;
		this.m_vbfs = new VisualBreadthFirstIterator();

		this.m_activity = activity;

		this.m_colorAction = new HighlightColorAction(distance, colorPalette);

	}

	/**
	 * @see prefuse.controls.Control#itemEntered(prefuse.visual.VisualItem, java.awt.event.MouseEvent)
	 */
	@Override
	public void itemEntered(final VisualItem item, final MouseEvent e) {
		if (item instanceof NodeItem) {
			this.setNeighborHighlight((NodeItem) item, true);
		}
	}

	/**
	 * @see prefuse.controls.Control#itemExited(prefuse.visual.VisualItem, java.awt.event.MouseEvent)
	 */
	@Override
	public void itemExited(final VisualItem item, final MouseEvent e) {
		if (item instanceof NodeItem) {
			this.setNeighborHighlight((NodeItem) item, false);
		}
	}

	/**
	 * Set the highlighted state of the neighbors of a node.
	 * 
	 * @param ni
	 *            the node under consideration
	 * @param state
	 *            the highlighting state to apply to neighbors
	 */
	protected void setNeighborHighlight(final NodeItem ni, final boolean state) {

		this.m_vbfs.init(ni, this.m_distance, Constants.NODE_AND_EDGE_TRAVERSAL);

		this.m_vbfs.setExcludeInvisible(!this.m_highlightWithInvisibleEdge);

		while (this.m_vbfs.hasNext()) {
			final VisualItem item = (VisualItem) this.m_vbfs.next();
			final int d = this.m_vbfs.getDepth(item);
			item.setHighlighted(state && (d > 0));
			item.setDOI(-d);
		}

		if (this.m_activity != null) {
			ni.getVisualization().run(this.m_activity);
		}
	}

	/**
	 * returns the color action which must be used to highlight the neighbors
	 * 
	 * @return
	 */

	public ColorAction getHighlightColorAction() {
		return this.m_colorAction;
	}

	/**
	 * sets the graph distance in which neighbors should be highlighted
	 * 
	 * @param distance
	 *            the distance to be used by the control
	 */
	public void setDistance(final int distance) {

		this.m_distance = distance;

		if (this.m_dynamicPalette && (distance > 1)) {
			// recalculating the color palette if dynamic
			this.m_colorAction.setColors(ColorLib.getInterpolatedPalette(distance, this.m_startColor, this.m_endColor));
		}

	}

	/**
	 * returns the distance used by the control
	 * 
	 * @return the distance used by this control
	 */
	public int getDistance() {
		return this.m_distance;
	}

	/**
	 * Indicates if neighbor nodes with edges currently not visible still get
	 * highlighted.
	 * 
	 * @return true if neighbors with invisible edges still get highlighted,
	 *         false otherwise.
	 */
	public boolean isHighlightWithInvisibleEdge() {
		return this.m_highlightWithInvisibleEdge;
	}

	/**
	 * Determines if neighbor nodes with edges currently not visible still get
	 * highlighted.
	 * 
	 * @param highlightWithInvisibleEdge
	 *            assign true if neighbors with invisible edges should still get
	 *            highlighted, false otherwise.
	 */
	public void setHighlightWithInvisibleEdge(final boolean highlightWithInvisibleEdge) {
		this.m_highlightWithInvisibleEdge = highlightWithInvisibleEdge;
	}

	private class HighlightColorAction extends ColorAction {

		int[] m_colors;

		public HighlightColorAction(final int size, final int[] colors) {
			super(Graph.NODES, VisualItem.FILLCOLOR);

			this.m_colors = colors;

		}

		public void setColors(final int[] colors) {
			this.m_colors = colors;
		}

		public int[] getColors() {
			return this.m_colors;
		}

		@Override
		public int getColor(final VisualItem item) {
			if (item == null) {
				return 0;
			}
			int moi = -1 * (int) item.getDOI() - 1;
			if (item.isHighlighted() && (moi >= 0)) {
				moi = moi >= this.m_colors.length ? this.m_colors.length - 1 : moi;
				return this.m_colors[moi];
			}

			return 0;
		}

	}

} // end of class ExtendedNeighborHighlightControl
