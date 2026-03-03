package entity;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;
import main.GamePanel;

public class Projectile extends Entity{
	private GamePanel gp;
	public boolean alive = true;
	private int lifeTime = 50;
	private BufferedImage sword;
	
	
	public Projectile(GamePanel gp, int startX, int startY, String dir) {
        this.gp = gp;

        worldX = startX;
        worldY = startY;
        direction = dir;
        
        speed = 12;
        
        int scale = 2;
        int size = this.gp.tileSize * scale;
        
        int hitWidth = 32;      // lascia un po' di margine laterale
        int hitHeight = 16;            // lama sottile

        int hitX = (size - hitWidth) / 2;
        int hitY = (size) - (hitHeight /2 );
        
        solidArea = new Rectangle(hitX, hitY, hitWidth, hitHeight); // hitbox piccola
        //solidArea = new Rectangle(0, 0, size, size);
        
        solidAreaDefaultX = solidArea.x;
        solidAreaDefaultY = solidArea.y;

        loadImage();
    }
	
	private void loadImage() {
        try {
            sword = ImageIO.read(getClass().getResource("/player/sword.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
	public void update() {

	    if (alive == true) {

	        int dx = 0;

	        if (direction.equals("right")) {
	            dx = speed;
	        }
	        else if (direction.equals("left")) {
	            dx = -speed;
	        }

	        boolean collision = gp.cChecker.willCollide(this, dx, 0);

	        if (collision == true) {
	            alive = false;
	        }
	        else {
	            worldX = worldX + dx;

	            lifeTime = lifeTime - 1;

	            if (lifeTime <= 0) {
	                alive = false;
	            }
	        }
	    }
	}
	
	public void draw(Graphics2D g2) {

		int scale = 2;
	    if (sword == null) {
	        return;
	    }
	   

	    int sx = worldX - gp.cameraX;
	    int sy = worldY - gp.cameraY;
	    
	    //int drawX = sx;
        //int drawY = sy - (gp.tileSize);
	    //int sx = worldX - gp.player.worldX + gp.player.screenX;
	    //int sy = worldY - gp.player.worldY + gp.player.screenY;
	    
	    //DEBUG
	    g2.setColor(java.awt.Color.YELLOW);
	    g2.drawRect(
	        (worldX - gp.cameraX) + solidArea.x,
	        (worldY - gp.cameraY) + solidArea.y,
	        solidArea.width,
	        solidArea.height
	    );
	    
	    
	    g2.drawImage(sword, sx, sy, gp.tileSize*scale, gp.tileSize*scale, null);
	}
}
	


