package ec.app.rubik;

import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;
import java.util.Vector;

import com.parabon.rubik.FrontierCube;
import com.parabon.rubik.Move;
import com.parabon.rubik.Solver;

import ec.app.ant.func.Progn2;
import ec.app.rubik.func.direction.Clockwise;
import ec.app.rubik.func.direction.CounterClockwise;
import ec.gp.GPIndividual;
import ec.gp.GPNode;
import ec.gp.GPTree;

/**
 * Class to test an individual. You need to copy the code from the evolution
 * log.
 * 
 * @author nacho
 * 
 */
public class IndividualTester extends Thread implements Solver {
	/**
	 * The new individual
	 */
	private GPIndividual individuo;
	/**
	 * Maximum iterations to solve a cube.
	 */
	public static int iterations = 200;
	public static int maxdiff = 10;
	public static int maxcubes = 1000;
	/**
	 * Singleton cache
	 */
	private static Cache cache;
	/**
	 * Thread number
	 */
	private int tNumber;
	private int tsolved=0;
	private boolean finish=false;
	
	/**
	 * To create a new individual, copy the code from the log created during the
	 * evolution.
	 * 
	 * @param codigogen
	 */
	public IndividualTester(String codigogen) {
		this.setGenCode(codigogen);
	}
	public IndividualTester(IndividualTester otro) {
		this.individuo=otro.individuo;
	}
	
	public static Cache getCache(){
		if(cache==null){
			cache  = new Cache(maxcubes, maxdiff);
		}
		return cache;
	}
	private String toStringAux(GPNode nodo){
		if(nodo.children.length>0){
			String aux="";
			for(int i=0;i<nodo.children.length;i++){
				aux+=" "+toStringAux(nodo.children[i]);
			}
			return "("+nodo.toString()+aux+")";
		}else{
			return nodo.toString();
		}
	}
	public String toString(){
		return toStringAux(individuo.trees[0].child);
	}
	private void setGenCode(String codigogen) {
		individuo = new GPIndividual();
		individuo.trees = new GPTree[1];
		individuo.trees[0] = new GPTree();
		StringTokenizer st = new StringTokenizer(codigogen, " ");
		Node actual = new Node();
		Node arbol = actual;
		while (st.hasMoreTokens()) {
			Node nuevo = new Node();
			String token = st.nextToken();
			boolean subarbol = false;
			boolean salesubarbol = false;
			int veces = 0;
			if (token.charAt(0) == '(') {
				token = token.substring(1);
				subarbol = true;
			} else if (token.indexOf(")") >= 0) {
				salesubarbol = true;
				while (token.indexOf(")") >= 0) {
					token = token.substring(0, token.lastIndexOf(")"));
					veces++;
				}
			}
			nuevo.title = token;
			nuevo.father = actual;
			actual.children.add(nuevo);
			if (subarbol) {
				actual = nuevo;
			}
			if (salesubarbol) {
				while (veces > 0) {
					actual = actual.father;
					veces--;
				}

			}
		}
		this.individuo.trees[0].child = new Progn2();
		this.individuo.trees[0].child.parent = this.individuo.trees[0];
		this.individuo.trees[0].child.children = new GPNode[0];
		fullNode(this.individuo.trees[0].child, arbol.children.get(0));
	}

