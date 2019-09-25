package com.berst.Wave;

import com.berst.StdAudio;
import java.util.Arrays;

public class Waves {

  public static void main(String[] args) {
    int count = 361;
    double[] x = new double[count];
    byte[] xb = new byte[count];
    for (int i = 0; i < count; i++) {
      x[i] =   Math.sin(Math.toRadians(i));
      xb[i] =  (byte) remapRound(x[i],-1D,1D,-128,127);
    }
//    System.err.println(Arrays.toString(x));
    System.err.println(Arrays.toString(xb));
    printHorizontal(xb,69,11);
//    printVertical  (xb,101,11);
  }

  public static double[] singleWave(double[] wave) {
    int wavelen = findWavelength(wave);
    double[] result= new double[wavelen];
    for (int i = 0; i < wavelen; i++) {
      result[i] = wave[i];
    }
    return result;
  }

  public static double[] tone(double hz, double duration) {
    int N = (int) (StdAudio.SAMPLE_RATE * duration);
    double[] a = new double[N + 1];
    double amplitude = 1D;
    for (int i = 0; i <= N; i++)
      a[i] = amplitude * Math.sin(2 * Math.PI * i * hz / StdAudio.SAMPLE_RATE);
    return a;
  }
  public static double[] mergeTones(double[] wave, double hz) {
    for (int i = 0; i < wave.length; i++) {
      wave[i] += Math.sin(2 * Math.PI * i * hz / StdAudio.SAMPLE_RATE);
      wave[i] /= 2;
    }
    return wave;
  }
  public static double[] mergeTones(double[] wave, double[] wave2) {
    double[] result = new double[wave.length];
    for (int i = 0; i < wave.length; i++) {
      result[i] = ( wave[i] + wave2[i] ) / 2;
    }
    return wave;
  }
  public static int[] toBuckets(byte[] wave, int numBuckets, int height) {
    int[] buckets = new int[numBuckets];
    for (int i = 0; i < buckets.length; i++) {
      int X = i;//remapRound(i,0,numBuckets,0,wave.length-1);
      int Y = remapRound(wave[X], Byte.MIN_VALUE,Byte.MAX_VALUE,0,height-1);
      buckets[i] = Y;
    }
    return buckets;
  }
  public static String printVertical(byte[] wave, int count, int height) {
    int[] buckets = toBuckets(wave, count, height);
    String fillerchar = ".";
    StringBuilder sb = new StringBuilder();
    for (int w = 0; w < count; w++) {

      for (int i = 0; i < buckets[w]; i++) {
        sb.append(fillerchar);
      }
      sb.append("X");
      for (int i = buckets[w]+1; i <height; i++) {
        sb.append(fillerchar);
      }

      sb.append("\n");
//        System.out.printf("%2d| %s\n", w, out);
    }
    System.out.println(sb.toString());
    return sb.toString();
  }
  public static String printHorizontal(double[] wave, int count, int height) {
    byte[] bytewave = new byte[wave.length];
    for (int i = 0; i < bytewave.length; i++) {
      bytewave[i] = (byte) remapRound(wave[i], -1D,1D,Byte.MIN_VALUE,Byte.MAX_VALUE);
    }
    return printHorizontal(bytewave,count,height);
  }
  public static String printHorizontal(byte[] wave, int count, int height) {
    int[] buckets = toBuckets(wave, count, height);
    String ON  = " ";
    String OFF = " ";
    int spacing = ON.length();
//    int
    StringBuilder sb = new StringBuilder();
//    sb.append("  "+Arrays.toString(buckets)).append("\n");
//    System.err.print("    ");
    for (int h = height-1; h >= 0; h--) {
      sb.append(String.format("%2d:",h));
      for (int i = 0; i < buckets.length; i++) {
        int cur = buckets[i];
        if (cur == h) {
            String s = "_";
            int next = (i<buckets.length-1) ? buckets[i + 1] : h;
            int prev = (i>0) ? buckets[i - 1] : 0;
            /*
            * 000 -
            * -00 -
            * +00 \
            * +0- \
            * 00+ /
            * -0+ /
            * 00- -
            * -0- -
            * +0+ -
            */

            if (prev < cur && cur < next) s = "/";
            if (prev > cur && cur > next) s = "\\";
            if (prev > cur && cur == next) s = "\\";
            if (prev == cur && cur < next) s = "/";

//            int determiner = next - prev;
//            if (Math.abs(determiner) > 1) s = (determiner > 0) ? "/" : "\\";
//            if (Math.abs(determiner) > 2) s = "|";
//            System.err.printf("%3d",buckets[i]);

          sb.append(String.format("%-"+spacing+"s",s));
        } else if (h==height/2) {
          sb.append(String.format("%-"+spacing+"s","-"));
        }
        else
          sb.append(String.format("%-"+spacing+"s",OFF));
//        sb.append("  ");
      }
      sb.append("\n");
    }
    System.err.println();

    sb.append("   ");
    for (int i = 0; i < count; i++) {
      String num = String.format("%-"+spacing+"d",i);
      if(num.length() > spacing) num = num.substring(num.length()-1);
      if(num.equals("0")) num = "|";
      sb.append(num);
    }
    System.out.println(sb.toString());
    return sb.toString();
  }

