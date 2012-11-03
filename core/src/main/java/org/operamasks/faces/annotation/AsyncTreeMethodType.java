/*
 * $Id: AsyncTreeMethodType.java,v 1.1 2008/02/22 10:04:18 yangdong Exp $
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

package org.operamasks.faces.annotation;

public enum AsyncTreeMethodType {
	asyncData {
		@Override
		public String toString() {
			return "asyncData";
		}
	},
	nodeText {
		@Override
		public String toString() {
			return "nodeText";
		}
	},
	nodeImage {
		@Override
		public String toString() {
			return "nodeImage";
		}
	},
	nodeHasChildren {
		@Override
		public String toString() {
			return "nodeHasChildren";
		}
	},
	nodeClass {
		@Override
		public String toString() {
			return "nodeClass";
		}
	},
	initAction {
		@Override
		public String toString() {
			return "initAction";
		}
	},
	postCreate {
		@Override
		public String toString() {
			return "postCreate";
		}
	},
	NULL {
		@Override
		public String toString() {
			return null;
		}
	}
}
