
package io.marioslab.basis.arguments;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import io.marioslab.basis.arguments.ArgumentWithValue.BooleanArgument;
import io.marioslab.basis.arguments.ArgumentWithValue.FloatArgument;
import io.marioslab.basis.arguments.ArgumentWithValue.IntegerArgument;
import io.marioslab.basis.arguments.ArgumentWithValue.StringArgument;

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
			new Arguments().addArgument(new Argument("-v", "--verbose", "help text", false), (a) -> {
			}).addArgument(new Argument("-v", "--verbose", "help text", false), (a) -> {
			});
			assertTrue("Expect an ArgumentException to be thrown.", false);
		} catch (ArgumentException e) {
			// expected state
		}
	}

	@Test
	public void testArgumentWithoutValue () {
		Arguments args = new Arguments();
		boolean[] matched = new boolean[2];
		args.addArgument(new Argument("-v", "--verbose", "Log verbosely.", false), (arg) -> {
			matched[0] = true;
		});
		args.addArgument(new Argument("-w", "--watch", "Watch the file system for changes.", false), (arg) -> {
			matched[1] = true;
		});

		args.parse(new String[] {"-v", "--watch"});

		assertTrue(matched[0]);
		assertTrue(matched[1]);
	}

	@Test
	public void testArgumentWithValue () {
		Arguments args = new Arguments();
		boolean[] matched = new boolean[4];

		args.addArgument(new BooleanArgument("-a", "--aaa", "A.", "<value>", false), (arg, value) -> {
			assertEquals(true, value);
			matched[0] = true;
		});
		args.addArgument(new IntegerArgument("-b", "--bbb", "B.", "<value>", false), (arg, value) -> {
			assertEquals((Integer)1234, value);
			matched[1] = true;
		});
		args.addArgument(new FloatArgument("-c", "--ccc", "C.", "<value>", false), (arg, value) -> {
			assertEquals((Float)123.4f, value);
			matched[2] = true;
		});
		args.addArgument(new StringArgument("-d", "--ddd", "D.", "<value>", false), (arg, value) -> {
			assertEquals("This is a test", value);
			matched[3] = true;
		});

		args.parse(new String[] {"-a", "true", "-b", "1234", "-c", "123.4", "-d", "This is a test"});

		assertTrue(matched[0]);
		assertTrue(matched[1]);
		assertTrue(matched[2]);
		assertTrue(matched[3]);
	}

	@Test
	public void testArgumentWithValueMissing () {
		Arguments args = new Arguments();
		boolean[] matched = new boolean[2];

		args.addArgument(new BooleanArgument("-a", "--aaa", "A.", "<value>", false), (arg, value) -> {
			assertEquals(true, value);
			matched[0] = true;
		});
		args.addArgument(new IntegerArgument("-b", "--bbb", "B.", "<value>", false), (arg, value) -> {
			assertEquals((Integer)1234, value);
			matched[1] = true;
		});

		try {
			args.parse(new String[] {"-a", "true", "-b"});
			assertTrue("Expect an ArgumentException to be thrown.", false);
		} catch (ArgumentException e) {
			// expected state
		}

		assertTrue(matched[0]);
		assertFalse(matched[1]);
	}

	@Test
	public void testNonOptional () {
		Arguments args = new Arguments();
		boolean[] matched = new boolean[3];

		args.addArgument(new BooleanArgument("-a", "--aaa", "A.", "<value>", true), (arg, value) -> {
			assertEquals(true, value);
			matched[0] = true;
		});
		args.addArgument(new IntegerArgument("-b", "--bbb", "B.", "<value>", false), (arg, value) -> {
			assertEquals((Integer)1234, value);
			matched[1] = true;
		});
		args.addArgument(new FloatArgument("-c", "--ccc", "C.", "<value>", false), (arg, value) -> {
			assertEquals((Float)123.4f, value);
			matched[2] = true;
		});

		try {
			args.parse(new String[] {});
			assertTrue("Expect an ArgumentException to be thrown.", false);
		} catch (ArgumentException e) {
			// Expected state
			assertEquals("Expected the following non-optional arguments: -b, -c.", e.getMessage());
		}

		assertFalse(matched[0]);
		assertFalse(matched[1]);
		assertFalse(matched[2]);
	}

	@Test
	public void testPrintHelp () {
		Arguments args = new Arguments();
		args.addArgument(new Argument("-v", "--verbose", "Log things verbosely. Optional.", true), (a) -> {
		});
		args.addArgument(new Argument("-d", "--dispose-all-the-things",
			"This is a help text that is way\nto long. So we stretch it out to multiple\nlines. Hopefully this is readable.", true), (a) -> {
			});
		args.addArgument(new StringArgument("-i", "--input",
			"This is a help text that is way\nto long. So we stretch it out to multiple\nlines. Hopefully this is readable.", "<path>", true), (a, val) -> {
			});
		args.printHelp(System.out);
	}
}
