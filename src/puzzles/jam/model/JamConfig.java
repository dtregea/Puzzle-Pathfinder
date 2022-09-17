package puzzles.jam.model;
import puzzles.common.solver.Configuration;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

/**
 * JamConfig class for JamModel
 * This is a representation of a particular state of a game
 */
public class JamConfig implements Configuration{
    private static int DIMENSION_X, DIMENSION_Y, NUM_CARS;
    private static int totalConfigs = 0;
    private final static HashSet<JamConfig> uniqueConfigs = new HashSet<>();
    private final Car[] cars; // all cars in a config
    private final HashMap<String, Car> carMap;
    private Car carX;

    /** Generate a jam config
     * @param filename the file to make a jam config
     * @throws FileNotFoundException Indicates the file was not found
     */
    public JamConfig(String filename) throws FileNotFoundException {
        Scanner scan = new Scanner(new File(filename));
        DIMENSION_X = scan.nextInt();
        DIMENSION_Y = scan.nextInt();
        NUM_CARS = scan.nextInt();
        carMap = new HashMap<>();
        cars = new Car[NUM_CARS];
        String name;
        int startX, startY, endX, endY;
        for(int i = 0; i< NUM_CARS; i ++){
            name = scan.next();
            startX = scan.nextInt();
            startY = scan.nextInt();
            endX = scan.nextInt();
            endY = scan.nextInt();
            cars[i] = new Car(name, startX, startY, endX, endY);
            carMap.put(name, cars[i]);
            if (Objects.equals(name, "X")) {
                carX = cars[i];
            }
        }
        totalConfigs++;
        addUnique();
    }

    /** Copy constructor
     * @param other the config to be copied
     */
    public JamConfig(JamConfig other){
        totalConfigs++;
        cars = new Car[other.cars.length];
        carMap = new HashMap<>();
        for(int i = 0; i < cars.length; i++){
            cars[i] = other.cars[i].copy();
            carMap.put(cars[i].getCarName(), cars[i]);
            if (Objects.equals(cars[i].getCarName(), "X")) {
                carX = cars[i];
            }
        }
        addUnique();
    }

    /** Get the total number of generated configs
     * @return total number of configs
     */
    public static int getTotalConfigs() {
        return totalConfigs;
    }

    /** Get the total number of generated unique configs
     * @return total number of unique configs
     */
    public static int getTotalUniqueConfigs() {
        return uniqueConfigs.size();
    }

    /**
     * Add to the unique config collection
     */
    private void addUnique(){
        uniqueConfigs.add(this);
    }

    /** Get a specific car from the cars collection
     * @param name the name of the car
     * @return the car specified
     */
    public Car getCar(String name){
        return carMap.get(name);
    }

    public Car getCarX(){
        return carX;
    }

    /** Get all cars in a config
     * @return all cars in the config
     */
    public Car[] getCars(){
        return cars;
    }

    /** Determine if car "carX" has reached the last column
     * @return whether car "carX" has reached the last column
     */
    @Override
    public boolean isSolution() {
        int[] carXCoordinates = getCarX().getCoordinates();
        for(int i = 1; i < carXCoordinates.length; i+=2){
            if(carXCoordinates[i] == DIMENSION_Y - 1) {
                return true;
            }
        }
        return false;
    }

    /** Return neighbors of the current configuration
     * @return neighbors of the current configuration
     */
    @Override
    public Configuration[] getNeighbors() {
        ArrayList<Configuration> neighbors = new ArrayList<>();
        // for each car, make a config for moving forward and backward
        for(int i = 0; i < NUM_CARS; i++){
            //new configuration of each car moved forward one place
            cars[i].moveForward();
            if (!isOutOfBounds(cars[i]) && !isConflict(cars[i])) {
                neighbors.add(new JamConfig(this));
            }
            // place the car back in original position
            cars[i].moveBackward();
            //new configuration of this moved backward one place
            cars[i].moveBackward();
            if (!isOutOfBounds(cars[i]) && !isConflict(cars[i])) {
                neighbors.add(new JamConfig(this));
            }
            // place the car back to original position
            cars[i].moveForward();
        }
        // Convert the ArrayList to a native array
        Configuration[] result = new Configuration[neighbors.size()];
        for(int i = 0; i < result.length; i++){
            result[i] = neighbors.get(i);
        }
        return result;
    }

