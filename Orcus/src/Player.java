import javax.swing.*;

@SuppressWarnings("LeakingThisInConstructor")
public final class Player extends JLabel{
    //Gui access variable
    private final GUI screen;

    //Stat variables
    private int maxHp = 100;
    private int moveSpd = 15;
    private int jumpStrength = 25;
    private int dodgeStrength = 15;
    private double damageMult = 1.0;
    private int immunityDuration = 35;
    private int blockRate = 0;
    private int damage = 0;

    //stats changing in-game
    private boolean petrified = false;
    private int petrifiedCount = 0;
    private int hp = 100;
    private int x;
    private int y;
    private double yVelocity;

    //ImageIcons setup
    private final ImageIcon idleRightIcon = Main.scaleImageIcon(new ImageIcon("img/Gladiator/IdleRight.gif"));
    private final ImageIcon runningRightIcon = Main.scaleImageIcon(new ImageIcon("img/Gladiator/RunningRight.gif"));
    private final ImageIcon jumpingRightIcon = Main.scaleImageIcon(new ImageIcon("img/Gladiator/JumpingRight.png"));
    private final ImageIcon dodgingRightIcon =Main.scaleImageIcon(new ImageIcon("img/Gladiator/DodgingRight.png"));
    private final ImageIcon blockingRightIcon =Main.scaleImageIcon(new ImageIcon("img/Gladiator/BlockingRight.png"));
    private final ImageIcon attackingRightIcon =Main.scaleImageIcon(new ImageIcon("img/Gladiator/AttackingRight.gif"));
    private final ImageIcon petrifiedRightIcon =Main.scaleImageIcon(new ImageIcon("img/Gladiator/PetrifiedRight.png"));
    private final ImageIcon idleLeftIcon = Main.scaleImageIcon(new ImageIcon("img/Gladiator/IdleLeft.gif"));
    private final ImageIcon runningLeftIcon = Main.scaleImageIcon(new ImageIcon("img/Gladiator/RunningLeft.gif"));
    private final ImageIcon jumpingLeftIcon = Main.scaleImageIcon(new ImageIcon("img/Gladiator/JumpingLeft.png"));
    private final ImageIcon dodgingLeftIcon = Main.scaleImageIcon(new ImageIcon("img/Gladiator/DodgingLeft.png"));
    private final ImageIcon blockingLeftIcon =Main.scaleImageIcon(new ImageIcon("img/Gladiator/BlockingLeft.png"));
    private final ImageIcon attackingLeftIcon =Main.scaleImageIcon(new ImageIcon("img/Gladiator/AttackingLeft.gif"));
    private final ImageIcon petrifiedLeftIcon =Main.scaleImageIcon(new ImageIcon("img/Gladiator/PetrifiedLeft.png"));

    //inputs and thier actions
    private int jumpFromY;
    private boolean jumping;
    private final int terminalVelocity = 25;
    private final int jumpHeight = 10;
    private boolean wPressed = false;
    private boolean aPressed = false;
    private boolean dPressed = false;
    private boolean spacePressed = false;
    private boolean leftClickPressed = false;
    private boolean rightClickPressed = false;
    private boolean lastMovingLeft = false;
    private boolean onGround;
    private boolean againstWallLeft;
    private boolean againstWallRight;
    private boolean dodging = false;
    private int spaceCount = 0;
    private int damageCount = 0;
    private boolean blocking = false;
    private int blockCooldown = 0;
    private boolean attacking = false;
    private int attackCooldown = 0;


    //Constructor
    public Player(GUI screen) {
        //grants access to the GUI, sets location, and beggining icon
        this.screen = screen;
        x = (int)(200*Main.getMagnification());
        y = (int)(450*Main.getMagnification());
        setIcon(idleRightIcon);
        setBounds(x, y, getIcon().getIconWidth(), getIcon().getIconHeight());
        screen.layers.add(this, JLayeredPane.MODAL_LAYER);
    }


