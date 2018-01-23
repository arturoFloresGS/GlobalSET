package com.webset.set.inversiones.dao.impl;

import com.webset.set.financiamiento.dto.Amortizaciones;
import com.webset.set.inversiones.dao.PosicionInversionDao;
import com.webset.set.inversiones.dto.PosicionInversionDto;
import com.webset.set.utilerias.Bitacora;
import com.webset.set.utilerias.ConsultasGenerales;
import com.webset.set.utilerias.Contexto;
import com.webset.set.utilerias.Funciones;
import com.webset.set.utilerias.GlobalSingleton;
import com.webset.set.utilerias.dto.LlenaComboGralDto;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

public class PosicionInversionDaoImpl implements PosicionInversionDao {
	
	
	
	private JdbcTemplate jdbcTemplate;
	Bitacora bitacora=new Bitacora();
	ConsultasGenerales consultasGenerales;
	Funciones funciones= new Funciones();
	GlobalSingleton globalSingleton;
	Contexto contexto=new  Contexto();
	
	public Bitacora getBitacora() {
		return bitacora;
	}




	public void setBitacora(Bitacora bitacora) {
		this.bitacora = bitacora;
	}




	public ConsultasGenerales getConsultasGenerales() {
		return consultasGenerales;
	}




	public void setConsultasGenerales(ConsultasGenerales consultasGenerales) {
		this.consultasGenerales = consultasGenerales;
	}




	public Funciones getFunciones() {
		return funciones;
	}




	public void setFunciones(Funciones funciones) {
		this.funciones = funciones;
	}




	public Contexto getContexto() {
		return contexto;
	}




