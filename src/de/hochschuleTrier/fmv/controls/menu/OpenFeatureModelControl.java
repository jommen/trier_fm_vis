package de.hochschuleTrier.fmv.controls.menu;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import javax.swing.filechooser.FileFilter;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import de.hochschuleTrier.fmv.exceptions.ComplexConstraintParseException;
import de.hochschuleTrier.fmv.exceptions.FileOpenFailedException;
import de.hochschuleTrier.fmv.io.complexConstraints.ComplexConstraintReader;
import de.hochschuleTrier.fmv.model.impl.ApplicationModel;
import de.hochschuleTrier.fmv.model.impl.complexConstraints.ComplexConstraints;
import de.hochschuleTrier.fmv.model.impl.constraints.ConstraintModel;
import de.hochschuleTrier.fmv.model.impl.featureDiagram.FeatureDiagramModel;
import de.hochschuleTrier.fmv.model.interfaces.complexConstraints.IComplexConstraintGroup;
import de.hochschuleTrier.fmv.model.interfaces.complexConstraints.IComplexConstraints;
import de.hochschuleTrier.fmv.model.interfaces.constraints.IConstraintModel;
import de.hochschuleTrier.fmv.model.interfaces.featureDiagram.IFeatureDiagramModel;
import de.hochschuleTrier.fmv.util.NodeLib;

public class OpenFeatureModelControl implements ActionListener {

	private class ReadXmlThread extends Thread {
		private final JDialog waitDialog;
		private File treeMLFile;
		private File graphMLFile;
		private File complexConstraintFile;
		private final File loadedFile;

		public ReadXmlThread(final JDialog waitDialog, final File loadedFile) {
			this.waitDialog = waitDialog;
			this.loadedFile = loadedFile;
		}

		@Override
		public void run() {
			try {
				this.unzipLoadedFile();
				final IFeatureDiagramModel newFeatureTreeModel = new FeatureDiagramModel(this.treeMLFile.getPath());
				final IConstraintModel newConstraintModel = new ConstraintModel(this.graphMLFile.getPath());
				this.loadComplexConstraints();

				final String featureModelName = NodeLib.getName(newFeatureTreeModel.getTree().getRoot());
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						final ApplicationModel applicationModel = ApplicationModel.getInstance();
						applicationModel.openNewFeatureTreeModel(newFeatureTreeModel, newConstraintModel, featureModelName);
					}
				});
			}
			catch (final ComplexConstraintParseException e) {
				e.printStackTrace();
				this.showErrorMessage("An error occured while loading the complex constraints.");
			}
			catch (final Exception e) {
				e.printStackTrace();
				this.showErrorMessage("An error occured while loading the feature model.");
			}
			finally {
				this.waitDialog.dispose();
			}
		}

		private void unzipLoadedFile() throws ZipException, IOException {
			// Find systems default temp directory
			final File tempFile = File.createTempFile("temp", ".tmp");
			final String directory = tempFile.getParent();
			tempFile.delete();

			// Define file names
			this.graphMLFile = new File(directory + File.separator + "constraints.gml");
			this.treeMLFile = new File(directory + File.separator + "tree.tml");
			this.complexConstraintFile = new File(directory + File.separator + "complex_constraints.xml");

			// Delete old files if existing
			this.graphMLFile.delete();
			this.treeMLFile.delete();
			this.complexConstraintFile.delete();

			final ZipFile zipFile = new ZipFile(this.loadedFile);
			zipFile.extractAll(directory);
		}

		private void loadComplexConstraints() throws ComplexConstraintParseException {
			final ComplexConstraintReader complexConstraintReader = new ComplexConstraintReader(this.complexConstraintFile);
			final List<IComplexConstraintGroup> complexConstraintList = complexConstraintReader.parseXmlFile();
			final IComplexConstraints complexConstraints = new ComplexConstraints(complexConstraintList);
			ApplicationModel.getInstance().setComplexConstraints(complexConstraints);
		}

		private void showErrorMessage(final String message) {
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					JOptionPane.showMessageDialog(null, message, "Error", JOptionPane.ERROR_MESSAGE);
				}
			});
		}
	}

	@Override
	public void actionPerformed(final ActionEvent e) {
		try {
			final JDialog waitDialog = new JDialog();
			final JPanel panel = new JPanel();
			panel.add(new JLabel("Please wait...", SwingConstants.CENTER));
			waitDialog.getContentPane().add(panel);
			waitDialog.setSize(200, 75);
			final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
			waitDialog.setLocation((int) (screenSize.getWidth() / 2 - 50), (int) (screenSize.getHeight() / 2 - 50));
			waitDialog.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
			waitDialog.setModal(true);

			//			final File treeMLFile = this.getFile(new XMLTreeMLFileFilter());
			//			final File graphMLFile = this.getFile(new XMLGraphMLFileFilter());

			final File zipFile = this.getFile(new ZIPFileFilter());

			new ReadXmlThread(waitDialog, zipFile).start();
			waitDialog.setVisible(true);
		}
		catch (final Exception exc) {
			return;
		}
	}

	private File getFile(final FileFilter filter) throws FileOpenFailedException {
		final JFileChooser chooser = new JFileChooser();
		chooser.setFileFilter(filter);
		chooser.setAcceptAllFileFilterUsed(false);
		if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
			return chooser.getSelectedFile();
		}
		throw new FileOpenFailedException();
	}

	private class XMLTreeMLFileFilter extends FileFilter {

		@Override
		public boolean accept(final File file) {
			return (file.isDirectory() || file.getName().toLowerCase().endsWith(".tml"));
		}

		@Override
		public String getDescription() {
			return "TreeML";
		}
	}

	private class XMLGraphMLFileFilter extends FileFilter {

		@Override
		public boolean accept(final File file) {
			return (file.isDirectory() || file.getName().toLowerCase().endsWith(".gml"));
		}

		@Override
		public String getDescription() {
			return "GraphML";
		}
	}

	private class ZIPFileFilter extends FileFilter {

		@Override
		public boolean accept(final File file) {
			return (file.isDirectory() || file.getName().toLowerCase().endsWith(".zip"));
		}

		@Override
		public String getDescription() {
			return "ZIP";
		}
	}
}
