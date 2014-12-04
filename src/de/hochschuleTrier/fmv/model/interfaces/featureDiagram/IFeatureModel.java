package de.hochschuleTrier.fmv.model.interfaces.featureDiagram;

/**
 * Eigenes Modell für die Datenhaltung von Feature-spezifischen Daten.
 * Dieses wird u. a. als Datencontainer für den Editor benötigt.
 * Prefuse selber verwendet eigene Datenstrukturen mit Schemata. Dies sollte aus Kompatibilitätsgründen so beibehalten werden.
 * 
 */
public interface IFeatureModel {
	public boolean isOptional();

	public void setOptional(final boolean optional);

	public String getOptionalAsString();

	public String getNodeType();

	public void setNodeType(final String nodeType);

	public String getGroupType();

	public void setGroupType(final String groupType);

	public String getName();

	public void setName(final String name);
}
