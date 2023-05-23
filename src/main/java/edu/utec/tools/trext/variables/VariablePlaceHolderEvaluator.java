package edu.utec.tools.trext.variables;

import java.util.HashMap;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import edu.utec.tools.trext.common.DataTypeHelper;
import edu.utec.tools.trext.common.StringHelper;
import edu.utec.tools.trext.common.VariablePlaceHolderHelper;

public class VariablePlaceHolderEvaluator {

  private final Logger logger = LogManager.getLogger(VariablePlaceHolderEvaluator.class);
  
  private final String variableSyntaxRegex = "(\\$\\{[\\w:\\^\\$\\s]+\\})";

  public String replaceVariablesAndJockersInString(String rawString, HashMap<String, ?> variables)
      throws Exception {

    if (rawString == null || rawString.isEmpty()) {
      throw new Exception("rawString is required to evaluate regex. Current value:" + rawString);
    }

    
    Matcher m = Pattern.compile(variableSyntaxRegex).matcher(rawString);
    while (m.find()) {

      String key = m.group(0).replace("${", "").replace("}", "");
      logger.debug(String.format("variable was detected: %s in raw string: %s", key, rawString));

      if (key == null || key.equals("")) {
        continue;
      }

      if (variables.containsKey(key)) {
        logger.debug("variable exists in context map");
        rawString = rawString.replace(String.format("${%s}", key),
            VariablePlaceHolderHelper.getStringRepresentation(variables.get(key)));
      } else if (VariablePlaceHolderHelper.containsJockers(key)) {
        logger.debug("variable is a jocker");
        rawString = rawString.replace(String.format("${%s}", key),
            VariablePlaceHolderHelper.parseJocker(key));
      } else if (System.getenv(key) != null && !System.getenv(key).isEmpty()) {
        logger.debug("variable is a system environment variable");
        rawString = rawString.replace(String.format("${%s}", key),
            VariablePlaceHolderHelper.getStringRepresentation(System.getenv(key)));
      } else {
        logger.debug(
            "Variable is not a jocker and does not exist in T-Rext context or s.o environment"
                + variables);
      }
    }
    return rawString;
  }

  public HashMap<String, Object> replaceVariablesAndJockersInMap(HashMap<String, ?> rawMap,
      HashMap<String, ?> variables) throws Exception {

    HashMap<String, Object> newMap = new HashMap<String, Object>();

    for (Entry<String, ?> entry : rawMap.entrySet()) {
      logger.debug("variable name: " + entry.getKey());
      newMap.put((String) entry.getKey(),
          replaceVariablesAndJockersInString((String) entry.getValue(), variables));
    }

    return newMap;
  }
  
  public void appendMapAsSingleVariablesWithPrefix(HashMap<String, ?> sourceMap,
      HashMap<String, Object> targetMap, String prefix) throws Exception {
    
    for (Entry<String, ?> entry : sourceMap.entrySet()) {
      targetMap.put(prefix+(String) entry.getKey(),entry.getValue());
    }
  }

  /*
   * Lookup values in the operative system environment.
   */
  public String evaluteValueIfIsEnvironmentVariable(String rawKey) throws Exception {

    Matcher m = Pattern.compile(variableSyntaxRegex).matcher(rawKey);
    while (m.find()) {
      String key = m.group(0).replace("${", "").replace("}", "");
      logger.debug(String.format("variable was detected: %s in raw key: %s", key, rawKey));

      if (key == null || key.equals("")) {
        continue;
      }

      if (System.getenv(key) != null && !System.getenv(key).isEmpty()) {
        logger.debug("variable is a system variable");
        // TODO
        // rawKey = rawKey.replace(String.format("${%s}", key),
        // VariablePlaceHolderHelper.getStringRepresentation(System.getenv(key)));
        rawKey = rawKey.replace(String.format("${%s}", key), System.getenv(key));
      }
    }

    return rawKey;
  }
  
  /*
   * receive a raw partial and transform it to a expected value
   * */
  public String convertVariableToArgument(String rawArgumentPayload,
      HashMap<String, Object> variables, String httpBodyRawString, boolean cameFromQuotedString)
      throws Exception {

    String evaluatedPartial = null;

    if (rawArgumentPayload.startsWith("$.")) {
      // placeholder is a json expression
      Object jsonPathEvaluatedValue =
          StringHelper.evaluateJsonExpression(rawArgumentPayload, httpBodyRawString);
      logger.debug(String.format("Partial is jsonpath - input: %s, output: %s class: %s",
          rawArgumentPayload, jsonPathEvaluatedValue, jsonPathEvaluatedValue.getClass()));

      if (cameFromQuotedString) {
        evaluatedPartial = "\"" + jsonPathEvaluatedValue + "\"";
      } else {
        // value here is genuine from jsonpath result
        evaluatedPartial =
            StringHelper.convertGeniuneValueToStringRepresentationSafe(jsonPathEvaluatedValue);
      }

    } else if (rawArgumentPayload.startsWith("${") && rawArgumentPayload.endsWith("}")) {
      logger.debug("Partial is variable");
      // placeholder is a variable
      // all values read from varialbes.properties are string
      // TODO: Review when is used a quotes string with prop var
      Object evaluatedValue =
          variables.get(StringHelper.getKeyFromVariableSyntax(rawArgumentPayload));
      if (evaluatedValue == null) {
        throw new Exception(rawArgumentPayload + " was not found as variable.");
      }

      if (cameFromQuotedString) {
        evaluatedPartial = "\"" + evaluatedValue + "\"";
      } else {
        // evaluatedPartial = (String)evaluatedValue;
        evaluatedPartial = convertValueToSimpleStringRepresentation(evaluatedValue);
      }

    } else {
      logger.debug("Partial is not global var nor jsonpath exp: "+rawArgumentPayload);
      logger.debug("Partial came from quotes string: " + cameFromQuotedString);
      if (cameFromQuotedString) {
        evaluatedPartial = "\"" + rawArgumentPayload + "\"";
      } else {
        evaluatedPartial = convertValueToStringRepresentationSafe(rawArgumentPayload);
      }
    }

    return evaluatedPartial;
  }
  
