package com.wydpp.utils;

import gov.nist.core.StackLogger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Properties;

public class Log4l2Writer implements StackLogger {

    /**
     * The logger to which we will write our logging output.
     */
    private Logger logger;

    /**
     * log a stack trace. This helps to look at the stack frame.
     */
    public void logStackTrace() {
        this.logStackTrace(TRACE_DEBUG);

    }

    public void logStackTrace(int traceLevel) {

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        StackTraceElement[] ste = new Exception().getStackTrace();
        // Skip the log writer frame and log all the other stack frames.
        for (int i = 1; i < ste.length; i++) {
            String callFrame = "[" + ste[i].getFileName() + ":"
                    + ste[i].getLineNumber() + "]";
            pw.print(callFrame);
        }
        pw.close();
        String stackTrace = sw.getBuffer().toString();
        logger.debug(stackTrace);

    }

    /**
     * Get the line count in the log stream.
     *
     * @return
     */
    public int getLineCount() {
        return 0;
    }

    /**
     * Get the logger.
     *
     * @return
     */
    public Logger getLogger() {
        return logger;
    }


    /**
     * Log an exception.
     *
     * @param ex
     */
    public void logException(Throwable ex) {

        logger.error("Error", ex);
    }

    /**
     * Log a message into the log file.
     *
     * @param message message to log into the log file.
     */
    public void logDebug(String message) {
        logger.debug(message);

    }

    /*
     * (non-Javadoc)
     * @see gov.nist.core.StackLogger#logDebug(java.lang.String, java.lang.Exception)
     */
    public void logDebug(String message, Exception ex) {
        logger.debug(message, ex);
    }

    /**
     * Log a message into the log file.
     *
     * @param message message to log into the log file.
     */
    public void logTrace(String message) {
        logger.debug(message);
    }

    @Override
    public void logFatalError(String message) {

    }

    /**
     * Set the trace level for the stack.
     */
    private void setTraceLevel(int level) {
        // Nothing
    }

    /**
     * Log an error message.
     *
     * @param message --
     *                error message to log.
     */
    public void logError(String message) {
        logger.error(message);

    }

    public Log4l2Writer(){
        this.logger = LoggerFactory.getLogger(Log4l2Writer.class);
    }

    public Log4l2Writer(String name) {
        this.logger = LoggerFactory.getLogger(name);
    }


    public Log4l2Writer(Class<?>[] classes) {
        this.logger = LoggerFactory.getLogger(Log4l2Writer.class);
    }

    public void setStackProperties(Properties configurationProperties) {

        // Do nothing (can't do anything here, this method is called only for legacy)

    }

    /**
     * @return flag to indicate if logging is enabled.
     */
    public boolean isLoggingEnabled() {

        return logger.isInfoEnabled();
    }

    @Override
    public boolean isLoggingEnabled(int logLevel) {
        return false;
    }


    /**
     * Log an error message.
     *
     * @param message
     * @param ex
     */
    public void logError(String message, Exception ex) {
        Logger logger = this.getLogger();
        logger.error(message, ex);

    }

    /**
     * Log a warning mesasge.
     *
     * @param string
     */
    public void logWarning(String string) {
        getLogger().warn(string);

    }

    /**
     * Log an info message.
     *
     * @param string
     */
    public void logInfo(String string) {
        getLogger().info(string);
    }

    /**
     * Disable logging altogether.
     */
    public void disableLogging() {
        // Do nothing
    }

    /**
     * Enable logging (globally).
     */
    public void enableLogging() {
        // Do nothing

    }

    public String getLoggerName() {
        if (this.logger != null) {
            return logger.getName();
        } else {
            return null;
        }
    }

    public void setBuildTimeStamp(String buildTimeStamp) {
        logger.info("Build timestamp: " + buildTimeStamp);
    }


}
