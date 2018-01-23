package com.webset.set.egresos.dao;

import java.util.List;
import com.webset.set.egresos.dto.MantenimientoBitacoraRentasDto;
import com.webset.set.utilerias.dto.LlenaComboGralDto;

public interface MantenimientoBitacoraRentasDao {
	public List<MantenimientoBitacoraRentasDto> llenaGridBitacoraRentas(String noEmpresa, String noBeneficiario, String estatus);
	public String updateMantenimientoBitacoraRentas(List<MantenimientoBitacoraRentasDto> dtos);
	// llenar combo para empresas y beneficiarios
	public List<LlenaComboGralDto> llenaComboEmpresa(String noUsuario);
	public List<LlenaComboGralDto> llenaComboProveedor(String nombre, String noProv);
}
