package com.webset.set.bancaelectronica.dao.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.eclipse.jdt.internal.compiler.batch.Main.Logger;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import com.webset.set.bancaelectronica.dao.ControlArchivosTransferidosDao;
import com.webset.set.bancaelectronica.dto.ArchTransferDto;
import com.webset.set.bancaelectronica.dto.MovimientoDto;
import com.webset.set.bancaelectronica.dto.MovtoBancaEDto;
import com.webset.set.caja.business.CajaBusinessImpl;
import com.webset.set.utilerias.Bitacora;
import com.webset.set.utilerias.ConsultasGenerales;
import com.webset.set.utilerias.Funciones;
import com.webset.set.utileriasmod.dto.ConfiguracionSolicitudPagoDto;
import com.webset.utils.tools.Utilerias;

public class ControlArchivosTransferidosDaoImpl implements ControlArchivosTransferidosDao{

	JdbcTemplate jdbcTemplate;
	Bitacora bitacora;
	ConsultasGenerales consultasGenerales;
	Funciones funciones = new Funciones();
//	 private static Logger logger = Logger.getLogger(ControlArchivosTransferidosDaoImpl.class);
	public String configuraSet(int indice){
		try {
			consultasGenerales = new ConsultasGenerales(jdbcTemplate);
		} catch (Exception e) {
			System.out.println("Error en: configuraSet");
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: BancaElectronica, C: configuraSet, M: llenaComboArchivos");
		}return consultasGenerales.consultarConfiguraSet(indice);
		
	}
	
	public List<ArchTransferDto> llenaComboArchivos(String fecValor, String bChequeOcurre){
		List<ArchTransferDto> listaResultado = new ArrayList<ArchTransferDto>();
		StringBuffer sql = new StringBuffer();	
		try{
			sql.append("Select nom_arch from arch_transfer ");
			sql.append("\n where fec_trans = convert(datetime,'"+ Utilerias.validarCadenaSQL(fecValor) +"',103) ");
			if (!bChequeOcurre.equals(""))
				sql.append("\n and b_cheque_ocurre='S'");
			else
				sql.append("\n and b_cheque_ocurre is null");
			System.out.println(sql.toString());
			listaResultado = jdbcTemplate.query(sql.toString(), new RowMapper<ArchTransferDto>(){
				public ArchTransferDto mapRow(ResultSet rs, int idx)throws SQLException{
					ArchTransferDto campos = new ArchTransferDto();
					campos.setNomArch(rs.getString("nom_arch"));
					return campos;
				}
			});			
		}catch(Exception e){
			System.out.println("Error en: llenaComboArchivos");
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: BancaElectronica, C: ControlArchivosTransferidosDaoImpl, M: llenaComboArchivos");
		}return listaResultado;
	}
	
	
	public List<ArchTransferDto> llenaGridArchivos(ArchTransferDto obj){
		List<ArchTransferDto> listaResultado = new ArrayList<ArchTransferDto>();
		StringBuffer sql = new StringBuffer();
		StringBuffer sql1 = new StringBuffer();
		//String tablas[]={"movimiento","hist_movimiento"};
		String tablas[]={"movimiento"};
		try{
			for (int i = 0; i < tablas.length; i++) { 			
			sql.append("Select a.id_banco, c.desc_banco, a.nom_arch, convert(char(10),a.fec_trans,103) as fec_trans,convert(char(10),a.fec_retrans,103) as fec_retrans, ");
			sql.append("\n a.importe, a.registros, u.nombre + ' ' + u.apellido_paterno + ' ' + u.apellido_materno as usuario ,");
			sql.append("\n a.no_usuario_alta, coalesce(um.nombre + ' ' + um.apellido_paterno + ' ' + um.apellido_materno, '') as usuario_modif, ");
			sql.append("\n coalesce(a.no_usuario_modif, 0) as no_usuario_modif, ");
			sql.append("\n (select coalesce(case when id_tipo_operacion = 3200 then 200 else 3006 end, 0) ");
			
			sql.append("\n from " + tablas[i] +" where no_folio_det in (  ");
			//Sustituido el 30.01.16 para optimizar codigo
		  //sql.append("\n select no_folio_det from det_arch_transfer dat where dat.nom_arch = a.nom_arch and rownum =1)) as ruta ");*/
			sql.append("\n select  top 1 no_folio_det from det_arch_transfer dat join arch_transfer at on dat.nom_arch = at.nom_arch ) )as ruta");
			sql.append("\n from arch_transfer a left join seg_usuario um on (a.no_usuario_modif = um.id_usuario), ");
			sql.append("\n 		cat_banco c, seg_usuario u ");
			sql.append("\n where ");
			switch (obj.getTipoCriterio()){
			case 0: //Busca por fechas
				if (!obj.getFecInicial().equals("") && !obj.getFecFinal().equals("")){
					sql.append("\n a.fec_trans between convert(datetime,'"+ Utilerias.validarCadenaSQL(obj.getFecInicial()) +"',103) and convert(datetime,'"+ Utilerias.validarCadenaSQL(obj.getFecFinal()) +"',103) ");					
				}
				else 
					sql.append("\n a.fec_trans = convert(datetime,'"+ Utilerias.validarCadenaSQL(obj.getFecInicial()) +"',103) or a.fec_retrans = convert(datetime,'"+ Utilerias.validarCadenaSQL(obj.getFecInicial()) +"',103) ");
				
				break;
			case 1: //Busca por archivo
				if (!obj.getNomArch().equals(""))
					sql.append("\n a.nom_arch = '"+ Utilerias.validarCadenaSQL(obj.getNomArch()) +"' ");
				break;
			case 2: //Busca por numero de documento
				if (!obj.getNoDocto().equals(""))
				{
					sql.delete(0, sql.length());
					for (int d = 0; d < tablas.length; d++) { 
						sql1.append("Select a.id_banco,c.desc_banco,a.nom_arch,convert(char(10),a.fec_trans,103) as fec_trans, convert(char(10),a.fec_retrans,103) as fec_retrans,a.importe,");
						sql1.append("\n a.registros,u.nombre + ' ' + u.apellido_paterno + ' ' + u.apellido_materno as usuario, ");
						sql1.append("\n a.no_usuario_alta, coalesce(um.nombre + ' ' + um.apellido_paterno + ' ' + um.apellido_materno, '') as usuario_modif, ");
						sql1.append("\n coalesce(a.no_usuario_modif, 0) as no_usuario_modif, ");
						sql1.append("\n 	(select top 1 coalesce(case when id_tipo_operacion = 3200 then 200 else 3006 end, 0) ");
						sql1.append("\n 	 from "+tablas[d]+" where no_folio_det in ( ");
						//Sustituido el 30.01.16 para optimizar codigo
						//sql.append("\n 		select no_folio_det from det_arch_transfer dat where dat.nom_arch = a.nom_arch)) as ruta ");
						sql1.append("\n 		select top 1 no_folio_det from det_arch_transfer dat join arch_transfer at on dat.nom_arch = at.nom_arch ) )as ruta");
						
						
						sql1.append("\n From arch_transfer a left join seg_usuario um on (a.no_usuario_modif = um.id_usuario), cat_banco c, det_arch_transfer d, seg_usuario u ");
						sql1.append("\n Where a.id_banco = c.id_banco");
						sql1.append("\n And a.no_usuario_alta = u.id_usuario ");
						//sql.append("\n and (a.no_usuario_modif = um.id_usuario or a.no_usuario_modif is null) ");
						sql1.append("\n And a.nom_arch = d.nom_arch");
						sql1.append("\n And d.no_docto = '"+ Utilerias.validarCadenaSQL(obj.getNoDocto()) +"'");
						
						sql1.append("\n and a.id_banco = c.id_banco ");
						sql1.append("\n and a.no_usuario_alta = u.id_usuario ");
						//sql.append("\n and (a.no_usuario_modif = um.id_usuario or a.no_usuario_modif is null) ");
						//sql.append("\n and u.id_usuario = um.id_usuario ");
						
						if (!obj.getBChequeOcurre().equals(""))
							sql1.append("\n and a.b_cheque_ocurre = 'S'");
						else
							sql1.append("\n and (a.b_cheque_ocurre <> 'S' or a.b_cheque_ocurre is null)");sql1.append(" and a.nom_arch in( select dat.nom_arch from arch_transfer at join  det_arch_transfer dat on at.nom_arch=dat.nom_arch where dat.id_estatus_arch<>'X')");
						//if(tablas[d].equals("movimiento"))
						//sql1.append("\n union all \n");
					}
					i++;
				}
				break;
			}
			
			sql.append("\n and a.id_banco = c.id_banco ");
			sql.append("\n and a.no_usuario_alta = u.id_usuario ");
			//sql.append("\n and (a.no_usuario_modif = um.id_usuario or a.no_usuario_modif is null) ");
			//sql.append("\n and u.id_usuario = um.id_usuario ");
			
			if (!obj.getBChequeOcurre().equals(""))
				sql.append("\n and a.b_cheque_ocurre = 'S'");
			else{
				sql.append("\n and (a.b_cheque_ocurre <> 'S' or a.b_cheque_ocurre is null)");
				sql.append(" and a.nom_arch in( select dat.nom_arch from arch_transfer at join  det_arch_transfer dat on at.nom_arch=dat.nom_arch where dat.id_estatus_arch<>'X')");
			}
				
			//sql1.append(" and a.nom_arch in( select dat.nom_arch from arch_transfer at join  det_arch_transfer dat on at.nom_arch=dat.nom_arch where dat.id_estatus_arch<>'X')");
			// if(tablas[i].equals("movimiento"))
			//sql.append("\n union all \n");
			}
			
			if(!sql1.toString().equals("")){
				sql.delete(0, sql.length());
				sql.append(sql1.toString());
			}
			System.out.println("\n----------LLena grid---------");
			System.out.println("\n--------------------------------------");
			System.out.println("\n"+ sql.toString());
			System.out.println("\n--------------------------------------");
			System.out.println("\n-----------Fin llena grid-------------");
			listaResultado = jdbcTemplate.query(sql.toString(), new RowMapper<ArchTransferDto>(){
				public ArchTransferDto mapRow(ResultSet rs, int idx)throws SQLException{
					ArchTransferDto campos = new ArchTransferDto();
					campos.setDescBanco(rs.getString("desc_banco"));
					campos.setNomArch(rs.getString("nom_arch"));
					campos.setFecTransferencia(rs.getString("fec_trans"));
					campos.setFecRetransferencia(rs.getString("fec_retrans"));
					campos.setImporte(rs.getDouble("importe"));
					campos.setRegistros(rs.getInt("registros"));
					campos.setNoUsuarioAlta(rs.getInt("no_usuario_alta"));
					campos.setNoUsuarioModif(rs.getInt("no_usuario_modif"));
					campos.setNomUsrAlta(rs.getString("usuario"));
					campos.setNomUsrModif(rs.getString("usuario_modif"));
					campos.setIdBanco(rs.getInt("id_banco"));
					campos.setEspeciales("");
					campos.setColor("color:#000000");
					campos.setRuta(rs.getInt("ruta"));
					
					if (campos.getNomArch().substring(0, 4).equalsIgnoreCase("tran")) {
						/*
						campos.setEspeciales(obtenerEstatusArchivo(leerIdMensajeEstatus(campos.getNomArch(), rs.getInt("ruta"))));
						if (!leerIdMensajeEstatus(campos.getNomArch(), rs.getInt("ruta")).equals("00")) {
							campos.setColor("color:#E81624");
						}
						*/
					}
					
					return campos;
				}
			});
			System.out.println("");
			
		}
		catch(Exception e){
			System.out.println("Error en: llenaComboArchivos llenaGridArchivos");
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: BancaElectronica, C: ControlArchivosTransferidosDaoImpl, M: llenaGridArchivos");
		}return listaResultado;
	}
	
