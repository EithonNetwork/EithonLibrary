package net.eithon.library.exceptions;

public class PlayerException extends EithonException {
	public PlayerException(Exception e) {
		super(e);
	}

	public PlayerException(String message, Exception e) {
		super(message, e);
	}

	public PlayerException(String message) {
		super(message);
	}

	private static final long serialVersionUID = 1L;
}
