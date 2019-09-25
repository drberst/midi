package com.berst;

import com.berst.Compose.Composer;
import com.berst.Sound.Player;

public class Main {

  public static void main(String[] args) throws Exception {
    Composer c = Midi.readMidi("neverEndingJourney.mid");
    Player p = new Player();
    p.playComposition(c);
  }
}
