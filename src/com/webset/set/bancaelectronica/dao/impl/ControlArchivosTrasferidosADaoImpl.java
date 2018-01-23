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

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import com.webset.set.bancaelectronica.dao.ControlArchivosTransferidosADao;
import com.webset.set.bancaelectronica.dto.ArchTransferDto;
import com.webset.set.utilerias.dto.*;
import com.webset.set.utilerias.Bitacora;
import com.webset.set.utilerias.ConsultasGenerales;
import com.webset.set.utilerias.Funciones;
import com.webset.utils.tools.Utilerias;

public class ControlArchivosTrasferidosADaoImpl implements ControlArchivosTransferidosADao{
	JdbcTemplate jdbcTemplate;
	Bitacora bitacora;
	ConsultasGenerales consultasGenerales;
	Funciones funciones = new Funciones();
	
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
		System.out.println("asdfdsasdsasdsasdsa---------------");
		try{
			for (int i = 0; i < tablas.length; i++) { 			
			sql.append("Select a.id_banco, c.desc_banco, a.nom_arch, convert(char(8),a.fec_trans,3) as fec_trans,convert(char(8),a.fec_retrans,3) as fec_retrans, ");
			sql.append("\n round(a.importe, 2), a.registros, u.nombre + ' ' + u.apellido_paterno + ' ' + u.apellido_materno as usuario ,");
			sql.append("\n a.no_usuario_alta, coalesce(um.nombre + ' ' + um.apellido_paterno + ' ' + um.apellido_materno, '') as usuario_modif, ");
			sql.append("\n coalesce(a.no_usuario_modif, 0) as no_usuario_modif, ");
			sql.append("\n (select coalesce(case when id_tipo_operacion = 3200 then 200 else 3006 end, 0) ");
			
			sql.append("\n from " + tablas[i] +" where no_folio_det in (  ");
			//Sustituido el 30.01.16 para optimizar codigo
		  //sql.append("\n select no_folio_det from det_arch_transfer dat where dat.nom_arch = a.nom_arch and rownum =1)) as ruta ");*/
			sql.append("\n select top 1 no_folio_det from det_arch_transfer dat join arch_transfer at on dat.nom_arch = at.nom_arch  ) )as ruta");
			sql.append("\n from arch_transfer a left join seg_usuario um on (a.no_usuario_modif = um.id_usuario), ");
			sql.append("\n 		cat_banco c, seg_usuario u ");
			sql.append("\n where ");
			switch (obj.getTipoCriterio()){
			case 0: //Busca por fechas
				if (!obj.getFecInicial().equals("") && !obj.getFecFinal().equals("")){
					sql.append("\n a.fec_trans between convert(datetime,'"+ Utilerias.validarCadenaSQL(obj.getFecInicial()) +"',103) and convert(datetime,'"+ Utilerias.validarCadenaSQL(obj.getFecFinal()) +"',103) ");					
				}
				else 
					sql.append("\n a.fec_trans = convert(datetime'"+ Utilerias.validarCadenaSQL(obj.getFecInicial()) +"',103) or a.fec_retrans = convert(datetime,'"+ Utilerias.validarCadenaSQL(obj.getFecInicial()) +"',103) ");
				
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
						sql1.append("Select top 1 a.id_banco,c.desc_banco,a.nom_arch,convert(char,a.fec_trans,3) as fec_trans, convert(char,a.fec_retrans,3) as fec_retrans,round(a.importe, 2),");
						sql1.append("\n a.registros,u.nombre + ' ' + u.apellido_paterno + ' ' + u.apellido_materno as usuario, ");
						sql1.append("\n a.no_usuario_alta, coalesce(um.nombre + ' ' + um.apellido_paterno + ' ' + um.apellido_materno, '') as usuario_modif, ");
						sql1.append("\n coalesce(a.no_usuario_modif, 0) as no_usuario_modif, ");
						sql1.append("\n 	(select coalesce(case when id_tipo_operacion = 3200 then 200 else 3006 end, 0) ");
						sql1.append("\n 	 from "+tablas[d]+" where  no_folio_det in ( ");
						//Sustituido el 30.01.16 para optimizar codigo
					  //sql.append("\n 		select no_folio_det from det_arch_transfer dat where dat.nom_arch = a.nom_arch)) as ruta ");
						sql1.append("\n 		select top 1 no_folio_det from det_arch_transfer dat join arch_transfer at on dat.nom_arch = at.nom_arch  ) )as ruta");
						
						
						sql1.append("\n From arch_transfer a left join seg_usuario um on (a.no_usuario_modif = um.id_usuario), cat_banco c, det_arch_transfer d, seg_usuario u ");
						sql1.append("\n Where a.id_banco = c.id_banco");
						sql1.append("\n And a.no_usuario_alta = u.id_usuario ");
	//					sql.append("\n and (a.no_usuario_modif = um.id_usuario or a.no_usuario_modif is null) ");
						sql1.append("\n And a.nom_arch = d.nom_arch");
						sql1.append("\n And d.no_docto = '"+ Utilerias.validarCadenaSQL(obj.getNoDocto()) +"'");
						
						sql1.append("\n and a.id_banco = c.id_banco ");
						sql1.append("\n and a.no_usuario_alta = u.id_usuario ");
//						sql.append("\n and (a.no_usuario_modif = um.id_usuario or a.no_usuario_modif is null) ");
						//sql.append("\n and u.id_usuario = um.id_usuario ");
						
						if (!obj.getBChequeOcurre().equals(""))
							sql1.append("\n and a.b_cheque_ocurre = 'S'");
						else
							sql1.append("\n and (a.b_cheque_ocurre <> 'S' or a.b_cheque_ocurre is null)");
						//if(tablas[d].equals("movimiento"))
							//sql1.append("\n union all \n");
					}
					i++;
				}
				break;
			}
			