  public static int findWavelength(double[] wave) {
    double firstval = wave[0];
    double secondval = wave[1];
    for (int i = 2; i < wave.length-1; i++) {
      double current = wave[i];
      double next = wave[i+1];

      if (current<0 && next > 0) {
        return i+1;
      }
    }
    return 1;
  }
  public static int findFrequency(double[] wave) {
    double firstval = wave[0];
    int cyclecount = 0;
    for (int i = 1; i < wave.length; i++) {
      double current = wave[i];
      if (Math.abs(current-firstval) < .0001) cyclecount++;
    }
    return cyclecount;
  }

  public static double findAmplitude(double[] wave) {
    double max = 0;
    double min = 0;
    for (double d : wave) {
      if (d>max) max = d;
      if (d<min) min = d;
    }
//    System.err.printf("min=%.3f,max=%.3f\n",min,max);
    return max;
  }


  public static double remap(double val, double aMin, double aMax, double bMin, double bMax) {
    return (bMin + 1.0 * (bMax - bMin) / (aMax - aMin) * (val - aMin));
  }

  public static int remapRound(double val, double aMin, double aMax, double bMin, double bMax) {
    double shifted = Math.round(remap(val,aMin,aMax,bMin,bMax));
    return (int) shifted;
  }

  public static Double[] double2Double(double[] array) {
    Double[] Wave = new Double[array.length];
    for (int i = 0; i < array.length; i++) {
      Wave[i] = array[i];
    }
    return Wave;
  }

  public static Byte[] byte2Byte(byte[] array) {
    Byte[] Wave = new Byte[array.length];
    for (int i = 0; i < array.length; i++) {
      Wave[i] = array[i];
    }
    return Wave;
  }


    public static byte[] doubleArrayToBytes(double[] array) {
//    Stream.of(dd).map(d -> (byte) remapRound(d,-1,1,Byte.MIN_VALUE,Byte.MAX_VALUE));
    byte[] byteArray = new byte[array.length];
    for(int i = 0; i < array.length; i++) byteArray[i] = (byte) remapRound(array[i],-1,1,Byte.MIN_VALUE,Byte.MAX_VALUE);
    return  byteArray;
  }

  public static void printWave(double[] wave, int count, int height) {
    int[] buckets = new int[count];
    String fillerchar = " ";
    double max = findAmplitude(wave);
    for (int i = 0; i < buckets.length; i++) {
      int X = i;//remapRound(i,0,count,0,wave.length-1);
      int Y = remapRound(wave[X],-max,max,0,height-1);
      buckets[i] = Y;
    }
    System.err.println(Arrays.toString(buckets));
    StringBuilder sb = new StringBuilder();
    for (int w = height-1; w >= 0; w--) {
      for (int i = 0; i < buckets.length; i++) {
        if (w == buckets[i])
          sb.append("0");
        else
          sb.append(fillerchar);
        sb.append("  ");
      }
      sb.append("\n");
    }
    System.out.println(sb.toString());
  }
  public static void printWave2(double[] wave, int count, int height) {
//    String[][] lines = new String[height][width];
    int[] buckets = new int[count];
    String fillerchar = ".";
    double max = findAmplitude(wave);
    for (int i = 0; i < buckets.length; i++) {
      int X = remapRound(i,0,count,0,wave.length-1);
      int Y = remapRound(wave[X],-max,max,0,height-1);
      buckets[i] = Y;
    }//0.00 -0.29 -0.61 0.53 0.17 -0.49 -0.38 0.06 0.72 -0.09

    System.err.println(Arrays.toString(buckets));
    StringBuilder sb = new StringBuilder();
    for (int w = 0; w < count; w++) {
      for (int i = 0; i < buckets[w]; i++) {
        sb.append(fillerchar);
      }
      sb.append(buckets[w]);
      for (int i = buckets[w]+1; i <height; i++) {
        sb.append(fillerchar);
      }

      sb.append("\n");
//        System.out.printf("%2d| %s\n", w, out);
    }
    System.out.println(sb.toString());
  }

}
