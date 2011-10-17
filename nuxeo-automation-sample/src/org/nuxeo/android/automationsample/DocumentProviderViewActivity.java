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

package org.nuxeo.android.automationsample;

import org.nuxeo.android.documentprovider.DocumentProvider;
import org.nuxeo.android.documentprovider.LazyUpdatableDocumentsList;

public class DocumentProviderViewActivity extends BaseSampleDocumentsListActivity {

	public static final String PROVIDER_NAME_PARAM = "providerName";

	@Override
	protected LazyUpdatableDocumentsList fetchDocumentsList(byte cacheParam) throws Exception {
		DocumentProvider docProvider = getAutomationClient().getDocumentProvider();
		String providerName = getIntent().getExtras().getString(PROVIDER_NAME_PARAM);
		return docProvider.getDocumentsList(providerName, getNuxeoSession());
	}


}
