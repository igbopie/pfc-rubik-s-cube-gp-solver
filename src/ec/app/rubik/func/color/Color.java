package ec.app.rubik.func.color;

import ec.EvolutionState;
import ec.Problem;
import ec.app.rubik.MyGPNode;
import ec.gp.ADFStack;
import ec.gp.GPData;
import ec.gp.GPIndividual;
import ec.util.Parameter;

public abstract class Color extends MyGPNode
    {
    /**
	 * 
	 */
	private static final long serialVersionUID = 7604959263283771074L;
	int value;

	public static final int C0=0;
	public static final int C1=1;
	public static final int C2=2;
	public static final int C3=3;
	public static final int C4=4;
	public static final int C5=5;
	public Color(int value){
		this.value=value;
	}
	public String toString() { return "Color"+value; }

    public void checkConstraints(final EvolutionState state,
                                 final int tree,
                                 final GPIndividual typicalIndividual,
                                 final Parameter individualBase)
        {
        super.checkConstraints(state,tree,typicalIndividual,individualBase);
        if (children.length!=0)
            state.output.error("Incorrect number of children for node " + 
                               toStringForError() + " at " +
                               individualBase);
        }

	@Override
    public void eval(final EvolutionState state,
                     final int thread,
                     final GPData input,
                     final ADFStack stack,
                     final GPIndividual individual,
                     final Problem problem)
        {
			//Nothing to do.
      }
	 public int getValue(){
		 return value;
	 }

	
    }