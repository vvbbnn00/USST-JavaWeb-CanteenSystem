package cn.vvbbnn00.canteen.util;

import cn.vvbbnn00.canteen.dao.Hikari;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.sql.*;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static cn.vvbbnn00.canteen.util.StringUtils.camelToSnake;

public class SqlStatementUtils {

    /**
     * 生成并返回一个PreparedStatement的插入语句
     *
     * @param connection 数据库连接
     * @param entity     实体对象
     * @param fields     需要插入的字段数组
     * @return PreparedStatement
     */
    public static PreparedStatement generateInsertStatement(Connection connection, Object entity, String[] fields)
            throws SQLException, IllegalAccessException, NoSuchFieldException {
        Class<?> clazz = entity.getClass(); // 获取实体类的Class对象
        String tableName = camelToSnakeQuote(clazz.getSimpleName()); // 将类名转换为下划线命名
        tableName = Hikari.getDbName() + "." + tableName; // 添加数据库名

        String fieldNames = Arrays.stream(fields)
                .map(SqlStatementUtils::camelToSnakeQuote)
                .collect(Collectors.joining(", "));

        String placeholders = Arrays.stream(fields)
                .map(field -> "?")
                .collect(Collectors.joining(", "));

        String sql = "INSERT INTO " + tableName + " (" + fieldNames + ") VALUES (" + placeholders + ");";
        PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        setStatementParams(preparedStatement, entity, fields);
        return preparedStatement;
    }


    /**
     * 生成并返回一个PreparedStatement的更新语句，仅限基础更新语句，不支持复杂的条件
     *
     * @param connection 数据库连接
     * @param entity     实体对象
     * @param fields     需要更新的字段数组
     * @param conditions 条件字段数组
     * @return PreparedStatement
     */
    public static PreparedStatement generateUpdateStatement(Connection connection, Object entity, String[] fields, String[] conditions)
            throws SQLException, IllegalAccessException, NoSuchFieldException {
        Class<?> clazz = entity.getClass(); // 获取实体类的Class对象
        String tableName = camelToSnakeQuote(clazz.getSimpleName()); // 将类名转换为下划线命名
        tableName = Hikari.getDbName() + "." + tableName; // 添加数据库名

        String fieldNames = Arrays.stream(fields)
                .map(SqlStatementUtils::camelToSnakeQuote)
                .collect(Collectors.joining(" = ?, "));

        String conditionNames = Arrays.stream(conditions)
                .map(SqlStatementUtils::camelToSnakeQuote)
                .collect(Collectors.joining(" = ? AND "));

        String sql = "UPDATE " + tableName + " SET " + fieldNames + " = ? WHERE " + conditionNames + " = ?;";
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        setStatementParams(preparedStatement, entity, fields);
        setStatementParams(preparedStatement, entity, conditions, fields.length);
        return preparedStatement;
    }

    /**
     * 生成并返回选择语句的基础部分
     *
     * @param clazz  实体类
     * @param fields 需要选择的字段数组
     * @return 选择语句的基础部分
     */
    public static String generateBasicSelectSql(Class<?> clazz, String[] fields) {
        String tableName = camelToSnakeQuote(clazz.getSimpleName()); // 将类名转换为下划线命名
        tableName = Hikari.getDbName() + "." + tableName; // 添加数据库名

        String fieldNames = Arrays.stream(fields)
                .map(SqlStatementUtils::camelToSnakeQuote)
                .collect(Collectors.joining(", "));

        return "SELECT " + fieldNames + " FROM " + tableName;
    }


    /**
     * 生成并返回选择语句的条件部分
     *
     * @param conditions 条件字段数组
     * @return 选择语句的条件部分
     */
    public static String generateWhereSql(List<String> conditions) {
        if (conditions.isEmpty()) {
            return "";
        }
        return " WHERE " + String.join(" AND ", conditions);
    }

    /**
     * 生成并返回一个PreparedStatement的更新语句
     *
     * @param preparedStatement PreparedStatement
     * @param entity            实体对象
     * @param fields            需要更新的字段数组
     * @param offset            偏移量
     */
    private static void setStatementParams(PreparedStatement preparedStatement, Object entity, String[] fields, int offset)
            throws SQLException, IllegalAccessException, NoSuchFieldException {
        Class<?> clazz = entity.getClass(); // 获取实体类的Class对象
        for (int i = 0; i < fields.length; i++) {
            int index = i + offset + 1;
            Field field = clazz.getDeclaredField(fields[i]);
            field.setAccessible(true);
            // 判断是否是enum类型
            if (field.getType().isEnum()) {
                preparedStatement.setObject(index, field.get(entity).toString());
                continue;
            }
            // 判断是否是boolean类型
            if (field.getType().equals(boolean.class)) {
                preparedStatement.setObject(index, field.getBoolean(entity) ? 1 : 0);
                continue;
            }
            // 判断是否为LocalDateTime类型，如是，则转换为Timestamp类型
            if (field.getType().equals(java.time.LocalDateTime.class)) {
                preparedStatement.setObject(index, java.sql.Timestamp.valueOf((java.time.LocalDateTime) field.get(entity)));
                continue;
            }
            preparedStatement.setObject(index, field.get(entity));
        }
    }

    /**
     * 生成并返回一个PreparedStatement的更新语句
     *
     * @param preparedStatement PreparedStatement
     * @param entity            实体对象
     * @param fields            需要更新的字段数组
     */
    private static void setStatementParams(PreparedStatement preparedStatement, Object entity, String[] fields)
            throws SQLException, IllegalAccessException, NoSuchFieldException {
        setStatementParams(preparedStatement, entity, fields, 0);
    }


    /**
     * 通过ResultSet装配实体
     *
     * @param resultSet ResultSet
     * @param clazz     实体类的Class对象
     */
    public static Object makeEntityFromResult(ResultSet resultSet, Class<?> clazz) throws SQLException, IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException {
        Object entity = clazz.getDeclaredConstructor().newInstance();
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            String column = camelToSnake(field.getName()); // 将驼峰命名转换为下划线命名
            // 判断column是否存在
            try {
                resultSet.findColumn(column);
            } catch (SQLException e) {
                continue;
            }
            // 判断是否是enum类型
            if (field.getType().isEnum()) {
                field.set(entity, Enum.valueOf((Class<Enum>) field.getType(), resultSet.getString(camelToSnake(field.getName()))));
                continue;
            }
            // 判断是否是boolean类型
            if (field.getType().equals(Boolean.class)) {
                field.set(entity, resultSet.getInt(camelToSnake(field.getName())) == 1);
                continue;
            }
            field.set(entity, resultSet.getObject(column));
        }
        return entity;
    }

    /**
     * 将驼峰命名转换为下划线命名，并添加反引号
     *
     * @param camelCaseString 驼峰命名的字符串
     * @return 下划线命名的字符串
     */
    public static String camelToSnakeQuote(String camelCaseString) {
        return "`" + camelToSnake(camelCaseString) + "`";
    }

    /**
     * 生成指定数量的问号
     *
     * @param count 问号的数量
     * @return 问号字符串
     */
    public static String generateQuestionMarks(int count) {
        StringBuilder sb = new StringBuilder();
        sb.append("?, ".repeat(Math.max(0, count)));
        return sb.substring(0, sb.length() - 2);
    }
}