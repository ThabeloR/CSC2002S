import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import javax.imageio.IIOException;
import javax.imageio.ImageIO;

public class MedianFilterSerial {

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
            // Call to start medianFilter with the input value
            BufferedImage filteredImage = medianFilter(image, windowSize);
           // assign the new image
            ImageIO.write(filteredImage, "png", destinationFile);
            System.out.println("Output image: " + destinationFile.getName());
          } catch (IIOException e) {
            System.out.println(e);
            System.exit(0);
          }
        }
      }

  public static BufferedImage medianFilter(BufferedImage srcImage, int windowSize) {
    int width = srcImage.getWidth();
    int hight = srcImage.getHeight();
    int windowwidth = (windowSize - 1) / 2;
    int windowArea = windowSize * windowSize;

    long startTime = System.currentTimeMillis();

    //create 2D Array for new picture
    int pictureFile[][] = new int[hight][width];
    for (int i = 0; i < hight; i++) {
      for (int j = 0; j < width; j++) {
        pictureFile[i][j] = srcImage.getRGB(j, i);
      }
    }

    int output[][] = new int[hight][width];
     // goes through the picture 
    for (int Y = 1; Y < hight; Y++) {
      for (int X = 1; X < width; X++) {
        ArrayList<Integer> red = new ArrayList<Integer>();
        ArrayList<Integer> green = new ArrayList<Integer>();
        ArrayList<Integer> blue = new ArrayList<Integer>();
        //goes through the window
        for (int V = -windowwidth; V <= windowwidth; V++) {
          for (int H = -windowwidth; H <= windowwidth; H++) {
            //edge cases
            if ((X + (V) >= 0 && Y + (H) >= 0 && X + (V) < width && Y + (H) < hight)) {
              int piXel = pictureFile[Y + H][X + V];
              int RedpiXel = (piXel & 0X00ff0000) >> 16, GreenpiXel =(piXel & 0X0000ff00) >> 8, BluepiXel = piXel & 0X000000ff;
              red.add(RedpiXel);
              green.add(GreenpiXel);
              blue.add(BluepiXel);
            }
          }
        }
        Collections.sort(red);
        Collections.sort(green);
        Collections.sort(blue);
        int median = (windowArea + 1) / 2;
        if (median * 2 <= red.size() + 1) {
          int Reds = red.get(median);
          int Blues = blue.get(median);
          int Greens = green.get(median);

          int newPiXel = 0Xff000000 | (Reds << 16) | (Greens << 8) | Blues;
          output[Y][X] = newPiXel;
        } else {
          int newPiXel = 0Xff000000;
          output[Y][X] = newPiXel;
        }
      }
    }

    //getting new image
    BufferedImage newImage = new BufferedImage(width,hight,BufferedImage.TYPE_INT_RGB);
    int value;
    for (int Y = 1; Y < hight; Y++) {
      for (int X = 1; X < width; X++) {
        value = output[Y][X];
        newImage.setRGB(X, Y, value);
      }
    }
    // End of bench-mark
    long endTime = System.currentTimeMillis();
    System.out.println("medianFilter took " + (endTime - startTime) + " milliseconds.");
    return newImage;
  }
}