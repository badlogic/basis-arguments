
package io.marioslab.basis.arguments;

import java.io.PrintStream;

import io.marioslab.basis.arguments.Arguments.ArgumentWithValueMatchedCallback;

/**
 * <p>
 * Defines an argument that expects a value, e.g. "-p path/to/somewhere", that can be parsed via {@link Arguments#parse(String[])}
 * or for which help can be displayed via {@link Arguments#printHelp(PrintStream)}.
 * </p>
 *
 * <p>
 * An argument has one or more forms, e.g. "-v" or "-verbose", that {@link Arguments#parse(String[])} matches against. When an
 * argument that expects a value is matched, its {@link ArgumentWithValueMatchedCallback} is called.
 * </p>
 *
 * <p>
 * The forms along with the general help text and the value help text returned by {@link #getHelpText()} and
 * {@link #getValueHelpText()} are used by {@link Arguments#printHelp(PrintStream)} to display the argument's help information.
 * </p>
 *
 * <p>
 * Every ArgumentWithValue must implement the {@link #parseValue(String)} method. The method is expected to parse the string into
 * the Java type the class is parameterized on.
 * </p>
 *
 * <p>
 * An argument may be optional or non-optional. If it is non-optional and not matched by {@link Arguments#parse(String[])}, then
 * an {@link ArgumentException} will be raised.
 * </p>
 */
public abstract class ArgumentWithValue<T> extends Argument {
	private final String valueHelpName;

	/** @param form the form of the argument, e.g. "-v".
	 * @param help the help text to be displayed by {@link Arguments#printHelp(PrintStream)} for this argument.
	 * @param valueHelpName the name for the expected value to be displayed by {@link Arguments#printHelp(PrintStream)} for this
	 *           argument.
	 * @param isOptional whether this argument is optional. */
	public ArgumentWithValue (String form, String help, String valueHelpName, boolean isOptional) {
		super(form, help, isOptional);
		this.valueHelpName = valueHelpName;
	}

	/** @param forms the forms of the argument, e.g. "-v", "--verbose".
	 * @param help the help text to be displayed by {@link Arguments#printHelp(PrintStream)} for this argument.
	 * @param valueHelpName the name for the expected value to be displayed by {@link Arguments#printHelp(PrintStream)} for this
	 *           argument.
	 * @param isOptional whether this argument is optional. */
	public ArgumentWithValue (String forms[], String help, String valueHelpName, boolean isOptional) {
		super(forms, help, isOptional);
		this.valueHelpName = valueHelpName;
	}

	/** Return the name for the expected value to be displayed by {@link Arguments#printHelp(PrintStream)} for this argument. **/
	public String getValueHelpText () {
		return valueHelpName;
	}

	/** Parses and returns the value string into a Java type. Raises a {@link ArgumentException} if the value could not be
	 * parsed. */
	public abstract T parseValue (String valueString);

