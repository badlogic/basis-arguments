
package io.marioslab.basis.arguments;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.UnsupportedEncodingException;

import org.junit.Test;

import io.marioslab.basis.arguments.ArgumentWithValue.BooleanArgument;
import io.marioslab.basis.arguments.ArgumentWithValue.FloatArgument;
import io.marioslab.basis.arguments.ArgumentWithValue.IntegerArgument;
import io.marioslab.basis.arguments.ArgumentWithValue.StringArgument;
import io.marioslab.basis.arguments.Arguments.ParsedArguments;

public class ArgumentsTest {
	@Test
	public void testEmptyArguments () {
		Arguments args = new Arguments();
		try {
			args.parse(new String[] {"-v", "--verbose", "--path", "path/to/somewhere"});
			assertTrue("Expect an ArgumentException to be thrown.", false);
		} catch (ArgumentException t) {
			// expected state
		}
	}

	@Test
	public void testDuplicateArgumentName () {
		try {
			Arguments args = new Arguments();
			args.addArgument(new Argument(new String[] {"-v", "--verbose"}, "help text", false));
			args.addArgument(new Argument(new String[] {"-v", "--verbose"}, "help text", false));
			assertTrue("Expect an ArgumentException to be thrown.", false);
		} catch (ArgumentException e) {
			// expected state
		}
	}

	@Test
	public void testArgumentWithoutValue () {
		Arguments args = new Arguments();
		Argument verbose = args.addArgument(new Argument(new String[] {"-v", "--verbose"}, "Log verbosely.", false));
		Argument watch = args.addArgument(new Argument(new String[] {"-w", "--watch"}, "Watch the file system for changes.", false));

		ParsedArguments parsedArgs = args.parse(new String[] {"-v", "--watch"});

		assertTrue(parsedArgs.has(verbose));
		assertTrue(parsedArgs.has(watch));
	}

	@Test
	public void testArgumentWithValue () {
		Arguments args = new Arguments();

		BooleanArgument a = args.addArgument(new BooleanArgument(new String[] {"-a", "--aaa"}, "A.", "<value>", false));
		IntegerArgument b = args.addArgument(new IntegerArgument(new String[] {"-b", "--bbb"}, "B.", "<value>", false));
		FloatArgument c = args.addArgument(new FloatArgument(new String[] {"-c", "--ccc"}, "C.", "<value>", false));
		StringArgument d = args.addArgument(new StringArgument(new String[] {"-d", "--ddd"}, "D.", "<value>", false));

		ParsedArguments parsed = args.parse(new String[] {"-a", "true", "-b", "1234", "-c", "123.4", "-d", "This is a test"});

		assertEquals(true, parsed.getValue(a));
		assertEquals((Integer)1234, parsed.getValue(b));
		assertEquals((Float)123.4f, parsed.getValue(c));
		assertEquals("This is a test", parsed.getValue(d));
	}

	@Test
	public void testArgumentWithValueMissing () {
		Arguments args = new Arguments();

		BooleanArgument a = args.addArgument(new BooleanArgument(new String[] {"-a", "--aaa"}, "A.", "<value>", false));
		IntegerArgument b = args.addArgument(new IntegerArgument(new String[] {"-b", "--bbb"}, "B.", "<value>", false));

		try {
			args.parse(new String[] {"-a", "true", "-b"});
			assertTrue("Expect an ArgumentException to be thrown.", false);
		} catch (ArgumentException e) {
			// expected state
		}
	}

	@Test
	public void testNonOptional () {
		Arguments args = new Arguments();

		BooleanArgument a = args.addArgument(new BooleanArgument(new String[] {"-a", "--aaa"}, "A.", "<value>", true));
		IntegerArgument b = args.addArgument(new IntegerArgument(new String[] {"-b", "--bbb"}, "B.", "<value>", false));
		FloatArgument c = args.addArgument(new FloatArgument(new String[] {"-c", "--ccc"}, "C.", "<value>", false));

		try {
			args.parse(new String[] {});
			assertTrue("Expect an ArgumentException to be thrown.", false);
		} catch (ArgumentException e) {
			// Expected state
			assertEquals("Expected the following non-optional arguments: -b, -c.", e.getMessage());
		}
	}

	@Test
	public void testPrintHelp () throws UnsupportedEncodingException {
		Arguments args = new Arguments();
		args.addArgument(new Argument(new String[] {"-v", "--verbose"}, "Log things verbosely. Optional.", true));
		args.addArgument(new Argument(new String[] {"-d", "--dispose-all-the-things"},
			"This is a help text that is way\nto long. So we stretch it out to multiple\nlines. Hopefully this is readable.", true));
		args.addArgument(new StringArgument(new String[] {"-i", "--input"},
			"This is a help text that is way\nto long. So we stretch it out to multiple\nlines. Hopefully this is readable.", "<path>", true));
		args.printHelp(System.out);
		assertEquals(
			"-v                Log things verbosely. Optional.\n" + "--verbose         \n" + "\n" + "-d                \n" + "--dispose-all-the-things\n"
				+ "                  This is a help text that is way\n" + "                  to long. So we stretch it out to multiple\n"
				+ "                  lines. Hopefully this is readable.\n" + "\n" + "-i <path>         This is a help text that is way\n"
				+ "--input <path>    to long. So we stretch it out to multiple\n" + "                  lines. Hopefully this is readable.\n" + "\n" + "",
			args.printHelp());
	}
}
