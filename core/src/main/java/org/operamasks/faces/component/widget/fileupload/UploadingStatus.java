package org.operamasks.faces.component.widget.fileupload;

public interface UploadingStatus {
	public Exception getError();
	public Long getContentLength();
	public int getCurrentIndex();
	public int getUploadingItemsNumber();
	public Long getBytesRead();
}
