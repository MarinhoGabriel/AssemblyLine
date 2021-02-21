package br.com.marinho.assemblyline.utils;

import br.com.marinho.assemblyline.testUtilities.TestUtils;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.ExpectedSystemExit;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileLock;
import java.nio.file.Paths;
import java.util.Map;

/**
 * Test class to test the {@link br.com.marinho.assemblyline.utils.FileLineUtils} methods and logic.
 */
public class FileLineUtilsTest {

    /** Text extension used to build the files name. */
    private static final String TXT_EXTENSION = ".txt";

    /** Test resource folder. */
    protected static final String SRC_TEST_RESOURCES
            = "src/test/resources/br/com/marinho/assemblyline/files/fileLineUtils/";

    /** Testing rule that expects a {@code System.exit()}. */
    @Rule
    public final ExpectedSystemExit exitRule = ExpectedSystemExit.none();

    /**
     * The test method tests if the {@code System.exit(0)} is called if the file does not exists.
     * In the code, if the files does not exist, a {@link java.io.FileNotFoundException} class is caught and the
     * {@code System.exit(0)} is called indicating that the program is going to finish because the file doesn't exists.
     */
    @Test
    public void should_CallSystemExitZero_When_FileDoesNotExists() {
        this.exitRule.expectSystemExitWithStatus(0);
        FileLineUtils.getAssemblyMap(Paths.get("nonExistentFile").toFile());
    }

    /**
     * The test method tests if the {@code System.exit(1)} is called if the file is corrupted or there is a problem
     * in reading the file.
     * In the code, if the file is corrupted, for instance, an {@link java.io.IOException} class is caught and the
     * {@code System.exit(1)} is called indicating that the program is going to finish.
     * The test locks a file and tries to read it, what results in a {@link IOException}. Then, after finishing the
     * test, the file is released to avoid problems.
     */
    @Test
    public void should_CallSystemExitOne_When_FileIsCorrupted() throws IOException {
        this.exitRule.expectSystemExitWithStatus(1);

        File file = new File(SRC_TEST_RESOURCES + TestUtils.getMethodName() + TXT_EXTENSION);
        RandomAccessFile raf = new RandomAccessFile(file.getAbsolutePath(), "rw");
        final FileLock[] fileLock = {raf.getChannel().tryLock()};
        this.exitRule.checkAssertionAfterwards(() -> fileLock[0].release());
        FileLineUtils.getAssemblyMap(file);
    }

    /**
     * The test method tests if the {@code System.exit(1)} is called if there is a line with no time.
     * The {@link FileLineUtils#getAssemblyMap(File)} method calls a method to convert the line. This method makes a
     * check to see if there's a number in the line, indicating the time, to get this value. If there's no number, it
     * calls {@code System.exit(1)} and finishes the program, printing the error message.
     */
    @Test
    public void should_CallSystemExitOne_When_ThereIsALineWithNoTime() {
        this.exitRule.expectSystemExitWithStatus(1);
        File file = Paths.get(SRC_TEST_RESOURCES + TestUtils.getMethodName() + TXT_EXTENSION).toFile();
        FileLineUtils.getAssemblyMap(file);
    }

    /**
     * The below test method tests if the {@code System.exit(1)} is called when the maintenance line has no "-". If
     * that happens, we can't know if the activity name has "maintenance" in its name (like "Computer maintenance",
     * for instance) or if it's the maintenance line. Thus, the {@code System.exit(1)} is called indicating a problem
     * and finishing the program.
     */
    @Test
    public void should_CallSystemExitOne_When_MaintenanceLineDoesNotHaveSeparator() {
        this.exitRule.expectSystemExitWithStatus(1);
        File file = Paths.get(SRC_TEST_RESOURCES + TestUtils.getMethodName() + TXT_EXTENSION).toFile();
        FileLineUtils.getAssemblyMap(file);
    }

