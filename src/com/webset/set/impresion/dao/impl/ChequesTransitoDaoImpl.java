package com.webset.set.impresion.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.jdbc.CannotGetJdbcConnectionException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import com.webset.set.impresion.dao.ChequesTransitoDao;
import com.webset.set.impresion.dto.ChequesTransitoDto;
import com.webset.set.utilerias.Bitacora;
import com.webset.set.utilerias.ConsultasGenerales;
import com.webset.set.utilerias.Funciones;
import com.webset.set.utilerias.dto.GeneradorDto;
import com.webset.set.utilerias.dto.LlenaComboGralDto;
import com.webset.set.utilerias.dto.ParametroDto;
import com.webset.utils.tools.Utilerias;

/**
 * 
 * @author YEC
 * 04 DE ENERO DEL 2015
 */

public class ChequesTransitoDaoImpl implements ChequesTransitoDao  {
	private JdbcTemplate jdbcTemplate;
	Bitacora bitacora = new Bitacora();

	
	public List<LlenaComboGralDto> llenarComboMotivos(){
		StringBuffer sb = new StringBuffer();
		List<LlenaComboGralDto>listDatos= new ArrayList<LlenaComboGralDto>();
		try{
		      sb.append("\n SELECT DISTINCT id_causa as ID , ");
		      sb.append("\n desc_causa as DESCRIPCION ");
		      sb.append("\n FROM cat_causa_regreso");
		      sb.append("\n ORDER BY desc_causa ");
		      
		    listDatos= jdbcTemplate.query(sb.toString(), new RowMapper<LlenaComboGralDto>(){
				public LlenaComboGralDto mapRow(ResultSet rs, int idx) throws SQLException {
					LlenaComboGralDto cons = new LlenaComboGralDto();
					cons.setId(rs.getInt("ID"));
					cons.setDescripcion(rs.getString("DESCRIPCION"));
					return cons;
				}});
		}
		catch(CannotGetJdbcConnectionException e){
			LlenaComboGralDto cons = new LlenaComboGralDto();
			cons.setCampoUno("Error de conexion");
			listDatos.add(cons);
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Impresion, C:ChequesTransitoDaoImpl, M:llenarComboMotivos");
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Impresion, C:ChequesTransitoDaoImpl, M:llenarComboMotivos");
		}return listDatos;
	}
	
