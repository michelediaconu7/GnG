package main;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class KeyHandler implements KeyListener {
	
	public boolean upPress, downPress, leftPress, rightPress, attackPress;
	GamePanel gp;
	
	public KeyHandler(GamePanel gp) {
		this.gp = gp;
	}

	@Override
	public void keyTyped(KeyEvent e) {
	}

	@Override
	public void keyPressed(KeyEvent e) {
	
		int code = e.getKeyCode();
		
		if(code == KeyEvent.VK_W) {
			upPress = true;
		}
		if(code == KeyEvent.VK_S) {
			downPress = true;
		}
		if(code == KeyEvent.VK_A) {
			leftPress = true;
		}
		if(code == KeyEvent.VK_D) {
			rightPress = true;
		}
		if(code == KeyEvent.VK_SPACE) {
			attackPress = true;
		}
		
		    if (gp.gameState == gp.stateMenu) {
		    	if (code == KeyEvent.VK_UP) {
		            gp.menu.commandNum--;
		            if (gp.menu.commandNum < 0) {
		                gp.menu.commandNum = 1; 
		            }
		        }

		        if (code == KeyEvent.VK_DOWN) {
		            gp.menu.commandNum++;
		            if (gp.menu.commandNum > 1) {
		                gp.menu.commandNum = 0;
		            }
		        }

		        if (code == KeyEvent.VK_ENTER) {
		            if (gp.menu.commandNum == 0) {
		                // START GAME
		                gp.gameState = gp.statePlay;
		            }
		            if (gp.menu.commandNum == 1) {
		                // EXIT
		                System.exit(0);
		            }
		        }
		    }
		   
		    else if (gp.gameState == gp.statePlay) {

		        if (code == KeyEvent.VK_P) {
		            gp.gameState = gp.statePause;
		        }

		        
		    }

		   //pausa
		    else if (gp.gameState == gp.statePause) {

		        if (code == KeyEvent.VK_P) {
		            gp.gameState = gp.statePlay;
		        }

		        
		    }

		    //gameover
		    else if (gp.gameState == gp.stateGameOver) {
		        if (code == KeyEvent.VK_ENTER) {
		            gp.gameState = gp.stateMenu;
		        }
		    }
		
	}

	@Override
	public void keyReleased(KeyEvent e) {
		
		int code = e.getKeyCode();
		
		if(code==KeyEvent.VK_W) {
			upPress = false;
		}
		if(code==KeyEvent.VK_S) {
			downPress = false;
		}
		if(code==KeyEvent.VK_A) {
			leftPress = false;
		}
		if(code==KeyEvent.VK_D) {
			rightPress = false;
		}
		if(code == KeyEvent.VK_SPACE) {
			attackPress = false;
		}
	}
	
	

}
