import java.awt.AWTException;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Robot;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

public class MessageHandler {

	private Robot robot = null;
	private static MessageHandler handler = null;

	public static MessageHandler getInstance() {
		if (handler == null)
			handler = new MessageHandler();

		return handler;
	}

	private MessageHandler() {
		try {
			robot = new Robot();
		} catch (AWTException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public synchronized void handle(String msg) {

		String[] ss = msg.split("/");
		// System.out.println(ss[0] + " " + ss[1] + " " + ss[2]);
		if (ss[0].equals("MOUSE")) {
			if (ss[1].equals("CLICK")) {
				// System.out.println(ss[1]);
				robot.mousePress(InputEvent.BUTTON1_MASK);
				robot.mouseRelease(InputEvent.BUTTON1_MASK);
			} else if (ss[1].equals("RIGHT_CLICK")) {
				// System.out.println(ss[1]);
				robot.mousePress(InputEvent.BUTTON3_MASK);
				robot.mouseRelease(InputEvent.BUTTON3_MASK);
			} else if (ss[1].equals("SCROLL")) {
				robot.mouseWheel(Integer.parseInt(ss[2]) / 3);

			} else {
				Point p = MouseInfo.getPointerInfo().getLocation();
				// robot.setAutoDelay(5);
				robot.mouseMove(Integer.parseInt(ss[1]) + p.x,
						Integer.parseInt(ss[2]) + p.y);
			}

		} else if (ss[0].equals("shake")) {
			robot.keyPress(KeyEvent.VK_WINDOWS);
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			robot.keyRelease(KeyEvent.VK_WINDOWS);

		} else if (ss[0].equals("VLC")) {
			if (ss[1].equals("PLAY")) {
				typeKey(KeyEvent.VK_SPACE);
			} else if (ss[1].equals("REWIND")) {
				typeKey(KeyEvent.VK_ALT, KeyEvent.VK_LEFT);
			} else if (ss[1].equals("FORWARD")) {
				typeKey(KeyEvent.VK_ALT, KeyEvent.VK_RIGHT);
			} else if (ss[1].equals("STOP")) {
				typeKey(KeyEvent.VK_S);
			} else if (ss[1].equals("PREVIOUS")) {
				typeKey(KeyEvent.VK_P);
			} else if (ss[1].equals("NEXT")) {
				typeKey(KeyEvent.VK_N);
			} else if (ss[1].equals("FULLSCREEN")) {
				typeKey(KeyEvent.VK_F);
			} else if (ss[1].equals("MUTE")) {
				typeKey(KeyEvent.VK_M);
			}
		} else if (ss[0].equals("PPT")) {
			if (ss[1].equals("NEXT")) {
				typeKey(KeyEvent.VK_RIGHT);
			} else if (ss[1].equals("PREVIOUS")) {
				typeKey(KeyEvent.VK_LEFT);
			} else if (ss[1].equals("GOTO")) {

				byte[] sNums = ss[2].getBytes();
				int tmp;

				for (int i = 0; i < sNums.length; i++) {
					tmp = sNums[i];
					typeKey(tmp + 48);
				}

				typeKey(KeyEvent.VK_ENTER);
			} else if (ss[1].equals("START")) {
				typeKey(KeyEvent.VK_F5);
			}

		} else if (ss[0].equals("KEY")) {
			int keycode = Integer.parseInt(ss[1]);
			type(keycode);
			System.out.println(keycode);

		} else
			System.out.println(msg);

	}

	private void type(int keycode) {

		if (keycode >= 65 && keycode <= 90) {
			typeKey(KeyEvent.VK_SHIFT, keycode);

		} else if (keycode >= 97 && keycode <= 122) {
			typeKey(keycode - 32);

		} else if (keycode >= 48 && keycode <= 57) {
			typeKey(keycode);

		} else {
			switch (keycode) {
			case 45:
				typeKey(KeyEvent.VK_MINUS);
				break;
			case 61:
				typeKey(KeyEvent.VK_EQUALS);
				break;
			case 126:
				typeKey(KeyEvent.VK_ALT_GRAPH, KeyEvent.VK_BACK_QUOTE);
				break;
			case 33:
				typeKey(KeyEvent.VK_SHIFT, KeyEvent.VK_1);
				break;
			case 64:
				typeKey(KeyEvent.VK_ALT_GRAPH, KeyEvent.VK_Q);
				break;
			case 35:
				typeKey(KeyEvent.VK_ALT_GRAPH, KeyEvent.VK_3);
				break;
			case 36:
				typeKey(KeyEvent.VK_ALT_GRAPH, KeyEvent.VK_4);
				break;
			case 37:
				typeKey(KeyEvent.VK_SHIFT, KeyEvent.VK_5);
				break;
			case 94:
				typeKey(KeyEvent.VK_SHIFT, KeyEvent.VK_3);
				break;
			case 38:
				typeKey(KeyEvent.VK_SHIFT, KeyEvent.VK_6);
				break;
			case 42:
				typeKey(KeyEvent.VK_ASTERISK);
				break;
			case 40:
				typeKey(KeyEvent.VK_SHIFT, KeyEvent.VK_8);
				break;
			case 41:
				typeKey(KeyEvent.VK_SHIFT, KeyEvent.VK_9);
				break;
			case 95:
				typeKey(KeyEvent.VK_UNDERSCORE);
				break;
			case 96:
				typeKey(KeyEvent.VK_BACK_QUOTE);
				break;
			case 43:
				typeKey(KeyEvent.VK_PLUS);
				break;
			case 9:
				typeKey(KeyEvent.VK_TAB);
				break;
			case 10:
				typeKey(KeyEvent.VK_ENTER);
				break;
			case 91:
				typeKey(KeyEvent.VK_OPEN_BRACKET);
				break;
			case 93:
				typeKey(KeyEvent.VK_CLOSE_BRACKET);
				break;
			case 92:
				typeKey(KeyEvent.VK_BACK_SLASH);
				break;
			case 123:
				typeKey(1, 91);
				break;
			case 125:
				typeKey(1, 93);
				break;
			case 124:
				typeKey(1, 92);
				break;
			case 59:
				typeKey(59);
				break;
			case 39:
				typeKey(222);
				break;
			case 34:
				typeKey(1, 222);
				break;
			case 44:
				typeKey(44);
				break;
			case 60:
				typeKey(1, 44);
				break;
			case 46:
				typeKey(46);
				break;
			case 62:
				typeKey(1, 46);
				break;
			case 47:
				typeKey(47);
				break;
			case 63:
				typeKey(1, 47);
				break;
			case 32:
				typeKey(32);
				break;
			case 8:
				typeKey(8);
				break;
			}
		}

	}

	private void typeKey(int keycode) {
		robot.keyPress(keycode);
		robot.keyRelease(keycode);
		
	}

	private void typeKey(int i, int keycode) {
		robot.keyPress(i);
		robot.keyPress(keycode);
		robot.keyRelease(keycode);
		robot.keyRelease(i);
		
	}

}
