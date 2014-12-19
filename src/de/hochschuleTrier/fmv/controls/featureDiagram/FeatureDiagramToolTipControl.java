package de.hochschuleTrier.fmv.controls.featureDiagram;

import java.awt.event.MouseEvent;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map.Entry;

import javax.swing.ToolTipManager;

import prefuse.Display;
import prefuse.controls.ControlAdapter;
import prefuse.visual.NodeItem;
import prefuse.visual.VisualItem;
import de.hochschuleTrier.fmv.model.impl.UISettings;
import de.hochschuleTrier.fmv.util.NodeLib;
import de.hochschuleTrier.fmv.util.Toolbox;

public class FeatureDiagramToolTipControl extends ControlAdapter {

	private static final int DISMISS_DELAY = Integer.MAX_VALUE;
	private static final int INITIAL_DELAY = 200;

	public FeatureDiagramToolTipControl() {
		ToolTipManager.sharedInstance().setInitialDelay(FeatureDiagramToolTipControl.INITIAL_DELAY);
	}

	@Override
	public void itemEntered(final VisualItem vi, final MouseEvent e) {
		if (UISettings.getInstance().isShowToolTip() && vi instanceof NodeItem) {
			this.handleNodeItemEntered((NodeItem) vi, e);
		}
	}

	private void handleNodeItemEntered(final NodeItem node, final MouseEvent e) {
		ToolTipManager.sharedInstance().setDismissDelay(FeatureDiagramToolTipControl.DISMISS_DELAY);

		final LinkedHashMap<String, String> nodeInfos = this.getNodeInfos(node);

		final StringBuffer infoString = new StringBuffer();
		infoString.append("<html><table>");

		final Iterator<Entry<String, String>> iter = nodeInfos.entrySet().iterator();
		while (iter.hasNext()) {
			final Entry<String, String> info = iter.next();
			infoString.append(String.format("<tr><td valign='top'>%s</td><td valign='top'>%s</td>", info.getKey(), info.getValue()));
		}

		infoString.append("</table></html>");
		final Display disp = (Display) e.getSource(); // the display
		disp.setToolTipText(infoString.toString());

	}

	private LinkedHashMap<String, String> getNodeInfos(final NodeItem node) {
		final LinkedHashMap<String, String> nodeInfos = new LinkedHashMap<String, String>();
		nodeInfos.put("Name", NodeLib.getName(node));
		nodeInfos.put("DOI", String.valueOf(node.getDOI()));

		return nodeInfos;
	}

	@Override
	public void itemExited(final VisualItem item, final MouseEvent e) {
		final Display disp = (Display) e.getSource();
		disp.setToolTipText(null);
	}

}
