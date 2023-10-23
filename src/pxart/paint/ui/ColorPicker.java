package pxart.paint.ui;

import java.awt.Color;
import java.awt.image.BufferedImage;

import pxart.paint.Frame;
import pxart.paint.ImageOps;
import pxart.paint.Mouse;
import pxart.paint.Panel;
import pxart.paint.Tools;

public class ColorPicker {
	public int x;
	public int y;
	public int width;
	public int height;
	
	public int getX() {
		return this.x;
	}
	
	public int getY() {
		return this.y;
	}
	
	public class Cursor {
		public int x;
		public int y;
		public Color currentColor;
		public Cursor(int x, int y) {
			this.x = this.y;
		}
		public int getXRelative() {
			return this.x - getX();
		}
		public int getYRelative() {
			return this.y - getY();
		}
	}
	
	public boolean mouseOver() {
		return Frame.mouse.lastPressX >= this.x && Frame.mouse.lastPressX <= (this.x+255) && Frame.mouse.lastPressY >= this.y && Frame.mouse.lastPressY <= (this.y+255);
	}
	
	public ColorPicker.Cursor cursor = new ColorPicker.Cursor(0, 0);
	public BufferedImage image;
	private int[][][] imageData;
	
	public ColorPicker(int x, int y, int w, int h) {
		this.x = x;
		this.y = y;
		this.width = w;
		this.height = h;
		this.cursor.x = this.x + this.width/2;
		this.cursor.y = this.y + this.height/2;
	}
	
	public void setHue(float hue) {
		this.imageData = ImageOps.generateColorPicker(hue);
		this.image = ImageOps.pixelArrayToImage(this.imageData);
	}
	
	private boolean dragging = false;
	private class DragThread extends Thread {
		public void run() {
			dragging = true;
			while(Frame.mouse.mouseDown) {
				if(Mouse.getScreenX() < x)
					cursor.x = x;
				else if(Mouse.getScreenX() > x + 255)
					cursor.x = x + width;
				else
					cursor.x = Mouse.getScreenX();
				if(Mouse.getScreenY() < y)
					cursor.y = y;
				else if(Mouse.getScreenY() > y + 255)
					cursor.y = y + height;
				else
					cursor.y = Mouse.getScreenY();
				// Update Saturation and Value of the main color
				Tools.mainColor[1] = (float)cursor.getXRelative() / 255f;
				Tools.mainColor[2] = 1f - ((float)cursor.getYRelative() / 255f);
			}
			dragging = false;
		}
	}
	
	public void dragColorCursorOnMouseClick() {
		if(
				Frame.mouse.lastPressX >= this.x
				&& Frame.mouse.lastPressX <= (this.x+255)
				&& Frame.mouse.lastPressY >= this.y
				&& Frame.mouse.lastPressY <= (this.y+255)) {
			if(!dragging) {
				DragThread dt = new DragThread();
				dt.start();
			}
		}
	}
	
	public Color getSelectedColor() {
		return ImageOps.HSVtoColor(Tools.mainColor[0], Tools.mainColor[1], Tools.mainColor[2], Tools.transparency);
	}
}
