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

package org.nuxeo.ecm.automation.client.jaxrs;

public class Dependency {

    public enum DependencyType {

        OPERATION_REQUEST("OperationRequest"), FILE_UPLOAD("FileUpload");

        private final String value;

        DependencyType(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        @Override
        public String toString() {
            return getValue();
        }

        public static DependencyType fromString(String value) {
            for (DependencyType nit : DependencyType.values()) {
                if (nit.getValue().equals(value)) {
                    return nit;
                }
            }
            return null;
        }
    }

    protected DependencyType type;

    protected String token;

    public Dependency(DependencyType type, String token) {
        this.type = type;
        this.token = token;
    }

    public DependencyType getType() {
        return type;
    }

    public String getToken() {
        return token;
    }

}
