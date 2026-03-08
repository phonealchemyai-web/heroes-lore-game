import javax.microedition.lcdui.*;
import javax.microedition.midlet.*;

public class GameCanvas extends Canvas implements Runnable {
    public static final int STATE_MENU = 0;
    public static final int STATE_GAME = 1;
    public static final int STATE_COMBAT = 2;
    public static final int STATE_SHOP = 3;
    public static final int STATE_INVENTORY = 4;
    public static final int STATE_QUEST = 5;
    public static final int STATE_PAUSE = 6;
    
    private int gameState = STATE_MENU;
    private int menuSelection = 0, shopSelection = 0, invSelection = 0;
    private Thread gameThread;
    private boolean running = false;
    private Graphics g;
    private int sw, sh;
    
    // Héroe
    private int heroX = 160, heroY = 120;
    private boolean up, down, left, right;
    
    // Estadísticas
    private int hp = 100, maxHp = 100;
    private int mp = 50, maxMp = 50;
    private int level = 1, exp = 0, gold = 100;
    private int attack = 10, defense = 5;
    
    // Inventario
    private String[] items = {"Poción", "Éter", "Espada", "Escudo"};
    private int[] qty = {3, 1, 1, 1};
    
    // Enemigos
    private Enemy[] enemies = {
        new Enemy("Slime", 30, 5, 15),
        new Enemy("Goblin", 50, 8, 25),
        new Enemy("Esqueleto", 70, 12, 40),
        new Enemy("Orco", 100, 15, 60),
        new Enemy("Dragón", 200, 25, 200)
    };
    private Enemy currentEnemy;
    
    // Tienda
    private String[] shopItems = {"Poción", "Éter", "Espada+1", "Armadura"};
    private int[] prices = {10, 25, 100, 150};
    
    // Misiones
    private String[] quests = {"Derrota 5 Slimes", "Consigue 200 oro", "Derrota Dragón"};
    private int[] questProg = {0, 0, 0};    
    // Combate
    private int combatTurn = 0;
    private String combatMsg = "";
    
    public GameCanvas() {
        setFullScreenMode(true);
        sw = getWidth();
        sh = getHeight();
    }
    
    public void start() {
        running = true;
        gameThread = new Thread(this);
        gameThread.start();
    }
    
    public void stop() {
        running = false;
        try { if(gameThread != null) gameThread.join(); } catch(Exception e) {}
    }
    
    public void run() {
        while(running) {
            update();
            repaint();
            try { Thread.sleep(50); } catch(Exception e) {}
        }
    }
    
    void update() {
        if(gameState == STATE_GAME) {
            if(up && heroY > 0) heroY -= 3;
            if(down && heroY < sh - 20) heroY += 3;
            if(left && heroX > 0) heroX -= 3;
            if(right && heroX < sw - 20) heroX += 3;
        }
        if(combatMsgTimer > 0) combatMsgTimer--;
    }
    
    protected void paint(Graphics g) {
        this.g = g;
        g.setColor(0x000000);
        g.fillRect(0, 0, sw, sh);
        
        switch(gameState) {
            case STATE_MENU: drawMenu(); break;
            case STATE_GAME: drawGame(); break;
            case STATE_COMBAT: drawCombat(); break;
            case STATE_SHOP: drawShop(); break;            case STATE_INVENTORY: drawInventory(); break;
            case STATE_QUEST: drawQuests(); break;
            case STATE_PAUSE: drawPause(); break;
        }
    }
    
    void drawMenu() {
        g.setColor(0x00ff00);
        g.setFont(Font.getFont(0, 1, 8));
        g.drawString("HEROES LORE", sw/2, 50, 1);
        g.drawString("Wind of Soltia", sw/2, 80, 1);
        g.setColor(menuSelection == 0 ? 0x00ff00 : 0x888888);
        g.drawString("> NUEVO JUEGO <", sw/2, 130, 1);
        g.setColor(menuSelection == 1 ? 0x00ff00 : 0x888888);
        g.drawString("> CARGAR <", sw/2, 160, 1);
        g.setColor(menuSelection == 2 ? 0x00ff00 : 0x888888);
        g.drawString("> SALIR <", sw/2, 190, 1);
    }
    
