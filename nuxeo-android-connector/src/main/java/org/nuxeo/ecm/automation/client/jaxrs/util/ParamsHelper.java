package org.nuxeo.ecm.automation.client.jaxrs.util;

import java.util.Date;

import org.nuxeo.ecm.automation.client.jaxrs.model.DateUtils;

public class ParamsHelper {

	public static String encodeParam(String[] param) {
		if (param==null || param.length==0) {
			return "";
		}
		StringBuffer sb = new StringBuffer();
		for (String value : param) {
			sb.append(value);
			sb.append(",");
		}

		String result = sb.toString();
		result = result.substring(0, result.length()-1);
		return result;
	}

	public static String encodeParam(Date param) {
		return DateUtils.formatDate(param);
	}

}
