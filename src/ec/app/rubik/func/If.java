package ec.app.rubik.func;

import ec.EvolutionState;
import ec.Problem;
import ec.app.rubik.MyGPNode;
import ec.app.rubik.MyRubiksCube;
import ec.app.rubik.RubiksData;
import ec.gp.ADFStack;
import ec.gp.GPData;
import ec.gp.GPIndividual;
import ec.util.Parameter;

public class If extends MyGPNode {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public String toString() {
		return "If";
	}

	public void checkConstraints(final EvolutionState state, final int tree,
			final GPIndividual typicalIndividual, final Parameter individualBase) {
		super.checkConstraints(state, tree, typicalIndividual, individualBase);
		if (children.length != 2)
			state.output.error("Incorrect number of children for node "
					+ toStringForError() + " at " + individualBase);

	}

	/*
	 * gp.nc.0 = ec.gp.GPNodeConstraints gp.nc.0.name = IfFunction
	 * gp.nc.0.returns = if gp.nc.0.size = 3 gp.nc.0.child.0 = cond
	 * gp.nc.0.child.1 = action gp.nc.0.child.2 = action
	 * 
	 * (non-Javadoc)
	 * 
	 * @see ec.gp.GPNode#eval(ec.EvolutionState, int, ec.gp.GPData,
	 * ec.gp.ADFStack, ec.gp.GPIndividual, ec.Problem)
	 */
	@Override
	public void eval(final EvolutionState state, final int thread,
			final GPData input, final ADFStack stack,
			final GPIndividual individual, final Problem problem) {
		boolean solution = false;
		Cond cond = ((Cond) children[0]);
		MyRubiksCube cub = ((RubiksData) input).getCube();

		// cub.transform(2, 7, 1) ;//lastRotation

		// cub.imprimirCubo();
		for (int i = 0; i < 6 && !solution; i++) {
			// rotate
			for (int j = 0; j < 4 && !solution; j++) {
				cub.unsetAllMatches();
				cub.transform(2, 7, 1);
				// cub.imprimirCubo();
				cond.eval(state, thread, input, stack, individual, problem);
				solution = cond.isTrue();
			}
			if (!solution && i < 4) {
				cub.transform(1, 7, 1);// right
			} else if (!solution && i == 4) {
				cub.transform(0, 7, 1);// up
			} else if (!solution && i == 5) {
				cub.transform(0, 7, 2);// down
			}
		}
		@SuppressWarnings("unused")
		int a = 0;
		if (solution) {
			children[1].eval(state, thread, input, stack, individual, problem);
		} else {
			// children[2].eval(state, thread,input,stack,individual,problem);
		}

	}

}
