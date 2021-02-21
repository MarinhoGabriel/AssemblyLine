package br.com.marinho.assemblyline.utils;

import java.util.Map;

/**
 * Class responsible for receiving a map with all activities and organize them into assembly lines.
 */
public class Organizer {

    /** Value representing the starting time of an assembly line (9:00). */
    private static final double INITIAL_TIME = 9.00;

    /** Value used to convert minute values into hour. */
    private static final double HOUR_MULTIPLIER = 60.;

    /** Value representing the starting time of an assembly line (9:00). */
    private static final double AFTER_LUNCH_TIME = 13.;

    /** Decimal separator. */
    private static final String POINT = ".";

    /** A blank space used in line building. */
    private static final String WHITESPACE = " ";

    /** Line break regex to jump a line at the end of a string. */
    private static final String LINE_BREAK = "\n";

    /** Character ":", used in time separation. */
    private static final String DOUBLE_COLON = ":";

    /** Suffix used in the end of a line together with the activity time. */
    private static final String MINUTE_SUFFIX = "min";

    /** Time used if the activities end before 16h. */
    private static final String GYM_DEFAULT_TIME = "16:00";

    /** Line representing the lunch time of an assembly line. */
    private static final String LUNCH_TIME = "12:00 Almoço\n";

    /** Line reprsenting the gym time of an assembly line. */
    private static final String GYM_TIME = " Ginástica laboral\n";

    /** Title that comes before the assembly line activities. */
    private static final String ASSEMBLY_LINE_TITLE = "Linha de montagem %d:\n";

    /**
     * If the value, represented as {@code value} is lesser than 10, we need to add a 0 before it so the time can be in
     * 0X:XX format.
     *
     * @param value Value used in the filling process.
     * @return String containing the filled, if necessary, value.
     */
    private static String fillMissingPlace(double value) {
        return (value < 10) ? "0" + (int) value : String.valueOf((int) value);
    }

    /**
     * Method responsible for append strings in a String builder.
     * Two interesting points about this method are (1) it's creation to avoid a lot of
     * {@code line.append().append()...} in the code and (2) the original for in it. The {@code for (item : array}
     * isn't too good for performance because it needs to instantiate a new item at each iteration.
     *
     * @param targetSting     String builder that is receiving the values.
     * @param stringsToAppend Strings to be appended.
     */
    private static void appendStrings(StringBuilder targetSting, String... stringsToAppend) {
        for (int i = 0; i < stringsToAppend.length; i++) {
            targetSting.append(stringsToAppend[i]);
        }
    }

    /**
     * The organization method builds the final string using the map coming from outside.
     * There are some checks to do like (1) checking if it's lunch time by checking if the current activity would end
     * after noon, (2) checking the gym time, also checking if the current activity would end after 17h and (3)
     * checking, in the end, if the gym activity is already in the string. It is possible to the last activity ends
     * before 16h and, if so, we need to add the gym in the end, starting t 16h.
     *
     * @param activities Activities coming from the file.
     * @return Organized assembly lines.
     */
    public static String organize(Map<String, Integer> activities) {
        StringBuilder line = new StringBuilder();
        double time = INITIAL_TIME;
        boolean hadLunch = false;
        double decimalTime = (time - (int) time) * HOUR_MULTIPLIER;
        int assemblyLine = 1;

        line.append(String.format(ASSEMBLY_LINE_TITLE, assemblyLine++));

        for (Map.Entry<String, Integer> entry : activities.entrySet()) {
            if (!hadLunch && time + (entry.getValue() / HOUR_MULTIPLIER) >= 12.) {
                line.append(LUNCH_TIME);
                hadLunch = true;
                time = AFTER_LUNCH_TIME;
                decimalTime = (time - (int) time) * HOUR_MULTIPLIER;
            }

            if (time + (entry.getValue() / HOUR_MULTIPLIER) >= 17.) {
                String minutes = fillMissingPlace(decimalTime).replace(POINT, "").substring(0, 2);
                appendStrings(line, fillMissingPlace(time), DOUBLE_COLON, minutes, GYM_TIME, LINE_BREAK);
                time = INITIAL_TIME;
                hadLunch = false;
                line.append(String.format(ASSEMBLY_LINE_TITLE, assemblyLine++));
                continue;
            }

            String minutes = fillMissingPlace(decimalTime).replace(POINT, "").substring(0, 2);
            appendStrings(line, fillMissingPlace((int) time), DOUBLE_COLON, minutes, WHITESPACE, entry.getKey(),
                    WHITESPACE);
            if (entry.getValue() != 5) {
                line.append(entry.getValue()).append(MINUTE_SUFFIX);
            }
            line.append(LINE_BREAK);
            time += entry.getValue() / HOUR_MULTIPLIER;
            decimalTime = (time - (int) time) * HOUR_MULTIPLIER;
        }

        if (!line.toString().endsWith(GYM_TIME)) {
            if (time < 16.) {
                line.append(GYM_DEFAULT_TIME).append(GYM_TIME);
            } else {
                String minutes = fillMissingPlace(decimalTime).replace(POINT, "").substring(0, 2);
                appendStrings(line, fillMissingPlace((int) time), DOUBLE_COLON, minutes, GYM_TIME);
            }
        }

        return line.toString();
    }
}