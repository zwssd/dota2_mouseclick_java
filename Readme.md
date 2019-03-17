# 关于、

Simplified JNA是一个库，允许在多线程环境中快速创建鼠标和键盘挂钩。此外，它还提供了向窗口、鼠标和键盘对象发送输入的简便方法。

# 使用

可以倒入 maven via JitPack:

Add the repo to your pom:
```
	<repositories>
		<repository>
		    <id>jitpack.io</id>
		    <url>https://jitpack.io</url>
		</repository>
	</repositories>
```
And add the dependency:
```
	<dependency>
	    <groupId>com.github.Col-E</groupId>
	    <artifactId>Simplified-JNA</artifactId>
	    <version>1.0</version>
	</dependency>
```

#### 使用案例

在这些示例中，returing“false”允许系统分析事件。将返回值更改为“true”将取消事件。

> Keyboard Hook
```java
public static void main(String[] args){
		KeyHookManager keyHook = new KeyHookManager();
KeyEventReceiver ker = new KeyEventReceiver(keyHook) {
    @Override
    public boolean onKeyUpdate(SystemState sysState, PressState pressState, int time, int vkCode) {
        System.out.println("Is pressed:" + (pressState == PressState.DOWN));
        System.out.println("Alt down:" + (sysState == SystemState.SYSTEM));
        System.out.println("Timestamp:" + time);
        System.out.println("KeyCode:" + vkCode);
        return false;
    }
};
KeyHook.hook(ker);
}
```
> Mouse Hook
```java
public static void main(String[] args){
		MouseHookManager mouseHook = new MouseHookManager();
		MouseEventReceiver mer = new MouseEventReceiver(mouseHook) {
			@Override
			public boolean onMousePress(MouseButtonType type, WinDef.HWND hwnd, WinDef.POINT info) {
				boolean isLeft = type == MouseButtonType.LEFT_DOWN;
				if (isLeft) {
					System.out.println("Left mouse button has been pressed!");
				}
				return false;
			}
			@Override public boolean onMouseRelease(MouseButtonType type, WinDef.HWND hwnd, WinDef.POINT info) { return false; }
			@Override public boolean onMouseScroll(boolean down, WinDef.HWND hwnd, WinDef.POINT info) { return false;  }
			@Override public boolean onMouseMove(WinDef.HWND hwnd, WinDef.POINT info) { return false; }
		};
		mouseHook.hook(mer);
	}
```
