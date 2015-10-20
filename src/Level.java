import java.awt.Dimension;


public class Level {
	public Dimension size;
	public char[][] board;
	public int[][] walls;
	public int[][] dynamicElements;
	public boolean[][] visibleArea;
	public char[][] debugBoard;
	public String[][] parties;
	
	Level(int width, int height){
		size = new Dimension(width,height);
		board = new char[height][width];
		walls = new int[height][width];
		dynamicElements = new int[height][width];
		visibleArea = new boolean[height][width];
		debugBoard = new char[height][width];
		parties = new String[height][width];
	}
}
