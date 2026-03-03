
package main;

import java.awt.Rectangle;
import java.util.ArrayList;

import entity.Enemy;
import entity.Projectile;

import entity.Entity;
import entity.Player;

public class CollisionChecker {

    GamePanel gp;

    public CollisionChecker(GamePanel gp) {
        this.gp = gp;
    }

    // ================= COLLISIONE ORIZZONTALE ==================
    /**
     * Controlla le collisioni orizzontali usando direction + speed dell'entity.
     * NON sposta l'entity, si limita a settare entity.collisionOn = true/false.
     * Viene usato dal Player prima di aggiornare worldX.
     */
    public void checkTile(Entity entity) {

        entity.collisionOn = false;

        int dx = 0;
        if ("left".equals(entity.direction)) {
            dx = -entity.speed;
        } else if ("right".equals(entity.direction)) {
            dx = entity.speed;
        } else {
            // nessun movimento orizzontale
            return;
        }

        if (willCollide(entity, dx, 0)) {
            entity.collisionOn = true;
        }
    }

    // ================= GRAVITÀ + COLLISIONI VERTICALI ==================
    
    public void applyGravityAndCollisions(Player player) {

        // Se sto scalando, niente gravità
        if (player.climbing) {
            player.onGround = false;
            return;
        }

        int dy = (int) Math.round(player.velocityY);

        // Quanto possiamo muoverci in verticale TENENDO CONTO delle scale
        int allowedDy = computeAllowedYWithStairs(player, dy);

        // Muovi il player verticalmente
        player.worldY += allowedDy;

        if (dy > 0) {
            // Stava cadendo
            if (allowedDy < dy) {
                // Ha toccato il "pavimento" (tile solido o scala che fa da pavimento)
                player.onGround = true;
                player.velocityY = 0;
            } else {
                player.onGround = false;
            }
        } else if (dy < 0) {
            // Stava salendo (salto)
            if (allowedDy > dy) {
                // Ha sbattuto la testa
                player.velocityY = 0;
            }
            // onGround non si tocca qui
        } else {
            // dy == 0: controlliamo se è appoggiato su qualcosa (anche scala)
            if (willCollideVerticalWithStairs(player, 1)) {
                player.onGround = true;
            } else {
                player.onGround = false;
            }
        }
    }
    
 // ================= GRAVITÀ + COLLISIONI VERTICALI (GENERICA) ==================
    public void applyGravityAndCollisionsEntity(Entity e) {

        int dy = (int) Math.round(e.velocityY);

        int allowedDy = computeAllowedY(e, dy);

        e.worldY = e.worldY + allowedDy;

        if (dy > 0) {

            if (allowedDy < dy) {
                e.onGround = true;
                e.velocityY = 0.0;
            } else {
                e.onGround = false;
            }

        } else if (dy < 0) {

            if (allowedDy > dy) {
                e.velocityY = 0.0;
            }

        } else {

            if (willCollide(e, 0, 1)) {
                e.onGround = true;
            } else {
                e.onGround = false;
            }
        }
    }

    private int computeAllowedY(Entity e, int dy) {

        if (dy == 0) {
            return 0;
        }

        int step;
        if (dy > 0) {
            step = 1;
        } else {
            step = -1;
        }

        int moved = 0;

        while (moved != dy) {

            int nextMove = moved + step;

            if (willCollide(e, 0, nextMove)) {
                break;
            }

            moved = nextMove;
        }

        return moved;
    }
    
    
    private int computeAllowedYWithStairs(Player player, int dy) {

        if (dy == 0) {
            return 0;
        }

        int step;
        if (dy > 0) {
            step = 1;
        } else {
            step = -1;
        }

        int moved = 0;

        while (moved != dy) {
            int nextMove = moved + step;

            if (willCollideVerticalWithStairs(player, nextMove)) {
                // Il prossimo passo causerebbe una collisione
                break;
            }

            moved = nextMove;
        }

        return moved;
    }

    /**
     * Controlla se il player, spostato di dy in verticale,
     * entrerebbe in un tile che deve bloccarlo (pavimento o scala "solida").
     */
    public boolean willCollideVerticalWithStairs(Player player, int dy) {

        
    	int leftWorldX   = player.worldX + player.solidArea.x;
    	int rightWorldX  = leftWorldX + player.solidArea.width - 1;

    	int topWorldY    = player.worldY + player.solidArea.y + dy;
    	int bottomWorldY = topWorldY + player.solidArea.height - 1;

        int leftCol   = leftWorldX  / gp.tileSize;
        int rightCol  = rightWorldX / gp.tileSize;
        int topRow    = topWorldY   / gp.tileSize;
        int bottomRow = bottomWorldY/ gp.tileSize;

        for (int col = leftCol; col <= rightCol; col++) {
            for (int row = topRow; row <= bottomRow; row++) {

                if (col < 0 || col >= gp.maxWorldCol || row < 0 || row >= gp.maxWorldRow) {
                    continue;
                }

                int id = gp.tileMan.mapTileNum[col][row];
                if (id < 0) continue;

                var tile = gp.tileMan.tile[id];
                if (tile == null) continue;

                boolean solid = tile.collision;

                // --- LOGICA SPECIALE SCALE ---
                if (tile.stair) {

                    // Caso 1: sto scendendo o fermo (dy >= 0),
                    // NON sto premendo GIÙ e NON sto scalando
                    // => la scala fa da pavimento (blocca la caduta)
                    if (dy >= 0 && !gp.keyH.downPress && !player.climbing) {
                        solid = true;
                    }

                    // Caso 2: sto andando GIÙ (dy > 0) e premo GIÙ
                    // => la scala NON è solida: posso "scendere dentro"
                    if (dy > 0 && gp.keyH.downPress) {
                        solid = false;
                    }
                }
                // --- FINE LOGICA SCALE ---

                if (solid) {
                    return true;
                }
            }
        }

        return false;
    }


