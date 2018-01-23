package com.webset.set.bancaelectronica.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import com.webset.set.bancaelectronica.dao.CapturaEstadoCuentaDao;
import com.webset.set.bancaelectronica.dto.CapturaEstadoCuentaDto;
import com.webset.set.personas.dto.ConsultaPersonasDto;
import com.webset.set.utilerias.Bitacora;
import com.webset.set.utilerias.ConsultasGenerales;
import com.webset.set.utilerias.Funciones;
//import com.webset.set.utilerias.business.impl.CargasInicialesBusinessImpl;
import com.webset.set.utilerias.dto.LlenaComboEmpresasDto;

public class CapturaEstadoCuentaDaoImpl implements CapturaEstadoCuentaDao{
	private static Logger logger = Logger.getLogger(CapturaEstadoCuentaDaoImpl.class);

	private JdbcTemplate jdbcTemplate;
	Bitacora bitacora = new Bitacora();
	Funciones funciones = new Funciones();
	ConsultasGenerales consultasGenerales;
	
	public List<LlenaComboEmpresasDto> llenaComboEmpresas(int numUsuario) {
		consultasGenerales = new ConsultasGenerales (jdbcTemplate);
		return consultasGenerales.llenarComboEmpresas(numUsuario);
}
	
	@SuppressWarnings("unchecked")
	public List<CapturaEstadoCuentaDto> llenaComboTipoBanco(int numEmpresa){
		
		List<CapturaEstadoCuentaDto> listaResultado = new ArrayList<CapturaEstadoCuentaDto>();
		StringBuffer sql = new StringBuffer();
		try{
			sql.append(" SELECT c.id_banco as ID, c.desc_banco as Descrip \n");
			sql.append(" FROM cat_banco c,cat_cta_banco cb, empresa e \n");
			sql.append(" WHERE c.id_banco = cb.id_banco \n");
			sql.append(" and cb.tipo_chequera in ('P','C','O','M','I') \n");
			sql.append(" and e.no_empresa = cb.no_empresa \n");
			sql.append(" and cb.no_empresa = " +numEmpresa+" \n");
			sql.append(" GROUP BY c.id_banco, c.desc_banco");
			System.out.println(sql+"Selecciona bancos");
			listaResultado = jdbcTemplate.query(sql.toString(), new RowMapper(){
				public CapturaEstadoCuentaDto mapRow(ResultSet rs, int idx)throws SQLException{
					CapturaEstadoCuentaDto campos = new CapturaEstadoCuentaDto();
					campos.setIdBanco(Integer.parseInt(rs.getString("ID")));
					campos.setDescBanco(rs.getString("Descrip"));
					return campos;
				}
			});
		}
		catch(Exception e){
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Personas, C: ConsultaPersonasDaoImpl, M: llenaComboTipoPersonas");
		}return listaResultado;
	}
	
	@SuppressWarnings("unchecked")
	public List<CapturaEstadoCuentaDto> llenaComboChequera(int numEmpresa,int numBanco){
		
		List<CapturaEstadoCuentaDto> listaResultado = new ArrayList<CapturaEstadoCuentaDto>();
		StringBuffer sql = new StringBuffer();
		try{
			
			sql.append(" SELECT id_chequera as ID, id_chequera as Descrip \n");
			sql.append(" FROM cat_cta_banco, empresa \n");
			sql.append( " WHERE  id_banco = " +numBanco+ " \n");
			sql.append(" and tipo_chequera in ('P','C','O','M', 'I') and \n");
			sql.append(" cat_cta_banco.no_empresa = empresa.no_empresa \n");
			sql.append(" And cat_cta_banco.no_empresa = " +numEmpresa);
			System.out.println(sql+"Selecciona chequeras");
			listaResultado = jdbcTemplate.query(sql.toString(), new RowMapper(){
				public CapturaEstadoCuentaDto mapRow(ResultSet rs, int idx)throws SQLException{
					CapturaEstadoCuentaDto campos = new CapturaEstadoCuentaDto();
					campos.setIdChequera(rs.getString("ID"));
					campos.setDescChequera(rs.getString("Descrip"));
					return campos;
				}
			});
		}
		catch(Exception e){
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Personas, C: ConsultaPersonasDaoImpl, M: llenaComboTipoPersonas");
		}return listaResultado;
	}
	
