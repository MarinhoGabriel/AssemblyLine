package br.com.marinho.assemblyline.utils;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Class used to deal with the file lines, converting them into values that are going to be used in a map to be able
 * to access the values.
 */
public class FileLineUtils {

    /** The value, in minutes, that the maintenance takes to finish. */
    private static final int MAINTENANCE_TIME_VALUE = 5;

    /** String representing the maintenance line indicator. */
    private static final String MAINTENANCE_STRING = "- maintenance";

    /** Suffix "min" in the end of a line to indicate the minutes of the production step. */
    private static final String MINUTES_SUFFIX = "min";

    /**
     * The method receives a text file with assembly lines and builds a map with the duration time of the activities
     * mapped by their names.
     *
     * @param inputFile File with the assembly lines.
     * @return A {@link HashMap} with the duration time of the activities mapped by their names.
     */
    public static Map<String, Integer> getAssemblyMap(File inputFile) {
        Map<String, Integer> map = new HashMap<>();

        try {
            BufferedReader reader = new BufferedReader(new FileReader(inputFile));
            String line;

            while ((line = reader.readLine()) != null) {
                convertLine(line, map);
            }

            reader.close();
        } catch (FileNotFoundException e) {
            System.err.println("The file " + inputFile.getName() + " could not be found. Finishing.");
            System.exit(0);
        } catch (IOException e) {
            System.err.println(
                    "There was a problem while trying to read the file " + inputFile.getName() + ". Finishing.");
            System.exit(1);
        }

        return map;
    }

    /**
     * The method checks if the line, coming from the file, contains the maintenance string to indicate that the
     * production step lasts 5min.
     * To avoid problem with space and case, the method removes all whitespaces from the line and makes it uppercase,
     * as so in {@link FileLineUtils#MAINTENANCE_STRING}.
     *
     * @param line File line to check.
     * @return {@code true} if the line has the maintenance string, {@code false} in opposite.
     */
    private static boolean isMaintenanceLine(String line) {
        String treatedLine = line.replaceAll("\\s+", "").toUpperCase();
        String treatedMaintenanceString = MAINTENANCE_STRING.replaceAll("\\s+", "").toUpperCase();

        return treatedLine.endsWith(treatedMaintenanceString);
    }

    /**
     * If the line has the maintenance string, it's necessary to get it. As seen in
     * {@link FileLineUtils#isMaintenanceLine(String)}, the checking is made without whitespaces. Then, here, it's
     * necessary to get the right strings by considering the amount of spaces in the string.
     * The method logic is quite simple: get the line substring, starting from the beginning and going until the size
     * of the line without the maintenance, represented as {@code treatedLine.length() - treatedMaintenanceString
     * .length()}, considering the whitespaces (the -2 in the end refers to the "-" separator between the production
     * step name and the "maintenance" string.
     *
     * @param line File line.
     * @return The production step name.
     */
    private static String getMaintenanceString(String line) {
        String treatedLine = line.replaceAll("\\s+", "").toUpperCase();
        String treatedMaintenanceString = MAINTENANCE_STRING.replaceAll("\\s+", "").toUpperCase();
        int totalWhiteSpace = Math.max((int) line.chars().filter(Character::isWhitespace).count(), -2);

        return line.substring(0, treatedLine.length() - treatedMaintenanceString.length() + (totalWhiteSpace == 0 ? 0 :
                                                                                             totalWhiteSpace - 2));
    }

    /**
     * Method responsible for converting a line, that comes from the input file, to add it on the map represented by
     * {@code map}.
     *
     * @param line Line from the input file.
     * @param map  Map used to store the values.
     */
    private static void convertLine(String line, Map<String, Integer> map) {
        if (isMaintenanceLine(line)) {
            map.put(getMaintenanceString(line), MAINTENANCE_TIME_VALUE);
        } else {
            Pattern pattern = Pattern.compile("\\d+");
            Matcher matcher = pattern.matcher(line);

            if (matcher.find()) {
                String group = matcher.group();
                String productionStepName = line.substring(0, line.lastIndexOf(group));

                if (line.indexOf(group) + group.length() + MINUTES_SUFFIX.length() != line.length()) {
                    System.err.println("There is a number in the production step title. Finishing.");
                    System.exit(1);
                }

                while (productionStepName.endsWith(" ") || productionStepName.endsWith("-")) {
                    productionStepName = productionStepName.substring(0, productionStepName.length() - 1);
                }

                map.put(productionStepName, Integer.parseInt(group));
            } else {
                System.err.println("There is no time in the line and it's not possible to determine it. Finishing.");
                System.exit(1);
            }
        }
    }
}