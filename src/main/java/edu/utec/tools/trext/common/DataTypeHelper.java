package edu.utec.tools.trext.common;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DataTypeHelper {

  private static final Logger logger = LogManager.getLogger(DataTypeHelper.class);

  private static boolean isNull(Object raw) {
    return raw == null;
  }

  public static boolean isInteger(Object raw) {

    if (isNull(raw)) {
      logger.debug("raw value to evaluate data type is null");
      return false;
    }

    if (raw instanceof Integer) {
      logger.debug(raw + " is an instance of Integer");
      return true;
    }

    if (!(raw instanceof String)) {
      logger.debug("object class is not Integer nor String. Definitely is not an integer");
      return false;
    }

    logger.debug(raw + " is an string. Detecting if it is a int value with regex");
    Pattern pattern = Pattern.compile("^-?\\d{1,10}$");
    Matcher matcher = pattern.matcher((String) raw);
    boolean result = matcher.find();
    logger.debug(result?"is an int":"not an int");
    return result;
  }

  public static int getInt(Object raw) throws Exception {

    if (isNull(raw)) {
      throw new Exception("raw value is null. Datatype cannot be inferred");
    }

    if (raw instanceof Integer) {
      return ((Integer) raw).intValue();
    }

    if (raw instanceof String) {
      try {
        return Integer.parseInt((String) raw);
      } catch (Exception e) {
        throw new Exception("raw value is not a valid integer: " + raw);
      }
    }

    throw new Exception(raw + " is not Boolean nor String. Definitely is not an integer");

  }

  public static boolean isDouble(Object raw) {

    if (isNull(raw)) {
      return false;
    }

    if (raw instanceof Double) {
      logger.debug(raw + " is an instance of Double");
      return true;
    }

    if (!(raw instanceof String)) {
      logger.debug("object class is not Double nor String. Definitely is not a double");
      return false;
    }

    logger.debug(raw + " is an string. Detecting if it is a double value with regex");
    Pattern pattern = Pattern.compile("\\d+\\.\\d+");
    Matcher matcher = pattern.matcher((String) raw);
    boolean result = matcher.find();
    logger.debug(result?"is a double":"not a double");
    return result;
  }

  public static double getDouble(Object raw) throws Exception {

    if (isNull(raw)) {
      throw new Exception("raw value is null. Datatype cannot be inferred");
    }

    if (raw instanceof Double) {
      return ((Double) raw).doubleValue();
    }

    if (raw instanceof String) {
      try {
        return Double.parseDouble((String) raw);
      } catch (Exception e) {
        throw new Exception("raw value is not a valid double: " + raw);
      }
    }

    throw new Exception(raw + " is not Boolean nor String. Definitely is not an double");

  }

  public static boolean isBoolean(Object raw) {

    if (isNull(raw)) {
      return false;
    }

    if (raw instanceof Boolean) {
      logger.debug(raw + " is an instance of Boolean");
      return true;
    }

    if (!(raw instanceof String)) {
      logger.debug("object class is not Boolean nor String. Definitely is not an boolean");
      return false;
    }

    logger.debug(raw + " is an string. Detecting if it is a boolean value with regex");
    boolean result = ((String) raw).matches("(true|false)");
    logger.debug(result?"is an boolean":"not a boolean");
    return result;
  }

  public static boolean getBoolean(Object raw) throws Exception {

    if (isNull(raw)) {
      throw new Exception("raw value is null. Datatype cannot be inferred");
    }

    if (raw instanceof Boolean) {
      return ((Boolean) raw).booleanValue();
    }

    if (raw instanceof String) {
      try {
        return Boolean.parseBoolean((String) raw);
      } catch (Exception e) {
        throw new Exception("raw value is not a valid boolean: " + raw);
      }
    }

    throw new Exception(raw + " is not Boolean nor String. Definitely is not an boolean");

  }

  public static boolean isLong(Object raw) {

    if (isNull(raw)) {
      return false;
    }

    if (raw instanceof Long) {
      logger.debug(raw + " is an instance of Long");
      return true;
    }

    if (!(raw instanceof String)) {
      logger.debug("object class is not Long nor String. Definitely is not an Long");
      return false;
    }
    
    logger.debug(raw + " is an string. Detecting if it is a long value with regex");
    Pattern pattern = Pattern.compile("^-?\\d{1,19}$");
    Matcher matcher = pattern.matcher((String) raw);
    boolean result = matcher.find();
    logger.debug(result?"is a long":"not a long");
    return result;    
  }
  
  public static long getLong(Object raw) throws Exception {

    if (isNull(raw)) {
      throw new Exception("raw value is null. Datatype cannot be inferred");
    }

    if (raw instanceof Long) {
      return ((Long) raw).longValue();
    }

    if (raw instanceof String) {
      try {
        return Long.parseLong((String) raw);
      } catch (Exception e) {
        throw new Exception("raw value is not a valid long: " + raw);
      }
    }

    throw new Exception(raw + " is not Long nor String. Definitely is not an Long");

  }  

  public static boolean isQuotedString(String raw) {
    if (isNull(raw)) {
      return false;
    }
    //"\"([^\"]*)\""
    Pattern pattern = Pattern.compile("\"[^\"]*\"");
    Matcher matcher = pattern.matcher(raw);
    return matcher.find();
  }

  public static boolean isString(Object raw) {
    if (isNull(raw)) {
      return false;
    }
    boolean result = raw instanceof String;
    logger.debug(result?raw+" is a string":raw+"is not a string");
    return result;
  }

}
