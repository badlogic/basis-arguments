
package io.marioslab.basis.arguments;

/**
 * <p>
 * An argument that can be parsed by an ArgumentParser. An argument consists of a short form ("-v"), a long form ("--verbose") and
 * a help string ("Turns on verbose logging."). The prefixes "-" and "--" are handled by the parser, the argument itself only
 * needs to return the actual short and long forms.
 * </p>
 *
 * <p>
 * An argument may be optional, indicated by returning <code>false</code> from {@link #isOptional()}.
 * <p>
 *
 * <p>
 * An argument may expect a value, indicated by returning <code>true</code> from {@link #expectsValue()}. If it does expect a
 * value, then the {@link #parseValue()} method will be called by the parser, providing the next argument string in the argument
 * list. The argument returns the parsed value when {@link #getValue()} is called. **/
public interface Argument<T> {
	/** The short form of the argument, e.g. "v". The parser will match this argument if it encounters "-v". **/
	String getShortForm ();

	/** The long form of the argument, e.g. "verbose". The parser will match this argument if it encounters "--verbose". **/
	String getLongForm ();

	/** The help string to be displayed when the parser is instructed to log the help strings of all arguments. **/
	String getHelp ();

	/** Whether the argument is optional. **/
	boolean isOptional ();

	/** Whether the argument expects a value. **/
	boolean expectsValue ();

	/** Parses the value for this argument. The result must be returned by a call to {@link #getValue()}. This method will be
	 * called by the parser with a <code>null</code> argument for arguments that do not expect a value. **/
	void parseValue (String value);

	/** Returns the parsed value or null if this argument does not have a value. **/
	T getValue ();

	/** Returns whether the value was matched during parsing. **/
	boolean wasMatched ();
}