    //Private Methods
    //checks for collision and sets respective variables likewise
    private void collisionCheck(){
        //Checks if this object is hitting any Roof objects
        for (int i = 1; i<=Roof.getNoOfRoofs(); i++) {
            Roof roof = Roof.getWithId(i);
            if (roof != null){
                if (this.getBounds().intersects(roof.getBounds())){
                    //sets velocity to zero if hiting a roof
                    y-=yVelocity;
                    yVelocity = 0;
                }
            }
        }

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
        onGround = counter > 0; //sets onGround variable to true if the player is on any instances of Ground
        if(onGround){
            yVelocity=0; //sets velocity to zero if on the Ground
        }

        //Checks if this object is against any LeftWall objects
        counter = 0;
        for (int i = 1; i<=LeftWall.getNoOfLeftWalls(); i++) {
            LeftWall leftWall = LeftWall.getWithId(i);
            if (leftWall != null){
                if (this.getBounds().intersects(leftWall.getBounds())){
                    counter ++;
                }
            }
        }
        againstWallLeft = counter > 0; //sets againstWallLeft variable to true if the player is against any instances of LeftWall

        //Checks if this object is against any RightWall objects
        counter = 0;
        for (int i = 1; i<=RightWall.getNoOfRightWalls(); i++) {
            RightWall rightWall = RightWall.getWithId(i);
            if (rightWall != null){
                if (this.getBounds().intersects(rightWall.getBounds())){
                    counter ++;
                }
            }
        }
        againstWallRight = counter > 0; //sets againstWallRight variable to true if the player is against any instances of RightWall

        damageCount++; //increases damageCount variable used for immunity
        //Checks if this object is touching any enemies
        for (int i = 1; i<=Enemy.getNoOfEnemies(); i++) {
            Enemy enemy = Enemy.getWithId(i);
            if (enemy != null){
                if (this.getBounds().intersects(enemy.getBounds())&& !dodging && enemy.getHp()>0){ //checks if the two are intersecting while the enemy is alive and the player isnt doging
                    //Petrifies the player if the enemy is a Gorgon and isnt on cooldown
                    if (enemy instanceof Gorgon && petrifiedCount < 100){
                        petrified = true;
                    }

                    if (damageCount > immunityDuration && !attacking){ //checks if the player is not immune and not attacking
                        damageCount = 0; //grants immunity
                        if (!blocking || (int)(Math.random()*100+1)>blockRate){ //ensures the player isnt blocking; if they are the block change is decided
                            //reduces player hp and sets healthbar to correct size; finishs run if the player dies, deleting all enemies.
                            hp -= enemy.getDamage(); 
                            if (hp<= 0){
                                screen.healthbar.setSize(0, (int)(20*Main.getMagnification()));
                                Enemy.deleteAll();
                                hp = maxHp;
                                screen.runFinished();
                            }
                            else {                        
                                screen.healthbar.setSize(((int)(1960*Main.getMagnification()))* (hp)/maxHp, (int)(20*Main.getMagnification()));
                            }
                        }
                        else if (blocking){
                            blockCooldown = 1;
                        }
                    }
                    //If the player is attacking, immunity is granted and attacking is set on cooldown, enemy hp is reducted
                    else if (attacking){
                        damageCount = 0;
                        attackCooldown = 21;
                        enemy.setHp((int)(enemy.getHp()-(damage*damageMult)));
                    }
                }
            }
        }

        //checks if the screen should scrool and does so if needed
        screen.scrollingLeft = this.getBounds().intersects(screen.leftScroller.getBounds()) && !againstWallRight && dPressed && !blocking && !petrified;
        screen.scrollingRight = this.getBounds().intersects(screen.rightScroller.getBounds()) && !againstWallLeft && aPressed && !blocking && !petrified;
    }


