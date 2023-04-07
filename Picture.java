import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.UIManager;

/**
 * A class that represents a picture made up of a rectangle of {@link Pixel}s
 */
public class Picture {

	/** The 2D array of pixels that comprise this picture */
	private Pixel[][] pixels;

	/**
	 * Creates a Picture from an image file in the "images" directory
	 * 
	 * @param picture The name of the file to load
	 */
	public Picture(String picture) {
		File file = new File("./images/" + picture);
		BufferedImage image;
		if (!file.exists())
			throw new RuntimeException("No picture at the location " + file.getPath() + "!");
		try {
			image = ImageIO.read(file);
		} catch (IOException e) {
			throw new RuntimeException(e.getMessage());
		}
		pixels = new Pixel[image.getHeight()][image.getWidth()];
		for (int y = 0; y < pixels.length; y++) {
			for (int x = 0; x < pixels[y].length; x++) {
				int rgb = image.getRGB(x, y);
				/*
				 * For the curious - BufferedImage saves an image's RGB info into a hexadecimal
				 * integer The below extracts the individual values using bit-shifting and
				 * bit-wise ANDing with all 1's
				 */
				pixels[y][x] = new Pixel((rgb >> 16) & 0xff, (rgb >> 8) & 0xff, rgb & 0xff);
			}
		}
	}

	/**
	 * Creates a solid-color Picture of a given color, width, and height
	 * 
	 * @param red    The red value of the color
	 * @param green  The green value of the color
	 * @param blue   The blue value of the color
	 * @param height The height of the Picture
	 * @param width  The width of the Picture
	 */
	public Picture(int red, int green, int blue, int height, int width) {
		pixels = new Pixel[height][width];
		for (int y = 0; y < pixels.length; y++) {
			for (int x = 0; x < pixels[y].length; x++) {
				pixels[y][x] = new Pixel(red, green, blue);
			}
		}
	}

	/**
	 * Creates a solid white Picture of a given width and height
	 * 
	 * @param color  The {@link Color} of the Picture
	 * @param height The height of the Picture
	 * @param width  The width of the Picture
	 */
	public Picture(int height, int width) {
		this(Color.WHITE, height, width);
	}

	/**
	 * Creates a solid-color Picture of a given color, width, and height
	 * 
	 * @param color  The {@link Color} of the Picture
	 * @param width  The width of the Picture
	 * @param height The height of the Picture
	 */
	public Picture(Color color, int height, int width) {
		this(color.getRed(), color.getGreen(), color.getBlue(), height, width);
	}

	/**
	 * Creates a Picture based off of an existing {@link Pixel} 2D array
	 * 
	 * @param pixels A rectangular 2D array of {@link Pixel}s. Must be at least 1x1
	 */
	public Picture(Pixel[][] pixels) {
		if (pixels.length == 0 || pixels[0].length == 0)
			throw new RuntimeException("Can't have an empty image!");
		int width = pixels[0].length;
		for (int i = 0; i < pixels.length; i++)
			if (pixels[i].length != width)
				throw new RuntimeException("Pictures must be rectangles. pixels[0].length!=pixels[" + i + "].length!");
		this.pixels = new Pixel[pixels.length][width];
		for (int i = 0; i < pixels.length; i++) {
			for (int j = 0; j < pixels[i].length; j++) {
				this.pixels[i][j] = new Pixel(pixels[i][j].getColor());
			}
		}
	}

	/**
	 * Creates a Picture based off of an existing Picture
	 * 
	 * @param picture The Picture to copy
	 */
	public Picture(Picture picture) {
		this(picture.pixels);
	}

	/**
	 * Gets the width of the Picture
	 * 
	 * @return The width of the Picture
	 */
	public int getWidth() {
		return pixels[0].length;
	}

	/**
	 * Gets the height of the Picture
	 * 
	 * @return The height of the Picture
	 */
	public int getHeight() {
		return pixels.length;
	}

	/**
	 * Gets the {@link Pixel} at a given coordinate
	 * 
	 * @param x The x location of the {@link Pixel}
	 * @param y The y location of the {@link Pixel}
	 * @return The {@link Pixel} at the given location
	 */
	public Pixel getPixel(int x, int y) {
		if (x >= getWidth() || y >= getHeight() || x < 0 || y < 0)
			throw new RuntimeException("No pixel at (" + x + ", " + y + ")");
		return pixels[y][x];
	}

