package puzzles.jam.gui;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import puzzles.common.Observer;
import puzzles.jam.model.Car;
import puzzles.jam.model.JamClientData;
import puzzles.jam.model.JamModel;
import java.io.FileNotFoundException;
import java.util.ArrayList;

/** GUI for Jam game.
 * @author Daniel Tregea
 */
public class JamGUI extends Application  implements Observer<JamModel, JamClientData> {
    // Global variables to be used across multiple stages when loading new files
    ArrayList<String> files;
    boolean carSelect = false;
    TextArea loadFileArea;
    Button loadButton, resetButton, hintButton;
    HBox bottomContainer;
    Label clientDataLabel;
    GridPane carGrid;
    Car carSelected;
    JamModel model;
    VBox root;

    public void init() throws FileNotFoundException {
        files = new ArrayList<>();
        for (int i = 0; i <= 11; i++){
            files.add("jam-" + i + ".txt");
        }
        String filename = getParameters().getRaw().get(0);
        model = new JamModel(filename);
    }

    @Override
    public void start(Stage stage) throws Exception {
        model.addObserver(this);
        loadFileArea = new TextArea("Filename");
        loadButton = new Button("Load");
        resetButton = new Button("Reset");
        hintButton = new Button("Hint");
        bottomContainer = new HBox(resetButton, hintButton, loadButton, loadFileArea);
        clientDataLabel = new Label("");
        carGrid = new GridPane();
        root = new VBox(clientDataLabel, carGrid, bottomContainer);
        root.setAlignment(Pos.CENTER);
        clientDataLabel.setFont(Font.font("Comic Sans MS", 24));
        loadFileArea.setPrefSize(75, resetButton.getHeight());
        bottomContainer.setAlignment(Pos.CENTER);

        // Load new stage with new file
        loadButton.setOnMouseClicked(mouseEvent -> {
            String filename = loadFileArea.getText();
            if(files.contains(filename)){
                stage.close();
                model.processMove("l " + filename);
                try {
                    start(new Stage());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else{
                update(model, new JamClientData("File Not Found"));
            }
        });

        // Add .txt to text area for convenience
        loadFileArea.setOnMouseClicked(mouseEvent -> loadFileArea.setText(".txt"));

        // Reset the config
        resetButton.setOnMouseClicked(mouseEvent -> model.processMove("r"));

        // Generate hint
        hintButton.setOnMouseClicked(mouseEvent -> model.processMove("h"));
        update(model, new JamClientData(""));
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.sizeToScene();
        stage.setResizable(false);
        stage.setTitle(model.getResetFile());
        stage.show();

    }

    @Override
    public void update(JamModel jamModel, JamClientData jamClientData) {
        Car[] cars = jamModel.getCars();
        clientDataLabel.setText(jamClientData.toString());
        GridButton button;
        // Add and update buttons
        for(int i = 0; i< jamModel.getDIMX(); i++){
            for(int j = 0; j< jamModel.getDIMY(); j++){
                button = new GridButton(JamGUI.getOccupied(cars, i, j), i, j);
                GridButton finalButton = button;
                button.setOnMouseClicked(mouseEvent -> {
                    // Select a car if one has not aleady
                    if(!carSelect){
                        String buttonName = finalButton.getCarName();
                        if(!buttonName.equals("empty")){
                            carSelected = jamModel.getCar(finalButton.getCarName());
                            clientDataLabel.setText(carSelected.getCarName() + " selected");
                            carSelect = true;
                        }
                    // If car has been selected, process the move
                    } else{
                        int rowSelect = finalButton.getRow(), columnSelect = finalButton.getColumn();
                        model.processMove("s " + carSelected.getCarName() + " " + rowSelect + " " + columnSelect);
                        carSelect = false;
                    }
                });
                carGrid.add(button, j, i);
            }
        }
    }

    public static void main(String[] args) {
        Application.launch(args);
    }

    /** Get the Car occupying a coordinate
     * @param cars the array of cars in the config
     * @param x the x coordinate to be checked
     * @param y the y coordinate to be checked
     * @return The car occupying the space
     */
    public static String getOccupied(Car[] cars, int x, int y){
        for(Car car: cars){
            int[] coords = car.getCoordinates();
            for(int i = 0; i < coords.length; i+=2){
                if(coords[i] == x && coords[i+1] == y) {
                    return car.getCarName();
                }
            }
        }
        return "empty";
    }
}
