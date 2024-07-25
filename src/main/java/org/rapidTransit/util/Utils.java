package org.rapidTransit.util;

import java.util.Scanner;

public class Utils {
    private static final int DEFAULT_MIN = 1;
    private static final int DEFAULT_MAX = 3;

    public static int getValidChoice(int min, int max, Scanner scanner) {
        while (true) {
            try {
                int choice = Integer.parseInt(scanner.nextLine());
                if (choice >= min && choice <= max) {
                    return choice;
                }
                System.out.print(STR."Please enter a number between \{min} and \{max}: ");
            } catch (NumberFormatException e) {
                System.out.print("Invalid input. Please enter a number: ");
            }
        }
    }

    public static long getValidNumber(Scanner scanner, String message) {
        System.out.print(message);
        try {
            return Long.parseLong(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter a number");
            return 0;
        }
    }

    public static int getDefaultChoice(Scanner scanner, String message) {
        return getChoice(scanner, message, null);
    }

    public static int getChoice(Scanner scanner, String message, Integer max) {
        int currentMax = (max == null) ? DEFAULT_MAX : max;
        System.out.println(message);
        System.out.print("Please select an option: ");
        return getValidChoice(DEFAULT_MIN, currentMax, scanner);
    }

    public static String getValidString(Scanner scanner, String message) {
        System.out.print(message);
        return scanner.nextLine();
    }
}
