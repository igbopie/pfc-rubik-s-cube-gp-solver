 /*
 * FrontierCube.java
 */
package com.parabon.rubik;
 import java.util.List;

import ch.randelshofer.rubik.RubiksCube;
 
 public class FrontierCube extends RubiksCube {

 void apply(Move move) {
 switch (move) {
 case F: twistSide(0, false); break;
 case F_: twistSide(0, true); break;
 case F2: twistSide(0, true);
 twistSide(0, true); break;

 case R: twistSide(1, false); break;
 case R_: twistSide(1, true); break;
 case R2: twistSide(1, true);
 twistSide(1, true); break;

 case D: twistSide(2, false); break;
 case D_: twistSide(2, true); break;
 case D2: twistSide(2, true);
 twistSide(2, true); break;

 case B: twistSide(3, false); break;
 case B_: twistSide(3, true); break;
 case B2: twistSide(3, true);
 twistSide(3, true); break;

 case L: twistSide(4, false); break;
 case L_: twistSide(4, true); break;
 case L2: twistSide(4, true);
 twistSide(4, true); break;

 case U: twistSide(5, false); break;
 case U_: twistSide(5, true); break;
 case U2: twistSide(5, true);
 twistSide(5, true); break;
 }
 }

 public void apply(List<Move> moves) {
 for (Move m : moves) apply(m);
 }

 }