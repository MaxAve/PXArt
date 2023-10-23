package pxart.paint;

import java.awt.MouseInfo;
import java.awt.event.MouseEvent;

public class Mouse {
	public int lastPressX;
	public int lastPressY;
	public boolean mouseDown;
	public boolean inFrame;
	
	public Mouse() {
		this.lastPressX = 0;
		this.lastPressY = 0;
		this.mouseDown = false;
		this.inFrame = false;
	}
	
	// Updates mouse state
	public void update(MouseEvent e, boolean mouseWasPressed) {
		this.lastPressX = e.getX();
		this.lastPressY = e.getY();
		this.mouseDown = mouseWasPressed;
		this.inFrame = true;
	}
	
	public static int getScreenX() {
		return (int)MouseInfo.getPointerInfo().getLocation().getX();
	}
	
	public static int getScreenY() {
		return (int)MouseInfo.getPointerInfo().getLocation().getY();
	}
}
