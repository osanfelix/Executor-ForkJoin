
package executor.forkjoin;

import java.awt.Color;
import java.awt.image.BufferedImage;
import static java.awt.image.BufferedImage.TYPE_INT_RGB;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;

/**
 *
 * @author osanf
 */
public class ForkJoinExemple extends RecursiveAction
{
	BufferedImage imgSrc;
	static volatile BufferedImage imgDst = null;
	File fileDst;
	private final int imgHeight;
	private final int imgWidth;
	private final int firstLine;
	private final int lastLine;
	
	static final int LINES = 50;		// 50 lines per task maximun
	static final int OFFSET = 24;		// average of 24 pixels, even.
	// scr: 
	public ForkJoinExemple(BufferedImage src, int lineFrom, int lineTo)
	{
		imgSrc = src;
		firstLine = lineFrom;
		lastLine = lineTo;
		imgHeight = imgSrc.getHeight();
		imgWidth = imgSrc.getWidth();
		// Initialize destination image (only one time)
		if(imgDst == null)
			imgDst = new BufferedImage(imgSrc.getWidth(),imgHeight,TYPE_INT_RGB);
	}

	public static BufferedImage getNegativeImage()
	{
		return imgDst;
	}
	
	@Override
	protected void compute()
	{
		// get current lines
		if(lastLine - firstLine > LINES)
		{
			// Split the task in 2
			int midLine = (firstLine+lastLine)/2+1;
			ForkJoinExemple task1 = new ForkJoinExemple(imgSrc, firstLine,  midLine);
			ForkJoinExemple task2 = new ForkJoinExemple(imgSrc, midLine, lastLine);
			task1.fork();	task2.fork();
			task1.join();	task2.join();
		}
		else
		{
			// Compute negative
			negative();
			System.out.println("Linies thread "+Thread.currentThread().getName()+":\nInici: "+firstLine + "\nFinal: "+ lastLine);
		}
		
	}
	
	protected void negative()
	{
		int half_offset = OFFSET/2;
		int xMin = 0;
		int xMax = 0;
		int yMin = 0;
		int yMax = 0;
		// Average
		long average = 0;
		long average_red = 0;
		long average_blue = 0;
		long average_green = 0;
		int counter = 0;
			// Manipulate an image
			for(int i = firstLine; i < lastLine; ++i)
			{
				for(int j = 0; j < imgHeight; ++j)
				{
					// Average
					average = 0;
					average_red = 0;
					average_blue = 0;
					average_green = 0;
					counter = 0;
					
					// Calculate xMin & xMax
					xMin = i - half_offset < 0			? 0 : i - half_offset;
					xMax = i + half_offset > imgWidth	? imgWidth : i + half_offset;
						
					for(int x = xMin; x < xMax; ++x)
					{
						// Calculate yMin & yMax
						yMin = j - half_offset < 0			? 0 : j - half_offset;
						yMax = j + half_offset > imgHeight	? imgHeight : j + half_offset;
						
						for(int y = yMin; y < yMax; ++y)
						{
//							average += imgSrc.getRGB(x, y);
							Color color = new Color(imgSrc.getRGB(x, y));
							average_red += color.getRed();
							average_green += color.getGreen();
							average_blue += color.getBlue();
							++counter;
						}
					}
//					average /= counter;
					average_red /= counter;
					average_green /= counter;
					average_blue /= counter;
					
					imgDst.setRGB(i, j, new Color((int)average_red, (int)average_green, (int)average_blue).getRGB());
				}
			}
	}
	
	public static void start()
	{
		try {
			// Source image
			BufferedImage src =  ImageIO.read(new File("Files/image.jpg"));
			// Destiny file
			File fileDst = new File("Files/image2.jpg");
			// Compute negative task
			long time = System.currentTimeMillis();
			
			ForkJoinPool pool = new ForkJoinPool();		// max threads (4 in my i3 core)
			//~ForkJoinPool pool = new ForkJoinPool(1);	// 1 thread
			//~ForkJoinPool pool = new ForkJoinPool(4);	// 2 threads
			
			ForkJoinExemple task = new ForkJoinExemple(src, 0, src.getWidth());
			pool.invoke(task);
			
			//~pool.awaitTermination(5, TimeUnit.SECONDS);
			pool.shutdown();
			
			System.out.println("Temps de processat: " + (System.currentTimeMillis() - time) + "ms");
			
			// Save image
			ImageIO.write(ForkJoinExemple.getNegativeImage(), "jpg", fileDst);
			
		} catch (IOException ex) {
			Logger.getLogger(ExecutorForkJoin.class.getName()).log(Level.SEVERE, null, ex);
		}
	}
}
