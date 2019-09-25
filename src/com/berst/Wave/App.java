package com.berst.Wave;

import com.berst.Compose.Composer;
import com.berst.Model.Note;
import com.berst.StdAudio;
import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.chart.AreaChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Data;
import javafx.stage.Stage;

//import javafx.scene.Scene;

public class App extends Application {
  static int REFRESH = 500;
  final int WINDOW_SIZE = 100;

  private ScheduledExecutorService scheduledExecutorService;
  NumberAxis xAxis;
  NumberAxis yAxis;
  XYChart.Series<Number,Number> series;
  XYChart.Series<Number,Number> old;
  int count;

  public static void main(String[] args) {
    launch(args);
  }

  @Override
  public void start(Stage primaryStage) throws Exception {
    count = 0;
    primaryStage.setTitle("JavaFX Realtime Chart Demo");
    xAxis = new NumberAxis(); // we are gonna plot against time
    yAxis = new NumberAxis();
    //defining the axes
    xAxis.setAutoRanging(true);
//    xAxis.setForceZeroInRange(false);

    xAxis.setLabel("Time/s");
    xAxis.setAnimated(false); // axis animations are removed
    yAxis.setLabel("Value");
    yAxis.setAnimated(false); // axis animations are removed

    xAxis.setAutoRanging(false);
    yAxis.setAutoRanging(false);
    yAxis.setLowerBound(-1D);
    yAxis.setUpperBound(1D);
    //creating the line chart with two axis created above
    final AreaChart<Number, Number> lineChart = new AreaChart<>(xAxis, yAxis);
    lineChart.setTitle("Realtime JavaFX Charts");
    lineChart.setCreateSymbols(false);
    lineChart.setAnimated(false); // disable animations

    //defining a series to display data
    series = new XYChart.Series();
    series.setName("Data Series");

    old = new XYChart.Series();
    old.setName("old");
//
//    XYChart.Series older = new XYChart.Series();
//    older.setName("older");

//    series.s

    // add series to chart
    lineChart.getData().add(series);
    lineChart.getData().add(old);
//    lineChart.getData().add(series);

    // setup scene
    Scene scene = new Scene(lineChart, 800, 600);
    primaryStage.setScene(scene);

    // show the stage
    primaryStage.show();

    scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();

    Note.notesOnly = true;
    Composer c = new Composer();
    c.applyDefaultSeq();
//    System.out.println(c);
//    Player.setBPM(89*4);
//    Player p = new Player(c.tracks());
//
//    p.playComposition(c);
    playComposition(c);

//
    // this is used to display time in HH:mm:ss format
//    final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss");
//    AtomicInteger loop = new AtomicInteger(1);
    // setup a scheduled executor to periodically put data into the chart
    // put dummy data onto graph per second

//    scheduledExecutorService.scheduleAtFixedRate(() -> {
//      // get a random integer between 0-10
//      Integer random = loop.getAndIncrement();//ThreadLocalRandom.current().nextInt(10);
//
//      // Update the chart
//      Platform.runLater(() -> {
//        Sinewave s = new Sinewave(random);
//
//        Double[] data = s.getDoublesObj(WINDOW_SIZE / 2);
//        update(data);
//
////        while (series.getData().size() > WINDOW_SIZE)
////          series.getData().remove(0);
//      });
//    }, 0, 1, TimeUnit.SECONDS);
  }
  public void update(Double[] data) {

    Platform.runLater(new Runnable() {
      @Override
      public void run() {

//        old.getData().clear();
        ObservableList<Data<Number, Number>> dat = series.getData();
        for (int i = 0; i < dat.size(); i++) {
          Data x = dat.get(i);
          old.getData().add(new Data(x.getXValue(),x.getYValue()));
          if(old.getData().size() > WINDOW_SIZE*4) old.getData().remove(0);
        }
//        old.getData().addAll(series.getData().);

//        series.getData().clear();
        for (Integer i = 0; i < WINDOW_SIZE; i++) {
          Double toAdd = data[i];
          if (toAdd == null) toAdd = 0D;

          if (dat.size() > i) {
            var x = dat.get(i);
            x.setXValue(count+i);
            x.setYValue(toAdd);
          } else {
            dat.add(new Data<>(count + i, toAdd));
          }
        }

//        System.out.println(series.getData());
        count += WINDOW_SIZE;

        xAxis.setLowerBound(count-WINDOW_SIZE*4);
        xAxis.setUpperBound(count);
        System.out.printf("Range[%d,%d][datasize=%d]\n",
            count-WINDOW_SIZE*2,count,data.length);

//        for(Data<Number, Number> v : old.getData()) {
//          System.out.printf("[%d,%.2f] ",v.getXValue(),v.getYValue());
//        }
//        System.out.println();
//
//        for(Data<Number, Number> v2 : series.getData()) {
//          System.out.printf("[%d,%.2f] ",v2.getXValue(),v2.getYValue());
//        }
//        System.out.println("\n");
//        if (old.getData().size() > WINDOW_SIZE * 5) {
//          old.getData().remove(0,old.getData().size()-WINDOW_SIZE);
//        }
      }
    });
  }



  public void playComposition(Composer aComposer) {
    System.out.printf("Playing: %d lines : %d tracks : buffer=%d\n",
        aComposer.ticks(), aComposer.tracks(), StdAudio.SAMPLE_RATE);

//    String[] lines = aComposer.pianoRollLines();
    int msPerTick = 500;
    AtomicInteger tick = new AtomicInteger();

    scheduledExecutorService.scheduleAtFixedRate(() -> {

      ArrayList<Integer> aLine = aComposer.getNotes(tick.getAndIncrement());
      System.out.println(aLine);
      ArrayList<Double> tones = new ArrayList<>();
//
      if(aLine.size() > 0) {
        int note = aLine.get(0);
        tones.add(new Note(note).getHz());
//        System.out.println(tones);
        double[] wave = Waves.singleWave(StdAudio.playChord(tones, .1));
        update(Waves.double2Double(wave));
      } else {
        update(new Double[WINDOW_SIZE]);
      }
    }, 0, msPerTick, TimeUnit.MILLISECONDS);


  }

  @Override
  public void stop() throws Exception {
    super.stop();
    scheduledExecutorService.shutdownNow();
  }
}