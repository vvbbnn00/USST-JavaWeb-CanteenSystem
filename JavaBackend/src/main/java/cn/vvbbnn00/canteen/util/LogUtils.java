package cn.vvbbnn00.canteen.util;

import java.util.logging.Level;
import java.util.logging.Logger;

public class LogUtils {
    private static final Logger LOGGER = Logger.getLogger(LogUtils.class.getName());

    // 添加异常作为参数
    public static void log(Level level, String message, Throwable throwable) {
        // 获取调用者信息
        String callerInfo = getCallerInfo();

        // 根据日志等级输出信息
        if (LOGGER.isLoggable(level)) {
            LOGGER.log(level, callerInfo + message);

            // 输出异常调用栈
            if (throwable != null && level == Level.SEVERE) {
                LOGGER.log(Level.SEVERE, "Exception caught", throwable);
            }
        }
    }

    // 获取调用此日志的类和方法信息
    private static String getCallerInfo() {
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        for (int i = 0; i < stackTrace.length; i++) {
            StackTraceElement ste = stackTrace[i];
            if (!ste.getClassName().equals(LogUtils.class.getName())) {
                return String.format("[%s.%s(Line:%d)] ", ste.getClassName(), ste.getMethodName(), ste.getLineNumber());
            }
        }
        return "";
    }

    // 方便使用的静态方法，不包含异常
    public static void info(String message) {
        log(Level.INFO, message, null);
    }

    public static void warning(String message) {
        log(Level.WARNING, message, null);
    }

    public static void severe(String message) {
        log(Level.SEVERE, message, null);
    }

    // 重载方法，包含异常
    public static void severe(String message, Throwable throwable) {
        log(Level.SEVERE, message, throwable);
    }

    public static void error(String message, Throwable throwable) {
        severe(message, throwable);
    }
}
