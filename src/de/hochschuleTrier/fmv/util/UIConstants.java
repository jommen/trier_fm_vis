package de.hochschuleTrier.fmv.util;

/**
 * Describes constants used in the user interface
 * 
 */
public interface UIConstants {
	/**
	 * The default distance for the fisheye view mode in the feature tree
	 * display
	 */
	public static final int DEFAULT_FISHEYE_DISTANCE = 3;

	/**
	 * The default distance for the constraint focus mode in the constraint view
	 * display
	 */
	public static final int DEFAULT_CONSTRAINT_FOCUS_DISTANCE = 4;

	/**
	 * The minimum distance for the constraint focus mode in the constraint view
	 * display
	 */
	public static final int MIN_CONSTRAINT_FOCUS_DISTANCE = 1;

	/**
	 * The maximum distance for the constraint focus mode in the constraint view
	 * display
	 */
	public static final int MAX_CONSTRAINT_FOCUS_DISTANCE = 7;

	/**
	 * Defines, if the tooltip on the feature should be shown by default
	 */
	public static final boolean DEFAULT_SHOW_TOOLTIP = true;
}
