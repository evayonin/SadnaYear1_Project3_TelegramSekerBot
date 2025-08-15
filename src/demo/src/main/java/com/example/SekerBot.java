package com.example;

import java.util.*;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.polls.SendPoll;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.polls.PollAnswer;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class SekerBot extends TelegramLongPollingBot {

  public static final String QUESTION1 = Main.q1;
  public static final String QUESTION2 = Main.q2;
  public static final List<String> ANSWERS_TO_Q1 = Main.ansToQ1;
  public static final List<String> ANSWERS_TO_Q2 = Main.ansToQ2;

  private List<Long> chatIds = new ArrayList<>();

  private enum userStage {
    FIRST_MESSAGE, QUESTION1, QUESTION2, FINISHED // יעבור לסטטוס השאלה לפי שענה עליה
  }

  private Map<Long, userStage> userStageMap = new HashMap<>();

  private enum sekerStage { // כדי לדעת מתי לשלוח את המפה הסופית
    NOT_FINISHED, FINISHED
  }

  private sekerStage sekerStage;

  private Map<Long, List<String>> userAnswersMap = new HashMap<>();
  // מפת התוצאות שנשמור בה את רשימת התשובות של שאלות 1 ו-2 עבור כל יוזר

  private Long firstHiTimestamp = null; // משתנה עבור הפעם הראשונה שנשלח היי - לספירת ה5 דקות

  public SekerBot() {
    this.sekerStage = sekerStage.NOT_FINISHED;
  }

  @Override
  public String getBotUsername() {
    return "AnnaAndEvaSekerBot";
  }

  public String getBotToken() {
    return "8400300848:AAFoHs1SwEuwr4hgAeHZMh8NAsdm_0JXiQg";
  }

  @Override
  public void onUpdateReceived(Update update) {

    if (update.hasPollAnswer()) {
      handlePollAnswer(update.getPollAnswer());
      // עם התייחסות בתוך המתודה למקרה שיוזר לא בחר תשובה או כשנשלחה תשובה ריקה כדי
      // שלא יקרוס או ימשיך לשלב הבא שלו

      checkFinish(); // אם כולם ענו או עברו כבר 5 דקות
      return; // יציאה מהמתודה. לא ימשיך את הסקר
    }

    // אם האפדייט היה שליחת הודעה
    if (update.hasMessage()) {
      String text = update.getMessage().getText();
      long chatID = update.getMessage().getChatId();

      // הבוט יגיב רק כששולחים היי
      if ("Hi".equalsIgnoreCase(text) || "היי".equals(text) || "הי".equals(text)) {

        if (firstHiTimestamp == null) { // מתי שיתחיל לספור 5 דקות (מההיי הראשון שנשלח כשהערך עוד נאל בהתחלה כפי שאותחל)
          firstHiTimestamp = System.currentTimeMillis();
        }

        if (!chatIds.contains(chatID)) {
          chatIds.add(chatID); // הוספת היוזר החדש
          userStageMap.put(chatID, userStage.FIRST_MESSAGE);
          // נאתחל ליוזר רשימת תשובות ריקה במפת התוצאות כדי שלא יהיו חריגות בשימוש שלה
          // בפאנל:
          List<String> answers = new ArrayList<>();
          answers.add(null);
          answers.add(null);
          userAnswersMap.put(chatID, answers);

          // הודעה לכל שאר היוזרים שיוזר הצטרף:
          broadcastJoin(update.getMessage().getFrom().getUserName());

          // ישלח את השאלה הראשונה רק אם הצטרפו 3 אנשים לפחות
          // (ישלח כל פעם לכל יוזר חדש שהוסף לרשימה ששלח היי):
          if (chatIds.size() >= 3) {
            sendQuestion1(chatID);
          }
        }
      }
    }
    checkFinish(); // בדיקה סופית אם כולם ענו או אם עברו 5 דקות גם אחרי האפדייט
  }

  // שלחית שאלה 1
  private void sendQuestion1(long userId) { // יישלח ליוזר שממנו הגיע האפדייט ששלח היי
    String q = this.QUESTION1 + " Choose one option.";
    List<String> opts = this.ANSWERS_TO_Q1;
    renderPollQuestionForUser(userId, q, opts);
    userStageMap.put(userId, userStage.QUESTION1);
  }

  // שליחת שאלה 2
  private void sendQuestion2(long userId) { // יישלח ליוזר שענה על שאלה 1 (שהגיע ממנו תשובה עבור שאלה 1 במתודה שמטפלת
                                            // בתשובה)
    String q = this.QUESTION2 + " Choose one option.";
    List<String> opts = this.ANSWERS_TO_Q2;
    renderPollQuestionForUser(userId, q, opts);
    userStageMap.put(userId, userStage.QUESTION2);
  }

  // מתודה כללית לשליחת שאלה של סקר לאותו יוזר שממנו התקבל אפדייט או היי ז״א
  // האס-מאסאג׳ (שליחת שאלה 1) או תשובה על שאלה 1 ז״א האס-פול-אנסר (שליחת שאלה 2)
  private void renderPollQuestionForUser(long userId, String questionText, List<String> options) {
    SendPoll sendPoll = new SendPoll();
    sendPoll.setChatId(userId);
    sendPoll.setQuestion(questionText);
    sendPoll.setOptions(options);
    sendPoll.setIsAnonymous(false); // שנדע מי ענה
    sendPoll.setType("regular");
    try {
      execute(sendPoll);
    } catch (TelegramApiException e) {
      throw new RuntimeException(e);
    }
  }

  // טיפול בתשובה שהתקבלה מהסקר
  private void handlePollAnswer(PollAnswer pollAnswer) {
    long userId = pollAnswer.getUser().getId();
    userStage stage = userStageMap.get(userId);

    List<Integer> chosen = pollAnswer.getOptionIds();
    // למרות שהיוזר בוחר רק תשובה אחת המבנה הוא של רשימה כי יש אפשרות להגדיר בחירת
    // כמה תשובות.

    // אם היוזר לא בחר תשובה - כדי שלא תהיה שגיאה כשניגש לאינדקס התשובה ושלא ייקדם
    // לשלב הבא של היוזר במצבים כמו כשהסקר בדיוק נסגר ונשלח פול-אנסר ריק:
    if (chosen == null || chosen.isEmpty())
      return;

    int idx = chosen.get(0);

    if (stage == userStage.QUESTION1) { // אם היוזר עונה על שאלה 1
      String ans = this.ANSWERS_TO_Q1.get(idx); // התשובה שבחר מהאפשרויות
      userAnswersMap.get(userId).set(0, ans); // הוספה למפת התוצאות את התשובה עבור השאלה הזאת באינדקס הראשון
      sendQuestion2(userId); // שליחת השאלה השנייה
    } else if (stage == userStage.QUESTION2) { // אם היוזר עונה על שאלה 2
      String ans = this.ANSWERS_TO_Q2.get(idx); // התשובה שבחר מהאפשרויות
      userAnswersMap.get(userId).set(1, ans); // הוספה למפת התוצאות את התשובה עבור השאלה הזאת באינדקס השני
      userStageMap.put(userId, userStage.FINISHED); // עדכון שהיוזר סיים
    }

  }

  // בדיקה לסגירת הסקר
  private void checkFinish() {
    // בדיקה אם כולם סיימו לענות:
    boolean everyoneAnswered = userStageMap != null
        && !userStageMap.isEmpty()
        && userStageMap.values().stream().allMatch(us -> us == userStage.FINISHED);
    // חייב להוסיף את שני התנאים הראשונים עבור הבדיקה הראשונה כשהמפה ריקה וממחזירה
    // נאל ובגלל זה חוזר טרו. כדי שיחזור פולס.

    // בדיקה אם עברו 5 דקות מאז היוזר הראשון ששלח היי:
    boolean fiveMinPassed = false;
    if (firstHiTimestamp != null) { // יבדוק אחרי שאתחלנו בפעם הראשונה (במתודה onUpdateRecieved)
      long currentTime = System.currentTimeMillis();
      fiveMinPassed = (currentTime - firstHiTimestamp) >= 5 * 60 * 1000;
    }
    if (everyoneAnswered || fiveMinPassed) {
      this.sekerStage = sekerStage.FINISHED;
    }
  }

  // שליחת הודעה מתאימה לכל יוזר כשיוזר חדש הצטרף
  private void broadcastJoin(String newUserName) { // נקראת אחרי שהתקבל אפדייט שנשלחה הודעה היי והיוזר לא קיים ברשימת
                                                   // האיידי׳ס
    SendMessage sendMessage = new SendMessage();
    for (Long currentId : this.chatIds) {
      sendMessage.setChatId(currentId);
      sendMessage.setText(
          "A new member joined the community! Welcome " + newUserName +
              "! \nCurrent number of members: " + this.chatIds.size());
      try {
        execute(sendMessage);
      } catch (TelegramApiException e) {
        throw new RuntimeException(e);
      }
    }
  }

  public Map<Long, List<String>> getFinalDataMap() {
    // אם הסקר-סטייג׳ הגיע לשלב האחרון (סיים) אז נשלח את המפה userAnswersMap
    if (this.sekerStage == sekerStage.FINISHED) {
      return this.userAnswersMap;
    }
    return null;
  }

}
