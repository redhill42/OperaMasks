/*
 * $Id: ClientValidatorHandler.java,v 1.2 2008/02/28 02:32:43 yangdong Exp $
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
package org.operamasks.faces.facelets.ajax;

import java.util.Iterator;

import javax.faces.validator.Validator;

import org.operamasks.faces.webapp.ajax.ClientValidatorImpl;

import com.sun.facelets.FaceletContext;
import com.sun.facelets.tag.TagAttribute;
import com.sun.facelets.tag.TextHandler;
import com.sun.facelets.tag.jsf.ValidateHandler;
import com.sun.facelets.tag.jsf.ValidatorConfig;

public class ClientValidatorHandler extends ValidateHandler
{
    private TagAttribute message;

    public ClientValidatorHandler(ValidatorConfig config) {
        super(config);
    }

    @SuppressWarnings("unchecked")
	protected Validator createValidator(FaceletContext ctx) {
        String msg = (this.message != null) ? this.message.getValue(ctx) : null;

        StringBuilder script = new StringBuilder();
        Iterator iter = findNextByType(TextHandler.class);
        while (iter.hasNext()) {
            TextHandler text = (TextHandler)iter.next();
            script.append(text.getText(ctx));
        }

        return new ClientValidatorImpl(msg, script.toString());
    }
}
