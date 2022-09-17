package puzzles.jam.gui;
import javafx.scene.control.Button;
import java.util.HashMap;

/**
 * Custom Button class for Jam GUI slot buttons.
 * @author Daniel Tregea
 */
public class GridButton extends Button {

    private final static int BUTTON_FONT_SIZE = 20;
    private final static int ICON_SIZE = 75;
    private static final HashMap<String, String> colors = new HashMap<>();
    private static boolean colorsLoaded = false;
    private final int row, column;
    private final String carName;

    /** Construct a GridButton
     * @param type The type of slot
     * @param row The row of the GridButton
     * @param column The column of the Gridbutton
     */
    public GridButton(String type, int row, int column) {
        if(!colorsLoaded) {
            loadColors();
            colorsLoaded = true;
        }
        this.row = row;
        this.column = column;
        if(type.equals("empty")){
            carName = "empty";
            setText(".");
        } else{
            carName = type;
            setText(type);
        }
        setStyle(
                "-fx-font-size: " + BUTTON_FONT_SIZE + ";" +
                        "-fx-background-color: " + colors.get(type) + ";" +
                        "-fx-font-weight: bold;");
        setMinSize(ICON_SIZE, ICON_SIZE);
        setMaxSize(ICON_SIZE, ICON_SIZE);
    }

    /** Get the row of the GridButton in the Gridpane
     * @return The row of the GridButton
     */
    public int getRow() {
        return row;
    }

    /** Get the column of the GridButton in the Gridpane
     * @return The column of the GridButton
     */
    public int getColumn() {
        return column;
    }

    /** Get the name of the space
     * @return the name of the space
     */
    public String getCarName(){
        return carName;
    }

    /** Load the colors of the cars
     */
    private void loadColors() {
        colors.put("A", "#81F781");
        colors.put("B", "#FE642E");
        colors.put("C", "#0101DF");
        colors.put("D", "#FF00FF");
        colors.put("E", "#AC58FA");
        colors.put("F", "#0B610B");
        colors.put("G", "#A4A4A4");
        colors.put("H", "#F5D0A9");
        colors.put("I", "#F3F781");
        colors.put("J", "#8A4B08");
        colors.put("K", "#0B6121");
        colors.put("L", "#F59900");
        colors.put("O", "#FFFF00");
        colors.put("P", "#DA81F5");
        colors.put("Q", "#58ACFA");
        colors.put("R", "#088A08");
        colors.put("S", "#000000");
        colors.put("X", "#DF0101");
        colors.put("empty", "FFFFFF");
    }
}