    //Public Methods
    //Movement script to be run every frame
    public void playerFrame(){
        collisionCheck();
        //changes the prtified, block, and attack cooldown counters
        if (petrified  || (petrifiedCount <= 100 && petrifiedCount >= 50)){
            petrifiedCount++;
            if (petrifiedCount >= 50) {
                petrified = false;
            }
            if (petrifiedCount >= 100) {
                petrifiedCount = 0;
            }
        }
        if (blockCooldown > 0 && blockCooldown <= 25){
            blockCooldown++;
        }
        else{
            blockCooldown = 0;
        }
        if (attackCooldown > 0 && attackCooldown <= 30){
            attackCooldown++;
        }
        else if (attackCooldown == 0 && leftClickPressed){
            attackCooldown = 1;
        }
        else{
            attackCooldown = 0;
        }
        //checks if all requirements to block or attack are met, and does so if requested
        blocking = rightClickPressed && blockCooldown==0 && !petrified && blockRate > 0 && !attacking;
        attacking = attackCooldown>0 && attackCooldown<=20 && !petrified && damage > 0 && !blocking;

        //falls down in an elipse, creating a gravity effect, by using quadratic relations
        if (!jumping && !onGround && (!dodging|| dodging && blocking)) {
            yVelocity+=(1.5*Main.getMagnification());
            if ((int)(yVelocity*Main.getMagnification()) > (int)(terminalVelocity))
                yVelocity = terminalVelocity;
            y+= (int) yVelocity;
        }

        if(!blocking && !petrified){ //chack if the player is allowed to move(isnt blocking or petrified)
            //moves along the y-axis by the player's jumpstrength if the player is jumping
            if (jumping){
                y -= (int)(jumpStrength*Main.getMagnification());
                if (y <= jumpFromY - (int)(jumpHeight*Main.getMagnification())) {
                    jumping = false;
                    yVelocity = (jumpStrength*Main.getMagnification()) * -1;
                }
            }
            //movement for if w is pressed; activates the player's jumping and moves upwards long the y-axis if dodoging
            if (wPressed){
                if (onGround){
                    onGround = false;
                    jumping = true;
                    jumpFromY = y;
                }
                if (dodging){
                    yVelocity = (dodgeStrength*Main.getMagnification()) * -1;
                    y -= (int)(dodgeStrength*Main.getMagnification());
                }
            }
            //ensures the player does not change on the y-axis if they are dodging and not pressing w
            else if (dodging){
                yVelocity = 0;
            }
            //movement for if a/← are pressed
            if (aPressed && !againstWallLeft && !screen.scrollingRight){ // ensures the player isnt against a left wall and isnt supposed to be scrolling left
                //moves 3* extra if the player is dodging
                if (dodging)
                    x -= 3*(int)(moveSpd*Main.getMagnification());
                
                x -= (int)(moveSpd*Main.getMagnification());
                //sets lastMovingLeft variable(used for icon selection) to true, as long as d/→ aren't pressed
                if(!dPressed){ 
                    lastMovingLeft = true;
                }
            }
            //movement for if d/→ are pressed
            if (dPressed && !againstWallRight && !screen.scrollingLeft){// ensures the player isnt against a right wall and isnt supposed to be scrolling right
                //moves 3* extra if the player is dodging
                if (dodging)
                    x += 3*(int)(moveSpd*Main.getMagnification());
                //moves right by the player's move speed
                x += (int)(moveSpd*Main.getMagnification());
                //sets lastMovingLeft variable(used for icon selection) to false, as long as a/← aren't pressed
                if(!aPressed){
                    lastMovingLeft = false;
                }
            }
            //movement for is space is pressed; ensures dodging isnt on cooldown and intiates dodge if it isnt
            if (spacePressed) {
                if (spaceCount == 0){
                    dodging = true;
                }
                spaceCount++;
                if (spaceCount >=10){
                    dodging = false;
                }
                if (spaceCount >= 100){
                    spaceCount = 0;
                    spacePressed = false;
                }
            }
        }

        //Sets icon depending on the player's current action(s)
        if (petrified){
            if(lastMovingLeft){
                setIcon(petrifiedLeftIcon);
            }
            else{
            setIcon(petrifiedRightIcon);
            }
        }
        else if (blocking){
            if(lastMovingLeft){
                setIcon(blockingLeftIcon);
            }
            else{
            setIcon(blockingRightIcon);
            }
        }
        else if (attacking){
            if(lastMovingLeft){
                setIcon(attackingLeftIcon);
            }
            else{
            setIcon(attackingRightIcon);
            }
        }
        else if (dodging){
            if(lastMovingLeft){
                setIcon(dodgingLeftIcon);
            }
            else{
            setIcon(dodgingRightIcon);
            }
        }
        else if (!onGround){
            if(lastMovingLeft){
                setIcon(jumpingLeftIcon);
            }
            else{
            setIcon(jumpingRightIcon);
            }
        }
        else if(dPressed && !aPressed){
            setIcon(runningRightIcon);
        }
        else if (!dPressed && aPressed) {
            setIcon(runningLeftIcon);
        }
        else {
            if(lastMovingLeft){
                setIcon(idleLeftIcon);
            }
            else{
            setIcon(idleRightIcon);
            }
        }

        //sets player's location and hitbox size depending on movement and icon changes on this frame
        setBounds(x, y, getIcon().getIconWidth(), getIcon().getIconHeight());
    }

