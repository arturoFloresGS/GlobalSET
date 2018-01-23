package com.webset.set.egresos.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import com.webset.set.egresos.dao.SeguimientoCPDao;
import com.webset.set.egresos.dto.SeguimientoCPDto;
import com.webset.set.utilerias.Bitacora;
import com.webset.set.utilerias.Funciones;
import com.webset.utils.tools.Utilerias;

public class SeguimientoCPDaoImpl implements SeguimientoCPDao{
	
	Funciones funciones = new Funciones();
	JdbcTemplate jdbcTemplate;
	Bitacora bitacora = new Bitacora();
	String sql = "";
	
	public void setDataSource(DataSource dataSource){
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}

	@Override
	public List<SeguimientoCPDto> llenaGrid(String idEmpresa, String idBeneficiario, String noDocumento, String periodo) {
		List<SeguimientoCPDto> listaResultado = new ArrayList<SeguimientoCPDto>();
		StringBuffer sql = new StringBuffer();
		try{
			sql.append("select z.no_doc_sap,z.fec_fact,z.estatus_compensa,z.fecha_imp, \n");
			sql.append("z.estatus , z.causa_rech, z.secuencia, z.importe, z.id_divisa,z.forma_pago, \n");
			sql.append("z.no_benef,p.razon_social + ' ' + p.paterno + ' ' + p.materno + ' ' + p.nombre as Nombre, \n");
			sql.append("ca.desc_caja, desc_forma_pago, fecha_imp, estatus \n");
			sql.append("from zimp_fact z left join persona p on (z.no_benef=p.equivale_persona and p.id_tipo_persona='P') \n");
			sql.append("join cat_caja ca on z.id_caja=ca.id_caja join cat_forma_pago fp on z.forma_pago=fp.id_forma_pago \n");
			sql.append("where z.no_empresa=(Select SOIEMP from SET006 where SETEMP='"+Utilerias.validarCadenaSQL(idEmpresa)+"' and SISCOD='CP') \n");
			sql.append("and z.no_doc_sap='"+Utilerias.validarCadenaSQL(noDocumento)+"' \n");
			sql.append("and z.no_benef ='"+Utilerias.validarCadenaSQL(idBeneficiario)+ "' \n");
			
			if (!periodo.equals("")) {
				sql.append("and z.ejercicio ='"+Utilerias.validarCadenaSQL(periodo)+ "' ");
			}
			
			
			
			System.out.println(sql);
			
			listaResultado = jdbcTemplate.query(sql.toString(), new RowMapper<SeguimientoCPDto>(){
				public SeguimientoCPDto mapRow(ResultSet rs, int idx)throws SQLException{
					SeguimientoCPDto campos = new SeguimientoCPDto();
					campos.setNoDocumento(rs.getString("no_doc_sap"));
					campos.setDescBeneficiario(rs.getString("nombre"));
					campos.setDivisa(rs.getString("id_divisa"));
					campos.setImporte(rs.getString("importe"));
					campos.setFec_imp(rs.getString("fecha_imp"));
					campos.setEstatus(rs.getString("estatus"));
					return campos;
				}
			});
		}
		catch(Exception e){
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Egresos, C: SeguimientoCPDaoImpl, M: llenaGrid");
		}return listaResultado;
	}