	public void setContexto(Contexto contexto) {
		this.contexto = contexto;
	}




	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}
	

	
	
	public void setDataSource(DataSource dataSource)
	{
		try
		{
			this.jdbcTemplate = new JdbcTemplate(dataSource);
		}
		catch(Exception e)
		{
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Personas, C: ConsultaPersonasDaoImpl, M:setDataSource");
		}
	}


	public GlobalSingleton getGlobalSingleton() {
		return globalSingleton;
	}


	public void setGlobalSingleton(GlobalSingleton globalSingleton) {
		this.globalSingleton = globalSingleton;
	}


	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}




	@Override
	public List<LlenaComboGralDto> consultarEmpresa(boolean bExistentes) {
		StringBuffer sbSql = new StringBuffer();
		List<LlenaComboGralDto> listListas = new ArrayList<LlenaComboGralDto>();

		try{
			
			if(bExistentes){
				sbSql.append("\n select no_empresa as ID,nom_empresa as describ from empresa where b_concentradora='S' ");
				sbSql.append("\n order by 1");
						    
			}else{
				sbSql.append("\n select no_empresa as ID,nom_empresa as describ from empresa where b_concentradora='S' ");
				sbSql.append("\n order by 1");
			}
			System.out.println("queryEmpresa..::"+sbSql.toString());
			listListas = jdbcTemplate.query(sbSql.toString(), new RowMapper()
			{
				@Override
				public LlenaComboGralDto mapRow(ResultSet rs, int idx)throws SQLException
				{
					LlenaComboGralDto dtoCons = new LlenaComboGralDto();
						dtoCons.setId(rs.getInt("ID"));
						dtoCons.setDescripcion(rs.getString("describ"));
					return dtoCons;
				}
			});
		}
		catch(Exception e)
		{
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P: FinanciamientoModificacionC, C: FinanciamientoModificacionCDaoImpl, consultarLineas");
		}

		return listListas;

	}




	@Override
	public List<LlenaComboGralDto> consultarDivisa(int empresa) {
		StringBuffer sbSql = new StringBuffer();
		List<LlenaComboGralDto> listListas = new ArrayList<LlenaComboGralDto>();

		try{
			if(empresa>0){
				sbSql.append("\n SELECT distinct cc.id_divisa as ID, cd.desc_divisa as describ ");
				sbSql.append("\n  FROM cat_banco cb ,cat_cta_banco cc, cat_divisa cd ");
				sbSql.append("\n	 WHERE cc.id_divisa = cd.id_divisa ");
				sbSql.append("\n  and cc.no_empresa = "+empresa+" ");
				sbSql.append("\n    and cc.tipo_chequera in ('I', 'M') ");
	}
			System.out.println("queryDivisa..::"+sbSql.toString());
			listListas = jdbcTemplate.query(sbSql.toString(), new RowMapper()
			{
				@Override
				public LlenaComboGralDto mapRow(ResultSet rs, int idx)throws SQLException
				{
					LlenaComboGralDto dtoCons = new LlenaComboGralDto();
						dtoCons.setIdStr(rs.getString("ID"));
						dtoCons.setDescripcion(rs.getString("describ"));
					return dtoCons;
				}
			});
		}
		catch(Exception e)
		{
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P: FinanciamientoModificacionC, C: FinanciamientoModificacionCDaoImpl, consultarLineas");
		}

		return listListas;
	}




	@Override
	public List<LlenaComboGralDto> consultarBanco(int emp, String divisa) {
		
		StringBuffer sbSql = new StringBuffer();
		List<LlenaComboGralDto> listListas = new ArrayList<LlenaComboGralDto>();

		try{
			if(emp>0 && divisa!=null){
				
				sbSql.append("\n   SELECT distinct cat_banco.id_banco as ID, "); 
				sbSql.append("\n  cat_banco.desc_banco as describ ");
				sbSql.append("\n FROM cat_banco,cat_cta_banco ");
				sbSql.append("\n   WHERE cat_cta_banco.id_banco = cat_banco.id_banco ");
				sbSql.append("\n  and cat_cta_banco.no_empresa = "+emp+" ");
				sbSql.append("\n  and cat_cta_banco.id_divisa = '"+divisa+"' ");
				sbSql.append("\n  and cat_cta_banco.tipo_chequera in ('I', 'M')  ");
		    }
			System.out.println("queryBanco..::"+sbSql.toString());
			listListas = jdbcTemplate.query(sbSql.toString(), new RowMapper()
			{
				@Override
				public LlenaComboGralDto mapRow(ResultSet rs, int idx)throws SQLException
				{
					LlenaComboGralDto dtoCons = new LlenaComboGralDto();
						dtoCons.setId(rs.getInt("ID"));
						dtoCons.setDescripcion(rs.getString("describ"));
					return dtoCons;
				}
			});
		}
		catch(Exception e)
		{
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P: FinanciamientoModificacionC, C: FinanciamientoModificacionCDaoImpl, consultarLineas");
		}

		return listListas;
		
		
		
		
		
		
	}




	@Override
	public List<LlenaComboGralDto> consultarChequera(int emp, String divisa, int banco) {
		StringBuffer sbSql = new StringBuffer();
		List<LlenaComboGralDto> listListas = new ArrayList<LlenaComboGralDto>();

		try{
			if(emp>0 && divisa!=null){

				
				sbSql.append("\n   SELECT distinct cat_banco.id_banco as ID, "); 
				sbSql.append("\n  id_chequera as describ ");
				sbSql.append("\n FROM cat_banco,cat_cta_banco ");
				sbSql.append("\n   WHERE cat_cta_banco.id_banco = cat_banco.id_banco ");
				sbSql.append("\n  and cat_cta_banco.no_empresa = "+emp+" ");
				sbSql.append("\n  and cat_cta_banco.id_divisa = '"+divisa+"' ");
				sbSql.append("\n   and cat_cta_banco.id_banco = "+banco+" ");
				sbSql.append("\n  and cat_cta_banco.tipo_chequera in ('I', 'M')  ");
		    }
			System.out.println("queryCheuqera..::"+sbSql.toString());
			listListas = jdbcTemplate.query(sbSql.toString(), new RowMapper()
			{
				@Override
				public LlenaComboGralDto mapRow(ResultSet rs, int idx)throws SQLException
				{
					LlenaComboGralDto dtoCons = new LlenaComboGralDto();
						dtoCons.setId(rs.getInt("ID"));
						dtoCons.setDescripcion(rs.getString("describ"));
					return dtoCons;
				}
			});
		}
		catch(Exception e)
		{
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P: FinanciamientoModificacionC, C: FinanciamientoModificacionCDaoImpl, consultarLineas");
		}

		return listListas;
		
		
		
		
		
	}




	@Override
	public String consultarInstitucion() {
		StringBuffer sbSql = new StringBuffer();
		List<LlenaComboGralDto> listListas = new ArrayList<LlenaComboGralDto>();
String res="";
		try{
			
			sbSql.append("\n  select p.nombre_corto as institucion ");
					sbSql.append("\n from persona p, cuenta c, ctas_contrato cc  ");
							sbSql.append("\n where cc.no_cuenta = c.no_cuenta ");
									sbSql.append("\n and c.no_institucion  = p.no_persona ");
											sbSql.append("\n and cc.no_cuenta = c.no_cuenta ");
													sbSql.append("\n and c.no_institucion  = p.no_persona ");
															sbSql.append("\n and p.no_empresa = 1  ");
																	sbSql.append("\n and p.id_tipo_persona = 'B' ");
			  
			  	
			
			
				  
		    
				System.out.println("Institucion..::"+sbSql.toString());
			listListas = jdbcTemplate.query(sbSql.toString(), new RowMapper()
			{
				@Override
				public LlenaComboGralDto mapRow(ResultSet rs, int idx)throws SQLException
				{
					LlenaComboGralDto dtoCons = new LlenaComboGralDto();

						dtoCons.setDescripcion(rs.getString("institucion"));
					return dtoCons;
				}
			});
		}
		catch(Exception e)
		{
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P: FinanciamientoModificacionC, C: FinanciamientoModificacionCDaoImpl, consultarLineas");
		}

		for (LlenaComboGralDto llena: listListas) {
			res= llena.getDescripcion();
		}
		
		return res;
	}




	@Override
	public List<PosicionInversionDto> consultarVencimientos(String fecha, int caja, String institucion, int empresa,String divisa, int banco, String chequera) {
		StringBuffer sbSql = new StringBuffer();
		List<PosicionInversionDto> listDisp= new ArrayList<PosicionInversionDto>();
		String f=funciones.ponerFechaSola(fecha);		
		try
		{
					
			
			if(caja==1){
				
			
			sbSql.append("\n  SELECT case when o.id_estatus_ord = 'V' then 'Vencidas' else 'Por Vencer' end as estatus, ");
			sbSql.append("\n  o.importe As importe ");
			sbSql.append("\n  FROM orden_inversion o  ");
			sbSql.append("\n  WHERE o.id_estatus_ord in ('V', 'A')  ");
			sbSql.append("\n  and (o.id_banco_reg ="+banco+" ");
			sbSql.append("\n 	or (o.id_banco = "+banco+" ");
			sbSql.append("\n  and o.id_banco_reg is NULL)) ");
			sbSql.append("\n  and (o.id_chequera ='"+chequera+"' ");
			sbSql.append("\n  or o.id_chequera_reg ='"+chequera+"' ) ");
			sbSql.append("\n  ORDER BY 1 desc, 2  ");
			}else{
				sbSql.append("\n  SELECT case when o.id_estatus_ord = 'V' then 'Vencidas' else 'Por Vencer' end as estatus, ");
				sbSql.append("\n  o.importe As importe ");
				sbSql.append("\n  FROM orden_inversion o  ");
				sbSql.append("\n  WHERE o.id_estatus_ord in ('V', 'A')  and  o.fec_venc = "+f+"  ");
				sbSql.append("\n  and (o.id_banco_reg ="+banco+" ");
				sbSql.append("\n 	or (o.id_banco = "+banco+" ");
				sbSql.append("\n  and o.id_banco_reg is NULL)) ");
				sbSql.append("\n  and (o.id_chequera ='"+chequera+"' ");
				sbSql.append("\n  or o.id_chequera_reg ='"+chequera+"' ) ");
				sbSql.append("\n  ORDER BY 1 desc, 2  ");	
			}
				


							
		System.out.println("query"+sbSql.toString());
			listDisp = jdbcTemplate.query(sbSql.toString(), new RowMapper()	{
				@Override
				public PosicionInversionDto mapRow(ResultSet rs, int idx)throws SQLException{
					PosicionInversionDto reporte = new PosicionInversionDto();

					
					reporte.setEstatus(rs.getString("estatus"));
					reporte.setImporte(rs.getDouble("importe"));
				    return reporte;
					
					
				}
			});
			
			
		}
		catch(Exception e)
		{
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P: FinanciamientoModificacionC, C: FinanciamientoModificacionCDaoImpl, consultarGrid");
			}
	
		return listDisp;
		
	}




	@Override
	public List<PosicionInversionDto> consultarBarridos(String fecha, int caja, String institucion, int empresa,
			String divisa, int banco, String chequera) {
		StringBuffer sbSql = new StringBuffer();
		List<PosicionInversionDto> listDisp= new ArrayList<PosicionInversionDto>();
String f=funciones.ponerFechaSola(fecha);
		
		try
		{
					
			
			if(caja==1){
				
				sbSql.append("\n SELECT fec_valor as hora, id_tipo_operacion as tipoOperacion , "); 
				sbSql.append("\n   importe, concepto ");
				sbSql.append("\n FROM movimiento ");
				sbSql.append("\n WHERE id_tipo_movto = 'I' ");
				sbSql.append("\n and (((id_estatus_mov = 'A'  ");
				sbSql.append("\n and not (id_tipo_operacion between 4000 and 4199 and b_salvo_buen_cobro='N')) ");
				sbSql.append("\n or (id_estatus_mov='A' ");
				sbSql.append("\n and id_tipo_operacion between 4000 and 4199 ");
				sbSql.append("\n	and b_salvo_buen_cobro='S' ");
				sbSql.append("\n	 ))) ");
				sbSql.append("\n and no_empresa ="+empresa+" ");
				sbSql.append("\n and id_banco="+banco+" ");
				sbSql.append("\n AND id_estatus_mov <> 'X' ");
				sbSql.append("\n and id_chequera='"+chequera+"' ");
				sbSql.append("\n and id_tipo_operacion not in(1013,1014) ");
				sbSql.append("\n union all ");
				sbSql.append("\n SELECT fec_valor as fec_alta, id_tipo_operacion, ");
				sbSql.append("\n importe, concepto ");
				sbSql.append("\n FROM hist_movimiento ");
				sbSql.append("\n WHERE id_tipo_movto = 'I' ");
				sbSql.append("\n and (((id_estatus_mov = 'A' ) ");
				sbSql.append("\n and not (id_tipo_operacion between 3100 and 3199 and b_salvo_buen_cobro='N')) ");
				sbSql.append("\n or (id_estatus_mov='A' ");
				sbSql.append("\n    and id_tipo_operacion between 3100 and 3199 "); 
				sbSql.append("\n and b_salvo_buen_cobro='S' ");
				sbSql.append("\n  )) ");
				sbSql.append("\n and no_empresa ="+empresa+" ");
				sbSql.append("\n	and id_banco="+banco+" ");
				sbSql.append("\n	 AND id_estatus_mov <> 'X' ");
				sbSql.append("\n	and id_chequera='"+chequera+"' ");
				sbSql.append("\n and id_tipo_operacion not in(1013,1014) ");
				 sbSql.append("\n	and id_tipo_operacion not between 4000 and 4199 ");

			}else{
				sbSql.append("\n SELECT fec_valor as hora, id_tipo_operacion as tipoOperacion, "); 
				sbSql.append("\n   importe, concepto ");
				sbSql.append("\n FROM movimiento ");
				sbSql.append("\n WHERE id_tipo_movto = 'I' ");
				sbSql.append("\n and (((id_estatus_mov = 'A' and fec_valor = "+f+" ");
				sbSql.append("\n and not (id_tipo_operacion between 4000 and 4199 and b_salvo_buen_cobro='N')) ");
				sbSql.append("\n or (id_estatus_mov='A' ");
				sbSql.append("\n and id_tipo_operacion between 4000 and 4199 ");
				sbSql.append("\n	and b_salvo_buen_cobro='S' ");
				sbSql.append("\n	and fec_valor = "+f+" ))) ");
				sbSql.append("\n and no_empresa ="+empresa+" ");
				sbSql.append("\n and id_banco="+banco+" ");
				sbSql.append("\n AND id_estatus_mov <> 'X' ");
				sbSql.append("\n and id_chequera='"+chequera+"' ");
				sbSql.append("\n and id_tipo_operacion not in(1013,1014) ");
				sbSql.append("\n union all ");
				sbSql.append("\n SELECT fec_valor as fec_alta, id_tipo_operacion, ");
				sbSql.append("\n importe, concepto ");
				sbSql.append("\n FROM hist_movimiento ");
				sbSql.append("\n WHERE id_tipo_movto = 'I' ");
				sbSql.append("\n and (((id_estatus_mov = 'A' and fec_valor = "+f+") ");
				sbSql.append("\n and not (id_tipo_operacion between 3100 and 3199 and b_salvo_buen_cobro='N')) ");
				sbSql.append("\n or (id_estatus_mov='A' ");
				sbSql.append("\n    and id_tipo_operacion between 3100 and 3199 "); 
				sbSql.append("\n and b_salvo_buen_cobro='S' ");
				sbSql.append("\n   and fec_valor = "+f+")) ");
				sbSql.append("\n and no_empresa ="+empresa+" ");
				sbSql.append("\n	and id_banco="+banco+" ");
				sbSql.append("\n	 AND id_estatus_mov <> 'X' ");
				sbSql.append("\n	and id_chequera='"+chequera+"' ");
				sbSql.append("\n and id_tipo_operacion not in(1013,1014) ");
				 sbSql.append("\n	and id_tipo_operacion not between 4000 and 4199 ");	
			}
				


							
		System.out.println("query"+sbSql.toString());
			listDisp = jdbcTemplate.query(sbSql.toString(), new RowMapper()	{
				@Override
				public PosicionInversionDto mapRow(ResultSet rs, int idx)throws SQLException{
					PosicionInversionDto reporte = new PosicionInversionDto();

					reporte.setHora(rs.getString("hora"));
					reporte.setTipoOperacion(rs.getInt("tipoOperacion"));
					reporte.setImporte(rs.getDouble("importe"));
					reporte.setConcepto(rs.getString("concepto"));
					
				    return reporte;
					
					
				}
			});
			
			
		}
		catch(Exception e)
		{
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P: FinanciamientoModificacionC, C: FinanciamientoModificacionCDaoImpl, consultarGrid");
			}
	
		return listDisp;
	}




	@Override
	public List<PosicionInversionDto> consultarPorInvertir(String fecha, int caja, String institucion, int empresa,
			String divisa, int banco, String chequera) {
		StringBuffer sbSql = new StringBuffer();
		List<PosicionInversionDto> listDisp= new ArrayList<PosicionInversionDto>();
		String f=funciones.ponerFechaSola(fecha);
		
		try
		{
					
			
			if(caja==1){
				
				sbSql.append("\n     	SELECT DISTINCT '' as estatus, ");
				sbSql.append("\n      coalesce(b.saldo_final, 0) as importe ");
				sbSql.append("\n      from ");
				sbSql.append("\n      cat_cta_banco b ");
				sbSql.append("\n      where ");
				sbSql.append("\n   b.no_empresa = "+empresa+" ");
				sbSql.append("\n      and b.id_divisa = '"+divisa+"' ");
				sbSql.append("\n      and b.id_banco = "+banco+" ");
				sbSql.append("\n      and b.id_chequera = '"+chequera+"' ");

				
			
			
					
			
			}else{
				sbSql.append("\n     	SELECT DISTINCT '' as estatus, ");
				sbSql.append("\n      coalesce(b.saldo_final, 0) as importe ");
				sbSql.append("\n      from ");
				sbSql.append("\n      cat_cta_banco b ");
				sbSql.append("\n      where ");
				sbSql.append("\n   b.no_empresa = "+empresa+" ");
				sbSql.append("\n      and b.id_divisa = '"+divisa+"' ");
				sbSql.append("\n      and b.id_banco = "+banco+" ");
				sbSql.append("\n      and b.id_chequera = '"+chequera+"' ");

			}
				


							
		System.out.println("query"+sbSql.toString());
			listDisp = jdbcTemplate.query(sbSql.toString(), new RowMapper()	{
				@Override
				public PosicionInversionDto mapRow(ResultSet rs, int idx)throws SQLException{
					PosicionInversionDto reporte = new PosicionInversionDto();

					
					reporte.setEstatus(rs.getString("estatus"));
					reporte.setImporte(rs.getDouble("importe"));
				    return reporte;
					
					
				}
			});
			
			
		}
		catch(Exception e)
		{
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P: FinanciamientoModificacionC, C: FinanciamientoModificacionCDaoImpl, consultarGrid");
			}
	
		return listDisp;
	}




	@Override
	public List<PosicionInversionDto> consultarInversiones(String fecha, int caja, String institucion, int empresa,
			String divisa, int banco, String chequera) {
		StringBuffer sbSql = new StringBuffer();
		List<PosicionInversionDto> listDisp= new ArrayList<PosicionInversionDto>();
	
		
		try
		{
					
			
			if(caja==1){
			
				  
				sbSql.append("\n SELECT  m.id_estatus_mov as estatus, concepto as concepto , ");
				sbSql.append("\n  case when m.id_estatus_mov in ('X') then m.importe *-1      else m.importe end as importe ");
				sbSql.append("\n    FROM movimiento m ");
				sbSql.append("\n     WHERE no_folio_det in ( ");
				sbSql.append("\n	select no_folio_det from movimiento m1 where ");
				sbSql.append("\n	m1.id_tipo_movto = 'E' and m1.id_estatus_mov =  'A' and m1.id_tipo_operacion = 4001 ");
				sbSql.append("\n    and m1.id_chequera='"+chequera+"' and m1.id_banco= "+banco+" ) ");
				sbSql.append("\n  union all ");
				sbSql.append("\n  SELECT m.id_estatus_mov as estatus, concepto as concepto,  ");
				sbSql.append("\n   case when m.id_estatus_mov in ('X') then m.importe *-1      else m.importe end as importe ");
				sbSql.append("\n      FROM hist_movimiento m ");
				sbSql.append("\n WHERE no_folio_det in ( ");
				sbSql.append("\n select no_folio_det from hist_movimiento m1 where ");
				sbSql.append("\n  m1.id_tipo_movto = 'E' and m1.id_estatus_mov =  'A' and m1.id_tipo_operacion = 4001 ");
				sbSql.append("\n and m1.id_chequera='"+chequera+"' and m1.id_banco= "+banco+" ) ");

				
				
				   
			
			
			}else{
	
			
				sbSql.append("\n SELECT  m.id_estatus_mov as estatus, concepto as concepto , ");
				sbSql.append("\n  case when m.id_estatus_mov in ('X') then m.importe *-1      else m.importe end as importe ");
				sbSql.append("\n    FROM movimiento m ");
				sbSql.append("\n     WHERE no_folio_det in ( ");
				sbSql.append("\n	select no_folio_det from movimiento m1 where ");
				sbSql.append("\n	m1.id_tipo_movto = 'E' and m1.id_estatus_mov =  'A' and m1.id_tipo_operacion = 4001 ");
				sbSql.append("\n    and m1.id_chequera='"+chequera+"' and m1.id_banco= "+banco+" ) ");
				sbSql.append("\n  union all ");
				sbSql.append("\n  SELECT m.id_estatus_mov as estatus, concepto as concepto,  ");
				sbSql.append("\n   case when m.id_estatus_mov in ('X') then m.importe *-1      else m.importe end as importe ");
				sbSql.append("\n      FROM hist_movimiento m ");
				sbSql.append("\n WHERE no_folio_det in ( ");
				sbSql.append("\n select no_folio_det from hist_movimiento m1 where ");
				sbSql.append("\n  m1.id_tipo_movto = 'E' and m1.id_estatus_mov =  'A' and m1.id_tipo_operacion = 4001 ");
				sbSql.append("\n and m1.id_chequera='"+chequera+"' and m1.id_banco= "+banco+" ) ");

				
				
			}
				


							
		System.out.println("query"+sbSql.toString());
			listDisp = jdbcTemplate.query(sbSql.toString(), new RowMapper()	{
				@Override
				public PosicionInversionDto mapRow(ResultSet rs, int idx)throws SQLException{
					PosicionInversionDto reporte = new PosicionInversionDto();

					
					reporte.setEstatus(rs.getString("estatus"));
					reporte.setConcepto(rs.getString("concepto"));
					reporte.setImporte(rs.getDouble("importe"));
				    return reporte;
					
					
				}
			});
			
			
		}
		catch(Exception e)
		{
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P: FinanciamientoModificacionC, C: FinanciamientoModificacionCDaoImpl, consultarGrid");
			}
	
		return listDisp;
	}




	@Override
	public int saldoInicial(int banco, String chequera) {
	
		StringBuffer sbSql = new StringBuffer();
		List<LlenaComboGralDto> listListas = new ArrayList<LlenaComboGralDto>();
int sald=0;
		try{
			
			sbSql.append("\n  SELECT saldo_inicial as saldoI ");
			sbSql.append("\n     FROM cat_cta_banco  ");
			sbSql.append("\n     where id_banco = "+banco+" ");
			sbSql.append("\n     and id_chequera='"+chequera+"' "); 
			     

			
				  
			System.out.println("queryBanco..::"+sbSql.toString());
			listListas = jdbcTemplate.query(sbSql.toString(), new RowMapper()
			{
				@Override
				public LlenaComboGralDto mapRow(ResultSet rs, int idx)throws SQLException
				{
					LlenaComboGralDto dtoCons = new LlenaComboGralDto();
						dtoCons.setValor(rs.getFloat("saldoI"));
						
					return dtoCons;
				}
			});
		}
		catch(Exception e)
		{
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P: FinanciamientoModificacionC, C: FinanciamientoModificacionCDaoImpl, consultarLineas");
		}

		
		for (LlenaComboGralDto ll : listListas) {
			sald=(int) ll.getValor();
		}
		
		return sald;
		
		
		
	}


}
