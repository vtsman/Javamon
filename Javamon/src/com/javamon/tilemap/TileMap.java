package com.javamon.tilemap;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import com.javamon.util.Utilities;

public class TileMap {

	// position
	private double x;
	private double y;
	private int windowWidth = 640, windowHeight = 360;

	// bounds
	private int xmin;
	private int ymin;
	private int xmax;
	private int ymax;

	private double tween;

	// map
	private int[][] map;
	private int tileSize;
	private int numRows;
	private int numCols;
	private int width;
	private int height;

	// tileset
	private BufferedImage tileset;
	private int numTilesAcross;
	private Tile[][] tiles;

	// drawing
	private int rowOffset;
	private int colOffset;
	private int numRowsToDraw;
	private int numColsToDraw;

	public TileMap(int tileSize) {
		this.tileSize = tileSize;
		numRowsToDraw = windowWidth / tileSize + 2;
		numColsToDraw = windowHeight / tileSize + 2;
		tween = 0.07;
	}

	/**
	 * Loads the given tileset
	 * @param i
	 */
	public void loadTiles(BufferedImage tileSet) {
		try {
			tileset = tileSet;
			numTilesAcross = tileset.getWidth() / tileSize;
			tiles = new Tile[2][numTilesAcross];

			BufferedImage subimage;
			for(int col = 0; col < numTilesAcross; col++) {
				subimage = tileset.getSubimage(col * tileSize, 0, tileSize, tileSize);
				tiles[0][col] = new Tile(subimage, col, false);
				//subimage = tileset.getSubimage(col * tileSize, tileSize, tileSize, tileSize);
				//tiles[1][col] = new Tile(subimage, Tile.BLOCKED);
			}
		}
		catch(Exception e) {
			e.printStackTrace();
			Utilities.createErrorDialog("TileSet Error", "An error occured while loading a tileset\n" + e.toString() + tileSet, e);
		}
	}

	/**
	 * Loads the map from a .map file
	 * @param level
	 */
	public void loadMap(String level) {
		try {
			InputStream in = getClass().getResourceAsStream(level);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));

			numCols = Integer.parseInt(br.readLine());
			numRows = Integer.parseInt(br.readLine());
			map = new int[numRows][numCols];
			width = numCols * tileSize;
			height = numRows * tileSize;

			xmin = windowWidth - width;
			xmax = 0;
			ymin = windowHeight - height;
			ymax = 0;

			String delims = "\\s+";
			for(int row = 0; row < numRows; row++) {
				String line = br.readLine();
				String[] tokens = line.split(delims);
				for(int col = 0; col < numCols; col++) {
					map[row][col] = Integer.parseInt(tokens[col]);
				}
			}
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Returns the tilesize
	 * @return
	 */
	public int getTileSize() { 
		return tileSize; 
	}

	public double getx() { 
		return x; 
	}

	public double gety() {
		return y;
	}

	public int getWidth() { 
		return width; 
	}

	public int getHeight() { 
		return height; 
	}

	public int getType(int row, int col) {
		// return Tile.BLOCKED if row or col are outside the map[][]
		if (row >= numRows || row < 0) {
			// LogHelper.logError("Parameter row is out of bounds: " + row);
			return 1;
		}
		if (col >= numCols || col < 0) {
			// LogHelper.logError("Parameter col is out of bounds: " + col);
			return 1;
		}

		int rc = map[row][col];
		int r = rc / numTilesAcross;
		int c = rc % numTilesAcross;
		return tiles[r][c].getType();
	}
	
	public boolean isBlocked(int row, int col) {
		// return Tile.BLOCKED if row or col are outside the map[][]
		if (row >= numRows || row < 0) {
			// LogHelper.logError("Parameter row is out of bounds: " + row);
			return true;
		}
		if (col >= numCols || col < 0) {
			// LogHelper.logError("Parameter col is out of bounds: " + col);
			return true;
		}

		int rc = map[row][col];
		int r = rc / numTilesAcross;
		int c = rc % numTilesAcross;
		return tiles[r][c].isBlocked();
	}

	public void setTween(double d) { 
		tween = d; 
	}

	public void setPosition(double x, double y) {
		this.x += (x - this.x) * tween;
		this.y += (y - this.y) * tween;

		fixBounds();

		colOffset = (int) - this.x / tileSize;
		rowOffset = (int) - this.y / tileSize;
	}

	private void fixBounds() {
		if(x < xmin) x = xmin;
		if(y < ymin) y = ymin;
		if(x > xmax) x = xmax;
		if(y > ymax) y = ymax;
	}

	public void render(Graphics2D g) {
		for(int row = rowOffset; row < rowOffset + numRowsToDraw; row++) {
			if(row >= numRows) break;
			for(int col = colOffset; col < colOffset + numColsToDraw; col++) {
				if(col >= numCols) break;

				if(map[row][col] == 0) continue;

				int rc = map[row][col];
				int r = rc / numTilesAcross;
				int c = rc % numTilesAcross;

				g.drawImage(tiles[r][c].getImage(), (int) x + col * tileSize, (int) y + row * tileSize, null);	
			}
		}
	}
}