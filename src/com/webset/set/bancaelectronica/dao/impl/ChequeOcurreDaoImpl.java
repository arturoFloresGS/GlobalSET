package com.webset.set.bancaelectronica.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import com.webset.set.bancaelectronica.dao.ChequeOcurreDao;
import com.webset.set.bancaelectronica.dto.CriterioBusquedaDto;
import com.webset.set.utilerias.Bitacora;
import com.webset.set.utilerias.ConsultasGenerales;
import com.webset.set.utilerias.Funciones;
import com.webset.set.utilerias.dto.LlenaComboEmpresasDto;
import com.webset.set.utilerias.dto.LlenaComboGralDto;
import com.webset.set.utilerias.dto.MovimientoDto;
import com.webset.set.utilerias.dto.UsuarioLoginDto;
import com.webset.utils.tools.Utilerias;

public class ChequeOcurreDaoImpl implements ChequeOcurreDao {
	private JdbcTemplate jdbcTemplate;
	Bitacora bitacora = new Bitacora();
	Funciones funciones = new Funciones();
	ConsultasGenerales consultasGenerales;
	String gsDBM = "SQL SERVER";
	public boolean pbH2HBital;
	@Override
	public List<LlenaComboEmpresasDto> obtenerEmpresas(int idUsuario) {
		consultasGenerales = new ConsultasGenerales (jdbcTemplate);
		return consultasGenerales.llenarComboEmpresas(idUsuario);
	}
	
