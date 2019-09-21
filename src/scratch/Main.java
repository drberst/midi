package scratch;

import java.util.Arrays;

import javax.sound.midi.MidiChannel;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.Synthesizer;

public class Main {

    public static void main(String[] args) {
	// write your code here
        int channel = 0; // 0 is a piano, 9 is percussion, other channels are for other instruments

        int volume = 80; // between 0 et 127
        int duration = 500; // in milliseconds
        p("start");
        try {
            Synthesizer synth = MidiSystem.getSynthesizer();
            synth.open();
            MidiChannel[] channels = synth.getChannels();
            p("Created channels");
            // channels[0].programChange(72);

            // ShortMessage a = new ShortMessage();
            // a.setMessage(192, 1, 4, 0);
            
            // String s = Arrays.toString(synth.getLoadedInstruments());
            // // synth.
            // p("instrument:");
            // p(s);
            // --------------------------------------
            // Play a few notes.
            // The two arguments to the noteOn() method are:
            // "MIDI note number" (pitch of the note),
            // and "velocity" (i.e., volume, or intensity).
            // Each of these arguments is between 0 and 127.
            // Thread.sleep( duration );

            
            channels[channel].noteOn( 60, volume ); // C note
            Thread.sleep( duration );
            channels[channel].noteOff( 60 );
            
            channels[channel].noteOn( 62, volume ); // D note
            Thread.sleep( duration );
            channels[channel].noteOff( 62 );

            channels[channel].noteOn( 64, volume ); // E note
            Thread.sleep( duration );
            channels[channel].noteOff( 64 );
            
            Thread.sleep( 500 );

            playSequence("sequence", channels[0], synth);

            // --------------------------------------
            // Play a C major chord.
            channels[channel].noteOn( 60, volume ); // C
            channels[channel].noteOn( 64, volume ); // E
            channels[channel].noteOn( 67, volume ); // G
            Thread.sleep( 800 );
            channels[channel].allNotesOff();
            Thread.sleep( 500 );
            
            Thread.sleep( 500 );

            synth.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void playSequence(String sequence, MidiChannel channel, Synthesizer synth) throws InterruptedException{
        int[] notes = {64,62,60};
        int volume = 80;
        int duration = 500;
        // Thread.sleep(duration);

        for (int note : notes) {
            channel.noteOn(note, volume);
            Thread.sleep(duration);
            channel.noteOff(note);
        }
        Thread.sleep(duration);

    }
    public static void p(Object o) {
        System.out.println(o.toString());
    }
    public static void printInstruments(int channel, Synthesizer synth)
        throws InterruptedException {
        p("Printing all the things");
        int start = 9;
        int volume = 100;
        MidiChannel[] channels = synth.getChannels();

        for (int i = start; i < start+10; i++) {
            channels[0].programChange(i);
            p(synth.getLoadedInstruments()[i].toString());
            channels[channel].noteOn( 60, volume ); // C
            channels[channel].noteOn( 64, volume ); // E
            channels[channel].noteOn( 67, volume ); // G
            Thread.sleep( 800 );
            channels[channel].allNotesOff();
            Thread.sleep( 500 );
        }
        Thread.sleep( 500 );
    }

    // int begin = 0;
    // int timeInterval = 1000/(120/60);

    // timer.schedule(new TimerTask() {
    //     int counter = 0;

    //     @Override
    //     public void run() {

    //         Sequence s = player.getSequence();
    //         Track track = s.getTracks()[0];
    //       System.out.printf("%s:%ds: Tick: %d, track len=%d (or maybe %.2f)\n",
    //           player.isRunning(),counter,player.getTickPosition(),track.ticks(),player.getTempoInBPM());

    //       counter++;
    //         if (counter >= 20 || player.getTickPosition() >= track.ticks()) {
    //             timer.cancel();
    //             player.stop();
    //             synth.close();
    //             System.exit(69);
    //         }
    //     }
    // }, begin, timeInterval);
}
