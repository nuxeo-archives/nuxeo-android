/*
 * (C) Copyright 2010-2011 Nuxeo SAS (http://nuxeo.com/) and contributors.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser General Public License
 * (LGPL) version 2.1 which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl.html
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * Contributors:
 */

package org.nuxeo.android.simpleclient;

import org.nuxeo.android.simpleclient.service.NuxeoAndroidServices;
import org.nuxeo.ecm.automation.client.jaxrs.model.Documents;

import com.smartnsoft.droid4me.framework.LifeCycle.BusinessObjectUnavailableException;

/**
 * @author Nuxeo & Smart&Soft
 * @since 2011.02.18
 */
public final class ClipboardDocumentsActivity extends MyDocumentsActivity {

    protected Documents getDocuments(boolean refresh)
            throws BusinessObjectUnavailableException {
        return NuxeoAndroidServices.getInstance().getMyWorklistContent(refresh);
    }
}