	/**
	 * Sets the {@link Pixel} at a given coordinate
	 * 
	 * @param x     The x location of the {@link Pixel}
	 * @param y     The y location of the {@link Pixel}
	 * @param pixel The new {@link Pixel}
	 */
	public void setPixel(int x, int y, Pixel pixel) {
		if (x >= getWidth() || y >= getHeight() || x < 0 || y < 0)
			throw new RuntimeException("No pixel at (" + x + ", " + y + ")");
		if (pixel == null)
			throw new NullPointerException("Pixel is null"); // guard is required because pixel's value isn't used in
																// this method
		pixels[y][x] = pixel;
	}

	/**
	 * Opens a {@link PictureViewer} to view this Picture
	 * 
	 * @return the {@link PictureViewer} viewing the Picture
	 */
	public PictureViewer view() {
		return new PictureViewer(this);
	}

	/**
	 * Save the image on disk as a JPEG Call programmatically on a Picture object,
	 * it will prompt you to choose a save location In the save dialogue window,
	 * specify the file AND extension (e.g. "lilies.jpg") Extension must be .jpg as
	 * ImageIO is expecting to write a jpeg
	 */
	public void save() {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			e.printStackTrace();
		}

		BufferedImage image = new BufferedImage(this.pixels[0].length, this.pixels.length, BufferedImage.TYPE_INT_RGB);

		for (int r = 0; r < this.pixels.length; r++)
			for (int c = 0; c < this.pixels[0].length; c++)
				image.setRGB(c, r, this.pixels[r][c].getColor().getRGB());

		// user's Desktop will be default directory location
		JFileChooser chooser = new JFileChooser(System.getProperty("user.home") + "/Desktop");

		chooser.setDialogTitle("Select picture save location / file name");

		File file = null;

		int choice = chooser.showSaveDialog(null);

		if (choice == JFileChooser.APPROVE_OPTION)
			file = chooser.getSelectedFile();

		// append extension if user didn't read save instructions
		if (!file.getName().endsWith(".jpg") && !file.getName().endsWith(".JPG") && !file.getName().endsWith(".jpeg")
				&& !file.getName().endsWith(".JPEG"))
			file = new File(file.getAbsolutePath() + ".jpg");

