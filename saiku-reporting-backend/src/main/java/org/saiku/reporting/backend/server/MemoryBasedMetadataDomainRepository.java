/*
 * Copyright (C) 2011 Marius Giepz
 *
 * This program is free software; you can redistribute it and/or modify it 
 * under the terms of the GNU General Public License as published by the Free 
 * Software Foundation; either version 2 of the License, or (at your option) 
 * any later version.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * 
 * See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along 
 * with this program; if not, write to the Free Software Foundation, Inc., 
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA 
 *
 */
package org.saiku.reporting.backend.server;

import java.io.InputStream;
import java.util.UUID;

import org.apache.commons.vfs.FileContent;
import org.apache.commons.vfs.FileObject;
import org.apache.commons.vfs.FileSystemException;
import org.apache.commons.vfs.FileSystemManager;
import org.apache.commons.vfs.VFS;
import org.pentaho.metadata.model.Domain;
import org.pentaho.metadata.repository.InMemoryMetadataDomainRepository;
import org.pentaho.metadata.util.XmiParser;

public class MemoryBasedMetadataDomainRepository {

    InMemoryMetadataDomainRepository immdr;
    
    private String metadataFile;

    public MemoryBasedMetadataDomainRepository() {

    }
    
    public MemoryBasedMetadataDomainRepository(String metadataFile) {
        createDomain(metadataFile);
    }
 
    public InMemoryMetadataDomainRepository getImmdr() {
    	return immdr;
    }

    private void createDomain(String metadataFile) {
    	
    	this.metadataFile = metadataFile;
        immdr = new InMemoryMetadataDomainRepository();
        
    	InputStream in = null;
        try {
            FileSystemManager fsManager = VFS.getManager();
            FileObject metadata = fsManager.resolveFile(metadataFile);
            
            FileContent fc = metadata.getContent(); 
            in = fc.getInputStream(); 

        } catch (FileSystemException e) {
			e.printStackTrace();
		}

        final XmiParser parser = new XmiParser();
        Domain domain = null;
        try {
            domain = parser.parseXmi(in);
            domain.setId(UUID.randomUUID().toString() + "/" + "metadata.xmi");
            immdr.storeDomain(domain, true);
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

	public String getMetadataFile() {
		return metadataFile;
	}

	public void setMetadataFile(String metadataFile) {
		createDomain(metadataFile);
	}
}
