package com.berst;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

public class Tone {
  static final int SAMPLE_RATE = 16 * 1024; // ~16KHz
  static final int SECONDS = 2;

  public static void main(String[] args) throws LineUnavailableException {
    final AudioFormat af =
        new AudioFormat(SAMPLE_RATE, 8, 1, true, true);
    SourceDataLine line = AudioSystem.getSourceDataLine(af);
    SourceDataLine line2 = AudioSystem.getSourceDataLine(af);

    line.open(af, SAMPLE_RATE);
    line.start();
    for  (Note n : Note.values()) {
      play(line, n, 500);
      play(line, Note.REST, 10);
    }
    Note n = Note.REST;
    double[] freqs = {262.0,327.5,393.0,491.2};
    for (double f : freqs) {
      play(line,waveform(f),500);
//      rest(line,500);
    }
    line.drain();
    line.close();
  }


  private static void rest(SourceDataLine line, int ms) {
    ms = Math.min(ms, SECONDS * 1000);
    int length = SAMPLE_RATE * ms / 1000;
    int count = line.write(new byte[length], 0, length);
  }

  private static void play(SourceDataLine line, byte[] bytes, int ms) {
    ms = Math.min(ms, SECONDS * 1000);
    int length = SAMPLE_RATE * ms / 1000;
    int count = line.write(bytes, 0, length);
  }

  private static void play(SourceDataLine line, Note note, int ms) {
    ms = Math.min(ms, Note.SECONDS * 1000);
    int length = Note.SAMPLE_RATE * ms / 1000;
    int count = line.write(note.data(), 0, length);
  }

  private static byte[] waveform(Double frequency) {

    byte[] sin = new byte[SECONDS * SAMPLE_RATE];
    for (int i = 0; i < sin.length; i++) {
      double period = (double)SAMPLE_RATE / frequency;
      double angle = 2.0 * Math.PI * i / period;
      sin[i] = (byte)(Math.sin(angle) * 127f);
    }
    return sin;
  }
}


enum Note {

  REST, A4, A4$, B4, C4, C4$, D4, D4$, E4, F4, F4$, G4, G4$, A5;
  public static final int SAMPLE_RATE = 16 * 1024; // ~16KHz
  public static final int SECONDS = 2;
  private byte[] sin = new byte[SECONDS * SAMPLE_RATE];

  Note() {
    int n = this.ordinal();
    if (n > 0) {
      double exp = ((double) n - 1) / 12d;
      double f = 440d * Math.pow(2d, exp);
      for (int i = 0; i < sin.length; i++) {
        double period = (double)SAMPLE_RATE / f;
        double angle = 2.0 * Math.PI * i / period;
        sin[i] = (byte)(Math.sin(angle) * 127f);
      }
    }
  }

  public byte[] data() {
    return sin;
  }
}
