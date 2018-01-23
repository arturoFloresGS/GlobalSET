package com.webset.set.egresos.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import com.webset.set.egresos.dto.DataReporteCupos;
import com.webset.set.egresos.dto.ProveedorDto;
import com.webset.set.utilerias.Bitacora;
import com.webset.set.utilerias.ConsultasGenerales;
import com.webset.set.utilerias.dto.LlenaComboEmpresasDto;





public class ReporteDeCuposDao {
	
	private JdbcTemplate jdbcTemplate;
	private Bitacora bitacora=new Bitacora();
	private ConsultasGenerales consultasGenerales;
//	private Funciones funciones= new Funciones();
	
	public ReporteDeCuposDao(){
		
		bitacora=new Bitacora();
//		funciones= new Funciones();
		
	}
		
	public void setDataSource(DataSource dataSource){
		
		try{
			
			this.jdbcTemplate = new JdbcTemplate(dataSource);
			
			if (jdbcTemplate != null)
			{
				consultasGenerales = new ConsultasGenerales(jdbcTemplate);
				
			}
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Egresos, C:ReporteDeCuposDao, M:setDataSource");
		}
	}
	
	public List<LlenaComboEmpresasDto> llenaComboEmpresas( int idUsuario )
	{
		return consultasGenerales.llenarComboEmpresas(idUsuario);
	}

	public List<ProveedorDto> llenaComboProveedor(int noPersona) {
		 
		 
		String sql = "";
		StringBuffer sb = new StringBuffer();
				
		sb.append( " SELECT	no_persona	as ID, ");	                    sb.append( "\n");
		sb.append( " rTRIM(isnull(razon_social,'')) +");                sb.append( "\n");
		sb.append( " ' ' + ");                                          sb.append( "\n");
		sb.append( " rTRIM(isnull(NOMBRE,'')) +");                      sb.append( "\n"); 
		sb.append( " ' ' ");                                            sb.append( "\n");
		sb.append( " + rTRIM(isnull(PATERNO,'')) +");                   sb.append( "\n");
		sb.append( " ' ' ");                                            sb.append( "\n");
		sb.append( " + rTRIM(isnull(MATERNO,'')) as DESCRIP");          sb.append( "\n");
		sb.append( " FROM	persona	");                                 sb.append( "\n");
		sb.append( " WHERE	id_tipo_persona='P'");		                sb.append( "\n");
		sb.append( " AND no_empresa in(552)	");                         sb.append( "\n");
		sb.append( " AND no_persona = " + noPersona + "");     sb.append( "\n");
	  	
				
	    sql=sb.toString();
	    
	    System.out.println( sql );
	    
		List <ProveedorDto> proveedores = null;

		proveedores = jdbcTemplate.query(sql, new RowMapper<ProveedorDto>(){
			
			public ProveedorDto mapRow(ResultSet rs, int idx){
				
				ProveedorDto proveedor = new ProveedorDto();
					try {
						
						proveedor.setNoPersona( rs.getInt("id") );
						proveedor.setNomPersona( rs.getString("descrip") );
						
					} catch (SQLException e) {
						
						bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Traspasos, C:ReporteDeCuposDao, M:llenarComboProveedores");
						
					}
					
				return proveedor;				
		}});
			
		return proveedores;		
		
		
	}
	
	public List<ProveedorDto> llenaComboProveedor(String nomPersona) {
		 
		String sql = "";
		StringBuffer sb = new StringBuffer();
				
		sb.append( " SELECT	no_persona	as ID, ");	                    sb.append( "\n");
		sb.append( " rTRIM(isnull(razon_social,'')) +");                sb.append( "\n");
		sb.append( " ' ' + ");                                          sb.append( "\n");
		sb.append( " rTRIM(isnull(NOMBRE,'')) +");                      sb.append( "\n"); 
		sb.append( " ' ' ");                                            sb.append( "\n");
		sb.append( " + rTRIM(isnull(PATERNO,'')) +");                   sb.append( "\n");
		sb.append( " ' ' ");                                            sb.append( "\n");
		sb.append( " + rTRIM(isnull(MATERNO,'')) as DESCRIP");          sb.append( "\n");
		sb.append( " FROM	persona	");                                 sb.append( "\n");
		sb.append( " WHERE	id_tipo_persona='P'");		                sb.append( "\n");
		sb.append( " AND no_empresa in(552)	");                         sb.append( "\n");
		sb.append( " AND ((razon_social like '%" + nomPersona + "%'");	sb.append( "\n");
		sb.append( " or paterno like '%" + nomPersona +  "%'	");     sb.append( "\n");
		sb.append( " or materno like '%" + nomPersona +"%'	");         sb.append( "\n");
		sb.append( " or nombre like '%" + nomPersona +"%' ))	");     sb.append( "\n");
	  	
				
	    sql=sb.toString();
	    
	    System.out.println( sql );
	    
		List <ProveedorDto> proveedores = null;

		proveedores = jdbcTemplate.query(sql, new RowMapper<ProveedorDto>(){
			
			public ProveedorDto mapRow(ResultSet rs, int idx){
				
				ProveedorDto proveedor = new ProveedorDto();
					try {
						
						proveedor.setNoPersona( rs.getInt("id") );
						proveedor.setNomPersona( rs.getString("descrip") );
						
					} catch (SQLException e) {
						
						bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Traspasos, C:ReporteDeCuposDao, M:llenarComboProveedores");
						
					}
					
				return proveedor;				
		}});
			
		return proveedores;		
		
	}//End function

