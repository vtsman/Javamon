package com.javamon.mapobject;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

import com.javamon.tilemap.TileMap;

public abstract class MapObject {
	
	protected BufferedImage[] sprites;
	
	// tile stuff
	protected TileMap tileMap;
	protected int tileSize;
	protected double xmap;
	protected double ymap;
	
	// position and vector
	protected double x;
	protected double y;
	protected double dx;
	protected double dy;
	
	// dimensions
	protected int width;
	protected int height;
	
	// collision box
	protected int cwidth;
	protected int cheight;
	
	// collision
	protected int currRow;
	protected int currCol;
	protected double xdest;
	protected double ydest;
	protected double xtemp;
	protected double ytemp;
	protected boolean topLeft;
	protected boolean topRight;
	protected boolean bottomLeft;
	protected boolean bottomRight;
	
	// animation
	protected Animation animation;
	protected int currentAction;
	protected int previousAction;
	protected boolean facingRight;
	
	// movement
	protected boolean left;
	protected boolean right;
	protected boolean up;
	protected boolean down;
	protected boolean jumping;
	protected boolean falling;
	
	// movement attributes
	protected double moveSpeed;
	private double maxSpeed;
	protected double stopSpeed;
	protected double fallSpeed;
	protected double maxFallSpeed;
	protected double jumpStart;
	protected double stopJumpSpeed;
	
	private static boolean showHitbox = true;
	
	
	// constructor
	public MapObject(TileMap tm) {
		tileMap = tm;
		tileSize = tm.getTileSize(); 
	}
	
	/**
	 * Checks to see if this MapObject is intersecting another MapObject
	 * @param o
	 * @return
	 */
	public boolean intersects(MapObject o) {
		Rectangle r1 = getRectangle();
		Rectangle r2 = o.getRectangle();
		return r1.intersects(r2);
	}
	
	/**
	 * Checks to see if this map object is intersecting the specified rectangle
	 * @param o
	 * @return
	 */
	public boolean intersects(Rectangle r) {
		Rectangle r1 = getRectangle();
		return r1.intersects(r);
	}
	
	/**
	 * Creates a new rectangle
	 * @return
	 */
	public Rectangle getRectangle() {
		return new Rectangle(new Rectangle((int) (x - width / 2 + ((width - cwidth) / 2)), (int) (y - height / 2 + ((height - cheight) / 2)), cwidth, cheight));
	}
	
	/**
	 * Calculates the corners of the map object
	 * @param x
	 * @param y
	 */
	public void calculateCorners(double x, double y) {
		int leftTile = (int)(x - cwidth / 2) / tileSize;
		int rightTile = (int)(x + cwidth / 2 - 1) / tileSize;
		int topTile = (int)(y - cheight / 2) / tileSize;
		int bottomTile = (int)(y + cheight / 2 - 1) / tileSize;
		
		topLeft =tileMap.isBlocked(topTile, leftTile);
		topRight = tileMap.isBlocked(topTile, rightTile);
		bottomLeft = tileMap.isBlocked(bottomTile, leftTile);
		bottomRight = tileMap.isBlocked(bottomTile, rightTile);
	}
	
	/**
	 * Checks to see if the mapobject is coliding with the terrain
	 * @returns true if a collision occured, false otherwise
	 */
	public boolean checkTileMapCollision() {
		boolean collision = false;
		currCol = (int)x / tileSize;
		currRow = (int)y / tileSize;
		
		xdest = x + dx;
		ydest = y + dy;
		
		xtemp = x;
		ytemp = y;
		
		calculateCorners(x, ydest);
		if(dy < 0) {
			if(topLeft || topRight) {
				dy = 0;
				ytemp = currRow * tileSize + cheight / 2;
				collision = true;
			} else {
				ytemp += dy;
			}
		}
		if(dy > 0) {
			if(bottomLeft || bottomRight) {
				dy = 0;
				falling = false;
				ytemp = (currRow + 1) * tileSize - cheight / 2;
				collision = true;
			}
			else {
				ytemp += dy;
			}
		}
		
		calculateCorners(xdest, y);
		if(dx < 0) {
			if(topLeft || bottomLeft) {
				dx = 0;
				xtemp = currCol * tileSize + cwidth / 2;
				collision = true;
			} else {
				xtemp += dx;
			}
		}
		if(dx > 0) {
			if(topRight || bottomRight) {
				dx = 0;
				xtemp = (currCol + 1) * tileSize - cwidth / 2;
				collision = true;
			} else {
				xtemp += dx;
			}
		}
		
		if(!falling) {
			calculateCorners(x, ydest + 1);
			if(!bottomLeft && !bottomRight) {
				falling = true;
			}
		}
		return collision;
	}
	
	public int getx() { 
		return (int)x; 
	}
	
	public int gety() { 
		return (int)y; 
	}
	
	public int getxmap() { 
		return (int)xmap; 
	}
	
	public int getymap() { 
		return (int)ymap; 
	}
	
	public int getWidth() { 
		return width; 
	}
	
	public int getHeight() { 
		return height; 
	}
	
	public int getCWidth() { 
		return cwidth; 
	}
	
	public int getCHeight() { 
		return cheight; 
	}
	
	public double getMaxSpeed() {
		return maxSpeed;
	}

	public void setMaxSpeed(double maxSpeed) {
		this.maxSpeed = maxSpeed;
	}

	public void setPosition(double x, double y) {
		this.x = x;
		this.y = y;
	}
	
	public void setVector(double dx, double dy) {
		this.dx = dx;
		this.dy = dy;
	}
	
	public void setMapPosition() {
		xmap = tileMap.getx();
		ymap = tileMap.gety();
	}
	
	public void setLeft(boolean b) { 
		left = b;
	}
	
	public void setRight(boolean b) { 
		right = b;
	}
	
	public void setUp(boolean b) { 
		up = b;
	}
	
	public void setDown(boolean b) { 
		down = b;
	}
	
	public void setJumping(boolean b) { 
		jumping = b;
	}
	
	public static boolean isHitboxEnabled() {
		return showHitbox;
	}
	
	public static void setShowHitbox(boolean b) {
		showHitbox = b;
	}
	
	public Point getScreenLocation() {
		return new Point((int)(x + xmap), (int)(y + ymap));
	}
	
	
	public abstract void tick();
	
	public void render(Graphics2D g) {
		if(facingRight) {
			animation.render(g, (int) (x + xmap - width / 2), (int) (y + ymap - height / 2), width, height);
		} else {
			animation.render(g, (int) (x + xmap - width / 2 + width), (int) (y + ymap - height / 2), -width, height);
		}
		
		if(showHitbox) { 
			g.setColor(Color.WHITE);
			g.draw(new Rectangle((int) (x + xmap - width / 2 + ((width - cwidth) / 2)), (int) (y + ymap - height / 2 + ((height - cheight) / 2)), cwidth, cheight));
		}
	}
}