	@SuppressWarnings("unchecked")
	public List<CapturaEstadoCuentaDto> llenaComboConcepto(int numBanco){
		
		List<CapturaEstadoCuentaDto> resultado = new ArrayList<CapturaEstadoCuentaDto>();
		List<CapturaEstadoCuentaDto> listaResultado = new ArrayList<CapturaEstadoCuentaDto>();
		StringBuffer sql = new StringBuffer();
		try{
			
			sql.append(" SELECT b_banca_elect FROM cat_banco WHERE id_banco = " + numBanco);
			resultado = jdbcTemplate.query(sql.toString(), new RowMapper(){
				public CapturaEstadoCuentaDto mapRow(ResultSet rs, int idx)throws SQLException{
					CapturaEstadoCuentaDto campos = new CapturaEstadoCuentaDto();
					campos.setBanca_elect(rs.getString("b_banca_elect"));
					return campos;
				}
			});
			if(!resultado.isEmpty()){
				//System.out.println(resultado.get(0).banca_elect);
				
					sql=new StringBuffer();
					try {
						sql.append(" SELECT distinct e.desc_concepto,e.id_cve_concepto \n");
						sql.append(" FROM regla_concepto r, equivale_concepto e \n");
						sql.append(" WHERE e.cargo_abono = r.ingreso_egreso \n");
						sql.append(" and r.concepto_set = e.concepto_set \n");
						sql.append(" and e.b_salvo_buen_cobro = r.b_salvo_buen_cobro \n");

							    
						if((resultado.get(0).banca_elect.equals("A")) || (resultado.get(0).banca_elect.equals("I"))){
							sql.append(" and e.id_banco = "+numBanco+"\n");
						}else{
							sql.append(" and e.id_banco = 0");
						}
						System.out.println(sql+"conceptos");
						listaResultado = jdbcTemplate.query(sql.toString(), new RowMapper(){
							public CapturaEstadoCuentaDto mapRow(ResultSet rs, int idx)throws SQLException{
								CapturaEstadoCuentaDto campos2 = new CapturaEstadoCuentaDto();
								campos2.setConceptoSet(rs.getString("desc_concepto"));
								campos2.setId_cve_concepto(rs.getString("id_cve_concepto"));
								System.out.println("cve" +rs.getString("id_cve_concepto"));
								return campos2;
							}
						});
					} catch (Exception e) {
						// TODO: handle exception
					}		
			}
		}
		catch(Exception e){
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Personas, C: ConsultaPersonasDaoImpl, M: llenaComboTipoPersonas");
		}return listaResultado;
	}
	
	@SuppressWarnings("unchecked")
	public String obtenerCargo(String concepto,int numBanco){
		String cargo="";
		List<CapturaEstadoCuentaDto> listaResultado = new ArrayList<CapturaEstadoCuentaDto>();
		StringBuffer sql = new StringBuffer();
		try{
			sql.append(" SELECT distinct cargo_abono FROM \n");
			sql.append(" equivale_concepto e WHERE \n");
			sql.append(" e.id_banco = "+numBanco+ "\n");
			sql.append(" and e.desc_concepto = '"+concepto+"'");
			System.out.println(sql+" Selecciona cargo");
			listaResultado = jdbcTemplate.query(sql.toString(), new RowMapper(){
				public CapturaEstadoCuentaDto mapRow(ResultSet rs, int idx)throws SQLException{
					CapturaEstadoCuentaDto campos = new CapturaEstadoCuentaDto();
					campos.setCargoAbono(rs.getString("cargo_abono"));
					return campos;
				}
			});
			System.out.println(listaResultado.get(0).getCargoAbono());
			if(!listaResultado.isEmpty()){
				cargo=listaResultado.get(0).getCargoAbono();
			}
		}
		catch(Exception e){
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Personas, C: ConsultaPersonasDaoImpl, M: llenaComboTipoPersonas");
		}return cargo;
	}
	
