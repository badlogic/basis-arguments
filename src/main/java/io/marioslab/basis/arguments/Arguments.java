
package io.marioslab.basis.arguments;

import java.io.Writer;
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

	/** Adds a new Argument. In case an argument with the short or long form was already added to the parser, an
	 * {@link ArgumentException} is thrown. Returns the added argument. **/
	public Arguments addArgument (Argument argument, ArgumentMatchedCallback callback) {
		for (Argument other : arguments) {
			if (other.getShortForm().equals(argument.getShortForm()))
				throw new ArgumentException("Argument with short form " + argument.getShortForm() + " already added to parser.");
			if (other.getLongForm().equals(argument.getLongForm()))
				throw new ArgumentException("Argument with long form " + argument.getLongForm() + " already added to parser.");
		}
		arguments.add(argument);
		argumentCallbacks.put(argument, callback);
		return this;
	}

	/** Adds a new Argument. In case an argument with the short or long form was already added to the parser, an
	 * {@link ArgumentException} is thrown. Returns the added argument. **/
	public <T> Arguments addArgument (ArgumentWithValue<T> argument, ArgumentWithValueMatchedCallback<T> callback) {
		for (Argument other : arguments) {
			if (other.getShortForm().equals(argument.getShortForm()))
				throw new ArgumentException("Argument with short form " + argument.getShortForm() + " already added to parser.");
			if (other.getLongForm().equals(argument.getLongForm()))
				throw new ArgumentException("Argument with long form " + argument.getLongForm() + " already added to parser.");
		}
		arguments.add(argument);
		argumentWithValueCallbacks.put(argument, callback);
		return this;
	}

	/** Parses the given arguments by matching them with the short or long form of {@link Argument} instances added to this parser
	 * via {@link #addArgument(Argument)}. */
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

	/** Outputs the help text of each argument in the order they were added with {@link #addArgument(Argument)}. **/
	public void printHelp (Writer writer) {
	}
}
