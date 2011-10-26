/*
 * (C) Copyright 2011 Nuxeo SAS (http://nuxeo.com/) and contributors.
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
 *     Nuxeo - initial API and implementation
 */

package org.nuxeo.android.cache.sql;

import java.util.Map;

import org.nuxeo.ecm.automation.client.jaxrs.OperationRequest;
import org.nuxeo.ecm.automation.client.jaxrs.Session;
import org.nuxeo.ecm.automation.client.jaxrs.model.FileBlob;
import org.nuxeo.ecm.automation.client.jaxrs.model.OperationDocumentation;
import org.nuxeo.ecm.automation.client.jaxrs.model.OperationInput;
import org.nuxeo.ecm.automation.client.jaxrs.spi.DefaultOperationRequest;
import org.nuxeo.ecm.automation.client.jaxrs.spi.DefaultSession;

public class OperationPersisterHelper {

    public static OperationRequest rebuildOperation(Session session,
            String operationId, String jsonParams, String jsonHeaders,
            String jsonCtx, final String inputType, final String inputRef,
            Boolean inputBin) {

        OperationDocumentation op = session.getOperation(operationId);
        Map<String, String> params = JSONHelper.readMapFromJson(jsonParams);
        Map<String, String> headers = JSONHelper.readMapFromJson(jsonHeaders);
        Map<String, String> ctx = JSONHelper.readMapFromJson(jsonCtx);

        OperationInput input = null;
        if (inputType != null) {
            if (inputBin) {
                input = new FileBlob(null);
                // XXX read Binary here
            } else {
                input = new OperationInput() {

                    @Override
                    public boolean isBinary() {
                        return false;
                    }

                    @Override
                    public String getInputType() {
                        return inputType;
                    }

                    @Override
                    public String getInputRef() {
                        return inputRef;
                    }
                };
            }
        }
        return new DefaultOperationRequest((DefaultSession) session, op,
                params, headers, ctx, input);
    }

}
