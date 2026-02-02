/*
 * Author: Amanda Foxley c3137540
 * Created: 15/05/25
 * SENG3320 Assignment 2
 * 
 * 
 * Class used to represent a single exception, storing its class name, input data, error message, and a list of all stack trace method calls (with line numbers) in order.
 *   Two ExceptionObjects are equal if:
 *       - Same exception name
 *       - Stack trace lists are the same length and all elements equal/in order (Ignoring last 2 calling methods in stack)
 * */

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

    public class ExceptionObject {
    	
        private final String exceptionName;
        private final List<String> inputDataList;
        private final List<String> stackTraceList; 	//Store the stack trace of the exception

        /**
         * Constructor for ExceptionObject.
		 * 
         * Preconditions: e must not be null.
         * Postconditions: An ExceptionObject is created with:
         *       		   exceptionName set to the class name of e
         *       		   stackTraceList populated with stack trace elements from e
         *       		   Ignore the last 2 lines in a stack			
         * 
         * @param e The exception to extract error information from
         */
        public ExceptionObject(Exception e, int iteration, String inputFileName) {
        	
            this.exceptionName = e.getClass().getName(); //Save the error name
            this.stackTraceList = new ArrayList<>();
            this.inputDataList = new ArrayList<>();
            
            //Read lines from the file and store them
            try {
                inputDataList.addAll(Files.readAllLines(Paths.get("inputData"+ File.separator + inputFileName)));
            } catch (IOException ioException) {
                inputDataList.add("[Error reading file: " + inputFileName + "]");
            }
            
            StackTraceElement[] stack = e.getStackTrace();
            
            // Don't add the last 2 lines - these are not needed as we only want unique errors. 
            // Errors are only unique from the KWIC.main onwards we don't care what method from the Main.java class called it. 
            // The last 3 lines in a stack look like this, so ignore the first 2:
            //                             Main.java class 
            //							   Calling class in Main.java e.g. generateErrorSeeds or mutateErrorSeeds
            //							   KWIC.main -- this is the third line in the stack. We need all lines from this line on
            for (int i = 0; i < stack.length - 2; i++) { //Loop through all elements in the error's stacktrace
            	StackTraceElement stackElement = stack[i];
                String methodCall = stackElement.getClassName() + "." + stackElement.getMethodName() + "():line " + stackElement.getLineNumber();
                stackTraceList.add(methodCall); //Add to the stack list
            }
        }

        /**
         * Determines whether another object (error) is equal to this ExceptionObject.
         *
         * Preconditions: None
         *
         * Postconditions: Returns true if the other object is an ExceptionObject with:
         *      		   The same exception name
         *                 An identical stack trace list (same elements, same order)
         *
         * @param obj The error object to compare with.
         * @return true if the objects are equal; false otherwise.
         */
        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof ExceptionObject)) return false;
            ExceptionObject other = (ExceptionObject) obj;

            return Objects.equals(exceptionName, other.exceptionName)
                && Objects.equals(stackTraceList, other.stackTraceList);
        }
        /**
         * Computes a hash code for this ExceptionObject.
         *
         * Preconditions: None
         * Postconditions: Returns an integer hash code, based on the exception name and stack trace
         * 				   Used for storing in hashed data structures
         * 				   Ignore the first two lines of the stack trace
         *
         * @return The hash code.
         */
        @Override
        public int hashCode() {
            return Objects.hash(exceptionName, stackTraceList.size(), stackTraceList);
        }

        /**
         * Returns a string representation of this ExceptionObject.
         *
         * Preconditions: None
         * Postconditions: Returns a string containing the exception name and stack trace.
         *
         * @return A string representation of the exception and its stack trace.
         */
        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("Exception: ").append(exceptionName).append("\n");
            sb.append("Input data that triggered this exception").append("\n");
            for (String input : inputDataList) {
                sb.append("  - ").append(input).append("\n");
            }
            sb.append("Stack Trace:\n");
            for (String methodCall : stackTraceList) {
                sb.append("  at ").append(methodCall).append("\n");
            }
            
            return sb.toString();
        }
    }
