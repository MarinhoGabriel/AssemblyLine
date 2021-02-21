package br.com.marinho.assemblyline.utils;

import br.com.marinho.assemblyline.testUtilities.TestUtils;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Test class fot the functionality of the {@link Organizer}.
 */
public class OrganizerTest {

    /** Organizer instance to make the tests. */
    private static Organizer organizer;

    /** Map with the scenarios values to run the organizer. */
    private static Map<String, Map<String, Integer>> testsScenariosMap;

    /**
     * The method initializes the {@code organizer} attribute and builds the map used in all tests, with their values
     * for each scenario.
     */
    @BeforeClass
    public static void beforeClass() {
        organizer = new Organizer();
        testsScenariosMap = new LinkedHashMap<>();

        // Building should_HaveLunchTime_When_ProductionStepsSurpassesNoon map
        Map<String, Integer> first = new LinkedHashMap<>();
        first.put("Production step I", 5);
        first.put("Production step II", 60);
        first.put("Production step III", 45);
        first.put("Production step IV", 45);
        first.put("Production step V", 60);
        first.put("Production step VI", 30);
        first.put("Production step VII", 30);
        first.put("Production step VIII", 45);
        first.put("Production step IX", 45);
        testsScenariosMap.put("should_HaveLunchTime_When_ProductionStepsSurpassesNoon", first);

        // Building should_HaveTwoAssemblyLines_When_ThereAreALotOfProductionSteps map
        Map<String, Integer> second = new LinkedHashMap<>();
        second.put("Production step I", 60);
        second.put("Production step II", 60);
        second.put("Production step III", 45);
        second.put("Production step IV", 45);
        second.put("Production step V", 30);
        second.put("Production step VI", 60);
        second.put("Production step VII", 30);
        second.put("Production step VIII", 45);
        second.put("Production step IX", 45);
        second.put("Production step X", 30);
        testsScenariosMap.put("should_HaveTwoAssemblyLines_When_ThereAreALotOfProductionSteps", second);

        // Building should_NotHaveLunchTimeAndNeitherGymTime_When_ThereAreNoAfternoonActivities map
        Map<String, Integer> third = new LinkedHashMap<>();
        third.put("Production step I", 5);
        third.put("Production step II", 60);
        third.put("Production step III", 45);
        third.put("Production step IV", 45);
        testsScenariosMap.put("should_NotHaveLunchTimeAndNeitherGymTime_When_ThereAreNoAfternoonActivities", third);

        // Building should_HaveCorrectAssemblyLines_When_SomeProductionLastsMoreThanSixtyMinutes map
        Map<String, Integer> fourth = new LinkedHashMap<>();
        fourth.put("Production step I", 120);
        fourth.put("Production step II", 60);
        fourth.put("Production step III", 45);
        fourth.put("Production step IV", 45);
        fourth.put("Production step V", 60);
        fourth.put("Production step VI", 120);
        fourth.put("Production step VII", 30);
        fourth.put("Production step VIII", 45);
        fourth.put("Production step IX", 45);
        fourth.put("Production step X", 30);
        testsScenariosMap.put("should_HaveCorrectAssemblyLines_When_SomeProductionLastsMoreThanSixtyMinutes", fourth);

        // Building should_HaveAProductionBeforeLunch_When_ItsTimeEndsOnLunch map
        Map<String, Integer> fifth = new LinkedHashMap<>();
        fifth.put("Production step I", 180);
        fifth.put("Production step II", 60);
        testsScenariosMap.put("should_HaveAProductionBeforeLunch_When_ItsTimeEndsOnLunch", fifth);

        // Building should_NotHaveAProductionLineBeforeGym_When_ItsTimeEndsOnGymTime map
        Map<String, Integer> sixth = new LinkedHashMap<>();
        sixth.put("Production step I", 180);
        sixth.put("Production step II", 120);
        sixth.put("Production step III", 120);
        testsScenariosMap.put("should_NotHaveAProductionLineBeforeGym_When_ItsTimeEndsOnGymTime", sixth);
    }

