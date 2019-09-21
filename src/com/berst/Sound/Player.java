package com.berst.Sound;

import com.berst.Compose.Composer;
import com.berst.Compose.Translator;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

public class Player {

  static final int SAMPLE_RATE = 16 * 1024; // ~16KHz
  //  static final int SECONDS = 2;
  static final AudioFormat af = new AudioFormat(SAMPLE_RATE, 16, 1, true, true);
//  long clickTime = System.nanoTime();

  //  SourceDataLine[] lines;
  ArrayList<SourceDataLine> lines;
  public static Integer BPM = 300;
  public static Integer ticklen = 250;//(int) Math.round((60f / BPM) * 1000);
  private int beat = 0;
  public ArrayList<Double> linePos;

  public Player() {
    lines = new ArrayList<>();
    addLines(1);
  }

  public Player(Integer numberOfLines) {
    lines = new ArrayList<>();
    linePos = new ArrayList<>();
    System.err.println(ticklen);
    addLines(numberOfLines);
  }

  public static void main(String[] args) throws LineUnavailableException {
    String[] tab = new String[]{
        //1 2 3 4 2 2 3 4 3 2 3 4 4 2 3 4 5 2 3 4 6 2 3 4 7 2 3 4 8 2 3 4 9 2 3 4
        //|       |       |       |       |       |       |       |       |
        "E||-----------------------|-----------------------|-----------------------|-----------------------|-----------------------|------------------3----|--2-------3p-2-0---------|---------------------0--|--0----0----0----5--2--|--2------------2--3--5--|--5---------3----5--2--|----------------------|----------------------|--0-----0--1-----1-----0--|--0-----3----3----|--0-----0--1-----1-----0--|--0-----3----3----|--0-----0--1-----1-----0--|--0-----3----3----|--0-----0--1-----1-----0--|--0-----3----3----|--0---------------------|-------------------------|--2---------------------|---------------------------||",
        "B||-----------------------|-------------0---------|--3-------1--1----0----|-----------------------|-----------------------|-----0-----------------|-------------------------|---------------1--3-----|-----------------------|------------------------|-----------------------|----------------------|----------------------|--0--3--0--1-----1-----0--|--0--3--3----3----|--0--3--0--1-----1-----0--|--0--3--3----3----|--0--3--0--1-----1-----0--|--0--3--3----3----|--0--3--0--1-----1-----0--|--0--3--3----3----|------------------------|-------------------------|------------------------|-----0---------------------||",
        "G||------------------0----|-----2------------2----|-----------------------|-----2-------0----2----|----------0-------2----|-------------2----0----|-------------------------|------------------------|-----------------------|------------------------|-----------------------|----------------------|----------------------|-----2--------------------|-----2------------|-----2--------------------|-----2------------|-----2--------------------|-----2------------|-----2--------------------|-----2------------|--4----4-----2----4-----|-----4-----2--5----4--2--|--4----4-----2----4-----|-----------2--0-----------*||",
        "D||--2-------4------------|-----------------------|-----------------------|-----------------------|--4--------------------|-----------------------|-------------------------|------------------------|-----------------------|------------------------|-----------------------|----------------------|----------------------|--------------------------|------------------|--------------------------|------------------|--------------------------|------------------|--------------------------|------------------|----------4-------------|--------4----------------|----------4-------------|--------4--------4--2-----*||",
        "A||-----------------------|-----------------------|-----------------------|-----------------------|-----------------------|-----------------------|--3----3-------3----3----|--3----3----3-----2--0--|--0----0----0----0--2--|--0----0----0-----0-----|--------------------2--|--2----2----2----2----|--2----2----2----2----|--------------------------|------------------|--------------------------|------------------|--------------------------|------------------|--------------------------|------------------|--2------------------2--|--2----------------------|--2------------------2--|--2--------------------2---||",
        "E||--0----0-----0----0----|--0-----0----0----0----|--1----1-----1----1----|--1-----1----1----1----|--0----0-----0----0----|--0-----0----0----0----|-------------------------|------------------------|-----------------------|---------------------1--|--1----1----1----1-----|----------------------|----------------------|--0--------1-----1-----0--|--0-----1----1----|--0--------1-----1-----0--|--0-----1----1----|--0--------1-----1-----0--|--0-----1----1----|--0--------1-----1-----0--|--0-----1----1----|------------------------|-------------------------|------------------------|---------------------------||"
    };

    String[] bass = new String[]{
        "|----------------------------------------------------3------5--------|",
        "|--3--3---3-5-7------------------------------------------------------|",
        "|----------------0-0-----------------------0----1--1---1/3----3--3~--|",
        "|--------------------------------------------------------------------|"
    };
    Composer c = Translator.fromTab(tab);
    Composer c2 = Translator.fromBassTab(bass);
//    Composer c3 = Composer.merge(c,c2);
//    c.applyDefaultSeq();
//    c.applySeqChord();

//    System.out.println(c.getNote());
    Player tones = new Player(4);

    tones.playComposition(c);
//    c.pianoRoll();
  }

///////////////////////////////////////////////////////////////////////////////////////////////////

