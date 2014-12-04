package de.hochschuleTrier.fmv.view;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;

import de.hochschuleTrier.fmv.controls.menu.DFGExportAnalyzeFeatureControl;
import de.hochschuleTrier.fmv.controls.menu.DFGImportAnalyzeFeatureControl;
import de.hochschuleTrier.fmv.controls.menu.NewFeatureModelControl;
import de.hochschuleTrier.fmv.controls.menu.OpenFeatureModelControl;
import de.hochschuleTrier.fmv.controls.menu.SaveFeatureModelControl;
import de.hochschuleTrier.fmv.io.proofer.DFGProofExporter;
import de.hochschuleTrier.fmv.io.proofer.DeadFeatureDFGProofExporter;
import de.hochschuleTrier.fmv.io.proofer.SpassProofImporter;
import de.hochschuleTrier.fmv.model.impl.ApplicationModel;
import de.hochschuleTrier.fmv.model.impl.UISettings;
import de.hochschuleTrier.fmv.model.interfaces.complexConstraints.IComplexConstraintModel;
import de.hochschuleTrier.fmv.model.interfaces.constraints.IConstraintModel;
import de.hochschuleTrier.fmv.model.interfaces.featureDiagram.IFeatureDiagramModel;
import de.hochschuleTrier.fmv.model.interfaces.listener.IFeatureModelClosedListener;
import de.hochschuleTrier.fmv.model.interfaces.listener.INewFeatureModelListener;
import de.hochschuleTrier.fmv.util.UIConstants;

public class ApplicationMenuBar extends JMenuBar implements INewFeatureModelListener, IFeatureModelClosedListener {

	private final ApplicationFrame applicationFrame;

	private JMenu analyzeMenu;

	public ApplicationMenuBar(final ApplicationFrame frame) {
		this.applicationFrame = frame;

		ApplicationModel.getInstance().addNewFeatureModelListener(this);
		ApplicationModel.getInstance().addFeatureModelClosedListener(this);

		this.constructFileMenu();
		this.constructViewMenu();
		this.constructAnalyzeMenu();
	}

	private void constructFileMenu() {
		final JMenu fileMenu = new JMenu("File");
		this.add(fileMenu);

		final JMenuItem newAction = new JMenuItem("New");
		final JMenuItem openAction = new JMenuItem("Open");
		final JMenuItem saveAction = new JMenuItem("Save");
		final JMenuItem exitAction = new JMenuItem("Exit");
		newAction.setMnemonic(KeyEvent.VK_N);
		newAction.setAccelerator(KeyStroke.getKeyStroke("control N"));
		openAction.setMnemonic(KeyEvent.VK_O);
		openAction.setAccelerator(KeyStroke.getKeyStroke("control O"));
		saveAction.setMnemonic(KeyEvent.VK_S);
		saveAction.setAccelerator(KeyStroke.getKeyStroke("control S"));
		fileMenu.add(newAction);
		fileMenu.add(openAction);
		fileMenu.add(saveAction);
		fileMenu.add(exitAction);

		newAction.addActionListener(new NewFeatureModelControl());
		openAction.addActionListener(new OpenFeatureModelControl());
		saveAction.addActionListener(new SaveFeatureModelControl());
		exitAction.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				ApplicationMenuBar.this.applicationFrame.setVisible(false);
				ApplicationMenuBar.this.applicationFrame.dispose();
			}
		});
	}

	private void constructViewMenu() {
		final JMenu viewMenu = new JMenu("View");
		this.add(viewMenu);

		// Show Tooltip
		final JMenuItem toggleTooltipAction = new JCheckBoxMenuItem("Show Tooltip");
		toggleTooltipAction.setAccelerator(KeyStroke.getKeyStroke("control T"));
		toggleTooltipAction.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				final boolean selected = ((JCheckBoxMenuItem) e.getSource()).isSelected();
				UISettings.getInstance().setShowToolTip(selected);
			}
		});
		toggleTooltipAction.setSelected(UIConstants.DEFAULT_SHOW_TOOLTIP);

		// Complex Constraint Icon
		final JMenuItem toggleShowComplexConstraintIconInFeatureDiagramAction = new JCheckBoxMenuItem("Show complex constraint icons in feature diagram");
		toggleShowComplexConstraintIconInFeatureDiagramAction.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				final boolean selected = ((JCheckBoxMenuItem) e.getSource()).isSelected();
				UISettings.getInstance().setShowComplexConstraintIconInFeatureDiagram(selected);
			}
		});
		toggleShowComplexConstraintIconInFeatureDiagramAction.setSelected(false);

		viewMenu.add(toggleTooltipAction);
		viewMenu.add(toggleShowComplexConstraintIconInFeatureDiagramAction);
	}

	private void constructAnalyzeMenu() {
		this.analyzeMenu = new JMenu("Analyze");
		this.analyzeMenu.setEnabled(false);
		final JMenu dfgMenu = new JMenu("DFG");
		final JMenu dfgExportMenu = new JMenu("Export");
		final JMenu dfgImportMenu = new JMenu("Import");
		this.analyzeMenu.add(dfgMenu);
		dfgMenu.add(dfgExportMenu);
		dfgMenu.add(dfgImportMenu);
		this.add(this.analyzeMenu);

		final SpassProofImporter importer = new SpassProofImporter();

		// Dead feature
		final JMenuItem exportDeadFeatureAction = new JMenuItem("Dead feature");
		final DeadFeatureDFGProofExporter deadFeatureExporter = new DeadFeatureDFGProofExporter();
		exportDeadFeatureAction.addActionListener(new DFGExportAnalyzeFeatureControl(deadFeatureExporter));
		dfgExportMenu.add(exportDeadFeatureAction);

		final JMenuItem importDeadFeatureAction = new JMenuItem("Dead feature");
		importDeadFeatureAction.addActionListener(new DFGImportAnalyzeFeatureControl(importer));
		dfgImportMenu.add(importDeadFeatureAction);

		// Void model
		final JMenuItem exportVoidModelAction = new JMenuItem("Void model");
		final DFGProofExporter voidModelExporter = new DFGProofExporter();
		exportVoidModelAction.addActionListener(new DFGExportAnalyzeFeatureControl(voidModelExporter));
		dfgExportMenu.add(exportVoidModelAction);

		final JMenuItem importVoidModelAction = new JMenuItem("Void model");
		importVoidModelAction.addActionListener(new DFGImportAnalyzeFeatureControl(importer));
		dfgImportMenu.add(importVoidModelAction);
	}

	@Override
	public void newFeatueTreeModelCreated(final IFeatureDiagramModel currentFeatureTreeModel, final IConstraintModel newConstraintModel, final String featureModelName) {
		this.analyzeMenu.setEnabled(true);
	}

	@Override
	public void newSubtreeCreated(final IFeatureDiagramModel featureTreeModel) {
		// TODO Auto-generated method stub

	}

	@Override
	public void newComplexConstraintView(final IComplexConstraintModel model) {
		// TODO Auto-generated method stub

	}

	@Override
	public void featureModelClosed() {
		this.analyzeMenu.setEnabled(false);
	}
}
