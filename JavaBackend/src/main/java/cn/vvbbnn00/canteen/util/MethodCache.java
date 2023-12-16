package cn.vvbbnn00.canteen.util;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.concurrent.ConcurrentHashMap;

public class MethodCache {
    private static final ConcurrentHashMap<String, Method> methodCache = new ConcurrentHashMap<>();

    /**
     * Retrieves a cached Method object based on the current stack trace.
     *
     * @param stackTraceDepth The depth of the stack trace.
     * @return The cached Method object.
     * @throws IllegalArgumentException if the stack trace depth is too large.
     * @throws RuntimeException         if there is an exception while retrieving the Method object.
     */
    public static Method getCachedMethod(int stackTraceDepth) {
        StackTraceElement[] stackTrace = new Exception().getStackTrace();
        // LogUtils.info(Arrays.toString(stackTrace));
        if (stackTrace.length <= stackTraceDepth) {
            throw new IllegalArgumentException("Stack trace depth is too large.");
        }

        StackTraceElement element = stackTrace[stackTraceDepth];
        String className = element.getClassName();
        String methodName = element.getMethodName();
        String key = className + "." + methodName;


        // 暂时不使用缓存，因为调试时会出现缓存不更新的问题
        try {
            Class<?> clazz = Class.forName(className);
            // Here, you would need to handle overloads appropriately.
            // This example assumes no overloading.
            for (Method method : clazz.getDeclaredMethods()) {
                if (method.getName().equals(methodName)) {
                    method.setAccessible(true); // if methods are not public
                    return method;
                }
            }
            throw new NoSuchMethodException("Method " + methodName + " not found in " + className);
        } catch (ClassNotFoundException | NoSuchMethodException e) {
            throw new IllegalArgumentException(e);
        }


//        return methodCache.computeIfAbsent(key, k -> {
//            try {
//                Class<?> clazz = Class.forName(className);
//                // Here, you would need to handle overloads appropriately.
//                // This example assumes no overloading.
//                for (Method method : clazz.getDeclaredMethods()) {
//                    if (method.getName().equals(methodName)) {
//                        method.setAccessible(true); // if methods are not public
//                        return method;
//                    }
//                }
//                throw new NoSuchMethodException("Method " + methodName + " not found in " + className);
//            } catch (ClassNotFoundException | NoSuchMethodException e) {
//                throw new IllegalArgumentException(e);
//            }
//        });
    }
}
