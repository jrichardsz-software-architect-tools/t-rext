package edu.utec.tools.trext.common;

import java.util.Random;
import java.util.UUID;
import org.apache.commons.lang3.RandomStringUtils;
import com.github.javafaker.Faker;

public class VariablePlaceHolderHelper {

  public static String getStringRepresentation(Object value) throws Exception {
    if (value instanceof String) {
      return (String) value;
    } else if (value instanceof Integer) {
      return "" + ((Integer) value).intValue();
    } else if (value instanceof Double) {
      return "" + ((Double) value).doubleValue();
    } else {
      throw new Exception("value class is not supported:" + value.getClass());
    }
  }

  public static boolean containsJockers(String key) {
    return key.contentEquals("rand:uuid") || key.contentEquals("rand:int")
        || key.contentEquals("rand:double") || key.contentEquals("rand:firstName")
        || key.contentEquals("rand:lastName")|| key.contentEquals("rand:letters");
  }

  public static String parseJocker(String key) throws Exception {

    Faker faker = new Faker();

    try {
      if (key.contentEquals("rand:uuid")) {
        return UUID.randomUUID().toString();
      } else if (key.contentEquals("rand:int")) {
        return "" + Math.abs(getRandomIntegerInRange(1000, 10000));
      } else if (key.contentEquals("rand:double")) {
        return "" + Math.abs(getRandomDoubleInRange(1000, 10000));
      } else if (key.contentEquals("rand:firstName")) {
        return faker.name().firstName();
      } else if (key.contentEquals("rand:lastName")) {
        return faker.name().lastName();
      } else if (key.contentEquals("rand:letters")) {
        return RandomStringUtils.randomAlphabetic(5);
      } else {
        throw new Exception(String.format("placeholder is not supported: ${%s}", key));
      }
    } catch (Exception e) {
      throw new Exception(String.format("Failed when placeholder is being evaluated: ${%s}", key),
          e);
    }
  }

  private static int getRandomIntegerInRange(int min, int max) {

    if (min >= max) {
      throw new IllegalArgumentException("max must be greater than min");
    }

    Random r = new Random();
    return r.nextInt((max - min) + 1) + min;
  }

  private static double getRandomDoubleInRange(int min, int max) {

    if (min >= max) {
      throw new IllegalArgumentException("max must be greater than min");
    }

    Random r = new Random();
    return min + (max - min) * r.nextDouble();
  }
}
