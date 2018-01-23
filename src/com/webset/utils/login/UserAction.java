/*
 * UserAction.java
 *
 * Created on 10 de octubre de 2008, 09:28 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.webset.utils.login;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

import org.apache.log4j.Logger;

import com.webset.utils.connection.ConnectionDBDatasource;


/**
 *
 * @author ARM
 */
public class UserAction {
   
   ConnectionDBDatasource conDataSrc = new ConnectionDBDatasource();   
   private static Logger logger = Logger.getLogger(UserAction.class);
   /** Creates a new instance of UserAction */
   public UserAction() {}
   
  /** 
  * Método para obtener una lista con todos los datos de la tabla
  * 
  * @return  ArrayList
  * @since   01/Junio/2008
  * @author  Armando Rodriguez Meneses
  */
   public HashMap login(String sUsuario, String sPassword){   
      Connection c = null;
      HashMap hashRenglon = new HashMap();

      PreparedStatement stm = null;
      ResultSet res = null;
      String sql = null;
      String sUsrID=""; 
     
      try{
        sql  = " SELECT A.ID USER_ID, A.ID_PROVEEDOR ID_PROVEEDOR, B.NOMBRE NOMBRE_PROVEEDOR, A.TIPO_USUARIO TIPO_USUARIO";
        sql += " FROM TBL_SCO_MNU_USUARIOS A, TBL_SCO_PROVEEDORES B";
        sql += " WHERE B.ID_PROVEEDOR = A.ID_PROVEEDOR";
        sql += " AND LOGIN = '"+sUsuario+"'";
        sql += " AND PASSWORD = '"+sPassword+"'";

        logger.info(sql);
        c = conDataSrc.getConn();
        stm = c.prepareStatement(sql);
        res = (ResultSet)stm.executeQuery();	

	if(res.next()){
	   hashRenglon.put("ID", res.getString("USER_ID")==null ? "":res.getString("USER_ID"));
	   hashRenglon.put("ID_PROVEEDOR", res.getString("ID_PROVEEDOR")==null ? "":res.getString("ID_PROVEEDOR"));
	   hashRenglon.put("NOMBRE_PROVEEDOR", res.getString("NOMBRE_PROVEEDOR")==null ? "":res.getString("NOMBRE_PROVEEDOR"));
	   hashRenglon.put("TIPO_USUARIO", res.getString("TIPO_USUARIO")==null ? "":res.getString("TIPO_USUARIO"));
	}

      }
      catch(SQLException e){
    	  try{
    		  c.close();
    	  }
    	  catch(SQLException e2){
    		  logger.error(e2.getLocalizedMessage(),e2);
    		  e2.printStackTrace();
    		  }
    	  catch(Exception ex){
    		  logger.error(ex.getLocalizedMessage(),ex);
    		  ex.printStackTrace();
    		  }
    	  logger.error(e.getLocalizedMessage(),e);
    	  e.printStackTrace();
      }
      catch(Exception ex){
    	  logger.error(ex.getLocalizedMessage(),ex);
    	  ex.printStackTrace();
      }
      finally{
        try{
        	res.close();
        	}
        catch(SQLException e){
        	logger.error(e.getLocalizedMessage(),e);
        	e.printStackTrace();
        	}
        catch(Exception ex){
        	logger.error(ex.getLocalizedMessage(),ex);
        	ex.printStackTrace();
        	}
        try{
        	stm.close();
        	}
        catch(SQLException e){
        	    logger.error(e.getLocalizedMessage(),e);
        		e.printStackTrace();
        		}
        catch(Exception ex){
        	logger.error(ex.getLocalizedMessage(),ex);
        	ex.printStackTrace();
        	}
        try{
        	c.close();
        	}
        catch(SQLException e){
        	logger.error(e.getLocalizedMessage(),e);
        	e.printStackTrace();
        	}
        catch(Exception ex){
        	logger.error(ex.getLocalizedMessage(),ex);
        	ex.printStackTrace();
        	}
      }

      return hashRenglon;
   }
     
   
}
