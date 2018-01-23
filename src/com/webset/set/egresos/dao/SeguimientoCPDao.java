package com.webset.set.egresos.dao;

import java.util.List;
import java.util.Map;

import com.webset.set.egresos.dto.SeguimientoCPDto;

public interface SeguimientoCPDao {

	List<SeguimientoCPDto> llenaGrid(String idEmpresa, String idBeneficiario, String noDocumento, String periodo);

	List<SeguimientoCPDto> cadenaDeInformacion(String idEmpresa, String idBeneficiario, String noDocumento, String periodo);

	

}
