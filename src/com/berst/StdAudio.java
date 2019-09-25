package com.berst;

import static com.berst.Midi.log;

import com.berst.Compose.Composer;
import com.berst.Model.Note;
import java.util.ArrayList;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.SourceDataLine;

public final class StdAudio {

  public static final int SAMPLE_RATE = 44100;

  private static final int BYTES_PER_SAMPLE = 2;                // 16-bit audio
  private static final int BITS_PER_SAMPLE = 16;                // 16-bit audio
  private static final double MAX_16_BIT = Short.MAX_VALUE;     // 32,767
  private static final int SAMPLE_BUFFER_SIZE = 4096;

  private static SourceDataLine line;   // to play the sound
  private static byte[] buffer;         // our internal buffer
  private static int bufferSize = 0;

  // not-instantiable
  private StdAudio() {
  }


  // static initializer
  static {
    init();
  }

  // open up an audio stream
  private static void init() {
    try {
      // 44,100 samples per second, 16-bit audio, mono, signed PCM, little Endian
      AudioFormat format = new AudioFormat((float) SAMPLE_RATE, BITS_PER_SAMPLE, 1, true, false);
      DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);

      line = (SourceDataLine) AudioSystem.getLine(info);
      line.open(format, SAMPLE_BUFFER_SIZE * BYTES_PER_SAMPLE);

      buffer = new byte[SAMPLE_BUFFER_SIZE * BYTES_PER_SAMPLE / 3];
    } catch (Exception e) {
      System.out.println(e.getMessage());
      System.exit(1);
    }

    // no sound gets made before this call
    line.start();
  }


  /**
   * Close standard audio.
   */
  public static void close() {
    line.drain();
    line.stop();
  }

  /**
   * Write one sample (between -1.0 and +1.0) to standard audio. If the sample is outside the range,
   * it will be clipped.
   */
  public static void play(double in) {

    // clip if outside [-1, +1]
    if (in < -1.0) {
      in = -1.0;
    }
    if (in > +1.0) {
      in = +1.0;
    }

    // convert to bytes
    short s = (short) (MAX_16_BIT * in);
    buffer[bufferSize++] = (byte) s;
    buffer[bufferSize++] = (byte) (s >> 8);   // little Endian

    // send to sound card if buffer is full
    if (bufferSize >= buffer.length) {
//      /printWave(buffer,20,9);
      line.write(buffer, 0, buffer.length);
      bufferSize = 0;
    }
  }

  /**
   * Write an array of samples (between -1.0 and +1.0) to standard audio. If a sample is outside the
   * range, it will be clipped.
   */
  public static void play(double[] input) {
//    printWave3(input,20,10);
//    printWave4(input,20,10);
    for (int i = 0; i < input.length; i++) {
      play(input[i]);
    }
  }

  public static double[] playChord(ArrayList<Double> tones, double duration) {
    int N = (int) (StdAudio.SAMPLE_RATE * duration);
    double[] a = new double[N + 1];
    byte[] temp;
    double amplitude = 1D / tones.size();
    for (int d = 0; d < tones.size(); d++) {
      double hz = tones.get(d);
      for (int i = 0; i <= N; i++) {
        a[i] += Math.sin(2 * Math.PI * i * hz / StdAudio.SAMPLE_RATE);

        if (d > 0) {
          a[i] /= 2;
        }
      }

//        int num = 400;
//        if (i < num) {
//          temp = Waves.doubleArrayToBytes(a);
//          Waves.printHorizontal(temp, num, 11);
//        }
    }
//    byte[] bytes = Waves.doubleArrayToBytes(a);
//    Waves.printHorizontal(bytes,200,21);
//    play(a);
//    log(" ----- ENDLINE ----- ");
    return a;
  }


  public static void playComposition(Composer aComposer) {
    System.out.printf("Playing: %d lines : %d tracks : buffer=%d\n",
        aComposer.ticks(),aComposer.tracks(), SAMPLE_RATE);


    String[] lines = aComposer.pianoRollLines();



    for (int tick = 0; tick < aComposer.ticks(); tick++) {
      StringBuilder sb = new StringBuilder();
      ArrayList<Integer> aLine = aComposer.getNotes(tick);
//      if (lines.size() < aLine.size()) {
//        addLines(lines.size() - aLine.size());
//      }
      sb.append(String.format("%3d: ", tick));
      ArrayList<Double> tones = new ArrayList<>();
      for (int i = 0; i < 1; i++) {
        int note = aLine.get(i);
        tones.add(new Note(note).getHz());
//        sb.append(String.format("[%d:%2d]",i,note));
        sb.append(lines[tick]);
      }

      log(sb.toString());
      double[] wave = playChord(tones, .25);

    }
  }


  /**
   * Test client - play an A major scale to standard audio.
   */
  public static void main(String[] args) {

//    double hz = 261.63;// 440.4 Hz for 1 sec
//    double duration = 2.0;
//    double[] x = new double[100];
//    for (int i = 0; i < 100; i++) {
//      x[i] =  Math.sin(i/5D);
//    }
//    System.err.println(Arrays.toString(x));
//    printWave3(x,20,9);
//    printWave4(x,100,21);

//    double[] a = tone(hz, duration);
    ArrayList<Double> tones = new ArrayList<>();
    tones.add(new Note("C").getHz());
    tones.add(new Note("E").getHz());
    tones.add(new Note("G").getHz());
    tones.add(new Note("B").getHz());
    System.out.println(tones);

    StdAudio.playChord(tones, .25);
//    for (Double freq : tones)
//      a = mergeTones(a,freq);
//    StdAudio.play(a);
//    StdAudio.play(a);

  }
}