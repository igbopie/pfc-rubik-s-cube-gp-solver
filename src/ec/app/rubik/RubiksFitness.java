package ec.app.rubik;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import ec.EvolutionState;
import ec.Fitness;
import ec.gp.koza.GPKozaDefaults;
import ec.simple.SimpleFitness;
import ec.util.Parameter;
/**
 * Specific fitness created for solve GP problems for solving rubik's cubes.
 * 
 * @author Ignacio Bona
 *
 */
public class RubiksFitness extends SimpleFitness {
	/**
	 * Array of the number of solved cubes for each difficulty
	 */
	private int[]solvedCubes;
	/**
	 * Array of total cubes for each difficulty
	 */
	private int[]totalCubes;
	/**
	 * Score based on solved cubes for each difficulty
	 */
	private float solvedCubesScore;
	/**
	 * Score based on the mean of all cube's entropy. 
	 */
	private float entropyScore;
	/**
	 * Size of the code of an Individual.
	 */
	private float individualSize;
	/**
	 * Highest diff cube solved.
	 */
	private int hidif=0;
	/**
	 * Set by <i>factorfitness</i> param. It is used in the solvedCubes score: score+= solvedCubes * factor^diff
	 */
	private float factor=1;
	private float nSteps;
	private float nSolved=0;
	private int random;
	public static final int MAXRANDOM=10;
	public int setRandom() {
		return random;
	}
	public void setRandom(int random) {
		this.random = random;
	}
	public final static String P_FACTORFITNESS = "factorfitness";

    public static final String P_RUBIKFITNESS = "fitness";
	public Parameter defaultBase()
    {
    return GPKozaDefaults.base().push(P_RUBIKFITNESS);
    }
	public boolean isIdealFitness()
    {
    return false;//We never know if we have found the optimal way 
    }
	
	public void setup(final EvolutionState state, final Parameter base) { 
		
		Parameter def = defaultBase();

		String s = state.parameters.getString(base.push(P_FACTORFITNESS), def
				.push(P_FACTORFITNESS));
		factor=Float.parseFloat(s);
	}
	/**
	 * This method reset all scores. It must be called before the evaluation.
	 */
	public void reset(){
		solvedCubesScore=0;
		solvedCubes= new int[100];
		totalCubes=new int[100];
		solvedCubesScore=0;
		entropyScore=0;
		individualSize=0;
		nSteps=0;
		hidif=0;
		nSolved=0;
	}
	/**
	 * This method sets the total cubes for a specific difficulty.
	 * @param totalCubos 
	 * @param diff
	 */
	public void setTotalCubes(int totalCubos,int diff) {
		this.totalCubes[diff-1] = totalCubos;
	}
	/**
	 * Returns the number of cubes evaluated for a specific difficulty
	 * @param diff - specified difficulty
	 * @return total cubes for the specified difficulty [1,inf)
	 */
	public int getTotalCubes(int diff) {
		return this.totalCubes[diff-1];
	}
	/**
	 * This method sets the solved cubes for the specified difficulty.
	 * This method should only be called in the evaluation process.
	 * @param cubosResueltos
	 * @param diff
	 */
	public void setSolvedCubes(int cubosResueltos,int diff) {
		this.solvedCubes[diff-1] = cubosResueltos;
		nSolved+=cubosResueltos;
		this.solvedCubesScore+=((float)cubosResueltos)*(Math.pow(factor,diff));
		if(cubosResueltos>0&&diff>this.hidif){
			this.hidif=diff;
		}
	}
	/**
	 * Returns the number of cubes solved for a specific difficulty.
	 * 
	 * @param diff - difficulty
	 * @return the number of cubes solved
	 */
	public int getSolvedCubes(int diff) {
		return this.solvedCubes[diff-1];
	}
	/**
	 * Has solved every cube tested in the evaluation?
	 * @return true if he(it?) could solved it.
	 */
	public boolean getAllSolved() {
		return entropyScore==MyRubiksCube.HIGHESTSCORE;
	}
	/**
	 * Highest cube's difficulty solved
	 * @return highest difficulty
	 */
	public int getHighestDif() {
		return hidif;
	}
	/**
	 * Gets the score made by solved cubes (score+= solvedCubes * factor^diff).
	 * @return the score
	 */
	public float getSolvedCubesScore() {
		return this.solvedCubesScore;
	}
	/**
	 * Returns the score calculated by the entropy's mean of each cube tested in the evaluation.
	 * @return the score
	 */
	public float getEntropyScore() {
		return entropyScore;
	}
	/**
	 * Sets entropy's mean score.
	 * @param mediaPuntuacion
	 */
	public void setEntropyScore(float mediaPuntuacion) {
		this.entropyScore = mediaPuntuacion;
	}
	/**
	 * Returns the size of the code of the individual tested.
	 * @return the size
	 */
	public float getIndividualSize() {
		return individualSize;
	}
	/**
	 * Sets the size of the code of the individual tested.
	 * @param tamanoCodigo
	 */
	public void setIndividualSize(float tamanoCodigo) {
		this.individualSize = tamanoCodigo;
	}

