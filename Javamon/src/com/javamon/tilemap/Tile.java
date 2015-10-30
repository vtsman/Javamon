package com.javamon.tilemap;

import java.awt.image.BufferedImage;

public class Tile {

	private BufferedImage image;
	private int type;
	private boolean blocked;
	
	public Tile(BufferedImage image, int type, boolean blocked) {
		this.image = image;
		this.type = type;
		this.blocked = blocked;
	}
	
	public BufferedImage getImage() { 
		return image; 
	}
	
	public int getType() { 
		return type; 
	}
	
	public boolean isBlocked() { 
		return blocked; 
	}
}