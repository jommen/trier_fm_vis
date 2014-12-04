package de.hochschuleTrier.fmv.util;

import java.util.Iterator;

import prefuse.Constants;
import prefuse.util.collections.Queue;
import prefuse.visual.EdgeItem;
import prefuse.visual.NodeItem;
import prefuse.visual.VisualItem;

/**
 * 
 * Provides a distance-limited breadth first traversal over nodeitems, edgeitems, or both, using any number of traversal "roots".
 * 
 * This modification of the BreadthFirstIterator class from the prefuse library acts on visual items and takes the visibility of the items into account if requested.
 * 
 * By default, invisible items are excluded, which also has the effect that invisible edges are not traversed.
 * 
 * @author <a href="http://jheer.org">jeffrey heer</a> (original BreadthFirstIterator)
 * @author <a href="http://goosebumps4all.net">martin dudek</a> (made it visual)
 */

public class VisualBreadthFirstIterator implements Iterator {

	protected Queue m_queue = new Queue();
	protected int m_depth;
	protected int m_traversal;
	protected boolean m_includeNodeItems;
	protected boolean m_includeEdgeItems;
	private boolean excludeInvisible = true;

	/**
	 * Create an uninitialized BreadthFirstIterator. Use the {@link #init(Object, int, int)} method to initialize the iterator.
	 */
	public VisualBreadthFirstIterator() {
		// do nothing, requires init call
	}

	/**
	 * Create a new BreadthFirstIterator starting from the given source node item.
	 * 
	 * @param ni
	 *            the source node item from which to begin the traversal
	 * @param depth
	 *            the maximum graph distance to traverse
	 * @param traversal
	 *            the traversal type, one of {@link prefuse.Constants#NODE_TRAVERSAL}, {@link prefuse.Constants#EDGE_TRAVERSAL}, or {@link prefuse.Constants#NODE_AND_EDGE_TRAVERSAL}
	 */
	public VisualBreadthFirstIterator(final NodeItem ni, final int depth, final int traversal) {
		this.init(new NodeItem[] { ni }, depth, traversal);
	}

	/**
	 * Create a new BreadthFirstIterator starting from the given source node items.
	 * 
	 * @param it
	 *            an Iterator over the source node items from which to begin the traversal
	 * @param depth
	 *            the maximum graph distance to traverse
	 * @param traversal
	 *            the traversal type, one of {@link prefuse.Constants#NODE_TRAVERSAL}, {@link prefuse.Constants#EDGE_TRAVERSAL}, or {@link prefuse.Constants#NODE_AND_EDGE_TRAVERSAL}
	 */
	public VisualBreadthFirstIterator(final Iterator it, final int depth, final int traversal) {
		this.init(it, depth, traversal);
	}

	/**
	 * Initialize (or re-initialize) this iterator.
	 * 
	 * @param o
	 *            Either a source node item or iterator over source node items
	 * @param depth
	 *            the maximum graph distance to traverse
	 * @param traversal
	 *            the traversal type, one of {@link prefuse.Constants#NODE_TRAVERSAL}, {@link prefuse.Constants#EDGE_TRAVERSAL}, or {@link prefuse.Constants#NODE_AND_EDGE_TRAVERSAL}
	 */
	public void init(final Object o, final int depth, final int traversal) {
		// initialize the member variables
		this.m_queue.clear();
		this.m_depth = depth;
		if ((traversal < 0) || (traversal >= Constants.TRAVERSAL_COUNT)) {
			throw new IllegalArgumentException("Unrecognized traversal type: " + traversal);
		}
		this.m_traversal = traversal;
		this.m_includeNodeItems = ((traversal == Constants.NODE_TRAVERSAL) || (traversal == Constants.NODE_AND_EDGE_TRAVERSAL));
		this.m_includeEdgeItems = ((traversal == Constants.EDGE_TRAVERSAL) || (traversal == Constants.NODE_AND_EDGE_TRAVERSAL));

		// seed the queue
		// TODO: clean this up? (use generalized iterator?)
		if (this.m_includeNodeItems) {
			if (o instanceof NodeItem) {
				if (this.checkVisible((NodeItem) o)) {
					this.m_queue.add(o, 0);
				}
			}
			else {
				final Iterator items = (Iterator) o;
				while (items.hasNext()) {
					final NodeItem ni = (NodeItem) items.next();
					if (this.checkVisible(ni)) {
						this.m_queue.add(ni, 0);
					}
				}
			}
		}
		else {
			if ((o instanceof NodeItem) && this.checkVisible((NodeItem) o)) {
				final NodeItem ni = (NodeItem) o;
				this.m_queue.visit(ni, 0);
				final Iterator edgeItems = this.getEdges(ni);
				while (edgeItems.hasNext()) {
					final EdgeItem ei = (EdgeItem) edgeItems.next();
					if (!this.checkVisible(ei)) {
						continue;
					}
					final NodeItem nni = ei.getAdjacentItem(ni);
					if (!this.checkVisible(nni)) {
						continue;
					}
					this.m_queue.visit(nni, 1);
					if (this.m_queue.getDepth(ei) < 0) {
						this.m_queue.add(ei, 1);
					}
				}
			}
			else {
				final Iterator items = (Iterator) o;
				while (items.hasNext()) {
					// TODO: graceful error handling when non-node in set?
					final NodeItem ni = (NodeItem) items.next();
					if (!this.checkVisible(ni)) {
						continue;
					}
					this.m_queue.visit(ni, 0);
					final Iterator edgeItems = this.getEdges(ni);
					while (edgeItems.hasNext()) {
						final EdgeItem ei = (EdgeItem) edgeItems.next();
						if (!this.checkVisible(ei)) {
							continue;
						}
						final NodeItem nni = ei.getAdjacentItem(ni);
						if (!this.checkVisible(nni)) {
							continue;
						}
						this.m_queue.visit(nni, 1);
						if (this.m_queue.getDepth(ei) < 0) {
							this.m_queue.add(ei, 1);
						}
					}
				}
			}
		}
	}

