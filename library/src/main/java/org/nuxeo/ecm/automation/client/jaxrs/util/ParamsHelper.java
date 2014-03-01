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

package org.nuxeo.ecm.automation.client.jaxrs.util;

import org.nuxeo.ecm.automation.client.jaxrs.model.DateUtils;

import java.util.Date;

public class ParamsHelper {

    public static String encodeParam(String[] param) {
        if (param == null || param.length == 0) {
            return "";
        }
        StringBuffer sb = new StringBuffer();
        for (String value : param) {
            sb.append(value);
            sb.append(",");
        }

        String result = sb.toString();
        result = result.substring(0, result.length() - 1);
        return result;
    }

    public static String encodeParam(Date param) {
        return DateUtils.formatDate(param);
    }

}
