package com.example;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ResultsPanel extends JPanel {

  private final Map<Long, List<String>> resultsMap;
  // המפה מכילה זוגות של מפתח היוזר ורשימה של התשובה שבחר עבור שאלה 1 (אינדקס 0)
  // והתשובה שבחר עבור שאלה 2 (אינדקס 1).
  private String question1 = SekerBot.QUESTION1;
  private String question2 = SekerBot.QUESTION2;
  private List<String> answersToQ1 = SekerBot.ANSWERS_TO_Q1;
  private List<String> answersToQ2 = SekerBot.ANSWERS_TO_Q2;

  // מה שנקבל מהמתודה sortTheResulstFromHighest:
  private List<String> sortedAnswersQ1;
  private List<Double> sortedPercentsQ1;

  private List<String> sortedAnswersQ2;
  private List<Double> sortedPercentsQ2;
  // האינדקסים שלהם מקבילים כך באותו אינדקס ברשימה הראשונה יש את התשובה וברשימה
  // השנייה את השכיחות שלה

  public ResultsPanel(int x, int y, int width, int height, Map<Long, List<String>> resultsMap) {
    this.setLayout(null);
    this.setBounds(x, y, width, height);
    this.resultsMap = resultsMap;
    // סידור התוצאות לפני התצוגה שנדרשת לפני שהופכים לטקסט ( הפיכה ל4 הרשימות
    // ממויינות):
    sortTheResulstFromHighest(resultsMap);
    // הטקסט הסופי שיוצג בסווינג - התפלגות האחוזים של כל תשובה בכל שאלה בסדר ממויין:
    // (עידכנו את הפרמטרים שנשלח למתודה הבאה בסוף מתודה הקודמת בקיבלה את המפה - שורה
    // קודמת).
    String finalText = setResultsTextForStatisticsJTextArea(this.sortedAnswersQ1, this.sortedPercentsQ1,
        this.sortedAnswersQ2, this.sortedPercentsQ2);

    JTextArea statisticsJTextArea = new JTextArea();
    statisticsJTextArea.setBounds(x - 25, y - 30, width - 25, height - 30);
    statisticsJTextArea.setEditable(false);
    statisticsJTextArea.setLineWrap(false); // כדי לאפשר גלילה אופקית
    statisticsJTextArea.setWrapStyleWord(false); // כדי לאפשר גלילה אופקית
    statisticsJTextArea.setFocusable(false);
    statisticsJTextArea.setOpaque(false); // רקע שקוף
    statisticsJTextArea.setForeground(Color.BLACK);
    statisticsJTextArea.setFont(new Font("Arial", Font.BOLD, 30)); // הטקסט באנגלית מצד שמאל
    statisticsJTextArea.setText(finalText);
    // אם הטקסט ארוך מדי:
    JScrollPane scrollPane = new JScrollPane(statisticsJTextArea);
    scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED); // מאפשר גלילה לרוחב
    scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED); // מאפשר גלילה לאורך
    this.add(scrollPane);

    this.setVisible(true);
  }

  // בפורמט הזה:
  // Question 1: "....?""
  // 1. "ans 1": 75% לדוגמה
  // 2. "ans 2": 20%
  // 3. "ans 3": 5%
  // 4. "ans 4": 5%
  private String setResultsTextForStatisticsJTextArea(List<String> SAQ1, List<Double> SPQ1, List<String> SAQ2,
      List<Double> SPQ2) {
    // שאלה 1:
    String finalText = "Question 1: " + this.question1 + "\n";
    // להוסיף את התשובות הממויינות וליד זה אחוזים
    for (int i = 0; i < this.answersToQ1.size(); i++) {
      finalText += (i + 1) + ". " + SAQ1.get(i) + " : " + SPQ1.get(i) + "%\n";
    }
    // שאלה 2:
    finalText += "Question 2: " + this.question2 + "\n";
    for (int i = 0; i < this.answersToQ2.size(); i++) {
      finalText += (i + 1) + ". " + SAQ2.get(i) + " : " + SPQ2.get(i) + "%\n";
    }
    return finalText;
  }

  private void sortTheResulstFromHighest(Map<Long, List<String>> resultsMap) {
    // מפת שכיחות עבור האינדקס הראשון(תשובות שבחרו לשאלה 1) ברשימת התשובות שהיוזר
    // ענה(הערך בזוג במפת התוצאות):
    Map<String, Integer> freqIndex0 = new HashMap<>();
    // מפת שכיחות עבור האינדקס השני:
    Map<String, Integer> freqIndex1 = new HashMap<>();

    // למקרה שיש תשובות שלא נבחרו כלל (שכיחות 0%), נאתחל את מספר ההופעות של כל תשובה
    // ל0:
    for (String answer : answersToQ1) {
      freqIndex0.put(answer, 0);
    }
    for (String answer : answersToQ2) {
      freqIndex1.put(answer, 0);
    }
    // נספור את מ״ס הפעמים שכל תשובה נבחרה בכל שאלה
    for (List<String> list : resultsMap.values()) {
      freqIndex0.put(list.get(0), freqIndex0.get(list.get(0)) + 1); // q1
      freqIndex1.put(list.get(1), freqIndex1.get(list.get(1)) + 1); // q2
      // לשים לב שמפת התוצאות במחלקת הבוט במתודה מאוחלת עם רשימות עם 2 מחרוזות נאל
    }
    // יצרנו עבור כל שאלה מפה שמכילה מפתחות של התשובה וערך של מספר הפעמים שנבחרה.
    // הן לא ממויינות לכן נרצה למיין:

    // יצירת רשימה ממויינת עבור כל שאלה לפי שכיחות יורדת - איברי כל רשימה יהיו
    // אובייקט של מפתח וערך מהמפה אבל ממויינים לפי הערך (מספר בחירות):
    // (ברשימה כי נצטרך את האינדקסים)
    List<Map.Entry<String, Integer>> sortedIndex0EntryList = freqIndex0.entrySet() // q1
        .stream()
        .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
        .toList();

    List<Map.Entry<String, Integer>> sortedIndex1EntryList = freqIndex1.entrySet() // q2
        .stream()
        .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
        .toList();

    // חישוב סך התשובות לכל שאלה (כדי לחשב אחוזים):
    // נסכום את מ״ס הבחירות של התשובות בכל מפה שיצרנו מקודם במקום של הערך:
    int totalCounOfAnsIndex0 = freqIndex0.values() // שאלה 1
        .stream()
        .mapToInt(Integer::intValue) // המרה מהערך האינטג׳ר של המפה לאינט
        .sum();
    int totalCountOfAnsIndex1 = freqIndex1.values() // שאלה 2
        .stream()
        .mapToInt(Integer::intValue)
        .sum();

    // כדי להשתמש בזה בהצגת הטקסט נרצה ליצור עבור כל שאלה 2 רשימות מקבילות-
    // רשימת סטרינג בשביל טקסטי התשובות - ממויינות לפי שכיחות
    // ורשימת דאבל עבור השכיחות של כל שאלה
    // ככה שבשתי הרשימות הם יהיו באינדקסים תואמים וכך יהיה קל לגשת.

    // עבור שאלה 1:
    this.sortedAnswersQ1 = new ArrayList<>();
    this.sortedPercentsQ1 = new ArrayList<>();
    for (Map.Entry<String, Integer> entry : sortedIndex0EntryList) {
      // מוכנס בסדר ממויין כי הרשימה ממויינת
      sortedAnswersQ1.add(entry.getKey());
      double percent = totalCounOfAnsIndex0 > 0 ? (entry.getValue() * 100.0) / totalCounOfAnsIndex0 : 0.0;
      // אם מספר התשובות גדול מ0 (יש עונים) אז תכפיל את מספר הבחירות של התשובה ב100
      // ותחלק בסכום הכולל של התשובות באותה שאלה, אחרת השכיחות 0%
      sortedPercentsQ1.add(percent);
    }

    // עבור שאלה 2:
    this.sortedAnswersQ2 = new ArrayList<>();
    this.sortedPercentsQ2 = new ArrayList<>();
    for (Map.Entry<String, Integer> entry : sortedIndex1EntryList) {
      sortedAnswersQ2.add(entry.getKey());
      double percent = totalCountOfAnsIndex1 > 0 ? (entry.getValue() * 100.0) / totalCountOfAnsIndex1 : 0.0;
      sortedPercentsQ2.add(percent);
    }
  }

  // לא נדרש שימוש בפיינט קומפוננט כי מציגים רק טקסט.
}
