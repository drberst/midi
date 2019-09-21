package com.berst;

import java.util.ArrayList;
import java.util.HashMap;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

public class TonePlayer {
  static final int SAMPLE_RATE = 16 * 1024; // ~16KHz
  static final int SECONDS = 6;
  static final AudioFormat af = new AudioFormat(SAMPLE_RATE, 8, 1, true, true);

//  SourceDataLine[] lines;
  ArrayList<SourceDataLine> lines;

  public static void main(String[] args) throws LineUnavailableException {
    playTones(args);
    if(true) return;
    Composer c = new Composer();
//    c.applyDefaultSeq();
    c.applySeqChord();
//    Integer[][] matrix = c.matrix;

     HashMap<Integer, ArrayList<Integer>> notes = c.toMap();
    System.out.println(notes);
    TonePlayer tones = new TonePlayer();
//    tones.play(0,wave(hz(40)),1000);
    tones.open();
    for (HashMap.Entry<Integer, ArrayList<Integer>> entry : notes.entrySet()) {
      Integer tick = entry.getKey();
      ArrayList<Integer> notesThisTick = entry.getValue();
      for (Integer note : notesThisTick) {
        tones.play(wave(hz(note)),250);
      }
    }

    c.pianoRoll();
    tones.close();

  }

  public static void playTones(String[] args) throws LineUnavailableException {
    TonePlayer tones = new TonePlayer();
    tones.open();

    tones.play(wave(hz(60)),1000);
    tones.play(wave(hz(60+3)),1000);
    tones.play(wave(hz(60+5)),1000);


    tones.close();
  }

  public TonePlayer() {
    lines = new ArrayList<>();
    addMoreLines();
  }



  public void open() throws LineUnavailableException {
    for (SourceDataLine line : lines) {
      line.open(af, SAMPLE_RATE);
      line.start();
    }
    System.out.printf("opened %d lines:\n%s",lines.size(),lines.toString());
  }

  public void close() throws LineUnavailableException {
    for (SourceDataLine line : lines) {
        line.drain();
        line.close();
    }
    System.out.printf("closed %d lines:\n",lines.size());
  }

  public void addMoreLines() {
    try {
      SourceDataLine line = AudioSystem.getSourceDataLine(af);
      if (lines.size() > 0 && lines.get(0).isOpen()) {
        System.out.println("Opening line: "+lines.size());
        line.open(af, SAMPLE_RATE);
        line.start();
      }
      lines.add(line);

    } catch (LineUnavailableException e) {
      e.printStackTrace();
    }
  }

  private void play(byte[] bytes, int ms) {
    int track = 0;
    SourceDataLine line = lines.get(track);
    while(line.isRunning()) {
      if(++track >= lines.size()) addMoreLines();
      line = lines.get(track);
    }
    play(track, bytes, ms);
  }

  private void play(Integer track, byte[] bytes, int ms) {
    System.out.println("Writing to open line "+track);
    ms = Math.min(ms, SECONDS * 1000);
    int length = SAMPLE_RATE * ms / 1000;
    int count = lines.get(track).write(bytes, 0, length);
  }

  private void rest(Integer track, int ms) {
    this.play(track,new byte[0],ms);
  }

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
