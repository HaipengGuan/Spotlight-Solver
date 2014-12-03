import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;

import javax.swing.border.Border;

import com.sun.corba.se.impl.protocol.giopmsgheaders.Message;
import com.sun.org.apache.regexp.internal.recompile;
import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils.Text;

import static edu.kit.iti.lfm.spotlight.Formula.*;
import edu.kit.iti.lfm.spotlight.Board;
import edu.kit.iti.lfm.spotlight.Clause;
import edu.kit.iti.lfm.spotlight.Field;
import edu.kit.iti.lfm.spotlight.Field.FieldColor;
import edu.kit.iti.lfm.spotlight.Formula.Equiv;
import edu.kit.iti.lfm.spotlight.Formula.Impl;
import edu.kit.iti.lfm.spotlight.Formula.Literal;
import edu.kit.iti.lfm.spotlight.Formula.Not;
import edu.kit.iti.lfm.spotlight.Formula.Or;
import edu.kit.iti.lfm.spotlight.Formula;
import edu.kit.iti.lfm.spotlight.FormulaVisitor;
import edu.kit.iti.lfm.spotlight.ISpotlightSolver;
import edu.kit.iti.lfm.spotlight.SATSolver;
import edu.kit.iti.lfm.spotlight.SpotlightException;

/**
 * Implement this class (in this package!) with your solution to the Spotlight
 * puzzle.
 * 
 * <p>
 * You may want to make use of class {@link SATSolver} which provides you with
 * an interface to the SAT solver.
 * 
 * <p>
 * Please do not use any file-system or network-routines. These will make your
 * application crash. To solve this assignment, no such routines are needed.
 * 
 * @see Board
 * @see Field
 * @see SATSolver
 * 
 * @author Haipeng Guan
 */
public class SpotlightSolver implements ISpotlightSolver {

    public SpotlightSolver() {
        // must be present since it is called by the framework.
    }
    
    
    TreeSet<Integer> clauseHashCodeSet = new TreeSet<>();
    HashSet<Integer> certainliteralSet = new HashSet<>();
    
