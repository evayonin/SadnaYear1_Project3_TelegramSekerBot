package com.example;

import javax.swing.JPanel;
import javax.swing.JTextArea;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ResultsPanel extends JPanel {

  private final Map<Long, List<String>> resultsMap;
  private String question1 = SekerBot.QUESTION1;
  private String question2 = SekerBot.QUESTION2;
  private List<String> answersToQ1 = SekerBot.ANSWERS_TO_Q1;
  private List<String> answersToQ2 = SekerBot.ANSWERS_TO_Q2;

  public ResultsPanel(int x, int y, int width, int height, Map<Long, List<String>> resultsMap) {
    this.setLayout(null);
    this.setBounds(x, y, width, height);
    this.resultsMap = resultsMap;

    JTextArea statisticsJTextArea = new JTextArea();
  }

  // צריך להיות ממוין!!!!
  // לכל שאלהתוצג התפלגות אחוזיםבין התשובות האפשריות- ממויינות לפי שכיחות
  // לדוגמה:
  // שאלה 1:
  // תשובה אחת: 70%
  // תשובה 2: 20%
  // תשובה 3: 10%
  // תשובה 4: 0%
  // אבל כדי לסדר אותם בסדר הנכון לפני ההצגה
  private void sortTheResulstFromHighest(Map<Long, List<String>> resultsMap) {
    // מפת שכיחות עבור האינדקס הראשון(תשובה שבחר לשאלה 1) ברשימת של התשובות שהיוזר
    // ענה(הערך במפה):
    Map<String, Integer> freqIndex0 = new HashMap<>();
    // מפת שכיחות עבור האינדקס השני(תשובה שבחר לשאלה 2) ברשימת של התשובות שהיוזר
    // ענה(הערך במפה):
    Map<String, Integer> freqIndex1 = new HashMap<>();

    for (List<String> list : resultsMap.values()) {
      // עבור אינדקס 0:
      if (freqIndex0.containsKey(list.get(0))) { // אם מכיל כבר את התשובה הזאת באינדקסי 0 של הרשימות
        freqIndex0.put(list.get(0), freqIndex0.get(list.get(0)) + 1); // תוסיף +1 לשכיחות
      } else {
        freqIndex0.put(list.get(0), 1); // אחרת תוסיף ותעדכן את השכיחות ל1
      }
      // עבור אינדקס 1:
      if (freqIndex1.containsKey(list.get(1))) {
        freqIndex1.put(list.get(1), freqIndex1.get(list.get(1)) + 1);
      } else {
        freqIndex1.put(list.get(1), 1);
      }
    }
    // עכשיו שיש לנו מפה עבור כל אינדקס(שאלה) שמכילה את התשובה (אחת מ-4) וכמה פעמים
    // היא נבחרה,
    // צריך למיין לפי שכיחות כל תשובה בשאלה 1 ובשאלה 2, מהשכיחות הגבוהה לנמוכה.
    // נעשה את זה בעזרת רשימה:
    // (כדי לדעת איפה לכתוב את התשובות בסווינג)
    List<Map.Entry<String, Integer>> sortedIndex0List = freqIndex0.entrySet()
        .stream()
        .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
        .toList();

    List<Map.Entry<String, Integer>> sortedIndex1List = freqIndex1.entrySet()
        .stream()
        .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
        .toList();
    // עכשיו עבור כל שאלה יש לנו
    // רשימת מפות ממויינת שכל מפה מכילה את טקסט התשובה(מפתח) וכמה פעמים בחרו בה(ערך)
    // -מסודרות מהשכיחוות הגבוה לנמוכה
  }

  protected void paintComponent(Graphics g) {
    super.paintComponent(g);
    // if () { // תנאי אם יש דאטה - לדוג אם שמרנו במפה אז שלא תהיה ריקה

    Graphics2D g2d = (Graphics2D) g; // casting

    // מה שצריך לתצוגה הגרפית

    // g2d.drawImage(this, 0, 0, getWidth(), getHeight(), this);
    // שיתאים את הגודל לפאנל
    // אם נרצה תמונת רקע זה יהיה הפרמטר הראשון שדרואו אימג׳ מקבלת (אתחול ועיבוד
    // תמונה בבנאי - באפרד אימג׳)

    // } // if
  }

}
