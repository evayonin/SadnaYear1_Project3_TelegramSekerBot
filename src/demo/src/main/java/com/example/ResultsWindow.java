package com.example;

import java.util.List;
import java.util.Map;

import javax.swing.JFrame;

public class ResultsWindow extends JFrame {
  // נקשר בין התוצאות במחלקת הבוט למחלקה הזאת ע״י מתודה במיין
  // ככה גם נוודא שהחלק של סווינג יתחיל לרוץ רק כשנשלחה למתודה במיין המפה הסופית
  // ואז נכניס אותה בבנאי של החלון.
  // זה יהיה אם איזשהו תנאי - אז שהמפה לא נשלחה כלומר כל עוד ריקה - זה לא ייצור את
  // החלון.
  // רק כאשר הסקר עבר לשלב האחרון - נגמר הסקר, נעביר את הדאטה למיין ולחלון ואז הוא
  // יעביר לפאנל.
  // נשמור כאן שדה פינאלי של המפה ונעביר אותו גם כן בבנאי של הפאנל.
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
