package com.berst.Model;

import javax.sound.midi.MidiEvent;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.ShortMessage;


public class Note {

  public static final int NOTE_ON = 0x90;
  public static final int NOTE_OFF = 0x80;
  public static final String[] NOTE_SHARP = {"C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A",
      "A#", "B"};
  public static final String[] NOTE_FLAT = {"C", "Db", "D", "Eb", "E", "F", "Gb", "G", "Ab", "A",
      "Bb", "B"};
  public static boolean notesOnly = false;
  public static boolean flat = false;

  public int val;
  public long tick;
  public byte[] data;

  public int velocity;
  public nType type;

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
//        String noteName = NOTE_NAMES[note];
//        String mapkey = String.format("%d",key,velocity);
//        String onOff = "+";
        if (sm.getCommand() == NOTE_OFF || velocity == 0) { // NOTE OFF
          type = nType.OFF;
//          if (mapHeldNote2Start.containsKey(mapkey)) {
//            c.addNote(key,mapHeldNote2Start.remove(mapkey),eventTick);
//          } else {
//            mapToEnd.put(mapkey,eventTick);
//          }
        } else { // NOTE ON
          type = nType.ON;
        }
      } else {
        val = sm.getCommand();
        type = nType.EVENT;
      }
    } else { // END OF SHORT MESSAGE
      if (data[1] == 88) {
        type = nType.MISC_TIMESIG;
      } else if (data[1] == 8) {
        type = nType.MISC_TITLE;
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

  @Override
  public String toString() {

    if (type == nType.ON || type == nType.OFF) {
      String identifier = type == nType.ON ? "+" : "-";
      return String.format("[%s%s]", identifier, getNoteName() + getOctave(), val, velocity);
    } else if (notesOnly) {
      return "";
//      return type.toString();
    } else if (type == nType.EVENT) {
      return String.format("%s[%d]", "e", val);
    } else {
      if (type == nType.MISC_TIMESIG) {
        return String.format("[Time: %d/%d %dppq 32:%d]", data[2], data[3], data[5], data[6]);
      } else if (type == nType.MISC_TITLE) {
        return "[Title: " + new String(data).substring(3) + "]";
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
    ON, OFF, EVENT, MISC_TIMESIG, MISC_TITLE, MISC_KEY, OTHER;
  }


}
