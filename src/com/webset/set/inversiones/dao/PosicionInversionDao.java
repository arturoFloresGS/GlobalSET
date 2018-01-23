package com.webset.set.inversiones.dao;

import java.util.List;

import com.webset.set.inversiones.dto.PosicionInversionDto;
import com.webset.set.utilerias.dto.LlenaComboGralDto;

public interface PosicionInversionDao {

	List<LlenaComboGralDto> consultarEmpresa(boolean bExistentes);

	List<LlenaComboGralDto> consultarDivisa(int empresa);

	List<LlenaComboGralDto> consultarBanco(int emp, String divisa);

	List<LlenaComboGralDto> consultarChequera(int emp, String divisa, int banco);

	String consultarInstitucion();

	List<PosicionInversionDto> consultarVencimientos(String fecha, int caja, String institucion, int empresa,
			String divisa, int banco, String chequera);

	List<PosicionInversionDto> consultarBarridos(String fecha, int caja, String institucion, int empresa, String divisa,
			int banco, String chequera);

	List<PosicionInversionDto> consultarPorInvertir(String fecha, int caja, String institucion, int empresa,
			String divisa, int banco, String chequera);

	List<PosicionInversionDto> consultarInversiones(String fecha, int caja, String institucion, int empresa,
			String divisa, int banco, String chequera);

	int saldoInicial(int banco, String chequera);

}
