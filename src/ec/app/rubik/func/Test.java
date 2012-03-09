package ec.app.rubik.func;

import ec.EvolutionState;
import ec.Problem;
import ec.app.rubik.MyRubiksCube;
import ec.app.rubik.RubiksData;
import ec.app.rubik.func.color.Color;
import ec.app.rubik.func.face.Face;
import ec.app.rubik.func.x.X;
import ec.app.rubik.func.y.Y;
import ec.gp.ADFStack;
import ec.gp.GPData;
import ec.gp.GPIndividual;
import ec.util.Parameter;

public class Test extends Cond {
	/**
	 * 
	 */
	X x;
	Y y;
	Face face;
	Color color;
	
	private static final long serialVersionUID = 1L;

	public String toString() { return "Test"; }

	public void checkConstraints(final EvolutionState state, final int tree,
			final GPIndividual typicalIndividual, final Parameter individualBase) {
		super.checkConstraints(state, tree, typicalIndividual, individualBase);
		if (children.length != 4)
			state.output.error("Incorrect number of children for node "
					+ toStringForError() + " at " + individualBase);
		
		
	}
	/*
	 * 	
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

		//System.out.print("test ");
		x=((X)children[0]);
		y=((Y)children[1]);
		face=((Face)children[2]);
		color=((Color)children[3]);
		

		MyRubiksCube cub=((RubiksData)input).getCube();
		
		int caras[][]=cub.getStickers();
		int index=(3*y.getValue())+x.getValue();//3 = length x
		int colorInPosition=caras[face.getValue()][index];
		// evito las siguientes situacioenes
		// 5 => 2
		// 3 => 2	Si el dos ya esta cogido, no lo puedo coger.
		//-------
		// 5 => 3 //imposible por diseno
		// 5 => 2 
		if(!cub.isMatchedColor(color.getValue())&&!cub.isMatchedColorInPosition(colorInPosition)){//&&!cub.isMatched(color.getValue())){
			cub.setMatch(color.getValue(),colorInPosition);
			this.setIsTrue(true);
		}else if(cub.getMatch(color.getValue())==colorInPosition){
			this.setIsTrue(true);
		}else{
			this.setIsTrue(false);
		} 
			
    }


}
