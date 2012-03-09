package ec.app.rubik;
import ec.gp.GPData;



public class RubiksData extends GPData {
	/**
	 * 
	 */
	private static final long serialVersionUID = -4443443784733823013L;
	private MyRubiksCube x;    // return value
	public RubiksData(){
		x=new MyRubiksCube();
	}
	public MyRubiksCube getCube(){
		return this.x;
	}
	public void setCube(MyRubiksCube cube){
		x=cube;
	}
    
	public Object clone(){
		RubiksData rd=new RubiksData();
		rd.setCube((MyRubiksCube) x.clone());
		return rd;
	}
	@Override
	public GPData copyTo(GPData arg0) {
		((RubiksData)arg0).setCube((MyRubiksCube) x.clone());
		return arg0;
	}
}