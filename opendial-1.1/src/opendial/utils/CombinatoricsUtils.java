// =================================================================                                                                   
// Copyright (C) 2011-2015 Pierre Lison (plison@ifi.uio.no)

// Permission is hereby granted, free of charge, to any person 
// obtaining a copy of this software and associated documentation 
// files (the "Software"), to deal in the Software without restriction, 
// including without limitation the rights to use, copy, modify, merge, 
// publish, distribute, sublicense, and/or sell copies of the Software, 
// and to permit persons to whom the Software is furnished to do so, 
// subject to the following conditions:

// The above copyright notice and this permission notice shall be 
// included in all copies or substantial portions of the Software.

// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, 
// EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
// MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. 
// IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY 
// CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, 
// TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE 
// SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
// =================================================================                                                                   

package opendial.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.OptionalInt;
import java.util.Set;
import java.util.stream.Collectors;

import opendial.arch.Logger;
import opendial.bn.values.Value;
import opendial.datastructs.Assignment;

/**
 * Utility functions connected to combinatorial computations.
 *
 * @author  Pierre Lison (plison@ifi.uio.no)
 * @version $Date:: 2014-11-03 15:53:16 #$
 *
 */
public class CombinatoricsUtils {

	// logger
	public static Logger log = new Logger("CombinatoricsUtils", Logger.Level.DEBUG);


	/**
	 * Generates all possible assignment combinations from the set of values
	 * provided as parameters -- each variable being associated with a set of
	 * alternative values.
	 * 
	 * <p>NB: use with caution, computational complexity is exponential!
	 * 
	 * @param valuesMatrix the set of values to combine 
	 * @return the list of all possible combinations
	 */
	public static Set<Assignment> getAllCombinations(Map<String,Set<Value>> valuesMatrix) {

		try {
			// start with a single, empty assignment
			Set<Assignment> assignments = new HashSet<Assignment>();
			assignments.add(new Assignment());

			// at each iterator, we expand each assignment with a new combination
			for (String label : valuesMatrix.keySet()) {
				Set<Value> values = valuesMatrix.get(label);
 				assignments = assignments.stream()
						.flatMap(a -> values.stream().map(v -> new Assignment(a,label,v)).sequential())
						.collect(Collectors.toSet());
			}	
			return assignments;
		}
		catch (OutOfMemoryError e) {
			log.debug("out of memory error, initial matrix: " + valuesMatrix);
			e.printStackTrace();
			return new HashSet<Assignment>();
		}
	}



	/**
	 * Returns the estimated number (higher bound) of combinations for the set of 
	 * variables and associated values given as argument.
	 * 
	 * @param valuesMatrix the set of values to combine
	 * @return the higher bound on the number of possible combinations
	 */
	public static int getNbCombinations( Map<String, Set<Value>> valuesMatrix) {	
		OptionalInt estimation = valuesMatrix.values().stream()
				.mapToInt(set -> set.size())
				.reduce((a,b) -> a*b);
		return (estimation.isPresent())? estimation.getAsInt() : 1;
	}


	/**
	 * Returns the set of possible (variable,value) pairs used in the the
	 * set of assignments given as arguments.
	 * 
	 * @param assignments the assignments from which to extract the pairs
	 * @return the extracted pairs
	 */
	public static Map<String,Set<Value>> extractPossiblePairs(Set<Assignment> assignments) {
		Map<String, Set<Value>> possiblePairs = new HashMap<String,Set<Value>>();
		for (Assignment condition: assignments) {
			for (String variable : condition.getVariables()) {
				if (!possiblePairs.containsKey(variable)) {
					possiblePairs.put(variable,new HashSet<Value>());
				}
				possiblePairs.get(variable).add(condition.getValue(variable));
			}
		}
		return possiblePairs;
	}


	/**
	 * Get the power set of the given set
	 * 
	 * @param originalSet the original set
	 * @param <T> the type of the elements in the set
	 * @return its power set
	 */
	public static <T> Set<Set<T>> getPowerset(Set<T> originalSet) {
		Set<Set<T>> sets = new HashSet<Set<T>>();
		if (originalSet.size() >= 8) {
			log.debug("original set is too big, not returning any result");
			return sets;
		}
		if (originalSet.isEmpty()) {
			sets.add(new HashSet<T>());
			return sets;
		}
		List<T> list = new ArrayList<T>(originalSet);
		T head = list.get(0);
		Set<T> rest = new HashSet<T>(list.subList(1, list.size())); 
		for (Set<T> set : getPowerset(rest)) {
			Set<T> newSet = new HashSet<T>();
			newSet.add(head);
			newSet.addAll(set);
			sets.add(newSet);
			sets.add(set); 
		}           
		return sets;
	}



}
