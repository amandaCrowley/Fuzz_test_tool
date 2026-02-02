/*
 * Author: Amanda Foxley c3137540
 * Created: 13/05/25
 * SENG3320 Assignment 2
 * 
 * 
 * Class to generate random ASCII strings and write them to a file
 * This file is used in fuzz testing (in main.java) to generate errors on the KWIC.class
 * */

import java.io.File;
import java.io.FileWriter;
import java.util.Random;

public class RandomStringGenerator {
	
	private static final int numberLinesInFile = 20; 	//This will be a random number up to 20 in this case, can be changed
	private static final Random rand = new Random();

	
    /**
     * Populates the input file with randomly generated ASCII strings
     * Preconditions: The file path specified by FILEPATH must be valid
     * Postconditions:	The file at FILEPATH is created or overwritten.
     * 					It contains up to (numberLinesInFile) lines, each line being a random printable ASCII string
     */
	public static void populateInputFiles(int numberInputFiles) {

		for (int i = 1; i <= numberInputFiles; i++) {
			
			int fileLength = rand.nextInt(numberLinesInFile) + 1; // Random file length up to 1-20 lines

			try
			{
				File inputRandomText = new File("inputData" + File.separator + String.format("inputRandomText%02d.txt", i)); //Create input text file up to numberInputFiles
				inputRandomText.createNewFile();
				FileWriter writer = new FileWriter(inputRandomText, false);

				//Generate a random string for the number of lines required in the file
				for(int j = 0; j < fileLength; j++) {

					String randomString = generateRandomString(); //Generate the string
					writer.write(randomString);	//Write to the text file
					writer.write("\n"); // add new line
				}

				writer.close();
			}
			catch(Exception e)
			{
				System.out.println("Error creating random input files: " + e.getMessage());
				e.printStackTrace();
			}
		}
	}
		
	/**
     * Generates a random ASCII string
     * Preconditions: None
     *
     * Postconditions: Returns a string with a length between 0 and 99.
     * 				   All characters in the string are printable ASCII characters
     *                 including space, digits 0–9, uppercase A–Z, lowercase a–z,
     *                 and symbols: !\"#$%&'()*+,-./:;<=>?@[\\]^_`{|}~.
     * @return A randomly generated ASCII string.
     */
	public static String generateRandomString() {
		int length = rand.nextInt(100); // random int from 0 to 99 (inclusive) 
		
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < length; i++) {
        	char c = (char) (rand.nextInt(94) + 32);
            sb.append(c);
        }
        return sb.toString();
	}
}