	public List<ChequesTransitoDto> llenaGrid(String noEmpresa, String noBanco, String idChequera, String noCheque,String fechaIni, String fechaFin, String dias){
			List<ChequesTransitoDto> listaResultado = new ArrayList<ChequesTransitoDto>();
			StringBuffer sql = new StringBuffer();
	try{
			sql.append("SELECT  COALESCE( REPLACE( m.no_docto,'null',' '),'') as no_docto,m.no_cheque,m.beneficiario,m.concepto, ");
			sql.append("\n m.importe ,m.no_empresa,e.nom_empresa , m.id_divisa ,m.id_banco, cb.desc_banco , m.id_chequera, ");
			sql.append("\n coalesce((SELECT top 1 f.fec_hoy - m.fec_cheque FROM fechas f),0) as dias ,");
			sql.append("\n m.no_folio_det,m.id_tipo_operacion,m.id_estatus_mov,m.origen_mov,m.id_forma_pago,");
			sql.append("\n m.id_tipo_movto,m.lote_entrada, m.lote_salida,m.usuario_alta,m.b_entregado,");
			sql.append("\n m.b_salvo_buen_cobro,convert(char(10), m.fec_valor,103) as fechaTransferencia, m.no_cuenta,"); 
			sql.append("\n m.division,COALESCE(m.no_poliza,0) as no_poliza,m.importe_original,m.tipo_cambio,");
			sql.append("\n convert(char(10), m.fec_operacion,103) as fec_operacion,");
			sql.append("\n convert(char(10), m.fec_alta,103) as fec_alta,");
			sql.append("\n convert(char(10),m.fec_valor_original,103) as fec_valor_original,");
			sql.append("\n convert(char(10),m.fec_cheque,103) as fec_cheque,");
			sql.append("\n  m.id_caja,m.id_divisa_original,m.no_cliente,m.id_banco_benef, m.referencia,");
			sql.append("\n m.id_leyenda,COALESCE(m.clabe,' ') as clabe,m.solicita,m.autoriza,m.plaza,m.sucursal, m.id_chequera_benef,");
			sql.append("\n m.no_folio_mov,m.id_rubro,m.id_grupo , m.po_headers , year(m.fec_valor) as ejercicio");
			sql.append("\n FROM movimiento m ,empresa e  ,cat_banco cb  ");
			sql.append("\n WHERE m.no_empresa=e.no_empresa ");
			sql.append("\n AND m.id_banco=cb.id_banco ");
			sql.append("\n AND  m.id_forma_pago in(1,10) ");
			sql.append("\n AND m.id_estatus_mov NOT IN('X','Z')");
			sql.append("\n AND m.id_tipo_operacion=3200 ");
			sql.append("\n AND coalesce(m.no_cheque,0) <> 0 ");
			sql.append("\n AND m.id_estatus_cb not in ('M','C','A') ");
			if(!noEmpresa.equals("")&& noEmpresa!=null && !noEmpresa.equals("0"))
				sql.append("\n AND m.no_empresa=" + Utilerias.validarCadenaSQL(noEmpresa));
			if(!noBanco.equals("")&& noBanco!=null && !noBanco.equals("0"))
				sql.append("\n AND m.id_banco= "+ Utilerias.validarCadenaSQL(noBanco));
			if(!idChequera.equals("")&& idChequera!=null)
				sql.append("\n AND m.id_chequera= "+ Utilerias.validarCadenaSQL(idChequera));
			if(!noCheque.equals("")&& noCheque!=null)
				sql.append("\n AND m.no_cheque= "+ Utilerias.validarCadenaSQL(noCheque));
			if(!dias.equals("")&& dias!=null)
				sql.append("\n AND coalesce((SELECT top 1 f.fec_hoy - m.fec_cheque FROM fechas f ),0) >= " + Utilerias.validarCadenaSQL(dias));
			if(!fechaIni.equals("")&& fechaIni!=null && !fechaFin.equals("")&& fechaFin!=null)
				sql.append("\n AND m.fec_cheque BETWEEN '" + Utilerias.validarCadenaSQL(fechaIni) + "' AND '" + Utilerias.validarCadenaSQL(fechaFin)+"'");
			System.out.println("Cheques transito: "+ sql.toString());		 
			listaResultado = jdbcTemplate.query(sql.toString(), new RowMapper<ChequesTransitoDto>(){
				public ChequesTransitoDto mapRow(ResultSet rs, int idx) throws SQLException{
					ChequesTransitoDto campos = new ChequesTransitoDto();
					String noDocto=rs.getString("NO_DOCTO");
					campos.setNoDocumento(noDocto!=null && !noDocto.equals("") && !noDocto.equals("null") ? noDocto:"");
					campos.setNoCheque(rs.getString("NO_CHEQUE"));
					campos.setConcepto(rs.getString("CONCEPTO"));
					campos.setImporte(rs.getString("IMPORTE"));
					campos.setDivisa(rs.getString("ID_DIVISA"));
					campos.setNomEmpresa(rs.getString("NOM_EMPRESA"));
					campos.setDescBanco(rs.getString("DESC_BANCO"));
					campos.setDias(rs.getString("DIAS"));
					campos.setBeneficiario(rs.getString("BENEFICIARIO"));
					campos.setFechaCheque(rs.getString("FEC_CHEQUE"));
					campos.setIdBanco(rs.getString("ID_BANCO"));
					campos.setIdChequera(rs.getString("ID_CHEQUERA"));
					campos.setNoEmpresa(rs.getString("NO_EMPRESA"));
					campos.setNoFolioDetalle(rs.getString("NO_FOLIO_DET"));
					campos.setTipoOperacion(rs.getString("ID_TIPO_OPERACION"));
					campos.setIdEstatus(rs.getString("ID_ESTATUS_MOV"));
					campos.setOrigenMovimiento(rs.getString("ORIGEN_MOV"));
					campos.setFormaPago(rs.getString("ID_FORMA_PAGO"));
					campos.setTipoMovimiento(rs.getString("ID_TIPO_MOVTO"));
					campos.setLoteE(rs.getString("LOTE_ENTRADA"));
					campos.setLoteS(rs.getString("LOTE_SALIDA"));
					campos.setUsuarioAlta(rs.getString("USUARIO_ALTA"));
					campos.setbEntregado(rs.getString("B_ENTREGADO"));
					campos.setBsbc(rs.getString("B_SALVO_BUEN_COBRO"));
					campos.setFecTransferencia(rs.getString("FECHATRANSFERENCIA"));
					campos.setNoCuenta(rs.getString("NO_CUENTA"));
					campos.setDivision(rs.getString("DIVISION"));
					campos.setIdPoliza(rs.getString("NO_POLIZA"));
					campos.setFecOperacion(rs.getString("FEC_OPERACION"));
					campos.setFecAlta(rs.getString("FEC_ALTA"));
					campos.setFecValorOriginal(rs.getString("FEC_VALOR_ORIGINAL"));
					campos.setImporteOriginal(rs.getString("IMPORTE_ORIGINAL"));
					campos.setTipoCambio(rs.getString("TIPO_CAMBIO"));
					campos.setIdCaja(rs.getString("ID_CAJA"));
					campos.setIdDivisaOriginal(rs.getString("ID_DIVISA_ORIGINAL"));
					campos.setIdBancoBenef(rs.getString("ID_BANCO_BENEF"));
					campos.setIdLeyenda(rs.getString("ID_LEYENDA"));
					campos.setClabe(rs.getString("CLABE"));
					campos.setSolicita(rs.getString("SOLICITA"));
					campos.setAutoriza(rs.getString("AUTORIZA"));
					campos.setPlaza(rs.getString("PLAZA"));
					campos.setSucursal(rs.getString("SUCURSAL"));
					campos.setNoFolioMov(rs.getString("NO_FOLIO_MOV"));
					campos.setIdRubro(rs.getString("ID_RUBRO"));
					campos.setIdGrupo(rs.getString("ID_GRUPO"));
					campos.setNoCliente(rs.getString("NO_CLIENTE"));
					campos.setIdChequeraBenef(rs.getString("ID_CHEQUERA_BENEF"));
					campos.setReferencia(rs.getString("REFERENCIA"));
					campos.setEjercicio(rs.getString("EJERCICIO"));
					campos.setPoHeders(rs.getString("PO_HEADERS"));
					return campos;	
				}
			});
	}catch(Exception e){
		bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:ChequesTransito, C:ChequesTransitoDaoImpl, M:llenaGrid");
	}return listaResultado;
	}
	
