
package io.marioslab.basis.arguments;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/** Takes {@link Argument} and {@link ArgumentWithValue} instances and matches them with a list of command line argument strings
 * or prints a help text for all arguments. **/
public class Arguments {
	/** A parsed argument with and optional value. */
	public class ParsedArgument {
		private final BaseArgument argument;
		private final Object value;

		<T> ParsedArgument (ArgumentWithValue<T> argument, T value) {
			this.argument = argument;
			this.value = value;
		}

		ParsedArgument (Argument argument) {
			this.argument = argument;
			this.value = null;
		}

		public boolean is (BaseArgument argument) {
			return argument == this.argument;
		}

		@SuppressWarnings("unchecked")
		public <T> T getValue (ArgumentWithValue<T> argument) {
			if (argument != this.argument) throw new ArgumentException(
				"The provided argument " + argument.getForms()[0] + " does not match the parsed argument " + argument.getForms()[0] + ".");
			return (T)value;
		}
	}

	/** Parsed arguments as returned by {@link Arguments#parse(String[])}. **/
	public class ParsedArguments {
		private final List<ParsedArgument> parsedArguments;

		ParsedArguments (List<ParsedArgument> parsedArguments) {
			this.parsedArguments = parsedArguments;
		}

		public List<ParsedArgument> getParsedArguments () {
			return parsedArguments;
		}

		public <T> T getValue (ArgumentWithValue<T> argument) {
			for (ParsedArgument parsedArg : parsedArguments) {
				if (parsedArg.is(argument)) {
					return parsedArg.getValue(argument);
				}
			}
			throw new ArgumentException("The argument " + argument.getForms()[0]);
		}

		public boolean has (BaseArgument argument) {
			for (ParsedArgument parsedArg : parsedArguments) {
				if (parsedArg.is(argument)) return true;
			}
			return false;
		}
	}

	private final List<BaseArgument> arguments = new ArrayList<>();

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

	/** Adds a new {@link Argument}. **/
	public Argument addArgument (Argument argument) {
		checkDuplicateForm(argument);
		arguments.add(argument);
		return argument;
	}

	/** Adds a new {@link ArgumentWithValue}. **/
	public <T extends ArgumentWithValue<V>, V> T addArgument (T argument) {
		checkDuplicateForm(argument);
		arguments.add(argument);
		return argument;
	}

	/** Parses the given arguments by matching them with the short or long form of {@link Argument} and {@link ArgumentWithValue}
	 * instances added via {@link #addArgument(Argument)} and {@link #addArgument(ArgumentWithValue)}. In case a non-optional
	 * argument is not matched, an {@link ArgumentException} is thrown with the message describing which non-optional arguments
	 * have not been found. In case the value of an argument could not be parsed, an {@link ArgumentException} is thrown describing
	 * why the value could not be parsed. In case a value for an argument with expected value is not found, an
	 * {@link ArgumentException} is thrown, with its message describing for which argument the value could not be found. */
	@SuppressWarnings({"unchecked", "rawtypes"})
	public ParsedArguments parse (String[] args) {
		Set<BaseArgument> nonOptional = new HashSet<>();
		for (BaseArgument arg : arguments) {
			if (!arg.isOptional()) nonOptional.add(arg);
		}

		List<ParsedArgument> parsedArguments = new ArrayList<>();
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
						parsedArguments.add(new ParsedArgument((ArgumentWithValue)arg, ((ArgumentWithValue)arg).parseValue(args[index++])));
					} else {
						parsedArguments.add(new ParsedArgument((Argument)arg));
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
		return new ParsedArguments(parsedArguments);
	}

	/** Outputs the help text of each argument in the order they were added with {@link #addArgument(Argument)} and
	 * {@link #addArgument(ArgumentWithValue, ArgumentWithValueMatchedCallback)}. Uses the values returned by
	 * {@link Argument#getHelpText()} and {@link ArgumentWithValue#getValueHelpText()}. **/
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
					if (i >= forms.length && i < lines.length) stream.print("                  ");
					if (i < lines.length) stream.print(lines[i]);
					stream.print("\n");
				}
			}
			stream.print("\n");
		}
	}

	/** Returns the help text of each argument in the order they were added with {@link #addArgument(Argument)} and
	 * {@link #addArgument(ArgumentWithValue, ArgumentWithValueMatchedCallback)} as a String. Uses the values returned by
	 * {@link Argument#getHelpText()} and {@link ArgumentWithValue#getValueHelpText()}. **/
	public String printHelp () {
		ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		PrintStream out = new PrintStream(bytes);
		printHelp(out);
		return new String(bytes.toByteArray());
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
