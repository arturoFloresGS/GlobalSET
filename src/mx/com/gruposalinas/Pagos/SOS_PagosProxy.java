package mx.com.gruposalinas.Pagos;

public class SOS_PagosProxy implements mx.com.gruposalinas.Pagos.SOS_Pagos {
  private String _endpoint = null;
  private mx.com.gruposalinas.Pagos.SOS_Pagos sOS_Pagos = null;
  
  public SOS_PagosProxy() {
    _initSOS_PagosProxy();
  }
  
  public SOS_PagosProxy(String endpoint) {
    _endpoint = endpoint;
    _initSOS_PagosProxy();
  }
  
  private void _initSOS_PagosProxy() {
    try {
      sOS_Pagos = (new mx.com.gruposalinas.Pagos.SOS_PagosServiceLocator()).getHTTPS_Port();
      if (sOS_Pagos != null) {
        if (_endpoint != null)
          ((javax.xml.rpc.Stub)sOS_Pagos)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
        else
          _endpoint = (String)((javax.xml.rpc.Stub)sOS_Pagos)._getProperty("javax.xml.rpc.service.endpoint.address");
      }
      
    }
    catch (javax.xml.rpc.ServiceException serviceException) {}
  }
  
  public String getEndpoint() {
    return _endpoint;
  }
  
  public void setEndpoint(String endpoint) {
    _endpoint = endpoint;
    if (sOS_Pagos != null)
      ((javax.xml.rpc.Stub)sOS_Pagos)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
    
  }
  
  public mx.com.gruposalinas.Pagos.SOS_Pagos getSOS_Pagos() {
    if (sOS_Pagos == null)
      _initSOS_PagosProxy();
    return sOS_Pagos;
  }
  
  public mx.com.gruposalinas.Pagos.DT_Pagos_ResponsePagosResponse[] SOS_Pagos(mx.com.gruposalinas.Pagos.DT_Pagos_OBPagos[] MT_Pagos_OB) throws java.rmi.RemoteException{
    if (sOS_Pagos == null)
      _initSOS_PagosProxy();
    return sOS_Pagos.SOS_Pagos(MT_Pagos_OB);
  }
  
  
}