	public String leerIdMensajeEstatus(String nomArch, int indice) {
		consultasGenerales = new ConsultasGenerales(jdbcTemplate);
		File file = new File(consultasGenerales.consultarConfiguraSet(indice) + "\\santander\\" + nomArch + ".in");
		FileReader fileReader = null;
		BufferedReader br = null;
		String encabezado=null;
		try {
			fileReader = new FileReader(file);
			br = new BufferedReader(fileReader);
			encabezado = br.readLine();
			encabezado = String.valueOf(encabezado.charAt(33)) + String.valueOf(encabezado.charAt(34));
			System.out.println("||Ensabezado: " + encabezado + "||");
		} catch (FileNotFoundException e) {
			System.out.println(e.toString());
		} catch (IOException e) {
		}
		return encabezado;
	}
	
	public String leerDetalleArchivo(String nomArch, String folioDet, int indice) {
		consultasGenerales = new ConsultasGenerales(jdbcTemplate);
		File file = new File(consultasGenerales.consultarConfiguraSet(indice) + "\\santander\\" + nomArch + ".in");
		FileReader fileReader = null;
		try {
			fileReader = new FileReader(file);
			BufferedReader br = new BufferedReader(fileReader);
			br.readLine();
			String linea = null;
			while ((linea = br.readLine()) != null) {
				String folioDetL = linea.substring(228, 268).trim();
				if (folioDet.equals(folioDetL)) {
					return linea;
				}
			}
			return null;
		} catch (FileNotFoundException e) {
			return null;
		} catch (IOException e) {
			return null;
		}
	}

	public String obtenerEstatusArchivo(String idMensaje){
		StringBuffer sql = new StringBuffer();		
		try{
			sql.append("select descripcion from \n");
			sql.append("cat_concep_rechazo_arch \n");
			sql.append("where id_mensaje = '" + Utilerias.validarCadenaSQL(idMensaje) + "'");
			
			List<String> list = jdbcTemplate.query(sql.toString(),
					new RowMapper<String>(){
						public String mapRow(ResultSet rs, int idx)throws SQLException{
							return rs.getString("descripcion");
						}
 					});	
			System.out.println(list.get(0));
			if (!list.isEmpty()) 
				return list.get(0);
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: BancaElectronica, C: ControlArchivosTransferidosDaoImpl, M: llenaComboArchivos");
		}return null;
	}
	
	public String obtenerEstatusRegistro(String idMensaje){
		StringBuffer sql = new StringBuffer();		
		try{
			sql.append("select descripcion from \n");
			sql.append("cat_concep_rechazo_reg \n");
			sql.append("where id_mensaje = '" + Utilerias.validarCadenaSQL(idMensaje) + "'");
			
			List<String> list = jdbcTemplate.query(sql.toString(),
					new RowMapper<String>(){
						public String mapRow(ResultSet rs, int idx)throws SQLException{
							return rs.getString("descripcion");
						}
 					});	
			System.out.println(list.get(0));
			if (!list.isEmpty()) 
				return list.get(0);
		}
		catch(Exception e){
			System.out.println("error en el dao obtenerEstatusRegistro ");
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: BancaElectronica, C: ControlArchivosTransferidosDaoImpl, M: llenaComboArchivos");
		}return null;
	}
	
	public String obtieneTipoDeMovto(String nomArchivo){
		System.out.println("en obtieneTipoDeMovto de daoIMPL");
		String cadena = "";
		List<ArchTransferDto> lista = new ArrayList<ArchTransferDto>();
		StringBuffer sql = new StringBuffer();
		try{	
			sql.append("select MAX(case when id_tipo_operacion between 3700 and 3899 then 'TRASPASOS' ELSE 'TRANSFERENCIAS' END) AS tipo ");
			sql.append("\n FROM (select id_tipo_operacion from movimiento where no_folio_det in ( ");
			sql.append("\n select no_folio_det from det_arch_transfer where nom_arch = '"+ Utilerias.validarCadenaSQL(nomArchivo) +"') ");
			sql.append("\n Union All ");
			sql.append("\n select id_tipo_operacion from hist_movimiento where no_folio_det in ( ");
			sql.append("\n select no_folio_det from det_arch_transfer where nom_arch = '"+ Utilerias.validarCadenaSQL(nomArchivo) +"') ");
			sql.append("\n Union All ");
			sql.append("\n select id_tipo_operacion from hist_solicitud where no_folio_det in ( ");
			sql.append("\n select no_folio_det from det_arch_transfer where nom_arch = '"+ Utilerias.validarCadenaSQL(nomArchivo) +"') ");
			sql.append("\n ) A");
			System.out.println("en el dao 1"+ sql.toString());
			lista = jdbcTemplate.query(sql.toString(), new RowMapper<ArchTransferDto>(){
				public ArchTransferDto mapRow(ResultSet rs, int idx)throws SQLException{
					ArchTransferDto campos = new ArchTransferDto();
					campos.setDescOperacion(rs.getString("tipo"));
					return campos;
				}
 			});
			
			
			if (lista.size() > 0){
				System.out.println("en el if");
				cadena = lista.get(0).getDescOperacion();
				System.out.println("en el if obtieneTipoDeMovto"+cadena);
			}else{
				System.out.println("en el else obtieneTipoDeMovto");
			}
			
		}
		catch(Exception e){
			System.out.println("Error dao obtieneTipoDeMovto");
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: BancaElectronica, C: ControlArchivosTransferidosDaoImpl, M: obtieneTipoDeMovto");
		}return cadena;
	}
	
	public String obtieneReferenciaDet(String tipo){
		String cadena = "";
		List<ArchTransferDto> listaResultado = new ArrayList<ArchTransferDto>();
		StringBuffer sql = new StringBuffer();
		try{
			sql.append("SELECT * from referencia_det ");
			sql.append("\n WHERE id_corresponde = '"+ Utilerias.validarCadenaSQL(tipo) +"'");
			
			listaResultado = jdbcTemplate.query(sql.toString(), new RowMapper<ArchTransferDto>(){
				public ArchTransferDto mapRow(ResultSet rs, int idx)throws SQLException{
					ArchTransferDto campos = new ArchTransferDto();
					campos.setValor(rs.getString("valor"));
					return campos;
				}
			});
			
			if (listaResultado.size() > 0)
				cadena = listaResultado.get(0).getValor();
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: BancaElectronica, C: ControlArchivosTransferidosDaoImpl, M: obtieneReferenciaDet");
		}return cadena;
	}


	public String tipoChequera(String idChequera){
		String cadena = "";
		StringBuffer sql = new StringBuffer();
		List<ArchTransferDto> listaResultado = new ArrayList<ArchTransferDto>();
		try{
			sql.append("Select tipo_chequera ");
			sql.append("\n from cat_cta_banco ");
			sql.append("\n where id_chequera = '"+ Utilerias.validarCadenaSQL(idChequera) +"' ");
			
			listaResultado = jdbcTemplate.query(sql.toString(), new RowMapper<ArchTransferDto>(){
				public ArchTransferDto mapRow(ResultSet rs, int idx)throws SQLException{
					ArchTransferDto campos = new ArchTransferDto();
					campos.setTipoChequera(rs.getString("tipo_chequera"));
					return campos;
				}
			});
			
			if (listaResultado.size() > 0)
				cadena = listaResultado.get(0).getTipoChequera();
			    
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: BancaElectronica, C: ControlArchivosTransferidosDaoImpl, M: tipoChequera");
		}return cadena;
	}
	
