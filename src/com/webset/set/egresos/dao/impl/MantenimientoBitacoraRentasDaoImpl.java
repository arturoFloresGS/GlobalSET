package com.webset.set.egresos.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.jdbc.CannotGetJdbcConnectionException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import com.webset.set.utilerias.Bitacora;
import com.webset.set.utilerias.dto.LlenaComboGralDto;
import com.webset.utils.tools.Utilerias;
import com.webset.set.egresos.dao.MantenimientoBitacoraRentasDao;
import com.webset.set.egresos.dto.MantenimientoBitacoraRentasDto;

public class MantenimientoBitacoraRentasDaoImpl implements MantenimientoBitacoraRentasDao{
	private JdbcTemplate jdbcTemplate = new JdbcTemplate();
	private Bitacora bitacora = new Bitacora();
	
	public List<MantenimientoBitacoraRentasDto> llenaGridBitacoraRentas(String noEmpresa, String noBeneficiario , String estatus){
		List<MantenimientoBitacoraRentasDto> listaResultado = new ArrayList<MantenimientoBitacoraRentasDto>();
		StringBuffer sql = new StringBuffer();
		try{ 
			sql.append("SELECT bpr.no_empresa, e.nom_empresa, no_benef, razon_social, no_docto, no_folio_det, importe, id_tipo_gasto, causa_rechazo, estatus, ");
			sql.append("\n convert(char,fecha_rechazo,103) AS fecha");
			sql.append( "\n  FROM bitacora_pago_rechazado bpr ");
			sql.append( "\n   JOIN empresa e ON bpr.no_empresa = e.no_empresa ");
			sql.append( "\n  JOIN persona p ON  bpr.no_benef=p.equivale_persona ");
			
			if(estatus!= null && !estatus.equals(""))
				sql.append("\n  where estatus = '"+Utilerias.validarCadenaSQL(estatus) +"' ");
			else
				sql.append("\n  where estatus in  ('C' ,'R')");
			
			if(noEmpresa!= null &&!noEmpresa.equals(""))
				sql.append( "\n AND bpr.no_empresa = "+Utilerias.validarCadenaSQL(noEmpresa));
			
			if(noBeneficiario!= null && !noBeneficiario.equals("")  && !noBeneficiario.equals("0"))
				sql.append( "\n AND bpr.no_benef = "+Utilerias.validarCadenaSQL(noBeneficiario));
			
			sql.append( "\n ORDER BY fecha_rechazo DESC");
			
			System.out.println(sql.toString());
			listaResultado = jdbcTemplate.query(sql.toString(), new RowMapper<MantenimientoBitacoraRentasDto>(){
					public MantenimientoBitacoraRentasDto mapRow(ResultSet rs, int idx) throws SQLException{
						MantenimientoBitacoraRentasDto campos = new MantenimientoBitacoraRentasDto();
						campos.setNoEmpresa(rs.getString("NO_EMPRESA"));
						campos.setNomEmpresa(rs.getString("NOM_EMPRESA"));
						campos.setNoBeneficiario(rs.getString("NO_BENEF"));
						campos.setNomBeneficiario(rs.getString("RAZON_SOCIAL"));
						campos.setNoFolioDetalle(rs.getString("NO_FOLIO_DET"));
						campos.setNoDocumento(rs.getString("NO_DOCTO"));
						campos.setFechaRechazo(rs.getString("FECHA"));
						campos.setImporte(rs.getString("IMPORTE"));
						campos.setIdTipoGasto(rs.getString("ID_TIPO_GASTO"));
						campos.setCausaRechazo(rs.getString("CAUSA_RECHAZO"));
						campos.setEstatus(rs.getString("ESTATUS"));
						return campos;
					}
				});
		
		}
		catch(CannotGetJdbcConnectionException e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Egresos, C:MantenimientoBitacoraRentasDaoImpl, M:llenaGridBitacoraRentas");
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Egresos, C:MantenimientoBitacoraRentasDaoImpl, M:llenaGridBitacoraRentas");
		}return listaResultado;
	}
	