    /**
     * The method tests if the file lines are going to be converted even if the maintenance line is written in the
     * uppercase form.
     * The code has a logic that does not consider case difference by comparing the all the strings in the uppercase
     * form.
     */
    @Test
    public void should_ConvertLine_When_MaintenanceTextIsUpperCase() {
        File file = Paths.get(SRC_TEST_RESOURCES + TestUtils.getMethodName() + TXT_EXTENSION).toFile();
        Map<String, Integer> assemblyMap = FileLineUtils.getAssemblyMap(file);

        Assert.assertEquals(8, assemblyMap.size());
        Assert.assertEquals(Integer.valueOf(60), assemblyMap.get("Cutting of steel sheets"));
        Assert.assertEquals(Integer.valueOf(30), assemblyMap.get("Austenpera (Heat treatment)"));
        Assert.assertEquals(Integer.valueOf(45), assemblyMap.get("Tempering sub-zero (Heat treatment)"));
        Assert.assertEquals(Integer.valueOf(60), assemblyMap.get("Safety sensor assembly"));
        Assert.assertEquals(Integer.valueOf(45), assemblyMap.get("Pieces washing"));
        Assert.assertEquals(Integer.valueOf(30), assemblyMap.get("Axis calibration"));
        Assert.assertEquals(Integer.valueOf(45), assemblyMap.get("Steel bearing assembly"));
        Assert.assertEquals(Integer.valueOf(5), assemblyMap.get("Assembly line cooling"));
    }

    /**
     * The method tests if the lines are going to be converted if the file only has maintenance activities. It's not
     * necessary to have an non-maintenance activity and the software must be prepared for that.
     */
    @Test
    public void should_ConvertLines_When_ThereAreJustMaintenance() {
        File file = Paths.get(SRC_TEST_RESOURCES + TestUtils.getMethodName() + TXT_EXTENSION).toFile();
        Map<String, Integer> assemblyMap = FileLineUtils.getAssemblyMap(file);

        Assert.assertEquals(8, assemblyMap.size());
        Assert.assertEquals(Integer.valueOf(5), assemblyMap.get("Cutting of steel sheets"));
        Assert.assertEquals(Integer.valueOf(5), assemblyMap.get("Austenpera (Heat treatment)"));
        Assert.assertEquals(Integer.valueOf(5), assemblyMap.get("Tempering sub-zero (Heat treatment)"));
        Assert.assertEquals(Integer.valueOf(5), assemblyMap.get("Safety sensor assembly"));
        Assert.assertEquals(Integer.valueOf(5), assemblyMap.get("Pieces washing"));
        Assert.assertEquals(Integer.valueOf(5), assemblyMap.get("Axis calibration"));
        Assert.assertEquals(Integer.valueOf(5), assemblyMap.get("Steel bearing assembly"));
        Assert.assertEquals(Integer.valueOf(5), assemblyMap.get("Assembly line cooling"));
    }

    /**
     * As in {@link FileLineUtilsTest#should_ConvertLines_When_ThereAreJustMaintenance()}, the below method, in
     * contrast, tests if the file lines are going to be converted if there is no maintenance lines.
     */
    @Test
    public void should_ConvertLines_When_ThereIsNoMaintenance() {
        File file = Paths.get(SRC_TEST_RESOURCES + TestUtils.getMethodName() + TXT_EXTENSION).toFile();
        Map<String, Integer> assemblyMap = FileLineUtils.getAssemblyMap(file);

        Assert.assertEquals(8, assemblyMap.size());
        Assert.assertEquals(Integer.valueOf(60), assemblyMap.get("Cutting of steel sheets"));
        Assert.assertEquals(Integer.valueOf(30), assemblyMap.get("Austenpera (Heat treatment)"));
        Assert.assertEquals(Integer.valueOf(45), assemblyMap.get("Tempering sub-zero (Heat treatment)"));
        Assert.assertEquals(Integer.valueOf(60), assemblyMap.get("Safety sensor assembly"));
        Assert.assertEquals(Integer.valueOf(45), assemblyMap.get("Pieces washing"));
        Assert.assertEquals(Integer.valueOf(30), assemblyMap.get("Axis calibration"));
        Assert.assertEquals(Integer.valueOf(45), assemblyMap.get("Steel bearing assembly"));
        Assert.assertEquals(Integer.valueOf(30), assemblyMap.get("Assembly line cooling"));
    }

