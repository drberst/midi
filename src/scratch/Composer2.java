package com.berst;

import java.util.ArrayList;
import java.util.HashMap;
import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiEvent;
import javax.sound.midi.Sequence;
import javax.sound.midi.ShortMessage;

public class Composer2 {

//  Track mainTrack;
  int ticks = 30;
//  double ticklen = 250;
  int low = 50;
  int high = 70;
  int tick = 0;



  Integer[][] matrix = new Integer[ticks][high];

  public static void main(String[] args) throws InvalidMidiDataException {
    Composer2 composer = new Composer2();
//    Player player = new Player();

    composer.applySeqChord();
//    player.play(composer.sequence);
//    composer.printTrack();
    composer.pianoRoll();
    System.out.println(composer.toMap());
  }
  public void applyDefaultSeq() {
    int n = 60; //C
    for (int i = 0; i < 10; i++) {
      addNote(n+i*3,i*3,i*3+2);
    }
  }
  public void applySeqChord() {
    addNote(60,0,4);
    addNote(60+3,0,4);
    addNote(60+5,0,4);
    addNote(60+7,0,4);
  }


  public void addNotes(int[] notes, int start, int end) {
    for(int note : notes) addNote(note,start,end);
  }

  public void addNote(int note, int start, int end) {
    for (int i = start; i < end; i++) {
      matrix[i][note] = note;
    }
  }

  public Integer[][] getMatrix() {
    return matrix;
  }
  
  public HashMap<Integer, ArrayList<Integer>> toMap() {
    HashMap<Integer, ArrayList<Integer>> result = new HashMap<>();

    for (int row = 0; row < ticks; row++) {
      for (int col = low; col < high; col++) {
        Integer val = matrix[row][col];
        if(val!=null) {
          if (result.containsKey(row)) {
            result.get(row).add(val);
          } else {
            ArrayList<Integer> notes = new ArrayList<Integer>();
            notes.add(val);
            result.put(row,notes);
          }
        }
      }
    }
    return result;
  }

  public Sequence toSequence() {
      Sequence result = null;
      try {
        result = new Sequence(Sequence.PPQ, 4);
        result.createTrack();
        int channel = 1;
        for (int note = low; note < high; note++) {

          for (int tick = 0; tick < ticks; tick++) {
            Integer currentNote = matrix[tick][note];
            if(currentNote!=null) {
              result.getTracks()[0].add(new MidiEvent(new ShortMessage(
                  ShortMessage.NOTE_ON, channel, note, 100), tick));
              while(currentNote!=null) {
                currentNote = matrix[++tick][note];
              }
              result.getTracks()[0].add(new MidiEvent(new ShortMessage(
                  ShortMessage.NOTE_OFF, channel, note, 100), tick));
            }

          }
        }
      } catch (InvalidMidiDataException e) {
        e.printStackTrace();
      }
      return result;
  }

  public void pianoRoll() {
//    // Header row:
//    System.out.print("    ");
//    for (int i = low; i < high; i++) {
//      System.out.printf("%3d",i);
//    }
//    System.out.println();
    for (int row = 0; row < ticks; row++) {
      if (row%4==0) {
        System.out.print("    ");
        for (int i = low; i < high; i++) {
          System.out.printf("%3d",i);
        }
        System.out.println();
      }
      System.out.printf("%2d: ",row);
      for (int col = low; col < high; col++) {
        Integer val = matrix[row][col];
        String entry = "";
        if (val!= null) {
          entry = String.format("%3d",val);
        }
        System.out.printf("%3s",entry);
      }
      System.out.println();
    }
  }
}
