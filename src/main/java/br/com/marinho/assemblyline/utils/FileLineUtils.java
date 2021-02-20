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
            String line = reader.readLine();

            while (line != null) {
                convertLine(line, map);
                line = reader.readLine();
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
     * Method responsible for converting a line, that comes from the input file, to add it on the map represented by
     * {@code map}.
     *
     * @param line Line from the input file.
     * @param map  Map used to store the values.
     */
    private static void convertLine(String line, Map<String, Integer> map) {
        if (line.endsWith(MAINTENANCE_STRING)) {
            map.put(line.substring(0, line.indexOf(MAINTENANCE_STRING) - 2), MAINTENANCE_TIME_VALUE);
        } else {
            Pattern pattern = Pattern.compile("\\d+");
            Matcher matcher = pattern.matcher(line);

            if (matcher.find()) {
                map.put(line.substring(0, line.lastIndexOf(" ")), Integer.parseInt(matcher.group()));
            }
        }
    }
}