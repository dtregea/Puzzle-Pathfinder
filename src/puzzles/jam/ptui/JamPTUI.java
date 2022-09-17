package puzzles.jam.ptui;
import puzzles.common.Observer;
import puzzles.jam.model.JamClientData;
import puzzles.jam.model.JamModel;
import java.io.FileNotFoundException;
import java.util.Scanner;

/**
 * Plain Text UI for Jam.
 * @author Daniel Tregea
 */
public class JamPTUI implements Observer<JamModel, JamClientData> {
    private final JamModel model;

    public JamPTUI(String filename) throws FileNotFoundException {
        model = new JamModel(filename);
        model.addObserver(this);
    }

    @Override
    public void update(JamModel jamModel, JamClientData jamClientData) {
        System.out.println(model);
        System.out.println(jamClientData);
    }

    public void run(){
        System.out.println("---- Car Jam ----\n");
        Scanner scan = new Scanner(System.in);
        while(!model.isGameEnd()){
            System.out.println("""
                    h(int) -- get next move
                    l(oad) filename.txt -- load jam file (add .txt)
                    s(elect), car, row, column -- move a car
                    q(uit) -- end the program
                    r(eset) -- reset the current game""");
            String input = scan.nextLine();
            if(model.isValidMove(input)){
                model.processMove(input);
            } else{
                System.out.println("Invalid Move!");
            }
        }
    }

    public static void main(String[] args) throws FileNotFoundException {
        if (args.length != 1) {
            System.out.println("Provide a file in the data directory as an argument");
        } else{
            JamPTUI ptui = new JamPTUI(args[0]);
            ptui.update(ptui.model, new JamClientData("Loaded: " + args[0]));
            ptui.run();
        }
    }
}
