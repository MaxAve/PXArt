package pxart.paint.ui;

import pxart.paint.Mouse;
import pxart.paint.Frame;

public class GUIComponent {
	public int x;
	public int y;
	public int width;
	public int height;
	
	public GUIComponent(int x, int y, int width, int height) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}
	
	public boolean mouseOver() {
		return (
				Mouse.getScreenX() >= this.x
				&& Mouse.getScreenX() <= (this.x+this.width)
				&& Mouse.getScreenY() >= this.y
				&& Mouse.getScreenY() <= (this.y+this.height));
	}
	
	public boolean beingClickedOn() {
		return (
				Frame.mouse.lastPressX >= this.x
				&& Frame.mouse.lastPressX <= (this.x+this.width)
				&& Frame.mouse.lastPressY >= this.y
				&& Frame.mouse.lastPressY <= (this.y+this.height));
	}
}
