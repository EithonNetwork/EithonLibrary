package net.eithon.library.exceptions;

public abstract class EithonException extends Exception {
	private static final long serialVersionUID = 1L;
	
	public EithonException(String message){
		super(message);

	}
	
	public EithonException(Exception e){
		super(e);
	}
	
	public EithonException(String message, Exception e){
		super(message, e);
	}
}
