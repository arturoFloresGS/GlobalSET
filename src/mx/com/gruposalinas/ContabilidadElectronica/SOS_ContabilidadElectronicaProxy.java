package mx.com.gruposalinas.ContabilidadElectronica;

public class SOS_ContabilidadElectronicaProxy implements mx.com.gruposalinas.ContabilidadElectronica.SOS_ContabilidadElectronica {
  private String _endpoint = null;
  private mx.com.gruposalinas.ContabilidadElectronica.SOS_ContabilidadElectronica sOS_ContabilidadElectronica = null;
  
  public SOS_ContabilidadElectronicaProxy() {
    _initSOS_ContabilidadElectronicaProxy();
  }
  
  public SOS_ContabilidadElectronicaProxy(String endpoint) {
    _endpoint = endpoint;
    _initSOS_ContabilidadElectronicaProxy();
  }
  
  private void _initSOS_ContabilidadElectronicaProxy() {
    try {
      sOS_ContabilidadElectronica = (new mx.com.gruposalinas.ContabilidadElectronica.SOS_ContabilidadElectronicaServiceLocator()).getHTTPS_Port();
      if (sOS_ContabilidadElectronica != null) {
        if (_endpoint != null)
          ((javax.xml.rpc.Stub)sOS_ContabilidadElectronica)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
        else
          _endpoint = (String)((javax.xml.rpc.Stub)sOS_ContabilidadElectronica)._getProperty("javax.xml.rpc.service.endpoint.address");
      }
      
    }
    catch (javax.xml.rpc.ServiceException serviceException) {}
  }
  
  public String getEndpoint() {
    return _endpoint;
  }
  
  public void setEndpoint(String endpoint) {
    _endpoint = endpoint;
    if (sOS_ContabilidadElectronica != null)
      ((javax.xml.rpc.Stub)sOS_ContabilidadElectronica)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
    
  }
  
  public mx.com.gruposalinas.ContabilidadElectronica.SOS_ContabilidadElectronica getSOS_ContabilidadElectronica() {
    if (sOS_ContabilidadElectronica == null)
      _initSOS_ContabilidadElectronicaProxy();
    return sOS_ContabilidadElectronica;
  }
  
  public mx.com.gruposalinas.ContabilidadElectronica.DT_ContabilidadElectronica_ResponseContabilidadResponse[] SOS_ContabilidadElectronica(mx.com.gruposalinas.ContabilidadElectronica.DT_ContabilidadElectronica_OBContabilidadElectronica[] MT_ContabilidadElectronica_OB) throws java.rmi.RemoteException{
    if (sOS_ContabilidadElectronica == null)
      _initSOS_ContabilidadElectronicaProxy();
    return sOS_ContabilidadElectronica.SOS_ContabilidadElectronica(MT_ContabilidadElectronica_OB);
  }
  
  
}