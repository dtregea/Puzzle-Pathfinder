package puzzles.common.solver;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

/**
 * BFS Solver class
 * @author Daniel Tregea
 */

public class BFS {

    public static List<Configuration> solve(Configuration start){
        HashMap<Configuration, Configuration> predecessors = new HashMap<>();
        List<Configuration> queue = new LinkedList<>();
        predecessors.put(start, null);
        queue.add(start);
        while(!queue.isEmpty()){
            Configuration current = queue.remove(0);
            for(Configuration config: current.getNeighbors()){
                if(config.isSolution()){
                    predecessors.put(config, current);
                    return constructPath(predecessors, start, config);
                }
                if(!predecessors.containsKey(config)){
                    predecessors.put(config, current);
                    queue.add(config);
                }
            }
        }
        return constructPath(predecessors, start, null);
    }

    public static List<Configuration> constructPath(HashMap<Configuration,Configuration> predecessors, Configuration start, Configuration end){
        List<Configuration> path = new LinkedList<>();
        if(predecessors.containsKey(end)){
            Configuration currNode = end;
            while(currNode != start){
                path.add(0, currNode);
                currNode = predecessors.get(currNode);
            }
            path.add(0, start);
        }
        return path;
    }
}
