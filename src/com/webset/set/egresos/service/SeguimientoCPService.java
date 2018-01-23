package com.webset.set.egresos.service;

import java.util.List;

import com.webset.set.egresos.dto.SeguimientoCPDto;

public interface SeguimientoCPService {

	List<SeguimientoCPDto> llenaGrid(String idEmpresa, String idBeneficiario, String noDocumento, String periodo);

	String cadenaDeInformacion(String idEmpresa, String idBeneficiario, String noDocumento, String periodo);

}
