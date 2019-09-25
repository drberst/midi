package com.berst.Sound;

import com.berst.Compose.Composer;
import com.berst.Midi;
import com.berst.Model.Note;
import com.berst.Wave.MergedWave;
import com.berst.Wave.Sinewave;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

public class MergedWavePlayer {
  static final int SAMPLE_RATE = 16 * 1024; // ~16KHz
  static final AudioFormat af = new AudioFormat(SAMPLE_RATE, 16, 1, true, true);
  static SourceDataLine line;

  public static void main(String[] args) throws Exception{
    Composer c = Midi.readMidi("neverEndingJourney.mid");
    MergedWavePlayer mp = new MergedWavePlayer();

    mp.playComposition(c);
  }

  public void playComposition(Composer c) throws LineUnavailableException {
    open();start();
    var pianoroll = c.pianoRollLines();
    System.out.println(c.pianoRollHeader(5));
    for (int tick = 0; tick < c.ticks(); tick++) {
      ArrayList<Integer> notesThisTick = c.getNotes(tick);
      System.out.printf("%3d: %s  ",tick,pianoroll[tick]);
      ArrayList<Sinewave> wavesThisTick = new ArrayList<>();
      for (Integer noteval : notesThisTick) wavesThisTick.add(new Sinewave(noteval));
      for (Integer noteval : notesThisTick) {
        System.out.print(new Note(noteval)+" ");
      }
      System.out.println();
      MergedWave mWave = new MergedWave(wavesThisTick);

//      send(mWave,1);

    }
  }
  private int send(MergedWave mWave, int ms) {
//    System.out.printf("[w%d]\n",track);
//    ms = Math.min(ms, SECONDS * 1000);
    int length = SAMPLE_RATE;
    byte[] bytes = mWave.getBytes(length);
    return line.write(bytes, 0, bytes.length);
  }

  double fCyclePosition = 0;
  private int play(Integer track, double fFreq, int ms) {
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
    return Math.round(SAMPLE_RATE * (ms / 1000f));
  }

  public void start() throws LineUnavailableException {
    if(line==null) line = AudioSystem.getSourceDataLine(af);
    line.start();

    System.out.println("started line");
  }

  public void open() throws LineUnavailableException {
    if(line==null) line = AudioSystem.getSourceDataLine(af);

    line.open(af, SAMPLE_RATE);
    System.out.println("opened line");
  }

  public void close() throws LineUnavailableException {
    line.drain();
    line.close();
    System.out.println("closed line");
  }

}
