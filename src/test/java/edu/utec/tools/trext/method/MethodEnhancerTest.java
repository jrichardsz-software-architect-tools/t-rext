package edu.utec.tools.trext.method;

import static org.junit.Assert.assertEquals;
import java.util.HashMap;
import org.junit.Test;
import edu.utec.test.common.TestHelper;
import edu.utec.tools.trext.common.LoggerHelper;

public class MethodEnhancerTest {

  @Test
  public void methodEnhancerSimpleValues() throws Exception {
    MethodEnhancer methodEnhancer = new MethodEnhancer();
    String equals1 = methodEnhancer.rawStringToConsecutiveMethodsWithSingleArgument(
        "assertThat 123 isEqualTo 123", null, null);
    assertEquals("assertThat(123).isEqualTo(123)", equals1);

    equals1 = methodEnhancer.rawStringToConsecutiveMethodsWithSingleArgument(
        "assertThat \"abc\" isNotNull isNotEmpty", null, null);
    assertEquals("assertThat(\"abc\").isNotNull().isNotEmpty()", equals1);

    equals1 = methodEnhancer
        .rawStringToConsecutiveMethodsWithSingleArgument("assertThat true isTrue", null, null);
    assertEquals("assertThat(true).isTrue()", equals1);
  }

  @Test
  public void methodEnhancerStringWithSpaces() throws Exception {
    MethodEnhancer methodEnhancer = new MethodEnhancer();

    assertEquals("assertThat(123).isEqualTo(\"1 2 3\")",
        methodEnhancer.rawStringToConsecutiveMethodsWithSingleArgument(
            "assertThat 123 isEqualTo \"1 2 3\"", null, null));

    assertEquals("assertThat(true).isEqualTo(\"true false   true\")",
        methodEnhancer.rawStringToConsecutiveMethodsWithSingleArgument(
            "assertThat true isEqualTo \"true false   true\"", null, null));
  }

  @Test
  public void methodEnhancerWithJsonPath() throws Exception {
    MethodEnhancer methodEnhancer = new MethodEnhancer();

    String json =
        TestHelper.getFileAsString("edu/utec/tools/trext/method/equalsEnhancerWithJsonPath.txt");

    assertEquals("assertThat(\"John\").isEqualTo(\"John\")",
        methodEnhancer.rawStringToConsecutiveMethodsWithSingleArgument(
            "assertThat \"John\" isEqualTo $.firstName", null, json));

    assertEquals("assertThat(26).isEqualTo(26)",
        methodEnhancer.rawStringToConsecutiveMethodsWithSingleArgument(
            "assertThat $.age isEqualTo 26 ", null, json));

    assertEquals("assertThat(\"iPhone\").isEqualTo(\"iPhone\")",
        methodEnhancer.rawStringToConsecutiveMethodsWithSingleArgument(
            "assertThat $.phoneNumbers[0].type isEqualTo \"iPhone\"", null, json));
  }

