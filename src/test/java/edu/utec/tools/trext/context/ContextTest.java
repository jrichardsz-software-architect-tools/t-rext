package edu.utec.tools.trext.context;

import static org.junit.Assert.assertEquals;
import java.util.HashMap;
import java.util.List;
import org.junit.Test;
import edu.utec.test.common.TestHelper;
import edu.utec.tools.trext.common.LoggerHelper;

public class ContextTest {

  @Test
  public void setVarV1ShouldPutSimpleValuesInGlobal() throws Exception {

    LoggerHelper.setDebugLevel();

    String setVarRawLines = TestHelper.getTestFileAsString(this, "set_var_v1_static_values.txt");
    List<String> contextLines = TestHelper.convertMultiLineStringToList(setVarRawLines);
    HashMap<String, Object> globalVariables = new HashMap<String, Object>();
    HashMap<String, Object> responseVariables = new HashMap<String, Object>();
    responseVariables.put("res:body", "{}");
    Context context = new Context();

    context.evaluate(contextLines, globalVariables, null, responseVariables);
    assertEquals(100, globalVariables.get("personId"));
    assertEquals("Richard", globalVariables.get("personName"));
    assertEquals(true, globalVariables.get("isGenius"));
    assertEquals(false, globalVariables.get("isBadGamer"));
    assertEquals(99.9, globalVariables.get("proximityToGod"));
    assertEquals(123456789012345l, globalVariables.get("dateOnMillis"));
    assertEquals("The Richard", globalVariables.get("nickname"));
    assertEquals("baz", globalVariables.get("foo bar"));
  }

  @Test
  public void setVarV1ShouldPutVariablesInGlobal() throws Exception {

    LoggerHelper.setDebugLevel();

    String setVarRawLines = TestHelper.getTestFileAsString(this, "set_var_v1_variable_values.txt");
    List<String> contextLines = TestHelper.convertMultiLineStringToList(setVarRawLines);
    HashMap<String, Object> globalVariables = new HashMap<String, Object>();
    globalVariables.put("personId", 100);
    globalVariables.put("personName", "Richard");
    globalVariables.put("isGenius", true);
    globalVariables.put("isBadGamer", false);
    globalVariables.put("proximityToGod", 99.9);
    globalVariables.put("dateOnMillis", 123456789012345l);
    globalVariables.put("nickname", "The Richard");
    HashMap<String, Object> responseVariables = new HashMap<String, Object>();
    responseVariables.put("res:body", "{}");
    Context context = new Context();

    context.evaluate(contextLines, globalVariables, null, responseVariables);
    assertEquals(100, globalVariables.get("_personId"));
    assertEquals("Richard", globalVariables.get("_personName"));
    assertEquals(true, globalVariables.get("_isGenius"));
    assertEquals(false, globalVariables.get("_isBadGamer"));
    assertEquals(99.9, globalVariables.get("_proximityToGod"));
    assertEquals(123456789012345l, globalVariables.get("_dateOnMillis"));
    assertEquals("The Richard", globalVariables.get("_nickname"));
    // value syntax should not have empty spaces

  }

  @Test
  public void setVarV1ShouldPutJsonPathInGlobal() throws Exception {

    String json = TestHelper.getTestFileAsString(this, "set_var_v1_json.txt");

    LoggerHelper.setDebugLevel();

    String setVarRawLines = TestHelper.getTestFileAsString(this, "set_var_v1_jsonpath_values.txt");
    List<String> contextLines = TestHelper.convertMultiLineStringToList(setVarRawLines);
    HashMap<String, Object> globalVariables = new HashMap<String, Object>();
    HashMap<String, Object> responseVariables = new HashMap<String, Object>();
    responseVariables.put("res:body", json);

    Context context = new Context();
    context.evaluate(contextLines, globalVariables, null, responseVariables);

    assertEquals(100, globalVariables.get("personId"));
    assertEquals("Richard", globalVariables.get("personName"));
    assertEquals(true, globalVariables.get("isGenius"));
    assertEquals(false, globalVariables.get("isBadGamer"));
    // assertEquals(99.9, globalVariables.get("proximityToGod"));
    // assertEquals(123456789012345l, globalVariables.get("dateOnMillis"));
    assertEquals("The Richard", globalVariables.get("nickname"));

  }
  
  @Test
  public void setVarV2ShouldGetValueFromJson() throws Exception {

    String json = TestHelper.getTestFileAsString(this, "set_var_v1_json.txt");

    LoggerHelper.setDebugLevel();

    String setVarRawLines = TestHelper.getTestFileAsString(this, "set_var_v2_simple_values.txt");
    List<String> contextLines = TestHelper.convertMultiLineStringToList(setVarRawLines);
    HashMap<String, Object> globalVariables = new HashMap<String, Object>();
    HashMap<String, Object> requestVariables = new HashMap<String, Object>();
    requestVariables.put("req:body", json);
    HashMap<String, Object> responseVariables = new HashMap<String, Object>();
    responseVariables.put("res:body", "{}");

    Context context = new Context();
    context.evaluate(contextLines, globalVariables, requestVariables, responseVariables);

    assertEquals(100, globalVariables.get("userId"));
    assertEquals("Richard", globalVariables.get("firstName"));
    assertEquals(true, globalVariables.get("isSmart"));
    assertEquals(false, globalVariables.get("isBadPlayer"));
    assertEquals("The Richard", globalVariables.get("label"));

  }  


  //// @Test
  //// public void methodEnhancerForComplexVariables() throws Exception {
  //// LoggerHelper.setDebugLevel();
  ////
  //// MethodEnhancer methodEnhancer = new MethodEnhancer();
  ////
  //// HashMap<String, Object> variables = new HashMap<String, Object>();
  //// variables.put("json", "{\"message\":\"hello\",\"id\":\"200\"}");
  ////
  //// String enhancedMethod = methodEnhancer.rawStringToOneMethodWithSeveralArguments(
  //// "setVarFromJson \"message\" $.message ${json}", variables, null);
  ////
  //// assertEquals("setVarFromJson(\"message\",$.message,\"{\"message\":\"hello\",\"id\":\"200\"}\"",
  //// enhancedMethod);
  ////
  ////
  //// }

  // TODO:
  // @Test
  // public void methodEnhancerIncongruentTypes() throws Exception {
  // LoggerHelper.setDebugLevel();
  //
  // MethodEnhancer methodEnhancer = new MethodEnhancer();
  //
  // String equals =
  // methodEnhancer.rawStringToOneMethodWithSeveralArguments("setVar \"id\" 123", null, null);
  // assertEquals("setVar(\"id\",123)", equals);
  //
  // equals = methodEnhancer.rawStringToOneMethodWithSeveralArguments("setVar \"id\" \"123\"", null,
  // null);
  // assertEquals("setVar(\"id\",\"123\")", equals);
  //
  // String json = "{\"message\":\"hello\",\"id\":\"200\"}";
  //
  // equals =
  // methodEnhancer.rawStringToOneMethodWithSeveralArguments("setVar \"id\" $.id", null, json);
  // assertEquals("setVar(\"id\",\"200\")", equals);
  //
  // equals = methodEnhancer.rawStringToOneMethodWithSeveralArguments("setVar \"string\" hola",
  // null,
  // json);
  // assertEquals("setVar(\"string\",\"hola\")", equals);
  //
  // }
  //
}
