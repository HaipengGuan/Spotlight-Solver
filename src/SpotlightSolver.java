import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
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
			// the sequence of the field in the region. -> waiting for being applied in the DNF and KNF.
			ArrayList<Integer> sequentialIndexList = new ArrayList<Integer>();
			for (Field fieldInRegion : region) {
				sequentialIndexList.add( board.getSequentialIndex(fieldInRegion));
			}
			
			int totalLoop = 1 << numOfFieldInRegion;
			for (int i = 0; i < totalLoop; i++) {
				if (pop2(i) == constraint) { // DNF
					int [] clause = applyIntoSeqIndex(field.getSequentialIndex(board), sequentialIndexList, ~i);
					satSolver.addClause(new Clause(clause));
				} else { // KNF
					int [] clause = applyIntoSeqIndex(-1 * field.getSequentialIndex(board), sequentialIndexList, (~i) & (totalLoop-1));
					satSolver.addClause(new Clause(clause));
				}
			}	
		}
        //

       
        //
        // call solver to solve
        int[] solution = satSolver.solve();
//        System.out.println(Arrays.toString(solution));
        if(solution == null) {
            return false;
        }
        
        //
        // translate result from solution to board using 
        // b.setColor(row, col, color) with color either 
        // Field.FieldColor.BLACK or Field.FieldColor.WHITE
        // TODO
        
        for (int i = 0; i < solution.length; i++) {
        	int absSol = Math.abs(solution[i]);
			int row = (absSol - 1) / board.countColumns() + 1;
			int col = (absSol - 1) % board.countColumns() + 1;
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
        if(args.length == 0) {
            throw new SpotlightException("Expected one argument");
        }
        Board b = Board.fromFile(new File(args[0]));
        SpotlightSolver solver = new SpotlightSolver();
        solver.solve(b);
        b.visualise();
    }

    // making sequence index into DIMACS-Format
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