    /**
     * The below method tests if there is a lunch time line (indicated as "12:00 Almoço") when some production step
     * surpasses noon. In other words, the test tests if there is a lunch time if there is an afternoon period in the
     * assembly line.
     */
    @Test
    public void should_HaveLunchTime_When_ProductionStepsSurpassesNoon() {
        String organized = organizer.organize(testsScenariosMap.get(TestUtils.getMethodName()));
        String[] splitResult = organized.split("\\n");

        Assert.assertEquals(12, splitResult.length);
        Assert.assertEquals("Linha de montagem 1:", splitResult[0]);
        Assert.assertEquals("09:00 Production step I ", splitResult[1]);
        Assert.assertEquals("09:05 Production step II 60min", splitResult[2]);
        Assert.assertEquals("10:05 Production step III 45min", splitResult[3]);
        Assert.assertEquals("10:50 Production step IV 45min", splitResult[4]);
        Assert.assertEquals("12:00 Almoço", splitResult[5]);
        Assert.assertEquals("13:00 Production step V 60min", splitResult[6]);
        Assert.assertEquals("14:00 Production step VI 30min", splitResult[7]);
        Assert.assertEquals("14:30 Production step VII 30min", splitResult[8]);
        Assert.assertEquals("15:00 Production step VIII 45min", splitResult[9]);
        Assert.assertEquals("15:45 Production step IX 45min", splitResult[10]);
        Assert.assertEquals("16:30 Ginástica laboral", splitResult[11]);
    }

    /**
     * If there is a lot of production steps to be organized and their time surpasses one whole day (morning and
     * afternoon periods), the software must divide them into two assembly lines. The below method tests if this
     * dividing step is happening.
     */
    @Test
    public void should_HaveTwoAssemblyLines_When_ThereAreALotOfProductionSteps() {
        String organized = organizer.organize(testsScenariosMap.get(TestUtils.getMethodName()));
        String[] splitResult = organized.split("\\n");

        Assert.assertEquals(15, splitResult.length);
        Assert.assertEquals("Linha de montagem 1:", splitResult[0]);
        Assert.assertEquals("09:00 Production step I 60min", splitResult[1]);
        Assert.assertEquals("10:00 Production step II 60min", splitResult[2]);
        Assert.assertEquals("11:00 Production step III 45min", splitResult[3]);
        Assert.assertEquals("12:00 Almoço", splitResult[4]);
        Assert.assertEquals("13:00 Production step IV 45min", splitResult[5]);
        Assert.assertEquals("13:45 Production step V 30min", splitResult[6]);
        Assert.assertEquals("14:15 Production step VI 60min", splitResult[7]);
        Assert.assertEquals("15:15 Production step VII 30min", splitResult[8]);
        Assert.assertEquals("15:45 Production step VIII 45min", splitResult[9]);
        Assert.assertEquals("16:30 Ginástica laboral", splitResult[10]);
        Assert.assertEquals("", splitResult[11]);
        Assert.assertEquals("Linha de montagem 2:", splitResult[12]);
        Assert.assertEquals("09:00 Production step IX 45min", splitResult[13]);
        Assert.assertEquals("09:45 Production step X 30min", splitResult[14]);
    }

    /**
     * As told at {@link OrganizerTest#should_HaveLunchTime_When_ProductionStepsSurpassesNoon}, the lunch time is
     * present if there is an afternoon period. The below test verify is there is not going to be a lunch time when
     * there are no steps in the afternoon.
     */
    @Test
    public void should_NotHaveLunchTimeAndNeitherGymTime_When_ThereAreNoAfternoonActivities() {
        String organized = organizer.organize(testsScenariosMap.get(TestUtils.getMethodName()));
        String[] splitResult = organized.split("\\n");

        Assert.assertEquals(5, splitResult.length);
        Assert.assertEquals("Linha de montagem 1:", splitResult[0]);
        Assert.assertEquals("09:00 Production step I ", splitResult[1]);
        Assert.assertEquals("09:05 Production step II 60min", splitResult[2]);
        Assert.assertEquals("10:05 Production step III 45min", splitResult[3]);
        Assert.assertEquals("10:50 Production step IV 45min", splitResult[4]);
    }

