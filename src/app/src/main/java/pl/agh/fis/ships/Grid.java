package pl.agh.fis.ships;

import java.io.Serializable;
import java.util.Random;

import android.view.View;
import android.widget.Button;

public class Grid implements Serializable {
	protected Ship[] ships;
	protected View[] rids;
	protected int[][] matrix;
	protected int shipCounter;
	protected boolean ready = false;
	protected boolean isAbstract;
	
	public static final int FREE = 0;
	public static final int USED = 1;
	
	public Grid(View[] rids, boolean isAbstract) { 
		this.rids = rids;
		this.matrix = new int[11][11];
		this.ships = new Ship[10];
		this.shipCounter = 0;
		for(int i = 0; i < 10; i++) {
			for(int j = 0; j < 10; j++)
				matrix[i][j] = Grid.FREE;
		}
		this.isAbstract = isAbstract;
	}
	
	
	public Grid(View[] rids) {
		this(rids, false);
	}
	
	
	public Grid() {
		this(null, true);
	}
	
	public Grid(int[] parsedGrid) {
		this.ships = new Ship[10];
		this.matrix = new int[11][11];
		this.shipCounter = 10;
		for(int i = 0; i < 10; i++) {
			ships[i] = new Ship(parsedGrid[i*3], parsedGrid[i*3 + 1], parsedGrid[i*3 +2]);
			for(int j = 0; j < parsedGrid[i*3]; j++) {
				if(parsedGrid[i*3 + 2] - parsedGrid[i*3 + 1] == parsedGrid[i*3] - 1) 
					matrix[(parsedGrid[i*3 + 1] + j)/10][(parsedGrid[i*3 + 1] + j)%10] = 1;
				if(parsedGrid[i*3 + 2] - parsedGrid[i*3 + 1] == (parsedGrid[i*3] - 1)*10) 
					matrix[(parsedGrid[i*3 + 1] + j*10)/10][(parsedGrid[i*3 + 1] + j*10)%10] = 1;
			}
		}
	}
	
	public Grid(int[] parsedGrid, View[] rids) {
		this.ships = new Ship[10];
		this.shipCounter = 10;
		this.matrix = new int[11][11];
		this.rids = rids;
		this.isAbstract = false;
		for(int i = 0; i < 10; i++) {
			ships[i] = new Ship(parsedGrid[i*3], parsedGrid[i*3 + 1], parsedGrid[i*3 +2], rids);
			for(int j = 0; j < parsedGrid[i*3]; j++) {
				if(parsedGrid[i*3 + 2] - parsedGrid[i*3 + 1] == parsedGrid[i*3] - 1) 
					matrix[(parsedGrid[i*3 + 1] + j)/10][(parsedGrid[i*3 + 1] + j)%10] = 1;
				if(parsedGrid[i*3 + 2] - parsedGrid[i*3 + 1] == (parsedGrid[i*3] - 1)*10) 
					matrix[(parsedGrid[i*3 + 1] + j*10)/10][(parsedGrid[i*3 + 1] + j*10)%10] = 1;
			}
		}
	}
	
	public Grid(int[] parsedGrid, View[] rids, boolean fake) {
		this.ships = new Ship[10];
		this.shipCounter = 10;
		this.matrix = new int[11][11];
		this.rids = rids;
		this.isAbstract = true;
		for(int i = 0; i < 10; i++){ 
			ships[i] = new Ship(parsedGrid[i*3], parsedGrid[i*3 + 1], parsedGrid[i*3 +2], rids, true);
			for(int j = 0; j < parsedGrid[i*3]; j++) {
				if(parsedGrid[i*3 + 2] - parsedGrid[i*3 + 1] == parsedGrid[i*3] - 1) 
					matrix[(parsedGrid[i*3 + 1] + j)/10][(parsedGrid[i*3 + 1] + j)%10] = 1;
				if(parsedGrid[i*3 + 2] - parsedGrid[i*3 + 1] == (parsedGrid[i*3] - 1)*10) 
					matrix[(parsedGrid[i*3 + 1] + j*10)/10][(parsedGrid[i*3 + 1] + j*10)%10] = 1;
			}
		}
	}
	
	public int[] getParsedGrid() {
		int[] temp = new int[30];
		int k = 0;
		for(int i = 0; i < 10; i++) {
			int[] t = this.ships[i].getParsedShip();
			for(int j = 0; j < 3; j++) {
				temp[k++] = t[j];
			}
		}
		return temp;
	}
	
	public void clearGrid() {
		ready = false;
		for(int i = 0; i < 100; i++) {
				Button temp = (Button)rids[i];
				temp.setBackgroundResource(R.drawable.button_field);
		}
		for(int i = 0; i < 10; i++) {
			for(int j = 0; j < 10; j++)
				matrix[i][j] = Grid.FREE;
		}
		shipCounter = 0;
	}
	
	public int[][] getMatrix() {
		return matrix;
	}
	
	public int getShipCounter() {
		return shipCounter;
	}
	
	public boolean isEmpty(int x, int y) {
		for(int i = -1; i < 2; i++) {
			for(int j = -1; j < 2; j++) {
				if(this.matrix[Math.abs(x + i)][Math.abs(y + j)] == Grid.USED)
					return false;
			}
		}
		return true;
	}
	
		
	public boolean insertRandomShip(int nr) {
		boolean inserted = false, direction;
		int x, y;
		while(!inserted) {
			direction = new Random().nextBoolean();
			inserted = true;
			x = new Random().nextInt(10);
			y = new Random().nextInt(11 - nr);
			//true - vertical, false - horizontal
			for(int i = 0; i < nr; i++) {
				if(direction) {
					if(!isEmpty(x, y + i)) {
						inserted = false;
						break;
					}
				} else {
					if(!isEmpty(y + i, x)) {
						inserted = false;
						break;
					}
				}
			}
			if(inserted) {
				View[] temp = new View[nr];
				int[] temp2 = new int[nr];
				for(int i = 0; i < nr; i++) {
					if(direction) {
						temp[i] = rids[x*10 + y + i];
						temp2[i] = x*10 + y + i;
						this.matrix[x][y + i] = 1;
					} else {
						temp[i] = rids[y*10 + i*10 + x];
						temp2[i] = y*10 + i*10 + x;
						this.matrix[y + i][x] = 1;
					}
				}
				if(isAbstract)
					ships[shipCounter] = new Ship(nr, temp, temp2, true);
				else
					ships[shipCounter] = new Ship(nr, temp, temp2);
				shipCounter++;
			}
		}
		return inserted;
	}
	
	public void randomize() {
		clearGrid();
		for(int i = 4; i > 0; i--) {
			for(int j = i; j < 5; j++)
				insertRandomShip(i);
		}
		this.ready = true;
	}
	
	public boolean isReady() {
		return this.ready; 
	}
	
	public Ship[] getShips() {
		return ships;
	}
	

	public View[] getRids() {
		return rids;
	}
	
	
}
