package com.berst.Model;

import javax.sound.midi.MidiEvent;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.ShortMessage;


public class Note {

  public static final int NOTE_ON = 0x90;
  public static final int NOTE_OFF = 0x80;
  public static final int CONTROL_CHANGE = 0xb0;
  public static final int PRGM_CHANGE = 0xc0;

  public static final String[] NOTE_SHARP = {"C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A",
      "A#", "B"};
  public static final String[] NOTE_FLAT = {"C", "Db", "D", "Eb", "E", "F", "Gb", "G", "Ab", "A",
      "Bb", "B"};
  public static boolean notesOnly = false;
  public static boolean flat = false;
  public static int resolution = 480;

  public int val;
  public long tick;
  public byte[] data;

  public int velocity;
  public int channel;
  public nType type;

  public Note(String input) {
    this(valFromString(input));
  }

  public Note(int i) {
    val = i;
    type = nType.ON;
    velocity = 100;
    tick = -1;
  }
  private static int valFromString(String input) {
    String pattern = "([ABCDEFG#b]+)(\\d)";
    String note = input.replaceFirst(pattern,"$1");
    String octave = input.replaceFirst(pattern,"$2");
    int noteval = 69;
    int octave_val = 12+12*4;
    if (octave.matches("\\d"))
      octave_val = 12+12*Integer.parseInt(octave);
    for (int i = 0; i < NOTE_SHARP.length; i++) {
      if(note.equals(NOTE_SHARP[i]) || note.equals(NOTE_FLAT[i])) {
        noteval = i;
      }
    }
    return noteval+octave_val;
  }
  public static void main(String[] args) {

//    for (int i = 0; i < 10; i++) {
      for (int j = 0; j < NOTE_SHARP.length; j++) {
        String sharp = NOTE_SHARP[j];
        String flat = NOTE_FLAT[j];
        Note ns = new Note(sharp);
        if (!sharp.equals(flat))
          ns = new Note(flat);

      }
//    }

  }

  public Note(MidiEvent event) {
    MidiMessage message = event.getMessage();
    data = message.getMessage();
    tick = event.getTick();
    if (message instanceof ShortMessage) {
      ShortMessage sm = (ShortMessage) message;
      if (sm.getCommand() == NOTE_ON || sm.getCommand() == NOTE_OFF) {
        val = sm.getData1();
        velocity = sm.getData2();
//        int octave = (key / 12)-1;
//        int note = key % 12;
//        String smallString = NOTE_NAMES[note];
//        String mapkey = String.format("%d",key,velocity);
//        String onOff = "+";
        if (sm.getCommand() == NOTE_OFF || velocity == 0) { // NOTE OFF
          type = nType.OFF;
        } else { // NOTE ON
          type = nType.ON;
        }
      } else {
        val = sm.getCommand();
        channel = sm.getChannel();
        type = nType.EVENT;
      }
    } else { // END OF SHORT MESSAGE
      if (data[1] == 88) {
        type = nType.MISC_TIMESIG;
      } else if (data[1] == 8) {
        type = nType.MISC_TITLE;
      } else if (data[1] == 81) {
        type = nType.MISC_TEMPO;
      } else if (data[1] == 89) {
        type = nType.MISC_KEY;
        if ((int)data[3] < 0) Note.flat = true;
      } else {
        type = nType.OTHER;
      }
    }
  }

  public String getNoteName() {
    return flat ? NOTE_FLAT[getNoteInt()] : NOTE_SHARP[getNoteInt()];
  }

  public int getNoteInt() {
    return val % 12;
  }

  public int getOctave() {
    return (val / 12) - 1;
  }
  public int get16th() {
    return (int) Math.round(tick*4F/resolution);
  }

  private static byte[] getWave(Double frequency, int ms, int sampleRate) {
    int adjusted = (int) (sampleRate / 1000d * ms);
    byte[] sin = new byte[adjusted];
    for (int i = 0; i < sin.length; i++) {
      double period = (double) sampleRate / frequency;
      double angle = 2.0 * Math.PI * i / period;
      sin[i] = (byte) (Math.sin(angle) * 128f);
    }
    return sin;
  }


  public double getHz() {
    double exp = ((double) val - 69) / 12d;
    double f = 440d * Math.pow(2d, exp);
    return f;
  }

  public String smallString() {
    return getNoteName() + getOctave();
  }
  @Override
  public String toString() {

    if (type == nType.ON || type == nType.OFF) {
      String identifier = type == nType.ON ? "+" : "-";
      return String.format("[%s%s]", identifier, getNoteName() + getOctave(), val);
//      return String.format("[%s%s:%d %3.2fhz]", identifier, getNoteName() + getOctave(), val,getHz());
    } else if (notesOnly) {
      return "";
//      return type.toString();
    } else if (type == nType.EVENT) {
      if(val == CONTROL_CHANGE) return String.format("[CTRL_%d]", channel);
      if(val == PRGM_CHANGE)    return String.format("[PRGM_%d]", channel);
      return String.format("%s[%d]", "e", val);
    } else {
      if (type == nType.MISC_TIMESIG) {
        return String.format("[Time: %d/%d %dppq 32:%d]", data[2], data[3], data[5], data[6]);
      } else if (type == nType.MISC_TITLE) {
        return "[Title: " + new String(data).substring(3) + "]";
      } else if (type == nType.MISC_TEMPO) {
        int usPerBeat = (data[5] & 0xFF) | ((data[4] & 0xFF) << 8) | ((data[3] & 0x0F) << 16);
        double SPB = usPerBeat/1000000D;
        double MPB = SPB/60D;
        double BPM = (1/MPB);
        return String.format("[Tempo:%.0f]",BPM);
      } else if (type == nType.MISC_KEY) {
        String quality = data[4] == 0 ? "M" : "m";
        return String.format("[Key:%d%s]", data[3], quality);
      } else {
        StringBuilder sb = new StringBuilder();
        for (byte b : data) {
          sb.append(String.format("%02x ", b));
        }
        return String.format("META: " + sb.toString());
      }
    }
  }

  public enum nType {
    ON, OFF, EVENT, MISC_TIMESIG, MISC_TITLE, MISC_KEY, MISC_TEMPO, OTHER;
  }


}
