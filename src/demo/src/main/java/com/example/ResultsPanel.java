package com.example;

import javax.swing.JPanel;

import java.awt.*;

public class ResultsPanel extends JPanel {

  // צריך לקשר את מפת התוצאות שמתקבלת במחלקת הבוט לפאנל כך שכבר את התוצאות נשלח
  // לכאן. כתבתי את השלבים בהערות במחלקת בוט וחלון.
  // נשמור שדה עבור המפה שתישלח לכאן - תהיה שדה פינאלי!
  // נשלח את זה לפאנל על ידי זה שנשלח לחלון והחלון יגזיר פאנל עם ויישלח את המפה
  // בבנאי לפאנל.
  // רק כאשר הסקר עבר לשלב האחרי אחרון - נגמר הסקר, נעביר את הדאטה לחלון ואז הוא
  // יעביר לפאנל.

  public ResultsPanel(int x, int y, int width, int height) { // נעביר גם את מפת הדאטה מההגרה בחלון
    // שאר המתודות של הגדרת הפאנל

    showSekerData();
  }

  public void showSekerData() {
    // נסדר את המידע שנקבל מהבוט ונציג בעזרת פיינט קומפוננט
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