	public String seleccionaReferencia(int noEmpresa, String idChequera, int idBanco){
		String referencia = "";
		List<ArchTransferDto> listaResultado = new ArrayList<ArchTransferDto>();
		List<ArchTransferDto> listaReferencia = new ArrayList<ArchTransferDto>();
		StringBuffer sql = new StringBuffer();
		try{
			//Se saca el no_linea y no_cuenta
			sql.append("Select no_linea, no_cuenta ");
			sql.append("\n from ctas_contrato");
			sql.append("\n where no_empresa = "+ noEmpresa +" ");
			sql.append("\n and id_chequera = '"+ Utilerias.validarCadenaSQL(idChequera) +"' ");
			sql.append("\n and id_banco = "+ idBanco +" ");
			
			listaResultado = jdbcTemplate.query(sql.toString(), new RowMapper<ArchTransferDto>(){
				public ArchTransferDto mapRow(ResultSet rs, int idx)throws SQLException{
					ArchTransferDto campos = new ArchTransferDto();
					campos.setNoLinea(rs.getInt("no_linea"));
					campos.setNoCuenta(rs.getInt("no_cuenta"));
					return campos;
				}
			});
			
			if (listaResultado.size() > 0){
				sql.delete(0, sql.length());
				sql.append("Select coalesce(persona_autoriza, '') as referencia ");
				sql.append("\n from cuenta ");
				sql.append("\n where no_empresa = "+ noEmpresa +" ");
				sql.append("\n and no_linea = "+ Utilerias.validarCadenaSQL(listaResultado.get(0).getNoLinea()) +" ");
				sql.append("\n and no_cuenta = "+ Utilerias.validarCadenaSQL(listaResultado.get(0).getNoCuenta()) +" ");
			}
			
			listaReferencia = jdbcTemplate.query(sql.toString(), new RowMapper<ArchTransferDto>(){
				public ArchTransferDto mapRow(ResultSet rs, int idx)throws SQLException{
					ArchTransferDto campos = new ArchTransferDto();
					campos.setReferencia(rs.getString("referencia"));
					return campos;
				}
			});
			
			if (listaReferencia.size() > 0)
				referencia = listaReferencia.get(0).getReferencia();
			else
				referencia = "";
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: BancaElectronica, C: ControlArchivosTransferidosDaoImpl, M: seleccionaReferencia");
		}return referencia;
	}
	
	//Funcion para obtener la Referencia, en caso que exista en ctas_contrato 
	public String obtieneReferencia(String idChequera, int idBanco, String idChequeraBenef, int idBancoBenef, String referencia, int noEmpresa){
		String cadena = "";
		String recibeDato = "";
		try{
			recibeDato = tipoChequera(idChequera);
			
			if (!recibeDato.equals("")){
				if (recibeDato.equals("I")){
					recibeDato = seleccionaReferencia(noEmpresa, idChequera, idBanco);
					if(!recibeDato.equals(""))
						cadena = recibeDato;
					else
						cadena = referencia;
				}
				else{
					recibeDato = tipoChequera(idChequeraBenef);
					if (!recibeDato.equals("")){
						if (recibeDato.equals("I")){
							recibeDato = seleccionaReferencia(noEmpresa, idChequeraBenef, idBancoBenef);
							if(!recibeDato.equals(""))
								cadena = recibeDato;
							else
								cadena = referencia;
						}
					}
				}		
			}
			
			if (cadena.equals(""))
				cadena = referencia;				
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: BancaElectronica, C: ControlArchivosTransferidosBusiness, M: obtieneReferencia");
		}return cadena;
	}
	
	public String obtieneChequeraBenefOriginal(int idBancoBenef, String sucursal, String plaza, String idChequeraBenef, int idBanco){
		String chequeraOriginal = "";
		String psSucursal = "";
		String psPlaza = "";
		try{
			psSucursal = sucursal.trim();
			psPlaza = plaza.trim();
			
			if (psPlaza.length() > 3)
				psPlaza = psPlaza.substring(0, 3);
					
			if (psSucursal.length() > 4)
				psPlaza = psPlaza.substring(0, 4);
						
			if (idBancoBenef == 2) {
				if (idBanco == 2){
					chequeraOriginal = funciones.ponerCeros(idChequeraBenef.substring(5, idChequeraBenef.length()), 11);					
				}
				else
					chequeraOriginal = idChequeraBenef;
			}
			else if (idBancoBenef == 12){
				if (idChequeraBenef.length() == 11){
					if (Integer.parseInt(idChequeraBenef.substring(0, 3)) == 0){
						chequeraOriginal = funciones.ajustarLongitudCampo(psPlaza, 3, "D", "", "0") + idChequeraBenef.substring(0, 4);
					}
					else
						chequeraOriginal = idChequeraBenef;
				}
				else
					chequeraOriginal = idChequeraBenef;
			}
			else
				chequeraOriginal = idChequeraBenef;			
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: BancaElectronica, C: ControlArchivosTransferidosBusiness, M: obtieneChequeraBenefOriginal");
		}return chequeraOriginal;
	} 
	
