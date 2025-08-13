// 7/8/25 next time continue from showSekerData() and check the bot
package com.example;

import java.util.ArrayList;
import java.util.List;
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
            telegramBotsApi.registerBot(new SekerBot());
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }

        SekerBot sekerBot = new SekerBot();
        // כדי לוודא שהחלון יווצר רק אחרי שהסקר יסתיים צריך לבדוק בלולאה שכל עוד חוזר
        // מאותה מתודה נאל, ז״א שהסקר לא הגיע לשלב האחרון, אז לא ייצור את החלון.
        while (sekerBot.getFinalDataMap() == null) { //
            if (sekerBot.getFinalDataMap() != null) {
                new ResultsWindow(sekerBot.getFinalDataMap());
                break;
            }
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
        q1 = scanner.nextLine();
        System.out.println("Enter 4 answer options for the second question:");
        for (int i = 0; i < 4; i++) {
            System.out.print(i + 1 + ". ");
            ansToQ2.add(scanner.nextLine());
        }
        scanner.close();
    }
}