	public boolean validarCancelar(String pwd, String usuario){
		ConsultasGenerales cg= new ConsultasGenerales(jdbcTemplate);
		Funciones fn= new Funciones();
		try {
			return cg.validarUsuarioAutenticado(Integer.parseInt(usuario), fn.encriptador(pwd));
		} catch (Exception e) {
			return false;
		}
	}
	
	public int insertarMotivo (String motivo, String noFolioDet){
		int iRegAfec = 0;
		StringBuffer sbSql = new StringBuffer();
		try{
			sbSql.append("update movimiento set observacion = 'observacion/ "+ Utilerias.validarCadenaSQL(motivo) +"' \n");
			sbSql.append("WHERE NO_FOLIO_DET = "+ Utilerias.validarCadenaSQL(noFolioDet) +"");
			System.out.println("Motivo"+sbSql);
			iRegAfec = jdbcTemplate.update(sbSql.toString());
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Consultas, C:ConsultasDao, M:insertarMotivo");
		}
		return iRegAfec;
	}
	/*****Insertar en paramentro ***********/
	
	public int insertarParametro(ParametroDto dto){
	    int res=0;
        String sql = "";
        Funciones funciones= new Funciones();
   try{
        sql+= "\n	INSERT INTO parametro(no_empresa,no_docto,no_folio_param,id_tipo_docto,id_forma_pago, ";
        sql+= "\n	usuario_alta,id_tipo_operacion,no_cuenta,no_factura,";
        sql+= "\n	fec_valor,fec_valor_original,fec_operacion,id_divisa, ";
        sql+= "\n	fec_alta,importe,importe_original,tipo_cambio, ";
        sql+= "\n	id_caja,id_divisa_original,";
        
        if(dto.getReferencia()!=null && !dto.getReferencia().equals(""))
        {
        	sql+= "referencia,";
        }
        sql+= "\n	beneficiario,concepto,id_banco_benef,id_chequera_benef,aplica,";
        sql+= "\n	id_estatus_mov,b_salvo_buen_cobro,id_estatus_reg,id_leyenda";
        
        if(dto.getFolioRef()>0)
        	sql+="\n	,folio_ref";
        if(dto.getIdChequera()!=null && !dto.getIdChequera().equals(""))
        	sql+="\n	,id_chequera";
        
        
        sql+= "\n	,observacion,origen_mov,no_cliente,";
        sql+= "\n	solicita, autoriza, plaza, sucursal,";
        sql+= "\n	grupo, no_folio_mov,id_rubro,agrupa1, agrupa2,agrupa3,no_cheque ";

		if(dto.isAdu())
	        sql+=" ,no_pedido ";
	    
		
	    if(dto.getIdArea()>0)
	        sql+=" ,id_area ";
	    
	    if(dto.getDivision()!=null && !dto.getDivision().equals(""))
	        sql+=" ,division ";
	    
	    

	    if(dto.isBNomina() && dto.getLote()!=0)
	        sql+=" ,lote ";
	    else if((dto.isBProvAc() && dto.getLote()==0) || (dto.isBProvAc() && dto.getLote()==1))
	    	sql+=" ,lote ";
	    else if (dto.getLote()!=0) 
	    	sql+=" ,lote ";
	    
	
	    if(dto.getIdGrupo().equals(""))
	        sql+=" ,id_grupo ";
	    if(dto.getPartida()!= null && !dto.getPartida().equals(""))
	        sql+=" ,invoice_type";
	    
	    if(  dto.getIdBanco() != 0 )
	    {
	    	
	    	sql+=" ,id_banco ";
	    }
	    
	    
	    sql+=")";
                  
        sql+= "VALUES("+Utilerias.validarCadenaSQL(dto.getNoEmpresa())+",'"+Utilerias.validarCadenaSQL(dto.getNoDocto())+"',"+Utilerias.validarCadenaSQL(dto.getNoFolioParam());
        sql+= ","+dto.getIdTipoDocto()+","+dto.getIdFormaPago()+","+dto.getUsuarioAlta();
        sql+= ","+dto.getIdTipoOperacion()+",'"+dto.getNoCuenta()+"'"+",'"+Utilerias.validarCadenaSQL(dto.getNoFactura())+"'";
        sql+= ",convert(datetime,'" +funciones.ponerFechaSola(dto.getFecValor())+"', 103), convert(datetime,'"+funciones.ponerFechaSola(dto.getFecValorOriginal())+"', 103)";
        sql+= ",convert(datetime,'"+funciones.ponerFechaSola(dto.getFecOperacion())+"', 103),'"+dto.getIdDivisa()+"', convert(datetime,'"+funciones.ponerFechaSola(dto.getFecAlta())+"', 103),"+dto.getImporte()+",";
        sql+= " "+dto.getImporteOriginal()+","+dto.getTipoCambio()+","+dto.getIdCaja()+",'"+Utilerias.validarCadenaSQL(dto.getIdDivisaOriginal())+"',";
        if(dto.getReferencia()!=null && !dto.getReferencia().equals(""))
        {
        	sql+= "'"+Utilerias.validarCadenaSQL(dto.getReferencia())+"',";
        }
        sql+= "'"+Utilerias.validarCadenaSQL(dto.getBeneficiario())+"','"+Utilerias.validarCadenaSQL(dto.getConcepto())+"',"+Utilerias.validarCadenaSQL(dto.getIdBancoBenef())+",";
        sql+= "'"+Utilerias.validarCadenaSQL(dto.getIdChequeraBenef())+"',"+Utilerias.validarCadenaSQL(dto.getAplica())+",'"+Utilerias.validarCadenaSQL(dto.getIdEstatusMov())+"','"+Utilerias.validarCadenaSQL(dto.getBSalvoBuenCobro())+"','"+Utilerias.validarCadenaSQL(dto.getIdEstatusReg())+"','"+Utilerias.validarCadenaSQL(dto.getIdLeyenda())+"'";
        
        
        if(dto.getFolioRef()>0)
        	sql+=","+Utilerias.validarCadenaSQL(dto.getFolioRef());
        if(dto.getIdChequera()!=null && !dto.getIdChequera().equals(""))
        	sql+=",'"+Utilerias.validarCadenaSQL(dto.getIdChequera())+"'";
        
        sql+= ",'"+Utilerias.validarCadenaSQL(dto.getObservacion())+"'";
        if(dto.isAdu())
             sql+=",'ADU'";
        else if (dto.isBCvDivisa())
         	 sql+=",'CVD'";
        else
        	 sql+=",'SET'";

        sql+= ",'"+Utilerias.validarCadenaSQL(dto.getNoCliente())+"'";
        sql+= ",'"+Utilerias.validarCadenaSQL(dto.getSolicita())+"','"+Utilerias.validarCadenaSQL(dto.getAutoriza())+"',"+Utilerias.validarCadenaSQL(dto.getPlaza())+","+Utilerias.validarCadenaSQL(dto.getSucursal());
        sql+= ","+Utilerias.validarCadenaSQL(dto.getGrupo())+","+Utilerias.validarCadenaSQL(dto.getNoFolioMov())+","+Utilerias.validarCadenaSQL(dto.getIdRubro())+","+dto.getAgrupa1()+","+dto.getAgrupa2()+","+dto.getAgrupa3()+","+Utilerias.validarCadenaSQL(dto.getNoCheque());
        
        if(dto.isAdu())
	        sql+=" ,"+Utilerias.validarCadenaSQL(dto.getNoPedido());
	    
	    if(dto.getIdArea()>0)
	        sql+=" ,"+Utilerias.validarCadenaSQL(dto.getIdArea());
	    
	    if(dto.getDivision()!=null && !dto.getDivision().equals(""))
	        sql+=" ,'"+dto.getDivision()+"'";


	    if(dto.isBNomina() && dto.getLote()!=0)
	        sql+=" ,"+Utilerias.validarCadenaSQL(dto.getLote());
	    else if((dto.isBProvAc() && dto.getLote()==0) || (dto.isBProvAc() && dto.getLote()==1))
	    	sql+=" ,"+Utilerias.validarCadenaSQL(dto.getLote());
	    else if (dto.getLote()!=0) 
	    	sql+=" ,"+Utilerias.validarCadenaSQL(dto.getLote());
	    if(dto.getIdGrupo().equals(""))
	        sql+=" ,"+Utilerias.validarCadenaSQL(dto.getIdGrupo());
	    if(dto.getPartida()!= null && !dto.getPartida().equals(""))
	        sql+=" ,'"+Utilerias.validarCadenaSQL(dto.getPartida())+"'";
	    
	    if(  dto.getIdBanco() != 0 )
	    {
	    	
	    	sql+=" ," + Utilerias.validarCadenaSQL(dto.getIdBanco());
	    }
	    sql+=")";
       //System.out.println("inicia-------------------------------------------------");
      // System.out.println("PAEE"+sql);
	   //System.out.println("termina-------------------------------------------------");
       res=jdbcTemplate.update(sql);
        
       System.out.println(sql);
       
	}catch(Exception e){
		bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
		+ "P:Egresos, C:ImportaBancaElectronicaDao, M:insertarParametro");
		e.printStackTrace();
	}
        
