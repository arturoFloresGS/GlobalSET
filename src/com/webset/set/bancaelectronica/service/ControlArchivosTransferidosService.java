package com.webset.set.bancaelectronica.service;
import java.util.List;
import java.util.Map;

import com.webset.set.bancaelectronica.dto.ArchTransferDto;

public interface ControlArchivosTransferidosService {
	public List<ArchTransferDto> llenaComboArchivos(String fecValor, String bChequeOcurre);
	public String validaCriterios(int optTodas, int optCriterios, String fecArchivo, String fecInicial, String fecFinal, String nomArchivo, String noDocto);
	public List<ArchTransferDto> llenaGridArchivos(String bChequeOcurre, int optCriterios, String fecArchivo, String fecInicial, String fecFinal, String nomArchivo, String noDocto);
	public List<ArchTransferDto> llenaGridDetalle(String nomArchivo, int idBanco, boolean enviadasHoy, String bChequeOcurre);
	public Map<String, Object> regenerar(List<Map<String, String>> grid, List<Map<String, String>> registroCancelado, int registroTotal);
	public String configuraSet(int indice);
}
