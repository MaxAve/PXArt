package pxart.paint.ui;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.util.ArrayList;

import pxart.paint.Frame;
import pxart.paint.ImageOps;
import pxart.paint.Panel;

public class ToggleButton {	
	// This makes it easier to update every button without having to manually call the update method on every single instance
	public static ArrayList<ToggleButton> objects = new ArrayList<>();
	
	public final int ID;
	
	// Example: buttonStateImages[0] will give you the button image for the button "off" state
	private BufferedImage[] buttonStateImages = new BufferedImage[2];
	
	public boolean on;
	
	public int x;
	public int y;
	public int width;
	public int height;
	
	private Action onClick;
	
	public ToggleButton(int id, String offImage, String onImage, int x, int y, int width, int height, Action a) {
		this.ID = id; // TODO: disallow ID collisions
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.on = false;
		this.onClick = a;
		this.buttonStateImages[0] = ImageOps.loadImage(offImage);
		this.buttonStateImages[1] = ImageOps.loadImage(onImage);
		objects.add(this); // Hold reference to the button in objects
	}
	
	// Returns the button with the ID
	// Returns null if the ID is invalid
	public static ToggleButton getButtonByID(int id) {
		for(int i = 0; i < objects.size(); i++) {
			if(objects.get(i).ID == id) {
				return objects.get(i);
			}
		}
		return null;
	}
	
	public void render(Graphics g, ImageObserver o) {
		if(on) {
			g.drawImage(buttonStateImages[1], this.x, this.y, this.width, this.height, o);
		} else {
			g.drawImage(buttonStateImages[0], this.x, this.y, this.width, this.height, o);
		}
	}
	
	public void update() {
		if(
				!Frame.mouse.mouseDown
				&& Frame.mouse.lastPressX >= this.x
				&& Frame.mouse.lastPressX <= (this.x + this.width)
				&& Frame.mouse.lastPressY+Panel.MOUSE_Y_OFFSET >= this.y
				&& Frame.mouse.lastPressY+Panel.MOUSE_Y_OFFSET <= (this.y + this.height)) {
			for(int i = 0; i < objects.size(); i++) {
				if(objects.get(i).equals(this)) {
					objects.get(i).on = true;
					objects.get(i).onClick.onClick(); // Execute callback when the button is toggled on
				} else {
					objects.get(i).on = false;
				}
			}
		}
	}
	
	public static void setStateOffAll() {
		for(int i = 0; i < objects.size(); i++) {
			objects.get(i).on = false;
		}
	}
	
	// Updates all ToggleButton instances at once
	public static void updateAll() {
		for(int i = 0; i < objects.size(); i++) {
			objects.get(i).update();
		}
	}
	
	// Renders all ToggleButton instances at once
	public static void renderAll(Graphics g, ImageObserver o) {
		for(int i = 0; i < objects.size(); i++) {
			objects.get(i).render(g, o);
		}
	}
}
