package br.com.marinho.assemblyline.testUtilities;

/**
 * Class with utility methods that can be used in all test classes,
 */
public class TestUtils {

    /**
     * Each test has a test file with the same name as the method. The belo method gets the method name from the
     * stacktrace to get the right file.
     *
     * @return The name of the executing method.
     */
    public static String getMethodName() {
        StackTraceElement[] stack = Thread.currentThread().getStackTrace();
        String methodName = stack[2].toString().substring(0, stack[2].toString().indexOf("("));

        return methodName.substring(methodName.lastIndexOf(".") + 1);
    }
}