package org.operamasks.faces.component.widget.fileupload;

import javax.faces.FacesException;

@SuppressWarnings("serial")
public class UploadingException extends FacesException {
	public UploadingException() {
		super();
	}

	public UploadingException(String message, Throwable cause) {
		super(message, cause);
	}

	public UploadingException(String message) {
		super(message);
	}

	public UploadingException(Throwable cause) {
		super(cause);
	}

}
