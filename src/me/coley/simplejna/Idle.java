package me.coley.simplejna;

import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef;
import me.coley.simplejna.hook.key.KeyEventReceiver;
import me.coley.simplejna.hook.key.KeyHookManager;
import me.coley.simplejna.hook.mouse.MouseEventReceiver;
import me.coley.simplejna.hook.mouse.MouseHookManager;
import me.coley.simplejna.hook.mouse.struct.MouseButtonType;

import java.awt.*;

import static java.lang.Thread.State.TERMINATED;

/**
 * Utility for retrieving the idle time on Windows.
 *
 * @author ochafik
 */
public class Idle {

    /**
     * Get the time elapsed since the last input event <i>((mouse or keyboard))</i>
     * in milliseconds.
     *
     * @return Time in milliseconds
     */
    public static int dxInputEventTime() {
        User32.LASTINPUTINFO lastInputInfo = new User32.LASTINPUTINFO();
        User32.INSTANCE.GetLastInputInfo(lastInputInfo);
        return Kernel32.INSTANCE.GetTickCount() - lastInputInfo.dwTime;
    }

    public static enum State {
        UNKNOWN, ONLINE, IDLE, AWAY
    }

    public static void main(String[] args) {

        KeyHookManager keyHook = new KeyHookManager();
        KeyEventReceiver ker = new KeyEventReceiver(keyHook) {

            //创建一个新的线程myThread此线程进入新建状态
            //Thread myThread = new MyThread();
            MyThread myThread = new MyThread();
            Thread thread = new Thread(myThread);

            @Override
            public boolean onKeyUpdate(SystemState sysState, PressState pressState, int time, int vkCode) {
                /*System.out.println("Is pressed:" + (pressState == PressState.DOWN));
                System.out.println("Alt down:" + (sysState == KeyEventReceiver.SystemState.SYSTEM));
                System.out.println("Timestamp:" + time);*/
                System.out.println("KeyCode:" + vkCode);
                if (vkCode == 121) {
                    if(thread.getState()==TERMINATED){
                        System.out.println("thread.getState():" + thread.getState());
                        thread = new Thread(myThread);
                        thread.start();
                    }else{
                        thread.start();
                    }
                } else if (vkCode == 123) {
                    //System.out.println("F12 click:" + vkCode);
                    thread.interrupt();
                }
                return false;
            }
        };
        keyHook.hook(ker);
    }

}

class MyThread implements Runnable {

    int num = 0;

    //获得屏幕高宽
    Dimension screensize = Toolkit.getDefaultToolkit().getScreenSize();
    int width = (int) screensize.getWidth();
    int height = (int) screensize.getHeight();

    //移动到冒个位置后左键单击
    Mouse ms = new Mouse();

    @Override
    public void run() {
        while (true) {
            if ( Thread.currentThread().isInterrupted() ) {
                System.out.println("i has interputed");
                break;
            }
            num++;
            //System.out.println("The width and the height of the screen are " + screensize.getWidth() + " x " + screensize.getHeight());
            System.out.println(num);
            //System.out.println(width);
            //System.out.println(height);

            ms.movemouseTo(947, 509);
            //ms.movemouseTo(width/2, height/2-30);
            //ms.movemouseTo(width, height);
            ms.mouseLeftClick(0, 0);
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}