  public void playComposition(Composer aComposer) throws LineUnavailableException {
//    click();
    open();
    start();
    for (int tick = 0; tick < aComposer.ticks(); tick++) {
    StringBuilder sb = new StringBuilder();
//      printLines();
      ArrayList<Integer> aLine = aComposer.getNotes(tick);
      if (lines.size() < aLine.size()) {
        addLines(lines.size() - aLine.size());
      }
      sb.append(String.format("%3d: ", tick));

      for (int i = 0; i < aLine.size(); i++) {
        int note = aLine.get(i);
        double freq = hz(note);

        int written = play(i, freq, ticklen);
//        int written = send(i, wave(freq), ticklen);
        sb.append(String.format("[%d:%2d]",i,note));
      }
    System.out.println(sb.toString());
    }
    close();
  }


  ///////////////////////////////////////////////////////////////////////////////////////////////////
  private int send(byte[] bytes, int ms) {
    int track = 0;
//    SourceDataLine line = lines.getNote(track);
//    while(line.getMicrosecondPosition() > 0) {
//      if(++track >= lines.size()) addLines();
////      System.out.printf("[w%d] ",track);
//      line = lines.getNote(track);
//    }
//    System.out.println();

    return send(track, bytes, ms);
  }

  private int send(Integer track, byte[] bytes, int ms) {
//    System.out.printf("[w%d]\n",track);
//    ms = Math.min(ms, SECONDS * 1000);
//    int length = SAMPLE_RATE;
    return lines.get(track).write(bytes, 0, bytes.length);
  }


//  double fCyclePosition = 0;

