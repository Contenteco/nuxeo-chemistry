/* 
 * Copyright (c) 2006-2011 Nuxeo SA (http://nuxeo.com/) and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Stephane Lacoin (aka matic)
 */
package org.nuxeo.ecm.core.opencmis.impl.client.protocol.http;

import java.net.URLStreamHandler;
import java.net.URLStreamHandlerFactory;

public class HttpURLStreamHandlerFactory implements URLStreamHandlerFactory {

    HttpURLClientProvider provider = new HttpURLMultiThreadedClientProvider();

    protected HttpURLStreamHandlerFactory(HttpURLClientProvider provider) {
        this.provider = provider;
    }

    @Override
    public URLStreamHandler createURLStreamHandler(String protocol) {
        if ("http".equals(protocol)) {
            return new HttpURLStreamHandler(provider);
        }
        return null;
    }

}
