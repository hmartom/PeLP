/*
	Copyright 2011-2012 Fundació per a la Universitat Oberta de Catalunya

	This file is part of PeLP (Programming eLearning Plaform).

    PeLP is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    PeLP is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/
package edu.uoc.pelp.test.model.dao.admin;

import edu.uoc.pelp.model.dao.admin.AdministrationDAO;
import edu.uoc.pelp.test.model.dao.LocalDAO;
import java.io.File;
import java.net.URL;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

/**
 * Implements the DAO object for Administration table. 
 * @author Xavier Baró
 */
public class LocalAdministrationDAO extends AdministrationDAO {
    
    private LocalDAO _localDAO=null;
    
    /**
     * Creates a new LocalAdministrationDAO object from given Hibernate SessionFactory
     * @param sessionFactory SessionFactory object
     */
    public LocalAdministrationDAO(SessionFactory sessionFactory) {
        // Call parent constructor
        super();
        
        // Create object to access local database
        _localDAO=new LocalDAO(sessionFactory) {
            @Override
            public void clearTableData() {
                deleteTableData("PelpActiveSubjects");
                deleteTableData("PelpAdmins");
                deleteTableData("PelpLanguages");
                deleteTableData("PelpMainLabSubjects");
                deleteTableData("Semester");
            }
        };
        // Assign the session Factory to the parent object
        _sessionFactory=_localDAO.getSessionFactory();
    }
    
    /**
     * Creates a new LocalAdministrationDAO object from given Hibernate configuration resource
     * @param resource Resource with configuration for Hibernate Database Connection
     */
    public LocalAdministrationDAO(String resource) {
        // Call parent constructor
        super();
        
        // Create object to access local database
        _localDAO=new LocalDAO(resource) {
            @Override
            public void clearTableData() {
                deleteTableData("PelpActiveSubjects");
                deleteTableData("PelpAdmins");
                deleteTableData("PelpLanguages");
                deleteTableData("PelpMainLabSubjects");
                deleteTableData("Semester");
            }
        };
        // Assign the session Factory to the parent object
        _sessionFactory=_localDAO.getSessionFactory();
    }
    
    /**
     * Creates a new LocalAdministrationDAO object from given Hibernate configuration file
     * @param confFile File with configuration for Hibernate Database Connection
     */
    public LocalAdministrationDAO(File confFile) {
        // Call parent constructor
        super();
        
        // Create object to access local database
        _localDAO=new LocalDAO(confFile) {
            @Override
            public void clearTableData() {
                deleteTableData("PelpActiveSubjects");
                deleteTableData("PelpAdmins");
                deleteTableData("PelpLanguages");
                deleteTableData("PelpMainLabSubjects");
                deleteTableData("Semester");
            }
        };
        // Assign the session Factory to the parent object
        _sessionFactory=_localDAO.getSessionFactory();
    }
    
    /**
     * Creates a new LocalAdministrationDAO object from given Hibernate configuration url
     * @param url URL with configuration for Hibernate Database Connection
     */
    public LocalAdministrationDAO(URL url) {
        // Call parent constructor
        super();
        
        // Create object to access local database
        _localDAO=new LocalDAO(url) {
            @Override
            public void clearTableData() {
                deleteTableData("PelpActiveSubjects");
                deleteTableData("PelpAdmins");
                deleteTableData("PelpLanguages");
                deleteTableData("PelpMainLabSubjects");
                deleteTableData("Semester");
            }
        };
        // Assign the session Factory to the parent object
        _sessionFactory=_localDAO.getSessionFactory();
    }
    
    @Override
    protected Session getSession() {     
        if(_localDAO==null) {
            return null;
        }
        return _localDAO.getSession();
    }
        
    /**
     * Remove all the data in this table of the database
     */
    public void clearTableData() {
        _localDAO.clearTableData();
    }
    
}

