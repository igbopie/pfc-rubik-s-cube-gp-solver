package ec.app.rubik.func;

import ec.EvolutionState;
import ec.Problem;
import ec.gp.ADFStack;
import ec.gp.GPData;
import ec.gp.GPIndividual;
import ec.util.Parameter;

public class No extends Cond {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public String toString() { return "No"; }

	public void checkConstraints(final EvolutionState state, final int tree,
			final GPIndividual typicalIndividual, final Parameter individualBase) {
		super.checkConstraints(state, tree, typicalIndividual, individualBase);
		if (children.length != 1)
			state.output.error("Incorrect number of children for node "
					+ toStringForError() + " at " + individualBase);
		
		
	}
	/*
	 * 	gp.nc.3 = ec.gp.GPNodeConstraints
		gp.nc.3.name = OrFunction
		gp.nc.3.returns = or
		gp.nc.3.size = 2
		gp.nc.3.child.0 = cond
		gp.nc.3.child.1 = cond
	 * 
	 * (non-Javadoc)
	 * @see ec.gp.GPNode#eval(ec.EvolutionState, int, ec.gp.GPData, ec.gp.ADFStack, ec.gp.GPIndividual, ec.Problem)
	 */
	@Override
    public void eval(final EvolutionState state,
                     final int thread,
                     final GPData input,
                     final ADFStack stack,
                     final GPIndividual individual,
                     final Problem problem)
        {
		
		children[0].eval(state, thread,input,stack,individual,problem);//evaluates condition1
		
		Cond cond1=((Cond)children[0]);
		
		this.setIsTrue(!cond1.isTrue());
		
		 
			
       }

}
