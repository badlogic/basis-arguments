
package io.marioslab.basis.arguments;

import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/** Takes {@link Argument} and {@link ArgumentWithValue} instances and matches them with a list of command line argument strings
 * or prints a help text for all arguments. **/
public class Arguments {
	/** Registered for an {@link Argument} via {@link Arguments#addArgument(Argument)}, called when the argument matches during a
	 * call to {@link Arguments#parse(String[])}. **/
	public interface ArgumentMatchedCallback {
		public void matched (Argument argument);
	}

	/** Registered for an {@link Argument} via {@link Arguments#addArgument(Argument)}, called when the argument matches during a
	 * call to {@link Arguments#parse(String[])}. **/
	public interface ArgumentWithValueMatchedCallback<T> {
		public void matched (ArgumentWithValue<T> argument, T value);
	}

	private final List<Argument> arguments = new ArrayList<>();
	private final Map<Argument, ArgumentMatchedCallback> argumentCallbacks = new HashMap<>();
	@SuppressWarnings("rawtypes") private final Map<ArgumentWithValue, ArgumentWithValueMatchedCallback> argumentWithValueCallbacks = new HashMap<>();

	/** Adds a new {@link Argument}. The {@link ArgumentMatchedCallback} will be called when the argument is matched by a
	 * {@link #parse(String[])} invocation. **/
	public Arguments addArgument (Argument argument, ArgumentMatchedCallback callback) {
		for (Argument other : arguments) {
			if (other.getShortForm().equals(argument.getShortForm()))
				throw new ArgumentException("Argument with short form " + argument.getShortForm() + " already added.");
			if (other.getLongForm().equals(argument.getLongForm()))
				throw new ArgumentException("Argument with long form " + argument.getLongForm() + " already added.");
		}
		arguments.add(argument);
		argumentCallbacks.put(argument, callback);
		return this;
	}

	/** Adds a new {@link ArgumentWithValue}. The {@link ArgumentWithValueMatchedCallback} will be called when the argument is
	 * matched by a {@link #parse(String[])} invocation. **/
	public <T> Arguments addArgument (ArgumentWithValue<T> argument, ArgumentWithValueMatchedCallback<T> callback) {
		for (Argument other : arguments) {
			if (other.getShortForm().equals(argument.getShortForm()))
				throw new ArgumentException("Argument with short form " + argument.getShortForm() + " already added.");
			if (other.getLongForm().equals(argument.getLongForm()))
				throw new ArgumentException("Argument with long form " + argument.getLongForm() + " already added.");
		}
		arguments.add(argument);
		argumentWithValueCallbacks.put(argument, callback);
		return this;
	}

	/** Parses the given arguments by matching them with the short or long form of {@link Argument} and {@link ArgumentWithValue}
	 * instances added via {@link #addArgument(Argument)} and
	 * {@link #addArgument(ArgumentWithValue, ArgumentWithValueMatchedCallback)}. In case a non-optional argument is not matched,
	 * an {@link ArgumentException} is thrown with the message describing which non-optional arguments have not been found. In case
	 * the value of an argument could not be cased, an {@link ArgumentException} is thrown describing why the value could not be
	 * parsed. In case a value for an argument with expected value is not found, an {@link ArgumentException} is thrown, with its
	 * message describing for which argument the value could not be found. */
	@SuppressWarnings({"unchecked", "rawtypes"})
	public void parse (String[] args) {
		Set<Argument> nonOptional = new HashSet<>();
		for (Argument arg : arguments) {
			if (!arg.isOptional()) nonOptional.add(arg);
		}

		int index = 0;
		while (index < args.length) {
			String a = args[index++];

			boolean matched = false;
			for (Argument arg : arguments) {
				if (arg.getShortForm().equals(a) || arg.getLongForm().equals(a)) {
					matched = true;

					if (!arg.isOptional()) nonOptional.remove(arg);

					if (arg instanceof ArgumentWithValue) {
						if (index >= args.length) throw new ArgumentException("Expected value for argument " + a + ", but no value was given.");
						argumentWithValueCallbacks.get(arg).matched((ArgumentWithValue)arg, ((ArgumentWithValue)arg).parseValue(args[index++]));
					} else {
						argumentCallbacks.get(arg).matched(arg);
					}
					break;
				}
			}

			if (!matched) {
				throw new ArgumentException("Unknown argument " + a);
			}
		}

		if (nonOptional.size() > 0) {
			StringBuilder builder = new StringBuilder();
			int i = 0;
			for (Argument arg : nonOptional) {
				builder.append(arg.getShortForm());
				if (i < nonOptional.size() - 1) builder.append(", ");
				i++;
			}
			throw new ArgumentException("Expected the following non-optional arguments: " + builder.toString() + ".");
		}
	}

	/** Outputs the help text of each argument in the order they were added with {@link #addArgument(Argument)} and
	 * {@link #addArgument(ArgumentWithValue, ArgumentWithValueMatchedCallback)}. Uses the values returned by
	 * {@link Argument#getHelpText()} and {@link ArgumentWithValue#getValueHelpText()}. Throws an {@link IOException} if writing to
	 * the writer failed. **/
	public void printHelp (PrintStream stream) {
		for (Argument arg : arguments) {
			String shortForm = arg.getShortForm();
			String longForm = arg.getLongForm();
			if (arg instanceof ArgumentWithValue) {
				shortForm += " " + ((ArgumentWithValue<?>)arg).getValueHelpText();
				longForm += " " + ((ArgumentWithValue<?>)arg).getValueHelpText();
			}
			shortForm = rightPad(shortForm, 18);
			longForm = rightPad(longForm, 18);

			boolean helpTextOnOwnLine = shortForm.length() > 18 || longForm.length() > 18;
			if (helpTextOnOwnLine) {
				stream.print(shortForm);
				stream.print("\n");
				stream.print(longForm);
				stream.print("\n");
				for (String line : arg.getHelpText().split("\n")) {
					stream.print("                  ");
					stream.print(line);
					stream.print("\n");
				}
			} else {
				String[] lines = arg.getHelpText().split("\n");
				stream.print(shortForm);
				stream.print(lines[0]);
				stream.print("\n");
				stream.print(longForm);
				if (lines.length > 1) {
					stream.print(lines[1]);
					stream.print("\n");
					for (int i = 2, n = lines.length; i < n; i++) {
						stream.print("                  ");
						stream.print(lines[i]);
						stream.print("\n");
					}
				} else {
					stream.print("\n");
				}
			}
			stream.print("\n");
		}
	}

	/** Pads the string with spaces to the right up until the minimum length. **/
	private String rightPad (String value, int minLength) {
		if (value.length() > minLength) return value;
		StringBuilder builder = new StringBuilder();
		builder.append(value);
		for (int i = 0, n = minLength - value.length(); i < n; i++) {
			builder.append(" ");
		}
		return builder.toString();
	}
}