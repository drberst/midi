package com.berst.Wave;

import java.awt.*;
import javax.swing.*;
import java.util.function.Function;

class Graph extends JComponent {
  private Function<Double, Double> fun;

  public Graph(Function<Double, Double> fun) {
    this.fun = fun;
    setPreferredSize(new Dimension(1000, 300));
  }

  public void paintComponent(Graphics g) {
    // clear background
    g.setColor(Color.white);
    Rectangle bounds = getBounds();
    int w = bounds.width;
    int h = bounds.height;
    g.fillRect(bounds.x, bounds.y, w, h);
    // draw the graph
    int prevx = 0;
    int prevy = fun.apply((double)prevx).intValue();
    g.setColor(Color.black);
    for (int i=1; i<w; i++) {
      int y = fun.apply((double)i).intValue();
      g.drawLine(prevx, prevy, i, y);
      prevx = i;
      prevy = y;
    }
  }
}

public class Wf {
  public static void main(String[] args) {
    JFrame f = new JFrame();
    // we're going to draw A sine wave for the width of the
    // whole Graph component
    Graph graph = new Graph(x -> Math.sin(x/(2*Math.PI))*100);
//    graph = new Graph()
    JScrollPane jsp = new JScrollPane(graph);
    f.setContentPane(jsp);
    f.setSize(800, 600);
    f.setVisible(true);
  }
}

