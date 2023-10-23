package pxart.paint;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;
import javax.swing.Timer;

import pxart.paint.ui.ColorPicker;
import pxart.paint.ui.HuePicker;
import pxart.paint.ui.ToggleButton;
import pxart.paint.ui.TransparencyPicker;

public class Panel extends JPanel implements ActionListener {
	static final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
	static final int SCREEN_WIDTH = (int)screenSize.getWidth();
	static final int SCREEN_HEIGHT = (int)screenSize.getHeight();
	Timer timer;
	
	// Image
	public static BufferedImage image = ImageOps.loadImage("C:\\Users\\doks\\Downloads\\test.png");
	public static int[][][] imageArray = ImageOps.imageToPixelArray(image);
	private static BufferedImage imageBackground = ImageOps.pixelArrayToImage(ImageOps.generateCheckerGrid(
			imageArray[0].length, imageArray.length, new Color(225, 225, 225), new Color(245, 245, 245)));
	
	public static int imageViewScale = SCREEN_HEIGHT / imageArray.length;
	public static int imageViewX = SCREEN_WIDTH/2 - imageArray[0].length * imageViewScale/2;
	public static int imageViewY = 0;
	public static final int MIN_ZOOM = 1;
	
	// UI variables
	public static int buttonSize = 70;
	public static final int MOUSE_Y_OFFSET = -25;
	
	// UI elements
	public static ColorPicker colorPicker = new ColorPicker(SCREEN_WIDTH - 265, 10, 255, 255);
	public static HuePicker huePicker = new HuePicker(SCREEN_WIDTH-365, 290, 355, 22);
	public static TransparencyPicker transparencyPicker = new TransparencyPicker(SCREEN_WIDTH-365, 327, 355, 22);
	
	private static BufferedImage transparencyPickerBackground = ImageOps.pixelArrayToImage(ImageOps.generateCheckerGrid(81, 5, new Color(230, 230, 230), Color.WHITE));
	private static BufferedImage fillButtonBackground = ImageOps.pixelArrayToImage(ImageOps.generateCheckerGrid(buttonSize/4, buttonSize/4, new Color(230, 230, 230), Color.WHITE));
	
	private BufferedImage grid100x255 = ImageOps.loadImage(System.getProperty("user.dir") + "/src/pxart/paint/images/ui/grid100x255.png");
	
	// Drag tool
	ToggleButton tool1 = new ToggleButton(
			1,
			System.getProperty("user.dir") + "/src/pxart/paint/images/ui/drag_mode.png",
			System.getProperty("user.dir") + "/src/pxart/paint/images/ui/drag_mode_on.png",
			10, 10, buttonSize, buttonSize,
			()->{
				Tools.tool = Tools.DRAG;
			});
	// Select tool
	ToggleButton tool2 = new ToggleButton(
			2,
			System.getProperty("user.dir") + "/src/pxart/paint/images/ui/select_mode.png",
			System.getProperty("user.dir") + "/src/pxart/paint/images/ui/select_mode_on.png",
			10, 10+buttonSize, buttonSize, buttonSize,
			()->{
				Tools.tool = Tools.SELECT;
			});
	// Fill tool
	ToggleButton tool3 = new ToggleButton(
			3,
			System.getProperty("user.dir") + "/src/pxart/paint/images/ui/fill_mode.png",
			System.getProperty("user.dir") + "/src/pxart/paint/images/ui/fill_mode_on.png",
			10, 10+buttonSize*2, buttonSize, buttonSize,
			()->{
				Tools.tool = Tools.FILL;
			});
	// Brush tool
	ToggleButton tool4 = new ToggleButton(
			3,
			System.getProperty("user.dir") + "/src/pxart/paint/images/ui/brush_mode.png",
			System.getProperty("user.dir") + "/src/pxart/paint/images/ui/brush_mode_on.png",
			10, 10+buttonSize*3, buttonSize, buttonSize,
			()->{
				Tools.tool = Tools.BRUSH;
			});
	