	public String armaQueryDetalle(String nombreTabla, String nomArchivo, int idBanco, String tipoOperacion){
		String sql = "";
		boolean manejaBital = false;
		
		try{
			if (idBanco == 135) //Para Nafin
			{
				sql = "SELECT convert(char,(convert(char(10),m.fec_valor_original,103))) as fec_valor_original,'' as id_banco_city, coalesce(m.id_servicio_be, '') as id_servicio_be ,m.id_forma_pago,m.id_tipo_operacion,m.id_banco,m.observacion, ";
				sql += "\n '' as b_layout_comerica,'' as plaza_benef, ";
				sql += "\n '' as plaza,'' as inst_finan,m.b_entregado, ";
				sql += "\n m.id_estatus_mov,m.no_cliente,d.no_docto,c.desc_estatus, ";
				sql += "\n convert(char(10),d.fec_valor,103) as fec_valor,'' as desc_banco, ";
				sql += "\n d.id_chequera,d.id_chequera_benef,d.importe, ";
				sql += "\n d.beneficiario, '' as sucursal_origen, '' as sucursal_destino, ";
				sql += "\n m.concepto , d.no_folio_det, m.fec_operacion,m.id_divisa,m.id_banco_benef, ";
				sql += "\n (select desc_banco from cat_banco where id_banco = m.id_banco_benef) as desc_banco_benef, ";
				sql += "\n '' as desc_banco_inbursa, '' as clabe_benef, '' as aba,'' as swift_code, ";
				sql += "\n '' as aba_intermediario, '' as swift_intermediario, '' as aba_corresponsal, '' as swift_corresponsal, ";
				sql += "\n '' as nom_banco_intermediario, '' as nom_banco_corresponsal, coalesce(t.tipo_envio_layout, '') as tipo_envio_layout, m.origen_mov,'' as tipo_chequera_benef, ";
				sql += "\n p.equivale_persona, m.no_factura, case when rtrim(ltrim(p.rfc)) = '' then m.rfc else rtrim(ltrim(p.rfc)) end as rfc_benef, ";
				sql += "\n '' as nom_empresa, m.no_empresa, pp.rfc, '' as especiales, '' as complemento, 0 as tipo_envio_layout_ctas, ";
				sql += "\n ''  as id_contrato_wlink, ' 'as direccion_benef , (select pa.desc_pais from cat_pais pa where pa.id_pais = 'MX') as pais_benef, ";
				sql += "\n coalesce(referencia_ban,'') as referencia_ban ";
				sql += "\n From det_arch_transfer d, cat_estatus c, movimiento  m, arch_transfer t, persona p, persona pp";
				sql += "\n Where ";
				sql += "\n cast(m.no_cliente as integer) = p.no_persona ";
				sql += "\n and p.id_tipo_persona = 'P' ";
				sql += "\n and d.nom_arch = t.nom_arch ";
				sql += "\n and m.no_folio_det = d.no_folio_det ";
				sql += "\n and d.id_estatus_arch = c.id_estatus ";
				sql += "\n And c.clasificacion = 'MOV' ";
				sql += "\n AND m.id_forma_pago = 7 ";
				sql += "\n And d.nom_arch = '"+ Utilerias.validarCadenaSQL(nomArchivo) +"' ";
				sql += "\n And m.no_empresa = pp.no_empresa ";
				sql += "\n And m.no_empresa = pp.no_persona ";
				sql += "\n And pp.id_tipo_persona = 'E' ";	
				sql += "\n  AND id_estatus_arch <> 'X'";
			}//Termina el if para el banco 135
			else
			{
				if (configuraSet(232).equals("SI") && idBanco == 21)
					manejaBital = true;
				
				if (tipoOperacion.equals("TRANSFERENCIAS"))
				{
					
				
					sql = "select DISTINCT convert(char,(convert(char(10),hm.fec_valor_original,103))) as fec_valor_original ,coalesce(b.id_banco_city, '0') as id_banco_city,  coalesce(hm.id_servicio_be, '') as id_servicio_be,hm.id_forma_pago, ";
					sql += "\n hm.id_tipo_operacion,hm.id_banco,hm.observacion,b.b_layout_comerica,cast(dar.plaza as varchar(10)) as plaza_benef,cb.desc_plaza as plaza,b.inst_finan,hm.b_entregado,";
					sql += "\n hm.id_estatus_mov,hm.no_cliente,hm.no_docto,e.desc_estatus,convert(char(10),dar.fec_valor,103) as fec_valor,b.desc_banco,hm.id_chequera,hm.id_chequera_benef,hm.importe, ";
					sql += "\n hm.beneficiario,cb.desc_sucursal as sucursal_origen,dar.sucursal as sucursal_destino,hm.concepto,hm.no_folio_det,convert(char,(convert(char,hm.fec_operacion,3))) as fec_operacion,hm.id_divisa, ";
					sql += "\n hm.id_banco_benef,(select desc_banco from cat_banco where id_banco = hm.id_banco_benef) as desc_banco_benef,b.desc_banco_inbursa,csb.id_clabe as clabe_benef,";
					sql += "\n coalesce(csb.aba, '') as aba,coalesce(csb.swift_code, '') as swift_code, coalesce(csb.aba_intermediario, '') as aba_intermediario,coalesce(csb.swift_intermediario, '') as swift_intermediario, ";
					sql += "\n coalesce(csb.aba_corresponsal, '') as aba_corresponsal, coalesce(csb.swift_corresponsal, '') as swift_corresponsal,b.desc_banco as nom_banco_intermediario, ";
					sql += "\n b.desc_banco  as nom_banco_corresponsal, coalesce(at.tipo_envio_layout, '') as tipo_envio_layout,hm.origen_mov,'' as tipo_chequera_benef,  ";
					sql += "\n p.equivale_persona as equivale_persona,hm.no_factura,(select top 1 rfc from persona where no_persona = hm.no_cliente) as rfc_benef, ";
					sql += "\n (select nom_empresa from empresa where no_empresa = hm.no_empresa) as nom_empresa, hm.no_empresa, ";
					
					if (manejaBital)
						sql += "\n u.desc_usuario_bital, s.desc_servicio_bital, ";
					else
						sql += "\n '0' as desc_usuario_bital, '' as desc_servicio_bital, ";
					
					sql += "\n p.rfc,csb.especiales, csb.complemento, coalesce((select max(id_tipo_envio) from cat_tipo_envio_layout ctt where ctt.tipo_envio_layout = csb.tipo_envio_layout), 0) as tipo_envio_layout_ctas, ";
					sql += "\n em.id_contrato_wlink,(select top 1 (coalesce(calle_no,'') + ', ' + coalesce(colonia,'') + ', CP.' + coalesce(id_cp,'') + ', ' + coalesce(deleg_municipio,'') + ', ' + coalesce(ciudad,'') )   from direccion where no_persona = hm.no_cliente) as direccion_benef, ";
					sql += "\n (select top 1 desc_pais from direccion where no_persona = hm.no_cliente) as pais_benef,hm.referencia as referencia_ban, 200 as ruta";
					sql += "\n FROM arch_transfer at, det_arch_transfer dar,cat_cta_banco cb, cat_estatus e, persona p,";
					
					sql += Utilerias.validarCadenaSQL(nombreTabla) +" hm";
					sql += "\n LEFT JOIN ctas_banco csb ON (hm.no_cliente = csb.no_persona ";
					sql += "\n 		And hm.id_banco_benef = csb.id_banco ";
					sql += "\n 		And hm.id_chequera_benef = csb.id_chequera) ";
					sql += "\n 			LEFT JOIN cat_banco b ON(csb.id_bank_true = b.id_banco and csb.id_bank_corresponding = b.id_banco and hm.id_banco = b.id_banco)";
					
					if (manejaBital){ 	
						sql += "\n  INNER JOIN (empresa em";
						sql += "\n  	LEFT JOIN cat_usuario_bital u ON(em.id_usuario_bital = u.id_usuario_bital)";
						sql += "\n  	LEFT JOIN cat_servicio_bital s ON (em.id_servicio_bital = s.id_servicio_bital)";
						sql += "\n  )   ON(hm.no_empresa = em.no_empresa) ";
						
					}else{
						sql += "\n 	INNER JOIN empresa em  ON(hm.no_empresa = em.no_empresa)";
					}
					
					sql += "\n where  hm.no_folio_det = dar.no_folio_det ";
					sql += "\n and at.nom_arch = dar.nom_arch ";
					sql += "\n and at.nom_arch = '"+ Utilerias.validarCadenaSQL(nomArchivo) +"' ";
					
//					if (manejaBital){
//						sql += "\n and e.id_usuario_bital *= u.id_usuario_bital ";
//						sql += "\n and e.id_servicio_bital *= s.id_servicio_bital ";
//					}					
					sql += "\n and coalesce(hm.origen_mov,'') <> 'FIL' ";
					sql += "\n and hm.no_cliente not in("+ configuraSet(206) +")";
					sql += "\n and hm.id_tipo_operacion not between 3700 and 3899 ";
					sql += "\n and csb.no_persona = hm.no_cliente ";
					sql += "\n and hm.id_chequera_benef = csb.id_chequera";
					sql += "\n and hm.id_estatus_mov = e.id_estatus";
					sql += "\n and e.clasificacion = 'MOV' ";
					sql += "\n and hm.id_chequera = cb.id_chequera ";
					sql += "\n and p.no_persona =  hm.no_empresa ";
					sql += "\n  AND id_estatus_arch <> 'X'";
					sql += "\n and p.id_tipo_persona = 'E' ";
						
					//Para Filiales y FYE
					sql += "\n union ";
					sql += "\n Select distinct convert(char,(convert(char(10),m.fec_valor_original,103))) as fec_valor_original, coalesce(b2.id_banco_city, '0') as id_banco_city, coalesce(m.id_servicio_be, '') as id_servicio_be, m.id_forma_pago, ";
					sql += "\n m.id_tipo_operacion, m.id_banco, m.observacion, b.b_layout_comerica, ";
					sql += "\n cast(m.plaza as varchar(10)) as plaza_benef, cc.desc_plaza as plaza, b2.inst_finan, m.b_entregado,";
					sql += "\n m.id_estatus_mov, m.no_cliente, d.no_docto, c.desc_estatus,convert(char(10),d.fec_valor,103) as fec_valor, b.desc_banco, ";
					sql += "\n d.id_chequera, d.id_chequera_benef, d.importe, d.beneficiario, cc.desc_sucursal as sucursal_origen, ";
					sql += "\n m.sucursal as sucursal_destino, m.concepto, d.no_folio_det, ";
					sql += "\n convert(char,(convert(char(10),m.fec_operacion,103))) as fec_operacion, m.id_divisa, m.id_banco_benef,";
					sql += "\n (select desc_banco from cat_banco where id_banco = m.id_banco_benef) as desc_banco_benef, ";
					sql += "\n b2.desc_banco_inbursa, m.clabe as clabe_benef, ''  as aba, ";
					sql += "\n ''  as swift_code, '' as aba_intermediario,'' as swift_intermediario, ";
					sql += "\n '' as aba_corresponsal, '' as swift_corresponsal, '' as nom_banco_intermediario, '' as nom_banco_corresponsal, ";
					sql += "\n coalesce(t.tipo_envio_layout, '') as tipo_envio_layout, m.origen_mov, '' as tipo_chequera_benef, '' as equivale_persona,m.no_factura, ";
					sql += "\n m.rfc as rfc_benef, e.nom_empresa, m.no_empresa, ";
					
					if (manejaBital)
						sql += "\n u.desc_usuario_bital,s.desc_servicio_bital, ";
					else
						sql += "\n '0' as desc_usuario_bital, '' as desc_servicio_bital, ";
					
					sql += "\n pp.rfc, '' as especiales, '' as complemento, 0 as tipo_envio_layout_ctas, e.id_contrato_wlink, ";
					sql += "\n dir.ciudad + ', ' + dir.id_estado as direccion_benef, ";
					sql += "\n (select pa.desc_pais from cat_pais pa where pa.id_pais = 'MX') as pais_benef, '' as referencia_ban, 200 as ruta ";
					sql += "\n FROM det_arch_transfer d, cat_estatus c, cat_banco b,cat_banco b2,"+ Utilerias.validarCadenaSQL(nombreTabla) +" m,";
					sql += "\n cat_cta_banco cc, arch_transfer t,persona pp, direccion dir,  ";
					
					if (manejaBital){
						sql += "\n empresa e";
						sql += "\n  	LEFT JOIN cat_usuario_bital u ON(e.id_usuario_bital = u.id_usuario_bital)";
						sql += "\n  	LEFT JOIN cat_servicio_bital s ON (e.id_servicio_bital = s.id_servicio_bital)";
					}else{
						sql += "\n 	empresa e ";
					}
					sql += "\n Where cc.no_empresa = e.no_empresa ";
					
//					if (manejaBital){
//						sql += "\n and e.id_usuario_bital *= u.id_usuario_bital ";					
//						sql += "\n and e.id_servicio_bital *= s.id_servicio_bital ";
//					}
					
					sql += "\n AND d.nom_arch = t.nom_arch ";
					sql += "\n AND m.id_banco = cc.id_banco ";
					sql += "\n AND m.id_chequera = cc.id_chequera ";
					sql += "\n and m.no_folio_det = d.no_folio_det ";
					sql += "\n and d.id_estatus_arch = c.id_estatus And c.clasificacion = 'TRN' ";
					sql += "\n And b.id_banco = m.id_banco ";
					sql += "\n And b2.id_banco = m.id_banco_benef ";
					sql += "\n And d.nom_arch = '"+ Utilerias.validarCadenaSQL(nomArchivo) +"' ";
					sql += "\n and (m.origen_mov = 'FIL' or m.no_cliente in ("+ configuraSet(206) +"))";
					sql += "\n And cc.no_empresa = pp.no_empresa ";
					sql += "\n And cc.no_empresa = pp.no_persona ";
					sql += "\n And pp.id_tipo_persona = 'E' ";
					sql += "\n AND pp.no_empresa = dir.no_persona ";
					sql += "\n and pp.no_persona = dir.no_persona ";
					sql += "\n and dir.id_tipo_direccion = 'OFNA' ";
					sql += "\n and id_estatus_arch <> 'X' ";
					sql += "\n and dir.id_tipo_persona = 'E' ";
					sql += "\n and no_direccion = 1";	
				}
				else //Para traspasos
				{
					sql = "SELECT convert(char,(convert(char(10),m.fec_valor_original,103))) as fec_valor_original, coalesce(b2.id_banco_city, '0') as id_banco_city, coalesce(m.id_servicio_be, '') as id_servicio_be, m.id_forma_pago, m.id_tipo_operacion, ";
					sql += "\n m.id_banco, m.observacion, b.b_layout_comerica, cc2.desc_plaza as plaza_benef, cc.desc_plaza as plaza,";
					sql += "\n b2.inst_finan, m.b_entregado, m.id_estatus_mov, m.no_cliente, d.no_docto, c.desc_estatus, convert(char(10),d.fec_valor,103) as fec_valor, b.desc_banco,";
					sql += "\n d.id_chequera, d.id_chequera_benef, d.importe,";
					sql += "\n d.beneficiario, cc.desc_sucursal as sucursal_origen, ";
					sql += "\n cc2.desc_sucursal as sucursal_destino, m.concepto , d.no_folio_det, convert(char,(convert(char(10),m.fec_operacion,3))) as fec_operacion, m.id_divisa, m.id_banco_benef,";
					sql += "\n (select desc_banco from cat_banco where id_banco = m.id_banco_benef) as desc_banco_benef, ";
					sql += "\n b2.desc_banco_inbursa, ";
					sql += "\n cc2.id_clabe as clabe_benef, coalesce(cc2.aba, '') as aba, coalesce(cc2.swift_code, '') as swift_code, ";
					sql += "\n '' as aba_intermediario,'' as swift_intermediario, '' as aba_corresponsal, '' as swift_corresponsal, ";
					sql += "\n '' as nom_banco_intermediario, '' as nom_banco_corresponsal, coalesce(t.tipo_envio_layout, '') as tipo_envio_layout, ";
					sql += "\n m.origen_mov,cc2.tipo_chequera as tipo_chequera_benef, p.equivale_persona, m.no_factura, ";
					sql += "\n p.rfc as rfc_benef, e.nom_empresa, m.no_empresa, ";
					
					if (manejaBital)
						sql += "\n u.desc_usuario_bital,s.desc_servicio_bital, ";
					else
						sql += "\n '0' as desc_usuario_bital, '' as desc_servicio_bital, ";
						
					if (idBanco == 12){
						sql += "\n pp.rfc, (select pa.desc_pais from cat_pais pa where pa.id_pais = 'MX') as especiales, ";
						sql += "\n (SELECT contacto FROM medios_contacto mc where ";
						sql += "\n pp.no_empresa = mc.no_persona and pp.no_persona = mc.no_persona ";
						sql += "\n and pp.id_tipo_persona = mc.id_tipo_persona and mc.id_tipo_medio = 'TOF1' ";
						sql += "\n and mc.no_medio = 1) as complemento, ";
					}
					else						
						sql += "\n pp.rfc, '' as especiales, '' as complemento, ";
					
					
					sql += "\n 0 as tipo_envio_layout_ctas, e.id_contrato_wlink, ";
					sql += "\n dir.ciudad + ', ' + dir.id_estado as direccion_benef , ";
					sql += "\n (select pa.desc_pais from cat_pais pa where pa.id_pais = 'MX') as pais_benef, '' as referencia_ban, 3006 as ruta ";
					sql += "\n FROM det_arch_transfer d, cat_estatus c, cat_banco b,cat_banco b2, persona p, cat_cta_banco cc, arch_transfer t,cat_cta_banco cc2,persona pp, direccion dir,";
					sql += "\n "+ nombreTabla +" m";
					
					
					if (manejaBital){ 	
						sql += "\n  INNER JOIN (empresa e";
						sql += "\n  	LEFT JOIN cat_usuario_bital u ON(e.id_usuario_bital = u.id_usuario_bital)";
						sql += "\n  	LEFT JOIN cat_servicio_bital s ON (e.id_servicio_bital = s.id_servicio_bital)";
						sql += "\n  )   ON(hm.no_empresa = em.no_empresa) ";
						
					}else{
						sql += "\n 	INNER JOIN empresa e  ON(m.no_empresa = e.no_empresa)";
					}
					
					
					sql += "\n Where ";
					
					
					
					sql += "\n cast(m.no_cliente as integer) = p.no_empresa ";
					sql += "\n AND cast(m.no_cliente as integer) = p.no_persona ";
					sql += "\n AND p.id_tipo_persona = 'E' ";
					sql += "\n AND m.id_chequera_benef = cc2.id_chequera ";
					sql += "\n and d.nom_arch = t.nom_arch ";
					sql += "\n and m.no_empresa = cc.no_empresa  AND m.id_banco = cc.id_banco";
					sql += "\n AND m.id_chequera = cc.id_chequera ";
					sql += "\n and m.no_folio_det = d.no_folio_det ";
					sql += "\n and d.id_estatus_arch = c.id_estatus And c.clasificacion = 'TRN' ";
					sql += "\n And b.id_banco = m.id_banco ";
					sql += "\n And b2.id_banco = m.id_banco_benef ";
					sql += "\n And d.nom_arch = '"+ Utilerias.validarCadenaSQL(nomArchivo) +"'";
					sql += "\n and m.id_tipo_operacion between 3700 and 3899 ";
					sql += "\n And m.no_empresa = pp.no_empresa ";
					sql += "\n And m.no_empresa = pp.no_persona ";
					sql += "\n And pp.id_tipo_persona = 'E' ";
					sql += "\n AND m.no_empresa = dir.no_persona ";
					sql += "\n and pp.no_persona = dir.no_persona ";
					sql += "\n and dir.id_tipo_persona = 'E' ";
					sql += "\n and dir.id_tipo_direccion = 'OFNA' ";
					sql += "\n and dir.no_direccion = 1 ";	
					sql += "\n  AND id_estatus_arch <> 'X'";
				}				
			}
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: BancaElectronica, C: ControlArchivosTransferidosDaoImpl, M: armaQueryDetalle");
		}return sql;
	}
	