	public boolean betterThan(final Fitness _fitness)
     {
		 RubiksFitness rf=(RubiksFitness)_fitness;
		 float dif=this.getSolvedCubesScore()-rf.getSolvedCubesScore();
		 dif=Math.abs(dif);
		 
		  /*if (nSolved !=rf.nSolved) return  nSolved>rf.nSolved;
		  if (nSolved <rf.nSolved) bbeatsa = true;  */
		  if(this.solvedCubesScore!=rf.solvedCubesScore)	return solvedCubesScore>rf.solvedCubesScore;
		  if(this.entropyScore!=rf.entropyScore) return entropyScore>rf.entropyScore;
		  if(this.getAllSolved()){
			  //we have solved everything, so who has the shorter code?
			  if(this.getNSteps()!=rf.getNSteps()){
				  //Who can get shortest way.
				  return this.getNSteps()<rf.getNSteps();
			  }
			  return this.individualSize<rf.individualSize; 
		  }
		  return random>rf.random;//we are the same so...
		 
     }
	public String fitnessToStringForHumans()
    {
		String texto=FITNESS_PREAMBLE + "Highest Diff="+hidif+" Solved=";
		int i=1;
		int total=0;
		while((total=this.getTotalCubes(i))!=0){
			texto+=" "+i+":"+this.getSolvedCubes(i)+"/"+total;
			i++;
		}
		texto+=" Solved Score="+this.solvedCubesScore+" Entropy Score=" + this.getEntropyScore() + "/240 Total Solved="+nSolved+" Steps Mean="+this.nSteps+" Individual Size=" + this.getIndividualSize();
		return  texto;
    }
	public void writeFitness(final EvolutionState state,final DataOutput dataOutput) 
    throws IOException
    {
		super.writeFitness(state, dataOutput);
		dataOutput.write(solvedCubes.length);
		for(int i=0;i<solvedCubes.length;i++){
			dataOutput.writeInt(solvedCubes[i]);
		}

		dataOutput.write(totalCubes.length);
		for(int i=0;i<totalCubes.length;i++){
			dataOutput.writeInt(totalCubes[i]);
		}

		dataOutput.writeFloat(solvedCubesScore);
        dataOutput.writeFloat(entropyScore);
        dataOutput.writeFloat(individualSize);
   
    }

    public void readFitness(final EvolutionState state,final DataInput dataInput) 
    throws IOException
    {
    	super.readFitness(state,dataInput);
    	int aux=dataInput.readInt();
    	solvedCubes=new int[aux];
    	for(int i=0;i<aux;i++){
    		solvedCubes[i]=dataInput.readInt();
    	} 
    	aux=dataInput.readInt();
    	totalCubes=new int[aux];
    	for(int i=0;i<aux;i++){
    		totalCubes[i]=dataInput.readInt();
    	}
    	solvedCubesScore=dataInput.readFloat();
    	entropyScore=dataInput.readFloat();
    	individualSize=dataInput.readFloat();
    }
	public void setNSteps(float mediasteps) {
		this.nSteps=mediasteps;
		
	}
	public float getNSteps() {
		return this.nSteps;
		
	}
}
