package tile;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.imageio.ImageIO;

import main.GamePanel;

public class TileManager{
	
	GamePanel gp;
    public Tile[] tile;
    public int mapTileNum[][];
    
     
    
   
   

    public TileManager(GamePanel gp) {
        this.gp = gp;
        
        tile = new Tile[695];
        mapTileNum = new int [gp.maxWorldCol][gp.maxWorldRow];
        
        
        getTileImage();
        loadMap();
       
    }
    
    
     public void getTileImage() {
    		try {
    		
    			for (int i = 0; i < tile.length; i++) {
    	            tile[i] = new Tile();
    	            
    	            String path = String.format("/tiles_1616/tile_%03d.png", i);
    	            tile[i].image = ImageIO.read(getClass().getResourceAsStream(path));
    	            
    	           
    			}
    			
    			for(int j = 652; j<694;j++) {
    				tile[j].collision = true;
    			}
    			
    			
    			tile[616].collision = true;
    			tile[618].collision = true;
    			tile[619].collision = true;
    			tile[623].collision = true;
    			tile[389].collision = true;
    			tile[393].collision = true;
    			tile[655].collision = false;
    			tile[654].collision = false;
    			tile[455].collision = false;
    			tile[458].collision = true;
    			for(int i = 451; i<=456;i++) {
    				tile[i].collision = true;
    			}
    			
    			tile[621].stair = true;
    			tile[567].stair = true;
    			tile[533].stair = true;
    			tile[505].stair = true;
    			tile[455].stair = true;
    			//tile[387].stair = true; //Cima scala
    			//tile[395].stair = true;
    			
    			
    			
    			
    			
    			
    			
    			
    			
    			
	
    		}catch(IOException e){
        		e.printStackTrace();
        		
        	}
     }
    	
    
  
    
    
    public boolean isTileSolid(int col, int row) {
        if (col < 0 || row < 0 || col >= gp.maxWorldCol || row >= gp.maxWorldRow) {
            return true; // fuori mappa = solido
        }
        int tileIndex = mapTileNum[col][row];
        return tile[tileIndex].collision;
    }
    
    
    public void loadMap() {
        try {
            InputStream is = getClass().getResourceAsStream("/maps/mapadapt.txt");
            BufferedReader br = new BufferedReader(new InputStreamReader(is));

            int row = 0;

            while (row < gp.maxWorldRow) {
                String line = br.readLine();
                if (line == null) {
                    break; // Evita NullPointerException se il file è più corto
                }

                String numbers[] = line.split(",");

                for (int col = 0; col < gp.maxWorldCol; col++) {
                    int num = Integer.parseInt(numbers[col]);
                    mapTileNum[col][row] = num;
                }
                row++;
            }
            

            br.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void draw(Graphics2D g2) {

        // Usa la camera calcolata UNA SOLA VOLTA in GamePanel.updateCamera()
        int cameraX = gp.cameraX;
        int cameraY = gp.cameraY;

        int worldCol = 0;
        int worldRow = 0;

        while (worldCol < gp.maxWorldCol && worldRow < gp.maxWorldRow) {

            int tileNum = mapTileNum[worldCol][worldRow];

            int worldX = worldCol * gp.tileSize;
            int worldY = worldRow * gp.tileSize;

            int screenX = worldX - cameraX ;
            int screenY = worldY - cameraY;

            // Disegna solo i tile visibili
            if (screenX + gp.tileSize > 0 && screenX < gp.screenWidth &&
                screenY + gp.tileSize > 0 && screenY < gp.screenHeight) {

                g2.drawImage(tile[tileNum].image, screenX, screenY, gp.tileSize, gp.tileSize, null);
                // ===== DEBUG COLLISION =====
                if (tile[tileNum].collision) {
                    g2.setColor(new java.awt.Color(255, 0, 0, 90)); // rosso trasparente
                    g2.fillRect(screenX, screenY, gp.tileSize, gp.tileSize);
                }

                if (tile[tileNum].stair) {
                    g2.setColor(new java.awt.Color(0, 255, 0, 90)); // verde trasparente
                    g2.fillRect(screenX, screenY, gp.tileSize, gp.tileSize);
                }
            }

            worldCol++;

            if (worldCol == gp.maxWorldCol) {
                worldCol = 0;
                worldRow++;
            }
           
        }
        
       
    }
    
}

