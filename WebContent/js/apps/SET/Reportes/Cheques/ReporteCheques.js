/*
@author: Jessica Arelly Cruz Cruz
@since: 11/07/2011
*/
Ext.onReady(function(){
	Ext.QuickTips.init();
	Ext.Direct.addProvider(Ext.setapp.REMOTING_API);
	var NS = Ext.namespace('apps.SET.Reportes.Cheques.ReporteCheques');
	NS.tabContId = apps.SET.tabContainerId;	
	var PF = apps.SET.tabID+'.';
	
	NS.GI_USUARIO = apps.SET.iUserId ;
	NS.GI_ID_CAJA = apps.SET.ID_CAJA;
	NS.GI_ID_EMPRESA = apps.SET.NO_EMPRESA;
	
	NS.chequera = '';
	NS.empresas = '';
	NS.divisa = '';
	NS.cajas = '';
	NS.bancoInf = '';
	NS.bancoSup = '';
	NS.fechaIni = '';
	NS.fechaFin = '';
	NS.estatusMov = '';
	NS.estatusEntregado = '';
	NS.origen = '';
	NS.agrupado = 'BANCO';
	NS.estatusCb = '';
	NS.ordenado = 'MONTO';
	NS.nomEmpresa = '';
	NS.descEstatus = 'Por imprimir';
	NS.subtitulo = '';
	NS.excel = false;
	
	//Etiqueta Cheque inicio
	NS.labCheIni = new Ext.form.Label({
		text: 'No. Cheques:',
        x: 440,
        y: 5
	});
    //Caja Cheque inicio
	NS.txtCheIni = new Ext.form.TextField({
		id: PF + 'txtCheIni',
		x: 440,
        y: 20,
        width: 60,
        tabIndex: 0
    });
	
	//Caja Cheque fin
	NS.txtCheFin = new Ext.form.TextField({
		id: PF + 'txtCheFin',
		x: 505,
        y: 20,
        width: 70,
        tabIndex: 1
    });
	
	//store empresa
	NS.storeEmpresa = new Ext.data.DirectStore({
		paramsAsHash: false,
		root: '',
		baseParams:{usuario : NS.GI_USUARIO},
		paramOrder:['usuario'],
		directFn: ReportesAction.llenarComboEmpresa,
		idProperty: 'noEmpresa', 
		fields: [
			 {name: 'noEmpresa'},
			 {name: 'nomEmpresa'}
		],
		listeners: {
			load: function(s, records){
				//Se agrega la opcion todas las empresas
	 			var recordsStoreEmpresas = NS.storeEmpresa.recordType;	
				var todas = new recordsStoreEmpresas({
					noEmpresa: 0,
					nomEmpresa: '*************** TODAS ***************'
		       	});
		   		NS.storeEmpresa.insert(0,todas);
			}
		}
	}); 
	
	//combo Empresa
	NS.storeEmpresa.load();
	NS.cmbEmpresa = new Ext.form.ComboBox({
		store: NS.storeEmpresa
		,name: PF+'cmbEmpresa'
		,id: PF+'cmbEmpresa'
		,typeAhead: true
		,mode: 'local'
		,minChars: 0
		,selecOnFocus: true
		,forceSelection: true
        ,x: 120
        ,y: 20
        ,width: 300
		,valueField:'noEmpresa'
		,displayField:'nomEmpresa'
		,autocomplete: true
		,emptyText: 'Seleccione una empresa'
		,triggerAction: 'all'
		,value: ''
		,visible: false
		,listeners:{
			select:{
				fn: 
				function(combo, valor) {
					//linea cambia combo
					BFwrk.Util.updateComboToTextField(PF+'txtEmpresa',NS.cmbEmpresa.getId());
					NS.accionarCmbEmpresa(combo.getValue());
				}
			}
		}
	});
	
	//store cajas
	NS.storeCaja = new Ext.data.DirectStore({
		paramsAsHash: false,
		root: '',
		baseParams:{usuario : NS.GI_USUARIO},
		paramOrder:['usuario'],
		directFn: ReportesAction.llenarComboCajas,
		idProperty: 'id', 
		fields: [
			 {name: 'id'},
			 {name: 'descripcion'}
		],
		listeners: {
			load: function(s, records){
			}
		}
	}); 
	
	//combo caja
	NS.storeCaja.load();
	NS.cmbCaja = new Ext.form.ComboBox({
		store: NS.storeCaja
		,name: PF+'cmbCaja'
		,id: PF+'cmbCaja'
		,typeAhead: true
		,mode: 'local'
		,minChars: 0
		,selecOnFocus: true
		,forceSelection: true
        ,x: 400
        ,y: 60
        ,width: 180
		,valueField:'id'
		,displayField:'descripcion'
		,autocomplete: true
		,emptyText: 'Seleccione una caja'
		,triggerAction: 'all'
		,value: ''
		,visible: false
		,listeners:{
			select:{
				fn: 
				function(combo, valor) {
					NS.cajas = combo.getValue();
				}
			}
		}
	});
	
	//store divisas
	NS.storeDivisa = new Ext.data.DirectStore({
		paramsAsHash: false,
		baseParams:{
				condicion:''
		},
		root: '',
		paramOrder:['condicion'],
		directFn: GlobalAction.llenarComboDivisas, 
		idProperty: 'idDivisa', 
		fields: [
			 {name: 'idDivisa'},
			 {name: 'descDivisa'}
		],
		listeners: {
			load: function(s, records){
				
			}
		}
	}); 
	
	//combo banco
	NS.storeDivisa.load();
	NS.cmbDivisa = new Ext.form.ComboBox({
		store: NS.storeDivisa
		,name: PF+'cmbDivisa'
		,id: PF+'cmbDivisa'
		,typeAhead: true
		,mode: 'local'
		,minChars: 0
		,selecOnFocus: true
		,forceSelection: true
        ,x: 120
        ,y: 60
        ,width: 200
		,valueField:'idDivisa'
		,displayField:'descDivisa'
		,autocomplete: true
		,emptyText: 'Seleccione una divisa'
		,triggerAction: 'all'
		,value: ''
		,hidden: false
		,listeners:{
			select:{
				fn: 
				function(combo, valor) {
					BFwrk.Util.updateComboToTextField(PF+'txtDivisa',NS.cmbDivisa.getId());
					NS.accionarCmbDivisa(combo.getValue());
				}
			}
		}
	});
	
	//store origen
	NS.storeOrigen = new Ext.data.DirectStore({
		paramsAsHash: false,
		baseParams:{
				tipoMovto:'E'
		},
		root: '',
		paramOrder:['tipoMovto'],
		directFn: ReportesAction.llenarComboOrigen, 
		idProperty: 'id', 
		fields: [
			 {name: 'id'},
			 {name: 'descripcion'}
		],
		listeners: {
			load: function(s, records){
				
			}
		}
	}); 
	
	//combo origen
	NS.storeOrigen.load();
	NS.cmbOrigen = new Ext.form.ComboBox({
		store: NS.storeOrigen
		,name: PF+'cmbOrigen'
		,id: PF+'cmbOrigen'
		,typeAhead: true
		,mode: 'local'
		,minChars: 0
		,selecOnFocus: true
		,forceSelection: true
        ,x: 400
        ,y: 140
        ,width: 180
		,valueField:'id'
		,displayField:'descripcion'
		,autocomplete: true
		,emptyText: 'Seleccione un origen'
		,triggerAction: 'all'
		,value: ''
		,hidden: false
		,listeners:{
			select:{
				fn: 
				function(combo, valor) {
					NS.origen = combo.getValue();
//					BFwrk.Util.updateComboToTextField(PF+'txtBanco',NS.cmbBanco.getId());
//					NS.accionarCmbBanco(combo.getValue());
				}
			}
		}
	});
	
	//store banco
	NS.storeBanco = new Ext.data.DirectStore({
		paramsAsHash: false,
		baseParams:{
				empresa: '',
				divisa: ''
		},
		root: '',
		paramOrder:['empresa', 'divisa'],
		directFn: ReportesAction.llenarComboBancos, 
		idProperty: 'id', 
		fields: [
			 {name: 'id'},
			 {name: 'descripcion'}
		],
		listeners: {
			load: function(s, records){
				
			}
		}
	}); 
	
	//combo banco
	NS.cmbBanco = new Ext.form.ComboBox({
		store: NS.storeBanco
		,name: PF+'cmbBanco'
		,id: PF+'cmbBanco'
		,typeAhead: true
		,mode: 'local'
		,minChars: 0
		,selecOnFocus: true
		,forceSelection: true
        ,x: 120
        ,y: 100
        ,width: 200
		,valueField:'id'
		,displayField:'descripcion'
		,autocomplete: true
		,emptyText: 'Seleccione un banco'
		,triggerAction: 'all'
		,value: ''
		,hidden: false
		,listeners:{
			select:{
				fn: 
				function(combo, valor) {
					BFwrk.Util.updateComboToTextField(PF+'txtBanco',NS.cmbBanco.getId());
					NS.accionarCmbBanco(combo.getValue());
				}
			}
		}
	});
	
	//store chequeras
	NS.storeChequera = new Ext.data.DirectStore({
		paramsAsHash: false,
		baseParams:{
				banco: '',
				empresa: ''
		},
		root: '',
		paramOrder:['banco', 'empresa'],
		directFn: ReportesAction.llenarComboChequeras, 
		idProperty: 'id', 
		fields: [
			 {name: 'id'},
			 {name: 'descripcion'}
		],
		listeners: {
			load: function(s, records){
				
			}
		}
	}); 
	
	//combo banco
	NS.cmbChequera = new Ext.form.ComboBox({
		store: NS.storeChequera
		,name: PF+'cmbChequera'
		,id: PF+'cmbChequera'
		,typeAhead: true
		,mode: 'local'
		,minChars: 0
		,selecOnFocus: true
		,forceSelection: true
        ,x: 400
        ,y: 100
        ,width: 180
		,valueField:'id'
		,displayField:'descripcion'
		,autocomplete: true
		,emptyText: 'Seleccione una chequera'
		,triggerAction: 'all'
		,value: ''
		,hidden: false
		,listeners:{
			select:{
				fn: 
				function(combo, valor) {
					NS.chequera = combo.getValue();
				}
			}
		}
	});
	
	//funciones
	NS.accionarCmbEmpresa = function(comboValor){
		NS.storeBanco.baseParams.empresa = comboValor;
		NS.storeChequera.baseParams.empresa = comboValor;
		
		if(comboValor == 0)
			NS.empresas = '';
		else
			NS.empresas = comboValor;
		NS.nomEmpresa = BFwrk.Util.scanFieldsStore(NS.storeEmpresa, 'noEmpresa', comboValor, 'nomEmpresa');
	}
	
	NS.accionarCmbDivisa = function(comboValor){
		NS.storeBanco.baseParams.divisa = comboValor;
		NS.storeBanco.load();
		NS.divisa = comboValor;
	}
	
	NS.accionarCmbBanco = function(comboValor){
		NS.storeChequera.baseParams.banco = comboValor;
		NS.storeChequera.load();
		NS.bancoInf = comboValor;
		NS.bancoSup = comboValor;
	}
	
	NS.escribeSubtitulo = function(texto){
		if(NS.fechaIni == NS.fechaFin)
		{
			NS.subtitulo = 'Cheques ' + texto + ' al '+ NS.fechaIni;
		}
		else
		{
			NS.subtitulo = 'Cheques ' + texto + ' del ' + NS.fechaIni + ' al ' + NS.fechaFin;
		}
	}
	
	NS.validarCriterios = function(){
		// Validacion de empresa
		//if(Ext.getCmp(PF+'optEmpresaActual').getValue() == true)
		//{
			if(Ext.getCmp(PF+'txtEmpresa').getValue() == '')
			{
				Ext.Msg.show({
					title: 'SET',
					msg: 'Seleccione una empresa',
					icon: Ext.MessageBox.WARNING,
					buttons: Ext.MessageBox.OK
				});
			
				//Ext.Msg.alert('SET','Seleccione una empresa');
	            return;
	        }
        //}
        //else
        //	NS.empresas = '';
	
	    // Validacion de divisa
	    if(Ext.getCmp(PF+'txtDivisa').getValue() == '')
	    {
	    	Ext.Msg.show({
				title: 'SET',
				msg: 'Seleccione una divisa',
				icon: Ext.MessageBox.WARNING,
				buttons: Ext.MessageBox.OK
			});
	       // Ext.Msg.alert('SET','Seleccione una divisa');
            return;
	    }
	
	     // Validacion de FecIniciales
     	if(Ext.getCmp(PF+'txtFechaDesde').getValue() !== '')
     	{
     		var fecIni = Ext.getCmp(PF+'txtFechaDesde').getValue();
     		var fecFin = Ext.getCmp(PF+'txtFechaHasta').getValue();
     		NS.fechaIni = BFwrk.Util.changeDateToString(''+ Ext.getCmp(PF+'txtFechaDesde').getValue());
     		NS.fechaFin = BFwrk.Util.changeDateToString(''+ Ext.getCmp(PF+'txtFechaHasta').getValue());
     		if(fecIni > fecFin)
     		{
     			Ext.Msg.show({
					title: 'SET',
					msg: 'La segunda fecha debe ser mayor que la primera',
					icon: Ext.MessageBox.WARNING,
					buttons: Ext.MessageBox.OK
				});
     			//Ext.Msg.alert('SET','La segunda fecha debe ser mayor que la primera');
     			return;
     		}
     	}
     	else
     	{
     		Ext.Msg.show({
				title: 'SET',
				msg: 'Se utilizara por default la fecha de hoy',
				icon: Ext.MessageBox.INFO,
				buttons: Ext.MessageBox.OK
			});	
			NS.fechaIni = BFwrk.Util.changeDateToString(''+ apps.SET.FEC_HOY);
			NS.fechaFin = NS.fechaIni;
     		//Ext.Msg.alert('SET','Se utilizara por default la fecha de hoy');
     	}
     	
	    // validacion de banco
	    if(Ext.getCmp(PF+'txtBanco').getValue() == '')
	    {
	    	NS.bancoInf = 0;
			NS.bancoSup = 32000;
		}
	
	    // validacion de chequera
	    if(Ext.getCmp(PF+'cmbChequera').getValue() == '')
	    	NS.chequera = '%25';
	
		// validacion cajas
		if(Ext.getCmp(PF+'cmbCaja').getValue() == '')
			NS.cajas = ''
		
	    // valida opcion seleccionada
	    var value = Ext.getCmp(PF+'estatus').getValue();
	    var valor = value.getGroupValue();
	    
	    NS.estatusMov = NS.estatusCheque(valor);
		NS.estatusEntregado = NS.estatusChequeEntregado(valor);

		NS.escribeSubtitulo(NS.descEstatus);
		
		if(NS.excel) {
			if(Ext.getCmp(PF+'cmbChequera').getValue() == '')
		    	NS.chequera = '%';
			
			var matriz = new Array();
			
			var vector = {};
			
			vector.chequera = NS.chequera;
			vector.empresas = NS.empresas;
			vector.divisa = NS.divisa;
			vector.cajas = NS.cajas;
			vector.bancoInf = NS.bancoInf;
			vector.bancoSup = NS.bancoSup;
			vector.fechaIni = NS.fechaIni;
			vector.fechaFin = NS.fechaFin;
			vector.estatusMov = NS.estatusMov;
			vector.estatusEntregado = NS.estatusEntregado;
			vector.origen = NS.origen;
			vector.agrupado = NS.agrupado;
			vector.estatusCb = NS.estatusCb;
			vector.ordenado = NS.ordenado;
			vector.usuario = NS.GI_USUARIO;
			vector.nomEmpresa = NS.nomEmpresa;
			vector.descEstatus = NS.descEstatus;
			vector.subtitulo = NS.subtitulo;
			vector.cheIni = Ext.getCmp(PF + 'txtCheIni').getValue();
			vector.cheFin = Ext.getCmp(PF + 'txtCheFin').getValue();
			
			matriz[0] = vector;
			
			var jSonString = Ext.util.JSON.encode(matriz);
			
			NS.exportaExcel(jSonString);
			NS.excel = false;
			return;
		}
		
		//imprime reporte
		var strParams = '';
		var nombreReporte='';
		if(NS.agrupado == 'BANCO')
		{
			strParams = '?nomReporte=ReporteChequesXChequera1';
			nombreReporte='ReporteChequesXChequera1';
		}
		else if(NS.agrupado == 'CAJA')
		{
			strParams = '?nomReporte=ReporteChequesXCaja1';
			nombreReporte='ReporteChequesXCaja1';
		}
		strParams += '&'+'nomParam1=chequera';
//		alert(NS.chequera);
		strParams += '&'+'valParam1='+NS.chequera;
		strParams += '&'+'nomParam2=empresas';
		strParams += '&'+'valParam2='+NS.empresas;
		strParams += '&'+'nomParam3=divisa';
		strParams += '&'+'valParam3='+NS.divisa;
		strParams += '&'+'nomParam4=cajas';
		strParams += '&'+'valParam4='+NS.cajas;
		strParams += '&'+'nomParam5=bancoInf';
		strParams += '&'+'valParam5='+NS.bancoInf;
		strParams += '&'+'nomParam6=bancoSup';
		strParams += '&'+'valParam6='+NS.bancoSup;
		strParams += '&'+'nomParam7=fechaIni';
		strParams += '&'+'valParam7='+NS.fechaIni;
		strParams += '&'+'nomParam8=fechaFin';
		strParams += '&'+'valParam8='+NS.fechaFin;
		strParams += '&'+'nomParam9=estatusMov';
		strParams += '&'+'valParam9='+NS.estatusMov;
		strParams += '&'+'nomParam10=estatusEntregado';
		strParams += '&'+'valParam10='+NS.estatusEntregado;
		strParams += '&'+'nomParam11=origen';
		strParams += '&'+'valParam11='+NS.origen;
		strParams += '&'+'nomParam12=agrupado';
		strParams += '&'+'valParam12='+NS.agrupado;
		strParams += '&'+'nomParam13=estatusCb';
		strParams += '&'+'valParam13='+NS.estatusCb;
		strParams += '&'+'nomParam14=ordenado';
		strParams += '&'+'valParam14='+NS.ordenado;
		strParams += '&'+'nomParam15=usuario';
		strParams += '&'+'valParam15='+NS.GI_USUARIO;
		strParams += '&'+'nomParam16=nomEmpresa';
		strParams += '&'+'valParam16='+NS.nomEmpresa;
		strParams += '&'+'nomParam17=descEstatus';
		strParams += '&'+'valParam17='+NS.descEstatus;
		strParams += '&'+'nomParam18=subtitulo';
		strParams += '&'+'valParam18='+NS.subtitulo;
		strParams += '&'+'nomParam19=cheIni';
		strParams += '&'+'valParam19='+Ext.getCmp(PF + 'txtCheIni').getValue();
		strParams += '&'+'nomParam20=cheFin';
		strParams += '&'+'valParam20='+Ext.getCmp(PF + 'txtCheFin').getValue();
		window.open("/SET/jsp/bfrmwrk/gnrators/genJasperPDF.jsp" + strParams);
//		var cheF=Ext.getCmp(PF + 'txtCheFin').getValue();
//		var cheI=Ext.getCmp(PF + 'txtCheIni').getValue();
//		var subtitulo=NS.subtitulo;
//		var fechaIni=NS.fechaIni;
//		var agrupado=NS.agrupado;
//		var divisa=NS.divisa;
//		var estatusMov=NS.estatusMov;
//		var origen=NS.origen;
//		var bancoInf=NS.bancoInf;
//		var fechaFin=NS.fechaFin;
//		var estatusCb=NS.estatusCb;
//		var ordenado=NS.ordenado;
//		var chequera=NS.chequera;
//		var nomEmpresa=NS.nomEmpresa;
//		var empresas=NS.empresas;
//		var bancoSup=NS.bancoSup;
//		var user=NS.GI_USUARIO;
//		var descEstatus=NS.descEstatus;
//		var cajas=NS.cajas;
//		var estatusEntregado=NS.estatusEntregado;
//		
//		ReportesAction.obtenerDatosReporteChequeVerifica(nombreReporte,subtitulo,fechaIni,agrupado,divisa,estatusMov,
//		origen,bancoInf,fechaFin,estatusCb,ordenado,cheF,chequera,nomEmpresa,empresas,bancoSup,user,descEstatus,cheI,cajas,estatusEntregado,function(result){
//			alert("entro a obtener datos"+result);
//			if(result==true){
//				window.open("/SET/jsp/bfrmwrk/gnrators/genJasperPDF.jsp" + strParams);
//			}else{
//				Ext.Msg.alert('SET','No existen reportes con los datos ingresados');
//			}
//			
//		});
		
		return;
	}
	
	NS.estatusCheque = function(valor){
		if(valor == 0)
	   		NS.estatusMov = "'J', 'P', 'V'"; 
		else if(valor == 1 || valor == 4 || valor == 6)
       		NS.estatusMov = "'I', 'R'"; 
	    else if(valor == 2)
       		NS.estatusMov = "'R'"; 
       	else if(valor == 3)
        	NS.estatusMov = "'I', 'R', 'X'"; 
        else if(valor == 5)
        	NS.estatusMov = "'X'"; 
        else if(valor == 7)
        	NS.estatusMov = "'I', 'P', 'R', 'X', 'J', 'V'"; 
		
		return NS.estatusMov;
	};
	
	NS.estatusChequeEntregado = function(valor){
		if(valor == 1 || valor == 2)
	   		NS.estatusEntregado = 'N'; 
		else if(valor == 4 || valor == 6)
       		NS.estatusEntregado = 'S'; 
	    else
       		NS.estatusEntregado = ''; 
		
		return NS.estatusEntregado;
	};
	
	NS.exportaExcel = function(jsonCadena) {
		ReportesAction.exportaExcel(jsonCadena, function(res, e){
			Ext.Msg.alert('SET', res);
		});
	};

	//formulario
	NS.contenedorReporteCheques = new Ext.FormPanel({
	    title: 'Reporte de Cheques',
	    width: 652,
	    height: 439,
	    padding: 10,
	    layout: 'absolute',
	    frame: true,
	    autoScroll: true,
	    renderTo: NS.tabContId,
	    items: [
	             {
	                xtype: 'fieldset',
	                title: '',
	                x: 10,
	                y: 10,
	                width: 640,
	                height: 380,
	                layout: 'absolute',
	                items: [
	                    {
	                        xtype: 'fieldset',
	                        title: '',
	                        x: 0,
	                        y: 0,
	                        height: 190,
	                        layout: 'absolute',
	                        items: [
	                            {
	                                xtype: 'textfield',
	                                name: PF+'txtEmpresa',
	                                id: PF+'txtEmpresa',
	                                x: 70,
	                                y: 20,
	                                width: 40,
	                                listeners:{
			                        	change:{
			                        		fn:function(caja, valor){
			                        			//linea cambia combo
			                        			var comboValue = BFwrk.Util.updateTextFieldToCombo(PF+'txtEmpresa',NS.cmbEmpresa.getId());
			                        			NS.accionarCmbEmpresa(comboValue);
			                        		}
			                        	}
			                        }
	                            },
	                            NS.cmbEmpresa,
	                            NS.labCheIni, 
	                            NS.txtCheIni, 
	                            NS.txtCheFin,
	                            {
	                                xtype: 'uppertextfield',
	                                name: PF+'txtDivisa',
	                                id: PF+'txtDivisa',
	                                x: 70,
	                                y: 60,
	                                width: 40,
	                                listeners:{
			                        	change:{
			                        		fn:function(caja, valor){
			                        			//linea cambia combo
			                        			var comboValue = BFwrk.Util.updateTextFieldToCombo(PF+'txtDivisa',NS.cmbDivisa.getId());
			                        			NS.accionarCmbDivisa(comboValue);
			                        		}
			                        	}
			                        }
	                            },
	                            NS.cmbDivisa,
	                            {
	                                xtype: 'textfield',
	                                name: PF+'txtBanco',
	                                id: PF+'txtBanco',
	                                x: 70,
	                                y: 100,
	                                width: 40,
	                                listeners:{
			                        	change:{
			                        		fn:function(caja, valor){
			                        			//linea cambia combo
			                        			var comboValue = BFwrk.Util.updateTextFieldToCombo(PF+'txtBanco',NS.cmbBanco.getId());
			                        			NS.accionarCmbBanco(comboValue);
			                        		}
			                        	}
			                        }
	                            },
	                            NS.cmbBanco,
	                            {
	                                xtype: 'fieldset',
	                                title: 'Empresa',
	                                x: 400,
	                                y: -2,
	                                width: 177,
	                                height: 50,
	                                layout: 'absolute',
	                                hidden: true,
	                                items: [
		                                    {
		                                        xtype: 'radio',
		                                        x: 10,
		                                        y: -5,
		                                        name: PF+'optEmpresaActual',
		                                        id: PF+'optEmpresaActual',
		                                		inputValue: 0,
		                                        boxLabel: 'Actual',
		                                        checked: true,
		                                        listeners:{
					              					check:{
			                   			   				fn:function(opt, valor)
				                               			{
				                               				if(valor==true)
				                               				{
				                               					Ext.getCmp(PF+'optEmpresaTodas').setValue(false);
				                               					Ext.getCmp(PF+'cmbEmpresa').setDisabled(false);
				                               					Ext.getCmp(PF+'txtEmpresa').setDisabled(false);
				                               					Ext.getCmp(PF+'cmbBanco').setDisabled(false);
				                               					Ext.getCmp(PF+'txtBanco').setDisabled(false);
				                               					Ext.getCmp(PF+'cmbChequera').setDisabled(false);
				                               				}
				                               			}
				                               		}
				                               	}
		                                    },
		                                    {
		                                        xtype: 'radio',
		                                        x: 83,
		                                        y: -5,
		                                        name: PF+'optEmpresaTodas',
		                                        id: PF+'optEmpresaTodas',
		                                		inputValue: 1,
		                                        boxLabel: 'Todas',
		                                        listeners:{
					              					check:{
			                   			   				fn:function(opt, valor)
				                               			{
				                               				if(valor==true)
				                               				{
				                               					Ext.getCmp(PF+'optEmpresaActual').setValue(false);
				                               					Ext.getCmp(PF+'cmbEmpresa').setDisabled(true);
				                               					Ext.getCmp(PF+'txtEmpresa').setDisabled(true);
				                               					Ext.getCmp(PF+'cmbBanco').setDisabled(true);
				                               					Ext.getCmp(PF+'txtBanco').setDisabled(true);
				                               					Ext.getCmp(PF+'cmbChequera').setDisabled(true);
				                               					NS.nomEmpresa = 'REPORTE GLOBAL';
				                               				}
				                               			}
				                               		}
				                               	}
		                                    }
	                                ]
	                            },
	                            {
	                                xtype: 'label',
	                                text: 'Caja:',
	                                x: 340,
	                                y: 60
	                            },
	                            {
	                                xtype: 'label',
	                                text: 'Chequera:',
	                                x: 340,
	                                y: 100
	                            },
	                            NS.cmbCaja,
	                            NS.cmbChequera,
	                            {
	                                xtype: 'label',
	                                text: 'Desde:',
	                                x: 10,
	                                y: 140
	                            },
	                            {
	                                xtype: 'datefield',
	                                format: 'd/m/Y',
	                                name: PF+'txtFechaDesde',
	                                id: PF+'txtFechaDesde',
	                                x: 70,
	                                y: 140,
	                                width: 100,
	                                value: apps.SET.FEC_HOY
	                            },
	                            {
	                                xtype: 'label',
	                                text: 'Hasta:',
	                                x: 180,
	                                y: 140
	                            },
	                            {
	                                xtype: 'datefield',
	                                format: 'd/m/Y',
	                                name: PF+'txtFechaHasta',
	                                id: PF+'txtFechaHasta',
	                                x: 220,
	                                y: 140,
	                                width: 100,
	                                value: apps.SET.FEC_HOY
	                            },
	                            {
	                                xtype: 'label',
	                                text: 'Origen:',
	                                x: 340,
	                                y: 140
	                            },
	                            NS.cmbOrigen,
	                            {
	                                xtype: 'label',
	                                text: 'Empresa:',
	                                x: 10,
	                                y: 20
	                            },
	                            {
	                                xtype: 'label',
	                                text: 'Banco:',
	                                x: 10,
	                                y: 100
	                            },
	                            {
	                                xtype: 'label',
	                                text: 'Divisa:',
	                                x: 10,
	                                y: 60
	                            }
	                        ]
	                    },
	                    {
	                        xtype: 'fieldset',
	                        title: 'Cheques',
	                        x: 0,
	                        y: 200,
	                        width: 310,
	                        height: 150,
	                        layout: 'absolute',
	                        items: [
		                        {
			                     	xtype: 'radiogroup',
			                     	name: PF+'estatus',
			                     	id: PF+'estatus',
		                     		columns: 2,
		                     		x: 20,
		                     		y: 5,
							       	items: [  
			                            {
			                                xtype: 'radio',
			                                boxLabel: 'Por imprimir',
			                                checked: true,
			                                name: PF+'optCheque',
			                                id: PF+'optCheque',
			                                inputValue: 0,
			                                listeners:{
				              					check:{
			                               			fn:function(opt, valor)
			                               			{
				                               			if(valor==true)
		                               					{
		                               						//alert('Por imprimir');
			                               					NS.descEstatus = 'Por imprimir';
			                               				}
			                               			}
			                               		}
			                          		}
			                            },
			                            {
			                                xtype: 'radio',
			                                boxLabel: 'Por entregar',
			                                name: PF+'optCheque',
			                                inputValue: 1,
			                                listeners:{
				              					check:{
			                               			fn:function(opt, valor)
			                               			{
			                               				if(valor==true)
			                               				{
			                               					//alert('Por entregar');
			                               					NS.descEstatus = 'Por entregar';
		                               					}
			                               			}
			                               		}
			                          		}
			                            },
			                            {
			                                xtype: 'radio',
			                                boxLabel: 'Reimpresos',
			                                name: PF+'optCheque',
			                                inputValue: 2,
			                                listeners:{
				              					check:{
			                               			fn:function(opt, valor)
			                               			{
			                               				if(valor==true)
			                               				{
			                               					//alert('Reimpresos');
			                               					NS.descEstatus = 'Reimpresos';
		                               					}
			                               			}
			                               		}
			                          		}
			                            },
			                            {
			                                xtype: 'radio',
			                                boxLabel: 'Emitidos',
			                                name: PF+'optCheque',
			                                inputValue: 3,
			                                listeners:{
				              					check:{
			                               			fn:function(opt, valor)
			                               			{
			                               				if(valor==true)
			                               				{
			                               					//alert('Emitidos');
			                               					NS.descEstatus = 'Emitidos';
		                               					}
			                               			}
			                               		}
			                          		}
			                            },
			                            {
			                                xtype: 'radio',
			                                boxLabel: 'Entregados',
			                                name: PF+'optCheque',
			                                inputValue: 4,
			                                listeners:{
				              					check:{
			                               			fn:function(opt, valor)
			                               			{
			                               				if(valor==true)
			                               				{
			                               					//alert('Entregados');
			                               					NS.descEstatus = 'Entregados';
		                               					}
			                               			}
			                               		}
			                          		}
			                            },
			                            {
			                                xtype: 'radio',
			                                boxLabel: 'Cancelados',
			                                name: PF+'optCheque',
			                                inputValue: 5,
			                                listeners:{
				              					check:{
			                               			fn:function(opt, valor)
			                               			{
			                               				if(valor==true)
			                               				{
			                               					//alert('Cancelados');
			                               					NS.descEstatus = 'Cancelados';
		                               					}
			                               			}
			                               		}
			                          		}
			                            },
			                            {
			                                xtype: 'radio',
			                                boxLabel: 'Transito',
			                                name: PF+'optCheque',
			                                inputValue: 6, 
			                                listeners:{
				              					check:{
			                               			fn:function(opt, valor)
			                               			{
			                               				if(valor==true)
			                               				{
			                               					//alert('en Transito');
				                               				NS.estatusCb = 'P';
				                               				NS.descEstatus = 'en Transito';
			                               				}
			                               			}
			                               		}
			                          		}
			                            },
			                            {
			                                xtype: 'radio',
			                                boxLabel: 'Todos',
			                                name: PF+'optCheque',
			                                inputValue: 7,
			                                listeners:{
				              					check:{
			                               			fn:function(opt, valor)
			                               			{
			                               				NS.estatusCb = 'P';
			                               				NS.descEstatus = '';
			                               			}
			                               		}
			                          		}
			                            }
		                            ]
	                            }
	                        ]
	                    },
	                    {
	                        xtype: 'fieldset',
	                        title: 'Ordenados por',
	                        x: 356,
	                        y: 209,
	                        width: 260,
	                        height: 60,
	                        layout: 'absolute',
	                        items: [
	                        	{	
		                        	xtype: 'radiogroup',
		                        	width: 260,
		                        	vertical: true,
							       	items: [
			                            {
			                                xtype: 'radio',
			                                boxLabel: 'Monto',
			                                checked: true,
			                                name: PF+'optOrdena',
			                                inputValue: 0,
			                                listeners:{
				              					check:{
			                               			fn:function(opt, valor)
			                               			{
			                               				if(valor == true)
			                               					NS.ordenado = 'MONTO';
			                               			}
			                               		}
			                          		}
			                            },
			                            {
			                                xtype: 'radio',
			                                boxLabel: 'No. Cheque',
			                                name: PF+'optOrdena',
			                                inputValue: 1,
			                                listeners:{
				              					check:{
			                               			fn:function(opt, valor)
			                               			{
			                               				if(valor == true)
			                               					NS.ordenado = 'NO_CHEQUE';
			                               			}
			                               		}
			                          		}
			                            },
			                            {
			                                xtype: 'radio',
			                                boxLabel: 'Fecha',
			                                name: PF+'optOrdena',
			                                inputValue: 2,
			                                listeners:{
				              					check:{
			                               			fn:function(opt, valor)
			                               			{
			                               				if(valor == true)
			                               					NS.ordenado = 'FECHA';
			                               			}
			                               		}
			                          		}
			                            }
		                            ]
	                            }
	                        ]
	                    },
	                    {
	                        xtype: 'fieldset',
	                        title: 'Agrupados por',
	                        x: 358,
	                        y: 277,
	                        width: 260,
	                        height: 60,
	                        layout: 'absolute',
	                        items: [
	                        	{	
		                        	xtype: 'radiogroup',
		                        	width: 260,
		                        	vertical: true,
							       	items: [
			                            {
			                                xtype: 'radio',
			                                x: 8,
			                                y: 0,
			                                boxLabel: 'Banco y Chequera',
			                                checked: true,
			                                name: PF+'optAgrupa',
			                                inputValue: 0,
			                                listeners:{
				              					check:{
			                               			fn:function(opt, valor)
			                               			{
			                               				if(valor == true)
			                               					NS.agrupado = 'BANCO';
			                               			}
			                               		}
			                          		}
			                            },
			                            {
			                                xtype: 'radio',
			                                x: 166,
			                                y: 0,
			                                boxLabel: 'Caja',
			                                name: PF+'optAgrupa',
			                                inputValue: 1,
			                                listeners:{
				              					check:{
			                               			fn:function(opt, valor)
			                               			{
			                               				if(valor == true)
			                               					NS.agrupado = 'CAJA';
			                               			}
			                               		}
			                          		}
			                            }
		                            ]
                            	}
	                        ]
	                    }
	                ]
	            },
	            {
	                xtype: 'button',
	                text: 'Limpiar',
	                x: 510,
	                y: 400,
	                width: 80,
	                id: PF+'cmdLimpiar',
                    name: PF+'cmdLimpiar',
                    listeners:{
               			click:{
           			   		fn:function(e){
	            			   	NS.contenedorReporteCheques.getForm().reset();
	            			   	Ext.getCmp(PF + 'txtEmpresa').setValue('0');
           			   		}
        			   	}
    			   	}
	            },
	            {
	                xtype: 'button',
	                text: 'Imprimir',
	                x: 420,
	                y: 400,
	                width: 80,
	                listeners:{
               			click:{
           			   		fn:function(e){
           			   			NS.validarCriterios();
           			   		}
        			   	}
    			   	}
	            },{
	                xtype: 'button',
	                text: 'Excel',
	                x: 330,
	                y: 400,
	                width: 80,
	                listeners:{
               			click:{
           			   		fn:function(e){
	            				NS.excel = true;
	            				NS.validarCriterios();
           			   		}
        			   	}
    			   	}
	            }
	        ]
	        
	});
		NS.contenedorReporteCheques.setSize(Ext.get(NS.tabContId).getWidth(), Ext.get(NS.tabContId).getHeight());
});
