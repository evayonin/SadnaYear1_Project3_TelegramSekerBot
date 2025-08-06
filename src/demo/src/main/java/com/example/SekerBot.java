package com.example;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class SekerBot extends TelegramLongPollingBot {
  // private Set<Long> chatIds = new HashSet<>();
  private List<Long> chatIds = new ArrayList<>();

  // שדה של אינאם של שלבי השיחה כולל שלב הסקר עצמו:
  enum userStage {
    // להוסיף שלבים
  }

  private HashMap<Long, userStage> userStageMap = new HashMap<>(); // השלב של כל יוזר

  enum sekerState { // השלבים של הסקר - חייב כדי לדעת מתי לשלוח את המפה של הדאטה הסופית למיין וליצור
                    // חלון מהדאטה
    // להוסיך שלבים - כנראה נעשה 2
  }

  // מפה של מפתח יוזר איידי וערך רשימה של התשובות שענה עליהם
  // לדוגמה האינדקס הראשון ברשימה (ערך המפה בכל זוג) יהיה התשובה הראשונה שבחר - כך
  // עבור כל המפתחות (איידי׳ס):
  private Map<Long, List<String>> userAnswersMap = new HashMap<>(); // הדאטה

  // עד שלא כל הצ׳אט איידיס שדיבר איתם לא הגיעו לשלב האחרון (שהתקבל עדכון אחרון
  // מהסקר) ועד שלא נגמר הזמן שהגדרנו הסקר ימשיך לרוץ.

  // לראות מה ביקשו שהבנאי יקבל. אם מקבל משהו אז נאתחל את הסטייג׳ בבנאי גם כן.

  @Override
  // כנראה תהיה מתודה במיין שתקבל את המפה הסופית, וברגע שהסקר יסתיים (עבור
  // המקרה שכולם ענו או עבור המקרה שעברו 5 דקות) נעביר למיין את המפה.
  // בעזרת מתודה מהמחלקה של הבוט שמעבירה רק את המפה הסופית (קצרה) יהיה בה תנאי
  // שהיא תעביר אותה רק אם הסקר-סטייג׳ הגיע לשלב אחרון שהסקר כבר הסתיים.

  // צריך לראות איך לממש את זה במחלקת החלון כך שיווצר רק אחרי שנשלחה המפה.
  // האפשרות הכי הגיונית היא לשלוח את המפה למיין ואם היא לא ריקה ליצור את החלון
  // ולשלוח לבנאי שלו את המפה שזה יהיה גם שדה ואז החלון ייצור בבנאי שלו אובייקט של
  // פאנל שילח לתוכו את המפה גם כן.
  public void onUpdateReceived(Update update) {
    SendMessage sendMessage = new SendMessage(); // אובייקט של ההודעה שהבוט יכתוב ליוזר
    sendMessage.setText("חחחח מה את עושה עוד לא הוספתי כלום");
    sendMessage.setChatId(update.getMessage().getChatId());

    try {
      execute(sendMessage);
    } catch (TelegramApiException e) {
      e.printStackTrace();
    }
  }

  @Override
  public String getBotUsername() {
    return "AnnaAndEvaSekerBot";
  }

  public String getBotToken() {
    return "8400300848:AAFoHs1SwEuwr4hgAeHZMh8NAsdm_0JXiQg";
  }
}

// לא להתייחס זה בשביל התוכנה שלי:

// להוסיף בוואץ׳ בדיבאגר (vscode) את הפקודות:
// update.getMessage().getFrom().getUserName()
// update.getMessage().getFrom().getFirstName()
// update.getMessage().getMessageId()
// update.getMessage().getText()
// עם פויינט על ההדפסות. אחרי כל שליחת הודעה להריץ מחדש דיבאגר כדי לראות.
