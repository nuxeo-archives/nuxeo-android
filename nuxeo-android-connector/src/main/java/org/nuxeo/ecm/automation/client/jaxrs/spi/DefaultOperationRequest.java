/*
 * (C) Copyright 2006-2008 Nuxeo SAS (http://nuxeo.com/) and contributors.
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
 *     bstefanescu
 */
package org.nuxeo.ecm.automation.client.jaxrs.spi;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.nuxeo.ecm.automation.client.cache.CacheBehavior;
import org.nuxeo.ecm.automation.client.jaxrs.AsyncCallback;
import org.nuxeo.ecm.automation.client.jaxrs.ExecutionDependencies;
import org.nuxeo.ecm.automation.client.jaxrs.OperationRequest;
import org.nuxeo.ecm.automation.client.jaxrs.Session;
import org.nuxeo.ecm.automation.client.jaxrs.model.OperationDocumentation;
import org.nuxeo.ecm.automation.client.jaxrs.model.OperationInput;
import org.nuxeo.ecm.automation.client.jaxrs.model.OperationDocumentation.Param;
import org.nuxeo.ecm.automation.client.jaxrs.util.ParamsHelper;

/**
 * @author <a href="mailto:bs@nuxeo.com">Bogdan Stefanescu</a>
 * @author tiry
 */
public class DefaultOperationRequest implements OperationRequest {

	protected final OperationDocumentation op;

	protected final DefaultSession session;

	protected final Map<String, String> params;

	protected final Map<String, String> ctx;

	protected final Map<String, String> headers;

	protected OperationInput input;

	protected boolean cachable = true;

	protected boolean refresh = false;

	protected ExecutionDependencies dependencies;

	public DefaultOperationRequest(DefaultSession session,
			OperationDocumentation op) {
		this(session, op, new HashMap<String, String>());
	}

	public DefaultOperationRequest(DefaultSession session,
			OperationDocumentation op, Map<String, String> ctx) {
		this.session = session;
		this.op = op;
		params = new HashMap<String, String>();
		headers = new HashMap<String, String>();
		this.ctx = ctx;
	}

	public DefaultOperationRequest(DefaultSession session,
			OperationDocumentation op, Map<String, String> params,
			Map<String, String> headers, Map<String, String> ctx,
			OperationInput input) {
		this.session = session;
		this.op = op;
		this.params = new HashMap<String, String>(params);
		this.headers = new HashMap<String, String>(headers);
		this.ctx = new HashMap<String, String>(ctx);
		this.input = input;
	}

	public OperationRequest clone() {
		return clone(session);
	}

	public OperationRequest clone(Session session) {
		DefaultOperationRequest clone = new DefaultOperationRequest(
				(DefaultSession) session, op, params, headers, ctx, input);
		return clone;
	}

	public DefaultSession getSession() {
		return session;
	}

	protected final boolean acceptInput(String type) {
		for (int i = 0, size = op.signature.length; i < size; i += 2) {
			if ("void".equals(op.signature[i])) {
				return true;
			}
			if (type.equals(op.signature[i])) {
				return true;
			}
		}
		return false;
	}

	protected final void checkInput(String type) {
		if (!acceptInput(type)) {
			throw new IllegalArgumentException("Input not supported: " + type);
		}
	}

	public List<String> getParamNames() {
		List<String> result = new ArrayList<String>();
		for (Param param : op.params) {
			result.add(param.name);
		}
		return result;
	}

	public Param getParam(String key) {
		for (Param param : op.params) {
			if (key.equals(param.name)) {
				return param;
			}
		}
		return null;
	}

	public OperationRequest setInput(OperationInput input) {
		if (input == null) {
			checkInput("void");
		} else {
			checkInput(input.getInputType());
		}
		this.input = input;
		return this;
	}

	public OperationInput getInput() {
		return input;
	}

	public String getUrl() {
		return session.getClient().getBaseUrl() + op.url;
	}

	public OperationRequest set(String key, Object value) {
		Param param = getParam(key);
		if (param == null) {
			throw new IllegalArgumentException("No such parameter '" + key
					+ "' for operation " + op.id + ".\n\tAvailable params: "
					+ getParamNames());
		}
		if (value == null) {
			params.remove(key);
			return this;
		}
		// handle strings and primitive differently
		// TODO
		// if (!param.type.equals(value.getParamType())) {
		// throw new
		// IllegalArgumentException("Invalid parameter type:
		// "+value.getParamType());
		// }
		String encodedValue = null;
		if (value.getClass() == Date.class) {
			encodedValue = ParamsHelper.encodeParam((Date) value);
		} else if (value instanceof String[]) {
			encodedValue = ParamsHelper.encodeParam((String[]) value);
		} else {
			encodedValue = value.toString();
		}
		params.put(key, encodedValue);

		return this;
	}

	public OperationRequest setContextProperty(String key, String value) {
		ctx.put(key, value);
		return this;
	}

	public Map<String, String> getContextParameters() {
		return ctx;
	}

	public Map<String, String> getParameters() {
		return params;
	}

	protected void setCacheFlags(byte cacheFlags) {
		this.refresh = (cacheFlags & CacheBehavior.FORCE_REFRESH) == CacheBehavior.FORCE_REFRESH;
		this.cachable = (cacheFlags & CacheBehavior.STORE) == CacheBehavior.STORE;
	}

	public Object execute(byte cacheFlags) throws Exception {
		setCacheFlags(cacheFlags);
		return session.execute(this);
	}

	public Object execute() throws Exception {
		return session.execute(this);
	}

	public String execute(AsyncCallback<Object> cb) {
		return session.execute(this, cb);
	}

	public String execute(AsyncCallback<Object> cb, byte cacheFlags) {
		setCacheFlags(cacheFlags);
		return session.execute(this, cb);
	}

	public OperationRequest setHeader(String key, String value) {
		headers.put(key, value);
		return this;
	}

	public Map<String, String> getHeaders() {
		return headers;
	}

	public boolean isCachable() {
		return cachable;
	}

	public boolean isForceRefresh() {
		return refresh;
	}

	public void forceCache() {
		cachable = false;
		refresh = false;
	}

	public OperationDocumentation getDocumentation() {
		return op;
	}

	public ExecutionDependencies getDependencies() {
		if (dependencies == null) {
			dependencies = new ExecutionDependencies();
		}
		return dependencies;
	}

    public boolean hasDependencies() {
    	return dependencies!= null && !dependencies.resolved();
    }

}
