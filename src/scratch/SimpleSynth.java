package com.berst;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.SourceDataLine;

class SimpleSynth
{
  private static int SAMPLE_BITS = 16;
  private static int CHANNELS = 1;
  private static boolean SIGNED_TRUE = true;
  private static boolean BIG_ENDIAN_FALSE = false;
  private static float CDROM_SAMPLE_FREQ = 44100;

  private SourceDataLine line;

  private double dDuration;
  private double dCyclesPerSec;
  private double dAmplitude;
//  private double[] adHarmonics;

  private double dMin;
  private double dMax;

  public SimpleSynth(String[] asArguments) throws Exception
  {
    dDuration = Double.parseDouble(asArguments[0]);
    dCyclesPerSec = Double.parseDouble(asArguments[1]);
    dAmplitude = Double.parseDouble(asArguments[2]);
//    adHarmonics = new double[asArguments.length - 3];
//    for (int i = 0; i < adHarmonics.length; ++ i)
//      adHarmonics[i] = Double.parseDouble(
//          asArguments[i + 3]);

    AudioFormat format = new AudioFormat(
        CDROM_SAMPLE_FREQ, SAMPLE_BITS,
        CHANNELS, SIGNED_TRUE, BIG_ENDIAN_FALSE);
    line = AudioSystem.getSourceDataLine(format);
    line.open();
    line.start();
  }

  public void closeLine()
  {
    line.drain();
    line.stop();
  }

  public void playSound()
  {
    // allocate and prepare byte buffer and its index
    int iBytes = (int) (2.0 * (0.5 + dDuration)
        * CDROM_SAMPLE_FREQ);
    byte[] ab = new byte[iBytes];
    int i = 0;

    // iterate through sample radian values
    // for the specified duration
    short i16;
    double dSample;
    double dRadiansPerSample = 2.0 * Math.PI
        * dCyclesPerSec / CDROM_SAMPLE_FREQ;
    double dDurationInRadians = 2.0 * Math.PI
        * dCyclesPerSec * dDuration;
    dMin = 0.0;
    dMax = 0.0;
    for (double d = 0.0;
        d < dDurationInRadians;
        d += dRadiansPerSample)
    {
      // add principle and the dot product of harmonics
      // and their amplitudes relative to the principle
      dSample = Math.sin(d);
//      for (int h = 0; h < adHarmonics.length; ++ h)
//        dSample += adHarmonics[h]
//            * Math.sin((h + 2) * d);

      // factor in amplitude
      dSample *= dAmplitude;

      // adjust statistics
      if (dMin > dSample)
        dMin = dSample;
      if (dMax < dSample)
        dMax = dSample;

      // store in byte buffer
      i16 = (short) (dSample);
      ab[i ++] = (byte) (i16);
      ab[i ++] = (byte) (i16 >> 8);
    }

    // send the byte array to the audio line
    line.write(ab, 0, i);
  }

  public void printStats()
  {
    System.out.println("sample range was ["
        + dMin + ", " + dMax + "]"
        + " in range of [-32768, 32767]");

    if (dMin < -32768.0 || dMax > 32767.0)
      System.out.println("sound is clipping"
          + "(exceeding its range),"
          + " so use a lower amplitude");
  }

  public static void main(String[] asArguments)
      throws Exception
  {
    if (asArguments.length < 3)
    {
      System.err.println("usage: java SimpleSynth"
          + " <duration>"
          + " <tone.cycles.per.sec>"
          + " <amplitude>"
          + " [<relative.amplitude.harmonic.2>"
          + " [...]]");
      System.err.println("pure tone:"
          + " java com/berst/SimpleSynth 1 440 32767");
      System.err.println("oboe-like:"
          + " java com/berst/SimpleSynth 1 440 15000  0 1 0 .9");
      System.err.println("complex:"
          + " java com/berst/SimpleSynth 1 440 800 .3"
          + " .5 .4 .2 .9 .7 5 .1 .9 12 0 3"
          + " .1 5.2 2.5 .5 1 7 6");

      System.exit(0);
    }

    SimpleSynth synth = new SimpleSynth(asArguments);
    synth.playSound();
    synth.closeLine();
    synth.printStats();

    System.exit(0);
  }
}