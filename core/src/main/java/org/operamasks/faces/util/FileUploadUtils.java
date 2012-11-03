package org.operamasks.faces.util;

import javax.faces.FacesException;
import javax.faces.component.UIComponent;
import javax.faces.component.html.HtmlOutputText;
import javax.faces.context.FacesContext;

import org.operamasks.faces.annotation.ManagedBeanScope;
import org.operamasks.faces.application.ManagedBeanContainer;
import org.operamasks.faces.component.ajax.AjaxProgress;
import org.operamasks.faces.component.ajax.ProgressAction;
import org.operamasks.faces.component.ajax.ProgressState;
import org.operamasks.faces.component.ajax.ProgressStatus;
import org.operamasks.faces.component.widget.UIFileUploadProgress;
import org.operamasks.faces.component.widget.UIProgressBar;
import org.operamasks.faces.component.widget.fileupload.UploadingMediator;
import org.operamasks.faces.component.widget.fileupload.UploadingStatus;
import org.operamasks.faces.config.ManagedBeanConfig;
import org.operamasks.resources.Resources;

public class FileUploadUtils {
	private static final String KEY_PROGRESS_ID = "org.operamasks.faces.webapp.widget.FileUploadProgressTag.progressId";
	
	public static void decorateFileUploadProgress(FacesContext context,
			UIFileUploadProgress fileUploadProgress) {
		UIComponentFactory factory = new UIComponentFactory();
    	HtmlOutputText output = (HtmlOutputText)factory.createComponent(context,
    			HtmlOutputText.COMPONENT_TYPE);
    	UIProgressBar progressBar = (UIProgressBar)factory.createComponent(context,
    			UIProgressBar.COMPONENT_TYPE);
    	
    	AjaxProgress progress = (AjaxProgress)factory.createComponent(context,
    			AjaxProgress.COMPONENT_TYPE);
    	progress.setFor(progressBar.getId() + " " + output.getId());
    	progress.setAction(context.getApplication().getExpressionFactory(
    			).createMethodExpression(context.getELContext(),
    			"#{_" + progress.getId() + "_bean.processAction}", Void.class,
    			new Class[] {ProgressStatus.class}));
    	if (fileUploadProgress.getInterval() > 1)
    		progress.setInterval(fileUploadProgress.getInterval());
    	else
    		progress.setInterval(1);

    	if (fileUploadProgress.getChildCount() == 0) {
    		fileUploadProgress.getChildren().add(progressBar);
    		fileUploadProgress.getChildren().add(output);
    		fileUploadProgress.getChildren().add(progress);
    	}
    	
    	ManagedBeanConfig mb = new ManagedBeanConfig();
    	mb.setManagedBeanName("_" + progress.getId() + "_bean");
    	mb.setManagedBeanClass(ProgressMonitor.class.getName());
    	mb.setManagedBeanScope(ManagedBeanScope.REQUEST);
    	ManagedBeanContainer.getInstance().addBeanFactory(mb);
    	
    	context.getExternalContext().getRequestMap().put(KEY_PROGRESS_ID, fileUploadProgress.getId());
	}
    
    public static class ProgressMonitor {
    	public void processAction(ProgressStatus status) {
    		FacesContext context = FacesContext.getCurrentInstance();
    		String progressId = (String)context.getExternalContext().getRequestMap().get(KEY_PROGRESS_ID);
    		if (progressId == null)
    			throw new FacesException("Can't retrieve progress id from request.");
    		
    		UIFileUploadProgress fileUploadProgress = findFileUploadProgress(context, progressId);
    		
    		if (fileUploadProgress == null)
    			throw new FacesException("UIFileUploadProgress not found.");
    		
    		if (status.getAction().ordinal() == ProgressAction._START) {
    			setStatusToStart(fileUploadProgress, status);
    		} else if (status.getAction().ordinal() == ProgressAction._POLL) {
    			if (isErrorStatus()) {
    				setStatusToError(fileUploadProgress, status);
    				return;
    			}
    			
    			if (isWaittingStatus()) {
    				setWaittingStatus(status);
    				return;
    			}
    				
    			if (isCompletedStatus()) {
    				setCompletedStatus(fileUploadProgress, status);
    				return;
    			}
    			
    			setRunningStatus(fileUploadProgress, status);
    		} else if (isStoppedStatus(status)) {
    			setStoppedStatus(status);
    		}
    	}
    	
    	private void setStoppedStatus(ProgressStatus status) {
    		status.setState(ProgressState.STOPPED);
    	}

