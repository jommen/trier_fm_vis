package de.hochschuleTrier.fmv.view;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.DefaultListCellRenderer;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JToggleButton;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import prefuse.Display;
import de.hochschuleTrier.fmv.model.impl.ApplicationModel;
import de.hochschuleTrier.fmv.model.impl.UISettings;
import de.hochschuleTrier.fmv.stategies.mousewheel.MouseWheelExpandCollapseStrategy;
import de.hochschuleTrier.fmv.stategies.mousewheel.MouseWheelFisheyeStrategy;
import de.hochschuleTrier.fmv.stategies.mousewheel.MouseWheelZoomStrategy;
import de.hochschuleTrier.fmv.util.FMDisplayLib;
import de.hochschuleTrier.fmv.util.UIConstants;

public class ApplicationButtonPanel extends JPanel {

	private final ImageIcon paintBucketIcon;
	private final ImageIcon zoomIcon;
	private final ImageIcon expandCollapseIcon;
	private final ImageIcon targetIcon;
	private final ImageIcon zoomToFitIcon;
	private final ImageIcon fisheyeIcon;
	private final ImageIcon zoomInIcon;
	private final ImageIcon zoomOutIcon;
	private final ImageIcon subtreeIcon;

	private final JToggleButton focusButton;
	private final JButton zoomToFitButton;
	private final JButton colorChooserButton;
	private final JButton paintBucketButton;
	private final JComboBox<ImageIcon> mouseWheelButton;
	private final JToggleButton fisheyeButton;
	private final JSlider fisheyeDistanceSlider;
	private final JButton zoomOutButton;
	private final JButton zoomInButton;
	private final JButton subtreeButton;

