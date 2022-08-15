/*MeanFilter that uses aImagehightaluearage of the window to set new pixel
 *By Thabelo Tshikalange
  12 August 2022
 */

 import java.awt.image.BufferedImage;
 import java.io.File;
 import javax.imageio.IIOException;
 import javax.imageio.ImageIO;
 
 public class MeanFilterSerial {
 
   public static void main(String[] args) throws Exception {
 
     File sourseFile;
     File destinationFile;
     int windowSize;
 
     if (args.length != 3) {
       System.out.println("Please enter three  parameters:<inputImageName> <outputImageName> <windowSize>");
       System.exit(0);
     } else {
       sourseFile = new File(args[0]);
       destinationFile = new File(args[1]);
       windowSize = Integer.parseInt(args[2]);
       if (windowSize % 2 == 0) {
         System.out.println("This must be an odd, positive integer >=3");
         System.exit(0);
       }
       try {
         BufferedImage image = ImageIO.read(sourseFile);
         System.out.println("Source image: " + sourseFile.getName());
         // Call to start filter with the input value
         BufferedImage filteredImage = meanFilter(image, windowSize);
        // assign the new image
         ImageIO.write(filteredImage, "png", destinationFile);
         System.out.println("Output image: " + destinationFile.getName());
       } catch (IIOException e) {
         System.out.println(e);
         System.exit(0);
       }
     }
   }
 
   public static BufferedImage meanFilter(BufferedImage sourceImage, int windowSize) {
    //Benchmarking test starts here
     long startTime = System.currentTimeMillis();
 
     int width = sourceImage.getWidth();
     int height = sourceImage.getHeight();
     int windowWidth = (windowSize - 1) / 2;
     int windowArea = windowSize * windowSize;
 
     int pictureFile[][] = new int[height][width];
     for (int i = 0; i < height; i++) {
       for (int j = 0; j < width; j++) {
         pictureFile[i][j] = sourceImage.getRGB(j, i);
       }
     }
 
     int output[][] = new int[height][width];
     

     // goes through the picture 
     for (int Y = 1; Y < height; Y++) {
       for (int X = 1; X < width; X++) {
         int Reds = 0, Greens = 0, Blues = 0;
         //goes through the window
         for (int V = -windowWidth; V <= windowWidth; V++) {
           for (int H = -windowWidth; H <= windowWidth; H++) {
             //edge cases
             if (X + (V) >= 0 && Y + (H) >= 0 && X + (V) < width && Y + (H) < height){
               int pixel = pictureFile[Y + H][X + V];
               int Redpixel = (pixel & 0x00ff0000) >> 16, Greenpixel = (pixel & 0x0000ff00) >> 8, Bluepixel = pixel & 0x000000ff;
               Reds += Redpixel;
               Greens += Greenpixel;
               Blues += Bluepixel;
             }
           }
         }
 
         Reds /= windowArea;
         Greens /= windowArea;
         Blues /= windowArea;
         int newPixel = 0xff000000 | (Reds << 16) | (Greens << 8) | Blues;
         output[Y][X] = newPixel;
       }
     }
 
     //getting new image
     BufferedImage newImage = new BufferedImage( width,height,BufferedImage.TYPE_INT_RGB);
     int value;
     for (int Y = 1; Y < height; Y++) {
       for (int X = 1; X < width; X++) {
         value = output[Y][X];
         newImage.setRGB(X, Y, value);
       }
     }
     // end of program run time bench-mark
     long endTime = System.currentTimeMillis();
     System.out.println("The program took " + (endTime - startTime) + " milliseconds.");
     return newImage;
   }
 }