    /** Determine if a car has reached out of bounds
     * @param car the car whose coordinates to check
     * @return Whether a car is out of bounds
     */
    public static boolean isOutOfBounds(Car car){
        int[] coords = car.getCoordinates();
        for(int i = 0; i < coords.length; i++){
            if(coords[i] <= -1) // if either carX or Y coordinates < 0
                return true;
            if (i % 2 == 0){ // if carX coordinates are > than number of rows
                if (coords[i] >= DIMENSION_X)
                    return true;
            } else{ // if Y coordinates are > than number of columns
                if (coords[i] >= DIMENSION_Y)
                    return true;
            }
        }
        return false;
    }

    /** Determine if a car is in conflict with any of the rest
     * @param car the car to check conflict with
     * @return Whether the car is in conflict with the rest
     */
    public boolean isConflict(Car car){
        int[] coords = car.getCoordinates();
        // For each car
        for (Car eachOtherCar : cars) {
            // Skip over comparing a car to itself
            if (!car.equals(eachOtherCar)) {
                int[] coordCompare = eachOtherCar.getCoordinates();
                // compare each set of (x,y) coordinates in the current to each set of (x,y) coordinates in the compared car
                for (int i = 0; i < coords.length; i += 2) {
                    for (int j = 0; j < coordCompare.length; j += 2) {
                        if (coords[i] == coordCompare[j] && coords[i + 1] == coordCompare[j + 1])
                            return true;
                    }
                }
            }
        }
        return false;
    }

    /** Generate a grid representing the configuration
     * @return A grid of the configuration
     */
    @Override
    public String toString() {
        StringBuilder build = new StringBuilder();
        String[][] grid = new String[DIMENSION_X][DIMENSION_Y];
        build.append("\t");
        for(int i = 0; i < DIMENSION_Y; i++){
            build.append(i).append(" ");
        }
        build.append("\n\t");
        build.append("-".repeat(Math.max(0, DIMENSION_Y * 2)));
        build.append("\n");
        for(Car car: cars){
            int[] coords = car.getCoordinates();
            for(int i = 0; i < coords.length; i+= 2){
                grid[coords[i]][coords[i + 1]] = car.getCarName();
            }
        }
        for(int i = 0; i < DIMENSION_X; i++){
            for(int j = 0; j < DIMENSION_Y; j++){
                if(grid[i][j] == null)
                    grid[i][j] = ".";
            }
        }
        for(int i = 0; i < DIMENSION_X; i++){
            build.append(i).append("|\t");
            for(int j = 0; j < DIMENSION_Y; j++){
                build.append(grid[i][j]);
                if (j!= DIMENSION_Y - 1)
                    build.append(" ");
            }
            build.append("\n");
        }
        return build.toString();
    }

    /** Get the carX dimension of the configuration
     * @return the carX dimension of the configuration
     */
    public int getDimensionX(){
        return DIMENSION_X;
    }

    /** Get the Y dimension of the configuration
     * @return the Y dimension of the configuration
     */
    public int getDimensionY(){
        return DIMENSION_Y;
    }

    /** Hash code function of JamConfig
     * @return hash code of a JamConfig
     */
    @Override
    public int hashCode() {
        return Arrays.hashCode(cars);

    }

    /** Determine equality of a JamConfig to another object
     * @param obj the object to be compared to
     * @return Whether a JamConfig's cars is equal to the object compared
     */
    @Override
    public boolean equals(Object obj) {
        if(obj instanceof JamConfig) {
            JamConfig temp = (JamConfig) obj;
            for (int i = 0; i < NUM_CARS; i++) {
                if(!cars[i].equals(temp.cars[i]))
                    return false;
            }
            return true;
        } else {
            return false;
        }
    }
}
