package de.hochschuleTrier.fmv.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JInternalFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JToggleButton;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.InternalFrameEvent;
import javax.swing.event.InternalFrameListener;

import prefuse.Display;
import prefuse.Visualization;
import prefuse.action.Action;
import prefuse.util.FontLib;
import prefuse.util.ui.JSearchPanel;
import prefuse.util.ui.UILib;
import de.hochschuleTrier.fmv.model.impl.ApplicationModel;
import de.hochschuleTrier.fmv.model.impl.FeatureSchema;
import de.hochschuleTrier.fmv.model.impl.UISettings;
import de.hochschuleTrier.fmv.model.impl.constraints.ConstraintNodesToInspectInspectListModel;
import de.hochschuleTrier.fmv.model.interfaces.constraints.IConstraintModel;
import de.hochschuleTrier.fmv.model.interfaces.listener.IFeatureModelClosedListener;
import de.hochschuleTrier.fmv.model.interfaces.listener.IUISettingsListener;
import de.hochschuleTrier.fmv.util.TreeDisplayEnum;
import de.hochschuleTrier.fmv.util.UIConstants;
import de.hochschuleTrier.fmv.view.display.ConstraintDisplay;

public class ConstraintInternalFrame extends JInternalFrame implements InternalFrameListener, IUISettingsListener, IFeatureModelClosedListener {

	private final IConstraintModel constraintModel;
	private JSlider slider;

	public ConstraintInternalFrame(final IConstraintModel constraintModel) {
		super("Constraint Diagram", true, true, true, true);
		this.addInternalFrameListener(this);
		UISettings.getInstance().addListener(this);
		ApplicationModel.getInstance().addFeatureModelClosedListener(this);
		this.constraintModel = constraintModel;
		this.setSize(600, 400);
		this.init();
	}

	private void init() {
		// define edge colors
		final Map<String, Color> colorMap = new HashMap<String, Color>();
		colorMap.put("excludedEdgeColor", Color.RED);
		colorMap.put("requiresEdgeColor", Color.GREEN);

		final JPanel InspectNodesBox = this.createInspectNodesBox();

		final ConstraintDisplay constraintDisplay = new ConstraintDisplay(this.constraintModel.getConstraintGraph(), colorMap);
		this.constraintModel.setDisplay(constraintDisplay);

		final Box searchBox = this.createSearchBox(constraintDisplay);

		final Box box = new Box(BoxLayout.Y_AXIS);
		box.add(searchBox);

		this.createSlider();

		final JPanel outerPanel = new JPanel(new BorderLayout());
		final JPanel innerPanel = new JPanel(new BorderLayout());
		innerPanel.add(constraintDisplay, BorderLayout.CENTER);
		innerPanel.add(box, BorderLayout.SOUTH);
		innerPanel.add(new JPanel().add(this.slider), BorderLayout.WEST);
		outerPanel.add(innerPanel, BorderLayout.CENTER);
		outerPanel.add(InspectNodesBox, BorderLayout.EAST);

		UILib.setColor(outerPanel, Color.WHITE, Color.DARK_GRAY);

		this.add(outerPanel);
	}

	private JPanel createInspectNodesBox() {
		final JPanel panel = new JPanel(new BorderLayout());
		panel.setBorder(BorderFactory.createMatteBorder(0, 1, 0, 0, Color.BLACK));

		final ConstraintNodesToInspectInspectListModel model = new ConstraintNodesToInspectInspectListModel();
		final ConstraintNodesToInspectList nodesToInspectList = new ConstraintNodesToInspectList();
		nodesToInspectList.setModel(model);
		this.constraintModel.setNodesToInspectListModel(model);

		final JScrollPane scrollPane = new JScrollPane(nodesToInspectList);
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		panel.add(scrollPane, BorderLayout.CENTER);

		final JToggleButton startStopButton = new StartStopInspectingButton(this.constraintModel, model);
		panel.add(startStopButton, BorderLayout.SOUTH);

		panel.setPreferredSize(new Dimension(150, 400));

		return panel;
	}

	private Box createSearchBox(final Display constraintDisplay) {
		final JSearchPanel search = new JSearchPanel(constraintDisplay.getVisualization(), TreeDisplayEnum.NODES.toString(), Visualization.SEARCH_ITEMS, FeatureSchema.NAME, true, false);
		search.setBorder(BorderFactory.createEmptyBorder(5, 5, 4, 0));
		search.setFont(FontLib.getFont("Tahoma", Font.PLAIN, 11));

		final JButton searchButton = new JButton("Search");
		searchButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				search.setQuery(search.getQuery());
			}
		});

		final JButton startStopAnimationButton = new JButton("Start/Stop Animation");
		startStopAnimationButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				final Action layoutAction = constraintDisplay.getVisualization().getAction("layout");
				if (layoutAction.isRunning()) {
					layoutAction.cancel();
				}
				else {
					layoutAction.run();
				}
			}
		});

		final Box searchbox = new Box(BoxLayout.X_AXIS);
		searchbox.add(search);
		searchbox.add(searchButton);
		searchbox.add(startStopAnimationButton);

		return searchbox;
	}

	private void createSlider() {
		this.slider = new JSlider(UIConstants.MIN_CONSTRAINT_FOCUS_DISTANCE, UIConstants.MAX_CONSTRAINT_FOCUS_DISTANCE, UIConstants.DEFAULT_CONSTRAINT_FOCUS_DISTANCE);
		this.slider.setOrientation(SwingConstants.VERTICAL);
		this.slider.setAutoscrolls(true);
		this.slider.setPaintTicks(true);
		this.slider.setMajorTickSpacing(1);
		this.slider.setSnapToTicks(true);
		this.slider.setSize(30, 300);
		this.slider.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(final ChangeEvent e) {
				UISettings.getInstance().setConstraintFocusDistance(((JSlider) e.getSource()).getValue());
			}
		});
	}

	@Override
	public void constraintFocusDistanceChanged(final int newDistance) {
		this.slider.setValue(newDistance);
	}

	@Override
	public void internalFrameActivated(final InternalFrameEvent e) {
		ApplicationModel.getInstance().setActiveDisplay(this.constraintModel.getDisplay());
	}

	@Override
	public void internalFrameOpened(final InternalFrameEvent e) {
		// nothing to do..
	}

	@Override
	public void internalFrameClosing(final InternalFrameEvent e) {
		ApplicationModel.getInstance().notifyFeatureModelClosed();
	}

	@Override
	public void internalFrameClosed(final InternalFrameEvent e) {
		this.constraintModel.getDisplay().getVisualization().getAction("layout").cancel();
	}

	@Override
	public void internalFrameIconified(final InternalFrameEvent e) {
		// nothing to do..
	}

	@Override
	public void internalFrameDeiconified(final InternalFrameEvent e) {
		// nothing to do..
	}

	@Override
	public void internalFrameDeactivated(final InternalFrameEvent e) {
		// nothing to do..
	}

	@Override
	public void fisheyeDistanceChanged(final int newDistance) {
		// TODO Auto-generated method stub

	}

	@Override
	public void featureModelClosed() {
		this.dispose();
	}

}
