package com.webset.set.monitor.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;

import com.webset.set.utilerias.Bitacora;

public class MonitorDaoImpl implements MonitorDao {
	JdbcTemplate jdbcTemplate;
	Bitacora bitacora;
	
	public List<Map<String,Object>> obtenerArbolCaja(String divisa){
		StringBuffer sql = new StringBuffer();
		List<Map<String,Object>> listaResultado = new ArrayList<Map<String,Object>>();
		try {
				sql.append("SELECT ge.id_grupo_flujo as bisabuelo,cgf.desc_grupo_flujo as descBisabuelo, " );
				sql.append("\n a.no_empresa as abuelo, b.razon_social as descAbuelo,   " );
				sql.append("\n c.id_banco as padre, d.desc_banco as descPadre,  " );
				sql.append("\n c.id_chequera as hijo, c.id_chequera as descHijo,  ");
				sql.append("\n COALESCE(c.saldo_final,0) as total ");
				sql.append("\n FROM empresa a");
				sql.append("\n JOIN persona b ON a.no_empresa = b.no_empresa");
				sql.append("\n JOIN grupo_empresa ge ON ge.no_empresa= a.no_empresa");
				sql.append("\n JOIN  cat_grupo_flujo cgf ON cgf.id_grupo_flujo = ge.id_grupo_flujo");
				sql.append("\n LEFT JOIN cat_cta_banco c ON a.no_empresa = c.no_empresa ");
				sql.append("\n LEFT JOIN cat_banco d ON c.id_banco = d.id_banco");
				sql.append("\n WHERE b.id_tipo_persona='E'");
				sql.append("\n AND c.id_chequera IS NOT NULL");
				sql.append("\n AND id_divisa = '"+ divisa +"'");
				
				sql.append("\n ORDER BY descbisabuelo, descabuelo,descpadre,hijo");
				
				System.out.println("------------------------------------------------------- \n");
				System.out.println("------------------sql: Arbol caja------------- \n");
				System.out.println(sql.toString());
				System.out.println("------------------------------------------------------- \n");
				
				listaResultado = jdbcTemplate.queryForList(sql.toString());
				 
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Monitor, C:MonitorDaoImpl, M:obtenerArbolCaja");
		}return listaResultado;
	}
	
	public List<Map<String,Object>> obtenerArbolInversion (String divisa){
		StringBuffer sql = new StringBuffer();
		List<Map<String,Object>> listaResultado = new ArrayList<Map<String,Object>>();
		try {
		   sql.append("select c.id_banco as abuelo, a.desc_banco as descAbuelo, \n");
		   sql.append("c.no_empresa as padre, b.nom_empresa as descPadre,\n");
		   sql.append("c.id_chequera as hijo, c.id_chequera as deschijo ,sum(c.importe) as total \n");
		   sql.append("from cat_banco a, empresa b, orden_inversion c  \n");
		   sql.append("where a.id_banco = c.id_banco and b.no_empresa = c.no_empresa \n");
		   sql.append("AND c.ID_ESTATUS_ORD = 'A'  \n");
		   sql.append("and c.fec_venc between (select fec_hoy-180 from fechas) and (select fec_hoy from fechas)  \n");
		   sql.append("group by c.id_banco,a.desc_banco,c.no_empresa, b.nom_empresa, c.id_chequera  \n");
		   sql.append("order by descabuelo, descpadre ,deschijo \n");
			
			System.out.println("------------------------------------------------------- \n");
			System.out.println("------------------sql: Arbol inversion------------- \n");
			System.out.println(sql.toString());
			System.out.println("------------------------------------------------------- \n");
		 
			listaResultado = jdbcTemplate.queryForList(sql.toString());
			 
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Monitor, C:MonitorDaoImpl, M:obtenerArbolInversion");
		}return listaResultado;	
	}
	
