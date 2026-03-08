import javax.microedition.midlet.*;
import javax.microedition.lcdui.*;

public class J2MERPG extends MIDlet {
    private GameCanvas gameCanvas;
    private Display display;
    
    public J2MERPG() {
        display = Display.getDisplay(this);
    }
    
    public void startApp() {
        gameCanvas = new GameCanvas();
        display.setCurrent(gameCanvas);
        gameCanvas.start();
    }
    
    public void pauseApp() {
        if (gameCanvas != null) gameCanvas.stop();
    }
    
    public void destroyApp(boolean unconditional) {
        if (gameCanvas != null) gameCanvas.stop();
    }
}
