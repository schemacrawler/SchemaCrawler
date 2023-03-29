package us.fatehi.test.utility;

import java.nio.file.AccessMode;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import com.fasterxml.jackson.databind.ObjectMapper;

public class TestObjectUtility {

  public static Map<String, Object> fakeObjectMapFor(final Class<?> clazz) {
    final Map<String, Object> fakeObjectMap = new HashMap<>();
    fakeObjectMap.put("@object", clazz.getName());
    return fakeObjectMap;
  }

  public static TestObject makeTestObject() {
    final TestObject testObject1 = new TestObject();
    testObject1.setPlainString("hello world");
    testObject1.setPrimitiveInt(99);
    testObject1.setPrimitiveDouble(99.99);
    testObject1.setPrimitiveBoolean(true);
    testObject1.setPrimitiveArray(new int[] {1, 1, 2, 3, 5, 8});
    testObject1.setPrimitiveEnum(AccessMode.READ);
    testObject1.setObjectArray(new String[] {"a", "b", "c"});
    testObject1.setIntegerList(Arrays.asList(1, 1, 2, 3, 5, 8));
    final HashMap<Integer, String> map = new HashMap<>();
    map.put(1, "a");
    map.put(2, "b");
    map.put(3, "c");
    testObject1.setMap(map);
    final TestObject testObject = testObject1;
    return testObject;
  }

  public static Map<String, Object> makeTestObjectMap() {

    final TestObject testObject = makeTestObject();

    final ObjectMapper objectMapper = new ObjectMapper();

    final Map<String, Object> testObjectMap =
        new TreeMap<>(objectMapper.convertValue(testObject, Map.class));
    testObjectMap.put("@object", testObject.getClass().getName());

    return testObjectMap;
  }

  private TestObjectUtility() {
    // Prevent instantiation
  }
}
