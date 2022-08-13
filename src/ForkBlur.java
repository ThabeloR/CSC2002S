import java.awt.image.BufferedImage;
import java.io.File;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;
import javax.imageio.ImageIO;
import java.io.IOException;
 
/**
 * ForkBlur implements a simple horizontal image blur. It averages pixels in the
 * source array and writes them to a destination array. The sThreshold value
 * determines whether the blurring will be performed directly or split into two
 * tasks.
 *
 * This is not the recommended way to blur images; it is only intended to
 * illustrate the use of the Fork/Join framework.
 */
public class ForkBlur extends RecursiveAction {
 
    private int[] mSource;
    private int mStart;
    private int mLength;
    private int[] mDestination;
    private int mBlurWidth =3; // Processing window size, should be odd.
 
    public ForkBlur(int[] src, int start, int length, int[] dst) {
        mSource = src;
        mStart = start;
        mLength = length;
        mDestination = dst;
    }
 
    // Average pixels from source, write results into destination.
    protected void computeDirectly() {
        int sidePixels = (mBlurWidth - 1) / 2;
        

        for (int index = mStart; index < mStart + mLength; index++) {
            // Calculate average.
            float rt = 0, gt = 0, bt = 0, at = 0;
            for (int mi = -sidePixels; mi <= sidePixels; mi++) {
                int mindex = Math.min(Math.max(mi + index, 0), mSource.length - 1);
                int pixel = mSource[mindex];
                at += (float)((pixel & 0xff) >> 24);
                rt += (float) ((pixel & 0x00ff0000) >> 16);
                gt += (float) ((pixel & 0x0000ff00) >> 8);
                bt += (float) ((pixel & 0x000000ff) >> 0);

            }
            at = at/mBlurWidth;
            rt = rt/mBlurWidth;
            gt = gt/mBlurWidth;
            bt = bt/mBlurWidth;
 
            // Re-assemble destination pixel.
            int dpixel = (0xff000000)
                    | (((int) at) << 24)
                    | (((int) rt) << 16)
                    | (((int) gt) << 8)
                    | (((int) bt) << 0);
            mDestination[index] = dpixel;
        }
    }
    protected static int sThreshold = 10000;
 
    @Override
    protected void compute() {
        if (mLength < sThreshold) {
            computeDirectly();
            return;
        }
 
        int split = mLength / 2;
 
        invokeAll(new ForkBlur(mSource, mStart, split, mDestination),
                new ForkBlur(mSource, mStart + split, mLength - split, 
                mDestination));
    }
 
    // Plumbing follows.
    public static void main(String[] args) throws Exception {
        BufferedImage img = null;
        File  srcName = null;
        try{
            srcName = new File("/home/thabelo/TSHTHA094_CSC2002S_PCP1/data/Test1.jpg");
            img = ImageIO.read(srcName);
          }catch(IOException e){
            System.out.println(e);
          }


        System.out.println("Source image: " + srcName);

        BufferedImage blurredImage = blur(img);
         
         
        //write image
       try{
        srcName  = new File("/home/thabelo/TSHTHA094_CSC2002S_PCP1/data/OutputTest1.jpg");
        ImageIO.write(blurredImage , "png", srcName );
      }catch(IOException e){
        System.out.println(e);
      }
         
        System.out.println("Output image: " + srcName );
         
    }
 
    public static BufferedImage blur(BufferedImage srcImage) {
        int w = srcImage.getWidth();
        int h = srcImage.getHeight();
 
        int[] src = srcImage.getRGB(0, 0, w, h, null, 0, w);
        int[] dst = new int[src.length];
        System.out.println("length of picture" + dst.length);

        System.out.println("Array size is " + src.length);
        System.out.println("Threshold is " + sThreshold);
 
        int processors = Runtime.getRuntime().availableProcessors();
        System.out.println(Integer.toString(processors) + " processor"
                + (processors != 1 ? "s are " : " is ")
                + "available");
 
        ForkBlur fb = new ForkBlur(src, 0, src.length, dst);
 
        ForkJoinPool pool = new ForkJoinPool();
 
        long startTime = System.currentTimeMillis();
        pool.invoke(fb);
        long endTime = System.currentTimeMillis();
 
        System.out.println("Image blur took " + (endTime - startTime) + 
                " milliseconds.");
 
        BufferedImage dstImage =
                new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        dstImage.setRGB(0, 0, w, h, dst, 0, w);
 
        return dstImage;
    }
}