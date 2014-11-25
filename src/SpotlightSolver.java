import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
    
    @Override
    public boolean solve(Board board) throws SpotlightException {
        
        SATSolver satSolver = new SATSolver();
        // addClauses to solver using satSolver.addClause()        
        // TODO
        for (Field field : board) {
        	System.out.println("Now processing Field Nr. " + board.getSequentialIndex(field));
			int constraint = field.getConstraint();
			List<Field> region = field.getRegion(board);
			int numOfFieldInRegion = region.size();

			if (numOfFieldInRegion == 0 && constraint == 0 ) {	// Point to outside of the board wit a zero constraint -> it must be TRUE
				satSolver.addClause(new Clause(board.getSequentialIndex(field)));
				continue;
			} else if (constraint > numOfFieldInRegion) {	// Number of fields in region is smaller than the constraint -> it must be FALSE
				satSolver.addClause(new Clause(-1 * board.getSequentialIndex(field)));
				continue;
			}
			ArrayList<Integer> sequentialIndexList = new ArrayList<Integer>();
			for (Field fieldInRegion : region) {
				sequentialIndexList.add(board.getSequentialIndex(fieldInRegion));
			}
			
			ArrayList<ArrayList<Integer>> tempArrayList = findDNFAndKNF(constraint, numOfFieldInRegion);
			
			ArrayList<Integer> DNF = tempArrayList.get(0);
			ArrayList<Integer> KNF = tempArrayList.get(1);
//	        System.out.println("DNF: " + tempArrayList.get(0).toString());
//	        System.out.println("KNF: " + tempArrayList.get(1).toString());
			for (Integer integer : DNF) {
				int [] clause = applyIntoSeqIndex(field.getSequentialIndex(board), sequentialIndexList, ~integer);	// It's '~' integer -> 'NOT' DNF
//				System.out.println(Arrays.toString(clause));
				satSolver.addClause(new Clause(clause));
			}
			for (Integer integer : KNF) {
				int [] clause = applyIntoSeqIndex(-1 * field.getSequentialIndex(board), sequentialIndexList, integer);
//				System.out.println(Arrays.toString(clause));
				satSolver.addClause(new Clause(clause));
			}
			
		}
        //

       
        //
        // call solver to solve
        int[] solution = satSolver.solve();
        System.out.println(Arrays.toString(solution));
        if(solution == null) {
            return false;
        }
        
        //
        // translate result from solution to board using 
        // b.setColor(row, col, color) with color either 
        // Field.FieldColor.BLACK or Field.FieldColor.WHITE
        // TODO
        
        for (int i = 0; i < solution.length; i++) {
			int col = (Math.abs(solution[i]) - 1) % board.countRows() + 1;
			int row = (Math.abs(solution[i]) - 1) / board.countRows() + 1;
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
    public static void main(String[] args) throws SpotlightException, IOException {
//      -------main program----------
//        if(args.length == 0) {
//            throw new SpotlightException("Expected one argument");
//        }
//        Board b = Board.fromFile(new File(args[0]));
//        SpotlightSolver solver = new SpotlightSolver();
//        solver.solve(b);
//        b.visualise();
//      -------main program without outside args----------
    	String fileString = "src/15x15.spotlight.txt";
        Board b = Board.fromFile(new File(fileString));
        SpotlightSolver solver = new SpotlightSolver();
        solver.solve(b);
        b.visualise();
//      ------test of SATSolver-----------
//        SATSolver satSolver = new SATSolver();
//        satSolver.addClause(new Clause(-1,4));
//        satSolver.addClause(new Clause(-2,3));
//        satSolver.addClause(new Clause(-3,-1));
//        satSolver.addClause(new Clause(-4,1));
//        int [] solution = satSolver.solve();
//        System.out.println(Arrays.toString(solution));
        
//      --------test of findKNFandDNK---------
//        long startTime = System.currentTimeMillis();
//        ArrayList<ArrayList<Integer>> tempArrayList = findKNFandDNK(1, 3);
//        long endTime = System.currentTimeMillis();
//        System.out.println("程序运行时间： "+ (endTime - startTime) + "ms");
//        System.out.println(tempArrayList.get(0).toString());
//        System.out.println(tempArrayList.get(1).toString());
    }

    /**
     * To generate both DNF and KNF express that, it is TRUE if and only if there are <b>X</b> ONE in <b>N</b> bit number.
     * <p>
     * Example: if there should be 1 ONE in 3 bit number 
     * <p>
     * <b>DNF</b>: (NOT P1 AND NOT P2 AND P3) OR (NOT P1 AND P2 AND NOT P3) OR (P1 AND NOT P2 AND NOT P3) 
     * <p>
     * -> 001 or 010 or 100; 
     * <p>
     * <b>KNF</b>: (P1 OR P2 OR P3) AND (P1 OR NOT P2 OR NOT P3) AND (NOT P1 AND P2 AND NOT P3) AND (NOT P1 OR NOT P2 OR P3) AND (NOT P1 OR NOT P2 OR NOT P3)
     * <p>
     * -> 111 and 100 and 010 and 001 and 000
     * 
     * @param numOfOne
     * how many ONE in totally numOfBit bits (e.g. 1011: numOfOne == 3, numOfBit == 4)
     * @param numOfBit
     * totally how many bit we talking about. 
     * @return
     * Two ArrayList indicate the DNF and KNF
     */
    private static ArrayList<ArrayList<Integer>> findDNFAndKNF(int numOfOne, int numOfBit) {
    	if (numOfBit < 0) {
			return null;
		}
		ArrayList<ArrayList<Integer>> formaleLIstArray = new ArrayList<ArrayList<Integer>>(2);
		ArrayList<Integer> KNF = new ArrayList<Integer>();
		ArrayList<Integer> DNF = new ArrayList<Integer>();
		if (numOfOne > numOfBit) {
			numOfOne = numOfBit;
		}
		int totalLoop = 1 << numOfBit;
		for (int i = 0; i < totalLoop; i++) {
			if (pop2(i) == numOfOne) {
				DNF.add(i);
			} else {
				KNF.add((~i) & (totalLoop-1));
			}
		}
		formaleLIstArray.add(DNF);
		formaleLIstArray.add(KNF);
		return formaleLIstArray;
	}
    
    //
    private int[] applyIntoSeqIndex(int theFirst, ArrayList<Integer> sequentialIndex, int indedxInTureTable) {
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
        x = x%63;
        return x;
      }
}

/*
 * If you want/need to implement more classes, you can put them here. A Java
 * source file can contain at most ONE public declaration, however.
 */
