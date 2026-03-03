
package entity;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;

public class Entity {

    public int worldX, worldY;
    public int speed;
    public String direction = "right";
    
    public int life = 3;

    // Animazione
    public int spriteCounter = 0;
    public int spriteNum = 1;

    // Collisioni
    public Rectangle solidArea = new Rectangle(0,0,48,48);
    
    public int solidAreaDefaultX = 0;
    public int solidAreaDefaultY = 0;
    public boolean collisionOn = false;

    // Sprite direzionali
    public BufferedImage left1,left2,left3,left4;
    public BufferedImage right1,right2,right3,right4;
    public BufferedImage climb1, climb2;
    
    public boolean onGround = false;
    public double velocityY = 0.0;
    public double gravity = 0.5;
    public double terminalVel = 12.0;
}