			sql.append("\n and a.id_banco = c.id_banco ");
			sql.append("\n and a.no_usuario_alta = u.id_usuario ");
//			sql.append("\n and (a.no_usuario_modif = um.id_usuario or a.no_usuario_modif is null) ");
			//sql.append("\n and u.id_usuario = um.id_usuario ");
			
			if (!obj.getBChequeOcurre().equals(""))
				sql.append("\n and a.b_cheque_ocurre = 'S'");
			else
				sql.append("\n and (a.b_cheque_ocurre <> 'S' or a.b_cheque_ocurre is null)");
				
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
						campos.setEspeciales(obtenerEstatusArchivo(leerIdMensajeEstatus(campos.getNomArch(), rs.getInt("ruta"))));
						if (!leerIdMensajeEstatus(campos.getNomArch(), rs.getInt("ruta")).equals("00")) {
							campos.setColor("color:#E81624");
						}
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
			sql.append("\n FROM (select id_tipo_operacion from movimiento");// where no_folio_det in ( ");
			
			sql.append("\n Where Po_Headers In");
			sql.append("\n   (Select Po_Headers From Det_Arch_Transfer_Agrup Where Nom_Arch In('"+ Utilerias.validarCadenaSQL(nomArchivo) +"'))");
			sql.append("\n	   And No_Empresa = (Select top 1 No_Empresa From Det_Arch_Transfer_Agrup Where Nom_Arch In('"+ Utilerias.validarCadenaSQL(nomArchivo) +"') )");
			sql.append("\n   And Id_Banco = (Select top 1 Id_Banco From Det_Arch_Transfer_Agrup Where Nom_Arch In('"+ Utilerias.validarCadenaSQL(nomArchivo) +"') )");
			sql.append("\n   And Id_Chequera = (Select top 1 Id_Chequera From Det_Arch_Transfer_Agrup Where Nom_Arch In('"+ Utilerias.validarCadenaSQL(nomArchivo) +"') )");
			sql.append("\n   and fec_propuesta = (Select top 1 Fec_Propuesta From Det_Arch_Transfer_Agrup Where Nom_Arch In('"+ Utilerias.validarCadenaSQL(nomArchivo) +"') )");

			sql.append("\n Union All ");
			
			sql.append("\n select id_tipo_operacion from hist_movimiento");// where no_folio_det in ( ");
			sql.append("\n Where Po_Headers In");
			sql.append("\n   (Select Po_Headers From Det_Arch_Transfer_Agrup Where Nom_Arch In('"+ Utilerias.validarCadenaSQL(nomArchivo) +"'))");
			sql.append("\n	   And No_Empresa = (Select top 1 No_Empresa From Det_Arch_Transfer_Agrup Where Nom_Arch In('"+ Utilerias.validarCadenaSQL(nomArchivo) +"') )");
			sql.append("\n   And Id_Banco = (Select top 1 Id_Banco From Det_Arch_Transfer_Agrup Where Nom_Arch In('"+ Utilerias.validarCadenaSQL(nomArchivo) +"') )");
			sql.append("\n   And Id_Chequera = (Select top 1 Id_Chequera From Det_Arch_Transfer_Agrup Where Nom_Arch In('"+ Utilerias.validarCadenaSQL(nomArchivo) +"') )");
			sql.append("\n   and fec_propuesta = (Select top 1 Fec_Propuesta From Det_Arch_Transfer_Agrup Where Nom_Arch In('"+ Utilerias.validarCadenaSQL(nomArchivo) +"') )");