	public List<ArchTransferDto> llenaGridDetalle(String nomArchivo, int idBanco, boolean enviadasHoy, String tipoOperacion){
		List<ArchTransferDto> listaResultado = new ArrayList<ArchTransferDto>();	
		StringBuffer sql = new StringBuffer();
		final int banco=idBanco;
		try{
			sql.append(armaQueryDetalle("movimiento", nomArchivo, idBanco, tipoOperacion));
			if (enviadasHoy == false){
				if (tipoOperacion.equals("TRASPASOS")){
					sql.append("\n union ");
					sql.append("\n " + armaQueryDetalle("hist_solicitud", nomArchivo, idBanco, tipoOperacion));
				}
				sql.append("\n union ");
				sql.append("\n " + armaQueryDetalle("hist_movimiento", nomArchivo, idBanco, tipoOperacion));
			}
			System.out.println("Query detalle arch: " + sql.toString() + "FIN SQL");
				
			listaResultado = jdbcTemplate.query(sql.toString(), new RowMapper<ArchTransferDto>(){
				public ArchTransferDto mapRow(ResultSet rs, int idx)throws SQLException{
					ArchTransferDto campos = new ArchTransferDto();
					/*de la consulta falta agregar los siguientes campos
					 * 
						ID_FORMA_PAGO
					 * */
					campos.setHora("");
					campos.setTipoEnvioComerica("");
					if(banco==2){
						String cheq=rs.getString("ID_CHEQUERA");
						int tamano=cheq.length();	
//						System.out.print("chequera original"+cheq);
						String cadena=cheq.substring(5);
//						System.out.print("chequera final"+cadena);
						String cad2=funciones.ponerCeros(cadena, 11);
						//System.out.print("chequera final"+cad2);
						campos.setIdChequeraBanamex(cad2);
					}else{
						campos.setIdChequeraBanamex("");	
					}
					
				 	campos.setAbaSwift("");
				 	campos.setTelefonoBenef("");
				 	campos.setColor("");
				 	campos.setIdAbaSwift(rs.getString("SWIFT_CODE")); //preguntar
				 	campos.setIdAbaSwiftInter(rs.getString("ABA_INTERMEDIARIO")); //SWIFT_INTERMEDIARIO
				 	campos.setIdChequeraBenefReal(rs.getString("TIPO_CHEQUERA_BENEF"));
				 	campos.setTipoEnvioLayout("TIPO_ENVIO_LAYOUT");
				 	campos.setEstatusEnArchivo("");
					campos.setNoDocto(rs.getString("NO_DOCTO"));
					campos.setDescEstatus(rs.getString("DESC_ESTATUS"));
					campos.setFecValor(rs.getString("FEC_VALOR"));
					campos.setDescBanco(rs.getString("DESC_BANCO"));
					campos.setIdChequera(rs.getString("ID_CHEQUERA"));
					campos.setDescBancoBenef(rs.getString("DESC_BANCO_BENEF"));
					campos.setIdChequeraBenef(rs.getString("ID_CHEQUERA_BENEF"));
					campos.setImporte(Double.parseDouble(rs.getString("IMPORTE")));
					campos.setBeneficiario(rs.getString("BENEFICIARIO"));
					campos.setPlaza(rs.getString("PLAZA"));
					campos.setSucursal(rs.getString("SUCURSAL_DESTINO"));
					campos.setConcepto(rs.getString("CONCEPTO"));
					campos.setNoFolioDet(Integer.parseInt(rs.getString("NO_FOLIO_DET")));
					campos.setNoCliente(rs.getString("NO_CLIENTE"));
					campos.setBEntregado(rs.getString("B_ENTREGADO"));
					campos.setIdEstatusMov(rs.getString("ID_ESTATUS_MOV"));
					campos.setIdDivisa(rs.getString("ID_DIVISA"));
					campos.setIdBancoBenef(Integer.parseInt(rs.getString("ID_BANCO_BENEF")));
					campos.setInstitucionFinan(rs.getString("INST_FINAN"));
					campos.setAba(rs.getString("ABA"));
					campos.setPlazaBenef(rs.getString("PLAZA_BENEF"));
					campos.setLayoutComerica(rs.getString("B_LAYOUT_COMERICA"));
					campos.setFecHoy(rs.getString("FEC_OPERACION"));
					campos.setSucOrigen(rs.getString("SUCURSAL_ORIGEN"));
					campos.setObservacion(rs.getString("OBSERVACION"));
					campos.setDescBancoInbursa(rs.getString("DESC_BANCO_INBURSA"));
					campos.setIdClabeBenef(rs.getString("CLABE_BENEF"));
				 	campos.setBancoInter(rs.getString("NOM_BANCO_INTERMEDIARIO"));
				 	campos.setIdAbaSwiftCorres(rs.getString("ABA_CORRESPONSAL"));
				 	campos.setSwiftCorres(rs.getString("SWIFT_CORRESPONSAL"));
				 	campos.setBancoCorres(rs.getString("NOM_BANCO_CORRESPONSAL"));
				 	campos.setIdTipoOperacion(rs.getString("ID_TIPO_OPERACION"));
				 	campos.setNoEmpresa(Integer.parseInt(rs.getString("NO_EMPRESA")));
				 	campos.setNomEmpresa(rs.getString("NOM_EMPRESA"));
				 	campos.setEquivalePersona(rs.getString("EQUIVALE_PERSONA"));
				 	campos.setNoFactura(rs.getString("NO_FACTURA"));
				 	campos.setIdServicioBE(rs.getString("ID_SERVICIO_BE"));
				 	campos.setIdBancoCity(Integer.parseInt(rs.getString("ID_BANCO_CITY")));
				 	campos.setRfcBenef(rs.getString("RFC_BENEF"));
				 	campos.setFecValorOriginal(rs.getString("FEC_VALOR_ORIGINAL"));
				 	campos.setUsuarioBital(rs.getString("DESC_USUARIO_BITAL"));
				 	campos.setServicioBital(rs.getString("DESC_SERVICIO_BITAL"));
				 	campos.setRfc(rs.getString("RFC"));
				 	campos.setContratoWlink(rs.getString("ID_CONTRATO_WLINK"));
				 	campos.setEspeciales(rs.getString("ESPECIALES"));
				 	campos.setComplemento(rs.getString("COMPLEMENTO"));
				 	campos.setTipoEnvioLayoutCtas(rs.getString("TIPO_ENVIO_LAYOUT_CTAS"));
				 	campos.setPaisBenef(rs.getString("PAIS_BENEF"));
				 	campos.setDireccionBenef(rs.getString("DIRECCION_BENEF"));
				 	campos.setReferenciaBan(rs.getString("REFERENCIA_BAN"));
				 	campos.setIdBanco(Integer.parseInt(rs.getString("ID_BANCO")));
				 	campos.setOrigenMov(rs.getString("ORIGEN_MOV"));
				 	campos.setRuta(Integer.parseInt(rs.getString("RUTA")));
				 	campos.setHoraRecibo(funciones.obtenerHoraActual(false).substring(0, 5));
				 	return campos;
				}
			});
		}
		catch(Exception e){
			System.out.println("error en el dao impl llenagridDetalle");
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: BancaElectronica, C: ControlArchivosTransferidosDaoImpl, M: llenaGridDetalle");
		}
		return listaResultado;
	}
	
