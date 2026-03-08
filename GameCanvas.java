import javax.microedition.lcdui.*;
import javax.microedition.midlet.*;

public class GameCanvas extends Canvas implements Runnable {
    
    public static final int STATE_MENU = 0;
    public static final int STATE_GAME = 1;
    public static final int STATE_PAUSE = 2;
    
    private int gameState = STATE_MENU;
    private int menuSelection = 0;
    private static final int FPS = 30;
    private static final int FRAME_TIME = 1000 / FPS;
    
    private boolean running = false;
    private Thread gameThread = null;
    
    private int heroX = 120;
    private int heroY = 120;
    private int heroSize = 16;
    private int heroFrame = 0;
    private int heroDirection = 0;
    
    private boolean upPressed = false;
    private boolean downPressed = false;
    private boolean leftPressed = false;
    private boolean rightPressed = false;
    
    private Graphics g;    private int screenWidth;
    private int screenHeight;
    
    private int mapOffsetX = 0;
    private int mapOffsetY = 0;
    
    private Font titleFont;
    private Font menuFont;
    private Font smallFont;
    
    public GameCanvas() {
        setFullScreenMode(true);
        screenWidth = getWidth();
        screenHeight = getHeight();
        
        titleFont = Font.getFont(Font.FACE_SYSTEM, Font.STYLE_BOLD, Font.SIZE_LARGE);
        menuFont = Font.getFont(Font.FACE_SYSTEM, Font.STYLE_PLAIN, Font.SIZE_MEDIUM);
        smallFont = Font.getFont(Font.FACE_SYSTEM, Font.STYLE_PLAIN, Font.SIZE_SMALL);
    }
    
    public void start() {
        running = true;
        gameThread = new Thread(this);
        gameThread.start();
    }
    
    public void stop() {
        running = false;
        try {
            if (gameThread != null) gameThread.join();
        } catch (InterruptedException e) {}
    }
    
    public void run() {
        long lastTime = System.currentTimeMillis();
        while (running) {
            long currentTime = System.currentTimeMillis();
            long elapsed = currentTime - lastTime;
            if (elapsed >= FRAME_TIME) {
                lastTime = currentTime;
                update();
                repaint();
            }
            try { Thread.sleep(1); } catch (InterruptedException e) {}
        }
    }
    
    private void update() {
        if (gameState == STATE_GAME) {
            int speed = 2;            boolean moved = false;
            
            if (upPressed && heroY > 0) {
                heroY -= speed;
                heroDirection = 0;
                moved = true;
            }
            if (downPressed && heroY < screenHeight - heroSize - 20) {
                heroY += speed;
                heroDirection = 1;
                moved = true;
            }
            if (leftPressed && heroX > 0) {
                heroX -= speed;
                heroDirection = 2;
                moved = true;
            }
            if (rightPressed && heroX < screenWidth - heroSize) {
                heroX += speed;
                heroDirection = 3;
                moved = true;
            }
            
            if (moved) {
                heroFrame = (heroFrame + 1) % 4;
            } else {
                heroFrame = 0;
            }
            
            mapOffsetX = heroX - screenWidth / 2;
            mapOffsetY = heroY - screenHeight / 2;
        }
    }
    
