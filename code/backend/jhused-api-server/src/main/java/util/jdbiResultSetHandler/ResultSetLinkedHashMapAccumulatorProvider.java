package util.jdbiResultSetHandler;

import exceptions.ResultSetMapAccumulatorException;
import lombok.Data;
import org.jdbi.v3.core.result.ResultSetAccumulator;
import org.jdbi.v3.core.statement.StatementContext;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.time.Instant;
import java.util.*;


/**
 * Provide result set accumulator for JDBI3's reduceResultSet func.
 * Can deal with any level of nested joins.
 * Assume column follow strictly underscore naming case, fields' name have their parent fields' name plus under score
 * as prefix. For example: the prefix of User.posts.images will be "user_posts_images_", this is expected when writing
 * the SQL.
 *
 * @param <T> Model (SomeModel)
 */
public class ResultSetLinkedHashMapAccumulatorProvider<T> implements ResultSetAccumulator<LinkedHashMap<Object, T>> {

  private final Class<T> parentClass;
  private final String objectPackageName;

  /**
   * Assume models are stored in <objectPackageName> package
   *
   * @param p The class of Model (SomeModel.class)
   */
  public ResultSetLinkedHashMapAccumulatorProvider(Class<T> p) throws ResultSetMapAccumulatorException {
    if (p == null) {
      throw new ResultSetMapAccumulatorException("Class passed in is null");
    }
    this.parentClass = p;
    objectPackageName = p.getPackageName();
  }

  /**
   * The result set accumulator
   *
   * @param previous previous set
   * @param rs       result set
   * @param ctx      statement context
   * @return previous set, contain a set of target model
   * @throws ResultSetMapAccumulatorException throws exception when mapping not supported, or
   *                                          NoSuchMethodException | IllegalAccessException |
   *                                          InvocationTargetException | InstantiationException
   *                                          | NoSuchFieldException | ResultSetMapAccumulatorException |
   *                                          SQLException occurs.
   */
  @Override
  public LinkedHashMap<Object, T> apply(LinkedHashMap<Object, T> previous, ResultSet rs, StatementContext ctx) throws ResultSetMapAccumulatorException {
    try {
      if (rs.isFirst()) {
        ctx.define(camelToUnderscore(parentClass.getName()), new LinkedHashMap<String, ChildParent>());
      }
      LinkedHashMap<Object, ChildParent> map =
          (LinkedHashMap<Object, ChildParent>) ctx.getAttribute(camelToUnderscore(parentClass.getName()));
      setFields(rs, ctx, map, parentClass, "");
      if (rs.isLast()) {
        for (Map.Entry<Object, ChildParent> entry : map.entrySet()) {
          previous.put(entry.getKey(), (T) entry.getValue().getChild());
        }
      }
    } catch (NoSuchMethodException
        | IllegalAccessException
        | InvocationTargetException
        | InstantiationException
        | NoSuchFieldException
        | ResultSetMapAccumulatorException
        | SQLException e) {
      e.printStackTrace();
      throw new ResultSetMapAccumulatorException(e.getMessage(), e.getCause());
    }
    return previous;
  }

  /**
   * Helper function, that recursively set fields.
   *
   * @param rs          result set
   * @param ctx         statement context, used to register set for complex fields
   * @param accumulator set of parent field/object
   * @param clazz       class of the object
   * @param prefix      prefix of the object, like "somemodel_anothermodel_"
   * @return returns the object (owner of the fields)
   * @throws NoSuchMethodException
   * @throws SQLException
   * @throws IllegalAccessException
   * @throws InvocationTargetException
   * @throws InstantiationException
   * @throws NoSuchFieldException
   * @throws ResultSetMapAccumulatorException
   */
  private Object setFields(ResultSet rs, StatementContext ctx, LinkedHashMap<Object, ChildParent> accumulator,
                           Class<?> clazz, String prefix) throws NoSuchMethodException, SQLException,
      IllegalAccessException, InvocationTargetException, InstantiationException, NoSuchFieldException,
      ResultSetMapAccumulatorException {
    Object object;
    boolean isNewObject = true;
    if (accumulator != null && accumulator.containsKey(castPrimaryKeyFromResultSet(rs, prefix,
        clazz.getDeclaredField("id").getType()))) {
      object =
          accumulator.get(castPrimaryKeyFromResultSet(rs, prefix, clazz.getDeclaredField("id").getType())).getChild();
      isNewObject = false;
    } else {
      object = clazz.getDeclaredConstructor().newInstance();
    }
    Field[] fields = clazz.getDeclaredFields();
    for (Field field : fields) {
      String fieldName = field.getName();
      if (!isMappingSupport(field)) {
        throw new ResultSetMapAccumulatorException("Mapping not supported: fieldName: " + fieldName + " fieldType: " + field.getType());
      }
      if (isNewObject) {
        mapSimpleField(rs, clazz, field, object, prefix);
      }
      mapComplexField(rs, clazz, field, object, prefix, ctx);
    }
    if (accumulator != null && isNewObject)
      accumulator.put(clazz.getDeclaredMethod("getId").invoke(object), new ChildParent(object, new HashSet<>()));
    return object;
  }