    /**
     * The test method tests if the file lines are going to be converted even if there is a separator between the
     * activity name and the time.
     * The method removes any whitespaces or "-" characters in the end of the activity name string, so the test
     * should pass.
     */
    @Test
    public void should_ConvertLines_When_ThereIsASeparatorBetweenTheNameAndTime() {
        File file = Paths.get(SRC_TEST_RESOURCES + TestUtils.getMethodName() + TXT_EXTENSION).toFile();
        Map<String, Integer> assemblyMap = FileLineUtils.getAssemblyMap(file);

        Assert.assertEquals(8, assemblyMap.size());
        Assert.assertEquals(Integer.valueOf(60), assemblyMap.get("Cutting of steel sheets"));
        Assert.assertEquals(Integer.valueOf(30), assemblyMap.get("Austenpera (Heat treatment)"));
        Assert.assertEquals(Integer.valueOf(45), assemblyMap.get("Tempering sub-zero (Heat treatment)"));
        Assert.assertEquals(Integer.valueOf(60), assemblyMap.get("Safety sensor assembly"));
        Assert.assertEquals(Integer.valueOf(45), assemblyMap.get("Pieces washing"));
        Assert.assertEquals(Integer.valueOf(30), assemblyMap.get("Axis calibration"));
        Assert.assertEquals(Integer.valueOf(45), assemblyMap.get("Steel bearing assembly"));
        Assert.assertEquals(Integer.valueOf(30), assemblyMap.get("Assembly line cooling"));
    }

    /**
     * The below test method tests is the file lines ae going to be converted if there is no space in the activities.
     * The code, as said in {@link FileLineUtilsTest#should_ConvertLine_When_MaintenanceTextIsUpperCase}, does not
     * consider case difference. Besides, it also does not consider whitespaces, converting the line even if has no
     * space (even between the activity name and time).
     */
    @Test
    public void should_ConvertLines_When_ThereIsNoSpace() {
        File file = Paths.get(SRC_TEST_RESOURCES + TestUtils.getMethodName() + TXT_EXTENSION).toFile();
        Map<String, Integer> assemblyMap = FileLineUtils.getAssemblyMap(file);

        Assert.assertEquals(8, assemblyMap.size());
        Assert.assertEquals(Integer.valueOf(60), assemblyMap.get("Cuttingofsteelsheets"));
        Assert.assertEquals(Integer.valueOf(30), assemblyMap.get("Austenpera(Heattreatment)"));
        Assert.assertEquals(Integer.valueOf(45), assemblyMap.get("Temperingsub-zero(Heattreatment)"));
        Assert.assertEquals(Integer.valueOf(60), assemblyMap.get("Safetysensorassembly"));
        Assert.assertEquals(Integer.valueOf(45), assemblyMap.get("Pieceswashing"));
        Assert.assertEquals(Integer.valueOf(30), assemblyMap.get("Axiscalibration"));
        Assert.assertEquals(Integer.valueOf(45), assemblyMap.get("Steelbearingassembly"));
        Assert.assertEquals(Integer.valueOf(5), assemblyMap.get("Assemblylinecooling"));
    }

    /**
     * The production step title does not have a number on it. Then, this test method tests if a {@code System.exit
     * (1)} is called if there is a number in the production title.
     */
    @Test
    public void should_CallSystemExitOne_When_ThereIsANumberInAProductionStepTitle() {
        this.exitRule.expectSystemExitWithStatus(1);
        File file = Paths.get(SRC_TEST_RESOURCES + TestUtils.getMethodName() + TXT_EXTENSION).toFile();
        FileLineUtils.getAssemblyMap(file);
    }

    /**
     * The below test method tests if the lines are going to be correctly converted if there is a production step
     * that lasts more than 60 minutes. There is no restriction for a production step to last 60 minutes at max, so
     * this test tests this case.
     */
    @Test
    public void should_ConvertLines_When_SomeTimeIsOverSixtyMinutes() {
        File file = Paths.get(SRC_TEST_RESOURCES + TestUtils.getMethodName() + TXT_EXTENSION).toFile();
        Map<String, Integer> assemblyMap = FileLineUtils.getAssemblyMap(file);

        Assert.assertEquals(8, assemblyMap.size());
        Assert.assertEquals(Integer.valueOf(60), assemblyMap.get("Cutting of steel sheets"));
        Assert.assertEquals(Integer.valueOf(30), assemblyMap.get("Austenpera (Heat treatment)"));
        Assert.assertEquals(Integer.valueOf(45), assemblyMap.get("Tempering sub-zero (Heat treatment)"));
        Assert.assertEquals(Integer.valueOf(100), assemblyMap.get("Safety sensor assembly"));
        Assert.assertEquals(Integer.valueOf(45), assemblyMap.get("Pieces washing"));
        Assert.assertEquals(Integer.valueOf(120), assemblyMap.get("Axis calibration"));
        Assert.assertEquals(Integer.valueOf(45), assemblyMap.get("Steel bearing assembly"));
        Assert.assertEquals(Integer.valueOf(5), assemblyMap.get("Assembly line cooling"));
    }
}