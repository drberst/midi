package com.berst;

import com.berst.Compose.Composer;
import com.berst.Model.Note;
import com.berst.Model.Note.nType;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map.Entry;
import java.util.TreeMap;
import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiEvent;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.Sequence;
import javax.sound.midi.Track;


public class Midi {

  public static final int NOTE_ON = 0x90;
  public static final int NOTE_OFF = 0x80;

  public static boolean fillInGaps = false;
  public static boolean compactTicks = false;
  public static boolean print = true;
  public static int baseNote = 16;
  public static ArrayList<Note> events = new ArrayList<>();


  public static void log(String toLog) {
    System.out.printf("LOG: [%s]\n", toLog);
  }

  public static void log(String logname, String toLog) {
    System.out.printf("%s: [%s]\n", logname, toLog);
  }

  public static void main(String[] args) throws Exception {
    Note.notesOnly = true;
    Composer c = readMidi("game_over.mid");
    c.applyDefaultSeq();
//    System.out.println(c);
//    Player.setBPM(89*4);
//    Player p = new Player(c.tracks());
//
//    p.playComposition(c);
    StdAudio.playComposition(c);
  }

  public static Composer readMidi(String midifilename)
      throws InvalidMidiDataException, IOException {
    Sequence sequence = MidiSystem.getSequence(new File(midifilename));

    Composer c = new Composer();
//    Note.notesOnly = true;
    int ticksPerQuarter = sequence.getResolution();
    int ticksPerEvent = ticksPerQuarter * 4 / baseNote;
    int quartersPerBar = 2;

    ArrayList<Note> heldNotes = new ArrayList<>();

//    for (Track track :  sequence.getTracks()) {
//      if (track.size() == 1 ) continue;
//      System.out.println("Track " + trackNumber + ": size = " + track.size() +" ---------------------------------------------------------");
    int tick = 0;

    StringBuilder line = new StringBuilder();
    StringBuilder prefix = new StringBuilder();
    int maxtracklen = 0;
//    String[] tracknames = new String[sequence.getTracks().length];
//    int tracknum = 1;
    TreeMap<Long, ArrayList<Note>> mapTickToEvent = new TreeMap<>();
//    for (Track track : sequence.getTracks()) {
    Track[] allTracks = sequence.getTracks();
    for (int tI = 0; tI < allTracks.length; tI++) {
      Track track = allTracks[tI];

      if (track.size() > maxtracklen) {
        maxtracklen = track.size();
      }
      for (int i = 0; i < track.size(); i++) {
        Note n = new Note(track.get(i));

        if (n.type == nType.ON || n.type == nType.OFF) {
          if (mapTickToEvent.containsKey(n.tick)) {
            mapTickToEvent.get(n.tick).add(n);
          } else {
            ArrayList notes = new ArrayList();
            notes.add(n);
            mapTickToEvent.put(n.tick, notes);
          }
        } else {
          events.add(n);
        }
      }
    }
    if (print) {
      for (Entry<Long, ArrayList<Note>> beep : mapTickToEvent.entrySet()) {
        System.out.printf("\n%5d ----------------------\n       ", beep.getKey());
        for (Note n : beep.getValue()) {
          System.out.printf("%s ", n);
        }
      }
    }
    if (print) System.out.println();
      while (tick < maxtracklen - 2) {
        int tracknum = 0;
        if (print) {
          System.out
              .println("---------------------------- " + tick + " ----------------------------");
        }
        for (Track track : sequence.getTracks()) {
          tracknum++;
          if (track.size() == 1 || tick > track.size() - 2) {
            continue;
          }

          MidiEvent event = track.get(tick);
          int eventTick = (int) event.getTick() / (ticksPerEvent);

          Note note = new Note(event);

          addToComposerIfNeeded(note, heldNotes, c);
          prefix.append(String.format("T[%2d] ", tracknum));
          prefix.append(timestamp(event.getTick(), ticksPerQuarter));
          line.append(note);

          if (compactTicks) {
            int temptick = tick;
            while (track.get(temptick + 1).getTick() - event.getTick() == 0) {
              event = track.get(++temptick);
              note = new Note(event);
              addToComposerIfNeeded(note, heldNotes, c);
              //.append("\n\t\t").append(" -> ").append("@").append(tick)
              if (note.type == nType.ON || note.type == nType.OFF) {
                line.append(" ").append(note);
              }
            }
          }
          String heldNotesStr = "empty";
          if (print) {
            heldNotesStr = heldNotes.toString()
                .replace("[", "")
                .replace("]", "")
                .replace(",", "")
                .replace("+", "");
            System.out
                .printf("%-24s:%24s: %-10s\n", prefix.toString(), heldNotesStr, line.toString());
          }
          line = new StringBuilder();
          prefix = new StringBuilder();

          if (fillInGaps) {
            long dif = (track.get(tick + 1).getTick() - event.getTick()) / ticksPerEvent;
            for (int i = 1; i <= dif; i++) {
              int tempTick = eventTick * ticksPerEvent + i * ticksPerEvent;
              if (print) {
                String timestamp = timestamp(tempTick, ticksPerQuarter);
                System.out.printf("%-24s|%12s :\n", timestamp, heldNotesStr);
              }
            }
          }


        } // end of tracks
        tick++;
      } // end of tick
//    for (String mapkey : mapToEnd.keySet()) {
//      int note = Integer.parseInt(mapkey.split(":")[0]);
//      c.addNote(note,mapHeldNote2Start.get(mapkey),mapToEnd.get(mapkey));
//    }
      if (print) {
        System.err.println(events.toString().replaceAll("],", "]\n"));
      }
      return c;
    }

    public static void addToComposerIfNeeded (Note note, ArrayList < Note > heldNotes, Composer c){
      if (note.type == nType.ON) {
        heldNotes.add(note);
      } else if (note.type == nType.OFF) {
        for (int i = 0; i < heldNotes.size(); i++) {
          Note n = heldNotes.get(i);
          if (note.val == n.val) {
            heldNotes.remove(i);

            c.addNote(note.val, n.get16th(), note.get16th());
            break;
          }
        }
      } else {
        events.add(note);
      }
    }

    public static String timestamp ( long tick, int ppq){
      // 1/4  = 480
      // 8th  = 240
      // 16th = 120
      // 32nd = 60
      long temp = tick;
      long[] sizes = {
          ppq * 2,  // 1
          ppq * 1,  // 1/4
          ppq / 2,  // 1/8
          ppq / 4}; // 1/16
      long[] results = new long[4];
      long sixteenth = sizes[3];
      long prox = temp % sixteenth;
      long maxdif = sixteenth / 2;
      if (sixteenth - prox < maxdif) {
        temp += maxdif;
      }
      for (int i = 0; i < sizes.length; i++) {
        long divider = sizes[i];
        if (divider == 0) {
          break;
        }
        results[i] = temp / divider;
        temp = temp % divider;
      }

//    String rem = temp > 0 ? String.format("+%d%%",100*temp/sixteenth) : "";
//    String rem = temp > 0 ? String.format("+%d/%d",temp,sixteenth) : "";
      long offby = maxdif - temp;
      String rem = offby != maxdif ? "-" + offby : "";
      return String.format("t%4d:%s%2s", tick, Arrays.toString(results), rem);
    }


  }