	// Panel
	public Panel() {
		this.setPreferredSize(new Dimension(SCREEN_WIDTH/2, SCREEN_HEIGHT/2));
		this.setBackground(Color.WHITE);
		this.setFocusable(true);
		this.addKeyListener(new MyKeyAdapter());
		image = ImageOps.pixelArrayToImage(imageArray);
		colorPicker.setHue(Tools.mainColor[0]);
		timer = new Timer(0, this);
		timer.start();
	}
	
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		g.drawImage(fillButtonBackground, 10, 10+buttonSize*2, buttonSize, buttonSize, this);
		g.drawImage(fillButtonBackground, 10, 10+buttonSize*3, buttonSize, buttonSize, this);
		
		//g.drawImage(imageBackground, imageViewX, imageViewY, imageViewScale*imageArray[0].length, imageViewScale*imageArray.length, this);
		//g.drawImage(image, imageViewX, imageViewY, imageViewScale*imageArray[0].length, imageViewScale*imageArray.length, this);
		
		draw(g);
		
		ToggleButton.renderAll(g, this);
		
		// Color picker
		g.drawImage(colorPicker.image, colorPicker.x, colorPicker.y, 255, 255, this);
		// Cursor
		g.setColor(Color.WHITE);
		g.fillOval(colorPicker.cursor.x-13, (colorPicker.cursor.y - 13) + MOUSE_Y_OFFSET, 26, 26);
		g.setColor(new Color(colorPicker.getSelectedColor().getRed(), colorPicker.getSelectedColor().getGreen(), colorPicker.getSelectedColor().getBlue(), 255));
		g.fillOval(colorPicker.cursor.x-10, (colorPicker.cursor.y - 10) + MOUSE_Y_OFFSET, 20, 20);
		// Color preview
		g.drawImage(grid100x255, colorPicker.x-100, colorPicker.y, 100, 255, this);
		g.setColor(ImageOps.HSVtoColor(Tools.mainColor[0], Tools.mainColor[1], Tools.mainColor[2], Tools.transparency));
		g.fillRect(colorPicker.x-100, colorPicker.y, 100, 255);
		
		// Hue picker
		g.drawImage(huePicker.image, huePicker.x, huePicker.y, this);
		// Cursor
		g.setColor(Color.WHITE);
		g.fillOval(huePicker.cursor.x-13, (huePicker.cursor.y - 13), 26, 26);
		g.setColor(ImageOps.HSVtoColor(Tools.mainColor[0], 1f, 1f, 255));
		g.fillOval(huePicker.cursor.x-10, (huePicker.cursor.y - 10), 20, 20);
		