	/**
	 * This method will create the GPNode from Node.
	 * 
	 * @param nodoGp
	 * @param mynode
	 */
	private void fullNode(GPNode nodoGp, Node mynode) {
		Iterator<Node> it = mynode.children.iterator();
		try {
			String prefix = "ec.app.rubik.func.";
			if (mynode.title.indexOf("Face") >= 0) {
				prefix += "face.";
			} else if (mynode.title.indexOf("Color") >= 0) {
				prefix += "color.";
			} else if (mynode.title.indexOf("X") >= 0) {
				prefix += "x.";
			} else if (mynode.title.indexOf("Y") >= 0) {
				prefix += "y.";
			} else if (mynode.title.indexOf("Clockwise") >= 0) {
				prefix += "direction.";
				if (mynode.title.compareTo(Clockwise.class.getSimpleName()) == 0) {
					mynode.title = CounterClockwise.class.getSimpleName();
				} else {
					mynode.title = Clockwise.class.getSimpleName();
				}
			}
			int i = 0;
			GPNode aux = (GPNode) Class.forName(prefix + mynode.title)
					.newInstance();
			nodoGp.children = new GPNode[mynode.children.size()];
			aux.children = new GPNode[mynode.children.size()];
			while (it.hasNext()) {
				nodoGp.children[i] = new Progn2();
				nodoGp.children[i].parent = nodoGp;
				nodoGp.children[i].children = new GPNode[0];
				nodoGp.children[i].argposition = (byte) i;
				this.fullNode(nodoGp.children[i], it.next());
				i++;

			}
			nodoGp.replaceWith(aux);

		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}

	/**
	 * Method that will return the plan to solve a cube.
	 */
	public List<Move> solve(FrontierCube cube) {
		// TODO Auto-generated method stub
		RubiksData data = new RubiksData();
		MyRubiksCube mycube = new MyRubiksCube(cube);
		mycube.setRemember(true);
		data.setCube(mycube);
		int j = 0;
		int score = 0;
		int nsteps=-1;
		while (score != MyRubiksCube.HIGHESTSCORE && j < IndividualTester.iterations
				&& nsteps!=data.getCube().getNSteps()) {
			nsteps=data.getCube().getNSteps();
			((GPIndividual) individuo).trees[0].child.eval(null, 0, data, null,
					((GPIndividual) individuo), null);
			// System.out.println(data.getCube());
			score = data.getCube().scoreCube();
			j++;
		}

		return data.getCube().getSteps();
	}

	public int getTNumber(){
		return this.tNumber;
	}
	public void setTNumber(int tNumber){
		this.tNumber=tNumber;
	}
	public int getTsolved() {
		return tsolved;
	}
	public void setTsolved(int tsolved) {
		this.tsolved = tsolved;
	}

	public boolean isFinish() {
		return finish;
	}
	public void setFinish(boolean finish) {
		this.finish = finish;
	}
	/**
	 * Auxiliar class to parse the individual code from the log.
	 * 
	 * @author nacho
	 * 
	 */
	class Node {
		public Vector<Node> children = new Vector<Node>();
		public Node father;
		public String title;
		public int depth;
	}

	public static void main(String[] args) {
		
		String ind1="(Progn3 (Progn3 (If (And (Or (Test X1 Y2     FaceBack Color1) (And (Test X0 Y1 FaceFront     Color0) (Test X0 Y2 FaceLeft Color4))) (Test     X1 Y1 FaceLeft Color1)) (Move FaceDown CounterClockwise))     (If (And (Or (Test X1 Y2 FaceBack Color1)         (And (Test X0 Y1 FaceFront Color0) (Test             X0 Y2 FaceLeft Color4))) (Test X1 Y1 FaceLeft         Color1)) (Move FaceDown CounterClockwise))     (Progn2 (Progn2 (Progn2 (Progn3 (If (Test         X0 Y0 FaceRight Color3) (Move FaceDown Clockwise))         (If (Test X1 Y0 FaceUp Color0) (Move FaceLeft             CounterClockwise)) Empty) Empty) Empty) (If         (And (Or (Test X1 Y2 FaceFront Color4) (And             (And (Test X0 Y1 FaceFront Color0) (Test                 X0 Y2 FaceLeft Color4)) (Test X1 Y1 FaceBack             Color3))) (Test X0 Y2 FaceLeft Color3)) (Move         FaceDown Clockwise)))) (Progn2 (If (And (Or     (Test X1 Y2 FaceBack Color1) (Test X1 Y1     FaceBack Color3)) (And (Test X2 Y1 FaceLeft     Color1) (Test X1 Y1 FaceBack Color3))) (Move     FaceDown CounterClockwise)) (Progn2 (If (No     (And (Test X2 Y1 FaceLeft Color1) (Test X1         Y1 FaceBack Color3))) (Move FaceUp Clockwise))     (If (No (Or (Test X2 Y0 FaceFront Color1)         (Test X1 Y1 FaceLeft Color0))) (Move FaceRight         CounterClockwise)))) (Progn2 (Progn2 (Progn2     (Progn3 (Progn3 (Progn3 (If (And (Or (Test         X1 Y2 FaceBack Color1) (And (Test X0 Y1 FaceFront         Color0) (Or (Test X1 Y2 FaceBack Color1)         (And (Test X0 Y1 FaceFront Color0) (Test             X0 Y2 FaceLeft Color4))))) (Test X1 Y1 FaceLeft         Color1)) (Move FaceDown CounterClockwise))         (Progn2 (If (And (Or (Test X1 Y2 FaceBack             Color1) (Test X1 Y1 FaceBack Color3)) (And             (No (Test X1 Y1 FaceLeft Color0)) (Test X1             Y1 FaceBack Color3))) (Move FaceDown CounterClockwise))             (Progn2 (If (No (And (Test X2 Y1 FaceLeft                 Color1) (Test X1 Y1 FaceBack Color3))) (Move                 FaceUp Clockwise)) (If (No (Test X2 Y1 FaceLeft                 Color1)) (Move FaceRight CounterClockwise))))         (Progn2 (Progn2 (Progn2 (Progn3 (If (Test             X0 Y0 FaceRight Color3) (Move FaceDown Clockwise))             (If (Test X1 Y0 FaceUp Color0) (Move FaceLeft                 CounterClockwise)) Empty) Empty) Empty) (If             (And (Or (Test X1 Y2 FaceFront Color4) (Test                 X0 Y2 FaceLeft Color4)) (Test X0 Y2 FaceLeft                 Color3)) (Move FaceDown Clockwise)))) (Progn2         (If (And (Or (Test X1 Y2 FaceBack Color1)             (Test X1 Y1 FaceBack Color3)) (And (Test             X2 Y1 FaceLeft Color1) (Test X1 Y1 FaceBack             Color3))) (Move FaceDown CounterClockwise))         (Progn2 (If (No (And (Test X2 Y1 FaceLeft             Color1) (Test X1 Y1 FaceBack Color3))) (Move             FaceUp Clockwise)) (If (No (Test X1 Y1 FaceLeft             Color0)) (Move FaceRight CounterClockwise))))         (Progn2 (Progn2 (Progn2 (Progn3 (If (Test             X0 Y0 FaceRight Color3) (Move FaceDown Clockwise))             (If (Test X1 Y0 FaceUp Color0) (Move FaceLeft                 CounterClockwise)) Empty) Empty) Empty) (If             (And (Or (Test X1 Y2 FaceFront Color4) (And                 (Test X2 Y1 FaceLeft Color1) (Test X1 Y1                 FaceBack Color3))) (Test X0 Y2 FaceLeft Color3))             (Move FaceDown Clockwise)))) (If (Test X1         Y0 FaceUp Color0) (Move FaceLeft CounterClockwise))         Empty) Empty) Empty) (If (And (Or (Test X1     Y2 FaceFront Color4) (And (Test X2 Y1 FaceLeft     Color1) (Test X1 Y1 FaceBack Color3))) (Test     X0 Y2 FaceLeft Color3)) (Move FaceDown Clockwise))))";
		String ind2="(Progn2 (If (No (And (Test X1 Y1 FaceRight    Color2) (Test X1 Y2 FaceBack Color1))) (Move    FaceDown Clockwise)) (Progn2 (If (No (And    (Test X1 Y2 FaceUp Color1) (Test X1 Y1 FaceFront    Color5))) (Move FaceUp CounterClockwise))    (If (Or (Or (No (Test X0 Y2 FaceUp Color1))        (Or (No (Test X0 Y2 FaceUp Color1)) (And            (Test X1 Y1 FaceFront Color5) (And (No (And            (Test X1 Y2 FaceUp Color1) (Test X1 Y1 FaceFront            Color5))) (Test X2 Y2 FaceFront Color1)))))        (Or (Or (No (Test X0 Y2 FaceUp Color1)) (And            (And (And (Or (Or (No (Test X0 Y2 FaceUp                Color1)) (Or (No (Test X0 Y1 FaceDown Color2))                (Test X0 Y2 FaceUp Color1))) (No (Or (Test                X1 Y2 FaceUp Color1) (Test X1 Y1 FaceDown                Color2)))) (And (And (Test X1 Y1 FaceUp Color1)                (Test X1 Y1 FaceFront Color5)) (Or (No (Test                X0 Y2 FaceUp Color1)) (And (And (And (Test                X1 Y1 FaceDown Color2) (Test X0 Y1 FaceDown                Color2)) (Test X1 Y2 FaceUp Color1)) (No                (And (Test X1 Y1 FaceRight Color2) (Test                    X1 Y2 FaceBack Color1))))))) (Test X1 Y2                FaceBack Color1)) (No (Test X2 Y2 FaceDown            Color3)))) (No (Or (Or (Or (Or (Test X1 Y2            FaceUp Color1) (No (Test X1 Y2 FaceLeft Color0)))            (Test X1 Y1 FaceDown Color2)) (Test X1 Y2            FaceBack Color1)) (Test X1 Y1 FaceDown Color2)))))        (Move FaceLeft CounterClockwise))))";
		String ind3="(Progn2 (If (No (And (Test X1 Y1 FaceRight    Color2) (Test X1 Y2 FaceBack Color1))) (Move    FaceDown Clockwise)) (Progn2 (If (No (And    (Test X1 Y2 FaceUp Color1) (Test X1 Y1 FaceFront    Color5))) (Move FaceUp CounterClockwise))    (If (Or (Or (No (Test X0 Y2 FaceUp Color1))        (And (No (And (Test X1 Y2 FaceUp Color1)            (Test X1 Y1 FaceFront Color5))) (Test X2            Y2 FaceFront Color1))) (Or (Or (No (Test        X0 Y2 FaceUp Color1)) (And (And (Or (No (Test        X0 Y2 FaceUp Color1)) (And (Or (Or (No (Test        X0 Y2 FaceUp Color1)) (Or (No (Test X0 Y1        FaceDown Color2)) (Test X0 Y2 FaceUp Color1)))        (No (Or (Test X1 Y2 FaceUp Color1) (Test            X1 Y1 FaceDown Color2)))) (And (And (Test        X1 Y2 FaceUp Color1) (Test X1 Y1 FaceFront        Color5)) (Or (No (Test X0 Y2 FaceUp Color1))        (And (And (And (Test X1 Y1 FaceDown Color2)            (Test X0 Y1 FaceDown Color2)) (Test X1 Y2            FaceUp Color1)) (No (And (Test X1 Y1 FaceRight            Color2) (Test X1 Y2 FaceBack Color1))))))))        (Test X1 Y2 FaceBack Color1)) (No (Test X2        Y2 FaceDown Color3)))) (No (Or (Or (Test        X1 Y2 FaceUp Color1) (No (Test X1 Y2 FaceLeft        Color0))) (Test X1 Y1 FaceDown Color2)))))        (Move FaceLeft CounterClockwise))))";
		String ind4="(Progn2 (Progn2 (Progn2 (Progn2 (Progn2 (Progn2     (If (And (And (Test X0 Y0 FaceBack Color2)         (Test X0 Y0 FaceUp Color5)) (Test X0 Y1 FaceBack         Color1)) (Move FaceUp CounterClockwise))     (Progn2 (If (And (And (Or (No (Test X0 Y2         FaceRight Color3)) (Test X2 Y2 FaceRight         Color1)) (Test X2 Y1 FaceFront Color1)) (Test         X2 Y1 FaceFront Color1)) (Move FaceDown Clockwise))         Empty)) Empty) (Progn2 (If (And (And (Or     (And (Or (No (Test X0 Y2 FaceRight Color3))         (Test X2 Y2 FaceRight Color1)) (Test X0 Y0         FaceBack Color2)) (Test X2 Y2 FaceRight Color1))     (Test X2 Y1 FaceFront Color1)) (Test X0 Y2     FaceLeft Color4)) (Move FaceDown Clockwise))     Empty)) Empty) (Progn2 (Progn2 (If (And (And     (Or (And (And (Or (And (Or (No (Test X0 Y2         FaceRight Color3)) (No (Test X0 Y2 FaceRight         Color3))) (Test X2 Y1 FaceFront Color1))         (Or (No (Test X0 Y2 FaceRight Color3)) (Test             X2 Y2 FaceRight Color1))) (Or (No (Or (No         (Test X0 Y2 FaceRight Color3)) (Test X2 Y0         FaceBack Color2))) (And (Or (No (Test X0         Y2 FaceRight Color3)) (Test X2 Y2 FaceRight         Color1)) (Test X2 Y1 FaceFront Color1))))         (Test X2 Y1 FaceFront Color1)) (Or (And (Or         (No (Test X0 Y2 FaceRight Color3)) (Test         X2 Y2 FaceRight Color1)) (And (Test X1 Y1         FaceLeft Color5) (Test X0 Y0 FaceBack Color2)))         (And (Or (And (Test X2 Y2 FaceRight Color1)             (And (Test X1 Y1 FaceLeft Color5) (Test X2                 Y1 FaceFront Color1))) (And (Or (No (Test             X0 Y2 FaceRight Color3)) (Test X2 Y2 FaceRight             Color1)) (Test X2 Y1 FaceFront Color1)))             (Test X2 Y1 FaceFront Color1)))) (Test X2     Y1 FaceFront Color1)) (Test X0 Y2 FaceLeft     Color4)) (Move FaceDown Clockwise)) Empty)     Empty)) (Progn2 (If (And (And (And (Or (No     (Test X2 Y2 FaceRight Color1)) (Or (And (Or     (No (Test X0 Y2 FaceRight Color3)) (Test     X2 Y2 FaceRight Color1)) (And (Test X1 Y1     FaceLeft Color5) (Test X0 Y1 FaceBack Color1)))     (And (Or (No (Test X0 Y2 FaceRight Color3))         (Test X2 Y2 FaceRight Color1)) (Test X2 Y1         FaceFront Color1)))) (And (Or (And (Or (No     (Test X0 Y2 FaceRight Color3)) (Test X0 Y0     FaceBack Color2)) (Test X2 Y1 FaceFront Color1))     (No (Test X0 Y2 FaceRight Color3))) (Test     X2 Y2 FaceRight Color1))) (Test X2 Y1 FaceFront     Color1)) (Test X0 Y2 FaceLeft Color4)) (Move     FaceDown Clockwise)) Empty))";
		String ind5="(Progn3 (If (And (And (Test X0 Y2 FaceFront     Color1) (And (Or (And (Or (Test X1 Y2 FaceFront     Color2) (Test X1 Y2 FaceRight Color2)) (Test     X1 Y1 FaceLeft Color2)) (Test X0 Y2 FaceRight     Color2)) (Or (Test X0 Y0 FaceUp Color1) (Test     X1 Y1 FaceLeft Color2)))) (Or (No (Test X2     Y2 FaceLeft Color0)) (And (Or (And (Or (Or     (And (Or (Test X1 Y2 FaceFront Color2) (Test         X2 Y2 FaceLeft Color0)) (Test X1 Y1 FaceLeft         Color2)) (Test X2 Y2 FaceBack Color3)) (Test     X2 Y2 FaceLeft Color0)) (And (And (Test X0     Y2 FaceFront Color1) (And (Or (Or (Test X0     Y0 FaceUp Color1) (Test X1 Y1 FaceLeft Color2))     (Or (Test X0 Y0 FaceUp Color1) (Test X1 Y1         FaceLeft Color2))) (Test X0 Y1 FaceLeft Color1)))     (And (Or (Test X0 Y0 FaceUp Color1) (Test         X1 Y2 FaceRight Color2)) (Test X2 Y2 FaceRight         Color2)))) (Test X0 Y1 FaceFront Color5))     (Or (And (Or (Test X0 Y1 FaceLeft Color1)         (Test X0 Y2 FaceRight Color2)) (Test X2 Y2         FaceBack Color3)) (Test X2 Y2 FaceRight Color2)))))     (Move FaceDown Clockwise)) (Progn2 (If (And     (Or (Or (Test X0 Y0 FaceUp Color1) (Or (And         (Test X1 Y1 FaceLeft Color3) (Test X1 Y1         FaceLeft Color2)) (Test X0 Y1 FaceLeft Color1)))         (Or (Test X0 Y0 FaceUp Color1) (Test X1 Y1             FaceLeft Color2))) (Or (Test X1 Y0 FaceFront     Color1) (And (And (Or (Test X1 Y2 FaceFront     Color2) (Test X1 Y2 FaceRight Color2)) (And     (Or (And (Test X1 Y1 FaceLeft Color3) (Test         X1 Y1 FaceLeft Color2)) (Test X0 Y2 FaceRight         Color2)) (Test X2 Y2 FaceBack Color3))) (Or     (Test X1 Y1 FaceLeft Color2) (And (Or (Or     (Test X0 Y0 FaceUp Color1) (Test X1 Y1 FaceLeft     Color2)) (Or (Test X0 Y0 FaceUp Color1) (Test     X0 Y2 FaceRight Color2))) (Test X0 Y1 FaceLeft     Color1)))))) (Move FaceLeft CounterClockwise))     (If (And (Test X2 Y2 FaceBack Color3) (And         (Or (Test X0 Y1 FaceFront Color5) (Or (Or             (Or (Test X0 Y0 FaceUp Color1) (Test X1 Y1                 FaceLeft Color2)) (Or (Test X0 Y0 FaceUp             Color1) (Test X1 Y1 FaceLeft Color2))) (Or             (Or (And (Or (And (Test X1 Y1 FaceLeft Color3)                 (Test X1 Y1 FaceLeft Color2)) (Test X0 Y2                 FaceRight Color2)) (Test X2 Y2 FaceBack Color3))                 (Test X2 Y2 FaceRight Color2)) (Test X0 Y0             FaceUp Color1)))) (Or (Test X1 Y0 FaceFront         Color1) (And (Or (Test X0 Y0 FaceUp Color1)         (Test X1 Y2 FaceRight Color2)) (Test X2 Y2         FaceRight Color2))))) (Move FaceLeft CounterClockwise)))     (If (And (And (Test X0 Y2 FaceFront Color1)         (And (Test X1 Y1 FaceLeft Color3) (Test X0             Y1 FaceLeft Color1))) (And (Or (Test X1 Y2         FaceFront Color2) (Test X1 Y1 FaceLeft Color3))         (Test X1 Y1 FaceLeft Color2))) (Move FaceDown         Clockwise)))";
		String inds[]={ind1,ind2,ind3,ind4,ind5};
		
		
		System.out.println(Calendar.getInstance().getTime() + "");
		System.out.println("Init cache");
		IndividualTester.getCache();
		System.out.println("Done.");
		int nInd=1;
		for(String ind:inds){
			IndividualTester []testers=new IndividualTester[maxdiff];
			int diff = 1;
			IndividualTester it = new IndividualTester(ind);
			System.out.print("Testing...");
			while (diff <= maxdiff) {
				testers[diff-1]=new IndividualTester(it);
				testers[diff-1].setTNumber(diff);
				testers[diff-1].start();
				System.out.print("s");
				diff++;
			}
			diff=1;
			int tsolved=0;
			int total=0;
			int []solved=new int[maxdiff];
			while (diff <= maxdiff) {
				if(testers[diff-1].isFinish()){
					tsolved+=testers[diff-1].getTsolved();
					solved[diff-1]=testers[diff-1].getTsolved();
					total+=cache.getNumCubos(diff);
					diff++;
					System.out.print("f");
				}else{
					try {
						Thread.sleep( 1000 );//1s 
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
			System.out.println("");
			System.out.println("Statistics for Individual"+nInd+":");
			for(int i=0;i<solved.length;i++){
				float por=(float)solved[i]/(float)cache.getNumCubos(i+1);
				por*=100;
				System.out.println("Difficulty "+(i+1)+":"+solved[i]+"/"+cache.getNumCubos(i+1)+" ("+por+"%)");
			}
			float por=(float)tsolved/(float)total;
			por*=100;
			System.out.println("Total solved:"+tsolved+"/"+total+" ("+por+"%)");
			nInd++;
		}

	}
	
	
	public void run(){
		Cache cache=IndividualTester.getCache();
		int diff=this.getTNumber();
		for (int i = 0; i < cache.getNumCubos(diff); i++) {
			FrontierCube cub=cache.getRubik(i, diff-1);
			List<Move> moves = solve(cub);
			//Iterator<Move> iter = moves.iterator();
			cub.apply(moves);
			if(cub.isSolved()){
				tsolved++;
			}
		}
		this.setFinish(true);
	}

}
