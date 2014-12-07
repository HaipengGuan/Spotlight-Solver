

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;

import org.sat4j.*;

import edu.kit.iti.lfm.spotlight.Board;
import edu.kit.iti.lfm.spotlight.Clause;
import edu.kit.iti.lfm.spotlight.Field;
import edu.kit.iti.lfm.spotlight.Field.FieldColor;
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
    
<<<<<<< HEAD
=======
    
//    TreeSet<Integer> clauseHashCodeSet = new TreeSet<>();
    
>>>>>>> develop
    @Override
    public boolean solve(Board board) throws SpotlightException {
        
    	HashSet<Integer> certainliteralSet = new HashSet<>();
        SATSolver satSolver = new SATSolver();
        // addClauses to solver using satSolver.addClause()        
        // TODO
<<<<<<< HEAD
        for (Field field : board) {
=======
//        System.out.println(board.toDescriptionString());
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
					certainliteralSet.add(fieldSeq);
					flag = true;
					continue;
				} else if (constraint > numOfFieldInRegion) {	// Number of fields in region is smaller than the constraint -> FALSE
					certainliteralSet.add(-1 * fieldSeq);
					flag = true;
					continue;
				}
				int constraint_0 = constraint;
				int numOfFieldInRegion_0 = numOfFieldInRegion;
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
				}	// else -> nothing change
			}
		}


        for (Field field : board) {
			int fieldSeq = field.getSequentialIndex(board);
        	System.out.println("Now processing Field No. " + fieldSeq);
			if (certainliteralSet.contains(fieldSeq) || certainliteralSet.contains(-1*fieldSeq)) {
				System.out.println("skip");
				continue;
			}
        	
>>>>>>> develop
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
				}
			}
//			System.out.println("Now this seq list is: " + seqIndexList.toString());
			int totalLoop = 1 << numOfFieldInRegion;
<<<<<<< HEAD
=======
			System.out.println("total Loop: " + totalLoop);
>>>>>>> develop
			for (int i = 0; i < totalLoop; i++) {
				int [] clauseInteger = new int [seqIndexList.size()];
				if (pop2(i) == constraint) { // DNF
					clauseInteger = applyIntoSeqIndex(fieldSeq, seqIndexList, ~i);
				} else { // KNF
					clauseInteger = applyIntoSeqIndex(-1 * fieldSeq, seqIndexList, (~i) & (totalLoop-1));
				}
				
				if (!satSolver.getClauses().contains(clauseInteger)) {
					satSolver.addClause(new Clause(clauseInteger));
				}
<<<<<<< HEAD
			}	
=======
			}
>>>>>>> develop
		}
        //

        //
        // call solver to solve
        int[] solution_1 = satSolver.solve();
        if(solution_1 == null) {
            return false;
        }
        Arrays.sort(solution_1);
        int [] solution = new int [certainliteralSet.size() + solution_1.length];
        System.arraycopy(solution_1, 0, solution, 0, solution_1.length);
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
<<<<<<< HEAD
        	int absSol = Math.abs(solution[i]);
			int row = (absSol - 1) / board.countColumns() + 1;
			int col = (absSol - 1) % board.countColumns() + 1;
=======
			int row = (Math.abs(solution[i]) - 1) / board.countColumns() + 1;
			int col = (Math.abs(solution[i]) - 1) % board.countColumns() + 1;
>>>>>>> develop
			if (solution[i] > 0) {
				board.getField(row, col).setColor(FieldColor.WHITE);
			} else {
				board.getField(row, col).setColor(FieldColor.BLACK);
			}
		}
        solution = null;
        solution_1 = null;
        return true;
    }
    
    /*
     * This main method can be used to test your implementation.
     */
<<<<<<< HEAD
    public static void main(String[] args) throws SpotlightException, IOException {
=======
    public static void main(String[] args) throws SpotlightException, IOException, NoSuchAlgorithmException {
>>>>>>> develop
        if(args.length == 0) {
            throw new SpotlightException("Expected one argument");
        }
        Board b = Board.fromFile(new File(args[0]));
        SpotlightSolver solver = new SpotlightSolver();
        solver.solve(b);
        b.visualise();
<<<<<<< HEAD
    }

    // making sequence index into DIMACS-Format
    private int[] applyIntoSeqIndex(int theFirst, ArrayList<Integer> sequentialIndex, int indedxInTureTable) {
=======
    	
    }
    
    //
    private int [] applyIntoSeqIndex(int theFirst, ArrayList<Integer> sequentialIndex, int indedxInTureTable) {
>>>>>>> develop
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
}

/*
 * If you want/need to implement more classes, you can put them here. A Java
 * source file can contain at most ONE public declaration, however.
 */
