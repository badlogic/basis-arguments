
package io.marioslab.basis.arguments;

import java.io.PrintStream;

/**
 * <p>
 * Defines an argument, e.g. "-v", that can be parsed via {@link Arguments#parse(String[])} or for which help can be displayed via
 * {@link Arguments#printHelp(PrintStream)}.
 * </p>
 *
 * <p>
 * An argument has one or more forms, e.g. "-v" and "-verbose", that {@link Arguments#parse(String[])} matches against. When an
 * argument is matched, its {@link ArgumentMatchedCallback} is called.
 * </p>
 *
 * <p>
 * The forms along with the help text returned by {@link #getHelpText()} are used by {@link Arguments#printHelp(PrintStream)} to
 * display the argument's help information.
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
public class Argument extends BaseArgument {
	/** @param form the form of the argument, e.g. "-v".
	 * @param help the help text to be displayed by {@link Arguments#printHelp(PrintStream)} for this argument.
	 * @param isOptional whether this argument is optional. */
	public Argument (String form, String help, boolean isOptional) {
		super(form, help, isOptional);
	}

	/** @param forms the forms of the argument, e.g. "-v", "--verbose".
	 * @param help the help text to be displayed by {@link Arguments#printHelp(PrintStream)} for this argument.
	 * @param isOptional whether this argument is optional. */
	public Argument (String[] forms, String help, boolean isOptional) {
		super(forms, help, isOptional);
	}
}