    public boolean willCollide(Entity entity, int dx, int dy) {

       
    	int leftWorldX   = entity.worldX + entity.solidArea.x + dx;
    	int rightWorldX  = leftWorldX + entity.solidArea.width - 1;

    	int topWorldY    = entity.worldY + entity.solidArea.y + dy;
    	int bottomWorldY = topWorldY + entity.solidArea.height - 1;

        int leftCol   = leftWorldX  / gp.tileSize;
        int rightCol  = rightWorldX / gp.tileSize;
        int topRow    = topWorldY   / gp.tileSize;
        int bottomRow = bottomWorldY/ gp.tileSize;

        for (int col = leftCol; col <= rightCol; col++) {
            for (int row = topRow; row <= bottomRow; row++) {
                if (isSolid(col, row)) {
                    return true;
                }
            }
        }

        return false;
    }
   
    
    public boolean isOnStair(Player player) {
    	int px = player.worldX + player.solidArea.x + player.solidArea.width / 2;
    	int py = player.worldY + player.solidArea.y + player.solidArea.height;

    	int col = px / gp.tileSize;
    	int row = py / gp.tileSize;

    	if (col < 0 || col >= gp.maxWorldCol || row < 0 || row >= gp.maxWorldRow) {
    	    return false;
    	}

    	int id = gp.tileMan.mapTileNum[col][row];
    	return gp.tileMan.tile[id].stair;
    }
    
    public boolean isOnLadder(Player player) {

        int centerX = player.worldX + player.solidArea.x + player.solidArea.width / 2;
        int centerY = player.worldY + player.solidArea.y + player.solidArea.height / 2;

        int col = centerX / gp.tileSize;
        int row = centerY / gp.tileSize;

        if (col < 0 || col >= gp.maxWorldCol || row < 0 || row >= gp.maxWorldRow) {
            return false;
        }

        int id = gp.tileMan.mapTileNum[col][row];
        return gp.tileMan.tile[id].stair; // o .ladder, come l’hai chiamato
    }
    
    public boolean isLadderBelow(Player player) {

        int centerX = player.worldX + player.solidArea.x + player.solidArea.width / 2;
        int feetY   = player.worldY + player.solidArea.y + player.solidArea.height + 1;

        int col = centerX / gp.tileSize;
        int row = feetY   / gp.tileSize;

        if (col < 0 || col >= gp.maxWorldCol || row < 0 || row >= gp.maxWorldRow) {
            return false;
        }

        int id = gp.tileMan.mapTileNum[col][row];
        return gp.tileMan.tile[id].stair;
    }



    /**
     * Controlla se il tile alla colonna/righe indicata è solido.
     * Usa la mappa di TileManager e il flag collision del singolo Tile.
     */
    private boolean isSolid(int col, int row) {

        
        return gp.tileMan.isTileSolid(col, row);
    }

	public void snapToGround(Player player) {

    // centro orizzontale del player
		int centerX = player.worldX + player.solidArea.x + player.solidArea.width / 2;
    // piedi (un po' sotto)
		int feetY   = player.worldY + player.solidArea.y + player.solidArea.height + 1;

		int col = centerX / gp.tileSize;
		int row = feetY / gp.tileSize;

		if (col < 0 || col >= gp.maxWorldCol || row < 0 || row >= gp.maxWorldRow) {
			return;
		}

    // Allineo i piedi esattamente sopra il tile "pavimento" di quella riga
		int tileTopY = row * gp.tileSize;
		player.worldY = tileTopY - player.solidArea.y - player.solidArea.height;

		player.onGround = true;
		player.velocityY = 0;
	}
	
	public void checkProjectileEnemyCollisions(ArrayList<Projectile> projectiles,
            ArrayList<Enemy> enemies) {

		for (int i = projectiles.size() - 1; i >= 0; i--) {

			Projectile p = projectiles.get(i);

			if (p.alive == false) {
				continue;
			}

			Rectangle pBox = new Rectangle(
					p.worldX + p.solidArea.x,
					p.worldY + p.solidArea.y,
					p.solidArea.width,
					p.solidArea.height
					);

			for (int j = enemies.size() - 1; j >= 0; j--) {

				Enemy e = enemies.get(j);

				if (e.alive == false) {
					continue;
				}

				Rectangle eBox = new Rectangle(
						e.worldX + e.solidArea.x,
						e.worldY + e.solidArea.y,
						e.solidArea.width,
						e.solidArea.height
						);

				if (pBox.intersects(eBox)) {

					e.takeDamage(1);
					p.alive = false;
					break;
				}
			}
		}
	}
	
}

