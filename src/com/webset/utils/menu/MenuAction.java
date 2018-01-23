package com.webset.utils.menu;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.apache.log4j.Logger;
import org.springframework.jdbc.core.JdbcTemplate;

import com.webset.utils.connection.ConnectionDBDatasource;
import com.webset.utils.login.UserAction;


public class MenuAction 
{
    private JdbcTemplate jdbcTemplate;

   ConnectionDBDatasource conDataSrc = new ConnectionDBDatasource();
   private static Logger logger = Logger.getLogger(MenuAction.class);

   private String sMenuTree = "";

  public MenuAction()
  {
  }
  
  /** 
  * Método para obtener una lista con todos los datos de la tabla
  * 
  * @return  ArrayList
  * @since   01/Junio/2008
  * @author  Armando Rodriguez Meneses
  */
   public ArrayList obtenLstResponsabilidades(int nTipoUsuario){  // OBTENER FACULTADES
      ArrayList listaDatos = new ArrayList(0);
      Connection c = null;

      PreparedStatement stm = null;
      ResultSet res = null;
      String sql = null;
      
      try{
         sql  = " SELECT DISTINCT B.ID, B.NOMBRE, B.ORDEN_DESPLEGADO";
         sql += " FROM TBL_SCO_MNU_RESP_FUNC A, TBL_SCO_MNU_RESPONSABILIDADES B, TBL_SCO_MNU_RESP_TIPUSU C";
         sql += " WHERE A.ID_RESPONSABILIDAD = B.ID";
         sql += " AND C.ID_RESPONSABILIDAD = B.ID";
         sql += " AND A.ESTATUS = 'A'";
         sql += " AND B.ESTATUS = 'A'";
         sql += " AND C.ESTATUS = 'A'";
         sql += " AND C.ID_TIPO_USUARIO = ?";
         sql += " ORDER BY B.ORDEN_DESPLEGADO, B.NOMBRE";

        c = conDataSrc.getConn();
        
        stm = c.prepareStatement(sql);
        stm.setInt(1, nTipoUsuario);

        res = (ResultSet)stm.executeQuery();

        while(res.next()){
          HashMap hashRenglon = new HashMap();
          
          hashRenglon.put("ID", res.getString("ID"));
          hashRenglon.put("NOMBRE", res.getString("NOMBRE"));
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
  * @since   01/Junio/2008
  * @author  Armando Rodriguez Meneses
  */
   public ArrayList obtenLstFunciones(int nResponsabilidad){   
      ArrayList listaDatos = new ArrayList(0);
      Connection c = null;

      PreparedStatement stm = null;
      ResultSet res = null;
      String sql = null;
      
      try{
        sql  = " SELECT DISTINCT C.ID, C.NOMBRE, C.ID_FUNCION_REF, C.URL_LINK, C.ORDEN_DESPLEGADO";
        sql += " FROM TBL_SCO_MNU_RESP_FUNC A, TBL_SCO_MNU_RESPONSABILIDADES B, TBL_SCO_MNU_FUNCIONES C";
        sql += " WHERE A.ID_RESPONSABILIDAD = B.ID";
        sql += " AND A.ID_FUNCION = C.ID";
        sql += " AND A.ESTATUS = 'A'";
        sql += " AND B.ESTATUS = 'A'";
        sql += " AND C.ESTATUS = 'A'";
        sql += " AND C.ID_FUNCION_REF IS NULL";
        sql += " AND A.ID_RESPONSABILIDAD = " + nResponsabilidad;
        sql += " ORDER BY C.ORDEN_DESPLEGADO, C.NOMBRE";

        c = conDataSrc.getConn();
        //c = conexion.getConexion();
        
        stm = c.prepareStatement(sql);
        //stm.setInt(1, nResponsabilidad);

        res = (ResultSet)stm.executeQuery();

        while(res.next()){
          HashMap hashRenglon = new HashMap();
          
          hashRenglon.put("ID", res.getString("ID"));
          hashRenglon.put("NOMBRE", res.getString("NOMBRE"));
          hashRenglon.put("ID_FUNCION_REF", res.getString("ID_FUNCION_REF"));
          hashRenglon.put("URL_LINK", res.getString("URL_LINK"));
          
          listaDatos.add(hashRenglon);
        }
      }
      //catch(SQLException e){e.printStackTrace(); try{c.close();}catch(SQLException e2){e2.printStackTrace();}catch(Exception ex){ex.printStackTrace();} e.printStackTrace();}
      catch(SQLException e){e.printStackTrace();}
      catch(Exception ex){ex.printStackTrace();}
      finally{
        try{res.close();}catch(SQLException e){e.printStackTrace();}catch(Exception ex){ex.printStackTrace();}
        try{stm.close();}catch(SQLException e){e.printStackTrace();}catch(Exception ex){ex.printStackTrace();}
        try{c.close();}catch(SQLException e){e.printStackTrace();}catch(Exception ex){ex.printStackTrace();}
      }

      return listaDatos;
   }


   /** 
   * Método para obtener una lista con todos los datos de la tabla
   * 
   * @return  ArrayList
   * @since   01/Junio/2008
   * @author  Armando Rodriguez Meneses
   */
   public ArrayList generarLstSubFunciones(int nFuncion, HashMap hmFuncionesReferencia, String sURL){   

      boolean subFuncionFlag = true;
      boolean existen = false;
      boolean existieron = false;

      ArrayList listaDatos = new ArrayList(0);
      Connection c = null;

      PreparedStatement stm = null;
      ResultSet res = null;
      String sql = null;

      try{
	 sql  = " SELECT DISTINCT A.ID, A.NOMBRE, A.ID_FUNCION_REF, A.URL_LINK, ORDEN_DESPLEGADO";
	 sql += " FROM TBL_SCO_MNU_FUNCIONES A";
	 sql += " WHERE A.ESTATUS = 'A'";
	 sql += " AND A.ID_FUNCION_REF = ?";
	 sql += " ORDER BY ORDEN_DESPLEGADO, NOMBRE";

	 c = conDataSrc.getConn();
	 //c = conexion.getConexion();

	 stm = c.prepareStatement(sql);
	 stm.setInt(1, nFuncion);

	 res = (ResultSet)stm.executeQuery();

	 existen = res.next();
	 if(!existen){

	   // GENERAR EL NOMBRE DEL ITEM
	   sMenuTree += ",iconCls:'user', leaf:true ";

	   // GENERAR EL LINK DEL ITEM
	   String sPagina = hmFuncionesReferencia.get("URL_LINK")==null? "":hmFuncionesReferencia.get("URL_LINK").toString().trim();
	   String sID = hmFuncionesReferencia.get("ID")==null? "":hmFuncionesReferencia.get("ID").toString().trim();

	   if(!sPagina.equals("")){
	       sMenuTree += ",id:'"+sID+"-"+sURL+"/"+sPagina+"'";
	       sMenuTree += ",href:'#"+sURL+"/"+sPagina+"'";
	   }

	    //System.out.println("** LINK:["+sPagina+"]");

	    sMenuTree += "}\n";

	    //System.out.println("\n\n***"+getMenuTree()+"\n***\n");

	 }else{
	    existieron = true;

	    while(existen){
	       HashMap hashRenglonFunciones = new HashMap();

	       hashRenglonFunciones.put("ID",              res.getString("ID"));
	       hashRenglonFunciones.put("NOMBRE",          res.getString("NOMBRE"));
	       hashRenglonFunciones.put("ID_FUNCION_REF",  res.getString("ID_FUNCION_REF"));
	       hashRenglonFunciones.put("URL_LINK", res.getString("URL_LINK"));
	       listaDatos.add(hashRenglonFunciones);
	       existen = res.next();
	    }
	 }

      }
      catch(SQLException e){try{c.close();}catch(SQLException e2){e2.printStackTrace();}catch(Exception ex){ex.printStackTrace();} e.printStackTrace();}
      catch(Exception ex){ex.printStackTrace();}
      finally{
	 try{res.close();}catch(SQLException e){e.printStackTrace();}catch(Exception ex){ex.printStackTrace();}
	 try{stm.close();}catch(SQLException e){e.printStackTrace();}catch(Exception ex){ex.printStackTrace();}
	 try{c.close();}catch(SQLException e){e.printStackTrace();}catch(Exception ex){ex.printStackTrace();}
      }

      if(existieron){    
      sMenuTree +=  ", children:[{\n";

      Iterator iterFunc = listaDatos.iterator();
      boolean primerItem = true;

      while( iterFunc.hasNext() ) {
	 HashMap hashFunc = (HashMap)iterFunc.next();

	 if(primerItem)
	     primerItem = false;
	 else
	     sMenuTree += ",{";

	 String sPagina = hashFunc.get("URL_LINK")==null? "":hashFunc.get("URL_LINK").toString().trim();
	 String sFuncion = hashFunc.get("NOMBRE")==null? "":hashFunc.get("NOMBRE").toString().trim();


	 // GENERAR EL NOMBRE DEL ITEM
	 sMenuTree += "text:'"+sFuncion+"', singleClickExpand:true ";
	 generarLstSubFunciones(Integer.parseInt(hashFunc.get("ID").toString()), hashFunc, sURL);
      }

      sMenuTree += "]}\n";
      //System.out.println("\n\n***"+getMenuTree()+"\n***\n");

   } // else if(!existen)...


   return listaDatos;
   }
  
   public String getMenuTree(){
      return this.sMenuTree;
   }

   public void setMenuTree(String menu){
      this.sMenuTree = menu;
   }

}