    void drawGame() {
        g.setColor(0x3d6b16);
        g.fillRect(0, 0, sw, sh);
        g.setColor(0x00ff00);
        g.fillRect(heroX, heroY, 16, 16);
        g.setColor(0xffffff);
        g.drawRect(heroX, heroY, 16, 16);
        g.drawString("HP:" + hp + "/" + maxHp, 5, 5, 0);
        g.drawString("MP:" + mp + "/" + maxMp, 5, 20, 0);
        g.drawString("Nvl:" + level, 5, 35, 0);
        g.drawString("Oro:" + gold, sw-60, 5, 0);
        g.drawString("5:Inv 7:Mapa 1:Tienda", sw/2, sh-20, 1);
    }
    
    void drawCombat() {
        g.setColor(0x1a1a2e);
        g.fillRect(0, 0, sw, sh);
        g.setColor(0xff0000);
        g.fillRect(sw/2-30, 50, 60, 60);
        g.drawString(currentEnemy.name, sw/2, 120, 1);
        g.drawString("HP:" + currentEnemy.hp + "/" + currentEnemy.maxHp, sw/2, 140, 1);
        g.setColor(0x00ff00);
        g.fillRect(50, sh-100, 60, 60);
        g.drawString("HEROE", 50, sh-30, 0);
        g.drawString("HP:" + hp, 50, sh-15, 0);
        g.setColor(0xffffff);
        g.drawString("1:Atacar 2:Magia 5:Huir", sw/2, sh-20, 1);
        if(combatMsg != null && combatMsgTimer > 0) {
            g.setColor(0xffff00);
            g.drawString(combatMsg, sw/2, 200, 1);
        }    }
    
    void drawShop() {
        g.setColor(0x2d1b00);
        g.fillRect(0, 0, sw, sh);
        g.setColor(0xffaa00);
        g.setFont(Font.getFont(0, 1, 8));
        g.drawString("TIENDA", sw/2, 20, 1);
        g.setColor(0xffffff);
        g.drawString("Oro: " + gold, sw/2, 45, 1);
        for(int i=0; i<shopItems.length; i++) {
            g.setColor(i == shopSelection ? 0x00ff00 : 0xffffff);
            g.drawString(shopItems[i] + " - $" + prices[i], 20, 70 + i*25, 0);
        }
        g.setColor(0x888888);
        g.drawString("2/8:Navegar 5:Comprar 0:Salir", sw/2, sh-20, 1);
    }
    
    void drawInventory() {
        g.setColor(0x001a33);
        g.fillRect(0, 0, sw, sh);
        g.setColor(0x00ccff);
        g.setFont(Font.getFont(0, 1, 8));
        g.drawString("INVENTARIO", sw/2, 20, 1);
        g.setColor(0xffffff);
        for(int i=0; i<items.length; i++) {
            g.setColor(i == invSelection ? 0x00ff00 : 0xffffff);
            g.drawString(items[i] + " x" + qty[i], 20, 70 + i*25, 0);
        }
        g.setColor(0x888888);
        g.drawString("2/8:Navegar 5:Usar 0:Salir", sw/2, sh-20, 1);
    }
    
    void drawQuests() {
        g.setColor(0x1a0033);
        g.fillRect(0, 0, sw, sh);
        g.setColor(0xcc00ff);
        g.setFont(Font.getFont(0, 1, 8));
        g.drawString("MISIONES", sw/2, 20, 1);
        g.setColor(0xffffff);
        for(int i=0; i<quests.length; i++) {
            g.setColor(questProg[i] > 0 ? 0x00ff00 : 0x888888);
            g.drawString(quests[i], 20, 70 + i*25, 0);
        }
        g.setColor(0x888888);
        g.drawString("5:Volver", sw/2, sh-20, 1);
    }
    
    void drawPause() {
        g.setColor(0x333333);        g.fillRect(0, 0, sw, sh);
        g.setColor(0xffffff);
        g.setFont(Font.getFont(0, 1, 8));
        g.drawString("PAUSA", sw/2, sw/4+15, 1);
        g.drawString("5:Continuar", sw/2, sw/4+40, 1);
        g.drawString("1:Salir al Menu", sw/2, sw/4+60, 1);
    }
    
