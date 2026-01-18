import java.util.ArrayList;
import java.util.List;
import javax.swing.*;

@SuppressWarnings("LeakingThisInConstructor")
public final class LeftWall extends JLabel implements Terrain{
    //Final Variables
    private final int originalX;
    private final int originalY;
    private final int id;
    //Static Methods
    private final static List<LeftWall> leftWallList = new ArrayList<>();
    private static int noOfLeftWalls = 0;
    

    //Constructor
    public LeftWall(int x, int y, int width, int height, GUI screen){
        super(); //Creates JLabel
        //Sets vars to their required values
        originalX = x;
        originalY = y;
        noOfLeftWalls++;
        id = noOfLeftWalls;

        //Sets bounds and adds to GUI and rightWallList
        if(width == 1){
            setBounds((int)(x*Main.getMagnification()), (int)(y*Main.getMagnification()), width, (int)(height*Main.getMagnification()));
        }
        else{
            setBounds((int)(x*Main.getMagnification()), (int)(y*Main.getMagnification()), (int)(width*Main.getMagnification()), (int)(height*Main.getMagnification()));
        }
        screen.layers.add(this, JLayeredPane.PALETTE_LAYER);
        leftWallList.add(this);
    }


    //Public Methods
    @Override
    public void reset(){
        //Returns object to original location
        setLocation((int)(originalX*Main.getMagnification()),(int)(originalY*Main.getMagnification())); //Returns object to original location
    }
    //Getter Methods
    @Override
    public int getId(){
        return id;
    }
    //Static Methods
    public static int getNoOfLeftWalls(){
        return noOfLeftWalls;
    }
    public static LeftWall getWithId(int id){
        //Goes through each instance of LeftWall until the one of the requested id is found
        for (LeftWall lfw : leftWallList) {
            if (lfw.getId() == id) {
                return lfw;
            }
        }
        return null;
    }
}