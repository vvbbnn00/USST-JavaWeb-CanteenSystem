package cn.vvbbnn00.canteen.util;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import jakarta.validation.constraints.*;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.URL;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeParseException;
import java.util.*;

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
        if (obj == null) {
            throw new IllegalArgumentException("请求内容不能为空");
        }
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
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
    }

    /**
     * 对提供的对象执行Hibernate验证，如果验证失败则抛出异常。
     *
     * @param obj 要进行验证的实体对象
     * @throws IllegalArgumentException 如果任何提供的对象的验证失败
     */
    public static void doHibernateValidate(Object... obj) throws IllegalArgumentException {
        for (Object o : obj) {
            doHibernateValidate(o);
        }
    }


    /**
     * 验证请求参数是否符合Hibernate的注解要求，若不符合则抛出异常。
     * <p>
     * 由于技术原因，目前该方法只支持以下几类最基本的注解：
     * <ul>
     *     <li>{@link NotNull}</li>
     *     <li>{@link Min}</li>
     *     <li>{@link Max}</li>
     *     <li>{@link Length}</li>
     *     <li>{@link NotEmpty}</li>
     *     <li>{@link NotBlank}</li>
     *     <li>{@link Email}</li>
     *     <li>{@link Pattern}</li>
     *     <li>{@link URL}</li>
     * </ul>
     * <p>
     * 代码示例：<p>
     * 该函数是Jax-RS资源类中的一个方法，用于校验请求参数是否符合Hibernate的注解要求。
     * <pre>
     * {@code
     * @GET
     * @Path("/{id}")
     * @Produces(MediaType.APPLICATION_JSON)
     * @CheckRole("admin")
     * public BasicDataResponse hello(
     *     @PathParam("id") @NotNull @Min(value = 5, message = "无效的Id") Integer id,
     *     @QueryParam("name") @Length(min = 1, max = 20, message = "用户名的长度不符合要求") String name
     * ) {
     *     // 校验请求参数，请仔细阅读该方法的文档
     *     RequestValidatorUtils.doHibernateParamsValidate(id, name);
     *     BasicDataResponse response = new BasicDataResponse();
     *     response.setMessage("Hello, " + name + "!");
     *     response.setData(id);
     *     return response;
     * }}
     * </pre>
     * 这段代码中，{@code @PathParam("id")} 和 {@code @QueryParam("name")} 是Jax-RS的注解，
     * 用于获取请求参数。{@code @NotNull} 和 {@code @Min} 是Hibernate的注解，用于校验请求参数。
     * <p><p>
     * 在这个案例中，当{@code GET /hello/1?name=vvbbnn00}请求到达时，函数将抛出异常，因为{@code id}参数的值为1，
     * 而{@code @Min(value = 5, message = "无效的Id")}要求`id`参数的值必须大于等于5。
     * <p>
     * 当`GET /hello/5?name=vvbbnn00`请求到达时，函数将正常执行，返回
     * {@code {"code": 200, "message": "Hello, vvbbnn00!", "data": 5}}。
     *
     * @param values  待验证的值，必须与方法的参数顺序一致，且数量一致
     * @throws IllegalArgumentException 当验证失败时抛出
     */
    public static void doHibernateParamsValidate(Object... values) throws IllegalArgumentException {
        // 获取当前方法的名称
        // 由于调用栈中的第一个方法是本方法，第二个方法是调用本方法的方法，第三个方法才是真正的请求方法，故取第三个方法
        Method requestMethod = MethodCache.getCachedMethod(2);

        // 获取当前方法的参数列表
        Class<?>[] parameterTypes = requestMethod.getParameterTypes();
        if (parameterTypes.length != values.length) {
            throw new IllegalArgumentException("[代码错误] 传入的参数数量与方法的参数数量不匹配，这会导致校验失败。");
        }

        // 获取当前方法的参数注解列表
        Annotation[][] parameterAnnotations = requestMethod.getParameterAnnotations();

        // 对每个参数进行验证
        for (int i = 0; i < parameterTypes.length; i++) {
            Class<?> parameterType = parameterTypes[i];
            Annotation[] annotations = parameterAnnotations[i];
            Object value = values[i];

            // 遍历每个注解，进行验证
            for (Annotation annotation : annotations) {
                // 若参数类型为基本类型，则不进行验证
                if (parameterType.isPrimitive()) {
                    continue;
                }
                checkAnnotation(annotation, value); // 验证注解
            }
        }
    }


    /**
     * 检查给定的注解与提供的值，如果校验失败将抛出异常。
     *
     * @param annotation 要进行比对的注解
     * @param value      需要校验的值
     * @throws IllegalArgumentException 如果校验失败
     */
    private static void checkAnnotation(Annotation annotation, Object value) throws IllegalArgumentException {
        if (value == null) {
            if (annotation instanceof NotNull) {
                throw new IllegalArgumentException(((NotNull) annotation).message());
            }
            return; // 对于其它注解，如果值为 null，则直接返回
        }

        // 数值型参数的验证

        // 验证最小值
        if (annotation instanceof Min) {
            if (value instanceof Number) {
                Number numberValue = (Number) value;
                if (numberValue.doubleValue() < ((Min) annotation).value()) {
                    throw new IllegalArgumentException(((Min) annotation).message());
                }
            } else {
                throw new IllegalArgumentException("[代码错误] 无法对该类型的参数进行最小值校验。");
            }
        }

        // 验证最大值
        if (annotation instanceof Max) {
            if (value instanceof Number) {
                Number numberValue = (Number) value;
                if (numberValue.doubleValue() > ((Max) annotation).value()) {
                    throw new IllegalArgumentException(((Max) annotation).message());
                }
            } else {
                throw new IllegalArgumentException("[代码错误] 无法对该类型的参数进行最大值校验。");
            }
        }

        // 字符型参数的验证

        // 验证字符串长度
        if (annotation instanceof Length) {
            if (value instanceof String) {
                String stringValue = (String) value;
                if (stringValue.length() < ((Length) annotation).min()
                        || stringValue.length() > ((Length) annotation).max()) {
                    throw new IllegalArgumentException(((Length) annotation).message());
                }
            } else {
                throw new IllegalArgumentException("[代码错误] 无法对该类型的参数进行字符串长度校验。");
            }
        }

        // 验证字符串是否为空
        if (annotation instanceof NotEmpty) {
            if (value instanceof String) {
                String stringValue = (String) value;
                if (stringValue.isEmpty()) {
                    throw new IllegalArgumentException(((NotEmpty) annotation).message());
                }
            } else {
                throw new IllegalArgumentException("[代码错误] 无法对该类型的参数进行字符串是否为空校验。");
            }
        }

        // 验证字符串是否为空或空白
        if (annotation instanceof NotBlank) {
            if (value instanceof String) {
                String stringValue = (String) value;
                if (stringValue.isBlank()) {
                    throw new IllegalArgumentException(((NotBlank) annotation).message());
                }
            } else {
                throw new IllegalArgumentException("[代码错误] 无法对该类型的参数进行字符串是否为空校验。");
            }
        }

        // 验证字符串是否为邮箱
        if (annotation instanceof Email) {
            if (value instanceof String) {
                String stringValue = (String) value;
                if (!stringValue.matches("^[a-zA-Z0-9_-]+@[a-zA-Z0-9_-]+(\\.[a-zA-Z0-9_-]+)+$")) {
                    throw new IllegalArgumentException(((Email) annotation).message());
                }
            } else {
                throw new IllegalArgumentException("[代码错误] 无法对该类型的参数进行邮箱格式校验。");
            }
        }

        // 验证字符串是否满足正则表达式
        if (annotation instanceof Pattern) {
            if (value instanceof String) {
                String stringValue = (String) value;
                if (!stringValue.matches(((Pattern) annotation).regexp())) {
                    throw new IllegalArgumentException(((Pattern) annotation).message());
                }
            } else {
                throw new IllegalArgumentException("[代码错误] 无法对该类型的参数进行正则表达式校验。");
            }
        }

        // 验证字符串是否为URL
        if (annotation instanceof URL) {
            if (value instanceof String) {
                String stringValue = (String) value;
                if (!stringValue.matches("^(https?://)?[a-zA-Z0-9_-]+(\\.[a-zA-Z0-9_-]+)+(:\\d+)?(/.*)?$")) {
                    throw new IllegalArgumentException(((URL) annotation).message());
                }
            } else {
                throw new IllegalArgumentException("[代码错误] 无法对该类型的参数进行URL格式校验。");
            }
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
