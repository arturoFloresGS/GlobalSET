package com.webset.set.egresos.service;

import java.util.List;

import com.webset.set.egresos.dto.MantenimientoBitacoraRentasDto;
import com.webset.set.utilerias.dto.LlenaComboGralDto;


public interface MantenimientoBitacoraRentasService {
	public List<MantenimientoBitacoraRentasDto> llenaGridBitacoraRentas(String noEmpresa, String noBeneficiario, String estatus);
	public List<LlenaComboGralDto> llenaComboEmpresa(String noUsuario);
	public List<LlenaComboGralDto> llenaComboProveedor(String nombre, String noProv);
	public String exportaExcel(String datos);
	public String updateMantenimientoBitacoraRentas(List<MantenimientoBitacoraRentasDto> listaResultado);
}
