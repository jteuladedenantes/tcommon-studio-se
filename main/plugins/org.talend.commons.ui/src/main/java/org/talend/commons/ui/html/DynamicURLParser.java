// ============================================================================
//
// Copyright (C) 2006-2016 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.commons.ui.html;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Properties;

import org.eclipse.ui.internal.intro.impl.util.Log;
import org.eclipse.ui.internal.intro.impl.util.StringUtil;

/**
 * DOC talend class global comment. Detailled comment
 */
public class DynamicURLParser {

    // private String url_string = null;
    private boolean hasProtocol = false;

    private boolean isIntroUrl = false;

    private URL urlInst = null;

    /**
     * Constructor that gets the URL to parse.
     */
    public DynamicURLParser(String url) {
        // create a URL instance, and parser it for parameters.
        parseUrl(url);
    }

    private void parseUrl(String url) {
        if (url == null) {
            return;
        }
        urlInst = null;
        try {
            urlInst = new URL(url);
        } catch (MalformedURLException e) {
            // not a valid URL. set state.
            return;
        }

        if (urlInst.getProtocol() != null) {
            // URL has some valid protocol. Check to see if it is an intro url.
            hasProtocol = true;
            isIntroUrl = isIntroUrl(urlInst);
            return;
        }

        // not an Intro URL. do nothing.
        return;
    }

    /**
     * @return Returns the hasProtocol.
     */
    public boolean hasProtocol() {
        return hasProtocol;
    }

    /**
     * @return Returns the isIntroUrl.
     */
    public boolean hasIntroUrl() {
        return isIntroUrl;
    }

    /**
     * @return Returns the currebt url Protocol.
     */
    public String getProtocol() {
        return urlInst.getProtocol();
    }

    /**
     * @return Returns the currebt url Protocol.
     */
    public String getHost() {
        return urlInst.getHost();
    }

    /**
     * Checks to see if tha passed URL is an Intro URL. An intro URL is an http URL that has the intro plugin id as a
     * host. eg: "http://org.eclipse.ui.intro/test".
     * 
     * @param url
     * @return true if url is an intro URL.
     */
    private boolean isIntroUrl(URL url) {
        if (!url.getProtocol().equalsIgnoreCase(IntroURL.INTRO_PROTOCOL)) {
            // quick exit. If it is not http, url is not an Intro url.
            return false;
        }

        if (url.getHost().equalsIgnoreCase(IntroURL.INTRO_HOST_ID)) {
            return true;
        }

        return false;
    }

    /**
     * @return Returns the introURL. Will be null if the parsed URL is not an Intro URL.
     */
    public DynamicHtmlURL getIntroURL() {
        DynamicHtmlURL introURL = null;
        if (isIntroUrl) {
            // valid intro URL. Extract the action and parameters.
            String action = getPathAsAction(urlInst);
            Properties parameters = getQueryParameters(urlInst);

            // class instance vars are already populated by now.
            introURL = new DynamicHtmlURL(action, parameters);
        }
        return introURL;
    }

    /**
     * Retruns the path attribute of the passed URL, stripped out of the leading "/". Returns null if the url does not
     * have a path.
     * 
     * @param url
     * @return
     */
    private String getPathAsAction(URL url) {
        // get possible action.
        String action = url.getPath();
        // remove leading "/" from path.
        if (action != null) {
            action = action.substring(1);
        }
        return action;
    }

    /**
     * Retruns the Query part of the URL as an instance of a Properties class.
     * 
     * @param url
     * @return
     */
    public Properties getQueryParameters(URL url) {
        // parser all query parameters.
        Properties properties = new Properties();
        String query = url.getQuery();
        if (query == null) {
            // we do not have any parameters in this URL, return an empty
            // Properties instance.
            return properties;
        }
        // now extract the key/value pairs from the query.
        String[] params;
        if (query.indexOf("&amp;") != -1) {
            query = query.replaceAll("&amp;", "&");
        }
        params = StringUtil.split(query, "&"); //$NON-NLS-1$
        for (String param : params) {
            // for every parameter, ie: key=value pair, create a property
            // entry. we know we have the key as the first string in the array,
            // and the value as the second array.
            String[] keyValuePair = StringUtil.split(param, "="); //$NON-NLS-1$
            if (keyValuePair.length != 2) {
                Log.warning("Ignoring the following Intro URL parameter: " //$NON-NLS-1$
                        + param);
                continue;
            }

            String key = urlDecode(keyValuePair[0]);
            if (key == null) {
                Log.warning("Failed to URL decode key: " + keyValuePair[0]); //$NON-NLS-1$
                continue;
            }

            String value = urlDecode(keyValuePair[1]);
            if (value == null) {
                Log.warning("Failed to URL decode value: " + keyValuePair[1]); //$NON-NLS-1$
                continue;
            }

            properties.setProperty(key, value);
        }
        return properties;
    }

    /*
     * Note: This was copied and adapted from org.eclipse.help.internal.util.URLCoder
     */
    private static String urlDecode(String encodedURL) {
        int len = encodedURL.length();
        ByteArrayOutputStream os = new ByteArrayOutputStream(len);

        try {
            for (int i = 0; i < len;) {
                switch (encodedURL.charAt(i)) {
                case '%':
                    if (len >= i + 3) {
                        os.write(Integer.parseInt(encodedURL.substring(i + 1, i + 3), 16));
                    }
                    i += 3;
                    break;
                case '+': // exception from standard
                    os.write(' ');
                    i++;
                    break;
                default:
                    os.write(encodedURL.charAt(i++));
                    break;
                }
            }
            return new String(os.toByteArray(), "UTF8"); //$NON-NLS-1$
        } catch (UnsupportedEncodingException ex) {
            return null;
        }
    }

}
