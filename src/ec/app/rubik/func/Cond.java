package ec.app.rubik.func;

import ec.app.rubik.MyGPNode;

public abstract class Cond extends MyGPNode {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7048138352479483455L;
	private boolean value;
	/**
	 * 
	 */
	public boolean isTrue() {
		// TODO Auto-generated method stub
		return value;
	}
	protected void setIsTrue(boolean value){
		this.value=value;
	}

		
}
