/* Mean Filter
   Thabelo Tshikalange
   07 August 2022 */
  import java.io.File;
  import java.io.IOException;
  import java.awt.image.BufferedImage;
  import javax.imageio.ImageIO;

public class MeanFilterSerial{
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

    // some code goes here...

  }//main() ends here
}//class ends here
