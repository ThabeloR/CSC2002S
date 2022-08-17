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

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;
import javax.imageio.IIOException;
import javax.imageio.ImageIO;

public class MedianFilterParallel extends RecursiveAction {
  private int windowWidth;
  private int mStart;
  private int[][] sourceImage;
  private int mLength;
  private int[][] newImage;
  private int height;
  private int width;

  public MedianFilterParallel(int[][] src,int start,int length,int height,int width,int[][] dst,int windowWidth) {
    sourceImage = src;
    mStart = start;
    mLength = length;
    newImage = dst;
    this.height = height;
    this.width = width;
    this.windowWidth = windowWidth;
  }

  // Average pixels from source, write results into destination.
  protected void computeDirectly() {
    int halfWindowWidth = (windowWidth - 1) / 2;
    int windowArea = windowWidth * windowWidth;

    for (int Y = 1; Y < height; Y++) {
      for (int X = 1; X < width; X++) {
        ArrayList<Integer> red = new ArrayList<Integer>();
        ArrayList<Integer> green = new ArrayList<Integer>();
        ArrayList<Integer> blue = new ArrayList<Integer>();
        //goes through the window
        for (int V = -halfWindowWidth; V <= halfWindowWidth; V++) {
          for (int H = -halfWindowWidth; H <= halfWindowWidth; H++) {
            //edge cases
            if ((X + (V) >= 0 && Y + (H) >= 0 && X + (V) < width && Y + (H) < height)) {
              int piXel = sourceImage[Y + H][X + V];
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
          newImage[Y][X] = newPiXel;
        } else {
          int newPiXel = 0Xff000000;
          newImage[Y][X] = newPiXel;
        }
      }
    }
  }

  protected static int sThreshold = 50000;

  @Override
  protected void compute() {
      if (mLength < sThreshold) {
          computeDirectly();

          return;
      }

      int split = mLength / 2;
    invokeAll(
      new MedianFilterParallel(sourceImage,mStart,mLength - split,height,width,newImage,windowWidth),
      new MedianFilterParallel( sourceImage,mStart + split,  mLength - split,height,width,newImage,windowWidth));
  }

  // Plumbing follows.

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
        BufferedImage filteredImage = Filter(image, windowSize);
       // assign the new image
        ImageIO.write(filteredImage, "png", destinationFile);
        System.out.println("Output image: " + destinationFile.getName());
      } catch (IIOException e) {
        System.out.println(e);
        System.exit(0);
      }
    }
  }

  public static BufferedImage Filter(BufferedImage srcImage,int windowWidth) {
    int width = srcImage.getWidth();
    int height = srcImage.getHeight();

    int pictureFile[][] = new int[height][width];
    for (int i = 0; i < height; i++) {
      for (int j = 0; j < width; j++) {
        pictureFile[i][j] = srcImage.getRGB(j, i);
      }
    }
    int output[][] = new int[height][width];
    MedianFilterParallel MedianFilter = new MedianFilterParallel(pictureFile,0,height,height, width, output,windowWidth);

    ForkJoinPool pool = new ForkJoinPool();

    long startTime = System.currentTimeMillis();
    pool.invoke(MedianFilter);
    long endTime = System.currentTimeMillis();

    //Turn the 2D array back into an image
    BufferedImage newimage = new BufferedImage(width,height,BufferedImage.TYPE_INT_RGB);
    int value;
    for (int y = 1; y < height; y++) {
      for (int x = 1; x < width; x++) {
        value = output[y][x];
        newimage.setRGB(x, y, value);
      }
    }

    System.out.println(
            "Meadian fliter took " + (endTime - startTime) + " milliseconds."
    );
    return newimage;
  }
}