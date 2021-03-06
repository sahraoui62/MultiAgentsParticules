package MultiAgentsParticules.wator.view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JFrame;
import javax.swing.JPanel;

import MultiAgentsParticules.core.Agent;
import MultiAgentsParticules.core.SMA;
import MultiAgentsParticules.hotPursuit.model.GameOverExcception;
import MultiAgentsParticules.launcher.Main;

public class View implements Observer {
	
	private final static String fileName = "stats.csv";
	private BufferedWriter writer;
	public PrintWriter out;
	private SMA sma;
	private Grid grid;
	private JFrame frame;
	private static boolean torique;
	private static int speed;
	private static int sizeAgent;
	public File png, csv;
	
	/* TODO
	 * Rendre paramétrable dans la vue :
	 * - hauteur, largeur
	 * - nombre de bille
	 * - vitesse
	 * - taille des billes
	 * - durée de l'exécution
	 */
	
	public View(boolean torique, int speed, int sizeAgent) throws InterruptedException, IOException {
		this.sma = Main.getSma();
		this.torique = torique;
		this.speed = speed;
		this.sizeAgent = sizeAgent;
		frame = new JFrame();
		frame.setPreferredSize(new Dimension(sma.getEnvironnement().getWidth() * sizeAgent + 30, sma.getEnvironnement().getHeight() * sizeAgent + 30));
		grid = new Grid(sma.getEnvironnement().getWidth() * sizeAgent ,sma.getEnvironnement().getHeight() * sizeAgent);
		frame.add(grid);
		frame.setVisible(true);
		//List<Agent> list = new ArrayList<Agent>(sma.getAgents());
		for(int i = 0 ; i < sma.getAgents().size() ; i++){
			Agent a = sma.getAgents().get(i);
			grid.fillCell(a.getPositionX(),a.getPositionY(),a.getColor());
		}
		System.out.println("create file writer");
		png = new File("stats.png");
		if(png.exists())
			png.delete();
		csv = new File("stats.csv");

		if(csv.exists())
			csv.delete();

		out = new PrintWriter(new BufferedWriter(new FileWriter(fileName, true)));
		out.println("fish, shark");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.pack();
		frame.setVisible(true);
	}
	
	
	
	public void launch() throws InterruptedException, GameOverExcception{
		sma.run(torique,speed);
	}

	public void update(Observable o, Object arg) {
		grid.clean();
		for(int i = 0 ; i < sma.getAgents().size() ; i++){
			Agent a = sma.getAgents().get(i);
			//System.out.println(a.getPositionX() + " " + a.getPositionY() + " " + a.getColor());
			grid.fillCell(a.getPositionX(),a.getPositionY(),a.getColor());
		}
		sma.nbTypeOfAgent();
		grid.repaint();	
		String stats = (sma.getNbFish())+", "+sma.getNbShark();
		if(sma.getCurrentTurn()%100 == 0){
			try {
				//System.out.println("close and re-open");
				out.close();
				out = new PrintWriter(new BufferedWriter(new FileWriter(fileName, true)));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		out.println(stats);
		
		//frame.add(grid);
	}

	public static class Grid extends JPanel {

		private List<Point> fillCells;
		private int width;
		private int height;
		private Map<Point,Color> mapColors;
		public Grid(int width, int height) {
			fillCells = new ArrayList<Point>();
			this.width = width;
			this.height = height; 
			mapColors = new HashMap<Point,Color>();
		}

		@Override
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);
			for (int i = 0 ; i < fillCells.size() ; i++) {
				Point fillCell = fillCells.get(i);
				//System.out.println(fillCell);
				Color c = mapColors.get(fillCell);
				int cellX = 5 + (fillCell.x * sizeAgent);
				int cellY = 5 + (fillCell.y * sizeAgent);
				//Random r = new Random();
				//Color couleur = new Color(r.nextFloat(), r.nextFloat(), r.nextFloat());
				g.setColor(c);
				g.fillOval(cellX, cellY, sizeAgent, sizeAgent);
			}
			g.setColor(Color.BLACK);
			g.drawRect(5, 5, width, height);

			for (int i = 10; i <= width; i += 10) {
				//g.drawLine(i, 10, i, height + 10);
			}

			for (int i = 10; i <= height; i += 10) {
				//g.drawLine(10, i, width + 10, i);
			}
		}
		
		public void clean(){
			fillCells = new ArrayList<Point>();
			//repaint();
		}
		
		public void fillCell(int x, int y, Color color) {
			Point p = new Point(x, y);
			fillCells.add(p);
			mapColors.put(p,color);
		}

		public void repaint(){
			super.repaint();
		}
	}
}
