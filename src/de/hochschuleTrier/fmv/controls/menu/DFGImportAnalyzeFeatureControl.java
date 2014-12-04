package de.hochschuleTrier.fmv.controls.menu;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileFilter;

import de.hochschuleTrier.fmv.io.proofer.SpassProofImporter;
import de.hochschuleTrier.fmv.model.impl.ApplicationModel;
import de.hochschuleTrier.fmv.model.impl.constraints.ConstraintNodesToInspectInspectListModel;

public class DFGImportAnalyzeFeatureControl implements ActionListener, Runnable {

	private File proofFile;
	private final SpassProofImporter proofImporter;

	public DFGImportAnalyzeFeatureControl(final SpassProofImporter proofImporter) {
		this.proofImporter = proofImporter;
	}

	@Override
	public void actionPerformed(final ActionEvent e) {
		this.proofFile = this.getFile(new ProofFileFilter());
		if (this.proofFile != null) {
			new Thread(this).start();
		}
	}

	private File getFile(final FileFilter filter) {
		final JFileChooser chooser = new JFileChooser();
		chooser.setFileFilter(filter);
		chooser.setAcceptAllFileFilterUsed(false);
		if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
			return chooser.getSelectedFile();
		}
		return null;
	}

	@Override
	public void run() {
		final ConstraintNodesToInspectInspectListModel inspectListModel = ApplicationModel.getInstance().getConstraintModel().getNodesToInspectListModel();
		inspectListModel.clear();

		this.proofImporter.importProof(this.proofFile);
		if (this.proofImporter.isSatisfiable()) {
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					final String message = "Your theorem prover found a completion.";
					final String title = "No problem found";
					final Icon icon = new ImageIcon(this.getClass().getResource("/images/ok.png"));
					JOptionPane.showMessageDialog(null, message, title, JOptionPane.PLAIN_MESSAGE, icon);
				}
			});
		}
		else {
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					final String message = "Your theorem prover found a problem.";
					final String title = "Problem occured";
					JOptionPane.showMessageDialog(null, message, title, JOptionPane.ERROR_MESSAGE);
				}
			});
			for (final String feature : this.proofImporter.getFeaturesInProof()) {
				inspectListModel.addElement(feature);
			}
		}
	}

	private class ProofFileFilter extends FileFilter {

		@Override
		public boolean accept(final File file) {
			return (file.isDirectory() || file.getName().toLowerCase().endsWith(".prf"));
		}

		@Override
		public String getDescription() {
			return "Proof-File";
		}

	}

}
