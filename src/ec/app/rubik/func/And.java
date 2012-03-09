package ec.app.rubik.func;

import ec.EvolutionState;
import ec.Problem;
import ec.gp.ADFStack;
import ec.gp.GPData;
import ec.gp.GPIndividual;
import ec.util.Parameter;

public class And extends Cond {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public String toString() { return "And"; }

	public void checkConstraints(final EvolutionState state, final int tree,
			final GPIndividual typicalIndividual, final Parameter individualBase) {
		super.checkConstraints(state, tree, typicalIndividual, individualBase);
		if (children.length != 2)
			state.output.error("Incorrect number of children for node "
					+ toStringForError() + " at " + individualBase);
		
		
	}
	/*
	 * 	gp.nc.3 = ec.gp.GPNodeConstraints
		gp.nc.3.name = AndFunction
		gp.nc.3.returns = and
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

		//ystem.out.print("And ");
		children[0].eval(state, thread,input,stack,individual,problem);//evaluates condition1
		children[1].eval(state, thread,input,stack,individual,problem);//evaluates condition2

		Cond cond1=((Cond)children[0]);
		Cond cond2=((Cond)children[1]);
		

		super.setIsTrue(cond1.isTrue() && cond2.isTrue());
		 
			
    }
	


}