	public List<ArchTransferDto> llenaGridDetalleCheque(String nomArchivo){
		List<ArchTransferDto> listaResultado = new ArrayList<ArchTransferDto>();
		StringBuffer sql = new StringBuffer();
		try{
				
			
			sql.append("Select e.nom_empresa,m.observacion,m.concepto,b.b_layout_comerica,m.id_estatus_mov, ");
			sql.append("\n m.b_entregado , m.no_docto, c.desc_estatus, m.fec_valor, d.nom_arch, d.id_banco, b.desc_banco,");
			sql.append("\n m.id_chequera , m.id_divisa, d.beneficiario, m.importe, m.no_folio_det, d.nom_arch, m.no_cliente ");
			sql.append("\n FROM det_arch_transfer d LEFT JOIN cat_estatus c ON(d.id_estatus_arch = c.id_estatus) ");
			sql.append("\n ,movimiento m, cat_banco b, empresa e ");
			sql.append("\n WHERE");
			sql.append("\n m.no_empresa = e.no_empresa ");
			sql.append("\n and m.no_folio_det = d.no_folio_det  and ");
			sql.append("\n d.id_banco = b.id_banco  and ");
			sql.append("\n and d.nom_arch = '"+ Utilerias.validarCadenaSQL(nomArchivo) +"' ");
			sql.append("\n d.id_estatus_arch <> 'X'  and c.clasificacion = 'MOV' ");
			sql.append("\n UNION ");
			sql.append("\n Select e.nom_empresa,m.observacion,m.concepto,b.b_layout_comerica,m.id_estatus_mov,m.b_entregado,m.no_docto,c.desc_estatus,m.fec_valor, d.nom_arch,d.id_banco,b.desc_banco,");
			sql.append("\n m.id_chequera , m.id_divisa, d.beneficiario, m.importe, m.no_folio_det, d.nom_arch, m.no_cliente"); 
			sql.append("\n FROM det_arch_transfer d LEFT JOIN cat_estatus c ON(d.id_estatus_arch = c.id_estatus)"); 
			sql.append("\n ,hist_movimiento m, cat_banco b,empresa e"); 
			sql.append("\n WHERE"); 
			sql.append("\n m.no_empresa = e.no_empresa"); 
			sql.append("\n and m.no_folio_det = d.no_folio_det  and"); 
			sql.append("\n d.id_banco = b.id_banco  and"); 
			sql.append("\n and d.nom_arch = '"+ Utilerias.validarCadenaSQL(nomArchivo) +"' ");  
			sql.append("\n d.id_estatus_arch <> 'X'  and c.clasificacion = 'MOV'"); 
			
			
			
			
			
			System.out.println("llenaGridDetalleCheque " +sql);
			listaResultado = jdbcTemplate.query(sql.toString(), 
			new DetalleArchivo(nomArchivo) {
				public ArchTransferDto mapRow(ResultSet rs, int idx)throws SQLException{
					ArchTransferDto campos = new ArchTransferDto();
					campos.setNoDocto(rs.getString("no_docto"));
					campos.setDescEstatus(rs.getString("desc_estatus"));
					campos.setFecValor(rs.getString("fec_valor"));
					campos.setDescBanco(rs.getString("desc_banco"));
					campos.setIdBanco(rs.getInt("id_banco"));
					campos.setIdChequera(rs.getString("id_chequera"));
					campos.setImporte(rs.getDouble("importe"));
					campos.setBeneficiario(rs.getString("beneficiario"));
					campos.setNoFolioDet(rs.getInt("no_folio_det"));
					campos.setBEntregado(rs.getString("b_entregado"));
					campos.setIdEstatusMov(rs.getString("id_estatus_mov"));
					campos.setIdDivisa(rs.getString("id_divisa"));
					campos.setConcepto(rs.getString("concepto"));
					campos.setObservacion(rs.getString("observacion"));
					campos.setNomEmpresa(rs.getString("nom_empresa"));
					campos.setNoCliente(rs.getString("no_cliente"));
					campos.setColor("color:#000000");
					campos.setEstatusEnArchivo("");
					if (getNomArchivo().substring(0, 4).equalsIgnoreCase("tran")) {
						String renglon = leerDetalleArchivo(getNomArchivo(), 
								String.valueOf(campos.getNoFolioDet()), 0);
						campos.setEstatusEnArchivo(
								obtenerEstatusRegistro(
										renglon.substring(400, 402)));
						if (!renglon.substring(400, 402).equals("00")) {
							campos.setColor("color:#E81624");
						}
					}
					
					return campos;
				}
			});
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: BancaElectronica, C: ControlArchivosTransferidosDaoImpl, M: llenaGridDetalleCheque");
		}return listaResultado;
	}	

