package com.berst.Compose;

import com.berst.Model.Note;
import java.util.ArrayList;
import java.util.HashMap;
import javax.sound.midi.InvalidMidiDataException;

public class Composer {

//  Track mainTrack;
//  int ticks = 30;
//  double ticklen = 250;
  int low = 100;
  int high = 0;

  ArrayList<ArrayList<Integer>> matrix;

  public static void main(String[] args) throws InvalidMidiDataException {
    Composer composer = new Composer();
//    Player player = new Player();

    composer.applySeqChord();
//    player.play(composer.sequence);
    composer.pianoRoll();
//    composer.pianoRoll2();
    composer.pianoRollLines();
//    System.out.println(composer.toMap());
  }

  public Composer() {
     matrix = new ArrayList<ArrayList<Integer>>();
//    matrix.add(new ArrayList<Integer>());
//    matrix.add(new ArrayList<Integer>());
//    matrix.add(new ArrayList<Integer>());
//    matrix.add(new ArrayList<Integer>());
  }

  public void applyDefaultSeq() {
    int n = 48; //C
    for (int i = 0; i < 8; i++) {
      addNote(n+i*3,i*3,i*3+1);
    }
  }
  public void applySeqChord() {
    addNote(60,0,4);
    addNote(60+3,0,4);
    addNote(60+5,0,4);
    addNote(60+7,0,4);

    addNote(65,4,8);
    addNote(65+3,4,8);
    addNote(65+5,4,8);
    addNote(65+7,4,8);
  }

  public void addNote(int note, int start, int end) {
    for (int i = start; i < end; i++) {
      add(i, note);
    }
//    System.out.printf("Added %d(%s) at [%d->%d]\n",note,new Note(note),start,end);
  }

  public void add(Integer tick, Integer val) {
    while(matrix.size() < (tick+1)) {
      matrix.add(new ArrayList<>());
    }
    matrix.get(tick).add(val);

    if(val > high) high = val+3;
    if(val < low) low = val-3;
  }

  public Integer getNote(Integer x, Integer y) {
    return matrix.get(x).get(y);
  }
//  public void setNote(Integer x, Integer y, Integer val) {
//    ArrayList line = getNotes(x);
//    line.set(y,val);
//  }


  public ArrayList<Integer> getNotes(Integer tick) {
    if (tick >= ticks()) return new ArrayList<>();
    return matrix.get(tick);
  }

  public ArrayList<ArrayList<Integer>> getMatrix() {
    return matrix;
  }

  public Integer ticks() {
    return matrix.size();
  }

  public Integer tracks() {
    int max = 0;
    for(ArrayList list : matrix) {
      if (list.size() > max) max = list.size();
    }
    return max;
  }

  public HashMap<Integer, ArrayList<Integer>> toMap() {
    HashMap<Integer, ArrayList<Integer>> result = new HashMap<>();

    for (int row = 0; row < ticks(); row++) {
      for (int col = low; col < high; col++) {
        Integer val = getNote(1,2);
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
  public String pianoRollHeader(int indent) {
    StringBuilder sb = new StringBuilder();
    sb.append(String.format("%"+indent+"s",""));
    for (int note = low; note < high; note++) {
      sb.append(String.format("%-3s|",new Note(note).smallString()));
    }
    return sb.toString();
  }
  public String[] pianoRollLines() {
    String[] result = new String[matrix.size()];

    for (int tick = 0; tick < matrix.size(); tick++) {
      StringBuilder sb = new StringBuilder();
      for (int note = low; note < high; note++) {
        String val = String.format("%4s","|");
        ArrayList<Integer> notesThisTick = getNotes(tick);
        for (int i = 0; i < notesThisTick.size(); i++) {
          if(notesThisTick.get(i) == note) val = String.format("%-3s|",new Note((note)).smallString());
        }
//        if (getNotes(tick).contains(note)) val = String.format("%3d",);
        sb.append(val);
      }
      result[tick] = sb.toString();
    }
    return result;
  }

  public String[] pianoRollLines2() {
    String[] result = new String[matrix.size()];

    for (int tick = 0; tick < matrix.size(); tick++) {
      StringBuilder sb = new StringBuilder();
      for (int note = low; note < high; note++) {
        String val = String.format("%3s","");
        ArrayList<Integer> notesThisTick = getNotes(tick);
        for (int i = 0; i < notesThisTick.size(); i++) {
          if(notesThisTick.get(i) == note) val = String.format("%3d",i);
        }
//        if (getNotes(tick).contains(note)) val = String.format("%3d",);
        sb.append(val);
      }
      result[tick] = sb.toString();
    }
    return result;
  }
  public void pianoRoll() {
    String[] lines = pianoRollLines();
    for (int i = 0; i < lines.length; i++) {
      if (i%15==0) {
        System.out.print("   ");
        for (int j = low; j < high; j++) {
          System.out.printf("%3d",j);
        }
        System.out.println();
      }
      System.out.printf("%d: %s\n",i,lines[i]);
    }
  }

//  public void pianoRoll2() {
////    // Header row:
////    System.out.print("    ");
////    for (int i = low; i < high; i++) {
////      System.out.printf("%3d",i);
////    }
////    System.out.println();
//    for (int row = 0; row < ticks(); row++) {
//      if (row%4==0) {
//        System.out.print("    ");
//        for (int i = low; i < high; i++) {
//          System.out.printf("%3d",i);
//        }
//        System.out.println();
//      }
//      System.out.printf("%2d: ",row);
//      for (int col = low; col < high; col++) {
//        Integer val = getNote(1,2);
//        String entry = "";
//        if (val!= null) {
//          entry = String.format("%3d",val);
//        }
//        System.out.printf("%3s",entry);
//      }
//      System.out.println();
//    }
//  }
  public static Composer merge(Composer a, Composer b) {
    Composer c = new Composer();
    ArrayList<Integer> toAdd = new ArrayList<>();
//    int max = a.ticks() < b.ticks() ? a.ticks() : b.ticks();


//    for (ArrayList)
    for (int tick = 0; tick < a.ticks(); tick++) {
      for(int note : a.getNotes(tick)) {
        c.add(tick,note);
      }
    }
    for (int tick = 0; tick < b.ticks(); tick++) {
      for(int note : b.getNotes(tick)) {
        c.add(tick,note);
      }
    }
    return c;
  }
  @Override
  public String toString() {
    String[] lines = pianoRollLines();
    StringBuilder result = new StringBuilder();
    for (int i = 0; i < lines.length; i++) {
      if (i%15==0) {
        result.append(("   "));
        for (int j = low; j < high; j++) {
          result.append(String.format("%3d",j));
        }
        result.append("\n");
      }
      result.append(String.format("%d: %s\n",i,lines[i]));
    }
    return result.toString();
  }
}
