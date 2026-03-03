
package entity;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;

import main.GamePanel;
import main.KeyHandler;

public class Player extends Entity {

    public final int screenX;
    public final int screenY;

    GamePanel gp;
    KeyHandler keyH;

    //Gravità e salto
    
    
    public double jumpStrength = -12.0;
    
    
    
    //Scale
    public boolean onLadder = false;
    public boolean climbing = false;
    public int climbSpeed = 6; 
    
    //Attacco
    private boolean attacking = false;
    private int attackCounter = 0;
    private int attackCooldown = 0;

    private BufferedImage rightAttack1, rightAttack2;
    private BufferedImage leftAttack1, leftAttack2;

    public Player(GamePanel gp, KeyHandler keyH) {
        this.gp = gp;
        this.keyH = keyH;

        screenX = gp.screenWidth / 2 - (gp.tileSize / 2);
        screenY = gp.screenHeight / 2 - (gp.tileSize / 2);

        solidArea = new Rectangle(8, 16, 32, 32);
        solidAreaDefaultX = solidArea.x;
        solidAreaDefaultY = solidArea.y;

        setDefaultValues();
        getPlayerImage();
    }

    public void setDefaultValues() {
        worldX = gp.tileSize;
        worldY = gp.tileSize *11;
        speed = 6;
        direction = "right";
        onGround = false;
        velocityY = 0.0;
    }