	@Override
	public int hashCode () {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((valueHelpName == null) ? 0 : valueHelpName.hashCode());
		return result;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public boolean equals (Object obj) {
		if (this == obj) return true;
		if (!super.equals(obj)) return false;
		if (getClass() != obj.getClass()) return false;
		ArgumentWithValue other = (ArgumentWithValue)obj;
		if (valueHelpName == null) {
			if (other.valueHelpName != null) return false;
		} else if (!valueHelpName.equals(other.valueHelpName)) return false;
		return true;
	}

	/** An {@link ArgumentWithValue} expecting a boolean ("true", "false"). **/
	public static class BooleanArgument extends ArgumentWithValue<Boolean> {
		/** @param form the form of the argument, e.g. "-v".
		 * @param help the help text to be displayed by {@link Arguments#printHelp(PrintStream)} for this argument.
		 * @param valueHelpName the name for the expected value to be displayed by {@link Arguments#printHelp(PrintStream)} for this
		 *           argument.
		 * @param isOptional whether this argument is optional. */
		public BooleanArgument (String form, String help, String valueHelpName, boolean isOptional) {
			super(form, help, valueHelpName, isOptional);
		}

		/** @param forms the forms of the argument, e.g. "-v", "--verbose".
		 * @param help the help text to be displayed by {@link Arguments#printHelp(PrintStream)} for this argument.
		 * @param valueHelpName the name for the expected value to be displayed by {@link Arguments#printHelp(PrintStream)} for this
		 *           argument.
		 * @param isOptional whether this argument is optional. */
		public BooleanArgument (String[] forms, String help, String valueHelpName, boolean isOptional) {
			super(forms, help, valueHelpName, isOptional);
		}

		@Override
		public Boolean parseValue (String valueString) {
			if ("true".equals(valueString))
				return true;
			else if ("false".equals(valueString))
				return false;
			else
				throw new ArgumentException("Could not parse value for argument " + getForms()[0] + ". Expected 'true' or 'false', got '" + valueString + "'");
		}
	}

	/** An {@link ArgumentWithValue} expecting an integer. */
	public static class IntegerArgument extends ArgumentWithValue<Integer> {
		/** @param form the form of the argument, e.g. "-v".
		 * @param help the help text to be displayed by {@link Arguments#printHelp(PrintStream)} for this argument.
		 * @param valueHelpName the name for the expected value to be displayed by {@link Arguments#printHelp(PrintStream)} for this
		 *           argument.
		 * @param isOptional whether this argument is optional. */
		public IntegerArgument (String form, String help, String valueHelpName, boolean isOptional) {
			super(form, help, valueHelpName, isOptional);
		}

		/** @param forms the forms of the argument, e.g. "-v", "--verbose".
		 * @param help the help text to be displayed by {@link Arguments#printHelp(PrintStream)} for this argument.
		 * @param valueHelpName the name for the expected value to be displayed by {@link Arguments#printHelp(PrintStream)} for this
		 *           argument.
		 * @param isOptional whether this argument is optional. */
		public IntegerArgument (String[] forms, String help, String valueHelpName, boolean isOptional) {
			super(forms, help, valueHelpName, isOptional);
		}

		@Override
		public Integer parseValue (String valueString) {
			try {
				return Integer.parseInt(valueString);
			} catch (NumberFormatException e) {
				throw new ArgumentException("Could not parse value for argument " + getForms()[0] + ". expected an integer number, got '" + valueString + "'", e);
			}
		}
	}

	/** An {@link ArgumentWithValue} expecting a 32-bit floating point number. */
	public static class FloatArgument extends ArgumentWithValue<Float> {
		/** @param form the form of the argument, e.g. "-v".
		 * @param help the help text to be displayed by {@link Arguments#printHelp(PrintStream)} for this argument.
		 * @param valueHelpName the name for the expected value to be displayed by {@link Arguments#printHelp(PrintStream)} for this
		 *           argument.
		 * @param isOptional whether this argument is optional. */
		public FloatArgument (String form, String help, String valueHelpName, boolean isOptional) {
			super(form, help, valueHelpName, isOptional);
		}

		/** @param forms the forms of the argument, e.g. "-v", "--verbose".
		 * @param help the help text to be displayed by {@link Arguments#printHelp(PrintStream)} for this argument.
		 * @param valueHelpName the name for the expected value to be displayed by {@link Arguments#printHelp(PrintStream)} for this
		 *           argument.
		 * @param isOptional whether this argument is optional. */
		public FloatArgument (String[] forms, String help, String valueHelpName, boolean isOptional) {
			super(forms, help, valueHelpName, isOptional);
		}

		@Override
		public Float parseValue (String valueString) {
			try {
				return Float.parseFloat(valueString);
			} catch (NumberFormatException e) {
				throw new ArgumentException("Could not parse value for argument " + getForms()[0] + ". expected a floating point number, got '" + valueString + "'",
					e);
			}
		}
	}

	/** An {@link ArgumentWithValue} expecting a 32-bit floating point number. */
	public static class StringArgument extends ArgumentWithValue<String> {
		/** @param form the form of the argument, e.g. "-v".
		 * @param help the help text to be displayed by {@link Arguments#printHelp(PrintStream)} for this argument.
		 * @param valueHelpName the name for the expected value to be displayed by {@link Arguments#printHelp(PrintStream)} for this
		 *           argument.
		 * @param isOptional whether this argument is optional. */
		public StringArgument (String form, String help, String valueHelpName, boolean isOptional) {
			super(form, help, valueHelpName, isOptional);
		}

		/** @param forms the forms of the argument, e.g. "-v", "--verbose".
		 * @param help the help text to be displayed by {@link Arguments#printHelp(PrintStream)} for this argument.
		 * @param valueHelpName the name for the expected value to be displayed by {@link Arguments#printHelp(PrintStream)} for this
		 *           argument.
		 * @param isOptional whether this argument is optional. */
		public StringArgument (String[] forms, String help, String valueHelpName, boolean isOptional) {
			super(forms, help, valueHelpName, isOptional);
		}

		@Override
		public String parseValue (String valueString) {
			return valueString;
		}
	}
}