	// ------------------------------------------------------------------------

	/**
	 * @see java.util.Iterator#remove()
	 */
	@Override
	public void remove() {
		throw new UnsupportedOperationException();
	}

	/**
	 * @see java.util.Iterator#hasNext()
	 */
	@Override
	public boolean hasNext() {
		return !this.m_queue.isEmpty();
	}

	/**
	 * Determines which edges are traversed for a given node.
	 * 
	 * @param n
	 *            a node
	 * @return an iterator over edges incident on the node
	 */
	protected Iterator getEdges(final NodeItem ni) {
		return ni.edges();
	}

	/**
	 * Get the traversal depth at which a particular VisualItem was encountered.
	 * 
	 * @param t
	 *            the VisualItem to lookup
	 * @return the traversal depth of the VisualItem, or -1 if the VisualItem has not been visited by the traversal.
	 */
	public int getDepth(final VisualItem t) {
		return this.m_queue.getDepth(t);
	}

	/**
	 * @see java.util.Iterator#next()
	 */
	@Override
	public Object next() {
		VisualItem t = (VisualItem) this.m_queue.removeFirst();

		switch (this.m_traversal) {

			case Constants.NODE_TRAVERSAL:
			case Constants.NODE_AND_EDGE_TRAVERSAL:
				for (; true; t = (VisualItem) this.m_queue.removeFirst()) {
					if (t instanceof EdgeItem) {
						return t;
					}
					else {
						final NodeItem ni = (NodeItem) t;
						if (!this.checkVisible(ni)) {
							continue;
						}

						final int d = this.m_queue.getDepth(ni);

						if (d < this.m_depth) {
							final int dd = d + 1;
							final Iterator edgeItems = this.getEdges(ni);
							while (edgeItems.hasNext()) {
								final EdgeItem ei = (EdgeItem) edgeItems.next();
								if (!this.checkVisible(ei)) {
									continue;
								}
								final NodeItem vi = ei.getAdjacentItem(ni);
								if (!this.checkVisible(vi)) {
									continue;
								}
								if (this.m_includeEdgeItems && (this.m_queue.getDepth(ei) < 0)) {
									this.m_queue.add(ei, dd);
								}
								if (this.m_queue.getDepth(vi) < 0) {
									this.m_queue.add(vi, dd);
								}
							}
						}
						else if (this.m_includeEdgeItems && (d == this.m_depth)) {
							final Iterator edgeItems = this.getEdges(ni);
							while (edgeItems.hasNext()) {
								final EdgeItem ei = (EdgeItem) edgeItems.next();
								if (!this.checkVisible(ei)) {
									continue;
								}
								final NodeItem vi = ei.getAdjacentItem(ni);
								if (!this.checkVisible(vi)) {
									continue;
								}
								final int dv = this.m_queue.getDepth(vi);
								if ((dv > 0) && (this.m_queue.getDepth(ei) < 0)) {
									this.m_queue.add(ei, Math.min(d, dv));
								}
							}
						}
						return ni;
					}
				}

			case Constants.EDGE_TRAVERSAL:
				final EdgeItem ei = (EdgeItem) t;

				final NodeItem ui = ei.getSourceItem();
				final NodeItem vi = ei.getTargetItem();
				final int du = this.m_queue.getDepth(ui);
				final int dv = this.m_queue.getDepth(vi);

				if (du != dv) {
					final NodeItem ni = (dv > du ? vi : ui);
					final int d = Math.max(du, dv);

					if (d < this.m_depth) {
						final int dd = d + 1;
						final Iterator edgeItems = this.getEdges(ni);
						while (edgeItems.hasNext()) {
							final EdgeItem eei = (EdgeItem) edgeItems.next();
							if (!this.checkVisible(eei)) {
								continue;
							}
							if (this.m_queue.getDepth(eei) >= 0) {
								continue; // already visited
							}

							final NodeItem nni = eei.getAdjacentItem(ni);
							this.m_queue.visit(nni, dd);
							this.m_queue.add(eei, dd);
						}
					}
				}
				return ei;

			default:
				throw new IllegalStateException();
		}
	}

	/**
	 * Indicates if invisible items are excluded
	 * 
	 * @return true if invisible items are excluded, false otherwise.
	 */
	public boolean isHighlightWithInvisibleEdge() {
		return this.excludeInvisible;
	}

	/**
	 * Determines if invisible items should be excluded
	 * 
	 * @param excludeInvisible
	 *            assign true if invisible items should be excluded, false otherwise highlightWithInvisibleEdge assign true if neighbors with invisible edges should still get highlighted, false
	 *            otherwise.
	 */
	public void setExcludeInvisible(final boolean excludeInvisible) {
		this.excludeInvisible = excludeInvisible;
	}

	/**
	 * checks if the given visual item should be taking into account; this is the case when the item is visible or if also invisible items should be used
	 * 
	 * @param vi
	 *            the visual item to be checked
	 * @return
	 */
	private boolean checkVisible(final VisualItem vi) {
		return vi.isVisible() || (!this.excludeInvisible);
	}

} // end of class BreadthFirstIterator
