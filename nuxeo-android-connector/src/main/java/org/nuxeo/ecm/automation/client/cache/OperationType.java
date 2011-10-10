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

public enum OperationType {

	UPDATE("Update"), CREATE("Create"), DELETE("Delete");

	private final String value;

	OperationType(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}

	public static OperationType fromString(String value) {
        for (OperationType nit : OperationType.values()) {
            if (nit.getValue().equals(value)) {
                return nit;
            }
        }
        return null;
    }

	@Override
	public String toString() {
		return getValue();
	}
}
