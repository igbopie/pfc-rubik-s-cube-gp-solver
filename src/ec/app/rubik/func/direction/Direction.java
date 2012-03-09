package ec.app.rubik.func.direction;

import ec.EvolutionState;
import ec.Problem;
import ec.app.rubik.MyGPNode;
import ec.gp.ADFStack;
import ec.gp.GPData;
import ec.gp.GPIndividual;
import ec.util.Parameter;

public abstract class Direction extends MyGPNode
    {
    /**
	 * 
	 */
	private static final long serialVersionUID = 7604959263283771074L;
	boolean clockwise;
	public Direction(boolean clockwise){
		this.clockwise=clockwise;
	}
	public String toString() { 
		if(clockwise)
			return "Clockwise";
		else
			return "CounterClockwise";
	}

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
	 public boolean getClockwise(){
		 return this.clockwise;
	 }

	
    }