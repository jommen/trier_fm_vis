package de.hochschuleTrier.fmv.model.interfaces.featureDiagram;

/**
 * Eigenes Modell f�r die Datenhaltung von Feature-spezifischen Daten.
 * Dieses wird u. a. als Datencontainer f�r den Editor ben�tigt.
 * Prefuse selber verwendet eigene Datenstrukturen mit Schemata. Dies sollte aus Kompatibilit�tsgr�nden so beibehalten werden.
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
