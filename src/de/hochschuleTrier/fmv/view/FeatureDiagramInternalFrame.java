package de.hochschuleTrier.fmv.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JInternalFrame;
import javax.swing.event.InternalFrameEvent;
import javax.swing.event.InternalFrameListener;

import prefuse.Visualization;
import prefuse.util.FontLib;
import prefuse.util.ui.JSearchPanel;
import de.hochschuleTrier.fmv.model.impl.ApplicationModel;
import de.hochschuleTrier.fmv.model.impl.FeatureSchema;
import de.hochschuleTrier.fmv.model.interfaces.featureDiagram.IFeatureDiagramModel;
import de.hochschuleTrier.fmv.model.interfaces.listener.IFeatureModelClosedListener;
import de.hochschuleTrier.fmv.util.TreeDisplayEnum;
import de.hochschuleTrier.fmv.view.display.FeatureDiagramDisplay;

public class FeatureDiagramInternalFrame extends JInternalFrame implements InternalFrameListener, IFeatureModelClosedListener {

	private final IFeatureDiagramModel treeModel;
	private FeatureDiagramDisplay treeView;
	private final ApplicationModel applicationModel;
	private final boolean mainFeatureDiagramFrame;

	public FeatureDiagramInternalFrame(final IFeatureDiagramModel treeModel) {
		this(treeModel, false);
	}

	public FeatureDiagramInternalFrame(final IFeatureDiagramModel treeModel, final boolean mainFeatureDiagramFrame) {
		super("Feature Diagram", true, true, true, true);
		this.treeModel = treeModel;
		this.mainFeatureDiagramFrame = mainFeatureDiagramFrame;
		this.applicationModel = ApplicationModel.getInstance();
		this.applicationModel.addFeatureModelClosedListener(this);
		this.setSize(400, 400);
		this.addInternalFrameListener(this);
		this.init();
	}

	private void init() {
		final Color BACKGROUND = Color.WHITE;
		final Color FOREGROUND = Color.BLACK;

		this.treeView = new FeatureDiagramDisplay(this.treeModel.getTree());
		this.treeModel.setDisplay(this.treeView);

		this.treeView.setBackground(BACKGROUND);
		this.treeView.setForeground(FOREGROUND);

		final JSearchPanel search = new JSearchPanel(this.treeView.getVisualization(), TreeDisplayEnum.NODES.toString(), Visualization.SEARCH_ITEMS, FeatureSchema.NAME, true, false);
		search.setShowResultCount(true);
		search.setBorder(BorderFactory.createEmptyBorder(5, 5, 4, 0));
		search.setFont(FontLib.getFont("Tahoma", Font.PLAIN, 11));
		search.setBackground(BACKGROUND);
		search.setForeground(FOREGROUND);

		final JButton nextHitButton = new JButton("Next");
		nextHitButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				FeatureDiagramInternalFrame.this.treeView.selectNextSearchHit();
			}
		});
		final JButton previousHitButton = new JButton("Previous");
		previousHitButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				FeatureDiagramInternalFrame.this.treeView.selectPreviousSearchHit();
			}
		});
		final JButton searchButton = new JButton("Search");
		searchButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				search.setQuery(search.getQuery());
			}
		});

		final Box box = new Box(BoxLayout.Y_AXIS);

		final Box searchAndButtonBox = new Box(BoxLayout.X_AXIS);
		searchAndButtonBox.add(search);
		searchAndButtonBox.add(searchButton);
		searchAndButtonBox.add(previousHitButton);
		searchAndButtonBox.add(nextHitButton);
		searchAndButtonBox.setBackground(BACKGROUND);

		box.add(searchAndButtonBox);

		this.setBackground(BACKGROUND);
		this.setForeground(FOREGROUND);
		this.add(this.treeView, BorderLayout.CENTER);
		this.add(box, BorderLayout.SOUTH);
	}

	public FeatureDiagramDisplay getTreeView() {
		return this.treeView;
	}

	public void setTreeView(final FeatureDiagramDisplay treeView) {
		this.treeView = treeView;
	}

	@Override
	public void internalFrameOpened(final InternalFrameEvent e) {
	}

	@Override
	public void internalFrameClosing(final InternalFrameEvent e) {
		if (this.mainFeatureDiagramFrame) {
			this.applicationModel.notifyFeatureModelClosed();
		}
	}

	@Override
	public void internalFrameClosed(final InternalFrameEvent e) {
	}

	@Override
	public void internalFrameIconified(final InternalFrameEvent e) {
	}

	@Override
	public void internalFrameDeiconified(final InternalFrameEvent e) {
	}

	@Override
	public void internalFrameActivated(final InternalFrameEvent e) {
		this.applicationModel.setCurrentFeatureTreeModel(this.treeModel);
		this.applicationModel.setActiveDisplay(this.treeView);
	}

	@Override
	public void internalFrameDeactivated(final InternalFrameEvent e) {
	}

	@Override
	public void featureModelClosed() {
		this.dispose();
	}
}
