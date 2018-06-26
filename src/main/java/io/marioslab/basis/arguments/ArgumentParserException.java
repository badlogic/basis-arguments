
package io.marioslab.basis.arguments;

/** Exception used by {@link Argument} and {@link ArgumentParser} to indicate failure states. */
@SuppressWarnings("serial")
public class ArgumentParserException extends RuntimeException {
	public ArgumentParserException (String message, Throwable cause) {
		super(message, cause);
	}

	public ArgumentParserException (String message) {
		super(message);
	}
}
