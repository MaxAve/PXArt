package pxart.paint;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.sql.Array;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.awt.Point;

import javax.imageio.ImageIO;

public class ImageOps {
	public static BufferedImage loadImage(String filePath) {
        try {
            return ImageIO.read(new File(filePath));
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

	public static int[][][] imageToPixelArray(BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();
        int[][][] pixelArray = new int[height][width][4]; // RGBA components

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int pixel = image.getRGB(x, y);
                pixelArray[y][x][0] = (pixel >> 24) & 0xFF; // Alpha
                pixelArray[y][x][1] = (pixel >> 16) & 0xFF; // Red
                pixelArray[y][x][2] = (pixel >> 8) & 0xFF;  // Green
                pixelArray[y][x][3] = pixel & 0xFF;         // Blue
            }
        }

        return pixelArray;
    }

    public static BufferedImage pixelArrayToImage(int[][][] pixelArray) {
        int height = pixelArray.length;
        int width = pixelArray[0].length;

        BufferedImage newImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int alpha = pixelArray[y][x][0];
                int red = pixelArray[y][x][1];
                int green = pixelArray[y][x][2];
                int blue = pixelArray[y][x][3];
                int pixel = (alpha << 24) | (red << 16) | (green << 8) | blue;
                newImage.setRGB(x, y, pixel);
            }
        }

        return newImage;
    }
    
    // Resize image from its origin (0,0)
    public static int[][][] simpleResize(int[][][] oldImage, int newWidth, int newHeight) {
    	int[][][] newImage = new int[newHeight][newWidth][4];
    	for(int i = 0; i < newHeight; i++) {
    		for(int j = 0; j < newWidth; j++) {
    			if(i < oldImage.length && j < oldImage[0].length) {
    				newImage[i][j] = oldImage[i][j];
    			} else {
    				newImage[i][j] = new int[] {0, 0, 0, 0};
    			}
    		}
    	}
    	return newImage;
    }
    
    public static int getRightImageEdge(int[][][] image) {
    	int max = 0;
    	for(int i = 0; i < image.length; i++) {
    		for(int j = 0; j < image[0].length; j++) {
    			if(image[i][j][0] > 0 && j > max) {
    				max = j;
    			}
    		}
    	}
    	if(max == 0)
    		return 0;
    	return max + 1;
    }
    
    public static int getLowerImageEdge(int[][][] image) {
    	int max = 0;
    	for(int i = 0; i < image.length; i++) {
    		for(int j = 0; j < image[0].length; j++) {
    			if(image[i][j][0] > 0 && i > max) {
    				max = i;
    			}
    		}
    	}
    	if(max == 0)
    		return 0;
    	return max + 1;
    }
    
    public static int getLeftImageEdge(int[][][] image) {
    	int min = 2147483647;
    	for(int i = 0; i < image.length; i++) {
    		for(int j = 0; j < image[0].length; j++) {
    			if(image[i][j][0] > 0 && j < min) {
    				min = j;
    			}
    		}
    	}
    	if(min == 2147483647)
    		return -1;
    	return min;
    }
    
    public static int getUpperImageEdge(int[][][] image) {
    	int min = 2147483647;
    	for(int i = 0; i < image.length; i++) {
    		for(int j = 0; j < image[0].length; j++) {
    			if(image[i][j][0] > 0 && i < min) {
    				min = i;
    			}
    		}
    	}
    	if(min == 2147483647)
    		return -1;
    	return min;
    }
    
    // Centers an image
    public static int[][][] center(int[][][] oldImage) {
    	int[][][] newImage = new int[oldImage.length][oldImage[0].length][4];
    	int shiftX = ((oldImage[0].length - getRightImageEdge(oldImage) + getLeftImageEdge(oldImage)) / 2) - getLeftImageEdge(oldImage);
    	int shiftY = ((oldImage[0].length - getLowerImageEdge(oldImage) + getUpperImageEdge(oldImage)) / 2) - getUpperImageEdge(oldImage);
    	for(int i = 0; i < oldImage.length; i++) {
    		for(int j = 0; j < oldImage[0].length; j++) {
    			if(oldImage[i][j][0] > 0) {
    				newImage[i+shiftY][j+shiftX] = oldImage[i][j];
    			}
    		}
    	}
    	return newImage;
    }
    
    // Shifts an image
    public static int[][][] shift(int[][][] image, int x, int y) {
    	int[][][] newImage = new int[image.length][image[0].length][4];
    	for(int i = 0; i < image.length; i++) {
    		for(int j = 0; j < image[0].length; j++) {
    			if(i+y >= 0 && j+x >= 0 && i+y < newImage.length && j+x < newImage[0].length) {
    				newImage[i+y][j+x] = image[i][j];
    			}
    		}
    	}
    	return newImage;
    }

	// Fill tool
	public static int[][][] fill(int[][][] image, int x, int y, Color c) {
    	int[][][] newImage = image.clone();
    	ArrayList<Point> pointsToFill = new ArrayList<Point>();
		pointsToFill.add(new Point(x, y));
		while(pointsToFill.size() > 0) {
			for(int i = 0; i < pointsToFill.size(); i++) {
				ArrayList<Point> newPointsToFill = new ArrayList<Point>();
				try {
					newImage[(int)pointsToFill.get(i).getY()][(int)pointsToFill.get(i).getX()] = new int[]{c.getAlpha(), c.getRed(), c.getGreen(), c.getBlue()};
					if(
							(int)pointsToFill.get(i).getX() < image[0].length
							&& image[(int)pointsToFill.get(i).getY()][(int)pointsToFill.get(i).getX()+1][0] == image[y][x][0]
							&& image[(int)pointsToFill.get(i).getY()][(int)pointsToFill.get(i).getX()+1][0] == image[y][x][1]
							//&& image[(int)pointsToFill.get(i).getX()+1][(int)pointsToFill.get(i).getY()][0] == image[(int)pointsToFill.get(i).getX()][(int)pointsToFill.get(i).getY()][2]
							//&& image[(int)pointsToFill.get(i).getX()+1][(int)pointsToFill.get(i).getY()][0] == image[(int)pointsToFill.get(i).getX()][(int)pointsToFill.get(i).getY()][3]
						) {
						newPointsToFill.add(new Point((int)pointsToFill.get(i).getX()+1, (int)pointsToFill.get(i).getY()));
					}
				} catch(Exception e) {
					// shut up
				}
				pointsToFill = newPointsToFill;
			}
		}
    	return newImage;
    }
    
    // Converts HSV to a Color object
    public static Color HSVtoColor(float h, float s, float v, int transparency) {
    	int rgb = Color.HSBtoRGB(h, s, v);
		int r = (rgb >> 16) & 0xFF;
		int g = (rgb >> 8) & 0xFF;
		int b = rgb & 0xFF;
		return new Color(r, g, b, transparency);
    }
    
    // Generates a color picker image (HSV)
    public static int[][][] generateColorPicker(float hue) {
    	int[][][] image = new int[255][255][4];
    	for(int i = 0; i < image.length; i++) {
    		for(int j = 0; j < image[0].length; j++) {
    			int rgb = Color.HSBtoRGB(hue, (float)j / 255f, 1f - ((float)i / 255f));
    			int r = (rgb >> 16) & 0xFF;
    			int g = (rgb >> 8) & 0xFF;
    			int b = rgb & 0xFF;
    			image[i][j] = new int[] {255, r, g, b};
    		}
    	}
    	return image;
    }
    
    // Generates color spectrum (hue)
    public static int[][][] generateColorSpectrum(int width, int height) {
    	int[][][] image = new int[height][width][4];
    	for(int i = 0; i < image.length; i++) {
    		for(int j = 0; j < image[0].length; j++) {
    			int rgb = Color.HSBtoRGB((float)j/(float)width, 1f, 1f);
    			int r = (rgb >> 16) & 0xFF;
    			int g = (rgb >> 8) & 0xFF;
    			int b = rgb & 0xFF;
    			image[i][j] = new int[] {255, r, g, b};
    		}
    	}
    	return image;
    }
    
    // Generates a gradient (transparency)
    public static int[][][] generateTransparencyGradient(int width, int height) {
    	int[][][] image = new int[height][width][4];
    	for(int i = 0; i < image.length; i++) {
    		for(int j = 0; j < image[0].length; j++) {
    			image[i][j] = new int[] {(int)((float)j/width*255), 0, 0, 0};
    		}
    	}
    	return image;
    }
    
    // Convert a Color object into an array
    public static int[] colorToArray(Color c) {
    	return new int[] {c.getAlpha(), c.getRed(), c.getGreen(), c.getBlue()};
    }
    
    // Generates a checker grid with 2 colors
    public static int[][][] generateCheckerGrid(int width, int height, Color c1, Color c2) {
    	int[][][] image = new int[height][width][4];
    	for(int i = 0; i < height; i++) {
    		for(int j = 0; j < width; j++) {
    			image[i][j] = (i+j)%2 == 0 ? colorToArray(c1) : colorToArray(c2);
    		}
    	}
    	return image;
    }
    
    // Draws a line on an image
    public static int[][][] line(int[][][] image, Color c, int x1, int y1, int x2, int y2) {
    	if(x1 < 0)
    		x1 = 0;
    	if(x1 >= image[0].length)
    		x1 = image[0].length;
    	if(x2 < 0)
    		x2 = 0;
    	if(x2 >= image[0].length)
    		x2 = image[0].length;
    	if(y1 < 0)
    		y1 = 0;
    	if(y1 >= image[0].length)
    		y1 = image[0].length;
    	if(y2 < 0)
    		y2 = 0;
    	if(y2 >= image.length)
    		y2 = image.length;
    	int[][][] newImage = image;
    	if(x1 == x2 && y1 == y2) {
    		newImage[y1][x1] = colorToArray(c);
    		return newImage;
    	}
    	if(x1 == x2) {
    		if(y2 > y1) {
    			for(int i = y1; i <= y2; i++) {
    				newImage[x1][i] = colorToArray(c);
    			}
    		} else {
    			for(int i = y2; i <= y1; i++) {
    				newImage[x1][i] = colorToArray(c);
    			}
    		}
    		return newImage;
    	}
    	double y = 0.0;
    	double m = ((double)y2-(double)y1)/Math.abs((double)x1-(double)x2);
    	if(x2>x1) {
    		for(int i = x1; i <= x2; i++) {
    			y += m;
    			newImage[y1+(int)y][i] = colorToArray(c);
    		}
    	} else {
    		for(int i = x2; i <= x1; i++) {
    			y += m;
    			newImage[y1+(int)y][i] = colorToArray(c);
    		}
    	}
    	return newImage;
    }
    
    // Save image
    public static void saveImage(BufferedImage image, String filePath) {
        try {
            ImageIO.write(image, "png", new File(filePath));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
