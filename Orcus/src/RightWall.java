import java.util.ArrayList;
import java.util.List;
import javax.swing.*;

@SuppressWarnings("LeakingThisInConstructor")
public final class RightWall extends JLabel implements Terrain{
    //Final Variables
    private final int originalX;
    private final int originalY;
    private final int id;
    //Static Variables
    private final static List<RightWall> rightWallList = new ArrayList<>();
    private static int noOfRightWalls = 0;
    

    //Constructor
    public RightWall(int x, int y, int width, int height, GUI screen){
        super(); //Creates JLabel
        //Sets vars to their required values
        originalX = x;
        originalY = y;
        noOfRightWalls++;
        id = noOfRightWalls;

        //Sets bounds and adds to GUI and rightWallList
        if(width == 1){
            setBounds((int)(x*Main.getMagnification()), (int)(y*Main.getMagnification()), width, (int)(height*Main.getMagnification()));
        }
        else{
            setBounds((int)(x*Main.getMagnification()), (int)(y*Main.getMagnification()), (int)(width*Main.getMagnification()), (int)(height*Main.getMagnification()));
        }
        screen.layers.add(this, JLayeredPane.PALETTE_LAYER);
        rightWallList.add(this);
    }


    //Public Methods
    @Override
    public void reset(){
        //Returns object to original location
        setLocation((int)(originalX*Main.getMagnification()),(int)(originalY*Main.getMagnification())); 
    }
    //Getter Methods
    @Override
    public int getId(){
        return id;
    }
    //Static Methods
    public static int getNoOfRightWalls(){
        return noOfRightWalls;
    }
    public static RightWall getWithId(int id){
        //Goes through each instance of RightWall until the one of the requested id is found
        for (RightWall rtw : rightWallList) {
            if (rtw.getId() == id) {
                return rtw;
            }
        }
        return null;
    }
}