  @Test
  public void methodEnhancerWithVariables() throws Exception {
    LoggerHelper.setDebugLevel();
    MethodEnhancer methodEnhancer = new MethodEnhancer();

    HashMap<String, Object> variables = new HashMap<String, Object>();
    variables.put("firstName", "Jane");
    variables.put("age", "26");
    variables.put("isAdmin", "false");

    assertEquals("assertThat(\"Jane\").isEqualTo(\"Jane\")",
        methodEnhancer.rawStringToConsecutiveMethodsWithSingleArgument(
            "assertThat \"Jane\" isEqualTo \"${firstName}\"", variables, null));

    assertEquals("assertThat(26).isEqualTo(26)",
        methodEnhancer.rawStringToConsecutiveMethodsWithSingleArgument(
            "assertThat ${age} isEqualTo 26 ", variables, null));

    assertEquals("assertThat(false).isEqualTo(false)",
        methodEnhancer.rawStringToConsecutiveMethodsWithSingleArgument(
            "assertThat ${isAdmin} isEqualTo false ", variables, null));
  }

//  @Test
//  public void methodEnhancer02SimpleValues() throws Exception {
//    MethodEnhancer methodEnhancer = new MethodEnhancer();
//    String equals1 =
//        methodEnhancer.rawStringToOneMethodWithSeveralArguments("setVar 123 123", null, null);
//    assertEquals("setVar(123,123)", equals1);
//
//    equals1 = methodEnhancer.rawStringToOneMethodWithSeveralArguments("setVar \"abc\" \"abc\"",
//        null, null);
//    assertEquals("setVar(\"abc\",\"abc\")", equals1);
//
//    equals1 =
//        methodEnhancer.rawStringToOneMethodWithSeveralArguments("setVar true false", null, null);
//    assertEquals("setVar(true,false)", equals1);
//  }

//  @Test
//  public void methodEnhancer02WithJsonPath() throws Exception {
//    MethodEnhancer methodEnhancer = new MethodEnhancer();
//
//    String json =
//        TestHelper.getFileAsString("edu/utec/tools/trext/method/equalsEnhancerWithJsonPath.txt");
//
//    assertEquals("setVar(\"Jane\",\"John\")", methodEnhancer
//        .rawStringToOneMethodWithSeveralArguments("setVar \"Jane\" $.firstName", null, json));
//
//    assertEquals("setVar(\"age\",26)", methodEnhancer
//        .rawStringToOneMethodWithSeveralArguments("setVar \"age\" $.age", null, json));
//
//    assertEquals("setVar(\"type\",\"iPhone\")",
//        methodEnhancer.rawStringToOneMethodWithSeveralArguments(
//            "setVar \"type\" $.phoneNumbers[0].type", null, json));
//  }

//  @Test
//  public void methodEnhancer02WithVariables() throws Exception {
//    
//    LoggerHelper.setDebugLevel();
//    MethodEnhancer methodEnhancer = new MethodEnhancer();
//
//    HashMap<String, Object> variables = new HashMap<String, Object>();
//    variables.put("firstName", "Jane");
//    variables.put("age", 26);
//    variables.put("isAdmin", false);
//
//    assertEquals("setVar(\"name\",\"Jane\")",
//        methodEnhancer.rawStringToOneMethodWithSeveralArguments("setVar \"name\" \"${firstName}\"",
//            variables, null));
//
//    assertEquals("setVar(\"name\",26)", methodEnhancer
//        .rawStringToOneMethodWithSeveralArguments("setVar \"name\" ${age}", variables, null));
//
//    assertEquals("setVar(\"canDelete\",false)",
//        methodEnhancer.rawStringToOneMethodWithSeveralArguments("setVar \"canDelete\" ${isAdmin}",
//            variables, null));
//  }

//  @Test
//  public void methodEnhancerIncongruentTypes() throws Exception {
//    LoggerHelper.setDebugLevel();
//
//    MethodEnhancer methodEnhancer = new MethodEnhancer();
//
//    String equals =
//        methodEnhancer.rawStringToOneMethodWithSeveralArguments("setVar \"id\" 123", null, null);
//    assertEquals("setVar(\"id\",123)", equals);
//
//    equals = methodEnhancer.rawStringToOneMethodWithSeveralArguments("setVar \"id\" \"123\"", null,
//        null);
//    assertEquals("setVar(\"id\",\"123\")", equals);
//
//    String json = "{\"message\":\"hello\",\"id\":\"200\"}";
//
//    equals =
//        methodEnhancer.rawStringToOneMethodWithSeveralArguments("setVar \"id\" $.id", null, json);
//    assertEquals("setVar(\"id\",\"200\")", equals);
//
//    equals = methodEnhancer.rawStringToOneMethodWithSeveralArguments("setVar \"string\" hola", null,
//        json);
//    assertEquals("setVar(\"string\",\"hola\")", equals);
//
//  }

  @Test
  public void rawStringToConsecutiveMethodsWithSingleArgumentLong() throws Exception {


    LoggerHelper.setDebugLevel();
    MethodEnhancer methodEnhancer = new MethodEnhancer();

    String json = TestHelper.getTestFileAsString(this,
        "rawStringToConsecutiveMethodsWithSingleArgumentLong.txt");

    assertEquals("assertThat(1598885893000).isEqualTo(1598885893000)",
        methodEnhancer.rawStringToConsecutiveMethodsWithSingleArgument(
            "assertThat $.id isEqualTo 1598885893000", null, json));

    assertEquals("assertThat(1598885893000).isEqualTo(1598885893000)",
        methodEnhancer.rawStringToConsecutiveMethodsWithSingleArgument(
            "assertThat 1598885893000 isEqualTo $.id", null, json));

    assertEquals("assertThat(1598885893000).isNotNull()", methodEnhancer
        .rawStringToConsecutiveMethodsWithSingleArgument("assertThat $.id isNotNull", null, json));
  }

//  @Test
//  public void methodEnhancerForComplexVariables() throws Exception {
//    LoggerHelper.setDebugLevel();
//
//    MethodEnhancer methodEnhancer = new MethodEnhancer();
//
//    HashMap<String, Object> variables = new HashMap<String, Object>();
//    variables.put("json", "{\"message\":\"hello\",\"id\":\"200\"}");
//
//    String enhancedMethod = methodEnhancer.rawStringToOneMethodWithSeveralArguments(
//        "setVarFromJson \"message\" $.message ${json}", variables, null);
//
//    assertEquals("setVarFromJson(\"message\",$.message,\"{\"message\":\"hello\",\"id\":\"200\"}\"",
//        enhancedMethod);
//
//
//  }
}
