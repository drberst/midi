package com.berst.Wave;

import java.util.ArrayList;

public class MergedWave {
  ArrayList<Sinewave> waves;

  public static void main(String[] args) {

    for (int i = 1; i < 10; i++) {
      System.out.printf("---------------------------"+i);
      ArrayList<Sinewave> somewaves = new ArrayList<>();
      somewaves.add(new Sinewave(1));
      Sinewave c = new Sinewave(i);
      somewaves.add(c);

      int len = 10;
      byte[] bytes = c.getBytes(len);
      double[] doubles = c.getDoubles(len);
      Waves.printHorizontal(bytes, len, 11);
      System.out.printf("Wave freq=%d", Waves.findFrequency(doubles));

      double[] onewave = Waves.singleWave(doubles);
      Waves.printHorizontal(onewave, onewave.length, 11);

      double[] morewaves = new MergedWave(somewaves).getDoubles(len);
      Waves.printHorizontal(morewaves, len, 11);

      double[] onemorewaves = Waves.singleWave(morewaves);
      Waves.printHorizontal(morewaves, onemorewaves.length, 11);

    }
  }
  public MergedWave() {
    waves = new ArrayList<>();
  }

  public MergedWave(  ArrayList<Sinewave> waves) {
    this.waves = waves;
  }
  public double getDouble(int t) {
    double result = 0;

    for (int d = 0; d < waves.size(); d++) {
      double hz = waves.get(d).hz;
      result += Math.sin(hz * 2.0 * Math.PI * t);

      if (d > 0) {
        result /= 2;
      }
    }
    return result;
  }

  public double[] getDoubles(int cTick, int len) {
    double[] result = new double[len];

    for (int d = 0; d < waves.size(); d++) {
      double hz = waves.get(d).hz;
      for (int t = 0; t < len; t++) {
        result[t] += getDouble(cTick+t);

        if (d > 0) {
          result[t] /= 2;
        }
      }
    }
    return result;
  }
  public double[] getDoubles(int len) {
    double[] result = new double[len];

    for (int d = 0; d < waves.size(); d++) {
      double hz = waves.get(d).hz;
      for (int t = 0; t < len; t++) {
        double period = (len - 1) / hz;
        double angle = 2.0 * Math.PI * t / period;
        result[t] += Math.sin(angle);

        if (d > 0) {
          result[t] /= 2;
        }
      }
    }
    return result;
  }
  public byte[] getBytes(int cTick, int len) {
    byte[] result = new byte[len];
    double[] doubles = getDoubles(cTick, len);
    for (int i = 0; i < len; i++) {
      result[i] = (byte) Waves.remapRound(doubles[i], -1, 1, Byte.MIN_VALUE, Byte.MAX_VALUE);
    }
    return result;
  }
  public byte[] getBytes(int len) {
    byte[] result = new byte[len];
    double[] doubles = getDoubles(len);
    for (int i = 0; i < len; i++) {
      result[i] = (byte) Waves.remapRound(doubles[i], -1, 1, Byte.MIN_VALUE, Byte.MAX_VALUE);
    }
    return result;
  }

}
