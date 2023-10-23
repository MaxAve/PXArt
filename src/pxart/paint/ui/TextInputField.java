package pxart.paint.ui;

public class TextInputField extends GUIComponent {
	boolean selected;
	
	public TextInputField(int x, int y, int w, int h) {
		super(x, y, w, h);
		this.selected = false;
	}
}
