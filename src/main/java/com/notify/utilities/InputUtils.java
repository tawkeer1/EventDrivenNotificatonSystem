package com.notify.utilities;

import org.slf4j.Logger;

import java.util.Scanner;
import java.util.function.Predicate;

public class InputUtils {

    private static final Scanner scanner = new Scanner(System.in);

    public static String prompt(String message, Predicate<String> validator, String errorMessage, Logger logger) {
        while (true) {
            logger.info(message);
            String input = scanner.nextLine().trim();

            if (validator.test(input)) {
                return input;
            }

            logger.warn(errorMessage);
        }
    }

    public static int promptInt(String message, Predicate<Integer> validator, String errorMessage, Logger logger) {
        while (true) {
            logger.info(message);
            String input = scanner.nextLine().trim();

            try {
                int value = Integer.parseInt(input);
                if (validator.test(value)) {
                    return value;
                } else {
                    logger.warn(errorMessage);
                }
            } catch (NumberFormatException e) {
                logger.warn("Invalid number format.");
            }
        }
    }

    public static String promptNonEmpty(String message, Logger logger) {
        return prompt(message,
                input -> input != null && !input.isBlank(),
                "Input cannot be empty.",
                logger);
    }

    public static String promptMatching(String message, Predicate<String> validator, String errorMessage, Logger logger) {
        String input;
        do {
            logger.info(message);
            input = scanner.nextLine().trim();
            if (!validator.test(input)) {
                logger.warn(errorMessage);
                input = ""; // force retry
            }
        } while (input.isEmpty());
        return input;
    }
}
