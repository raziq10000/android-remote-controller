import com.sun.jna.Native;
import com.sun.jna.Platform;

public class WinSleepHibernate {
	public static native boolean SetSuspendState(boolean hibernate,
			boolean forceCritical, boolean disableWakeEvent);

	static {
		if (Platform.isWindows())
			Native.register("powrprof");
		
	}
}