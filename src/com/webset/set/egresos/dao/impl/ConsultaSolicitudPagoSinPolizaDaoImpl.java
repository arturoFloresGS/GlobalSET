package com.webset.set.egresos.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import com.webset.set.utilerias.Bitacora;
import com.webset.utils.tools.Utilerias;
import com.webset.set.egresos.dao.ConsultaSolicitudPagoSinPolizaDao;
import com.webset.set.egresos.dto.ConsultaSolicitudPagoSinPolizaDto;

public class ConsultaSolicitudPagoSinPolizaDaoImpl implements ConsultaSolicitudPagoSinPolizaDao{
	JdbcTemplate jdbcTemplate;
	Bitacora bitacora;

	public List<ConsultaSolicitudPagoSinPolizaDto> llenaGrid(String noEmpresa, String fechaIni, String fechaFin, String usuario,boolean facultad,String usuarioEnCurso){
		List<ConsultaSolicitudPagoSinPolizaDto> listaResultado = new ArrayList<ConsultaSolicitudPagoSinPolizaDto>();
		StringBuffer sql = new StringBuffer();
		try{
				sql.append("SELECT  m.no_empresa,e.nom_empresa,convert(char,m.fec_valor,10) as fecha,m.beneficiario,m.concepto,m.importe,m.no_docto,m.usuario_alta ");
				sql.append( "\n FROM movimiento m ");
				sql.append( "\n JOIN  empresa e ON m.no_empresa = e.no_empresa ");
				sql.append( "\n  where m.id_tipo_operacion=3000 ");
				sql.append( "\n AND m.id_estatus_mov='C'");
				if(!noEmpresa.equals("")){
					sql.append( "\n AND m.no_empresa="+Utilerias.validarCadenaSQL(noEmpresa));
				}
				if(facultad){
					if(!usuario.equals(""))
						sql.append( "\n AND m.usuario_alta="+Utilerias.validarCadenaSQL(usuario));
					
				}else{
					sql.append( "\n AND m.usuario_alta="+Utilerias.validarCadenaSQL(usuarioEnCurso));
				}
				if(!fechaIni.equals("") && !fechaFin.equals("")){
					sql.append( "\n AND m.fec_valor BETWEEN convert(datetime,'"+  Utilerias.validarCadenaSQL(fechaIni) + "',103) AND convert(datetime,'"+ Utilerias.validarCadenaSQL(fechaFin)+"',103)" );
				}
				if(!fechaIni.equals("") && fechaFin.equals("")){
					sql.append( "\n AND m.fec_valor > convert(datetime,'"+  Utilerias.validarCadenaSQL( fechaIni) + "',103)");
				}
				if(fechaIni.equals("") && !fechaFin.equals("")){
					sql.append( "\n AND m.fec_valor <  convert(datetime,'" + Utilerias.validarCadenaSQL(fechaFin)+ "',103)" );
				}
				
				sql.append( "\n UNION ");
				sql.append( "\n SELECT  hs.no_empresa,e.nom_empresa,convert(char(10),hs.fec_valor,103) as fecha,hs.beneficiario,hs.concepto,hs.importe,hs.no_docto,hs.usuario_alta ");
				sql.append( "\n FROM hist_solicitud hs  ");
				sql.append( "\n  JOIN  empresa e ON hs.no_empresa = e.no_empresa ");
				sql.append( "\n  where hs.id_tipo_operacion=3000");
				sql.append( "\n  AND hs.origen_mov='SET' ");
				sql.append( "\n  AND hs.id_estatus_MOV='A' ");
			if(!noEmpresa.equals("")){
					sql.append( "\n AND hs.no_empresa="+Utilerias.validarCadenaSQL(noEmpresa));
				}
				if(facultad){
					if(!usuario.equals(""))
						sql.append( "\n AND m.usuario_alta="+Utilerias.validarCadenaSQL(usuario));
					
				}else{
					sql.append( "\n AND hs.usuario_alta="+Utilerias.validarCadenaSQL(usuarioEnCurso));
				}
				if(!fechaIni.equals("") && !fechaFin.equals("")){
					sql.append( "\n AND hs.fec_valor BETWEEN convert(datetime,'"+  Utilerias.validarCadenaSQL(fechaIni) + "',103) AND convert(datetime,'"+ Utilerias.validarCadenaSQL(fechaFin)+ "',103)" );
				}
				if(!fechaIni.equals("") && fechaFin.equals("")){
					sql.append( "\n AND hs.fec_valor > convert(datetime,'"+   Utilerias.validarCadenaSQL(fechaIni) + "',103)");
				}
				if(fechaIni.equals("") && !fechaFin.equals("")){
					sql.append( "\n AND hs.fec_valor <  convert(datetime,'" + Utilerias.validarCadenaSQL(fechaFin)+ "',103)" );
				}
				System.out.println(sql.toString());
				listaResultado = jdbcTemplate.query(sql.toString(), new RowMapper<ConsultaSolicitudPagoSinPolizaDto>(){
					public ConsultaSolicitudPagoSinPolizaDto mapRow(ResultSet rs, int idx) throws SQLException{
						ConsultaSolicitudPagoSinPolizaDto campos = new ConsultaSolicitudPagoSinPolizaDto();
						campos.setNomEmpresa(rs.getString("nom_empresa"));
						campos.setFechaPago(rs.getString("fecha"));
						campos.setBeneficiario(rs.getString("beneficiario"));
						campos.setConcepto(rs.getString("concepto"));
						campos.setImporte(rs.getString("importe"));
						campos.setNoDocumento(rs.getString("no_docto"));
						campos.setUsuario(rs.getString("usuario_alta"));
						return campos;
					}
				});
			}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Utilerias, C:ConsultaSolicitudPagoSinPolizaDaoImpl, M:llenaGrid");
		}return listaResultado;
	}
	
	public List<ConsultaSolicitudPagoSinPolizaDto> llenaComboUsuario(boolean facultad, String usuario){
		List<ConsultaSolicitudPagoSinPolizaDto> listaResultado = new ArrayList<ConsultaSolicitudPagoSinPolizaDto>();
		StringBuffer sbSql = new StringBuffer();
		try{
			sbSql.append("SELECT NO_USUARIO , (NOMBRE + ' ' + PATERNO + ' ' + MATERNO) as nombre ");
			sbSql.append("\n FROM CAT_USUARIO");
			if(!facultad){
				sbSql.append("\n where  no_usuario = "+ Utilerias.validarCadenaSQL(usuario));
			}
			listaResultado = jdbcTemplate.query(sbSql.toString(), new RowMapper<ConsultaSolicitudPagoSinPolizaDto>(){
				public ConsultaSolicitudPagoSinPolizaDto mapRow(ResultSet rs, int idx) throws SQLException{
					ConsultaSolicitudPagoSinPolizaDto campos = new ConsultaSolicitudPagoSinPolizaDto();
					campos.setUsuario(rs.getString("NO_USUARIO"));
					campos.setNomUsuario(rs.getString("nombre"));
					return campos;
				}
			});
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Egresos, C:MantenimientoBitacoraRentasDaoImpl, M: llenaComboUsuario");
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
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Utilerias, C:ConsultaSolicitudPagoSinPolizaDaoImpl, M:setDataSource");
		}
	}
	
}
