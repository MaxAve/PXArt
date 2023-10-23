package pxart.paint.ui;

import pxart.paint.ImageOps;
import pxart.paint.Tools;

public class TransparencyPicker extends HuePicker{
	public TransparencyPicker(int x, int y, int w, int h) {
		super(x, y, w, h);
		this.imageData = ImageOps.generateTransparencyGradient(this.width, this.height);
		this.image = ImageOps.pixelArrayToImage(this.imageData);
		this.cursor.x = this.x + this.width;
	}
	
	@Override
	public void updateMainColor() {
		Tools.transparency = (int)((float)cursor.getXRelative() / (float)width * 255f);
		if(Tools.transparency > 255)
			Tools.transparency = 255;
		if(Tools.transparency < 0)
			Tools.transparency = 0;
	}
}
