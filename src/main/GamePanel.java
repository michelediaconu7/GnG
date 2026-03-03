package main;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;

import java.util.ArrayList;
import entity.Projectile;

import javax.swing.JPanel;

import entity.Player;
import tile.TileManager;

import java.awt.Rectangle;
import entity.Enemy;
import entity.Slime;

public class GamePanel extends JPanel implements Runnable{
	
	
	//screen set
	final int originalTileSize = 16;
	final int scale = 5;
	public final int tileSize = originalTileSize*scale; //48x48
	public final int maxScreenCol = 16;
	public final int maxScreenRow = 8;
	public final int screenWidth = tileSize*maxScreenCol; 
	public final int screenHeight = tileSize*maxScreenRow; 
	//world set
	public final int maxWorldCol = 223;
	public final int maxWorldRow = 14;
	public final int worldWidth = tileSize * maxWorldCol;
	public final int worldHeight = tileSize* maxWorldRow;
	//FPS
	int FPS = 60;
	//Stato del gioco
	public final int stateMenu = 0;
	public final int statePlay = 1;
	public final int statePause = 2;
	public final int stateGameOver = 3;
	public int gameState = stateMenu;
	
	//Camera
	public int cameraX;
	public int cameraY;
	
	
	public TileManager tileMan = new TileManager(this);
	public KeyHandler keyH = new KeyHandler(this);
	
	Thread gameThread;
	public CollisionChecker cChecker = new CollisionChecker(this);
	public Player player = new Player(this,keyH);
	public Menu menu = new Menu(this);
	
	//Proiettile
	public ArrayList<Projectile> projectiles = new ArrayList<>();
	
	//Nemici
	public ArrayList<Enemy> enemies = new ArrayList<>();
	public GamePanel() {
		this.setPreferredSize(new Dimension(screenWidth,screenHeight));
		this.setBackground(Color.black);
		this.setDoubleBuffered(true);
		this.addKeyListener(keyH);
		this.setFocusable(true);
		this.spawnTestEnemies();
	
		
	}
	
	public void startGameThread() {
		
		gameThread = new Thread(this);
		gameThread.start();
	}

	@Override
	public void run() {
		
		double drawInterval = 1000000000/FPS;
		double delta = 0;
		long lastTime = System.nanoTime();
		long currentTime;
		
		while(gameThread != null) {
			
			currentTime = System.nanoTime();
			delta += (currentTime-lastTime)/drawInterval;
			lastTime = currentTime;
			if(delta>=1) {
				update();
				repaint();
				delta--;
			}
		}
		
	}
	public void update() {
		
		switch (gameState) {
        case stateMenu:
            // nessun update del mondo di gioco
            break;

        case statePlay:
            player.update();
            
            for (int i = projectiles.size() - 1; i >= 0; i--) {
                Projectile p = projectiles.get(i);
                p.update();
                if (!p.alive) projectiles.remove(i);
            }
            for (int i = enemies.size() - 1; i >= 0; i--) {
                Enemy e = enemies.get(i);
                e.update();
                if (e.alive == false) enemies.remove(i);
            }

            cChecker.checkProjectileEnemyCollisions(projectiles, enemies);
            updateCamera();
            break;

        case statePause:
            // non aggiorni nulla
            break;

        case stateGameOver:
            // eventuale animazione morte
            break;
    }
		
	}
	
	public void paintComponent(Graphics g) {
		
		super.paintComponent(g);
		
		Graphics2D g2 = (Graphics2D)g;
		
		
		
		if(gameState== statePlay|| gameState == statePause) {
			tileMan.draw(g2);
			for (int i = 0; i < enemies.size(); i++) {
			    Enemy e = enemies.get(i);
			    e.draw(g2);
			}
			
			for (int i = 0; i < projectiles.size(); i++) {

			    Projectile p = projectiles.get(i);
			    p.draw(g2);
			}
			
			player.draw(g2);
		}
		
		menu.draw(g2);
		
		g2.dispose();
	}
	public void updateCamera() {

	    cameraX = player.worldX - screenWidth / 2 + tileSize / 2;
	    cameraY = player.worldY - screenHeight / 2 + tileSize / 2;

	    if (cameraX < 0) {
	        cameraX = 0;
	    }
	    if (cameraY < 0) {
	        cameraY = 0;
	    }

	    int maxCameraX = maxWorldCol * tileSize - screenWidth;
	    int maxCameraY = maxWorldRow * tileSize - screenHeight;

	    if (cameraX > maxCameraX) {
	        cameraX = maxCameraX;
	    }
	    if (cameraY > maxCameraY) {
	        cameraY = maxCameraY;
	    }
	}
	
	private void spawnTestEnemies() {
	    enemies.add(new Slime(this, tileSize * 12, tileSize * 11));
	    enemies.add(new Slime(this, tileSize * 20, tileSize * 11));
	}
	
	

}
