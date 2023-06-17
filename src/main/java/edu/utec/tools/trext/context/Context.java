package edu.utec.tools.trext.context;

import java.util.HashMap;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.jayway.jsonpath.JsonPath;
import edu.utec.tools.trext.common.DataTypeHelper;
import edu.utec.tools.trext.common.StringHelper;
import edu.utec.tools.trext.variables.VariablePlaceHolderEvaluator;

public class Context {

  private final Logger logger = LogManager.getLogger(this.getClass());
  private VariablePlaceHolderEvaluator variablePlaceHolderEvaluator =
      new VariablePlaceHolderEvaluator();
  private final String spaceEnhancer = "@space";

  public void evaluate(List<String> rawContextLines, HashMap<String, Object> globalVariables,
      HashMap<String, Object> localHttpRequestVariables,
      HashMap<String, Object> localHttpResponseVariables) throws Exception {

    logger.debug("context evaluation is starting...");

    if (rawContextLines == null || rawContextLines.isEmpty()) {
      logger.debug("context is missing. Nothing will be evaluated.");
      return;
    }

    String body = (String) localHttpResponseVariables.get("res:body");

    HashMap<String, Object> variables = new HashMap<String, Object>();
    if (globalVariables != null) {
      variables.putAll(globalVariables);
    }

    if (localHttpRequestVariables != null) {
      variables.putAll(localHttpRequestVariables);
    }

    if (localHttpResponseVariables != null) {
      variables.putAll(localHttpResponseVariables);
    }

    for (int a = 1; a < rawContextLines.size(); a++) {
      String rawContext = rawContextLines.get(a);
      logger.debug("raw context: " + rawContext);
      rawContext =
          StringHelper.enhanceSpacesInQuotedString(rawContext, spaceEnhancer);      
      logger.debug("raw context space fixed: " + rawContext);
      
      if (rawContext.length() < 15) {
        logger.debug("raw context should have at least 15 chars. Current " + rawContext.length());
        continue;
      }

      String rawContextPartials[] = rawContext.split("\\s+");

      if (rawContextPartials[0].contentEquals("setVar")) {
        if (rawContextPartials.length == 3) {
          // expected syntax: setVar "key" value
          // value could simple, global var or jsontpath ex to be evaluated on body response
          String rawKey = rawContextPartials[1].trim();
          //key should be quoted String
          //TODO: print or throw an error if is not a quoted string
          String key = (String)variablePlaceHolderEvaluator.convertRawStringToObject(
              StringHelper.getPayloadFromQuotedString(rawKey), variables, body);
          logger.debug("converted key: "+key);
          String spaceRestoredKey = key.replaceAll(spaceEnhancer, " ");
          logger.debug("converted key space fixed: "+spaceRestoredKey);
          
          String rawValue = rawContextPartials[2].trim();
          Object value = variablePlaceHolderEvaluator.convertRawStringToObject(
              StringHelper.getPayloadFromQuotedString(rawValue), variables, body);
          logger.debug("converted value: "+value);
          Object spaceRestoredValue = (value instanceof String)?((String)value).replaceAll(spaceEnhancer, " "):value;
          logger.debug("converted value space fixed: "+spaceRestoredValue);
          if(DataTypeHelper.isQuotedString(rawValue)) {
            String stringValue = StringHelper.convertValueToString(spaceRestoredValue);
            logger.debug("value come from double quoted, so it should be a string");
            globalVariables.put(spaceRestoredKey, stringValue);
          }else {
            globalVariables.put(spaceRestoredKey, spaceRestoredValue);
          }
          
        } else if (rawContextPartials.length == 4) {

          String rawKey = rawContextPartials[1].trim();
          String key = (String)variablePlaceHolderEvaluator.convertRawStringToObject(
              StringHelper.getPayloadFromQuotedString(rawKey), variables, body);
          logger.debug("converted key: "+key);
          String jsonpathExpression = rawContextPartials[2].trim();
          logger.debug("jsonpathExpression: "+jsonpathExpression);
          String keyToSearch = StringHelper.getKeyFromVariableSyntax(rawContextPartials[3].trim());
          String jsonValueFromGlobals = (String) variables.get(keyToSearch);
          logger.debug("jsonValueFromGlobals: "+jsonValueFromGlobals);
          Object value = JsonPath.parse(jsonValueFromGlobals).read(jsonpathExpression);          
          globalVariables.put(StringHelper.getPayloadFromQuotedString(key), value);
          logger.debug("converted value: "+value);

        } else {
          logger.debug("setVar not supported all these arguments: " + rawContext);
          logger.debug(
              "Expected 1 : setVar \"key\" $-foo ${some_json}   Expected 2 : setVar \\\"key\\\" ${var}");
        }

      } else {
        logger.debug("not supported method in context: " + rawContextPartials[0]);
      }

    }

  }
}
