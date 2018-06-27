
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

	private final List<BaseArgument> arguments = new ArrayList<>();
	private final Map<BaseArgument, ArgumentMatchedCallback> argumentCallbacks = new HashMap<>();
	@SuppressWarnings("rawtypes") private final Map<ArgumentWithValue, ArgumentWithValueMatchedCallback> argumentWithValueCallbacks = new HashMap<>();

	private void checkDuplicateForm (BaseArgument argument) {
		for (BaseArgument other : arguments) {
			for (String otherForm : other.getForms()) {
				for (String form : argument.getForms()) {
					if (otherForm.equals(form)) throw new ArgumentException("An Argument with form " + form + " has already been added.");
				}
			}
		}
	}

	private boolean formMatches (BaseArgument argument, String formToMatch) {
		for (String form : argument.getForms()) {
			if (form.equals(formToMatch)) return true;
		}
		return false;
	}

	/** Adds a new {@link Argument}. The {@link ArgumentMatchedCallback} will be called when the argument is matched by a
	 * {@link #parse(String[])} invocation. **/
	public Arguments addArgument (Argument argument, ArgumentMatchedCallback callback) {
		checkDuplicateForm(argument);
		arguments.add(argument);
		argumentCallbacks.put(argument, callback);
		return this;
	}

	/** Adds a new {@link ArgumentWithValue}. The {@link ArgumentWithValueMatchedCallback} will be called when the argument is
	 * matched by a {@link #parse(String[])} invocation. **/
	public <T> Arguments addArgument (ArgumentWithValue<T> argument, ArgumentWithValueMatchedCallback<T> callback) {
		checkDuplicateForm(argument);
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
		Set<BaseArgument> nonOptional = new HashSet<>();
		for (BaseArgument arg : arguments) {
			if (!arg.isOptional()) nonOptional.add(arg);
		}

		int index = 0;
		while (index < args.length) {
			String a = args[index++];

			boolean matched = false;
			for (BaseArgument arg : arguments) {
				if (formMatches(arg, a)) {
					matched = true;

					if (!arg.isOptional()) nonOptional.remove(arg);

					if (arg instanceof ArgumentWithValue) {
						if (index >= args.length) throw new ArgumentException("Expected value for argument " + a + ", but no value was given.");
						argumentWithValueCallbacks.get(arg).matched((ArgumentWithValue)arg, ((ArgumentWithValue)arg).parseValue(args[index++]));
					} else {
						argumentCallbacks.get(arg).matched((Argument)arg);
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
			for (BaseArgument arg : nonOptional) {
				builder.append(arg.getForms()[0]);
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
		for (BaseArgument arg : arguments) {
			String[] formTexts = new String[arg.getForms().length];
			String[] forms = arg.getForms();

			boolean helpTextOnOwnLine = false;
			for (int i = 0, n = formTexts.length; i < n; i++) {
				String form = forms[i];
				if (arg instanceof ArgumentWithValue) form += " " + ((ArgumentWithValue<?>)arg).getValueHelpText();
				form = rightPad(form, 18);
				formTexts[i] = form;
				if (form.length() > 18) helpTextOnOwnLine = true;
			}

			if (helpTextOnOwnLine) {
				for (String form : formTexts) {
					stream.print(form);
					stream.print("\n");
				}
				for (String line : arg.getHelpText().split("\n")) {
					stream.print("                  ");
					stream.print(line);
					stream.print("\n");
				}
			} else {
				String[] lines = arg.getHelpText().split("\n");
				for (int i = 0, n = Math.max(lines.length, forms.length); i < n; i++) {
					if (i < forms.length) stream.print(formTexts[i]);
					if (i < lines.length) stream.print(lines[i]);
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
