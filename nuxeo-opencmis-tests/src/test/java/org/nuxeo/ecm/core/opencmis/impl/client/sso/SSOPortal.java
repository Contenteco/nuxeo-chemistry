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

package org.nuxeo.ecm.core.opencmis.impl.client.sso;

import org.apache.chemistry.opencmis.client.api.CmisObject;
import org.apache.chemistry.opencmis.client.api.Property;
import org.apache.chemistry.opencmis.client.api.Session;
import org.apache.chemistry.opencmis.commons.SessionParameter;
import org.nuxeo.ecm.core.opencmis.impl.client.NuxeoPortalSSOAuthenticationProvider;

public class SSOPortal extends AbstractClientSupport {

    protected final String secret;

    public SSOPortal(String location, String secret) {
        super(location);
        this.secret = secret;
    }

    @Override
    protected void injectParameters() {
        super.injectParameters();
        params.put(NuxeoPortalSSOAuthenticationProvider.SECRET_KEY, secret);
        params.put(SessionParameter.AUTHENTICATION_PROVIDER_CLASS, NuxeoPortalSSOAuthenticationProvider.class.getName()); // to
                                                                                                                          // //
                                                                                                                          // server.
        params.put(SessionParameter.USER, "Administrator");
    }

    public static void main(String args[]) {
        SSOPortal client = new SSOPortal("http://localhost:8080/nuxeo/atom/cmis", "nuxeo5secretkey");
        Session session = client.connect();
        CmisObject root = session.getRootFolder();
        for (Property<?> prop : root.getProperties()) {
            String msg = String.format("%s=%s", prop.getDisplayName(), prop.getValueAsString());
            System.out.println(msg);
        }
    }

}
