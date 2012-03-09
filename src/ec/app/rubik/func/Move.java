package ec.app.rubik.func;

import ec.EvolutionState;
import ec.Problem;
import ec.app.rubik.GPRubiks;
import ec.app.rubik.MyGPNode;
import ec.app.rubik.MyRubiksCube;
import ec.app.rubik.RubiksData;
import ec.app.rubik.func.direction.Direction;
import ec.app.rubik.func.face.Face;
import ec.gp.ADFStack;
import ec.gp.GPData;
import ec.gp.GPIndividual;
import ec.util.Parameter;

public class Move extends MyGPNode {


	public Move() {
	}

	public String toString() {
		return "Move";
	}

	public void checkConstraints(final EvolutionState state, final int tree,
			final GPIndividual typicalIndividual, final Parameter individualBase) {
		super.checkConstraints(state, tree, typicalIndividual, individualBase);
		if (children.length != 2)
			state.output.error("Incorrect number of children for node "
					+ toStringForError() + " at " + individualBase);
	}

	@Override
	public void eval(final EvolutionState state, final int thread,
			final GPData input, final ADFStack stack,
			final GPIndividual individual, final Problem problem) {
		int face=((Face)children[0]).getValue();
		boolean clockwise=((Direction)children[1]).getClockwise();
		
		GPRubiks p = ((GPRubiks) problem);
		MyRubiksCube cub = ((RubiksData) input).getCube();
		/*String m="";
		switch(face){
			case 0:
				m="F";
				break;
			case 1:
				m="R";
				break;
			case 2:
				m="D";
				break;
			case 3:
				m="B";
				break;
			case 4:
				m="L";
				break;
			case 5:
				m="U";
				break;
		}
		
		
		if (clockwise) {
			m+="_";
		}
		Move mo=Move.valueOf(m);
		p.addMove(mo);
		p.setCurrentFace(0);
		p.setCurrentX(0);
		p.setCurrentY(0);
		p.debug += "move\n";*/

		cub.twistSide(face, clockwise);
	}

}
