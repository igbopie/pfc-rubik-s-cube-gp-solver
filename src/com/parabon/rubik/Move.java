 /*
 * Move.java
 */
package com.parabon.rubik;
 
 public enum Move {
 
 F("F"), F_("F'"), F2("F2"),
 B("B"), B_("B'"), B2("B2"),
 U("U"), U_("U'"), U2("U2"),
 D("D"), D_("D'"), D2("D2"),
 L("L"), L_("L'"), L2("L2"),
 R("R"), R_("R'"), R2("R2");

 private final String label;
 Move(String name) { this.label = name; }
 public String toString() { return label; }
 }