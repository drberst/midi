package scratch;

import com.berst.Composer;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

public class Tones {
  static final int SAMPLE_RATE = 16 * 1024; // ~16KHz
  static final int SECONDS = 2;
  static final AudioFormat af = new AudioFormat(SAMPLE_RATE, 8, 1, true, true);

  SourceDataLine[] lines;
  public static void main2(String[] args) throws LineUnavailableException {
    Composer c = new Composer();
//    c.applyDefaultSeq();
    c.applySeqChord();
//    Integer[][] matrix = c.matrix;
//
//     HashMap<Integer, ArrayList<Integer>> notes = c.toMap();
//    System.out.println(notes);
//    Tones tones = new Tones(3);
//    tones.open();
////    tones.play(0,wave(hz(40)),1000);
//
//    for (HashMap.Entry<Integer, ArrayList<Integer>> entry : notes.entrySet()) {
//      Integer tick = entry.getKey();
//      ArrayList<Integer> notesThisTick = entry.getValue();
//      for (Integer note : notesThisTick) {
//        tones.play(0,wave(hz(note)),250);
//      }
//    }
//    tones.close();
//    c.pianoRoll();

  }

  public static void main(String[] args) throws LineUnavailableException {
    Tones tones = new Tones(3);
    tones.open();
    tones.play(0, wave(262.0),1000);
    tones.play(1, wave(393.0),1000);
    tones.play(2, wave(327.5),1000);
    tones.start();
    tones.close();
//    line.open(af, SAMPLE_RATE);
//    line.start();
////    for  (Note n : Note.values()) {
////      play(line, n, 500);
////      play(line, Note.REST, 10);
////    }
////    Note n = Note.REST;
//    double[] freqs = {262.0,327.5,393.0,491.2};
//    for (double f : freqs) {
//      play(line,wave(f),500);
////      rest(line,500);
//    }
//    line.drain();
//    line.close();
  }

  public Tones() {
    new Tones(1);
  }

  public Tones(Integer tracks) {
    try {
      lines = new SourceDataLine[tracks];
      for (int i = 0; i < tracks; i++) {
        lines[i] = AudioSystem.getSourceDataLine(af);
      }
    } catch (LineUnavailableException e) {
      e.printStackTrace();
    }
  }
  public void open() throws LineUnavailableException {
    for (SourceDataLine line : lines) {
      line.open(af, SAMPLE_RATE);
//      line.start();
    }
  }
  public void start() throws LineUnavailableException {
    for (SourceDataLine line : lines) {
      line.start();
    }
  }
  public void close() throws LineUnavailableException {
      for (SourceDataLine line : lines) {
        line.drain();
        line.close();
    }
  }

//  public void playAll(Integer track, Double[] freqs
  private void play(Integer track, byte[] bytes, int ms) {
    ms = Math.min(ms, SECONDS * 1000);
    int length = SAMPLE_RATE * ms / 1000;
    int count = lines[track].write(bytes, 0, length);
  }

  private void rest(Integer track, int ms) {
    this.play(track,new byte[0],ms);
  }
//  private static void rest(SourceDataLine line, int ms) {
//    ms = Math.min(ms, SECONDS * 1000);
//    int length = SAMPLE_RATE * ms / 1000;
//    int count = line.write(new byte[length], 0, length);
//  }
//
//  private static void play(SourceDataLine line, byte[] bytes, int ms) {
//    ms = Math.min(ms, SECONDS * 1000);
//    int length = SAMPLE_RATE * ms / 1000;
//    int count = line.write(bytes, 0, length);
//  }

  private static byte[] wave(Double frequency) {

    byte[] sin = new byte[SECONDS * SAMPLE_RATE];
    for (int i = 0; i < sin.length; i++) {
      double period = (double)SAMPLE_RATE / frequency;
      double angle = 2.0 * Math.PI * i / period;
      sin[i] = (byte)(Math.sin(angle) * 127f);
    }
    return sin;
  }

  private static double hz(Integer val) {
//    Integer val = Integer.parseInt(s);
    double exp = ((double) val - 69) / 12d;
    double f = 440d * Math.pow(2d, exp);
    return f;
  }
}
