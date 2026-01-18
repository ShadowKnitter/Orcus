import java.util.ArrayList;
import java.util.List;
import javax.swing.*;

@SuppressWarnings("LeakingThisInConstructor")
public class Enemy extends JLabel{
    //Final Variables
    private final ImageIcon rightIcon;
    private final ImageIcon leftIcon;
    //Characteristics
    private final boolean originalyMovingLeft;
    private final int originalX;
    private final int originalY;
    private final int damage;
    private final int moveSpd;
    private final int terminalVelocity;
    //Identification
    private final int id;
    //Static Variables
    private final static List<Enemy> enemyList = new ArrayList<>();
    private static int noOfEnemies = 0;
    
    //Movement and Health Variables
    private final int maxHp;
    private int hp;
    private int x;
    private int y;
    private int wallBreak;
    private double yVelocity;
    private boolean onGround;
    private boolean movingLeft;
    

    //Constructor
    public Enemy(int x, int y, ImageIcon rightIcon, ImageIcon leftIcon, int hp, int moveSpd, int damage, int terminalVelocity, boolean movingLeft, GUI screen){
        //increases number of enemies, sets id, and adds to enemyList
        noOfEnemies++;
        id = noOfEnemies;
        enemyList.add(this);

        //sets stat and loction vars
        originalX = x;
        originalY = y;
        this.x = (int)(x*Main.getMagnification());
        this.y = (int)(y*Main.getMagnification());
        this.maxHp = hp;
        this.hp = hp;
        this.moveSpd = moveSpd;
        this.damage = damage;
        this.terminalVelocity = terminalVelocity;
        this.originalyMovingLeft = movingLeft;
        this.movingLeft = movingLeft;

        //sets Icons and bounds, then adds to GUI
        this.rightIcon = Main.scaleImageIcon(rightIcon);
        this.leftIcon = Main.scaleImageIcon(leftIcon);
        setIcon(rightIcon);
        setBounds(x, y, rightIcon.getIconWidth(), rightIcon.getIconHeight());
        screen.layers.add(this, JLayeredPane.MODAL_LAYER);
    }


    //Private Methods
    //moves enemy and checks for collision
    private void moveEnemy(){

        setLocation(x,y);

        //Checks if this object is on any Ground objects
        int counter = 0;
        for (int i = 1; i<=Ground.getNoOfGrounds(); i++) {
            Ground ground = Ground.getWithId(i);
            if (ground != null){
                if (this.getBounds().intersects(ground.getBounds())){
                    counter ++;
                }
            }
        }
        onGround = counter > 0; //if on any Ground objects, onGround is set to true

        //Checks if this object is against any LeftWall objects
        for (int i = 1; i<=LeftWall.getNoOfLeftWalls(); i++) {
            LeftWall leftWall = LeftWall.getWithId(i);
            if (leftWall != null){
                if (this.getBounds().intersects(leftWall.getBounds())){
                    if((int)(Math.random()*100)+1<=20){ //20% chance to break through a wall (excluding walls > 1px wide)
                        wallBreak = 1;
                    }
                    if (leftWall.getWidth()>1 || wallBreak==0){
                        movingLeft = false; //if against a LeftWall object while wallbreak is inactive, enemy starts moving right
                    }
                }
            }
        }

        //Checks if this object is against any RightWall objects
        for (int i = 1; i<=RightWall.getNoOfRightWalls(); i++) {
            RightWall rightWall = RightWall.getWithId(i);
            if (rightWall != null){
                if (this.getBounds().intersects(rightWall.getBounds())){
                    if((int)(Math.random()*100)+1<=20){ //20% chance to break through a wall (excluding walls > 1px wide)
                        wallBreak = 1;
                    }
                    if (rightWall.getWidth()>1 || wallBreak==0){
                        movingLeft = true; //if against a RightWall object while wallbreak is inactive, enemy starts moving left
                    }
                }
            }
        }

        //Wall break effect duration logic, ensuring wall break lasts for 30 frames so the enemy can pass through the wall entirely.
        if (wallBreak>0 && wallBreak < 30){
            wallBreak++;
        }
        else{
            wallBreak = 0;
        }
    }


    //Public Methods
    //runs every frame
    public void enemyFrame(){
        //Sets icon
        if (movingLeft){
            setIcon(leftIcon);
        }
        else{
            setIcon(rightIcon);
        }

        //falls down in an elipse, creating a gravity effect, by using quadratic relations
        if (!onGround && terminalVelocity > 0) {
            yVelocity+=(1.5*Main.getMagnification());
            if ((int)(yVelocity*Main.getMagnification()) > (int)(terminalVelocity*Main.getMagnification()))
                yVelocity = terminalVelocity;
            y+= (int) yVelocity;
        }
        else{
            yVelocity = 0;
        }

        //changes x value depending on which direction is being moved in
        if (movingLeft){
            x -= (int)(moveSpd*Main.getMagnification());
        }
        if (!movingLeft){
            x += (int)(moveSpd*Main.getMagnification());
        }

        //runs visual movement and collision checks;
        moveEnemy();
    }

    //empties enemyList of all objects, makes those objects invisible, and runs garbage collector
    public static void deleteAll(){
        for (int i = 1; Enemy.getNoOfEnemies()>0; i++){
            getWithId(i).setVisible(false);
            noOfEnemies--;
        }
        enemyList.clear();
        System.gc();
    }

    //Returns enemy to original location and direction
    public void reset(){
        x = (int)(originalX*Main.getMagnification());
        y = (int)(originalY*Main.getMagnification());
        hp = maxHp;
        setVisible(true);
        movingLeft = originalyMovingLeft;
        setLocation(x,y);
    }

    //Getter Methods
    public ImageIcon getRightIcon(){
        return rightIcon;
    }
    public ImageIcon getLeftIcon(){
        return leftIcon;
    }
    public int getMoveSpd(){
        return moveSpd;
    }
    public int getDamage(){
        return damage;
    }
    public int getHp(){
        return hp;
    }
    public int getOriginalX(){
        return originalX;
    }
    public int getOriginalY(){
        return originalY;
    }
    public boolean getOriginalyMovingLeft(){
        return originalyMovingLeft;
    }
    public int getId(){
        return id;
    }
    //Static Methods
    public static int getNoOfEnemies(){
        return noOfEnemies;
    }
    public static Enemy getWithId(int id){
        //Goes through each instance of Enemy until the one of the requested id is found; that instance is returned
        for (Enemy enm : enemyList) {
            if (enm.getId() == id) {
                return enm;
            }
        }
        return null;
    }  
    
    //Setter Methods
    public void setX(int x){
        this.x = x;
        setLocation(x,y);
    }
    public void setY(int y){
        this.y = y;
        setLocation(x,y);
    }
    public void setHp(int hp){
        //sets the enemy's hp; if it is below zero, the enemy dies and becomes invisible.
        this.hp = hp;
        if (hp <= 0){
            setVisible(false);
        }
    }
}