	public List<Map<String,Object>> obtenerArbolDeudas (String divisa){
		StringBuffer sql = new StringBuffer();
		List<Map<String,Object>> listaResultado = new ArrayList<Map<String,Object>>();
		try {
			
		    sql.append("SELECT ge.id_grupo_flujo as abuelo, cgf.desc_grupo_flujo as descAbuelo, \n");
		    sql.append("ccc.id_banco as padre, cb.desc_banco as descPadre , \n");
		    sql.append("ccc.no_empresa as hijo, e.nom_empresa as descHijo, sum(cac.capital) as total \n");
			sql.append("from CAT_AMORTIZACION_CREDITO cac, CAT_CONTRATO_CREDITO ccc \n");
			sql.append(" JOIN CAT_BANCO cb ON ccc.id_banco = cb.id_banco \n");
			sql.append(" JOIN EMPRESA e ON ccc.no_empresa = e.no_empresa \n");
			sql.append(" JOIN grupo_empresa ge ON e.no_empresa = ge.no_empresa \n");
			sql.append(" JOIN cat_grupo_flujo cgf  ON e.id_grupo_flujo = ge.id_grupo_flujo \n");
			sql.append("group by ge.id_grupo_flujo,cgf.desc_grupo_flujo,ccc.id_banco,cb.desc_banco,ccc.no_empresa,e.nom_empresa \n");
			sql.append(" order by descabuelo,descpadre,deschijo \n");
			
			System.out.println("------------------------------------------------------- \n");
			System.out.println("------------------sql: Arbol deuda------------- \n");
			System.out.println(sql.toString());
			System.out.println("------------------------------------------------------- \n");
		 
			listaResultado = jdbcTemplate.queryForList(sql.toString());
			 
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Monitor, C:MonitorDaoImpl, M:obtenerArbolDeudas");
		}return listaResultado;
	}
	
	public List<Map<String,Object>>  obtenerArbolConciliaciones (String divisa){
		StringBuffer sql = new StringBuffer();
		List<Map<String,Object>> listaResultado = new ArrayList<Map<String,Object>>();
		try {
			sql.append("SELECT  case when m.id_tipo_movto = 'E' then 1 else 2 end as bisabuelo ,\n" );
			sql.append(" case when m.id_tipo_movto = 'E' then 'EGRESO NO CONCILIADOS' else 'DEPOSITOS NO CONCILIADOS' end as descBisabuelo, \n" );
			sql.append("m.no_empresa as abuelo, e.nom_empresa as descAbuelo, \n" );
			sql.append("m.id_banco as padre, b.desc_banco as descPadre ,\n" );
			sql.append(" m.id_chequera as hijo, m.id_chequera as descHijo, \n" );
			sql.append("SUM(m.importe) as total , m.id_divisa as divisa \n");
			sql.append("FROM movimiento m \n");
			sql.append("JOIN empresa e on m.no_empresa= e.no_empresa \n");
			sql.append("JOIN cat_banco b on m.id_banco = b.id_banco \n");
			sql.append("WHERE  m.id_tipo_operacion in (3107, 1018 , 3101, 3200,4001,4101,4102,4103,3700,3701,3705,3706,3708,3709) \n");
			sql.append("AND m.id_estatus_cb in ('P', 'D' , 'N') \n");
			sql.append("AND m.id_estatus_mov not in ('X','L') \n");
			sql.append("AND m.ID_divisa in ('"+divisa+"') \n");
			sql.append("GROUP BY m.id_tipo_movto,m.id_divisa,m.no_empresa, e.nom_empresa, m.id_banco, b.desc_banco, m.id_chequera \n");
			
			String auxiliar=sql.toString();
			auxiliar=auxiliar.replace("m.", "h.");
			auxiliar=auxiliar.replace("movimiento m", "hist_movimiento h");
			sql.append("UNION  \n");
			sql.append(auxiliar);
			sql.append("ORDER BY descbisabuelo,descabuelo,descpadre, hijo   ");
		
			System.out.println("------------------------------------------------------- \n");
			System.out.println("------------------sql: Arbol conciliaciones------------- \n");
			System.out.println(sql.toString());
			System.out.println("------------------------------------------------------- \n");
		
			listaResultado = jdbcTemplate.queryForList(sql.toString());
			 
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Monitor, C:MonitorDaoImpl, M:obtenerArbolConciliaciones");
		}return listaResultado;	
	}
	
