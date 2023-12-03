package cn.vvbbnn00.canteen.util;

import java.util.logging.Level;
import java.util.logging.Logger;

public class LogUtils {
    private static final Logger LOGGER = Logger.getLogger(LogUtils.class.getName());

    public static void log(Level level, String message) {
        // 获取调用者信息
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        String callerInfo = "";
        if (stackTrace.length > 3) {
            StackTraceElement caller = stackTrace[3]; // 第一个是getStackTrace，第二个是log方法本身
            callerInfo = String.format("[%s.%s(Line:%d)] ", caller.getClassName(), caller.getMethodName(), caller.getLineNumber());
        }

        // 根据日志等级输出信息
        if (LOGGER.isLoggable(level)) {
            LOGGER.log(level, callerInfo + message);

            // 输出traceback信息
            if (level == Level.SEVERE) {
                for (StackTraceElement ste : stackTrace) {
                    LOGGER.log(Level.SEVERE, "at " + ste.toString());
                }
            }
        }
    }

    // 方便使用的静态方法
    public static void info(String message) {
        log(Level.INFO, message);
    }

    public static void warning(String message) {
        log(Level.WARNING, message);
    }

    public static void severe(String message) {
        log(Level.SEVERE, message);
    }

    public static void error(String message) {
        severe(message);
    }
}
