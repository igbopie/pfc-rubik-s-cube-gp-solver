package ec.app.rubik;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;

import ec.EvolutionState;
import ec.Individual;
import ec.Statistics;
import ec.gp.GPProblem;
import ec.steadystate.SteadyStateStatisticsForm;
import ec.util.Output;
import ec.util.Parameter;

/**
 * This class extends KozaStatistics functionality due to make my own
 * statistics.
 * 
 * 
 * 
 * @author Ignacio Bona
 * 
 */
public class RubiksStatistics extends Statistics implements SteadyStateStatisticsForm{
	private int numInds = 0;
	private Individual[] best_of_run;
	/**
	 * Array containing the topten individuals from all the run of all
	 * populations.
	 */
	private Individual[][] topten;
	/**
	 * TEN value, 10. Someday, this value could be read from the params file.
	 */
	final static int TEN = 10;
	 /** The Statistics' log */
    public int statisticslog;
    /** log file parameter */
    public static final String P_STATISTICS_FILE = "file";
    public RubiksStatistics(){
    	statisticslog = 0; 
    }
    public void setup(final EvolutionState state, final Parameter base)
    {
    	super.setup(state,base);
    	File statisticsFile = state.parameters.getFile(
                base.push(P_STATISTICS_FILE),null);

            if (statisticsFile!=null) try
                {
                statisticslog = state.output.addLog(statisticsFile,Output.V_NO_GENERAL-1,false,true,false);
                }
            catch (IOException i)
                {
                state.output.fatal("An IOException occurred while trying to create the log " + statisticsFile + ":\n" + i);
                }
                
    }
	public void postInitializationStatistics(final EvolutionState state) {
		super.postInitializationStatistics(state);
		best_of_run = new Individual[state.population.subpops.length];

		topten = new Individual[state.population.subpops.length][TEN];
	}