	public List<Map<String,Object>>  obtenerArbolComparativo (String divisa){
		StringBuffer sql = new StringBuffer();
		List<Map<String,Object>> listaResultado = new ArrayList<Map<String,Object>>();
		try {
			
			sql.append("SELECT  case when m.id_tipo_movto = 'E' then 1 else 2 end as bisabuelo , \n");
			sql.append("case when m.id_tipo_movto = 'E' then 'EGRESO GLOBAL DEL DIA' else 'INGRESO GLOBAL DEL DIA' end as descBisabuelo, \n");
			sql.append("ge.id_grupo_flujo as abuelo, cgf.desc_grupo_flujo as descAbuelo, \n");
			sql.append("m.no_empresa as padre, e.nom_empresa as descPadre, \n"); 
			sql.append("m.id_chequera as hijo, m.id_chequera as descHijo , \n");
			sql.append("SUM(m.importe) as total \n");
			sql.append("FROM movimiento m  \n");
			sql.append("JOIN empresa e on m.no_empresa= e.no_empresa \n");
			sql.append("JOIN grupo_empresa ge ON e.no_empresa = ge.no_empresa \n");
			sql.append("JOIN cat_grupo_flujo cgf  ON ge.id_grupo_flujo = cgf.id_grupo_flujo \n");
			sql.append("WHERE  m.id_tipo_operacion in (3107, 1018 , 3101, 3200,4001,4101,4102,4103,3700,3701,3705,3706,3708,3709) \n");
			sql.append("AND m.id_estatus_cb in ('P', 'D' , 'N' , 'J') \n");
			sql.append("AND m.id_estatus_mov not in ('X','L') \n");
			sql.append("AND m.ID_divisa in ('"+divisa+"') \n");
			sql.append("GROUP BY m.id_tipo_movto,ge.id_grupo_flujo, cgf.desc_grupo_flujo, m.no_empresa, e.nom_empresa, m.id_chequera \n");
			
			String auxiliar=sql.toString();
			auxiliar=auxiliar.replace("m.", "h.");
			auxiliar=auxiliar.replace("movimiento m", "hist_movimiento h");
			sql.append("UNION  \n");
			sql.append(auxiliar);
			sql.append("ORDER BY descbisabuelo,descabuelo,descpadre, hijo   ");
		
			System.out.println("------------------------------------------------------- \n");
			System.out.println("------------------sql: Arbol comparaciones------------- \n");
			System.out.println(sql.toString());
			System.out.println("------------------------------------------------------- \n");
		
			listaResultado = jdbcTemplate.queryForList(sql.toString());
			 
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Monitor, C:MonitorDaoImpl, M:obtenerArbolComparativo");
		}return listaResultado;	
	}
	
	public String  calcularGlobal (String divisa){
		StringBuffer sql = new StringBuffer();
		String resultado= "0";
		try {
			
			sql.append("SELECT  case when m.id_tipo_movto = 'E' then sum(m.importe) else 0 end as totalEgreso, \n");
			sql.append("case when m.id_tipo_movto = 'I' then sum(m.importe) else 0  end as totalIngreso \n");
			sql.append("FROM movimiento m  \n");
			sql.append("JOIN empresa e on m.no_empresa= e.no_empresa \n");
			sql.append("JOIN grupo_empresa ge ON e.no_empresa = ge.no_empresa \n");
			sql.append("JOIN cat_grupo_flujo cgf  ON ge.id_grupo_flujo = cgf.id_grupo_flujo \n");
			sql.append("WHERE  m.id_tipo_operacion in (3107, 1018 , 3101, 3200,4001,4101,4102,4103,3700,3701,3705,3706,3708,3709) \n");
			sql.append("AND m.id_estatus_cb in ('P', 'D' , 'N' , 'J') \n");
			sql.append("AND m.id_estatus_mov not in ('X','L') \n");
			sql.append("AND m.ID_divisa in ('"+divisa+"') \n");
			sql.append("GROUP BY m.id_tipo_movto \n");
			
			String auxiliar=sql.toString();
			auxiliar=auxiliar.replace("m.", "h.");
			auxiliar=auxiliar.replace("movimiento m", "hist_movimiento h");
			sql.append("UNION  \n");
			sql.append(auxiliar);
			
			auxiliar ="";
			auxiliar ="select sum (totalIngreso-totalEgreso) as total from ( \n";
			auxiliar += sql.toString();
			auxiliar += ") suma \n";
		
			System.out.println("------------------------------------------------------- \n");
			System.out.println("------------------sql: Calcular global ------------- \n");
			System.out.println(auxiliar.toString());
			System.out.println("------------------------------------------------------- \n");
		
			resultado = jdbcTemplate.queryForList(auxiliar).get(0).get("total") +"";
			 
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Monitor, C:MonitorDaoImpl, M:calcularGlobal");
		}return resultado;	
	}
	
