import java.util.ArrayList;
import java.util.List;
import javax.swing.*;

@SuppressWarnings("LeakingThisInConstructor")
public final class Ground extends JLabel implements Terrain{
    //Final Variables
    private final int originalX;
    private final int originalY;
    private final int id;
    //Static Variables
    private final static List<Ground> groundList = new ArrayList<>();
    private static int noOfGrounds = 0;


    //Constructor
    public Ground(int x, int y, int width, int height, GUI screen){
        super(); //Creates JLabel
        //Sets vars to their required values
        originalX = x;
        originalY = y;
        noOfGrounds++;
        id = noOfGrounds;
        
        //Sets bounds and adds to GUI and rightWallList
        if(height == 1){
            setBounds((int)(x*Main.getMagnification()), (int)(y*Main.getMagnification()), (int)(width*Main.getMagnification()), height);
        }
        else{
            setBounds((int)(x*Main.getMagnification()), (int)(y*Main.getMagnification()), (int)(width*Main.getMagnification()), (int)(height*Main.getMagnification()));
        }
        screen.layers.add(this, JLayeredPane.PALETTE_LAYER);
        groundList.add(this);
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
    public static int getNoOfGrounds(){
        return noOfGrounds;
    }
    public static Ground getWithId(int id){
        //Goes through each instance of Ground until the one of the requested id is found
        for (Ground grd : groundList) {
            if (grd.getId() == id) {
                return grd;
            }
        }
        return null;
    }
}