	public int actualizaArchTransfer(double importeTotal, int registroTotal, String fecHoy, int noUsuario, String nomArch){
		int resultado = 0;
		StringBuffer sql = new StringBuffer();
		
		try{
			sql.append("update arch_transfer set importe = (importe- "+ importeTotal +"), ");
			sql.append("\n registros = (registros-"+ registroTotal +"), ");
			sql.append("\n fec_retrans = '"+ Utilerias.validarCadenaSQL(fecHoy) +"', ");
			sql.append("\n no_usuario_modif = "+ noUsuario +"");
			sql.append("\n where nom_arch = '"+ Utilerias.validarCadenaSQL(nomArch) +"'");
			System.out.println("actualizarArchTranfer:"+ sql.toString());
			resultado = jdbcTemplate.update(sql.toString());
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: BancaElectronica, C: ControlArchivosTransferidosDaoImpl, M: actualizaArchTransfer");
		}return resultado;
	}
	
	public int actualizaArchCancelado(double importeTotal, int registroTotal, String nomArch){
		System.out.println("en el daoimpl");
		int resultado = 0;
		StringBuffer sql = new StringBuffer();
		try{
			sql.append("update arch_transfer set importe = "+ importeTotal +", ");
			sql.append("\n registros = "+ registroTotal +"");
			sql.append("\n where nom_arch = '"+ Utilerias.validarCadenaSQL(nomArch) +"'");
			System.out.println(sql.toString());
			resultado = jdbcTemplate.update(sql.toString());
			System.out.println(sql.toString());
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: BancaElectronica, C: ControlArchivosTransferidosDaoImpl, M: actualizaArchTransfer");
		}return resultado;
	}
	
	public int actualizaEstatus(int noFolioDet, String estatusMov){
		int resultado = 0;
		StringBuffer sql = new StringBuffer();
		try{
			sql.append("update movimiento set id_estatus_mov = '"+ Utilerias.validarCadenaSQL(estatusMov) +"' ");
			sql.append("\n where no_folio_det = "+ noFolioDet +" ");
			System.out.println("actualiza status movimiento "+sql.toString());
			resultado = jdbcTemplate.update(sql.toString());
			
			if (resultado <= 0){
				sql.append("update hist_movimiento set id_estatus_mov = '"+ estatusMov +"' ");
				sql.append("\n where no_folio_det = "+ noFolioDet +" ");
				System.out.println("actualizar estus "+sql.toString());
				resultado = jdbcTemplate.update(sql.toString());
			}
			
		}
		catch(Exception e){
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:BancaElectronica, C: ControlArchivosTransferidosDaoImpl, M: actualizaEstatus");
		}return resultado;
	}
	
	public int actualizaDetArchTransfer(String nomArchivo, int noFolioDet){
		int respuesta = 0;
		StringBuffer sql = new StringBuffer();
		try{
			sql.append("update det_arch_transfer set id_estatus_arch = 'X' ");
			sql.append("\n where nom_arch = '"+ Utilerias.validarCadenaSQL(nomArchivo) +"' ");
			sql.append("\n and no_folio_det = "+ noFolioDet +"");
			
			System.out.println("actualizaDetArchTransfer "+sql.toString());
			
			respuesta = jdbcTemplate.update(sql.toString());
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: BancaElectronica, C: ControlArchivosTransferidosDaoImpl, M: actulizaDetArchTransfer");
		}return respuesta;
	}
	
	public int actualizaArchivoRegenerado(String nomArchivo, int noFolioDet){
		int respuesta = 0;
		StringBuffer sql = new StringBuffer();
		System.out.println("folio det  "+noFolioDet);
		try{
			System.out.println("llego al update");
			sql.append("update det_arch_transfer set id_estatus_arch = 'R' ");
			sql.append("\n where nom_arch = '"+ Utilerias.validarCadenaSQL(nomArchivo) +"' ");
			sql.append("\n and no_folio_det = "+ noFolioDet +"");
			
			System.out.println(sql.toString());
			
			respuesta = jdbcTemplate.update(sql.toString());
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: BancaElectronica, C: ControlArchivosTransferidosDaoImpl, M: actulizaArchivoRegenerado");
		}return respuesta;
	}
	
