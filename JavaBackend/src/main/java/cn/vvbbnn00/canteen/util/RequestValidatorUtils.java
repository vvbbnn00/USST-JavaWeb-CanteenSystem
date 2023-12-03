package cn.vvbbnn00.canteen.util;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

import java.io.IOException;
import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class RequestValidatorUtils {
    /**
     * 获取一个类的所有字段，包括其父类的字段
     *
     * @param clazz 类的Class对象
     * @return 字段列表
     */
    public static List<Field> getAllFields(Class<?> clazz) {
        List<Field> fields = new ArrayList<>();
        while (clazz != null) {
            Collections.addAll(fields, clazz.getDeclaredFields());
            clazz = clazz.getSuperclass();
        }
        return fields;
    }

    /**
     * 通过反射校验请求参数，若校验失败则抛出异常
     *
     * @param request 请求
     * @param clazz   实体类的Class对象
     * @return 实体类对象
     */
    public static Object validate(HttpServletRequest request, Class<?> clazz) throws Exception {
        Object obj = clazz.getDeclaredConstructor().newInstance();
        // 获取实体类的所有字段，包括其父类的字段
        List<Field> fields = getAllFields(clazz);
        for (Field field : fields) {
            String fieldName = field.getName();
            String value = request.getParameter(fieldName);
            if (value == null) {
                continue;
            }
            field.setAccessible(true);
            try {
                setField(obj, field, value);
            } catch (IllegalAccessException e) {
                throw new IllegalArgumentException(fieldName + "参数错误");
            }
        }
        doHibernateValidate(obj);
        return obj;
    }

    /**
     * 执行Hibernate校验，若校验失败则抛出异常
     *
     * @param obj 实体类对象
     */
    public static void doHibernateValidate(Object obj) throws IllegalArgumentException {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();
        Set<ConstraintViolation<Object>> violations = validator.validate(obj);
        if (!violations.isEmpty()) {
            StringBuilder errorMessage = new StringBuilder();
            for (ConstraintViolation<?> violation : violations) {
                errorMessage.append(violation.getMessage()).append(";");
            }
            throw new IllegalArgumentException(errorMessage.toString());
        }
    }

    /**
     * 通过反射设置字段的值
     *
     * @param obj   实体类对象
     * @param field 字段
     * @param value 字段的值
     */
    private static void setField(Object obj, Field field, String value) throws IllegalArgumentException, IllegalAccessException {
        Class<?> type = field.getType();
        // 判断是否是String类型
        if (type.equals(String.class)) {
            field.set(obj, value);
        }

        // 若字段值为空，则不设置
        if (value.isEmpty()) {
            return;
        }

        // 判断是否是enum类型
        if (type.isEnum()) {
            try {
                field.set(obj, Enum.valueOf((Class<Enum>) type, value));
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException(field.getName() + "参数错误;");
            }
            return;
        }
        // 判断是否是boolean类型
        if (type.equals(Boolean.class)) {
            field.set(obj, Boolean.parseBoolean(value));
            return;
        }
        // 判断是否是int类型
        if (type.equals(Integer.class)) {
            try {
                field.set(obj, Integer.parseInt(value));
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException(field.getName() + "不是一个正确的数字;");
            }
            return;
        }
        // 判断是否是LocalDateTime类型
        if (type.equals(LocalDateTime.class)) {
            // LocalDateTime类型在传入时传入时间戳，因此需要转换为LocalDateTime类型
            try {
                long timestamp = Long.parseLong(value);
                field.set(obj, LocalDateTime.ofEpochSecond(timestamp / 1000, 0, ZoneOffset.UTC));
            } catch (DateTimeParseException e) {
                throw new IllegalArgumentException(field.getName() + "不是一个正确的时间;");
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException(field.getName() + "不是一个正确的时间戳;");
            }
        }
        if (type.equals(Long.class)) {
            try {
                field.set(obj, Long.parseLong(value));
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException(field.getName() + "不是一个正确的数字;");
            }
            return;
        }
        if (type.equals(Double.class)) {
            try {
                field.set(obj, Double.parseDouble(value));
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException(field.getName() + "不是一个正确的数字;");
            }
            return;
        }
        if (type.equals(Float.class)) {
            try {
                field.set(obj, Float.parseFloat(value));
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException(field.getName() + "不是一个正确的数字;");
            }
            return;
        }
    }


    /**
     * 从请求路径中解析出ID
     *
     * @param pathInfo 请求路径
     * @param resp     响应
     * @return ID
     */
    public static Integer parseRestIdFromPathInfo(String pathInfo, HttpServletResponse resp) throws IllegalArgumentException, IOException {
        if (pathInfo == null) {
            GsonFactory.makeErrorResponse(resp, 404, "Not Found");
            return null;
        }
        String[] split = pathInfo.split("/");
        if (split.length != 2) {
            GsonFactory.makeErrorResponse(resp, 404, "Not Found");
            return null;
        }
        String restIdStr = split[1];
        int restId;
        if (restIdStr == null) {
            GsonFactory.makeErrorResponse(resp, 404, "Not Found");
            return null;
        }
        try {
            restId = Integer.parseInt(restIdStr);
            if (restId <= 0) {
                throw new NumberFormatException();
            }
        } catch (Exception e) {
            GsonFactory.makeErrorResponse(resp, 400, "无效的ID");
            return null;
        }
        return restId;
    }

    /**
     * 获取请求体
     *
     * @param request 请求
     * @return 请求体
     */
    public static String getFullBody(HttpServletRequest request) throws IOException {
        StringBuilder requestBody = new StringBuilder();
        String line;
        while ((line = request.getReader().readLine()) != null) {
            requestBody.append(line);
        }
        return requestBody.toString();
    }
}
