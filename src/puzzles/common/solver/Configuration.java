package puzzles.common.solver;

/**
 * Configuration interface
 * An object must implement these methods to be solved by a pathfinding algorithm
 * @author Daniel Tregea
 */

public interface Configuration {

    /** Determine if the Configuration is a solution
     * @return if the Configuration is the solution
     */
    boolean isSolution();

    /** Get the neighbors, "adjacent nodes", of the Configuration
     * @return The neighbors of the Configuration
     */
    Configuration[] getNeighbors();
}
