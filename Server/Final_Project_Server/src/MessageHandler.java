import java.awt.AWTException;
import java.awt.Desktop;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Robot;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import com.google.gson.Gson;

public class MessageHandler {

	private Robot robot = null;
	private static MessageHandler handler = null;
	private InputStream input;
	private OutputStream output;
	private String delimeter = "/";

	//Return singleton instance
	public static MessageHandler getInstance() {
		if (handler == null)
			handler = new MessageHandler();

		return handler;
	}

	//Constructor of message handler
	private MessageHandler() {
		try {
			robot = new Robot();
		} catch (AWTException e) {
			e.printStackTrace();
		}

	}

	public InputStream getInput() {
		return input;
	}

	public void setInput(InputStream input) {
		this.input = input;
	}

	public OutputStream getOutput() {
		return output;
	}

	public void setOutput(OutputStream output) {
		this.output = output;
	}

	//Handle messages
	public synchronized void handle(String msg) {

		String[] tokens = msg.split(delimeter);

		//Mouse commands
		if (tokens[0].equals("MOUSE")) {
			if (tokens[1].equals("CLICK")) {
				robot.mousePress(InputEvent.BUTTON1_MASK);
				robot.mouseRelease(InputEvent.BUTTON1_MASK);
			} else if (tokens[1].equals("RIGHT_CLICK")) {
				robot.mousePress(InputEvent.BUTTON3_MASK);
				robot.mouseRelease(InputEvent.BUTTON3_MASK);
			} else if (tokens[1].equals("SCROLL")) {
				robot.mouseWheel(Integer.parseInt(tokens[2]) / 3);

			} else if (tokens[1].equals("DRAG")) {
				robot.mousePress(InputEvent.BUTTON1_MASK);
			} else if (tokens[1].equals("FIN_DRAG")) {
				robot.mouseRelease(InputEvent.BUTTON1_MASK);
			} else {
				Point p = MouseInfo.getPointerInfo().getLocation();
				robot.mouseMove(Integer.parseInt(tokens[1]) + p.x,
						Integer.parseInt(tokens[2]) + p.y);
			}

		}
		//VLC commands
		else if (tokens[0].equals("VLC")) {
			if (tokens[1].equals("PLAY")) {
				typeKey(KeyEvent.VK_SPACE);
			} else if (tokens[1].equals("REWIND")) {
				typeKey(KeyEvent.VK_ALT, KeyEvent.VK_LEFT);
			} else if (tokens[1].equals("FORWARD")) {
				typeKey(KeyEvent.VK_ALT, KeyEvent.VK_RIGHT);
			} else if (tokens[1].equals("STOP")) {
				typeKey(KeyEvent.VK_S);
			} else if (tokens[1].equals("PREVIOUS")) {
				typeKey(KeyEvent.VK_P);
			} else if (tokens[1].equals("NEXT")) {
				typeKey(KeyEvent.VK_N);
			} else if (tokens[1].equals("FULLSCREEN")) {
				typeKey(KeyEvent.VK_F);
			} else if (tokens[1].equals("MUTE")) {
				typeKey(KeyEvent.VK_M);
			} else if (tokens[1].equals("VOLUMEDOWN")) {
				typeKey(KeyEvent.VK_CONTROL, KeyEvent.VK_DOWN);
			} else if (tokens[1].equals("VOLUMEUP")) {
				typeKey(KeyEvent.VK_CONTROL, KeyEvent.VK_UP);
			}
		} 
		//Presentation commands
		else if (tokens[0].equals("PPT")) {
			if (tokens[1].equals("NEXT")) {
				typeKey(KeyEvent.VK_RIGHT);
			} else if (tokens[1].equals("PREVIOUS")) {
				typeKey(KeyEvent.VK_LEFT);
			} else if (tokens[1].equals("GOTO")) {

				byte[] sNums = tokens[2].getBytes();
				int tmp;

				for (int i = 0; i < sNums.length; i++) {
					tmp = sNums[i];
					typeKey(tmp + 48);
				}

				typeKey(KeyEvent.VK_ENTER);
			} else if (tokens[1].equals("START")) {
				typeKey(KeyEvent.VK_F5);
			} else if (tokens[1].equals("FINISH")) {
				typeKey(KeyEvent.VK_ESCAPE);
			}

		}
		//Keyboard 
		else if (tokens[0].equals("KEY")) {
			int keycode = Integer.parseInt(tokens[1]);
			if (keycode >= 32) {
				keyToChar(keycode);
			} else {
				switch (keycode) {
				case 8:
					typeKey(8);
					break;
				case 10:
					typeKey(10);
					break;
				}
			}

		} 
		//System commands
		else if (tokens[0].equals("SYS")) {
			if (System.getProperty("os.name").startsWith("Windows")) {
				String command = "";

				if (tokens[1].equals("SHUTDOWN")) {
					command = "shutdown /p";
				} else if (tokens[1].equals("RESTART")) {
					command = "shutdown /r /f /t 0";
				} else if (tokens[1].equals("LOCK")) {
					command = "rundll32.exe user32.dll, LockWorkStation";
				} else if (tokens[1].equals("LOGOUT")) {
					command = "shutdown /l /f";
				} else if (tokens[1].equals("HIBERNATE")) {
					WinSleepHibernate.SetSuspendState(true, true, false);
				} else if (tokens[1].equals("SLEEP")) {
					WinSleepHibernate.SetSuspendState(false, true, false);
				}

				if (command != "") {
					try {
						Runtime.getRuntime().exec(command);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

			}

		} 
		//Send or open files
		else if (tokens[0].equals("sendFile")) {
			sendFiles(createPath(tokens));
		} else if (tokens[0].equals("openFile")) {
			openFile(createPath(tokens));
		} else
			System.out.println(msg);

	}

	//Open requested file
	private void openFile(String filePath) {
		try {
			Desktop.getDesktop().open(new File(filePath));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	//Send files as json objects
	private void sendFiles(String absolutePath) {
		try {

			RemoteFile file = new RemoteFile(new File(
					(absolutePath.equals("HOME")) ? System
							.getProperty("user.home") : absolutePath));
			Gson gson = new Gson();
			String jsonStr = gson.toJson(file);
			output.write("&".getBytes());
			output.write(jsonStr.getBytes("UTF-8"));
			output.write("?".getBytes());
			output.flush();
			ServerScreen.LOGGER.info("File whose  path is " + absolutePath
					+ " send");
		} catch (IOException e) {
			ServerScreen.LOGGER.severe("File path does not exist! " + absolutePath);
			e.printStackTrace();
		}
	}

	//Convert unicode values to characters using "ALT+unicode" combination
	private void keyToChar(int c) {
		int nunpads[] = { KeyEvent.VK_NUMPAD0, KeyEvent.VK_NUMPAD1,
				KeyEvent.VK_NUMPAD2, KeyEvent.VK_NUMPAD3, KeyEvent.VK_NUMPAD4,
				KeyEvent.VK_NUMPAD5, KeyEvent.VK_NUMPAD6, KeyEvent.VK_NUMPAD7,
				KeyEvent.VK_NUMPAD8, KeyEvent.VK_NUMPAD9 };

		char digits[] = Integer.toString(c).toCharArray();
		int numbers[] = new int[digits.length];
		for (int i = 0; i < numbers.length; i++) {
			numbers[i] = digits[i] - 48;
		}

		robot.keyPress(KeyEvent.VK_ALT);
		for (int i = 0; i < numbers.length; i++) {
			int keycode = nunpads[numbers[i]];
			robot.keyPress(keycode);
			robot.keyRelease(keycode);
		}

		robot.keyRelease(KeyEvent.VK_ALT);
	}

	//Linux path creation
	private String createPath(String[] tokens) {
		if (tokens.length > 2) {
			String path = "";
			for (int i = 1; i < tokens.length; i++) {
				path = path.concat("/" + tokens[i]);
			}
			return path;
		}
		else if (tokens.length == 1 && !System.getProperty("os.name").contains("Windows")) {
			return "/";
		}
		
		return tokens[1];
	}

	//Type one key
	private void typeKey(int keycode) {
		robot.keyPress(keycode);
		robot.keyRelease(keycode);

	}

	//Type two keys
	private void typeKey(int keycode1, int keycode2) {
		robot.keyPress(keycode1);
		robot.keyPress(keycode2);
		robot.keyRelease(keycode2);
		robot.keyRelease(keycode1);

	}

}
