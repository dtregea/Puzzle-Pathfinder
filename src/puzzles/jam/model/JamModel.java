package puzzles.jam.model;
import puzzles.common.Observer;
import puzzles.common.solver.BFS;
import puzzles.common.solver.Configuration;

import java.io.FileNotFoundException;
import java.util.LinkedList;
import java.util.List;

/**
 * Model of the GUI and PTUI
 * @author Daniel Tregea
 * @author RIT CS
 */
public class JamModel {
    /** the collection of observers of this model */
    private final List<Observer<JamModel, JamClientData>> observers = new LinkedList<>();

    /** the current configuration */
    private JamConfig currentConfig;
    private String resetFile = null;
    private boolean gameEnd = false, gameSolved = false;

    public JamModel(String filename) throws FileNotFoundException {
        if(resetFile == null)
            resetFile = filename;
        currentConfig = new JamConfig(filename);
        JamClientData data = new JamClientData("Loaded: " + filename);
        alertObservers(data);
    }
    /**
     * The view calls this to add itself as an observer.
     *
     * @param observer the view
     */
    public void addObserver(Observer<JamModel, JamClientData> observer) {
        this.observers.add(observer);
    }

    /**
     * The model's state has changed (the counter), so inform the view via
     * the update method
     */
    private void alertObservers(JamClientData data) {
        for (var observer : observers) {
            observer.update(this, data);
        }
    }

    /** Verify the validity of a command
     * @param move The command string
     * @return the validity of the command
     */
    public boolean isValidMove(String move){
        String[] args = move.trim().split(" ");
        switch (args[0]) {
            case "h":
            case "q":
            case "r":
                return true;
            case "l":
                if (args.length == 2)
                    return true;
                break;
            case "s":
                if (args.length == 4) {
                    try {
                        if (getCar(args[1]) == null)
                            throw new Exception("Car Selected Does Not Exist!");
                        int rowSelect, colSelect;
                        rowSelect = Integer.parseInt(args[2]);
                        colSelect = Integer.parseInt(args[3]);
                        if (rowSelect < 0 || colSelect < 0 || rowSelect >= getDIMX() || colSelect >= getDIMY())
                            throw new Exception("Coordinates Out Of Bounds!");
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid Coordinate Type Input");
                        return false;
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                        return false;
                    }
                    return true;
                }
                break;
        }
        return false;
    }

    /** Execute a command
     * @param move The command to be executed
     */
    public void processMove(String move){
        String[] args = move.trim().split(" ");
        JamClientData message = new JamClientData("");
        switch(args[0]){
            // Get hint
            case "h":
                if(gameSolved){
                    alertObservers(new JamClientData("Already Solved"));
                    return;
                }
                alertObservers(new JamClientData("Generating next move (Please Wait)"));
                // Solve the current config and set it to the next step
                List<Configuration> endPath = BFS.solve(currentConfig);
                try {
                    currentConfig = (JamConfig) endPath.get(1);
                    message = new JamClientData("Next Move Found");
                } catch(IndexOutOfBoundsException e){
                    message = new JamClientData("No Solution");
                }
                break;
            // Load a file
            case "l":
                try{
                    String filename = "data/jam/" + args[1];
                    resetFile = filename;
                    currentConfig = new JamConfig(filename);
                    gameSolved = false;
                    alertObservers(new JamClientData(args[1] + " Loaded!"));
                } catch (FileNotFoundException e){
                    alertObservers(new JamClientData("File Not Found!"));
                }
                return;
            // select a car to move
            case "s":
                if(gameSolved){
                    alertObservers(new JamClientData("Already Solved"));
                    return;
                }
                Car carMoved = currentConfig.getCar(args[1].toUpperCase());
                int[] coords = carMoved.getCoordinates();
                message = new JamClientData("");
                String command = "";
                int rowSelect = Integer.parseInt(args[2]), colSelect = Integer.parseInt(args[3]);
                // If the car is horizontal
                if(carMoved.getOrientation() == Car.ORIENTATION.HORIZONTAL){
                    if(rowSelect == coords[0]){ // if valid row selected
                        if (colSelect < coords[1]){ // if spot chosen behind car
                            command = "b";
                        } else if (colSelect > coords[coords.length - 1]){ // if spot chosen ahead of car
                            command = "f";
                        } else{ // if spot chosen on car
                            message = new JamClientData("You're Already There!");
                        }

                    } else{
                        message = new JamClientData("Invalid Row!");
                    }
                // If the car is vertical
                }else{
                    if(colSelect == coords[1]){ // if valid column is selected
                        if (rowSelect < coords[0]){
                            command = "f";
                        } else if (rowSelect > coords[coords.length - 2]){
                            command = "b";
                        } else{
                            message = new JamClientData("You're Already There!");
                        }
                    } else{
                        message = new JamClientData("Invalid Column!");
                    }
                }
                // Move the car until it makes an invalid move
                if (carMoved.getOrientation() == Car.ORIENTATION.HORIZONTAL) {
                    while ((colSelect != coords[1] && colSelect != coords[coords.length - 1])) {
                        if (moveCar(carMoved, command)) break;
                    }
                } else {
                    while(rowSelect != coords[0] && rowSelect != coords[coords.length - 2]){
                        if (moveCar(carMoved, command)) break;
                    }
                }
                break;
            // Reset the config
            case "r":
                try{
                    currentConfig = new JamConfig(resetFile);
                    message = new JamClientData("Game Reset!");
                    gameSolved = false;
                } catch (FileNotFoundException e){
                    message = new JamClientData("File Not Found!");
                }
                break;
            // End the game (PTUI use)
            case "q":
                gameEnd = true;
                break;
        }
        if(currentConfig.isSolution()) {
            gameSolved = true;
            message = new JamClientData("Game Solved!");
        }
        if (args[0].equals("q")){
            message = new JamClientData("Game Ended!");
        }
        alertObservers(message);
    }

    /** Helper function of processMove to move a car
     * @param carMoved The car to be moved
     * @param command The command to be executed
     * @return Whether the car made an invalid move
     */
    private boolean moveCar(Car carMoved, String command) {
        if (command.equals("f")) {
            carMoved.moveForward();
        } else if (command.equals("b")){
            carMoved.moveBackward();
        } else{
            return true;
        }
        // Correct an invalid move
        if (JamConfig.isOutOfBounds(carMoved) || currentConfig.isConflict(carMoved)){
            if (command.equals("f")) {
                carMoved.moveBackward();
            } else{
                carMoved.moveForward();
            }
            return true;
        }
        return false;
    }

    /** Determine whether to end the game (PTUI use)
     * @return Whether the game has ended
     */
    public boolean isGameEnd(){
        return gameEnd;
    }

    /** Get the X dimension of the current config
     * @return The X dimension of the current config
     */
    public int getDIMX(){
        return currentConfig.getDimensionX();
    }

    /** Get the Y dimension of the current config
     * @return The Y dimension of the current config
     */
    public int getDIMY(){
        return currentConfig.getDimensionY();
    }

    /** Get an array of all the configs cars
     * @return array of all the configs cars
     */
    public Car[] getCars(){
        return currentConfig.getCars();
    }

    /** Get a specific car from the config
     * @param name the name of the car to get from the config
     * @return a car from the current config
     */
    public Car getCar(String name){
        return currentConfig.getCar(name);
    }

    /** Get the reset file
     * @return the reset file
     */
    public String getResetFile(){
        return resetFile;
    }

    /** Generate a toString for the current config
     * @return a Grid representing the current config
     */
    @Override
    public String toString() {
        return currentConfig.toString();
    }
}