	@SuppressWarnings("unchecked")
	public int guardarNuevoEstado(CapturaEstadoCuentaDto obj,String fecha,String fechaHoy){
		int resultado=0;
		int listaResultado=0;
		StringBuffer sql= new StringBuffer();
		StringBuffer sql2= new StringBuffer();
		float rubro=0;
		
		try{
			//System.out.println(obj.getSecuencia()+" entro a daoImpl");
			sql.append("INSERT INTO movto_banca_e (");
			sql.append("secuencia,no_empresa,id_banco,id_chequera,fec_valor,");
			sql.append("sucursal,folio_banco,referencia,cargo_abono,importe,");
			sql.append("b_salvo_buen_cobro,concepto,id_estatus_trasp,fec_alta,");
			sql.append("b_trasp_banco,b_trasp_conta,observacion,descripcion,id_cve_concepto,id_rubro)");
			sql.append("values ("+ obj.getSecuencia()+"," + obj.getIdEmpresa()+ "," + obj.getIdBanco() +",'");
			sql.append(obj.getIdChequera() + "'"+ ",CONVERT(DATE,'" + fecha+ "',103)");
			sql.append(",'" + obj.getSucursal() + "' "+ ",'" + obj.getFolioBanco() + "' ");
			sql.append(",'" + obj.getReferencia() + "'"+ ",'" + obj.getCargoAbono() + "'");
			sql.append(", " + obj.getImporte() + ","+ "'" + obj.getSalvoBuenCobro() + "'");
			sql.append(",'" + obj.getConceptoSet() + "'"+ ",'N'"+ ",GETDATE()");
			sql.append(",'N'"+ ",'N'"+ ",'" + obj.getObservacion() + "'");
			sql.append(",'" + obj.getConceptoSet() + "'");
			sql.append(",'" + obj.getId_cve_concepto() + "'");
			if(obj.getIdRubro()==0){
//				rubro=null;
				sql.append(","+null+ " )");
			}else{
				rubro=obj.getIdRubro();
				sql.append("," +rubro+ " )");
			}
			System.out.println(sql+" inserto en movto_banca_e");
			listaResultado = jdbcTemplate.update(sql.toString());
			
        //FunSQLInsert26 = gobjBD.ejecutaSentencia(sSQL)
        
        //Actualiza el saldo_banco_ini si cargo_abono es S
        		if(obj.getCargoAbono().equals('S')){
		            if(fecha.equals(fechaHoy)){
		            	sql2.append("UPDATE cat_cta_banco");
		            }else{
		            	sql2.append("UPDATE hist_cat_cta_banco");
		            }
		            sql2.append(" SET saldo_banco_ini = " + obj.getImporte());
		            sql2.append(" WHERE no_empresa = " + obj.getIdEmpresa());
		            sql2.append(" AND id_banco = " + obj.getIdBanco());
		            sql2.append(" AND id_chequera = '" + obj.getIdChequera() + "'");   
		            		if(!fecha.equals(fechaHoy)){
		            			sql2.append(" AND fec_valor = '" + fecha + "'");
		            		}
		            //FunSQLInsert26 = gobjBD.ejecutaSentencia(sSQL)
            		resultado = jdbcTemplate.update(sql2.toString());
            		System.out.println(sql2);
        		} 
		}
		catch(Exception e){
			e.printStackTrace();
			logger.error(e);
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Personas, C: ConsultaPersonasDaoImpl, M: llenaComboTipoPersonas");
		}return listaResultado;
	}
	@SuppressWarnings("unchecked")
	public int actualizarEstadoCuenta(CapturaEstadoCuentaDto obj){
		int resultado=0;
		int listaResultado=0;
		StringBuffer sql= new StringBuffer();
		StringBuffer sql2= new StringBuffer();
		float rubro=0;
		
		try{
			//System.out.println(obj.getSecuencia()+" entro a daoImpl");
			sql.append(" UPDATE movto_banca_e SET sucursal= '" + obj.getSucursal() + "',");
			sql.append(" folio_banco = '" + obj.getFolioBanco() + "', referencia = '" + obj.getReferencia()+ "', ");
			sql.append(" cargo_abono = '" + obj.getCargoAbono() + "' , importe = " + obj.getImporte() + ",");
			sql.append(" b_salvo_buen_cobro = '" + obj.getSalvoBuenCobro() + "', concepto = '" + obj.getConceptoSet()+ "', ");
			sql.append( " observacion = '" + obj.getObservacion() + "' ");
			sql.append(" WHERE no_empresa =" + obj.getIdEmpresa()+" and  id_banco = " + obj.getIdBanco());
			sql.append(" and id_chequera ='" + obj.getIdChequera() + "' and secuencia = " + obj.getSecuencia());
			System.out.println(sql+" update en movto_banca_e");
			listaResultado = jdbcTemplate.update(sql.toString());
      
		}
		catch(Exception e){
			e.printStackTrace();
			logger.error(e);
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Personas, C: ConsultaPersonasDaoImpl, M: llenaComboTipoPersonas");
		}return listaResultado;
	}
	@SuppressWarnings("unchecked")
	public String obtenerSalvoBuenCobro(int numBanco,String concepto){
		String cargo="";
		List<CapturaEstadoCuentaDto> listaResultado = new ArrayList<CapturaEstadoCuentaDto>();
		StringBuffer sql = new StringBuffer();
		try{
			sql.append("SELECT distinct b_salvo_buen_cobro FROM \n");
			sql.append(" equivale_concepto e WHERE \n");
			sql.append(" e.id_banco = "+numBanco+ "\n");
			sql.append(" and e.desc_concepto = '"+concepto+"'");
			System.out.println(sql+" Selecciona salvobc");
			listaResultado = jdbcTemplate.query(sql.toString(), new RowMapper(){
				public CapturaEstadoCuentaDto mapRow(ResultSet rs, int idx)throws SQLException{
					CapturaEstadoCuentaDto campos = new CapturaEstadoCuentaDto();
					campos.setSalvoBuenCobro(rs.getString("b_salvo_buen_cobro"));
					return campos;
				}
			});
			System.out.println(listaResultado.get(0).getSalvoBuenCobro());
			if(!listaResultado.isEmpty()){
				cargo=listaResultado.get(0).getSalvoBuenCobro();
			}
		}
		catch(Exception e){
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Personas, C: ConsultaPersonasDaoImpl, M: llenaComboTipoPersonas");
		}return cargo;
	}
	
