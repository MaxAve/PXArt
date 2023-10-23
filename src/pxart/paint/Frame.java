package pxart.paint;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import javax.swing.JFrame;
import javax.swing.JTextField;

import pxart.paint.ui.ToggleButton;

@SuppressWarnings("serial")
public class Frame extends JFrame {
	public static Mouse mouse = new Mouse();

	public Frame() {
		this.add(new Panel());
		this.setTitle("PXArt");
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setResizable(true);
		this.pack();
		this.setVisible(true);
		this.setLocationRelativeTo(null);
		this.setSize(Panel.SCREEN_WIDTH, Panel.SCREEN_HEIGHT);
		this.setLocation(0, 0);
		this.setExtendedState(JFrame.MAXIMIZED_BOTH);
		this.addMouseWheelListener(new MouseWheelListener() {
			@Override
			public void mouseWheelMoved(MouseWheelEvent e) {
				Panel.zoom(-e.getWheelRotation());
				if(-e.getWheelRotation() > 0) {
					Panel.imageViewX -= Panel.imageArray[0].length/2;
					Panel.imageViewY -= Panel.imageArray.length/2;
				} else if(-e.getWheelRotation() < 0 && Panel.imageViewScale > Panel.MIN_ZOOM) {
					Panel.imageViewX += Panel.imageArray[0].length/2;
					Panel.imageViewY += Panel.imageArray.length/2;
				}
			}
		});
		this.addMouseListener(new MouseListener() {
		    @Override
		    public void mouseClicked(MouseEvent e) {}

            @Override
            public void mousePressed(MouseEvent e) {
            	mouse.update(e, true);
            	ToggleButton.updateAll();
            	Panel.colorPicker.dragColorCursorOnMouseClick();
            	Panel.huePicker.dragColorCursorOnMouseClick();
            	Panel.transparencyPicker.dragColorCursorOnMouseClick();
            	if(Tools.tool == Tools.DRAG)
            		Tools.dragImage();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
            	mouse.update(e, false);
            	ToggleButton.updateAll();
            }

            @Override
            public void mouseEntered(MouseEvent e) {
            	mouse.inFrame = true;
            }

            @Override
            public void mouseExited(MouseEvent e) {
            	mouse.inFrame = false;
            }
		});
	}
}
