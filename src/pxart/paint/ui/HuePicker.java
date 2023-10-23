package pxart.paint.ui;

import java.awt.Color;
import java.awt.image.BufferedImage;

import pxart.paint.Frame;
import pxart.paint.ImageOps;
import pxart.paint.Mouse;
import pxart.paint.Panel;
import pxart.paint.Tools;

public class HuePicker {
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
	
	public HuePicker.Cursor cursor = new HuePicker.Cursor(0, 0);
	public BufferedImage image;
	protected int[][][] imageData;
	
	public HuePicker(int x, int y, int w, int h) {
		this.x = x;
		this.y = y;
		this.width = w;
		this.height = h;
		this.cursor.x = this.x;
		this.cursor.y = this.y + this.height/2;
		this.imageData = ImageOps.generateColorSpectrum(this.width, this.height);
		this.image = ImageOps.pixelArrayToImage(this.imageData);
	}
	
	public void updateMainColor() {
		Tools.mainColor[0] = (float)cursor.getXRelative() / width;
		Panel.colorPicker.setHue(Tools.mainColor[0]);
	}
	
	public boolean mouseOver() {
		return Frame.mouse.lastPressX >= this.x && Frame.mouse.lastPressX <= this.x+this.width && Frame.mouse.lastPressY+Panel.MOUSE_Y_OFFSET >= this.y && Frame.mouse.lastPressY+Panel.MOUSE_Y_OFFSET <= this.y+this.height;
	}
	
	protected boolean dragging = false;
	protected class DragThread extends Thread {
		public void run() {
			dragging = true;
			while(Frame.mouse.mouseDown) {
				if(Mouse.getScreenX() < x)
					cursor.x = x;
				else if(Mouse.getScreenX() > x + width)
					cursor.x = x + width;
				else
					cursor.x = Mouse.getScreenX();
				
				updateMainColor();
			}
			dragging = false;
		}
	}
	
	public void dragColorCursorOnMouseClick() {
		if(
				Frame.mouse.lastPressX >= this.x
				&& Frame.mouse.lastPressX <= this.x+this.width
				&& Frame.mouse.lastPressY+Panel.MOUSE_Y_OFFSET >= this.y
				&& Frame.mouse.lastPressY+Panel.MOUSE_Y_OFFSET <= this.y+this.height) {
			if(!dragging) {
				DragThread dt = new DragThread();
				dt.start();
			}
		}
	}
}