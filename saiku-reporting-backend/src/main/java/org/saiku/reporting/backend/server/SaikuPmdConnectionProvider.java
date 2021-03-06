/*******************************************************************************
 * Copyright 2013 Marius Giepz
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package org.saiku.reporting.backend.server;

import java.sql.Connection;

import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.metadata.repository.IMetadataDomainRepository;
import org.pentaho.reporting.engine.classic.core.ReportDataFactoryException;
import org.pentaho.reporting.engine.classic.extensions.datasources.pmd.PmdConnectionProvider;
import org.pentaho.reporting.libraries.resourceloader.ResourceKey;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;

public class SaikuPmdConnectionProvider extends PmdConnectionProvider {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private IMetadataDomainRepository repo;

    public SaikuPmdConnectionProvider() {
    }

    public SaikuPmdConnectionProvider(IMetadataDomainRepository repo) {
        this.repo = repo;
    }

    public void setPmdConnectionProvider(IMetadataDomainRepository repo) {
        this.repo = repo;
    }

    public IMetadataDomainRepository getMetadataDomainRepository(final String domain,
            final ResourceManager resourceManager,
            final ResourceKey contextKey,
            final String xmiFile) throws ReportDataFactoryException {
        return repo;
    }

    public Connection createConnection(final DatabaseMeta databaseMeta,
            final String username,
            final String password) throws ReportDataFactoryException {
        try {
            final String realUser = (databaseMeta.getUsername() == null) ? username : databaseMeta.getUsername();
            final String realPassword = (databaseMeta.getPassword() == null) ? password : databaseMeta.getPassword();

            if (databaseMeta.getAccessType() == DatabaseMeta.TYPE_ACCESS_JNDI) {
                final String jndiName = databaseMeta.getDatabaseName();
                if (jndiName != null) {
                    try {
                        final SaikuJndiDatasourceConnectionProvider connectionProvider = new SaikuJndiDatasourceConnectionProvider();
                        connectionProvider.setJndiName(jndiName);
                        return connectionProvider.createConnection(realUser, realPassword);
                    } catch (Exception e) {
                        // fall back to JDBC
                    }
                }
            }
        } catch (Exception e) {
            throw new ReportDataFactoryException("ReportPlugin.unableToCreateConnection", e); //$NON-NLS-1$
        }

        return super.createConnection(databaseMeta, username, password);
    }
}
