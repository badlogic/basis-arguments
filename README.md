# basis-arguments
Basis-arguments is a command line argument parsing and help text printing library for Java and other JVM languages.

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
   <version>1.2</version>
</dependency>
```

As a dependency of your Gradle project:
```
compile 'io.marioslab.basis:arguments:1.2'
```

You can also build the `.jar` file yourself, assuming you have Maven and JDK 1.8+ installed:
```
mvn clean install
```

The resulting `.jar` file will be located in the `target/` folder.

## Basic Usage

```java
public static void main (String[] argv) {
   Arguments args = new Arguments();

   // Add a simple, optional argument that doesn't expect a value.
   Argument verbose = args.addArgument(new Argument("-v", "Display verbose log messages.", true));

   // Add an argument that expects a string value.
   StringArgument serve = args.addArgument(new StringArgument(new String["-s", "--serve-static-files"], "Serve static files from the given directory, non-optional.", "<directory>", false));

   // Add an argument that expects an integer value.
   IntegerArgument port = args.addArgument(new IntegerArgument("-p", "--port", "The port to serve the files from, non-optional.", "<port>", false));

   Argument help = args.addArgument(new Argument("-h", "--help", "Display this help text and exit.", true));

   try {
      ParsedArguments parsed = args.parse(argv);
      
      // If the user requested to be shown the help text, use the Arguments#printHelp function to output
      // it nicely formated.
      if (parsed.has(help)) {      
      	args.printHelp(System.out);
      	System.exit(0);
      }
      
      // Otherwise check if non-value arguments are given, and get the non-optional port value.
      boolean isLogVerbosely = parsed.has(verbose);
      boolean isServeStaticFiles = parsed.has(serve);
      int portNumber = parsed.getValue(port);
   } catch (ArgumentException e) {
      // We got an unexpected argument, or a non-optional argument wasn't given, or an argument value couldn't be parsed,
      // so tell the user what they did wrong, using the error message from the exception.
      System.err.println(e.getMessage());
   }
}
```

First, we create a new `Arguments` instance to which we add the arguments the program can handle. Each argument specifies one or more forms (e.g. '-v' and '--verbose'), a help text to be displayed when the help text is printed out, and whether the argument is optional or not. For arguments that also expect a value, like `StringArgument` or `IntegerArgument`, we additionally specify the help text to be displayed for that value, e.g. `<port>`.

After constructing an argument, we can add it to the `Arguments` instance.

When all arguments have been added, we can perform two actions.

## Argument Parsing
To parse an array of strings use the `Arguments#parse()` method. The method ensures that all non-optional have been specified and parses argument values into their Java type representation. 

The parsed arguments are returned in form of a `ParsedArguments` instance. 

To check if an argument without value was given, pass the argument to the `ParsedArguments#has()` method, which returns true if the argument was part of the command line arguments. To get the value of an argument with an expected value, pass the argument to the `ParsedArguments#getValue()` method. It returns the parsed value as a Java type instance, e.g. Integer, Float, etc. 

If you depend on the order of arguments passed to your application, or if an argument can occur multiple times, you can iterate through the `ParsedArgument` instances via `ParsedArguments#getParsedArguments()

## Print a help text
To format and print a help text to a `PrintStream` use the `Arguments#printHelp()`. The method will output a nicely formatted list of arguments and their (value) help texts. You must print thel general usage help text yourself.

## Customization
For arguments that expect a value, basis-arguments provides a handful of built-in implementations that know how to parse a specific value type. These should usually be sufficient. However, you may implement your own subclass of `ArgumentWithType`, e.g. to parse file paths. See [src/main/java/io/marioslab/basis/arguments/ArgumentWithValue.java](src/main/java/io/marioslab/basis/arguments/ArgumentWithValue.java) for the default implementations.

## License
See [LICENSE](./LICENSE)

## Contributing
Simply send a PR and grant written, irrevocable permission in your PR description to publish your code under this repository's [LICENSE](./LICENSE).