  private int play(Integer track, double fFreq, int ms) {
    SourceDataLine line = lines.get(track);
    double fCyclePosition = linePos.get(track);
    int ctSamplesTotal = Math.round(SAMPLE_RATE * (ms / 1000f));
    ByteBuffer cBuf = ByteBuffer.allocate(line.getBufferSize());

    //On each pass main loop fills the available free space in the audio buffer
    //Main loop creates audio samples for sine wave, runs until we tell the thread to exit
    //Each sample is spaced 1/SAMPLING_RATE apart in time
    while (ctSamplesTotal > 0) {
      double fCycleInc = fFreq / SAMPLE_RATE;  // Fraction of cycle between samples

      cBuf.clear();                            // Discard samples from previous pass

      // Figure out how many samples we can add
      int ctSamplesThisPass = line.available() / 2;
      for (int i = 0; i < ctSamplesThisPass; i++) {
        cBuf.putShort((short) (Short.MAX_VALUE * Math.sin(2 * Math.PI * fCyclePosition)));

        fCyclePosition += fCycleInc;
        if (fCyclePosition > 1) {
          fCyclePosition -= 1;
        }
      }

      //Write sine samples to the line buffer.  If the audio buffer is full, this will
      // block until there is room (we never write more samples than buffer will hold)
      line.write(cBuf.array(), 0, cBuf.position());
      ctSamplesTotal -= ctSamplesThisPass;     // Update total number of samples written

      //Wait until the buffer is at least half empty  before we add more
      while (line.getBufferSize() / 2 < line.available()) {
        try {
          Thread.sleep(1);
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
      }
    }
    linePos.set(track, fCyclePosition);
    return Math.round(SAMPLE_RATE * (ms / 1000f));
  }

///////////////////////////////////////////////////////////////////////////////////////////

  private void addLines(Integer numLines) {
    try {
      for (int i = 0; i < numLines; i++) {
        SourceDataLine line = AudioSystem.getSourceDataLine(af);
        lines.add(line);
        linePos.add(0D);
      }
    } catch (LineUnavailableException e) {
      e.printStackTrace();
    }
  }

  public void start() {

    for (SourceDataLine line : lines) {
      line.start();
    }
    System.out.printf("started %d lines\n", lines.size());
  }

//  public void stop() {
//    for (SourceDataLine line : lines) {
//      while (line.isActive()) {
//      }
//      ;
////      line.stop();
//    }
//    System.out.printf("stopped %d lines\n", lines.size());
//  }

  public void open() throws LineUnavailableException {
    for (SourceDataLine line : lines) {
      line.open(af, SAMPLE_RATE);
//      line.start();
    }
    System.out.printf("opened %d lines\n", lines.size());
  }

  public void close() throws LineUnavailableException {
    for (SourceDataLine line : lines) {
      line.drain();
      line.close();
    }
    System.out.printf("closed %d lines\n", lines.size());
  }

  //  public boolean isPlaying() {
//    boolean result = false;
//    for (SourceDataLine line : lines ){
//      if(line.available() < line.getBufferSize()) {
//        result = true;
//        System.out.printf("[L%d: %.2f%% (%d/%d) %.2fms]\n"
//            ,0,100f-100f*line.available()/line.getBufferSize(),
//            line.available(),line.getBufferSize(),
//            line.getMicrosecondPosition()/1000.0);
//      }
//    }
//    return result;
//  }
  private static byte[] wave(byte[] waveA, byte[] waveB) {
    int target = waveA[waveA.length];
    Math.asin(target);
    return waveB;
  }

  private static byte[] wave(Double frequency) {
    int adjusted = (int) (SAMPLE_RATE / 1000d * ticklen);
    byte[] sin = new byte[adjusted];
    for (int i = 0; i < sin.length; i++) {
      double period = (double) SAMPLE_RATE / frequency;
      double angle = 2.0 * Math.PI * i / period;
      sin[i] = (byte) (Math.sin(angle) * 128f);
    }
    return sin;
  }


  private static double hz(Integer val) {
//    Integer val = Integer.parseInt(s);
    double exp = ((double) val - 69) / 12d;
    double f = 440d * Math.pow(2d, exp);
    return f;
  }
//
//  private long elapsed() {
//    return (System.nanoTime() - clickTime) / 1000000;
//  }
//
//  private void click() {
//    clickTime = System.nanoTime();
//  }

  //  private static void
  private static void printWave(byte[] wave, int w, int h) {
    double step = (1f * wave.length / w);
    int len = (int) Math.round(wave.length / step);
    char[][] lines = new char[h][len];

    for (int i = 0; i < lines[0].length; i++) {
      int index = ab2cdShift(i, 0, w, 0, wave.length);
      int data = (int) wave[index];
//      System.out.println(index);
      int shifted = ab2cdShift(data, -128, 127, 0, h - 1);
      lines[shifted][i] = '.';
    }
    for (int i = 0; i < h; i++) {
      String out = new String(lines[i]);
      System.out.printf("%2d| %s\n", i, out);
    }
    System.out.println();
  }

  private static void printWave2(byte[] wave, int w, int h) {
    boolean flipped = false;
    if (w < 0) {
      flipped = true;
      w *= -1;
    }

    char[][] lines = new char[h][w];

    for (int i = 0; i < w; i++) {
      int data = (int) wave[i];
      if (flipped) {
        data = (int) (wave[wave.length - w + i]);
      }
//      System.out.println(index);
      int shifted = ab2cdShift(data, -128, 127, 0, h - 1);
//      int index = ab2cdShift(i,0,w,0, wave.length);
      lines[shifted][i] = '.';
    }
    for (int i = 0; i < h; i++) {
      String out = new String(lines[i]);
      System.out.printf("%2d| %s\n", i, out);
    }
    System.out.println();
  }

  private static void printWave3(byte[] wave, int w, int h) {
    char[] base = new char[w];
    for (int i = 0; i < w; i++) {
      base[i] = '.';
    }
    String s = "...............................";
    for (int i = 0; i < wave.length; i++) {

      int data = wave[i];
      int loc = ab2cdShift(data, -128, 127, 0, w - 1);
      //      System.out.printf("%"+loc+"s\n",'.');
      base[loc] = '0';
      System.out.printf("%4d: %s\n", i, new String(base));
      base[loc] = '.';

      if (i == h) {
//        System.out.println("--------------------- Last ");
        i = wave.length - h;
      }
    }
  }

  private static int ab2cdShift(int val, int a, int b, int c, int d) {
    // System.out.printf("%d+(%d-%d)/(%d-%d)*(%d-%d)\n",c,d,c,b,a,val,a);
    double shifted = Math.round((c + 1.0 * (d - c) / (b - a) * (val - a)));
//    System.out.printf("%d->%.2f\n",val,shifted);
    return (int) shifted;
  }

  private void printLines() {
    for (int i = 0; i < lines.size(); i++) {
      SourceDataLine line = lines.get(i);
      System.out.printf("[L%d: %.2f%% (%d/%d) %.2fms]\n"
          , i, 100f - 100f * line.available() / line.getBufferSize(),
          line.getBufferSize() - line.available(), line.getBufferSize(),
          line.getMicrosecondPosition() / 1000.0);
    }
  }
}
