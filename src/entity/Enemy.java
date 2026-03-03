package entity;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

import main.GamePanel;

public abstract class Enemy extends Entity {

    protected GamePanel gp;

    public boolean alive = true;

    protected int invulCounter = 0;
    protected int invulTime = 12;

    protected BufferedImage sprite1;
    protected BufferedImage sprite2;

    public Enemy(GamePanel gp, int worldX, int worldY) {

        this.gp = gp;
        this.worldX = worldX;
        this.worldY = worldY;

        direction = "left";
        speed = 2;

        // Puoi cambiare questi valori per ogni nemico
        gravity = 0.5;
        terminalVel = 12.0;
        velocityY = 0.0;
        onGround = false;

        solidArea = new Rectangle(8, 16, 32, 32);
        solidAreaDefaultX = solidArea.x;
        solidAreaDefaultY = solidArea.y;
    }

    public void update() {

        if (alive == false) {
            return;
        }

        if (invulCounter > 0) {
            invulCounter = invulCounter - 1;
        }

        updateAI();

        moveHorizontal();

        // stessa pipeline del Player
        velocityY = velocityY + gravity;

        if (velocityY > terminalVel) {
            velocityY = terminalVel;
        }

        gp.cChecker.applyGravityAndCollisionsEntity(this);

        // animazione base 2 frame
        spriteCounter = spriteCounter + 1;
        if (spriteCounter > 12) {
            if (spriteNum == 1) {
                spriteNum = 2;
            } else {
                spriteNum = 1;
            }
            spriteCounter = 0;
        }
    }

    protected abstract void updateAI();

    protected void moveHorizontal() {

        int dx = 0;

        if (direction.equals("left")) {
            dx = -speed;
        } else if (direction.equals("right")) {
            dx = speed;
        }

        if (dx == 0) {
            return;
        }

        boolean hit = gp.cChecker.willCollide(this, dx, 0);

        if (hit == true) {

            if (direction.equals("left")) {
                direction = "right";
            } else {
                direction = "left";
            }

        } else {
            worldX = worldX + dx;
        }
    }

    public void takeDamage(int dmg) {

        if (alive == false) {
            return;
        }

        if (invulCounter > 0) {
            return;
        }

        life = life - dmg;
        invulCounter = invulTime;

        if (life <= 0) {
            alive = false;
        }
    }

    public void draw(Graphics2D g2) {

        if (alive == false) {
            return;
        }

        int sx = worldX - gp.cameraX;
        int sy = worldY - gp.cameraY;
        
        int drawX = sx;
        int drawY = sy - gp.tileSize;

        BufferedImage img = null;

        if (spriteNum == 1) {
            img = sprite1;
        } else {
            img = sprite2;
        }
        
        //DEBUG
        g2.setColor(java.awt.Color.RED);
        g2.drawRect(
            (worldX - gp.cameraX) + solidArea.x,
            (worldY - gp.cameraY) + solidArea.y,
            solidArea.width,
            solidArea.height
        );
        
        if (img != null) {
            g2.drawImage(img, drawX, drawY, gp.tileSize, gp.tileSize, null);
        } else {
            g2.setColor(Color.GREEN);
            g2.fillRect(sx, sy, gp.tileSize, gp.tileSize);
        }
        
       
        
    }
}