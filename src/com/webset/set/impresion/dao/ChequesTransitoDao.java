package com.webset.set.impresion.dao;

import java.util.List;
import java.util.Map;
import com.webset.set.impresion.dto.ChequesTransitoDto;
import com.webset.set.utilerias.dto.LlenaComboGralDto;

public interface ChequesTransitoDao {
	public List<ChequesTransitoDto> llenaGrid(String noEmpresa, String noBanco, String idChequera, String noCheque,String fechaIni, String fechaFin, String dias);
	public Map<String, Object> ejecutarRevividor(ChequesTransitoDto dto);
	public List<LlenaComboGralDto> llenarComboMotivos();
	public Map<String, Object> llenarParametro(ChequesTransitoDto cheque);
	public Map<String, Object> ejecutarGenerador(String noEmpresa, String noFolioParam);
	public boolean validarCancelar(String pwd, String usuario);
	public int insertarMotivo (String motivo, String noFolioDet);
	public String conciliarChequeCajaOCertificado(String noFolioDetalle);
	
	public String configuraSet(int indice);
}
