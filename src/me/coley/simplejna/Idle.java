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

    ;

    public static void main(String[] args) {

        KeyHookManager keyHook = new KeyHookManager();
        KeyEventReceiver ker = new KeyEventReceiver(keyHook) {
            //创建一个新的线程myThread此线程进入新建状态
            Thread myThread = new MyThread();
            @Override
            public boolean onKeyUpdate(SystemState sysState, PressState pressState, int time, int vkCode) {
                /*System.out.println("Is pressed:" + (pressState == PressState.DOWN));
                System.out.println("Alt down:" + (sysState == KeyEventReceiver.SystemState.SYSTEM));
                System.out.println("Timestamp:" + time);
                System.out.println("KeyCode:" + vkCode);*/
                System.out.println("KeyCodea:" + vkCode);
                if (vkCode == 121) {
                    myThread.start();
                } else if (vkCode == 123) {
                    //System.out.println("F12 click:" + vkCode);
                    myThread.interrupt();
                }
                return false;
            }
        };
        keyHook.hook(ker);

        MouseHookManager mouseHook = new MouseHookManager();
        MouseEventReceiver mer = new MouseEventReceiver(mouseHook) {
            @Override
            public boolean onMousePress(MouseButtonType type, WinDef.HWND hwnd, WinDef.POINT info) {
                boolean isLeft = type == MouseButtonType.LEFT_DOWN;
                if (isLeft) {
					/*System.out.println("Left mouse button has been pressed!");
					int mouseX = info.x;
					System.out.println("Left mouse button X to:"+mouseX);
					int mouseY = info.y;
					System.out.println("Left mouse button Y to:"+mouseY);*/
                }
                return false;
            }

            @Override
            public boolean onMouseRelease(MouseButtonType type, WinDef.HWND hwnd, WinDef.POINT info) {
                return false;
            }

            @Override
            public boolean onMouseScroll(boolean down, WinDef.HWND hwnd, WinDef.POINT info) {
                return false;
            }

            @Override
            public boolean onMouseMove(WinDef.HWND hwnd, WinDef.POINT info) {
                return false;
            }
        };
        mouseHook.hook(mer);
    }

    //@formatter:off
	/*
	public static void main(String[] args) {
		if (!System.getProperty("os.name").contains("Windows")) {
			System.err.println("ERROR: Only implemented on Windows");
			System.exit(1);
		}
		State state = State.UNKNOWN;
		DateFormat dateFormat = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss");
		for (;;) {
			int idleSec = dxInputEventTime() / 1000;
			State newState = idleSec < 30 ? State.ONLINE : idleSec > 5 * 60 ? State.AWAY : State.IDLE;
			if (newState != state) {
				state = newState;
				System.out.println(dateFormat.format(new Date()) + " # " + state);
			}
			try {
				Thread.sleep(100);
			} catch (Exception ex) {}
		}
	}
	*/
}

class MyThread extends Thread {

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
            System.out.println(num);
            ms.movemouseTo(width / 2, height / 2);
            ms.mouseLeftClick(0, 0);
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}
