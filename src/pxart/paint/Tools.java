package pxart.paint;

import java.awt.MouseInfo;
import java.awt.Color;

public class Tools {
	public static final int NONE=0, DRAG=1, SELECT=2, FILL=3, BRUSH=4, LINE=5, RECT=6, ELLIPSE=7;
	public static int tool = NONE;
	public static float[] mainColor = {0f, 1f, 1f};
	public static int transparency = 255;
	
	public static int getBrushX() {
		return (Mouse.getScreenX() - Panel.imageViewX) / Panel.imageViewScale;
	}
	
	public static int getBrushY() {
		return (Mouse.getScreenY() - Panel.imageViewY + Panel.MOUSE_Y_OFFSET) / Panel.imageViewScale;
	}
	
	public static void updateMainImage() {
		Panel.image = ImageOps.pixelArrayToImage(Panel.imageArray);
	}
	
	public static void setPixel(int x, int y) {
		if(x >= 0 && x < Panel.imageArray[0].length && y >= 0 && y < Panel.imageArray.length) {
			Panel.imageArray[y][x] = ImageOps.colorToArray(ImageOps.HSVtoColor(mainColor[0], mainColor[1], mainColor[2], transparency));
			updateMainImage();
		}
	}
	
	public static Color getMainHSVColorAsColor() {
		return ImageOps.HSVtoColor(mainColor[0], mainColor[1], mainColor[2], transparency);
	}
	
	// Dragging thread (for continuous dragging)
	private static boolean dragging = false;
	private static class DragThread extends Thread {
		public void run() {
			dragging = true;
			int distanceToMouseX = Mouse.getScreenX() - Panel.imageViewX;
			int distanceToMouseY = Mouse.getScreenY() - Panel.imageViewY;
			while(Frame.mouse.mouseDown) {
				Panel.imageViewX = Mouse.getScreenX() - distanceToMouseX;
				Panel.imageViewY = Mouse.getScreenY() - distanceToMouseY;
			}
			dragging = false;
		}
	}
	// Dragging
	public static void dragImage() {
		if(
				Frame.mouse.mouseDown
				&& Frame.mouse.lastPressX >= Panel.imageViewX
				&& Frame.mouse.lastPressX <= Panel.imageViewX + Panel.imageArray[0].length * Panel.imageViewScale
				&& Frame.mouse.lastPressY >= Panel.imageViewY
				&& Frame.mouse.lastPressY <= Panel.imageViewY + Panel.imageArray.length * Panel.imageViewScale) {
			if(!dragging) {
				DragThread dt = new DragThread();
				dt.start();
			}
		}
	}
}
