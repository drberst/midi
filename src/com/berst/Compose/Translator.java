package com.berst.Compose;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Translator {
  static int[] guitarRoots = {64, 59,55,40,45,40};
  static int[] bassRoots = {43,38,33,28};

  public static void main(String[] args) {
    String[] tab = new String[]{
        //1 2 3 4 2 2 3 4 3 2 3 4 4 2 3 4 5 2 3 4 6 2 3 4 7 2 3 4 8 2 3 4 9 2 3 4
        //|       |       |       |       |       |       |       |       |
        "|------13------15~------12------13~------17------15------13------11-|",
        "|----15------17-------13------15-------18------17------15------12---|",
        "|--15------17-------14------15-------19------17------15------14-----|",
        "|-------------------------------------------------------------------|",
        "|-------------------------------------------------------------------|",
        "|-------------------------------------------------------------------|"
    };
    System.err.println("START");
    Composer c = Translator.fromTab(tab);
    c.pianoRoll();
  }

  public static Composer fromTab(String[] tab, int[] roots) {
    Composer com = new Composer();
//    int beats = (int) Math.round(1f * tab[0].length() / len);
    for (int str = 0; str < tab.length; str++) {
      String line = tab[str];
      String pattern = "(\\d+)[^-]?";
      Pattern r = Pattern.compile(pattern);

      Matcher m = r.matcher(line);
      while (m.find()) {
        int note = 0;
        String mod = "";
        if (m.groupCount() == 1) {
          note = Integer.parseInt(m.group(1)) ;
        }
        if (m.groupCount() > 1) {
          mod = m.group(2);
        }

        int start = m.start(1);
        int end = m.end(1) ;
        com.addNote(note + roots[str], start, end);
      }
    }
    return com;
  }
  public static Composer fromBassTab(String[] tab) {
//    int[] bassRoots
    return fromTab(tab,bassRoots);
  }
  public static Composer fromTab(String[] tab) {
    return fromTab(tab,guitarRoots);
  }

}
