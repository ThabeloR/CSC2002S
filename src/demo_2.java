import java.io.File;
import java.io.IOException;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;



public class demo_2{
    public static void main(String[] args){
        BufferedImage srcImage = null;
        File file = null;
        
        
        
    
        //read image
        try{
          file = new File("/home/thabelo/TSHTHA094_CSC2002S_PCP1/data/Test1.jpg");
          srcImage = ImageIO.read(file);
        }catch(IOException e){
          System.out.println(e);
          System.exit(0);
        }
        BufferedImage filteredImage = filter(srcImage);

        //write image
        try{
            file = new File("/home/thabelo/TSHTHA094_CSC2002S_PCP1/data/OutputTest1.jpg");
            ImageIO.write(filteredImage, "jpg", file);
          }catch(IOException e){
            System.out.println(e);
          }

    }
//MeanFilter
public static  BufferedImage filter(BufferedImage srcImage){
    int window = 3;
    int at = 0;
    int rt = 0;
    int gt = 0;
    int bt = 0;
    for (int x = 0; x < srcImage.getWidth(); x++){
        for (int y = 0; y < srcImage.getHeight();y++){
                    int pixel = srcImage.getRGB(x, y);
                    at += ((pixel & 0xff) >> 24);
                    rt += ((pixel & 0x00ff0000) >> 16);
                    gt += ((pixel & 0x0000ff00) >> 8);
                    bt += ((pixel & 0x000000ff) >> 0);

                    at = at/window;
                    rt = rt/window;
                    gt = gt/window;
                    bt = bt/window;
                    pixel = (at<<24) | (rt<<16) | (gt<<8) | bt;
                    srcImage.setRGB(x, y, pixel);

                } 
            }

    
    return srcImage;
}
}

