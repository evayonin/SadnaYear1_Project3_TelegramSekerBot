package com.example;

import java.util.List;
import java.util.Map;

import javax.swing.JFrame;

public class ResultsWindow extends JFrame {

  private final Map<Long, List<String>> results;

  public static final int WIDTH = 800;
  public static final int HEIGHT = 800;

  public ResultsWindow(Map<Long, List<String>> results) { // הבנאי יקבל את המפה מתוך המיין
    this.results = results;
    this.setSize(WIDTH, HEIGHT);
    this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    this.setResizable(false);
    this.setLocationRelativeTo(null);
    this.setLayout(null);
    this.add(new ResultsPanel(0, 0, WIDTH, HEIGHT, results));
    this.setVisible(true);
  }
}
