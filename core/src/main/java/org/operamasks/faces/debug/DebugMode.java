/*
 * $$Id: DebugMode.java,v 1.2 2008/01/30 07:58:14 yangdong Exp $$
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

package org.operamasks.faces.debug;

public enum DebugMode {
	// Lifecycle phases
	RESTORE_VIEW,
	APPLY_REQUEST_VALUES,
	PROCESS_VALIDATIONS,
	UPDATE_MODE_VALUES,
	INVOKE_APPLICATION,
	RENDER_RESPONSE,
	
	// Component lifecycle
	INCLUDE_COMPONENT_IDS,
	EXCLUDE_COMPONENT_IDS,
	INCLUDE_COMPONENT_CLASSES,
	EXCLUDE_COMPONENT_CLASSES,
	INCLUDE_COMPONENT_METHODS,
	EXCLUDE_COMPONENT_METHODS,
	
	// Renderer lifecycle
	INCLUDE_RENDERER_TYPES,
	EXCLUDE_RENDERER_TYPES,
	INCLUDE_RENDERER_METHODS,
	EXCLUDE_RENDERER_METHODS,
	
	// Others
	EXCEPTION,
	UNCOMPRESSED_JS,
	MISC
}