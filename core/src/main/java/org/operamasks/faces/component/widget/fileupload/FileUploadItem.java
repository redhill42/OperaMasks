package org.operamasks.faces.component.widget.fileupload;

import java.io.IOException;
import java.io.InputStream;

public interface FileUploadItem {
	public String getName();
	public String getFieldName();
	public String getContentType();
	public int getItemIndex();
	public InputStream openStream() throws IOException;
}
