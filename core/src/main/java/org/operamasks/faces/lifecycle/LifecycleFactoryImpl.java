/*
 * $Id: LifecycleFactoryImpl.java,v 1.4 2007/07/02 07:38:20 jacky Exp $
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

package org.operamasks.faces.lifecycle;

import javax.faces.lifecycle.LifecycleFactory;
import javax.faces.lifecycle.Lifecycle;
import javax.faces.FacesException;
import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;
import static org.operamasks.resources.Resources.*;

public class LifecycleFactoryImpl extends LifecycleFactory
{
    private Map<String,Lifecycle> lifecycles = new HashMap<String,Lifecycle>();

    public LifecycleFactoryImpl() {
        addLifecycle(LifecycleFactory.DEFAULT_LIFECYCLE, new LifecycleImpl());
    }

    public void addLifecycle(String lifecycleId, Lifecycle lifecycle) {
        if (lifecycleId == null || lifecycle == null)
            throw new NullPointerException();

        if (lifecycles.containsKey(lifecycleId))
            throw new IllegalArgumentException(_T(JSF_DUPLICATE_LIFECYCLE_ID, lifecycleId));

        lifecycles.put(lifecycleId, lifecycle);
    }

    public Lifecycle getLifecycle(String lifecycleId)
        throws FacesException
    {
        if (lifecycleId == null)
            throw new NullPointerException();

        Lifecycle result = lifecycles.get(lifecycleId);
        if (result == null)
            throw new IllegalArgumentException(_T(JSF_NO_SUCH_LIFECYCLE_ID, lifecycleId));
        return result;
    }

    public Iterator<String> getLifecycleIds() {
        return lifecycles.keySet().iterator();
    }
}
