package main;

import java.awt.Color;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

public class Menu {
	GamePanel gp;
	Font titleFont;
	Font menuFont;
	Font smallFont;
	Font heartFont;
	int hudCounter = 0;
	
	private int titleY = -100;
	private int titleTargetY; //Posizione finale

	    // Per animazioni
	int frameCounter = 0;
	boolean showPressEnter = true;

	   
	int commandNum = 0; // 0 = Start, 1 = Exit 
	
	public Menu(GamePanel gp) {
		this.gp = gp;
		
		titleFont = new Font("Serif", Font.BOLD, 64);
        menuFont = new Font("SansSerif", Font.BOLD, 32);
        smallFont = new Font("SansSerif", Font.PLAIN, 20);
        heartFont = new Font("SansSerif", Font.BOLD, 48);
        
        titleTargetY = (int)(gp.screenHeight * 0.4);
	}
	
	public void draw(Graphics2D g2) {
		
		g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		
		if(gp.gameState == gp.stateMenu) {
			drawMenu(g2);
		}else if(gp.gameState == gp.statePlay) {
			drawHUD(g2);
		}else if(gp.gameState == gp.statePause) {
			drawPause(g2);
		}else if(gp.gameState == gp.stateGameOver) {
			drawGameOver(g2);
		}
	}
	
	private void drawMenu(Graphics2D g2) {
		
		int width = gp.screenWidth;
		int height = gp.screenHeight;
		
		//sfondo
		g2.setColor(Color.black);
		g2.fillRect(0,0,width, height);
		
		//g2.setColor(new Color(100, 100, 120, 80)); // grigio-blu molto trasparente
		//g2.fillRect(0, height / 4, width, height / 2);
		
		// GRADIENTE VERTICALE per effetto “vapore”
		GradientPaint fogGradient = new GradientPaint(
		        0, 0, new Color(0, 0, 0, 0),               // completamente trasparente
		        0, height, new Color(150, 150, 200, 60)    // leggero blu/grigio nebbioso
		);
		g2.setPaint(fogGradient);
		g2.fillRect(0, 0, width, height);
		
		// animazione titolo
		if (titleY < titleTargetY) {
		    titleY += Math.max(1, (titleTargetY - titleY) / 50);
		}

		String text = "Ghosts 'n Goblins SR";
		g2.setFont(titleFont);

		int textWidth = g2.getFontMetrics().stringWidth(text);
		int x = (width - textWidth) / 2;
		int y = titleY;

		// Ombra
		g2.setColor(new Color(0, 0, 0, 200));
		g2.drawString(text, x + 4, y + 4);

		// Bordi 
		g2.setColor(new Color(150, 0, 0));
		g2.drawString(text, x - 2, y);
		g2.drawString(text, x + 2, y);
		g2.drawString(text, x, y - 2);
		g2.drawString(text, x, y + 2);

		// Testo principale 
		g2.setColor(new Color(255, 40, 40)); 
		g2.drawString(text, x, y);


       

        // Opzioni menu
        g2.setFont(menuFont);
        String start = "Start Game";
        String exit = "Exit";

        int startW = g2.getFontMetrics().stringWidth(start);
        int exitW = g2.getFontMetrics().stringWidth(exit);

        int menuY = height / 2 + 40;

        for (int i = 0; i < 2; i++) {

            String optionText = "";
            int optionY = menuY + (i * 50);

            switch (i) {
                case 0:
                    optionText = "Start Game";
                    break;
                case 1:
                    optionText = "Exit";
                    break;
            }

            // COLORI
            if (commandNum == i) {
                g2.setColor(new Color(50, 150, 255));  // selezionato (blu brillante)
            } else {
                g2.setColor(new Color(40, 80, 160)); // non selezionato (celeste)
            }

            int optionW = g2.getFontMetrics().stringWidth(optionText);
            g2.drawString(optionText, (width - optionW) / 2, optionY);
        }
        
        // "Press ENTER" lampeggiante
        frameCounter++;
        if (frameCounter > 50) { // ogni tot frame invertiamo
            frameCounter = 0;
            showPressEnter = !showPressEnter;
        }

        if (showPressEnter) {
            g2.setFont(smallFont);
            String pressEnter = "Premi ENTER per confermare";
            int peW = g2.getFontMetrics().stringWidth(pressEnter);
            g2.setColor(new Color(255, 255, 255, 220));
            g2.drawString(pressEnter, (width - peW) / 2, height - 40);
        }
    }
	

    private void drawHUD(Graphics2D g2) {
    	hudCounter++;

        g2.setFont(heartFont);

        int maxLives = 2;                 // numero totale cuori (puoi leggere da gp.player.maxLife se ce l’hai)
        int currentLives = gp.player.life; // vite correnti

        int baseX = 20;
        int baseY = 45;
        int spacing = 40; // distanza tra un cuore e l'altro
        
        
        for (int i = 0; i < maxLives; i++) {
        
            // piccolo movimento su/giù per animazione (solo i cuori ancora "vivi")
            int offsetY = 0;
            if (i < currentLives) {
                offsetY = (int)(2 * Math.sin((hudCounter + i * 10) * 0.15));
            }

            int x = baseX + i * spacing;
            int y = baseY + offsetY;

            if (i < currentLives) {
                // cuore pieno 
                g2.setColor(new Color(160, 20, 30));
            } else {
                // cuore vuoto
                g2.setColor(Color.black);
            }

            g2.drawString("\u2665", x, y); // ♥
        }
        int col = gp.player.worldX / gp.tileSize;
        int row = gp.player.worldY / gp.tileSize;

        int tileID = gp.tileMan.mapTileNum[col][row];

        g2.drawString("TileID: " + tileID, 20, 130);

        
    }

    private void drawPause(Graphics2D g2) {
    	int width = gp.screenWidth;
        int height = gp.screenHeight;

        // sfondo semi-trasparente sopra il gioco
        g2.setColor(new Color(0, 0, 0, 100)); // nero trasparente
        g2.fillRect(0, 0, width, height);

        // testo "PAUSA"
        g2.setFont(titleFont);
        String text = "PAUSA";
        int textW = g2.getFontMetrics().stringWidth(text);
        int x = (width - textW) / 2;
        int y = height / 2 - 40;

        g2.setColor(Color.black);
        g2.drawString(text, x + 3, y + 3);      // ombra
        g2.setColor(Color.white);
        g2.drawString(text, x, y);

        // messaggio istruzioni
        g2.setFont(smallFont);
        String msg1 = "Premi P per riprendere";
        

        int msg1W = g2.getFontMetrics().stringWidth(msg1);
        

        g2.setColor(new Color(230, 230, 230));
        g2.drawString(msg1, (width - msg1W) / 2, y + 40);
        
    }
    private void drawGameOver(Graphics2D g2) {
    	int width = gp.screenWidth;
        int height = gp.screenHeight;

        g2.setColor(Color.black);
        g2.fillRect(0, 0, width, height);

        g2.setFont(titleFont);
        String text = "GAME OVER";
        int textW = g2.getFontMetrics().stringWidth(text);
        int x = (width - textW) / 2;
        int y = height / 2;

        g2.setColor(Color.red);
        g2.drawString(text, x, y);

        g2.setFont(smallFont);
        String retry = "Premi ENTER per tornare al menu";
        int rW = g2.getFontMetrics().stringWidth(retry);
        g2.setColor(Color.white);
        g2.drawString(retry, (width - rW) / 2, y + 40);
    }
    

}