	public List<DataReporteCupos> getReporteCupos(int tipoBusqueda,
			int noEmpresa, int noProveedor, String fecIni, String fecFin) {

		String sql = "";
		StringBuffer sb = new StringBuffer();
		
		if( tipoBusqueda == 1 ){
				
			sb.append( "SELECT mov.no_empresa as noEmpresa,           ");	sb.append( "\n");
			sb.append( "	   mov.id_banco as idBanco,               "); 	sb.append( "\n");
			sb.append( "	   mov.id_chequera as idChequera,         "); 	sb.append( "\n");
			sb.append( "	   convert(VARCHAR,sag.fecha_propuesta,103) as fechaPropuesta, "); 	sb.append( "\n");
			sb.append( "	   sag.fecha_pago as fechaPago,           "); 	sb.append( "\n");
			sb.append( "	   mov.id_forma_pago as idFormaPago,      ");	sb.append( "\n");
			sb.append( "	   mov.no_cliente as noBeneficiario,      ");	sb.append( "\n");
			sb.append( "	   mov.beneficiario as nomBeneficiario,   ");	sb.append( "\n");
			sb.append( "	   mov.concepto as concepto,              ");	sb.append( "\n");
			sb.append( "	   id_banco_benef as idBancoBenef,        "); 	sb.append( "\n");
			sb.append( "	   id_chequera_benef as idChequeraBenef,  "); 	sb.append( "\n");
			sb.append( "	   mov.no_docto as noDocto,               "); 	sb.append( "\n");
			sb.append( "	   mov.importe as importe,                "); 	sb.append( "\n");
			sb.append( "   	   sag.cve_control as cveControl,         ");	sb.append( "\n");			         
			sb.append( "   	   cb.desc_banco as descBanco,            ");	sb.append( "\n");
			sb.append( "   	   e.nom_empresa as nomEmpresa,           ");	sb.append( "\n");
			sb.append( "   	   cfp.desc_forma_pago as descFormaPago   ");	sb.append( "\n");
			sb.append( "FROM seleccion_automatica_grupo sag           ");	sb.append( "\n");
			sb.append( "									inner join movimiento mov                "); 	sb.append( "\n");
			sb.append( "									on sag.cve_control = mov.cve_control     ");	sb.append( "\n");
			sb.append( "									and mov.id_tipo_operacion in(3200, 3701) ");	sb.append( "\n");
			sb.append( "									and mov.id_estatus_mov not in('X', 'Z')  ");	sb.append( "\n");
			sb.append( "									and mov.no_empresa in(" + noEmpresa +  ")");	sb.append( "\n");
			sb.append( "									inner join cat_banco cb                  ");	sb.append( "\n");
			sb.append( "									on mov.id_banco = cb.id_banco            ");	sb.append( "\n");
			sb.append( "									inner join empresa e                     ");	sb.append( "\n");
			sb.append( "									on mov.no_empresa = e.no_empresa         ");	sb.append( "\n");
			sb.append( "									inner join cat_forma_pago cfp            ");	sb.append( "\n");
			sb.append( "									on mov.id_forma_pago = cfp.id_forma_pago ");	sb.append( "\n");
			sb.append( "WHERE sag.fecha_pago between '" + fecIni + "' and '" + fecFin + "'"); 				sb.append( "\n");
			//sb.append( "WHERE convert(DATETIME, sag.fecha_pago, 103) between '" + fecIni + "' and '" + fecFin + "'"); 				sb.append( "\n");
		
		}
		
		if( tipoBusqueda == 2 ){
			
			sb.append( "SELECT mov.no_empresa as noEmpresa,           ");	sb.append( "\n");
			sb.append( "	   mov.id_banco as idBanco,               "); 	sb.append( "\n");
			sb.append( "	   mov.id_chequera as idChequera,         "); 	sb.append( "\n");
			sb.append( "	   convert(VARCHAR,sag.fecha_propuesta,103) as fechaPropuesta, "); 	sb.append( "\n");
			sb.append( "	   sag.fecha_pago as fechaPago,           "); 	sb.append( "\n");
			sb.append( "	   mov.id_forma_pago as idFormaPago,      ");	sb.append( "\n");
			sb.append( "	   mov.no_cliente as noBeneficiario,      ");	sb.append( "\n");
			sb.append( "	   mov.beneficiario as nomBeneficiario,   ");	sb.append( "\n");
			sb.append( "	   mov.concepto as concepto,              ");	sb.append( "\n");
			sb.append( "	   id_banco_benef as idBancoBenef,        "); 	sb.append( "\n");
			sb.append( "	   id_chequera_benef as idChequeraBenef,  "); 	sb.append( "\n");
			sb.append( "	   mov.no_docto as noDocto,               "); 	sb.append( "\n");
			sb.append( "	   mov.importe as importe,               "); 	sb.append( "\n");
			sb.append( "   	   sag.cve_control as cveControl,         ");	sb.append( "\n");			         
			sb.append( "   	   cb.desc_banco as descBanco,            ");	sb.append( "\n");
			sb.append( "   	   e.nom_empresa as nomEmpresa,           ");	sb.append( "\n");
			sb.append( "   	   cfp.desc_forma_pago as descFormaPago   ");	sb.append( "\n");
			sb.append( "FROM seleccion_automatica_grupo sag           ");	sb.append( "\n");
			sb.append( "									inner join movimiento mov                "); 	sb.append( "\n");
			sb.append( "									on sag.cve_control = mov.cve_control     ");	sb.append( "\n");
			sb.append( "									and mov.id_tipo_operacion in(3200, 3701) ");	sb.append( "\n");
			sb.append( "									and mov.id_estatus_mov not in('X', 'Z')  ");	sb.append( "\n");
			sb.append( "									and mov.no_cliente in(" + noProveedor +  ")");	sb.append( "\n");
			sb.append( "									inner join cat_banco cb                  ");	sb.append( "\n");
			sb.append( "									on mov.id_banco = cb.id_banco            ");	sb.append( "\n");
			sb.append( "									inner join empresa e                     ");	sb.append( "\n");
			sb.append( "									on mov.no_empresa = e.no_empresa         ");	sb.append( "\n");
			sb.append( "									inner join cat_forma_pago cfp            ");	sb.append( "\n");
			sb.append( "									on mov.id_forma_pago = cfp.id_forma_pago ");	sb.append( "\n");	
			sb.append( "WHERE  sag.fecha_pago between '" + fecIni + "' and '" + fecFin + "'"); 				sb.append( "\n");
			
		}
				
	    sql=sb.toString();
	    
	    System.out.println( sql );
	    
		List <DataReporteCupos> datas = null;

		datas = jdbcTemplate.query(sql, new RowMapper<DataReporteCupos>(){
			
			public DataReporteCupos mapRow(ResultSet rs, int idx){
				
				DataReporteCupos data = new DataReporteCupos();
					try {
						
						data.setNoEmpresa( rs.getInt( "noEmpresa" ) );
						data.setIdBanco( rs.getInt( "idBanco") );
						data.setIdChequera( rs.getString( "idChequera" ) );
						data.setFechaPropuesta( rs.getString( "fechaPropuesta" ) );
						data.setFechaPago( rs.getString( "fechaPago" ) );
						data.setIdFormaPago( rs.getInt( "idFormaPago" ) );
						data.setNoBeneficiario( rs.getInt( "noBeneficiario" ) );
						data.setNomBeneficiario( rs.getString( "nomBeneficiario" ) );
						data.setConcepto( rs.getString( "concepto" ) );
						data.setIdBancoBenef( rs.getInt( "idBancoBenef" ) );
						data.setIdChequeraBenef( rs.getString( "idChequeraBenef" ) );
						data.setNoDocto( rs.getString( "noDocto" ) );
						data.setCveControl( rs.getString( "cveControl") );
						data.setImporte( rs.getDouble("importe"));
						data.setDescFormaPago(rs.getString( "descFormaPago"));
						data.setDescBanco(rs.getString( "descBanco"));
						data.setCveControl(rs.getString( "cveControl"));
						data.setNomEmpresa(rs.getString( "nomEmpresa"));
						data.setDescFormaPago(rs.getString( "descFormaPago"));
						data.setDescBanco(rs.getString( "descBanco"));

						
					} catch (SQLException e) {						
						bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Traspasos, C:ReporteDeCuposDao, M:getReporteCupos");						
					}
					
				return data;				
		}});
			
		return datas;		
		
	}

