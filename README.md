# Fuzz_test_tool
README file specifying instructions for compiling and executing the fuzz tool.

IDE: Eclipse (Version 2024-06 (4.32.0))
Build system: no Gradle or Maven used
Java runtime version = 22.0.1+8

The fuzzing tool will run for a set duration (Default 10mins) however this can be changed as desired.

When using Eclipse you'll need to make sure you add the lib folder to the Java build path. This is because it contains the KWIC.class file, and won't compile without knowing where this is.

Once that is done simply run the Main.java class and the Fuzz testing will commence.

After the test's duration has elasped the fuzz tester will output it's statistics to the console and produce an output.txt file (in outputData folder)

-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
Fuzz testing (also known as random testing) is commonly used to discover software crashes by generating a large amount of random input data. It is a cost-effective alternative to more systematic testing techniques. In this assignment, you are required to apply fuzz testing to evaluate a KWIC program. The KWIC (Key Word in Context) problem is defined as follows:
---------------------------------------------------------------------------------------
The KWIC [Key Word in Context] index system accepts an ordered set of lines; each line is an ordered set of words, and each word is an ordered set of characters. Any line may be “circularly shifted” by repeatedly removing the first word and appending it at the end of the line. The KWIC index system outputs a list of all circular shifts of all lines in alphabetical order.”
The KWIC system is a real system and is widely used in keyword in context indexes for libraries.

For this assignment, you are provided with a Java implementation of the KWIC problem (KWIC.class), which can be found on Canvas. The program is executed using the following
command:

java KWIC input.txt

where input.txt is a plain text file containing the input book titles (each line is a title).
You are required to apply fuzz testing to generate test inputs aimed at crashing the KWIC program. Specifically, you must:

a) Design and develop a fuzz testing tool that can feed a large volume of random data (e.g., random book titles such as “a8h h19%p”) into the KWIC program in an attempt to trigger crashes. (20 marks)

Note: Marks for this component will depend on the sophistication of your tool. A feedbackguided fuzzing tool (e.g., coverage-guided) can receive up to 20 marks if successfully implemented. A non-feedback-based (i.e., pure random) fuzzer can receive a maximum of 10 marks if correctly implemented and functional.

b) Perform the fuzz testing, record the number of unique crashes (i.e., crashes with different exception messages or occurring at different program locations/line numbers), and capture the input that causes each crash. (10 marks)

c) Write a test report describing the design of your fuzzing tool, the test environment, examples of test cases, and a summary of the test results (particularly the number of unique crashes detected). (10 marks)
