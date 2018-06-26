
package io.marioslab.basis.arguments;

import java.io.PrintStream;

/**
 * <p>
 * Defines an argument that can be parsed or for which help can be displayed by {@link Arguments#parse(String[])} and
 * {@link Arguments#printHelp(PrintStream)}.
 * </p>
 *
 * <p>
 * An argument has a short and long form, e.g. "-v" and "-verbose" that {@link Arguments#parse(String[])} matches against. When an
 * argument is matched, its {@link ArgumentMatchedCallback} is called.
 * </p>
 *
 * <p>
 * The long and short form along with the help text returned by {@link #getHelpText()} are used by
 * {@link Arguments#printHelp(PrintStream)} to display the argument's help information.
 * </p>
 *
 * <p>
 * An argument may be optional or non-optional. If it is non-optional and not matched by {@link Arguments#parse(String[])}, then
 * an {@link ArgumentException} will be raised.
 * </p>
 *
 * <p>
 * For arguments that expect a value, see {@link ArgumentWithValue} and its inner classes for concrete implementations.
 * </p>
 */
public class Argument {
	private final String shortForm;
	private final String longForm;
	private final String help;
	private final boolean isOptional;

	/** @param shortForm the short form of the argument, e.g. "-v".
	 * @param longForm the long form of the argument, e.g. "--verbose".
	 * @param help the help text to be displayed by {@link Arguments#printHelp(PrintStream)} for this argument.
	 * @param isOptional whether this argument is optional. */
	public Argument (String shortForm, String longForm, String help, boolean isOptional) {
		super();
		this.shortForm = shortForm;
		this.longForm = longForm;
		this.help = help;
		this.isOptional = isOptional;
	}

	/** Returns the short form of the argument, e.g. "v" would match "-v". **/
	String getShortForm () {
		return shortForm;
	}

	/** Returns the long form of the argument, e.g. "verbose" would match "--verbose". **/
	String getLongForm () {
		return longForm;
	}

	/** Returns the help string to be displayed. **/
	String getHelpText () {
		return help;
	}

	/** Whether the argument is optional. {@link Arguments#parse(String[])} will raise an {@link ArgumentException} if the
	 * argument is not optional and is never matched. **/
	boolean isOptional () {
		return isOptional;
	}

	@Override
	public int hashCode () {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((help == null) ? 0 : help.hashCode());
		result = prime * result + (isOptional ? 1231 : 1237);
		result = prime * result + ((longForm == null) ? 0 : longForm.hashCode());
		result = prime * result + ((shortForm == null) ? 0 : shortForm.hashCode());
		return result;
	}

	@Override
	public boolean equals (Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		Argument other = (Argument)obj;
		if (help == null) {
			if (other.help != null) return false;
		} else if (!help.equals(other.help)) return false;
		if (isOptional != other.isOptional) return false;
		if (longForm == null) {
			if (other.longForm != null) return false;
		} else if (!longForm.equals(other.longForm)) return false;
		if (shortForm == null) {
			if (other.shortForm != null) return false;
		} else if (!shortForm.equals(other.shortForm)) return false;
		return true;
	}
}
