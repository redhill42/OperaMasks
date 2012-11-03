package org.operamasks.faces.component.widget.fileupload;

import javax.faces.context.FacesContext;

import org.operamasks.faces.component.widget.UIFileUpload;

public final class UploadingMediator {
	private static final String FILE_UPLOAD_KEY_PREFIX = "org.operamasks.faces.FileUpload.";
	private static UploadingMediator instance;
	
	private UploadingMediator() {}
	
	public static UploadingMediator getInstance() {
		if (instance == null)
			instance = new UploadingMediator();
		
		return instance;
	}
	
	public String getUploadingSerialNumber(FacesContext context) {
		return (String)getFromSession(context, getUploadingSerialNumberKey(context));
	}
	
	private String getUploadingSerialNumberKey(FacesContext context) {
		return FILE_UPLOAD_KEY_PREFIX + ".uploadingSerialNumber";
	}

	public boolean canStartUploading(FacesContext context) {
		return !isUploading(context);
	}
	
	public Object getFromSession(FacesContext context, String key) {
		return context.getExternalContext().getSessionMap().get(key);
	}
	
	private String getUploadingKey() {
		return FILE_UPLOAD_KEY_PREFIX + "uploading";
	}
	
	public synchronized void startUploading(FacesContext context) {
		String uploadingSerialNumber = Long.toString(System.currentTimeMillis());
		putIntoSession(context, getUploadingSerialNumberKey(context), uploadingSerialNumber);
		putIntoSession(context, getUploadingKey(), "true");
		removeFromSession(context, getContentLengthKey());
		removeFromSession(context, getBytesReadKey());
		removeFromSession(context, getUploadingErrorKey());
	}

	private void putIntoSession(FacesContext context, String key, Object value) {
		context.getExternalContext().getSessionMap().put(key, value);
	}
	
	public synchronized void stopUploading(FacesContext context) {
		removeFromSession(context, getUploadingKey());
	}

	private Object removeFromSession(FacesContext context, String key) {
		return context.getExternalContext().getSessionMap().remove(key);
	}
	
	public void setContentLength(FacesContext context, long contentLength) {
		putIntoSession(context, getContentLengthKey(), contentLength);
	}

	private String getContentLengthKey() {
		return FILE_UPLOAD_KEY_PREFIX + "contentLength";
	}
	
	public Long getContentLength(FacesContext context) {
		Long length = (Long)getFromSession(context, getContentLengthKey());
		
		if (length == null)
			return 0l;
		
		return length;
	}
	
	public void setBytesRead(FacesContext context, long bytesRead) {
		putIntoSession(context, getBytesReadKey(), bytesRead);
	}

	private String getBytesReadKey() {
		return FILE_UPLOAD_KEY_PREFIX + "bytesRead";
	}
	
	public Long getBytesRead(FacesContext context) {
		Long length = (Long)getFromSession(context, getBytesReadKey());
		
		if (length == null)
			return 0l;
		
		return length;
	}
	
	public void updateProgress(FacesContext context, long bytesRead ,
			long contentLength, int currentIndex) {
		setBytesRead(context, bytesRead);
		setContentLength(context, contentLength);
		setCurrentIndex(context, currentIndex);
	}
	
	private void setCurrentIndex(FacesContext context, int currentIndex) {
		putIntoSession(context, getCurrentIndexKey(), currentIndex);
	}

	private String getCurrentIndexKey() {
		return FILE_UPLOAD_KEY_PREFIX + "currentIndex";
	}

	public boolean isUploading(FacesContext context) {
		return getFromSession(context, getUploadingKey()) != null;
	}

	public boolean hasContextLengthSet(FacesContext context) {
		return getContentLength(context) != null;
	}
	
	public boolean needRefreshContentLength(FacesContext context, UIFileUpload fileUpload) {
		if (!isUploading(context))
			return false;
		
		return fileUpload.getUploadingStatus() == null ||
				!getUploadingSerialNumber(context).equals(fileUpload.getUploadingSerialNumber());
	}

	public void stopUploading(FacesContext context, Exception error) {
		context.getExternalContext().getSessionMap().put(getUploadingErrorKey(), error);
		stopUploading(context);
	}

	private String getUploadingErrorKey() {
		return FILE_UPLOAD_KEY_PREFIX + "uploadingError";
	}
	
	public UploadingStatus getUploadingStatus(FacesContext context) {
		UploadingStatusImpl status = new UploadingStatusImpl();
		status.setContentLength(getContentLength(context));
		status.setBytesRead(getBytesRead(context));
		
		Exception error = (Exception)getFromSession(context, getUploadingErrorKey());
		if (error != null) {
			status.setError(error);
		}
		
		//status.setUploadingItemsNumber(getContentLength(context));
		
		return status;
	}
	
    private class UploadingStatusImpl implements UploadingStatus {
		private Long bytesRead;
    	private Long contentLength;
    	private int currentIndex;
    	private int uploadingItemsNumber;
    	private Exception error;
    	
    	public Long getBytesRead() {
    		if (bytesRead == null)
    			return 0l;
    		
    		if (bytesRead == UIFileUpload.END_UPLOADING)
    			return contentLength;
    			
    		return bytesRead;
    	}

    	public void setBytesRead(Long readBytes) {
    		this.bytesRead = readBytes;
    	}

    	public Long getContentLength() {
    		return contentLength;
    	}

    	public void setContentLength(Long contentLength) {
    		this.contentLength = contentLength;
    	}

		public int getCurrentIndex() {
			return currentIndex;
		}

		public Exception getError() {
			return error;
		}

		public int getUploadingItemsNumber() {
			return uploadingItemsNumber;
		}

		public void setCurrentIndex(int currentIndex) {
			this.currentIndex = currentIndex;
		}

		public void setError(Exception error) {
			this.error = error;
		}

		public void setUploadingItemsNumber(int uploadingItemsNumber) {
			this.uploadingItemsNumber = uploadingItemsNumber;
		}
    }
}
