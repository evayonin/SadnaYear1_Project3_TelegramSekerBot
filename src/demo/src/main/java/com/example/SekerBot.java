package com.example;

import java.util.*;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.polls.SendPoll;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.polls.PollAnswer;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class SekerBot extends TelegramLongPollingBot {
  public static final String QUESTION1 = "What's your favorite animal?";
  public static final String QUESTION2 = "How many pets have you had?";
  public static final List<String> ANSWERS_TO_Q1 = List.of("Cat", "Dog", "Hamster", "Rabbit");
  public static final List<String> ANSWERS_TO_Q2 = List.of("0", "1", "2", "More than 2");

  private List<Long> chatIds = new ArrayList<>();

  private enum userStage {
    FIRST_MESSAGE, QUESTION1, QUESTION2, FINISHED // יעבור לסטטוס השאלה לפי שענה עליה
  }

  private Map<Long, userStage> userStageMap = new HashMap<>(); // השלב של כל יוזר

  private enum sekerStage { // השלבים של הסקר - חייב כדי לדעת מתי לשלוח את המפה של הדאטה הסופית למיין וליצור
    // חלון מהדאטה
    NOT_FINISHED, FINISHED
  }

  private sekerStage ss;
  // מפה של מפתח יוזר איידי וערך רשימה של התשובות שענה עליהם
  // לדוגמה האינדקס הראשון ברשימה (ערך המפה בכל זוג) יהיה התשובה הראשונה שבחר - כך
  // עבור כל המפתחות (איידי׳ס):
  private Map<Long, List<String>> userAnswersMap = new HashMap<>(); // הדאטה

  private Long firstHiTimestamp = null; // משתנה עבור הפעם הראשונה שנשלח היי - לספירת ה5 דקות

  public SekerBot() {
    sekerStage ss = sekerStage.NOT_FINISHED;
  }

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
    // מתי הסקר ייסגר:
    // תנאי 1
    boolean everyOneAnswered = true;
    for (userStage us : this.userStageMap.values()) {
      // בדיקה שכל הערכים במפה של יוזר סטייג׳ לא שווים לפינישד - לא כולם ענו
      if (us != userStage.FINISHED) {
        everyOneAnswered = false;
      }
    }
    // תנאי 2
    boolean fiveMinPassed = false;
    if (firstHiTimestamp != null) {
      long currentTime = System.currentTimeMillis();
      if (currentTime - firstHiTimestamp >= 5 * 60 * 1000) { // 5 דקות
        fiveMinPassed = true;
      }
    }
    while (!everyOneAnswered && !fiveMinPassed) { // כל עוד לא כולם ענו
      // וגם לא עברו חמש דקות שלפחות לערך אחד במפה אין פינישד

      SendMessage sendMessage = new SendMessage(); // אובייקט של ההודעה שהבוט יכתוב ליוזר
      long chatID = update.getMessage().getChatId();
      sendMessage.setChatId(chatID);

      // ההודעה הראשונה שהיוזר שולח:
      if (update.hasMessage()) { // יענה רק אם שלחו את זה
        if (update.getMessage().getText().equals("Hi") ||
            update.getMessage().getText().equals("hi") ||
            update.getMessage().getText().equals("היי")) {

          if (firstHiTimestamp == null) {
            firstHiTimestamp = System.currentTimeMillis();
          }

          if (!chatIds.contains(chatID)) {
            chatIds.add(chatID);
            // נשלח לכל היוזרים כשיוזר הצטרף:
            for (Long currentId : this.chatIds) {
              sendMessage.setChatId(currentId);
              sendMessage.setText("A new member joined the community! Welcome " + update.getMessage().getFrom()
                  .getUserName() + "! \nCurrent number of members: " + this.chatIds.size());
            }
            this.userStageMap.put(chatID, userStage.FIRST_MESSAGE); // כדי שנדע פעם הבא להציג לו את השאלה הראשונה
          }
          // הסקר יתחיל אחרי שהצטרפו 3 אנשים:
          else if (this.chatIds.size() >= 3) {
            String question = null;
            List<String> answerOptions = new ArrayList<>();
            // הסקר:
            if (this.userStageMap.get(chatID) == userStage.FIRST_MESSAGE) { // אם שלח היי
              this.userStageMap.put(chatID, userStage.QUESTION1); // question 1
              question = this.QUESTION1 + " Choose one option.";
              answerOptions = this.ANSWERS_TO_Q1;
              renderPollQuestion(update, question, answerOptions); // יציג את שאלה 1

              if (update.hasPollAnswer()) { // אם היוזר בחר תשובה
                String answer = handlePollAnswer(update.getPollAnswer(), answerOptions);
                // נוסיף את התשובה שהיוזר בחר באותה שאלה למפת התוצאות:
                if (!userAnswersMap.containsKey(chatID)) {
                  // אם המפה לא מכילה את היוזר אז נוסיף אותו עם רשימת תשובות ריקה
                  userAnswersMap.put(chatID, new ArrayList<>());
                }
                userAnswersMap.get(chatID).add(answer); // הוספת התשובה (לרשימת המחרוזות)
              }
              // ולעדכן את המצב של הסקר

              // אחרי שענה על שאלה 1:
              this.userStageMap.put(chatID, userStage.QUESTION2); // עדכון במפה שענה על שאלה 1 שנציג פעם הבאה את 2
            } //
            if (this.userStageMap.get(chatID) == userStage.QUESTION2) { // question 2
              question = this.QUESTION2 + " Choose one option.";
              answerOptions = this.ANSWERS_TO_Q2;
              renderPollQuestion(update, question, answerOptions); // יציג את שאלה 2

              if (update.hasPollAnswer()) { // אם היוזר בחר תשובה
                String answer = handlePollAnswer(update.getPollAnswer(), answerOptions);
                // נוסיף את התשובה שהיוזר בחר באותה שאלה למפת התוצאות:
                userAnswersMap.get(chatID).add(answer); // הוספה של התשובה (לרשימת המחרוזות)
                // ולעדכן את המצב של הסקר
              }
              // // אחרי שענה על שאלה 2:
              this.userStageMap.put(chatID, userStage.FINISHED); // עדכון במפה שענה על שאלה 1 שנציג פעם הבאה את 2

              // ולעדכן את המצב של הסקר
            }
          }
        }
      }
      try {
        execute(sendMessage);
      } catch (TelegramApiException e) {
        e.printStackTrace();
      }
    }
    this.ss = sekerStage.FINISHED; // אם כולם ענו או שעברו 5 דקות
  }

  private void renderPollQuestion(Update update, String questionText, List<String> options) { // מתודה שמציגה שאלה בסקר
    // נותן לבחור אופציה אחת
    SendPoll sendPoll = new SendPoll();
    sendPoll.setChatId(update.getMessage().getChatId()); // כדי שיידע באיזה צ׳אט לשלוח את הסקר
    sendPoll.setQuestion(questionText);
    sendPoll.setOptions(options);
    sendPoll.setIsAnonymous(false); // שנדע מי ענה (יוזר איי די)
    try {
      execute(sendPoll);
    } catch (TelegramApiException e) {
      throw new RuntimeException();
    }
  }

  private String handlePollAnswer(PollAnswer pollAnswer, List<String> options) { // מתודה שמקבלת את התשובה עבור השאלה
                                                                                 // ומחזירה כמחרוזת את
    // התשובה
    String answer = null;
    List<Integer> selectedOptions = pollAnswer.getOptionIds(); // אילו אפשרויות סומנו (לפי האינדקס שלהן)
    // למרות שיוזר יכול לסמן אפשרות אחת, בגלל המבנה גייסון של השרת של טלגרם זה מחייב
    // לשמור ברשימה של אינטגרים את אינדקס האפשרות שנבחרה.
    // כי אפשר גם לשנות שייתן לבחור יותר מתשובה אחת.
    int answerNumber = selectedOptions.get(0);
    answer = options.get(answerNumber);
    return answer;
  }

  @Override
  public String getBotUsername() {
    return "AnnaAndEvaSekerBot";
  }

  public String getBotToken() {
    return "8400300848:AAFoHs1SwEuwr4hgAeHZMh8NAsdm_0JXiQg";
  }

  public Map<Long, List<String>> getFinalDataMap() {
    // תנאי - אם הסקר-סטייג׳ הגיע לשלב האחרון (סיים) אז נשלח את המפה userAnswersMap
    if (this.ss == sekerStage.FINISHED) {
      return this.userAnswersMap;
    }
    return null;
  }

}

// לא להתייחס זה בשביל התוכנה שלי:

// להוסיף בוואץ׳ בדיבאגר (vscode) את הפקודות:
// update.getMessage().getFrom().getUserName()
// update.getMessage().getFrom().getFirstName()
// update.getMessage().getMessageId()
// update.getMessage().getText()
// עם פויינט על ההדפסות. אחרי כל שליחת הודעה להריץ מחדש דיבאגר כדי לראות.