  private String uppercaseFirstLetter(String str) {
    if (str.length() == 0)
      throw new IndexOutOfBoundsException();
    if (str.length() == 1)
      return str.toUpperCase();
    return str.substring(0, 1).toUpperCase() + str.substring(1);
  }

  private String getGetterFuncName(String fieldName) {
    return getFuncName("get", fieldName);
  }

  private String getSetterFuncName(String fieldName) {
    return getFuncName("set", fieldName);
  }

  private String getFuncName(String pre, String fieldName) {
    return pre + uppercaseFirstLetter(fieldName);
  }

  private String camelToUnderscore(String str) {
    return str.replaceAll("(.)(\\p{Upper})", "$1_$2").toLowerCase();
  }

  private Object castPrimaryKeyFromResultSet(ResultSet rs, String prefix, Class<?> clazz) throws SQLException {
    Object res;
    switch (clazz.getSimpleName()) {
      case "Integer":
      case "int":
        res = rs.getInt(prefix + "id");
        break;
      case "String":
        res = rs.getString(prefix + "id");
        break;
      default:
        throw new IllegalStateException("Unexpected value: " + clazz.getSimpleName());
    }
    return res;
  }

  private boolean isThere(ResultSet rs, String column) throws SQLException {
    ResultSetMetaData resultSetMetaData = rs.getMetaData();
    int columns = resultSetMetaData.getColumnCount();
    for (int i = 1; i <= columns; ++i) {
      if (column.equals(resultSetMetaData.getColumnName(i)))
        return true;
    }
    return false;
  }

  private boolean isFieldSimpleType(Field field) {
    return field.getType().isPrimitive()
        || field.getType() == BigDecimal.class
        || field.getType() == Instant.class
        || field.getType() == String.class
        || field.getType().isEnum()
        || field.getType().equals(Double.class);
  }

  private boolean isMappingSupport(Field field) {
    return isFieldSimpleType(field)
        || field.getType().getPackageName().equals(objectPackageName)
        || field.getType().equals(List.class);
  }

  /**
   * Map simple field like int, double, String, Instant that do not involve in join.
   *
   * @param rs          result set.
   * @param objectClass object's class
   * @param field       field
   * @param object      object (the parent field/object)
   * @param prefix      prefix
   * @throws NoSuchMethodException
   * @throws SQLException
   * @throws InvocationTargetException
   * @throws IllegalAccessException
   */
  private void mapSimpleField(ResultSet rs, Class<?> objectClass, Field field, Object object, String prefix)
      throws NoSuchMethodException, SQLException, InvocationTargetException, IllegalAccessException {
    String fieldName = field.getName();
    String objectColumnLabel = prefix + camelToUnderscore(fieldName);
    if (isThere(rs, objectColumnLabel)
        && rs.getObject(objectColumnLabel) != null) {
      if (field.getType().isPrimitive() || field.getType().equals(String.class)) {
        objectClass.getDeclaredMethod(getSetterFuncName(fieldName), field.getType()).
            invoke(object, rs.getObject(objectColumnLabel));
      } else if (field.getType().equals(Double.class)) {
        if (rs.getObject(objectColumnLabel).getClass().equals(BigDecimal.class)) {
          objectClass.getDeclaredMethod(getSetterFuncName(fieldName), field.getType()).
              invoke(object, (rs.getBigDecimal(objectColumnLabel)).doubleValue());
        } else {
          objectClass.getDeclaredMethod(getSetterFuncName(fieldName), field.getType()).
              invoke(object, rs.getObject(objectColumnLabel));
        }
      } else if (field.getType().equals(Instant.class)) {
        objectClass.getDeclaredMethod(getSetterFuncName(fieldName), field.getType()).
            invoke(object, (rs.getTimestamp(objectColumnLabel)).toInstant());
      } else if (field.getType().isEnum()) {
        objectClass.getDeclaredMethod(getSetterFuncName(fieldName), field.getType()).
            invoke(object, Enum.valueOf((Class<Enum>) field.getType(),
                rs.getString(objectColumnLabel)));
      }

    }
  }

