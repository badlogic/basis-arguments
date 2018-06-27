
package io.marioslab.basis.arguments;

import java.io.PrintStream;
import java.util.Arrays;

/**
 * <p>
 * Base class for arguments, i.e. {@link Argument} and {@link ArgumentWithValue}. The reason for this "weird" class hierarchy is
 * that {@link Arguments} has two methods called
 * {@link Arguments#addArgument(Argument, io.marioslab.basis.arguments.Arguments.ArgumentMatchedCallback)} and
 * {@link Arguments#addArgument(ArgumentWithValue, io.marioslab.basis.arguments.Arguments.ArgumentWithValueMatchedCallback)}. If
 * ArgumentWithValue was a subclass of Argument, then users could pass that to the wrong method, so no value parsing would happen.
 * </p>
 *
 * <p>
 * This class is package private as {@link Arguments} can only handle the subclasses {@link Argument} and
 * {@link ArgumentWithValue}. Subclass these classes instead of {@link BaseArgument}. */
abstract class BaseArgument {
	private final String[] forms;
	private final String help;
	private final boolean isOptional;

	/** @param form the form of the argument, e.g. "-v".
	 * @param help the help text to be displayed by {@link Arguments#printHelp(PrintStream)} for this argument.
	 * @param isOptional whether this argument is optional. */
	public BaseArgument (String form, String help, boolean isOptional) {
		this(new String[] {form}, help, isOptional);
	}

	/** @param forms the forms of the argument, e.g. "-v", "--verbose".
	 * @param help the help text to be displayed by {@link Arguments#printHelp(PrintStream)} for this argument.
	 * @param isOptional whether this argument is optional. */
	public BaseArgument (String[] forms, String help, boolean isOptional) {
		if (forms.length == 0) throw new ArgumentException("Argument must have at least one form");
		this.forms = forms;
		this.help = help;
		this.isOptional = isOptional;
	}

	/** Returns forms of the argument, e.g. "-v", "--verbose" **/
	String[] getForms () {
		return forms;
	}

	/** Returns the help string to be displayed. **/
	String getHelpText () {
		return help;
	}

	/** Whether the argument is optional. {@link Arguments#parse(String[])} will raise an {@link ArgumentException} if the argument
	 * is not optional and is never matched. **/
	boolean isOptional () {
		return isOptional;
	}

	@Override
	public int hashCode () {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(forms);
		result = prime * result + ((help == null) ? 0 : help.hashCode());
		result = prime * result + (isOptional ? 1231 : 1237);
		return result;
	}

	@Override
	public boolean equals (Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		BaseArgument other = (BaseArgument)obj;
		if (!Arrays.equals(forms, other.forms)) return false;
		if (help == null) {
			if (other.help != null) return false;
		} else if (!help.equals(other.help)) return false;
		if (isOptional != other.isOptional) return false;
		return true;
	}
}
