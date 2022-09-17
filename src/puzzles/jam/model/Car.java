package puzzles.jam.model;
import java.util.Arrays;

/**
 * Car class for Jam Config Functionality
 * @author Daniel Tregea
 */
public class Car {
    private final String carName;
    private final int[] coordinates; // Even indexes are X coordinates, with the next value its corresponding Y value
    private final ORIENTATION orientation;

    /**
     * Enum for car orientation
     */
    public enum ORIENTATION{
        HORIZONTAL, VERTICAL
    }

    /** Initialize a car
     * @param carName The car name
     * @param startX the starting X coordinate
     * @param startY the starting Y coordinate
     * @param endX the ending X coordinate
     * @param endY the ending Y coordinate
     */
    public Car(String carName, int startX, int startY, int endX, int endY) {
        this.carName = carName;
        // Get the car length
        int length;
        if(endX - startX == 1 || endY - startY == 1) { // if size 2
            length = 2;
            coordinates = new int[4];
            coordinates[2] = endX; // set end coordinates
            coordinates[3] = endY;
        }
        else{ // size 3
            length = 3;
            coordinates = new int[6];
            coordinates[4] = endX; // set end coordinates
            coordinates[5] = endY;
        }
        // Set start coordinates
        coordinates[0] = startX;
        coordinates[1] = startY;
        // Get car orientation
        if (startX == endX) {
            this.orientation = ORIENTATION.HORIZONTAL;
        } else {
            this.orientation = ORIENTATION.VERTICAL;
        }
        // Get middle coordinates for length 3 cars
        if (length == 3){
            if (orientation == ORIENTATION.VERTICAL){
                coordinates[2] = coordinates[4] - 1;
                coordinates[3] = endY;
            } else{
                coordinates[2] = startX;
                coordinates[3] = coordinates[5] - 1;
            }
        }
    }

    /** Copy Constructor
     * @return a copy of a car
     */
    public Car copy(){
        return new Car(carName, coordinates[0], coordinates[1], coordinates[coordinates.length - 2], coordinates[coordinates.length - 1]);
    }

    /**
     * Move a car backwards
     */
    public void moveBackward(){
        if (orientation == ORIENTATION.HORIZONTAL){
            for (int i = 1; i < coordinates.length; i+= 2)
                coordinates[i] -= 1;
        } else{
            for (int i = 0; i < coordinates.length; i+= 2)
                coordinates[i] += 1;
        }
    }

    /**
     * Move a car forwards
     */
    public void moveForward(){
        if (orientation == ORIENTATION.HORIZONTAL){
            for (int i = 1; i < coordinates.length; i+= 2)
                coordinates[i] += 1;
        } else{
            for (int i = 0; i < coordinates.length; i+= 2)
                coordinates[i] -= 1;
        }
    }

    /** Get the name of a car
     * @return name of the car
     */
    public String getCarName() {
        return carName;
    }


    /** Get the coordinate array of a car
     * @return the coordinate array of a car
     */
    public int[] getCoordinates() {
        return coordinates;
    }

    /** Get the orientation of a car
     * @return the orientation of a car
     */
    public ORIENTATION getOrientation(){
        return orientation;
    }


    /** Determine if two cars are equal
     * @param obj The car to be compared
     * @return Equality of the cars name
     */
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Car){
            Car temp = (Car) obj;
            return carName.equals(temp.carName) && Arrays.equals(coordinates, temp.coordinates);
        } else {
            return false;
        }
    }

    /** Hashcode function of a car
     * @return hashcode of a car
     */
    @Override
    public int hashCode() {
        return Arrays.hashCode(coordinates);
    }
}
