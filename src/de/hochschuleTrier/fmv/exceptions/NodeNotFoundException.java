package de.hochschuleTrier.fmv.exceptions;

public class NodeNotFoundException extends Exception {
	public NodeNotFoundException() {
		super();
	}

	public NodeNotFoundException(final String message, final Throwable cause) {
		super(message, cause);
	}

	public NodeNotFoundException(final String message) {
		super(message);
	}

	public NodeNotFoundException(final Throwable cause) {
		super(cause);
	}
}