	/******************************
	 * GRID
	 */
	
	public List<Map<String,Object>>  consultaGridDiversos (String divisa){
		StringBuffer sql = new StringBuffer();
		List<Map<String,Object>> listaResultado = new ArrayList<Map<String,Object>>();
		try {
			
			sql.append("select 'Pagos realizados' as descripcion , 0 as total  \n");
			
			sql.append("union all \n");
			
			sql.append("select 'Rechazos' as descripcion  , count(1) as total from bitacora_rechazos_sap \n");
			
			sql.append("union all \n");
			
			sql.append("select 'Descompensaciones' as descripcion  , count(1) as total from movimiento m \n");
			sql.append("where m.id_estatus_cb in ('P', 'D' , 'N') \n");
			sql.append("AND m.id_estatus_mov  in ('X')\n");
			
			sql.append("union all \n");
			
			sql.append("select 'Pasivos vencidos' as descripcion  , count(1) as total from movimiento m \n");
			sql.append("where FEC_OPERACION < (SELECT SYSDATE FROM DUAL) \n");
			sql.append("AND ID_TIPO_OPERACION IN (3000, 3801) \n");
			sql.append("AND ID_ESTATUS_MOV IN ('N','C','L') \n");
			sql.append("AND (CVE_CONTROL IS NULL OR CVE_CONTROL = '') \n");
			sql.append("AND m.id_estatus_mov not in ('X','L') \n");
			sql.append("AND ID_DIVISA = 'MN' \n");
			
			sql.append("union all \n");
			
			sql.append("select 'Por vencer' as descripcion  , count(1) as total from movimiento m \n");
			sql.append("where FEC_OPERACION >= (SELECT SYSDATE FROM DUAL) \n");
			sql.append("AND ID_TIPO_OPERACION IN (3000, 3801) \n");
			sql.append("AND ID_ESTATUS_MOV IN ('N','C','L')  \n");
			sql.append("AND m.id_estatus_mov not in ('X','L')  \n");			
			sql.append("AND (CVE_CONTROL IS NULL OR CVE_CONTROL = '')  \n");
			sql.append("AND ID_DIVISA = 'MN' \n");
			
			sql.append("union all \n");
			
			sql.append("select 'Total pasivos' as descripcion  , count(1) as total from movimiento m \n");
			sql.append("where ID_TIPO_OPERACION IN (3000, 3801) \n");
			sql.append("AND ID_ESTATUS_MOV IN ('N','C','L') \n");
			sql.append("AND m.id_estatus_mov not in ('X','L') \n");
			sql.append("AND (CVE_CONTROL IS NULL OR CVE_CONTROL = '') \n");
			sql.append("AND ID_DIVISA = 'MN' \n");
		
			System.out.println("------------------------------------------------------- \n");
			System.out.println("------------------sql: consulta gridDiversos------------- \n");
			System.out.println(sql.toString());
			System.out.println("------------------------------------------------------- \n");
		
			listaResultado = jdbcTemplate.queryForList(sql.toString());
			 
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Monitor, C:MonitorDaoImpl, M:obtenerArbolComparativo");
		}return listaResultado;	
	}
	