	@Override
	public List<LlenaComboGralDto> llenaComboBanco(int noEmpresa) {
		StringBuffer sql=new StringBuffer();
		List<LlenaComboGralDto> list = new ArrayList<LlenaComboGralDto>();
		
		try{
		    sql.append(" SELECT id_banco as ID, desc_banco as DESCRIPCION ");
		    sql.append(" \n	FROM cat_banco ");
		    sql.append(" \n	WHERE id_banco in (SELECT distinct id_banco FROM cat_cta_banco ");
		    sql.append(" \n					   WHERE tipo_chequera not in (SELECT valor FROM configura_set WHERE indice = 202) ");
		  //  sql.append("\n AND id_banco = 14 ");
		    
		    if(noEmpresa != 0) sql.append(" \n	AND no_empresa = " + noEmpresa + ") ");
		    else  sql.append(" \n ) ");
		    
			System.out.println("llenaComboBanco: " + sql.toString());
			
		    list = jdbcTemplate.query(sql.toString(), new RowMapper<LlenaComboGralDto>(){
		    	public LlenaComboGralDto mapRow(ResultSet rs, int idx) throws SQLException {
					LlenaComboGralDto cons = new LlenaComboGralDto();
					cons.setId(rs.getInt("ID"));
					cons.setDescripcion(rs.getString("DESCRIPCION"));
					return cons;
				}
		    });
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) 
			+ "P:BE, C:ChequeOcurreDaoImpl, M:llenaComboBanco ");
		}
		return list;
	}

	@Override
	public List<MovimientoDto> consultarCheques(int noEmpresa, int idBanco) {
		StringBuffer sql = new StringBuffer();
		try {		     
		    sql.append(" SELECT m.observacion, e.nom_empresa, m.plaza, m.referencia, m.no_cliente, m.no_empresa, m.lote_entrada, \n"); 
			sql.append(" 	m.no_empresa AS no_empresaCXP, m.origen_mov, m.fec_valor, m.sucursal, m.no_docto, m.fec_operacion, \n"); 
			sql.append(" 	m.importe AS importe, m.referencia, m.id_divisa, m.no_folio_mov, m.id_chequera_benef, m.id_banco, \n"); 
			sql.append(" 	m.id_banco_benef,m.id_chequera, \n"); 
			sql.append(" 	(SELECT desc_banco \n"); 
			sql.append(" 		FROM cat_banco \n"); 
			sql.append(" 		WHERE id_banco = m.id_banco_benef) AS desc_banco_benef, \n"); 
			sql.append(" 	(SELECT desc_banco \n"); 
			sql.append(" 		FROM cat_banco \n");
			sql.append(" 		WHERE id_banco = m.id_banco) AS desc_banco, \n"); 
			sql.append(" 	(SELECT no_empresa \n"); 
			sql.append(" 		FROM cat_cta_banco \n");
			sql.append(" 		WHERE id_banco = m.id_banco_benef \n"); 
			sql.append(" 		    AND id_chequera = m.id_chequera_benef) AS no_empresa_benef, \n");
			sql.append(" 	m.beneficiario, m.concepto, m.no_folio_det, M.no_factura, \n"); 
			sql.append(" 	(SELECT rfc \n"); 
			sql.append(" 		FROM persona \n"); 
			sql.append(" 		WHERE no_persona = m.no_cliente AND id_tipo_persona = 'P') AS RFC_benef \n"); 
			sql.append(" FROM movimiento m, cat_cta_banco c, cat_banco cb, empresa e \n"); 
			sql.append(" WHERE m.no_empresa = e.no_empresa \n");
			sql.append(" 	AND m.id_estatus_mov IN ('P') \n"); 
			sql.append(" 	AND (origen_mov IN ('SET','') OR origen_mov IS NULL) \n"); 
			sql.append(" 	AND m.no_empresa = c.no_empresa \n"); 
			sql.append(" 	AND m.id_banco = c.id_banco \n"); 
			sql.append(" 	AND m.id_chequera = c.id_chequera \n"); 
			sql.append("    AND m.id_tipo_movto = 'E' \n"); 
			sql.append("    AND cb.id_banco = c.id_banco \n"); 
			sql.append(" 	AND m.id_tipo_operacion = 3200 \n");
			sql.append(" 	AND (m.id_forma_pago = 8 OR m.id_forma_pago=9) \n");

		     if (noEmpresa > 0) {
		          sql.append(" AND m.no_empresa = " + noEmpresa + "\n");
		     }

		     if (idBanco > 0) {
		          sql.append(" AND m.id_banco = " + idBanco);
		     }

			System.out.println("--Consulta cheques ocurre: \n" + sql.toString());
				
		     List<MovimientoDto> list = jdbcTemplate.query(sql.toString().toString(), new RowMapper<MovimientoDto>(){
			    public MovimientoDto mapRow(ResultSet rs, int idx) throws SQLException {
			    	MovimientoDto cons = new MovimientoDto();
			    	cons.setObservacion(rs.getString("observacion"));
			    	cons.setNomEmpresa(rs.getString("nom_empresa")); 
			    	cons.setPlaza(rs.getInt("plaza")); 
			    	cons.setReferencia(rs.getString("referencia")); 
			    	cons.setNoCliente(rs.getString("no_cliente")); 
			    	cons.setNoEmpresa(rs.getInt("no_empresa")); 
			    	cons.setLoteEntrada(rs.getInt("lote_entrada")); 
			    	cons.setNoEmpresa(rs.getInt("no_empresaCXP")); 
			    	cons.setOrigenMov(rs.getString("origen_mov")); 
			    	cons.setFecValor(rs.getDate("fec_valor")); 
			    	cons.setSucursal(rs.getInt("sucursal")); 
			    	cons.setNoDocto(rs.getString("no_docto")); 
			    	cons.setFecOperacion(rs.getDate("fec_operacion")); 
			    	cons.setImporte(rs.getInt("importe")); 
			    	cons.setReferencia(rs.getString("referencia"));
			    	cons.setIdDivisa(rs.getString("id_divisa")); 
			    	cons.setNoFolioMov(rs.getInt("no_folio_mov")); 
			    	cons.setIdChequeraBenef(rs.getString("id_chequera_benef")); 
			    	cons.setIdBanco(rs.getInt("id_banco")); 
			    	cons.setIdBancoBenef(rs.getInt("id_banco_benef"));
			    	cons.setIdChequera(rs.getString("id_chequera")); 
			    	cons.setDescBancoBenef(rs.getString("desc_banco_benef")); 
			    	cons.setDescBanco(rs.getString("desc_banco")); 
			    	cons.setNoEmpresaBenef(rs.getInt("no_empresa_benef")); 
			    	cons.setBeneficiario(rs.getString("beneficiario")); 
			    	cons.setConcepto(rs.getString("concepto")); 
			    	cons.setNoFolioDet(rs.getInt("no_folio_det")); 
			    	cons.setNoFactura(rs.getString("no_factura"));
			    	cons.setRfcBenef(rs.getString("rfc_benef"));
					return cons;
				}
		     });
		     return list;
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:BE, C:ChequeOcurreDaoImpl, M:consultarCheques ");
			System.out.println(e.toString());
		}
		return null;
	}

	@Override
	public List<MovimientoDto> seleccionarRegistros(String ps_folios) {
		return null;
	}

	@Override
	public boolean insertarDetArchTransfer(String ps_nom_archivo, int noFolioDet, String noDocto, Date fecValor,
			String idChequera, int idBanco, int i, String noCliente, String string, double importe, String beneficiario,
			int sucursal, int plaza, String ps_concepto) {
		return false;
	}

	@Override
	public boolean insertarArchTransfer(String ps_nom_archivo, int idBanco, double pd_importe, int pi_registros,
			UsuarioLoginDto usuarioLoginDto) {
		return false;
	}

	@Override
	public boolean actulizaFolios(String ps_folios) {
		return false;
	}

	@Override
	public String consultarConfiguraSet(int i) {
		consultasGenerales = new ConsultasGenerales (jdbcTemplate);
		return consultasGenerales.consultarConfiguraSet(i);
	}

	@Override
	public int obtenFolio(String string) {
		consultasGenerales = new ConsultasGenerales (jdbcTemplate);
		return consultasGenerales.seleccionarFolioReal(string);
	}
	
	public void setDataSource(DataSource dataSource){
		try {
			this.jdbcTemplate = new JdbcTemplate(dataSource);
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Egresos, C:ConfirmacionTransferenciasDao, M:setDataSource ");
		}
	}

	@Override
	public int actualizarMovimiento(int folio) {
		StringBuffer sql=new StringBuffer();
		int resultado;
		
		try{
		    sql.append(" Update movimiento set id_estatus_mov = 'I',b_entregado = 'N' where no_folio_det in ("+folio+") ");
		    resultado = jdbcTemplate.update(sql.toString());
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) 
			+ "P:BE, C:ChequeOcurreDaoImpl, M:llenaComboBanco ");
			return 0;
		}
		return resultado;
	}
	
	
	
	
	
	@Override
	public int regresaEstatusMovimiento(int folio) {
		StringBuffer sql=new StringBuffer();
		int resultado;
		try{
		    sql.append(" Update movimiento set id_estatus_mov = 'P',b_entregado = NULL where no_folio_det in ("+folio+") ");
		    resultado = jdbcTemplate.update(sql.toString());
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) 
			+ "P:BE, C:ChequeOcurreDaoImpl, M:llenaComboBanco ");
			return 0;
		}
		return resultado;
	}

	@Override
	public int eliminaArchivosErroneos(String nomArch) {
		StringBuffer sql=new StringBuffer();
		int resultado;
		try {
			sql.append("delete from det_arch_transfer where nom_arch='"+nomArch+"'");
			resultado=jdbcTemplate.update(sql.toString());
		} catch (Exception e) {
			System.out.println("error al realizar delete de det_arch_transfer");
			resultado=0;
		}
		try {
			sql.append("delete from arch_transfer where nom_arch='"+nomArch+"'");
			resultado=jdbcTemplate.update(sql.toString());
		} catch (Exception e) {
			System.out.println("error al realizar delete de det_arch_transfer");
			resultado=0;
		}
		return resultado;
	}
	/// consulta de pagos
	
	public List<MovimientoDto> consultaCheques(CriterioBusquedaDto dtoBus){
		List<MovimientoDto> list = new ArrayList<MovimientoDto>();
		StringBuffer sql = new StringBuffer();
		pbH2HBital = false;
		try{
		    if(consultarConfiguraSet(232).trim().equals("SI") && dtoBus.getIdBanco() == 21) 
		    	pbH2HBital = true;
		    //'********************************* ORIGEN MOV <> FIL
	    	sql = new StringBuffer();
	    	sql.append("Select distinct convert(char,m.fec_valor_original,3) as fec_valor_original,m.id_tipo_operacion,\n");
	    	sql.append("m.observacion,m.id_banco,case when m.no_docto='null' then m.no_factura else m.no_docto end as no_docto,\n");
	    	sql.append(" convert(char,m.fec_operacion,103) as fec_operacion,o.desc_cve_operacion,  \n");
	    	sql.append("CASE WHEN m.origen_mov='CVT' THEN m.importe ELSE m.importe END as importe,\n");  
	    	sql.append("CASE WHEN m.origen_mov='CVT' THEN m.id_divisa_original ELSE m.id_divisa END as id_divisa,\n"); 
	    	sql.append("(SELECT coalesce(pp.razon_social, '') FROM persona pp \n"); 
	    	sql.append("WHERE pp.no_persona = m.no_cliente and pp.id_tipo_persona = 'P' ) as beneficiario,\n"); 
	    	sql.append("m.origen_mov, m.division,m.concepto,m.no_folio_det,m.no_empresa,\n"); 
	    	sql.append(" m.id_chequera,convert(char,m.fec_valor,103) as fec_valor,m.no_cliente,\n");
	    	sql.append("e.id_contrato_mass,e.id_contrato_wlink ,p.rfc as rfc_benef ,m.no_factura,e.nom_empresa,pp.rfc,\n"); 
            
//            if(pbH2HBital)
//            	sql.append("\n,u.desc_usuario_bital,s.desc_servicio_bital");
            
            sql.append("\n p.equivale_persona, "
            		+ "(select max(id_tipo_envio) from cat_tipo_envio_layout ctt where "
            		+ "ctt.id_banco = m.id_banco) as tipo_envio_layout ");
            
            //'MAS 04/09/02007 se agrega para validar la divisa de la chequera de pago contra la del docto
//            sql.append("\n,(select id_divisa from cat_cta_banco ccb where ccb.no_empresa = m.no_empresa ");
//            sql.append("\n   and ccb.id_banco = m.id_banco and ccb.id_chequera = m.id_chequera) as divisa_chequera");
            sql.append("\n   ,d.ciudad +  ', ' +  d.id_estado as direccion_benef , "
            		+ "(select pa.desc_pais from cat_pais pa where pa.id_pais = 'MX') as pais_benef,"
            		+ " m.cve_control as clave, m.no_folio_mov");
            ///
            sql.append(" From Movimiento M ");
//            sql.append(" Left Join Cat_Banco B2 On ( m.Id_Banco_Benef = B2.Id_Banco )");
//            sql.append(" Left Join Ctas_Banco Ctas On (M.Id_Banco_Benef = Ctas.Id_Banco"
//                                           +" And  M.Id_Chequera_Benef = Ctas.Id_Chequera"
//                                           +" And Cast(M.No_Cliente As Integer) = Ctas.No_Persona )");
            sql.append(" Left Outer Join Direccion D on (Cast(M.No_Cliente As Integer) = d.no_persona "
            		+"and d.id_tipo_direccion = 'OFNA'"
            		+"and d.no_direccion = 1 "
            		+"and d.id_tipo_persona = 'P'"
            		+"and d.no_empresa = 552"
            		+ "),");
            sql.append(" Empresa E,Persona P,"
            		+ " Persona Pp,"
            		+ " cat_cve_operacion o");
         
            //
                        
//            if(pbH2HBital)
//                sql.append("\n  ,cat_usuario_bital u, cat_servicio_bital s");
            
            sql.append("\n  WHERE ");
            sql.append("\n   Cast(M.No_Cliente As Integer) = p.no_persona ");
            sql.append("\n  and ((m.origen_mov='CVT' and  p.id_tipo_persona in ('K')) "
            		+ "or( m.origen_mov<>'CVT' and  p.id_tipo_persona in ('P')) ) ");
            
//            if(pbH2HBital)
//            {
//                sql.append("\n  and e.id_usuario_bital *= u.id_usuario_bital ");
//                sql.append("\n  and e.id_servicio_bital *= s.id_servicio_bital ");
//            }
            
            sql.append("\n  and m.no_empresa = pp.no_empresa ");
            sql.append("\n  and m.no_empresa = pp.no_persona ");
            sql.append("\n  and pp.id_tipo_persona = 'E'");
            
            sql.append("\n  and m.no_empresa = e.no_empresa ");
            
            sql.append("\n  and ((m.id_tipo_operacion = 3000 and m.id_estatus_mov = 'V' And (m.id_divisa <> 'DLS' And m.id_divisa <> 'MN')) OR m.id_estatus_mov = 'P') ");
            sql.append("\n  and m.id_forma_pago = 9 ");
            sql.append("\n  AND m.id_cve_operacion = o.id_cve_operacion ");
            sql.append("\n  AND m.id_tipo_movto = 'E' ");
            sql.append("\n  and m.id_banco = " + Utilerias.validarCadenaSQL(dtoBus.getIdBanco()));
            
           // if(dtoBus.getIdEmpresa()==0)
            if(dtoBus.isChkTodasEmpresas())
                sql.append("\n  AND m.no_empresa in (select no_empresa from usuario_empresa where no_usuario = " +Utilerias.validarCadenaSQL(dtoBus.getIdUsuario())+ ")");
            else
                //sql.append("\n  AND m.no_empresa = " +dtoBus.getIdEmpresa());
            	sql.append("\n  AND m.no_empresa in (Select No_Empresa From Grupo_Empresa Where Id_Grupo_Flujo = " +Utilerias.validarCadenaSQL(dtoBus.getIdEmpresa())+") ");
            

//            if(dtoBus.isChkConvenioCie())
//                sql.append("\n  AND substring(id_chequera_benef,1,4) = 'CONV'");
//            else if (dtoBus.getIdBanco()==12 && (dtoBus.getOptTipoEnvio()!=null && dtoBus.getOptTipoEnvio().trim().equals("Mismo")))
//                sql.append("\n  AND substring(id_chequera_benef,1,4) <> 'CONV'");
            
//            if(dtoBus.getOptComerica()!=null && dtoBus.getOptComerica().trim().equals("ACH"))
//                sql.append("\n  AND m.id_servicio_be = 'ACH' ");
//            
//            if(dtoBus.isChkSoloLocales())
//                sql.append("\n  AND e.b_local = 'S' ");
               
//            if(dtoBus.getOptTipoEnvio()!=null && dtoBus.getOptTipoEnvio().trim().equals("SPEUA"))
//            {
//            	sql.append("\n  and m.id_banco <> m.id_banco_benef ");
//                sql.append("\n  and m.importe >= 50000 ");
//            }
//            else if (dtoBus.getOptTipoEnvio()!=null && dtoBus.getOptTipoEnvio().trim().equals("Mismo"))
//                    sql.append("\n  and m.id_banco = m.id_banco_benef");
//            else if (dtoBus.getOptTipoEnvio()!=null && dtoBus.getOptTipoEnvio().trim().equals("Internacional"))
//               sql.append("\n AND m.id_banco <> m.id_banco_benef");
//            else
//            {
//               if(dtoBus.getIdBanco()!=12 && (dtoBus.getIdDivisa()!=null && dtoBus.getIdDivisa().trim().equals(consultarConfiguraSet(771))))
//               {
//                    sql.append("\n AND ((b.nac_ext = 'N' and b2.nac_ext = 'N')");
//                    sql.append("\n or (b2.nac_ext = 'N' and b.nac_ext = 'N'))");
//               }
                sql.append("\n AND m.id_banco <> m.id_banco_benef");
//            }
            sql.append("\n  AND m.origen_mov <> 'FIL' ");
            if (dtoBus.isNomina()){
                sql.append("\n  AND m.origen_mov = 'PRO' ");
            } else {

                sql.append("\n  AND m.origen_mov <> 'PRO' ");
            }

            if (dtoBus.isChkConvenioSant()) {
            	sql.append("\n AND m.id_chequera_benef like 'SANT%' ");
            } else {
            	sql.append("\n AND m.id_chequera_benef not like 'SANT%' ");
            }
            
            if (dtoBus.isChkDebito()) {
            	sql.append("\n AND LEN(m.id_chequera_benef) = 16 ");
            } else {
            	sql.append("\n AND LEN(m.id_chequera_benef) <> 16 ");
            }
            //'********************************* ORIGEN MOV = FIL
            
            sql.append("\n  UNION ALL ");
            sql.append("Select distinct convert(char,m.fec_valor_original,103) as fec_valor_original,\n");
            sql.append("m.id_tipo_operacion,m.observacion,m.id_banco,\n");
            sql.append("case when m.no_docto='null' then m.no_factura else m.no_docto end as no_docto,\n"); 
            sql.append("convert(char,m.fec_operacion,103) as fec_operacion,o.desc_cve_operacion, \n");  
            sql.append("CASE WHEN m.origen_mov='CVT' THEN m.importe ELSE m.importe END as importe, \n");
            sql.append("CASE WHEN m.origen_mov='CVT' THEN m.id_divisa_original ELSE m.id_divisa END as id_divisa,\n");
            sql.append("(SELECT coalesce(pp.razon_social, '') FROM persona pp \n"); 
	    	sql.append("WHERE pp.no_persona = m.no_cliente and pp.id_tipo_persona = 'P' ) as beneficiario,\n"); 
            sql.append("m.origen_mov, m.division,m.concepto,m.no_folio_det,m.no_empresa,\n");
            sql.append("m.id_chequera,convert(char,m.fec_valor, 103) as fec_valor,m.no_cliente,\n"); 
            sql.append("e.id_contrato_mass,e.id_contrato_wlink ,case when m.origen_mov = 'FIL' then 'AAA999999AAA' \n"); 
            sql.append("else 'AAAA999999XXX' end as rfc_benef,m.no_factura,e.nom_empresa,pp.rfc, \n"); 
            
//            if(pbH2HBital)
//                sql.append("\n,u.desc_usuario_bital,s.desc_servicio_bital");
            
            sql.append("\n '' as equivale_persona, 0 as tipo_envio_layout");
            
            //'MAS 04/09/02007 se agrega para validar la divisa de la chequera de pago contra la del docto
//            sql.append("\n,(select id_divisa from cat_cta_banco ccb where ccb.no_empresa = m.no_empresa ");
//            sql.append("\n   and ccb.id_banco = m.id_banco and ccb.id_chequera = m.id_chequera) as divisa_chequera");
//            
            sql.append("\n   ,d.ciudad + ', ' + d.id_estado as direccion_benef , (select pa.desc_pais from cat_pais pa where pa.id_pais = 'MX') as pais_benef, m.cve_control as clave, m.no_folio_mov");
            
            
            ///
            sql.append(" From  Cat_Cve_Operacion O, movimiento M ");
            sql.append(" Left Outer Join direccion D On (m.no_empresa = d.no_persona "
            		+ "and d.id_tipo_direccion = 'OFNA'"
            		+ "and d.no_direccion = 1"
            		+ "and d.id_tipo_persona = 'P'"
            		+ "and d.no_empresa = 552"
            		+ ")");		
            sql.append("Left Outer Join persona pp on (pp.no_persona = d.no_persona), empresa e");
            ///
           
//            if(pbH2HBital) 
//                sql.append("\n  ,cat_usuario_bital u, cat_servicio_bital s");
            
            sql.append("\n  WHERE ");
            sql.append("\n  m.no_empresa = e.no_empresa ");
            
//            if(pbH2HBital)
//            {
//                sql.append("\n  and e.id_usuario_bital *= u.id_usuario_bital ");
//                sql.append("\n  and e.id_servicio_bital *= s.id_servicio_bital ");
//            }
            //'de Luis3321
            sql.append("\n  and m.no_empresa = pp.no_empresa ");
            sql.append("\n  and m.no_empresa = pp.no_persona ");
            sql.append("\n  and pp.id_tipo_persona = 'E'");
            
            
            sql.append("\n  and ((m.id_tipo_operacion = 3000 and m.id_estatus_mov = 'V' And (m.id_divisa <> 'DLS' And m.id_divisa <> 'MN')) OR m.id_estatus_mov = 'P') ");
            sql.append("\n  and m.id_forma_pago = 9 ");
            sql.append("\n  AND m.id_cve_operacion = o.id_cve_operacion ");
            sql.append("\n  AND m.id_tipo_movto = 'E'");
            sql.append("\n  and m.id_banco = " + Utilerias.validarCadenaSQL(dtoBus.getIdBanco()));
            
            if(dtoBus.isChkTodasEmpresas())
                sql.append("\n  AND m.no_empresa in (select no_empresa from usuario_empresa where no_usuario = " +Utilerias.validarCadenaSQL(dtoBus.getIdUsuario())+ ")");
            else
                //sql.append("\n  AND m.no_empresa = " +dtoBus.getIdEmpresa());
            	sql.append("\n  AND m.no_empresa in (Select No_Empresa From Grupo_Empresa Where Id_Grupo_Flujo = " +Utilerias.validarCadenaSQL(dtoBus.getIdEmpresa())+") ");
            
//            if(dtoBus.getOptComerica()!=null && dtoBus.getOptComerica().trim().equals("ACH"))
//                sql.append("\n  AND m.id_servicio_be = 'ACH' ");
//            
//            if(dtoBus.isChkSoloLocales())
//                sql.append("\n  AND e.b_local = 'S' ");

            
            if(dtoBus.getOptTipoEnvio()!=null && dtoBus.getOptTipoEnvio().trim().equals("SPEUA"))
            {
                sql.append("\n  and m.id_banco <> m.id_banco_benef ");
                sql.append("\n  and m.importe >= 50000 ");
            }
            else if(dtoBus.getOptTipoEnvio()!=null && dtoBus.getOptTipoEnvio().trim().equals("Mismo"))
                    sql.append("\n  and m.id_banco = m.id_banco_benef");
            else if (dtoBus.getOptTipoEnvio()!=null && dtoBus.getOptTipoEnvio().trim().equals("Internacional"))
            {
            //   sql.append("\n AND b.nac_ext = 'N' and b2.nac_ext = 'E'");
               sql.append("\n AND m.id_banco <> m.id_banco_benef");
            }else{
//               sql.append("\n AND ((b.nac_ext = 'N' and b2.nac_ext = 'N')");
//               sql.append("\n or (b2.nac_ext = 'N' and b.nac_ext = 'N'))");
               sql.append("\n AND m.id_banco <> m.id_banco_benef");
            }
            
            sql.append("\n  AND m.origen_mov = 'FIL' ");
            if (dtoBus.isNomina()){
                sql.append("\n  AND m.origen_mov = 'PRO' ");
            } else {

                sql.append("\n  AND m.origen_mov <> 'PRO' ");
            }

            if (dtoBus.isChkConvenioSant()) {
            	sql.append("\n AND m.id_chequera_benef like 'SANT%' ");
            } else {
            	sql.append("\n AND m.id_chequera_benef not like 'SANT%' ");
            }

            if (dtoBus.isChkDebito()) {
            	sql.append("\n AND LEN(m.id_chequera_benef) = 16 ");
            } else {
            	sql.append("\n AND LEN(m.id_chequera_benef) <> 16 ");
            }
            sql.append("\n  ORDER BY importe ");

            System.out.println("\n consulta chequess"+sql);
		    
		    list= jdbcTemplate.query(sql.toString(), new RowMapper <MovimientoDto>(){
				public MovimientoDto mapRow(ResultSet rs, int idx) throws SQLException {
					MovimientoDto cons = new MovimientoDto();
			
					cons.setIdTipoOperacion(rs.getInt("id_tipo_operacion"));
					//cons.setIdBancoCityStr(rs.getString("banco_city"));
				//	cons.setInstFinan(rs.getString("inst_finan"));
					cons.setObservacion(rs.getString("observacion"));
				//	cons.setSucOrigen(rs.getString("suc_origen"));
				//	cons.setSucDestino(rs.getString("suc_destino"));
					cons.setIdBanco(rs.getInt("id_banco"));
				//	cons.setBLayoutComerica(rs.getString("b_layout_comerica"));
					cons.setNoDocto(rs.getString("no_docto"));
					
					cons.setDescCveOperacion(rs.getString("desc_cve_operacion"));
					cons.setImporte(rs.getDouble("importe")); 
					cons.setIdDivisa(rs.getString("id_divisa"));
				//	cons.setIdChequeraBenef(rs.getString("id_chequera_benef"));
					cons.setOrigenMov(rs.getString("origen_mov"));
					cons.setDivision(rs.getString("division"));
				//	cons.setNombreBancoBenef(rs.getString("nombre_banco_benef"));
			//		cons.setDescBancoBenef(rs.getString("desc_banco_benef"));
					cons.setBeneficiario(rs.getString("beneficiario"));
					cons.setConcepto(rs.getString("concepto"));
					cons.setNoFolioDet(rs.getInt("no_folio_det")); 
					cons.setNoEmpresa(rs.getInt("no_empresa"));
					cons.setIdChequera(rs.getString("id_chequera"));
				
				//	cons.setIdBancoBenef(rs.getInt("id_banco_benef"));
			//		cons.setPlazaBenef(rs.getString("plaza_benef"));
					
					cons.setNoCliente(rs.getString("no_cliente"));
				//	cons.setDescBancoInbursa(rs.getString("desc_banco_inbursa"));
			//		cons.setClabeBenef(rs.getString("clabe_benef"));
					
			//		cons.setAba(rs.getString("aba"));
			//		cons.setSwiftCode(rs.getString("swift_code"));
		//			cons.setAbaIntermediario(rs.getString("aba_intermediario"));
		//			cons.setSwiftIntermediario(rs.getString("swift_intermediario"));
			//		cons.setAbaCorresponsal(rs.getString("aba_corresponsal"));
			//		cons.setSwiftCorresponsal(rs.getString("swift_corresponsal"));
			//		cons.setNomBancoIntermediario(rs.getString("nom_banco_intermediario"));
		//			cons.setNomBancoCorresponsal(rs.getString("nom_banco_corresponsal"));
			//		cons.setValidaCLABE(rs.getString("valida_clabe"));
					cons.setIdContratoMass(rs.getInt("id_contrato_mass"));
					cons.setRfcBenef(rs.getString("rfc_benef"));
					cons.setNoFactura(rs.getString("no_factura"));
					cons.setNomEmpresa(rs.getString("nom_empresa"));
			//		cons.setEspeciales(rs.getString("especiales")); 
					cons.setEquivalePersona(rs.getString("equivale_persona"));
			//		cons.setComplemento(rs.getString("complemento"));
					cons.setIdContratoWlink(rs.getString("id_contrato_wlink"));
			//		cons.setExisteChequeraProv(rs.getString("existe_chequera_prov"));
					cons.setRfc(rs.getString("rfc"));
					cons.setTipoEnvioLayout(rs.getInt("tipo_envio_layout"));
			//		cons.setDivisaChequera(rs.getString("divisa_chequera"));
					cons.setDireccionBenef(rs.getString("direccion_benef"));
					cons.setPaisBenef(rs.getString("pais_benef"));
					cons.setClave(rs.getString("clave"));
			//		cons.setIdChequeraBenefReal(obtenerChequeraBenefOficial(rs.getInt("id_banco_benef"), rs.getString("suc_destino"), 
	//					rs.getString("plaza_benef"), rs.getString("id_chequera_benef"), rs.getInt("id_banco")));
					cons.setHoraRecibo(funciones.obtenerHoraActual(false).substring(0, 5));
		//			cons.setDescUsuarioBital(pbH2HBital ? rs.getString("desc_usuario_bital") : "");
			//		cons.setDescServicioBital(pbH2HBital ? rs.getString("desc_servicio_bital") : "");
					cons.setNoFolioMov(rs.getInt("no_folio_mov"));
					
					cons.setFecValorStr(rs.getString("fec_valor"));
					cons.setFecValorOriginalStr(rs.getString("fec_valor_original"));
					cons.setFecOperacionStr(rs.getString("fec_operacion"));
					return cons;
				}
		    });
		     
		}catch(Exception e){
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) 
			+ "P:BancaElectronica, C:EnvioTransferenciasDao, M:consultarPagos");
		}
		return list;
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
	public String agregarCriteriosBusqueda(double montoIni, double montoFin, int idBancoReceptor, String fechaValor,
			String fechaValorOri, String idDivisa, String psDivision){
			StringBuffer sqlCriterios = new StringBuffer();
		try{
		    if(psDivision!=null && !psDivision.equals("")) 
		        sqlCriterios.append("\n AND m.division = '" +Utilerias.validarCadenaSQL( psDivision) + "'");
		            
		    if(montoIni>0 || montoFin>0)
		        sqlCriterios.append("\n AND m.importe between " + montoIni + " and " + montoFin);
		            
		    if(idBancoReceptor>0) 
		        sqlCriterios.append("\n AND m.id_banco_benef = " + idBancoReceptor);
		            
		    if(fechaValor!=null && !fechaValor.equals(""))
		        sqlCriterios.append("\n AND m.fec_valor = convert(datetime,'" +Utilerias.validarCadenaSQL(fechaValor)+ "',103)");
		            
		    if(fechaValorOri!=null && !fechaValorOri.equals(""))
		    	sqlCriterios.append("\n AND m.fec_valor_original = convert(datetime,'" +Utilerias.validarCadenaSQL(fechaValorOri)+ "',103)");
			
		    return sqlCriterios.toString();
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+"P:BancaElectronica, C:EnvioTransferenciasDao, M:agregarCriteriosBusqueda");
			return "";
		}
	}
}
