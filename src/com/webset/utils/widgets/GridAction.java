package com.webset.utils.widgets;

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

public class GridAction 
{
   ConnectionDBDatasource conDataSrc = new ConnectionDBDatasource();
   private static Logger logger = Logger.getLogger(GridAction.class);

  public GridAction()
  {
  }


  /** 
  * Método para obtener una lista con todos los datos de la tabla
  * 
  * @return  ArrayList
  * @since   19/Mayo/2008
  * @author  Armando Rodriguez Meneses
  */
  public ArrayList<HashMap<String, String>> getList(String sSqlTables, String sqlOrder, ArrayList<HashMap<String, String>> listCols, String sSqlWhere){   
    ArrayList<HashMap<String, String>> listaDatos = new ArrayList<HashMap<String, String>>(0);
    Connection c = null;
    PreparedStatement stm = null;
    ResultSet res = null;
    String sSqlColumns = "";

    // GENERATE COLUMNS STRING
    Iterator<HashMap<String, String>> iter = listCols.iterator();
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
          HashMap<String, String> hashRenglon = new HashMap<String, String>();
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
  public ArrayList<HashMap<String, String>> getListFromQuery(String sSqlQuery, ArrayList<HashMap<String, String>> listCols){
    ArrayList<HashMap<String, String>> listaDatos = new ArrayList<HashMap<String, String>>(0);
    Connection c = null;
    PreparedStatement stm = null;
    ResultSet res = null;
    String sSqlColumns = "";

    // GENERATE COLUMNS STRING
    Iterator<HashMap<String, String>> iter = listCols.iterator();
    for(int i=1; iter.hasNext(); i++){
       if(i > 1)
        sSqlColumns += ",";
      
      sSqlColumns += " " + iter.next();
    }
    System.out.println(sSqlColumns);
    try{
       logger.info(sSqlQuery);

       c = conDataSrc.getConn();
       stm = c.prepareStatement(sSqlQuery);
       res = (ResultSet)stm.executeQuery();
      
       while(res.next()){
          HashMap<String, String> hashRenglon = new HashMap<String, String>();
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
  * Método para obtener una lista con todos los datos de la tabla
  * 
  * @return  ArrayList
  * @since   19/Mayo/2008
  * @author  Armando Rodriguez Meneses
  */
  public ArrayList<HashMap<String, String>> getList(String sSqlTables, String sSqlOrder, ArrayList<HashMap<String, String>> listCols){   
    
    return getList(sSqlTables, sSqlOrder, listCols, null);
  }

  /** 
  * Método para obtener una lista con todos los datos de la tabla
  * 
  * @return  ArrayList
  * @since   19/Mayo/2008
  * @author  Armando Rodriguez Meneses
  */
  public ArrayList<HashMap<String, String>> getList(String sSqlTables, String sSqlOrder){   
    
    return getList(sSqlTables, sSqlOrder, null);
  }

  /** 
  * Método para obtener una lista con todos los datos de la tabla
  * 
  * @return  ArrayList
  * @since   19/Mayo/2008
  * @author  Armando Rodriguez Meneses
  */
  public ArrayList<HashMap<String, String>> getList(String sSqlTables){   
    
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
  public ArrayList<HashMap<String, String>> obtenListaCombo(){   
    ArrayList<HashMap<String, String>> listaDatos = new ArrayList<HashMap<String, String>>(0);
    Connection c = null;
    CallableStatement cs = null;
    ResultSet res = null;

    try{
       String sql = "{ call PKG_GRUPO_REPORTES.listacombo(?) }";
       c = conDataSrc.getConn();
       //c = conexion.getConexion();
       cs = c.prepareCall(sql);
       cs.registerOutParameter(1, Types.ARRAY);
       cs.execute();
       res = (ResultSet)cs.getObject(1);
       while(res.next()){
          HashMap<String, String> hashRenglon = new HashMap<String, String>();
          
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
  
  public String alta(String sDesc, String sFuncion, String sConsulta, 
                                  String sAlmacen, String sEstatus, String sUsuario){
     String dato = "";
     Connection c = null;
     CallableStatement cs = null;
     try{
        String sql = "{ ? = call PKG_GRUPO_REPORTES.agrega(?,?,?,?,?,?) }";
        	c = conDataSrc.getConn();
        //c = conexion.getConexion();
        cs = c.prepareCall(sql);
        cs.registerOutParameter(1, Types.VARCHAR);
        cs.setString(2, sDesc);
        cs.setString(3, sFuncion);
        cs.setString(4, sAlmacen);
        cs.setString(5, sConsulta);
        cs.setString(6, sEstatus);
        cs.setString(7, sUsuario);
        cs.executeQuery();
        dato = cs.getString(1);
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
      return dato;
   }

 /** 
  * Modifica un registro en la base de datos
  * 
  * @param   
  * @return  cadena con mensaje de error si falló, null si fue exito
  * @since   24/Eenero/2008
  * @author  Armando Rodriguez Meneses.
  */
  
  public String modifica(String sId, String sDesc, String sFuncion, String sConsulta, 
                                      String sAlmacen, String sEstatus, String sUsuario){
     String dato = "";
     Connection c = null;
     CallableStatement cs = null;
     try{
        String sql = "{ ? = call PKG_GRUPO_REPORTES.modifica(?,?,?,?,?,?,?) }";
	c = conDataSrc.getConn();
        //c = conexion.getConexion();
        cs = c.prepareCall(sql);
        cs.registerOutParameter(1, Types.VARCHAR);
        cs.setInt(2, Integer.parseInt(sId));
        cs.setString(3, sDesc);
        cs.setString(4, sFuncion);
        cs.setString(5, sAlmacen);
        cs.setString(6, sConsulta);
        cs.setString(7, sEstatus);
        cs.setString(8, sUsuario);
        cs.executeQuery();
        dato = cs.getString(1);
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
      
      return dato;
   }

}