			sql.append("\n Union All ");
			
			sql.append("\n select id_tipo_operacion from hist_solicitud ");//where no_folio_det in ( ");
			
			sql.append("\n Where Po_Headers In");
			sql.append("\n   (Select Po_Headers From Det_Arch_Transfer_Agrup Where Nom_Arch In('"+ Utilerias.validarCadenaSQL(nomArchivo) +"'))");
			sql.append("\n	   And No_Empresa = (Select top 1 No_Empresa From Det_Arch_Transfer_Agrup Where Nom_Arch In('"+ Utilerias.validarCadenaSQL(nomArchivo) +"') )");
			sql.append("\n   And Id_Banco = (Select top 1 Id_Banco From Det_Arch_Transfer_Agrup Where Nom_Arch In('"+ Utilerias.validarCadenaSQL(nomArchivo) +"') )");
			sql.append("\n   And Id_Chequera = (Select top 1 Id_Chequera From Det_Arch_Transfer_Agrup Where Nom_Arch In('"+ Utilerias.validarCadenaSQL(nomArchivo) +"') )");
			sql.append("\n   and fec_propuesta = (Select top 1 Fec_Propuesta From Det_Arch_Transfer_Agrup Where Nom_Arch In('"+ Utilerias.validarCadenaSQL(nomArchivo) +"') )");

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
		StringBuffer sql = new StringBuffer();
		boolean manejaBital = false;
		