	public String updateMantenimientoBitacoraRentas(List<MantenimientoBitacoraRentasDto> dtos){
		String resultado="Error";
		StringBuffer sql = new StringBuffer();
		int resp=0;
		boolean error=false;
		
		try{
		
			for (int i = 0; i < dtos.size(); i++) {
				sql.delete(0, sql.length());
				sql.append("update BITACORA_PAGO_RECHAZADO ");
				sql.append("\n set ESTATUS='C' ");
				sql.append("\n  where ");
				sql.append("\n NO_EMPRESA= " + Utilerias.validarCadenaSQL(dtos.get(i).getNoEmpresa()) + " ");
				sql.append("\n AND NO_BENEF= '" +  Utilerias.validarCadenaSQL(dtos.get(i).getNoBeneficiario()) + "'");
				sql.append("\n AND NO_DOCTO= '" +  Utilerias.validarCadenaSQL(dtos.get(i).getNoDocumento()) + "' ");
				sql.append("\n AND FECHA_RECHAZO= CONVERT(DATETIME,'" +  Utilerias.validarCadenaSQL(dtos.get(i).getFechaRechazo()) + "',103)");
				sql.append("\n AND ID_TIPO_GASTO= " +  Utilerias.validarCadenaSQL(dtos.get(i).getIdTipoGasto()) + "");
				sql.append("\n AND IMPORTE= " +  Utilerias.validarCadenaSQL(dtos.get(i).getImporte()) + "");
				sql.append("\n AND no_folio_det= " +  Utilerias.validarCadenaSQL(dtos.get(i).getNoFolioDetalle()) + "");
				resp=jdbcTemplate.update(sql.toString());
			System.out.println("Query Update \n"+sql.toString());
				
				if(resp!=1){error=true; resultado+=": Durante la correccion de datos";}
			
			}
			if(!error){
				resultado="Exito, Datos corregidos";
				
			}
		
		}
		catch(CannotGetJdbcConnectionException e){
			resultado="Error de conexion con la base de datos";
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Egresos, C:MantenimientoBitacoraRentasDaoImpl, M:llenaGridBitacoraRentas");
			
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Egresos, C:MantenimientoBitacoraRentasDaoImpl, M:updateMantenimientoBitacoraRentas");
		
		}return resultado;
	}
	
	public List<LlenaComboGralDto> llenaComboEmpresa(String noUsuario){
		List<LlenaComboGralDto> listaResultado = new ArrayList<LlenaComboGralDto>();
		StringBuffer sbSql = new StringBuffer();
		try{
			if(noUsuario != null && !noUsuario.equals("")){
				 sbSql.append("SELECT e.no_empresa as id , e.nom_empresa as descripcion  ");
				    sbSql.append("\n FROM bitacora_pago_rechazado b join  empresa e  on b.no_empresa=e.no_empresa");
				    sbSql.append("\n join usuario_empresa ue on e.no_empresa = ue.no_empresa");
				    sbSql.append("\n where ue.no_usuario = " + Utilerias.validarCadenaSQL(noUsuario ));
				    sbSql.append("\n  ORDER BY nom_empresa");
				    
				    
				    
				    System.out.println(sbSql.toString());
				listaResultado = jdbcTemplate.query(sbSql.toString(), new RowMapper<LlenaComboGralDto>(){
					public LlenaComboGralDto mapRow(ResultSet rs, int idx) throws SQLException{
						LlenaComboGralDto campos = new LlenaComboGralDto();
						campos.setId(rs.getInt("id"));
						campos.setDescripcion(rs.getString("descripcion"));
						return campos;
					}
				});
			}
		}
		
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Egresos, C:MantenimientoBitacoraRentasDaoImpl, M:llenaComboEmpresa");
		}return listaResultado;
		
	}
	
	public List<LlenaComboGralDto> llenaComboProveedor(String nombre , String noProv){
		List<LlenaComboGralDto> listaResultado = new ArrayList<LlenaComboGralDto>();
		StringBuffer sbSql = new StringBuffer();
		try{
			sbSql.append("select p.equivale_persona as id, p.razon_social as descripcion ");
			sbSql.append("\n from bitacora_pago_rechazado b join  persona p ");
			sbSql.append("\n on b.no_benef=p.equivale_persona");
			sbSql.append("\n WHERE id_tipo_persona = 'P' ");
			if(!nombre.equals(""))
				sbSql.append("\n AND razon_social like '%"+ Utilerias.validarCadenaSQL(nombre.toUpperCase() )+"%'");
			if(!noProv.equals("0"))
				sbSql.append("\n AND no_benef ="+ noProv);
			sbSql.append("\n ORDER BY razon_social");
			
			System.out.println( "Combo proveedor: "+sbSql.toString());
			listaResultado = jdbcTemplate.query(sbSql.toString(), new RowMapper<LlenaComboGralDto>(){
				public LlenaComboGralDto mapRow(ResultSet rs, int idx) throws SQLException{
					LlenaComboGralDto campos = new LlenaComboGralDto();
					campos.setId(rs.getInt("id"));
					campos.setDescripcion(rs.getString("descripcion"));
					return campos;
				}
			});
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Egresos, C:MantenimientoBitacoraRentasDaoImpl, M: llenaComboProveedor");
		}return listaResultado;
	}
	
	//**************************************************************		
	public void setDataSource(DataSource dataSource)
	{
		try
		{
			this.jdbcTemplate = new JdbcTemplate(dataSource);
		}
		catch(Exception e)
		{
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Utilerias, C:MantenimientoBitacoraRentasDaoImpl, M:setDataSource");
		}
	}
	
}