    /**
     * This test tests if there is going to be the correct lines representing the production steps even if the time
     * of some steps is greater than 60 minutes. For example, if some step starts at 9AM and has 180min, after this
     * one comes the lunch. The test test if that would happen.
     */
    @Test
    public void should_HaveCorrectAssemblyLines_When_SomeProductionLastsMoreThanSixtyMinutes() {
        String organized = organizer.organize(testsScenariosMap.get(TestUtils.getMethodName()));
        String[] splitResult = organized.split("\\n");

        Assert.assertEquals(17, splitResult.length);
        Assert.assertEquals("Linha de montagem 1:", splitResult[0]);
        Assert.assertEquals("09:00 Production step I 120min", splitResult[1]);
        Assert.assertEquals("11:00 Production step II 60min", splitResult[2]);
        Assert.assertEquals("12:00 Almoço", splitResult[3]);
        Assert.assertEquals("13:00 Production step III 45min", splitResult[4]);
        Assert.assertEquals("13:45 Production step IV 45min", splitResult[5]);
        Assert.assertEquals("14:30 Production step V 60min", splitResult[6]);
        Assert.assertEquals("16:00 Ginástica laboral", splitResult[7]);
        Assert.assertEquals("", splitResult[8]);
        Assert.assertEquals("Linha de montagem 2:", splitResult[9]);
        Assert.assertEquals("09:00 Production step VI 120min", splitResult[10]);
        Assert.assertEquals("11:00 Production step VII 30min", splitResult[11]);
        Assert.assertEquals("12:00 Almoço", splitResult[12]);
        Assert.assertEquals("13:00 Production step VIII 45min", splitResult[13]);
        Assert.assertEquals("13:45 Production step IX 45min", splitResult[14]);
        Assert.assertEquals("14:30 Production step X 30min", splitResult[15]);
        Assert.assertEquals("16:00 Ginástica laboral", splitResult[16]);
    }

    /**
     * The lunch time is inclusive. That means that a production step can finish exactly on the time of the lunch,
     * 12h. The below method tests if this behavior is being respected and if it's happening.
     */
    @Test
    public void should_HaveAProductionBeforeLunch_When_ItsTimeEndsOnLunch() {
        String organized = organizer.organize(testsScenariosMap.get(TestUtils.getMethodName()));
        String[] splitResult = organized.split("\\n");

        Assert.assertEquals(5, splitResult.length);
        Assert.assertEquals("Linha de montagem 1:", splitResult[0]);
        Assert.assertEquals("09:00 Production step I 180min", splitResult[1]);
        Assert.assertEquals("12:00 Almoço", splitResult[2]);
        Assert.assertEquals("13:00 Production step II 60min", splitResult[3]);
        Assert.assertEquals("16:00 Ginástica laboral", splitResult[4]);
    }

    /**
     * Almost like {@link OrganizerTest#should_HaveAProductionBeforeLunch_When_ItsTimeEndsOnLunch}, the method below
     * tests finishing on exact time. However, different from the above method, the below method has as premise that
     * the gum time is exclusive. That means that a production step can finish, at max, 16h59min, never 17h.
     */
    @Test
    public void should_NotHaveAProductionLineBeforeGym_When_ItsTimeEndsOnGymTime() {
        String organized = organizer.organize(testsScenariosMap.get(TestUtils.getMethodName()));
        String[] splitResult = organized.split("\\n");

        Assert.assertEquals(8, splitResult.length);
        Assert.assertEquals("Linha de montagem 1:", splitResult[0]);
        Assert.assertEquals("09:00 Production step I 180min", splitResult[1]);
        Assert.assertEquals("12:00 Almoço", splitResult[2]);
        Assert.assertEquals("13:00 Production step II 120min", splitResult[3]);
        Assert.assertEquals("16:00 Ginástica laboral", splitResult[4]);
        Assert.assertEquals("", splitResult[5]);
        Assert.assertEquals("Linha de montagem 2:", splitResult[6]);
        Assert.assertEquals("09:00 Production step III 120min", splitResult[7]);
    }
}