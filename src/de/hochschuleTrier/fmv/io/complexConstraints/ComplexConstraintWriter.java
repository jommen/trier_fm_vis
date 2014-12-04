package de.hochschuleTrier.fmv.io.complexConstraints;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import prefuse.data.io.DataIOException;
import de.hochschuleTrier.fmv.model.impl.complexConstraints.ComplexConstraintLiteral;
import de.hochschuleTrier.fmv.model.interfaces.complexConstraints.IComplexConstraint;
import de.hochschuleTrier.fmv.model.interfaces.complexConstraints.IComplexConstraintGroup;
import de.hochschuleTrier.fmv.model.interfaces.complexConstraints.IComplexConstraintLiteral;
import de.hochschuleTrier.fmv.model.interfaces.complexConstraints.IComplexConstraints;

/**
 * Writer to save the complex constraints in a xml file
 * 
 */
public class ComplexConstraintWriter {

	private final IComplexConstraints complexConstraints;
	private XMLStreamWriter xmlWriter;

	public ComplexConstraintWriter(final IComplexConstraints complexConstraints) {
		this.complexConstraints = complexConstraints;
	}

	/**
	 * Writes the complex constraints in the given file
	 * 
	 * @param pathname
	 *            Path to the file
	 * @throws DataIOException
	 */
	public void write(final String pathname) throws DataIOException {
		this.write(new File(pathname));
	}

	/**
	 * Writes the complex constraints in the given file
	 * 
	 * @param pathname
	 *            Path to the file
	 * @throws DataIOException
	 */
	public void write(final File file) throws DataIOException {
		try (final OutputStreamWriter outputStreamWriter = new OutputStreamWriter(new FileOutputStream(file), "utf-8")) {
			this.xmlWriter = XMLOutputFactory.newInstance().createXMLStreamWriter(outputStreamWriter);

			this.xmlWriter.writeStartDocument();
			this.xmlWriter.writeStartElement("complexConstraints");

			for (final IComplexConstraintGroup complexConstraintGroup : this.complexConstraints.getConstraintList()) {
				this.writeComplexConstraints(complexConstraintGroup);
			}

			this.xmlWriter.writeEndElement();
			this.xmlWriter.writeEndDocument();
			this.xmlWriter.flush();
			this.xmlWriter.close();
		}
		catch (final Exception e) {
			throw new DataIOException(e);
		}
	}

	/**
	 * Writes the complex constraint recursive. The {@link ComplexConstraintLiteral} is the recursive anker.
	 * 
	 * @param complexConstraint
	 * @throws XMLStreamException
	 */
	private void writeComplexConstraints(final IComplexConstraint complexConstraint) throws XMLStreamException {
		if (complexConstraint instanceof IComplexConstraintLiteral) {
			final IComplexConstraintLiteral node = (IComplexConstraintLiteral) complexConstraint;
			this.writeComplexConstraint(node);
		}
		else {
			final IComplexConstraintGroup group = (IComplexConstraintGroup) complexConstraint;
			this.writeComplexConstraint(group);
		}
	}

	private void writeComplexConstraint(final IComplexConstraintLiteral node) throws XMLStreamException {
		this.xmlWriter.writeStartElement("node");
		if (node.isNegated()) {
			this.xmlWriter.writeAttribute("negate", "true");
		}
		this.xmlWriter.writeCharacters(node.getName());
		this.xmlWriter.writeEndElement();
	}

	private void writeComplexConstraint(final IComplexConstraintGroup group) throws XMLStreamException {
		this.xmlWriter.writeStartElement(group.getType().getXmlTag());
		if (group.isNegated()) {
			this.xmlWriter.writeAttribute("negate", "true");
		}

		for (final IComplexConstraint child : group.getChildren()) {
			this.writeComplexConstraints(child);
		}

		this.xmlWriter.writeEndElement();
	}
}
