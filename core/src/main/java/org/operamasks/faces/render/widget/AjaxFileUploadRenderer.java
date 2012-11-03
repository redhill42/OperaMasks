/*
 * $$Id: AjaxFileUploadRenderer.java,v 1.3 2008/04/15 03:23:18 patrick Exp $$
 *
 * Copyright (c) 2006-2007 Operamasks Community.
 * Copyright (c) 2000-2007 Apusic Systems, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.operamasks.faces.render.widget;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.FacesException;
import javax.faces.component.UIComponent;
import javax.faces.component.UIForm;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.ProgressListener;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.operamasks.faces.component.ajax.AjaxProgress;
import org.operamasks.faces.component.widget.UIFileUpload;
import org.operamasks.faces.component.widget.UIFileUploadProgress;
import org.operamasks.faces.component.widget.fileupload.FileUploadItem;
import org.operamasks.faces.component.widget.fileupload.UploadingException;
import org.operamasks.faces.component.widget.fileupload.UploadingMediator;
import org.operamasks.faces.render.html.HtmlRenderer;
import org.operamasks.faces.util.FacesUtils;
import org.operamasks.resources.Resources;

public class AjaxFileUploadRenderer extends HtmlRenderer {
	private static final int UPLOADING_BUFFER_SIZE = 4096;
	
	private Logger logger = Logger.getLogger("org.operamasks.faces.render");

	/* (non-Javadoc)
     * @see javax.faces.render.Renderer#encodeEnd(javax.faces.context.FacesContext, javax.faces.component.UIComponent)
     */
    @SuppressWarnings("unchecked")
	@Override
    public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
    	if (context == null || component == null)
            throw new NullPointerException();
        if (!component.isRendered())
            return;

        UIForm parentForm = getParentForm(component);
        if (isAjaxHtmlResponse(context)) {
        	if (!ServletFileUpload.isMultipartContent(((HttpServletRequest)context.getExternalContext().getRequest()))) {
        		renderFileUploadField(context, parentForm, component);
        	} else {
        		// In a form, only first file upload component needs to process uploading
        		if (isFirstFileUploadComponent(context, parentForm, component)) {
        			try {
        				processUploading(context, component);
					} catch (Throwable t) {
						getUploadingMediator().stopUploading(context,
								new UploadingException("Error occurs when uploading file", t));
					}
        		}
        	}
        	
        	context.renderResponse();
        } else if (isAjaxResponse(context)) {
        	UIFileUpload fileUpload = (UIFileUpload)component;
        	
        	UploadingMediator uploadingMediator = getUploadingMediator();
        	if (uploadingMediator.needRefreshContentLength(context, fileUpload)) {
        		while (!uploadingMediator.hasContextLengthSet(context)) {
        			try {
						Thread.sleep(50);
					} catch (InterruptedException e) {
						throw new FacesException(e);
					}
        		}
        	}
        	
        	fileUpload.setUploadingStatus(uploadingMediator.getUploadingStatus(context));
        }
    }

	private UploadingMediator getUploadingMediator() {
		return UploadingMediator.getInstance();
	}
    
	@SuppressWarnings("unchecked")
	private void processUploading(FacesContext context, UIComponent component)
				throws IOException, FileUploadException {
		UploadingMediator uploadingMediator = getUploadingMediator();
		
		if (!uploadingMediator.canStartUploading(context)) {
			return;
		} else {
			uploadingMediator.startUploading(context);
		}
		
		UIForm parentForm = getParentForm(component);		
		
		ServletFileUpload upload = new ServletFileUpload();
		upload.setProgressListener(new FileUploadProgressListener(context));
		HttpServletRequest request = (HttpServletRequest)context.getExternalContext().getRequest();
		
		InputStream input = null;
		try {
			FileItemIterator iter = upload.getItemIterator(request);
			
			int itemIndex = 0;
			while (iter.hasNext()) {
				FileItemStream item = iter.next();
				UIFileUpload fileUpload = findFileUploadByFieldName(parentForm, item.getFieldName());
				
				if (!item.isFormField() && isFieldRequired(fileUpload) && isFieldNull(item)) {
					throw new FacesException(getRequiredFacesMessage(context, fileUpload));
				}
				
				if (!item.isFormField()) {
					FileUploadItem uploadItem = createFileUploadItem(item, ++itemIndex, fileUpload.getMaxSize());
					
					if (fileUpload == null)
						throw new FacesException(
							"Can't find corresponding UIFileUpload component for this field " +
							uploadItem.getFieldName()
						);
					
					input = openStream(item, fileUpload);
					if (fileUpload.getWriteTo() != null) {
						writeToFile(input, fileUpload.getWriteTo());
					} else {
						fileUpload.getUploadListener().invoke(context.getELContext(),
								new Object[] {uploadItem});
						
						consumeStream(input);
					}
				} else {
					// Only need process file uploading, so consume other form fields
					input = openStream(item, fileUpload);
					consumeStream(input);
				}
			}
		} catch (FileUploadException e) {
			throw e;
		} catch (IOException e) {
			throw e;
		} finally {
			if (input != null)
				input.close();
		}
	}

	private String getRequiredFacesMessage(FacesContext context, UIFileUpload fileUpload) {
		if (fileUpload.getRequiredMessage() != null )
			return fileUpload.getRequiredMessage();

		return Resources._T(Resources.JSF_VALIDATE_REQUIRED, fileUpload.getClientId(context));
	}

	private boolean isFieldRequired(UIFileUpload fileUpload) {
		return Boolean.TRUE.equals(fileUpload.getRequired());
	}

	private boolean isFieldNull(FileItemStream item) {
		return "".equals(item.getName());
	}

	private InputStream openStream(FileItemStream item, UIFileUpload fileUpload) throws IOException {
		if (fileUpload != null && fileUpload.getMaxSize() != null)
			return new LimitedSizeInputStream(item, fileUpload.getMaxSize());
		
		return item.openStream();
	}
	
	private class LimitedSizeInputStream extends InputStream {
		private FileItemStream item;
		private long maxSize;
		private long count;
		private InputStream stream;
		
		public LimitedSizeInputStream(FileItemStream item, long maxSize) {
			this.maxSize = maxSize;
			count = 0;
			this.item = item;
		}
		
		private InputStream getInputStream() throws IOException {
			if (stream == null)
				stream = item.openStream();
			
			return stream;
		} 

		@Override
		public int read() throws IOException {
			int ret = getInputStream().read();
			
			if (ret != -1) {
				count++;
				checkLimit();
			}
			
			return ret;
		}
		
		@Override
		public int read(byte[] b) throws IOException {
			int ret = getInputStream().read(b);
			
			if (ret != -1) {
				count += ret;
				checkLimit();
			}

			return ret;
		}
		
		@Override
		public int read(byte[] b, int off, int len) throws IOException {
			int ret = getInputStream().read(b, off, len);
			
			if (ret != -1) {
				count += ret;
				checkLimit();
			}
			
			return ret;
		}
		
		private void checkLimit() throws IOException {
			if (count > maxSize)
				throw new ExceedMaxSizeException(Resources._T(Resources.JSF_UPLOADING_EXCEEDS_MAX_SIZE,
						item.getFieldName(), maxSize));
			
		}
		
		@SuppressWarnings({ "serial"})
		private class ExceedMaxSizeException extends IOException {

			public ExceedMaxSizeException() {
				super();
			}

			public ExceedMaxSizeException(String s) {
				super(s);
			}
		} 
	}
	
	private FileUploadItem createFileUploadItem(FileItemStream item, int itemIndex, Long maxSize) {
		return new FileUploadItemImpl(item, itemIndex, maxSize);
	}

	private class FileUploadProgressListener implements ProgressListener {
		private FacesContext context;
		private long lastBytesRead;
		
		public FileUploadProgressListener(FacesContext context) {
			this.context = context;
		}
		
		public void update(long pBytesRead, long pContentLength, int pItems) {
			UploadingMediator uploadingMediator = getUploadingMediator();
			
			if ((pBytesRead == pContentLength) || ((pBytesRead - lastBytesRead) > 1024 * 16)) {
				uploadingMediator.updateProgress(
						context, pBytesRead, pContentLength, pItems);
				
				if (pBytesRead == pContentLength) {
					uploadingMediator.stopUploading(context);
				}
				
				lastBytesRead = pBytesRead;
			}
		}
	}
	
	private void consumeStream(InputStream stream) throws IOException {
		try {
			byte[] buf = new byte[UPLOADING_BUFFER_SIZE];
			
			while (stream.read(buf) != -1) {
				;
			}
		} catch (IOException e) {
			throw e;
		} finally {
			if (stream != null)
				stream.close();
		}
		
	}

	private UIFileUpload findFileUploadByFieldName(UIComponent component, String fieldName) {
		List<UIComponent> children = component.getChildren();
		
		for (UIComponent child : children) {
			if (child instanceof UIFileUpload) {
				UIFileUpload fileUpload = (UIFileUpload)child;
				
				if (fileUpload.getId().equals(fieldName))
					return fileUpload;
			}
			
			UIFileUpload findInChild = findFileUploadByFieldName(child, fieldName);
			
			if (findInChild != null)
				return findInChild;
		}
		
		return null;
	}

	private class FileUploadItemImpl implements FileUploadItem {
		private FileItemStream item;
		private int itemIndex;
		private Long maxSize;
		
		public FileUploadItemImpl(FileItemStream item, int itemIndex, Long maxSize) {
			this.item = item;
			this.itemIndex = itemIndex;
			this.maxSize = maxSize;
		}
		
		public int getItemIndex() {
			return itemIndex;
		}

		public String getContentType() {
			return item.getContentType();
		}

		public String getFieldName() {
			return item.getFieldName();
		}

		public String getName() {
			return item.getName();
		}

		public InputStream openStream() throws IOException {
			if (maxSize != null)
				return new LimitedSizeInputStream(item, maxSize);
			
			return item.openStream();
		}
	}
	