		try {
			ImageIO.write(image, "jpg", file);
			System.out.println("File created at " + file.getAbsolutePath());
		} catch (IOException e) {
			System.out.println("Can't write to location: " + file.toString());
		} catch (NullPointerException | IllegalArgumentException e) {
			System.out.println("Invalid directory choice");
		}
	}

	/**
	 * return a copy of the reference to the 2D array of pixels that comprise this
	 * picture
	 */
	public Pixel[][] getPixels() {
		return pixels;
	}

	/********************************************************
	 *************** STUDENT METHODS BELOW ******************
	 ********************************************************/

	/** remove all blue tint from a picture */
	public void zeroBlue() {
		for (int i = 0; i < pixels.length; i++) {
			for (int j = 0; j < pixels[i].length; j++) {
				if (pixels[i][j].getBlue() > 0) {
					pixels[i][j].setColor(pixels[i][j].getRed(), pixels[i][j].getGreen(), 0);
				} else {
					continue;
				}
			}
		}
	}

	/** remove everything BUT blue tint from a picture */
	public void keepOnlyBlue() {
		for (int i = 0; i < pixels.length; i++) {
			for (int j = 0; j < pixels[i].length; j++) {
				if (pixels[i][j].getBlue() > 0) {
					pixels[i][j].setColor(0, 0, pixels[i][j].getBlue());
				} else {
					continue;
				}
			}
		}
	}

	/** invert a picture's colors */
	public void negate() {
		for (int i = 0; i < pixels.length; i++) {
			for (int j = 0; j < pixels[i].length; j++) {

				pixels[i][j].setColor(255 - pixels[i][j].getRed(), 255 - pixels[i][j].getGreen(),
						255 - pixels[i][j].getBlue());
			}
		}
	}

	/** simulate the over-exposure of a picture in film processing */
	public void solarize(int threshold) {
		int t = threshold;

		for (int i = 0; i < pixels.length; i++) {
			for (int j = 0; j < pixels[i].length; j++) {
				if (pixels[i][j].getRed() < t) {
					pixels[i][j].setRed(255 - pixels[i][j].getRed());
				}
				if (pixels[i][j].getGreen() < t) {
					pixels[i][j].setGreen(255 - pixels[i][j].getGreen());
				}
				if (pixels[i][j].getBlue() < t) {
					pixels[i][j].setBlue(255 - pixels[i][j].getBlue());
				}
			}
		}
	}

	/** convert an image to grayscale */
	public void grayscale() {
		int avg = 0;

		for (int i = 0; i < pixels.length; i++) {
			for (int j = 0; j < pixels[i].length; j++) {
				avg = (pixels[i][j].getRed() + pixels[i][j].getGreen() + pixels[i][j].getBlue()) / 3;

				pixels[i][j].setColor(avg, avg, avg);
			}
		}

	}

	/** change the tint of the picture by the supplied coefficients */
	public void tint(double red, double blue, double green) {
		int r = 0;
		int g = 0;
		int b = 0;

		for (int i = 0; i < pixels.length; i++) {
			for (int j = 0; j < pixels[i].length; j++) {
				r = (int) (pixels[i][j].getRed() * red);
				g = (int) (pixels[i][j].getGreen() * green);
				b = (int) (pixels[i][j].getBlue() * blue);
				if (r > 255 || g > 255 || b > 255) {
					r = 255;
					g = 255;
					b = 255;
				}
				pixels[i][j].setColor(r, g, b);
			}
		}
	}

	/**
	 * reduces the number of colors in an image to create a "graphic poster" effect
	 */
	public void posterize(int span) {
		int r = 0;
		int g = 0;
		int b = 0;

		for (int i = 0; i < pixels.length; i++) {
			for (int j = 0; j < pixels[i].length; j++) {
				r = pixels[i][j].getRed() / span;
				r *= span;

				g = pixels[i][j].getGreen() / span;
				g *= span;

				b = pixels[i][j].getBlue() / span;
				b *= span;

				pixels[i][j].setColor(r, g, b);
			}
		}
	}

	/** mirror an image about a vertical midline, left to right */
	public void mirrorVertical() {
		Pixel leftPixel = null;
		Pixel rightPixel = null;

		int width = pixels[0].length;

		for (int r = 0; r < pixels.length; r++) {
			for (int c = 0; c < width / 2; c++) {
				leftPixel = pixels[r][c];
				rightPixel = pixels[r][(width - 1) - c];

				rightPixel.setColor(leftPixel.getColor());
			}
		}
	}

	/** mirror about a vertical midline, right to left */
	public void mirrorRightToLeft() {
		Pixel leftPixel = null;
		Pixel rightPixel = null;

		int width = pixels[0].length;

		for (int r = 0; r < pixels.length; r++) {
			for (int c = width / 2; c < width; c++) {
				leftPixel = pixels[r][c];
				rightPixel = pixels[r][(width - 1) - c];

				rightPixel.setColor(leftPixel.getColor());
			}
		}
	}

	/** mirror about a horizontal midline, top to bottom */
	public void mirrorHorizontal() {
		Pixel topPixel = null;
		Pixel bottomPixel = null;
		int width = pixels[0].length;

		for (int row = 0; row < pixels.length / 2; row++) {
			for (int col = 0; col < width; col++) {
				bottomPixel = pixels[(pixels.length - 1) - row][col];
				topPixel = pixels[row][col];

				bottomPixel.setColor(topPixel.getColor());
			}
		}
	}

	/** flip an image upside down about its bottom edge */
	public void verticalFlip() {
		int height = pixels.length;
		int width = pixels[0].length;

		for (int row = 0; row < height / 2; row++) {
			for (int col = 0; col < width; col++) {
				Pixel p = pixels[row][col];

				pixels[row][col] = pixels[height - row - 1][col];
				pixels[height - row - 1][col] = p;
			}
		}
	}

	/** fix roof on greek temple */
	public void fixRoof() {
		Pixel leftPixel = null;
		Pixel rightPixel = null;

		int width = pixels[0].length;

		for (int r = 0; r < pixels.length; r++) {
			for (int c = 0; c < width / 2; c++) {
				if (pixels[r][c].getColor() == new Color(234, 214, 189)) {
					break;
				} else {
					leftPixel = pixels[r][c];
					rightPixel = pixels[r][(width - 1) - c];
				}
				rightPixel.setColor(leftPixel.getColor());

			}
		}
	}

	/** detect and mark edges in an image */
	public void edgeDetection(int dist) {

		Color white = new Color(255, 255, 255);
		Color black = new Color(0, 0, 0);

		Pixel Pixel1 = null;
		Pixel Pixel2 = null;

		for (int y = 0; y < pixels.length - 1; y++) {
			for (int x = 0; x < pixels[y].length; x++) {
				Pixel1 = this.getPixel(x, y);
				Pixel2 = this.getPixel(x, y + 1);

				Color temp = Pixel2.getColor();

				if (Pixel1.colorDistance(temp) < dist)
					Pixel1.setColor(white);
				else
					Pixel1.setColor(black);
			}
		}
	}

	/**
	 * copy another picture's pixels into this picture, if a color is within dist of
	 * param Color
	 */
	public void chromakey(Picture other, Color color, int dist) {
		System.out.println(getHeight());
		System.out.println(getWidth());

		for (int r = 0; r < pixels.length; r++) {
			for (int c = 0; c < pixels[r].length; c++) {
				if (pixels[r][c].colorDistance(color) <= dist)
					pixels[r][c].setColor(other.getPixel(c, r).getColor());
			}
		}
	}

	/** steganography encode (hide the message in msg in this picture) */
	public void encode(Picture msg) {
		for (int i = 0; i < pixels.length; i++) {
			for (int j = 0; j < pixels[i].length; j++) {
				if (pixels[i][j].getRed() % 2 == 1) {
					pixels[i][j].setRed(pixels[i][j].getRed() - 1);
				}
			}
		}

		for (int i = 0; i < pixels.length; i++) {
			for (int j = 0; j < pixels[i].length; j++) {
				if (msg.getPixel(j, i).colorDistance(Color.BLACK) <= 50) {
					pixels[i][j].setRed(pixels[i][j].getRed() + 1);
				}
			}
		}
	}

	/**
	 * steganography decode (return a new Picture containing the message hidden in
	 * this picture)
	 */
	public Picture decode() {
		Picture dec = new Picture(getHeight(), getWidth());

		for (int i = 0; i < pixels.length; i++) {
			for (int j = 0; j < pixels[i].length; j++) {
				if (pixels[i][j].getRed() % 2 == 1) {
					dec.getPixel(j, i).setColor(Color.BLACK);
				}
			}
		}

		return dec; // REPLACE
	}

	/** perform a simple blur using the colors of neighboring pixels */
	public Picture simpleBlur() {
		Picture ret = new Picture(getHeight(), getWidth());

		for (int r = 0; r < pixels.length; r++) {
			for (int c = 0; c < pixels[r].length - 1; c++) {
				int count = 0;
				int avgR = 0;
				int avgB = 0;
				int avgG = 0;
				
				if(r - 1 >= 0) {
					avgR += pixels[r-1][c].getRed();
					avgG += pixels[r-1][c].getGreen();
					avgB += pixels[r-1][c].getBlue();
					count++;
				}
				if(c - 1 >= 0) {
					avgR += pixels[r][c - 1].getRed();
					avgG += pixels[r][c - 1].getGreen();
					avgB += pixels[r][c - 1].getBlue();
					count++;
				}
				if(r + 1 <= pixels.length - 1) {
					avgR += pixels[r + 1][c].getRed();
					avgG += pixels[r + 1][c].getGreen();
					avgB += pixels[r + 1][c].getBlue();
					count++;
				}
				if(c + 1 <= pixels[0].length - 1) {
					avgR += pixels[r][c + 1].getRed();
					avgG += pixels[r][c + 1].getGreen();
					avgB += pixels[r][c + 1].getBlue();
					count++;
				}
				ret.getPixel(c, r).setRed(avgR/ count);
				ret.getPixel(c, r).setGreen(avgG/ count);
				ret.getPixel(c, r).setBlue(avgB/ count);
			}
		}
		return ret; // REPLACE
	}

	/** perform a blur using the colors of pixels within radius of current pixel */
	public Picture blur(int radius) {
		Picture ret = new Picture(getHeight(), getWidth());

		for (int r = 0; r < pixels.length; r++) {
			for (int c = 0; c < pixels[r].length; c++) {
				int count = 1;
				int avgR = 0;
				int avgG = 0;
				int avgB = 0;
				avgR += pixels[r][c].getRed();
				avgG += pixels[r][c].getGreen();
				avgB += pixels[r][c].getBlue();
				for(int j = 1; j <= radius; j++) {
					if(r - j >= 0) {
						avgR += pixels[r - j][c].getRed();
						avgG += pixels[r - j][c].getGreen();
						avgB += pixels[r - j][c].getBlue();
						count++;
					}
					if(c - j >= 0) {
						avgR += pixels[r][c - j].getRed();
						avgG += pixels[r][c - j].getGreen();
						avgB += pixels[r][c - j].getBlue();
						count++;
					}
					if(r - j >= 0) {
						avgR += pixels[r - j][c].getRed();
						avgG += pixels[r - j][c].getGreen();
						avgB += pixels[r - j][c].getBlue();
						count++;
					}
					if(r + j <= pixels.length - 1) {
						avgR += pixels[r + j][c].getRed();
						avgG += pixels[r + j][c].getGreen();
						avgB += pixels[r + j][c].getBlue();
						count++;
					}
					if(c + j <= pixels[0].length - 1) {
						avgR += pixels[r][c + j].getRed();
						avgG += pixels[r][c + j].getGreen();
						avgB += pixels[r][c + j].getBlue();
						count++;
					}
					if(r - j >= 0 && c - j >= 0) {
						avgR += pixels[r - j][c - j].getRed();
						avgG += pixels[r - j][c - j].getGreen();
						avgB += pixels[r - j][c - j].getBlue();
						count++;
					}
					if(r - j >= 0 && c + j <= pixels.length - 1) {
						avgR += pixels[r - j][c + j].getRed();
						avgG += pixels[r - j][c + j].getGreen();
						avgB += pixels[r - j][c + j].getBlue();
						count++;
					}
					if(r + j <= pixels.length - 1 && c - j >= 0) {
						avgR += pixels[r + j][c - j].getRed();
						avgG += pixels[r + j][c - j].getGreen();
						avgB += pixels[r + j][c - j].getBlue();
						count++;
					}
					if(r + j <= pixels.length - 1 && c + j <= pixels[0].length - 1) {
						avgR += pixels[r + j][c + j].getRed();
						avgG += pixels[r + j][c + j].getGreen();
						avgB += pixels[r + j][c + j].getBlue();
						count++;
					}
				}
				ret.getPixel(c, r).setRed(avgR/count);
				ret.getPixel(c, r).setGreen(avgG/count);
				ret.getPixel(c, r).setBlue(avgB/count);
			}
		}

		return ret; // REPLACE

	}

	/**
	 * Simulate looking at an image through a pane of glass
	 * 
	 * @param dist the "radius" of the neighboring pixels to use
	 * @return a new Picture with the glass filter applied
	 */
	public Picture glassFilter(int dist) {
		Picture temp = new Picture(getHeight(), getWidth());
		for (int i = 0; i < pixels.length; i++) {
			for (int j = 0; j < pixels[i].length; j++) {
				int xPos = i;
				int yPos = j;
				Random rand = new Random();
				int xPos1 = rand.nextInt(dist * 2 + 1) + xPos - dist;
				int yPos1 = rand.nextInt(dist * 2 + 1) + yPos - dist;
				
				if (xPos1 < 0) {
					xPos1 = pixels.length + xPos1;
				} else if (xPos1 >= pixels.length) {
					xPos1 = xPos1 - pixels.length;
				}
				if (yPos1 < 0) {
					yPos1 = pixels[0].length + yPos1;
				} else if (yPos1 >= pixels[0].length) {
					yPos1 = yPos1 - pixels[0].length;
				}
				temp.getPixel(yPos, xPos).setColor(pixels[xPos1][yPos1].getColor());
			}
		}

		return temp; // REPLACE
	}
}