	public List<Map<String,Object>>  consultaIndicadoresBancarios (){
		StringBuffer sql = new StringBuffer();
		List<Map<String,Object>> listaResultado = new ArrayList<Map<String,Object>>();
		try {
			
			sql.append("SELECT id_divisa as id , valor  , 'divisa' as tipo FROM valor_divisa \n");
			sql.append("where fec_divisa in ( select fec_hoy from fechas) \n");
			
			sql.append("union all \n");
			
			sql.append("select id_tasa as id, valor , 'tasa' as tipo from tasa  \n");
			sql.append("where fecha in ( select fec_hoy from fechas) \n");
			
			sql.append("order by  tipo,id \n");
		
			System.out.println("------------------------------------------------------- \n");
			System.out.println("------------------sql: consultaIndicadoresBancarios------------- \n");
			System.out.println(sql.toString());
			System.out.println("------------------------------------------------------- \n");
		
			listaResultado = jdbcTemplate.queryForList(sql.toString());
			 
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Monitor, C:MonitorDaoImpl, M:consultaIndicadoresBancarios");
		}return listaResultado;	
	}
	/***********************************************************************
	 * GRAFICAS
	 */
	public List<Map<String, Object>> graficaPoscionCaja(String divisa){
		StringBuffer sql = new StringBuffer();
		List<Map<String, Object>> resultado = new ArrayList<Map<String, Object>>();
		try {
				sql.append("\n SELECT ge.id_grupo_flujo as id, cgf.desc_grupo_flujo as descripcion, sum(COALESCE(c.saldo_final,0)) as total");
				sql.append("\n FROM empresa a");
				sql.append("\n JOIN persona b ON a.no_empresa = b.no_empresa");
				sql.append("\n JOIN grupo_empresa ge ON ge.no_empresa= a.no_empresa");
				sql.append("\n JOIN  cat_grupo_flujo cgf ON cgf.id_grupo_flujo = ge.id_grupo_flujo");
				sql.append("\n LEFT JOIN cat_cta_banco c ON a.no_empresa = c.no_empresa ");
				sql.append("\n LEFT JOIN cat_banco d ON c.id_banco = d.id_banco");
				sql.append("\n WHERE b.id_tipo_persona='E'");
				sql.append("\n AND c.id_chequera IS NOT NULL");
				sql.append("\n AND id_divisa = '"+divisa +"'");
				//Solo para la prueba eliminar
				sql.append("\n and c.saldo_final <> 0");
				sql.append("\n and c.saldo_final is not null");
				//Solo para la prueba eliminar
				sql.append("\n group by ge.id_grupo_flujo , cgf.desc_grupo_flujo");
				sql.append("\n ORDER BY cgf.desc_grupo_flujo,ge.id_grupo_flujo");
				
				
				System.out.println("------------------------------------------------------- \n");
				System.out.println("------------------sql: graficaPosicionCaja------------- \n");
				System.out.println(sql.toString());
				System.out.println("------------------------------------------------------- \n");
			resultado = jdbcTemplate.queryForList(sql.toString());		 
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Monitor, C:MonitorDaoImpl, M:graficaPoscionCaja");
		}return resultado;
	}
	
