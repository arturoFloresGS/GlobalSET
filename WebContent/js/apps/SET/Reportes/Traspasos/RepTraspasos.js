/*
 * @author: Victor H. Tello
 * @since: 06/Mar/2012
 */
Ext.onReady(function() {
	var NS = Ext.namespace('apps.SET.Egresos.RepTraspasos');
	NS.tabContId = apps.SET.tabContainerId;
	NS.idUsuario = apps.SET.iUserId;
	var PF = apps.SET.tabID + '.';
	Ext.Direct.addProvider(Ext.setapp.REMOTING_API);
	Ext.QuickTips.init();
	
	NS.noEmpresa = apps.SET.NO_EMPRESA;
	
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
		columns: 2, //muestra los radiobuttons en dos columnas
		items: [
	          {boxLabel: 'Entre Cuentas', name: 'optSi', inputValue: 0, checked: true},  
	          {boxLabel: 'Entre Empresas', name: 'optSi', inputValue: 1},
	          {boxLabel: 'Todos', name: 'optSi', inputValue: 2, hidden: true}
	     ]
	});
	//Contenedor de Radio Button
	NS.contTipoRep = new Ext.form.FieldSet({
		title: 'Tipo de reporte',
		x: 10,
		y: 115,
		width: 400,
		height: 55,
		layout: 'absolute',
		items: [
		    NS.optRadios
		]
	});
	
	NS.optRadiosTT = new Ext.form.RadioGroup({
		id: PF + 'optTipoRepTT',
		name: PF + 'optTipoRepTT',
		x: 25,
		y: 0,
		columns: 2, //muestra los radiobuttons en dos columnas
		items: [
	          {boxLabel: 'Pendientes', name: 'optTT', inputValue: 0, checked: true},  
	          {boxLabel: 'Realizados', name: 'optTT', inputValue: 1},
	          {boxLabel: 'Todos', name: 'optTT', inputValue: 2, hidden: true}
	     ]
	});
	//Contenedor de Radio Button
	NS.contTipoTrasp = new Ext.form.FieldSet({
		title: 'Tipo de traspaso',
		x: 500,
		y: 115,
		width: 300,
		height: 55,
		layout: 'absolute',
		items: [
		    NS.optRadiosTT
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
            NS.contTipoTrasp
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
			    x: 880,
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
	
	//Función para la llamada al reporte
	NS.imprimirReporte = function() {
		var optEstatus = Ext.getCmp(PF + 'optTipoRep').getValue();
		var valEstatus = parseInt(optEstatus.getGroupValue());
		var optEstatusTT = Ext.getCmp(PF + 'optTipoRepTT').getValue();
		var valEstatusTT = parseInt(optEstatusTT.getGroupValue());
		var nomEmpresa;
		var nomRep;
		var estRep;
		
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
		
		if(valEstatus == 0 || valEstatus == 2) {
			if(valEstatus == 0) nomRep = 'TRASPASOS ENTRE CUENTAS DEL';
			else nomRep = 'TRASP. ENTRE CTAS. E INTEREMPRESAS';
		}else if(valEstatus == 1)
			nomRep = 'TRASPASOS ENTRE EMPRESAS DEL';
		
		if(valEstatusTT == 0)
			estRep = 'PENDIENTES';
		else
			estRep = 'REALIZADOS';
		
		strParams = '?nomReporte=TraspasosCuentas';
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
		strParams += '&'+'nomParam8=NOM_REP';
		strParams += '&'+'valParam8=' + nomRep;
		strParams += '&'+'nomParam9=tipoReporte';
		strParams += '&'+'valParam9=' + 'traspasos';
		strParams += '&'+'nomParam10=usuario';
		strParams += '&'+'valParam10=' + apps.SET.iUserId;
		strParams += '&'+'nomParam11=tipoTrasp';
		strParams += '&'+'valParam11=' + valEstatusTT;
		strParams += '&'+'nomParam12=EST_REP';
		strParams += '&'+'valParam12=' + estRep;
		
		
		var idDivisa=Ext.getCmp(PF + 'txtDivisa').getValue();
		var fechIni=cambiarFecha('' + Ext.getCmp(PF + 'txtFechaIni').getValue());
		var tipoTrasp=valEstatusTT;
		var estatusReporte=valEstatus;
		var user=apps.SET.iUserId;
		var noBanco=Ext.getCmp(PF + 'txtBanco').getValue();
		var fechFin=cambiarFecha('' + Ext.getCmp(PF + 'txtFechaFin').getValue());
		var noEmpresa=Ext.getCmp(PF + 'txtEmpresa').getValue();
		ReportesAction.reporteTransConfirmadasVerificarTranspasos(idDivisa,fechIni, tipoTrasp, estatusReporte,"traspasos",user,noBanco, fechFin,noEmpresa,nomRep,nomEmpresa,estRep,function(result){
			
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
	};
	
	NS.contRepTraspasos = new Ext.FormPanel( {
		title: 'Reporte Traspasos',
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
	NS.contRepTraspasos.setSize(Ext.get(NS.tabContId).getWidth(), Ext.get(NS.tabContId).getHeight());
});
