package com.arc.client;

import java.net.SocketException;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

public class InputActivity extends Activity implements OnTouchListener {

	private Connection conn;
	/*private SensorManager mgr;
	private Sensor gyro;
	private GyroscopeListener gyroListener;*/

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_input);

		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
				.permitAll().build();
		StrictMode.setThreadPolicy(policy);

		AppUtil.CURRENT_CONTEXT = this;
		conn = Connection.getConnection();

		if (conn != null && conn.isConnected()) {
			/*mgr = (SensorManager) this.getSystemService(SENSOR_SERVICE);
			gyro = mgr.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
			
			 * gyroListener = new GyroscopeListener(c);
			 * mgr.registerListener(gyroListener, gyro,
			 * SensorManager.SENSOR_DELAY_NORMAL);
			 */
			if (conn.getConnectionType() == Connection.WIFI_CONNECTION) {
				WifiConnection wifiConnection = Connection.getWifiConnection();
				wifiConnection.setUnReliableMode();
			}
			View v = findViewById(R.id.inputView);
			v.setOnTouchListener(this);

		} else {
			Toast.makeText(this, R.string.not_connected_toast,
					Toast.LENGTH_SHORT).show();
		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu2, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.keyboard) {
			((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE))
					.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT,
							InputMethodManager.HIDE_IMPLICIT_ONLY);
		}

		return super.onOptionsItemSelected(item);
	}

	int lastX, lastY, x, y, downX, downY;
	boolean scrolling = false;
	long time, lasttime = -1;
	double factor;
	boolean isDragging = false;
	long firstDown = 0;

	public boolean onTouch(View v, MotionEvent event) {

		//Mouse right click
		if (event.getActionMasked() == MotionEvent.ACTION_POINTER_UP
				&& event.getPointerCount() == 2) {
			//Cancel scrolling
			if (scrolling) {
				scrolling = false;
				if (event.getActionIndex() == 0) {
					lastX = (int) event.getX(1);
					lastY = (int) event.getY(1);
				}

			} else {
				try {
					conn.sendMessage("MOUSE/RIGHT_CLICK/");
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

		} 
		//First finger down
		else if (event.getAction() == MotionEvent.ACTION_DOWN) {
			firstDown = System.currentTimeMillis();
	
			downX = lastX = (int) event.getX();
			downY = lastY = (int) event.getY();

			return true;

		}
		//Second finger down
		else if (event.getAction() == MotionEvent.ACTION_POINTER_1_DOWN) {
			
			lastX = (int) event.getX();
			lastY = (int) event.getY();
			return true;

		}
		//Mouse left click
		else if (event.getAction() == MotionEvent.ACTION_UP) {
			if(isDragging) {
				try {
					conn.sendMessage("MOUSE/FIN_DRAG");
					isDragging = false;
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			if (Math.abs((int) event.getX() - downX) < 2
					&& Math.abs((int) event.getY() - downY) < 2) {

				try {
					conn.sendMessage("MOUSE/CLICK/");
				} catch (Exception e) {
					e.printStackTrace();
				}

			}
			lasttime = -1;
			return false;

		} else if (event.getAction() == MotionEvent.ACTION_MOVE) {
			x = (int) event.getX();
			y = (int) event.getY();

			time = System.currentTimeMillis();
			if (lasttime == -1) {
				factor = 1;
				lasttime = System.currentTimeMillis();
			} else {
				factor = (double) ((Math.sqrt(Math.pow(x - lastX, 2)
						+ Math.pow(y - lastY, 2))) / (time - lasttime));
				if (factor < 1)
					factor = 1;

				lasttime = System.currentTimeMillis();
			}
			//Mouse scroll
			if (event.getPointerCount() == 2) {
				scrolling = true;

				try {
					conn.sendMessage("MOUSE/SCROLL/" + (y - lastY) + "/");

				} catch (Exception e) {
					e.printStackTrace();
				}

			} else {
				if(firstDown != 0 && time - firstDown > 1000) {
					try {
						conn.sendMessage("MOUSE/DRAG");//Activate dragging
						isDragging = true;
					} catch (Exception e) {
						e.printStackTrace();
					} finally {
						firstDown = 0;
					}
				}
				//Mouse move
				try {
					conn.sendMessage("MOUSE/" + (int) ((x - lastX) * factor)
							+ "/" + (int) ((y - lastY) * factor) + "/");
					firstDown = 0;
				} catch (Exception e) {
					e.printStackTrace();
				}

			}
			lastX = x;
			lastY = y;
			return true;
		}
		return false;
	}

	@Override
	protected void onPause() {
		/*if (gyroListener != null)
			mgr.unregisterListener(gyroListener, gyro);*/
		super.onPause();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		AppUtil.CURRENT_CONTEXT = this;
	}

	@Override
	public boolean dispatchKeyEvent(KeyEvent KEvent) {

		int keyaction = KEvent.getAction();

		if (conn != null && conn.isConnected()) {

			if (keyaction == KeyEvent.ACTION_DOWN) {

				int keycode = KEvent.getKeyCode();

				if (keycode == KeyEvent.KEYCODE_DEL) {
					try {
						conn.sendMessage("KEY/" + 8 + "/");
					} catch (Exception e) {
						e.printStackTrace();
					}
				} else {

					int keyunicode = KEvent.getUnicodeChar();
					try {
						conn.sendMessage("KEY/" + keyunicode + "/");
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			} else if (keyaction == KeyEvent.ACTION_MULTIPLE) {
				String a = KEvent.getCharacters();
				char[] as = a.toCharArray();

				try {
					conn.sendMessage("KEY/" + (int) as[0] + "/");
				} catch (Exception e) {
					e.printStackTrace();
				}

			}

		}

		return super.dispatchKeyEvent(KEvent);
	}

}
