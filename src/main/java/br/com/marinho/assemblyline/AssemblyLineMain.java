package br.com.marinho.assemblyline;

import br.com.marinho.assemblyline.utils.FileLineUtils;
import br.com.marinho.assemblyline.utils.Organizer;

import java.io.File;
import java.util.Map;

/**
 * Main class of the assembly line program.
 */
public class AssemblyLineMain {

    /**
     * Method where the organizing logic happens, to get an output from a file with some production steps.
     * @param args Running arguments
     */
    public static void main(String[] args) {
        Organizer organizer = new Organizer();
        File file = new File("input.txt");
        Map<String, Integer> map = FileLineUtils.getAssemblyMap(file);

        String organize = organizer.organize(map);
        System.out.println(organize);
    }
}