		// Transparency picker
		g.drawImage(transparencyPickerBackground, transparencyPicker.x, transparencyPicker.y, 355, 22, this);
		g.drawImage(transparencyPicker.image, transparencyPicker.x, transparencyPicker.y, this);
		// Cursor
		g.setColor(Color.WHITE);
		g.fillOval(transparencyPicker.cursor.x-13, (transparencyPicker.cursor.y - 13), 26, 26);
		g.setColor(new Color(255-Tools.transparency, 255-Tools.transparency, 255-Tools.transparency));
		g.fillOval(transparencyPicker.cursor.x-10, (transparencyPicker.cursor.y - 10), 20, 20);
	}
	
	public void draw(Graphics g) {
		// Main image
		renderImage(g, imageArray, imageViewScale, imageViewX, imageViewY, true);

		// Coloring for buttons
		// Fill button
		g.setColor(ImageOps.HSVtoColor(Tools.mainColor[0], Tools.mainColor[1], Tools.mainColor[2], Tools.transparency));
		g.fillRect(10, 10+buttonSize*2, buttonSize, buttonSize);
		// Brush button
		g.setColor(ImageOps.HSVtoColor(Tools.mainColor[0], Tools.mainColor[1], Tools.mainColor[2], Tools.transparency));
		g.fillRect(10, 10+buttonSize*3, buttonSize, buttonSize);
		
		// Text
		// Color data description
		g.setColor(Color.BLACK);
		Color c = ImageOps.HSVtoColor(Tools.mainColor[0],Tools.mainColor[1], Tools.mainColor[2], Tools.transparency);
		g.setFont(new Font(null, Font.PLAIN, 15));
		g.drawString("RGBA (" + c.getRed() + ", " + c.getGreen() + ", " + c.getBlue() + ", " + c.getAlpha() + ")    HSV (" + (int)(Tools.mainColor[0]*360) +", " + (int)(Tools.mainColor[1]*100) + ", " + (int)(Tools.mainColor[2]*100) + ")", transparencyPicker.x, transparencyPicker.y+transparencyPicker.height+40);
	}
	
	// Renders the picture
	// TODO: This is too slow
	public void renderImage(Graphics g, int image[][][], int scale, int x, int y, boolean showTransparentPixels) {
		// Transparent background
		if(showTransparentPixels) {
			final int rs = 1;
			for(int i = 0; i < ((image.length/rs % 8 == 0) ? image.length/rs : image.length/rs+1); i++) {
				for(int j = 0; j < ((image[0].length/rs % 8 == 0) ? image[0].length/rs : image[0].length/rs+1); j++) {
					g.setColor((i+j)%2 != 0 ? new Color(245, 245, 245) : new Color(230, 230, 230));
					g.fillRect(j*rs*scale+x, i*rs*scale+y, j >= image[0].length/rs ? (image[0].length % 8) * scale : rs*scale, i >= image.length/rs ? (image.length % 8) * scale : rs*scale);
				}
			}
		}
		// Picture
		for(int i = 0; i < image.length; i++) {
			for(int j = 0; j < image[0].length; j++) {
				if(
						(j * scale)+x < SCREEN_WIDTH
						&& (j * scale)+x > -scale
						&& (i * scale)+y < SCREEN_HEIGHT
						&& (i * scale)+y > -scale) {
					g.setColor(new Color(image[i][j][1], image[i][j][2], image[i][j][3], image[i][j][0]));
					g.fillRect(j * scale + x, i * scale + y, scale, scale);
				}
			}
		}
	}
	
	// Zoom out/in picture
	public static void zoom(int s) {
		if(imageViewScale <= MIN_ZOOM && s < 0) {
			return;
		}
		imageViewScale += s;
	}
	
	//private static Point brushStartPoint, brushEndPoint;
	@Override
	public void actionPerformed(ActionEvent e) {
		if(Frame.mouse.mouseDown) {
			try {
				if(
						Tools.tool == Tools.BRUSH
						&& !colorPicker.mouseOver()
						&& !huePicker.mouseOver()
						&& !transparencyPicker.mouseOver()) {
					/*if(brushEndPoint == null)
						brushEndPoint = new Point(Tools.getBrushX(), Tools.getBrushY());
					else
						brushEndPoint.setLocation(Tools.getBrushX(), Tools.getBrushY());
					if(brushStartPoint == null)
						brushStartPoint = new Point(brushEndPoint);
					ImageOps.line(imageArray, Tools.getMainHSVColorAsColor(), brushStartPoint.x, brushStartPoint.y, brushEndPoint.x, brushEndPoint.y);
					brushStartPoint.setLocation(brushEndPoint);*/
					
					Tools.setPixel(Tools.getBrushX(), Tools.getBrushY());
				} else if(
						Tools.tool == Tools.FILL
						&& !colorPicker.mouseOver()
						&& !huePicker.mouseOver()
						&& !transparencyPicker.mouseOver()) {
					imageArray = ImageOps.fill(imageArray, Tools.getBrushX(), Tools.getBrushY(), ImageOps.HSVtoColor(Tools.mainColor[0], Tools.mainColor[1], Tools.mainColor[2], Tools.transparency));
				}
			}catch(Exception e_) {
				System.err.println(e_.getMessage());
			}
		}
		repaint();
	}

	public class MyKeyAdapter extends KeyAdapter {
		@Override
		public void keyPressed(KeyEvent e) {
			if(e.getKeyCode() >= 48 && e.getKeyCode() < 58) {
				Tools.tool = (int)e.getKeyCode() - 48;
			}
			switch(e.getKeyCode()) {
			case KeyEvent.VK_UP:
				if(Tools.tool > Tools.NONE+1) {
					Tools.tool--;
					ToggleButton.setStateOffAll();
					ToggleButton.getButtonByID(Tools.tool).on = true;
				}
				break;
			case KeyEvent.VK_DOWN:
				Tools.tool++;
				ToggleButton.setStateOffAll();
				ToggleButton.getButtonByID(Tools.tool).on = true;
				break;
			case KeyEvent.VK_S:
				image = ImageOps.pixelArrayToImage(imageArray);
				ImageOps.saveImage(image, "C:\\Users\\doks\\Downloads\\15e3798eebdbd7309deb385daf11a1c55ab39f9a.png");
			}
		}
	}
}
