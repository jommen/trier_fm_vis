package de.hochschuleTrier.fmv.util;

import java.util.StringTokenizer;

import javax.swing.JTree;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

/**
 * 
 * @author santhosh kumar T - santhosh@in.fiorano.com
 */
public class JTreeUtil {

	/**
	 * Is path1 descendant of path2?
	 */
	public static boolean isDescendant(TreePath path1, final TreePath path2) {
		int count1 = path1.getPathCount();
		final int count2 = path2.getPathCount();
		if (count1 <= count2) {
			return false;
		}
		while (count1 != count2) {
			path1 = path1.getParentPath();
			count1--;
		}
		return path1.equals(path2);
	}

	public static String getExpansionState(final JTree tree, final int row) {
		final TreePath rowPath = tree.getPathForRow(row);
		final StringBuffer buf = new StringBuffer();
		final int rowCount = tree.getRowCount();
		for (int i = row; i < rowCount; i++) {
			final TreePath path = tree.getPathForRow(i);
			if (i == row || JTreeUtil.isDescendant(path, rowPath)) {
				if (tree.isExpanded(path)) {
					buf.append("," + String.valueOf(i - row));
				}
			}
			else {
				break;
			}
		}
		return buf.toString();
	}

	public static void restoreExpanstionState(final JTree tree, final int row, final String expansionState) {
		final StringTokenizer stok = new StringTokenizer(expansionState, ",");
		while (stok.hasMoreTokens()) {
			final int token = row + Integer.parseInt(stok.nextToken());
			tree.expandRow(token);
		}
	}

	public static void expandAll(final JTree tree, final boolean expand) {
		final Object root = tree.getModel().getRoot();

		JTreeUtil.expandAll(tree, new TreePath(root), expand);
	}

	private static void expandAll(final JTree tree, final TreePath parent, final boolean expand) {
		final Object node = parent.getLastPathComponent();
		final TreeModel model = tree.getModel();
		if (!model.isLeaf(node)) {
			for (int i = 0; i < model.getChildCount(node); i++) {
				final Object child = model.getChild(node, i);
				final TreePath path = parent.pathByAddingChild(child);
				JTreeUtil.expandAll(tree, path, expand);
			}
		}

		if (expand) {
			tree.expandPath(parent);
		}
		else {
			tree.collapsePath(parent);
		}
	}
}