    public void getPlayerImage() {
        try {
            left1 = ImageIO.read(getClass().getResource("/player/left1.png"));
            left2 = ImageIO.read(getClass().getResource("/player/left2.png"));
            left3 = ImageIO.read(getClass().getResource("/player/left3.png"));
            left4 = ImageIO.read(getClass().getResource("/player/left4.png"));

            right1 = ImageIO.read(getClass().getResource("/player/right1.png"));
            right2 = ImageIO.read(getClass().getResource("/player/right2.png"));
            right3 = ImageIO.read(getClass().getResource("/player/right3.png"));
            right4 = ImageIO.read(getClass().getResource("/player/right4.png"));

 
            
            climb1 = ImageIO.read(getClass().getResource("/player/upp1.png"));
            climb2 = ImageIO.read(getClass().getResource("/player/upp2.png"));
            
            rightAttack1 = ImageIO.read(getClass().getResource("/player/right_attack1.png"));
            rightAttack2 = ImageIO.read(getClass().getResource("/player/right_attack2.png"));

            leftAttack1  = ImageIO.read(getClass().getResource("/player/left_attack1.png"));
            leftAttack2  = ImageIO.read(getClass().getResource("/player/left_attack2.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    

    public void update() {
    	
    	if (attackCooldown > 0) {
    	    attackCooldown = attackCooldown - 1;
    	}

    	if (attacking == false) {
    	    if (attackCooldown == 0) {
    	        if (keyH.attackPress == true) {
    	            // se vuoi evitare attacco mentre scali:
    	            if (climbing == false) {
    	                startAttack(); // qui dentro spawni il proiettile 1 sola volta
    	            }
    	        }
    	    }
    	}

    	if (attacking == true) {
    	    attackCounter = attackCounter + 1;

    	    // qui NON fare return; così continui a cadere/saltare
    	    if(attackCounter <6) {
    	    	spriteNum = 1;
    	    }else {
    	    	spriteNum = 2;
    	    }
    	    if (attackCounter >= 12) {
    	        attacking = false;
    	        attackCounter = 0;
    	        spriteNum = 1;
    	    }
    	}

        
        
        // =============== INPUT ORIZZONTALE ===============
        boolean movingHoriz = false;
        int oldWorldX = worldX;
        
        /*if (attackCooldown > 0) attackCooldown--;

        if (!attacking && attackCooldown == 0 && keyH.attackPress && !climbing) {
            startAttack();
        }*/
        
        if (keyH.leftPress) {
            direction = "left";
            collisionOn = false;
            gp.cChecker.checkTile(this); // collisione orizzontale
            if (!collisionOn) {
                worldX -= speed;
            }
            movingHoriz = true;
        } 
        else if (keyH.rightPress) {
            direction = "right";
            collisionOn = false;
            gp.cChecker.checkTile(this);
            if (!collisionOn) {
                worldX += speed;
            }
            movingHoriz = true;
        }
        

       
     // =============== SCALE ===============

        // 1) Controllo se sono dentro una scala e se sotto di me c'è una scala
        onLadder = gp.cChecker.isOnLadder(this);
        boolean ladderBelow = gp.cChecker.isLadderBelow(this);

        // 2) Decide quando ENTRARE in modalità scalata
        if (!climbing) {

            // CASO A: sono già "dentro" la scala e premo SU → inizio a salire
            if (onLadder && keyH.upPress&& onGround) {
                climbing = true;
                onGround = false;
                velocityY = 0;
            }

           

            // CASO C: sono SUL PAVIMENTO, sopra una scala (ladderBelow = true),
            // premo GIÙ → mi "aggancio" e inizio a scendere
            else if (!onLadder && ladderBelow && keyH.downPress && onGround) {
                climbing = true;
                onGround = false;
                velocityY = 0;

                
            }
        }

        // Se non sono più su una scala e non ho scala sotto → esco dalla scalata
        if (!onLadder && !ladderBelow) {
            climbing = false;
            
        }

        // 3) Movimento VERTICALE mentre sto scalando
        /*if (climbing) {
            // niente gravità mentre scalo
            velocityY = 0;
            onGround = false;

            if (keyH.upPress) {
                worldY -= climbSpeed;
            } 
            else if (keyH.downPress) {
            	// se sotto NON c'è più scala → esco dalla scalata
                if (!gp.cChecker.isLadderBelow(this)) {
                    climbing = false;

                    // mi assicuro che la gravità mi "appoggi" sul pavimento
                    onGround = false;
                    velocityY = 1;   // piccola spinta verso il basso
                } 
                else {
                    // continuo a scendere sulla scala
                    worldY += climbSpeed;
                    
                    if(gp.cChecker.willCollideVerticalWithStairs(this,1)) {
                    	climbing = false;
                    	gp.cChecker.snapToGround(this);
                    }
                }
            }

            // ANIMAZIONE SCALATA (spriteNum = 1 o 2)
            if (keyH.upPress || keyH.downPress) {
                spriteCounter++;
                if (spriteCounter > 10) {
                    if (spriteNum == 1) {
                        spriteNum = 2;
                    } else {
                        spriteNum = 1;
                    }
                    spriteCounter = 0;
                }
            } else {
                // se fermo sulla scala, tengo la prima posa
                spriteNum = 1;
                spriteCounter = 0;
            }
        
            
            // =============== ANIMAZIONE ===============
            if (attacking == false) {

            	if (movingHoriz == true || oldWorldX != worldX) {
            		spriteCounter = spriteCounter + 1;

            		if (spriteCounter > 10) {
            			spriteNum = (spriteNum % 4) + 1;
            			spriteCounter = 0;
            		}
            	}
            	else {
            		spriteNum = 1;
            		spriteCounter = 0;
            	}
            }
            return;
        }*/
        if (climbing) {

            velocityY = 0;
            onGround = false;

            if (keyH.upPress) {
                worldY = worldY - climbSpeed;
            }
            else if (keyH.downPress) {

                if (gp.cChecker.isLadderBelow(this) == false) {
                    climbing = false;
                    onGround = false;
                    velocityY = 1;
                }
                else {
                    worldY = worldY + climbSpeed;

                    if (gp.cChecker.willCollideVerticalWithStairs(this, 1) == true) {
                        climbing = false;
                        gp.cChecker.snapToGround(this);
                    }
                }
            }

            // animazione scalata
            if (keyH.upPress == true || keyH.downPress == true) {
                spriteCounter = spriteCounter + 1;

                if (spriteCounter > 10) {
                    if (spriteNum == 1) {
                        spriteNum = 2;
                    }
                    else {
                        spriteNum = 1;
                    }
                    spriteCounter = 0;
                }
            }
            else {
                spriteNum = 1;
                spriteCounter = 0;
            }

            return;
        }
        //Salto se NON scalo
        if (keyH.upPress && onGround) {
            velocityY = jumpStrength;
            onGround = false;
        }

        // =============== GRAVITÀ + COLLISIONE VERTICALE ===============
        velocityY += gravity;
        if (velocityY > terminalVel) velocityY = terminalVel;

        gp.cChecker.applyGravityAndCollisions(this);

        // =============== ANIMAZIONE ===============
        /*if (movingHoriz || oldWorldX != worldX) {
            spriteCounter++;
            if (spriteCounter > 10) {
                spriteNum = (spriteNum % 4) + 1;
                spriteCounter = 0;
            }
        } else {
            spriteNum = 1;
            spriteCounter = 0;
        }*/
     // =============== ANIMAZIONE ===============
        if (attacking == false) {

            if (movingHoriz == true || oldWorldX != worldX) {
                spriteCounter = spriteCounter + 1;

                if (spriteCounter > 10) {
                    spriteNum = (spriteNum % 4) + 1;
                    spriteCounter = 0;
                }
            }
            else {
                spriteNum = 1;
                spriteCounter = 0;
            }
        }
    }

    public void draw(Graphics2D g2) {
        BufferedImage image = null;
        if (attacking) {
        	if ("right".equals(direction)) {

        	    if (spriteNum == 1) {
        	        image = rightAttack1;
        	    } else {
        	        image = rightAttack2;
        	    }

        	} else {

        	    if (spriteNum == 1) {
        	        image = leftAttack1;
        	    } else {
        	        image = leftAttack2;
        	    }
        	}
        }else if (climbing) {//Sulla scala
            if (spriteNum == 1) {
                image = climb1;
            } else {
                image = climb2;
            }
        } else {
            //Direzioni normali
            if ("left".equals(direction)) {
                if (spriteNum == 1) {
                    image = left1;
                } else if (spriteNum == 2) {
                    image = left2;
                } else if (spriteNum == 3) {
                    image = left3;
                } else {
                    image = left4;
                }
            } else if ("right".equals(direction)) {
                if (spriteNum == 1) {
                    image = right1;
                } else if (spriteNum == 2) {
                    image = right2;
                } else if (spriteNum == 3) {
                    image = right3;
                } else {
                    image = right4;
                }
            }
        }

        // Calcolo camera
        int sx = worldX - gp.cameraX;
        int sy = worldY - gp.cameraY;
        

        // settaggio proporzione allo stage
        
        //int drawW = gp.tileSize * 2;
        //int drawH = gp.tileSize * 2;
	
        //int drawX = sx - (drawW - gp.tileSize) / 2;
        //int drawY = sy - (drawH - gp.tileSize);
        
       
        
        int drawX = sx;
        int drawY = sy - (gp.tileSize);
        
        //DEBUG
        g2.setColor(java.awt.Color.BLUE);
        g2.drawRect(
            (worldX - gp.cameraX) + solidArea.x,
            (worldY - gp.cameraY) + solidArea.y,
            solidArea.width,
            solidArea.height
        );

        g2.drawImage(image, drawX, drawY, gp.tileSize, gp.tileSize, null);
        //g2.drawImage(image, sx, sy, gp.tileSize, gp.tileSize, null);
        //System.out.println(solidArea.y + solidArea.height);
    }
    /*private void startAttack() {

    	attacking = true;
        attackCounter = 0;
        attackCooldown = 20;
        int scale = 2;
        int projectileW = gp.tileSize*scale;

        int hitLeft = worldX + solidArea.x;                 // con la tua solidArea: worldX
        int hitRight = worldX + solidArea.x + solidArea.width*2/3; // worldX + 48

        int spawnX = hitLeft-projectileW;
        int spawnY = worldY + solidArea.y + (solidArea.height / 2);

        // altezza: un po' più su della metà (aggiusta a gusto)
        spawnY = spawnY - (gp.tileSize*2);  // leggermente più alto di -tileSize

        if (direction.equals("right")) {
            // il lato sinistro della spada coincide col bordo destro della hitbox
            spawnX = hitRight;
        }
        else if (direction.equals("left")) {
            // il lato destro della spada coincide col bordo sinistro della hitbox
            spawnX = hitLeft - projectileW;
        }

        gp.projectiles.add(new Projectile(gp, spawnX, spawnY, direction));
    }*/
    private void startAttack() {

        attacking = true;
        attackCounter = 0;
        attackCooldown = 20;

        int scale = 2;
        int projectileW = gp.tileSize * scale;

        int hitLeft = worldX + solidArea.x;
        int hitRight = worldX + solidArea.x + solidArea.width;

        int spawnX = hitLeft;
        int spawnY = worldY + solidArea.y + (solidArea.height / scale);

        // più in alto (mano)
        spawnY = spawnY - (gp.tileSize * scale);

        if (direction.equals("right")) {
            spawnX = hitRight;
        }
        else if (direction.equals("left")) {
            spawnX = hitLeft - projectileW;
        }

        gp.projectiles.add(new Projectile(gp, spawnX, spawnY, direction));
    }
  
}
