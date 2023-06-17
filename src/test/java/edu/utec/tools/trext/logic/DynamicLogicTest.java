package edu.utec.tools.trext.logic;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import org.junit.Test;
import org.junit.runner.RunWith;
import edu.utec.test.common.TestHelper;
import edu.utec.test.junit.DefaultOrderedRunner;
import edu.utec.test.junit.ExplicitOrder;
import edu.utec.tools.trext.common.FileHelper;
import edu.utec.tools.trext.common.LoggerHelper;

@RunWith(DefaultOrderedRunner.class)
@ExplicitOrder({"perform"})
public class DynamicLogicTest {

  @Test
  public void shouldWorkWithStaticValues() throws Exception {

    LoggerHelper.setDebugLevel();

    File file = TestHelper.getFile("edu/utec/tools/trext/logic/asserts_static.txt");

    ArrayList<String> rawAsserts = FileHelper.getFileAsLines(file);

    HashMap<String, Object> variables = new HashMap<String, Object>();
    HashMap<String, Object> responseVariables = new HashMap<String, Object>();

    DynamicLogic dynamicLogic = new DynamicLogic();
    dynamicLogic.perform(rawAsserts, variables, null, responseVariables);
    //asserts are not required due to if this line is reached, it means raw asserts was successful converted
    // and no error were thrown in its execution 
  }
  
  @Test
  public void shouldWorkWithVariables() throws Exception {

    LoggerHelper.setDebugLevel();

    File file = TestHelper.getFile("edu/utec/tools/trext/logic/asserts_variables.txt");

    ArrayList<String> rawAsserts = FileHelper.getFileAsLines(file);

    HashMap<String, Object> variables = new HashMap<String, Object>();
    variables.put("lastNameFromCsv", "doe");
    variables.put("ageFromCsv", "26");
    variables.put("isAdmin", "false");

    HashMap<String, Object> responseVariables = new HashMap<String, Object>();

    DynamicLogic dynamicLogic = new DynamicLogic();
    dynamicLogic.perform(rawAsserts, variables, null, responseVariables);
  }

  @Test
  public void shouldWorkWithJsonPath() throws Exception {

    LoggerHelper.setDebugLevel();

    String json = TestHelper.getFileAsString("edu/utec/tools/trext/logic/jsonSample.txt");

    File file = TestHelper.getFile("edu/utec/tools/trext/logic/asserts_jsonpath.txt");

    ArrayList<String> rawAsserts = FileHelper.getFileAsLines(file);

    HashMap<String, Object> variables = new HashMap<String, Object>();
    variables.put("lastNameFromCsv", "doe");
    variables.put("ageFromCsv", "26");

    HashMap<String, Object> responseVariables = new HashMap<String, Object>();
    responseVariables.put("res:body", json);

    DynamicLogic dynamicLogic = new DynamicLogic();
    dynamicLogic.perform(rawAsserts, variables, null, responseVariables);
  }
}
