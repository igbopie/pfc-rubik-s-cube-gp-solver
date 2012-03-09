package ec.app.rubik;

import ec.EvolutionState;
import ec.Individual;
import ec.gp.GPIndividual;
import ec.gp.GPNode;
import ec.gp.GPProblem;
import ec.gp.GPTree;
import ec.simple.SimpleProblemForm;
import ec.util.Parameter;

/**
 * GPRubiks is Genetic Programming Problem (GPProblem) made to create a Rubik's
 * cube solver.
 * <p>
 * <b>Parameters</b><br>
 * 
 * @author Ignacio Bona
 * 
 * 
 */
public class GPRubiks extends GPProblem implements SimpleProblemForm {
	/**
	 * 
	 */
	private static final long serialVersionUID = -1L;
	/**
	 * Max moves from a solved cube the solver will try to solve.
	 */
	private int rubiksmoves = 0;
	/**
	 * Number of cubes the solver will have to solve during the evaluation per
	 * difficulty (rubiksmoves).
	 */
	private int rubikstestedperevaluation = 0;
	/**
	 * Max iterations the solver can have to solve a cube.
	 */
	private int iterations = 0;

	public static final String P_RUBIKSMOVES = "rubiksmoves";
	public static final String P_RUBIKSTESTEDPEREVALUATION = "rubikstestedperevaluation";
	public static final String P_ITERATIONS = "iterationsinevaluation";
	/**
	 * Max score a cube can have.
	 */
	private final int maxP = new MyRubiksCube().scoreCube();
	/**
	 * A warehouse for pre-generated cubes. So I do not have to create them in
	 * the evaluation process.
	 */
	private Cache cache;

	/**
	 * Use this method to clone this object.
	 * 
	 * @see ec.gp.GPProblem#clone()
	 */
	public Object clone() {
		GPRubiks newobj = new GPRubiks();
		newobj.cache = cache;// doesn't need to be clone.
		newobj.rubiksmoves = rubiksmoves;
		newobj.rubikstestedperevaluation = rubikstestedperevaluation;
		newobj.iterations = iterations;
		return newobj;
	}

	/**
	 * (non-Javadoc)
	 * 
	 * @see ec.gp.GPProblem#setup(ec.EvolutionState, ec.util.Parameter)
	 */
	public void setup(final EvolutionState state, final Parameter base) {
		super.setup(state, base);

		Parameter def = defaultBase();
		// Read rubiksmoves param
		String s = state.parameters.getString(base.push(P_RUBIKSMOVES), def
				.push(P_RUBIKSMOVES));
		rubiksmoves = Integer.parseInt(s);
		state.output.message("Rubiks Moves:" + rubiksmoves);

		// Read rubikstestedperevaluation param
		s = state.parameters.getString(base.push(P_RUBIKSTESTEDPEREVALUATION),
				def.push(P_RUBIKSTESTEDPEREVALUATION));
		rubikstestedperevaluation = Integer.parseInt(s);
		state.output.message("Rubiks Tested on Evaluation:"
				+ rubikstestedperevaluation);

		// Read iterations param
		s = state.parameters.getString(base.push(P_ITERATIONS), def
				.push(P_ITERATIONS));
		this.iterations = Integer.parseInt(s);
		state.output.message("Iterations per evaluation:" + this.iterations);

		// Initialize cache...
		state.output.message("Init cache...");
		cache = new Cache(rubikstestedperevaluation, rubiksmoves, state);
		state.output.message("Ok.");

	}

	/**
	 * In order to wake up from a checkpoint properly, cache needs to be rebuilt
	 * and all individuals needs to be evaluated
	 * 
	 * @see ec.Problem#reinitializeContacts(ec.EvolutionState)
	 */
	public void reinitializeContacts(EvolutionState state) {
		super.reinitializeContacts(state);
		// rebuild cache
		cache = new Cache(rubikstestedperevaluation, rubiksmoves, state);

		// every individual needs to be evaluated.
		for (int x = 0; x < state.population.subpops.length; x++) {
			for (int i = 0; i < state.population.subpops[x].individuals.length; i++) {
				state.population.subpops[x].individuals[i].evaluated = false;
			}
		}
	}

	/**
	 * Auxiliary and internal function to get an individual score used in the
	 * evaluation process.
	 */
	private int getTreeScore(GPTree[] trees) {
		int max = 0;
		for (int i = 0; i < trees.length; i++) {
			max += getNodeScore(trees[i].child);
		}
		return max;
	}

	/**
	 * Auxiliary and internal function to get a node score.
	 */
	private int getNodeScore(GPNode node) {
		int max = 1;
		for (int i = 0; i < node.children.length; i++) {
			max += this.getNodeScore(node.children[i]);
		}
		return max;
	}

	/**
	 * Evaluate an individual.
	 * 
	 * 
	 * 
	 * @see ec.simple.SimpleProblemForm#evaluate(ec.EvolutionState,
	 *      ec.Individual, int, int)
	 */
	public void evaluate(final EvolutionState state, final Individual ind,
			final int subpopulation, final int threadnum) {
		// Doesn't need to reevaluate
		if (!ind.evaluated) {

			try {
				RubiksData data = new RubiksData();
				int j = 0;
				int score = 0;
				RubiksFitness f = ((RubiksFitness) ind.fitness);
				f.reset();
				int cubesSolved = 0;
				int totalSolved=0;
				float mediahits = 0;
				float mediasteps=0;
				int cubes = 0;
				//for each difficulty.
				for (int dif = 0; dif < this.rubiksmoves; dif++) {
					int maxcubos = cache.getNumCubos(dif + 1);
					cubes += maxcubos;
					cubesSolved = 0;
					//for each cube in each difficulty
					for (int i = 0; i < maxcubos; i++) {
						score = 0;
						j = 0;
						data.setCube(cache.getRubik(i, dif));
						data.getCube().resetNSteps();
						int nsteps=-1;
						//while 
						//cube is not solved
						//and have not reached maxiterations
						//and have move something since the last iteration
						while (score != maxP && j < this.iterations && nsteps!=data.getCube().getNSteps()) {
							nsteps=data.getCube().getNSteps();
							((GPIndividual) ind).trees[0].child.eval(state,
									threadnum, data, stack,
									((GPIndividual) ind), this);
							
							score = data.getCube().scoreCube();
							j++;
							
						}
						if (score == maxP) {
							cubesSolved++;
							totalSolved++;
							mediasteps+=(float)data.getCube().getNSteps();;//just if solved
						}
						mediahits += (float) score;
					}
					f.setSolvedCubes(cubesSolved, dif + 1);
					f.setTotalCubes(maxcubos, dif + 1);
				}
				mediasteps=(float)mediasteps/(float)totalSolved;
				mediahits = mediahits / cubes;
				ind.evaluated = true;
				f.setNSteps(mediasteps);
				
				f.setIndividualSize(this
								.getTreeScore(((GPIndividual) ind).trees));
				f.setEntropyScore(mediahits);
				f.setRandom(state.random[threadnum].nextInt(RubiksFitness.MAXRANDOM));
				//f.setStandardizedFitness(state, 1 / mediahits);//not used, really.

			} catch (Exception e) {
				state.output.message("Evaluation exception:");
				e.printStackTrace();
			}
		}
	}

}
