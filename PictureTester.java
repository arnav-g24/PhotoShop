import java.awt.*;

public class PictureTester {
	public static void main(String[] args) {
		// construct a Picture object from a jpg image on disk
		Picture beach = new Picture("beach.jpg");
		Picture koala = new Picture("koala.jpg");
		Picture lily = new Picture("waterlilies.jpg"); 
		Picture gorge = new Picture("gorge.jpg");
		Picture moto = new Picture("redMotorcycle.jpg");
		Picture temple = new Picture("temple.jpg");
		Picture swan = new Picture("swan.jpg");

		beach.view();
		beach.zeroBlue();
		beach.view();
		
//		beach.view();
//		beach.keepOnlyBlue();
//		beach.view();
	
//		koala.view();
//		koala.negate();
//		koala.view();
		
//		lily.view();
//		lily.solarize(127);
//		lily.view();
		
//		gorge.view();
//		gorge.grayscale();
//		gorge.view();
		
//		beach.view();
//		beach.tint(1.25, 0.75, 1);
//		beach.view();
//		
//		beach.view();
//		beach.posterize(63);
//		beach.view();
//		
//		moto.view();
//		moto.mirrorRightToLeft();
//		moto.view();
//		
//		moto.view();
//		moto.mirrorHorizontal();
//		moto.view();
//		
//		lily.view();
//		lily.verticalFlip();
//		lily.view();
//		
//		temple.view();
//		temple.fixRoof();
//		temple.view();
//		
//		swan.view();
//		swan.edgeDetection(25);
//		swan.view();
//		
//		testChromakey();
//		
//		testSteganography();
//		
//		koala.view();
//		koala.simpleBlur().view();
//		
//		lily.view();
//		lily.blur(5).view();
//		
//		lily.view();
//		lily.glassFilter(5).view();
		
	}

	/**
	 * this method is static, you don't need to call it on an object (just
	 * "testChromekey()")
	 */
	public static void testChromakey() {
		Picture one = new Picture("blue-mark.jpg");
		Picture two = new Picture("moon-surface.jpg");

		one.view(); // show original mustache guy picture
		two.view(); // show the untouched moon's surface pic

		one.chromakey(two, new Color(10, 40, 75), 60); // replace this color if within 60

		one.view();
	}

	/**
	 * this method is static, you don't need to call it on an object (just
	 * "testSteganography()")
	 */
	public static void testSteganography() {
		Picture msg = new Picture("msg.jpg");
		Picture beach = new Picture("beach.jpg");

		beach.encode(msg); // hide message in beach picture
		beach.view(); // beach w/ hidden message inside, shouldn't look different

		beach.decode().view(); // see the hidden message in the beach picture
	}
}
