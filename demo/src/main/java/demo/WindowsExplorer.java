/*
 * $Id: WindowsExplorer.java,v 1.3 2008/02/22 13:19:47 lishaochuan Exp $
 *
 * Copyright (C) 2006 Operamasks Community.
 * Copyright (C) 2000-2006 Apusic Systems, Inc.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see http://www.gnu.org/licenses.
 */
package demo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.operamasks.faces.annotation.ManagedBean;
import org.operamasks.faces.annotation.ManagedBeanScope;

@ManagedBean(name="WindowsExplorer", scope=ManagedBeanScope.SESSION)
public class WindowsExplorer {
	public List<File> getFiles(File file) {
		if (file == null) {
			return Arrays.asList(new File[] {
					new File("My Documents", true)
			});
		}
		
		String fileName = file.getName();
		if (fileName.equals("My Documents")) {
			return Arrays.asList(new File[] {
					new File("My Music", true),
					new File("My Pictures", true),
					new File("Text 1.txt", false),
					new File("Text 2.txt", false),
					new File("Rich Text 1.doc", false),
					new File("Spreadsheet 1.xsl", false)
			});
		}
		
		if (fileName.equals("My Music")) {
			return Arrays.asList(new File[] {
					new File("Song 1.wma", false),
					new File("Song 2.wma", false),
					new File("Song 3.wma", false)
			});
		}
		
		if (fileName.equals("My Pictures")) {
			return Arrays.asList(new File[] {
				new File("Picture 1.gif", false),	
				new File("Picture 2.jpg", false),
				new File("Picture 3.jpg", false),
			});
		}
		
		return new ArrayList<File>();
	}
	
	public String getIcon(File file) {
		if (file.getName().equals("My Documents"))
			return "../tree/../tree/images/my_documents.gif";
		
		if (file.getName().equals("My Music"))
			return "../tree/images/my_music.gif";
		
		if (file.getName().equals("My Pictures"))
			return "../tree/images/my_pictures.gif";
		
		String postfix = getFileNamePostfix(file.getName());
		
		if ("wma".equals(postfix))
			return "../tree/images/document_music.gif";
		
		if ("gif".equals(postfix))
			return "../tree/images/document_gif.gif";
		
		if ("jpg".equals(postfix))
			return "../tree/images/document_jpg.gif";

		if ("txt".equals(postfix))
			return "../tree/images/document_text.gif";

		if ("doc".equals(postfix))
			return "../tree/images/document_word.gif";
		
		if ("xsl".equals(postfix))
			return "../tree/images/document_excel.gif";
		
		return null;		
	}
	
	private String getFileNamePostfix(String name) {
		return name.substring(name.lastIndexOf(".") + 1, name.length());
	}
}