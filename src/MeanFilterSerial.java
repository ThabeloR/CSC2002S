/*MeanFilter that uses aImagehightaluearage of the window to set new pixel
 *By Thabelo Tshikalange
  12 August 2022
 */

 //Imported library
import java.util.Scanner;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
public class MeanFilterSerial{

    public static void main(String[] args) throws Exception {

        Scanner scanner = new Scanner(System.in);

        File srcFile;
        File dstFile;
        int windowWidth;

        if (args.length == 0) {
            String srcName = "MicrosoftTeams-image.png";
            String dstName = "filtered"+srcName;
            srcFile = new File(srcName);
            dstFile = new File(dstName);
            System.out.println("enter window width");
            windowWidth = Integer.parseInt(scanner.nextLine());
        } else {
            srcFile = new File(args[0]);
            dstFile = new File(args[1]);
            windowWidth = Integer.parseInt(args[2]);
        }

        BufferedImage image = ImageIO.read(srcFile);
        System.out.println("Source image: " + srcFile.getName());
        ImageIO.write(runMeanFilter(image, windowWidth), "png", dstFile);


    }

    public static BufferedImage runMeanFilter(BufferedImage srcImage, int windowWidth) {

        long startTime = System.currentTimeMillis();

        int width = srcImage.getWidth();
        int height = srcImage.getHeight();
        int halfWindowWidth = (windowWidth-1)/2;
        int windowArea = windowWidth*windowWidth;

        int[] src = srcImage.getRGB(0, 0, width, height, null, 0, width);
        int[] dst = new int[src.length];


        int pictureFile[][] = new int [height][width];
        for( int i = 0; i < height; i++ ){
            for( int j = 0; j < width; j++ ){
                pictureFile[i][j] = srcImage.getRGB(j,i);
            }
        }

        int output [][] = new int [height][width];
        System.out.println(height);

        for (int h=1; h<height; h++) {
            for (int w=1; w<width; w++) {
                int sumr=0, sumg =0, sumb = 0;
                //for vertical part of the window
                for (int vert=-halfWindowWidth; vert<=halfWindowWidth; vert++) {
                    //for horizontal part of the window
                    for (int hori=-halfWindowWidth; hori<=halfWindowWidth; hori++) {
                        //making show ignoring edges
                        if((w+(vert)>=0 && h+(hori)>=0 && w+(vert)<width && h+(hori)<height)){
                            int pixel=pictureFile[h+hori][w+vert];
                            int rr=(pixel&0x00ff0000)>>16, rg=(pixel&0x0000ff00)>>8, rb=pixel&0x000000ff;
                            sumr+=rr;
                            sumg+=rg;
                            sumb+=rb;
                        }
                    }
                }

                sumr/=windowArea; sumg/=windowArea; sumb/=windowArea;
                int newPixel=0xff000000|(sumr<<16)|(sumg<<8)|sumb;
                output[h][w] = newPixel;
            }
        }

        //Turn the 2D array back into an image
        BufferedImage theImage = new BufferedImage(
                width,
                height,
                BufferedImage.TYPE_INT_RGB);
        int value;
        for(int y = 1; y<height; y++){
            for(int x = 1; x<width; x++){
                value = output[y][x] ;
                theImage.setRGB(x, y, value);
            }
        }


        long endTime = System.currentTimeMillis();

        System.out.println("Image filter took " + (endTime - startTime) +
                " milliseconds.");
        return theImage;
    }
}