    @Override
    public boolean solve(Board board) throws SpotlightException {
        
        SATSolver satSolver = new SATSolver();
//		HashSet<Integer> clauseHashSet = new HashSet<>();
        // addClauses to solver using satSolver.addClause()        
        // TODO
		boolean flag = true;
		while (flag) {
			flag = false;
			outsideLoop:
			for (Field field : board) {
				int fieldSeq = field.getSequentialIndex(board);
				if (certainliteralSet.contains(fieldSeq) || certainliteralSet.contains(-1*fieldSeq)) {
					continue;
				}
				int constraint = field.getConstraint();
				List<Field> region = field.getRegion(board);
				int numOfFieldInRegion = region.size();
				if (numOfFieldInRegion == 0 && constraint == 0) {	// Point to outside of the board with a zero constraint -> TRUE
					certainliteralSet.add(field.getSequentialIndex(board));
					flag = true;
					continue;
				} else if (constraint > numOfFieldInRegion) {	// Number of fields in region is smaller than the constraint -> FALSE
					certainliteralSet.add(-1 * field.getSequentialIndex(board));
					flag = true;
					continue;
				}
				int constraint_0 = constraint;
				int numOfFieldInRegion_0 = numOfFieldInRegion;
//				ArrayList<Integer> seqIndexList = new ArrayList<Integer>();
				for (Field fieldInRegion : region) {
					int seqInRegion = fieldInRegion.getSequentialIndex(board);
					if (certainliteralSet.contains(seqInRegion)) {	// -> already TRUE
						if (constraint == 0) {	// TRUE field in region with a zero constraint -> FALSE
							certainliteralSet.add(-1 * fieldSeq);
							flag = true;
							continue outsideLoop;
						} else {
							constraint -= 1;
							numOfFieldInRegion -= 1;
						}
					} else if (certainliteralSet.contains(-1 * seqInRegion)) { // -> already FALSE
						numOfFieldInRegion -= 1;
					} else {
	//					seqIndexList.add(seqInRegion);
					}
				}
				if (constraint_0 == 0) {	//	With a zero constraint and all the fields in region is FLASE -> TRUE
					if (numOfFieldInRegion == 0) {
						certainliteralSet.add(fieldSeq);
						flag = true;
						continue;	
					}	// else -> nothing change
				} else if (constraint < constraint_0) {	//	There are at least one TURE in region
					if (constraint < 0) { // to many TRUE -> FALSE
						certainliteralSet.add(-1 * fieldSeq);
						flag = true;
						continue;
					} else if (constraint == 0 && numOfFieldInRegion == 0) {	// just enough TURE fields in region -> TRUE
						certainliteralSet.add(fieldSeq);
						flag = true;
						continue;
					}
				} else if (numOfFieldInRegion < numOfFieldInRegion_0) {	// No TRUE but at least one FLASE
					if (numOfFieldInRegion < constraint) {	// too many FLASE, not enough fields for TRUE -> FLASE
						certainliteralSet.add(-1 * fieldSeq);
						flag = true;
						continue;
					}
				} else {	//	nothing change
					
				}
			}
		}


        for (Field field : board) {
			int fieldSeq = field.getSequentialIndex(board);
        	System.out.println("Now processing Field Nr. " + fieldSeq);
			if (certainliteralSet.contains(fieldSeq) || certainliteralSet.contains(-1*fieldSeq)) {
				System.out.println("skip");
				continue;
			}
        	
			int constraint = field.getConstraint();
			List<Field> region = field.getRegion(board);
			int numOfFieldInRegion = region.size();
			// the sequence of the field in the region. -> waiting for being applied in the DNF and KNF.
			ArrayList<Integer> seqIndexList = new ArrayList<Integer>();
			for (Field fieldInRegion : region) {
				int seqInRegion = fieldInRegion.getSequentialIndex(board);
				if (certainliteralSet.contains(seqInRegion)) {	// -> already TRUE
						constraint -= 1;
						numOfFieldInRegion -= 1;
				} else if (certainliteralSet.contains(-1 * seqInRegion)) { // -> already FALSE
					numOfFieldInRegion -= 1;
				} else {
					seqIndexList.add(seqInRegion);
//					if (seqInRegion > fieldSeq) {	//	It can't be TRUE at the same time.
//						satSolver.addClause(new Clause(-1 * seqInRegion, -1 * fieldSeq));
//					}	
				}
			}

			int totalLoop = 1 << numOfFieldInRegion;
			System.out.println("totalLoop: " + totalLoop);
			int [] clauseInteger = new int [seqIndexList.size()];
			for (int i = 0; i < totalLoop; i++) {
				if (pop2(i) == constraint) { // DNF
//					indedxInTureTable = ~i;
//					clause[mainFieldIndex] = seqIndexList.get(mainFieldIndex);
					clauseInteger = applyIntoSeqIndex(fieldSeq, seqIndexList, ~i);
//					satSolver.addClause(new Clause(clause));
				} else { // KNF
//					indedxInTureTable = (~i) & (totalLoop-1);
//					seqIndexList.set(mainFieldIndex, -1 * seqIndexList.get(mainFieldIndex));
//					clause[mainFieldIndex] = -1 * seqIndexList.get(mainFieldIndex);
					clauseInteger = applyIntoSeqIndex(-1 * fieldSeq, seqIndexList, (~i) & (totalLoop-1));
//					satSolver.addClause(new Clause(clause));
				}
//				Arrays.sort(clauseInteger, new byAbsComparator()); // sort by absolute value

//				satSolver.addClause(new Clause(clauseInteger));
//				System.out.println("Now the size of clause set is " + satSolver.getClauses().size() + " in the field " + fieldSeq);
				
				int hashCode = Arrays.toString(clauseInteger).hashCode();
				if (!clauseHashCodeSet.contains(hashCode)) {
					satSolver.addClause(new Clause(clauseInteger));
					clauseHashCodeSet.add(hashCode);
				}
			}

			System.out.println("Now the size of clause set is " + satSolver.getClauses().size() + " in the field " + fieldSeq);
		}
        //

//        System.out.println("finish collect, now transfering...");
//        Iterator<Integer []> iterator = resultClauseSet.iterator();
//        while (iterator.hasNext()) {
//        	satSolver.addClause(new Clause(toPrimitive(iterator.next())));
//        	iterator.remove();
//		}
        //
        // call solver to solve
        System.out.println("Total size of clause: " + satSolver.getClauses().size());
        System.out.println("SAT solving...");
        int[] solution_1 = satSolver.solve();
        if(solution_1 == null) {
        	System.out.println("solution is null");
            return false;
        }
        System.out.println("Copying result part 1....");
        int [] solution = new int [certainliteralSet.size() + solution_1.length];
        System.arraycopy(solution_1, 0, solution, 0, solution_1.length);
        System.out.println("Copying result part 2....");
        int j = solution_1.length;
		Iterator<Integer> iterator = certainliteralSet.iterator();
		while (iterator.hasNext()) {
			solution[j] = iterator.next();
			j += 1;
		}
        //
        // translate result from solution to board using 
        // b.setColor(row, col, color) with color either 
        // Field.FieldColor.BLACK or Field.FieldColor.WHITE
        // TODO
        
        for (int i = 0; i < solution.length; i++) {
//        	int[] rowAndCol = seqToRowCol(Math.abs(solution[i]) - 1, board.countRows(), board.countColumns());
			int row = (Math.abs(solution[i]) - 1) / board.countColumns() + 1;
			int col = (Math.abs(solution[i]) - 1) % board.countColumns() + 1;
			if (solution[i] > 0) {
				board.getField(row, col).setColor(FieldColor.WHITE);
			} else {
				board.getField(row, col).setColor(FieldColor.BLACK);
				
			}
		}
        
        return true;
    }
    
