/*
 * Author: Amanda Foxley c3137540
 * Created: 24/05/25
 * SENG3320 Assignment 2
 * 
 * 
 * Class used to mutate a text file's content
 * Randomly mutates each line in the file
 * Mutation can be a large or small (i.e. delete a character or reverse a whole string)
 * Changes are written back to the file
 * 
 * */

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class Mutate {
    private static final Random rand = new Random();

    /**
     * Mutates the content of a file by applying random small or large mutations to each line and overwriting the original file.
     * Input files will go through a small mutation phase first, if the inputs still do not produce errors they will go through the large mutation phase
     * 
     * Precondition: filePath must point to an existing readable and writable file.
     * Postcondition: The file at filePath is overwritten with mutated content.
     * 
     * @param filePath Path to the file to be mutated.
     * @throws IOException If an I/O error occurs reading or writing the file.
     *
     */
    public static void mutateFile(String filePath, boolean largeMutation) throws IOException {
        List<String> originalLines = Files.readAllLines(Paths.get(filePath));
        List<String> mutatedLines = new ArrayList<>();

        
        for (String line : originalLines) {
        	
        	//Prevents mutation logic from being applied to empty lines
            if (line.trim().isEmpty()) { //Checks if trimmed result is empty
                mutatedLines.add(line); // Keep empty lines. If the line is blank, it's added unchanged to the mutatedLines list.
                continue;
            }
            
          //Line is not empty - mutate
            String mutatedLine;
            if(largeMutation) { //
            	mutatedLine = Mutate.largeMutate(line);
            }else {
            	mutatedLine = Mutate.smallMutate(line); 
            }

            mutatedLines.add(mutatedLine); //Add mutated line to list
        }

        //Write changes (mutated list) back to file
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, false))) { //Overwrite file with changes
            for (String line : mutatedLines) {
                writer.write(line);
                writer.newLine();
            }
        }
    }

    /**
     * Applies a small random mutation to the input string.
     *
     * Precondition: originalInput must not be null.
     * Postcondition: Returns a string of similar length and content with minor alterations.
     * 
     * @param originalInput The original string to mutate.
     * @return A slightly mutated version of the input string.
     */
    public static String smallMutate(String originalInput) {
        if (originalInput == null || originalInput.isEmpty()) return originalInput;

        char[] chars = originalInput.toCharArray();
        int mutationType = rand.nextInt(6); // 0–5

        switch (mutationType) {
            case 0: // Change a character
                int index = rand.nextInt(chars.length);
                chars[index] = randomChar();
                return new String(chars);

            case 1: // Insert a random char
                int insertIndex = rand.nextInt(chars.length + 1);
                char insertChar = randomChar();
                StringBuilder sbInsert = new StringBuilder(originalInput);
                sbInsert.insert(insertIndex, insertChar);
                return sbInsert.toString();

            case 2: // Delete a char;
                if (chars.length == 1) return originalInput;
                int deleteIndex = rand.nextInt(chars.length);
                StringBuilder sbDelete = new StringBuilder(originalInput);
                sbDelete.deleteCharAt(deleteIndex);
                return sbDelete.toString();

            case 3: // Insert space
                int spaceInsertIndex = rand.nextInt(chars.length + 1);
                StringBuilder sbSpaceInsert = new StringBuilder(originalInput);
                sbSpaceInsert.insert(spaceInsertIndex, ' ');
                return sbSpaceInsert.toString();

            case 4: // Remove space or if no spaces in string change a character instead
                int spaceIndex = originalInput.indexOf(' ');
                
                if (spaceIndex == -1) { //No spaces found
                    int randomPosition = rand.nextInt(chars.length);
                    chars[randomPosition] = randomChar();
                    return new String(chars);
                } else {
                	//Space found
                    StringBuilder removedSpace = new StringBuilder(originalInput);
                    removedSpace.deleteCharAt(spaceIndex);
                    return removedSpace.toString();
                }
            case 5: // Arithmetic mutation on digit characters
            	for (int attempts = 0; attempts < 5; attempts++) {
                    int i = rand.nextInt(chars.length);
                    if (Character.isDigit(chars[i])) {
                        int digit = chars[i] - '0';
                        digit += rand.nextBoolean() ? 1 : -1;
                        digit = Math.max(0, Math.min(9, digit));
                        chars[i] = (char)(digit + '0');
                        return new String(chars);
                    }
                }
                return new String(chars); // fallback
            default:
                return originalInput;
        }
    }

    /**
     * Applies a large mutation to the input string.
     *
     * Precondition: originalInput must not be null.
     * Postcondition: Returns a significantly modified string, possibly different length and character set.
     * 
     * @param originalInput The original string to mutate.
     * @return A heavily altered version of the input string.
     * 
     */
    public static String largeMutate(String originalInput) {
        if (originalInput == null || originalInput.isEmpty()) return originalInput;

        int mutationType = rand.nextInt(5); // 0-4

        switch (mutationType) {
            case 0: // Reverse input
                return new StringBuilder(originalInput).reverse().toString();

            case 1: // Repeat input up to 3 times
                return originalInput.repeat(1 + rand.nextInt(3));

            case 2: // Inject control characters
                return originalInput + "\0\u0001\u0002";

            case 3: // Replace with random characters
                int len = 10 + rand.nextInt(100);
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < len; i++) {
                    sb.append((char)(32 + rand.nextInt(95)));
                }
                return sb.toString();
            case 4: // Halve the input
                if (originalInput.length() < 2) return originalInput;
                return originalInput.substring(0, originalInput.length() / 2);
            default:
                return originalInput;
        }
    }

    /**
     * Generates a random printable ASCII character.
     * 
     * Precondition: None
     * Postcondition: Returns a printable ASCII character.
     * 
     * Possible chars: including space, digits 0–9, uppercase A–Z, lowercase a–z,
     *                 and symbols: !\"#$%&'()*+,-./:;<=>?@[\\]^_`{|}~.
     * 
     * @return A char in range 32 to 125.
     *
     */
    private static char randomChar() {
        return (char) (rand.nextInt(95) + 32);
    }
}