    protected void paint(Graphics g) {
        this.g = g;
        
        g.setColor(0x000000);
        g.fillRect(0, 0, screenWidth, screenHeight);
        
        if (gameState == STATE_MENU) {
            drawMenu();
        } else if (gameState == STATE_GAME) {
            drawGame();
        } else if (gameState == STATE_PAUSE) {
            drawGame();
            drawPauseMenu();
        }
    }
        private void drawMenu() {
        g.setColor(0x1a1a2e);
        g.fillRect(0, 0, screenWidth, screenHeight);
        
        for (int i = 0; i < screenWidth; i += 30) {
            g.setColor(0x16213e);
            g.drawLine(i, 0, i, screenHeight);
        }
        for (int i = 0; i < screenHeight; i += 30) {
            g.setColor(0x16213e);
            g.drawLine(0, i, screenWidth, i);
        }
        
        g.setFont(titleFont);
        g.setColor(0x00ff00);
        int titleWidth = titleFont.stringWidth("HEROES LORE");
        g.drawString("HEROES LORE", (screenWidth - titleWidth) / 2, 40, Graphics.TOP | Graphics.LEFT);
        
        g.setColor(0x00aa00);
        int subtitleWidth = menuFont.stringWidth("Wind of Soltia");
        g.drawString("Wind of Soltia", (screenWidth - subtitleWidth) / 2, 75, Graphics.TOP | Graphics.LEFT);
        
        g.setFont(menuFont);
        g.setColor(0xffffff);
        
        int yPos = 150;
        if (menuSelection == 0) {
            g.setColor(0x00ff00);
            g.drawString("> NUEVO JUEGO <", screenWidth / 2, yPos, Graphics.TOP | Graphics.HCENTER);
        } else {
            g.setColor(0x888888);
            g.drawString("NUEVO JUEGO", screenWidth / 2, yPos, Graphics.TOP | Graphics.HCENTER);
        }
        
        yPos += 30;
        if (menuSelection == 1) {
            g.setColor(0x00ff00);
            g.drawString("> CONTINUAR <", screenWidth / 2, yPos, Graphics.TOP | Graphics.HCENTER);
        } else {
            g.setColor(0x888888);
            g.drawString("CONTINUAR", screenWidth / 2, yPos, Graphics.TOP | Graphics.HCENTER);
        }
        
        yPos += 30;
        if (menuSelection == 2) {
            g.setColor(0x00ff00);
            g.drawString("> OPCIONES <", screenWidth / 2, yPos, Graphics.TOP | Graphics.HCENTER);
        } else {
            g.setColor(0x888888);
            g.drawString("OPCIONES", screenWidth / 2, yPos, Graphics.TOP | Graphics.HCENTER);        }
        
        yPos += 30;
        if (menuSelection == 3) {
            g.setColor(0x00ff00);
            g.drawString("> SALIR <", screenWidth / 2, yPos, Graphics.TOP | Graphics.HCENTER);
        } else {
            g.setColor(0x888888);
            g.drawString("SALIR", screenWidth / 2, yPos, Graphics.TOP | Graphics.HCENTER);
        }
        
        g.setFont(smallFont);
        g.setColor(0x555555);
        g.drawString("Use 2/8 navegar, 5 seleccionar", screenWidth / 2, screenHeight - 30, Graphics.BOTTOM | Graphics.HCENTER);
    }
    
    private void drawGame() {
        for (int x = 0; x < screenWidth; x += 20) {
            for (int y = 0; y < screenHeight; y += 20) {
                int mapX = x + mapOffsetX;
                int mapY = y + mapOffsetY;
                
                if ((mapX / 20 + mapY / 20) % 2 == 0) {
                    g.setColor(0x2d5016);
                } else {
                    g.setColor(0x3d6b16);
                }
                g.fillRect(x, y, 20, 20);
            }
        }
        
        g.setColor(0x1a4d1a);
        for (int i = 0; i < screenWidth; i += 40) {
            g.drawLine(i, 0, i, screenHeight);
        }
        for (int i = 0; i < screenHeight; i += 40) {
            g.drawLine(0, i, screenWidth, i);
        }
        
        drawHero(heroX, heroY, heroFrame, heroDirection);
        
        g.setColor(0x000000);
        g.fillRect(0, screenHeight - 25, screenWidth, 25);
        
        g.setColor(0x00ff00);
        g.setFont(smallFont);
        g.drawString("HP:100/100", 5, screenHeight - 20, Graphics.TOP | Graphics.LEFT);
        g.drawString("MP:50/50", screenWidth / 2 - 20, screenHeight - 20, Graphics.TOP | Graphics.LEFT);
        g.drawString("LVL:1", screenWidth - 35, screenHeight - 20, Graphics.TOP | Graphics.LEFT);
                g.setColor(0x00ff00);
        g.fillRect(50, screenHeight - 18, 40, 4);
        g.setColor(0x0000ff);
        g.fillRect(50, screenHeight - 12, 30, 4);
    }
    
