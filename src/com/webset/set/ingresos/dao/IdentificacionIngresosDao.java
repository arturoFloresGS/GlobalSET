package com.webset.set.ingresos.dao;

import java.util.List;
import java.util.Map;

import com.webset.set.conciliacionbancoset.dto.ConciliaBancoDto;
import com.webset.set.conciliacionbancoset.dto.CriteriosBusquedaDto;
import com.webset.set.ingresos.dto.CuentaContableDto;
import com.webset.set.seguridad.dto.PersonaDto;
import com.webset.set.utilerias.dto.LlenaComboChequeraDto;
import com.webset.set.utilerias.dto.LlenaComboGralDto;
import com.webset.set.utilerias.dto.MovimientoDto;

public interface IdentificacionIngresosDao {

	List<LlenaComboGralDto> llenarComboBancos(int noEmpresa);
	List<CuentaContableDto> getCuentaContable(int noEmpresa, String idGrupo, String idRubro);
	List<LlenaComboChequeraDto> llenarCmbChequeras(int iBanco, int iEmpresa, int iOpc);
	List<ConciliaBancoDto> llenarMovsEmpresa(CriteriosBusquedaDto dto);
	List<LlenaComboGralDto> llenarComboDivisa();
	List<MovimientoDto> llenaComboCuentaContable();
	List<ConciliaBancoDto> llenaComboCuentaContable2();
	List<ConciliaBancoDto> llenaComboOrigen();
	List<MovimientoDto> llenarMovsBancos(CriteriosBusquedaDto dto);
	List<MovimientoDto> llenarMovsBancos2(CriteriosBusquedaDto dto);
	Map<String, String> conciliaBancosEmpresa(List<ConciliaBancoDto> listCobranza, List<MovimientoDto> listBanco);
	Map<String, String> parcializarBancos(List<ConciliaBancoDto> listCobranza, double diferencia2);
	Map<String, String> parcializarBancos2( List<MovimientoDto> listBanco, double diferencia);
	Map<String, String> conciliaVirtualCobranza(List<ConciliaBancoDto> listCobranza, List<MovimientoDto> listBanco, String causa, String origen, String cuenta);
	Map<String, String> conciliaVirtualBanco(List<ConciliaBancoDto> listCobranza, List<MovimientoDto> listBanco, String causa, String origen, String cuenta);
	Map<String, String> concilVirSBanco( List<MovimientoDto> listBanco, String causa, String cuenta);
	Map<String, String> concilVirSCobranza(List<ConciliaBancoDto> listCobranza,String causa, String cuenta);
	List<ConciliaBancoDto> datosExcel(int empresa, String fechaIni, String fechaFin);
	List<Map<String, String>> obtieneFacturas(int empresa, String fecIni, String fecFin);
	List<Map<String, String>> obtieneDepositos(int empresa, String fecIni, String fecFin);
	List<Map<String, String>> obtieneConciliados(int empresa, String fecIni, String fecFin);
	List<PersonaDto> obtenerClientes(String cliente);
	String enviarAnticipo(int folioDet, String cliente, double importe, int noEmpresa, String divisa);

}
