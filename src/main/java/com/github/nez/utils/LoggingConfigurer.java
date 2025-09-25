package com.github.nez.utils;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.core.ConsoleAppender;
import ch.qos.logback.core.FileAppender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;

public class LoggingConfigurer {

    private static final Logger LOGGER = LoggerFactory.getLogger(LoggingConfigurer.class);
    private static boolean isConfigured = false;

    public static void configureFileLogging(String logFilePath) {
        if (isConfigured) {
            LOGGER.debug("Logging already configured, skipping reconfiguration");
            return;
        }

        try {
            // Create log directory if it doesn't exist
            File logFile = new File(logFilePath);
            Files.createDirectories(logFile.getParentFile().toPath());

            // Get the root logger context
            LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();

            // Clear existing appenders
            context.reset();

            // Create file appender
            FileAppender fileAppender = new FileAppender();
            fileAppender.setContext(context);
            fileAppender.setName("FILE");
            fileAppender.setFile(logFilePath);
            fileAppender.setAppend(false); // Overwrite log file each run

            // Create pattern encoder
            PatternLayoutEncoder encoder = new PatternLayoutEncoder();
            encoder.setContext(context);
            encoder.setPattern("%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n");
            encoder.start();

            fileAppender.setEncoder(encoder);
            fileAppender.start();

            // Create console appender (optional - for immediate feedback)
            ConsoleAppender consoleAppender = new ConsoleAppender();
            consoleAppender.setContext(context);
            consoleAppender.setName("CONSOLE");
            consoleAppender.setEncoder(encoder);
            consoleAppender.start();

            // Get root logger and add appenders
            ch.qos.logback.classic.Logger rootLogger = context.getLogger(Logger.ROOT_LOGGER_NAME);
            rootLogger.addAppender(fileAppender);
            rootLogger.addAppender(consoleAppender); // Comment this out if you want file-only logging
            rootLogger.setLevel(Level.INFO);

            // Set debug level for our packages
            ch.qos.logback.classic.Logger ourPackageLogger = context.getLogger("com.github.nez");
            ourPackageLogger.setLevel(Level.DEBUG);

            isConfigured = true;
            LOGGER.info("Logging configured successfully. Log file: {}", logFilePath);

        } catch (Exception e) {
            System.err.println("Failed to configure logging: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void configureFileLogging(String outputDir, String logFileName) {
        String logFilePath = Paths.get(outputDir, logFileName).toString();
        configureFileLogging(logFilePath);
    }
}