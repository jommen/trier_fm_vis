package de.hochschuleTrier.fmv.controls.menu;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

import de.hochschuleTrier.fmv.io.proofer.DFGProofExporter;

public class DFGExportAnalyzeFeatureControl implements ActionListener, Runnable {

	private File fileToSave;
	private final DFGProofExporter proofExporter;

	public DFGExportAnalyzeFeatureControl(final DFGProofExporter proofExporter) {
		this.proofExporter = proofExporter;
	}

	@Override
	public void actionPerformed(final ActionEvent e) {
		this.fileToSave = this.getFile(new DFGFileFilter());
		if (this.fileToSave != null) {
			new Thread(this).start();
		}
	}

	private File getFile(final FileFilter filter) {
		final JFileChooser chooser = new JFileChooser();
		chooser.setFileFilter(filter);
		chooser.setAcceptAllFileFilterUsed(false);
		chooser.setApproveButtonText("Save");
		if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
			return chooser.getSelectedFile();
		}
		return null;
	}

	@Override
	public void run() {
		if (!this.fileToSave.getName().endsWith(".dfg")) {
			this.fileToSave = new File(this.fileToSave.getAbsoluteFile() + ".dfg");
		}

		this.proofExporter.exportProof(this.fileToSave);
	}

	private class DFGFileFilter extends FileFilter {

		@Override
		public boolean accept(final File file) {
			return (file.isDirectory() || file.getName().toLowerCase().endsWith(".dfg"));
		}

		@Override
		public String getDescription() {
			return "dfg";
		}

	}

}
