package edu.utec.tools.trext.method;

import java.util.HashMap;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import edu.utec.tools.trext.common.DataTypeHelper;
import edu.utec.tools.trext.common.MethodEnhancers;
import edu.utec.tools.trext.common.StringHelper;
import edu.utec.tools.trext.variables.VariablePlaceHolderEvaluator;

public class MethodEnhancer {

  private final Logger logger = LogManager.getLogger(this.getClass());
  private final String spaceEnhancer = "@space";
  private VariablePlaceHolderEvaluator variablePlaceHolderEvaluator = new VariablePlaceHolderEvaluator();

  public String rawStringToConsecutiveMethodsWithSingleArgument(String rawMethodArgumentLine,
      HashMap<String, Object> variables, String httpBodyRawString) throws Exception {

    logger.debug("convert raw string into consecutive methods with single argument");

    logger.debug("Line to evaluate: " + rawMethodArgumentLine);
    String rawMethodArgumentLineSpacesFixed =
        StringHelper.enhanceSpacesInQuotedString(rawMethodArgumentLine, spaceEnhancer);
    logger.debug("Line fixed to evaluate: " + rawMethodArgumentLineSpacesFixed);
    String[] rawMethodArgumentLinePartials = rawMethodArgumentLineSpacesFixed.split("\\s+");
    String finalAssertLine = "";
    String singleArgumentTemplate = "(%s)";

    for (int a = 1; a < rawMethodArgumentLinePartials.length; a++) {
      String rawArgumentOrMethod = rawMethodArgumentLinePartials[a].trim();
      logger.debug("Partial to evaluate: " + rawArgumentOrMethod);
      if (isMethod(rawArgumentOrMethod)) {
        logger.debug("Partial is a method");
        String evaluatedPartial = MethodEnhancers.getProperty(rawArgumentOrMethod);
        logger.debug("Partial evaluated: " + evaluatedPartial);
        finalAssertLine += evaluatedPartial;
      } else if (DataTypeHelper.isQuotedString(rawArgumentOrMethod)) {
        logger.debug("Partial is quoted string");
        String rawArgumentPayload = StringHelper.getPayloadFromQuotedString(rawArgumentOrMethod);
        String readyArgument =
            variablePlaceHolderEvaluator.convertVariableToArgument(rawArgumentPayload, variables, httpBodyRawString, true);
        // add the initial quotes
        String evaluatedPartial = String.format(singleArgumentTemplate, readyArgument);
        logger.debug("Partial evaluated: " + evaluatedPartial);
        finalAssertLine += evaluatedPartial;
      } else {
        logger.debug("Partial is not a method, nor a quoted string. "+"It could be an unquoted string a number, boolean, global var or jsonpath");
        String readyArgument =
            variablePlaceHolderEvaluator.convertVariableToArgument(rawArgumentOrMethod, variables, httpBodyRawString, false);
        String evaluatedPartial = String.format(singleArgumentTemplate, readyArgument);
        logger.debug("Partial evaluated: " + evaluatedPartial);
        finalAssertLine += evaluatedPartial;
      }
      
      logger.debug("improvement progress: "+finalAssertLine);
    }

    // if line contained a quoted string with spaces : "Hello World"
    // it was converted to "Hello@spaceWorld"
    // so, at the end, we need to revert it
    finalAssertLine = finalAssertLine.replaceAll(spaceEnhancer, " ");
    logger.debug("Final assert line: " + finalAssertLine);
    return rawMethodArgumentLinePartials[0].trim() + finalAssertLine;
  }

  public String rawStringToOneMethodWithSeveralArguments(String rawMethodArgumentLine,
      HashMap<String, Object> variables, String httpBodyRawString) throws Exception {

    logger.debug("convert raw string into one method with several variables");
    logger.debug("Line to evaluate: " + rawMethodArgumentLine);
    // TODO: ensure not spaces in the first argument or variable name
    String[] rawMethodArgumentLinePartials = rawMethodArgumentLine.split("\\s+");
    String args = "";
    String methodName = null;
    String methodTemplate = "%s(%s)";
    for (int a = 0; a < rawMethodArgumentLinePartials.length; a++) {
      String rawArgumentOrMethod = rawMethodArgumentLinePartials[a].trim();
      logger.debug("Partial to evaluate: " + rawArgumentOrMethod);
      if (a == 0 && isMethod(rawArgumentOrMethod)) {
        methodName = MethodEnhancers.getProperty(rawArgumentOrMethod).trim();
        logger.debug("Partial is method");
        logger.debug("Partial evaluated: " + methodName);
      } else if (DataTypeHelper.isQuotedString(rawArgumentOrMethod)) {
        logger.debug("Partial is quoted string");
        String rawArgumentPayload = StringHelper.getPayloadFromQuotedString(rawArgumentOrMethod);
        String evaluatedPartial =
            variablePlaceHolderEvaluator.convertVariableToArgument(rawArgumentPayload, variables, httpBodyRawString, true);
        logger.debug("Partial evaluated: " + evaluatedPartial);
        args += evaluatedPartial;
      } else {
        logger.debug("Partial is not a method, nor a quoted string. "+"It could be an unquoted string a number, boolean, global var or jsonpath");
        String evaluatedPartial =
            variablePlaceHolderEvaluator.convertVariableToArgument(rawArgumentOrMethod, variables, httpBodyRawString, false);
        logger.debug("Partial evaluated: " + evaluatedPartial);
        args += evaluatedPartial;
      }

      if (a > 0 && a < rawMethodArgumentLinePartials.length - 1) {
        args += ",";
      }
      
      logger.debug("improvement progress- method "+methodName+" args: "+args);
    }

    String enhancedLine = String.format(methodTemplate, methodName, args);
    logger.debug("Final enhanced line: " + enhancedLine);
    return enhancedLine;
  }

  private boolean isMethod(String raw) throws Exception {
    try {
      return MethodEnhancers.getProperty(raw) != null;
    } catch (Exception e) {
      return false;
    }
  }

}
