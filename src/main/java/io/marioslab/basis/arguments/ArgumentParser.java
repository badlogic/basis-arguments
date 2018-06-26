
package io.marioslab.basis.arguments;

import java.util.ArrayList;
import java.util.List;

public class ArgumentParser {
	private final List<Argument<?>> arguments = new ArrayList<>();

	/** Adds a new Argument to this parser. In case an argument with the short or long form was already added to the parser, an
	 * {@link ArgumentParserException} is thrown. Returns the added argument. **/
	public Argument<?> addArgument (Argument<?> argument) {
		for (Argument<?> other : arguments) {
			if (other.getShortForm().equals(argument.getShortForm())) throw new ArgumentParserException(
				"Argument with short form " + argument.getShortForm() + " already added to parser.");
			if (other.getLongForm().equals(argument.getLongForm()))
				throw new ArgumentParserException("Argument with long form " + argument.getLongForm() + " already added to parser.");
		}
		arguments.add(argument);
		return argument;
	}

	/** Parses the given arguments by matching them with short and long form of {@link Argument} instances added to this parser via
	 * {@link #addArgument(Argument)}. */
	public void parseArguments (String[] args) {

	}
}
