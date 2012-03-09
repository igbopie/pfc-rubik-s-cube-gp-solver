
package ec.app.rubik;

import ec.EvolutionState;
import ec.gp.GPNode;
import ec.util.Parameter;


/**
 * A Little modification from the Sean's code.
 * @author Ignacio Bona
 */

public abstract class MyGPNode extends GPNode
    {
    /**
	 * 
	 */
	private static final long serialVersionUID = -2582405732001621794L;
	public static final String P_NODEACTASTERMINAL = "at";
    private boolean actAsTerminal=false;
    
    public void setup(final EvolutionState state, final Parameter base)
        {
        super.setup(state, base);
        
        Parameter def = defaultBase();

        String s = state.parameters.getString(base.push(P_NODEACTASTERMINAL),
                                              def.push(P_NODEACTASTERMINAL));
        if (s==null){
           // state.output.warning("Normal Function");
        }else{
        	actAsTerminal=Boolean.valueOf(s);
        	if(actAsTerminal){
        		state.output.warning(this.toString()+": node will act as terminal");
        	}
        }
        

        }
    public boolean actAsTerminal(){
    	return this.actAsTerminal;
    }
    

   }