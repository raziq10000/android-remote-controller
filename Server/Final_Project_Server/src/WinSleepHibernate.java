import com.sun.jna.Native;
import com.sun.jna.Platform;

public class WinSleepHibernate {
	
	//Sleep or hibernate function
	public static native boolean SetSuspendState(boolean hibernate,
			boolean forceCritical, boolean disableWakeEvent);

	static {
		if (Platform.isWindows())
			Native.register("powrprof");
	}
}