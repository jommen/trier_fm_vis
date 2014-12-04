package de.hochschuleTrier.fmv.io.proofer;

import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

public class SpassProofImporter implements IProofImporter {

	private boolean satisfiable;
	private boolean proofStarted;
	private final List<String> featuresInProof;

	public SpassProofImporter() {
		this.featuresInProof = new ArrayList<>();
	}

	@Override
	public void importProof(final File file) {
		this.featuresInProof.clear();
		this.proofStarted = false;
		try (final Scanner reader = new Scanner(new FileReader(file))) {
			while (reader.hasNextLine()) {
				this.parseLine(reader.nextLine());
			}
		}
		catch (final Exception e) {
			e.printStackTrace();
		}
	}

	private void parseLine(final String line) {
		if (line.startsWith("SPASS beiseite: Proof found.")) {
			this.satisfiable = false;
		}
		else if (line.startsWith("SPASS beiseite: Completion found.")) {
			this.satisfiable = true;
		}
		else if (line.startsWith("Here is a proof")) {
			this.proofStarted = true;
		}
		else if (line.startsWith("Formulae used in the proof")) {
			this.proofStarted = false;
			return;
		}
		else if (this.proofStarted) {
			try (final Scanner scanner = new Scanner(line)) {
				scanner.findInLine("\\|\\|");
				while (scanner.hasNext()) {
					final String feature = scanner.next().replaceAll("[.]$", "").replaceAll("[*]", "");
					if (!feature.equals("->") && !feature.isEmpty() && !this.featuresInProof.contains(feature)) {
						this.featuresInProof.add(feature);
					}
				}
			}
		}
	}

	public boolean isSatisfiable() {
		return this.satisfiable;
	}

	public boolean isProofStarted() {
		return this.proofStarted;
	}

	public List<String> getFeaturesInProof() {
		return Collections.unmodifiableList(this.featuresInProof);
	}

}
