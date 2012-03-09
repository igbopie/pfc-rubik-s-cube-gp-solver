package ec.app.rubik;

import java.io.Serializable;
import java.util.Vector;

import ec.EvolutionState;
/**
 * Create and save new rubik's cubes from different difficulty.
 * @author nacho
 *
 */
public class Cache implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 940890455361472998L;
	/**
	 * Rubiks storage.
	 * 
	 */
	private MyRubiksCube[][] cache;
	/**
	 * Initializate storage.
	 * @param maxcubes - max number of cubes a difficulty can have
	 * @param diff - highest difficulty we will reach.
	 * @param state
	 */
	public Cache(int maxcubes, int diff, EvolutionState state) {
		this.init(maxcubes, diff, state);
	}
	public Cache(int maxcubes, int diff) {
		this.init(maxcubes, diff, null);
	}

	private void init(int maxcubes, int diff, EvolutionState state){
		cache = new MyRubiksCube[diff][];
		int posibilities = 12;
		int maxiters = 20000;
		for (int i = 0; i < cache.length; i++) {

			Vector<MyRubiksCube> auxvector = new Vector<MyRubiksCube>();
			if(state!=null)
				state.output.message("Init Diff " + (i + 1));
			if (posibilities <= maxcubes) {
				auxvector.addAll(generatePosibilities(i + 1));
			} else {
				boolean exit = false;
				for (int j = 0; auxvector.size() < maxcubes && !exit; j++) {
					MyRubiksCube uno = new MyRubiksCube();
					uno.desordenarCubo(i + 1);
					int z = 0;
					while (auxvector.contains(uno) && !exit) {// No quiero
																// repetidos.
						uno = new MyRubiksCube();
						uno.desordenarCubo(i + 1);
						if (z > maxiters) {
							exit = true;
						}
						z++;
					}
					if (!exit) {
						auxvector.add(uno);
					}
				}
			}
			cache[i] = auxvector.toArray(new MyRubiksCube[0]);
			posibilities *= 6 * 2;
		}
	}
	private Vector<MyRubiksCube> generatePosibilities(int i) {
		return this.generatePosibilities(new MyRubiksCube(), 0, i);

	}
	private Vector<MyRubiksCube> generatePosibilities(MyRubiksCube cubo,
			int depth, int maxdepth) {
		Vector<MyRubiksCube> array = new Vector<MyRubiksCube>();
		if (depth >= maxdepth) {
			array.add(cubo);
		} else {
			for (int i = 0; i < 6; i++) {
				MyRubiksCube clonClock = (MyRubiksCube) cubo.clone();
				MyRubiksCube clonCClock = (MyRubiksCube) cubo.clone();
				clonClock.twistSide(i, true);
				array.addAll(this.generatePosibilities(clonClock,
						depth + 1, maxdepth));
				clonCClock.twistSide(i, false);
				array.addAll(this.generatePosibilities(clonCClock,
						depth + 1, maxdepth));
			}
		}
		return array;
	}
	/**
	 * Returns the number of cubes generated for the specified difficulty.
	 * @param dif
	 * @return number of cubes.
	 */
	public int getNumCubos(int dif) {
		return this.cache[dif - 1].length;
	}
	/**
	 * Returns a clone of a rubik's cube for the specified index and difficulty
	 * @param index
	 * @param diff
	 * @return a rubik's cube
	 */
	public MyRubiksCube getRubik(int index, int diff) {
		return (MyRubiksCube) this.cache[diff][index].clone();
	}
}
