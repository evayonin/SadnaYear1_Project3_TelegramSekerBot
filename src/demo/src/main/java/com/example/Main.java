package com.example;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

public class Main {

    public static String q1;
    public static String q2;
    public static List<String> ansToQ1 = new ArrayList<>();
    public static List<String> ansToQ2 = new ArrayList<>();
    // לא יכולים להיות פינאליים כי מאתחלים עם סקאנר בהמשך. בבוט הם פינאליים

    public static void main(String[] args) {
        chooseQuestionsAndAns();

        try {
            TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
            SekerBot sekerBot = new SekerBot();
            telegramBotsApi.registerBot(sekerBot); // הופעה יחידה

            Map<Long, List<String>> results;
            // בדיקה מקוצרת שכל עוד המפה ריקה ומה שחוזר מהמתודה זה נאל (שהסקר לא הגיע לשלב
            // פינישד):
            while ((results = sekerBot.getFinalDataMap()) == null) {
                try {
                    Thread.sleep(200);
                } catch (InterruptedException ignored) {
                }
            }
            Map<Long, List<String>> finalResults = results; // שמירת המפה הסופית
            // רק אחרי שייצא מלולאת הווייל ז״א כשיש מפה סופית, ייצור את החלון:
            javax.swing.SwingUtilities.invokeLater(() -> new ResultsWindow(finalResults));

        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    public static void chooseQuestionsAndAns() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter the first question of your questionnaire:");
        q1 = scanner.nextLine();
        System.out.println("Enter 4 answer options for the first question:");
        for (int i = 0; i < 4; i++) {
            System.out.print(i + 1 + ". ");
            ansToQ1.add(scanner.nextLine());
        }
        System.out.println("Enter the second question of your questionnaire:");
        q2 = scanner.nextLine();
        System.out.println("Enter 4 answer options for the second question:");
        for (int i = 0; i < 4; i++) {
            System.out.print(i + 1 + ". ");
            ansToQ2.add(scanner.nextLine());
        }
        scanner.close();
    }
}