	//************************************************************************************************************************************
	public void setDataSource(DataSource dataSource) {
		try {
			this.jdbcTemplate = new JdbcTemplate(dataSource);
		}
		catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:BancaElectronica, C: ControlArchivosTransferidosDaoImpl, M:setDataSource");
		}
	}
	
	public class DetalleArchivo implements RowMapper<ArchTransferDto>{
		private String nomArchivo;
		
		public DetalleArchivo(String nomArchivo) {
			this.nomArchivo = nomArchivo;
		}		
		
		public String getNomArchivo() {
			return nomArchivo;
		}

		public ArchTransferDto mapRow (ResultSet rs, int idx)throws SQLException{
			String psDestino = "";
			String psReferenciaTraspaso = "";
			String psReferencia = "";
			boolean bTraspasos = false;
			ArchTransferDto campos = new ArchTransferDto();
			
			campos.setNoDocto(rs.getString("no_docto"));					
			campos.setDescEstatus(rs.getString("desc_estatus"));
			if (campos.getDescEstatus().equals("CANCELADO"))
				campos.setColor("color:#E81624");
			else
				campos.setColor("color:#000000");
			
			campos.setFecValor(rs.getString("fec_valor"));
			campos.setDescBanco(rs.getString("desc_banco"));
			campos.setIdChequera(rs.getString("id_chequera"));
			campos.setDescBancoBenef(rs.getString("desc_banco_benef"));
			campos.setIdChequeraBenef(rs.getString("id_chequera_benef"));
			campos.setImporte(rs.getDouble("importe"));
			campos.setBeneficiario(rs.getString("beneficiario"));
			campos.setPlaza(rs.getString("plaza"));
			campos.setSucursal(rs.getString("sucursal_destino"));
			campos.setConcepto(rs.getString("concepto"));
			campos.setNoFolioDet(rs.getInt("no_folio_det"));
			campos.setNoCliente(rs.getString("no_cliente"));
			campos.setBEntregado(rs.getString("b_entregado"));
			campos.setIdEstatusMov(rs.getString("id_estatus_mov"));
			campos.setIdDivisa(rs.getString("id_divisa"));
			campos.setIdBancoBenef(rs.getInt("id_banco_benef"));
			campos.setInstitucionFinan(rs.getString("inst_finan"));
			campos.setPlazaBenef(rs.getString("plaza_benef"));
			campos.setLayoutComerica(rs.getString("b_layout_comerica"));	
			//campos.setFecHoy(rs.getString("Date.toString()"));
			/*
 	{name: 'fecHoy'},*/
			campos.setSucOrigen(rs.getString("sucursal_origen"));
	//{name: 'hora'},
			campos.setObservacion(rs.getString("observacion"));
			campos.setIdChequeraBenefReal(rs.getString("clabe_benef"));
			campos.setDescBancoInbursa(rs.getString("desc_banco_inbursa"));
			campos.setIdClabeBenef(rs.getString("clabe_benef"));
			campos.setTipoEnvioComerica(rs.getString("tipo_envio_layout"));					
			campos.setIdChequeraBanamex(rs.getString("id_chequera"));
			campos.setAba(rs.getString("aba"));
			campos.setAbaSwift(rs.getString("swift_code"));
			campos.setAbaInter(rs.getString("aba_intermediario"));
			campos.setSwiftInter(rs.getString("swift_intermediario"));
			campos.setBancoInter(rs.getString("nom_banco_intermediario"));
			campos.setAbaCorres(rs.getString("aba_corresponsal"));
			campos.setSwiftCorres(rs.getString("swift_corresponsal"));
			campos.setBancoCorres(rs.getString("nom_banco_corresponsal"));
			campos.setIdTipoOperacion(rs.getString("id_tipo_operacion"));
			campos.setNomEmpresa(rs.getString("nom_empresa"));
			campos.setNoEmpresa(rs.getInt("no_empresa"));
			campos.setEquivalePersona(rs.getString("equivale_persona"));
			campos.setNoFactura(rs.getString("no_factura"));
			campos.setIdServicioBE(rs.getString("id_servicio_be"));
			campos.setIdBancoCity(rs.getInt("id_banco_city"));
			campos.setRfcBenef(rs.getString("rfc_benef"));
			campos.setFecValorOriginal(rs.getString("fec_valor_original"));
			campos.setRfc(rs.getString("rfc"));
			campos.setContratoWlink(rs.getString("id_contrato_wlink"));
			campos.setUsuarioBital(rs.getString("desc_usuario_bital"));
			campos.setServicioBital(rs.getString("desc_servicio_bital"));
			campos.setEspeciales(rs.getString("especiales"));
			campos.setComplemento(rs.getString("complemento"));
			campos.setTipoEnvioLayoutCtas(rs.getString("tipo_envio_layout_ctas"));
			//campos.setTipoEnvioLayout(rs.getString("tipo_envio_layout"));
			campos.setPaisBenef(rs.getString("pais_benef"));
			campos.setDireccionBenef(rs.getString("direccion_benef"));
			campos.setTelefonoBenef(rs.getString("complemento"));
			campos.setReferenciaBan(rs.getString("referencia_ban"));
			campos.setIdBanco(rs.getInt("id_banco"));
			campos.setFecHoy(consultasGenerales.obtenerFechaHoy().toString());					
			campos.setHora(funciones.obtenerHoraActual(false));
			campos.setOrigenMov(rs.getString("origen_mov"));
			campos.setRuta(rs.getInt("ruta"));
			
			/*Se realizaron las validaciones desde aqui para que se vaya la informacion
			 * directo al grid
			 */
			
			if (campos.getIdDivisa().equals("DLS"))
				psDestino = "ESTADOS UNIDOS";
				
			psReferenciaTraspaso = obtieneReferenciaDet("T");
			psReferencia = psReferenciaTraspaso;
			psReferenciaTraspaso = obtieneReferencia(campos.getIdChequera(), campos.getIdBanco(),
			         								campos.getIdChequeraBenef(), campos.getIdBancoBenef(),
			         								psReferencia, campos.getNoEmpresa());
			
			if (campos.getIdBanco() == 1026 && !campos.getIdClabeBenef().equals("")){
				campos.setIdChequeraBenefReal(rs.getString("clabe_benef"));
			}
			else{
				campos.setIdChequeraBenefReal(obtieneChequeraBenefOriginal(campos.getIdBancoBenef(), campos.getSucursal(), 
											  campos.getPlazaBenef(), campos.getIdChequeraBenef(), campos.getIdBanco()));
			}
			
			if (campos.getIdBanco() == 2)
				campos.setIdChequeraBanamex(funciones.ajustarLongitudCampo(campos.getIdChequera().substring(0, 5), 11, "D", "", "0"));
			else
				campos.setIdChequeraBanamex("");

			//Campos para DLS
			if (!campos.getAba().equals("")){
				campos.setIdAbaSwift("FW");
				campos.setAbaSwift(campos.getAba().trim());
			}
			else{
				campos.setIdAbaSwift("IS");
				campos.setAbaSwift(campos.getAbaSwift().trim());
			}
			
			if (!campos.getAbaInter().equals("")){
				campos.setIdAbaSwiftInter("FW");
				campos.setAbaSwiftInter(campos.getAbaInter());
			}
			else if(!campos.getSwiftInter().equals("")){
				campos.setIdAbaSwiftInter("IS");
				campos.setAbaSwiftInter(campos.getSwiftInter());
			}
				
			if (!campos.getAbaCorres().equals("")){
				campos.setIdAbaSwiftCorres("//FW=" + campos.getAbaCorres().trim());
				campos.setAbaSwiftCorres("");
			}
			else if(!campos.getSwiftCorres().equals("")){
				campos.setIdAbaSwiftCorres("IS=" + campos.getIdAbaSwiftCorres().trim());
				campos.setAbaSwiftCorres("");
			}
			
			if (Integer.parseInt(campos.getIdTipoOperacion()) >= 3700 && Integer.parseInt(campos.getIdTipoOperacion()) <= 3899 && 
				bTraspasos == false){
				bTraspasos = true;
			}
			
			if (bTraspasos){
				if (campos.getIdBanco() == 1026)
					campos.setFolioDetReferencia(psReferenciaTraspaso + campos.getNoFolioDet());
				else
					campos.setFolioDetReferencia(psReferenciaTraspaso);
			}
			else
				campos.setFolioDetReferencia(campos.getNoFolioDet() + "");
													
			
			if (!campos.getAba().equals("") && campos.getIdBanco() == 12)
				campos.setEspeciales(psDestino);
			else
				campos.setEspeciales(campos.getEspeciales());
			
			campos.setEstatusEnArchivo("");
			if (nomArchivo.substring(0, 4).equalsIgnoreCase("tran")) {
				String renglon = leerDetalleArchivo(nomArchivo, 
						String.valueOf(campos.getNoFolioDet()), rs.getInt("ruta"));
				campos.setEstatusEnArchivo(
						obtenerEstatusRegistro(
								renglon.substring(400, 402)));
				if (!renglon.substring(400, 402).equals("00")) {
					campos.setColor("color:#E81624");
				}
			}
			
			return campos;
		}
	}
	
	//Revividor 
	@Override
	public Map<String, Object> ejecutarRevividor(Map<String, String> dtoRev, boolean brevividor) {
		int formaPago=0;//forma de pago
		String revividor="";
		String tipoCancelacion="R";
		int tipoOperacion=0;
		ArchTransferDto dto=new  ArchTransferDto();
		String resultadoRevividor = "";		
		int result = 0;
		Map<String, Object> resultado = new HashMap<String, Object>();
		System.out.println("Entra a ejecuta revividor");
		consultasGenerales = new ConsultasGenerales(jdbcTemplate);	
		try{
			
			int noFolioDet=Integer.parseInt(dtoRev.get("noFolioDet").toString());
			MovimientoDto movimiento= cancelar_revivir(noFolioDet+"");
			if(movimiento!=null){
				dto.setNoFolioDet(noFolioDet);
				if(brevividor){
					tipoCancelacion="R";
					revividor="S";
					tipoOperacion=movimiento.getIdTipoOperacion();
				}else{
					tipoCancelacion="C";
					revividor="N";
					tipoOperacion=movimiento.getIdTipoOperacion();
				}
			}
			//String archivo= dtoRev.get("nomArchivo").toString();
			String noDocumento=dtoRev.get("noDocto").toString();
			if(noDocumento==null || noDocumento.equals("null"))
				noDocumento="";
			movimiento.setUsuarioAlta(Integer.parseInt(dtoRev.get("usuario").toString()));
			dto.setFecTransferencia(dtoRev.get("fecTransferencia").toString());
			result=consultasGenerales.ejecutarRevividorOR(revividor, movimiento.getNoFolioDet(),  tipoOperacion,
					tipoCancelacion,  movimiento.getIdEstatusMov(), movimiento.getOrigenMov(), formaPago, 
					"", "E", movimiento.getImporte(), movimiento.getNoEmpresa(), 
					movimiento.getNoCuenta(), movimiento.getIdChequera(), movimiento.getIdBanco(), movimiento.getUsuarioAlta(), 
					movimiento.getNoDocto() + "", 0, "S", dto.getFecTransferencia(), 
					movimiento.getIdDivisa(), resultadoRevividor, false);		
			
			if(result==0)				
				resultado.put("result","0");		
			else				
				resultado.put("result","1");
								
			
		}
		catch (Exception e) {
			System.out.println("error en el revividor");
			bitacora.insertarRegistro(new Date().toString()+ " "+ e.toString()
			+  "P:BancaElectronica, C:ControlArchivosDaoImpl, M: ejecutarRevividor");
			e.printStackTrace();
		}
		return resultado;
	}
	
	//fin revividor
	public MovimientoDto cancelar_revivir(String noFolioDet){
		System.out.println("EN cancelar_revivir");
		StringBuffer sql = new StringBuffer();
		try{
			sql.append("SELECT m.id_estatus_mov,m.importe,m.origen_mov,m.no_folio_det,m.id_tipo_operacion,m.id_forma_pago, \n");
			sql.append("m.b_entregado,m.id_tipo_movto,m.no_empresa,m.no_cuenta,m.id_chequera,id_banco,no_docto, \n" );
			sql.append("lote_entrada,b_salvo_buen_cobro,fec_conf_trans,id_divisa \n");
			sql.append("FROM movimiento m \n");
			sql.append(" WHERE \n");
			sql.append("id_estatus_mov <> 'X' \n");
			sql.append("and ((id_tipo_operacion between 3700 and 3799");
			sql.append("and (folio_ref = " + Utilerias.validarCadenaSQL(noFolioDet) + " or no_folio_det =  " + Utilerias.validarCadenaSQL(noFolioDet) + ") And id_tipo_movto = 'E') \n");
			sql.append("or (id_tipo_operacion = 3200 \n");
			sql.append("and no_folio_det = " + Utilerias.validarCadenaSQL(noFolioDet) + ")");
			sql.append("or (id_tipo_operacion between 3700 and 3799 \n");
			sql.append("and no_folio_det = " + Utilerias.validarCadenaSQL(noFolioDet) + " and grupo_pago <> 0)) ");
			System.out.println("SQL cancelar_revivir"+sql.toString());
					
			List<MovimientoDto> list = jdbcTemplate.query(sql.toString(), new RowMapper<MovimientoDto>(){
				public MovimientoDto mapRow(ResultSet rs, int idx)throws SQLException{
							System.out.println(idx);
							MovimientoDto dto =new MovimientoDto();
							dto.setIdEstatusMov(rs.getString("id_estatus_mov"));
							dto.setImporte(Double.parseDouble(rs.getString("importe")));
							dto.setOrigenMov(rs.getString("origen_mov"));
							dto.setNoFolioDet(Integer.parseInt(rs.getString("no_folio_det")));
							dto.setIdTipoOperacion(Integer.parseInt(rs.getString("id_tipo_operacion")));
							dto.setIdFormaPago(Integer.parseInt(rs.getString("id_forma_pago")));
							dto.setLoteSalida(Integer.parseInt(rs.getString("lote_entrada")));
							dto.setBSalvoBuenCobro(rs.getString("b_salvo_buen_cobro"));
							dto.setNoEmpresa(Integer.parseInt(rs.getString("no_empresa")));
							dto.setNoCuenta(Integer.parseInt(rs.getString("no_cuenta")));
							dto.setIdChequera(rs.getString("id_chequera"));
							dto.setIdBanco(Integer.parseInt(rs.getString("id_banco")));
							dto.setNoDocto(rs.getString("no_docto"));
							dto.setIdDivisa(rs.getString("id_divisa"));
							return dto;
						}
 					});
			return list.get(0);
		}
		catch(Exception e){
			System.out.println("ERROR EN cancelar_revivir");
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: BancaElectronica, C: ControlArchivosTransferidosDaoImpl, M: llenaComboArchivos");
		}return null;
	}
}