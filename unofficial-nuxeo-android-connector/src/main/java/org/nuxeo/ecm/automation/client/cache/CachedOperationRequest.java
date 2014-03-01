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

package org.nuxeo.ecm.automation.client.cache;

import org.nuxeo.ecm.automation.client.jaxrs.OperationRequest;

public class CachedOperationRequest {

    protected final OperationRequest request;

    protected final String operationKey;

    protected final OperationType opType;

    public CachedOperationRequest(OperationRequest request,
            String operationKey, OperationType opType) {
        this.request = request;
        this.operationKey = operationKey;
        this.opType = opType;
    }

    public OperationRequest getRequest() {
        return request;
    }

    public String getOperationKey() {
        return operationKey;
    }

    public OperationType getOpType() {
        return opType;
    }

}