/*	private List<UIFileUpload> getAllFileUploadsInForm(UIForm parentForm) {
		return getAllFileUploadsInComponent(parentForm);
	}

	private List<UIFileUpload> getAllFileUploadsInComponent(UIComponent parent) {
		List<UIFileUpload> fileUploads = new ArrayList<UIFileUpload>();
		List<UIComponent> children = parent.getChildren();
		
		for (UIComponent child : children) {
			if (child instanceof UIFileUpload) {
				fileUploads.add((UIFileUpload)child);
			} else {
				List<UIFileUpload> fileUploadsInChild = getAllFileUploadsInComponent(child);
				if (fileUploadsInChild != null)
					fileUploads.addAll(fileUploadsInChild);
			}
		}
		
		return fileUploads;
	}*/

	private void writeToFile(InputStream input, String writeTo) throws IOException {
		FileOutputStream output = new FileOutputStream(new File(writeTo));
		
		try {
			byte[] buf = new byte[UPLOADING_BUFFER_SIZE];
			int length = UIFileUpload.END_UPLOADING;
			
			while ((length = input.read(buf)) != UIFileUpload.END_UPLOADING) {
				output.write(buf, 0, length);
			}
		} catch (IOException e) {
			throw e;
		} finally {
			if (output != null)
				output.close();
		}
	}

	private void renderFileUploadField(FacesContext context, UIForm form,
			UIComponent component) throws IOException {
		ResponseWriter writer = context.getResponseWriter();
		
		if (form != null && isFirstFileUploadComponent(context, form, component)) {
			modifyFormToSupportFileUpload(context, form);
		}
		
		encodeComponent(writer, component);
	}

	private void encodeComponent(ResponseWriter writer, UIComponent component) throws IOException {
	    UIFileUpload fileUpload = (UIFileUpload)component;
	    String codeFragment;
	    if (fileUpload.getRich() == null || fileUpload.getRich() == false) {
	        codeFragment = String.format(
	                "\n<div id='%1$s_container' style='position:relative;'>" +
	                "\n<input id='%1$s' name='%1$s' type='file' />" +
	                "\n</div>", component.getId()); 
	    } else {
	        codeFragment = String.format(
	                "\n<div id='%1$s_container' style='position:relative;'>" +
	                "\n<input id='%1$s' name='%1$s' type='file'" +
	                " style='position:relative;-moz-opacity:0;filter:alpha(opacity:0);opacity:0;z-index:2;'" +
	                " onselect=\"document.getElementById('%1$s_fake_input').select();\"" +
	                " onchange=\"document.getElementById('%1$s_fake_input').value=document.getElementById('%1$s').value;\"" +
	                " onkeyup=\"document.getElementById('%1$s_fake_input').value=document.getElementById('%1$s').value;\" />" +
	                "<div class='%1$s_fake_container' style='position:absolute;top:0px;left:0px;z-index:1;'>" +
	                "\n<table cellspacing='0' cellpadding='0'><tr><td><input id='%1$s_fake_input' %2$s /></td>" + 
	                "<td>%3$s</td></tr></table>" +
	                "\n</div>" +
	                "\n</div>",
	                component.getId(), getStylingCodeFragment(fileUpload), getButtonFragment(fileUpload) 
	        );
	    }

		writer.write(codeFragment);
        if (logger.isLoggable(Level.FINEST)) {
            logger.finest(this.getClass().getSimpleName() + " : " + FacesUtils.getComponentDesc(component)+
                    " is encoded as [" + codeFragment +"\n]");
        }
	}

	private String getStylingCodeFragment(UIFileUpload fileUpload) {
		String style = fileUpload.getStyle();
		String styleClass = fileUpload.getStyleClass();
		
		if (style == null && styleClass == null) {
			style = "width:160px";
			styleClass = "x-form-text x-form-field";
		}
		
		return getStyleClassCodeFragment(styleClass) + getStyleCodeFragment(style);
	}

	private String getStyleCodeFragment(String style) {
		if (style == null)
			return "";
		
		return "style='" + style + "'";
	}

	private String getStyleClassCodeFragment(String styleClass) {
		if (styleClass == null)
			return "";
		
		return " class='" + styleClass + "'";
	}

	private String getButtonFragment(UIFileUpload fileUpload) {
		if (fileUpload.getBrowseIcon() != null) {
			return "<a><img src='" + fileUpload.getBrowseIcon() + "'></a>";
		} else {
			return getExtButtonCodeFragment(fileUpload.getId());
		}
	}
	
	private String getExtButtonCodeFragment(String componentId) {
		String codeFragment = String.format(
				"<table style='width: auto;' id='%1$s_btn_table'" +
				" class='x-btn-wrap x-btn' border='0' cellpadding='0' cellspacing='0'>" +
				"<tbody>" +
				"<tr>" +
				"<td class='x-btn-left'>" +
				"<i>&nbsp;</i>" +
				"</td>" +
				"<td class='x-btn-center'>" +
				"<em unselectable='on'>" +
				"<button id='%1$s' class=''x-btn-text' type='button'>" +
					getButtonValue() +
				"</button>" +
				"</em>" +
				"</td>" +
				"<td class='x-btn-right'>" +
				"<i>&nbsp;</i>" +
				"</td>" +
				"</tr>" +
				"</tbody>" +
				"</table>",
					componentId
		);
		
		return codeFragment;
	}

	private String getButtonValue() {
		return Resources._T(Resources.UI_FILE_UPLOAD_BROWSE);
	}

	private void modifyFormToSupportFileUpload(FacesContext context,
			UIForm form) throws IOException {
		ResponseWriter writer = context.getResponseWriter();
		String code= getFileUploadSupportCode(context, form);
		writer.write(code);
		if (logger.isLoggable(Level.FINEST)) {
		    logger.finest(this.getClass().getSimpleName() + " : Form of id '" + form.getId() + 
		            "' is modified to support file upload, by javascript [\n" + code + "\n]");
		}
	}

	private String getFileUploadSupportCode(FacesContext context, UIForm form) {
		return String.format(
				"\n<script type=\"text/javascript\">" +
				"\n<!--" +
				"\nvar currentTime = new Date().getTime()" +
				"\nOM.ajax.ajaxSubmit = OM.ajax.submit;" +
				"\nOM.ajax.submit = function(source, url, params, immediate) {" +
				"\nvar sourceForm = this.getParentForm(source);" +
				"\nvar %1$s = document.getElementById('%2$s');" +
				"\nif (sourceForm == %1$s) {" + 
				"\nvar %1$s_oldEnctype = %1$s.enctype;" +
				"\nvar %1$s_oldTarget = %1$s.target;" +
				"\n%1$s.enctype = 'multipart/form-data';" +
				"\n%1$s.target = '%3$s';" +
				"%4$s" +
				"\n%1$s.nonAjaxSubmit();" +
				"\n%1$s.enctype = %1$s_oldEnctype;" +
				"\n%1$s.target = %1$s_oldTarget;" +
				"\n}"+
				"\nOM.ajax.ajaxSubmit(source, url, params, immediate);" +
				"\n};" +
				"\n//-->" +
				"\n</script>" +
				"\n<iframe id='%3$s' name='%3$s' style='display:none'></iframe>",
				FacesUtils.makeJavascriptIdentifier(form.getClientId(context)) + "_var", form.getClientId(context),
				getHiddenIFrameName(context, form), getProgressSupportCode(context, form)
		);
	}

	private Object getProgressSupportCode(FacesContext context, UIForm form) {
		UIFileUploadProgress fileUploadProgress = FacesUtils.findComponent(
				form, UIFileUploadProgress.class);
		
		if (fileUploadProgress == null)
			return "";
		
		AjaxProgress progress = (AjaxProgress)fileUploadProgress.getChildren().get(2);
		return "\n" + FacesUtils.getJsvar(context, progress) + ".start();";
	}

	private Object getHiddenIFrameName(FacesContext context, UIForm form) {
		return form.getClientId(context) + "_file_upload_iframe";
	}

	private boolean isFirstFileUploadComponent(FacesContext context,
			UIForm form, UIComponent component) {
		UIFileUpload firstFileUpload = findFirstFileUploadComponent(form);
		
		return (firstFileUpload != null) && component.getId().equals(firstFileUpload.getId());
	}
	
	@SuppressWarnings("unchecked")
	private UIFileUpload findFirstFileUploadComponent(UIComponent component) {
		List children = component.getChildren();
		
		for (int i = 0; i < children.size(); i++) {
			if (children.get(i) instanceof UIFileUpload)
				return (UIFileUpload)children.get(i);
			
			UIFileUpload findInChildren = findFirstFileUploadComponent((UIComponent)children.get(i));
			
			if (findInChildren != null)
				return findInChildren;
		}
		
		return null;
	}
}
