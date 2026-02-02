/*
 * Author: Amanda Foxley c3137540
 * Created: 15/05/25
 * SENG3320 Assignment 2
 * 
 * 
 * Class used to store unique ExceptionObject errors, based on exception name and stack trace.
 * 
 * Objects are stored in a HashSet. This preserves uniqueness.
 * 
 *   Two ExceptionObjects are equal if:
 *       - Same exception name
 *       - Stack trace lists are the same length and all elements equal/in order (Ignoring last 2 calling methods in stack)
 * */

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.List;
import java.util.ArrayList;

/**
 * Stores only unique ExceptionObject instances, based on exception name and stack trace.
 * 
 */
public class ExceptionObjectStorage {
    private final Set<ExceptionObject> exceptionSet = new HashSet<>();

    /**
     * Adds a new {@link ExceptionObject} created from the given Exception.
     *
     * Preconditions: e must not be null
     * Postconditions: If an equivalent ExceptionObject does not already exist, it is added to the set and true is returned
     *                 If an equivalent ExceptionObject already exists, the error is not added to the HashSet and false is returned
     *
     * @param e The Exception object to store.
     * @return true if the exception was added, false if it was already present
     */
    public boolean addException(Exception e, int iterationNo, String inputFileName) {
        return exceptionSet.add(new ExceptionObject(e, iterationNo, inputFileName));
    }

    /**
     * Returns an unmodifiable list of all stored ExceptionObject instances in HashSet
     *
     * Preconditions: None
     * Postconditions: Returns an immutable list containing all unique ExceptionObjects
     *                 The order of elements is arbitrary (not insertion order)
     *
     * @return An unmodifiable list of stored ExceptionObjects
     */
    public List<ExceptionObject> getExceptionList() {
        return Collections.unmodifiableList(new ArrayList<>(exceptionSet));
    }

    /**
     * Returns the number of unique ExceptionObjects stored in the HashSet.
     * 
     * Preconditions: None
     * Postconditions: Returns a non-negative integer representing the number of unique exceptions stored.
     *
     * @return The number of stored unique ExceptionObjects.
     */
    public int getSize() {
        return exceptionSet.size();
    }
}