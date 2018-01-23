package com.webset.utils.sql;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.apache.log4j.Logger;

import com.webset.utils.connection.ConnectionDBDatasource;

public class SqlAction 
{
   ConnectionDBDatasource conDataSrc = new ConnectionDBDatasource();
   private static Logger logger = Logger.getLogger(SqlAction.class);
  public SqlAction()
  {
  }


  /** 
  * Método para obtener una lista con todos los datos de la tabla
  * 
  * @return  ArrayList
  * @since   19/Mayo/2008
  * @author  Armando Rodriguez Meneses
  */
  public ArrayList getList(String sSqlTables, String sqlOrder, ArrayList listCols, String sSqlWhere){   
    ArrayList listaDatos = new ArrayList(0);
    Connection c = null;
    PreparedStatement stm = null;
    ResultSet res = null;
    String sSqlColumns = "";

    // GENERATE COLUMNS STRING
    Iterator iter = listCols.iterator();
    for(int i=1; iter.hasNext(); i++){
       if(i > 1)
        sSqlColumns += ",";
      
      sSqlColumns += " " + iter.next();
    }
    logger.info("sSqlColumns="+sSqlColumns);

    try{
      String sql = "SELECT ";
      
      sql += (sSqlColumns==null || sSqlColumns.equals("")) ? "* ": sSqlColumns+" ";
      sql += "FROM "+sSqlTables+" ";
      
      if(sSqlWhere != null && !sSqlWhere.equals(""))
        sql += "WHERE "+sSqlWhere+" ";
   
      if(sqlOrder != null && !sqlOrder.equals(""))
        sql += "ORDER BY "+sqlOrder+" ";
       
      logger.info(sql);
      
       c = conDataSrc.getConn();
       
       stm = c.prepareStatement(sql);

       res = (ResultSet)stm.executeQuery();
       
       while(res.next()){
    	   ArrayList listRow = new ArrayList(0);
          ResultSetMetaData resMetaDat = res.getMetaData();
          
          int colCount = (listCols == null || listCols.size() < 1) ? resMetaDat.getColumnCount():listCols.size();
          for(int i=1; i <= colCount; i++)
        	  listRow.add(res.getString(i));          
          listaDatos.add(listRow);
       }
       
    }
    catch(SQLException e){
    	try{c.close();
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
    
    return listaDatos;
  }
  
  /** 
  * Método para obtener una lista con todos los datos de la tabla
  * 
  * @return  ArrayList
  * @since   19/Mayo/2008
  * @author  Armando Rodriguez Meneses
  */
  public ArrayList getHashList(String sSqlTables, String sqlOrder, ArrayList listCols, String sSqlWhere){   
    ArrayList listaDatos = new ArrayList(0);
    Connection c = null;
    PreparedStatement stm = null;
    ResultSet res = null;
    String sSqlColumns = "";

    // GENERATE COLUMNS STRING
    Iterator iter = listCols.iterator();
    for(int i=1; iter.hasNext(); i++){
       if(i > 1)
        sSqlColumns += ",";
      
      sSqlColumns += " " + iter.next();
    }
    logger.info("sSqlColumns="+sSqlColumns);

    try{
      String sql = "SELECT ";
      
      sql += (sSqlColumns==null || sSqlColumns.equals("")) ? "* ": sSqlColumns+" ";
      sql += "FROM "+sSqlTables+" ";
      
      if(sSqlWhere != null && !sSqlWhere.equals(""))
        sql += "WHERE "+sSqlWhere+" ";
   
      if(sqlOrder != null && !sqlOrder.equals(""))
        sql += "ORDER BY "+sqlOrder+" ";
       
       logger.info(sql);
       
       //c = conexion.getConexion();
       c = conDataSrc.getConn();
       
       stm = c.prepareStatement(sql);

       res = (ResultSet)stm.executeQuery();
       
      
       while(res.next()){
          HashMap hashRenglon = new HashMap();
          ResultSetMetaData resMetaDat = res.getMetaData();
          
          int colCount = (listCols == null || listCols.size() < 1) ? resMetaDat.getColumnCount():listCols.size();
          for(int i=1; i <= colCount; i++){
              hashRenglon.put(resMetaDat.getColumnName(i),   res.getString(i));
              hashRenglon.put("type_"+resMetaDat.getColumnName(i),  resMetaDat.getColumnTypeName(i).toString());
          }
          
          listaDatos.add(hashRenglon);
       }
    }
    catch(SQLException e){
    	try{c.close();
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
    
    return listaDatos;
  }
  

  /** 
  * Método para obtener una lista con todos los datos de la tabla desde un query
  * 
  * @return  ArrayList
  * @since   19/Mayo/2008
  * @author  Armando Rodriguez Meneses
  */
  public ArrayList getListFromQuery(String sSqlQuery, ArrayList listCols){
    ArrayList listData = new ArrayList(0);
    Connection c = null;
    PreparedStatement stm = null;
    ResultSet res = null;
    String sSqlColumns = "";

    // GENERATE COLUMNS STRING
    Iterator iter = listCols.iterator();
    for(int i=1; iter.hasNext(); i++){
       if(i > 1)
        sSqlColumns += ",";
      
      sSqlColumns += " " + iter.next();
    }

    try{
       logger.info(sSqlQuery);

       c = conDataSrc.getConn();
       stm = c.prepareStatement(sSqlQuery);
       res = (ResultSet)stm.executeQuery();
      
       while(res.next()){
	  ArrayList listRow = new ArrayList(0);
          ResultSetMetaData resMetaDat = res.getMetaData();
          
          int colCount = (listCols == null || listCols.size() < 1) ? resMetaDat.getColumnCount():listCols.size();
          for(int i=1; i <= colCount; i++)
	    listRow.add(res.getString(i));
          
          listData.add(listRow);
       }
    }
    catch(SQLException e){
    	try{c.close();
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
     
    return listData;
  }
  
  /** 
  * Método para obtener una lista con todos los datos de la tabla desde un query
  * 
  * @return  ArrayList
  * @since   19/Mayo/2008
  * @author  Armando Rodriguez Meneses
  */
  public ArrayList getHashListFromQuery(String sSqlQuery, ArrayList listCols){
    ArrayList listData = new ArrayList(0);
    Connection c = null;
    PreparedStatement stm = null;
    ResultSet res = null;
    String sSqlColumns = "";

    // GENERATE COLUMNS STRING
    Iterator iter = listCols.iterator();
    for(int i=1; iter.hasNext(); i++){
       if(i > 1)
        sSqlColumns += ",";
      
      sSqlColumns += " " + iter.next();
    }

    try{
       logger.info(sSqlQuery);

       c = conDataSrc.getConn();
       stm = c.prepareStatement(sSqlQuery);
       res = (ResultSet)stm.executeQuery();
      
       while(res.next()){
          HashMap hashRenglon = new HashMap();
          ResultSetMetaData resMetaDat = res.getMetaData();
          
          int colCount = (listCols == null || listCols.size() < 1) ? resMetaDat.getColumnCount():listCols.size();
          for(int i=1; i <= colCount; i++){
              hashRenglon.put(resMetaDat.getColumnName(i),   res.getString(i));
              hashRenglon.put("type_"+resMetaDat.getColumnName(i),  resMetaDat.getColumnTypeName(i).toString());
          }
          
          listData.add(hashRenglon);
       }
    }
    catch(SQLException e){
    	try{c.close();
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
    
    return listData;
  }
  
  /** 
  * Método para obtener una lista con todos los datos de la tabla
  * 
  * @return  ArrayList
  * @since   19/Mayo/2008
  * @author  Armando Rodriguez Meneses
  */
  public ArrayList getList(String sSqlTables, String sSqlOrder, ArrayList listCols){   
    
    return getList(sSqlTables, sSqlOrder, listCols, null);
  }

  /** 
  * Método para obtener una lista con todos los datos de la tabla
  * 
  * @return  ArrayList
  * @since   19/Mayo/2008
  * @author  Armando Rodriguez Meneses
  */
  public ArrayList getList(String sSqlTables, String sSqlOrder){   
    
    return getList(sSqlTables, sSqlOrder, null);
  }

  /** 
  * Método para obtener una lista con todos los datos de la tabla
  * 
  * @return  ArrayList
  * @since   19/Mayo/2008
  * @author  Armando Rodriguez Meneses
  */
  public ArrayList getList(String sSqlTables){   
    
    return getList(sSqlTables, null, null);
  }


  /** 
  * Método para obtener una lista con todos los datos de la tabla
  * que tengan el estatus activo (se usa para los combos)
  * 
  * @return  ArrayList
  * @since   22/Enero/2008
  * @author  Armando Rodriguez Meneses
  */
  public ArrayList getListCombo(){   
    ArrayList listaDatos = new ArrayList(0);
    Connection c = null;
    CallableStatement cs = null;
    ResultSet res = null;

    try{
       String sql = "{ call listacombo(?) }";
       c = conDataSrc.getConn();
       //c = conexion.getConexion();
       cs = c.prepareCall(sql);
       cs.registerOutParameter(1, Types.ARRAY);
       cs.execute();
       res = (ResultSet)cs.getObject(1);
       while(res.next()){
          HashMap hashRenglon = new HashMap();
          
          hashRenglon.put("ID",           res.getString("ID"));
          hashRenglon.put("DESCRIPCION",  res.getString("DESCRIPCION"));
          listaDatos.add(hashRenglon);
       }
    }
    catch(SQLException e){
    	try{c.close();
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
    	  cs.close();
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
    
    return listaDatos;
  }

  
   /** 
   * Da de alta un registro en la base de datos
   * 
   * @param   
   * @return  cadena con mensaje de error si falló, null si fue exito
   * @since   24/Eenero/2008
   * @author  Armando Rodriguez Meneses
   */

   public int insert(String sSqlTable, ArrayList listCols, ArrayList listValues){
      int nRowsInserted = 0;
      Connection c = null;
      PreparedStatement stmt = null; 

      ResultSet res = null;
      String sSqlColumns = "";
      String sSqlValues = "";

      // GENERATE COLUMNS STRING
      Iterator iter = listCols.iterator();
      for(int i=0; i < listCols.size(); i++){
	 if(i > 0){
	    sSqlColumns += ",";
	    sSqlValues += ",";
	 }

	 sSqlColumns += " " + listCols.get(i);
	 sSqlValues += " "+listValues.get(i);
      }

      try{
	 String sql = "INSERT INTO "+sSqlTable+" ";
	 sql       += "("+sSqlColumns+")";
	 sql       += "VALUES ("+sSqlValues+")";

	 logger.info("insert="+sql);

	 c = conDataSrc.getConn();
	 stmt = c.prepareStatement(sql); 
	 nRowsInserted = stmt.executeUpdate();
      }
      catch(SQLException e){
    	  try{c.close();
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
    		  stmt.close();
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
      
      return nRowsInserted;
   }

 /** 
  * Modifica un registro en la base de datos
  * 
  * @param   
  * @return  cadena con mensaje de error si falló, null si fue exito
  * @since   24/Eenero/2008
  * @author  Armando Rodriguez Meneses.
  */
  
  public int update(String sSqlTable, ArrayList listCols, ArrayList listValues, String sSqlWhere){
      int nRowsUpdated = 0;
      Connection c = null;
      PreparedStatement stmt = null; 

      ResultSet res = null;
      String sSqlSets = "";
      
      sSqlSets = "SET "; 

      // GENERATE COLUMNS STRING
      Iterator iter = listCols.iterator();
      for(int i=0; i < listCols.size() && i < listValues.size(); i++){
	 if(i > 0)
	    sSqlSets += ", ";

	 sSqlSets += listCols.get(i) +" = "+listValues.get(i);
      }

      try{
	 String sql = "UPDATE "+sSqlTable+ " " + sSqlSets;
	 if(!sSqlWhere.equals(""))
	    sql += " WHERE " + sSqlWhere;
	 logger.info("update="+sql);
	 c = conDataSrc.getConn();
	 stmt = c.prepareStatement(sql); 
	 nRowsUpdated = stmt.executeUpdate();
      }
      catch(SQLException e){
    	  try{c.close();
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
    		  stmt.close();
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
      
      
      return nRowsUpdated;
   }

}
