package cn.vvbbnn00.canteen.util;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * 用于记录不同日志级别消息的实用工具类
 */
public class LogUtils {
    private static final Logger LOGGER = Logger.getLogger(LogUtils.class.getName());

    /**
     * 使用指定的日志级别和可选的 throwable 记录一条消息。
     *
     * @param level     日志级别
     * @param message   需要记录的消息
     * @param throwable 需要记录的 throwable（可以为 null）
     */
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


    /**
     * 以格式化的字符串形式获取调用者的信息，包括类名，方法名和行号。
     *
     * @return 以 "[ClassName.methodName(行:lineNumber)]" 格式表示的调用者信息的字符串。
     */
    private static String getCallerInfo() {
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        for (StackTraceElement ste : stackTrace) {
            if (!ste.getClassName().equals(LogUtils.class.getName())
                    && ste.getClassName().indexOf("java.lang.Thread") != 0) {
                return String.format("[%s.%s(Line:%d)] ", ste.getClassName(), ste.getMethodName(), ste.getLineNumber());
            }
        }
        return "";
    }


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
