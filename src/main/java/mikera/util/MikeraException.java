package mikera.util;

@SuppressWarnings("serial")
public class MikeraException extends RuntimeException {
	public MikeraException (String message) {
		super(message);
	}

	public MikeraException(Throwable x) {
		super(x);
	}
}
