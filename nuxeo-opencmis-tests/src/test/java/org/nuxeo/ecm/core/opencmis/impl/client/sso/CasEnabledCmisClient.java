/*
 * (C) Copyright 2010 Nuxeo SA (http://nuxeo.com/) and contributors.
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
 *     Stephane Lacoin (aka matic)
 */
package org.nuxeo.ecm.core.opencmis.impl.client.sso;

import java.util.HashMap;
import java.util.Map;

import org.apache.chemistry.opencmis.client.api.CmisObject;
import org.apache.chemistry.opencmis.client.api.Repository;
import org.apache.chemistry.opencmis.client.api.Session;
import org.apache.chemistry.opencmis.client.api.SessionFactory;
import org.apache.chemistry.opencmis.client.runtime.SessionFactoryImpl;
import org.apache.chemistry.opencmis.commons.SessionParameter;
import org.apache.chemistry.opencmis.commons.enums.BindingType;
import org.apache.chemistry.opencmis.commons.exceptions.CmisConnectionException;
import org.apache.chemistry.opencmis.commons.exceptions.CmisRuntimeException;
import org.apache.commons.httpclient.HttpClient;
import org.nuxeo.ecm.core.opencmis.impl.client.protocol.http.HttpURLInstaller;
import org.nuxeo.ecm.core.opencmis.impl.client.protocol.http.NullAuthenticationProvider;

/**
 * CMIS client authenticated using CAS. Fetch documents using the atomPUB protocol.
 *
 * The authentication outside the scope of chemistry. We've managed to direct
 * chemistry for using the authenticated http client.
 *
 * @author matic
 *
 */
public class CasEnabledCmisClient {

    protected HttpClient client;

    public final String location;

    protected Session session;

    public CasEnabledCmisClient(String location) {
        this.location = location;
    }

    public void restoreThreadContext() {
        HttpURLInstaller.INSTANCE.setClient(client);
    }

    public CasGreeter newGreeter() {
        client = HttpURLInstaller.INSTANCE.getClient();
        return new CasGreeter(client, location);
    }

    public Session newSession() throws Exception {
        // Default factory implementation of client runtime.
        SessionFactory sessionFactory = SessionFactoryImpl.newInstance();
        Map<String, String> parameter = new HashMap<String, String>();

        // Where to go
        parameter.put(SessionParameter.ATOMPUB_URL, location); // URL

        // Do not set authentication header
        parameter.put(SessionParameter.AUTHENTICATION_PROVIDER_CLASS, NullAuthenticationProvider.class.getName());                                                                                     // to                                                                                     // server.

        // parameter.put(SessionParameter.REPOSITORY_ID, "myRepository"); //
        // Only necessary if there is more than one repository.
        parameter.put(SessionParameter.BINDING_TYPE, BindingType.ATOMPUB.value());

        // Session locale.
        parameter.put(SessionParameter.LOCALE_ISO3166_COUNTRY, "");
        parameter.put(SessionParameter.LOCALE_ISO639_LANGUAGE, "en");
        parameter.put(SessionParameter.LOCALE_VARIANT, "US");

        // Create session.
        try {
            // This supposes only one repository is available at the URL.
            Repository soleRepository = sessionFactory.getRepositories(parameter).get(0);
            return session = soleRepository.createSession();
        } catch (CmisConnectionException e) {
            // The server is unreachable
            throw new Error("Server unreachable", e);
        } catch (CmisRuntimeException e) {
            // The user/password have probably been rejected by the server.
            throw new Error("Security error ?", e);
        }
    }

    public static void main(String args[]) throws Exception {

        HttpURLInstaller.install();

        CasEnabledCmisClient cmis = new CasEnabledCmisClient("http://127.0.0.1:8080/nuxeo/atom/cmis");

        String ticketGranting = cmis.newGreeter().credsLogon("slacoin", "slacoin");

        assert ticketGranting != null;

//        client.client = installClient();
//
//        String secondTicket = client.logon(firstTicket);
//
//        assert firstTicket.equals(secondTicket);

        Session session = cmis.newSession();
        CmisObject root = session.getRootFolder();

        assert root != null;
    }

}