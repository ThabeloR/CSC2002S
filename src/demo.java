    /* Mean Filter
    Thabelo Tshikalange
    07 August 2022 */
    import java.io.File;
    import java.io.IOException;
    import java.awt.image.BufferedImage;
    import javax.imageio.ImageIO;

public class demo{
  public static void main(String[] args) throws IOException {
    BufferedImage img = ImageIO.read(new File("/home/thabelo/TSHTHA094_CSC2002S_PCP1/data/Test1.jpg"));

    //get dimensions
    int maxHeight = img.getHeight();
    int maxWidth = img.getWidth();

    //create 2D Array for new picture
    int pictureFile[][] = new int [maxHeight][maxWidth];
    for( int i = 0; i < maxHeight; i++ ){
        for( int j = 0; j < maxWidth; j++ ){
            pictureFile[i][j] = img.getRGB( j, i );
        }
    }

    int output [][] = new int [maxHeight][maxWidth];

    //Apply Mean Filter
    for (int v=1; v<maxHeight; v++) {
        for (int u=1; u<maxWidth; u++) {
            //compute filter result for position (u,v)

            int sumr= 0;
            int sumg = 0;
            int sumb = 0;
            for (int j=-1; j<=1; j++) {
              for (int i=-1; i<=1; i++) {
                  if((u+(j)>=0 && v+(i)>=0 && u+(j)<maxWidth && v+(i)<maxHeight)){
                    int pixel=pictureFile[u+i][v+j];
                    int rr=(pixel&0x00ff0000)>>16, rg=(pixel&0x0000ff00)>>8, rb=pixel&0x000000ff;
                    sumr+=rr;
                    sumg+=rg;
                    sumb+=rb;
                  }
              }
          }

          sumr/=9; sumg/=9; sumb/=9;
          int newPixel=0xff000000|(sumr<<16)|(sumg<<8)|sumb;
          output[v][u] = newPixel;
      }
  }

  //Turn the 2D array back into an image
  BufferedImage theImage = new BufferedImage(
      maxHeight, 
      maxWidth, 
      BufferedImage.TYPE_INT_RGB);
  int value;
  for(int y = 1; y<maxHeight; y++){
      for(int x = 1; x<maxWidth; x++){
        value = output[y][x] ;
        theImage.setRGB(y, x, value);
    }
}

File outputfile = new File("/home/thabelo/TSHTHA094_CSC2002S_PCP1/data/Test4.png");
ImageIO.write(theImage, "png", outputfile);
}
}