    	private boolean isStoppedStatus(ProgressStatus status) {
    		return status.getAction().ordinal() == ProgressAction._STOP;
    	}
    	
    	private boolean isWaittingStatus() {
    		return getUploadingStatus().getContentLength() == null ||
    				getUploadingStatus().getContentLength() == 0;
    	}
    	
    	private boolean isCompletedStatus() {
    		return getUploadingStatus().getContentLength().equals(
    				getUploadingStatus().getBytesRead());
    	}

    	private void setRunningStatus(UIFileUploadProgress progress, ProgressStatus status) {
    		status.setPercentage(getPercentage());
    		status.setMessage(getMessage(progress.getUploadingMessage(),
    				Resources.UI_FILE_UPLOAD_UPLOADING_MESSAGE,
    				getReadBytes(), getTotal(), getPercentage()));
    		setWaittingStatus(status);
    	}

    	private void setCompletedStatus(UIFileUploadProgress progress,
    			ProgressStatus status) {
    		status.setMessage(getMessage(progress.getCompleteMessage(),
    				Resources.UI_FILE_UPLOAD_COMPLETE_MESSAGE, getTotal()));
    		status.setPercentage(100);
    		status.setState(ProgressState.COMPLETED);
    	}

    	private void setWaittingStatus(ProgressStatus status) {
    		status.setState(ProgressState.RUNNING);
    	}
    	
		private boolean isErrorStatus() {
			return getUploadingStatus().getError() != null;
		}

		private UploadingStatus getUploadingStatus() {
			return UploadingMediator.getInstance().getUploadingStatus(
					FacesContext.getCurrentInstance());
		}
		
		private void setStatusToError(UIFileUploadProgress progress, ProgressStatus status) {
			status.setMessage(getMessage(progress.getErrorMessage(),
					Resources.UI_FILE_UPLOAD_ERROR_MESSAGE, getUploadingStatus(
					).getError().getCause().getMessage()));
			
			status.setState(ProgressState.COMPLETED);
		}
    	
    	private void setStatusToStart(UIFileUploadProgress progress, ProgressStatus status) {
    		status.setMessage(getMessage(progress.getStartMessage(),
    				Resources.UI_FILE_UPLOAD_START_MESSAGE));
    		status.setPercentage(0);
    		status.setState(ProgressState.RUNNING);
    	}
    	
		private String getMessage(String userMessage, String resourceKey, Object... params) {
			if (userMessage != null) {
				return replaceVariableHolders(userMessage);
			} else {
				return Resources._T(resourceKey, params);
			}
		}

		private String replaceVariableHolders(String message) {
			if (message == null || message.equals(""))
				return message;
			
			message = message.replace("{total}", Long.toString(getTotal()));
			message = message.replace("{readBytes}", Long.toString(getReadBytes()));
			message = message.replace("{percentage}", Long.toString(getPercentage()));
			
			if (getUploadingStatus().getError() != null &&
					getUploadingStatus().getError().getCause() != null &&
					getUploadingStatus().getError().getCause().getMessage() != null) {
				message = message.replace("{error}", getUploadingStatus().getError(
					).getCause().getMessage());
			} else {
				message = message.replace("{error}", "Unknown error");
			}
			
			return message;
		}

		private UIFileUploadProgress findFileUploadProgress(FacesContext context, String progressId) {
			return findFileUploadProgress(context, progressId, context.getViewRoot());
		}

		private UIFileUploadProgress findFileUploadProgress(FacesContext context,
				String progressId, UIComponent component) {
			for (UIComponent child : component.getChildren()) {
				if (child.getId().equals(progressId))
					return (UIFileUploadProgress)child;
				
				UIFileUploadProgress fileUploadProgress = findFileUploadProgress(context, progressId, child);
				if (fileUploadProgress != null)
					return fileUploadProgress;
			}
			
			return null;
		}
		
		private long getReadBytes() {
			return bytesToKilo(getUploadingStatus().getBytesRead());
		}

		private long getTotal() {
			return bytesToKilo(getUploadingStatus().getContentLength());
		}

		private int getPercentage() {
			if (getUploadingStatus().getContentLength() == 0)
				return 0;
			
			return (int)(100 * getUploadingStatus().getBytesRead() /
					getUploadingStatus().getContentLength());
		}
		
		public long bytesToKilo(long bytes) {
			return bytes / 1024 + ((bytes % 1024 > 0) ? 1 : 0);
		}
    }
}