	@Override
	public List<SeguimientoCPDto> cadenaDeInformacion(String idEmpresa, String idBeneficiario, String noDocumento, String periodo) {
		List<SeguimientoCPDto> listaResultado = new ArrayList<SeguimientoCPDto>();
		StringBuffer sql = new StringBuffer();
		try {
			
			sql.append("select 'M'AS ORIGEN,a.no_folio_det,a.no_empresa,desc_tipo_operacion,desc_forma_pago,desc_estatus \n");
			sql.append(",a.importe,cc.desc_caja,a.importe_original,a.id_divisa,a.id_divisa_original, \n");
			sql.append("case when a.id_estatus_mov in('X','Y','Z') then a.fec_modif else \n");
			sql.append("case when a.id_estatus_mov in('I') then a.fec_imprime else \n");
			sql.append("case when a.id_estatus_mov in('R') then a.fec_reimprime else \n");
			sql.append("case when a.id_estatus_mov in('T') then a.fec_trans else \n");
			sql.append("case when a.id_estatus_mov in('K') then a.fec_conf_trans else fec_valor \n");
			sql.append("end end end end end  as fec_valor, \n");
			sql.append("case when grupo_pago>0 then grupo_pago else 0 end  as grupo, \n");
			sql.append("case when a.cve_control<>'NULL'   then a.cve_control else '' end as propuesta \n");
			sql.append(",a.id_tipo_operacion,desc_divisa, \n");
			sql.append("cua.nombre + cua.paterno + cua.materno as desc_usuario,cb.desc_banco as banco,a.id_chequera, \n");
			sql.append("cum.nombre + cum.paterno + cum.materno as desc_usuario_mod, \n");
			sql.append("cb2.desc_banco as banco_benef, a.id_chequera_benef,a.origen_mov, \n");
			sql.append("cb2.desc_banco as banco_benef, a.id_chequera_benef,a.origen_mov, \n");
			sql.append("sag.fecha_propuesta,sag.concepto, sag.usuario_uno, sag.usuario_dos, sag.usuario_tres, sag.fecha_pago, --sag.fecha_aut_2, \n");
			sql.append("case WHEN usuario_uno IS null and usuario_dos IS null and usuario_tres IS null then \n");
			sql.append("'PENDIENTE' \n");
			sql.append("when usuario_uno  IS NOT  null and usuario_dos IS null and usuario_tres IS null then \n");
			sql.append("'CONGELADA' \n");
			sql.append("when usuario_uno IS NOT null and usuario_dos IS NOT null and usuario_tres IS null then \n");
			sql.append("'AUTORIZADA' \n");
			sql.append("when usuario_uno IS NOT null and usuario_dos IS NOT null \n");
			sql.append("then \n");
			sql.append("'PAGADA' \n");
			sql.append("end estatus_propuesta \n");
			sql.append("from movimiento a \n");
			sql.append("left join seleccion_automatica_grupo sag on sag.cve_control=a.cve_control \n");
			sql.append("left join cat_banco cb on (a.id_banco=cb.id_banco) \n");
			sql.append("left join cat_banco cb2 on (a.id_banco_benef=cb2.id_banco) \n");
			sql.append("left join cat_usuario cua on (a.usuario_alta = cua.no_usuario) \n");
			sql.append("left join cat_usuario cum on (a.usuario_modif = cum.no_usuario) \n");
			sql.append("left join cat_caja cc on (a.id_caja=cc.id_caja), \n");
			sql.append("cat_estatus b, cat_tipo_operacion c,cat_forma_pago d, \n");
			sql.append("cat_divisa e \n");
			sql.append("Where \n");
			sql.append("((a.id_estatus_mov <> 'H')  or (a.id_estatus_mov='H' and grupo_pago>0)) \n");
			sql.append("and a.id_tipo_operacion = c.id_tipo_operacion \n");
			sql.append("and a.id_forma_pago = d.id_forma_pago \n");
			sql.append("and a.id_divisa = e.id_divisa \n");
			sql.append("and a.id_estatus_mov = b.id_estatus \n");
			sql.append("and a.no_empresa ='"+Utilerias.validarCadenaSQL(idEmpresa)+"'\n");
			sql.append("and no_docto ='"+Utilerias.validarCadenaSQL(noDocumento)+"'\n");
			sql.append("and a.id_tipo_operacion <>3001 \n"); 
			sql.append("and b.clasificacion ='MOV' \n");
			sql.append("and a.id_tipo_movto ='E' \n");
			
			
			sql.append("UNION \n"); 
			sql.append("select 'S'AS ORIGEN,a.no_folio_det,a.no_empresa,desc_tipo_operacion,desc_forma_pago,desc_estatus \n");
			sql.append(",a.importe,cc.desc_caja,a.importe_original,a.id_divisa,a.id_divisa_original, \n");
			sql.append("case when a.id_estatus_mov in('X','Y','Z') then a.fec_modif else \n");
			sql.append("case when a.id_estatus_mov in('I') then a.fec_imprime else \n");
			sql.append("case when a.id_estatus_mov in('R') then a.fec_reimprime else \n");
			sql.append("case when a.id_estatus_mov in('T') then a.fec_trans else \n");
			sql.append("case when a.id_estatus_mov in('K') then a.fec_conf_trans else fec_valor \n");
			sql.append("end end end end end  as fec_valor , \n");
			sql.append("case when grupo_pago>0 then grupo_pago else 0 end  as grupo, \n");
			sql.append("case when a.cve_control<>'NULL'   then a.cve_control else '' end as propuesta \n");
			sql.append(",a.id_tipo_operacion,desc_divisa, \n");
			sql.append("cua.nombre + cua.paterno + cua.materno as desc_usuario,cb.desc_banco as banco,a.id_chequera, \n");
			sql.append("cum.nombre + cum.paterno + cum.materno as desc_usuario_mod, \n");
			sql.append("cb2.desc_banco as banco_benef, a.id_chequera_benef,a.origen_mov, \n");
			sql.append("cb2.desc_banco as banco_benef, a.id_chequera_benef,a.origen_mov, \n");
			sql.append("sag.fecha_propuesta,sag.concepto, sag.usuario_uno, sag.usuario_dos, sag.usuario_tres, sag.fecha_pago, --sag.fecha_aut_2, \n");
			sql.append("case WHEN usuario_uno IS null and usuario_dos IS null and usuario_tres IS null then \n");
			sql.append("'PENDIENTE' \n");
			sql.append("when usuario_uno  IS NOT  null and usuario_dos IS null and usuario_tres IS null then \n");
			sql.append("'CONGELADA' \n");
			sql.append("when usuario_uno IS NOT null and usuario_dos IS NOT null and usuario_tres IS null then \n");
			sql.append("'AUTORIZADA' \n");
			sql.append("when usuario_uno IS NOT null and usuario_dos IS NOT null and usuario_tres IS NOT null then \n");
			sql.append("'PAGADA' \n");
			sql.append("end estatus_propuesta \n");
			sql.append("from movimiento a  \n");
			sql.append("left join seleccion_automatica_grupo sag on sag.cve_control=a.cve_control \n");
			sql.append("left join cat_banco cb on (a.id_banco=cb.id_banco) \n");
			sql.append("left join cat_banco cb2 on (a.id_banco_benef=cb2.id_banco) \n");
			sql.append("left join cat_usuario cua on (a.usuario_alta = cua.no_usuario) \n");
			sql.append("left join cat_usuario cum on (a.usuario_modif = cum.no_usuario) \n");
			sql.append("left join cat_caja cc on (a.id_caja=cc.id_caja), \n");
			sql.append("cat_estatus b, cat_tipo_operacion c,cat_forma_pago d,  \n");
			sql.append("cat_divisa e \n");
			sql.append("Where \n");
			sql.append("((a.id_estatus_mov <> 'H')  or (a.id_estatus_mov='H' and grupo_pago>0)) \n");
			sql.append("and a.id_tipo_operacion = c.id_tipo_operacion \n");
			sql.append("and a.id_forma_pago = d.id_forma_pago \n");
			sql.append("and a.id_divisa = e.id_divisa \n");
			sql.append("and a.id_estatus_mov = b.id_estatus \n");
			sql.append("and a.no_empresa ='"+Utilerias.validarCadenaSQL(idEmpresa)+"'\n");
			sql.append("and a.id_tipo_operacion =3200 \n");
			sql.append("and b.clasificacion ='MOV' \n");
			sql.append("and a.id_tipo_movto ='E' \n");
			sql.append("and a.grupo_pago in (select grupo_pago from movimiento \n");
			sql.append("where no_docto ='"+Utilerias.validarCadenaSQL(noDocumento)+"'\n");
			sql.append("and no_empresa='"+Utilerias.validarCadenaSQL(idEmpresa)+"' ) \n");
                     
                  
			sql.append("UNION ALL \n");
			sql.append("select 'H'AS ORIGEN,a.no_folio_det,a.no_empresa,desc_tipo_operacion,desc_forma_pago,desc_estatus \n");
			sql.append(",a.importe,cc.desc_caja,a.importe_original,a.id_divisa,a.id_divisa_original, \n");
			sql.append("case when a.id_estatus_mov in('X','Y','Z') then a.fec_modif else \n");
			sql.append("case when a.id_estatus_mov in('I') then a.fec_imprime else \n");
			sql.append("case when a.id_estatus_mov in('R') then a.fec_reimprime else \n");
			sql.append("case when a.id_estatus_mov in('T') then a.fec_trans else \n");
			sql.append("case when a.id_estatus_mov in('K') then a.fec_conf_trans else fec_valor \n");
			sql.append("end end end end end  as fec_valor , \n");
			sql.append("case when grupo_pago>0 then grupo_pago else 0 end  as grupo, \n");
			sql.append("case when a.cve_control<>'NULL'   then a.cve_control else '' end as propuesta \n");
			sql.append(",a.id_tipo_operacion,desc_divisa, \n");
			sql.append("cua.nombre + cua.paterno + cua.materno as desc_usuario,cb.desc_banco as banco,a.id_chequera, \n");
			sql.append("cum.nombre + cum.paterno + cum.materno as desc_usuario_mod, \n");
			sql.append("cb2.desc_banco as banco_benef, a.id_chequera_benef,a.origen_mov, \n");
			sql.append("cb2.desc_banco as banco_benef, a.id_chequera_benef,a.origen_mov, \n");
			sql.append("sag.fecha_propuesta,sag.concepto, sag.usuario_uno, sag.usuario_dos, sag.usuario_tres, sag.fecha_pago, \n"); 
			sql.append("case WHEN usuario_uno IS null and usuario_dos IS null and usuario_tres IS null then \n");
			sql.append("'PENDIENTE' \n");
			sql.append("when usuario_uno  IS NOT  null and usuario_dos IS null and usuario_tres IS null then \n");
			sql.append("'CONGELADA' \n");
			sql.append("when usuario_uno IS NOT null and usuario_dos IS NOT null and usuario_tres IS null then \n");
			sql.append("'AUTORIZADA' \n");
			sql.append("when usuario_uno IS NOT null and usuario_dos IS NOT null and usuario_tres IS NOT null then \n");
			sql.append("'PAGADA' \n");
			sql.append("end estatus_propuesta \n");
			sql.append("from hist_movimiento a  \n");
			sql.append("left join seleccion_automatica_grupo sag on sag.cve_control=a.cve_control \n");
			sql.append("left join cat_banco cb on (a.id_banco=cb.id_banco) \n");
			sql.append("left join cat_banco cb2 on (a.id_banco_benef=cb2.id_banco) \n");
			sql.append("left join cat_usuario cua on (a.usuario_alta = cua.no_usuario) \n");
			sql.append("left join cat_usuario cum on (a.usuario_modif = cum.no_usuario) \n");
			sql.append("left join cat_caja cc on (a.id_caja=cc.id_caja), \n");
			sql.append("cat_estatus b, cat_tipo_operacion c,cat_forma_pago d,  \n");
			sql.append("cat_divisa e \n");
			sql.append("Where \n");
			
			sql.append("((a.id_estatus_mov <> 'H')  or (a.id_estatus_mov='H' and grupo_pago>0)) \n");
			sql.append("and a.id_tipo_operacion = c.id_tipo_operacion \n");
			sql.append("and a.id_forma_pago = d.id_forma_pago \n");
			sql.append("and a.id_divisa = e.id_divisa \n");
			sql.append("and a.id_estatus_mov = b.id_estatus \n");
			sql.append("and a.no_empresa ='"+Utilerias.validarCadenaSQL(idEmpresa)+"'\n");
			sql.append("and a.id_tipo_operacion =3200  \n");
			sql.append("and b.clasificacion ='MOV' \n");
			sql.append("and a.id_tipo_movto ='E' \n");
			sql.append("and a.contrarecibo =' 001 ' \n");
			sql.append("and a.grupo_pago in (select grupo_pago from movimiento \n");
			sql.append("where no_docto ='"+Utilerias.validarCadenaSQL(noDocumento)+"'\n");
			sql.append("and no_empresa='"+Utilerias.validarCadenaSQL(idEmpresa)+"')\n");

			sql.append("ORDER BY no_folio_det,fec_valor \n");
		        
			
			System.out.println(sql);
			
			listaResultado = jdbcTemplate.query(sql.toString(), new RowMapper<SeguimientoCPDto>(){
				public SeguimientoCPDto mapRow(ResultSet rs, int idx)throws SQLException{
					SeguimientoCPDto campos = new SeguimientoCPDto();
					campos.setOrigen(rs.getString("origen"));
					campos.setNoFolio(rs.getString("no_folio_det"));
					campos.setIdEmpresa(rs.getString("no_empresa"));
					campos.setTipoOperacion("desc_tipo_operacion");
					campos.setDescFormaPago(rs.getString("desc_forma_pago"));
					campos.setDescEstatus(rs.getString("desc_estatus"));
					campos.setImporte(rs.getString("importe"));
					campos.setDescCaja(rs.getString("desc_caja"));
					campos.setId_divisa(rs.getString("id_divisa"));
					campos.setFechaV(rs.getString("fec_valor"));
					campos.setGrupo(rs.getString("grupo"));
					campos.setCveControl(rs.getString("propuesta"));
					campos.setIdTipoOperacion(rs.getString("id_tipo_operacion"));
					campos.setDescUsuario(rs.getString("desc_usuario"));
					campos.setOrigenMov(rs.getString("origen_mov"));
					campos.setFechaPropuesta(rs.getString("fecha_pago"));
					campos.setUsuarioUno(rs.getString("usuario_uno"));
					campos.setDescUsuarioMod(rs.getString("desc_usuario_mod"));
					campos.setBancoBenef(rs.getString("banco_benef"));
					campos.setIdChequeraBenef(rs.getString("id_chequera_benef"));
					campos.setBanco(rs.getString("banco"));
					campos.setIdChequera(rs.getString("id_chequera"));
					return campos;
				}
			});
			
		} catch (Exception e) {
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Egresos, C: SeguimientoCPDaoImpl, M: cadenaDeInformacion");
		}return listaResultado;
	}



}
