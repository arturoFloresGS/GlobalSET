/*
 * @author: Victor H. Tello
 * @since: 01/Mar/2012
 */
Ext.onReady(function() {
	var NS = Ext.namespace('apps.SET.Egresos.RepTransferencias');
	NS.tabContId = apps.SET.tabContainerId;
	NS.idUsuario = apps.SET.iUserId;
	var PF = apps.SET.tabID + '.';
	Ext.Direct.addProvider(Ext.setapp.REMOTING_API);
	Ext.QuickTips.init();
	
	NS.noEmpresa = '';
	NS.chkSmart = false;
	NS.excel = false;
	
	//Etiqueta empresa
	NS.labEmpresa = new Ext.form.Label({
		text: 'Empresa:',
        x: 10,
        y: 0
	});
    //Caja numero de empresa
	NS.txtEmpresa = new Ext.form.TextField({
		id: PF + 'txtEmpresa',
        name:PF + 'txtEmpresa',
		x: 10,
        y: 15,
        width: 50,
        tabIndex: 0,
        listeners:{
			change:{
				fn: function(caja,valor){
					if(caja.getValue() != null && caja.getValue() != undefined && caja.getValue() != '') {
						var valueCombo = BFwrk.Util.updateTextFieldToCombo(PF + 'txtEmpresa', NS.cmbEmpresas.getId());
						
						if(valueCombo != null && valueCombo != undefined && valueCombo != '')
							NS.cambiaBanco(valueCombo);
						else
							NS.cambiaBanco(0);
					}else {
						NS.cambiaBanco(0);
						BFwrk.Util.updateTextFieldToCombo(PF + 'txtEmpresa', NS.cmbEmpresas.getId());
					}
                }
			}
    	}
    });
	//Store empresas
    NS.storeEmpresas = new Ext.data.DirectStore({
		paramsAsHash: false,
		root: '',
		baseParams: {idUsuario: NS.idUsuario},
		paramOrder: ['idUsuario'],
		directFn: ConfirmacionTransferenciasAction.obtenerEmpresas, 
		idProperty: 'noEmpresa', 
		fields: [
			 {name: 'noEmpresa'},
			 {name: 'nomEmpresa'}
		],
		listeners: {
			load: function(s, records){
				var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeEmpresas, msg:"Cargando..."});
				if(records.length == null || records.length <= 0)
				{
					Ext.Msg.alert('SET','No tiene empresas asignadas');
				}
				//Se agrega la opcion todas las empresas
	 			var recordsStoreEmpresas = NS.storeEmpresas.recordType;	
				var todas = new recordsStoreEmpresas({
					noEmpresa: 0,
					nomEmpresa: '***************TODAS***************'
		       	});
		   		NS.storeEmpresas.insert(0,todas);
			}
		}
	}); 
	NS.storeEmpresas.load();
	//Combo empresas
	NS.cmbEmpresas = new Ext.form.ComboBox({
		store: NS.storeEmpresas,
		id: PF + 'cmbEmpresas',
		name: PF + 'cmbEmpresas',
		x: 65,
        y: 15,
        width: 320,
		typeAhead: true,
		mode: 'local',
		selecOnFocus: true,
		forceSelection: true,
        tabIndex: 1,
		valueField: 'noEmpresa',
		displayField: 'nomEmpresa',
		autocomplete: true,
		emptyText: 'Seleccione una empresa',
		triggerAction: 'all',
		value: '',
		visible: false,
		listeners:{
			select:{
				fn:function(combo, valor) {
					BFwrk.Util.updateComboToTextField(PF + 'txtEmpresa', NS.cmbEmpresas.getId());
					NS.cambiaBanco(combo.getValue());
				}
			}
		}
	});
	//Refresca el combo bancos en base a la empresa
	NS.cambiaBanco = function(valueCombo){
		Ext.getCmp(PF + 'txtBanco').reset();
		NS.cmbBanco.reset();
		NS.storeBancos.removeAll();
		
		if((valueCombo != null && valueCombo != undefined && valueCombo != '') || valueCombo == 0) {
			//if(valueCombo == 0)
				Ext.getCmp(PF + 'txtEmpresa').setValue(valueCombo);
			//else {
				NS.storeBancos.baseParams.noEmpresa = valueCombo;
				NS.storeBancos.load();
			//}
			NS.noEmpresa = valueCombo;
		}
	};
	//Etiqueta Banco
	NS.labBanco = new Ext.form.Label({
		text: 'Banco:',
		x: 10,
		y: 50
	});
	//Caja numero banco 
	NS.txtBanco = new Ext.form.TextField({
		id: PF + 'txtBanco',
		name: PF + 'txtBanco',
		x: 10,
		y: 65,
		width: 50,
		tabIndex: 2,
		listeners:{
			change:{
				fn: function(caja,valor){
                    if(caja.getValue() != null && caja.getValue() != undefined && caja.getValue() != '')
                    	var valueCombo = BFwrk.Util.updateTextFieldToCombo(PF + 'txtBanco', NS.cmbBanco.getId());
                }
			}
    	}
	});
	//Store banco
	NS.storeBancos = new Ext.data.DirectStore({
		paramsAsHash: false,
		root: '',
		baseParams:{
			noEmpresa: 0
		},
		paramOrder:['noEmpresa'],
		directFn: ConfirmacionCargoCtaAction.obtenerBancos, 
		idProperty: 'id', 
		fields: [
			 {name: 'id'},
			 {name: 'descripcion'}
		],
		listeners: {
			load: function(s, records) {
				var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeBancos, msg:"Cargando..."});
				if(records.length == null || records.length <= 0)
					Ext.Msg.alert('SET','No Existen bancos para esta empresa');
			}
		}
	});
	//Combo banco
	NS.cmbBanco = new Ext.form.ComboBox({
		store: NS.storeBancos,
		id: PF + 'cmbBanco',
		name: PF + 'cmbBanco',
		x: 65,
	    y: 65,
	    width: 180,
		typeAhead: true,
		mode: 'local',
		selecOnFocus: true,
		forceSelection: true,
		valueField:'id',
		displayField:'descripcion',
		autocomplete: true,
		emptyText: 'Seleccione banco',
		triggerAction: 'all',
		value: '',
		visible: false,
		tabIndex: 3,
		listeners:{
			select:{
				fn:function(combo, valor) {
					BFwrk.Util.updateComboToTextField(PF + 'txtBanco', NS.cmbBanco.getId());
				}
			}
		}
	});
	
	//Etiqueta Banco
	NS.labDivisa = new Ext.form.Label({
		text: 'Divisa:',
		x: 500,
		y: 50
	});
	
	NS.txtDivisa = new Ext.form.TextField({
	    x: 500,
	    y: 65,
	    width: 35,
	    tabIndex: 14,
	    id: PF+'txtDivisa',
	    name:PF+'txtDivisa',
	    listeners:{
	    	change:{
	    		fn:function(caja, valor) {
	    			if(caja.getValue() != null && caja.getValue() != undefined && caja.getValue() != '') {
	    				var valueCombo = BFwrk.Util.updateTextFieldToCombo(PF+'txtDivisa', NS.cmbDivisa.getId());
	    				
	    				if(valueCombo == null || valueCombo == undefined || valueCombo == '') Ext.getCmp(PF + 'txtDivisa').reset();
	    			}else
	    				NS.cmbDivisa.reset();
	    		}
	    	}
	    }
	});
	
	//store Divisas
    NS.storeDivisas = new Ext.data.DirectStore({
		paramsAsHash: false,
		root: '',
		directFn: CapturaSolicitudesPagoAction.obtenerDivisas, 
		idProperty: 'idDivisa', 
		fields: [
			 {name: 'idDivisa'},
			 {name: 'descDivisa'},
			 {name: 'idDivisaSoin'},
			 {name: 'clasificacion'}
		],
		listeners: {
			load: function(s, records){
				var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeDivisas, msg:"Cargando..."});
				if(records.length==null || records.length<=0)
				{
					Ext.Msg.alert('SET','No Existen divisas');
				}
			}
		}
	}); 
	NS.storeDivisas.load();
	
	//combo Divisa
	NS.cmbDivisa = new Ext.form.ComboBox({
		store: NS.storeDivisas
		,name: PF+'cmbDivisa'
		,id: PF+'cmbDivisa'
		,typeAhead: true
		,mode: 'local'
		,minChars: 0
		,selecOnFocus: true
		,forceSelection: true
        ,x: 540
        ,y: 65
        ,width: 150
        ,tabIndex: 15
		,valueField:'idDivisa'
		,displayField:'descDivisa'
		,autocomplete: true
		,emptyText: 'Seleccione la divisa'
		,triggerAction: 'all'
		,value: ''
		,visible: false
		,listeners:{
			select:{
				fn:function(combo, valor) {
						BFwrk.Util.updateComboToTextField(PF+'txtDivisa', NS.cmbDivisa.getId());
				}
			}
		}
	});	
	
	//Etiqueta Fecha inicial
	NS.labFechaIni = new Ext.form.Label({
		text: 'Fecha Inicial:',
		x: 500,
		y: 0
	});
	//Fecha inicial
	NS.txtFechaIni = new Ext.form.DateField({
		id: PF + 'txtFechaIni',
		name: PF + 'txtFechaIni',
		x: 500,
		y: 15,
		width: 110,
		format: 'd/m/Y',
		value : apps.SET.FEC_HOY
	});
	//Etiqueta Fecha Final
	NS.labFechaFin = new Ext.form.Label({
		text: 'Fecha Final:',
		x: 640,
		y: 0
	});
	//Fecha Final
	NS.txtFechaFin = new Ext.form.DateField({
		id: PF + 'txtFechaFin',
		name: PF + 'txtFechaFin',
		x: 640,
		y: 15,
		width: 110,
		format: 'd/m/Y',
		value: apps.SET.FEC_HOY
	});
	
	NS.optRadios = new Ext.form.RadioGroup({
		id: PF + 'optTipoRep',
		name: PF + 'optTipoRep',
		x: 25,
		y: 0,
		columns: 3, //muestra los radiobuttons en dos columnas
		items: [
	          {boxLabel: 'Confirmadas', name: 'optSi', inputValue: 0, checked: true},  
	          {boxLabel: 'Transferidas', name: 'optSi', inputValue: 1},
	          {boxLabel: 'Pendientes', name: 'optSi', inputValue: 2},
	          {boxLabel: 'Todas', name: 'optSi', inputValue: 3, hidden: true}
	     ]
	});
	//Contenedor de Radio Button
	NS.contTipoRep = new Ext.form.FieldSet({
		title: 'Estatus',
		x: 10,
		y: 115,
		width: 400,
		height: 55,
		layout: 'absolute',
		items: [
		    NS.optRadios
		]
	});
	
	//Contenedor de Busqueda
	NS.contBusqueda = new Ext.form.FieldSet({
		title: '',
		width: 985,
	    height: 250,
	    layout: 'absolute',
	    items:[
	        NS.labEmpresa,
	        NS.txtEmpresa,
	        NS.cmbEmpresas,
	        NS.labBanco,
            NS.txtBanco,
            NS.cmbBanco,
            NS.labDivisa,
            NS.txtDivisa,
            NS.cmbDivisa,
            NS.labFechaIni,
            NS.txtFechaIni,
            NS.labFechaFin,
            NS.txtFechaFin,
            NS.contTipoRep,
            {
	    		xtype: 'checkbox',
	    		id: PF + 'chkSmart',
                name: PF + 'chkSmart',
                x: 800,
        	    y: 15,
                boxLabel: 'Cuentas Smart',
                hidden: true,
                listeners: {
                    check: {
                    	fn:function(checkBox,valor) {
                    		if(valor) {
                    			NS.chkSmart = true;
                    		}else{
                 				NS.chkSmart = false;
                    		}
                        }
	    			}
	    		}
            }
	    ]
	});
	//Contenedor General
	NS.contGeneral = new Ext.form.FieldSet({
		title: '',
		width: 1010,
	    height: 490,
	    x: 20,
	    y: 5,
	    layout: 'absolute',
	    items:[
			NS.contBusqueda,
			{
                xtype: 'button',
                text: 'Excel',
                x: 690,
                y: 270,
                width: 80,
                hidden: true,
                listeners:{
           			click:{
       			   		fn:function(e){
            				NS.excel = true;
            				NS.imprimirReporte();
       			   		}
    			   	}
			   	}
            },
			{
			    xtype: 'button',
			    text: 'Imprimir',
			    x: 780,
			    y: 270,
			    width: 80,
			    height: 22,
			    listeners: {
					click: {
						fn: function(e) {
							NS.imprimirReporte();
						}
					}
				}
			},{
			    xtype: 'button',
			    text: 'Limpiar',
			    x: 870,
			    y: 270,
			    width: 80,
			    height: 22,
			    listeners: {
					click: {
						fn: function(e) {
							NS.limpiar();
						}
					}
				}
			}
		]
	});
	
	NS.exportaExcel = function(jsonCadena) {
		ReportesAction.exportaExcelTransfer(jsonCadena, function(res, e){
			Ext.Msg.alert('SET', res);
		});
	};
	
	//Función para la llamada al reporte
	NS.imprimirReporte = function() {
		var optEstatus = Ext.getCmp(PF + 'optTipoRep').getValue();
		var valEstatus = parseInt(optEstatus.getGroupValue());
		var nomEmpresa;
		var nomRep;
		var reg = {};
		var mat = new Array();

		if(NS.cmbEmpresas.getValue()=='' && NS.txtEmpresa.getValue()==''){
			Ext.Msg.alert('SET','Ingrese una empresa válida');
			return;
		}
		
		if(NS.cmbDivisa.getValue()=='' && NS.txtDivisa.getValue()==''){
			Ext.Msg.alert('SET','Ingrese una divisa válida');
			return;
		}
		
		if(NS.noEmpresa == 0)
			nomEmpresa = 'REPORTE GLOBAL DE EMPRESAS';
		else
			nomEmpresa = NS.storeEmpresas.getById(NS.noEmpresa).get('nomEmpresa');
		
		if(valEstatus == 0 || valEstatus == 2 || valEstatus == 3) {
			if(valEstatus == 0) nomRep = 'TRANSFERENCIAS CONFIRM. GENERADAS DEL';
			else if(valEstatus == 2)
				nomRep = 'TRANSFERENCIAS PENDIENTES GENERADAS DEL';
			else if(valEstatus == 3)
				nomRep = 'TRANSFERENCIAS REALIZADAS Y CONFIRM. DEL';
			strParams = '?nomReporte=TransferenciasConfirmadas';
		}else if(valEstatus == 1)
			strParams = '?nomReporte=TransferenciasTransferidas';
		

		if(NS.excel) {
			reg.noEmpresa = Ext.getCmp(PF + 'txtEmpresa').getValue();
			reg.nomEmpresa = nomEmpresa;
			reg.noBanco = Ext.getCmp(PF + 'txtBanco').getValue();
			reg.idDivisa = Ext.getCmp(PF + 'txtDivisa').getValue();
			reg.FECHA_INI = cambiarFecha('' + Ext.getCmp(PF + 'txtFechaIni').getValue());
			reg.FECHA_FIN = cambiarFecha('' + Ext.getCmp(PF + 'txtFechaFin').getValue());
			reg.estatusReporte = valEstatus;
			reg.chkSmart = NS.chkSmart;
			reg.NOM_REP = nomRep;
			reg.tipoReporte = 'transfer';
			reg.usuario = apps.SET.iUserId;
			mat[0] = reg;
			
			var jsonString = Ext.util.JSON.encode(mat);
			
			NS.exportaExcel(jsonString);
			
			NS.excel = false;
			return;
		}
		strParams += '&'+'nomParam1=NOM_EMPRESA';
		strParams += '&'+'valParam1=' + nomEmpresa;
		strParams += '&'+'nomParam2=FECHA_INI';
		strParams += '&'+'valParam2=' + cambiarFecha('' + Ext.getCmp(PF + 'txtFechaIni').getValue());
		strParams += '&'+'nomParam3=FECHA_FIN';
		strParams += '&'+'valParam3=' + cambiarFecha('' + Ext.getCmp(PF + 'txtFechaFin').getValue());
		strParams += '&'+'nomParam4=estatusReporte';
		strParams += '&'+'valParam4=' + valEstatus;
		strParams += '&'+'nomParam5=noEmpresa';
		strParams += '&'+'valParam5=' + '' + Ext.getCmp(PF + 'txtEmpresa').getValue();
		strParams += '&'+'nomParam6=noBanco';
		strParams += '&'+'valParam6=' + '' + Ext.getCmp(PF + 'txtBanco').getValue();
		strParams += '&'+'nomParam7=idDivisa';
		strParams += '&'+'valParam7=' + '' + Ext.getCmp(PF + 'txtDivisa').getValue();
		strParams += '&'+'nomParam8=chkSmart';
		strParams += '&'+'valParam8=' + NS.chkSmart;
		strParams += '&'+'nomParam9=NOM_REP';
		strParams += '&'+'valParam9=' + nomRep;
		strParams += '&'+'nomParam10=tipoReporte';
		strParams += '&'+'valParam10=' + 'transfer';
		strParams += '&'+'nomParam11=usuario';
		strParams += '&'+'valParam11=' + apps.SET.iUserId;
		
		var idDivisa=Ext.getCmp(PF + 'txtDivisa').getValue();
		var fechIni=cambiarFecha('' + Ext.getCmp(PF + 'txtFechaIni').getValue());
		var fechFin=cambiarFecha('' + Ext.getCmp(PF + 'txtFechaFin').getValue());
		var chkSmart=NS.chkSmart;
		var estatusReporte=valEstatus;
		var tipoReporte="transfer";
		var user=apps.SET.iUserId;
		var noBanco=Ext.getCmp(PF + 'txtBanco').getValue();
		var noEmpresa=Ext.getCmp(PF + 'txtEmpresa').getValue();
		ReportesAction.reporteTransConfirmadasVerificar(idDivisa,fechIni,chkSmart,estatusReporte,tipoReporte,user,noBanco,fechFin,noEmpresa,function(result){
			
			if(result==true){
				window.open("/SET/jsp/bfrmwrk/gnrators/genJasperPDF.jsp" + strParams);
			}else{
				Ext.Msg.alert('SET','No existen reportes con los datos ingresados');
			}
			
		});
		
	};
	
	function cambiarFecha(fecha) {
		var mesArreglo=new Array(11);
		mesArreglo[0]="Jan";mesArreglo[1]="Feb";mesArreglo[2]="Mar";mesArreglo[3]="Apr";
		mesArreglo[4]="May";mesArreglo[5]="Jun";mesArreglo[6]="Jul";mesArreglo[7]="Aug";mesArreglo[8]="Sep";
		mesArreglo[9]="Oct";mesArreglo[10]="Nov";mesArreglo[11]="Dec";
		var mesDate=fecha.substring(4,7);
		var dia=fecha.substring(8,10);
		var anio=fecha.substring(11,15);
		
		for(var i=0;i<12;i=i+1) {
			if(mesArreglo[i]===mesDate) {
			var mes=i+1;
				if(mes<10)
				mes = '0' + mes;
			}
		}
		var fechaString=''+dia+'/'+mes+'/'+anio;
		return fechaString;		
	};
	
	NS.limpiar = function() {
		Ext.getCmp(PF + 'txtEmpresa').setValue(0);
		Ext.getCmp(PF + 'cmbEmpresas').reset();
		Ext.getCmp(PF + 'txtBanco').reset();
		Ext.getCmp(PF + 'cmbBanco').reset();
		NS.storeBancos.removeAll();
		Ext.getCmp(PF + 'txtFechaIni').setValue(apps.SET.FEC_HOY);
		Ext.getCmp(PF + 'txtFechaFin').setValue(apps.SET.FEC_HOY);
		Ext.getCmp(PF + 'chkSmart').setValue(false);
	};
	
	NS.contRepTransferencias = new Ext.FormPanel( {
		title: 'Reporte Transferencias',
	    width: 900,
	    height: 700,
	    padding: 10,
	    layout: 'absolute',
	    frame: true,
	    autoScroll: true,
	    renderTo: NS.tabContId,
	    items: [
	            NS.contGeneral
	    ]
		});
	NS.contRepTransferencias.setSize(Ext.get(NS.tabContId).getWidth(), Ext.get(NS.tabContId).getHeight());
});
