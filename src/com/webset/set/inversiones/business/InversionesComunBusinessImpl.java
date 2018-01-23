package com.webset.set.inversiones.business;

import java.util.List;

import com.webset.set.inversiones.middleware.service.InversionesComunService;
import com.webset.set.inversiones.dto.LlenaComboValoresDto;
import com.webset.set.inversiones.dao.InversionesComunDao;
import com.webset.set.utilerias.Bitacora;

public class InversionesComunBusinessImpl implements InversionesComunService{
	Bitacora bitacora = new Bitacora();
	private InversionesComunDao inversionesComunDao;
	
	/* (non-Javadoc)
	 * @see com.webset.set.inversiones.middleware.service.InversionesComunService#consultarInstitucion()
	 */
	@Override
	public List<LlenaComboValoresDto> consultarInstitucion() {
		return this.getInversionesComunDao().consultarInstitucion();
	}
	
	/* (non-Javadoc)
	 * @see com.webset.set.inversiones.middleware.service.InversionesComunService#consultarTipoValor()
	 */
	@Override
	public List<LlenaComboValoresDto> consultarTipoValor() {
		return this.getInversionesComunDao().consultarTipoValor();
	}
	
	/* (non-Javadoc)
	 * @see com.webset.set.inversiones.middleware.service.InversionesComunService#consultarDivisas()
	 */
	@Override
	public List<LlenaComboValoresDto> consultarDivisas() {
		return this.getInversionesComunDao().consultarDivisa();
	}

	/**
	 * @return the inversionesComunDao
	 */
	public InversionesComunDao getInversionesComunDao() {
		return inversionesComunDao;
	}
	/**
	 * @param inversionesComunDao the inversionesComunDao to set
	 */
	public void setInversionesComunDao(InversionesComunDao inversionesComunDao) {
		this.inversionesComunDao = inversionesComunDao;
	}
	
}
