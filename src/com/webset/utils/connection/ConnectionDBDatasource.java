/*
 * ConnectionDBDatasource.java
 *
 * Created on 9 de julio de 2008, 15:00
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.webset.utils.connection;

import java.sql.SQLException;

import javax.naming.NamingException;

import org.apache.log4j.Logger;

import com.webset.set.seguridad.business.SegUsuarioBusiness;

/**
 *
 * @author ARM
 */
public class ConnectionDBDatasource {
    
	private java.sql.Connection conn;
	private static Logger logger = Logger.getLogger(ConnectionDBDatasource.class);
	
	/** Creates a new instance of ConnectionDBDatasource **/

	private javax.sql.DataSource getDatasourceApp() throws javax.naming.NamingException {
		javax.naming.Context c = new javax.naming.InitialContext();
		return (javax.sql.DataSource) c.lookup("java:comp/env/jdbc/datasourceFacil"); // PARA NETBEANS
		//return (javax.sql.DataSource) c.lookup("jdbc/FacilPooledDS"); // para JDEVELOPER. OLD=jdbc/facilPooledDS
	}
    
	public java.sql.Connection getConn(){
		try{
		   javax.sql.DataSource ds = getDatasourceApp();
		   conn = ds.getConnection();
		}catch(SQLException ex){
			logger.error(ex.getLocalizedMessage(), ex);
		   ex.printStackTrace();
		}catch(NamingException ex){
			logger.error(ex.getLocalizedMessage(), ex);
		   ex.printStackTrace();
		}
		return conn;
	}

}