    private void drawHero(int x, int y, int frame, int direction) {
        int bounce = (frame % 2) * 2;
        
        g.setColor(0x0066cc);
        g.fillRect(x + 2, y + bounce, 12, 14 - bounce);
        
        g.setColor(0xffcc99);
        g.fillRect(x + 4, y + 2 + bounce, 8, 6);
        
        g.setColor(0x000000);
        if (direction == 2) {
            g.fillRect(x + 5, y + 4 + bounce, 2, 2);
        } else if (direction == 3) {
            g.fillRect(x + 9, y + 4 + bounce, 2, 2);
        } else {
            g.fillRect(x + 5, y + 4 + bounce, 2, 2);
            g.fillRect(x + 9, y + 4 + bounce, 2, 2);
        }
        
        g.setColor(0xcc6600);
        g.fillRect(x + 1, y + 8 + bounce, 14, 2);
        
        if (frame > 0) {
            g.setColor(0x0066cc);
            if (frame % 4 == 1 || frame % 4 == 3) {
                g.fillRect(x + (frame % 2) * 4, y + 14, 4, 4);
            }
        }
    }
    
    private void drawPauseMenu() {
        g.setColor(0x000000);
        g.fillRect(0, 0, screenWidth, screenHeight);
        
        g.setColor(0x333333);
        g.fillRect(screenWidth / 4, screenHeight / 3, screenWidth / 2, screenHeight / 3);
        
        g.setColor(0x00ff00);
        g.drawRect(screenWidth / 4, screenHeight / 3, screenWidth / 2, screenHeight / 3);
        
        g.setFont(menuFont);
        g.setColor(0xffffff);
        g.drawString("PAUSA", screenWidth / 2, screenHeight / 3 + 10, Graphics.TOP | Graphics.HCENTER);
        g.drawString("5 - Continuar", screenWidth / 2, screenHeight / 3 + 40, Graphics.TOP | Graphics.HCENTER);        g.drawString("7 - Salir", screenWidth / 2, screenHeight / 3 + 60, Graphics.TOP | Graphics.HCENTER);
    }
    
    protected void keyPressed(int keyCode) {
        int gameAction = getGameAction(keyCode);
        
        if (gameState == STATE_MENU) {
            if (keyCode == KEY_NUM2 || gameAction == UP) {
                menuSelection = (menuSelection - 1 + 4) % 4;
            }
            if (keyCode == KEY_NUM8 || gameAction == DOWN) {
                menuSelection = (menuSelection + 1) % 4;
            }
            if (keyCode == KEY_NUM5 || gameAction == FIRE) {
                if (menuSelection == 0) {
                    gameState = STATE_GAME;
                    heroX = screenWidth / 2;
                    heroY = screenHeight / 2;
                } else if (menuSelection == 3) {
                    running = false;
                }
            }
        } else if (gameState == STATE_GAME) {
            if (gameAction == UP) upPressed = true;
            if (gameAction == DOWN) downPressed = true;
            if (gameAction == LEFT) leftPressed = true;
            if (gameAction == RIGHT) rightPressed = true;
            if (keyCode == KEY_NUM2) upPressed = true;
            if (keyCode == KEY_NUM8) downPressed = true;
            if (keyCode == KEY_NUM4) leftPressed = true;
            if (keyCode == KEY_NUM6) rightPressed = true;
            if (keyCode == KEY_NUM5 || keyCode == KEY_NUM7) {
                gameState = STATE_PAUSE;
            }
        } else if (gameState == STATE_PAUSE) {
            if (keyCode == KEY_NUM5) {
                gameState = STATE_GAME;
            }
            if (keyCode == KEY_NUM7) {
                gameState = STATE_MENU;
            }
        }
    }
    
    protected void keyReleased(int keyCode) {
        int gameAction = getGameAction(keyCode);
        if (gameAction == UP) upPressed = false;
        if (gameAction == DOWN) downPressed = false;
        if (gameAction == LEFT) leftPressed = false;
        if (gameAction == RIGHT) rightPressed = false;        if (keyCode == KEY_NUM2) upPressed = false;
        if (keyCode == KEY_NUM8) downPressed = false;
        if (keyCode == KEY_NUM4) leftPressed = false;
        if (keyCode == KEY_NUM6) rightPressed = false;
    }
}
