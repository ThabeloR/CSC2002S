/*
* Copyright (c) 2010, 2013, Oracle and/or its affiliates. All rights reserved.
*
* Redistribution and use in source and binary forms, with or without
* modification, are permitted provided that the following conditions
* are met:
*
*   - Redistributions of source code must retain the above copyright
*     notice, this list of conditions and the following disclaimer.
*
*   - Redistributions in binary form must reproduce the above copyright
*     notice, this list of conditions and the following disclaimer in the
*     documentation and/or other materials provided with the distribution.
*
*   - Neither the name of Oracle or the names of its
*     contributors may be used to endorse or promote products derived
*     from this software without specific prior written permission.
*
* THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
* IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
* THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
* PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT OWNER OR
* CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
* EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
* PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
* PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
* LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
* NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
* SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/

import java.util.Scanner;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;
import javax.imageio.ImageIO;

public class MeanFilterParallel extends RecursiveAction {

    private File srcFile;
    private File dstFile;
    private int windowWidth;
    private int mStart;
    private int[][] mSource;
    private int mLength;
    private int[][] mDestination;
    private int mHeight;
    private int mWidth;

    public MeanFilterParallel(int[][] src, int start, int length,int height,int width,  int[][] dst) {
        mSource = src;
        mStart = start;
        mLength = length;
        mDestination = dst;
        mHeight = height;
        mWidth = width;
    }

    // Average pixels from source, write results into destination.
    protected void computeDirectly() {
        int sidePixels = (windowWidth- 1) / 2;
        int halfWindowWidth = (windowWidth-1)/2;
        int windowArea = windowWidth*windowWidth;
        for (int h=mStart; h<mHeight; h++) {
            for (int w=1; w<mWidth; w++) {
                int sumr=0, sumg =0, sumb = 0;
                for (int vert=-halfWindowWidth; vert<=halfWindowWidth; vert++) {
                    for (int hori=-halfWindowWidth; hori<=halfWindowWidth; hori++) {
                        if((w+(vert)>=0 && h+(hori)>=0 && w+(vert)<mWidth && h+(hori)<mHeight)){
                            int pixel=mSource[h+hori][w+vert];
                            int rr=(pixel&0x00ff0000)>>16, rg=(pixel&0x0000ff00)>>8, rb=pixel&0x000000ff;
                            sumr+=rr;
                            sumg+=rg;
                            sumb+=rb;
                        }
                    }
                }

                sumr/=windowArea; sumg/=windowArea; sumb/=windowArea;
                int newPixel=0xff000000|(sumr<<16)|(sumg<<8)|sumb;
                mDestination[h][w] = newPixel;
            }
        }
    }
    protected static int sThreshold = 10000;

    @Override
    protected void compute() {
        if (mHeight*mWidth < sThreshold) {
            computeDirectly();
            return;
        }

        int split = mHeight / 2;

        //MeanFilterParallel(int[][] src, int start, int length,int height,int width,  int[][] dst)
        invokeAll(new MeanFilterParallel(mSource,mStart,mLength- split,mHeight,mWidth,mDestination),
        new MeanFilterParallel(mSource,mStart+split,mLength- split,mHeight,mWidth,mDestination));
    }

    // Plumbing follows.

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

    }

    public static BufferedImage runMeanFilter(BufferedImage srcImage, int windowWidth) {



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
        MeanFilterParallel meanFilter = new MeanFilterParallel(pictureFile,0, height,height,width, output);

        ForkJoinPool pool = new ForkJoinPool();

        long startTime = System.currentTimeMillis();
        pool.invoke(meanFilter);
        long endTime = System.currentTimeMillis();

        System.out.println("Image blur took " + (endTime - startTime) +
                " milliseconds.");

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
        return theImage;
    }
}
























































































