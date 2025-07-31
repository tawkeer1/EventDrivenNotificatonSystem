package com.notify.utilities;

import org.slf4j.Logger;

public class ConsolePrinter {
    private static final Object lock = new Object();

    public static void println(Logger logger, String message) {
        synchronized (lock) {
            logger.info(message);
        }
    }

    public static void printPrompt(Logger logger, String prompt) {
        synchronized (lock) {
            logger.info(prompt);
        }
    }

    public static void printMenu(Logger logger, String... lines) {
        synchronized (lock) {
            for (String line : lines) {
                logger.info(line);
            }
        }
    }
}
