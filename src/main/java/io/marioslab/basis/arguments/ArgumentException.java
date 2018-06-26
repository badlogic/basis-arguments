
package io.marioslab.basis.arguments;

/** Exception used by {@link ArgumentWithValue} implementations and {@link Arguments} to indicate failure states. */
@SuppressWarnings("serial")
public class ArgumentException extends RuntimeException {
	public ArgumentException (String message, Throwable cause) {
		super(message, cause);
	}

	public ArgumentException (String message) {
		super(message);
	}
}