	public ApplicationButtonPanel() {
		super(new FlowLayout(FlowLayout.LEFT));

		final String pathPrefix = "/images/";

		this.paintBucketIcon = new ImageIcon(this.getClass().getResource(pathPrefix + "paint_bucket.png"));
		this.zoomIcon = new ImageIcon(this.getClass().getResource(pathPrefix + "zoom.png"));
		this.zoomInIcon = new ImageIcon(this.getClass().getResource(pathPrefix + "zoom_in.png"));
		this.zoomOutIcon = new ImageIcon(this.getClass().getResource(pathPrefix + "zoom_out.png"));
		this.expandCollapseIcon = new ImageIcon(this.getClass().getResource(pathPrefix + "expandCollapse.png"));
		this.zoomToFitIcon = new ImageIcon(this.getClass().getResource(pathPrefix + "fitToZoom.png"));
		this.targetIcon = new ImageIcon(this.getClass().getResource(pathPrefix + "target.png"));
		this.fisheyeIcon = new ImageIcon(this.getClass().getResource(pathPrefix + "fisheye.png"));
		this.subtreeIcon = new ImageIcon(this.getClass().getResource(pathPrefix + "subtree.png"));

		this.focusButton = new JToggleButton(this.targetIcon);
		this.focusButton.setPreferredSize(new Dimension(30, 30));
		this.focusButton.setMaximumSize(new Dimension(30, 30));
		this.focusButton.setName("focusButton");
		this.focusButton.setToolTipText("Mode: center selected feature");
		this.focusButton.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(final ItemEvent e) {
				final boolean isSelected = ApplicationButtonPanel.this.focusButton.isSelected();
				UISettings.getInstance().setCenterSelectedNode(isSelected);
			}
		});

		this.colorChooserButton = new JButton();
		this.colorChooserButton.setPreferredSize(new Dimension(30, 30));
		this.colorChooserButton.setMaximumSize(new Dimension(30, 30));
		this.colorChooserButton.setName("colorChooserButton");
		this.colorChooserButton.setToolTipText("Choose color");
		this.colorChooserButton.setBackground(Color.WHITE);
		this.colorChooserButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						final Color initialColor = ApplicationButtonPanel.this.colorChooserButton.getBackground();
						final Color color = JColorChooser.showDialog(ApplicationButtonPanel.this.colorChooserButton, "Choose color", initialColor);
						if (color != null) {
							ApplicationButtonPanel.this.colorChooserButton.setBackground(color);
							UISettings.getInstance().setSelectedColor(color);
						}
					}
				});
			}
		});

		this.paintBucketButton = new JButton(this.paintBucketIcon);
		this.paintBucketButton.setPreferredSize(new Dimension(30, 30));
		this.paintBucketButton.setMaximumSize(new Dimension(30, 30));
		this.paintBucketButton.setName("paintBucketButton");
		this.paintBucketButton.setToolTipText("Color selected feature");
		this.paintBucketButton.addMouseListener(new MouseListener() {

			@Override
			public void mouseReleased(final MouseEvent e) {
			}

			@Override
			public void mousePressed(final MouseEvent e) {
			}

			@Override
			public void mouseExited(final MouseEvent e) {
			}

			@Override
			public void mouseEntered(final MouseEvent e) {
			}

			@Override
			public void mouseClicked(final MouseEvent e) {
				final ApplicationModel model = ApplicationModel.getInstance();
				final Color color = UISettings.getInstance().getSelectedColor();
				if (e.getClickCount() == 1) {
					model.paintSelectedNode(color);
				}
				else {
					model.paintSelectedSubtree(color);
				}
			}
		});

		final ImageIcon[] items = { this.zoomIcon, this.expandCollapseIcon, this.fisheyeIcon };
		final List<String> tooltips = new ArrayList<String>();
		tooltips.add("Zoom");
		tooltips.add("Expand selected feature");
		tooltips.add("Configure distance of fisheye focus");
		this.mouseWheelButton = new JComboBox<ImageIcon>(items);
		this.mouseWheelButton.setPreferredSize(new Dimension(50, 30));
		this.mouseWheelButton.setMaximumSize(new Dimension(50, 30));
		this.mouseWheelButton.setName("mouseWheelButton");
		this.mouseWheelButton.setToolTipText("Select scroll wheel action");
		this.mouseWheelButton.setRenderer(new ComboBoxToolTipRenderer(tooltips));
		this.mouseWheelButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				final ImageIcon selectedIcon = (ImageIcon) ApplicationButtonPanel.this.mouseWheelButton.getSelectedItem();
				if (selectedIcon.equals(ApplicationButtonPanel.this.zoomIcon)) {
					UISettings.getInstance().setMouseWheelStrategy(new MouseWheelZoomStrategy());
				}
				else if (selectedIcon.equals(ApplicationButtonPanel.this.expandCollapseIcon)) {
					ApplicationButtonPanel.this.fisheyeButton.setSelected(false);
					UISettings.getInstance().setMouseWheelStrategy(new MouseWheelExpandCollapseStrategy());
				}
				else if (selectedIcon.equals(ApplicationButtonPanel.this.fisheyeIcon)) {
					ApplicationButtonPanel.this.fisheyeButton.setSelected(true);
					UISettings.getInstance().setMouseWheelStrategy(new MouseWheelFisheyeStrategy());
				}
			}
		});

		this.fisheyeButton = new JToggleButton(this.fisheyeIcon);
		this.fisheyeButton.setPreferredSize(new Dimension(30, 30));
		this.fisheyeButton.setMaximumSize(new Dimension(30, 30));
		this.fisheyeButton.setName("fisheyeButton");
		this.fisheyeButton.setToolTipText("Activate fisheye focus");
		this.fisheyeButton.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(final ItemEvent arg0) {
				final ImageIcon selectedMouseWheelIcon = (ImageIcon) ApplicationButtonPanel.this.mouseWheelButton.getSelectedItem();
				final boolean isSelected = ApplicationButtonPanel.this.fisheyeButton.isSelected();
				UISettings.getInstance().setFisheyeZoomEnabled(isSelected);

				if (isSelected) {
					ApplicationButtonPanel.this.fisheyeDistanceSlider.setEnabled(true);
					ApplicationButtonPanel.this.mouseWheelButton.setSelectedItem(ApplicationButtonPanel.this.fisheyeIcon);
					if (selectedMouseWheelIcon.equals(ApplicationButtonPanel.this.expandCollapseIcon)) {
						ApplicationButtonPanel.this.mouseWheelButton.setSelectedIndex(0);
					}
				}
				else {
					ApplicationButtonPanel.this.fisheyeDistanceSlider.setEnabled(false);
					if (selectedMouseWheelIcon.equals(ApplicationButtonPanel.this.fisheyeIcon)) {
						ApplicationButtonPanel.this.mouseWheelButton.setSelectedIndex(0);
					}
				}
			}
		});

		this.fisheyeDistanceSlider = new JSlider(1, 9, UIConstants.DEFAULT_FISHEYE_DISTANCE);
		this.fisheyeDistanceSlider.setPaintLabels(true);
		this.fisheyeDistanceSlider.setPaintTicks(true);
		this.fisheyeDistanceSlider.setMinorTickSpacing(1);
		this.fisheyeDistanceSlider.setMajorTickSpacing(2);
		this.fisheyeDistanceSlider.setSnapToTicks(true);
		this.fisheyeDistanceSlider.setFont(new Font("Helvetica", Font.PLAIN, 8));
		this.fisheyeDistanceSlider.setValue(UIConstants.DEFAULT_FISHEYE_DISTANCE);
		this.fisheyeDistanceSlider.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(final ChangeEvent arg0) {
				final int value = ApplicationButtonPanel.this.fisheyeDistanceSlider.getValue();
				UISettings.getInstance().setFisheyeDistance(value);
			}
		});
		UISettings.getInstance().setFisheyeDistanceModel(this.fisheyeDistanceSlider.getModel());

		this.zoomOutButton = new JButton(this.zoomOutIcon);
		this.zoomOutButton.setPreferredSize(new Dimension(30, 30));
		this.zoomOutButton.setMaximumSize(new Dimension(30, 30));
		this.zoomOutButton.setName("zoomOutButton");
		this.zoomOutButton.setToolTipText("Zoom out");
		this.zoomOutButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent arg0) {
				final Display display = ApplicationModel.getInstance().getActiveDisplay();
				FMDisplayLib.zoomCenter(display, 0.8);
			}
		});

		this.zoomInButton = new JButton(this.zoomInIcon);
		this.zoomInButton.setPreferredSize(new Dimension(30, 30));
		this.zoomInButton.setMaximumSize(new Dimension(30, 30));
		this.zoomInButton.setName("zoomOutButton");
		this.zoomInButton.setToolTipText("Zoom in");
		this.zoomInButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent arg0) {
				final Display display = ApplicationModel.getInstance().getActiveDisplay();
				FMDisplayLib.zoomCenter(display, 1.2);
			}
		});

		this.zoomToFitButton = new JButton(this.zoomToFitIcon);
		this.zoomToFitButton.setPreferredSize(new Dimension(30, 30));
		this.zoomToFitButton.setMaximumSize(new Dimension(30, 30));
		this.zoomToFitButton.setName("zoomToFitButton");
		this.zoomToFitButton.setToolTipText("Fit to view");
		this.zoomToFitButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				final Display display = ApplicationModel.getInstance().getActiveDisplay();
				FMDisplayLib.zoomToFit(display);
			}
		});

		this.subtreeButton = new JButton(this.subtreeIcon);
		this.subtreeButton.setPreferredSize(new Dimension(30, 30));
		this.subtreeButton.setMaximumSize(new Dimension(30, 30));
		this.subtreeButton.setName("zoomOutButton");
		this.subtreeButton.setToolTipText("Extract sub-tree");
		this.subtreeButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent arg0) {
				ApplicationModel.getInstance().extractSubTree();
			}
		});

		this.fisheyeButton.setSelected(true);
		this.focusButton.setSelected(true);

		this.add(this.subtreeButton);
		this.add(this.focusButton);
		this.add(this.colorChooserButton);
		this.add(this.paintBucketButton);
		this.add(this.mouseWheelButton);
		this.add(this.fisheyeButton);
		this.add(this.fisheyeDistanceSlider);
		this.add(this.zoomOutButton);
		this.add(this.zoomInButton);
		this.add(this.zoomToFitButton);
	}

	private class ComboBoxToolTipRenderer extends DefaultListCellRenderer {
		private final List<String> tooltips;

		public ComboBoxToolTipRenderer(final List<String> tooltips) {
			this.tooltips = Collections.unmodifiableList(tooltips);
		}

		@Override
		public Component getListCellRendererComponent(final JList<?> list, final Object value, final int index, final boolean isSelected, final boolean cellHasFocus) {
			final JComponent comp = (JComponent) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

			if (-1 < index && null != value && null != this.tooltips) {
				list.setToolTipText(this.tooltips.get(index));
			}
			return comp;
		}

	}
}