	@SuppressWarnings("unchecked")
	public String obtenerSucursal(int numBanco,String chequera){
		String cargo="";
		List<CapturaEstadoCuentaDto> listaResultado = new ArrayList<CapturaEstadoCuentaDto>();
		StringBuffer sql = new StringBuffer();
		try{
			sql.append(" SELECT distinct desc_sucursal FROM \n");
			sql.append(" cat_cta_banco WHERE \n");
			sql.append(" id_banco = "+numBanco+ "\n");
			sql.append(" and id_chequera = '"+chequera+"'");
			System.out.println(sql+" Selecciona sucursal");
			listaResultado = jdbcTemplate.query(sql.toString(), new RowMapper(){
				public CapturaEstadoCuentaDto mapRow(ResultSet rs, int idx)throws SQLException{
					CapturaEstadoCuentaDto campos = new CapturaEstadoCuentaDto();
					campos.setSucursal(rs.getString("desc_sucursal"));
					return campos;
				}
			});
			System.out.println(listaResultado.get(0).getSucursal());
			if(!listaResultado.isEmpty()){
				cargo=listaResultado.get(0).getSucursal();
			}
		}
		catch(Exception e){
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Personas, C: ConsultaPersonasDaoImpl, M: llenaComboTipoPersonas");
		}return cargo;
	}
	@SuppressWarnings("unchecked")
	public List<CapturaEstadoCuentaDto> llenaGrid(int numEmpresa,boolean banco,int numBanco,boolean chequera,String numChequera,boolean fecha,String fechaV){
		System.out.println("entro a sql");
		List<CapturaEstadoCuentaDto> listaResultado = new ArrayList<CapturaEstadoCuentaDto>();
		StringBuffer sql = new StringBuffer();
		//System.out.println(fecha+"nueva fecha");
		try{
			
			sql.append(" SELECT distinct  b.secuencia,b.fec_valor,b.sucursal,b.folio_banco, \n");
			sql.append("b.referencia,b.cargo_abono,b.observacion,b.importe,b.b_salvo_buen_cobro, \n");
			sql.append("e.concepto_set as concepto,b.id_estatus_trasp,b.b_trasp_banco,b.b_trasp_conta, \n");
			sql.append("b.id_rubro FROM movto_banca_e b,equivale_concepto e \n");
			sql.append(" WHERE b.no_empresa = " + numEmpresa+" and b.id_estatus_trasp = 'N' \n");
			sql.append(" and b.id_banco = e.id_banco and b.concepto = e.concepto_set \n");
			sql.append(" and b.cargo_abono = e.cargo_abono \n");
			    if(banco==true){
			    	sql.append(" AND b.id_banco = " + numBanco+" \n");
			    }
			    if(chequera==true){
			    	 sql.append(" AND b.id_chequera = '" + numChequera + "' \n");
			    }
			    if(fecha==true){
		            sql.append(" AND b.fec_valor BETWEEN convert(datetime,'" + fechaV + " 00:00" + "',103) AND convert(datetime,'" + fechaV + " 23:59"+"',103) \n");
			    }

			       sql.append(" UNION SELECT b.secuencia,b.fec_valor,b.sucursal,b.folio_banco, \n");
			       sql.append("b.referencia,b.cargo_abono,b.observacion,b.importe,b.b_salvo_buen_cobro, \n");
			       sql.append("e.concepto_set as concepto,b.id_estatus_trasp,b.b_trasp_banco, \n");
			       sql.append("b.b_trasp_conta,b.id_rubro FROM movto_banca_e b,equivale_concepto e \n");
			       sql.append(" WHERE b.no_empresa = " + numEmpresa +" and b.id_estatus_trasp = 'N' \n");
			       sql.append(" and e.id_banco = 0 and b.concepto = e.concepto_set \n");
			       sql.append(" and b.cargo_abono = e.cargo_abono \n");
			    
			       if(banco==true){
			    	   sql.append(" AND b.id_banco = " + numBanco+" \n");
			       }
			            
			       if(chequera==true){
			    	   sql.append(" AND b.id_chequera = '" + numChequera + "' \n");
			       }
			       if(fecha==true){
			            sql.append(" AND b.fec_valor BETWEEN convert(datetime,'" + fechaV + " 00:00" + "',103) AND convert(datetime,'" + fechaV + " 23:59"+"',103) \n");
			       }

			        sql.append(" ORDER BY secuencia ");
			    
			System.out.println(sql+"Selecciona registros");
			listaResultado = jdbcTemplate.query(sql.toString(), new RowMapper(){
				public CapturaEstadoCuentaDto mapRow(ResultSet rs, int idx)throws SQLException{
					CapturaEstadoCuentaDto campos = new CapturaEstadoCuentaDto();
					campos.setCargoAbono(rs.getString("cargo_abono"));
					campos.setFolioBanco(rs.getString("folio_banco"));
					campos.setImporte(Double.parseDouble(rs.getString("importe")));
					campos.setReferencia(rs.getString("referencia"));
					campos.setConceptoSet(rs.getString("concepto"));
					campos.setSucursal(rs.getString("sucursal"));
					campos.setFecValor(rs.getString("fec_valor"));
					campos.setObservacion(rs.getString("observacion"));
					campos.setSalvoBuenCobro(rs.getString("b_salvo_buen_cobro"));
					campos.setIdEstatusTrasp(rs.getString("id_estatus_trasp"));
					campos.setSecuencia(Integer.parseInt(rs.getString("secuencia")));
					System.out.println(rs.getString("id_estatus_trasp"));
					if(rs.getString("id_rubro")==null){
						campos.setIdRubro(0);
						
					}else{
						campos.setIdRubro(Float.parseFloat(rs.getString("id_rubro")));
					}
					
					return campos;
				}
			});
		}
		catch(Exception e){
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Personas, C: ConsultaPersonasDaoImpl, M: llenaComboTipoPersonas");
		}return listaResultado;
	}

	@SuppressWarnings("unchecked")
	public int eliminarEstadoCuenta(int numEmpresa,int secuencia){
		int resultado=0;
		StringBuffer sql= new StringBuffer();
		
		float rubro=0;
		
		try{
			 sql.append(" DELETE FROM movto_banca_e \n");
			 sql.append(" WHERE no_empresa =" + numEmpresa + " and secuencia = " +secuencia);
			resultado = jdbcTemplate.update(sql.toString());
      
		}
		catch(Exception e){
			e.printStackTrace();
			logger.error(e);
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Personas, C: ConsultaPersonasDaoImpl, M: llenaComboTipoPersonas");
		}return resultado;
	}
	public JdbcTemplate getJdbcTemplate(){
		return jdbcTemplate;
	}
	public void setDataSource(DataSource dataSource){
		try{
			this.jdbcTemplate=new JdbcTemplate(dataSource);
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	public static Logger getLogger() {
		return logger;
	}


	public static void setLogger(Logger logger) {
		CapturaEstadoCuentaDaoImpl.logger = logger;
	}
	

}
