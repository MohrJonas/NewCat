package cat;

import lombok.experimental.UtilityClass;

import static cTools.KernelWrapper.exit;

@UtilityClass
public class Util {

	public void errorAndExit(String msg) {
		System.err.println(msg);
		exit(1);
	}

	public void errorAndExit(String msg, Object... args) {
		System.err.printf(msg, args);
		exit(1);
	}

}