        return res;
}
	
	/************************************/
	
	public Map<String, Object> llenarParametro(ChequesTransitoDto cheque){
		ParametroDto dto = new ParametroDto();
		Funciones funcion=new Funciones();
		int res=0; 
		Map<String, Object> respuesta=  new HashMap<String, Object>();
		ConsultasGenerales consultasGenerales= new ConsultasGenerales(jdbcTemplate);	
	   try{
		   
		   dto.setIdTipoPoliza(Integer.parseInt(cheque.getIdPoliza()));
			dto.setNoEmpresa(Integer.parseInt(cheque.getNoEmpresa()));
			dto.setIdFormaPago(Integer.parseInt(cheque.getFormaPago()));
			dto.setUsuarioAlta(Integer.parseInt(cheque.getUsuarioAlta()));
			dto.setNoCuenta(Integer.parseInt(cheque.getNoCuenta()));
			dto.setReferencia(cheque.getReferencia());
			dto.setDivision(cheque.getDivision());
			//centroCosto
			dto.setFecValor(funcion.ponerFechaDate(cheque.getFecTransferencia())); //fechaValor
			dto.setFecOperacion(funcion.ponerFechaDate(cheque.getFecOperacion()));
			dto.setFecAlta(funcion.ponerFechaDate(cheque.getFecAlta()));
			dto.setFecValorOriginal(funcion.ponerFechaDate(cheque.getFecValorOriginal()));
			dto.setIdDivisa(cheque.getDivisa());
			dto.setImporte(Double.parseDouble(cheque.getImporte()));
			dto.setImporteOriginal(Double.parseDouble(cheque.getImporteOriginal()));
			dto.setTipoCambio(Double.parseDouble(cheque.getTipoCambio()));
			dto.setIdCaja(Integer.parseInt(cheque.getIdCaja()));
			dto.setIdDivisaOriginal(cheque.getIdDivisaOriginal());
			dto.setBeneficiario(cheque.getBeneficiario());
			dto.setNoCliente(cheque.getNoCliente());
			dto.setConcepto(cheque.getConcepto());
			dto.setIdBancoBenef(Integer.parseInt(cheque.getIdBancoBenef()));
			dto.setIdChequeraBenef(cheque.getIdChequeraBenef()); //chequera benef.
			dto.setIdLeyenda(cheque.getIdLeyenda());
			dto.setIdChequera(cheque.getIdChequera());
			dto.setObservacion(cheque.getObservacion());
			dto.setClabe(cheque.getClabe());
			dto.setSolicita(cheque.getSolicita());
			dto.setAutoriza(cheque.getAutoriza());
			dto.setPlaza(Integer.parseInt(cheque.getPlaza()));
			dto.setSucursal(Integer.parseInt(cheque.getSucursal()));
			dto.setNoFolioMov(Integer.parseInt(cheque.getNoFolioMov()));
			dto.setIdRubroInt(cheque.getIdRubro());
			dto.setIdGrupo(cheque.getIdGrupo());
			dto.setNoCheque(Integer.parseInt(cheque.getNoCheque()));
			dto.setIdBanco(Integer.parseInt(cheque.getIdBanco()));
			//dto.setIdRubro(3200);
			//Valores fijos
			dto.setAplica(1);
			dto.setIdEstatusMov("A");
			dto.setBSalvoBuenCobro("S");
			dto.setIdEstatusReg("P");
			dto.setIdTipoDocto(41);
			dto.setIdTipoOperacion(3101);
			dto.setFolioRef(0);
			dto.setAgrupa1(0);
			dto.setAgrupa2(0);
			dto.setAgrupa3(0);
			dto.setAdu(false);
			dto.setLote(1);
			dto.setPartida("0");
			
			//valores requeridos
			
			int folioParametro=0;
			
			String folioNuevo="";
			
			consultasGenerales.actualizarFolioReal("no_folio_param");
			folioParametro= consultasGenerales.seleccionarFolioReal("no_folio_param");
			
			consultasGenerales.actualizarFolioReal("no_folio_docto");
			folioNuevo=""+consultasGenerales.seleccionarFolioReal("no_folio_docto");
			
			dto.setNoFolioParam(folioParametro);
			dto.setNoDocto(folioNuevo);
			dto.setNoFactura(folioNuevo);
			dto.setGrupo(folioParametro);   
			
			res=insertarParametro(dto);
			
			respuesta.put("respuesta", res);
			respuesta.put("folioParametro",folioParametro);
	        System.out.println("res"+ res);
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)+ "P:Egresos, C:ImportaBancaElectronicaDao, M:insertarParametro");
			e.printStackTrace();
			respuesta.put("respuesta", "Error");
			System.out.println("error en insertar paramentro");
		}return respuesta;
	}
	/************************Ejecutar revividor*********/
	public Map<String, Object> ejecutarRevividor(ChequesTransitoDto dto) {
		Map<String, Object> resultado = new HashMap<String, Object>();
		int result = 0;
		String resultadoRevividor = "";	
		ConsultasGenerales consultasGenerales= new ConsultasGenerales(jdbcTemplate);	
		System.out.println("Entra a ejecuta revividor");
		try{
			
			result=consultasGenerales.ejecutarRevividorOR(
															"true", //String psRevividor,
															Integer.parseInt(dto.getNoFolioDetalle()),
															Integer.parseInt(dto.getTipoOperacion()), //int idTipoOperacion,
															"R", //String psTipoCancelacion
															"P", 
															"SOI",
															Integer.parseInt(dto.getFormaPago()), // int idFormaPago
															dto.getbEntregado(), //String bEntregado
															dto.getTipoMovimiento(),
															Double.parseDouble(dto.getImporte()), 
															Integer.parseInt(dto.getNoEmpresa()), 
															Integer.parseInt(dto.getNoCuenta()), 
															dto.getIdChequera(), 
															Integer.parseInt(dto.getIdBanco()), 
															Integer.parseInt(dto.getUsuarioAlta()),
															dto.getNoDocumento(),
															0, //lote
															dto.getBsbc(), //bSalvoBuenCobro
															dto.getFecTransferencia(), 
															dto.getDivisa(), 
															resultadoRevividor, 
															false
			);		

			
			
			if(result!=0)				
				resultado.put("result","1");
				
			else				
				resultado.put("result","0");
		}
		catch (Exception e) {
			System.out.println("error en el revividor");
			bitacora.insertarRegistro(new Date().toString()+ " "+ e.toString()
			+  "P:Impresion, C:ChequesTransitoDaoImpl, M: ejecutarRevividor");
			e.printStackTrace();
		}
		return resultado;
	}
	/*********Fin revividor ****************/
	
	
	/********Ejecutar generador ************/
	
	/**
	 * 
	 * @param emp numero de la empresa
	 * @param folParam numero de folio de parametro
	 * @param folMovi folio de movimiento este lo obtiene en el store asi que se manda un cero
	 * @param folDeta este parametro es igual al anterior
	 * @param result se envia el numero 1 por default
	 * @param mensajeResp es una variable que maneja el store se puede enviar como: ''
	 * @return res para verificar si hizo o no el commit
	 */
	
	public Map<String, Object> ejecutarGenerador(String noEmpresa, String noFolioParam) {
		Map<String, Object> resultado = new HashMap<String, Object>();
		ConsultasGenerales consultasGenerales= new ConsultasGenerales(jdbcTemplate);
		GeneradorDto generador= new GeneradorDto();
		System.out.println("Entra a ejecuta Generador: noempresa:" + noEmpresa + ", folioParam:" + noFolioParam);
		try{
			generador.setEmpresa(Integer.parseInt(noEmpresa));
			generador.setFolParam(Integer.parseInt(noFolioParam));
			generador.setFolMovi(0);
			generador.setFolDeta(0);
			generador.setResult(1);
			generador.setMensajeResp("");
			resultado=consultasGenerales.ejecutarGenerador(generador);												
		}
		catch (Exception e) {
			System.out.println("error en el generador");
			bitacora.insertarRegistro(new Date().toString()+ " "+ e.toString()
			+  "P:Impresion, C:ChequesTransitoDaoImpl, M: ejecutarGenerador");
			e.printStackTrace();
		}
		return resultado;
	}
	
	
	
	/*******FIN DEL GENERADOR***************/
	
	
	public String conciliarChequeCajaOCertificado(String noFolioDetalle){
		String resultado = "Error en la cancelacion del cheque.";
		int result = 0;
		StringBuffer sb = new StringBuffer();
		try {
			sb.append("update from movimiento m");
			sb.append("set id_estatus_cb= 'C'");
			sb.append("where m.no_folio_det= "+ noFolioDetalle);
			sb.append("AND m.b_certificado= 'S' ");
			sb.append("AND m.id_estatus_cb not in ('M','C','A')");
			
			result = jdbcTemplate.update(sb.toString());
			if(result!=0)				
				resultado= "Exito al cancelar el cheque";	
			
		} catch (Exception e) {
			System.out.println("error en conciliarChequeCajaOCertificado");
			bitacora.insertarRegistro(new Date().toString()+ " "+ e.toString()
			+  "P:Impresion, C:ChequesTransitoDaoImpl, M: conciliarChequeCajaOCertificado");
			e.printStackTrace();
		}return resultado;
		
		
	}
	
	public String configuraSet(int indice) {
		ConsultasGenerales consultasGenerales = new ConsultasGenerales(jdbcTemplate);
		return consultasGenerales.consultarConfiguraSet(indice);
	}
	
	/***********************************************************/
	public void setDataSource(DataSource dataSource){
		try {
			this.jdbcTemplate = new JdbcTemplate(dataSource);
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:impresion.mantenimientos, C:ChequesTransitoDaoImpl, M:setDataSource");
		}
	}	
	
}
