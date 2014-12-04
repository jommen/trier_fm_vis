package de.hochschuleTrier.fmv.io.complexConstraints;

import java.io.File;
import java.io.FileReader;
import java.util.List;

import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import de.hochschuleTrier.fmv.exceptions.ComplexConstraintParseException;
import de.hochschuleTrier.fmv.model.interfaces.complexConstraints.IComplexConstraintGroup;

public class ComplexConstraintReader {

	private final File xmlFile;

	public ComplexConstraintReader(final File xmlFile) {
		this.xmlFile = xmlFile;
	}

	public List<IComplexConstraintGroup> parseXmlFile() throws ComplexConstraintParseException {
		try {
			final XMLReader xmlReader = XMLReaderFactory.createXMLReader();

			final FileReader fileReader = new FileReader(this.xmlFile);
			final InputSource inputSource = new InputSource(fileReader);

			final ComplexConstraintContentHandler contentHandler = new ComplexConstraintContentHandler();
			xmlReader.setContentHandler(contentHandler);
			xmlReader.parse(inputSource);
			return contentHandler.getAllConstraints();
		}
		catch (final Exception e) {
			e.printStackTrace();
			throw new ComplexConstraintParseException();
		}

	}

}
