package com.berst.Wave;

public class Sinewave {

  double amplitude;
  double hz;
  double phase;

  public static void main(String[] args) {
    for (int i = 1; i < 10; i++) {
      Sinewave c = new Sinewave(i);
      int len = 100;
      byte[] bytes = c.getBytes(len);
      double[] doubles = c.getDoubles(len);
      Waves.printHorizontal(bytes, len, 11);
      System.out.printf("Wave freq=%d", Waves.findFrequency(doubles));

      double[] onewave = Waves.singleWave(doubles);
      Waves.printHorizontal(onewave, onewave.length, 11);

      double[] morewaves = Waves.mergeTones(doubles,1);
      Waves.printHorizontal(morewaves, onewave.length, 11);

    }

  }

  public Sinewave(double hz) {
    this(hz, 1D);
  }

  public Sinewave(double hz, double amplitude) {
    this(hz, amplitude, 0D);
  }

  public Sinewave(double hz, double amplitude, double phase) {
    this.hz = hz;
    this.amplitude = amplitude;
    this.phase = phase;
  }

  public Double[] getDoublesObj(int len) {
    Double[] result = new Double[len];
    byte[] sin = new byte[len];
    for (int t = 0; t < sin.length; t++) {
      double period = (len - 1) / hz;
      double angle = 2.0 * Math.PI * t / period;
      result[t] = Math.sin(angle);

    }
//    for (int t = 0; t < len; t++) {
//      result[t] = amplitude * Math.sin(2 * Math.PI * t * hz / len);
//    }
    return result;
  }

  public double[] getDoubles(int len) {
    double[] result = new double[len];
    byte[] sin = new byte[len];
    for (int t = 0; t < sin.length; t++) {
      double period = (len - 1) / hz;
      double angle = 2.0 * Math.PI * t / period;
      result[t] = Math.sin(angle);
    }
//    for (int t = 0; t < len; t++) {
//      result[t] = amplitude * Math.sin(2 * Math.PI * t * hz / len);
//    }
//    printLast5(result);
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

  private void printLast5(double[] array) {
    for (int i = array.length - 5; i < array.length; i++) {
      System.err.print(array[i] + " ");
    }
    System.err.println();
  }
}