	public void postEvaluationStatistics(final EvolutionState state) {
		super.postEvaluationStatistics(state);
		Individual[] best_i = new Individual[state.population.subpops.length];// best
																				// of
																				// the
																				// generation
		Individual[][] gentopten = new Individual[state.population.subpops.length][TEN];// top
																						// ten
																						// of
																						// the
																						// generation

		 state.output.println("\n\n\nGeneration " + state.generation +" Time: "+Calendar.getInstance().getTime()+"\n=============================================================================",Output.V_NO_GENERAL,statisticslog);

		
		for (int x = 0; x < state.population.subpops.length; x++) {
			state.output.println("\nSubpopulation " + x + "\n----------------",
					Output.V_NO_GENERAL, statisticslog);

			float meanCubeSolvedScore = 0;
			float meanEntropyScore = 0;
			float meanIndSize = 0;
			float meanMaxDif = 0;
			float meanNSteps=0;
			float[] meanCubes = new float[100];
			float[] meanSolved = new float[100];
			if (!(state.population.subpops[x].species.f_prototype instanceof RubiksFitness))
				state.output
						.fatal("Subpopulation "
								+ x
								+ " is not of the fitness Rubiksfitness.  Cannot do timing statistics with RubikStatistics.");

			best_i[x] = state.population.subpops[x].individuals[0];
			for (int i = 0; i < TEN; i++) {
				gentopten[x][i] = state.population.subpops[x].individuals[i];
			}
			// For each individual
			for (int y = 0; y < state.population.subpops[x].individuals.length; y++) {
				// is in the generation's Topten?
				boolean asigned = false;
				for (int i = 0; i < TEN && !asigned; i++) {
					if (state.population.subpops[x].individuals[y].fitness
							.betterThan(gentopten[x][i].fitness)) {
						gentopten[x][i] = state.population.subpops[x].individuals[y];
						asigned = true;
					}
				}
				//is the generation's best individual?
				if (state.population.subpops[x].individuals[y].fitness
						.betterThan(best_i[x].fitness)) {
					best_i[x] = state.population.subpops[x].individuals[y];
				}
				
				
				//Statistics
				meanCubeSolvedScore += ((RubiksFitness) (state.population.subpops[x].individuals[y].fitness))
						.getSolvedCubesScore();
				meanEntropyScore += ((RubiksFitness) (state.population.subpops[x].individuals[y].fitness))
						.getEntropyScore();
				meanIndSize += ((RubiksFitness) (state.population.subpops[x].individuals[y].fitness))
						.getIndividualSize();
				meanMaxDif += ((RubiksFitness) (state.population.subpops[x].individuals[y].fitness))
						.getHighestDif();
				meanNSteps += ((RubiksFitness) (state.population.subpops[x].individuals[y].fitness))
				.getNSteps();
				int i = 1;
				int total = 0;
				while ((total = ((RubiksFitness) (state.population.subpops[x].individuals[y].fitness))
						.getTotalCubes(i)) != 0) {
					meanSolved[i - 1] += ((RubiksFitness) (state.population.subpops[x].individuals[y].fitness))
							.getSolvedCubes(i);
					meanCubes[i - 1] += total;
					i++;
				}
				
				
				

			}

			// compute fitness stats
			meanCubeSolvedScore /= state.population.subpops[x].individuals.length;
			meanEntropyScore /= state.population.subpops[x].individuals.length;
			meanIndSize /= state.population.subpops[x].individuals.length;
			meanMaxDif /= state.population.subpops[x].individuals.length;
			meanNSteps /= state.population.subpops[x].individuals.length;
			int i = 1;
			float total = 0;
			String text = "";
			while ((total = meanCubes[i - 1]) != 0) {
				text += " "
						+ i
						+ ":"
						+ (meanSolved[i - 1] / state.population.subpops[x].individuals.length)
						+ "/"
						+ (total / state.population.subpops[x].individuals.length);
				i++;
			}
			state.output.print("Mean Highest Diff=" + meanMaxDif + " Solved=" + text
					+ " Mean Solved Score=" + meanCubeSolvedScore + " Mean Entropy Score="
					+ meanEntropyScore +" Steps Mean="+meanNSteps+" Individual Size=" + meanIndSize,
					Output.V_NO_GENERAL, statisticslog);
			state.output.println("", Output.V_NO_GENERAL, statisticslog);

			numInds += state.population.subpops[x].individuals.length;

		}
		// now test to see if it's the new best_of_run
		for (int x = 0; x < state.population.subpops.length; x++) {

			// print the best-of-generation individual
			//LOG
			state.output.println("\nBest Individual of Generation:",
					Output.V_NO_GENERAL, statisticslog);
			best_i[x].printIndividualForHumans(state, statisticslog,
					Output.V_NO_GENERAL);
			
			//SCREEN
			state.output.message("Subpop " + x
					+ " best fitness of generation: "
					+ best_i[x].fitness.fitnessToStringForHumans());
			
			//TOPTEN
			
			//LOG
			state.output.println("\nTOPTEN fitness of generation: ",
					Output.V_NO_GENERAL, statisticslog);
			//SCREEN
			state.output.message("Subpop " + x
					+ " TOPTEN fitness of generation ");
			for (int i = 0; i < TEN; i++) {
				//LOG
				state.output.print((i+1)+":",
						Output.V_NO_GENERAL, statisticslog);
				gentopten[x][i].fitness.printFitnessForHumans(state,
						statisticslog, Output.V_NO_GENERAL);
				
				//SCREEN
				state.output.message((i+1)+":"
						+ gentopten[x][i].fitness.fitnessToStringForHumans());
			}

			//run's TOPTEN calculations 
			for (int i = 0; i < TEN; i++) {
				for (int j = 0; j < TEN; j++) {
					if (gentopten[x][j] != null
							&& (topten[x][i] == null || gentopten[x][j].fitness
									.betterThan(topten[x][i].fitness))) {
						topten[x][i] = (Individual) (gentopten[x][j].clone());
						gentopten[x][j] = null;// I don't want to repeat individuals
					}
				}
			}
			if (best_of_run[x] == null
					|| best_i[x].fitness.betterThan(best_of_run[x].fitness))
				best_of_run[x] = (Individual) (best_i[x].clone());
		}
	}

	public void finalStatistics(final EvolutionState state, final int result) {
		super.finalStatistics(state, result);
    
        state.output.println("\n\n\nFinal Statistics\n================",Output.V_NO_GENERAL,statisticslog);

        state.output.println("Total Individuals Evaluated: " + numInds,Output.V_NO_GENERAL,statisticslog);
        // for now we just print the best fitness 
        
        state.output.println("\nBest Individual of Run:",Output.V_NO_GENERAL,statisticslog);
        for(int x=0;x<state.population.subpops.length;x++)
            {
            best_of_run[x].printIndividualForHumans(state,statisticslog,Output.V_NO_GENERAL);
            state.output.message("Subpop " + x + " best fitness of run: " + best_of_run[x].fitness.fitnessToStringForHumans());

            // finally describe the winner if there is a description
            ((GPProblem)(state.evaluator.p_problem.clone())).describe(best_of_run[x], state, x, 0, statisticslog,Output.V_NO_GENERAL);  
            }

		state.output.println("\nTopten Individual of Run:",
				Output.V_NO_GENERAL, statisticslog);
		for (int x = 0; x < state.population.subpops.length; x++) {
			for (int i = 0; i < TEN; i++) {
				topten[x][i].printIndividualForHumans(state, statisticslog,
						Output.V_NO_GENERAL);

				state.output.message("Subpop " + x + " TOPTEN fitness of run: "
						+ topten[x][i].fitness.fitnessToStringForHumans());
			}

		}

	}

}
