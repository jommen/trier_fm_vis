package de.hochschuleTrier.fmv.controls.menu;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.util.Zip4jConstants;
import prefuse.data.Graph;
import prefuse.data.Tree;
import prefuse.data.io.DataIOException;
import prefuse.data.io.GraphMLWriter;
import prefuse.data.io.TreeMLWriter;
import de.hochschuleTrier.fmv.io.complexConstraints.ComplexConstraintWriter;
import de.hochschuleTrier.fmv.model.impl.ApplicationModel;
import de.hochschuleTrier.fmv.model.interfaces.complexConstraints.IComplexConstraints;

public class SaveFeatureModelControl implements ActionListener, Runnable {

	private String tempDirectory;
	private Tree featureTree;
	private Graph constraintGraph;
	private IComplexConstraints complexConstraints;

	private File featureTreeFile;
	private File constraintGraphFile;
	private File complexConstraintGraphFile;
	private File fileToSave;

	@Override
	public void actionPerformed(final ActionEvent e) {
		this.fileToSave = this.getFile(new ZIPFileFilter());
		if (this.fileToSave != null) {
			new Thread(this).start();
		}
	}

	private File getFile(final FileFilter filter) {
		final JFileChooser chooser = new JFileChooser();
		chooser.setFileFilter(filter);
		chooser.setAcceptAllFileFilterUsed(false);
		chooser.setDialogTitle("Choose file..");
		chooser.setApproveButtonText("Save");
		if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
			return chooser.getSelectedFile();
		}
		return null;
	}

	@Override
	public void run() {
		this.featureTree = ApplicationModel.getInstance().getCurrentFeatureTreeModel().getTree();
		this.constraintGraph = ApplicationModel.getInstance().getConstraintModel().getConstraintGraph();
		this.complexConstraints = ApplicationModel.getInstance().getComplexConstraints();

		try {
			// Get systems temp directory
			final File tempFile = File.createTempFile("temp", ".tmp");
			this.tempDirectory = tempFile.getParent();
			tempFile.delete();

			// Define names of the temp files
			this.featureTreeFile = new File(this.tempDirectory + File.separator + "tree.tml");
			this.constraintGraphFile = new File(this.tempDirectory + File.separator + "constraints.gml");
			this.complexConstraintGraphFile = new File(this.tempDirectory + File.separator + "complex_constraints.xml");

			// Write the data to the temp files
			this.writeFeatureTree();
			this.writeConstraintGraph();
			this.writeComplexConstraints();

			// Create the zip archive
			this.archiveDir();
		}
		catch (final Exception e1) {
			e1.printStackTrace();
		}
	}

	private void writeFeatureTree() throws DataIOException {
		final TreeMLWriter writer = new TreeMLWriter();
		writer.writeGraph(this.featureTree, this.featureTreeFile);
	}

	private void writeConstraintGraph() throws DataIOException {
		final GraphMLWriter writer = new GraphMLWriter();
		writer.writeGraph(this.constraintGraph, this.constraintGraphFile);
	}

	private void writeComplexConstraints() throws DataIOException {
		final ComplexConstraintWriter writer = new ComplexConstraintWriter(this.complexConstraints);
		writer.write(this.complexConstraintGraphFile);
	}

	private void archiveDir() {
		try {
			if (!this.fileToSave.getName().endsWith(".zip")) {
				this.fileToSave = new File(this.fileToSave.getAbsoluteFile() + ".zip");
			}
			final ZipFile zipFile = new ZipFile(this.fileToSave);

			// Initiate Zip Parameters which define various properties such as compression method, etc.
			final ZipParameters parameters = new ZipParameters();
			parameters.setCompressionMethod(Zip4jConstants.COMP_DEFLATE);
			parameters.setCompressionLevel(Zip4jConstants.DEFLATE_LEVEL_NORMAL);

			// Add files to zip archive
			zipFile.addFile(this.featureTreeFile, parameters);
			zipFile.addFile(this.constraintGraphFile, parameters);
			zipFile.addFile(this.complexConstraintGraphFile, parameters);
		}
		catch (final Exception e) {
			e.printStackTrace();
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