    protected void keyPressed(int k) {
        int a = getGameAction(k);
        
        if(gameState == STATE_MENU) {
            if(a == UP) menuSelection = (menuSelection - 1 + 3) % 3;
            if(a == DOWN) menuSelection = (menuSelection + 1) % 3;
            if(a == FIRE) {
                if(menuSelection == 0) gameState = STATE_GAME;
                else if(menuSelection == 2) System.exit(0);
            }
        }
        else if(gameState == STATE_GAME) {
            if(a == UP) up = true;
            if(a == DOWN) down = true;
            if(a == LEFT) left = true;
            if(a == RIGHT) right = true;
            if(k == 5) gameState = STATE_PAUSE;
            if(k == 7) gameState = STATE_QUEST;
            if(k == 1) gameState = STATE_SHOP;
            if(k == 2) { // Inventario
                if(qty[0] > 0 && hp < maxHp) {
                    qty[0]--;
                    hp = Math.min(hp + 30, maxHp);
                }
            }
            // Combate aleatorio
            if(Math.random() < 0.01) {
                currentEnemy = enemies[(int)(Math.random() * enemies.length)];
                currentEnemy.hp = currentEnemy.maxHp;
                gameState = STATE_COMBAT;
                combatTurn = 0;
            }
        }
        else if(gameState == STATE_COMBAT) {
            if(combatTurn == 0) {
                if(k == 1) { // Atacar
                    int dmg = attack + (int)(Math.random() * 5);
                    currentEnemy.hp -= dmg;
                    combatMsg = "Atacaste: " + dmg + " daño";
                    combatMsgTimer = 60;
                    combatTurn = 1;
                }                if(k == 2) { // Magia
                    if(mp >= 10) {
                        mp -= 10;
                        int dmg = attack * 2;
                        currentEnemy.hp -= dmg;
                        combatMsg = "Magia: " + dmg + " daño";
                        combatMsgTimer = 60;
                        combatTurn = 1;
                    }
                }
                if(k == 5) { // Huir
                    gameState = STATE_GAME;
                }
            }
        }
        else if(gameState == STATE_SHOP) {
            if(a == UP) shopSelection = (shopSelection - 1 + shopItems.length) % shopItems.length;
            if(a == DOWN) shopSelection = (shopSelection + 1) % shopItems.length;
            if(k == 5) { // Comprar
                if(gold >= prices[shopSelection]) {
                    gold -= prices[shopSelection];
                    if(shopSelection < 2) qty[shopSelection]++;
                    combatMsg = "¡Comprado!";
                } else combatMsg = "Sin oro";
            }
            if(k == 0) gameState = STATE_GAME;
        }
        else if(gameState == STATE_INVENTORY) {
            if(a == UP) invSelection = (invSelection - 1 + items.length) % items.length;
            if(a == DOWN) invSelection = (invSelection + 1) % items.length;
            if(k == 5) { // Usar item
                if(qty[invSelection] > 0) {
                    qty[invSelection]--;
                    if(invSelection == 0) hp = Math.min(hp + 30, maxHp);
                    if(invSelection == 1) mp = Math.min(mp + 20, maxMp);
                }
            }
            if(k == 0) gameState = STATE_GAME;
        }
        else if(gameState == STATE_QUEST) {
            if(k == 5) gameState = STATE_GAME;
        }
        else if(gameState == STATE_PAUSE) {
            if(k == 5) gameState = STATE_GAME;
            if(k == 1) gameState = STATE_MENU;
        }
    }
    
    protected void keyReleased(int k) {
        int a = getGameAction(k);        if(a == UP) up = false;
        if(a == DOWN) down = false;
        if(a == LEFT) left = false;
        if(a == RIGHT) right = false;
    }
    
    class Enemy {
        String name;
        int hp, maxHp, attack, exp, gold;
        Enemy(String name, int hp, int attack, int exp) {
            this.name = name;
            this.hp = hp;
            this.maxHp = hp;
            this.attack = attack;
            this.exp = exp;
            this.gold = 10 + (int)(Math.random() * 20);
        }
    }
    }
