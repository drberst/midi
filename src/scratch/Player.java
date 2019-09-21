package com.berst;

import java.util.ArrayList;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

public class Player {
  static final int SAMPLE_RATE = 16 * 1024; // ~16KHz
  static final int SECONDS = 2;
  static final AudioFormat af = new AudioFormat(SAMPLE_RATE, 8, 1, true, true);

//  SourceDataLine[] lines;
  ArrayList<SourceDataLine> lines;
  public static Integer ticklen = 500;

  public Player() {
    lines = new ArrayList<>();
    addMoreLines();
  }

  public static void main(String[] args) throws LineUnavailableException {
    Composer c = new Composer();
//    c.applyDefaultSeq();
    c.applySeqChord();
    System.out.println(c.get());
    Player tones = new Player();
    tones.open();
    tones.start();
    tones.playComposition(c);
    c.pianoRoll();
    tones.close();
  }

  public static void playTones(String[] args) throws LineUnavailableException {
    Player tones = new Player();
    tones.open();
    tones.play(wave(262.0),1000);
    tones.play(wave(393.0),1000);
    tones.play(wave(327.5),1000);

    tones.start();
    tones.close();
  }

  public void playComposition(Composer aComposer) {
    for (int tick = 0; tick < aComposer.ticks(); tick++) {
      playLine(aComposer.get(tick));
    }
  }

  public void playLine(ArrayList<Integer> aLine) {
//    this.start();
    for (Integer note : aLine) {
      play(wave(hz(note)),ticklen);
    }
//    this.stop();
//    this.stop();
  }


///////////////////////////////////////////////////////////////////////////////////////////////////
  private void play(byte[] bytes, int ms) {
    int track = 0;
    SourceDataLine line = lines.get(track);
    while(line.getMicrosecondPosition() > 0) {
      if(++track >= lines.size()) addMoreLines();
//      System.out.printf("[w%d] ",track);
      line = lines.get(track);
    }
//    System.out.println();

    play(track, bytes, ms);
  }

  private void play(Integer track, byte[] bytes, int ms) {
//    System.out.printf("[w%d]\n",track);
//    ms = Math.min(ms, SECONDS * 1000);
    int length = SAMPLE_RATE * ms / 1000;
    int count = lines.get(track).write(bytes, 0, length);
  }

  private void rest(Integer track, int ms) {
    this.play(track,new byte[0],ms);
  }



  private void addMoreLines() {
    try {
      SourceDataLine line = AudioSystem.getSourceDataLine(af);

      if (lines.size() > 0 && lines.get(0).isOpen()) {
        System.out.println("Opening line: "+lines.size());
        line.open(af, SAMPLE_RATE);
        line.start();
      }
      lines.add(line);
      printLines();
    } catch (LineUnavailableException e) {
      e.printStackTrace();
    }
  }

  private void printLines() {
    for (int i = 0; i < lines.size(); i++) {
      SourceDataLine line = lines.get(i);
      System.out.printf("[L%d: _%d _%d %.2fms]\n"
          ,i,line.getBufferSize(),line.available(),line.getMicrosecondPosition()/1000.0);
    }
  }

///////////////////////////////////////////////////////////////////////////////////////////

  public void start() {
    for (SourceDataLine line : lines) {
      line.start();
    }
    System.out.printf("started %d lines\n",lines.size());
  }

  public void stop() {
    for (SourceDataLine line : lines) {
      while(line.isActive()){};
//      line.stop();
    }
    System.out.printf("stopped %d lines\n",lines.size());
  }

  public void open() throws LineUnavailableException {
    for (SourceDataLine line : lines) {
      line.open(af, SAMPLE_RATE);
//      line.start();
    }
    System.out.printf("opened %d lines\n",lines.size());
  }

  public void close() throws LineUnavailableException {
    for (SourceDataLine line : lines) {
      line.drain();
      line.close();
    }
    System.out.printf("closed %d lines\n",lines.size());
  }
  public boolean isPlaying() {
    boolean result = false;
    for (SourceDataLine line : lines ){
      if(line.isActive()) result = true;
    }
    return result;
  }

  private static byte[] wave(Double frequency) {

    byte[] sin = new byte[(int) (ticklen/1000.0* SAMPLE_RATE)];
    System.err.println(sin.length);
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