    /*
     * This main method can be used to test your implementation.
     */
    public static void main(String[] args) throws SpotlightException, IOException, NoSuchAlgorithmException {
//      -------main program----------
//        if(args.length == 0) {
//            throw new SpotlightException("Expected one argument");
//        }
//        Board b = Board.fromFile(new File(args[0]));
//        SpotlightSolver solver = new SpotlightSolver();
//        solver.solve(b);
//        b.visualise();
//      -------main program without outside args----------
    	String fileString = "src/20x20.spotlight.txt";
        Board b = Board.fromFile(new File(fileString));
        SpotlightSolver solver = new SpotlightSolver();
        
        long startTime = System.currentTimeMillis();
        solver.solve(b);
        long endTime = System.currentTimeMillis();
        System.out.println("Total time: "+ (endTime - startTime) + "ms");
        
        b.visualise();

//      --------test of Comparator---------
//    	int[] data = new int[] { 5, 4, 2, 1, 3 };
//    	Integer[] sorted = new Integer[] { 5, -4, 2, 1, 3 };
//    	Arrays.sort(sorted, new byAbsComparator());
//    	Arrays.sort(sorted, new Comparator<Integer>() {
//    	    public int compare(Integer o1, Integer o2) {
//    	        int x = Math.abs(o1);
//    	        int y = Math.abs(o2);
//    	        return (x < y) ? -1 : ((x == y) ? 0 : 1);
//    	    }
//    	});
//    	System.out.println(Arrays.toString(sorted));
//      --------test of findSameList---------

//    	Integer [] a1 = new Integer[] {1,2,3,4};
//    	Integer [] a2 = new Integer[] {1,-2,3,4};
//    	Integer [] a3 = new Integer[] {1,2,3};
//    	Integer [] a4 = new Integer[] {2,1,3,4};
//    	Integer[] test = findSameList(a1, a4);
//    	if (test != null) {
//    		System.out.println(Arrays.toString(test));
//		} else {
//			System.out.println("null");
//		}
    	
    }
    
    //
    private int [] applyIntoSeqIndex(int theFirst, ArrayList<Integer> sequentialIndex, int indedxInTureTable) {
    	int[] seqIndexInClause = new int[sequentialIndex.size() + 1];
    	seqIndexInClause[0] = theFirst;
		for (int i = 0; i < sequentialIndex.size(); i++) {
			seqIndexInClause[i + 1] = sequentialIndex.get(i); 
			if ((indedxInTureTable & 1<<i) == 0) {
				seqIndexInClause[i + 1] *= -1;
			}
		}
		return seqIndexInClause;
	}


    
    
    // 判断两个有序的数组是否“互补”
    private  Integer[] findSameList(Integer[] a, Integer[] b) {
		if (a.length != b.length) {
			return null;
		}
		ArrayList<Integer> resultList = new ArrayList<Integer>();
		for (int i = 0; i < a.length; i++) {
			if (a[i] == b[i]) {
				resultList.add(a[i]);
			} else if (a[i] + b[i] == 0) {
				continue;
			} else {
				return null;
			}
		}
		return (Integer[]) resultList.toArray(new Integer[0]);
	}
    
//    private Integer [] simplifySet(Integer [] key, HashSet<Integer []> intergerSet) {
//		if (!intergerSet.contains(key)) {
//			return null;
//		}
//		for (Integer[] integers : intergerSet) {
//			if (key.equals(integers)) {
//				continue;
//			}
//			Integer [] sameIntegers = findSameList(key, integers);
//			if (sameIntegers != null) {
//				resultClauseSet.remove(key);
//				resultClauseSet.remove(integers);
//				resultClauseSet.add(sameIntegers);
////				return simplifySet(sameIntegers,intergerSet);
//				break;
//			}
//		}
//		return key;
//	}

    
    // Calculate the number of One in a bit number. 
    private static int pop2(int x) {
        int n;
        n = (x >> 1) & 033333333333; 
        x = x - n;
        n = (n >> 1) & 033333333333;
        x = x - n;
        x = (x + (x >> 3)) & 030707070707;
        x = x % 63;
        return x;
      }
    
    // Convert Integer list to int list
    private int [] toPrimitive(Integer [] list) {
		int [] listInt = new int [list.length];
		for (int i = 0; i < listInt.length; i++) {
			listInt[i] = list[i];
		}
		return listInt;
	}
    // Convert int list to Integer list
    private Integer [] toObject(int [] list) {
		Integer [] listInt = new Integer [list.length];
		for (int i = 0; i < listInt.length; i++) {
			listInt[i] = list[i];
		}
		return listInt;
	}
}


// Absolute value comparator
class byAbsComparator implements Comparator<Object>{
	public int compare(Object o1, Object o2) {
        int x = Math.abs((Integer)o1);
        int y = Math.abs((Integer)o2);
        return (x < y) ? -1 : ((x == y) ? 0 : 1);
	}
}
/*
 * If you want/need to implement more classes, you can put them here. A Java
 * source file can contain at most ONE public declaration, however.
 */
