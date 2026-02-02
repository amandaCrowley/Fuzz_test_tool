/*
 * Author: Amanda Foxley c3137540
 * Created: 13/05/25
 * SENG3320 Assignment 2
 * 
 * 
 * This is the main class to run fuzz tests on the KWIC.class.
 * 
 * It first populates a set number of input text files, which contain random text/data (Can be changed to desired amount from 1-99).
 * These text files will be used in the generateNonErrorSeeds method. Each file is passed to the KWIC.main(inputText) method to see if it makes the KWIC class error.
 * 
 * If it does cause an error, it will be recorded and stored in generationStorage.
 * If it does not cause an error it's index will be stored in the nonErrorSeedSet, for mutation in the mutation phases.
 * 
 * Once all input files have been tested, the mutateNonErrorSeeds method is called to apply small mutations to the text files which didn't originally cause an error.
 * These are mutated using the Mutate class and again passed to the KWIC.main(inputText) method to see if it makes the KWIC class error.
 * 
 * After this the mutateNonErrorSeeds is called again to apply large mutations to the inputs that still didn't cause errors.
 * 
 * The total error for generation and mutation methods are recorded, the number of unique errors are also recorded.
 * These error stats and their inputs are written to an external file (outputData/output.txt).
 * 
 * */
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class Main {

	private static final int numberInputFiles = 99; 		//up to 99 files
	final static String OUTPUTFILE = "outputData\\output.txt";
	static ExceptionObjectStorage generationStorage = new ExceptionObjectStorage(); //Errors encountered in the generation portion of the program will be stored in this data structure (underlying is a HashSet)
	static ExceptionObjectStorage mutationStorage = new ExceptionObjectStorage();  //Mutation errors stored here
	
	private static final long MAX_DURATION_MILLIS = 10 * 60 * 1000; // Runs testing for 10 minutes, can be changed as desired
	//private static final long MAX_DURATION_MILLIS = 1 * 60 * 1000; // Runs testing for 1 minute
	//private static final long MAX_DURATION_MILLIS = 60 * 60 * 1000; // Runs testing for 60 minutes
	
	
	/*
	 * Entry point for the KWIC fuzz testing process.
	 * 
	 * Preconditions: inputData directory must exist.
	 * Postconditions: Random input files are generated, tested for errors, then mutate ones that didn't cause errors, results are written to output file & displayed to console.
	 * 
	 * @param args Command-line arguments - not used
	 * */
	public static void main(String[] args) {
		long startTime = System.currentTimeMillis(); //Current time
		int totalGenerationErrors = 0;
		int totalMutationErrors = 0;
		
		while (System.currentTimeMillis() - startTime < MAX_DURATION_MILLIS) { //Loop through test until desired time reached
			
			Set<Integer> nonErrorSeedSet = new HashSet<>(); //File number (index) of input files that have not generated errors, in this loop 
			
			RandomStringGenerator.populateInputFiles(numberInputFiles); //Generate input files with random text in them
			totalGenerationErrors += generateNonErrorSeeds(nonErrorSeedSet); 	// Store inputs that haven't caused errors
			totalMutationErrors += mutateNonErrorSeeds(nonErrorSeedSet, false);	  // Small mutate phase - Mutate inputs that haven't caused errors to find new methods/areas of the code to explore
			totalMutationErrors += mutateNonErrorSeeds(nonErrorSeedSet, true);	  // Large mutate phase
		}
		
		//Total fuzzing stats - Print to console and write to file
		displayStats(totalGenerationErrors, totalMutationErrors);
	}
	
	/**
	 * Tests initial random input files with KWIC class and records which inputs that don't cause exceptions.
	 *
	 * Preconditions: inputData directory contains files from RandomStringGenerator
	 * Postconditions: Exception details are stored, non error-triggering text file indexes are recorded
	 * 
	 * @param nonErrorSeedSet Set to record text file indexes (that didn't trigger exceptions)
	 */
	private static int  generateNonErrorSeeds(Set<Integer> nonErrorSeedSet) {
		int generationErrors = 0;
		
		//Retrieve the input file names
		String[] inputFilePaths;
		File inputFolder = new File("inputData"); //Folder name
		inputFilePaths = inputFolder.list();	  //List of items stored in folder
		
		for(int i = 0; i < numberInputFiles; i++) { //Loop through input text files
			try {

                String[] inputText = new String[1];
                inputText[0] = "inputData"+ File.separator + inputFilePaths[i];

                KWIC.main(inputText); //Run KWIC program with input file, each file contains a random input strings

                //Input file didn't cause an error, store input index
				if (!nonErrorSeedSet.contains(i+1)) {
					nonErrorSeedSet.add(i+1);
				}
				
			}catch (Exception e) { 	
				
				//Input caused error
				generationErrors++;
				generationStorage.addException(e, i, inputFilePaths[i]); //Add exception to set - Won't add if not unique
			}
		}
		return generationErrors; //Returns number of non-unique errors
	}
	
	
	/**
	 * Mutates previously identified non error-causing inputs (text files) and checks for errors on the mutated files.
	 * 
	 * Preconditions: nonErrorSeedSet contains valid indices of files within "inputData/"
	 * Postconditions: Each non error-seed file is mutated multiple times.
	 * 				   Input files will go through a small mutation phase first, if the inputs still do not produce errors they will go through the large mutation phase
	 * 				   KWIC is run against mutated files.
	 * 				   Unique exceptions are stored in mutationStorage.
	 *
	 * @param nonErrorSeedSet Set to record text file indexes (that didn't trigger exceptions)
	 */
	private static int mutateNonErrorSeeds(Set<Integer> nonErrorSeedSet, boolean largeMutation) {
		
		int mutationErrors = 0;
		int mutateFileAttempts = 8; //Number of times we want to mutate a file before moving on
	    Random rand = new Random();
	    
	    String[] inputFilePaths;
		File inputFolder = new File("inputData"); //Folder name
		inputFilePaths = inputFolder.list();	  //List of items stored in folder

		//Check if we have anything to mutate
		if (inputFilePaths == null) {
	        System.err.println("Nothing to mutate. No input files found in inputData folder.");
	        return mutationErrors;
	    }
		
		List<Integer> seedList = new ArrayList<>(nonErrorSeedSet); //Holds previously discovered indexes of text files that didn't cause errors (from generation phase)
		
		//Loop through all non-error seeds (input file indexes) to find errors
	    for (int i = 0; i < nonErrorSeedSet.size(); i++) { 
	    	
	    	int seedIndex = seedList.get(rand.nextInt(seedList.size())); //Get a random file index from set
	    	
	        if (seedIndex < 0 || seedIndex >= inputFilePaths.length) { //Index not valid
	            System.err.println("Invalid seed index: " + seedIndex);
	            continue;
	        }
	        
	        String fileName = inputFilePaths[seedIndex];
	        String filePath = "inputData" + File.separator + fileName;
	        
	        //Mutate each file several times to try find a unique error
	        for (int j = 0; j < mutateFileAttempts; j++) { 
	            try {
	                Mutate.mutateFile(filePath, largeMutation); // Mutate and overwrite file
	                
	                String[] inputFile = new String[1];
	                inputFile[0] = filePath;
	                KWIC.main(inputFile);

	            } catch (Exception e) { 
	            	
	            	//Input caused error
	                mutationErrors++;
	                mutationStorage.addException(e, i, inputFilePaths[i]); //Add exception to set - will not add if not unique
	            }
	        }
	    }
		return mutationErrors; //Returns number non-unique errors
	}

	/**
	 * Calculate fuzz test statistics, output them to console and write to output.txt file
	 * 
	 * Precondition: Output directory "outputData" exists and is writable.
     * Postcondition: Exception details and fuzzing stats have been displayed to console and written to output.txt
     * 
	 * @param totalGenerationErrors - Stores total number of generation errors over the lifetime of the fuzzer
	 * @param totalMutationErrors - Stores total number of mutation errors over the lifetime of the fuzzer
	 */
	private static void displayStats(int totalGenerationErrors, int totalMutationErrors) {
		
		int uniqueMutationErrors = 0; //Stores unique number of mutation phase errors
		
		try (FileWriter writer = new FileWriter(OUTPUTFILE)) {
			
			writer.write("--------------------------------------------------------------------------------\n");
			writer.write("KWIC Fuzz Test Report"+ "\n");
			writer.write("Unique errors running fuzz tool:"+ "\n");
			writer.write("--------------------------------------------------------------------------------\n\n");
			
			//Compare sets for unique errors and return quantity
			Set<ExceptionObject> generationSet = new HashSet<>(generationStorage.getExceptionList()); //ExceptionObject stored from generation phases
		    Set<ExceptionObject> mutatationSet = new HashSet<>(mutationStorage.getExceptionList());	//ExceptionObject stored from mutation phases
		    List<ExceptionObject> uniqueErrors = new ArrayList<ExceptionObject>(generationSet); //Unique list of errors
		    
		    //Check each ExceptionObject in the mutation set and add to the unique errors list if it not already in the generationSet
		    for(ExceptionObject error: mutatationSet) {
		    	if(!generationSet.contains(error)) { //If object is not in generationError storage, it is unique
		    		uniqueMutationErrors++;
		    		uniqueErrors.add(error);
		    	}
		    }
		    
			// Write exception details to file
			for (ExceptionObject e : uniqueErrors) {
				writer.write(e.toString());
				writer.write(System.lineSeparator());
			}	    
			
			writer.write("--------------------------------------------------------------------------------\n");
			writer.write("Fuzzer exception statistics::"+"\n");
			writer.write("Number non-unique exceptions during generation phase:" + totalGenerationErrors +"\n");
			writer.write("Number non-unique exceptions during mutation phase:" + totalMutationErrors +"\n");
			writer.write("Total unique exceptions during generation phase: " + generationStorage.getSize() +"\n");
			writer.write("Total unique exceptions during mutation phase: " + uniqueMutationErrors +"\n");
			writer.write("Total unique exceptions running fuzz tool: " + uniqueErrors.size() +"\n");
			writer.write("--------------------------------------------------------------------------------\n");
			
			//Display exception details to console
			System.out.println("--------------------------------------------------------------------------------\n");
			System.out.println("KWIC Fuzz Test Report"+ "\n");
			System.out.println("Unique errors running fuzz tool:");
			System.out.println("--------------------------------------------------------------------------------\n");
			
			//Display error details
			for (ExceptionObject e : uniqueErrors) {
				System.out.println(e.toString());
				System.out.println(System.lineSeparator());
			}	
			
			//Display stats
			System.out.println("--------------------------------------------------------------------------------\n");
			System.out.println("Fuzzer exception statistics:"+"\n");
			System.out.println("Number non-unique exceptions during generation phase:" + totalGenerationErrors +"\n");
			System.out.println("Number non-unique exceptions during mutation phase:" + totalMutationErrors +"\n");
			System.out.println("Total unique exceptions during generation phase: " + generationStorage.getSize() +"\n");
			System.out.println("Total unique exceptions during mutation phase: " + uniqueMutationErrors +"\n");
			System.out.println("Total unique exceptions running fuzz tool: " + uniqueErrors.size() +"\n");
			System.out.println("--------------------------------------------------------------------------------\n");
			
		} catch (IOException e) {
			System.err.println("Error writing to output file: " + e.getMessage());
		}
	}
}