  /**
   * Map complex field that involve in join. Could be a list of String or Model.
   *
   * @param rs          result set.
   * @param objectClass object's class
   * @param field       field
   * @param object      object (the parent field/object)
   * @param prefix      prefix
   * @param ctx         statement context
   * @throws SQLException
   * @throws InvocationTargetException
   * @throws NoSuchMethodException
   * @throws NoSuchFieldException
   * @throws InstantiationException
   * @throws IllegalAccessException
   */
  private void mapComplexField(ResultSet rs, Class<?> objectClass, Field field, Object object, String prefix,
                               StatementContext ctx)
      throws SQLException, InvocationTargetException, NoSuchMethodException, NoSuchFieldException,
      InstantiationException,
      IllegalAccessException {

    if (field.getType().getPackageName().equals(objectPackageName) && !isFieldSimpleType(field)
        || (field.getType().equals(List.class))) {
      String fieldName = field.getName();
      LinkedHashMap<Object, ChildParent> map =
          (LinkedHashMap<Object, ChildParent>) ctx.getAttribute(prefix + camelToUnderscore(fieldName));
      if (map == null) {
        ctx.define(prefix + camelToUnderscore(fieldName), new LinkedHashMap<String, ChildParent>());
        map = (LinkedHashMap<Object, ChildParent>) ctx.getAttribute(prefix + camelToUnderscore(fieldName));
      }

      if ((isThere(rs, prefix + camelToUnderscore(fieldName) + "_id")
          && rs.getObject(prefix + camelToUnderscore(fieldName) + "_id") != null)) {
        Object fieldObject;
        Class<?> itemType = field.getType();
        if (field.getType().equals(List.class)) {
          itemType = (Class<?>) ((ParameterizedType) field.getGenericType()).getActualTypeArguments()[0];
        }
        fieldObject = setFields(rs, ctx, map, itemType, prefix + camelToUnderscore(fieldName) + "_");
        ChildParent childParent = map.get(fieldObject.getClass().getDeclaredMethod("getId").invoke(fieldObject));
        childParent.getParents().add(object);
      }
      if (rs.isLast()) {
        reduceComplexField(objectClass, field, map);
      }
    }
  }

  /**
   * When all rows have been iterated, reduce all objects (set child field to their parent).
   *
   * @param objectClass object class.
   * @param field       field
   * @param map         LinkedHashMap that has key as id, value as ChildParent, which stores one child and a set of
   *                    parent.
   * @throws NoSuchMethodException
   * @throws InvocationTargetException
   * @throws IllegalAccessException
   */
  private void reduceComplexField(Class<?> objectClass, Field field, LinkedHashMap<Object, ChildParent> map)
      throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
    String fieldName = field.getName();
    Class<?> itemType = field.getType();
    Method mapMethod = objectClass.getDeclaredMethod(getSetterFuncName(fieldName), itemType);
    if (field.getType().equals(List.class)) {
      itemType = (Class<?>) ((ParameterizedType) field.getGenericType()).getActualTypeArguments()[0];
      mapMethod = objectClass.getDeclaredMethod(getFuncName("add", fieldName), itemType);
    }
    for (Map.Entry<Object, ChildParent> objectChildParentEntry : map.entrySet()) {
      ChildParent cp = objectChildParentEntry.getValue();
      for (Object parent : cp.getParents()) {
        mapMethod.invoke(parent, cp.getChild());
      }
    }
  }
}

@Data
class ChildParent {
  Object child;
  Set<Object> parents;

  public ChildParent() {
    parents = new HashSet<>();
  }

  public ChildParent(Object child, HashSet<Object> parents) {
    this.child = child;
    this.parents = parents;
  }
}