	public List<Map<String, Object>> graficaPoscionConciliacion(String divisa){
		StringBuffer sql = new StringBuffer();
		List<Map<String, Object>> resultado = new ArrayList<Map<String, Object>>();
		String auxiliar = "";
		try {
				sql.append("\n SELECT  case when m.id_tipo_movto = 'E' THEN 'EGRESOS NO CONCILIADOS' ELSE 'DEPOSITOS NO IDENTIFICADOS' END as descripcion,");
				sql.append("\n SUM(m.importe) as total");
				sql.append("\n FROM movimiento m ");
				sql.append("\n JOIN empresa e on m.no_empresa= e.no_empresa ");
				sql.append("\n JOIN cat_banco b on m.id_banco = b.id_banco ");
				sql.append("\n WHERE  m.id_tipo_operacion in (3107, 1018 , 3101, 3200,4001,4101,4102,4103,3700,3701,3705,3706,3708,3709)");
				sql.append("\n AND m.id_estatus_cb in ('P', 'D' , 'N')  ");
				sql.append("\n AND m.id_estatus_mov not in ('X','L') ");
				sql.append("\n AND m.ID_divisa in ('"+divisa+"') ");
				sql.append("\n GROUP BY m.id_tipo_movto");
				
				auxiliar=sql.toString();
				auxiliar.replace("m.", "h.");
				auxiliar.replace("movimiento m", "hist_movimiento h");
				sql.append("\n UNION");
				sql.append(auxiliar);
				
				System.out.println("------------------------------------------------------- \n");
				System.out.println("------------sql: graficaPoscionConciliacion------------ \n");
				System.out.println(sql.toString());
				System.out.println("------------------------------------------------------- \n");
				
			resultado = jdbcTemplate.queryForList(sql.toString());		 
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Monitor, C:MonitorDaoImpl, M:graficaPoscionConciliacion");
		}return resultado;
	}
	
	public List<Map<String, Object>> graficarPoscionInversion(String divisa){
		StringBuffer sql = new StringBuffer();
		List<Map<String, Object>> resultado = new ArrayList<Map<String, Object>>();
		try {
			
		   sql.append("select c.id_banco as id, a.desc_banco as descripcion, sum(c.importe) as total \n");
		   sql.append("from cat_banco a, empresa b, orden_inversion c  \n");
		   sql.append("where a.id_banco = c.id_banco and b.no_empresa = c.no_empresa \n");
		   sql.append("AND c.ID_ESTATUS_ORD = 'A'  \n");
		   sql.append("and c.fec_venc between (select fec_hoy-180 from fechas) and (select fec_hoy from fechas)  \n");
		   sql.append("group by c.id_banco,a.desc_banco,c.no_empresa, b.nom_empresa, c.id_chequera  \n");
		   sql.append("order by id \n");
				
			System.out.println("------------------------------------------------------- \n");
			System.out.println("------------sql: graficarPoscionInversion------------ \n");
			System.out.println(sql.toString());
			System.out.println("------------------------------------------------------- \n");
			
			resultado = jdbcTemplate.queryForList(sql.toString());		 
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Monitor, C:MonitorDaoImpl, M:graficarPoscionInversion");
		}return resultado;
	}
	
	public List<Map<String, Object>> graficarPoscionDeuda(String divisa){
		StringBuffer sql = new StringBuffer();
		List<Map<String, Object>> resultado = new ArrayList<Map<String, Object>>();
		try {
			
		    sql.append("SELECT ge.id_grupo_flujo as id, cgf.desc_grupo_flujo as descripcion , sum(cac.capital) as total \n");
			sql.append("from CAT_AMORTIZACION_CREDITO cac, CAT_CONTRATO_CREDITO ccc \n");
			sql.append(" JOIN CAT_BANCO cb ON ccc.id_banco = cb.id_banco \n");
			sql.append(" JOIN EMPRESA e ON ccc.no_empresa = e.no_empresa \n");
			sql.append(" JOIN grupo_empresa ge ON e.no_empresa = ge.no_empresa \n");
			sql.append(" JOIN cat_grupo_flujo cgf  ON e.id_grupo_flujo = ge.id_grupo_flujo \n");
			sql.append("group by ge.id_grupo_flujo,cgf.desc_grupo_flujo \n");
			sql.append(" order by id \n");
			
			System.out.println("------------------------------------------------------- \n");
			System.out.println("------------sql: graficarPoscionDeuda------------ \n");
			System.out.println(sql.toString());
			System.out.println("------------------------------------------------------- \n");
				
			resultado = jdbcTemplate.queryForList(sql.toString());		 
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Monitor, C:MonitorDaoImpl, M:graficarPoscionDeuda");
		}return resultado;
	}
	/***************************************************************/
	
	public void setDataSource(DataSource dataSource){
		try{
			this.jdbcTemplate = new JdbcTemplate(dataSource);
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Monitor, C:MonitorDaoImpl, M:setDataSource");
		}
	}
	
}