	public List<Map<String, Object>> getReporteCuposV2(int tipoBusqueda, int noEmpresa,
										  int noProveedor, String fecIni, 
										  String fecFin) {
		
		List<Map<String, Object>> listResult = new ArrayList<Map<String, Object>>();
		
		String sql = "";
		StringBuffer sb = new StringBuffer();
		
		if( tipoBusqueda == 1 ){
				
			sb.append( "SELECT mov.no_empresa as noEmpresa,           ");	sb.append( "\n");
			sb.append( "	   mov.id_banco as idBanco,               "); 	sb.append( "\n");
			sb.append( "	   mov.id_chequera as idChequera,         "); 	sb.append( "\n");
			sb.append( "	   convert(VARCHAR,sag.fecha_propuesta,103) as fechaPropuesta, "); 	sb.append( "\n");
			sb.append( "	   sag.fecha_pago as fechaPago,           "); 	sb.append( "\n");
			sb.append( "	   mov.id_forma_pago as idFormaPago,      ");	sb.append( "\n");
			sb.append( "	   mov.no_cliente as noBeneficiario,      ");	sb.append( "\n");
			sb.append( "	   mov.beneficiario as nomBeneficiario,   ");	sb.append( "\n");
			sb.append( "	   mov.concepto as concepto,              ");	sb.append( "\n");
			sb.append( "	   id_banco_benef as idBancoBenef,        "); 	sb.append( "\n");
			sb.append( "	   id_chequera_benef as idChequeraBenef,  "); 	sb.append( "\n");
			sb.append( "	   mov.no_docto as noDocto,               "); 	sb.append( "\n");
			sb.append( "	   mov.importe as importe,               "); 	sb.append( "\n");
			sb.append( "   	   sag.cve_control as cveControl,         ");	sb.append( "\n");			         
			sb.append( "   	   cb.desc_banco as descBanco,            ");	sb.append( "\n");
			sb.append( "   	   e.nom_empresa as nomEmpresa,           ");	sb.append( "\n");
			sb.append( "   	   cfp.desc_forma_pago as descFormaPago   ");	sb.append( "\n");
			sb.append( "FROM seleccion_automatica_grupo sag           ");	sb.append( "\n");
			sb.append( "									inner join movimiento mov                "); 	sb.append( "\n");
			sb.append( "									on sag.cve_control = mov.cve_control     ");	sb.append( "\n");
			sb.append( "									and mov.id_tipo_operacion in(3200, 3701) ");	sb.append( "\n");
			sb.append( "									and mov.id_estatus_mov not in('X', 'Z')  ");	sb.append( "\n");
			sb.append( "									and mov.no_empresa in(" + noEmpresa +  ")");	sb.append( "\n");
			sb.append( "									inner join cat_banco cb                  ");	sb.append( "\n");
			sb.append( "									on mov.id_banco = cb.id_banco            ");	sb.append( "\n");
			sb.append( "									inner join empresa e                     ");	sb.append( "\n");
			sb.append( "									on mov.no_empresa = e.no_empresa         ");	sb.append( "\n");
			sb.append( "									inner join cat_forma_pago cfp            ");	sb.append( "\n");
			sb.append( "									on mov.id_forma_pago = cfp.id_forma_pago ");	sb.append( "\n");
			sb.append( "WHERE  sag.fecha_pago between '" + fecIni + "' and '" + fecFin + "'"); 				sb.append( "\n");
		
		}
		
		if( tipoBusqueda == 2 ){
			
			sb.append( "SELECT mov.no_empresa as noEmpresa,           ");	sb.append( "\n");
			sb.append( "	   mov.id_banco as idBanco,               "); 	sb.append( "\n");
			sb.append( "	   mov.id_chequera as idChequera,         "); 	sb.append( "\n");
			sb.append( "	   convert(VARCHAR,sag.fecha_propuesta,103) as fechaPropuesta, "); 	sb.append( "\n");
			sb.append( "	   sag.fecha_pago as fechaPago,           "); 	sb.append( "\n");
			sb.append( "	   mov.id_forma_pago as idFormaPago,      ");	sb.append( "\n");
			sb.append( "	   mov.no_cliente as noBeneficiario,      ");	sb.append( "\n");
			sb.append( "	   mov.beneficiario as nomBeneficiario,   ");	sb.append( "\n");
			sb.append( "	   mov.concepto as concepto,              ");	sb.append( "\n");
			sb.append( "	   id_banco_benef as idBancoBenef,        "); 	sb.append( "\n");
			sb.append( "	   id_chequera_benef as idChequeraBenef,  "); 	sb.append( "\n");
			sb.append( "	   mov.no_docto as noDocto,               "); 	sb.append( "\n");
			sb.append( "	   mov.importe as importe,               "); 	sb.append( "\n");
			sb.append( "   	   sag.cve_control as cveControl,         ");	sb.append( "\n");			         
			sb.append( "   	   cb.desc_banco as descBanco,            ");	sb.append( "\n");
			sb.append( "   	   e.nom_empresa as nomEmpresa,           ");	sb.append( "\n");
			sb.append( "   	   cfp.desc_forma_pago as descFormaPago   ");	sb.append( "\n");
			sb.append( "FROM seleccion_automatica_grupo sag           ");	sb.append( "\n");
			sb.append( "									inner join movimiento mov                "); 	sb.append( "\n");
			sb.append( "									on sag.cve_control = mov.cve_control     ");	sb.append( "\n");
			sb.append( "									and mov.id_tipo_operacion in(3200, 3701) ");	sb.append( "\n");
			sb.append( "									and mov.id_estatus_mov not in('X', 'Z')  ");	sb.append( "\n");
			sb.append( "									and mov.no_cliente in(" + noProveedor +  ")");	sb.append( "\n");
			sb.append( "									inner join cat_banco cb                  ");	sb.append( "\n");
			sb.append( "									on mov.id_banco = cb.id_banco            ");	sb.append( "\n");
			sb.append( "									inner join empresa e                     ");	sb.append( "\n");
			sb.append( "									on mov.no_empresa = e.no_empresa         ");	sb.append( "\n");
			sb.append( "									inner join cat_forma_pago cfp            ");	sb.append( "\n");
			sb.append( "									on mov.id_forma_pago = cfp.id_forma_pago ");	sb.append( "\n");
			sb.append( "WHERE  sag.fecha_pago between '" + fecIni + "' and '" + fecFin + "'"); 				sb.append( "\n");
			
		}
		
		sql=sb.toString();
		
		listResult = jdbcTemplate.query(sql.toString(), new RowMapper<Map<String, Object>>() {
			
			public Map<String, Object> mapRow(ResultSet rs, int idx)throws SQLException {
				Map<String, Object> map = new HashMap<String, Object>();
				
				map.put("noEmpresa", rs.getInt( "noEmpresa" ) );
				map.put("idBanco", rs.getInt( "idBanco") );
				map.put("idChequera", rs.getString( "idChequera" ) );
				map.put("fechaPropuesta", rs.getString( "fechaPropuesta" ) );
				map.put("fechaPago", rs.getString( "fechaPago" ) );
				map.put("idFormaPago", rs.getInt( "idFormaPago" ) );
				map.put("noBeneficiario", rs.getInt( "noBeneficiario" ) );
				map.put("nomBeneficiario", rs.getString( "nomBeneficiario" ) );
				map.put("concepto", rs.getString( "concepto" ) );
				map.put("idBancoBenef", rs.getInt( "idBancoBenef" ) );
				map.put("idChequeraBenef", rs.getString( "idChequeraBenef" ) );
				map.put("noDocto", rs.getString( "noDocto" ) );
				map.put("cveControl", rs.getString( "cveControl") );
				map.put("importe", rs.getDouble("importe"));
				map.put("nomEmpresa", rs.getString( "nomEmpresa"));
				map.put("descFormaPago", rs.getString( "descFormaPago"));
				map.put("descBanco", rs.getString( "descBanco"));
				
				return map;
        	}
			
		});
		
		System.out.println( listResult.toString());
		
		return listResult;
	}
	

}// End class ReporteDeCuposDao
