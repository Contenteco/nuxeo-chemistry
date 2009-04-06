/*
 * (C) Copyright 2006-2008 Nuxeo SAS (http://nuxeo.com/) and contributors.
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
 *     bstefanescu
 */
package org.nuxeo.chemistry.client.app;

import java.io.Serializable;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.chemistry.Document;
import org.apache.chemistry.Folder;
import org.apache.chemistry.ObjectEntry;
import org.apache.chemistry.Policy;
import org.apache.chemistry.Relationship;
import org.apache.chemistry.property.Property;
import org.apache.chemistry.type.Type;
import org.nuxeo.chemistry.client.app.model.DataMap;

/**
 * A compact representation of an object entry to be used on ATOM clients.
 * 
 * TODO: use state to cache boolean properties?
 * 
 * @author <a href="mailto:bs@nuxeo.com">Bogdan Stefanescu</a>
 *
 */
public class APPObjectEntry extends AbstractObjectEntry {

    protected APPConnection connection;
        
    protected List<String> links;
    protected URI uri;
    protected String id;
    protected String name;
    protected String typeId;
    protected String createdBy;
    protected Calendar lastModificationDate;
    
    protected int state; //TODO locked, lifecycle etc?

    protected DataMap map;
    
    /**
     * For internal use - clients must use the other constructor.
     */
    public APPObjectEntry() {
        this (null);
    }

    public APPObjectEntry(DataMap properties) {
        links = new ArrayList<String>();
        init(properties);
    }

    public void init(DataMap map) {
        this.map = map;
    }
    
    public void attach(APPConnection connection) {
        this.connection = connection;
    }
    
    public void detach() {
        this.connection = null;
    }
    
    public APPConnection getConnection() {
        return connection;
    }
    
    
    public void pack() {
        //??
    }
        
    public void addLink(String rel, String href) {
        links.add(rel == null ? "" : rel);
        links.add(href);
    }

    public String[] getLinks() {
       return links.toArray(new String[links.size()]);  
    }
    
    public String getLink(String rel) {
        for (int i=0, len=links.size(); i<len; i+=2) {
            if (rel.equals(links.get(i))) {
                return links.get(i+1);
            }
        }
        return null;
    }
    
    public Serializable getValue(String name) {
        return (Serializable)map.get(name);
    }        

    public URI getURI() {
        if (uri == null) {
            String value = getLink("edit");
            if (uri == null) {
                value = getLink("self");
                if (uri == null) {
                    value = getLink("alternate");
                }
            }
            try {
                uri = new URI(value);
            } catch (URISyntaxException e) {
                throw new IllegalArgumentException("Not an URI: "+value);
            }
        }
        return null;
    }
        
    public String getTypeId() {
        if (typeId == null) {
            typeId = getString(Property.TYPE_ID);
        }
        return typeId;
    }
    
    public String getId() {
        if (id == null) {
            id = getString(Property.ID);
            if (id == null) {
                throw new IllegalArgumentException("A CMIS Object without ID");  
            }
        }
        return id;
    }

    public String getName() {
        if (name == null) {
            name = getString(Property.NAME);
            if (name == null) {
                name = getId();
            }
        }
        return name;
    }
    
    public String getCreatedBy() {
        if (createdBy == null) {
            createdBy = getString(Property.CREATED_BY);
        }
        return createdBy;
    }
    
    public Calendar getLastModificationDate() {
        if (lastModificationDate == null) {
            lastModificationDate = getDateTime(Property.LAST_MODIFICATION_DATE);
        }
        return lastModificationDate;
    }
        
    
    public Type getType() {
        throw new UnsupportedOperationException("Not yet implemented");
    }
    
    public Document getDocument() {
        throw new UnsupportedOperationException("Not yet implemented");
    }
    
    public Folder getFolder() {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public Policy getPolicy() {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public Collection<String> getAllowableActions() {
        throw new UnsupportedOperationException("Not yet implemented");
    }
    
    public Map<String, Property> getProperties() {
        throw new UnsupportedOperationException("Not yet implemented");
    }
    
    public Property getProperty(String name) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public Relationship getRelationship() {
        throw new UnsupportedOperationException("Not yet implemented");
    }
    
    public Collection<ObjectEntry> getRelationships() {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public boolean hasContentStream() {
        throw new UnsupportedOperationException("Not yet implemented");
    }
    
    @Override
    public String toString() {
        return getName();
    }
}