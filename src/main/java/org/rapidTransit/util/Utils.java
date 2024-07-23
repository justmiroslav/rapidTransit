package org.rapidTransit.util;

import java.util.Scanner;

public class Utils {
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
}
