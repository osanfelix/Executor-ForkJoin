
package executor.forkjoin;

import static executor.forkjoin.ForkJoinExemple.imgDst;
import java.awt.Color;
import java.awt.Image;
import java.awt.image.BufferedImage;
import static java.awt.image.BufferedImage.TYPE_INT_RGB;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;

public class ExecutorForkJoin
{
					
	
	public static void exempleImage()
	{
		try {
			
			// Manipulate an image

			BufferedImage src =  ImageIO.read(new File("Files/image.jpg"));
//			BufferedImage dst = new BufferedImage(src.getWidth(),src.getHeight(),src.getType());
			BufferedImage dst = new BufferedImage(src.getWidth(),src.getHeight(),TYPE_INT_RGB);
			dst.setRGB(250, 250, new Color(255,0,0).getRGB());
			
			for(int i = 0; i < src.getWidth(); ++i)
			{
				for(int j = 0; j < src.getHeight(); ++j)
				{
//					System.out.println(src.getRGB(i, j));
					dst.setRGB(i, j, src.getRGB(i, j));
				}
			}
			
			ImageIO.write(dst, "jpg", new File("Files/image2.jpg"));
			
			
		} catch (IOException ex) {
			Logger.getLogger(ExecutorForkJoin.class.getName()).log(Level.SEVERE, null, ex);
		}
	}
	
	
	public static void main(String[] args) throws InterruptedException
	{	
		// Image smoothing: Fork - Join
		ForkJoinExemple.start();
		
		// Look for a password in serveral files: Executor
//		ExecutorExemple.start();	
	}
}
