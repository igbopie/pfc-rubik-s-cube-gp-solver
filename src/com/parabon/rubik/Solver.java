 /*
 * Solver.java
 */
package com.parabon.rubik;
 import java.util.List;

 
 /**
 * An interface for a class that can solve any arbitrarily scrambled
 * Rubik's Cube with the goal of doing so in the minimal number of moves.
 */
public interface Solver {
 
 /**
 * Returns a minimal length list of moves that will solve the
 * specified cube.
 * @param cube an arbitrarily scrambled Rubik's Cube.
 * @return a minimal length list of moves that will solve the
 * specified cube.
 */
List<Move> solve(FrontierCube cube);
}