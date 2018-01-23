/**
 * SOS_PagosService.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package mx.com.gruposalinas.Pagos;

public interface SOS_PagosService extends javax.xml.rpc.Service {
    public java.lang.String getHTTPS_PortAddress();

    public mx.com.gruposalinas.Pagos.SOS_Pagos getHTTPS_Port() throws javax.xml.rpc.ServiceException;

    public mx.com.gruposalinas.Pagos.SOS_Pagos getHTTPS_Port(java.net.URL portAddress) throws javax.xml.rpc.ServiceException;
    public java.lang.String getHTTP_PortAddress();

    public mx.com.gruposalinas.Pagos.SOS_Pagos getHTTP_Port() throws javax.xml.rpc.ServiceException;

    public mx.com.gruposalinas.Pagos.SOS_Pagos getHTTP_Port(java.net.URL portAddress) throws javax.xml.rpc.ServiceException;
}
