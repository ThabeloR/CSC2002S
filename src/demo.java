    /* Mean Filter
    Thabelo Tshikalange
    07 August 2022 */
    import java.io.File;
    import java.io.IOException;
    import java.awt.image.BufferedImage;
    import javax.imageio.ImageIO;

public class demo{


      public static void main(String args[])throws IOException{
        BufferedImage img = null;
        File f = null;
    
        //read image
        try{
          f = new File("/home/thabelo/TSHTHA094_CSC2002S_PCP1/data/Test1.jpg");
          img = ImageIO.read(f);
        }catch(IOException e){
          System.out.println(e);
        }
    
        //get image width and height
        int width = img.getWidth();
        int height = img.getHeight();
        int mBlurWidth = 3;
        //convert to grayscale
        for(int y = 0; y < height; y++){
          for(int x = 0; x < width; x++){
            int p = img.getRGB(x,y);
           
 
            int a = (p>>24)&0xff;
            int r = (p>>16)&0xff;
            int g = (p>>8)&0xff;
            int b = p&0xff;
           
 
    
            //replace RGB value with avg
            p = (a<<24) | (r<<16) | (g<<8) | b;
    
            img.setRGB(x, y, p);
          }
        }
    
        //write image
        try{
          f = new File("/home/thabelo/TSHTHA094_CSC2002S_PCP1/data/OutputTest1.jpg");
          ImageIO.write(img, "jpg", f);
        }catch(IOException e){
          System.out.println(e);
        }
      }//main() ends here
    }//class ends here
 