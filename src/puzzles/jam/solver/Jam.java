package puzzles.jam.solver;

import puzzles.common.solver.Configuration;
import puzzles.common.solver.BFS;
import puzzles.jam.model.JamConfig;
import java.io.FileNotFoundException;
import java.util.List;

/**
 * Driver class for JamConfig
 * Solve a JamConfig
 * @author Daniel Tregea
 */
public class Jam {
    public static void main(String[] args){
        if (args.length != 1) {
            System.out.println("Provide a file in the data directory as an argument");
        }
        List<Configuration> result;
        double start = System.currentTimeMillis();
        try{
            result = BFS.solve(new JamConfig(args[0]));

            double elapsed = (System.currentTimeMillis() - start) / 1000.0;
            if (result.isEmpty()){
                System.out.println("No solution");
            } else {
                for (int i = 0; i < result.size(); i++) {
                    System.out.println("Step " + i + ":\n" + result.get(i));
                }
            }
            System.out.println("Time elapsed: " + elapsed);
            System.out.println("Total configs: " + JamConfig.getTotalConfigs());
            System.out.println("Unique configs: " + JamConfig.getTotalUniqueConfigs());
        } catch (FileNotFoundException e){
            System.out.println("File Not Found");
        }
    }
}