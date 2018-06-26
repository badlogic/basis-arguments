#basis-arguments
Basis-arguments is a command line argument parsing and help text library for Java and other JVM languages.

## Motivation
Why another argument parser library?

* No magic API (annotations, reflection, ...).
* Deployed to Maven Central.
* Zero dependencies.

## Setup
As a dependency of your Maven project:

```
<dependency>
   <groupId>io.marioslab.basis</groupId>
   <artifactId>arguments</artifactId>
   <version>1.0</version>
</dependency>
```

As a dependency of your Gradle project:
```
compile 'io.marioslab.basis:arguments:1.0'
```

You can also build the `.jar` file yourself, assuming you have Maven and JDK 1.8+ installed:
```
mvn clean install
```

The resulting `.jar` file will be located in the `target/` folder.

## Basic Usage
Assume you have a class `ConfigBuilder` which allows you to build the configuration for your app with chained method invocations. You can then use basis-arguments to parse command line arguments and invoke the builder.

```
public static void main (String[] argv) {
   Arguments args = new Arguments();

   // Add a simple, optional argument that doesn't expect a value.
   args.addArgument(new Argument("-v", "--verbose", "Display verbose log messages.", true), (argument) -> { configBuilder.verboseLogging(true); });

   // Add an argument that expects a string value.
   args.addArgument(new StringArgument("-s", "--serve-static-files", "Serve static files from the given directory, non-optional.", "<directory>", false), (argument, value) -> { configBuilder.serveStaticFiles(new File(value)); });

   // Add an argument that expects an integer value.
   args.addArgument(new IntegerArgument("-p", "--port", "The port to serve the files from, non-optional.", "<port>", false), (argument, value) -> { configBuilder.port(value); });

   // Add a simple that uses the built-in help text generator to display the help text and exit.
   args.addArgument(new Argument("-h", "--help", "Display this help text and exit.", true), (argument) -> { args.printHelp(System.out); System.exit(0); });

   try {
      args.parse(argv);
   } catch (ArgumentException e) {
      // We got an unexpected argument, or a non-optional argument wasn't given, or an argument value couldn't be parsed,
      // so tell the user what they did wrong, using the error message from the exception.
      System.err.println(e.getMessage());
   }
}
```

First, we create a new `Arguments` instance to which we add the arguments the program can handle. Each argument specifies its short and long form (e.g. '-v' and '--verbose'), a help text to be displayed when the help text is printed out, and whether the argument is optional or not. For arguments that also expect a value, like `StringArgument` or `IntegerArgument, we additionally specify the help text to be displayed for that value, e.g. `<port>`.

After constructing an argument, we can add it to the `Arguments` instance, along with a callback that will be called when that argument is matched. For arguments that don't expect a value, only the argument is passed along. For arguments that expect a value, the argument plus the value as its Java type are passed to the callback.

When all arguments have been added, we can perform two actions:

1) Parse an array of strings and match them with the known arguments via `Arguments#parse()`. The method ensures that all non-optional have been specified and parses argument values into their Java type representation.
2) Format and print a help text to a `PrintStream` based on the arguments via `Arguments#printHelp()`.

## Customization
For arguments that expect a value, basis-arguments provides a handful of built-in implementations that know how to parse a specific value type. These should usually be sufficient. However, you may implement your own subclass of `ArgumentWithType`, e.g. to parse file paths. See [src/main/java/io/marioslab/basis/arguments/ArgumentWithValue.java](src/main/java/io/marioslab/basis/arguments/ArgumentWithValue.java) for the default implementations.

## License
See [LICENSE](./LICENSE)

## Contributing
Simply send a PR and grant written, irrevocable permission in your PR description to publish your code under this repositories [LICENSE](./LICENSE).