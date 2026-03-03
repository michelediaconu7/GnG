package entity;

import java.io.IOException;
import javax.imageio.ImageIO;

import main.GamePanel;

public class Slime extends Enemy {

    public Slime(GamePanel gp, int worldX, int worldY) {

        super(gp, worldX, worldY);

        life = 1;
        speed = 2;

        loadSprites();
    }

    @Override
    protected void updateAI() {
        // patrol: fa tutto Enemy.moveHorizontal() invertendo quando collide
    }

    private void loadSprites() {
        try {
            sprite1 = ImageIO.read(getClass().getResource("/enemy/zombie_left.png"));
            sprite2 = ImageIO.read(getClass().getResource("/enemy/zombie_right.png"));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            // fallback: rettangolo verde
        }
    }
    
    
}