  /*
   * Used to convert a string representation to a value, not an argument
   * */
  public Object convertRawStringToObject(String rawArgumentPayload,
      HashMap<String, Object> variables, String httpBodyRawString)
      throws Exception {

    String evaluatedPartial = null;

    if (rawArgumentPayload.startsWith("$.")) {
      // placeholder is a json expression
      Object jsonPathEvaluatedValue =
          StringHelper.evaluateJsonExpression(rawArgumentPayload, httpBodyRawString);
      logger.debug(String.format("Partial is jsonpath - input: %s, output: %s class: %s",
          rawArgumentPayload, jsonPathEvaluatedValue, jsonPathEvaluatedValue.getClass()));

      if (DataTypeHelper.isQuotedString(rawArgumentPayload)) {
        evaluatedPartial = "\"" + jsonPathEvaluatedValue + "\"";
        return evaluatedPartial;
      } else {
        // value here is genuine from jsonpath result
        return jsonPathEvaluatedValue; 
      }

    } else if (rawArgumentPayload.startsWith("${") && rawArgumentPayload.endsWith("}")) {
      logger.debug("Partial is variable");
      // placeholder is a variable
      // all values read from varialbes.properties are string
      // TODO: Review when is used a quotes string with prop var
      Object evaluatedValue =
          variables.get(StringHelper.getKeyFromVariableSyntax(rawArgumentPayload));
      if (evaluatedValue == null) {
        throw new Exception(rawArgumentPayload + " was not found as variable.");
      }

      if (DataTypeHelper.isQuotedString(rawArgumentPayload)) {
        evaluatedPartial = "\"" + evaluatedValue + "\"";
        return evaluatedPartial;
      } else {
        return evaluatedValue;
      }

    } else {
      logger.debug("Partial is not global var nor jsonpath exp: "+rawArgumentPayload);
      return convertValueToObjectSafe(rawArgumentPayload);      
    }
  }  
  
  private String convertValueToSimpleStringRepresentation(Object value) throws Exception {
    logger.debug(String.format("transform %s to string representation", value));
    if (DataTypeHelper.isString(value)) {
      return (String) value;
    } else if (DataTypeHelper.isInteger(value)) {
      return "" + DataTypeHelper.getInt(value);
    } else if (DataTypeHelper.isLong(value)) {
      return "" + DataTypeHelper.getLong(value);
    } else if (DataTypeHelper.isDouble(value)) {
      return "" + DataTypeHelper.getDouble(value);
    } else if (DataTypeHelper.isBoolean(value)) {
      return "" + DataTypeHelper.getBoolean(value);
    } else {
      throw new Exception(
          String.format("value %s or its class %s is not supported", value, value.getClass()));
    }
  } 
  
  private String convertValueToStringRepresentationSafe(Object value) throws Exception {
    logger.debug(String.format("transform %s to string safe representation", value));
    if (DataTypeHelper.isInteger(value)) {
      return "" + DataTypeHelper.getInt(value);
    } else if (DataTypeHelper.isLong(value)) {
      return "" + DataTypeHelper.getLong(value);
    } else if (DataTypeHelper.isDouble(value)) {
      return "" + DataTypeHelper.getDouble(value);
    } else if (DataTypeHelper.isBoolean(value)) {
      return "" + DataTypeHelper.getBoolean(value);
    } else if (DataTypeHelper.isString(value)) {

      return String.format("\"%s\"", value);
    } else {
      throw new Exception(
          String.format("value %s or its class %s is not supported", value, value.getClass()));
    }
  }
  
  private Object convertValueToObjectSafe(Object value) throws Exception {
    logger.debug(String.format("transform %s to object representation", value));
    if (DataTypeHelper.isInteger(value)) {
      return DataTypeHelper.getInt(value);
    } else if (DataTypeHelper.isLong(value)) {
      return DataTypeHelper.getLong(value);
    } else if (DataTypeHelper.isDouble(value)) {
      return DataTypeHelper.getDouble(value);
    } else if (DataTypeHelper.isBoolean(value)) {
      return DataTypeHelper.getBoolean(value);
    } else if (DataTypeHelper.isString(value)) {
      return value;
    } else {
      throw new Exception(
          String.format("value %s or its class %s is not supported", value, value.getClass()));
    }
  }  
}
