
import java.awt.AWTException;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Robot;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;


public class MessageHandler {
	
	private Robot robot = null;
    private static MessageHandler handler = null;
	
	public static MessageHandler getInstance(){
		if(handler == null)
			handler = new MessageHandler();
		
		return handler;
	}
	
	private MessageHandler(){
		try {
			robot = new Robot();
		} catch (AWTException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	
	public synchronized void handle(String msg){
		
		String[] ss = msg.split("/");
		//System.out.println(ss[0] + " " + ss[1] + 	" " + ss[2]);
		if(ss[0].equals("MOUSE")){
			if(ss[1].equals("CLICK")){
				//System.out.println(ss[1]);
				robot.mousePress(InputEvent.BUTTON1_MASK);
				robot.mouseRelease(InputEvent.BUTTON1_MASK);
			}else if(ss[1].equals("RIGHT_CLICK")){
				//System.out.println(ss[1]);
				robot.mousePress(InputEvent.BUTTON3_MASK);
				robot.mouseRelease(InputEvent.BUTTON3_MASK);
			}else if(ss[1].equals("SCROLL")){
				robot.mouseWheel(Integer.parseInt(ss[2]) / 3);
				
			}else{
				Point p = MouseInfo.getPointerInfo().getLocation();
				//robot.setAutoDelay(5);
				robot.mouseMove(Integer.parseInt(ss[1]) + p.x , Integer.parseInt(ss[2]) + p.y);	
			}
			
						
		}else if (ss[0].equals("shake")){ 
			robot.keyPress(KeyEvent.VK_WINDOWS);
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			robot.keyRelease(KeyEvent.VK_WINDOWS);
			
		}else if (ss[0].equals("VLC")){
			if (ss[1].equals("PLAY")) {
				robot.keyPress(KeyEvent.VK_SPACE);
				robot.keyRelease(KeyEvent.VK_SPACE);			
			} else if (ss[1].equals("REWIND")) {
				robot.keyPress(KeyEvent.VK_ALT);
				robot.keyPress(KeyEvent.VK_LEFT);
				robot.keyRelease(KeyEvent.VK_LEFT);
				robot.keyRelease(KeyEvent.VK_ALT);
			}else if (ss[1].equals("FORWARD")) {
				robot.keyPress(KeyEvent.VK_ALT);
				robot.keyPress(KeyEvent.VK_RIGHT);
				robot.keyRelease(KeyEvent.VK_RIGHT);
				robot.keyRelease(KeyEvent.VK_ALT);
			}
		}
		else System.out.println(msg);
		
		
	}
	

}
