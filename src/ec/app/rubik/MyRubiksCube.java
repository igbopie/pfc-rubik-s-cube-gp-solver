package ec.app.rubik;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Random;
import java.util.Vector;

import com.parabon.rubik.FrontierCube;
import com.parabon.rubik.Move;


public class MyRubiksCube extends FrontierCube implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -1042465112341339028L;
	private Random aleatorio;
	private int[]match;
	private boolean refreshStickers;
	private int[][]stickers;
	private Vector<Move>steps;
	private boolean remember;
	private int[]facematch;
	private int nSteps=0;
	public int getNSteps() {
		return nSteps;
	}
	public void resetNSteps() {
		nSteps = 0;
	}
	public final static int HIGHESTSCORE=240; 
	public MyRubiksCube(FrontierCube cube){
		this.setStickers(cube.getStickers());
		init();
		
	}
	public MyRubiksCube(){
		super();
		init();
	}
	private void init(){
		aleatorio=new Random(Calendar.getInstance().getTimeInMillis());
		match=new int[6];
		unsetAllMatches();
		this.refreshStickers=true;
		steps=new Vector<Move>();
		remember=false;
		this.resetNSteps();
	}
	public void resetSteps(){
		steps=new Vector<Move>();
	}
	
	public Vector<Move>getSteps(){
		return this.steps;
	}
	public void setRemember(boolean remember){
		this.remember=remember;
		if(remember){
			this.facematch=new int[6];
			//init facematch
			for(int i=0;i<facematch.length;i++){
				facematch[i]=i;
			}
		}
	}
	public int[][]getStickers(){
		if(this.refreshStickers){
			this.stickers=super.getStickers();	
			this.refreshStickers=false;
			this.refreshStickers();
		}
		return this.stickers;
	}
	synchronized private void refreshStickers(){
		//Sync problems??!
		this.stickers=super.getStickers();
	}
	public void transform(int a, int b,int c){
		this.refreshStickers=true;
		super.transform(a,b,c);
		if(remember){
			int[][]stick=this.getStickers();
			for(int i=0;i<facematch.length;i++){
				facematch[i]=stick[i][4];//4 is the central sticker, which can't be move by side moves only.
			}
		}
	}
	public void twistSide(int i,boolean clock){
		this.refreshStickers=true;
		nSteps++;
		super.twistSide(i, clock);
		if(remember){
			//TRANSLATE
			int face=facematch[i];
			boolean clockwise=clock;
			/*if((i==3||face==3)&&face!=i){//backface works the oposite way!
				clockwise=!clock;
			}*/
			
			
			String move="";
			 switch (face) {
			 	case 0: move+="F";break;
			 	case 1:	move+="R";break;
			 	case 2:	move+="D";break;
			 	case 3:	move+="B";break;
			 	case 4:	move+="L";break;
			 	case 5:	move+="U";break;
			 	default:move+="?";
			 }
			if(clockwise){
				move+="_";
			}
			Move m=Move.valueOf(move);
			steps.add(m);
		}
	}
	public int getMatch(int color){
		return match[color];
	}
	public void setMatch(int color,int newcolor){
		match[color]=newcolor;
	}
	public void unsetMatch(int color){
		match[color]=-1;
	}
	public void unsetAllMatches(){
		int[] unmatched={-1,-1,-1,-1,-1,-1};
		match=unmatched;
	}
	public boolean isMatchedColor(int color){
		return match[color]>=0;
	}
	public boolean isMatchedColorInPosition(int color){
		for(int i=0;i<match.length;i++){
			if(match[i]==color){
				return true;
			}
		}
		return false;
	}
	
	public Object clone(){
		return super.clone();
	}
	
	public void imprimirCubo() {
		System.out.println(this.toString());
	}
	public void desordenarCubo(int numpasos) {
		int cara;
		int clock = -1;
		int pasos = 0;
		int reintentos=0;

		int punt=this.scoreCube();
		while (pasos < numpasos) {
			cara = aleatorio.nextInt(6);// [0,6)
			clock = aleatorio.nextInt(2);// [0,2)
			String m="";
			switch(cara){
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
			
			
			if (clock == 0) {
				m+="_";
				twistSide(cara, true);
			} else {
				twistSide(cara, false);
			}
			if(punt>this.scoreCube()||reintentos>10){
				pasos++;
				Move mo=Move.valueOf(m);
				punt=this.scoreCube();
				reintentos=0;
			}else{
				//undo
				if (clock == 0) {
					m+="_";
					twistSide(cara, false);
				} else {
					twistSide(cara, true);
				}
				reintentos++;
				
			}
			
		}

	}
	public int scoreCube() {
		int puntuacionTotal = 0;
		int[][]stikers=this.getStickers();
		int center;
		int up;
		int upright;
		int right;
		int downright;
		int down;
		int downleft;
		int left;
		int upleft;
		
		for (int caras = 0; caras < 6; caras++) {
			int puntuacionCara = 0;
			for (int i = 0; i < 3; i++) {
				for (int j = 0; j < 3; j++) {
					center=(3*i)+j;
					up=i>0?(3*(i-1))+j:-1;
					upright=i>0&&j<2?(3*(i-1))+(j+1):-1;
					upleft=i>0&&j>0?(3*(i-1))+(j-1):-1;
					right=j<2?(3*(i))+(j+1):-1;
					downright=i<2&&j<2?(3*(i+1))+(j+1):-1;
					down=i<2?(3*(i+1))+(j):-1;
					downleft=i<2&&j>0?(3*(i+1))+(j-1):-1;
					left=j>0?(3*(i))+(j-1):-1;
					if (up>=0&&stikers[caras][center] == stikers[caras][up]) {
							puntuacionCara++;
					}
					if (upright>=0&&stikers[caras][center] == stikers[caras][upright]) {
							puntuacionCara++;
						}
					if (right>=0&&stikers[caras][center] == stikers[caras][right]) {
							puntuacionCara++;
						}
					if (downright>=0&&stikers[caras][center] == stikers[caras][downright]) {
							puntuacionCara++;
						}
					if (down>=0&&stikers[caras][center] == stikers[caras][down]) {
							puntuacionCara++;
						}
					if (downleft>=0&&stikers[caras][center] == stikers[caras][downleft]) {
							puntuacionCara++;
						}
					if (left>=0&&stikers[caras][center] == stikers[caras][left]) {
							puntuacionCara++;
						}
					if (upleft>=0&&stikers[caras][center] == stikers[caras][upleft]) {
							puntuacionCara++;
						}
				}
			}
			puntuacionTotal += puntuacionCara;
		}

		return puntuacionTotal;
	}

	
}
