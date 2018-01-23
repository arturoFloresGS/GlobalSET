package com.webset.set.inversiones.middleware.service;

import java.util.List;

import com.webset.set.inversiones.dto.PosicionInversionDto;
import com.webset.set.utilerias.dto.LlenaComboGralDto;

public interface PosicionInversionService {

	List<LlenaComboGralDto> consultarEmpresa(boolean bExistentes);

	List<LlenaComboGralDto> consultarDivisa(int empresa);

	List<LlenaComboGralDto> consultarBanco(int emp, String divisa);

	List<LlenaComboGralDto> consultarChequera(int emp, String divisa, int banco);

	String consultarInstitucion();

	List<PosicionInversionDto> llenarGrid1(String fecha, int caja, String institucion, int empresa, String divisa,
			int banco, String chequera);

	List<PosicionInversionDto> llenarGrid2(String fecha, int caja, String institucion, int empresa, String divisa,
			int banco, String chequera);

	List<PosicionInversionDto> llenarGrid3(String fecha, int caja, String institucion, int empresa, String divisa,
			int banco, String chequera);

	List<PosicionInversionDto> llenarGrid4(String fecha, int caja, String institucion, int empresa, String divisa,
			int banco, String chequera);

	int consultarSaldoInicial(int banco, String chequera);




}
