package org.nuxeo.ecm.automation.client.cache;

import java.io.InputStream;

public class CacheEntry {

    protected InputStream is;

    protected String ctype;

    protected String disp;


    public CacheEntry(String ctype, String disp, InputStream is) {
        this.is = is;
        this.ctype=ctype;
        this.disp = disp;
    }

    public InputStream getInputStream() {
        return is;
    }

    public void setInputStream(InputStream is) {
        this.is = is;
    }

    public String getCtype() {
        return ctype;
    }

    public void setCtype(String ctype) {
        this.ctype = ctype;
    }

    public String getDisp() {
        return disp;
    }

    public void setDisp(String disp) {
        this.disp = disp;
    }

}