    public void reset(){
        //resets player location and all actions to their defaults
        lastMovingLeft = false;
        x = (int)(200*Main.getMagnification());
        y = (int)(450*Main.getMagnification());
        jumping = false;
        dodging = false;
        petrified = false;
        blocking = false;
        attacking = false;
        petrifiedCount = 0;
        blockCooldown = 0;
        attackCooldown = 0;
        spaceCount = 0;
        yVelocity = 0;
        damageCount = 0;
        setBounds(x, y, getIcon().getIconWidth(), getIcon().getIconHeight());
    }
    //Getter Methods
    public boolean isDodging(){
        return dodging;
    }
    public int getMaxHp(){
        return maxHp;
    }
    public int getHp(){
        return hp;
    }
    public int getMoveSpd(){
        return moveSpd;
    }
    public int getJumpStrength(){
        return jumpStrength;
    }
    public int getDodgeStrength(){
        return dodgeStrength;
    }
    public double getDamageMult(){
        return damageMult;
    }
    public int getImmunityDuration(){
        return immunityDuration;
    }
    public int getBlockRate(){
        return blockRate;
    }
    public int getDamage(){
        return damage;
    }
    //Setter Methods
    public void setWPressed(boolean wPressed){
        this.wPressed = wPressed;
    }
    public void setAPressed(boolean aPressed){
        this.aPressed = aPressed;
    }
    public void setDPressed(boolean dPressed){
        this.dPressed = dPressed;
    }
    public void setSpacePressed(boolean spacePressed){
        this.spacePressed = spacePressed;
    }
    public void setLeftClickPressed(boolean leftClickPressed){
        this.leftClickPressed = leftClickPressed;
    }
    public void setRightClickPressed(boolean rightClickPressed){
        this.rightClickPressed = rightClickPressed;
    }
    public void setHp(int hp){
        //sets the hp and also corrects the healthbar
        this.hp = hp;
        screen.healthbar.setSize(((int)(1960*Main.getMagnification()))* (hp)/maxHp, (int)(20*Main.getMagnification())); 
    }
    public void setMaxHP(int maxHp){
        this.maxHp = maxHp;
    }
    public void setMoveSpd(int moveSpd){
        this.moveSpd = moveSpd;
    }
    public void setJumpStrength(int jumpStrength){
        this.jumpStrength = jumpStrength;
    }
    public void setDodgeStrength(int dodgeStrength){
        this.dodgeStrength = dodgeStrength;
    }
    public void setDamageMult(double damageMult){
        this.damageMult = damageMult;
    }
    public void setImmunityDuration(int immunityDuration){
        this.immunityDuration = immunityDuration;
    }
    public void setBlockRate(int blockRate){
        this.blockRate = blockRate;
    }
    public void setDamage(int damage){
        this.damage = damage;
    }
}