
package io.marioslab.basis.arguments;

import java.io.Writer;

/** An {@link Argument} that expects a value, e.g. "--path path/to/somewhere". In addition to the properties of an Argument,
 * instances of this class must also provide a name for the value to be used when this argument is displayed by
 * {@link Arguments#printHelp(Writer)}. An implementation of this class must provide a {@link #parseValue(String)} implementation
 * that can parse the expected value and return it. */
public abstract class ArgumentWithValue<T> extends Argument {
	private final String valueHelpName;

	/** @param shortForm the short form of the argument, e.g. "-v".
	 * @param longForm the long form of the argument, e.g. "--verbose".
	 * @param help the help text to be displayed by {@link Arguments#printHelp(java.io.Writer)} for this argument.
	 * @param valueHelpName the name for the expected value to be displayed by {@link Arguments#printHelp(java.io.Writer)} for this
	 *           argument.
	 * @param isOptional whether this argument is optional. */
	public ArgumentWithValue (String shortForm, String longForm, String help, String valueHelpName, boolean isOptional) {
		super(shortForm, longForm, help, isOptional);
		this.valueHelpName = valueHelpName;
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
		/** @param shortForm the short form of the argument, e.g. "-v".
		 * @param longForm the long form of the argument, e.g. "--verbose".
		 * @param help the help text to be displayed by {@link Arguments#printHelp(java.io.Writer)} for this argument.
		 * @param valueHelpName the name for the expected value to be displayed by {@link Arguments#printHelp(java.io.Writer)} for
		 *           this argument.
		 * @param isOptional whether this argument is optional. */
		public BooleanArgument (String shortForm, String longForm, String help, String valueHelpName, boolean isOptional) {
			super(shortForm, longForm, help, valueHelpName, isOptional);
		}

		@Override
		public Boolean parseValue (String valueString) {
			if ("true".equals(valueString))
				return true;
			else if ("false".equals(valueString))
				return false;
			else
				throw new ArgumentException("Could not parse value for argument " + getShortForm() + ". Expected 'true' or 'false', got '" + valueString + "'");
		}
	}

	/** An {@link ArgumentWithValue} expecting an integer. */
	public static class IntegerArgument extends ArgumentWithValue<Integer> {
		/** @param shortForm the short form of the argument, e.g. "-v".
		 * @param longForm the long form of the argument, e.g. "--verbose".
		 * @param help the help text to be displayed by {@link Arguments#printHelp(java.io.Writer)} for this argument.
		 * @param valueHelpName the name for the expected value to be displayed by {@link Arguments#printHelp(java.io.Writer)} for
		 *           this argument.
		 * @param isOptional whether this argument is optional. */
		public IntegerArgument (String shortForm, String longForm, String help, String valueHelpName, boolean isOptional) {
			super(shortForm, longForm, help, valueHelpName, isOptional);
		}

		@Override
		public Integer parseValue (String valueString) {
			try {
				return Integer.parseInt(valueString);
			} catch (NumberFormatException e) {
				throw new ArgumentException("Could not parse value for argument " + getShortForm() + ". expected an integer number, got '" + valueString + "'", e);
			}
		}
	}

	/** An {@link ArgumentWithValue} expecting a 32-bit floating point number. */
	public static class FloatArgument extends ArgumentWithValue<Float> {
		/** @param shortForm the short form of the argument, e.g. "-v".
		 * @param longForm the long form of the argument, e.g. "--verbose".
		 * @param help the help text to be displayed by {@link Arguments#printHelp(java.io.Writer)} for this argument.
		 * @param valueHelpName the name for the expected value to be displayed by {@link Arguments#printHelp(java.io.Writer)} for
		 *           this argument.
		 * @param isOptional whether this argument is optional. */
		public FloatArgument (String shortForm, String longForm, String help, String valueHelpName, boolean isOptional) {
			super(shortForm, longForm, help, valueHelpName, isOptional);
		}

		@Override
		public Float parseValue (String valueString) {
			try {
				return Float.parseFloat(valueString);
			} catch (NumberFormatException e) {
				throw new ArgumentException(
					"Could not parse value for argument " + getShortForm() + ". expected a floating point number, got '" + valueString + "'", e);
			}
		}
	}

	/** An {@link ArgumentWithValue} expecting a 32-bit floating point number. */
	public static class StringArgument extends ArgumentWithValue<String> {
		/** @param shortForm the short form of the argument, e.g. "-v".
		 * @param longForm the long form of the argument, e.g. "--verbose".
		 * @param help the help text to be displayed by {@link Arguments#printHelp(java.io.Writer)} for this argument.
		 * @param valueHelpName the name for the expected value to be displayed by {@link Arguments#printHelp(java.io.Writer)} for
		 *           this argument.
		 * @param isOptional whether this argument is optional. */
		public StringArgument (String shortForm, String longForm, String help, String valueHelpName, boolean isOptional) {
			super(shortForm, longForm, help, valueHelpName, isOptional);
		}

		@Override
		public String parseValue (String valueString) {
			return valueString;
		}
	}
}
