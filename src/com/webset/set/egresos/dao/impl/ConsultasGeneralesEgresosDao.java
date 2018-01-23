package com.webset.set.egresos.dao.impl;

import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;

import com.webset.set.utilerias.Bitacora;
import com.webset.set.utilerias.ConsultasGenerales;
import com.webset.set.utilerias.dto.LlenaComboGralDto;

public class ConsultasGeneralesEgresosDao {
	private JdbcTemplate jdbcTemplate;
	Bitacora bitacora=new Bitacora();
	
	public ConsultasGeneralesEgresosDao (JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	public List<LlenaComboGralDto>llenarComboBeneficiarioParametrizado(LlenaComboGralDto dto, String grupo, String rubro){
		//System.out.println("entro a Dao parametrizado");
		String cond="";
		cond=dto.getCondicion();
		dto.setCampoDos("case when SUBSTRING(equivale_persona, 3,1) = 'P' then \n"
				+ " cast( coalesce((select SETEMP \n"
				+ "				from set006  \n"
				+ "			where SOIEMP = SUBSTRING( p.equivale_persona,1,2)),'') as varchar) + ' - ' + \n"
				+ "			 rTRIM(COALESCE(P.razon_social,''))				\n"
				+ "	else '[' + substring(p.equivale_persona, 2, 3) + '] ' + rTRIM(COALESCE(P.razon_social,'')) end \n");
		
		if(dto.isRegistroUnico())
		{
			dto.setCondicion("p.equivale_persona="+cond
					+ " AND s.id_poliza = " + dto.getIdStr()
					+ " And S.Id_Grupo = "+ grupo
					+ " and s.id_rubro = "+rubro);
			
		}else{
			dto.setCondicion("p.id_tipo_persona='P'	\n"
					+"	AND p.no_empresa in(552,217) \n"
					+"	AND ((p.razon_social like '"+cond+"%' \n"     
					+"	or p.paterno like '"+cond+"%' \n" 
					+"	or p.materno like '"+cond+"%' \n"   
					+"	or p.nombre like '"+cond+"%' ) \n" 
					+"	or (p.equivale_persona like '"+cond+"%')) \n");	
		}
		
		ConsultasGenerales consultasGenerales = new ConsultasGenerales(jdbcTemplate);
		return consultasGenerales.llenarComboGral(dto);
	}
	
	public List<LlenaComboGralDto>llenarComboBeneficiario(LlenaComboGralDto dto){
		
		String cond="";
		dto.setTabla("persona p");
		cond=dto.getCondicion();
		dto.setCampoDos("COALESCE(p.razon_social,'')");
		
		if(dto.isRegistroUnico()){
			dto.setCondicion("p.equivale_persona= convert(varchar,'"+cond+"')");
		}else{
			dto.setCondicion("p.id_tipo_persona='P'	"
					+"	AND p.no_empresa in(552,217)"
					+"	AND ((p.razon_social like '"+cond+"%'"     
					+"	or p.paterno like '"+cond+"%'" 
					+"	or p.materno like '"+cond+"%'"   
					+"	or p.nombre like '"+cond+"%' )" 
					+"	or (p.equivale_persona like '"+cond+"%'))");	
		}
		
		ConsultasGenerales consultasGenerales = new ConsultasGenerales(jdbcTemplate);
		return consultasGenerales.llenarComboGral(dto);
	}
	
	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}
	
	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}
}
