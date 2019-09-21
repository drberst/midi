package scratch;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiEvent;
import javax.sound.midi.Sequence;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.Track;

public class Composer {

  Sequence sequence;
//  Track mainTrack;

  public Composer() {
    try {
      init();
    } catch (InvalidMidiDataException e) {
      e.printStackTrace();
    }
  }
  public static void main(String[] args) throws InvalidMidiDataException {
    // System.out.println("Started main");
    Composer composer = new Composer();
    Player player = new Player();

    composer.applyDefaultSeq();
//    player.play(composer.sequence);
//    composer.printTrack();
    composer.pianoRoll();
  }
  public void applySeq(Sequence s ) {
    sequence = s;
  }
  public void applyDefaultSeq() {
    int n = 40; //C
    for (int i = 0; i < 10; i++) {
      addNote(n+i*3,i*3,i*3+2);
    }
//    addNote(n,0,20);
//    addNote(n+4,10,20);
//    addNote(n+7,10,20);
//    addNote(n+11,10,20);
  }

  public void init() throws InvalidMidiDataException {
    try {
      sequence = new Sequence(Sequence.PPQ, 4);
      sequence.createTrack();
    } catch (InvalidMidiDataException e) {
      e.printStackTrace();
    }
    System.out.println("init done");
  }

  public void addNote(int note, int start, int end) {
    int channel = 1;
    try {
      ShortMessage a = new ShortMessage(ShortMessage.NOTE_ON, channel, note, 100);
      MidiEvent noteOn = new MidiEvent(a, start);
      sequence.getTracks()[0].add(noteOn);
      MidiEvent noteOff = new MidiEvent(new ShortMessage(ShortMessage.NOTE_OFF, channel, note, 100),
          end);
      sequence.getTracks()[0].add(noteOff);
    } catch (InvalidMidiDataException e) {
      e.printStackTrace();
    }
  }

  public String midiNoteName (int notetype) {
    String name = "?"+notetype;
    if(notetype==144) name = "On!";
    if(notetype==128) name = "Off";
    if(notetype==254) name = "End";
    return name;
  }

  public void printTrack() throws InvalidMidiDataException {
    Track track = sequence.getTracks()[0];
    int nNotes = track.size();
    int maxTick = (int) track.get(nNotes-1).getTick();
    String[] lines = new String[maxTick+1];

    for (int i = 0; i < nNotes; i++) {
      MidiEvent m = sequence.getTracks()[0].get(i);
      byte[] bytes = m.getMessage().getMessage();
      int value = (int)(bytes[0] & 0xFF)-1;
      int data1 = bytes[1];

      String msg = String.format("%s:%d",midiNoteName(value),data1);
//      String s = String.format("%2d:%2d: [%s]",i,m.getTick(),msg);
      lines[(int) m.getTick()] = msg;
//      System.out.println(s);
    }
    for (int i = 0; i < lines.length; i++) {
      String s = lines[i];
      if (s != null) {
        System.out.print(i+": ");
        System.out.println(s);
      } else {
        System.out.println(i+": ");
      }
    }
    System.out.println("done");
  }

  public void pianoRoll() {
    // init
    Track track = sequence.getTracks()[0];
    int nNotes = track.size();
    int maxTick = (int) track.get(nNotes-1).getTick();
    int ticks = maxTick + 1;
    int vals = 88;
    String[][] matrix = new String[ticks][vals];
    // Fill
    for (int i = 0; i < nNotes; i++) {
      MidiEvent m = track.get(i);
      byte[] bytes = m.getMessage().getMessage();
      int value = (int)(bytes[0] & 0xFF)-1;
      int data1 = bytes[1];
      int tick = (int) m.getTick();
      String msg = String.format("%s:%d",midiNoteName(value),data1);
      matrix[tick][data1] = ""+midiNoteName(value);

//      String s = String.format("%2d:%2d: [%s]",i,m.getTick(),msg);
//      System.out.println(s);
    }



    // Print
    int low = 30;
    int high = 70;
    // Header row:
    System.out.print("    ");
    for (int i = low; i < high; i++) {
      System.out.printf("%3d",i);
    }
    System.out.println();
    for (int row = 0; row < ticks; row++) {
      System.out.printf("%2d: ",row);
      for (int col = low; col < high; col++) {
        String entry = matrix[row][col];
        if (entry == null) entry = "";
        System.out.printf("%3s",entry);
      }
      System.out.println();
    }
  }
}