		try{
			
			if (tipoOperacion.equals("TRANSFERENCIAS")){
		    	
		    	sql.append("\n	Select Po_Headers,max(Fec_Valor_Original) as Fec_Valor_Original,Id_Tipo_Operacion,Id_Banco_City,Inst_Finan,Observacion,Suc_Origen,Suc_Destino, ");
				sql.append("\n	  Id_Banco,B_Layout_Comerica,No_Docto,max(Fec_Operacion) as Fec_Operacion,Desc_Cve_Operacion,sum (Importe) as importe,Id_Divisa,Id_Chequera_Benef, ");
				sql.append("\n	  Origen_Mov,max(Division) as Division,Nombre_Banco_Benef,Desc_Banco_Benef,Beneficiario,Concepto,No_Folio_Det,No_Empresa, ");
				sql.append("\n	  Id_Chequera,max(Fec_Valor) as Fec_Valor,Id_Banco_Benef,Plaza_Benef,Plaza,No_Cliente,Desc_Banco_Inbursa,Clabe_Benef,Aba, ");
				sql.append("\n	  Swift_Code,Aba_Intermediario,Swift_Intermediario,Aba_Corresponsal,Swift_Corresponsal,Nom_Banco_Intermediario, ");
				sql.append("\n	  Nom_Banco_Corresponsal,Valida_Clabe,Id_Contrato_Mass,Id_Contrato_Wlink,Rfc_Benef,No_Factura,Nom_Empresa, ");
				sql.append("\n	  Existe_Chequera_Prov,Rfc,Especiales,Equivale_Persona,Complemento,Tipo_Envio_Layout,Divisa_Chequera,direccion_benef,pais_benef, ");
				sql.append("\n	  clave,NO_FOLIO_MOV, id_servicio_be from( ");

				sql.append("\n	Select m.po_headers,M.fec_propuesta as fec_valor_original, M.Id_Tipo_Operacion,B2.Id_Banco_City,B2.Inst_Finan,Coalesce(M.Observacion,'') As Observacion,C.Desc_Sucursal As Suc_Origen, ");
				sql.append("\n	Coalesce(Ctas.Sucursal, 0) As Suc_Destino, M.Id_Banco,Coalesce (B1.B_Layout_Comerica, '') As B_Layout_Comerica, ");
				sql.append("\n	m.Po_Headers As No_Docto, ");
				sql.append("\n	M.fec_propuesta as Fec_Operacion,O.Desc_Cve_Operacion, ");
				sql.append("\n	CASE WHEN coalesce(b1.transfer_casa_cambio, 'N') = 'S' and m.origen_mov='CVT' "); 
				sql.append("\n		THEN m.importe ");
				sql.append("\n		Else M.Importe End As Importe, "); 
				sql.append("\n	CASE WHEN coalesce(b1.transfer_casa_cambio, 'N') = 'S' and m.origen_mov='CVT' "); 
				sql.append("\n		 Then M.Id_Divisa_Original ");
				sql.append("\n       Else M.Id_Divisa End As Id_Divisa, "); 
				sql.append("\n 	M.Id_Chequera_Benef,M.Origen_Mov, M.Division, ");
				sql.append("\n  B2.Desc_Banco As Nombre_Banco_Benef,B2.Desc_Banco As Desc_Banco_Benef, ");
				sql.append("\n  Case When ");
				sql.append("\n		 ( SELECT coalesce(b.transfer_casa_cambio, 'N') "); 
				sql.append("\n		   FROM cat_banco b ");
				sql.append("\n		   WHERE b.id_banco = m.id_banco ) = 'S' and m.origen_mov='CVT' "); 
				sql.append("\n  THEN ( SELECT coalesce(pp.razon_social, '') ");
				sql.append("\n	      FROM persona pp ");
				sql.append("\n	      WHERE pp.no_persona = m.no_cliente "); 
				sql.append("\n	           And Pp.Id_Tipo_Persona = 'P' ) ");
				sql.append("\n	     Else M.Beneficiario End As Beneficiario, "); 
				sql.append("\n	'pago' as concepto,m.po_headers as no_folio_det,m.no_empresa, "); 
				sql.append("\n 	m.id_chequera,m.fec_propuesta as fec_valor,m.id_banco_benef, ");
				sql.append("\n 	Coalesce(Ctas.Plaza, 0) As Plaza_Benef, C.Desc_Plaza As Plaza, ");
				sql.append("\n 	M.No_Cliente, B2.Desc_Banco_Inbursa, Ctas.Id_Clabe As Clabe_Benef ");
				sql.append("\n 	,coalesce(ctas.aba, '') as aba, coalesce(ctas.swift_code, '') as swift_code "); 
				sql.append("\n 	,coalesce(ctas.aba_intermediario, '') as aba_intermediario, coalesce(ctas.swift_intermediario, '') as swift_intermediario "); 
				sql.append("\n 	,coalesce(ctas.aba_corresponsal, '') as aba_corresponsal, coalesce(ctas.swift_corresponsal, '') as swift_corresponsal ");
				sql.append("\n 	, ' ' As Nom_Banco_Intermediario, ' ' As Nom_Banco_Corresponsal ");
				sql.append("\n 	,B1.Valida_Clabe,E.Id_Contrato_Mass,E.Id_Contrato_Wlink, P.Rfc As Rfc_Benef "); 
				sql.append("\n  ,M.po_headers as No_Factura,E.Nom_Empresa, 'S' As Existe_Chequera_Prov, Pp.Rfc ");
				sql.append("\n 	,Ctas.Especiales, P.Equivale_Persona, Ctas.Complemento, c.id_divisa As Divisa_Chequera, ");
				sql.append("\n 	D.Ciudad +  ', ' +  D.Id_Estado As Direccion_Benef , 'MX' As Pais_Benef, M.Cve_Control As Clave, ");
				sql.append("\n 	m.po_headers as no_folio_mov, '' As Tipo_Envio_Layout,coalesce(m.id_servicio_be, '') as id_servicio_be  ");



				sql.append("\n 	From Det_Arch_Transfer_Agrup Datap Join "+ nombreTabla +" M On(Datap.Po_Headers = M.Po_Headers And");
				sql.append("\n					  										Datap.Id_Banco = M.Id_Banco And");
				sql.append("\n					  										Datap.Id_Chequera = M.Id_Chequera)");
				sql.append("\n					  Join Cat_Banco B2 On ( M.Id_Banco_Benef = B2.Id_Banco ) "); 
				sql.append("\n 	                  Join Cat_Banco B1 On ( M.Id_Banco = B1.Id_Banco ) ");
				sql.append("\n 	                  Join Cat_Cta_Banco C On (M.Id_Chequera = C.Id_Chequera And ");
				sql.append("\n 	                                          M.Id_Banco = C.Id_Banco )");
				//sql.append("\n 	                                          M.No_Empresa = C.No_Empresa) ");
				sql.append("\n 	                  Join Ctas_Banco Ctas On (M.Id_Banco_Benef = Ctas.Id_Banco And ");
				sql.append("\n 	                                          M.Id_Chequera_Benef = Ctas.Id_Chequera And ");
				sql.append("\n 	                                          M.No_Cliente = No_Persona) ");
				sql.append("\n 	                  Join Cat_Cve_Operacion O On (M.Id_Cve_Operacion = O.Id_Cve_Operacion) ");
				sql.append("\n 	                  Join Empresa E On (M.No_Empresa = E.No_Empresa) ");
				sql.append("\n 	                  Join Persona P On (Cast(M.No_Cliente As Integer) = P.No_Persona ) ");
				sql.append("\n 	                  Join Persona Pp On (M.No_Empresa = Pp.No_Empresa ");
				sql.append("\n 	                                      And M.No_Empresa = Pp.No_Persona) ");
				sql.append("\n 	                  Join Direccion D On (Cast(M.No_Cliente As Integer) = D.No_Persona) ");                    
			
				
				sql.append("\n Where ");
				sql.append("\n  Datap.nom_arch = '"+ Utilerias.validarCadenaSQL(nomArchivo) +"'");
				sql.append("\n  and M.Id_Tipo_Movto = 'E'");
				sql.append("\n  And M.Id_Forma_Pago = 3");
				sql.append("\n  AND m.no_cliente not in (" +consultarConfiguraSet(206)+")");
				sql.append("\n  And M.Origen_Mov <> 'FIL'");
				sql.append("\n  And M.Id_Estatus_Mov = 'T'");
				sql.append("\n  And (M.Po_Headers Is Not Null  Or M.Po_Headers <> '')");
	            sql.append("\n  and p.id_tipo_persona in('P','K') ");
	            sql.append("\n  AND b1.b_banca_elect  in ('A','E')  ");
	            sql.append("\n  and pp.id_tipo_persona = 'E'");
	            sql.append("\n  and d.id_tipo_direccion = 'OFNA' ");
	            sql.append("\n  and d.no_direccion = 1 ");
	            sql.append("\n  and d.id_tipo_persona = 'P' ");
	            sql.append("\n  and d.no_empresa = 552 ");

	            sql.append("\n ) X");
	            sql.append("\n Group By po_headers,Id_Tipo_Operacion,Id_Banco_City,Inst_Finan,Observacion,Suc_Origen,Suc_Destino,");
	    		sql.append("\n Id_Banco,B_Layout_Comerica,No_Docto,Desc_Cve_Operacion,Id_Divisa,Id_Chequera_Benef,");
				sql.append("\n Origen_Mov,Nombre_Banco_Benef,Desc_Banco_Benef,Beneficiario,Concepto,No_Folio_Det,No_Empresa,");
				sql.append("\n Id_Chequera,Id_Banco_Benef,Plaza_Benef,Plaza,No_Cliente,Desc_Banco_Inbursa,Clabe_Benef,Aba,");
				sql.append("\n Swift_Code,Aba_Intermediario,Swift_Intermediario,Aba_Corresponsal,Swift_Corresponsal,Nom_Banco_Intermediario,");
				sql.append("\n Nom_Banco_Corresponsal,Valida_Clabe,Id_Contrato_Mass,Id_Contrato_Wlink,Rfc_Benef,No_Factura,Nom_Empresa,");
				sql.append("\n Existe_Chequera_Prov,Rfc,Especiales,Equivale_Persona,Complemento,Tipo_Envio_Layout,Divisa_Chequera,direccion_benef,pais_benef,");
				sql.append("\n clave,NO_FOLIO_MOV,id_servicio_be ");

	            
	            
	            
	            
			    System.out.println("\n consulta"+sql);	
				
						
			}else{
				//Para traspasos
				
					/*sql = "SELECT to_char(to_date(m.fec_valor_original,'dd/mm/yy')) as fec_valor_original, coalesce(b2.id_banco_city, '0') as id_banco_city, coalesce(m.id_servicio_be, '') as id_servicio_be, m.id_forma_pago, m.id_tipo_operacion, ";
					sql += "\n m.id_banco, m.observacion, b.b_layout_comerica, cc2.desc_plaza as plaza_benef, cc.desc_plaza as plaza,";
					sql += "\n b2.inst_finan, m.b_entregado, m.id_estatus_mov, m.no_cliente, d.no_docto, c.desc_estatus, to_char(to_date(d.fec_valor,'dd/mm/yy')) as fec_valor, b.desc_banco,";
					sql += "\n d.id_chequera, d.id_chequera_benef, d.importe,";
					sql += "\n d.beneficiario, cc.desc_sucursal as sucursal_origen, ";
					sql += "\n cc2.desc_sucursal as sucursal_destino, m.concepto , d.no_folio_det, to_char(to_date(m.fec_operacion,'dd/mm/yy')) as fec_operacion, m.id_divisa, m.id_banco_benef,";
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
					sql += "\n dir.ciudad || ', ' || dir.id_estado as direccion_benef , ";
					sql += "\n (select pa.desc_pais from cat_pais pa where pa.id_pais = 'MX') as pais_benef, '' as referencia_ban, 3006 as ruta ";
					sql += "\n FROM det_arch_transfer d, cat_estatus c, cat_banco b,cat_banco b2, ";
					sql += "\n "+ nombreTabla +" m, empresa e, ";
					
					if (manejaBital)
						sql += "\n cat_usuario_bital u, cat_servicio_bital s, ";
					
					sql += "\n persona p, cat_cta_banco cc, arch_transfer t,cat_cta_banco cc2, ";
					sql += "\n persona pp, direccion dir ";
					sql += "\n Where m.no_empresa = e.no_empresa ";
					
					if (manejaBital)
					{
						sql += "\n and e.id_usuario_bital *= u.id_usuario_bital";
						sql += "\n and e.id_servicio_bital *= s.id_servicio_bital ";
					}
					
					sql += "\n AND cast(m.no_cliente as integer) = p.no_empresa ";
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
					sql += "\n  AND id_estatus_arch <> 'X'";*/
				}				
			
		}
		catch(Exception e){
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: BancaElectronica, C: ControlArchivosTransferidosDaoImpl, M: armaQueryDetalle");
		}
		return sql+"";
	}
	public String consultarConfiguraSet(int indice){
		consultasGenerales= new ConsultasGenerales(jdbcTemplate);
		return consultasGenerales.consultarConfiguraSet(indice);
	}
	
	public List<MovimientoDto> llenaGridDetalle(String nomArchivo, int idBanco, boolean enviadasHoy, String tipoOperacion){
		List<MovimientoDto> listaResultado = new ArrayList<MovimientoDto>();	
		StringBuffer sql = new StringBuffer();
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
				
			listaResultado = jdbcTemplate.query(sql.toString(), new RowMapper<MovimientoDto>(){
				public MovimientoDto mapRow(ResultSet rs, int idx)throws SQLException{
					MovimientoDto cons = new MovimientoDto();
					cons.setFecValorOriginal(rs.getDate("fec_valor_original"));
					cons.setIdTipoOperacion(rs.getInt("id_tipo_operacion"));
					cons.setIdBancoCityStr(rs.getString("id_banco_city"));
					cons.setInstFinan(rs.getString("inst_finan"));
					cons.setObservacion(rs.getString("observacion"));
					cons.setSucOrigen(rs.getString("suc_origen"));
					cons.setSucDestino(rs.getString("suc_destino"));
					cons.setIdBanco(rs.getInt("id_banco"));
					cons.setBLayoutComerica(rs.getString("b_layout_comerica"));
					cons.setNoDocto(rs.getString("no_docto"));
					cons.setFecOperacion(rs.getDate("fec_operacion"));
					cons.setDescCveOperacion(rs.getString("desc_cve_operacion"));
					cons.setImporte(rs.getDouble("importe")); 
					cons.setIdDivisa(rs.getString("id_divisa"));
					cons.setIdChequeraBenef(rs.getString("id_chequera_benef"));
					cons.setOrigenMov(rs.getString("origen_mov"));
					cons.setDivision(rs.getString("division"));
					cons.setNombreBancoBenef(rs.getString("nombre_banco_benef"));
					cons.setDescBancoBenef(rs.getString("desc_banco_benef"));
					cons.setBeneficiario(rs.getString("beneficiario"));
					cons.setConcepto(rs.getString("concepto"));
					cons.setPoHeaders(rs.getString("no_folio_det")); 
					cons.setNoEmpresa(rs.getInt("no_empresa"));
					cons.setIdChequera(rs.getString("id_chequera"));
					cons.setFecValor(rs.getDate("fec_valor")); 
					cons.setIdBancoBenef(rs.getInt("id_banco_benef"));
					cons.setPlazaBenef(rs.getString("plaza_benef"));
					cons.setPlaza(rs.getInt("plaza"));
					cons.setNoCliente(rs.getString("no_cliente"));
					cons.setDescBancoInbursa(rs.getString("desc_banco_inbursa"));
					cons.setClabeBenef(rs.getString("clabe_benef"));
					
					cons.setAba(rs.getString("aba"));
					cons.setSwiftCode(rs.getString("swift_code"));
					cons.setAbaIntermediario(rs.getString("aba_intermediario"));
					cons.setSwiftIntermediario(rs.getString("swift_intermediario"));
					cons.setAbaCorresponsal(rs.getString("aba_corresponsal"));
					cons.setSwiftCorresponsal(rs.getString("swift_corresponsal"));
					cons.setNomBancoIntermediario(rs.getString("nom_banco_intermediario"));
					cons.setNomBancoCorresponsal(rs.getString("nom_banco_corresponsal"));
					cons.setValidaCLABE(rs.getString("valida_clabe"));
					cons.setIdContratoMass(rs.getInt("id_contrato_mass"));
					cons.setRfcBenef(rs.getString("rfc_benef"));
					cons.setNoFactura(rs.getString("no_factura"));
					cons.setNomEmpresa(rs.getString("nom_empresa"));
					cons.setEspeciales(rs.getString("especiales")); 
					cons.setEquivalePersona(rs.getString("equivale_persona"));
					cons.setComplemento(rs.getString("complemento"));
					cons.setIdContratoWlink(rs.getString("id_contrato_wlink"));
					cons.setExisteChequeraProv(rs.getString("existe_chequera_prov"));
					cons.setRfc(rs.getString("rfc"));
					cons.setTipoEnvioLayout(rs.getInt("tipo_envio_layout"));
					cons.setDivisaChequera(rs.getString("divisa_chequera"));
					cons.setDireccionBenef(rs.getString("Direccion_Benef"));
					cons.setIdServicioBe(rs.getString("id_servicio_be"));
					//System.out.println(rs.getString("Direccion_Benef"));
					cons.setPaisBenef(rs.getString("pais_benef"));
					cons.setClave(rs.getString("clave"));
					cons.setIdChequeraBenefReal(obtenerChequeraBenefOficial(rs.getInt("id_banco_benef"), rs.getString("suc_destino"), 
							rs.getString("plaza_benef"), rs.getString("id_chequera_benef"), rs.getInt("id_banco")));
					cons.setHoraRecibo(funciones.obtenerHoraActual(false).substring(0, 5));
					
					cons.setPoHeaders(rs.getString("no_folio_mov"));
					
				 	return cons;
				}
			});
		}
		catch(Exception e){
			e.printStackTrace();
			System.out.println("error en el dao impl llenagridDetalle");
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: BancaElectronica, C: ControlArchivosTransferidosDaoImpl, M: llenaGridDetalle");
		}return listaResultado;
	}
	
	public String obtenerChequeraBenefOficial(int idBancoBenef, String sucBenef, String plazaBenef, String idChequeraBenef, int idBanco) {
		String idChequeraBenefReal = "";
		
		try {
			if(sucBenef.length() > 4) sucBenef = sucBenef.substring(0, 3);
			if(plazaBenef.length() > 3) plazaBenef = plazaBenef.substring(0, 2);
			
			if(idBancoBenef == 2) {
				if(idBanco == 2)
					idChequeraBenefReal = funciones.ponerCeros(idChequeraBenef.substring(4), 11);
				else
					idChequeraBenefReal = idChequeraBenef;
			}else if(idBancoBenef == 12) {
				if(idChequeraBenef.length() == 11) {
					if(Integer.parseInt(idChequeraBenef.substring(0, 2)) == 0)
						idChequeraBenefReal = funciones.ponerCeros(plazaBenef, 3) + idChequeraBenef.substring(3);
					else
						idChequeraBenefReal = idChequeraBenef;
				}else
					idChequeraBenefReal = idChequeraBenef;
			}else
				idChequeraBenefReal = idChequeraBenef;
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:BancaElectr�nica, C:EnvioTransferenciasDao, M:obtenerChequeraBenefOficial");
		}
		return idChequeraBenefReal;
	}
	
	public List<ArchTransferDto> llenaGridDetalleCheque(String nomArchivo){
		List<ArchTransferDto> listaResultado = new ArrayList<ArchTransferDto>();
		StringBuffer sql = new StringBuffer();
		try{
			sql.append("Select e.nom_empresa,m.observacion,m.concepto,b.b_layout_comerica,m.id_estatus_mov, ");
			sql.append("\n m.b_entregado , m.no_docto, c.desc_estatus, m.fec_valor, d.nom_arch, d.id_banco, b.desc_banco,");
			sql.append("\n m.id_chequera , m.id_divisa, d.beneficiario, m.importe, m.no_folio_det, d.nom_arch, m.no_cliente ");
			sql.append("\n FROM det_arch_transfer d, movimiento m, cat_estatus c, cat_banco b,empresa e ");
			sql.append("\n where m.no_empresa = e.no_empresa ");
			sql.append("\n and m.no_folio_det = d.no_folio_det ");
			sql.append("\n and d.id_estatus_arch *= c.id_estatus and d.id_banco = b.id_banco ");
			sql.append("\n and d.nom_arch = '"+ Utilerias.validarCadenaSQL(nomArchivo) +"' ");
			sql.append("\n d.id_estatus_arch <> 'X' and c.clasificacion = 'TRN' ");
			sql.append("\n union ");
			sql.append("\n Select e.nom_empresa,m.observacion,m.concepto,b.b_layout_comerica,m.id_estatus_mov,m.b_entregado, ");
			sql.append("\n m.no_docto,c.desc_estatus,m.fec_valor, d.nom_arch,d.id_banco,b.desc_banco, ");
			sql.append("\n m.id_chequera , m.id_divisa, d.beneficiario, m.importe, m.no_folio_det, d.nom_arch, m.no_cliente ");
			sql.append("\n FROM det_arch_transfer d,hist_movimiento m, cat_estatus c, cat_banco b, empresa e ");
			sql.append("\n where m.no_empresa = e.no_empresa ");
			sql.append("\n and m.no_folio_det = d.no_folio_det ");
			sql.append("\n and d.id_estatus_arch *= c.id_estatus and d.id_banco = b.id_banco ");
			sql.append("\n and d.nom_arch = '"+ Utilerias.validarCadenaSQL(nomArchivo) +"' ");
			sql.append("\n d.id_estatus_arch <> 'X' and c.clasificacion = 'TRN' ");
			
			listaResultado = jdbcTemplate.query(sql.toString(), 
			new DetalleArchivo(nomArchivo) {
				public ArchTransferDto mapRow(ResultSet rs, int idx)throws SQLException{
					ArchTransferDto campos = new ArchTransferDto();
					campos.setNoDocto(rs.getString("no_docto"));
					campos.setDescEstatus(rs.getString("desc_estatus"));
					campos.setFecValor(rs.getString("fec_valor"));
					campos.setDescBanco(rs.getString("desc_banco"));
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
			System.out.println(sql.toString());
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
	
	public int actualizaEstatusAgrupapdo(String poHeaders, int banco, String chequera,int noEmpresa,String fecha, String estatusMov){
		int resultado = 0;
		StringBuffer sql = new StringBuffer();
		try{
			sql.append("update movimiento set id_estatus_mov = '"+ Utilerias.validarCadenaSQL(estatusMov) +"' ");
			sql.append("\n where po_headers = '"+poHeaders+"'");
			sql.append("\n and id_banco ="+ Utilerias.validarCadenaSQL(banco));
			sql.append("\n and id_chequera = '"+ Utilerias.validarCadenaSQL(chequera)+"'");
			sql.append("\n and no_empresa = "+ Utilerias.validarCadenaSQL(noEmpresa));
			sql.append("\n ");
			System.out.println(sql.toString());
			resultado = jdbcTemplate.update(sql.toString());
			
			if (resultado <= 0){
				sql = new StringBuffer();
				sql.append("update hist_movimiento set id_estatus_mov = '"+ Utilerias.validarCadenaSQL(estatusMov) +"' ");
				sql.append("\n where po_headers = '"+poHeaders+"'");
				sql.append("\n and id_banco ="+ Utilerias.validarCadenaSQL(banco));
				sql.append("\n and id_chequera = '"+ Utilerias.validarCadenaSQL(chequera)+"'");
				sql.append("\n and no_empresa = "+ Utilerias.validarCadenaSQL(noEmpresa));
				System.out.println(sql.toString());
				resultado = jdbcTemplate.update(sql.toString());
			}
			
		}
		catch(Exception e){
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) +
					"P:BancaElectronica, C: ControlArchivosTransferidosDaoImpl, M: actualizaEstatusAgrupapdo");
		}
		return resultado;
	}
	
	public int actualizaDetArchTransferAgrupado(String nomArchivo, String poHeaders, int banco, String chequera,int noEmpresa,String fecha){
		int respuesta = 0;
		StringBuffer sql = new StringBuffer();
		try{
			sql.append("update Det_Arch_Transfer_Agrup set id_estatus_arch = 'X' ");
			sql.append("\n where nom_arch = '"+ Utilerias.validarCadenaSQL(nomArchivo) +"' ");
			sql.append("\n and po_headers = '"+poHeaders+"'");
			sql.append("\n and id_banco ="+ Utilerias.validarCadenaSQL(banco));
			sql.append("\n and id_chequera = '"+ Utilerias.validarCadenaSQL(chequera)+"'");
			sql.append("\n and no_empresa = "+ Utilerias.validarCadenaSQL(noEmpresa));
			
			System.out.println(sql.toString());
			
			respuesta = jdbcTemplate.update(sql.toString());
		}
		catch(Exception e){
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: BancaElectronica, C: ControlArchivosTransferidosDaoImpl, M: actulizaDetArchTransfer");
		}
		return respuesta;
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
					"N", movimiento.getIdEstatusMov(), movimiento.getImporte(), movimiento.getNoEmpresa(), 
					movimiento.getNoCuenta(), movimiento.getIdChequera(), movimiento.getIdBanco(), movimiento.getUsuarioAlta(), 
					movimiento.getNoDocto() + "", 0, "S", dto.getFecTransferencia(), 
					movimiento.getIdDivisa(), resultadoRevividor, false);		
			
			if(result>0)				
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

	@Override
	public List<String> armaCadenaConexion() {
		// TODO Auto-generated method stub
		return null;
	}
}

