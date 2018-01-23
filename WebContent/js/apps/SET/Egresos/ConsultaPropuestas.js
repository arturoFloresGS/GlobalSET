//@autor Cristian Garcia Garcia
//22/Feb/2011
Ext.onReady(function(){
	var NS = Ext.namespace('apps.SET.Egresos.ConsultaPropuestas');
	//NS.tabContId = 'contenedorConsultaPropuestas';
	var PF = apps.SET.tabID+'.'; // Generar prefijo para id html
    Ext.Direct.addProvider(Ext.setapp.REMOTING_API);	
	Ext.QuickTips.init(); 
	NS.fecHoy = apps.SET.FEC_HOY;
	NS.autoDesauto='';
	NS.parametroBus='';
	NS.tabContId = apps.SET.tabContainerId;
	NS.foliosCancela = "";
	NS.todosUsuarios = false;
	NS.propVigentes = false;
	NS.sDivisa = '';
	NS.folios = '';
	NS.noEmpresa = 0;
	NS.noEmpresaMulti = 0;
	NS.campoRef='';
	NS.jsonPropuestas;
	NS.jsonDetPropuestas;
	NS.registros;
	NS.recordsAuto;
	NS.descEmpresa = '';
	NS.descReglaNegocio = ''
	NS.noPersona = 0;
	NS.multisociedad = false;
	NS.idDivisaCambio = '';
	NS.usr1 = '';
	NS.usr2 = '';
	NS.usr3 = '';
	NS.autCheq = 0;
	
	NS.Excel = false;
	NS.autorizarModificacion = false;
	NS.fecModGlob = '';
	NS.seleccionPropuestaGlobal = ''; //Ayuda a no perder la selección de una propuesta del grid maestro cuando despliega sus detalles
	NS.indxMaestro = '';
	NS.modifPag = false;
	NS.modifBenef = false;
	NS.valida=false;
	
	var matrizBloqGlob = new Array();
	var matrizBancoChequerasGlob = new Array();
	var threadReportes;
	var contReporte = 0;
	var idPago=0;
	
	var mascara = new Ext.LoadMask(Ext.getBody(), {msg:"Cargando..."});
	
	//Funcion para verificar si ya existen los componentes de las ventanas modales.
	verificaComponentesCreados();
	
	//facultades de nomina
	NS.nominaRH = false;
	NS.nominaTes = false;
	
	verificaFacultadUsuario();
	
	//Para evitar el desface de los grids
	Ext.override(Ext.grid.GridView, {
	    getColumnWidth : function(col){
	        var w = this.cm.getColumnWidth(col);
	        if(typeof w == 'number'){
	            return (Ext.isBorderBox || Ext.isSafari3 ? w : (w-this.borderWidth > 0 ? w-this.borderWidth:0)) + 'px';
	        }
	        return w;
	    }
	});
	
	NS.filterRow = new Ext.ux.grid.FilterRow({
		id: PF + 'filterRow',
		refilterOnStoreUpdate: true
	});
	NS.filterRowDos = new Ext.ux.grid.FilterRow({
		id: PF + 'filterRowDos',
		refilterOnStoreUpdate: true
	});
	
	NS.sm = new Ext.grid.CheckboxSelectionModel({
		singleSelect: false,
	    //tbar:NS.gridTopbar,
        listeners:{
        	click:{
        		fn:function(e){
        			
        			// LIMPIA COMPONENTES DEL GRID A MODIFICAR EMS 261115
        			NS.cmbFormaPago.setValue("");
        			NS.cmbBancoPag.setValue("");
        			NS.cmbBancoPagBenef.setValue("");
        			//NS.cmbChequeraPag.setValue("");
        			NS.cmbChequeraPag.reset();
        			NS.storeChequeraPag.removeAll();
        			//NS.cmbChequeraPagBenef.setValue("");
        			NS.storeChequeraPagBenef.removeAll();
					NS.cmbChequeraPagBenef.reset();
        			Ext.getCmp(PF + 'txtBanco').setValue("");
        			Ext.getCmp(PF + 'txtBancoBenef').setValue("");
        			
        			/*NS.cmbFormaPago.setDisabled(false);
        			NS.cmbBancoPag.setDisabled(false);
        			NS.cmbBancoPagBenef.setDisabled(false);
        			NS.cmbChequeraPag.setDisabled(false);
        			NS.cmbChequeraPagBenef.setDisabled(false);
        			Ext.getCmp(PF + 'txtBanco').setDisabled(false);
        			Ext.getCmp(PF + 'txtBancoBenef').setDisabled(false);
        			Ext.getCmp(PF + 'dateNvaFecPago').setValue("");*/ 
        			//
        			
        			
          			var totalImporte=0;
          			var totalImporteDLS=0;
          			var sumaDetProp = 0;
          			var regSelec=NS.gridDetalle.getSelectionModel().getSelections();
          			
          			if(regSelec.length <= 0){
          				Ext.Msg.alert('SET','No hay detalles seleccionados.');
          				return;
          			}
          			
          			NS.sDivisa = regSelec[0].get('idDivisa');
          			
          			for(var k=0; k<regSelec.length; k++) {
          				if(regSelec[k].get('idDivisa') == 'DLS')
          					totalImporteDLS += regSelec[k].get('importe');
          				else
          					totalImporte += regSelec[k].get('importe');
          			}
          			
          			var regTodos =NS.gridDetalle.store.data.items;
          			for(var k=0; k<regTodos.length; k++) {
          				sumaDetProp += regTodos[k].get('importe');
          			}
          			
          			Ext.getCmp(PF+'totalMN').setValue(NS.formatNumber(Math.round((totalImporte)*100)/100));
          			Ext.getCmp(PF+'sumMN').setValue(NS.formatNumber(Math.round((sumaDetProp)*100)/100));
          			Ext.getCmp(PF+'totalDLS').setValue(NS.formatNumber(Math.round((totalImporteDLS)*100)/100));
          			
          			if(Ext.getCmp(PF + 'btnModificar').disabled && regSelec.length > 0){
          				
          				if(NS.modifBenef == true){
          					if(regSelec[0].get('descFormaPago') == 'TRANSFERENCIA' || regSelec[0].get('descFormaPago') == 'TRASPASO'){
	          					NS.noPersona = regSelec[0].get('noCliente');
	    	          			NS.storeBancoPagBenef.baseParams.idDivisa = NS.sDivisa;
	    	          			NS.storeBancoPagBenef.baseParams.noPersona = parseInt(NS.noPersona);
	    	          			
	    	          			NS.storeBancoPagBenef.load();
	          				}
          				}
          				
	          			
          			}
          			
        		}
        	}
        }
	});
	
	NS.iporteS = new Ext.form.TextField
	({
        xtype: 'textfield',
        x: 430,
        y: 210,
        width: 100,
        id: PF+'sumMN',
        name: PF+'sumMN',
        value: '0.00',
        disabled: true
    });
		
	NS.txtIdEmpresa = new Ext.form.TextField
	({			
		id: PF + 'txtIdEmpresa',
		name: PF + 'txtIdEmpresa',
		x: 10,
		y: 15,
		width: 30,
		value: '',
        disabled: false,
        tabIndex: 1,
        listeners: {
        	//YEC
    		change: {
    			fn:function(caja,valor) {
    				
    				if(valor != null && valor != undefined && valor != '') {
    					
    					var empresas = NS.cmbGrupoEmpresas.store.data.items;
    					var i=0; 
    					for(i=0 ; i< empresas.length; i++){
    						if(empresas[i].get('id') == valor ){
    							NS.cmbGrupoEmpresas.setValue(valor);
    							NS.storeMaestro.baseParams.idGrupoEmpresa = combo.getValue();
    							break;
    						}
    					}
    					
    					if(i == empresas.length){
    						NS.cmbGrupoEmpresas.reset();
    						NS.storeMaestro.baseParams.idGrupoEmpresa = 0;
    					}

    					//NS.storeMaestro.baseParams.idGrupoEmpresa = combo.getValue();	
    						
					  }else {
						 NS.cmbGrupoEmpresas.reset();
						 //NS.storeUnicaEmpresa.removeAll();
					  }
    				
    			}
    		}
        }
	
	});
	
	//Formato de un numero a monetario
	NS.formatNumber = function(num,prefix){
		num = num.toString();
		if(num.indexOf('.')>-1){
			if(num.substring(num.indexOf('.')).length<3){
				num = num + '0';
			}
		}
		else{
			num = num + '.00';
		}
		prefix = prefix || '';
		var splitStr = num.split('.');
		var splitLeft = splitStr[0];
		var splitRight = splitStr.length > 1 ? '.' + splitStr[1] : '';
		var regx = /(\d+)(\d{3})/;
		while (regx.test(splitLeft)) {
			splitLeft = splitLeft.replace(regx, '$1' + ',' + '$2');
		}
			return prefix + splitLeft + splitRight;
	};
	
	//Quitar formato a las cantidades
	NS.unformatNumber=function(num) {
		return num.replace(/(,)/g,''); //num.replace(/([^0-9\.\-])/g,''*1);
	};
	
	
	function cambiarFecha(fecha)
	{
		var mesArreglo=new Array(11);
		mesArreglo[0]="Jan";mesArreglo[1]="Feb";mesArreglo[2]="Mar";mesArreglo[3]="Apr";
		mesArreglo[4]="May";mesArreglo[5]="Jun";mesArreglo[6]="Jul";mesArreglo[7]="Aug";mesArreglo[8]="Sep";
		mesArreglo[9]="Oct";mesArreglo[10]="Nov";mesArreglo[11]="Dec";
		var mesDate=fecha.substring(4,7);
		var dia=fecha.substring(8,10);
		var anio=fecha.substring(11,15);
		var mes;
		
		for(var i=0;i<12;i=i+1)
		{
			if(mesArreglo[i]===mesDate)
			{
			mes = i+1;
				if(mes<10)
					mes='0'+mes;
			}
		}		
		var fechaString=''+dia+'/'+mes+'/'+anio;
		return fechaString;		
	}
	
	NS.fechaInicial = function(fechaIni) {
		var fec = BFwrk.Util.sumDate(fechaIni, 'dia', -90);
		return BFwrk.Util.changeStringToDate(fec);
	};
	
	/* 31/01/2013 -90
	 * 
	 * 
	 * */
	
	NS.cambiFecha = function(fecha, diasSR) {
		var dia = parseInt(fecha.substring(0, 2));
		var mes = parseInt(fecha.substring(3, 5));
		var year = parseInt(fecha.substring(6, 10));
		var diasMes = 0;
		var sDia = '';
		var sMes = '';
		
		if(diasSR < 0) {	//el caso de restar dias a la fecha
			while(diasSR < 0) {
				diasSR += dia;
				
				if(diasSR < 0) mes--;
				if(mes == 0) {
					mes = 12;
					year--; 
				}
				dia = BFwrk.Util.daysInMonth(mes, year);
			}
		}else {	////el caso de sumar dias a la fecha
			while(diasSR > 0) {
				diasSR += diasMes;
				
				if(diasSR < 0) mes++;
				if(mes == 13) {
					mes = 1;
					year++; 
				}
				dia = BFwrk.Util.daysInMonth(mes, year);
			}
		}
		sDia = diasSR+'';
		sMes = mes+'';
		sDia = sDia.length == 1 ? '0'+sDia : sDia;
		sMes = sMes.length == 1 ? '0'+sMes : sMes
						
		return sDia + "/" + sMes + "/" + year
	}
	
	//Store para Reglas negocios
	NS.storeReglaNegocio = new Ext.data.DirectStore({
		paramsAsHash: false,
		baseParams:{
			tipoRegla: 'S'
		},
		root: '',
		paramOrder:['tipoRegla'],
		root: '',
		directFn: ConsultaPropuestasAction.llenarComboReglaNegocio, 
		idProperty: 'id', 
		fields: [
			 {name: 'id'},
			 {name: 'descripcion'},
		],
		listeners: {
			load: function(s, records){
				var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeReglaNegocio, msg:"Cargando..."});
				if(records.length==null || records.length<=0){
					Ext.Msg.alert('SET','No Existen reglas de negocios');
					return;
				}
				
				var recordsStoreReglas = NS.storeReglaNegocio.recordType;	
				var todas = new recordsStoreReglas({
			       	id : 0,
			       	descripcion : '***** TODAS *****'
		       	});
		   		NS.storeReglaNegocio.insert(0,todas);
		   		
			},
			exception: function(mist){
				Ext.Msg.alert('SET','Error en conexión al servidor');
				mascara.hide();
			}
		}
	}); 
	NS.storeReglaNegocio.load();
	
	//store  Grupo
	    NS.storeGrupo = new Ext.data.DirectStore({
		paramsAsHash: false,
		baseParams:{
			campoUno:'id_grupo_cupo',
			campoDos:'desc_grupo_cupo',
			tabla:'cat_grupo_cupo',
			condicion:'',
			orden:''
		},
		root: '',
		paramOrder:['campoUno','campoDos','tabla','condicion','orden'],
		root: '',
		directFn: ConsultaPropuestasAction.llenarCombo, 
		idProperty: 'id', 
		fields: [
			 {name: 'id'},
			 {name: 'descripcion'},
		],
		listeners: {
			load: function(s, records){
				var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeGrupo, msg:"Cargando..."});
				if(records.length==null || records.length<=0)
				{
					Ext.Msg.alert('SET','No Existen grupos');
				}
			},
			exception: function(mist){
				Ext.Msg.alert('SET','Error en conexión al servidor');
				mascara.hide();
			}
		}
	}); 
	
	NS.eliminaDetalleDePropuesta = function() {
		var registroSelDetalle = NS.gridDetalle.getSelectionModel().getSelections();
		var registroSelPropuesta = NS.gridMaestroPropuestas.getSelectionModel().getSelections();
		NS.foliosCancela = "";
		var pdImporte = 0;
		var matrizCancela = new Array();
		var c = 0;
		
		Ext.Msg.confirm('SET', '¿Esta seguro de eliminar los registros de la propuesta?', function(btn){
			if (btn === 'yes'){		
				for (var i = 0; i < registroSelDetalle.length; i++){			
					var vectorCancela = {};
					
					vectorCancela.noEmpresa = registroSelDetalle[i].get('noEmpresa');
					vectorCancela.noFolioDet = registroSelDetalle[i].get('noFolioDet');
					vectorCancela.importe = registroSelDetalle[i].get('importe');
					vectorCancela.importeMN = registroSelDetalle[i].get('importeMn');
					vectorCancela.noCheque = registroSelDetalle[i].get('noCheque');
					vectorCancela.estatusPropuesta = registroSelPropuesta[0].get('estatus');
					vectorCancela.cveControl = registroSelPropuesta[0].get('cveControl');
					vectorCancela.numIntercos = registroSelPropuesta[0].get('numIntercos');
					vectorCancela.importeTotal = registroSelPropuesta[0].get('cupoTotal');
					vectorCancela.fecHoy = NS.fecHoy;
					
					matrizCancela[c] = vectorCancela;
					c = c + 1;
				}
				var jSonString = Ext.util.JSON.encode(matrizCancela);
				
				ConsultaPropuestasAction.eliminaDetallePropuesta(jSonString, function(resultado, e) {
					
					if(e.message=="Unable to connect to the server."){
						Ext.Msg.alert('SET','Error de conexión al servidor');
						return;
					}
					
					if (resultado != '' && resultado != null && resultado != undefined) {
						Ext.Msg.alert('SET', resultado);
						//console.log("respuesta de eliminacion "+resultado);
						
						//Inserta en bitacora propuesta
						ConsultaPropuestasAction.insertaBitacoraPropuesta('ElimDetProp', jSonString, 
								'', '' , function(result, e){
							
							if(e.message=="Unable to connect to the server."){
								Ext.Msg.alert('SET','Error de conexión al servidor');
								return;
							}
							
							if(result != null && result.length != '') {
								if(result.error != ''){
									Ext.Msg.alert('SET',''+result.error);
									console.log("error "+result.error);
								}
							}
						});
						
						NS.buscar();
					}
				});
			}
		});
	};
	

	//store  GrupoEmpresas
	NS.storeGrupoEmpresas = new Ext.data.DirectStore({
		paramsAsHash: false,
		baseParams:{
		},
		root: '',
		paramOrder:[],
		root: '',
		directFn: ConsultaPropuestasAction.llenarComboGrupoEmpresas, 
		idProperty: 'id', 
		fields: [
			 {name: 'id'},
			 {name: 'descripcion'},
		],
		listeners: {
			load: function(s, records){
				var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeGrupoEmpresas, msg:"Cargando..."});
				if(records.length==null || records.length<=0)
				{
					Ext.Msg.alert('SET','No Existen empresas asignadas al usuario');
				}
			},
			exception: function(mist){
				Ext.Msg.alert('SET','Error en conexión al servidor');
				mascara.hide();
			}
		}
	}); 
	NS.storeGrupoEmpresas.load();
		
 	NS.storeUnicaEmpresa = new Ext.data.DirectStore({
 		paramsAsHash: false,
		baseParams:{
			idEmpresa: '0'
		},
		root: '',
		paramOrder:['idEmpresa'],
		root: '',
		directFn: ConsultaPropuestasAction.llenarComboGrupoEmpresaUnica, 
		idProperty: 'id', 
		fields: [
			 {name: 'id'},
			 {name: 'descripcion'}
		],
		listeners: {
			load: function(s, records){
				var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeUnicaEmpresa, msg:"Cargando..."});
				if(records.length==null || records.length<=0)
				{
					Ext.Msg.alert('SET','La empresa no existe');
					Ext.getCmp(PF + 'cmbGrupoEmpresas').reset();
					Ext.getCmp(PF + 'txtIdEmpresa').reset();
					return;
				}
				else{
					var reg = NS.storeUnicaEmpresa.data.items;
					Ext.getCmp(NS.cmbGrupoEmpresas.getId()).setValue(reg[0].get('descripcion'));
					//NS.accionarUnicaEmpresa(reg[0].get('id'));
				}
			},
			exception: function(mist){
				Ext.Msg.alert('SET','Error en conexión al servidor');
				mascara.hide();
			}
			
		}
	});
	
	//combo Grupo
	NS.cmbGrupoEmpresas = new Ext.form.ComboBox({
	store: NS.storeGrupoEmpresas
	,name: PF+'cmbGrupoEmpresas'
	,id: PF+'cmbGrupoEmpresas'
	,typeAhead: true
	,mode: 'local'
	,minChars: 0
	,selecOnFocus: true
	,forceSelection: true
  	,x: 45
    ,y: 15
    ,width: 255
	,valueField:'id'
	,displayField:'descripcion'
	,autocomplete: true
	,emptyText: 'Seleccione un grupo empresa'
	,triggerAction: 'all'
	,value: ''
	,visible: false
	,tabIndex: 2
	,listeners:{
		select:{
			fn:function(combo, valor) {
				//BFwrk.Util.updateComboToTextField(PF + 'txtIdEmpresa', NS.cmbGrupoEmpresas.getId());
				//Ext.getCmp(PF + 'txtIdEmpresa').setValue(''+Ext.getCmp(NS.cmbGrupoEmpresas.getId()).getValue());
				//NS.accionarEmpresas(combo.getValue());
				NS.storeMaestro.baseParams.idGrupoEmpresa =combo.getValue();
				
				BFwrk.Util.updateComboToTextField(PF+'txtIdEmpresa',NS.cmbGrupoEmpresas.getId());
				//NS.accionarEmpresas(combo.getValue());
			}
		}
	}
	});
	
	NS.storeNiveles= new Ext.data.DirectStore({
		paramsAsHash: false,
		root: '',
		baseParams:{},
		paramOrder:[],
		directFn: ConsultaPropuestasAction.obtenerNiveles,
		idProperty: 'nivel', 
		fields: [
			 {name: 'nivelAutorizacion'},
			// {name: 'nomEmpresa'}
		],
		listeners: {
			load: function(s, records){
				var nivel=false;
				var datos= NS.storeNiveles.data.items;
				for(var k = 0; k < datos.length; k++){	
					if(datos[k].get('nivelAutorizacion')>1){								
						nivel=true;
					}
				}
				//Ext.Msg.alert("SET",nivel);
				if(nivel==true){
					Ext.getCmp(PF+'cmdAutorizar').setText("Revisar");	
					Ext.getCmp(PF+'cmbQuitAut').setText("Eliminar Revision");
				}
			},
			exception: function(mist){
				Ext.Msg.alert('SET','Error en conexión al servidor');
				mascara.hide();
			}
		}
	});
	NS.storeNiveles.load();
	 	
	NS.storeEmpresas = new Ext.data.DirectStore({
		paramsAsHash: false,
		root: '',
		baseParams:{},
		paramOrder:[],
		directFn: ConsultaPropuestasAction.llenarComboEmpresa,
		idProperty: 'id', 
		fields: [
			 {name: 'noEmpresa'},
			 {name: 'nomEmpresa'}
		],
		listeners: {
			load: function(s, records){
			},
			exception: function(mist){
				Ext.Msg.alert('SET','Error en conexión al servidor');
				mascara.hide();
			}
		}
	}); 
	
	//Este store se carga segun la clave tecleada en la caja de texto
	NS.storeUnicoBeneficiario = new Ext.data.DirectStore({
		paramsAsHash: false,
		baseParams:{
			campoUno:'equivale_persona',
			campoDos:'',
			tabla:'persona',
			condicion:'',
			orden:'',
			registroUnico:false
		},
		root: '',
		paramOrder:['campoUno','campoDos','tabla','condicion','orden','registroUnico'],
		root: '',
		directFn: ConsultaPropuestasAction.llenarComboBeneficiario, 
		idProperty: 'id', 
		fields: [
			 {name: 'id'},
			 {name: 'descripcion'},
		],
		listeners: {
			load: function(s, records){
				var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeUnicoBeneficiario, msg:"Cargando..."});
				if(records.length==null || records.length<=0)
				{
					Ext.Msg.alert('SET','No Existen el beneficiario con esa clave');
					Ext.getCmp(PF + 'txtIdProveedor').reset();
					return;
				}
				else{
					var reg = NS.storeUnicoBeneficiario.data.items;
					Ext.getCmp(NS.cmbBeneficiario.getId()).setValue(reg[0].get('descripcion'));
					NS.accionarUnicoBeneficiario(reg[0].get('id'));
				}
			}
		}
	}); 
	
		//store  Beneficiario
    NS.storeBeneficiario = new Ext.data.DirectStore({
		paramsAsHash: false,
		baseParams:{
			campoUno:'equivale_persona',
			campoDos:'',
			tabla:'persona',
			condicion:'',
			orden:'',
			registroUnico:false
		},
		root: '',
		paramOrder:['campoUno','campoDos','tabla','condicion','orden','registroUnico'],
		root: '',
		directFn: ConsultaPropuestasAction.llenarComboBeneficiario, 
		idProperty: 'idStr', 
		fields: [
			 {name: 'idStr'},
			 {name: 'descripcion'},
		],
		listeners: {
			load: function(s, records){
				var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeBeneficiario, msg:"Cargando..."});
				if(records.length==null || records.length<=0)
				{
					Ext.Msg.alert('SET','No Existen Beneficiarios con ese nombre');
				}
			},
			exception: function(mist){
				Ext.Msg.alert('SET','Error en conexión al servidor');
				mascara.hide();
			}
		}
	}); 
	
	NS.accionarUnicoBeneficiario = function(valueCombo){
 		NS.idPersona=valueCombo;
 		NS.nomPersona=NS.storeUnicoBeneficiario.getById(valueCombo).get('descripcion');
 	}
	
 	NS.accionarBeneficiario = function(valueCombo){
 		NS.idPersona=valueCombo;
 		NS.nomPersona=NS.storeBeneficiario.getById(valueCombo).get('descripcion');
 	}
	
	//combo Beneficiario
	NS.cmbBeneficiario = new Ext.form.ComboBox({
		store: NS.storeBeneficiario
		,name: PF+'cmbBeneficiario'
		,id: PF+'cmbBeneficiario'
		,typeAhead: true
		,mode: 'local'
		//,mode: 'remote'
		,minChars: 0
		,selecOnFocus: true
		,forceSelection: true
		//,hideTrigger: true
        ,pageSize: 10
        ,x: 135
        ,y: 120
        ,width: 175
		,valueField:'idStr'
		,displayField:'descripcion'
		,autocomplete: true
		,emptyText: 'Seleccione beneficiario'
		,triggerAction: 'all'
		,value: ''
		,visible: false
		,editable: true
		,hidden: false //true
		,tabIndex: 11
		,listeners:{
			select:{
				fn:function(combo, valor) {
					BFwrk.Util.updateComboToTextField(PF+'txtIdProveedor',NS.cmbBeneficiario.getId());
					NS.accionarBeneficiario(combo.getValue());
				}
			},
				beforequery: function(qe){
					NS.parametroBus=qe.combo.getRawValue();
				}
		}
	});
	
	
	//Store para llenar el grid maestro
  	 NS.storeMaestro = new Ext.data.DirectStore({
  	 	paramsAsHash: false,
  	 	baseParams: {
			idGrupoEmpresa:0,
			idProv:'0',
			idGrupoRubro:0,
	        fecIni: apps.SET.FEC_HOY,
	        fecFin: apps.SET.FEC_HOY,
	        todosUsuarios: 'false', //por default se muestran solo las propuestas del usuario.
	        propVigentes:'false',
	        todasPro: false,
	        divMN: false,
	        divDLS:false,
	        divEUR:false,
	        divOtras:false,
	        tipoRegla: 'S',
	        reglaNegocio: '',
	        nominaRH: false,
	        nominaTes: false
		},
		root: '',
		paramOrder:['idGrupoEmpresa','idProv','idGrupoRubro','fecIni','fecFin','todosUsuarios','propVigentes', 'todasPro','divMN','divDLS','divEUR','divOtras','tipoRegla', 'reglaNegocio', 'nominaRH','nominaTes'],
		directFn: ConsultaPropuestasAction.consultarSeleccionAutomatica,
		//idProperty: 'marca',
		fields: [
			{name: 'cveControl'}, //id propuesta
			{name: 'idGrupoFlujo'}, 
			{name: 'propuesto'},
			{name: 'cupoTotal'}, //total
			{name: 'descGrupoCupo'},
			//{name: 'fecLimiteSelecc'}, //Fecha elaboracion
			//Recibe un tipo String formato dd/mm/yyyy y lo comvierte a date de Extjs.
			{name: 'fecLimiteSelecc', type: 'date', dateFormat:'d/m/Y'}, //Fecha elaboracion
			{name: 'fechaPropuesta',  type: 'date', dateFormat:'d/m/Y'}, 
			//{name: 'fechaPropuesta'},
			{name: 'idGrupo'}, //sociedad
			{name: 'descGrupoFlujo'}, //nombre sociedad
			{name: 'divisa'}, //moneda
			{name: 'origenPropuesta'}, //origen propuesta
			{name: 'concepto'},
			{name: 'estatus'},
			{name: 'usuarioUno'},
			{name: 'usuarioDos'},
			{name: 'usuarioTres'},
			{name: 'nivelAutorizacion'},
			//{name: 'idDivision'},
			//{name: 'numIntercos'},
			//{name: 'totalIntercos'},
			{name: 'user1'},
			{name: 'user2'},
			{name: 'user3'},
			{name: 'viaPago'},
			{name: 'color'},
			{name: 'habilitado'},
			{name: 'motivoRechazo'},
			{name: 'nombreRegla'}
		],
		listeners: {
			load: function(s, records){
				//var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeMaestro, msg:"Cargando..."});
				
				if(records.length==null || records.length<=0){
					Ext.Msg.alert('SET','No existen propuestas con los parámetros de búsqueda');
				}
				
				if(NS.indxMaestro != ''){
					//Una vez que realice la búsqueda se selecciona el registro maestro que estaba seleccionado
					//Para seleccionarlo y cargar sus detalles (cuando es eliminado un detalle).
					
					var datosPropuestas = NS.gridMaestroPropuestas.store.data.items;
					for(var k = 0; k < datosPropuestas.length; k++){	
						//Ext.getCmp(PF+'cmdAutorizar').setValue("Autoriza");
						if(datosPropuestas[k].get('cveControl') == NS.indxMaestro){								
							//NS.sm.selectRange(k,k);
							NS.seleccion.selectRow(k, false, false);
							break;
						}
					}
					
					//NS.indxMaestro = '';
				}
				
				
				/*for(var i=0; i< records.length; i++){
					NS.gridMaestroPropuestas.getView().getRow(i).style.backgroundColor  = '#4BD844';
					//NS.gridMaestroPropuestas.getView().getRow(i).style.color  = '#4BD844';
				}
				
				NS.gridMaestroPropuestas.setVisible(false);
				NS.gridMaestroPropuestas.setVisible(true);
				*/
				
				//mascara.hide();
				if(NS.indxMaestro == ''){
					mascara.hide();
				}else{
					NS.indxMaestro = '';
					//EMS: 22/04/2016 - Agregado por la modificación del dlbclick a las columnas del grid maestro,
					//para que ejecute el cargar detalle cuando se hace una modificación a un movimiento.
					NS.clickGridMaestroPropuestas();
				}
				
			},
			exception: function(mist){
				Ext.Msg.alert('SET','Error en conexión al servidor');
				mascara.hide();
			}
		}
	});
  	 
  	//Para multiple seleccion en el grid
 	
  	 NS.seleccion = new Ext.grid.CheckboxSelectionModel({
 		
 		singleSelect: false,
 		listeners: {
 			//beforerowselect: function(chkSelectionModel, rowIndex, keepExisting, record ){
 			
 			//Se sustituye el evento click del gridMaestroPropuestas por este rowselect.
 			rowselect: function(chkSelectionModel, rowIndex, keepExisting, record ){
 				
 				NS.gridDetalle.store.removeAll();
 				NS.gridDetalle.getView().refresh();
 				Ext.getCmp(PF + 'txtImporteProp').setValue('0.00');
				Ext.getCmp(PF+'totalMN').setValue('0.00');
      			Ext.getCmp(PF+'sumMN').setValue('0.00');
      			Ext.getCmp(PF+'totalDLS').setValue('0.00');
      			
 				var regSeleccionados = NS.gridMaestroPropuestas.getSelectionModel().getSelections();
    			var importeMN = 0;
    			var importeDLS = 0;
    			var importeEUR = 0;
    			var importeOTR = 0;
    			
    			Ext.getCmp(PF + 'txtImporteProp').setValue(NS.formatNumber(Math.round((importeMN)*100)/100));
    			Ext.getCmp(PF + 'txtImporteDLSProp').setValue(NS.formatNumber(Math.round((importeDLS)*100)/100));
    			Ext.getCmp(PF + 'txtImporteEURProp').setValue(NS.formatNumber(Math.round((importeEUR)*100)/100));
    			Ext.getCmp(PF + 'txtImporteOTRProp').setValue(NS.formatNumber(Math.round((importeOTR)*100)/100 ));
    			
    			if(regSeleccionados != null && regSeleccionados != undefined && regSeleccionados != '') {
    				
    				for(var i=0; i<regSeleccionados.length; i++) {
    					
    					if(regSeleccionados[i].get('concepto') != null && regSeleccionados[i].get('concepto') != undefined && regSeleccionados[i].get('concepto') != ''){
    						
        					if(regSeleccionados[i].get('concepto').indexOf('MN') > 0){
        						importeMN += parseFloat(NS.unformatNumber('' + regSeleccionados[i].get('cupoTotal')));
        						Ext.getCmp(PF + 'txtImporteProp').setValue(NS.formatNumber(Math.round((importeMN)*100)/100 ));
        					}else if(regSeleccionados[i].get('concepto').indexOf('DLS') > 0){
        						importeDLS += parseFloat(NS.unformatNumber('' + regSeleccionados[i].get('cupoTotal')));
        						Ext.getCmp(PF + 'txtImporteDLSProp').setValue(NS.formatNumber(Math.round((importeDLS)*100)/100));
        					}else if(regSeleccionados[i].get('concepto').indexOf('EUR') > 0){
        						importeEUR += parseFloat(NS.unformatNumber('' + regSeleccionados[i].get('cupoTotal')));
        						Ext.getCmp(PF + 'txtImporteEURProp').setValue(NS.formatNumber(Math.round((importeEUR)*100)/100 ));
        					}else{
        						importeOTR += parseFloat(NS.unformatNumber('' + regSeleccionados[i].get('cupoTotal')));
        						Ext.getCmp(PF + 'txtImporteOTRProp').setValue(NS.formatNumber(Math.round((importeOTR)*100)/100 ));
        					}
    					}
        					
					}
    				
    			}
			},
			rowdeselect: function(chkSelectionModel, rowIndex, keepExisting, record ){
				NS.gridDetalle.store.removeAll();
 				NS.gridDetalle.getView().refresh();
 				Ext.getCmp(PF + 'txtImporteProp').setValue('0.00');
				Ext.getCmp(PF+'totalMN').setValue('0.00');
      			Ext.getCmp(PF+'sumMN').setValue('0.00');
      			Ext.getCmp(PF+'totalDLS').setValue('0.00');
      			
 				var regSeleccionados = NS.gridMaestroPropuestas.getSelectionModel().getSelections();
    			var importeMN = 0;
    			var importeDLS = 0;
    			var importeEUR = 0;
    			var importeOTR = 0;
    			
    			Ext.getCmp(PF + 'txtImporteProp').setValue(NS.formatNumber(Math.round((importeMN)*100)/100));
    			Ext.getCmp(PF + 'txtImporteDLSProp').setValue(NS.formatNumber(Math.round((importeDLS)*100)/100));
    			Ext.getCmp(PF + 'txtImporteEURProp').setValue(NS.formatNumber(Math.round((importeEUR)*100)/100));
    			Ext.getCmp(PF + 'txtImporteOTRProp').setValue(NS.formatNumber(Math.round((importeOTR)*100)/100 ));
    			
    			if(regSeleccionados != null && regSeleccionados != undefined && regSeleccionados != '') {
    				
    				for(var i=0; i<regSeleccionados.length; i++) {
    					
    					if(regSeleccionados[i].get('concepto') != null && regSeleccionados[i].get('concepto') != undefined && regSeleccionados[i].get('concepto') != ''){
    						
        					if(regSeleccionados[i].get('concepto').indexOf('MN') > 0){
        						importeMN += parseFloat(NS.unformatNumber('' + regSeleccionados[i].get('cupoTotal')));
        						Ext.getCmp(PF + 'txtImporteProp').setValue(NS.formatNumber(Math.round((importeMN)*100)/100 ));
        					}else if(regSeleccionados[i].get('concepto').indexOf('DLS') > 0){
        						importeDLS += parseFloat(NS.unformatNumber('' + regSeleccionados[i].get('cupoTotal')));
        						Ext.getCmp(PF + 'txtImporteDLSProp').setValue(NS.formatNumber(Math.round((importeDLS)*100)/100));
        					}else if(regSeleccionados[i].get('concepto').indexOf('EUR') > 0){
        						importeEUR += parseFloat(NS.unformatNumber('' + regSeleccionados[i].get('cupoTotal')));
        						Ext.getCmp(PF + 'txtImporteEURProp').setValue(NS.formatNumber(Math.round((importeEUR)*100)/100 ));
        					}else{
        						importeOTR += parseFloat(NS.unformatNumber('' + regSeleccionados[i].get('cupoTotal')));
        						Ext.getCmp(PF + 'txtImporteOTRProp').setValue(NS.formatNumber(Math.round((importeOTR)*100)/100 ));
        					}
    					}
        					
					}
    				
    			}
			}
		}	
 	});
 	
  	 
  	/*
  	 NS.seleccion = new Ext.grid.CheckColumn({
        header: 'ab',
        id: PF + 'selGridMaestro',
        width:35,
        //dataIndex: 'bandera',
        value:true,
        listeners:{
       	 click:function(column,recordIndex, checked){
		 },
        }
   });
  */

 	//Columnas del grid	    	
 	NS.columnas = new Ext.grid.ColumnModel
 	([	
 	  	NS.seleccion,
 	  	{
 			header :'Nombre Regla',
 			width :80,
 			sortable :true,
 			dataIndex :'nombreRegla',
 			renderer: function (value, meta, record) {
 				if(record.get('color') === 'color:#C5B8D6' ){
 					meta.attr = 'style="background-color:#C5B8D6;"';
 				}else{
 					meta.attr = 'style=' + record.get('color');
 				}
 	            return value;
 	        },
 	        listeners:{
		    	dblclick:{
		    		fn:function(e){
		    			NS.clickGridMaestroPropuestas();
	      			}
	    		}
	    	},
	    	filter: {testTex: "/^{0}/i"}
 		},{	
 			header :'Fecha Pago',
 			width :100,
 			xtype: 'datecolumn',
 			format: 'd/m/Y',
 			//dateFormat: 'd/m/Y',	
 			sortable :true,
 			dataIndex :'fechaPropuesta', //fechaPropuesta
 			editor: {
	            xtype : 'datefield',
	            //format: 'Y-m-d H:i:s',
	            format: 'd/m/Y',
	            submitFormat: 'c'
	        },
 			renderer: function (value, meta, record) {
 				if(record.get('color') === 'color:#C5B8D6' ){
 					meta.attr = 'style="background-color:#C5B8D6;"';
 				}else{
 					meta.attr = 'style=' + record.get('color');
 				}
 	            return value;
 			},
 	        listeners:{
		    	dblclick:{
		    		fn:function(e){
		    			NS.clickGridMaestroPropuestas();
	      			}
	    		}
	    	}
 		},{	
 			header :'Grupo de propuesta', //no empresa/no Grupo
 			width :100,
 			sortable :true,
 			dataIndex :'idGrupoFlujo', 
 			renderer: function (value, meta, record) {
 				if(record.get('color') === 'color:#C5B8D6' ){
 					meta.attr = 'style="background-color:#C5B8D6;"';
 				}else{
 					meta.attr = 'style=' + record.get('color');
 				}
 	            return value;
 			},
 			hidden: true,
 	        listeners:{
		    	dblclick:{
		    		fn:function(e){
		    			NS.clickGridMaestroPropuestas();
	      			}
	    		}
	    	}
 		},{	
 			header :'Nombre de Grupo de Propuesta', //PENDIENTE
 			width :200,
 			sortable :true,
 			dataIndex :'descGrupoFlujo', 
 			renderer: function (value, meta, record) {
 				if(record.get('color') === 'color:#C5B8D6' ){
 					meta.attr = 'style="background-color:#C5B8D6;"';
 				}else{
 					meta.attr = 'style=' + record.get('color');
 				}
 	            return value;
 			},
 			hidden: true,
 	        listeners:{
		    	dblclick:{
		    		fn:function(e){
		    			NS.clickGridMaestroPropuestas();
	      			}
	    		}
	    	}
 		},/*{
 			header :'Fecha Propuesta Original',
 			width :100,
 			sortable :true,
 			//renderer: Ext.util.Format.dateRenderer('Y-m-d'),
 			//renderer: Ext.util.Format.dateRenderer('d/m/Y'),
 			dataIndex :'fecLimiteSelecc',
 			renderer: function (value, meta, record) {
 	            meta.attr = 'style=' + record.get('color');
 	            return value;
 	        }
 		},{
 			header :'Monto Propuesto',
 			width :100,
 			sortable :true,
 			dataIndex :'propuesto',
 			hidden: true,
 			renderer: function (value, meta, record) {
 	            meta.attr = 'style=' + record.get('color');
 	            return '$ ' + NS.formatNumber(value);
 	        }
 		},*/{
 			header :'Total',
 			width :80,
 			sortable :true,
 			dataIndex :'cupoTotal',
 			css: 'text-align:right;',
 			renderer: function (value, meta, record) {
 				if(record.get('color') === 'color:#C5B8D6' ){
 					meta.attr = 'style="background-color:#C5B8D6;"';
 				}else{
 					meta.attr = 'style=' + record.get('color');
 				}
 	            return '$ ' + NS.formatNumber(Math.round((value)*100)/100);
 	        },
 	        listeners:{
		    	dblclick:{
		    		fn:function(e){
		    			NS.clickGridMaestroPropuestas();
	      			}
	    		}
	    	}
 		},{
 			header :'Moneda', //divisa
 			width :80,
 			sortable :true,
 			dataIndex :'divisa',
 			css: 'text-align:right;',
 			renderer: function (value, meta, record) {
 				if(record.get('color') === 'color:#C5B8D6' ){
 					meta.attr = 'style="background-color:#C5B8D6;"';
 				}else{
 					meta.attr = 'style=' + record.get('color');
 				}
 	            return value;
 	        },
 	        listeners:{
		    	dblclick:{
		    		fn:function(e){
		    			NS.clickGridMaestroPropuestas();
	      			}
	    		}
	    	}
 		},{
 			header :'Vía Pago',	//PENDIENTE
 			width :80,
 			sortable :true,
 			dataIndex :'viaPago',
 			renderer: function (value, meta, record) {
 				if(record.get('color') === 'color:#C5B8D6' ){
 					meta.attr = 'style="background-color:#C5B8D6;"';
 				}else{
 					meta.attr = 'style=' + record.get('color');
 				}
 	            return value;
 	        },
 	        listeners:{
		    	dblclick:{
		    		fn:function(e){
		    			NS.clickGridMaestroPropuestas();
	      			}
	    		}
	    	}
 		},{
			header :'ID Propuesta',
			width :100,
			sortable :true,
			dataIndex :'cveControl',
			renderer: function (value, meta, record) {
				if(record.get('color') === 'color:#C5B8D6' ){
 					meta.attr = 'style="background-color:#C5B8D6;"';
 				}else{
 					meta.attr = 'style=' + record.get('color');
 				}
				return value;
			},
 	        listeners:{
		    	dblclick:{
		    		fn:function(e){
		    			NS.clickGridMaestroPropuestas();
	      			}
	    		}
	    	}
		},{
			header :'Origen Propuesta',
			width :100,
			sortable :true,
			dataIndex :'origenPropuesta',
			renderer: function (value, meta, record) {
				if(record.get('color') === 'color:#C5B8D6' ){
 					meta.attr = 'style="background-color:#C5B8D6;"';
 				}else{
 					meta.attr = 'style=' + record.get('color');
 				}
				return value;
			},
 	        listeners:{
		    	dblclick:{
		    		fn:function(e){
		    			NS.clickGridMaestroPropuestas();
	      			}
	    		}
	    	}
		},{	
 			header :'Observación', //no empresa/no Grupo
 			width :100,
 			sortable :true,
 			dataIndex :'estatus', 
 			renderer: function (value, meta, record) {
 				if(record.get('color') === 'color:#C5B8D6' ){
 					meta.attr = 'style="background-color:#C5B8D6;"';
 				}else{
 					meta.attr = 'style=' + record.get('color');
 				}
 	            return value;
 			},
 	        listeners:{
		    	dblclick:{
		    		fn:function(e){
		    			NS.clickGridMaestroPropuestas();
	      			}
	    		}
	    	}
 		},{	
 			header :'Motivo', //no empresa/no Grupo
 			width :100,
 			sortable :true,
 			dataIndex :'motivoRechazo', 
 			renderer: function (value, meta, record) {
 				if(record.get('color') === 'color:#C5B8D6' ){
 					meta.attr = 'style="background-color:#C5B8D6;"';
 				}else{
 					meta.attr = 'style=' + record.get('color');
 				}
 	            return value;
 			},
 	        listeners:{
		    	dblclick:{
		    		fn:function(e){
		    			NS.clickGridMaestroPropuestas();
	      			}
	    		}
	    	}
 		},/*{
			header :'Usuario',
			width :100,
			sortable :true,
			dataIndex :'cveControl',
			renderer: function (value, meta, record) {
				meta.attr = 'style=' + record.get('color');
				return value;
			}
		},*/{
 			header :'Nivel 1', 
 			width :75, sortable :true, 
 			dataIndex :'user1', 
 			hidden: false,
 			renderer: function (value, meta, record) {
 				if(record.get('color') === 'color:#C5B8D6' ){
 					meta.attr = 'style="background-color:#C5B8D6;"';
 				}else{
 					meta.attr = 'style=' + record.get('color');
 				}
 				return value;
 			},
 	        listeners:{
		    	dblclick:{
		    		fn:function(e){
		    			NS.clickGridMaestroPropuestas();
	      			}
	    		}
	    	}
 		},{
 			header :'Nivel 2', 
 			width :75, sortable :true, 
 			dataIndex :'user2', 
 			hidden: true,
 			renderer: function (value, meta, record) {
 				if(record.get('color') === 'color:#C5B8D6' ){
 					meta.attr = 'style="background-color:#C5B8D6;"';
 				}else{
 					meta.attr = 'style=' + record.get('color');
 				}
 				return value;
 			},
 	        listeners:{
		    	dblclick:{
		    		fn:function(e){
		    			NS.clickGridMaestroPropuestas();
	      			}
	    		}
	    	}
 		},{
 			header :'Nivel 3', 
 			width :75, sortable :true, 
 			dataIndex :'user3', 
 			hidden: true,
 			renderer: function (value, meta, record) {
 				if(record.get('color') === 'color:#C5B8D6' ){
 					meta.attr = 'style="background-color:#C5B8D6;"';
 				}else{
 					meta.attr = 'style=' + record.get('color');
 				}
 				return value;
 			},
 	        listeners:{
		    	dblclick:{
		    		fn:function(e){
		    			NS.clickGridMaestroPropuestas();
	      			}
	    		}
	    	}
 		},{
			header :'Nivel de Autorización',
			width :120,
			sortable :true,
			dataIndex :'nivelAutorizacion',
			hidden: false,
			renderer: function (value, meta, record) {
				if(record.get('color') === 'color:#C5B8D6' ){
 					meta.attr = 'style="background-color:#C5B8D6;"';
 				}else{
 					meta.attr = 'style=' + record.get('color');
 				}
	            return value;
	        },
 	        listeners:{
		    	dblclick:{
		    		fn:function(e){
		    			NS.clickGridMaestroPropuestas();
	      			}
	    		}
	    	}
		},{	
 			header :'Fecha Elaboración',
 			width :100,
 			xtype: 'datecolumn',
 			format: 'd/m/y',
 			sortable :true,
 			dataIndex :'fecLimiteSelecc',
 			//renderer: Ext.util.Format.dateRenderer('Y-m-d'),
 			editor: {
	            xtype : 'datefield',
	            //format: 'Y-m-d H:i:s',
	            format: 'd/m/Y',
	            submitFormat: 'c'  
	        },
 			renderer: function (value, meta, record) {
 				if(record.get('color') === 'color:#C5B8D6' ){
 					meta.attr = 'style="background-color:#C5B8D6;"';
 				}else{
 					meta.attr = 'style=' + record.get('color');
 				}
  	            return value;
 			},
 	        listeners:{
		    	dblclick:{
		    		fn:function(e){
		    			NS.clickGridMaestroPropuestas();
	      			}
	    		}
	    	}
 		},{
 			header :'Concepto',
 			width :120,
 			sortable :true,
 			dataIndex :'concepto',
 			renderer: function (value, meta, record) {
 				if(record.get('color') === 'color:#C5B8D6' ){
 					meta.attr = 'style="background-color:#C5B8D6;"';
 				}else{
 					meta.attr = 'style=' + record.get('color');
 				}
 	            return value;
 	        },
 	        listeners:{
		    	dblclick:{
		    		fn:function(e){
		    			NS.clickGridMaestroPropuestas();
	      			}
	    		}
	    	}
 		},{
			header :'Compra. Transfer.',
			width :80,
			sortable :true,
			dataIndex :'habilitado',
			renderer: function (value, meta, record) {
				if(record.get('color') === 'color:#C5B8D6' ){
 					meta.attr = 'style="background-color:#C5B8D6;"';
 				}else{
 					meta.attr = 'style=' + record.get('color');
 				}
 	            return value;
			},
 	        listeners:{
		    	dblclick:{
		    		fn:function(e){
		    			NS.clickGridMaestroPropuestas();
	      			}
	    		}
	    	}
 		}
 		/*,{
			header :'Id Grupo Flujo',
			width :90,
			sortable :true,
			dataIndex :'idGrupoFlujo',
			hidden: true,
			renderer: function (value, meta, record) {
	            meta.attr = 'style=' + record.get('color');
	            return value;
	        }
		},{
			header :'Grupo Propuesta',
			width :130,
			sortable :true,
			dataIndex :'descGrupoCupo',
			hidden: true,
			renderer: function (value, meta, record) {
	            meta.attr = 'style=' + record.get('color');
	            return value;
	        }
		},{
			header :'Id Grupo',
			width :80,
			sortable :true,
			dataIndex :'idGrupo',
			hidden: true,
			renderer: function (value, meta, record) {
	            meta.attr = 'style=' + record.get('color');
	            return value;
	        }
		},{
			header :'Desc Grupo Flujo',
			width :100,
			sortable :true,
			dataIndex :'descGrupoFlujo',
			hidden: true,
			renderer: function (value, meta, record) {
	            meta.attr = 'style=' + record.get('color');
	            return value;
	        }
		},{
			header :'Estatus',
			width :220,
			sortable :true,
			dataIndex :'estatus',
			hidden: true,
			renderer: function (value, meta, record) {
	            meta.attr = 'style=' + record.get('color');
	            return value;
	        }
		},{
			header :'Nivel 1',
			width :50,
			sortable :true,
			dataIndex :'usuarioUno',
			hidden: true,
			renderer: function (value, meta, record) {
	            meta.attr = 'style=' + record.get('color');
	            return value;
	        }
		},{
			header :'Nivel 2',
			width :50,
			sortable :true,
			dataIndex :'usuarioDos',
			hidden: true,
			renderer: function (value, meta, record) {
	            meta.attr = 'style=' + record.get('color');
	            return value;
	        }
		},{
			header :'Nivel 3',
			width :50,
			sortable :true,
			dataIndex :'usuarioTres',
			hidden: true,
			renderer: function (value, meta, record) {
	            meta.attr = 'style=' + record.get('color');
	            return value;
	        }
		},{
			header :'Id division',
			width :100,
			sortable :true,
			dataIndex :'idDivision',
			hidden: true,
			renderer: function (value, meta, record) {
	            meta.attr = 'style=' + record.get('color');
	            return value;
	        }
		},{
			header :'Num Intercos',
			width :100,
			sortable :true,
			dataIndex :'numIntercos',
			hidden: true,
			renderer: function (value, meta, record) {
	            meta.attr = 'style=' + record.get('color');
	            return value;
	        }
		},{
			header :'Total Intercos',
			width :100,
			sortable :true,
			dataIndex :'totalIntercos',
			hidden: true,
			renderer: function (value, meta, record) {
	            meta.attr = 'style=' + record.get('color');
	            return value;
	        }
		}*/
 	  ]);

  	 
	//grid de datos
	 NS.gridMaestroPropuestas = new Ext.grid.GridPanel({
        store : NS.storeMaestro,
        viewConfig: {emptyText: 'Resumen de propuestas'},
        x:470,
        y:0,
        cm: NS.columnas,
		sm: NS.seleccion,
        width:510,
        height:205,
        frame:true,
        plugins: [NS.filterRow],
        /*listeners: {
        	dblclick: {
        		fn:function(e) {
        			
        		}
        	}
        }*/
    });
	 
	 //
	 NS.lblVisualizarDetalle = new Ext.form.Label
		({
			text: '* Para visualizar el detalle dar doble click sobre el renglón de la propuesta',
			width: 140,
			height: 50,
			x: 320,
			y: 5,
			style: {
				  color: 'red'
			}
		});
	 
	//Label para Total de importe
	NS.lblImporteMN = new Ext.form.Label
	({
		text: 'MN',
		x: 470,
		y: 215
	});
	
	//TextField para importes totales
	NS.txtImporteProp = new Ext.form.TextField
	({			
		id: PF + 'txtImporteProp',
		name: PF + 'txtImporteProp',
		x: 470,
		y: 230,
		width: 100,
		value: '0.00',
        disabled: true
	});
	
	//Label para importe DLS
	NS.lblImporteDLS = new Ext.form.Label
	({
		text: 'DLS',
		x: 575,
		y: 215
	});
	
	//TextField para importes totales
	NS.txtImporteDLSProp = new Ext.form.TextField
	({			
		id: PF + 'txtImporteDLSProp',
		name: PF + 'txtImporteDLSProp',
		x: 575,
		y: 230,
		width: 100,
		value: '0.00',
        disabled: true
	});
	
	//Label para importe EUR
	NS.lblImporteEUR = new Ext.form.Label
	({
		text: 'EUR',
		x: 680,
		y: 215
	});
	
	NS.txtImporteEURProp = new Ext.form.TextField
	({			
		id: PF + 'txtImporteEURProp',
		name: PF + 'txtImporteEURProp',
		x: 680,
		y: 230,
		width: 100,
		value: '0.00',
        disabled: true
	});
	
	//Label para importe otras divisas
	NS.lblImporteOTR = new Ext.form.Label
	({
		text: 'OTRAS DIV',
		x: 785,
		y: 215
	});
	
	NS.txtImporteOTRProp = new Ext.form.TextField
	({			
		id: PF + 'txtImporteOTRProp',
		name: PF + 'txtImporteOTRProp',
		x: 785,
		y: 230,
		width: 100,
		value: '0.00',
        disabled: true
	});
	
	 //Store para llenar el grid del detalle
  	 NS.storeDetalle = new Ext.data.DirectStore({
  	 	paramsAsHash: false,
  	 	baseParams: {
			idGrupoEmpresa:0,
			idGrupoRubro:0,
			cveControl:'',
			idUsuario1:0,
			idUsuario2:0,
			idUsuario3:0
		},
		root: '',
		paramOrder:['idGrupoEmpresa','idGrupoRubro','cveControl','idUsuario1','idUsuario2','idUsuario3'],
		directFn: ConsultaPropuestasAction.consultarDetalle,
		//autoLoad: {params:{start: 0, limit: 10}},
		fields: [
			{name: 'nomEmpresa'},//Grupo
			{name: 'noEmpresa'},//Nom. Empresa
			{name: 'noDocto'},//No. Docto
			{name: 'nomProveedor'},//Proveedor
			{name: 'importe'},//importe
			{name: 'fecValorOriginalStr', type: 'date', dateFormat:'d/m/Y'},//fecha de vencimiento
			{name: 'idDivisa'},//Divisa
			{name: 'importeMn'},//ImporteMN
			{name: 'idFormaPago'},//ID Forma Pago
			{name: 'descFormaPago'},//Forma Pago
			{name: 'fecPropuestaStr', type: 'date', dateFormat:'d/m/Y'},//Fec Prop
			{name: 'bancoPago'},//Banco de Pago
			{name: 'idChequera'},//Chequera de Pago
			{name: 'descGrupoCupo'},//Grupo de Rubros
			{name: 'idRubro'},//Rubro
			{name: 'importeOriginal'},//Importe Original
			{name: 'idDivisaOriginal'},//DivisaOriginal
			{name: 'beneficiario'},//Beneficiario
			{name: 'noFolioDet'},//No. Folio Det
			{name: 'concepto'},//Concepto
			{name: 'idBancoBenef'},//Id Banco Benef
			{name: 'idChequeraBenef'},//Id Chequera Benef
			{name: 'noFactura'},//No. Factura
			{name: 'diasInv'},//Dias Vto
			{name: 'origenMov'},//Origen
			{name: 'observacion'},
			{name: 'noCliente'},//No. Persona
			{name: 'noCheque'},//No. Cheque
			{name: 'idBanco'},//No. Banco pagador
			{name: 'usr1'},
			{name: 'usr2'},
			{name: 'color'},
			{name: 'fecContabilizacionStr', type: 'date', dateFormat:'d/m/Y'}, //Fecha Contabilizacion
			{name: 'fecOperacionStr', type: 'date', dateFormat:'d/m/Y'}, //Fecha Documento
			{name: 'fecValorStr', type: 'date', dateFormat:'d/m/Y'}, //Fecha Vencimiento
			{name: 'equivale'},
			{name: 'idContable'},	//clase docto.
			{name: 'invoiceType'},
			{name: 'idClabe'},
			{name: 'rfc'},
			{name: 'referenciaCte'},
		],
		listeners: {
			load: function(s, records){
				//var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeDetalle, msg:"Cargando..."});
				
				if(records.length==null || records.length<=0){
					Ext.Msg.alert('SET','No existe detalle');
					NS.registros = NS.gridDetalle.getSelectionModel().getSelections();
				}else {
					
          			var totalImporte=0;
          			var totalImporteDLS=0;
          			var sumaDetProp=0;
          			
          			var bandera = false;
          			var regSelMaestros = NS.gridMaestroPropuestas.getSelectionModel().getSelections();
          			
          			if(regSelMaestros.length > 0) {
          				if(regSelMaestros[0].get('user1') != '')
          					bandera = true;
          			}
          			if(bandera) {
          				for(var k=0; k<records.length; k++) {
          					
          					if(records[k].get('idDivisa') == 'DLS')
              					totalImporteDLS += records[k].get('importe');
              				else
              					totalImporte += records[k].get('importe');
              				
              				NS.sm.selectRow(k, true, false);
              				
              			}
          				
          				sumaDetProp = 0;
          				
          				var regTodos =NS.gridDetalle.store.data.items;
              			for(var k=0; k<regTodos.length; k++) {
              				sumaDetProp += regTodos[k].get('importe');
              			}
              			
          			}else {
          				//NS.sm.selectRange(0,records.length-1); //lo  comento para que no seleccione nada
	          			
          				sumaDetProp = 0;
          				
	          			//var regSelec=NS.gridDetalle.getSelectionModel().getSelections();
	          			var regSelec=NS.gridDetalle.store.data.items; //todos los registros en lugar de seleccionarlos
	          			for(var kk=0; kk<regSelec.length; kk++) {
	          				if(regSelec[kk].get('idDivisa') == 'DLS')
	          					totalImporteDLS += regSelec[kk].get('importe');
	          				else
	          					totalImporte += regSelec[kk].get('importe');
	          			}
	          			
	          			for(var k=0; k < regSelec.length; k++) {
	          				sumaDetProp += regSelec[k].get('importe');
	          			}
	          			
          			}
          			Ext.getCmp(PF+'totalMN').setValue(NS.formatNumber(Math.round((totalImporte)*100)/100 ));
          			Ext.getCmp(PF+'sumMN').setValue(NS.formatNumber(Math.round((sumaDetProp)*100)/100));
          			Ext.getCmp(PF+'totalDLS').setValue(NS.formatNumber(Math.round((totalImporteDLS)*100)/100));

	        		NS.registros = NS.gridDetalle.getSelectionModel().getSelections();
	        		
	        		//Deseleccionamos todo
	        		NS.sm.selectRange(-1,-1);
				}
				
				mascara.hide();
			},
			exception: function(mist){
				Ext.Msg.alert('SET','Error en conexión al servidor');
				mascara.hide();
			}
		}
	}); 
	
  	 

	NS.columnasDetalle = new Ext.grid.ColumnModel
	([	
		NS.sm,
		{
			header :'No.Empresa',
			width : 70,
			sortable :true,
			dataIndex :'noEmpresa',
			hidden: false,
			renderer: function (value, meta, record) {
		        //meta.attr = 'style=' + record.get('color') +'';
				meta.attr = 'style=' + record.get('color') +';padding : 0px !important; margin : 0px !important;';
		        return value;
		    },
		    align: 'center'
		},{
			header :'Nombre Empresa',
			width : 180,
			sortable :true,
			dataIndex :'nomEmpresa', 
			renderer: function (value, meta, record) {
		        //meta.attr = 'style=' + record.get('color');
				meta.attr = 'style=' + record.get('color') +';padding : 0px !important; margin : 0px !important;';
		        return value;
		    },
		    align: 'center'
		},
		{
			header :'No. Benef',
			width :60,
			sortable :true,
			dataIndex :'equivale', 
			hidden: false,
			renderer: function (value, meta, record) {
		        //meta.attr = 'style=' + record.get('color');
				meta.attr = 'style=' + record.get('color') +';padding : 0px !important; margin : 0px !important;';
		        return value;
		    },
		    align: 'center'
		},{
			header :'Beneficiario',
			width : 180,
			sortable :true,
			dataIndex :'beneficiario', 
			renderer: function (value, meta, record) {
		        //meta.attr = 'style=' + record.get('color');
				meta.attr = 'style=' + record.get('color') +';padding : 0px !important; margin : 0px !important;';
		        return value;
		    },
		    align: 'center'
		},{
			header :'No. Factura',
			width :80,
			sortable :true,
			dataIndex :'noFactura', 
			renderer: function (value, meta, record) {
		        //meta.attr = 'style=' + record.get('color');
				meta.attr = 'style=' + record.get('color') +';padding : 0px !important; margin : 0px !important;';
		        return value;
		    },
		    align: 'center',
		    filter: {testNoFact: "/^{0}/i"}
		},/*{
			header :'Empresa',
			width :180,
			sortable :true,
			dataIndex :'nomEmpresa',
			hidden: false,
			renderer: function (value, meta, record) {
		        meta.attr = 'style=' + record.get('color');
		        return value;
		    }
		},*/{
			
			header :'No.Docto',
			width :60,
			sortable :true,
			dataIndex :'noDocto', 
			//hidden: true,
			renderer: function (value, meta, record) {
		        //meta.attr = 'style=' + record.get('color');
				meta.attr = 'style=' + record.get('color') +';padding : 0px !important; margin : 0px !important;';
		        return value;
		    },
		    filter: {testNoDoc: "/^{0}/i"}
		},{
			
			header :'Posición',
			width : 50,
			sortable :true, 
			dataIndex :'invoiceType', 
			//hidden: true,
			renderer: function (value, meta, record) {
		        //meta.attr = 'style=' + record.get('color');
				meta.attr = 'style=' + record.get('color') +';padding : 0px !important; margin : 0px !important;';
		        return value;
		    },
		    align: 'center'
		},{
			header :'Clase Docto',
			width : 70,
			sortable :true,
			dataIndex :'idContable', 
			renderer: function (value, meta, record) {
		        //meta.attr = 'style=' + record.get('color');
				meta.attr = 'style=' + record.get('color') +';padding : 0px !important; margin : 0px !important;';
		        return value;
		    },
		    align: 'center'
		},{
			header :'Importe',
			width :90,
			sortable :true,
			dataIndex :'importe', 
			css: 'text-align:right;',
			renderer: function (value, meta, record) {
		        //meta.attr = 'style=' + record.get('color');
				meta.attr = 'style=' + record.get('color') +';padding : 0px !important; margin : 0px !important;';
		        return '$' + NS.formatNumber(Math.round((value)*100)/100 );
		    },
		    align: 'center'
		},{
			header :'Divisa',
			width :120,
			sortable :true,
			dataIndex :'idDivisa', 
			renderer: function (value, meta, record) {
		        //meta.attr = 'style=' + record.get('color');
				meta.attr = 'style=' + record.get('color') +';padding : 0px !important; margin : 0px !important;';
		        return value;
		    },
		    align: 'center'
		},/*{
			header :'Importe MN',
			width :95,
			sortable :true,
			dataIndex :'importeMn',
			css: 'text-align:right;',
			renderer: function (value, meta, record) {
		        meta.attr = 'style=' + record.get('color');
		        return '$' + NS.formatNumber(Math.round((value)*100)/100);
		    }
		},*/{
			header :'Concepto',
			width :220,
			sortable :true,
			dataIndex :'concepto', 
			renderer: function (value, meta, record) {
		        //meta.attr = 'style=' + record.get('color');
				meta.attr = 'style=' + record.get('color') +';padding : 0px !important; margin : 0px !important;';
		        return value;
		    },
		    listeners:{
		    	click:{
		    		fn:function(e){
		      			var regSelec = NS.gridDetalle.getSelectionModel().getSelections();
		      			
		      			if(regSelec.length > 0) {
		      				Ext.Msg.confirm('SET', '¿Desea ver el concepto completo?', function(btn) {
		          				if(btn == 'yes') {
		          					Ext.Msg.alert('SET','Concepto: ' + regSelec[0].get('concepto'), 'INFO');
		          				}
		          			});
		      			}
		    		}
		    	}
		    },
		    align: 'center',
			filter: {testText: "/^{0}/i"}
		},
		{
			header :'Forma Pago',
			width :120,
			sortable :true,
			dataIndex :'descFormaPago', 
			renderer: function (value, meta, record) {
		        //meta.attr = 'style=' + record.get('color');
				meta.attr = 'style=' + record.get('color') +';padding : 0px !important; margin : 0px !important;';
		        return value;
		    },
		    align: 'center'
		},
		{
			header :'Fecha Prop.',
			width : 80,
			sortable :true,
			dataIndex :'fecPropuestaStr', 
			renderer: function (value, meta, record) {
		        //meta.attr = 'style=' + record.get('color');
				meta.attr = 'style=' + record.get('color') +';padding : 0px !important; margin : 0px !important;';
		        return value;
		    },
		    xtype: 'datecolumn',
			format: 'd/m/Y',
			editor: {
				xtype : 'datefield',
			    //format: 'Y-m-d H:i:s',
			    format: 'd/m/Y',
			    submitFormat: 'c'
			},
			align: 'center'
		},{
			header :'Fecha Venc.',
			width : 80,
			sortable :true,
			dataIndex :'fecValorStr', 
			renderer: function (value, meta, record) {
		        //meta.attr = 'style=' + record.get('color');
				meta.attr = 'style=' + record.get('color') +';padding : 0px !important; margin : 0px !important;';
		        return value;
		    },
		    xtype: 'datecolumn',
				format: 'd/m/Y',
				editor: {
		        xtype : 'datefield',
		        //format: 'Y-m-d H:i:s',
		        format: 'd/m/Y',
		        submitFormat: 'c'
		    },
		    align: 'center'
		},{
			header :'Fecha Contabilización',
			width : 80,
			sortable :true,
			dataIndex :'fecContabilizacionStr', 
			renderer: function (value, meta, record) {
		        //meta.attr = 'style=' + record.get('color');
				meta.attr = 'style=' + record.get('color') +';padding : 0px !important; margin : 0px !important;';
		        return value;
		    },
		    xtype: 'datecolumn',
				format: 'd/m/Y',
				editor: {
		        xtype : 'datefield',
		        //format: 'Y-m-d H:i:s',
		        format: 'd/m/Y',
		        submitFormat: 'c'
		    },
		    align: 'center'
		},{
			header :'Fecha Documento',
			width : 80,
			sortable :true,
			dataIndex :'fecOperacionStr', 
			renderer: function (value, meta, record) {
		        //meta.attr = 'style=' + record.get('color');
				meta.attr = 'style=' + record.get('color') +';padding : 0px !important; margin : 0px !important;';
		        return value;
		    },
		    xtype: 'datecolumn',
				format: 'd/m/Y',
				editor: {
		        xtype : 'datefield',
		        format: 'd/m/Y',
		        submitFormat: 'c'
		    },
		    align: 'center'
		},{
			header :'Banco Pago',
			width :90,
			sortable :true,
			dataIndex :'bancoPago', 
			renderer: function (value, meta, record) {
		        //meta.attr = 'style=' + record.get('color');
				meta.attr = 'style=' + record.get('color') +';padding : 0px !important; margin : 0px !important;';
		        return value;
		    },
		    align: 'center'
		},{
			header :'Chequera de Pago',
			width :100,
			sortable :true,
			dataIndex :'idChequera', 
			renderer: function (value, meta, record) {
		        //meta.attr = 'style=' + record.get('color');
				meta.attr = 'style=' + record.get('color') +';padding : 0px !important; margin : 0px !important;';
		        return value;
		    },
		    align: 'center'
		},/*{
			header :'Proveedor',
			width :80,
			sortable :true,
			dataIndex :'nomProveedor',
			hidden: true
		},{
			header :'Divisa',
			width :50,
			sortable :true,
			dataIndex :'idDivisa',
			renderer: function (value, meta, record) {
		        meta.attr = 'style=' + record.get('color');
		        return value;
		    }
		},{
			header :'Grupo Rubros',
			width :90,
			sortable :true,
			dataIndex :'descGrupoCupo',
			hidden: true,
			renderer: function (value, meta, record) {
		        meta.attr = 'style=' + record.get('color');
		        return value;
		    }
		},{
			header :'Rubro',
			width :60,
			sortable :true,
			dataIndex :'idRubro',
			hidden: true,
			renderer: function (value, meta, record) {
		        meta.attr = 'style=' + record.get('color');
		        return value;
		    }
		},{
			header :'Importe Original',
			width :90,
			sortable :true,
			dataIndex :'importeOriginal',
			css: 'text-align:right;',
			renderer: function (value, meta, record) {
		        meta.attr = 'style=' + record.get('color');
		        return '$' + NS.formatNumber(Math.round((value)*100)/100 );
		    }
		},{
			header :'Divisa Original',
			width :80,
			sortable :true,
			dataIndex :'idDivisaOriginal',
			renderer: function (value, meta, record) {
		        meta.attr = 'style=' + record.get('color');
		        return value;
		    }
		},{
			header :'No.Folio Det',
			width :80,
			sortable :true,
			dataIndex :'noFolioDet',
			hidden: true,
			renderer: function (value, meta, record) {
		        meta.attr = 'style=' + record.get('color');
		        return value;
		    }
		},*/{
			header :'Id Banco Benef',
			width :90,
			sortable :true,
			dataIndex :'idBancoBenef', 
			renderer: function (value, meta, record) {
		        //meta.attr = 'style=' + record.get('color');
				meta.attr = 'style=' + record.get('color') +';padding : 0px !important; margin : 0px !important;';
		        return value;
		    },
		    align: 'center'
		},{
			header :'Id Chequera Benef',
			width :120,
			sortable :true,
			dataIndex :'idChequeraBenef', 
			renderer: function (value, meta, record) {
		        //meta.attr = 'style=' + record.get('color');
				meta.attr = 'style=' + record.get('color') +';padding : 0px !important; margin : 0px !important;';
		        return value;
		    },
		    align: 'center'
		},{
			header :'CLABE',
			width :120,
			sortable :true,
			dataIndex :'idClabe', 
			renderer: function (value, meta, record) {
		        //meta.attr = 'style=' + record.get('color');
				meta.attr = 'style=' + record.get('color') +';padding : 0px !important; margin : 0px !important;';
		        return value;
		    },
		    align: 'center'
		},{
			header :'RFC',
			width :120,
			sortable :true,
			dataIndex :'rfc', 
			renderer: function (value, meta, record) {
		        //meta.attr = 'style=' + record.get('color');
				meta.attr = 'style=' + record.get('color') +';padding : 0px !important; margin : 0px !important;';
		        return value;
		    },
		    align: 'center'
		},{
			header :'Referencia CTE',
			width :120,
			sortable :true,
			dataIndex :'referenciaCte', 
			renderer: function (value, meta, record) {
		        //meta.attr = 'style=' + record.get('color');
				meta.attr = 'style=' + record.get('color') +';padding : 0px !important; margin : 0px !important;';
		        return value;
		    },
		    align: 'center'
		},
		{
			header :'Observacion',
			width :120,
			sortable :true,
			dataIndex :'observacion', 
			hidden: true,
			renderer: function (value, meta, record) {
		        //meta.attr = 'style=' + record.get('color');
				meta.attr = 'style=' + record.get('color') +';padding : 0px !important; margin : 0px !important;';
		        return value;
		    },
		    align: 'center'
		},
		{
			header :'Id Form.Pago',
			width :120,
			sortable :true,
			hidden:true,
			dataIndex :'idFormaPago', 
			renderer: function (value, meta, record) {
		        //meta.attr = 'style=' + record.get('color');
				meta.attr = 'style=' + record.get('color') +';padding : 0px !important; margin : 0px !important;';
		        return value;
		    },
		    align: 'center'
		},
		/*,{
			header :'Dias Vto',
			width :60,
			sortable :true,
			dataIndex :'diasInv',
			renderer: function (value, meta, record) {
		        meta.attr = 'style=' + record.get('color');
		        return value;
		    }
		},{
			header :'Origen',
			width :60,
			sortable :true,
			dataIndex :'origenMov',
			renderer: function (value, meta, record) {
		        meta.attr = 'style=' + record.get('color');
		        return value;
		    }
		},{
			header :'No. Persona',
			width :80,
			sortable :true,
			dataIndex :'noCliente',
			hidden: false,
			renderer: function (value, meta, record) {
		        meta.attr = 'style=' + record.get('color');
		        return value;
		    }
		},{
			header :'No. Cheque',
			width :80,
			sortable :true,
			dataIndex :'noCheque',
			hidden: true,
			renderer: function (value, meta, record) {
		        meta.attr = 'style=' + record.get('color');
		        return value;
		    }
		},*/
	 ]);
		
	
	/*NS.gridTopbar = new Ext.Toolbar({  
	    id: 'categoriesGridTopbarId',  
	    items: [
	        {  
	            xtype:'button',  
	            text:'Agregar Nuevo',
	            iconCls:'forma_alta',
	            id: 'new_btn',  
	            handler: NS.addRecord,  
	            type:'button'  
	        },' | ',
	        {  
	            xtype:'button',  
	            text:'Eliminar',
	            iconCls:'forma_baja',
	            id: 'delete_btn',  
	            handler:NS.deleteRecord,  
	            type:'button'  
	        },' | ',
	        {  
	            xtype:'button',  
	            text:'Guardar',  
	            id: 'save_btn',  
	            handler: NS.save,  
	            type:'button'  
	        },' | ',
	        {  
	            xtype:'button',  
	            text:'Excel',  
	            id: 'excel_btn',  
	            handler: NS.reporteExcel,  
	            type:'button'  
	        }
	    ]  
	});*/
	
	NS.columnasDetalleNom = new Ext.grid.ColumnModel
	([	
		NS.sm,
		{
			header :'No.Empresa',
			width : 70,
			sortable :true,
			dataIndex :'noEmpresa',
			hidden: false,
			renderer: function (value, meta, record) {
		        //meta.attr = 'style=' + record.get('color') +'';
				meta.attr = 'style=' + record.get('color') +';padding : 0px !important; margin : 0px !important;';
		        return value;
		    },
		    align: 'center'
		},{
			header :'No. Factura',
			width :80,
			sortable :true,
			dataIndex :'noFactura', 
			renderer: function (value, meta, record) {
		        //meta.attr = 'style=' + record.get('color');
				meta.attr = 'style=' + record.get('color') +';padding : 0px !important; margin : 0px !important;';
		        return value;
		    },
		    align: 'center'
		},/*{
			header :'Empresa',
			width :180,
			sortable :true,
			dataIndex :'nomEmpresa',
			hidden: false,
			renderer: function (value, meta, record) {
		        meta.attr = 'style=' + record.get('color');
		        return value;
		    }
		},*/{
			
			header :'No.Docto',
			width :60,
			sortable :true,
			dataIndex :'noDocto', 
			//hidden: true,
			renderer: function (value, meta, record) {
		        //meta.attr = 'style=' + record.get('color');
				meta.attr = 'style=' + record.get('color') +';padding : 0px !important; margin : 0px !important;';
		        return value;
		    }
		},{
			
			header :'Posición',
			width : 50,
			sortable :true, 
			dataIndex :'invoiceType', 
			//hidden: true,
			renderer: function (value, meta, record) {
		        //meta.attr = 'style=' + record.get('color');
				meta.attr = 'style=' + record.get('color') +';padding : 0px !important; margin : 0px !important;';
		        return value;
		    },
		    align: 'center'
		},{
			header :'Clase Docto',
			width : 70,
			sortable :true,
			dataIndex :'idContable', 
			renderer: function (value, meta, record) {
		        //meta.attr = 'style=' + record.get('color');
				meta.attr = 'style=' + record.get('color') +';padding : 0px !important; margin : 0px !important;';
		        return value;
		    },
		    align: 'center'
		},{
			header :'Importe',
			width :90,
			sortable :true,
			dataIndex :'importe', 
			css: 'text-align:right;',
			renderer: function (value, meta, record) {
		        //meta.attr = 'style=' + record.get('color');
				meta.attr = 'style=' + record.get('color') +';padding : 0px !important; margin : 0px !important;';
		        return '$' + NS.formatNumber(Math.round((value)*100)/100 );
		    },
		    align: 'center'
		},/*{
			header :'Importe MN',
			width :95,
			sortable :true,
			dataIndex :'importeMn',
			css: 'text-align:right;',
			renderer: function (value, meta, record) {
		        meta.attr = 'style=' + record.get('color');
		        return '$' + NS.formatNumber(Math.round((value)*100)/100);
		    }
		},*/{
			header :'Forma Pago',
			width :120,
			sortable :true,
			dataIndex :'descFormaPago', 
			renderer: function (value, meta, record) {
		        //meta.attr = 'style=' + record.get('color');
				meta.attr = 'style=' + record.get('color') +';padding : 0px !important; margin : 0px !important;';
		        return value;
		    },
		    align: 'center'
		},{
			header :'Concepto',
			width :220,
			sortable :true,
			dataIndex :'concepto', 
			renderer: function (value, meta, record) {
		        //meta.attr = 'style=' + record.get('color');
				meta.attr = 'style=' + record.get('color') +';padding : 0px !important; margin : 0px !important;';
		        return value;
		    },
		    listeners:{
		    	click:{
		    		fn:function(e){
		      			var regSelec = NS.gridDetalle.getSelectionModel().getSelections();
		      			
		      			if(regSelec.length > 0) {
		      				Ext.Msg.confirm('SET', '¿Desea ver el concepto completo?', function(btn) {
		          				if(btn == 'yes') {
		          					Ext.Msg.alert('SET','Concepto: ' + regSelec[0].get('concepto'), 'INFO');
		          				}
		          			});
		      			}
		    		}
		    	}
		    },
		    align: 'center'
		},{
			header :'Fecha Prop.',
			width : 80,
			sortable :true,
			dataIndex :'fecPropuestaStr', 
			renderer: function (value, meta, record) {
		        //meta.attr = 'style=' + record.get('color');
				meta.attr = 'style=' + record.get('color') +';padding : 0px !important; margin : 0px !important;';
		        return value;
		    },
		    xtype: 'datecolumn',
			format: 'd/m/Y',
			editor: {
				xtype : 'datefield',
			    //format: 'Y-m-d H:i:s',
			    format: 'd/m/Y',
			    submitFormat: 'c'
			},
			align: 'center'
		},{
			header :'Fecha Venc.',
			width : 80,
			sortable :true,
			dataIndex :'fecValorStr', 
			renderer: function (value, meta, record) {
		        //meta.attr = 'style=' + record.get('color');
				meta.attr = 'style=' + record.get('color') +';padding : 0px !important; margin : 0px !important;';
		        return value;
		    },
		    xtype: 'datecolumn',
				format: 'd/m/Y',
				editor: {
		        xtype : 'datefield',
		        //format: 'Y-m-d H:i:s',
		        format: 'd/m/Y',
		        submitFormat: 'c'
		    },
		    align: 'center'
		},{
			header :'Fecha Contabilización',
			width : 80,
			sortable :true,
			dataIndex :'fecContabilizacionStr', 
			renderer: function (value, meta, record) {
		        //meta.attr = 'style=' + record.get('color');
				meta.attr = 'style=' + record.get('color') +';padding : 0px !important; margin : 0px !important;';
		        return value;
		    },
		    xtype: 'datecolumn',
				format: 'd/m/Y',
				editor: {
		        xtype : 'datefield',
		        //format: 'Y-m-d H:i:s',
		        format: 'd/m/Y',
		        submitFormat: 'c'
		    },
		    align: 'center'
		},{
			header :'Fecha Documento',
			width : 80,
			sortable :true,
			dataIndex :'fecOperacionStr', 
			renderer: function (value, meta, record) {
		        //meta.attr = 'style=' + record.get('color');
				meta.attr = 'style=' + record.get('color') +';padding : 0px !important; margin : 0px !important;';
		        return value;
		    },
		    xtype: 'datecolumn',
				format: 'd/m/Y',
				editor: {
		        xtype : 'datefield',
		        format: 'd/m/Y',
		        submitFormat: 'c'
		    },
		    align: 'center'
		},{
			header :'Banco Pago',
			width :90,
			sortable :true,
			dataIndex :'bancoPago', 
			renderer: function (value, meta, record) {
		        //meta.attr = 'style=' + record.get('color');
				meta.attr = 'style=' + record.get('color') +';padding : 0px !important; margin : 0px !important;';
		        return value;
		    },
		    align: 'center'
		},{
			header :'Chequera de Pago',
			width :100,
			sortable :true,
			dataIndex :'idChequera', 
			renderer: function (value, meta, record) {
		        //meta.attr = 'style=' + record.get('color');
				meta.attr = 'style=' + record.get('color') +';padding : 0px !important; margin : 0px !important;';
		        return value;
		    },
		    align: 'center'
		},{
			header :'Id Banco Benef',
			width :90,
			sortable :true,
			dataIndex :'idBancoBenef', 
			renderer: function (value, meta, record) {
		        //meta.attr = 'style=' + record.get('color');
				meta.attr = 'style=' + record.get('color') +';padding : 0px !important; margin : 0px !important;';
		        return value;
		    },
		    align: 'center'
		},{
			header :'Id Chequera Benef',
			width :120,
			sortable :true,
			dataIndex :'idChequeraBenef', 
			renderer: function (value, meta, record) {
		        //meta.attr = 'style=' + record.get('color');
				meta.attr = 'style=' + record.get('color') +';padding : 0px !important; margin : 0px !important;';
		        return value;
		    },
		    align: 'center'
		},{
			header :'CLABE',
			width :120,
			sortable :true,
			dataIndex :'idClabe', 
			renderer: function (value, meta, record) {
		        //meta.attr = 'style=' + record.get('color');
				meta.attr = 'style=' + record.get('color') +';padding : 0px !important; margin : 0px !important;';
		        return value;
		    },
		    align: 'center'
		},{
			header :'RFC',
			width :120,
			sortable :true,
			dataIndex :'rfc', 
			renderer: function (value, meta, record) {
		        //meta.attr = 'style=' + record.get('color');
				meta.attr = 'style=' + record.get('color') +';padding : 0px !important; margin : 0px !important;';
		        return value;
		    },
		    align: 'center'
		},{
			header :'Referencia CTE',
			width :120,
			sortable :true,
			dataIndex :'referenciaCte', 
			renderer: function (value, meta, record) {
		        //meta.attr = 'style=' + record.get('color');
				meta.attr = 'style=' + record.get('color') +';padding : 0px !important; margin : 0px !important;';
		        return value;
		    },
		    align: 'center'
		},{
			header :'HORA RECIBO',
			width :120,
			sortable :true,
			dataIndex :'horaRecibo', 
			renderer: function (value, meta, record) {
				meta.attr = 'style=' + record.get('color') +';';
		        return value;
		    }
		},{
			header :'REFERENCIA',
			width :120,
			sortable :true,
			dataIndex :'referencia', 
			renderer: function (value, meta, record) {
				meta.attr = 'style=' + record.get('color') +';';
		        return value;
		    }
		},{
			header :'LEYENDA BLOQUEO',
			width :120,
			sortable :true,
			dataIndex :'leyendaBloqueo', 
			renderer: function (value, meta, record) {
				meta.attr = 'style=' + record.get('color') +';';
		        return value;
		    }
		}
		
	 ]);
	
	NS.columnasDetalleNomDet = new Ext.grid.ColumnModel
	([	
		NS.sm,
		{
			header :'No.Empresa',
			width : 70,
			sortable :true,
			dataIndex :'noEmpresa',
			hidden: false,
			renderer: function (value, meta, record) {
				meta.attr = 'style=' + record.get('color') +';padding : 0px !important; margin : 0px !important;';
		        return value;
		    },
		    align: 'center'
		},{
			header :'No. Benef',
			width :60,
			sortable :true,
			dataIndex :'equivale', 
			hidden: false,
			renderer: function (value, meta, record) {
		        //meta.attr = 'style=' + record.get('color');
				meta.attr = 'style=' + record.get('color') +';padding : 0px !important; margin : 0px !important;';
		        return value;
		    },
		    align: 'center'
		},{
			header :'Beneficiario',
			width : 180,
			sortable :true,
			dataIndex :'beneficiario', 
			renderer: function (value, meta, record) {
		        //meta.attr = 'style=' + record.get('color');
				meta.attr = 'style=' + record.get('color') +';padding : 0px !important; margin : 0px !important;';
		        return value;
		    },
		    align: 'center'
		},{
			header :'No. Factura',
			width :80,
			sortable :true,
			dataIndex :'noFactura', 
			renderer: function (value, meta, record) {
		        //meta.attr = 'style=' + record.get('color');
				meta.attr = 'style=' + record.get('color') +';padding : 0px !important; margin : 0px !important;';
		        return value;
		    },
		    align: 'center'
		}
		
	 ]);

	NS.gridPagingBar = new Ext.PagingToolbar({  
	    pageSize: 500,
	    store : NS.storeDetalle,
	    autoHeight:true,
	    autoWidth:true,
	    displayInfo: true,
	    prependButtons: true,
	    emptyMsg : 'No hay datos para este catálogo'
	    	
	});

	
	
	NS.gridDetalle = new Ext.grid.GridPanel({
        store : NS.storeDetalle,
        /*cm: new Ext.grid.ColumnModel({
            defaults: {
                width: 120,
                value:true,
                sortable: true
            },
            columns: [
                
            ]
        }),*/
        columnLines: true,
        x:10,
        y:0,
        width:940,
        height:205,
        viewConfig: {emptyText: 'Resumen de detalles'},
        frame:true,
        sm: NS.sm,
        cm: NS.columnasDetalle,
        bbar:NS.gridPagingBar,
        stripeRows: true,
        plugins: [NS.filterRowDos],
        style: {
            width: '100%',
            marginBottom: '0px'
        }

    });
	
	
	//store  forma pago
    NS.storeFormaPago = new Ext.data.DirectStore({
		paramsAsHash: false,
		baseParams:{
    		campoUno:'id_forma_pago',
			campoDos:'desc_forma_pago',
			tabla:'cat_forma_pago',
			condicion:'id_forma_pago in (1, 3, 5, 9)',
			orden:''
		},
		root: '',
		paramOrder:['campoUno','campoDos','tabla','condicion','orden'],
		directFn: ConsultaPropuestasAction.llenarCombo, 
		//idProperty: 'id', 
		fields: [
				 {name: 'id'},
				 {name: 'descripcion'}
			 ],
		listeners: {
			load: function(s, records){
				var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeFormaPago, msg:"Cargando..."});
				
				if(records.length == null || records.length <= 0)
					Ext.Msg.alert('SET','No existen las formas de pago');
			},
			exception: function(mist){
				Ext.Msg.alert('SET','Error en conexión al servidor');
				mascara.hide();
			}
		}
    });
    
    //combo forma pago
	NS.cmbFormaPago = new Ext.form.ComboBox({
		store: NS.storeFormaPago
		,name: PF+'cmbFormaPago'
		,id: PF+'cmbFormaPago'
		,typeAhead: true
		,mode: 'local'
		,minChars: 0
		,selecOnFocus: true
		,forceSelection: true
      	,x: 10
        ,y: 15
        ,width: 140
		,valueField:'id'
		,displayField:'descripcion'
		,autocomplete: true
		,emptyText: 'Forma de Pago'
		,triggerAction: 'all'
		,value: ''
		,listeners:{
			select:{
				fn:function(combo, valor) {
					if(combo.getValue() != '') {
						
						Ext.getCmp(PF + 'dateNvaFecPago').setDisabled(true);
						
					}else{
						
						//validar que todos los campos sean vacios para  bloquear fecha
						if(NS.cmbFormaPago.getValue() == '' && NS.cmbChequeraPag.getValue() == '' 
							&& NS.cmbChequeraPagBenef.getValue() == ''
							&& NS.cmbBancoPag.getValue() == '' && NS.cmbBancoPagBenef.getValue() == ''){
							
							Ext.getCmp(PF + 'dateNvaFecPago').setDisabled(false);
							
							Ext.getCmp(PF + 'dateNvaFecPago').setValue('');
							NS.cmbFormaPago.reset();
							NS.cmbChequeraPag.reset();
							NS.storeChequeraPag.removeAll();
							NS.storeChequeraPagBenef.removeAll();
							NS.cmbChequeraPagBenef.reset();
							NS.cmbBancoPag.reset();
							NS.cmbBancoPagBenef.reset();
						}
						
						
					}
				}
			},
			blur:{
				fn:function(combo, valor) {
					
					if(combo.getValue() != '') {
						
						Ext.getCmp(PF + 'dateNvaFecPago').setDisabled(true);
						
					}else{
						
						//validar que todos los campos sean vacios para  bloquear fecha
						if(NS.cmbFormaPago.getValue() == '' && NS.cmbChequeraPag.getValue() == '' 
							&& NS.cmbChequeraPagBenef.getValue() == ''
							&& NS.cmbBancoPag.getValue() == '' && NS.cmbBancoPagBenef.getValue() == ''){
							
							Ext.getCmp(PF + 'dateNvaFecPago').setDisabled(false);
							
						}
						
					}
				}
			},
			
		}
	});
	
	//store Divisa pago
    NS.storeDivisaPag = new Ext.data.DirectStore({
		paramsAsHash: false,
		root: '',
		directFn: ConsultaPropuestasAction.llenarComboDivisa, 
		idProperty: 'idDivisa', 
		fields: [
				 {name: 'idDivisa'},
				 {name: 'descDivisa'}
			 ],
		listeners: {
			load: function(s, records){
				var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeDivisaPag, msg:"Cargando..."});
				
				if(records.length == null || records.length <= 0)
					Ext.Msg.alert('SET','No existen divisas para el pago');
			},
			exception: function(mist){
				Ext.Msg.alert('SET','Error en conexión al servidor');
				mascara.hide();
			}
		}
    });
    //NS.storeDivisaPag.load();
    
    //combo Divisa pago (Modificación detalles)
	NS.cmbDivisaPag = new Ext.form.ComboBox({
		store: NS.storeDivisaPag
		,name: PF + 'cmbDivisaPag'
		,id: PF + 'cmbDivisaPag'
		,typeAhead: true
		,mode: 'local'
		,minChars: 0
		,selecOnFocus: true
		,forceSelection: true
      	,x: 335
        ,y: 15
        ,width: 150
		,valueField:'idDivisa'
		,displayField:'descDivisa'
		,autocomplete: true
		,emptyText: 'Divisa de Pago.'
		,triggerAction: 'all'
		,value: '',
		hidden: false //true
		,listeners:{
			select:{
				fn:function(combo, valor) {
					BFwrk.Util.updateComboToTextField(PF + 'txtIdDivisa', NS.cmbDivisaPag.getId());
					
					if(combo.getValue() != '') NS.obtenerTipoCambio(combo.getValue());
				}
			}
		}
	});
	
	
	//store Banco Pago
    NS.storeBancoPag = new Ext.data.DirectStore({
		paramsAsHash: false,
		baseParams: { idDivisa: '', noEmpresa : ''},
		root: '',
		paramOrder:['idDivisa', 'noEmpresa'],
		directFn: ConsultaPropuestasAction.llenarComboBancoPag,
		idProperty: 'id', 
		fields: [
				 {name: 'id'},
				 {name: 'descripcion'}
			 ],
		listeners: {
			load: function(s, records){
				var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeBancoPag, msg:"Cargando..."});
				
				if(records.length == null || records.length <= 0)
					Ext.Msg.alert('SET','No existen bancos para el pago');
			},
			exception: function(mist){
				Ext.Msg.alert('SET','Error en conexión al servidor');
				mascara.hide();
			}
		}
    });
  //store divisa pago
    NS.storeDivisaPago = new Ext.data.DirectStore({
		paramsAsHash: false,
		//baseParams: { idDivisa: 'id_divisa', descDivisa : 'desc_divisa'},
		root: '',
		//paramOrder:['idDivisa', 'descDivisa'],
		directFn: ConsultaPropuestasAction.llenarComboDivisaPago,
		idProperty: 'idStr', 
		fields: [
				 {name: 'idStr'},
				 {name: 'descripcion'}
			 ],
		listeners: {
			load: function(s, records){
				var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeBancoPag, msg:"Cargando..."});
				
				if(records.length == null || records.length <= 0)
					Ext.Msg.alert('SET','No existen divisas');
			},
			exception: function(mist){
				Ext.Msg.alert('SET','Error en conexión al servidor');
				mascara.hide();
			}
		}
    });
    //divisa de pago
	NS.cmbDivisaCambio = new Ext.form.ComboBox({
		store: NS.storeDivisaPago
		,name: PF + 'cmbDivisaCambio'
		,id: PF + 'cmbDivisaCambio'
		,emptyText: 'Divisa de Pago...'
	    ,x: 280
	    ,y: 15
	    ,width: 150
		,typeAhead: true
		,mode: 'local'
		,minChars: 0
		,selecOnFocus: true
		,forceSelection: true
		,valueField:'idStr'
		,displayField:'descripcion'
		,autocomplete: true
		,triggerAction: 'all'
		,value: ''
		,listeners:{
			select:{
				fn:function(combo, valor) {
					BFwrk.Util.updateComboToTextField(PF + 'txtDivisaPago', NS.cmbDivisaCambio.getId());
					var idDivisa = Ext.getCmp(PF + 'txtDivisaPago').value;
					NS.storeBancoPag.baseParams.idDivisa = idDivisa;

					var seleccion = NS.gridDetalle.getSelectionModel().getSelections();
					var mat = new Array();
					
					for(var i=0; i < seleccion.length; i++) {
						mat[i] = seleccion[i].get('noFolioDet');
					}
					var jsonFolios = Ext.util.JSON.encode(mat);
					if (seleccion[0].get('descFormaPago')=='TRANSFERENCIA' && (NS.cmbFormaPago.getValue()=='' || NS.cmbFormaPago.getValue()=='3')) {
					ConsultaPropuestasAction.verificaChequerabenef(idDivisa, jsonFolios, function(res, e) {						
						if(e.message=="Unable to connect to the server."){
							Ext.Msg.alert('SET','Error de conexión al servidor');
							return;
						}
						
						if(res.error == 0){
							if (res.mensage != null && res.mensage != ""){
								Ext.Msg.alert('SET', res.mensage);
								NS.actualizaTipoCambio();
								NS.cmbBancoPag.reset();
								NS.cmbChequeraPagBenef.reset();
								Ext.getCmp(PF + 'txtBancoBenef').setValue('');
			    				Ext.getCmp(PF + 'txtBanco').setValue('');
			    				NS.storeBancoPag.removeAll();
			    				NS.storeBancoPagBenef.removeAll();
							}		
							if(res.folios != null && res.folios != ""){
								var seleccion = NS.gridDetalle.getSelectionModel().getSelections();
								NS.folios = res.folios.split("|");
								if(seleccion[0].get('idDivisa')!=idDivisa && seleccion[0].get('descFormaPago')=='TRANSFERENCIA'){
                    				Ext.getCmp(PF + 'txtBancoBenef').setDisabled(false);
	                    		    NS.cmbBancoPagBenef.setDisabled(false);
	                    		    NS.cmbChequeraPagBenef.setDisabled(false);
	                    		    var noPersona = seleccion[0].get('noCliente');
		    	          			NS.storeBancoPagBenef.baseParams.idDivisa = idDivisa;
		    	          			NS.storeBancoPagBenef.baseParams.noPersona = parseInt(noPersona);
		    	          			NS.storeBancoPagBenef.load();
		    	          			NS.valida=true;
								}else{
									Ext.getCmp(PF + 'txtBancoBenef').setDisabled(true);
	                    		    NS.cmbBancoPagBenef.setDisabled(true);
	                    		    NS.cmbChequeraPagBenef.setDisabled(true);
	                    		    NS.valida=false;
	                    		    NS.modifBenef = false;
								}
								//var seleccionados=NS.gridDetalle.getStore().getRange();
								NS.cmbBancoPag.reset();
								NS.cmbChequeraPagBenef.reset();
								Ext.getCmp(PF + 'txtBancoBenef').setValue('');
								NS.storeBancoPag.load();
								NS.actualizaTipoCambio();
			    				Ext.getCmp(PF + 'txtBanco').setValue('');
							}
						}
						else if(res.mensage != null || res.mensage != "" ){
							Ext.Msg.alert('SET', "[" + res.error + "]: " + res.mensage);
						}else
							Ext.Msg.alert('SET', 'Ocurrio un error al verificar la chequera beneficiaria.');
					});
					} else {
						for (var i = 0; mat.length > i; i++) {
							NS.folios += mat[i] + ',';
						}
							NS.cmbBancoPag.reset();
							NS.cmbChequeraPagBenef.reset();
							Ext.getCmp(PF + 'txtBancoBenef').setValue('');
							NS.storeBancoPag.load();
							NS.actualizaTipoCambio();
		    				Ext.getCmp(PF + 'txtBanco').setValue('');
		    				NS.valida=false;
					}
				}
			}
		}
	});
	
	NS.actualizaTipoCambio = function() {
		if (NS.idDivisaCambio != Ext.getCmp(PF + 'txtDivisaPago').getValue()) {
			if (NS.idDivisaCambio == "MN") {
				NS.divTemp = Ext.getCmp(PF + 'txtDivisaPago').getValue();
			} else {
				NS.divTemp = NS.idDivisaCambio;
			}
			ConsultaPropuestasAction.obtenerTipoCambioD(NS.divTemp, apps.SET.FEC_HOY, function(res, e) {				
				if(e.message=="Unable to connect to the server."){
					Ext.Msg.alert('SET','Error de conexión al servidor');
					return;
				}

				if (res != null) {
					Ext.getCmp(PF + 'txtTipoCambio').setValue(res);	
				} else {
					Ext.getCmp(PF + 'txtTipoCambio').setValue("0.00");
				}
			});		
		} else {
			Ext.getCmp(PF + 'txtTipoCambio').setValue("1.00");
		}
	}
    //combo Banco Pago
	NS.cmbBancoPag = new Ext.form.ComboBox({
		store: NS.storeBancoPag
		,name: PF + 'cmbBancoPag'
		,id: PF + 'cmbBancoPag'
		,typeAhead: true
		,mode: 'local'
		,minChars: 0
		,selecOnFocus: true
		,forceSelection: true
      	,x: 560
        ,y: 15
        ,width: 150
		,valueField:'id'
		,displayField:'descripcion'
		,autocomplete: true
		,emptyText: 'Banco de Pago'
		,triggerAction: 'all'
		,value: ''
		,listeners:{
			select:{
				fn:function(combo, valor) {
					BFwrk.Util.updateComboToTextField(PF + 'txtBanco', NS.cmbBancoPag.getId());
					
					if(combo.getValue() != 0) {
						
						Ext.getCmp(PF + 'dateNvaFecPago').setDisabled(true);
						
						NS.storeChequeraPag.removeAll();
						NS.cmbChequeraPag.reset();
						NS.storeChequeraPag.baseParams.idBanco = combo.getValue();
						NS.storeChequeraPag.baseParams.noEmpresa = NS.noEmpresa;
						NS.storeChequeraPag.baseParams.idDivisa = NS.cmbDivisaCambio.getValue();
						NS.storeChequeraPag.load();
					}else{
						
						//validar que todos los campos sean vacios para  bloquear fecha
						if(NS.cmbFormaPago.getValue() == '' && NS.cmbChequeraPag.getValue() == '' 
							&& NS.cmbChequeraPagBenef.getValue() == ''
							&& NS.cmbBancoPag.getValue() == '' && NS.cmbBancoPagBenef.getValue() == ''){
							
							Ext.getCmp(PF + 'dateNvaFecPago').setDisabled(false);
							
							Ext.getCmp(PF + 'dateNvaFecPago').setValue('');
							NS.cmbFormaPago.reset();
							NS.cmbChequeraPag.reset();
							NS.storeChequeraPag.removeAll();
							NS.storeChequeraPagBenef.removeAll();
							NS.cmbChequeraPagBenef.reset();
							NS.cmbBancoPag.reset();
							NS.cmbBancoPagBenef.reset();
						}
						
					}
				}
			},
			blur:{
				fn:function(combo, valor) {
					
					if(combo.getValue() != '') {
						
						Ext.getCmp(PF + 'dateNvaFecPago').setDisabled(true);
						
					}else{
						
						//validar que todos los campos sean vacios para  bloquear fecha
						if(NS.cmbFormaPago.getValue() == '' && NS.cmbChequeraPag.getValue() == '' 
							&& NS.cmbChequeraPagBenef.getValue() == ''
							&& NS.cmbBancoPag.getValue() == '' && NS.cmbBancoPagBenef.getValue() == ''){
							
							Ext.getCmp(PF + 'dateNvaFecPago').setDisabled(false);
							
							Ext.getCmp(PF + 'dateNvaFecPago').setValue('');
							NS.cmbFormaPago.reset();
							NS.cmbChequeraPag.reset();
							NS.storeChequeraPag.removeAll();
							NS.storeChequeraPagBenef.removeAll();
							NS.cmbChequeraPagBenef.reset();
							NS.cmbBancoPag.reset();
							NS.cmbBancoPagBenef.reset();
						}
						
					}
				}
			}
		}
	});
	
	//store Banco Pago
    NS.storeBancoPagBenef = new Ext.data.DirectStore({
		paramsAsHash: false,
		baseParams: { idDivisa: '', noPersona: 0, descFormaPago: ''},
		root: '',
		paramOrder:['idDivisa', 'noPersona','descFormaPago'],
		directFn: ConsultaPropuestasAction.llenarComboBancoPagBenef,
		idProperty: 'id', 
		fields: [
				 {name: 'id'},
				 {name: 'descripcion'}
			 ],
		listeners: {
			load: function(s, records){
				var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeBancoPagBenef, msg:"Cargando..."});
				
				if(records.length == null || records.length <= 0)
					Ext.Msg.alert('SET','No existen bancos beneficiarios para el pago');
			},
			exception: function(mist){
				Ext.Msg.alert('SET','Error en conexión al servidor');
				mascara.hide();
			}
		}
    });
    
	NS.cmbBancoPagBenef = new Ext.form.ComboBox({
		store: NS.storeBancoPagBenef
		,name: PF + 'cmbBancoPagBenef'
		,id: PF + 'cmbBancoPagBenef'
		,typeAhead: true
		,mode: 'local'
		,minChars: 0
		,selecOnFocus: true
		,forceSelection: true
      	,x: 560
        ,y: 65
        ,width: 150
		,valueField:'id'
		,displayField:'descripcion'
		,autocomplete: true
		,emptyText: 'Banco de Pago Beneficiario'
		,triggerAction: 'all'
		,value: ''
		,listeners:{
			select:{
				fn:function(combo, valor) {
					BFwrk.Util.updateComboToTextField(PF + 'txtBancoBenef', NS.cmbBancoPagBenef.getId());
					
					if(combo.getValue() != 0) {
						
						Ext.getCmp(PF + 'dateNvaFecPago').setDisabled(true);
						
						var rec = NS.gridDetalle.getSelectionModel().getSelections();
						
						if(rec.length > 0 ){
							
							var noProvTmp = rec[0].get('equivale');
							for (i=0; i<rec.length; i++) {
								if(noProvTmp != rec[i].get('equivale')){
									Ext.Msg.alert('SET','Para cargar chequera beneficiaria es necesario seleccionar registros de un mismo proveedor.');
									return;
								}
							}
							
							NS.storeChequeraPagBenef.baseParams.idBenef = parseInt(rec[0].get('noCliente'));
						}else{
							NS.storeChequeraPagBenef.baseParams.idBenef = 0;
						}
						
						NS.storeChequeraPagBenef.removeAll();
						NS.cmbChequeraPagBenef.reset();
						NS.descFormaPago = rec[0].get('descFormaPago');
						NS.storeChequeraPagBenef.baseParams.idBanco = combo.getValue();
						NS.storeChequeraPagBenef.baseParams.noEmpresa = NS.noEmpresa;
						NS.storeChequeraPagBenef.baseParams.idDivisa = NS.cmbDivisaCambio.getValue();
					    //NS.sDivisa;
						NS.storeChequeraPagBenef.baseParams.descFormaPago = NS.descFormaPago;
						NS.storeChequeraPagBenef.load();
					}else{
						
						//validar que todos los campos sean vacios para  bloquear fecha
						if(NS.cmbFormaPago.getValue() == '' && NS.cmbChequeraPag.getValue() == '' 
							&& NS.cmbChequeraPagBenef.getValue() == ''
							&& NS.cmbBancoPag.getValue() == '' && NS.cmbBancoPagBenef.getValue() == ''){
							
							Ext.getCmp(PF + 'dateNvaFecPago').setDisabled(false);
							
							Ext.getCmp(PF + 'dateNvaFecPago').setValue('');
							NS.cmbFormaPago.reset();
							NS.cmbChequeraPag.reset();
							NS.storeChequeraPag.removeAll();
							NS.storeChequeraPagBenef.removeAll();
							NS.cmbChequeraPagBenef.reset();
							NS.cmbBancoPag.reset();
							NS.cmbBancoPagBenef.reset();
						}
						
					}
					
				}
			},
			blur:{
				fn:function(combo, valor) {
					
					if(combo.getValue() != '') {
						
						Ext.getCmp(PF + 'dateNvaFecPago').setDisabled(true);
						
					}else{
						
						//validar que todos los campos sean vacios para  bloquear fecha
						if(NS.cmbFormaPago.getValue() == '' && NS.cmbChequeraPag.getValue() == '' 
							&& NS.cmbChequeraPagBenef.getValue() == ''
							&& NS.cmbBancoPag.getValue() == '' && NS.cmbBancoPagBenef.getValue() == ''){
							
							Ext.getCmp(PF + 'dateNvaFecPago').setDisabled(false);
							
							Ext.getCmp(PF + 'dateNvaFecPago').setValue('');
							NS.cmbFormaPago.reset();
							NS.cmbChequeraPag.reset();
							NS.storeChequeraPag.removeAll();
							NS.storeChequeraPagBenef.removeAll();
							NS.cmbChequeraPagBenef.reset();
							NS.cmbBancoPag.reset();
							NS.cmbBancoPagBenef.reset();
						}
						
					}
				}
			}
		}
	});
	
	//store Chequera Pago
    NS.storeChequeraPag = new Ext.data.DirectStore({
		paramsAsHash: false,
		baseParams: { idBanco: 0, noEmpresa : 0, idDivisa: ''},
		root: '',
		paramOrder:['idBanco', 'noEmpresa', 'idDivisa'],
		directFn: ConsultaPropuestasAction.llenarComboChequeraPag,
		idProperty: 'descripcion', 
		fields: [
				 {name: 'descripcion'},
				 {name: 'descripcion'}
			 ],
		listeners: {
			load: function(s, records){
				var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeChequeraPag, msg:"Cargando..."});
				
				if(records.length == null || records.length <= 0)
					Ext.Msg.alert('SET','No existen chequera para el pago');
			},
			exception: function(mist){
				Ext.Msg.alert('SET','Error en conexión al servidor');
				mascara.hide();
			}
		}
    });
    
    //combo Chequera Pagadora
	NS.cmbChequeraPag = new Ext.form.ComboBox({
		store: NS.storeChequeraPag
		,name: PF + 'cmbChequeraPag'
		,id: PF + 'cmbChequeraPag'
		,typeAhead: true
		,mode: 'local'
		,minChars: 0
		,selecOnFocus: true
		,forceSelection: true
      	,x: 765
        ,y: 15
        ,width: 150
		,valueField:'descripcion'
		,displayField:'descripcion'
		,autocomplete: true
		,emptyText: 'Chequera de Pago'
		,triggerAction: 'all'
		,value: ''
		,listeners:{
			select:{
				fn:function(combo, valor) {
					
					if(combo.getValue() != '') {
						
						Ext.getCmp(PF + 'dateNvaFecPago').setDisabled(true);
						
					}else{
						
						//validar que todos los campos sean vacios para  bloquear fecha
						if(NS.cmbFormaPago.getValue() == '' && NS.cmbChequeraPag.getValue() == '' 
							&& NS.cmbChequeraPagBenef.getValue() == ''
							&& NS.cmbBancoPag.getValue() == '' && NS.cmbBancoPagBenef.getValue() == ''){
							
							Ext.getCmp(PF + 'dateNvaFecPago').setDisabled(false);
							
							Ext.getCmp(PF + 'dateNvaFecPago').setValue('');
							NS.cmbFormaPago.reset();
							NS.cmbChequeraPag.reset();
							NS.storeChequeraPag.removeAll();
							NS.storeChequeraPagBenef.removeAll();
							NS.cmbChequeraPagBenef.reset();
							NS.cmbBancoPag.reset();
							NS.cmbBancoPagBenef.reset();
						}
						
					}
				}
			},
			blur:{
				fn:function(combo, valor) {
					
					if(combo.getValue() != '') {
						
						Ext.getCmp(PF + 'dateNvaFecPago').setDisabled(true);
						
					}else{
						
						//validar que todos los campos sean vacios para  bloquear fecha
						if(NS.cmbFormaPago.getValue() == '' && NS.cmbChequeraPag.getValue() == '' 
							&& NS.cmbChequeraPagBenef.getValue() == ''
							&& NS.cmbBancoPag.getValue() == '' && NS.cmbBancoPagBenef.getValue() == ''){
							
							Ext.getCmp(PF + 'dateNvaFecPago').setDisabled(false);
							
							Ext.getCmp(PF + 'dateNvaFecPago').setValue('');
							NS.cmbFormaPago.reset();
							NS.cmbChequeraPag.reset();
							NS.storeChequeraPag.removeAll();
							NS.storeChequeraPagBenef.removeAll();
							NS.cmbChequeraPagBenef.reset();
							NS.cmbBancoPag.reset();
							NS.cmbBancoPagBenef.reset();
						}
						
					}
				}
			}
			
		}
	});
	
	NS.storeChequeraPagBenef = new Ext.data.DirectStore({
		paramsAsHash: false,
		baseParams: { idBanco: 0, noEmpresa : 0, idDivisa: '', idBenef: 0, descFormaPago: ''},
		root: '',
		paramOrder:['idBanco', 'noEmpresa', 'idDivisa', 'idBenef','descFormaPago'],
		directFn: ConsultaPropuestasAction.llenarComboChequeraPagBenef,
		idProperty: 'descripcion', 
		fields: [
				 {name: 'descripcion'},
				 {name: 'descripcion'}
			 ],
		listeners: {
			load: function(s, records){
				var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeChequeraPagBenef, msg:"Cargando..."});
				
				if(records.length == null || records.length <= 0)
					Ext.Msg.alert('SET','No existen chequera para el pago');
			},
			exception: function(mist){
				Ext.Msg.alert('SET','Error en conexión al servidor');
				mascara.hide();
			}
		}
    });
	
	 //combo Chequera Pago beneficiario
	NS.cmbChequeraPagBenef = new Ext.form.ComboBox({
		store: NS.storeChequeraPagBenef
		,name: PF + 'cmbChequeraPagBenef'
		,id: PF + 'cmbChequeraPagBenef'
		,typeAhead: true
		,mode: 'local'
		,minChars: 0
		,selecOnFocus: true
		,forceSelection: true
      	,x: 765
        ,y: 65
        ,width: 150
		,valueField:'descripcion'
		,displayField:'descripcion'
		,autocomplete: true
		,emptyText: 'Chequera de Pago Beneficiario'
		,triggerAction: 'all'
		,value: ''
			,listeners:{
				select:{
					fn:function(combo, valor) {
						//if(Ext.getCmp(PF + 'chkCVT').checked){
							/*if(NS.cmbChequeraPagBenef.getValue()!=""){
                				NS.chequeraPagadora=NS.gridDetalle.getSelectionModel().getSelections();
                				ConsultaPropuestasAction.obtenerDivisaChequera(NS.chequeraPagadora[0].get('idChequera'),function(result, e){
                					if(result!=null && result.error!='' && result!=undefined){
                						NS.cheqBen=NS.cmbChequeraPagBenef.getValue();
                						NS.cheqBen2=NS.cheqBen.split("]");
                						NS.divisaBen=NS.cheqBen2[0].replace("[","");
            							if(NS.divisaBen==result){
                							Ext.Msg.alert('SET',"Debe deseleccionar la casilla de CVT para poder elegir esta chequera");
                							NS.cmbChequeraPagBenef.setValue("");
                							
                						}
                					
                						
                					}
                				});
                			}*/
							if(combo.getValue() != '') {
								
								Ext.getCmp(PF + 'dateNvaFecPago').setDisabled(true);
								
							}else{
								
								//validar que todos los campos sean vacios para  bloquear fecha
								if(NS.cmbFormaPago.getValue() == '' && NS.cmbChequeraPag.getValue() == '' 
									&& NS.cmbChequeraPagBenef.getValue() == ''
									&& NS.cmbBancoPag.getValue() == '' && NS.cmbBancoPagBenef.getValue() == ''){
									
									Ext.getCmp(PF + 'dateNvaFecPago').setDisabled(false);
									
									Ext.getCmp(PF + 'dateNvaFecPago').setValue('');
									NS.cmbFormaPago.reset();
									NS.cmbChequeraPag.reset();
									NS.storeChequeraPag.removeAll();
									NS.storeChequeraPagBenef.removeAll();
									NS.cmbChequeraPagBenef.reset();
									NS.cmbBancoPag.reset();
									NS.cmbBancoPagBenef.reset();
								}
								
							}
						/*}else{
							if(NS.cmbChequeraPagBenef.getValue()!=""){
                				NS.chequeraPagadora=NS.gridDetalle.getSelectionModel().getSelections();
                				ConsultaPropuestasAction.obtenerDivisaChequera(NS.chequeraPagadora[0].get('idChequera'),function(result, e){
                					if(result!=null && result.error!='' && result!=undefined){
                						NS.cheqBen=NS.cmbChequeraPagBenef.getValue();
                						NS.cheqBen2=NS.cheqBen.split("]");
                						NS.divisaBen=NS.cheqBen2[0].replace("[","");
            							/*if(NS.divisaBen!=result){
                							Ext.Msg.alert('SET',"Al seleccionar esta chequera debe realizara una Compra Transfer porque las divisas son diferentes");
                							NS.cmbChequeraPagBenef.setValue("");
                						}
                					
                						
                					}
                				});
                			}
						}*/
					}
				},
				blur:{
					fn:function(combo, valor) {
						
						if(combo.getValue() != '') {
							
							Ext.getCmp(PF + 'dateNvaFecPago').setDisabled(true);
							
						}else{
							
							//validar que todos los campos sean vacios para  bloquear fecha
							if(NS.cmbFormaPago.getValue() == '' && NS.cmbChequeraPag.getValue() == '' 
								&& NS.cmbChequeraPagBenef.getValue() == ''
								&& NS.cmbBancoPag.getValue() == '' && NS.cmbBancoPagBenef.getValue() == ''){
								
								Ext.getCmp(PF + 'dateNvaFecPago').setDisabled(false);
								
								Ext.getCmp(PF + 'dateNvaFecPago').setValue('');
								NS.cmbFormaPago.reset();
								NS.cmbChequeraPag.reset();
								NS.storeChequeraPag.removeAll();
								NS.storeChequeraPagBenef.removeAll();
								NS.cmbChequeraPagBenef.reset();
								NS.cmbBancoPag.reset();
								NS.cmbBancoPagBenef.reset();
							}
							
						}
					}
				}
			}	
		
	});
	
	NS.obtenerTipoCambio = function(sDivisa) {
		var rec = NS.gridDetalle.getSelectionModel().getSelections();
		var mat = new Array();
		
		for(var i=0; i<rec; i++) {
			var array = {};
			array.idDivisa = rec[i].get('idDivisa');
			mat[i] = array;
		}
		var sJson = Ext.util.JSON.encode(mat);
		
		ConsultaPropuestasAction.obtenerTipoCambio(sJson, sDivisa, apps.SET.FEC_HOY, function(res, e) {
			
			if(e.message=="Unable to connect to the server."){
				Ext.Msg.alert('SET','Error de conexión al servidor');
				return;
			}
			
			if(res == 100)
				Ext.Msg.alert('SET', 'Seleccione movimientos de una misma divisa!!');
			else if(res == 101)
				Ext.Msg.alert('SET', 'Las divisas de los movimientos y la de cambio no son correctas!!');
			else
				Ext.getCmp(PF + 'txtTipoCambio').setValue(NS.formatNumber(Math.round((res)*100)/100));
		});
	};
	
	//Ventana para autorizar
	var winLogin = new Ext.Window({
		title: 'Confirmación de contraseña'
		,modal:true
		,shadow:true
		,width: 200
	   	,height:200
	   	,minWidth: 400
	   	,minHeight: 580
	   	,layout: 'fit'
	   	,plain:true
	    ,bodyStyle:'padding:10px;'
	   	,buttonAlign:'center'
	   	,closable: false
	   	,resizable: false
	   	,draggable: false
	   	,items: [
	   	{
	   	xtype: 'fieldset',
        title: 'Introduce tu contraseña',
        id:PF+'framePendiente',
        name:PF+'framePendientes',
       	x: 0,
        y: 0,
        width: 80,
        height: 100,
        layout: 'absolute',
        items: [
	   		{
	       		inputType: 'password',
				xtype: 'textfield',
	       		id:PF+'txtPsw',
	       		name:PF+'txtPsw',
	       		x:20,
	       		y:10,
	       		width:100,
	       		hidden:false,
	       		allowBlank: false,
	       		blankText:'Debe introducir su contraseña',
	       		listeners:{
	       			change:{
	       				fn:function(caja,valor){ }
			        }
			     }
	   		}
		]
        }
		]
	   	,buttons: [
		   	{
			 	text: 'Aceptar',
				handler: function(e) {
					
              		//Solo se utiliza para modificarPropuestas: EMS 180915)
					if(NS.autorizarModificacion == true){
						//Ext.Msg.alert("SET","entro a modificar");
						var matrizAutorizar = new Array();
						
						var recordsAutorizar = NS.gridMaestroPropuestas.getSelectionModel().getSelections();
						
						for(var inc = 0; inc<recordsAutorizar.length; inc++) {
							var regAutorizar = {};
							regAutorizar.idGrupoFlujo=recordsAutorizar[inc].get('idGrupoFlujo');
		    				regAutorizar.idGrupo=recordsAutorizar[inc].get('idGrupo');
		    				regAutorizar.cveControl=recordsAutorizar[inc].get('cveControl');
		    				regAutorizar.usr1=recordsAutorizar[inc].get('usuarioUno');
		    				regAutorizar.usr2=recordsAutorizar[inc].get('usuarioDos');
		    				regAutorizar.usr3=recordsAutorizar[inc].get('usuarioTres');
		    				regAutorizar.nivelAut=recordsAutorizar[inc].get('nivelAutorizacion');
		    				regAutorizar.cupoTotal=recordsAutorizar[inc].get('cupoTotal');
		    				regAutorizar.psw=Ext.getCmp(PF+'txtPsw').getValue();
		    				
		    				matrizAutorizar[inc]=regAutorizar;
							
		    			}
						
						NS.jsonPropuestas = Ext.util.JSON.encode(matrizAutorizar);
						
						ConsultaPropuestasAction.autorizarModificaciones(NS.jsonPropuestas, function(result, e){
							
							if(e.message=="Unable to connect to the server."){
								Ext.Msg.alert('SET','Error de conexión al servidor');
								return;
							}
							
							if(result != null && result.msgUsuario != '' && result.msgUsuario != undefined) {								
								Ext.Msg.alert('SET',''+result.msgUsuario);
								habilitarBotones(true);
								//mascara.hide();
								return;
							}else{
								//Validar usuarios con firmas
								
								var matrizModificarProp = new Array();
								var cont = 0;
								for(var inc=0; inc<recordsAutorizar.length; inc++) {
									var regAutorizar = {};
									//En caso de varios registros solo se actualizaran si no tiene ninguna autorización.
									if(recordsAutorizar[inc].get('user1') == null || recordsAutorizar[inc].get('user1') == "" ){
										regAutorizar.cveControl=recordsAutorizar[inc].get('cveControl');
										
										//+++ Sólo se usa para insetar en la bitacora +++++//
					    				regAutorizar.fechaProp =cambiarFecha(''+recordsAutorizar[inc].get('fechaPropuesta'));
					    				//+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++//
					    				
					    				matrizModificarProp[cont]=regAutorizar;
					    				cont++;
									}
				    			}
								
								NS.jsonPropuestas = Ext.util.JSON.encode(matrizModificarProp);
								
								if(NS.fecModGlob!=''){
									ConsultaPropuestasAction.updateFecProp(NS.jsonPropuestas, NS.fecModGlob, function(resultado, e) {
										
										if(e.message=="Unable to connect to the server."){
											Ext.Msg.alert('SET','Error de conexión al servidor');
											return;
										}
										
										if (resultado != '' && resultado != undefined) {
											Ext.Msg.alert('SET', "Se actualizaron " + resultado + " propuestas");
											habilitarBotones(true);
											//mascara.hide();
											//Inserta en bitacora propuesta
											ConsultaPropuestasAction.insertaBitacoraPropuesta('ModProp', NS.jsonPropuestas, 
													'', NS.fecModGlob , function(result, e){
												
												if(e.message=="Unable to connect to the server."){
													Ext.Msg.alert('SET','Error de conexión al servidor');
													return;
												}
												
												if(result != null && result.length != '') {
													if(result.error != ''){
														Ext.Msg.alert('SET',''+result.error);
													}
												}
											});
											
											NS.buscar();
											return;
										}
									});
								}
							}
						});
					}else{
						//Para autorizacion/Desautorizacion de propuestas
						
						var matrizAutorizar = new Array();
						
						var recordsAutorizar = NS.gridMaestroPropuestas.getSelectionModel().getSelections();
						var cont = 0;
						var bloqSAP = false;
						var sinBancoChequera = false;
						for(var inc = 0; inc<recordsAutorizar.length; inc++) {
							
							//Si tiene alguna chequera que no existe en ctas_banco, se salta el registro.
							//O si tiene una chequera en color azul con pagos interbancarios tambien saltar.
							if(NS.autoDesauto == 'AUT'){
								
								if(recordsAutorizar[inc].get('color')== 'color:#C5B8D6'
									|| recordsAutorizar[inc].get('color')== 'color:#090DF9' 
										|| recordsAutorizar[inc].get('color')== 'color:#A901DB'
											|| recordsAutorizar[inc].get('color')== 'color:#FF00BF'
												|| recordsAutorizar[inc].get('color')== 'color:#FA58D0'){
									continue;
								}
								//si se encuentra en la matriz global, no autoriza. FALSE = SI autoriza 
								if(matrizBloqGlob.indexOf(recordsAutorizar[inc].get('cveControl')) < 0){
									bloqSAP = false;
								}else{
									bloqSAP = true;
								}
								//alert("matriz "+matrizBancoChequerasGlob.indexOf(recordsAutorizar[inc].get('cveControl')));
								if(matrizBancoChequerasGlob.indexOf(recordsAutorizar[inc].get('cveControl')) < 0){
									sinBancoChequera = false;
								}else{
									sinBancoChequera = true;
								}
							}
							//alert(bloqSAP+" "+sinBancoChequera);
							if(bloqSAP == false && sinBancoChequera == false){
								var regAutorizar = {};
								
								regAutorizar.idGrupoFlujo=recordsAutorizar[inc].get('idGrupoFlujo');
			    				regAutorizar.idGrupo=recordsAutorizar[inc].get('idGrupo');
			    				regAutorizar.cveControl=recordsAutorizar[inc].get('cveControl');
			    				regAutorizar.usr1=recordsAutorizar[inc].get('usuarioUno');
			    				regAutorizar.usr2=recordsAutorizar[inc].get('usuarioDos');
			    				regAutorizar.usr3=recordsAutorizar[inc].get('usuarioTres');
			    				regAutorizar.nivelAut=recordsAutorizar[inc].get('nivelAutorizacion');
			    				regAutorizar.cupoTotal=recordsAutorizar[inc].get('cupoTotal');
			    				regAutorizar.psw=Ext.getCmp(PF+'txtPsw').getValue();
			    				matrizAutorizar[cont]=regAutorizar;
			    				cont = cont + 1;
							}
							
		    			}
		    			
						NS.jsonPropuestas = Ext.util.JSON.encode(matrizAutorizar);
						//Ext.Msg.alert("propuestas",NS.jsonPropuestas);
						//console.log(NS.autoDesauto);
						if(NS.autoDesauto!='AUT'){
							//console.log("entro a DAUT");
							console.log(NS.jsonPropuestas);
							ConsultaPropuestasAction.buscarPropuestasPagadas(NS.jsonPropuestas,function(result,e){
								console.log("result.msgUsuario "+''+result.msgUsuario);
								if(result.msgUsuario=="1"){
									Ext.Msg.confirm("SET","La propuesta esta pagada, ¿Desea quitar la autorización?",function(btn){
										if(btn=='yes'){
											ConsultaPropuestasAction.autorizar(NS.jsonPropuestas, NS.funAutoDesAutorisa(), NS.autoDesauto, NS.autCheq, function(result, e){
												
												if(e.message=="Unable to connect to the server."){
													Ext.Msg.alert('SET','Error de conexión al servidor');
													return;
												}
												
												if(result != null && result.length != '') {
													
													Ext.Msg.alert('SET',''+result.msgUsuario);
													habilitarBotones(true);
													//mascara.hide();
							//												if(result.msgUsuario == 'Su autorización ha sido almacenada con exito' ||
							//														result.msgUsuario == 'Las autorizaciónes han sido eliminadas con exito') {							
													
													if(result.bit == '1'){
														ConsultaPropuestasAction.insertaBitacoraPropuesta(NS.autoDesauto, NS.jsonPropuestas, 
																'', '', function(result, e){
															
															if(e.message=="Unable to connect to the server."){
																Ext.Msg.alert('SET','Error de conexión al servidor');
																return;
															}
															
															if(result != null && result.length != '') {
																if(result.error != ''){
																	Ext.Msg.alert('SET',''+result.error);
																}
															}
														});
																
													}
													
														mascara.show();
														NS.storeMaestro.load();
														NS.gridMaestroPropuestas.getView().refresh();
														NS.gridDetalle.store.removeAll();
								        				NS.gridDetalle.getView().refresh();
								        				Ext.getCmp(PF + 'txtImporteProp').setValue('0.00');
								        				Ext.getCmp(PF+'totalMN').setValue('0.00');
								              			Ext.getCmp(PF+'sumMN').setValue('0.00');
								              			Ext.getCmp(PF+'totalDLS').setValue('0.00');

								              			
							//												}
														
												}
											});
										}else{
											return;
										}
									});
								}else{
									ConsultaPropuestasAction.autorizar(NS.jsonPropuestas, NS.funAutoDesAutorisa(), NS.autoDesauto, NS.autCheq, function(result, e){
										
										if(e.message=="Unable to connect to the server."){
											Ext.Msg.alert('SET','Error de conexión al servidor');
											return;
										}
										
										if(result != null && result.length != '') {
											
											Ext.Msg.alert('SET',''+result.msgUsuario);
											habilitarBotones(true);
//											//mascara.hide();
//					//												if(result.msgUsuario == 'Su autorización ha sido almacenada con exito' ||
//					//														result.msgUsuario == 'Las autorizaciónes han sido eliminadas con exito') {							
											
											if(result.bit == '1'){
												ConsultaPropuestasAction.insertaBitacoraPropuesta(NS.autoDesauto, NS.jsonPropuestas, 
														'', '', function(result, e){
													
													if(e.message=="Unable to connect to the server."){
														Ext.Msg.alert('SET','Error de conexión al servidor');
														return;
													}
													
													if(result != null && result.length != '') {
														if(result.error != ''){
															Ext.Msg.alert('SET',''+result.error);
														}
													}
												});
														
											}
											
												mascara.show();
												NS.storeMaestro.load();
												NS.gridMaestroPropuestas.getView().refresh();
												NS.gridDetalle.store.removeAll();
						        				NS.gridDetalle.getView().refresh();
						        				Ext.getCmp(PF + 'txtImporteProp').setValue('0.00');
						        				Ext.getCmp(PF+'totalMN').setValue('0.00');
						              			Ext.getCmp(PF+'sumMN').setValue('0.00');
						              			Ext.getCmp(PF+'totalDLS').setValue('0.00');
						              			
//					//												}
												
										}
									});
								}
								
							});
						}else{
							ConsultaPropuestasAction.autorizar(NS.jsonPropuestas, NS.funAutoDesAutorisa(), NS.autoDesauto, NS.autCheq, function(result, e){
								
								if(e.message=="Unable to connect to the server."){
									Ext.Msg.alert('SET','Error de conexión al servidor');
									return;
								}
								
								if(result != null && result.length != '') {
								//	console.log("MENSAJE DE AUTORIZAR "+result.msgUsuario);
									Ext.Msg.alert('SET',''+result.msgUsuario);
									habilitarBotones(true);									
//									//mascara.hide();
//			//												if(result.msgUsuario == 'Su autorización ha sido almacenada con exito' ||
//			//														result.msgUsuario == 'Las autorizaciónes han sido eliminadas con exito') {							
									
									if(result.bit == '1'){
										ConsultaPropuestasAction.insertaBitacoraPropuesta(NS.autoDesauto, NS.jsonPropuestas, 
												'', '', function(result, e){
											
											if(e.message=="Unable to connect to the server."){
												Ext.Msg.alert('SET','Error de conexión al servidor');
												return;
											}
											
											if(result != null && result.length != '') {
												if(result.error != ''){
													console.log("error de bitacora "+result.error);
													Ext.Msg.alert('SET',''+result.error);
												}
											}
										});
												
									}
									//console.log("usuario "+recordsAutorizar[0].get('usuarioUno'))
									if(recordsAutorizar[0].get('usuarioUno')==0){
//										var regSelecMaestro = NS.gridMaestroPropuestas.getSelectionModel().getSelections();
//										for(var i=0;i<regSelecMaestro.length;i++){
											NS.imprimirReporte();
//										}
										
									}
										
										mascara.show();
										NS.storeMaestro.load();
										NS.gridMaestroPropuestas.getView().refresh();
										NS.gridDetalle.store.removeAll();
				        				NS.gridDetalle.getView().refresh();
				        				Ext.getCmp(PF + 'txtImporteProp').setValue('0.00');
				        				Ext.getCmp(PF+'totalMN').setValue('0.00');
				              			Ext.getCmp(PF+'sumMN').setValue('0.00');
				              			Ext.getCmp(PF+'totalDLS').setValue('0.00');
				              			
				              			
			//												}
										
								}
							});
						}

					}
					
					NS.autorizarModificacion = false
				 	winLogin.hide();
				 	Ext.getCmp(PF+'txtPsw').setValue("");
				 }
		     },{
			 	text: 'Cancelar',
				handler: function(e) {
					habilitarBotones(true);
					//mascara.hide();
					NS.autorizarModificacion = false 
				 	winLogin.hide();
				 }
		     }
	 	]
	});
	
	/*
	NS.eliminarPropuesta = function() {
		var matrizElim= new Array();
		var recPropuestas = NS.gridMaestroPropuestas.getSelectionModel().getSelections();
		
		if(recPropuestas.length == 0) {
			Ext.Msg.alert('SET','Es necesario seleccionar un registro!!');
			return;
		}
		Ext.Msg.confirm('SET','¿Esta seguro de eliminar las propuestas?', function(btn) {
			if(btn == 'yes') {
				for(var i=0; i<recPropuestas.length; i++) {
					var regElim={};
					regElim.cveControl=recPropuestas[i].get('cveControl');
					matrizElim[i]=regElim;
				}
				
				var jsonString = Ext.util.JSON.encode(matrizElim);
				
				ConsultaPropuestasAction.eliminarProp(jsonString,NS.cmbGrupoEmpresas.getValue(), function(resultado, e) {
					if (resultado != '' && resultado != undefined){
						Ext.Msg.alert('SET', resultado + '!!');
						NS.buscar();
					}else
						Ext.Msg.alert('SET', 'Error en la eliminación de la propuesta!!');
				});
			}
		});
	};
	*/
	
	NS.modificarPropuesta = function() {
		
		var gridPropuestas = new Array();
		var regPropuestas = {}; 
		var recSelectProp = NS.gridMaestroPropuestas.getSelectionModel().getSelections();
		var i = 0;
		var gridDetPropuestas = new Array();
		var recSelectDetProp = NS.gridDetalle.getSelectionModel().getSelections();
		
		if(recSelectProp.length <= 0){
			Ext.Msg.alert('SET','Es necesario seleccionar una propuesta.');
			return;
		}
		
		if(recSelectDetProp.length == 0) {
			Ext.Msg.alert('SET','Es necesario seleccionar un detalle a modificar!!');
			return;
		}
		
		//Valida que solo tenga seleccionados registros con la misma empresa
		var noEmpresaTmp = recSelectDetProp[0].get('noEmpresa');
		var noEquivale = recSelectDetProp[0].get('equivale');
		for (i=0; i<recSelectDetProp.length; i++) {			
			
			if(noEmpresaTmp != recSelectDetProp[i].get('noEmpresa') && NS.modifPag == true){
				Ext.Msg.alert('SET','Para modificar detalles es necesario seleccionar registros de una misma empresa.');
				return;
			}
			
			if(noEquivale != recSelectDetProp[i].get('equivale') && NS.modifBenef == true){
				Ext.Msg.alert('SET','Para modificar detalles es necesario seleccionar registros de un mismo proveedor.');
				return;
			}
			
		}
		
		NS.gridDetalle.setDisabled(true);
		Ext.getCmp(PF + 'contFormaPago').setDisabled(false);
		
		for (i=0; i<recSelectProp.length; i++) {
			regPropuestas.cveControl = recSelectProp[i].get('cveControl');
			regPropuestas.usr1 = recSelectProp[i].get('usuarioUno');
			regPropuestas.usr2 = recSelectProp[i].get('usuarioDos');
			regPropuestas.usr3 = recSelectProp[i].get('usuarioTres');
			regPropuestas.numIntercos = recSelectProp[i].get('numIntercos');
			gridPropuestas[i] = regPropuestas;
		}
		NS.jsonPropuestas = Ext.util.JSON.encode(gridPropuestas);
		
		for (i=0; i<recSelectDetProp.length; i++) {
			var regDetPropuestas = {};
			regDetPropuestas.noEmpresa = recSelectDetProp[i].get('noEmpresa');
			regDetPropuestas.idDivisa = recSelectDetProp[i].get('idDivisa');
			regDetPropuestas.bancoPago = recSelectDetProp[i].get('idBanco');
			regDetPropuestas.idBancoBenef = recSelectDetProp[i].get('idBancoBenef');
			regDetPropuestas.noCliente = recSelectDetProp[i].get('noCliente');
			regDetPropuestas.noFolioDet = recSelectDetProp[i].get('noFolioDet');
			gridDetPropuestas[i] = regDetPropuestas;
			
			NS.sDivisa = recSelectDetProp[i].get('idDivisa');
			NS.noEmpresa = recSelectDetProp[i].get('noEmpresa');
			NS.noPersona = recSelectDetProp[i].get('noCliente');
		}
		NS.jsonDetPropuestas = Ext.util.JSON.encode(gridDetPropuestas);
		
		habilitarBotones(false);
		//mascara.show();
		
		ConsultaPropuestasAction.modificarProp(NS.jsonDetPropuestas, NS.jsonDetPropuestas, function(resultado, e) {
			
			if(e.message=="Unable to connect to the server."){
				Ext.Msg.alert('SET','Error de conexión al servidor');
				return;
			}
			
			if (resultado != '' && resultado != undefined) {
				habilitarBotones(true);
				
				//mascara.hide();
				Ext.Msg.alert('SET', resultado + '!!');
				return;
			}
		});
		
		//Limpia y habilita los componentes a modificar - EMS 261115
		NS.cmbFormaPago.setValue("");
		NS.cmbBancoPag.setValue("");
		NS.cmbChequeraPag.reset();
		NS.storeChequeraPag.removeAll();
		Ext.getCmp(PF + 'txtBanco').setValue("");
		
		NS.cmbBancoPagBenef.setValue("");
		//NS.cmbChequeraPag.setValue("");
		NS.cmbChequeraPagBenef.setValue("");
		NS.storeChequeraPagBenef.removeAll();
		NS.cmbChequeraPagBenef.reset();
		Ext.getCmp(PF + 'txtBancoBenef').setValue("");
		
		NS.cmbFormaPago.setDisabled(false);
		NS.cmbBancoPag.setDisabled(false);
		NS.cmbBancoPagBenef.setDisabled(false);
		NS.cmbChequeraPag.setDisabled(false);
		NS.cmbChequeraPagBenef.setDisabled(false);
		Ext.getCmp(PF + 'txtBanco').setDisabled(false);
		Ext.getCmp(PF + 'txtBancoBenef').setDisabled(false);
		Ext.getCmp(PF + 'dateNvaFecPago').setValue(""); 
		//
		
		NS.sDivisa = recSelectDetProp[0].get('idDivisa');
		
		if(NS.modifPag){
			NS.storeBancoPag.baseParams.idDivisa = NS.sDivisa;
			NS.storeBancoPag.baseParams.noEmpresa = ''+noEmpresaTmp;
			//NS.storeBancoPag.load();
		}
		
		if(NS.modifBenef){
			if(recSelectDetProp[0].get('descFormaPago') == 'TRANSFERENCIA' || recSelectDetProp[0].get('descFormaPago')== 'TRASPASO'){
				NS.noPersona = recSelectDetProp[0].get('noCliente');
				NS.descFormaPago = recSelectDetProp[0].get('descFormaPago');
	  			NS.storeBancoPagBenef.baseParams.idDivisa = NS.sDivisa;
	  			NS.storeBancoPagBenef.baseParams.noPersona = parseInt(NS.noPersona);
	  			NS.storeBancoPagBenef.baseParams.descFormaPago = NS.descFormaPago;
	  			NS.storeBancoPagBenef.load();
			}
		}
		
	};
	
	
	NS.aceptarModificar = function() {
		
		var recSelectDetProp = NS.gridDetalle.getSelectionModel().getSelections();
		
		if(recSelectDetProp.length == 0) {
			Ext.Msg.alert('SET','Es necesario seleccionar un registro a modificar!!');
			return;
		}
		
		//var recSelectDetProp = NS.storeDetalle.data.items;
		var gridDetPropuestas = new Array();
		console.log("detalles seleccionados "+recSelectDetProp.length);
		for (var i=0; i<recSelectDetProp.length; i++) {
			var regDetPropuestas = {};
			regDetPropuestas.noEmpresa = recSelectDetProp[i].get('noEmpresa');
			regDetPropuestas.idDivisa = recSelectDetProp[i].get('idDivisa');
			regDetPropuestas.bancoPago = recSelectDetProp[i].get('bancoPago'); //idBanco
			regDetPropuestas.idBancoBenef = recSelectDetProp[i].get('idBancoBenef');
			regDetPropuestas.noCliente = recSelectDetProp[i].get('noCliente');
			regDetPropuestas.noFolioDet = recSelectDetProp[i].get('noFolioDet');
			//+++++ Solo se usa para obtener datos de inserccion a la bitacora +++++
			regDetPropuestas.idChequera = recSelectDetProp[i].get('idChequera');
			regDetPropuestas.idChequeraBenef = recSelectDetProp[i].get('idChequeraBenef');
			regDetPropuestas.formaPago = recSelectDetProp[i].get('descFormaPago');
			regDetPropuestas.noDocto = recSelectDetProp[i].get('noFactura');
			//++++++++++++++++++++++++++++
			
			gridDetPropuestas[i] = regDetPropuestas;
		}
		NS.jsonDetPropuestas = Ext.util.JSON.encode(gridDetPropuestas);
		NS.mod();
		
	};
	
	NS.mod = function() {
		//Es posible qie hayan seleccionado otra empresa, se vuelve a validar las empresas.
		var recSelectDetProp = NS.gridDetalle.getSelectionModel().getSelections();
				
		var noEmpresaTmp = recSelectDetProp[0].get('noEmpresa');
		var noEquivale = recSelectDetProp[0].get('equivale');
		for (i=0; i<recSelectDetProp.length; i++) {
			
			if(noEmpresaTmp != recSelectDetProp[i].get('noEmpresa') && NS.modifPag == true){
				Ext.Msg.alert('SET','Para modificar detalles es necesario seleccionar registros de una misma empresa.');
				return;
			}
			
			if(noEquivale != recSelectDetProp[i].get('equivale') && NS.modifBenef == true){
				Ext.Msg.alert('SET','Para modificar detalles es necesario seleccionar registros de una mismo proveedor.');
				return;
			}
			
		}
		
		NS.tipoCambio = parseFloat(Ext.getCmp(PF + 'txtTipoCambio').getValue());
		var actualizaBenef = false;
		if(NS.cmbBancoPagBenef.getValue() != '' && NS.cmbChequeraPagBenef.getValue() != ''){
			if(isNaN(NS.tipoCambio)) {
				NS.tipoCambio = 0.0;
			}
			
			actualizaBenef = true;
			
			var noProvTmp = recSelectDetProp[0].get('equivale');
			for (i=0; i<recSelectDetProp.length; i++) {
				if(noProvTmp != recSelectDetProp[i].get('equivale') && NS.modifBenef == true){
					Ext.Msg.alert('SET','Para modificar chequera beneficiaria es necesario seleccionar registros de un mismo proveedor.');
					return;
				}
			}
		}  
		if(recSelectDetProp.length==1 && !actualizaBenef){
			//console.log("forma "+Ext.getCmp(PF+'cmbFormaPago').getValue());
			if(Ext.getCmp(PF+'cmbFormaPago').getValue()==3 || Ext.getCmp(PF+'cmbFormaPago').getValue()==9){
				if(Ext.getCmp(PF + 'txtReferencia1').getValue()=='' && Ext.getCmp(PF + 'txtReferencia2').getValue()==''
					&& Ext.getCmp(PF + 'txtReferencia3').getValue()==''){
					Ext.Msg.alert('SET','Debe ingresar las referencias');
					return;
				}
			}
			
		}
			NS.divTmp = Ext.getCmp(PF + 'txtDivisaPago').getValue();
			if(Ext.getCmp(PF+'cmbFormaPago').getValue()>0){
				NS.idPago=Ext.getCmp(PF+'cmbFormaPago').getValue();
			}else{
				NS.idPago=NS.idPago;
			}
			
			console.log("detalles "+NS.jsonDetPropuestas);
		ConsultaPropuestasAction.aceptarModificar(NS.jsonPropuestas, NS.jsonDetPropuestas, '' + NS.idPago,
				 '' + Ext.getCmp(PF + 'txtBanco').getValue(), Ext.getCmp(PF + 'cmbChequeraPag').getValue(),
				 '' + Ext.getCmp(PF + 'txtBancoBenef').getValue(), '' + Ext.getCmp(PF + 'cmbChequeraPagBenef').getValue(),
				actualizaBenef, apps.SET.FEC_HOY, '' + NS.divTmp, NS.tipoCambio, ''+NS.folios, '' + NS.campoRef.trim(), 
				'' + Ext.getCmp(PF + 'txtReferencia1').getValue(),'' + Ext.getCmp(PF + 'txtReferencia2').getValue(),
				'' + Ext.getCmp(PF + 'txtReferencia3').getValue(),function(resultado, e) {
		
			if(e.message != undefined && e.message != ""){
				Ext.Msg.alert('SET','Ocurrio el siguiente error [' + e.message + ']. Contacte a su administrador.');
				return;
			}
			
			if (resultado != '' ) {
				Ext.Msg.alert('SET', resultado + '!!');
				NS.cancelar();
				return;
			}
			
			Ext.Msg.alert('SET', 'Datos modificados correctamente');
			
			var concatBit='';
			var tmp = Ext.getCmp(PF+'cmbFormaPago').getValue();
			
			if(tmp == '1'){
				concatBit = 'FP:' + 'CHEQUE';
			}else if(tmp == '3'){
				concatBit = 'FP:' + 'TRANSFERENCIA';
			}else if(tmp == '5'){
				concatBit = 'FP:' + 'CARGO EN CUENTA';
			}else if(tmp == '10'){
				concatBit = 'FP:' + 'CHEQUE DE CAJA';
			}
			
			concatBit += ',B:' + Ext.getCmp(PF + 'txtBanco').getValue();
			concatBit += ',C:' + Ext.getCmp(PF + 'cmbChequeraPag').getValue();
			concatBit += ',BB:' + Ext.getCmp(PF + 'txtBancoBenef').getValue();
			concatBit += ',CB:' + Ext.getCmp(PF + 'cmbChequeraPagBenef').getValue();
				
			ConsultaPropuestasAction.insertaBitacoraPropuesta('ModDet', NS.jsonPropuestas, 
					NS.jsonDetPropuestas, concatBit, function(result, e){
				
				if(e.message=="Unable to connect to the server."){
					Ext.Msg.alert('SET','Error de conexión al servidor');
					return;
				}
				
				if(result != null && result.length != '') {
					if(result.error != ''){
						Ext.Msg.alert('SET',''+result.error);
					}
				}
			});
			
			var selec = NS.gridMaestroPropuestas.getSelectionModel().getSelections();
			if(selec.length >0){
				NS.indxMaestro = NS.gridMaestroPropuestas.getSelectionModel().getSelections()[0].get('cveControl');
			}else{
				Ext.Msg.alert('SET','No hay propuestas seleccionadas.');
				return;
			}
			
			NS.cancelar();
			/*NS.gridDetalle.store.removeAll();
			NS.gridDetalle.getView().refresh();
			NS.storeDetalle.load();
			*/
			NS.buscar();
		});

	};
	
	function threadGeneraReportes(maxProp) {
		
		var recordsAutorizar = NS.gridMaestroPropuestas.getSelectionModel().getSelections();
		
		if(contReporte >= maxProp){
			pararThreadGeneraReportes();
			return;
		}
		
		//var recSelectDetProp = NS.storeDetalle.data.items;
		var strParams = "";
		strParams = '?nomReporte=reporteDetalleCupos';
		strParams += '&'+'nomParam1=cveControl';
		strParams += '&'+'valParam1=' + recordsAutorizar[contReporte].get('cveControl');
		strParams += '&'+'nomParam2=concepto';
		strParams += '&'+'valParam2=' + recordsAutorizar[contReporte].get('concepto');
		strParams += '&'+'nomParam3=fecPago';
		strParams += '&'+'valParam3=' + cambiarFecha(''+recordsAutorizar[contReporte].get('fechaPropuesta'));
		strParams += '&'+'nomParam4=nivel1';
		
		if(parseInt(recordsAutorizar[contReporte].get('nivelAutorizacion')) >= 1)
			strParams += '&'+'valParam4=' + recordsAutorizar[contReporte].get('nivelAutorizacion');
		else
			strParams += '&'+'valParam4=' + '';
		
		strParams += '&'+'nomParam5=nivel2';
		
		if(parseInt(recordsAutorizar[contReporte].get('nivelAutorizacion')) >= 2)
			strParams += '&'+'valParam5=' + '';//recordsAutorizar[0].get('nivelAutorizacion');
		else
			strParams += '&'+'valParam5=' + '';
		
		strParams += '&'+'nomParam6=nivel3';
		
		if(parseInt(recordsAutorizar[contReporte].get('nivelAutorizacion')) == 3)
			strParams += '&'+'valParam6=' + '';//recordsAutorizar[0].get('nivelAutorizacion');
		else
			strParams += '&'+'valParam6=' + '';
		
		strParams += '&'+'nomParam7=autoriza1';
		strParams += '&'+'valParam7=' + NS.usr1;
		strParams += '&'+'nomParam8=autoriza2';
		strParams += '&'+'valParam8=' + NS.usr2;
		strParams += '&'+'nomParam9=autoriza3';
		strParams += '&'+'valParam9=' + '';
		strParams += '&'+'nomParam10=idGrupoEmpresa';
		strParams += '&'+'valParam10=' + recordsAutorizar[contReporte].get('idGrupoFlujo');
		strParams += '&'+'nomParam11=idGrupoRubro';
		strParams += '&'+'valParam11=' + recordsAutorizar[contReporte].get('idGrupo');
		strParams += '&'+'nomParam12=cveControl';
		strParams += '&'+'valParam12=' + recordsAutorizar[contReporte].get('cveControl');
		strParams += '&'+'nomParam13=idUsuario1';
		strParams += '&'+'valParam13=' + recordsAutorizar[contReporte].get('usuarioUno');
		strParams += '&'+'nomParam14=idUsuario2';
		strParams += '&'+'valParam14=' + recordsAutorizar[contReporte].get('usuarioDos');
		strParams += '&'+'nomParam15=idUsuario3';
		strParams += '&'+'valParam15=' + recordsAutorizar[contReporte].get('usuarioTres');
		
		
		/*
		//var recSelectDetProp = NS.storeDetalle.data.items;
		var strParams = "";
		strParams = '?nomReporte=reporteDetalleCupos';
		strParams += '&'+'nomParam1=cveControl';
		strParams += '&'+'valParam1=' + recordsAutorizar[contReporte].get('cveControl');
		//strParams += '&'+'nomParam2=nomEmpresa';
		//strParams += '&'+'valParam2=' + recSelectDetProp[contReporte].get('nomEmpresa');
		//strParams += '&'+'nomParam3=descBanco';
		//strParams += '&'+'valParam3=' + recSelectDetProp[contReporte].get('bancoPago');
		strParams += '&'+'nomParam4=concepto';
		strParams += '&'+'valParam4=' + recordsAutorizar[contReporte].get('concepto');
		//strParams += '&'+'nomParam5=idChequeraPago';
		//strParams += '&'+'valParam5=' + recSelectDetProp[contReporte].get('idChequera');
		strParams += '&'+'nomParam6=fecPago';
		strParams += '&'+'valParam6=' + recordsAutorizar[contReporte].get('fechaPropuesta');
		strParams += '&'+'nomParam7=nivel1';
		
		if(parseInt(recordsAutorizar[contReporte].get('nivelAutorizacion')) >= 1)
			strParams += '&'+'valParam7=' + recordsAutorizar[contReporte].get('nivelAutorizacion');
		else
			strParams += '&'+'valParam7=' + '';
		
		strParams += '&'+'nomParam8=nivel2';
		
		if(parseInt(recordsAutorizar[contReporte].get('nivelAutorizacion')) >= 2)
			strParams += '&'+'valParam8=' + '';//recordsAutorizar[0].get('nivelAutorizacion');
		else
			strParams += '&'+'valParam8=' + '';
		
		strParams += '&'+'nomParam9=nivel3';
		
		if(parseInt(recordsAutorizar[contReporte].get('nivelAutorizacion')) == 3)
			strParams += '&'+'valParam9=' + '';//recordsAutorizar[0].get('nivelAutorizacion');
		else
			strParams += '&'+'valParam9=' + '';
		
		strParams += '&'+'nomParam10=autoriza1';
		strParams += '&'+'valParam10=' + NS.usr1;
		strParams += '&'+'nomParam11=autoriza2';
		strParams += '&'+'valParam11=' + NS.usr2;
		strParams += '&'+'nomParam12=autoriza3';
		strParams += '&'+'valParam12=' + '';
		strParams += '&'+'nomParam13=idGrupoEmpresa';
		strParams += '&'+'valParam13=' + recordsAutorizar[contReporte].get('idGrupoFlujo');
		strParams += '&'+'nomParam14=idGrupoRubro';
		strParams += '&'+'valParam14=' + recordsAutorizar[contReporte].get('idGrupo');
		strParams += '&'+'nomParam15=cveControl';
		strParams += '&'+'valParam15=' + recordsAutorizar[contReporte].get('cveControl');
		strParams += '&'+'nomParam16=idUsuario1';
		strParams += '&'+'valParam16=' + recordsAutorizar[contReporte].get('usuarioUno');
		strParams += '&'+'nomParam17=idUsuario2';
		strParams += '&'+'valParam17=' + recordsAutorizar[contReporte].get('usuarioDos');
		strParams += '&'+'nomParam18=idUsuario3';
		strParams += '&'+'valParam18=' + recordsAutorizar[contReporte].get('usuarioTres');
		*/
		
		window.open("/SET/jsp/bfrmwrk/gnrators/genJasperPDF.jsp" + strParams);
		
		contReporte++;
	}

	function pararThreadGeneraReportes() {
	    clearInterval(threadReportes);
	}
	
	NS.imprimir = function() {
		
		var recordsAutorizar = NS.gridMaestroPropuestas.getSelectionModel().getSelections();
		
		if(recordsAutorizar.length == 0) {
			Ext.Msg.alert('SET', 'Es necesario seleccionar algun registro');
			return;
		}
		
		contReporte = 0;
		
		threadReportes = setInterval(function(){ threadGeneraReportes(recordsAutorizar.length) }, 1500);
		
	};
	
	NS.buscarNombre = function() {
		var recordsAutorizar = NS.gridMaestroPropuestas.getSelectionModel().getSelections();
		if(recordsAutorizar.length == 0) return;
		
		var recSelectDetProp = NS.storeDetalle.data.items;
		NS.usr1 = '' + recordsAutorizar[0].get('usuarioUno');
		NS.usr2 = '' + recordsAutorizar[0].get('usuarioDos');
		NS.usr3 = '' + recordsAutorizar[0].get('usuarioTres');
		
		ConsultaPropuestasAction.nombreUsuarios(NS.usr1, NS.usr2, NS.usr3, function(resultado, e) {
			
			if(e.message=="Unable to connect to the server."){
				Ext.Msg.alert('SET','Error de conexión al servidor');
				return;
			}
			
			if (resultado != '') {
				NS.usr1 = resultado.substring(0, resultado.indexOf(','));
				NS.usr2 = resultado.substring(resultado.indexOf(',') + 1, resultado.length - 1);
				
				//if(NS.usr2 != '') NS.usr2 = nombre2.substring(0, resultado.length);
				//NS.usr3 = resultado.get('usuario3');
			}else {
				NS.usr1 = '';
				NS.usr2 = '';
				NS.usr3 = '';
			}
		});
	};
	
	NS.validaDatosExcel = function() {
		
		var recSelectProp = NS.gridMaestroPropuestas.getSelectionModel().getSelections();

		//Primero valida si se seleccionaron propuestas para el excel, sino toma todas
		if(recSelectProp.length == 0) {
			
			recSelectProp = NS.storeMaestro.data.items;
			
			if(recSelectProp.length == 0) {
				Ext.Msg.alert('SET','No existen propuestas para el reporte!!');
				return;
			}
		}
		
		if(NS.Excel) {
			
			var matriz = new Array();
			
			for(var i=0;i<recSelectProp.length;i++){
				var vector = {};
				
				vector.fechaElaboracion = cambiarFecha(''+recSelectProp[i].get('fecLimiteSelecc'));
				vector.fechaPago = cambiarFecha(''+recSelectProp[i].get('fechaPropuesta'));
				vector.sociedad = recSelectProp[i].get('idGrupoFlujo');
				vector.nombreSociedad = recSelectProp[i].get('descGrupoFlujo');
				vector.total = recSelectProp[i].get('cupoTotal');
				vector.moneda = recSelectProp[i].get('divisa');
				vector.concepto = recSelectProp[i].get('concepto');
				vector.viaPago = recSelectProp[i].get('viaPago');
				vector.idPropuesta = recSelectProp[i].get('cveControl');
				vector.origenPropuesta = recSelectProp[i].get('origenPropuesta');
				vector.usuario1 = recSelectProp[i].get('user1'); //usuario_uno, usuario_dos -> obtiene el numero de usuario
				vector.usuario2 = recSelectProp[i].get('user2');
				//vector.usuario3 = recSelectProp[i].get('user3');
				vector.idGrupoEmpresa = recSelectProp[i].get('idGrupoFlujo');
				vector.nombreRegla = recSelectProp[i].get('nombreRegla');
				
				matriz[i] = vector;
	 		
			}
			
			var jSonString = Ext.util.JSON.encode(matriz);
		}
		
		mascara.show(); //se quita la mascara cuando respo
		NS.exportaExcel(jSonString);
		NS.excel = false;
	};
	
	NS.validaDatosExcelPropuestas = function(){
		var recSelectProp = NS.gridMaestroPropuestas.getSelectionModel().getSelections();

		//Primero valida si se seleccionaron propuestas para el excel, sino toma todas
		if(recSelectProp.length == 0) {
			
			recSelectProp = NS.storeMaestro.data.items;
			
			if(recSelectProp.length == 0) {
				Ext.Msg.alert('SET','No existen propuestas para el reporte!!');
				return;
			}
		}
		
		if(NS.Excel) {
			
			var matriz = new Array();
			
			for(var i=0;i<recSelectProp.length;i++){
				var vector = {};
				
				vector.fechaElaboracion = cambiarFecha(''+recSelectProp[i].get('fecLimiteSelecc'));
				vector.fechaPago = cambiarFecha(''+recSelectProp[i].get('fechaPropuesta'));
				vector.nombreRegla = recSelectProp[i].get('nombreRegla');
				vector.sociedad = recSelectProp[i].get('idGrupoFlujo');
				vector.nombreSociedad = recSelectProp[i].get('descGrupoFlujo');
				vector.total = recSelectProp[i].get('cupoTotal');
				vector.moneda = recSelectProp[i].get('divisa');
				vector.concepto = recSelectProp[i].get('concepto');
				vector.viaPago = recSelectProp[i].get('viaPago');
				vector.idPropuesta = recSelectProp[i].get('cveControl');
				vector.origenPropuesta = recSelectProp[i].get('origenPropuesta');
				vector.usuario1 = recSelectProp[i].get('user1'); //usuario_uno, usuario_dos -> obtiene el numero de usuario
				vector.usuario2 = recSelectProp[i].get('user2');
				//vector.usuario3 = recSelectProp[i].get('user3');
				matriz[i] = vector;
	 		
			}
			
			var jSonString = Ext.util.JSON.encode(matriz);
		}
		
		mascara.show(); //se quita la mascara cuando respo
		NS.exportaExcelPropuestas(jSonString);
		NS.excel = false;
	}
	
	NS.validaDatosExcelPropDet = function() {
		
		var recSelectProp = NS.gridMaestroPropuestas.getSelectionModel().getSelections();

		//Primero valida si se seleccionaron propuestas para el excel, sino toma todas
		if(recSelectProp.length == 0) {
			
			recSelectProp = NS.storeMaestro.data.items;
			
			if(recSelectProp.length == 0) {
				Ext.Msg.alert('SET','No existen propuestas para el reporte!!');
				return;
			}
		}
		
		if(NS.Excel) {
			
			var matriz = new Array();
			
			for(var i=0;i<recSelectProp.length;i++){
				var vector = {};
				
				vector.fechaElaboracion = cambiarFecha(''+recSelectProp[i].get('fecLimiteSelecc'));
				vector.fechaPago = cambiarFecha(''+recSelectProp[i].get('fechaPropuesta'));
				vector.nombreRegla = recSelectProp[i].get('nombreRegla');
				vector.sociedad = recSelectProp[i].get('idGrupoFlujo');
				vector.nombreSociedad = recSelectProp[i].get('descGrupoFlujo');
				vector.total = recSelectProp[i].get('cupoTotal');
				vector.moneda = recSelectProp[i].get('divisa');
				vector.concepto = recSelectProp[i].get('concepto');
				vector.viaPago = recSelectProp[i].get('viaPago');
				vector.idPropuesta = recSelectProp[i].get('cveControl');
				vector.origenPropuesta = recSelectProp[i].get('origenPropuesta');
				vector.usuario1 = recSelectProp[i].get('user1'); //usuario_uno, usuario_dos -> obtiene el numero de usuario
				vector.usuario2 = recSelectProp[i].get('user2');
				vector.usuario3 = recSelectProp[i].get('user3');
				vector.idGrupoEmpresa = recSelectProp[i].get('idGrupoFlujo');
				
				matriz[i] = vector;
	 		
			}
			
			var jSonString = Ext.util.JSON.encode(matriz);
		}
		
		mascara.show(); //se quita la mascara cuando respo
		NS.exportaExcelPropDet(jSonString);
		NS.excel = false;
	};
	
	NS.validaDatosExcelDetalles = function() {
		
		var recSelectProp = NS.gridDetalle.getSelectionModel().getSelections();
		
		//Primero valida si se seleccionaron propuestas para el excel, sino toma todas
		if(recSelectProp.length == 0) {
			
			recSelectProp = NS.gridDetalle.store.data.items;
			
			if(recSelectProp.length == 0) {
				Ext.Msg.alert('SET','No existen detalles de propuestas para el reporte!!');
				return;
			}
		}
		
		var matriz = new Array();
		
		for(var i=0;i<recSelectProp.length;i++){
			var vector = {};
			
			vector.noEmpresa = recSelectProp[i].get('noEmpresa');
			vector.equivale = recSelectProp[i].get('equivale');
			vector.beneficiario = recSelectProp[i].get('beneficiario');
			vector.noFactura = recSelectProp[i].get('noFactura');
			vector.noDocto = recSelectProp[i].get('noDocto');
			vector.idContable = recSelectProp[i].get('idContable');
			vector.importe = recSelectProp[i].get('importe');
			vector.descFormaPago = recSelectProp[i].get('descFormaPago');
			vector.concepto = recSelectProp[i].get('concepto');
			vector.fecPropuesta = cambiarFecha(''+recSelectProp[i].get('fecPropuestaStr'));
			vector.fecValor = cambiarFecha(''+recSelectProp[i].get('fecValorStr')); 
			vector.fecContabilizacion = cambiarFecha(''+recSelectProp[i].get('fecContabilizacionStr'));
			vector.fecOperacion = cambiarFecha(''+recSelectProp[i].get('fecOperacionStr'));
			vector.bancoPago = recSelectProp[i].get('bancoPago');
			vector.idChequera = recSelectProp[i].get('idChequera');
			vector.idBancoBenef = recSelectProp[i].get('idBancoBenef');
			vector.idChequeraBenef = recSelectProp[i].get('idChequeraBenef');
			
			matriz[i] = vector;			
 		
		}
		
		var jSonString = Ext.util.JSON.encode(matriz);
		
		mascara.show(); //se quita la mascara cuando respo
		//NS.exportaExcelDetalles(jSonString);
		NS.exportaExcelDetallesC();
		
	};
	
	NS.exportaExcel = function(jsonCadena) {
		ConsultaPropuestasAction.exportaExcel(jsonCadena, function(res, e){
			if(e.message=="Unable to connect to the server."){
				Ext.Msg.alert('SET','Error de conexión al servidor');
				return;
			}
			
			if (res != null && res != undefined && res == "") {
				Ext.Msg.alert('SET', "Error al generar el archivo");
			}else{
				strParams = '?nomReporte=ResumenPropuestasDetalles';
				strParams += '&'+'nomParam1=nomArchivo';
				strParams += '&'+'valParam1='+res;
				window.open("/SET/jsp/bfrmwrk/gnrators/abreExcel.jsp"+strParams);
			}
			mascara.hide();
		});
	};
	
	NS.exportaExcelPropuestas = function(jsonCadena) {
		ConsultaPropuestasAction.exportaExcelPropuestas(jsonCadena, function(res, e){
			if(e.message=="Unable to connect to the server."){
				Ext.Msg.alert('SET','Error de conexión al servidor');
				return;
			}
			
			if (res != null && res != undefined && res == "") {
				Ext.Msg.alert('SET', "Error al generar el archivo");
			} else {
				strParams = '?nomReporte=ResumenPropuestas';
				strParams += '&'+'nomParam1=nomArchivo';
				strParams += '&'+'valParam1='+res;
				window.open("/SET/jsp/bfrmwrk/gnrators/abreExcel.jsp"+strParams);
			}
			mascara.hide();
		});
	};
	
	NS.exportaExcelPropDet = function(jsonCadena) {
		ConsultaPropuestasAction.exportaExcelPropDet(jsonCadena, function(res, e){
			if(e.message=="Unable to connect to the server."){
				Ext.Msg.alert('SET','Error de conexión al servidor');
				return;
			}
			
			if (res != null && res != undefined && res == "") {
				Ext.Msg.alert('SET', "Error al generar el archivo");
			} else {
				strParams = '?nomReporte=ResumenPropDet';
				strParams += '&'+'nomParam1=nomArchivo';
				strParams += '&'+'valParam1='+res;
				window.open("/SET/jsp/bfrmwrk/gnrators/abreExcel.jsp"+strParams);
			}
			mascara.hide();
		});
	};
	/*NS.exportaExcelDetalles = function(jsonCadena) {
		
		ConsultaPropuestasAction.exportaExcelDetalles(jsonCadena, function(res, e){
			if(e.message=="Unable to connect to the server."){
				Ext.Msg.alert('SET','Error de conexión al servidor');
				return;
			}
			
			if(res != null && res != undefined && res == "") {
				Ext.Msg.alert('SET', "Error al generar el archivo");
			} else {
				strParams = '?nomReporte=ExcelDetalles';
				strParams += '&'+'nomParam1=nomArchivo';
				strParams += '&'+'valParam1='+res;
				window.open("/SET/jsp/bfrmwrk/gnrators/abreExcel.jsp"+strParams);
			}
			mascara.hide();
		});
	};*/
	
	NS.exportaExcelDetallesC = function() {
		
		ConsultaPropuestasAction.exportaExcelDetallesC(function(res, e){
			if(e.message=="Unable to connect to the server."){
				Ext.Msg.alert('SET','Error de conexión al servidor');
				return;
			}
			
			if(res != null && res != undefined && res == "") {
				Ext.Msg.alert('SET', "Error al generar el archivo");
			} else {
				strParams = '?nomReporte=ExcelDetalles';
				strParams += '&'+'nomParam1=idUsu';
				strParams += '&'+'valParam1='+res;
				window.open("/SET/jsp/bfrmwrk/gnrators/abreExcel.jsp"+strParams);
			}
			mascara.hide();
		});
		
		/*strParams = '?nomReporte=ExcelDetalles';
		//strParams += '&'+'nomParam1=nomArchivo';
		//strParams += '&'+'valParam1='+res;
		window.open("/SET/jsp/bfrmwrk/gnrators/abreExcel.jsp"+strParams);
		mascara.hide();
		*/
	};
	
	NS.buscar = function() {	
		
		if((Ext.getCmp(PF+'fecha1').getValue()==="") && (Ext.getCmp(PF+'fecha2').getValue()==="")) {
			BFwrk.Util.msgShow('Debe establecer el rango de fechas','INFO');
			return;
		}
		NS.gridDetalle.store.removeAll();
		NS.gridDetalle.getView().refresh();
		Ext.getCmp(PF+'totalMN').setValue('0.00');
		Ext.getCmp(PF+'sumMN').setValue('0.00');
		Ext.getCmp(PF+'totalDLS').setValue('0.00');
			
		//NS.storeMaestro.baseParams.idProv = parseInt(Ext.getCmp(PF + 'txtIdProveedor').getValue() != '' ? Ext.getCmp(PF + 'txtIdProveedor').getValue() : 0);
		NS.storeMaestro.baseParams.idProv = Ext.getCmp(PF + 'txtIdProveedor').getValue() != '' ? Ext.getCmp(PF + 'txtIdProveedor').getValue() : '';
		//NS.storeMaestro.baseParams.idGrupoRubro = parseInt(Ext.getCmp(PF + 'txtIdGrupo').getValue() != '' ? Ext.getCmp(PF + 'txtIdGrupo').getValue() : 0);
		NS.storeMaestro.baseParams.fecIni = cambiarFecha('' + Ext.getCmp(PF+'fecha1').getValue());
		NS.storeMaestro.baseParams.fecFin = cambiarFecha('' + Ext.getCmp(PF+'fecha2').getValue());
		
		if(NS.txtIdEmpresa.getValue()==''){
			NS.storeMaestro.baseParams.idGrupoEmpresa = 0;
		}else{
			NS.storeMaestro.baseParams.idGrupoEmpresa = parseInt(NS.txtIdEmpresa.getValue());
		}
		
		if(NS.cmbReglaNegocios.getValue() == ''){
			
			if(NS.cmbReglaNegocios.getRawValue() == '***** TODAS *****'){
				NS.storeMaestro.baseParams.reglaNegocio = '***** TODAS *****';
			}else{
				NS.storeMaestro.baseParams.reglaNegocio = '';
			}
			
		}else{
			NS.storeMaestro.baseParams.reglaNegocio = NS.descReglaNegocio;
		}
		
		NS.storeMaestro.baseParams.nominaRH = NS.nominaRH;
		NS.storeMaestro.baseParams.nominaTes = NS.nominaTes;
		
		//mascara.show(); //se quita la mascara cuando termine de cargar el store
		var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeMaestro, msg:"Cargando..."});
		NS.storeMaestro.load();
		
	};
	
	NS.cancelar = function() {
		
		NS.noPersona = '';
		habilitarBotones(true);	
		NS.modifPag = false;
		NS.modifBenef = false;
		//mascara.hide();
		
		NS.cmbFormaPago.reset();
		//Ext.getCmp(PF + 'fecPago').setValue('');
		Ext.getCmp(PF + 'txtBanco').setValue('');
		NS.cmbBancoPag.reset();
		Ext.getCmp(PF + 'txtDivisaPago').setValue('');
		NS.cmbDivisaCambio.reset();
		NS.cmbChequeraPag.reset();
		NS.storeChequeraPag.removeAll();
		Ext.getCmp(PF + 'txtBancoBenef').setValue('');
		NS.cmbBancoPagBenef.reset();
		NS.storeChequeraPagBenef.removeAll();
		NS.cmbChequeraPagBenef.reset();
   		Ext.getCmp(PF + 'contFormaPago').setDisabled(true);
   		Ext.getCmp(PF + 'txtReferencia1').setValue('');
   		Ext.getCmp(PF + 'txtReferencia2').setValue('');
   		Ext.getCmp(PF + 'txtReferencia3').setValue('');
   		NS.limpiaCamposMultisociedad();
   		
   		NS.gridDetalle.setDisabled(false);
	};
	
	NS.funAutoDesAutorisa = function() {
		var recSelectDetProp = NS.gridDetalle.getSelectionModel().getSelections();
		var recSelProp = NS.gridMaestroPropuestas.getSelectionModel().getSelections();
		//var recSelectPropTodo = NS.storeDetalle.data.items;
		var gridDetPropuestas = new Array();
		var recSelectProp;
		
		if(recSelectDetProp.length == 0 || recSelProp.length == 0) {
			//Ext.Msg.alert('SET','Seleccione un registro para autorizar o quitar la autorización!!');
			return '';
		}
		//if((recSelProp[0].get('usuarioDos') == apps.SET.iUserId && NS.autoDesauto == 'DAUT') ||
		//		(recSelProp[0].get('user1') != '' && NS.autoDesauto == 'AUT' && apps.SET.iUserId != recSelProp[0].get('usuarioUno')))
		//	recSelectProp = recSelectPropTodo;
		//else
		recSelectProp = recSelectDetProp;
		
		for(var i=0; i<recSelectProp.length; i++) {
			var regDetPropuestas = {};
			regDetPropuestas.noFolioDet = recSelectProp[i].get('noFolioDet');
			gridDetPropuestas[i] = regDetPropuestas;
			
		}
		return Ext.util.JSON.encode(gridDetPropuestas);
	};
	
	//Ventana para modificar la fecha de una propuesta.
	var winModif = new Ext.Window({
		title: 'Modificar fecha propuesta'
		,modal:true
		,shadow:true
		,width: 200
	   	,height:200
	   	,minWidth: 400
	   	,minHeight: 580
	   	,layout: 'fit'
	   	,plain:true
	    ,bodyStyle:'padding:10px;'
	   	,buttonAlign:'center'
	   	,closable: false
	   	,items: [
	   	{
	   	xtype: 'fieldset',
        title: 'Nueva Fecha',
        id:PF+'frameFecha',
        name:PF+'frameFecha',
       	x: 0,
        y: 0,
        width: 80,
        height: 100,
        layout: 'absolute',
        items: [
	   		{
	   			xtype:'datefield',
        		format:'d/m/Y',
        		id: PF + 'fecModificacion',
        		name: PF + 'fecModificacion',
        		value: '',
	       		x:20,
	       		y:10,
	       		width:100,
	       		hidden:false,
	       		allowBlank: false,
	       		blankText:'Fecha requerida',
	       		listeners:{
	            	change:{
	            		fn:function(caja, valor){
	            			
	    				}
	    			}
	    		}
	   		}
		]
        }
		]
	   	,buttons: [
		   	{
			 	text: 'Aceptar',
				handler: function(e) {
					var matrizModificarProp = new Array();
					var recordsAutorizar = NS.gridMaestroPropuestas.getSelectionModel().getSelections();
					NS.fecModGlob = cambiarFecha('' + Ext.getCmp(PF+'fecModificacion').getValue());
					
					var pideAutorizacion = false;
					var cont = 0;
					for(var inc=0; inc<recordsAutorizar.length; inc++) {
						var regAutorizar = {};
						var fecPago = cambiarFecha(''+recordsAutorizar[inc].get('fechaPropuesta')).substring(0,10);
						var respFec = compararFechas(NS.fecModGlob, fecPago);
						
						//En caso de varios registros solo se actualizaran si no tiene ninguna autorización.
						if(recordsAutorizar[inc].get('user1') == null || recordsAutorizar[inc].get('user1') == "" ){
							if(respFec == "Menor"){
								pideAutorizacion=true;
							}
							regAutorizar.cveControl=recordsAutorizar[inc].get('cveControl');
							//++++ solo se utliza para insertar en la bitacora.	+++++
							regAutorizar.fechaProp = cambiarFecha(''+recordsAutorizar[inc].get('fechaPropuesta'));
							//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++//
		    				matrizModificarProp[cont]=regAutorizar;
		    				cont++;
						}
						
	    			}
					
					if(pideAutorizacion){
						NS.autorizarModificacion = true
						winLogin.show();
						//y se pasa la matriz de datos a actualizar, entonces solo se actualiza. 
					}else{
						
						NS.jsonPropuestas = Ext.util.JSON.encode(matrizModificarProp);
						
						ConsultaPropuestasAction.updateFecProp(NS.jsonPropuestas, NS.fecModGlob, function(resultado, e) {
							
							if(e.message=="Unable to connect to the server."){
								Ext.Msg.alert('SET','Error de conexión al servidor');
								return;
							}
							
							if (resultado != '' && resultado != undefined) {
								Ext.Msg.alert('SET', "Se actualizaron " + resultado + " propuestas");
								
								habilitarBotones(true);
								//mascara.hide();
								
								ConsultaPropuestasAction.insertaBitacoraPropuesta('ModProp', NS.jsonPropuestas, 
										'', NS.fecModGlob , function(result, e){
									
									if(e.message=="Unable to connect to the server."){
										Ext.Msg.alert('SET','Error de conexión al servidor');
										return;
									}
									
									if(result != null && result.length != '') {
										if(result.error != ''){
											Ext.Msg.alert('SET',''+result.error);
										}
									}
								});
								
								NS.buscar();
								return;
							}
						});
						
					}
					
					habilitarBotones(true);
					//mascara.hide();
		   	
					winModif.hide();
				 	Ext.getCmp(PF+'fecModificacion').setValue("");
				 }
		     },{
			 	text: 'Cancelar',
				handler: function(e) {
					habilitarBotones(true);
					//mascara.hide();
					
					winModif.hide();
				 }
		     }
	 	]
	});
	
	
	//función que compara si una fecha es mayor a otra con formato d/m/Y: EMS 21/09/15
	function compararFechas(fecha1, fecha2){
		var fechaArray1=fecha1.split("/");
		var fechaArray2=fecha2.split("/");
	
		for(var i=fechaArray1.length; i>=0; i--){
			//Comienzo por años, luego meses al final dias para saber si la fecha es mayor
			//años
			if(i==2){
				if(parseInt(fechaArray1[i]) < parseInt(fechaArray2[i])){
					return "Menor"
				}else if(parseInt(fechaArray1[i]) > parseInt(fechaArray2[i])){
					return "Mayor";
				}
			}
			//meses
			if(i==1){
				if(parseInt(fechaArray1[i]) < parseInt(fechaArray2[i])){
					return "Menor"
				}else if(parseInt(fechaArray1[i]) > parseInt(fechaArray2[i])){
					return "Mayor";
				}
			}
			//dias
			if(i==0){
				if(parseInt(fechaArray1[i]) < parseInt(fechaArray2[i])){
					return "Menor"
				}else if(parseInt(fechaArray1[i]) > parseInt(fechaArray2[i])){
					return "Mayor";
				}else{
					return "Iguales";
				}
			}
		}		
		
		return "";		
	}
	
	
	NS.fechaInicial = function(fechaIni) {
		var fec = BFwrk.Util.sumDate(fechaIni, 'dia', -90);
		return BFwrk.Util.changeStringToDate(fec);
	};
	
	
	//******************************************************** Este codigo es para el detalle de los pagos a proveedores *****************************************//
	NS.labFechas = new Ext.form.Label({
		text: 'Rango de fechas:',
		x: 10
    });
	
	//Fecha inicial
	NS.txtFecIni = new Ext.form.DateField({
		id: PF + 'txtFecIni',
		name: PF + 'txtFecIni',
		x: 10,
		y: 15,
		width: 110,
		format: 'd/m/Y',
		value : NS.cambiFecha(apps.SET.FEC_HOY, -90),
		allowBlank: false,
   		blankText:'Fecha inicial requerida',
   		listeners:{
        	change:{
        		fn:function(caja, valor){
        			var fechaFin = Ext.getCmp(PF + 'txtFecFin').getValue();
        			
        			if(fechaFin < caja.getValue()){
        				Ext.Msg.alert('SET', 'La fecha inicial no puede ser mayor a la fecha final!!');
        				Ext.getCmp(PF + 'txtFecFin').setValue('');
        			}
				}
			}
		}
	});
	//Fecha Final
	NS.txtFecFin = new Ext.form.DateField({
		id: PF + 'txtFecFin',
		name: PF + 'txtFecFin',
		x: 125,
		y: 15,
		width: 110,
		format: 'd/m/Y',
		value: apps.SET.FEC_HOY,
		allowBlank: false,
   		blankText:'Fecha final requerida',
   		listeners:{
        	change:{
        		fn:function(caja, valor){
        			var fechaIni = Ext.getCmp(PF + 'txtFecIni').getValue();
        			
        			if(fechaIni > caja.getValue()){
        				Ext.Msg.alert('SET', 'La fecha inicial no puede ser mayor');
        				Ext.getCmp(PF + 'txtFecIni').setValue('');
        			}
				}
			}
		}
	});
	
	NS.labTotalFac = new Ext.form.Label({
		text: 'Total Facturas:',
		x: 10,
		y: 340
    });
	
	NS.txtTotalFac = new Ext.form.TextField({
        id: PF + 'txtTotalFac',
        name: PF + 'txtTotalFac',
        value: '0.00',
        x: 10,
        y: 355,
        width: 100,
        disabled: true
    	
    });
	
	//Store detalle de los pagos
	NS.storeDetaPro = new Ext.data.DirectStore({
		paramsAsHash: false,
		baseParams: {noCliente: 0, fecIni: '', fecFin: ''},
		root: '',
		paramOrder:['noCliente','fecIni','fecFin'],
		directFn: ConsultaPropuestasAction.consultarDetPagos, 
		fields: [
		        {name: 'fecValor'},
		        {name: 'noFactura'},
		        {name: 'concepto'},
				{name: 'importe'},
				{name: 'idDivisa'},
				{name: 'descFormaPago'},
				{name: 'noCheque'},
				{name: 'bancoPago'},
				{name: 'idChequeraPago'}
		],
		listeners: {
			load: function(s, records) {
				var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeDetaPro, msg: "Buscando..."});
				var importe = 0;
				
				if(records.length == null || records.length <= 0)
				{
					Ext.Msg.alert('SET', 'No hay detalle de pagos, para este proveedor seleccionado!!');
				}
				else
				{
					for(var i=0; i<records.length; i++)
						importe += records[i].get('importe');
					
					Ext.getCmp(PF + 'txtTotalFac').setValue(NS.formatNumber(Math.round((importe)*100)/100 ));
				}
			},
			exception: function(mist){
				Ext.Msg.alert('SET','Error en conexión al servidor');
				mascara.hide();
			}
		}
	});
	
	//Columna de Seleccion  
	NS.columDeta = new Ext.grid.ColumnModel([
	    {header :'Fecha Pago', width :90, sortable :true, dataIndex :'fecValor'},
	    {header :'No.Factura', width :80, sortable :true, dataIndex :'noFactura'},
	    {header :'Concepto', width :200, sortable :true, dataIndex :'concepto'},
	    {header :'Importe', width :90, sortable :true, dataIndex :'importe', renderer: BFwrk.Util.rendererMoney},
	    {header :'Divisa', width :60, sortable :true, dataIndex :'idDivisa'},
	    {header :'Forma Pago', width :100, sortable :true, dataIndex :'descFormaPago'},
	    {header :'No. Cheque', width :80, sortable :true, dataIndex :'noCheque'},
	    {header :'Banco Pago', width :100, sortable :true, dataIndex :'bancoPago'},
	    {header :'Chequera Pago', width :100, sortable :true, dataIndex :'idChequeraPago'}
	]);
	
	//Grid para mostrar los datos seleccionados
	NS.gridDetaProp = new Ext.grid.GridPanel({
		store: NS.storeDetaPro,
		id: 'gridDetaProp',
		x: 10,
		y: 50,
		cm: NS.columDeta,
		width: 680,
	    height: 280,
	    stripeRows: true,
	    columnLines: true,
	    title: ''
	});
	
	//Contenedor de detalle de las propuestas
	NS.contDetalle = new Ext.form.FieldSet({
		title: '',
		layout: 'absolute',
	    items: [
	           NS.labFechas,
	           NS.txtFecIni,
	           NS.txtFecFin, 
	           NS.gridDetaProp,
	           NS.labTotalFac,
	           NS.txtTotalFac,
	           {
	        	    xtype: 'button',
	        	   	text: 'Buscar',
	        	   	x: 580,
					y: 15,
					width: 80,
					height: 22,
					listeners: {
						click:{
							fn:function(e){
	        	   				var regSelec = NS.gridDetalle.getSelectionModel().getSelections();
	        	   				
	        	   				NS.storeDetaPro.baseParams.noCliente = parseInt(regSelec[0].get('noCliente'));
				          		NS.storeDetaPro.baseParams.fecIni = Ext.getCmp(PF + 'txtFecIni').getValue();
				          		NS.storeDetaPro.baseParams.fecFin = Ext.getCmp(PF + 'txtFecFin').getValue();
				          		NS.storeDetaPro.load();
							}
						}
					}
	           },{
	        	    xtype: 'button',
	        	   	text: 'Cerrar',
	        	   	x: 580,
					y: 345,
					width: 80,
					height: 22,
					listeners: {
						click:{
							fn:function(e){
				        	   	winDetPagos.hide();
							}
						}
					}
	           }
	           ]
	});
	//ventana para el detalle de las propuestas a pagar
	var winDetPagos = new Ext.Window({
		title: 'Detalle de Facturas Pagadas',
		modal: true,
		shadow: true,
		closable: false,
		width: 750,
	   	height: 450,
	   	layout: 'fit',
	   	plain: true,
	    bodyStyle: 'padding:10px;',
	    buttonAlign: 'center',
	   	items: [
	   	        NS.contDetalle
	   	        ]
	});//Termina ventana
	
	NS.limpiar = function() {
		Ext.getCmp(PF + 'txtFecIni').setValue(apps.SET.FEC_HOY);
		Ext.getCmp(PF + 'txtFecFin').setValue(apps.SET.FEC_HOY);
		NS.gridDetaProp.store.removeAll();
		NS.gridDetaProp.getView().refresh();
		Ext.getCmp(PF + 'txtTotalFac').setValue('0.00');
	};

	NS.limpiarTodo = function(){
	
		habilitarBotones(true);
		//mascara.hide();
		
		NS.autoDesauto='';
		NS.parametroBus='';
		NS.descEmpresa = '';
		NS.descReglaNegocio = ''
		NS.indxMaestro = '';
			
		//NS.cmbGrupo.reset();
		NS.cmbGrupoEmpresas.reset();
		NS.cmbBeneficiario.reset();
		
		NS.gridMaestroPropuestas.store.removeAll();
		NS.gridMaestroPropuestas.getView().refresh();
		
		NS.gridDetalle.store.removeAll();
		NS.gridDetalle.getView().refresh();
		
		NS.ConsultaPropuestas.getForm().reset();
		NS.cancelar();
		
		Ext.getCmp(PF + 'dateNvaFecPago').setValue('');
		NS.cmbFormaPago.reset();
		NS.cmbChequeraPag.reset();
		NS.storeChequeraPag.removeAll();
		NS.storeChequeraPagBenef.removeAll();
		NS.cmbChequeraPagBenef.reset();
		
		NS.gridDetalle.setDisabled(false);
		
	};
	
	/************************************************************************************************
	 *  					Metodos/Funciones Agregados por EMS										*
	 * *********************************************************************************************/
	
	//Radio Button Reglas
	NS.optReglas = new Ext.form.RadioGroup({
		id: PF + 'optReglas',
		name: PF + 'optReglas',
		x: 10,
		y: -5,
		columns: 1, //muestra los radiobuttons en x columnas
		items: [
	          {boxLabel: 'Servicios', id: PF + 'optServicios', name: 'optReglas', inputValue: 0, tabIndex: 7, checked: true},  
	          {boxLabel: 'Líneas', id: PF + 'optLineas', name: 'optReglas', inputValue: 1, tabIndex: 8}
	     ],
	     listeners:{
				change:{
					fn:function(){
			
						var opcion = NS.optReglas.getValue();
						
						if(opcion == null || opcion == undefined ){
							return;
						}else{
							opcion = NS.optReglas.getValue().getGroupValue();
							
							//REALIZA UNA CONSULTA DIFERENTE AL SELECCIONAR X TIPO DE REGLA Y CARGA EL COMBO
							switch(parseInt(opcion)){
							
								case 0:
									NS.cmbReglaNegocios.setValue("");
									NS.storeReglaNegocio.baseParams.tipoRegla = 'S';
									NS.storeReglaNegocio.load();
									NS.storeMaestro.baseParams.tipoRegla = 'S';
									break;
								case 1:
									NS.cmbReglaNegocios.setValue("");
									NS.storeReglaNegocio.baseParams.tipoRegla = 'L';
									NS.storeMaestro.baseParams.tipoRegla = 'L';
									NS.storeReglaNegocio.load();
									break;
							}
						}
					}//End function
				} //End change
			} // End listeners	
	});
	
    //combo Regla negocios
	NS.cmbReglaNegocios = new Ext.form.ComboBox({
		store: NS.storeReglaNegocio //Falta crear el stored
		,name: PF + 'cmbReglaNegocios'
		,id: PF + 'cmbReglaNegocios'
		,typeAhead: true
		,mode: 'local'
		,minChars: 0
		//,selecOnFocus: true
		//,forceSelection: true
      	,x: 95
        ,width: 210
		,valueField:'id'
		,displayField:'descripcion'
		,autocomplete: true
		,emptyText: 'Seleccione'
		,triggerAction: 'all'
		,value: ''
		,tabIndex: 9
		,listeners:{
			select:{
				fn:function(combo, valor) {
					NS.descReglaNegocio = '' + valor.data.descripcion;
				}
			}
		}
		
	});
	
	//Contenedor de Radio Button SI - NO
	NS.contReglas = new Ext.form.FieldSet({
		title: 'Reglas de Negocios',
		x: 120,
		y: 40,
		width: 330,
		height: 70,
		layout: 'absolute',
		items: [
		    NS.optReglas,
		    NS.cmbReglaNegocios
		]
	});
	
	NS.modificarPropuestaMaestro = function() {
		var gridPropuestas = new Array();
		var recSelectProp = NS.gridMaestroPropuestas.getSelectionModel().getSelections();
		
		if(recSelectProp.length == 0) {
			Ext.Msg.alert('SET','Es necesario seleccionar una propuesta a modificar!!');
			return;
		}
		habilitarBotones(false);
		//mascara.show();
		//Igual a uno valida facultad y firmas
		if(recSelectProp.length == 1) {
			for (var i=0; i<recSelectProp.length; i++) {
				var regPropuestas = {};
				regPropuestas.cveControl = recSelectProp[i].get('cveControl');
				regPropuestas.usr1 = recSelectProp[i].get('usuarioUno');
				regPropuestas.usr2 = recSelectProp[i].get('usuarioDos');
				regPropuestas.usr3 = recSelectProp[i].get('usuarioTres');
				regPropuestas.numIntercos = recSelectProp[i].get('numIntercos');
				gridPropuestas[i] = regPropuestas;
			}
			NS.jsonPropuestas = Ext.util.JSON.encode(gridPropuestas);
			//valida que no tenga autorizaciones
			ConsultaPropuestasAction.validaModPropMaestro(NS.jsonPropuestas, function(resultado, e) {
				
				if(e.message=="Unable to connect to the server."){
					Ext.Msg.alert('SET','Error de conexión al servidor');
					return;
				}
				
				if (resultado != '' && resultado != undefined) {
					NS.autorizarModificacion = false;
					Ext.Msg.alert('SET', resultado + '!!');
					habilitarBotones(true);
					//mascara.hide();
					return;
				}else{
					//Muestra ventana modificación fecha.		
					//NS.autorizarModificacion = true
					winModif.show();
				}
			});
		}else{
			//Valida solo facultad y pasa directo a ventana de modificar fecha.
			ConsultaPropuestasAction.validaFacultadUsuario('btnModifProp', function(resultado, e) {
				if(e.message=="Unable to connect to the server."){
					Ext.Msg.alert('SET','Error de conexión al servidor');
					return;
				}
				
				if (resultado == 0) {
					NS.autorizarModificacion = false;
					Ext.Msg.alert('SET', resultado + '!!');
					habilitarBotones(true);
					//mascara.hide();
					return;
				}else{

					NS.autorizarModificacion = true
					winModif.show();
					
				}
			});
		}
			
	};
	
	//Radio Button Reglas
	NS.optParametros = new Ext.form.RadioGroup({
		id: PF + 'optParametros',
		name: PF + 'optParametros',
		x: 10,
		columns: 1, //muestra los radiobuttons en x columnas
		items: [
	          {boxLabel: 'Bloqueo Proveedores', id: PF + 'optBloqProv', name: 'optParametros', inputValue: 0}, //, checked: true  
	          {boxLabel: 'Pagos Cruzados', id: PF + 'optBloqPagoCruzado', name: 'optParametros', inputValue: 1}
	     ],
	     
	     listeners:{
				change:{
					fn:function(){
			
						var opcion = Ext.getCmp(PF + 'optParametros').getValue();
						
						if(opcion == null || opcion == undefined ){
							return;
						}else{
							opcion = Ext.getCmp(PF + 'optParametros').getValue().getGroupValue();
							
							//PRESENTA UNA VENTANA DIFERENTE DEPENDIENDO DEL RADIO SELECCIONADO
							switch(parseInt(opcion)){
							
								case 0:
									winBloqueoProveedores.show();
									//ventanaActiva.destroy();
									winParametros.hide();
									break;
								case 1:
									winPagosCruzados.show();
									winParametros.hide();
									break;
							}//End switch
							
						}

					}//End function
				} //End change
			} // End listeners	
	
	});
	
	
	/***************************************
	 ** Ventana de Bloqueo de Proveedores **
	 **************************************/
	//Agregado EMS 220915: Para la ventana de Bloqueo proveedor
	NS.cmbEmpresasBloq = new Ext.form.ComboBox({
		store: NS.storeEmpresas
		,name: PF+'cmbEmpresasBloq'
		,id: PF+'cmbEmpresasBloq'
		,typeAhead: true
		,mode: 'local'
		,minChars: 0
		,selecOnFocus: true
		,forceSelection: true
      	,x: 75
        ,y: 5
        ,width: 300
		,valueField:'noEmpresa'
		,displayField:'nomEmpresa'
		,autocomplete: true
		,emptyText: 'Seleccione un grupo empresa'
		,triggerAction: 'all'
		,value: ''
		,visible: false
		,listeners:{
			select:{
				fn:function(combo, valor) {
				//NS.storeMaestro.baseParams.idGrupoEmpresa=combo.getValue();
				}
			}
		}
	});

	//Agregado EMS 220915: Para la ventana de Bloqueo proveedor
	NS.storeUnicoBeneficiarioBloq = new Ext.data.DirectStore({
		paramsAsHash: false,
		baseParams:{
			campoUno:'equivale_persona',
			campoDos:'',
			tabla:'persona',
			condicion:'',
			orden:'',
			registroUnico:false
		},
		root: '',
		paramOrder:['campoUno','campoDos','tabla','condicion','orden','registroUnico'],
		root: '',
		directFn: ConsultaPropuestasAction.llenarComboBeneficiario, 
		idProperty: 'id', 
		fields: [
			 {name: 'id'},
			 {name: 'descripcion'},
		],
		listeners: {
			load: function(s, records){
				var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeUnicoBeneficiarioBloq, msg:"Cargando..."});
				if(records.length==null || records.length<=0)
				{
					Ext.Msg.alert('SET','No Existen el beneficiario con esa clave');
					Ext.getCmp(PF + 'txtIdProveedorBloq').reset();
					Ext.getCmp(PF + 'cmbBeneficiarioBloq').reset();
					return;
				}
				else{
					var reg = NS.storeUnicoBeneficiarioBloq.data.items;
					Ext.getCmp(NS.cmbBeneficiarioBloq.getId()).setValue(reg[0].get('descripcion'));
					NS.accionarUnicoBeneficiarioBloq(reg[0].get('id'));
					//actualiza grid
					
				}
			},
			exception: function(mist){
				Ext.Msg.alert('SET','Error en conexión al servidor');
				mascara.hide();
			}
		}
	}); 

	//Agregado EMS 220915: Para la ventana de Bloqueo proveedor
	NS.accionarUnicoBeneficiarioBloq = function(valueCombo){
 		NS.idPersona=valueCombo;
 		NS.nomPersona=NS.storeUnicoBeneficiarioBloq.getById(valueCombo).get('descripcion');
 	}
	
	//Agregado EMS 220915: Para la ventana de Bloqueo proveedor
	NS.cmbBeneficiarioBloq = new Ext.form.ComboBox({
		store: NS.storeBeneficiario
		,name: PF+'cmbBeneficiarioBloq'
		,id: PF+'cmbBeneficiarioBloq'
		,typeAhead: true
		,mode: 'local'
		,minChars: 0
		,selecOnFocus: true
		,forceSelection: true
        ,pageSize: 10
        ,x: 160
        ,y: 40
        ,width: 255
		,valueField:'idStr'
		,displayField:'descripcion'
		,autocomplete: true
		,emptyText: 'Seleccione beneficiario'
		,triggerAction: 'all'
		,value: ''
		,visible: false
		,editable: true
		,hidden: false //true
		,listeners:{
			select:{
				fn:function(combo, valor) {
					BFwrk.Util.updateComboToTextField(PF+'txtIdProveedorBloq',NS.cmbBeneficiarioBloq.getId());
					NS.accionarBeneficiario(combo.getValue());
					
					buscarProveedoresBloqueados();
				}
			},
				beforequery: function(qe){
					NS.parametroBus=qe.combo.getRawValue();
				}
		}
	});
	
	NS.storePeriodos = new Ext.data.DirectStore({
		paramsAsHash: false,
		root: '',
		directFn: ConsultaPropuestasAction.llenarComboPeriodos,
		idProperty: 'descripcion',
		fields: [
			 {name: 'descripcion'}
		],
		listeners: 
			{
				load: function(s, records)
				{
					var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storePeriodos, msg:"Cargando..."});
					if(records.length === null || records.length <= 0){
						Ext.Msg.alert('SET','No existen periodos');
						return;
					}else{
						NS.cmbPeriodos.setValue(''+NS.fecHoy.substring(6,''+NS.fecHoy.length));
					}
				},
				exception: function(mist){
					Ext.Msg.alert('SET','Error en conexión al servidor');
					mascara.hide();
				}
			}
		}); 

	//NS.storePeriodos.load();
	
	NS.cmbPeriodos = new Ext.form.ComboBox({
		store: NS.storePeriodos,
		name: PF + 'cmbPeriodos',
		id: PF + 'cmbPeriodos',
		typeAhead: true,
		mode: 'local',
		minChars: 0,
		selecOnFocus: true,
		forceSelection: true,
	 	x: 250,
	    y: 5,
	    width: 80,
		valueField: 'descripcion',
		displayField: 'descripcion',
		autocomplete: true,
		emptyText: 'Seleccione un periodo',
		value: '',
		triggerAction: 'all',
		listeners:{
			select:
				{
				fn:function(combo, valor) 
					{
					 	 
					}
				}
			}
		});
	
		
	var ventanaActiva;
	
	var winParametros = new Ext.Window({
		title: 'Parámetros',
		modal: true,
		shadow: true,
		//closable: true,
		closeAction: 'hide',
		width: 250,
	   	height: 200,
	   	//x: 800,
	   	//y: 100,
	   	layout: 'absolute',
	   	plain: true,
	    bodyStyle: 'padding:10px;',
	    buttonAlign: 'center',
	   	items: [
	   	        NS.optParametros
	   	        ],
	     listeners:{
	    	 show:{
	    		 fn:function(){
	    			 
	    			 Ext.getCmp(PF+'optBloqProv').setValue(false);
	    			 Ext.getCmp(PF+'optBloqPagoCruzado').setValue(false);
	    		 }//End function
	    	 }, //End show
	    	 // hide:{
	    	//	 fn:function(){
	    	//		//Ext.getCmp('my-form').remove(Ext.getCmp('allRadiosID'), true);
		    //		// Ext.getCmp(PF+'optParametros').destroy();
	    	//	 }
	    	 //}
	
	     } // End listeners	
	
	});
	
	var winBloqueoProveedores = new Ext.Window({
		title: 'Bloqueo de Proveedores y Documentos',
		modal: true,
		shadow: true,
		//closable: false,
		closeAction: 'hide',
		width: 550,
	   	height: 520,
	   	//x: 800,
	   	//y: 100,
	   	layout: 'absolute',
	   	plain: true,
	    bodyStyle: 'padding:10px;',
	    buttonAlign: 'center',
	   	items: [
	   	        {
	   	        	xtype: 'fieldset',
	                title: 'Bloqueo Proveedor',
	                width: 510,
	                height: 100,
	                x: 10,
	                y: 10,
	                layout: 'absolute',
	                id: PF + 'fieldBloqProv',
	                items: [
	                       {
	                    	   xtype: 'label',
							   text: 'Empresa',
							   x: 10,
							   y: 5
						  },
						  NS.cmbEmpresasBloq,
						  {
							  xtype: 'label',
							  text: 'Proveedor',
							  x: 10,
							  y: 40
						  },
						  {
							  xtype: 'textfield',
							  x: 75,
							  y: 40,
							  width: 80,
							  name: PF+'txtIdProveedorBloq',
							  id: PF+'txtIdProveedorBloq',
							  hidden: false, //true
							  listeners: {
								  change: {
									  fn:function(caja) {
										  if(caja.getValue() != null && caja.getValue() != undefined && caja.getValue() != '') {
											  NS.storeUnicoBeneficiarioBloq.baseParams.registroUnico=true;
											  NS.storeUnicoBeneficiarioBloq.baseParams.condicion=''+caja.getValue();
											  NS.storeUnicoBeneficiarioBloq.load();
											  
											  //
											  buscarProveedoresBloqueados();
											  
										  }else {
											  NS.cmbBeneficiarioBloq.reset();
											  NS.storeUnicoBeneficiarioBloq.removeAll();
										  }	
									  }
								  }
							  }
						  },
						  {
							  xtype: 'button',
							  text: '...',
							  x: 420,
							  y: 40,
							  width: 20,
							  height: 22,
							  id: PF+'btnComboBenefBloq',
							  name: PF+'btnComboBenefBloq',
							  hidden: false,
							  listeners:{
								  click:{
									  fn:function(e){
										  if(NS.parametroBus==='' || (isNaN(NS.parametroBus) && NS.parametroBus.length < 4)) {
											  Ext.Msg.alert('SET','Es necesario escribir un mínimo de 4 caracteres para la búsqueda');
										  }else{
											  NS.storeBeneficiario.baseParams.condicion=NS.parametroBus;
											  NS.storeBeneficiario.load();
										  }
									  }
								  }
							  }
						  },
						  NS.cmbBeneficiarioBloq
					]
	   	        	},
	   	        	{
	   	        		xtype: 'fieldset',
	   	        		title: 'Bloqueo Documento',
	   	        		width: 510,
	   	        		height: 140,
	   	        		x: 10,
	   	        		y: 125,
	   	        		layout: 'absolute',
	   	        		id: PF + 'fieldBloqDoc',
	   	        		items: [
	   	        		        {
	   	        		        	xtype: 'label',
	   	        		        	text: 'Documento',
	   	        		        	x: 10,
	   	        		        	y: 5
	   	        		        },
	   	        		        {
	   	        		        	xtype: 'textfield',
	   	        		        	x: 75,
	   	        		        	y: 5,
	   	        		        	width: 100,
	   	        		        	name: PF+'txtDocumentoBloq',
	   	        		        	id: PF+'txtDocumentoBloq',
	   	        		        	hidden: false, //true
	   	        		        	listeners: {
	   	        		        		change: {
	   	        		        			fn:function(caja) {
	   	        		        				
	   	        		        			}
	   	        		        		}
	   	        		        	}
	   	        		        },
	   	        		       {
	   	        		        	xtype: 'label',
	   	        		        	text: 'Periodo',
	   	        		        	x: 200,
	   	        		        	y: 5
	   	        		        },
	   	        		        	NS.cmbPeriodos,
	   	        		        {
	   	        		        	xtype: 'label',
	   	        		        	text: 'Importe',
	   	        		        	x: 10,
	   	        		        	y: 40
	   	        		        },
	   	        		        {
	   	        		        	xtype: 'textfield',
	   	        		        	x: 75,
	   	        		        	y: 40,
	   	        		        	width: 100,
	   	        		        	name: PF+'txtImporteBloq',
	   	        		        	id: PF+'txtImporteBloq',
	   	        		        	hidden: false, //true
	   	        		        	listeners: {
	   	        		        		change: {
	   	        		        			fn:function(caja) {
	   	        		        				
	   	        		        			}
	   	        		        		}
	   	        		        	}
	   	        		        },
	   	        		        {
	   	        		        	xtype: 'label',
	   	        		        	text: 'Fecha Pago',
	   	        		        	x: 10,
	   	        		        	y: 75
	   	        		        },
	   	        		        {
	   	        		        	xtype:'textfield',
	   	        		        	format:'d/m/Y',
	   	        		        	id:PF+'txtFechaBloq',
	   	        		        	name:PF+'txtFechaBloq',
	   	        		        	//value: apps.SET.FEC_HOY,
	   	        		        	x:75,
	   	        		        	y:75,
	   	        		        	width:100,
	   	        		        	listeners:{
	   	        		        		change:{
	   	        		        			fn:function(caja,valor){
				       					        			
	   	        		        			}
	   	        		        		}
	   	        		        	}
	   	        		        },
	   	        		        {
	   	        		        	xtype: 'label',
	   	        		        	text: 'Concepto',
	   	        		        	x: 200,
	   	        		        	y: 75
	   	        		        },
	   	        		        {
	   	        		        	xtype: 'textfield',
	   	        		        	x: 260,
	   	        		        	y: 75,
	   	        		        	width: 170,
	   	        		        	name: PF+'txtConceptoBloq',
	   	        		        	id: PF+'txtConceptoBloq',
	   	        		        	hidden: false, //true
	   	        		        	listeners: {
	   	        		        		change: {
	   	        		        			fn:function(caja) {
	   	        		        				
	   	        		        			}
	   	        		        		}
	   	        		        	}
	   	        		        },
	   	        		        {
	   	        		        	xtype: 'button',
	   	        		        	text: 'Buscar',
	   	        		        	x: 340, 
	   	        		        	y: 5, 
	   	        		        	width: 100,
	   	        		        	height: 22,
	   	        		        	id: PF + 'btnBuscarBloq',
	   	        		        	name: PF + 'btnBuscarBloq',
	   	        		        	listeners:{
	   	        		        		click:{
	   	        		        			fn:function(e){
	   	        		        				buscarProveedoresBloqueados();
	   	        		        			}
	   	        		        		}
	   	        		        	}
	   	        		        }
	   	        		        ]
	   	        	},
	   	        	{
	   	        		xtype: 'fieldset',
	   	        		title: 'Acción',
	   	        		width: 510,
	   	        		height: 130,
	   	        		x: 10,
	   	        		y: 280,
	   	        		layout: 'absolute',
	   	        		id: PF + 'fieldBloqAccion',
	   	        		items: [
	   	        		        {
	   	        		        	xtype: 'label',
	   	        		        	text: 'Motivo',
	   	        		        	x: 10,
	   	        		        	y: 35
	   	        		        },
	   	        		        {
	   	        		        	xtype: 'textfield',
	   	        		        	x: 75,
	   	        		        	y: 35,
	   	        		        	width: 200,
	   	        		        	name: PF+'txtMotivoBloq',
	   	        		        	id: PF+'txtMotivoBloq',
	   	        		        	hidden: false, //true
	   	        		        	listeners: {
	   	        		        		change: {
	   	        		        			fn:function(caja) {
	   	        		        				
	   	        		        			}
	   	        		        		}
	   	        		        	}
	   	        		        },
	   	        		        {
	   	        		        	xtype: 'checkbox',
	   	        		        	id: PF + 'chkBloqProveedor',
	   	        		        	name: PF + 'chkBloqProveedor',
	   	        		        	x: 10,
	   	        		        	y: 0,
	   	        		        	boxLabel: 'Bloquear Proveedor',
	   	        		        	listeners:{
	   	        		        		change:{
	   	        		        			fn:function(e){
	   	        		        				if(Ext.getCmp(PF + 'chkBloqDoc').getValue() == true ){
	   	        		        					Ext.getCmp(PF + 'chkBloqDoc').setValue(false);
	   	        		        				}
	   	        		        			}
	   	        		        		}
	   	        		        	}
	   	        		        },
	   	        		        {
	   	        		        	xtype: 'checkbox',
	   	        		        	id: PF + 'chkBloqDoc',
	   	        		        	name: PF + 'chkBloqDoc',
	   	        		        	x: 200,
	   	        		        	y: 0,
	   	        		        	boxLabel: 'Bloquear Documento',
	   	        		        	listeners:{
	   	        		        		change:{
	   	        		        			fn:function(e){
	   	        		        				if(Ext.getCmp(PF + 'chkBloqProveedor').getValue() == true ){
	   	        		        					Ext.getCmp(PF + 'chkBloqProveedor').setValue(false);
	   	        		        				}
	   	        		        			}
	   	        		        		}
	   	        		        	}
	   	        		        },
	   	        		        {
	   	        		        	xtype: 'button',
	   	        		        	text: 'Bloquear',
	   	        		        	x: 340, 
	   	        		        	y: 21, 
	   	        		        	width: 100,
	   	        		        	height: 22,
	   	        		        	id: PF + 'btnAceptarBloq',
	   	        		        	name: PF + 'btnAceptarBloq',
	   	        		        	listeners:{
	   	        		        		click:{
	   	        		        			fn:function(e){
	   	        		        				
	   	        		        				if(Ext.getCmp(PF + 'chkBloqProveedor').getValue() == true ){
	   	        		        					var noProv = Ext.getCmp(PF + 'txtIdProveedorBloq').getValue();
		   	        		        				var noEmpresa = "" + NS.cmbEmpresasBloq.getValue(); 
		   	        		        				var motivo = Ext.getCmp(PF + 'txtMotivoBloq').getValue();

		   	        		        				ConsultaPropuestasAction.insertarBloqueo(noEmpresa, noProv, "", motivo, function(resultado, e) {
		   	        		        					if(e.message=="Unable to connect to the server."){
		   	        		        						Ext.Msg.alert('SET','Error de conexión al servidor');
		   	        		        						return;
		   	        		        					}
		   	        		        					if (resultado.error != '') {
															Ext.Msg.alert('SET', resultado.error );
															return;
														}else{
															Ext.Msg.alert('SET', resultado.resp );
															
															//Inserta bitacora proveedor
															ConsultaPropuestasAction.insertaBitacoraPropuesta('bloqueoProv', '', 
																	'', 'E:'+noEmpresa+'P:'+noProv+'D:'+'M:'+motivo , function(result, e){
																
																if(e.message=="Unable to connect to the server."){
																	Ext.Msg.alert('SET','Error de conexión al servidor');
																	return;
																}
																
																if(result != null && result.length != '') {
																	if(result.error != ''){
																		Ext.Msg.alert('SET',''+result.error);
																	}
																}
															});
															
															return;
														}
													});
		   	        		        				
	   	        		        				}else if(Ext.getCmp(PF + 'chkBloqDoc').getValue() == true ){
	   	        		        					
	   	        		        					var noProv = "" + Ext.getCmp(PF + 'txtIdProveedorBloq').getValue();
		   	        		        				var noEmpresa = "" + NS.cmbEmpresasBloq.getValue(); 
		   	        		        				var noDocto = "" + Ext.getCmp(PF + 'txtDocumentoBloq').getValue();
		   	        		        				var motivo = "" + Ext.getCmp(PF + 'txtMotivoBloq').getValue();
		   	        		        				
		   	        		        				ConsultaPropuestasAction.insertarBloqueo(noEmpresa, noProv, noDocto, motivo, function(resultado, e) {
		   	        		        					
		   	        		        					if(e.message=="Unable to connect to the server."){
		   	        		        						Ext.Msg.alert('SET','Error de conexión al servidor');
		   	        		        						return;
		   	        		        					}
		   	        		        					
		   	        		        					if (resultado.error != '') {
															Ext.Msg.alert('SET', resultado.error );
															return;
														}else{
															Ext.Msg.alert('SET', resultado.resp );
															//Inserta bitacora documento
															ConsultaPropuestasAction.insertaBitacoraPropuesta('bloqueoProv', '', 
																	'', 'E:'+noEmpresa+'P:'+noProv+'D:'+noDocto+'M:'+motivo , function(result, e){
																
																if(e.message=="Unable to connect to the server."){
																	Ext.Msg.alert('SET','Error de conexión al servidor');
																	return;
																}
																
																if(result != null && result.length != '') {
																	if(result.error != ''){
																		Ext.Msg.alert('SET',''+result.error);
																	}
																}
															});
															
															return;
														}
													});
		   	        		        				
	   	        		        				}else{
	   	        		        					Ext.Msg.alert('SET', "Seleccione un checkBox de bloqueo (Proveedor/Documento)" );
	   	        		        				}
	   	        		        				
	   	        		        			}
	   	        		        		}
	   	        		        	}	
	   	        		        },
	   	        		        {
	   	        		        	xtype: 'button',
	   	        		        	text: 'Desbloquear',
	   	        		        	x: 340, 
	   	        		        	y: 47, 
	   	        		        	width: 100,
	   	        		        	height: 22,
	   	        		        	id: PF + 'btnDesbloquear',
	   	        		        	name: PF + 'btnDesbloquear',
	   	        		        	listeners:{
	   	        		        		click:{
	   	        		        			fn:function(e){
	   	        		        				
	   	        		        				var noProv = "" + Ext.getCmp(PF + 'txtIdProveedorBloq').getValue();
	   	        		        				var noEmpresa = "" + NS.cmbEmpresasBloq.getValue(); 
	   	        		        				var noDocto = "" + Ext.getCmp(PF + 'txtDocumentoBloq').getValue();
	   	        		        				
	   	        		        				ConsultaPropuestasAction.eliminarBloqueo(noEmpresa, noProv, noDocto, function(resultado, e) {
	   	        		        					
	   	        		        					if(e.message=="Unable to connect to the server."){
	   	        		        						Ext.Msg.alert('SET','Error de conexión al servidor');
	   	        		        						return;
	   	        		        					}
	   	        		        					
	   	        		        					if (resultado.error != '') {
														Ext.Msg.alert('SET', resultado.error );
														return;
													}else{
														Ext.Msg.alert('SET', resultado.resp );
														
														//Inserta bitacora documento
														ConsultaPropuestasAction.insertaBitacoraPropuesta('desbloqueoProv', '', 
																'', 'E:'+noEmpresa+'P:'+noProv+'D:'+noDocto , function(result, e){
															
															if(e.message=="Unable to connect to the server."){
																Ext.Msg.alert('SET','Error de conexión al servidor');
																return;
															}
															
															if(result != null && result.length != '') {
																if(result.error != ''){
																	Ext.Msg.alert('SET',''+result.error);
																}
															}
														});
														
														limpiarBloqueados();
														return;
													}
												});
	   	        		        				
	   	        		        			}
	   	        		        		}
	   	        		        	}	
	   	        		        },
	   	        		     {
	   	        		        	xtype: 'button',
	   	        		        	text: 'Limpiar',
	   	        		        	x: 340, 
	   	        		        	y: 73, 
	   	        		        	width: 100,
	   	        		        	height: 22,
	   	        		        	id: PF + 'btnLimpiarBloq',
	   	        		        	name: PF + 'btnLimpiarBloq',
	   	        		        	listeners:{
	   	        		        		click:{
	   	        		        			fn:function(e){
	   	        		        				limpiarBloqueados();
	   	        		        			}
	   	        		        		}
	   	        		        	}	
	   	        		        }
	   	        		        ]
	   	        	},
	   	        	{
	   	        		xtype: 'fieldset',
	   	        		title: 'Ver Proveedores/Documentos Bloqueados',
	   	        		width: 510,
	   	        		height: 60,
	   	        		x: 10,
	   	        		y: 420,
	   	        		layout: 'absolute',
	   	        		id: PF + 'fieldVerBloqueados',
	   	        		items: [
								{
								   	xtype: 'checkbox',
								   	id: PF + 'chkBloqSetExcel',
								   	name: PF + 'chkBloqSetExcel',
								   	x: 10,
								   	y: 0,
								   	boxLabel: 'Registros bloqueados SET'
								   },
								   {
								   	xtype: 'checkbox',
								   	id: PF + 'chkBloqSAPExcel',
								   	name: PF + 'chkBloqSAPExcel',
								   	x: 200,
								   	y: 0,
								   	boxLabel: 'Registros bloqueados SAP'
								   },
								   {
		   	        		        	xtype: 'button',
		   	        		        	text: 'Generar Excel',
		   	        		        	x: 360, 
		   	        		        	y: 0, 
		   	        		        	width: 100,
		   	        		        	height: 22,
		   	        		        	id: PF + 'btnExcelBloqueados',
		   	        		        	name: PF + 'btnExcelBloqueados',
		   	        		        	listeners:{
		   	        		        		click:{
		   	        		        			fn:function(e){
		   	        		        				
		   	        		        				var muestraSet = false;
		   	        		        				var muestraSap = false;
		   	        		        				
		   	        		        				if(Ext.getCmp(PF + 'chkBloqSetExcel').getValue() == true ){
		   	        		        					muestraSet = true;
		   	        		        				}
		   	        		        				
		   	        		        				if(Ext.getCmp(PF + 'chkBloqSAPExcel').getValue() == true ){
		   	        		        					muestraSap = true;
		   	        		        				}
		   	        		        				
		   	        		        				if(muestraSet == false && muestraSap == false){
		   	        		        					Ext.Msg.alert('SET','Seleccione que tipo de bloqueados desea ver.');
		   	        		        					return;
		   	        		        				}
		   	        		        				
   	        		        						strParams = '?nomReporte=bloqueados';
   	        		        						strParams += '&'+'nomParam1=bloqSet';
   	        		        						strParams += '&'+'valParam1='+muestraSet;
   	        		        						strParams += '&'+'nomParam2=bloqSap';
   	        		        						strParams += '&'+'valParam2='+muestraSap;
   	        		        						window.open("/SET/jsp/bfrmwrk/gnrators/abreExcel.jsp"+strParams);
		   	        		        				
		   	        		        			}
		   	        		        		}
		   	        		        	}	
		   	        		        }
	   	        		       ]
	   	        		     
	   	        	}
	   	        	
	   	        	],
	   	        	listeners:{
	   	        		show:{
	   	        			fn:function(){
	   	        				//	Limpiar componentes
	   	        				limpiarBloqueados();
	   	        				//NS.cmbBeneficiarioNvoPag.reset();
	        					NS.storeBeneficiario.removeAll();
	        					NS.storeUnicoBeneficiarioBloq.removeAll();
	        					
	        					NS.storeEmpresas.load();
	        					
	   	        			}//End function
	   	        		} //End change
	   	        	} // End listeners	
		});
	
	function limpiarBloqueados(){
		
		Ext.getCmp(PF+'cmbEmpresasBloq').setValue("");
		Ext.getCmp(PF+'txtIdProveedorBloq').setValue("");
		Ext.getCmp(PF+'cmbBeneficiarioBloq').setValue("");
		Ext.getCmp(PF+'txtDocumentoBloq').setValue("");
		Ext.getCmp(PF+'txtImporteBloq').setValue("");
		Ext.getCmp(PF+'txtConceptoBloq').setValue("");
		Ext.getCmp(PF+'txtFechaBloq').setValue("");
		Ext.getCmp(PF+'chkBloqProveedor').setValue(false);
		Ext.getCmp(PF+'chkBloqDoc').setValue(false);
		Ext.getCmp(PF+'txtMotivoBloq').setValue("");
		Ext.getCmp(PF + 'chkBloqSetExcel').setValue(false);
		Ext.getCmp(PF + 'chkBloqSAPExcel').setValue(false);
		
		NS.cmbPeriodos.setValue(''+NS.fecHoy.substring(6,''+NS.fecHoy.length));
	}
	
	function buscarProveedoresBloqueados(){
		
			var noProv = Ext.getCmp(PF + 'txtIdProveedorBloq').getValue();
			//var noEmpresa = NS.storeGrupoEmpresas.getById(NS.cmbEmpresasBloq.getValue()).get('descripcion'); 
			var noEmpresa = "" + NS.cmbEmpresasBloq.getValue(); //No funciona: Ext.getCmp(PF + 'cmbEmpresasBloq').getValue();
			var noDocto = Ext.getCmp(PF + 'txtDocumentoBloq').getValue();
			var periodo = NS.cmbPeriodos.getValue();
			
			ConsultaPropuestasAction.buscarBloqueo(noEmpresa, noProv, noDocto, periodo,  function(resultado, e) {
				
				if(e.message=="Unable to connect to the server."){
					Ext.Msg.alert('SET','Error de conexión al servidor');
					return;
				}
				
			if (resultado.error != '') {
				Ext.Msg.alert('SET', resultado.error );
				return;
			}else{
				
				if(resultado.existe == 'proveedor'){
					Ext.getCmp(PF + 'chkBloqProveedor').setValue(true);
					Ext.getCmp(PF + 'txtMotivoBloq').setValue(resultado.motivo);
					//limpia campos documentos
					Ext.getCmp(PF + 'txtImporteBloq').setValue("");
					Ext.getCmp(PF + 'txtFechaBloq').setValue("");
					Ext.getCmp(PF + 'txtConceptoBloq').setValue("");
					Ext.getCmp(PF + 'chkBloqDoc').setValue(false);
					
				}else if(resultado.existe == 'documento'){
					Ext.getCmp(PF + 'txtImporteBloq').setValue(NS.formatNumber(Math.round((resultado.importe)*100)/100 ));
					Ext.getCmp(PF + 'txtFechaBloq').setValue(resultado.fechaPago);
					Ext.getCmp(PF + 'txtConceptoBloq').setValue(resultado.concepto);
					Ext.getCmp(PF + 'txtMotivoBloq').setValue(resultado.motivo);
					Ext.getCmp(PF + 'chkBloqDoc').setValue(true);
					Ext.getCmp(PF + 'chkBloqProveedor').setValue(false);
				}else if(resultado.existe == 'documento2'){
					Ext.getCmp(PF + 'txtImporteBloq').setValue(NS.formatNumber(Math.round((resultado.importe)*100)/100 ));
					Ext.getCmp(PF + 'txtFechaBloq').setValue(resultado.fechaPago);
					Ext.getCmp(PF + 'txtConceptoBloq').setValue(resultado.concepto);
					Ext.getCmp(PF + 'txtMotivoBloq').setValue("");
					Ext.getCmp(PF + 'chkBloqDoc').setValue(false);
					Ext.getCmp(PF + 'chkBloqProveedor').setValue(false);
				}else{
					Ext.getCmp(PF + 'txtMotivoBloq').setValue("");
					Ext.getCmp(PF + 'chkBloqDoc').setValue(false);
					Ext.getCmp(PF + 'chkBloqProveedor').setValue(false);
				}
				
			}
		});
			
	}
	/***********************************************
	 ** Termina Ventana de Bloqueo de Proveedores **
	 ***********************************************/
	
	/***********************************************
	 **		    Ventana de Pagos Cruzados 		  **
	 ***********************************************/
	
	NS.storePagoCruzado = new Ext.data.DirectStore({
		paramsAsHash: false,
		baseParams:{
			noPersona:''
		},
		paramOrder:['noPersona'],
		root: '',
		directFn: ConsultaPropuestasAction.consultaPagosCruzados, //crear metodo Action
		//idProperty: 'id', 
		fields: [
			 {name: 'id'},
			 {name: 'proveedor'},
			 {name: 'divisaOrig'}, //nombre del atributo DTO
			 {name: 'divisaPago'},
		],
		listeners: {
			load: function(s, records){
				var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storePagoCruzado, msg:"Cargando..."});
				if(records.length==null || records.length<=0){
					Ext.Msg.alert('SET','No existen proveedores con pagos cruzados');
				}
			},
			exception: function(mist){
				Ext.Msg.alert('SET','Error en conexión al servidor');
				mascara.hide();
			}
		}
	}); 
	
	//solo puede elegir una fila a la vez
 	NS.seleccionPagoCruzado = new Ext.grid.CheckboxSelectionModel
 	({
 		singleSelect: true
 	});
 	
	//Columnas del grid	    	
 	NS.columnasPagoCruzado = new Ext.grid.ColumnModel
 	([	
 	  	NS.seleccionPagoCruzado,
 	  	{	
 			header :'ID',
 			width :50,
 			sortable :true,
 			dataIndex :'id', 
 			renderer: function (value, meta, record) {
 	            meta.attr = 'style=' + record.get('color');
 	            return value;
 			}
 		},{
 			header :'Proveedor',
 			width :200,
 			sortable :true,
 			//renderer: Ext.util.Format.dateRenderer('Y-m-d'),
 			dataIndex :'proveedor',
 			renderer: function (value, meta, record) {
 	            meta.attr = 'style=' + record.get('color');
 	            return value;
 	        }
 		},{
 			header :'Divisa Original',
 			width :100,
 			sortable :true,
 			dataIndex :'divisaOrig',
 			//hidden: false,
 			renderer: function (value, meta, record) {
 	            meta.attr = 'style=' + record.get('color');
 	            return value;
 	        }
 		},{
 			header :'Divisa Pago',
 			width :100,
 			sortable :true,
 			dataIndex :'divisaPago',
 			renderer: function (value, meta, record) {
 	            meta.attr = 'style=' + record.get('color');
 	            return value;
 	        }
 		}
 	  ]);
 	
	//grid de datos
	 NS.gridConsultasPagoCruzado = new Ext.grid.GridPanel({
       store : NS.storePagoCruzado,
       viewConfig: {emptyText: 'Proveedores con pagos cruzados'},
       x:0,
       y:0,
       cm: NS.columnasPagoCruzado,
		sm: NS.seleccionPagoCruzado,
       width:480,
       height:205,
       frame:true,
       listeners: {
       	click: {
       		fn:function(e) {
       			var regSeleccionados = NS.gridConsultasPagoCruzado.getSelectionModel().getSelections();
       			
       			if(regSeleccionados != null && regSeleccionados != undefined && regSeleccionados != '') {
       				
       				//regSeleccionados[0].color = "#FDA920";
		        		/*if(regSeleccionados[0].get('color') == "color:#090DFA" || regSeleccionados[0].get('color') == "color:#04A861" 
		        			|| regSeleccionados[0].get('color') == "color:#FDA920")
		        			Ext.getCmp(PF + 'btnModificar').setDisabled(true);
		        		*/
       			}else {
       				
       			}
       		}
       	}
       }
   });
	 
	 
	 NS.storeBeneficiarioPagoCruzado = new Ext.data.DirectStore({
			paramsAsHash: false,
			baseParams:{
				noPersona: ''
			},
			root: '',
			paramOrder:['noPersona'],
			root: '',
			directFn: ConsultaPropuestasAction.llenarComboBenefPagoCruzado, 
			idProperty: 'idStr', 
			fields: [
				 {name: 'idStr'},
				 {name: 'descripcion'},
			],
			listeners: {
				load: function(s, records){
					var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeBeneficiarioPagoCruzado, msg:"Cargando..."});
					if(records.length==null || records.length<=0)
					{
						Ext.Msg.alert('SET','No Existen Beneficiarios con ese nombre');
					}else{
						//alert("param:  " + Ext.getCmp(PF+'cmbBeneficiarioCruz').getValue());
						//NS.parametroBus = Ext.getCmp(PF+'cmbBeneficiarioCruz').getValue();
						//NS.storePagoCruzado.baseParams.noPersona = NS.parametroBus;
						//NS.storePagoCruzado.load();
					}
				},
				exception: function(mist){
					Ext.Msg.alert('SET','Error en conexión al servidor');
					mascara.hide();
				}
			}
		}); 
	 
	 
	//Agregado EMS 220915: Para la ventana de pagos cruzados
	NS.storeUnicoBeneficiarioCruz = new Ext.data.DirectStore({
		paramsAsHash: false,
		baseParams:{
			noPersona:''
		},
		root: '',
		paramOrder:['noPersona'],
		root: '',
		directFn: ConsultaPropuestasAction.llenarComboBenefPagoCruzado, 
		idProperty: 'id', 
		fields: [
			 {name: 'id'},
			 {name: 'descripcion'},
		],
		listeners: {
			load: function(s, records){
				var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeUnicoBeneficiarioCruz, msg:"Cargando..."});
				if(records.length==null || records.length<=0)
				{
					Ext.Msg.alert('SET','No Existen el beneficiario con esa clave');
					Ext.getCmp(PF + 'txtIdProveedorCruz').reset();
					Ext.getCmp(PF + 'cmbBeneficiarioCruz').reset();
					return;
				}
				else{
					var reg = NS.storeUnicoBeneficiarioCruz.data.items;
					Ext.getCmp(NS.cmbBeneficiarioCruz.getId()).setValue(reg[0].get('descripcion'));
					NS.accionarUnicoBeneficiarioCruz(reg[0].get('id'));
					//actualiza grid
					NS.parametroBus = Ext.getCmp(PF+'cmbBeneficiarioCruz').getValue();
					//NS.storePagoCruzado.baseParams.noPersona = NS.parametroBus;
					//NS.storePagoCruzado.load();
				}
			},
			exception: function(mist){
				Ext.Msg.alert('SET','Error en conexión al servidor');
				mascara.hide();
			}
		}
	}); 
	
	//Agregado EMS 220915: Para la ventana de Bloqueo proveedor
	NS.accionarUnicoBeneficiarioCruz = function(valueCombo){
 		NS.idPersona=valueCombo;
 		NS.nomPersona=NS.storeUnicoBeneficiarioCruz.getById(valueCombo).get('descripcion');
 	}
	
	NS.accionarBeneficiarioPagoCruzado = function(valueCombo){
 		NS.idPersona=valueCombo;
 		NS.nomPersona=NS.storeBeneficiarioPagoCruzado.getById(valueCombo).get('descripcion');
 	}
 	
	
	//Agregado EMS 220915: Para la ventana de Bloqueo proveedor
	NS.cmbBeneficiarioCruz = new Ext.form.ComboBox({
		store: NS.storeBeneficiarioPagoCruzado
		,name: PF+'cmbBeneficiarioCruz'
		,id: PF+'cmbBeneficiarioCruz'
		,typeAhead: true
		,mode: 'local'
		,minChars: 0
		,selecOnFocus: true
		,forceSelection: true
        ,pageSize: 10
        ,x: 150
        ,y: 5
        ,width: 265
		,valueField:'idStr'
		,displayField:'descripcion'
		,autocomplete: true
		,emptyText: 'Seleccione beneficiario'
		,triggerAction: 'all'
		,value: ''
		,visible: false
		,editable: true
		,hidden: false //true
		,listeners:{
			select:{
				fn:function(combo, valor) {
					
					BFwrk.Util.updateComboToTextField(PF+'txtIdProveedorCruz',NS.cmbBeneficiarioCruz.getId());
					NS.accionarBeneficiarioPagoCruzado(combo.getValue());
					
					//actualiza grid
					NS.storePagoCruzado.removeAll();
					NS.storePagoCruzado.baseParams.noPersona = ''+combo.getValue();
					NS.storePagoCruzado.load();
					
				}
			},
				beforequery: function(qe){
					NS.parametroBus=qe.combo.getRawValue();
				}
		}
	});

	 
	/****************************************************
	 ** 		   VENTANA NUEVO PROVEEDOR			 	*
	 ****************************************************/
	
	//Agregado EMS 220915: Para la ventana de Bloqueo proveedor
	NS.storeUnicoBeneficiarioNvoPag = new Ext.data.DirectStore({
		paramsAsHash: false,
		baseParams:{
			campoUno:'equivale_persona',
			campoDos:'',
			tabla:'persona',
			condicion:'',
			orden:'',
			registroUnico:false
		},
		root: '',
		paramOrder:['campoUno','campoDos','tabla','condicion','orden','registroUnico'],
		root: '',
		directFn: ConsultaPropuestasAction.llenarComboBeneficiario, 
		idProperty: 'id', 
		fields: [
			 {name: 'id'},
			 {name: 'descripcion'},
		],
		listeners: {
			load: function(s, records){
				var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeUnicoBeneficiarioNvoPag, msg:"Cargando..."});
				if(records.length==null || records.length<=0)
				{
					Ext.Msg.alert('SET','No Existen el beneficiario con esa clave');
					Ext.getCmp(PF + 'txtIdNuevoProvCruz').reset();
					Ext.getCmp(PF + 'cmbBeneficiarioNvoPag').reset();
					return;
				}
				else{
					var reg = NS.storeUnicoBeneficiarioNvoPag.data.items;
					Ext.getCmp(NS.cmbBeneficiarioNvoPag.getId()).setValue(reg[0].get('descripcion'));
					NS.accionarUnicoBeneficiarioNvoPag(reg[0].get('id'));
				}
			},
			exception: function(mist){
				Ext.Msg.alert('SET','Error en conexión al servidor');
				mascara.hide();
			}
		}
	}); 
	
	//Agregado EMS 220915: Para la ventana de pagos cruzados
	NS.accionarUnicoBeneficiarioNvoPag = function(valueCombo){
 		NS.idPersona=valueCombo;
 		NS.nomPersona=NS.storeUnicoBeneficiarioNvoPag.getById(valueCombo).get('descripcion');
 	}
	
	//Agregado EMS 220915: Para la ventana de pagos cruzados
	NS.cmbBeneficiarioNvoPag = new Ext.form.ComboBox({
		store: NS.storeBeneficiario
		,name: PF+'cmbBeneficiarioNvoPag'
		,id: PF+'cmbBeneficiarioNvoPag'
		,typeAhead: true
		,mode: 'local'
		,minChars: 0
		,selecOnFocus: true
		,forceSelection: true
        ,pageSize: 10
        ,x: 180
        ,y: 5
        ,width: 250
		,valueField:'idStr'
		,displayField:'descripcion'
		,autocomplete: true
		,emptyText: 'Seleccione beneficiario'
		,triggerAction: 'all'
		,value: ''
		,visible: false
		,editable: true
		,hidden: false //true
		,listeners:{
			select:{
				fn:function(combo, valor) {
					var x = combo.getValue();
					BFwrk.Util.updateComboToTextField(PF+'txtIdNuevoProvCruz',NS.cmbBeneficiarioNvoPag.getId());
					NS.accionarBeneficiario(combo.getValue());
				}
			},
				beforequery: function(qe){
					NS.parametroBus=qe.combo.getRawValue();
				}
		}
	});
	
	
	//store Divisas
    NS.storeDivisas = new Ext.data.DirectStore({
		paramsAsHash: false,
		root: '',
		directFn: ConsultaPropuestasAction.obtenerDivisas, 
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
			},
			exception: function(mist){
				Ext.Msg.alert('SET','Error en conexión al servidor');
				mascara.hide();
			}
		}
	}); 
	NS.storeDivisas.load();
	
	NS.accionarDivisaOriginal = function(valueCombo){
		NS.idDivOriginal=''+valueCombo;
	}
	//combo Divisa Original
	NS.cmbDivisaOriginal = new Ext.form.ComboBox({
		store: NS.storeDivisas
		,name: PF+'cmbDivisaOriginal'
		,id: PF+'cmbDivisaOriginal'
		,typeAhead: true
		,mode: 'local'
		,minChars: 0
		,selecOnFocus: true
		,forceSelection: true
        ,x: 60
        ,y: 75
        ,width: 150
        ,tabIndex: 10
		,valueField:'idDivisa'
		,displayField:'descDivisa'
		,autocomplete: true
		,emptyText: 'Seleccione divisa original'
		,triggerAction: 'all'
		,value: 'PESOS'
		//,visible: false
		,listeners:{
			select:{
				fn:function(combo, valor) {
				 		BFwrk.Util.updateComboToTextField(PF+'txtIdDivisaOriginal', NS.cmbDivisaOriginal.getId());
						NS.accionarDivisaOriginal(combo.getValue());
				}
			}
		}
	});	
	
	
	NS.accionarDivisaPago = function(valueCombo) {
		NS.idDivPago = valueCombo;
	}
	
	//combo Divisa Pago
	NS.cmbDivisaPago = new Ext.form.ComboBox({
		store: NS.storeDivisas
		,name: PF+'cmbDivisaPago'
		,id: PF+'cmbDivisaPago'
		,typeAhead: true
		,mode: 'local'
		,minChars: 0
		,selecOnFocus: true
		,forceSelection: true
        ,x: 300
        ,y: 75
        ,width: 150
        ,tabIndex: 15
		,valueField:'idDivisa'
		,displayField:'descDivisa'
		,autocomplete: true
		,emptyText: 'Seleccione divisa pago'
		,triggerAction: 'all'
		,value: 'PESOS'
		//,visible: false
		,listeners:{
			select:{
				fn:function(combo, valor) {
						BFwrk.Util.updateComboToTextField(PF+'txtIdDivisaPago', NS.cmbDivisaPago.getId());
						NS.accionarDivisaPago(combo.getValue());
				}
			}
		}
	});	
	
	
	var winNuevoProvPagoCruzado = new Ext.Window({
		title: 'Nuevo Proveedor Pago Cruzado',
		modal: true,
		shadow: true,
		//closable: false,
		closeAction: 'hide',
		width: 500,
	   	height: 240,
	   	layout: 'absolute',
	   	plain: true,
	    bodyStyle: 'padding:10px;',
	    buttonAlign: 'center',
	   	items: [
	   	        {
					xtype: 'fieldset',
					title: 'Seleccione Proveedor',
					width: 510,
					height: 200,
					x: 10,
					y: 5,
					layout: 'absolute',
					id: PF + 'fieldProvNuevoPagCruz',
					items: [
				
					        {
					        	xtype: 'label',
					        	text: 'Proveedor',
					        	x: 10,
					        	y: 5
					        },
					        {
					        	xtype: 'textfield',
					        	x: 75,
					        	y: 5,
					        	width: 90,
					        	name: PF+'txtIdNuevoProvCruz',
					        	id: PF+'txtIdNuevoProvCruz',
					        	hidden: false, //true
					        	listeners: {
					        		change: {
					        			fn:function(caja) {
					        				if(caja.getValue() != null && caja.getValue() != undefined && caja.getValue() != '') {
					        					NS.storeUnicoBeneficiarioNvoPag.baseParams.registroUnico=true;
					        					NS.storeUnicoBeneficiarioNvoPag.baseParams.condicion=''+caja.getValue();
					        					NS.storeUnicoBeneficiarioNvoPag.load();
					        				}else {
					        					NS.cmbBeneficiarioNvoPag.reset();
					        					NS.storeUnicoBeneficiarioNvoPag.removeAll();
					        					
					        				}	
					        			}
					        		}
					        	}
					        },
					        {
					        	xtype: 'button',
					        	text: '...',
					        	x: 440,
					        	y: 5,
					        	width: 20,
					        	height: 22,
					        	id: PF+'btnNuevoBenefCruz',
					        	name: PF+'btnNuevoBenefCruz',
					        	hidden: false,
					        	listeners:{
					        		click:{
					        			fn:function(e){
					        				if(NS.parametroBus==='') {
												  Ext.Msg.alert('SET','Es necesario agregar una letra o nombre');
					        				}else{
					        					NS.storeBeneficiario.baseParams.condicion=NS.parametroBus;
					        					NS.storeBeneficiario.load();
					        				}
					        			}
					        		}
					        	}
					        },
					        NS.cmbBeneficiarioNvoPag,
					        {
		                    	   xtype: 'label',
								   text: 'Divisa Original',
								   x: 10,
								   y: 55
							  },
							  {
								  	xtype: 'uppertextfield',
			                        x: 15,
			                        y: 75,
			                        width: 40,
			                        //tabIndex: 9,
			                        id: PF+'txtIdDivisaOriginal', 
			                        name: PF+'txtIdDivisaOriginal',
			                        value: 'MN',
			                        listeners:{
			                        	change:{
			                        		fn:function(caja){
			                    				if(caja.getValue() != null && caja.getValue() != undefined && caja.getValue() != '') {
				                        			var valueCombo = BFwrk.Util.updateTextFieldToCombo(PF+'txtIdDivisaOriginal', NS.cmbDivisaOriginal.getId());
				                        			
					                        		if(valueCombo != null && valueCombo != undefined && valueCombo != '')
					                        			NS.accionarDivisaOriginal(valueCombo);
					                        		else
					                        			Ext.getCmp(PF + 'txtIdDivisaOriginal').reset();
			                    				}else
			                    					NS.cmbDivisaOriginal.reset();
			                        		}
			                        	}
			                        }
							  },
							  NS.cmbDivisaOriginal,
							  {
		                    	   xtype: 'label',
								   text: 'Divisa Pago',
								   x: 250,
								   y: 55
							  },
							  {
								  	xtype: 'uppertextfield',
			                        x: 255,
			                        y: 75,
			                        width: 40,
			                        //tabIndex: 14,
			                        id: PF+'txtIdDivisaPago', 
			                        name:PF+'txtIdDivisaPago',
			                        value: 'MN',
			                        listeners:{
			                        	change:{
			                        		fn:function(caja, valor) {
				                    			if(caja.getValue() != null && caja.getValue() != undefined && caja.getValue() != '') {
				                    				var valueCombo = BFwrk.Util.updateTextFieldToCombo(PF+'txtIdDivisaPago', NS.cmbDivisaPago.getId());
				                    				
				                    				if(valueCombo != null && valueCombo != undefined && valueCombo != '')
				                    					NS.accionarDivisaPago(valueCombo);
				                    				else
				                    					Ext.getCmp(PF + 'txtIdDivisaPago').reset();
				                    			}else {
				                    				NS.cmbDivisaPago.reset();
				                    			}
			                        		}
			                        	}
			                        }
							  },
							  NS.cmbDivisaPago,
							  {
						        	xtype: 'button',
						        	text: 'Aceptar',
						        	x: 380,
						        	y: 130,
						        	width: 70,
						        	height: 22,
						        	id: PF+'btnAceptarNuevoProv',
						        	name: PF+'btnAceptarNuevoProv',
						        	hidden: false,
						        	listeners:{
						        		click:{
						        			fn:function(e){
						        				
					        					var matriz = new Array();
			                					var registro = {};
			                					
			                					registro.noProv = Ext.getCmp(PF + 'txtIdNuevoProvCruz').getValue();
			                					registro.divOriginal = Ext.getCmp(PF + 'txtIdDivisaOriginal').getValue();
			                					registro.divPago = Ext.getCmp(PF + 'txtIdDivisaPago').getValue();
		                					
			                					matriz[0] = registro;
			                						
				                				var jSonString = Ext.util.JSON.encode(matriz);
				                				
				                				//Valida que no exista el proveedor en el catalogo de pagos cruzados y obtiene el consecutivo.
				                				//Inserta nuevo proveedor
												ConsultaPropuestasAction.insertaNuevoProvPagoCruzado(jSonString, function(resultado, e) {
													
													if(e.message=="Unable to connect to the server."){
														Ext.Msg.alert('SET','Error de conexión al servidor');
														return;
													}
													
													if (resultado != '' && resultado != undefined) {
														Ext.Msg.alert('SET', resultado);
														if(resultado == 'Proveedor registrado con éxito!'){
															
															//Inserta bitacora
															ConsultaPropuestasAction.insertaBitacoraPropuesta('nvoPagCruz', '', 
																	'', 'Prov:' +Ext.getCmp(PF + 'txtIdNuevoProvCruz').getValue() +'DivOrig:' +Ext.getCmp(PF + 'txtIdDivisaOriginal').getValue() + 'DivPago:'+Ext.getCmp(PF + 'txtIdDivisaPago').getValue() 
																	, function(result, e){
																
																if(e.message=="Unable to connect to the server."){
																	Ext.Msg.alert('SET','Error de conexión al servidor');
																	return;
																}
																
																if(result != null && result.length != '') {
																	if(result.error != ''){
																		Ext.Msg.alert('SET',''+result.error);
																	}
																}
															});
															
															NS.storePagoCruzado.baseParams.noPersona='';
								        					NS.storePagoCruzado.load();
															winNuevoProvPagoCruzado.hide();
														}
														
														return;
													}
												});
				                				
						        			}
						        		}
						        	}
						        } 
					        ]
						}
	    ],listeners:{
       		show:{
       			fn:function(){
       				Ext.getCmp(PF+'txtIdDivisaPago').setValue("");
       				Ext.getCmp(PF+'txtIdDivisaOriginal').setValue("");
       				Ext.getCmp(PF+'txtIdNuevoProvCruz').setValue("");
       				Ext.getCmp(PF+'cmbDivisaOriginal').setValue("PESOS");
       				Ext.getCmp(PF+'cmbDivisaPago').setValue("PESOS");
       				NS.storeBeneficiario.removeAll();
       				NS.storeUnicoBeneficiarioNvoPag.removeAll();
       			}
       		}
       	}		 
	});
	   	        
	var winPagosCruzados = new Ext.Window({
		title: 'Pagos Cruzados',
		modal: true,
		shadow: true,
		//closable: false,
		closeAction: 'hide',
		width: 550,
	   	height: 450,
	   	layout: 'absolute',
	   	plain: true,
	    bodyStyle: 'padding:10px;',
	    buttonAlign: 'center',
	   	items: [
				{
					xtype: 'fieldset',
					title: 'Seleccione Proveedor',
					width: 510,
					height: 65,
					x: 10,
					y: 5,
					layout: 'absolute',
					id: PF + 'fieldProvPagCruz',
					items: [
		        
					        {
					        	xtype: 'label',
					        	text: 'Proveedor',
					        	x: 0,
					        	y: 5
					        },
					        {
					        	xtype: 'textfield',
					        	x: 55,
					        	y: 5,
					        	width: 90,
					        	name: PF+'txtIdProveedorCruz',
					        	id: PF+'txtIdProveedorCruz',
					        	hidden: false, //true
					        	listeners: {
					        		change: {
					        			fn:function(caja) {
					        				if(caja.getValue() != null && caja.getValue() != undefined && caja.getValue() != '') {
					        					NS.storeUnicoBeneficiarioCruz.baseParams.noPersona=''+caja.getValue();
					        					NS.storeUnicoBeneficiarioCruz.load();
					        					//actualiza grid
					        					NS.storePagoCruzado.baseParams.noPersona=''+caja.getValue();
					        					var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storePagoCruzado, msg:"Cargando..."});
					        					NS.storePagoCruzado.load();
					        				} else {
					        					NS.cmbBeneficiarioCruz.reset();
					        					NS.storeUnicoBeneficiarioCruz.removeAll();
					        				}	
					        			}
					        		}
					        	}
					        },
					        {
					        	xtype: 'button',
					        	text: '...',
					        	x: 420,
					        	y: 5,
					        	width: 20,
					        	height: 22,
					        	id: PF+'btnComboBenefCruz',
					        	name: PF+'btnComboBenefCruz',
					        	hidden: false,
					        	listeners:{
					        		click:{
					        			fn:function(e){
					        				if(NS.parametroBus==='') {
					        					//Si la busqueda es vacia, carga todos los proveedores con pagos cruzados
					        					NS.storePagoCruzado.baseParams.noPersona='';
					        					var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storePagoCruzado, msg:"Cargando..."});
					        					NS.storePagoCruzado.load();
					        				}else{

					        					NS.storeBeneficiarioPagoCruzado.baseParams.noPersona = NS.parametroBus; 
					        					NS.storeBeneficiarioPagoCruzado.load();
					        					//Carga el grid con los resultados
					        					NS.storePagoCruzado.baseParams.noPersona = NS.parametroBus;
					        					var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storePagoCruzado, msg:"Cargando..."});
					        					NS.storePagoCruzado.load();
					        				}
					        			}
					        		}
					        	}
					        },
					        NS.cmbBeneficiarioCruz
					]
					},//Fin fieldset Proveedor
					{
						xtype: 'fieldset',
						title: 'Consulta',
						width: 510,
						height: 300,
						x: 10,
						y: 90,
						layout: 'absolute',
						id: PF + 'fieldConsultaPagCruz',
						items: [
						        	NS.gridConsultasPagoCruzado,
						        	{
							        	xtype: 'button',
							        	text: 'Crear Nuevo',
							        	x: 300,
							        	y: 215,
							        	width: 80,
							        	height: 22,
							        	id: PF+'btnNuevoPagoCruzado',
							        	name: PF+'btnNuevoPagoCruzado',
							        	hidden: false,
							        	listeners:{
							        		click:{
							        			fn:function(e){	
							        				winNuevoProvPagoCruzado.show();
							        			}
							        		}
							        	}
							        },
							        {
							        	xtype: 'button',
							        	text: 'Eliminar',
							        	x: 390,
							        	y: 215,
							        	width: 80,
							        	height: 22,
							        	id: PF+'btnEliminarPagoCruzado',
							        	name: PF+'btnEliminarPagoCruzado',
							        	hidden: false,
							        	listeners:{
							        		click:{
							        			fn:function(e){
							        				
							        				var regSeleccionados = NS.gridConsultasPagoCruzado.getSelectionModel().getSelections();
			                						
			                						if(regSeleccionados.length === 0){
			                							Ext.Msg.alert('SET', "Seleccione un proveedor");
			                							return;
			                						}
			                						
							        				Ext.Msg.confirm('SET','Se eliminará el pago cruzado del proveedor seleccionado, ¿Desea continuar?',function(btn) {
					                					if(btn === 'yes') {
					                						
					                						var noProv = regSeleccionados[0].get("id");
					                						var divOrig = regSeleccionados[0].get("divisaOrig");
					                						
					                						ConsultaPropuestasAction.eliminarProvPagoCruzado(noProv, divOrig, function(resultado, e) {
																
					                							if(e.message=="Unable to connect to the server."){
					                								Ext.Msg.alert('SET','Error de conexión al servidor');
					                								return;
					                							}
					                							
					                							if (resultado != '' && resultado != undefined) {
																	Ext.Msg.alert('SET', resultado);
																	
																	if(resultado == 'Registro eliminado con éxito!'){
																		
																		//Inserta bitacora documento
																		ConsultaPropuestasAction.insertaBitacoraPropuesta('elimPagCruz', '', 
																				'Prov:'+noProv+'divOrig:'+divOrig+'', '' 
																				, function(result, e){
																			
																			if(e.message=="Unable to connect to the server."){
																				Ext.Msg.alert('SET','Error de conexión al servidor');
																				return;
																			}
																			
																			if(result != null && result.length != '') {
																				if(result.error != ''){
																					Ext.Msg.alert('SET',''+result.error);
																				}
																			}
																		});
																		
																		
																		NS.storePagoCruzado.baseParams.noPersona = '';
											        					NS.storePagoCruzado.load();
											        					
											        					//Actualiza los combos de banco pagador y beneficiario
											        					NS.cmbBancoPag.reset();
											        					NS.cmbBancoPagBenef.reset();
											        					NS.cmbChequeraPag.reset();
											        					NS.storeChequeraPag.removeAll();
											        					NS.storeChequeraPagBenef.removeAll();
											        					NS.cmbChequeraPagBenef.reset();
											        					Ext.getCmp(PF + 'txtBanco').setValue('');
											        					Ext.getCmp(PF + 'txtBancoBenef').setValue('');
											        					
											        					var regSelec=NS.gridDetalle.getSelectionModel().getSelections();
											        					if(regSelec.length >0){
											        						
											        						NS.sDivisa = regSelec[0].get('idDivisa');
											        						
											        						var noEmpresaTmp = regSelec[0].get('noEmpresa');
											        						NS.storeBancoPag.baseParams.idDivisa = NS.sDivisa;
											        						NS.storeBancoPag.baseParams.noEmpresa = ''+noEmpresaTmp;
											        						NS.storeBancoPag.load();
											        						
											        						if(regSelec[0].get('descFormaPago') == 'TRANSFERENCIA' || regSelec[0].get('descFormaPago') == 'TRASPASO'){
											                  					NS.noPersona = regSelec[0].get('noCliente');
											            	          			NS.storeBancoPagBenef.baseParams.idDivisa = NS.sDivisa;
											            	          			NS.storeBancoPagBenef.baseParams.noPersona = parseInt(NS.noPersona);
											            	          			
											            	          			NS.storeBancoPagBenef.load();
											                  				}
											        	          			
											        						//NS.storeBancoPag.load();
												        					
											        						
											        					}
											        					
											        					
																	}
																	
																	return;
																}
															});
					                						
					                					}
					                				});
							        			}
							        		}
							        	}
							        }
						        ]
					}//Fin fieldset consultas
   	        	],
   	        	listeners:{
   	        		show:{
   	        			fn:function(){
   	        				Ext.getCmp(PF+'txtIdProveedorCruz').setValue("");
   	        				Ext.getCmp(PF+'cmbBeneficiarioCruz').setValue("");
   	        				NS.gridConsultasPagoCruzado.store.removeAll();
							//NS.storePagoCruzado.getView().refresh();
   	        				NS.storePagoCruzado.baseParams.noPersona = '';
   	        				var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storePagoCruzado, msg:"Cargando..."});
   	        				NS.storePagoCruzado.load();
   	        				NS.storeBeneficiarioPagoCruzado.removeAll();
   	        				NS.storeUnicoBeneficiarioCruz.removeAll();
   	        				NS.storeBeneficiario.removeAll();
   	        			 
   	        			}
   	        		}
   	        	}	
		});
	
	/***********************************************
	 **    Termina Ventana de Pagos Cruzados	  **
	 ***********************************************/
	
	
	/***************************************************
	 * 	VENTANA DE PROVEEDORES BLOQUEADOS AUTORIZACION *
	 ***************************************************/
	
	NS.storeAutorizarProvBloq = new Ext.data.DirectStore({
		paramsAsHash: false,
		baseParams:{
			empresas:'',
			propuestas: '' //Inserta claves control
		},
		paramOrder:['empresas','propuestas'],
		root: '',
		directFn: ConsultaPropuestasAction.consultaProveedoresBloqueados, 
		//idProperty: 'id', 
		fields: [
		     {name: 'cveControl'},   //nombre del atributo DTO 
			 {name: 'empresa'},
			 {name: 'proveedor'},
			 {name: 'noDocumento'},
			 {name: 'fechaPago'},
			 {name: 'importe'},
			 {name: 'concepto'},
			 {name: 'fechaBloqueo'}, 
			 {name: 'usuario'},
			 {name: 'motivo'}
		],
		listeners: {
			load: function(s, records){
				var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeAutorizarProvBloq, msg:"Cargando..."});
				if(records.length==null || records.length<=0){
					//Ext.Msg.alert('SET','No existen registros bloqueados');
					//Proceso de autorizacion normal
					NS.autoDesauto='AUT';
					winLogin.show();
				}else{
					winAutorizarBloqueados.show();
					Ext.Msg.alert('SET','registros bloqueados');
				}
				
			},
			exception: function(mist){
				Ext.Msg.alert('SET','Error en conexión al servidor');
				mascara.hide();
			}
		}
	}); 
	

 	//Columnas del grid autorizacion de proveedores bloqueados	    	
 	NS.columnasAutorizacionProvBloq = new Ext.grid.ColumnModel
 	([	
 	  	//NS.seleccion,
 	  	
 	  	{	
 			header :'Propuesta',
 			width :100,
 			sortable :true,
 			dataIndex :'cveControl'
 		},{
 			header :'Empresa',
 			width :100,
 			sortable :true,
 			dataIndex :'empresa'
 		},{
 			header :'Proveedor',
 			width :100,
 			sortable :true,
 			dataIndex :'proveedor'
 		},{
 			header :'Documento',
 			width :100,
 			sortable :true,
 			dataIndex :'noDocumento'
 		},{	
 			header :'Fecha Pago',
 			width :100,
 			sortable :true,
 			dataIndex :'fechaPago', //fechaPropuesta
 			renderer: function (value, meta, record) {
 	            meta.attr = 'style=' + record.get('color');
 	            return value;
 			}
 		},{
 			header :'Importe',
 			width :100,
 			sortable :true,
 			dataIndex :'importe',
 			hidden: true,
 			renderer: function (value, meta, record) {
 	            meta.attr = 'style=' + record.get('color');
 	            return '$ ' + NS.formatNumber(Math.round((value)*100)/100 );
 	        }
 		},{
 			header :'Concepto',
 			width :120,
 			sortable :true,
 			dataIndex :'concepto',
 			renderer: function (value, meta, record) {
 	            meta.attr = 'style=' + record.get('color');
 	            return value;
 	        }
 		},{	
 			header :'Fecha Bloqueo',
 			width :100,
 			sortable :true,
 			dataIndex :'fechaBloqueo', //fechaPropuesta
 			renderer: function (value, meta, record) {
 	            meta.attr = 'style=' + record.get('color');
 	            return value;
 			}
 		},{
			header :'Usuario',
			width :80,
			sortable :true,
			dataIndex :'usuario',
 		},{
			header :'Motivo',
			width :100,
			sortable :true,
			dataIndex :'motivo',
		}
 	  ]);
 	
	//grid de datos
	 NS.gridConsultaProveedoresBloqueados = new Ext.grid.GridPanel({
      store : NS.storeAutorizarProvBloq,
      viewConfig: {emptyText: 'Proveedores con bloqueo'},
      x:0,
      y:0,
      cm: NS.columnasAutorizacionProvBloq,
	  //sm: NS.seleccion,
      width:490,
      height:225,
      frame:true,
      listeners: {
      	click: {
      		fn:function(e) {
      			
      			/*var regSeleccionados = NS.gridConsultasPagoCruzado.getSelectionModel().getSelections();
      			
      			if(regSeleccionados != null && regSeleccionados != undefined && regSeleccionados != '') {
      				
      			}else {
      				
      			}*/
      		}
      	}
      }
  });
	 
	var winAutorizarBloqueados = new Ext.Window({
		title: 'Informe de Proveedores/ Documentos Bloqueados',
		modal: true,
		shadow: true,
		//closable: false,
		closeAction: 'hide',
		width: 550,
	   	height: 350,
	   	layout: 'absolute',
	   	plain: true,
	    bodyStyle: 'padding:10px;',
	    buttonAlign: 'center',
	   	items: [
				{
					xtype: 'fieldset',
					title: 'Proveedores/Documentos Bloqueados',
					width: 510,
					height: 330,
					x: 10,
					y: 2,
					layout: 'absolute',
					//id: PF + 'fieldProvPagCruz',
					items: [
					        NS.gridConsultaProveedoresBloqueados,
					        {
					        	xtype: 'label',
					        	text: '¿Desea autorizar sin considerar proveedores/documentos bloqueados? ',
					        	x: 10,
					        	y: 255
					        },
					        {
					        	xtype: 'button',
					        	text: 'Aceptar',
					        	x: 400,
					        	y: 235,
					        	width: 80,
					        	height: 22,
					        	id: PF+'btnAutorizarBloqueados',
					        	name: PF+'btnAutorizarBloqueados',
					        	hidden: false,
					        	listeners:{
					        		click:{
					        			fn:function(e){
					        				
					        				//Se guardan las claves de SAP, estas claves no se autorizaran
					        				var regNoProcesar = NS.storeAutorizarProvBloq.data.items;
					        				var cont = 0;
					        				matrizBloqGlob = new Array();
					        				
					        				for(var i = 0; i < regNoProcesar.length; i++){
			                					if(regNoProcesar[i].get('usuario')=='SAP'){
			                						matrizBloqGlob[cont] = regNoProcesar[i].get('cveControl');
			                						cont = cont + 1 ; 
			                					}
			                				}

	                						NS.autoDesauto='AUT';
	                						winLogin.show();
	                						winAutorizarBloqueados.hide();		
					        			}
					        			
					        		}
					        	}
					        },
					        {
					        	xtype: 'button',
					        	text: 'Cancelar',
					        	x: 400,
					        	y: 260,
					        	width: 80,
					        	height: 22,
					        	id: PF+'btnCancelarBloqueados',
					        	name: PF+'btnCancelarBloqueados',
					        	hidden: false,
					        	listeners:{
					        		click:{
					        			fn:function(e){
					        				winAutorizarBloqueados.hide();
					        			}
					        		}
					        	}
					        }
					]
					
					}//Fin fieldset consultas
   	        	],
   	        	listeners:{
   	        		show:{
   	        			fn:function(){
   	        				//Se pueden Limpiar componentes   	        			
   	        			}
   	        		}
   	        	}	
		});
	 
	/***********************************************************
	 * 	TERMINA VENTANA DE PROVEEDORES BLOQUEADOS AUTORIZACION *
	 ***********************************************************/
	
	
	/************************************************************************************************
	 *  					Termina Metodos Agregados por EMS										*
	 * *********************************************************************************************/
	
	NS.btnModifProp = apps.SET.btnFacultativo( new Ext.Button({
        text: 'Modificar',
        x: 900,
        y: 210,
        width: 80,
        id: 'btnModifProp',
        name: 'btnModifProp',
        hidden:false, 
        listeners: {
        	click: {
        		fn:function(e) {
        			NS.modificarPropuestaMaestro();
        		}
        	}
    	}
    }));
    
	
	//NS.fieldsetAutorizar = apps.SET.btnFacultativo({
	NS.fieldsetAutorizar = apps.SET.btnFacultativo( new Ext.form.FieldSet({
 		   	//xtype: 'fieldset',
 	        title: '',
 	        id:'fieldAutorizar',
 	        name:'fieldAutorizar',
 	        border: false,
 	       	x: 340,
 	        y: 110,
 	        width: 120,
 	        height: 75,
 	        layout: 'absolute',
 	        items: [
				{
				    xtype: 'button',
				    text: 'Autorizar',
				    x: 0,
				    y: 0,
				    width: 100,
				    id: PF+'cmdAutorizar',
				    name: PF+'cmdAutorizar',
				    listeners: {
				    	click: {
				    		fn:function(e) {
									NS.recordsAuto = NS.gridDetalle.getSelectionModel().getSelections();
									var matrizCancela = new Array();
									var matrizPOrig = new Array();
									var matrizPModi = new Array();
									var c = 0;
									var c1 = 0;
									var c2 = 0;
				
									habilitarBotones(false);
									//mascara.show();
									
									if((NS.registros != null && NS.recordsAuto != null) && (NS.registros.length == NS.recordsAuto.length)){
										ConsultaPropuestasAction.validaFacultadUsuario("fieldAutorizar", function(result, e) {

											if(e.message=="Unable to connect to the server."){
												Ext.Msg.alert('SET','Error de conexión al servidor');
												return;
											}
											
											NS.autCheq = 0;
					   						
					   						if(result == 0){
					            				Ext.Msg.alert('SET', 'No cuenta con la facultad para autorizar propuestas!!');
					   							habilitarBotones(true);
					   							//mascara.hide();
					   						}else {
					            				NS.autCheq = result;
					            				var recordsAutorizar=NS.gridMaestroPropuestas.getSelectionModel().getSelections();
					                			if(recordsAutorizar.length===0) {
					                				Ext.Msg.alert('SET','Es necesario seleccionar algun registro');
					                				habilitarBotones(true);
					                				//mascara.hide();
					                			}else {
					                				
					                				//***** bloqueados
					                				
					                				NS.validaAutorizacionBloqueados();
	
					                			}
					                		}
					            		});
									}else{
										ConsultaPropuestasAction.validaFacultadUsuario("fieldAutorizar", function(result, e) {
				   						
											if(e.message=="Unable to connect to the server."){
												Ext.Msg.alert('SET','Error de conexión al servidor');
												return;
											}
											
											NS.autCheq = 0;
				   						
				   						if(NS.registros == null) NS.registros = NS.gridDetalle.getSelectionModel().getSelections();
				   						
				   						if(result == 0){
				            				Ext.Msg.alert('SET', 'No cuenta con la facultad para autorizar propuestas!!');
				   							habilitarBotones(true);
				   							//mascara.hide();
				   						}else {
				            				
				            				NS.autCheq = result;
				            				var recordsAutorizar=NS.gridMaestroPropuestas.getSelectionModel().getSelections();
				                			if(recordsAutorizar.length===0) {
				                				Ext.Msg.alert('SET','Es necesario seleccionar algún registro');
				                				habilitarBotones(true);
				                				//mascara.hide();
				                			}else {
				                				
				                				//***** bloqueados
				                				NS.validaAutorizacionBloqueados();
				                			}
				                		}
				                		
				            		});
									}
									
				           	}
							}
						}
					},{
				    xtype: 'button',
				    text: 'Eliminar Autorización',
				    x: 0,
				    y: 30,
				    width: 100,
				    id: PF+'cmbQuitAut',
				    name: PF+'cmbQuitAut',
				    listeners:{
					click:{
						fn:function(e){
							var recordsDesAutorizar=NS.gridMaestroPropuestas.getSelectionModel().getSelections();
						
							if(recordsDesAutorizar.length===0)
							{
								Ext.Msg.alert('SET','Es necesario seleccionar algun registro');
							}
							else
							{
								habilitarBotones(false);
	            		   		NS.autoDesauto='DAUT';
								winLogin.show();	
							}
						}
					}
					 }
				},
 			]
		   	}));
		   	

	NS.cmbEmpresaMulti = new Ext.form.ComboBox({
		store: NS.storeEmpresas
		,name: PF+'cmbEmpresaMulti'
		,id: PF+'cmbEmpresaMulti'
		,typeAhead: true
		,mode: 'local'
		,minChars: 0
		,selecOnFocus: true
		,forceSelection: true
        ,x: 60
        ,y: 0
        ,width: 190
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
					BFwrk.Util.updateComboToTextField(PF+'txtEmpresaMulti',NS.cmbEmpresaMulti.getId());
					
					NS.noEmpresaMulti = valor.data.noEmpresa;
					
					var recSelectDetProp = NS.gridDetalle.getSelectionModel().getSelections();
					
					if(recSelectDetProp.length <= 0){
						Ext.Msg.alert('SET','No hay detalles seleccionados.');
						return;
					}
							
					NS.sDivisa = recSelectDetProp[0].get('idDivisa');
					
					NS.storeBancoPagMulti.baseParams.idDivisa = NS.sDivisa;
					NS.storeBancoPagMulti.baseParams.noEmpresa = ''+NS.noEmpresaMulti;
					NS.storeBancoPagMulti.load();
						
				}
			}
		}
	});

	
	//store Banco Pago
    NS.storeBancoPagMulti = new Ext.data.DirectStore({
		paramsAsHash: false,
		baseParams: { idDivisa: '', noEmpresa : ''},
		root: '',
		paramOrder:['idDivisa', 'noEmpresa'],
		directFn: ConsultaPropuestasAction.llenarComboBancoPag,
		idProperty: 'id', 
		fields: [
				 {name: 'id'},
				 {name: 'descripcion'}
			 ],
		listeners: {
			load: function(s, records){
				var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeBancoPagMulti, msg:"Cargando..."});
				
				if(records.length == null || records.length <= 0)
					Ext.Msg.alert('SET','No existen bancos para el pago multisociedad.');
			},
			exception: function(mist){
				Ext.Msg.alert('SET','Error en conexión al servidor');
				mascara.hide();
			}
		}
    });
    
    //combo Banco Pago
	NS.cmbBancoPagMulti = new Ext.form.ComboBox({
		store: NS.storeBancoPagMulti
		,name: PF + 'cmbBancoPagMulti'
		,id: PF + 'cmbBancoPagMulti'
		,typeAhead: true
		,mode: 'local'
		,minChars: 0
		,selecOnFocus: true
		,forceSelection: true
      	,x: 340
        ,y: 0
        ,width: 150
		,valueField:'id'
		,displayField:'descripcion'
		,autocomplete: true
		,emptyText: 'Banco de Pago'
		,triggerAction: 'all'
		,value: ''
		,listeners:{
			select:{
				fn:function(combo, valor) {
					BFwrk.Util.updateComboToTextField(PF + 'txtBancoMulti', NS.cmbBancoPagMulti.getId());
					
					if(combo.getValue() != 0) {
						NS.storeChequeraPagMulti.removeAll();
						NS.cmbChequeraPagMulti.reset();
						NS.storeChequeraPagMulti.baseParams.idBanco = combo.getValue();
						NS.storeChequeraPagMulti.baseParams.noEmpresa = parseInt(NS.noEmpresaMulti);
						NS.storeChequeraPagMulti.baseParams.idDivisa = NS.sDivisa;
						NS.storeChequeraPagMulti.load();
					}
				}
			}
		}
	});
	
	//store Chequera Pago
    NS.storeChequeraPagMulti = new Ext.data.DirectStore({
		paramsAsHash: false,
		baseParams: { idBanco: 0, noEmpresa : 0, idDivisa: ''},
		root: '',
		paramOrder:['idBanco', 'noEmpresa', 'idDivisa'],
		directFn: ConsultaPropuestasAction.llenarComboChequeraPag,
		idProperty: 'descripcion', 
		fields: [
				 {name: 'descripcion'},
				 {name: 'descripcion'}
			 ],
		listeners: {
			load: function(s, records){
				var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeChequeraPagMulti, msg:"Cargando..."});
				
				if(records.length == null || records.length <= 0)
					Ext.Msg.alert('SET','No existen chequera para el pago multisociedad.');
			},
			exception: function(mist){
				Ext.Msg.alert('SET','Error en conexión al servidor');
				mascara.hide();
			}
		}
    });
    
    //combo Chequera Pagadora
	NS.cmbChequeraPagMulti = new Ext.form.ComboBox({
		store: NS.storeChequeraPagMulti
		,name: PF + 'cmbChequeraPagMulti'
		,id: PF + 'cmbChequeraPagMulti'
		,typeAhead: true
		,mode: 'local'
		,minChars: 0
		,selecOnFocus: true
		,forceSelection: true
      	,x: 500
        ,y: 0
        ,width: 150
		,valueField:'descripcion'
		,displayField:'descripcion'
		,autocomplete: true
		,emptyText: 'Chequera de Pago'
		,triggerAction: 'all'
		,value: ''
	});
	
	
	//**************************************************** Termina el codigo para el detalle de las propuestas *****************************************//	

	NS.btnMultiSociedad = apps.SET.btnFacultativo( new Ext.Button({
        text: 'Multisociedad',
        x: 435,
        y: 240,
        width: 80,
        id: 'btnMultiSociedad',
        name: 'btnMultiSociedad',
        hidden:true, 
        listeners: {
        	click: {
        		fn:function(e) {
        			var registroSeleccionado = NS.gridDetalle.getSelectionModel().getSelections();
    				
    				NS.storeEmpresas.load();
    				
    				Ext.getCmp(PF + 'contFormaPago').setDisabled(false);
    				Ext.getCmp(PF+'fieldMultiSociedad').setDisabled(false);
    				NS.multisociedad = true;
    				NS.habilitaDiversos(false);
    				NS.limpiaCamposMultisociedad();
        		}
        	}
    	}
    }));
	
	
NS.ConsultaPropuestas = new Ext.form.FormPanel ({
    title: 'Bitácora de propuestas',
    width: 1020,
    height: 936,
    padding: 10,
    frame: true,
    autoScroll: true,
    layout: 'absolute',
    id: PF + 'contenedorConsultaPropuestas',
    name: PF+ 'contenedorConsultaPropuestas',
    renderTo: NS.tabContId,
        items:[
            {
                xtype: 'fieldset',
                title: '',
                width: 1010,
                height: 815,
                x: 20,
                y: 5,
                layout: 'absolute',
                id: PF + 'nvaFecPago',
                items: [                    
						{
						    xtype: 'button',
						    text: 'Limpiar',
						    x: 885,
						    y: 770,
						    width: 80,
						    id: 'btnLimpiar',
						    listeners:{
						    	click:{
						    	fn:function(e){
										NS.limpiarTodo();
						    		}
						    	}
						    }
						},
                       NS.cmbGrupoEmpresas,
                       NS.contReglas,
                    /*{
                        xtype: 'textfield',
                        x: 10,
                        y: 165,
                        width: 30,
                        name: PF+'txtIdGrupo',
                        id: PF+'txtIdGrupo',
                        hidden: false, //true
                        tabIndex: 12,
                        listeners: {
                        	change: {
                        		fn:function(caja) {
                    	   			if(caja.getValue() != null && caja.getValue() != undefined && caja.getValue() != '') {
	                        			valueCombo = BFwrk.Util.updateTextFieldToCombo(PF+'txtIdGrupo',NS.cmbGrupo.getId());
	                        			
	                        			if(valueCombo != null && valueCombo != undefined && valueCombo != '')
	                        				NS.storeMaestro.baseParams.idGrupoRubro = valueCombo;
	                        			else
	                        				Ext.getCmp(PF + 'txtIdGrupo').reset();
                    	   			}else
                    	   				NS.cmbGrupo.reset();
                        		}
                        	}
                        }
                    },*/
                    {
                        xtype: 'textfield',
                        x: 10,
                        y: 120,
                        width: 120,
                        id: PF+'txtIdProveedor',
                        name: PF+'txtIdProveedor',
                        tabIndex: 10,
                        hidden: false, //true
                        listeners:{
                        	change:{
                        		fn:function(caja, valor){
                        			if(caja.getValue() != null && caja.getValue() != undefined && caja.getValue() != '') {
                        				NS.storeUnicoBeneficiario.baseParams.registroUnico=true;
                        				NS.storeUnicoBeneficiario.baseParams.condicion=''+caja.getValue();
                        				NS.storeUnicoBeneficiario.load();
                       				}else {
                       					NS.cmbBeneficiario.reset();
                       					NS.storeUnicoBeneficiario.removeAll();
                       				}
                        		}
                        	}
                        }
                    },
                    NS.txtIdEmpresa,
                   NS.cmbBeneficiario,
                   //NS.cmbGrupo,
                   {
                        xtype: 'button',
                        text: 'Buscar',
                        x: 350, //260
                        y: 180, //165
                        width: 100,
                        height: 22,
                        id: PF + 'btnBuscar',
                        name: PF + 'btnBuscar',
                        listeners:{
                        click:{
                        		fn:function(e){
									
									NS.buscar();
///									Ext.getCmp(PF + 'btnBuscar').setDisabled(true);
                        		}
                        	}
                        }
                    },{
	                        xtype: 'button',
	                        text: '...',
	                        x: 315,
	                        y: 120,
	                        width: 20,
	                        height: 22,
	                        id: PF+'btnComboBenef',
	                        name: PF+'btnComboBenef',
	                        hidden: false, //true
	                        listeners:{
	                        	click:{
	                        		fn:function(e){
	                        		if(NS.parametroBus==='' || (isNaN(NS.parametroBus) && NS.parametroBus.length < 4)) {
	                        			Ext.Msg.alert('SET','Es necesario escribir un mínimo de 4 caracteres para la búsqueda');
	                        		}else{
	                        			NS.storeBeneficiario.baseParams.condicion=NS.parametroBus;
	                        			NS.storeBeneficiario.load();
	                        		}
	                       		}
	                   		}
	               		}
               		},{
                        xtype: 'label',
                        text: 'Grupo Empresas:',
                        x: 10
                    },
                    {
                        xtype: 'label',
                        text: 'Proveedor:',
                        x: 10,
                        hidden: false, //true
                        y: 105
                    },
                    {
                        xtype: 'checkbox',
                        id: PF + 'chkTodosUsuarios',
                        x: 10,
                        y: 145,
                        boxLabel: 'Todos los usuarios',
                        listeners:{
                        check:{
                        		fn:function(checkBox,valor){
	                        		if(valor==true) {
	                     				NS.storeMaestro.baseParams.todosUsuarios = ''+valor;
	                     				NS.todosUsuarios = true;
	                        		}else{
	                     			  	NS.storeMaestro.baseParams.todosUsuarios= 'false';
	                     			  	NS.todosUsuarios = false;
		                        	}
                        		}
                        }
                      }
                    },
                    {
                        xtype: 'checkbox',
                        x: 10,
                        y: 35,
                        boxLabel: 'MN',
                        tabIndex: 3,
                        listeners:{
                        	check:{
                        		fn:function(checkBox,valor){
	                        		if(valor==true) {
	                        			NS.storeMaestro.baseParams.divMN=true;
	                        		}else{
	                        			NS.storeMaestro.baseParams.divMN=false;
		                        	}
                        		}
                        	}
                        }
                    },
                    {
                        xtype: 'checkbox',
                        x: 10,
                        y: 50,
                        boxLabel: 'Dólares',
                        tabIndex: 4,
                        listeners:{
                        	check:{
                        		fn:function(checkBox,valor){
	                        		if(valor==true) {
	                        			NS.storeMaestro.baseParams.divDLS=true;
	                        		}else{
	                        			NS.storeMaestro.baseParams.divDLS=false;
		                        	}
                        		}
                        	}
                        }
                    },
                    {
                        xtype: 'checkbox',
                        x: 10,
                        y: 65,
                        boxLabel: 'Euros',
                        tabIndex: 5,
                        listeners:{
                        	check:{
                        		fn:function(checkBox,valor){
	                        		if(valor==true) {
	                        			NS.storeMaestro.baseParams.divEUR=true;
	                        		}else{
	                        			NS.storeMaestro.baseParams.divEUR=false;
		                        	}
                        		}
                        	}
                        }
                    },
                    {
                        xtype: 'checkbox',
                        x: 10,
                        y: 80,
                        boxLabel: 'Otras Divisas',
                        tabIndex: 6,
                        listeners:{
                        	check:{
                        		fn:function(checkBox,valor){
	                        		if(valor==true) {
	                        			NS.storeMaestro.baseParams.divOtras=true;
	                        		}else{
	                        			NS.storeMaestro.baseParams.divOtras=false;
		                        	}
                        		}
                        	}
                        }
                    },
                    /*{
                        xtype: 'label',
                        text: 'Grupo de Rubros:',
                        x: 10,
                        hidden: false, //true
                        y: 150
                    },*/
                    
                    {
                        xtype: 'label',
                        text: '',
                        x: 340,
                        y: 150
                    },
                    /*{
                        xtype: 'checkbox',
                        x: 10,
                        y: 185,
                        boxLabel: 'Propuestas Todas',
                        checked: false,
                        listeners: {
	                        check: {
	                    		fn:function(checkBox, valor) {
	                        		if(valor == true) {
	                     				NS.storeMaestro.baseParams.todasPro = true;
	                     				NS.storeMaestro.baseParams.fecFin = apps.SET.FEC_HOY;
	                     				Ext.getCmp(PF + 'fecha1').setDisabled(true);
	                        			Ext.getCmp(PF + 'fecha2').setDisabled(true);
	                        		}else {
	                        			NS.storeMaestro.baseParams.todasPro = false;
	                        			Ext.getCmp(PF + 'fecha1').setDisabled(false);
	                        			Ext.getCmp(PF + 'fecha2').setDisabled(false);
	                        		}
	                    		}
	                        }
                    	}
                    },*/
                    {
                        xtype: 'label',
                        text: 'Rango de Fechas:',
                        x: 10,
                        y: 170
                    },
                   {
                   		xtype:'datefield',
                   		format:'d/m/Y',
                   		id:PF+'fecha1',
                   		name:PF+'fecha1',
                   		value: apps.SET.FEC_HOY,
                   		x:10,
                   		y:185,
                   		width:110,
                   		allowBlank: false,
                   		blankText:'La fecha inicial es requerida',
                   		disabled: false,
                   		listeners:{
                   			change:{
                   				fn:function(caja,valor){
                   					NS.storeMaestro.baseParams.fecIni=cambiarFecha(''+valor);
									         		
                   				}
                   			}
                   		}
                   	},
                      {
                   		xtype:'datefield',
                   		format:'d/m/Y',
                   		id:PF+'fecha2',
                   		name:PF+'fecha2',
                   		value: apps.SET.FEC_HOY,
                   		x:130,
                   		y:185,
                   		width:110,
                   		allowBlank: false,
                   		blankText:'La fecha final es requerida',
                   		disabled: false,
                   		listeners:{
                   			change:{
                   				fn:function(caja,valor){
                   					if(Ext.getCmp(PF+'fecha1').getValue()>valor)
									{
										Ext.Msg.alert('SET','La fecha inicial debe ser menor a la final');
										return;
									}
									NS.storeMaestro.baseParams.fecFin=cambiarFecha(''+valor);
                   				}
                   			}
                   		}
                   	},
                    {
                        xtype: 'button',
                        text: 'Excel Detalles',
                        x: 350,
                        y: 210,
                        width: 100,
                        id: PF + 'cmbExcel',
                        listeners: {
                   			click: {
                   				fn: function(e) {
                   					NS.Excel = true;
                   					NS.validaDatosExcel();
                   					
                   				}
                   			}
                   		}
                   	},
                    {
                        xtype: 'button',
                        text: 'Excel Resumen',
                        x: 245,
                        y: 150,
                        width: 100,
                        id: PF + 'cmbExcelPropuestas',
                        listeners: {
                   			click: {
                   				fn: function(e) {
                   					NS.Excel = true;
                   					NS.validaDatosExcelPropuestas();
                   				}
                   			}
                   		}
                   	},
                    {
                        xtype: 'button',
                        text: 'Excel Prop/Det',
                        x: 245,
                        y: 180,
                        width: 100,
                        id: PF + 'cmbExcelPropDet',
                        listeners: {
                   			click: {
                   				fn: function(e) {
                   					NS.Excel = true;
                   					NS.validaDatosExcelPropDet();
                   				}
                   			}
                   		}
                   	},
                    {
                        xtype: 'button',
                        text: 'Catálogo Colores',
                        x: 350,
                        y: 240,
                        width: 100,
                        id: PF + 'btnColores',
                        listeners: {
                   			click: {
                   				fn: function(e) {
                   					//qwer
                   					winCatalogoColores.show();
                   				}
                   			}
                   		}
                   	},/*
                    {
                        xtype: 'button',
                        text: 'Eliminar propuesta',
                        x: 245,
                        y: 210,
                        width: 100,
                        visible: false,
                        id: PF + 'btnEliminarPropuesta',
                        listeners: {
                   			click: {
                   				fn: function(e) {
                   					var regsMaestro = NS.gridMaestroPropuestas.getSelectionModel().getSelections();
                   					if (regsMaestro.length > 0) {
                   						var vec = new Array();
                   						
                   						for(var i=0; i < regsMaestro.length; i++){	 
                   							vec[i] = regsMaestro[i].get('cveControl');
	                					}
	            							
	            						var jsonCveControl = Ext.util.JSON.encode(vec);
	                   					var mask = new Ext.LoadMask(Ext.getBody(), {msg: "Ejecutando la acción..." });
	                   					
	                   					ConsultaPropuestasAction.eliminarPropuesta(jsonCveControl, function(resultado, e){
	            							mask.hide();
	                						if (e.message == "Unable to connect to the server.") {
	                							Ext.Msg.alert('SET', 'Error de conexión al servidor. ');
	                						}	    
	                						if (parseInt(resultado.error) == 0 || resultado.error != null) {
	                							Ext.Msg.alert('SET', "No se completo la acción: " + resultado.mensaje);
	                						} else {
	                							Ext.Msg.alert('SET', "Acción completada: " + resultado.mensaje);
                    							NS.buscar();
	                						}
	                					});
                   					} else {
                   						Ext.Msg.alert('SET', 'Debe seleccionar al menos un registro. ');
                   					}
                   				}
                   			}
                   		}
                   	},
                   	{
                        xtype: 'button',
                        text: 'Pagos CT',
                        x: 245,
                        y: 240,
                        width: 100,
                        visible: false,
                        id: PF + 'btnPagosCT',
                        listeners: {
                   			click: {
                   				fn: function(e) {
                   					
                   					var regsMaestro = NS.gridMaestroPropuestas.getSelectionModel().getSelections();
                   					
                   					if(regsMaestro.length <= 0){
                   						Ext.Msg.alert('SET','Para marcar pagos como "Compra de Transferencias" es necesario seleccionar una propuesta');
                   						return;
                   					}
                   					
                   					//revisa que no tenga propuestas marcadas y desmarcadas con T
                   					var mark, unmark = false;
                   					
                   					for(var i=0; i<regsMaestro.length; i++){
                   						if(regsMaestro[i].get('habilitado') == 'T' ){
                   							mark = true;
                   						}else{
                   							unmark = true;
                   						}
                   						
                   						if(mark && unmark){
                   							Ext.Msg.alert('SET','<b>No es posible marcar o desmarcar propuestas.</b>' +
                   									'<br>Tiene propuestas seleccionadas como marcadas y desmarcadas para "Compra de Transferencias."'+
                   									'<br>Por favor elija solo propuestas marcadas o desmarcadas.');
                       						return;
                   						}
                   					}
                   					
                   					if(mark){
                   						Ext.Msg.confirm('SET', 'Las propuestas seleccionadas serán desmacadas de Compra de Transferencias'+
                   											'<br>¿Desea continuar?', function(btn) {
                   							
            		          				if(btn == 'yes') {
            		          					
            		          					mascara.show();
            		          					
            		          					var matCT = new Array();
            		          					
            		          					for(var i=0; i<regsMaestro.length;i++){
    		                						
    		                						var vec = {};
    		            							vec.cveControl = regsMaestro[i].get('cveControl');
    		            							matCT[i] = vec;
    		                					}
    		            							
    		            						var jsonCT = Ext.util.JSON.encode(matCT);
    		            						
    		            						ConsultaPropuestasAction.actualizaCompraTransfer(jsonCT, 'D', function(resultado, e){
                            						
                            						if(e.message=="Unable to connect to the server."){
                            							Ext.Msg.alert('SET','Error de conexión al servidor');
                            							mascara.hide();
                            							return;
                            						}
                            						
                            						if (resultado.error != '' && resultado.error != null && resultado.error != undefined) {
                            							Ext.Msg.alert('SET',resultado.error);
                            							return;
                            						}else{
                            							Ext.Msg.alert('SET','Propuestas desmarcadas con éxito.');
                            							NS.buscar();
                            							return;
                            						}   
                            						
                            					});

            		          				}
            		          			});
                   					}
                   					
                   					if(unmark){
                   						Ext.Msg.confirm('SET', 'Las propuestas seleccionadas serán marcadas para Compra de Transferencias'+
                   											'<br>¿Desea continuar?', function(btn) {
                   							
            		          				if(btn == 'yes') {
            		          					
            		          					mascara.show();
            		          					
            		          					var matCT = new Array();
            		          					
            		          					for(var i=0; i<regsMaestro.length;i++){
    		                						
    		                						var vec = {};
    		            							vec.cveControl = regsMaestro[i].get('cveControl');
    		            							matCT[i] = vec;
    		                					}
    		            							
    		            						var jsonCT = Ext.util.JSON.encode(matCT);
    		            						
    		            						ConsultaPropuestasAction.actualizaCompraTransfer(jsonCT, 'M', function(resultado, e){
                            						
                            						if(e.message=="Unable to connect to the server."){
                            							Ext.Msg.alert('SET','Error de conexión al servidor');
                            							mascara.hide();
                            							return;
                            						}
                            						
                            						if (resultado.error != '' && resultado.error != null && resultado.error != undefined) {
                            							Ext.Msg.alert('SET',resultado.error);
                            							return;
                            						}else{
                            							Ext.Msg.alert('SET','Propuestas marcadas con éxito.');
                            							NS.buscar();
                            							return;
                            						}   
                            						
                            					});
    		            						
            		          				}
            		          			});
                   					}
                   					
                   				}
                   			}
                   		}
                   	},*/
                   	
                   	NS.fieldsetAutorizar,
                   	NS.btnModifProp,
                 	//,items: [
                   	
                   /* {
                        xtype: 'checkbox',
                        boxLabel: 'Propuestas vigentes',
                        x: 320,
                        y: 0,
                        hidden: true, //true
                        listeners:{
                        check:{
                        		fn:function(checkBox,valor)
                        		{
	                        		if(valor==true) {
	                     				NS.storeMaestro.baseParams.propVigentes=''+valor;
	                     				NS.propVigentes = true;
	                        		}else {
	                     			  	NS.storeMaestro.baseParams.propVigentes='false';
	                     			  	NS.propVigentes = false;
		                        	}
                        		}
                        }
                      }
                    },*/
                    /*
                    {
                        xtype: 'button',
                        text: 'Eliminar',
                        x: 875,
                        y: 215,
                        width: 80,
                        id: PF + 'btnElimProp',
                        name: PF + 'btnElimProp',
                        hidden:false, 
                        listeners: {
		                	click: {
		                		fn:function(e) {
		                			NS.eliminarPropuesta();
		                		}
		                	}
                    	}
                    },*/
                    
                    {
                        xtype: 'button',
                        text: 'Parámetros',
                        x: 900,
                        y: 235,
                        width: 80,
                        id: PF + 'btnParametros',
                        name: PF + 'btnParametros',
                        hidden:true, 
                        listeners: {
		                	click: {
		                		fn:function(e) {
		                			//despliega ventana de parametros
		                			 winParametros.show();
		                		}
		                	}
                    	}
                    },
                     NS.gridMaestroPropuestas,
                     NS.lblVisualizarDetalle,
                     NS.lblImporteMN,
                     NS.txtImporteProp,
                     NS.lblImporteDLS,
                     NS.txtImporteDLSProp,
                     NS.lblImporteEUR,
                     NS.txtImporteEURProp,
                     NS.lblImporteOTR,
                     NS.txtImporteOTRProp,
                    {
                        xtype: 'fieldset',
                        title: 'Movimientos',
                        x: 0,
                        y: 260,
                        width: 985,
                        height: 300,
                        layout: 'absolute',
                        id: 'contMovimientos',
                        items: [
                        NS.gridDetalle,
                            {
                                xtype: 'textfield',
                                x: 60,
                                y: 210,
                                width: 100,
                                id: PF+'totalMN',
                                name: PF+'totalMN',
                                disabled: true
                                
                            },
                            {
                                xtype: 'textfield',
                                x: 240,
                                y: 210,
                                width: 100,
                                id: PF+'totalDLS',
                                name: PF+'totalDLS',
                                disabled: true
                            },
                            NS.iporteS,
                            {
                                xtype: 'label',
                                text: 'Total MN:',
                                x: 10,
                                y: 215
                            },
                            {
                                xtype: 'label',
                                text: 'Total DLS:',
                                x: 180,
                                y: 215
                            },
                            {
                                xtype: 'label',
                                text: 'Suma detalles:',
                                x: 350,
                                y: 215
                            },{
             		    	   xtype: 'button',
            		    	   text: 'compra Trans.Divisas',
            		    	   x: 320,
            		    	   y: 240,
            		    	   width: 170,
            		    	   id: 'cvt',
            		    	   listeners:
            		    	   {
            		    		   click:
            		    		   {
            		    			   fn: function(e)
            		    			   {
            		    				   
            		    		   var regDet = NS.gridDetalle.getSelectionModel().getSelections();
                                if(regDet.length < 0){
                                	alert("debe se leccionar almenos un rtegistroi");
                                } else{
                                	var matCheq = new Array();
                					for(var k=0; k<regDet.length;k++){
                						var vec = {};
            							vec.noFolioDet = regDet[k].get('noFolioDet');
            							matCheq[k] = vec;
                					}
            						var jsonString = Ext.util.JSON.encode(matCheq);
                        	
            						ConsultaPropuestasAction.actualizaCVT(jsonString, function(resultado, e) {
										if (resultado != '' && resultado != undefined && resultado != null){

											Ext.Msg.alert('SET','Documentos Marcados para CVT');
											NS.gridDetalle.store.removeAll();
        				    				NS.gridDetalle.getView().refresh();
        				    	
										}else{
											Ext.Msg.alert('SET','Error');
											
										}        		            										
									});
            						
            						
                                }  	
            
            		    				   
            		    				   
            		    				   
            		    			   }
            		    		   }
            		    	   }
            		       },
                            {
                                xtype: 'button',
                                text: 'Cheque sin agrupar',
                                x: 525,
                                y: 240,
                                width: 80,
                                id: PF + 'btnCheqNoagrupa',
                                listeners: {
                            		click: {
                            			fn: function(e) {
                            				
                            				var regsProp = NS.gridMaestroPropuestas.getSelectionModel().getSelections();
                            				var regs = NS.gridDetalle.getSelectionModel().getSelections();
                            				
                            				if(regsProp.lenth <= 0){
                            					Ext.Msg.alert('SET','Debe seleccionar al menos una propuesta');
                    							return;
                            				}
                            				
                            				if(regs.lenth <= 0){
                            					Ext.Msg.alert('SET','Debe seleccionar al menos un detalle.');
                    							return;
                            				}

                            				for(var k=0; k<regs.length;k++){
		                						if(regs[k].get('descFormaPago')!= 'CHEQUE'){
		                							Ext.Msg.alert('SET','Debe seleccionar solo cheques');
	                    							return;
		                						}
		            							
		                					}
                            				
                            				var matCheq = new Array();
		                					
		                					for(var k=0; k<regs.length;k++){
		                						var vec = {};
		            							vec.noFolioDet = regs[k].get('noFolioDet');
		            							matCheq[k] = vec;
		                					}
		            							
		            						var jsonStringDetalles = Ext.util.JSON.encode(matCheq);
		            						
		            						//DATOS A MODIFICAR
		            						var cveControlTmp = regsProp[0].get('cveControl');	
		            						
		            						if(cveControlTmp == '' || cveControlTmp == null || cveControlTmp == undefined){
		            							Ext.Msg.alert('SET','Error al obtener clave control de la propuesta.');
                    							return;
		            						}
		            							
		                					mascara.show();
		                					
		                					ConsultaPropuestasAction.actualizarChequeraNoAgrupa(cveControlTmp, jsonStringDetalles,
		                																	 function(result, e){

		                						mascara.hide();
		                						
		            							if(e.message=="Unable to connect to the server."){
		            								Ext.Msg.alert('SET','Error de conexión al servidor');
		            								return;
		            							}
		            							
		            							if(result.error != null && result.error != '' && result.error != undefined) {
		            								Ext.Msg.alert('SET',''+result.error);
		            							}else{
		            								Ext.Msg.alert('SET',''+result.mensaje);
		            								
		            								mascara.show();
		            								
		            								var regSeleccionados = NS.gridMaestroPropuestas.getSelectionModel().getSelections();
	            					        		
	            									NS.gridDetalle.store.removeAll();
	            				    				NS.gridDetalle.getView().refresh();
	            				    				//NS.storeDetalle.load();
	            				    				
	            				    				NS.storeDetalle = new Ext.data.DirectStore({
            				    				  	 	paramsAsHash: false,
            				    				  	 	baseParams: {
            				    							idGrupoEmpresa:0,
            				    							idGrupoRubro:0,
            				    							cveControl:'',
            				    							idUsuario1:0,
            				    							idUsuario2:0,
            				    							idUsuario3:0
            				    						},
            				    						root: '',
            				    						paramOrder:['idGrupoEmpresa','idGrupoRubro','cveControl','idUsuario1','idUsuario2','idUsuario3'],
            				    						directFn: ConsultaPropuestasAction.consultarDetalle,
            				    						//autoLoad: {params:{start: 0, limit: 10}},
            				    						fields: [
            				    							{name: 'nomEmpresa'},//Grupo
            				    							{name: 'noEmpresa'},//Nom. Empresa
            				    							{name: 'noDocto'},//No. Docto
            				    							{name: 'nomProveedor'},//Proveedor
            				    							{name: 'importe'},//importe
            				    							{name: 'fecValorOriginalStr', type: 'date', dateFormat:'d/m/Y'},//fecha de vencimiento
            				    							{name: 'idDivisa'},//Divisa
            				    							{name: 'importeMn'},//ImporteMN
            				    							{name: 'idFormaPago'},//ID Forma Pago
            				    							{name: 'descFormaPago'},//Forma Pago
            				    							{name: 'fecPropuestaStr', type: 'date', dateFormat:'d/m/Y'},//Fec Prop
            				    							{name: 'bancoPago'},//Banco de Pago
            				    							{name: 'idChequera'},//Chequera de Pago
            				    							{name: 'descGrupoCupo'},//Grupo de Rubros
            				    							{name: 'idRubro'},//Rubro
            				    							{name: 'importeOriginal'},//Importe Original
            				    							{name: 'idDivisaOriginal'},//DivisaOriginal
            				    							{name: 'beneficiario'},//Beneficiario
            				    							{name: 'noFolioDet'},//No. Folio Det
            				    							{name: 'concepto'},//Concepto
            				    							{name: 'idBancoBenef'},//Id Banco Benef
            				    							{name: 'idChequeraBenef'},//Id Chequera Benef
            				    							{name: 'noFactura'},//No. Factura
            				    							{name: 'diasInv'},//Dias Vto
            				    							{name: 'origenMov'},//Origen
            				    							{name: 'observacion'},
            				    							{name: 'noCliente'},//No. Persona
            				    							{name: 'noCheque'},//No. Cheque
            				    							{name: 'idBanco'},//No. Banco pagador
            				    							{name: 'usr1'},
            				    							{name: 'usr2'},
            				    							{name: 'color'},
            				    							{name: 'fecContabilizacionStr', type: 'date', dateFormat:'d/m/Y'}, //Fecha Contabilizacion
            				    							{name: 'fecOperacionStr', type: 'date', dateFormat:'d/m/Y'}, //Fecha Documento
            				    							{name: 'fecValorStr', type: 'date', dateFormat:'d/m/Y'}, //Fecha Vencimiento
            				    							{name: 'equivale'},
            				    							{name: 'idContable'},	//clase docto.
            				    							{name: 'invoiceType'},
            				    							{name: 'idClabe'},
            				    							{name: 'rfc'},
            				    							{name: 'referenciaCte'},
            				    							{name: 'nombreRegla'},
            				    							
            				    						],
            				    						listeners: {
            				    							load: function(s, records){
            				    								//var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeDetalle, msg:"Cargando 222..."});
            				    								
            				    								if(records.length==null || records.length<=0){
            				    									Ext.Msg.alert('SET','No existe detalle');
            				    									NS.registros = NS.gridDetalle.getSelectionModel().getSelections();
            				    								}else {
            				    									console.log("observacion "+records[0].get('observacion'));
            				    				          			var totalImporte=0;
            				    				          			var totalImporteDLS=0;
            				    				          			var sumaDetProp=0;
            				    				          			
            				    				          			var bandera = false;
            				    				          			var regSelMaestros = NS.gridMaestroPropuestas.getSelectionModel().getSelections();
            				    				          			
            				    				          			if(regSelMaestros.length > 0) {
            				    				          				if(regSelMaestros[0].get('user1') != '')
            				    				          					bandera = true;
            				    				          			}
            				    				          			if(bandera) {
            				    				          				for(var k=0; k<records.length; k++) {
            				    				          					
            				    				          					if(records[k].get('idDivisa') == 'DLS')
            				    				              					totalImporteDLS += records[k].get('importe');
            				    				              				else
            				    				              					totalImporte += records[k].get('importe');
            				    				              				
            				    				              				NS.sm.selectRow(k, true, false);
            				    				              				
            				    				              			}
            				    				          				
            				    				          				sumaDetProp = 0;
            				    				          				/*
            				    				          				var regTodos =NS.gridDetalle.store.data.items;
            				    				              			for(var k=0; k<regTodos.length; k++) {
            				    				              				sumaDetProp += regTodos[k].get('importe');
            				    				              			}*/
            				    				              			
            				    				          			}else {
            				    				          				//NS.sm.selectRange(0,records.length-1); //lo  comento para que no seleccione nada
            				    					          			
            				    				          				sumaDetProp = 0;
            				    				          				
            				    					          			//var regSelec=NS.gridDetalle.getSelectionModel().getSelections();
            				    					          			var regSelec=NS.gridDetalle.store.data.items; //todos los registros en lugar de seleccionarlos
            				    					          			for(var kk=0; kk<regSelec.length; kk++) {
            				    					          				if(regSelec[kk].get('idDivisa') == 'DLS')
            				    					          					totalImporteDLS += regSelec[kk].get('importe');
            				    					          				else
            				    					          					totalImporte += regSelec[kk].get('importe');
            				    					          			}
            				    					          			
            				    					          			/*for(var k=0; k < regSelec.length; k++) {
            				    					          				sumaDetProp += regSelec[k].get('importe');
            				    					          			}
            				    					          			*/
            				    				          			}
            				    				          			Ext.getCmp(PF+'totalMN').setValue(NS.formatNumber(Math.round((totalImporte)*100)/100 ));
            				    				          			Ext.getCmp(PF+'totalDLS').setValue(NS.formatNumber(Math.round((totalImporteDLS)*100)/100));

            				    					        		NS.registros = NS.gridDetalle.getSelectionModel().getSelections();
            				    					        		
            				    					        		//Deseleccionamos todo
            				    					        		NS.sm.selectRange(-1,-1);
            				    								}
            				    								
            				    								mascara.hide();
            				    							},
            				    							exception: function(mist){
            				    								Ext.Msg.alert('SET','Error en conexión al servidor');
            				    								mascara.hide();
            				    							}
            				    						}
            				    					}); 
            				    				 
	            					        		//Facultades de nómina
	            				    				if(NS.nominaRH){
	            				    	    			NS.gridDetalle.reconfigure(NS.storeDetalle, NS.columnasDetalle);
	            				    	    			habilitarBotonesDetalle(false);
	            				    	    		}else if(NS.nominaTes && regSeleccionados[0].get('cveControl').indexOf('MAN') >= 0){
	            				    	    			NS.gridDetalle.reconfigure(NS.storeDetalle, NS.columnasDetalleNom);
	            				    	    			habilitarBotonesDetalle(false);
	            				    	    		}else{
	            				    	    			NS.gridDetalle.reconfigure(NS.storeDetalle, NS.columnasDetalle);
	            				    	    			habilitarBotonesDetalle(true);
	            				    	    		}
	            					        		
	            				    				//NS.gridDetalle.reconfigure(NS.storeDetalle, NS.columnasDetalle);
	            				    				NS.gridPagingBar.bindStore(NS.storeDetalle, true);
	            				    				
	            				    				NS.storeDetalle.baseParams.idGrupoEmpresa=regSeleccionados[0].get('idGrupoFlujo');
	            					        		NS.storeDetalle.baseParams.idGrupoRubro=regSeleccionados[0].get('idGrupo');
	            					        		NS.storeDetalle.baseParams.cveControl=regSeleccionados[0].get('cveControl');
	            					        		NS.storeDetalle.baseParams.idUsuario1=regSeleccionados[0].get('usuarioUno');
	            					        		NS.storeDetalle.baseParams.idUsuario2=regSeleccionados[0].get('usuarioDos');
	            					        		//NS.storeDetalle.baseParams.idUsuario3=regSeleccionados[0].get('usuarioTres');
	            					        		
	            					        		//No perder el registro que carga los detalles, esto para sus modificaciones (Si es que se han seleccionado mas registros en el grid maestro)
	            					        		NS.seleccionPropuestaGlobal = regSeleccionados[0];
            				    				
	            				    				NS.storeDetalle.load({
	            				    				    params: {
	            				    				        // specify params for the first page load if using paging
	            				    				        start: 0,          
	            				    				        limit: 500,
	            				    				    }
	            				    				});
        		                					
	            				    				//POR PAGINACIÓN ES NECESARIO REALIZAR ESTA CONSULTA PARA SACAR EL TOTAL.
	            									ConsultaPropuestasAction.sumaTotalDetalles(''+NS.seleccionPropuestaGlobal.get('cveControl'), function(resultado, e) {
	            										if (resultado != '' && resultado != undefined && resultado != null){
	            											//Ext.Msg.alert('SET', resultado);
	            											Ext.getCmp(PF+'sumMN').setValue(NS.formatNumber(Math.round((resultado)*100)/100));
	            										}else{
	            											Ext.Msg.alert('SET', 'Error al obtener la suma total del detalle.');
	            										}        		            										
	            									});
	            									
	            									
		            							}//fin else de resultado.error
		            								
	            				    		}); 
										 
		            				    				
                            			}
                            		}
                                }
                            },{
                                xtype: 'button',
                                text: 'Modif. Pag.',
                                x: 720,
                                y: 240,
                                width: 70,
                                id: PF + 'btnModificarPag',
                                listeners: {
                            		click: {
                            			fn: function(e) {
                            				var sel=NS.gridDetalle.getSelectionModel().getSelections();
                            			    NS.storeDivisaPago.load();
                            			    NS.storeFormaPago.load();
                            				NS.modifPag = true;
                            				NS.modifBenef = false;
                            				NS.campoRef='';
                            				NS.modificarPropuesta();
//                            
                            				var referencias=new Array();
                            				Ext.getCmp(PF + 'contFormaPago').setDisabled(false);
//                            				if(sel.length==1){
                            					Ext.getCmp(PF+'fieldReferencias').setDisabled(false);
                            					NS.campoRef = sel[0].get('referenciaCte');
                            					var observ=sel[0].get('observacion');
                            					if(observ!=null && observ!=''){
                            						referencias=observ.split('|');
                            					}
                        						
                                				switch(NS.campoRef.trim()){
                                					case '':
                                						Ext.getCmp(PF+'txtReferencia1').setDisabled(true);
                                						Ext.getCmp(PF+'txtReferencia2').setDisabled(false);
                                						Ext.getCmp(PF+'txtReferencia1').setValue(sel[0].get('noFolioDet'));
                                						Ext.getCmp(PF+'txtReferencia3').setDisabled(true);
                                					
                                						break;
                                					case 'concepto':
                                						Ext.getCmp(PF+'txtReferencia1').setDisabled(true);
                                						Ext.getCmp(PF+'txtReferencia1').setValue(sel[0].get('noFolioDet'));
                                						Ext.getCmp(PF+'txtReferencia2').setDisabled(false);
                                						Ext.getCmp(PF+'txtReferencia2').setValue(sel[0].get('concepto'));
                                						Ext.getCmp(PF+'txtReferencia3').setDisabled(true);
                                						Ext.getCmp(PF+'txtReferencia3').setValue(sel[0].get('observacion'));
                                						break;
                                					case 'no_factura':
                                						Ext.getCmp(PF+'txtReferencia1').setDisabled(true);
                                						Ext.getCmp(PF+'txtReferencia1').setValue(sel[0].get('noFolioDet'));
                                						Ext.getCmp(PF+'txtReferencia2').setDisabled(false);
                                						Ext.getCmp(PF+'txtReferencia2').setValue(sel[0].get('noFactura'));
                                						Ext.getCmp(PF+'txtReferencia3').setDisabled(true);
                                						Ext.getCmp(PF+'txtReferencia3').setValue(sel[0].get('observacion'));
                            							break;
                                					case 'no_docto':
                                						Ext.getCmp(PF+'txtReferencia1').setDisabled(true);
                                						Ext.getCmp(PF+'txtReferencia1').setValue(sel[0].get('noFolioDet'));
                                						Ext.getCmp(PF+'txtReferencia2').setDisabled(false);
                                						Ext.getCmp(PF+'txtReferencia2').setValue(sel[0].get('noDocto'));
                                						Ext.getCmp(PF+'txtReferencia3').setDisabled(true);
                                						Ext.getCmp(PF+'txtReferencia3').setValue(sel[0].get('observacion'));
                            							break;
                                					case 'concepto y no_factura':
                                						Ext.getCmp(PF+'txtReferencia1').setDisabled(false);
                                						Ext.getCmp(PF+'txtReferencia1').setValue(sel[0].get('concepto'));
                                						Ext.getCmp(PF+'txtReferencia2').setDisabled(false);
                                						Ext.getCmp(PF+'txtReferencia2').setValue(sel[0].get('noFactura'));
                                						Ext.getCmp(PF+'txtReferencia3').setDisabled(true);
                                						Ext.getCmp(PF+'txtReferencia3').setValue(sel[0].get('observacion'));
                            							break;
                                					case 'referencia_dalton':
                                						console.log("tamaño referencias "+referencias.length);
                                						if(referencias.length>0){
                                							Ext.getCmp(PF+'txtReferencia1').setDisabled(false);
                                    						Ext.getCmp(PF+'txtReferencia1').setValue(referencias[0]);
                                    						Ext.getCmp(PF+'txtReferencia2').setDisabled(false);
                                    						Ext.getCmp(PF+'txtReferencia2').setValue(referencias[1]);
                                    						Ext.getCmp(PF+'txtReferencia3').setDisabled(false);
                                    						Ext.getCmp(PF+'txtReferencia3').setValue(referencias[2]);
                                						}else{
                                							Ext.getCmp(PF+'txtReferencia1').setDisabled(false);
                                    						Ext.getCmp(PF+'txtReferencia1').setValue("");
                                    						Ext.getCmp(PF+'txtReferencia2').setDisabled(false);
                                    						Ext.getCmp(PF+'txtReferencia2').setValue("");
                                    						Ext.getCmp(PF+'txtReferencia3').setDisabled(false);
                                    						Ext.getCmp(PF+'txtReferencia3').setValue("");
                                						}
                                						
                            							break;
                                					default:
                                						Ext.getCmp(PF+'txtReferencia1').setDisabled(true);
                                						Ext.getCmp(PF+'txtReferencia1').setValue(sel[0].get('noFolioDet'));
                                						Ext.getCmp(PF+'txtReferencia2').setDisabled(true);
                                						Ext.getCmp(PF+'txtReferencia2').setValue(sel[0].get('referenciaCte'));
                                						Ext.getCmp(PF+'txtReferencia3').setDisabled(true);
                                				}
//                            				}else{
//                            					Ext.getCmp(PF+'fieldReferencias').setDisabled(true);
//                            				}
		                    				NS.multisociedad = false;
		                    				NS.habilitaDiversos(true);
		                    				
		                    				Ext.getCmp(PF + 'txtBanco').setDisabled(false);
		                    				Ext.getCmp(PF + 'txtBancoBenef').setDisabled(true);
		                    				//console.log("forma de pago "+sel[0].get('descFormaPago'));
		                    				if(sel[0].get('descFormaPago') == 'TRASPASO'){
		                    					Ext.getCmp(PF + 'dateNvaFecPago').setDisabled(true);
		                    					NS.cmbFormaPago.setDisabled(true);
		                    					NS.cmbFormaPago.setValue(sel[0].get('descFormaPago'));
		                    					NS.idPago=sel[0].get('idFormaPago');
		                    				}else{
		                    					Ext.getCmp(PF + 'dateNvaFecPago').setDisabled(false);
		                    					NS.cmbFormaPago.setDisabled(false);
		                    					NS.idPago=sel[0].get('idFormaPago');
		                    				}	
		                    				Ext.getCmp(PF + 'txtDivisaPago').setDisabled(false);
		                    		        NS.cmbBancoPag.setDisabled(false);
		                    		        NS.cmbChequeraPag.setDisabled(false);
		                    		        NS.cmbDivisaCambio.setDisabled(false);
		                    		    	NS.cmbBancoPagBenef.setDisabled(true);
		                    		        NS.cmbChequeraPagBenef.setDisabled(true);

		                    		        Ext.getCmp(PF + 'txtDivisaPago').setValue(NS.idDivisaCambio);
		                    		        NS.cmbDivisaCambio.setValue(NS.idDivisaCambio);
		                    		        NS.actualizaTipoCambio();
		                    		        //NS.cmbDivisaCambio.setStartValue(NS.idDivisaCambio);
		                    				//NS.gridDetalle.setDisabled(true);	
                            			}
                            		}
                                }
                            },
                            {
                                xtype: 'button',
                                text: 'Modif Benef.',
                                x: 800,
                                y: 240,
                                width: 70,
                                id: PF + 'btnModificar',
                                listeners: {
                            		click: {
                            			fn: function(e) {

                            				NS.tipoCambio = 1;
                            				NS.modifPag = false;
                            				NS.modifBenef = true;
                            				
                            				NS.modificarPropuesta();
                            				
                            				Ext.getCmp(PF + 'contFormaPago').setDisabled(false);
                            				Ext.getCmp(PF+'fieldReferencias').setDisabled(true);
		                    				NS.multisociedad = false;
		                    				NS.habilitaDiversos(true);
		                    				
		                    				Ext.getCmp(PF + 'txtBanco').setDisabled(true);
		                    				Ext.getCmp(PF + 'txtBancoBenef').setDisabled(false);
		                    				Ext.getCmp(PF + 'dateNvaFecPago').setDisabled(true);
		                    				Ext.getCmp(PF + 'txtDivisaPago').setDisabled(true);
		                    		        NS.cmbFormaPago.setDisabled(true);
		                    		        NS.cmbBancoPag.setDisabled(true);
		                    		        NS.cmbChequeraPag.setDisabled(true);
		                    		        NS.cmbDivisaCambio.setDisabled(true);
		                    		    	NS.cmbBancoPagBenef.setDisabled(false);
		                    		        NS.cmbChequeraPagBenef.setDisabled(false);
		                    				
                            			}
                            		}
                                }
                            },
                            {
                            	id: PF + 'btnEliminar',
                                xtype: 'button',
                                text: 'Eliminar',
                                x: 640,
                                y: 240,
                                width: 70,
                                listeners: {
                            		click: {
                            			fn: function(e) {                            				
                            				var registroSeleccionado = NS.gridDetalle.getSelectionModel().getSelections();
                            				if (registroSeleccionado.length <= 0)
                            					Ext.Msg.alert('SET', 'Se debe seleccionar al menos un registro para eliminar');
                            				else{
                            					
                            					habilitarBotones(false);
                            					//mascara.show();
                            					
                            					//Selecciona el indice de la fila seleccionada.
                            					//var seleccionado = NS.gridMaestroPropuestas.getSelectionModel().getSelections()[0];
                            					//var indxRow = NS.gridMaestroPropuestas.store.indexOf(seleccionado);
                            					
                            					NS.indxMaestro = NS.gridMaestroPropuestas.getSelectionModel().getSelections()[0].get('cveControl');                    							
                    							
                            					ConsultaPropuestasAction.configuraSet(240, function(resultado, e){
                            						
                            						if(e.message=="Unable to connect to the server."){
                            							Ext.Msg.alert('SET','Error de conexión al servidor');
                            							return;
                            						}
                            						
                            						if (resultado != '' && resultado != null && resultado != undefined && resultado == 'SI') {
                            							
                            							habilitarBotones(true);
                            							//mascara.hide();
                            							
                            							var registroMaestroPropuestas = NS.gridMaestroPropuestas.getSelectionModel().getSelections();
                            							if (registroMaestroPropuestas[0].get('usuarioUno') != '' && registroMaestroPropuestas[0].get('usuarioDos') != ''){
                            								Ext.Msg.alert('SET', 'No se puede realizar este movimiento, hasta que se eliminen las autorizaciones de la propuesta');
                            							}else if(registroMaestroPropuestas[0].get('usuarioUno') != '' && registroMaestroPropuestas[0].get('usuarioDos') == '' || registroMaestroPropuestas[0].get('usuarioUno') == '' && registroMaestroPropuestas[0].get('usuarioDos') != '' ){
                            								Ext.Msg.alert('SET', 'No se puede realizar este movimiento, hasta que se eliminen las autorizaciones de la propuesta');
                            							}else{
                            								NS.eliminaDetalleDePropuesta();
                            							}
                            						}                            						
                            					});
                            				}
                            			}
                            		}
                                }
                            },
                            {
                                xtype: 'button',
                                text: 'Excel', //Se cambio el boton de imprimir por exportar Excel
                                x: 880,
                                y: 240,
                                width: 70,
                                id: PF + 'btnImprimir',
                                listeners: {
                           			click: {
                           				fn: function(e) {
                            				//NS.imprimir();
                           					NS.validaDatosExcelDetalles();
                           				}
                           			}
                           		}
                            }
                            //,NS.btnMultiSociedad
                        ] //Fin field movimientos
                    },
                    
                    {
                        xtype: 'fieldset',
                        title: 'Diversos',
                        x: 0,
                        y: 560,
                        width: 985,
                        height: 210,
                        layout: 'absolute',
                        id: PF + 'contFormaPago',
                        hidden: false,
                        disabled: true,
                        items: [
                                {
                                    xtype: 'textfield',
                                    x: 500,
                                    y: 15,
                                    width: 50,
                                    id: PF + 'txtBanco'
                                },
                                {
                                    xtype: 'label',
                                    text: 'Divisa de pago: ',
                                    x: 220,
                                    y: 0
                                },
                                {
                                    xtype: 'textfield',
                                    x: 220,
                                    y: 15,
                                    width: 50,
                                    id: PF + 'txtDivisaPago'
                                },
                            NS.cmbDivisaCambio,
                            NS.cmbFormaPago,
                            NS.cmbBancoPag,
                            NS.cmbChequeraPag,
                        	NS.cmbBancoPagBenef,
                            {
                                xtype: 'label',
                                text: 'Forma Pago',
                                x: 10,
                                y: 0
                            },
                            {
                                xtype: 'label',
                                text: 'Banco',
                                x: 500,
                                y: 0
                            },
                            {
                                xtype: 'label',
                                text: 'Banco chequera beneficiario',
                                x: 500,
                                y: 50
                            },
                            {
                            	xtype: 'textfield',
                            	x: 500,
                            	y: 65,
                            	width: 50,
                            	id: PF + 'txtBancoBenef'
                        	},
                            {
                                xtype: 'label',
                                text: 'Chequera',
                                x: 765,
                                y: 0
                            },
                            {
                                xtype: 'label',
                                text: 'Cuenta de cheques beneficiario',
                                x: 765,
                                y: 50
                            },
                            NS.cmbChequeraPagBenef,
                            {
                                xtype: 'label',
                                text: 'Nva. Fecha Pago',
                                x: 10,
                                y: 50
                            },
                            {
                	   			xtype:'datefield',
                        		format:'d/m/Y',
                        		id: PF + 'dateNvaFecPago',
                        		name: PF + 'dateNvaFecPago',
                        		value: '',
                	       		x:10,
                	       		y:65,
                	       		width:120,
                	       		hidden:false,
                	       		allowBlank: false,
                	       		//blankText:'Fecha pago subpropuesta',
                	       		listeners:{
                	            	change:{
                	            		fn:function(cmpFecha, valor){
                	            			
                	            			//Si selecciona limpia y deshabilita 
                	            			if(cmpFecha.isValid()){
                	            				
                	            				//if(NS.modifPag == true){
                	            					NS.cmbFormaPago.setValue("");
                	            					NS.cmbBancoPag.setValue("");
                	            					NS.cmbChequeraPagBenef.setValue("");
                	            					Ext.getCmp(PF + 'txtBanco').setValue("");
                	            					NS.cmbFormaPago.setDisabled(true);
                    	            				NS.cmbBancoPag.setDisabled(true);
                    	            				NS.cmbChequeraPag.setDisabled(true);
                    	            				Ext.getCmp(PF + 'txtBanco').setDisabled(true);
                	            				//}
                	            				
                	            				//if(NS.modifBenef == true){
                	            					NS.cmbBancoPagBenef.setValue("");
                	            					NS.cmbChequeraPag.setValue("");
                	            					Ext.getCmp(PF + 'txtBancoBenef').setValue("");
                	            					NS.cmbBancoPagBenef.setDisabled(true);
                	            					NS.cmbChequeraPagBenef.setDisabled(true);
                	            					Ext.getCmp(PF + 'txtBancoBenef').setDisabled(true);
                	            				//}                	            				
                	            				
                	            			}else{
                	            				
                	            				cmpFecha.setValue("");
                	            				
                	            				if(NS.modifPag == true){
                	            					NS.cmbFormaPago.setDisabled(false);
                	            					NS.cmbBancoPag.setDisabled(false);
                	            					NS.cmbChequeraPag.setDisabled(false);
                	            					Ext.getCmp(PF + 'txtBanco').setDisabled(false);
                	            				}                	            				
                	            				if(NS.modifBenef == true){
                	            					NS.cmbBancoPagBenef.setDisabled(false);
                	            					NS.cmbChequeraPagBenef.setDisabled(false);
                	            					Ext.getCmp(PF + 'txtBancoBenef').setDisabled(false);
                	            				}
                	            			}                	            	
                	    				}
                	    			}
                	    		}
                	   		},
                            {
                                xtype: 'label',
                                text: 'Tipo de cambio',
                	       		x:220,
                	       		y:50,
                            },
                	   		{
                	   			xtype: 'textfield',
                	       		x:220,
                	       		y:65,
                	       		width:80,
                	       		//disabled:true,
                            	id: PF + 'txtTipoCambio'
                	   		}/*,
                            {
                	   			
                                xtype: 'checkbox',
                                id:PF+'chkCVT',
                                name:PF+'chkCVT',
                                x: 330,
                                y: 60,
                                boxLabel: 'CVT',
                                listeners:{
                                	check:{
                                		fn:function(checkBox,valor){
        	                        		if(valor=true){
        	                        			if(NS.cmbChequeraPagBenef.getValue()!=""){
        	                        				NS.chequeraPagadora=NS.gridDetalle.getSelectionModel().getSelections();
        	                        				ConsultaPropuestasAction.obtenerDivisaChequera(NS.chequeraPagadora[0].get('idChequera'),function(result, e){
        	                        					if(result!=null && result.error!='' && result!=undefined){
        	                        						NS.cheqBen=NS.cmbChequeraPagBenef.getValue();
        	                        						NS.cheqBen2=NS.cheqBen.split("]");
        	                        						NS.divisaBen=NS.cheqBen2[0].replace("[","");
        	                        						if (checkBox.checked==valor){
        	                        							if(NS.divisaBen==result){
            	                        							Ext.Msg.alert('SET',"No puede Realizar Compra de Transfer cuando las divisas son iguales");
            	                        							NS.cmbChequeraPagBenef.setValue("");
            	                        						}
        	                        						}else{
        	                        							if(NS.divisaBen!=result){
            	                        							Ext.Msg.alert('SET',"Debe Realizar Compra de Transfer cuando las divisas son diferentes");
            	                        							NS.cmbChequeraPagBenef.setValue("");
            	                        						}
        	                        						}
        	                        						
        	                        					}
        	                        				});
        	                        			}
        	                        		}
                                		}
                                	}
                                }
                            }*/,
                            {
                                xtype: 'button',
                                text: 'Aceptar',
                                x: 870,
                                y: 105,
                                width: 80,
                                height: 22,
                                id: 'btnAceptar',
                                listeners: {
        		                	click: {
        		                		fn:function(e) {
        		                			//Modifica la chequera pagadora, no permite modificar los demas campos (deshabilitados).
        		                			if(NS.multisociedad == true){
        		                				
        		                				if(recSelectDetProp.length <= 0){
        		                					Ext.Msg.alert('SET','Seleccione un registro para modificar multisociedad.');
            		                				return;
        		                				}
        		                				
        		                				
        		                				if(NS.validaDatosMultiSociedad()){
        		                					
        		                					var matMulti = new Array();
        		                					
        		                					var noEmpresaTmp = recSelectDetProp[0].get('noEmpresa');
        		                					
        		                					for(var k=0; k<recSelectDetProp.length;k++){
        		                						
        		                						if(noEmpresaTmp != recSelectDetProp[k].get('noEmpresa')){
        		                							Ext.Msg.alert('SET','Para actualizar multisociedad es' 
        		                									+ 'necesario seleccionar registros de una misma empresa.');
        		                							
        		                							NS.multisociedad = false;
                    		                				return;
                    		                				
        		                						}
        		                						
        		                						var vec = {};
        		            							vec.noFolioDet = recSelectDetProp[k].get('noFolioDet');
        		            							matMulti[k] = vec;
        		                					}
        		            							
        		            						var jsonStringDetalles = Ext.util.JSON.encode(matMulti);
        		            						
        		            						//DATOS A MODIFICAR
        		            						var matDatos = new Array();
        		            						
        		            						var vecDatos = {};
        		            						vecDatos.noEmpresa = Ext.getCmp(PF+'txtEmpresaMulti').getValue();
        		                					vecDatos.idBanco = NS.cmbBancoPagMulti.getValue();
        		                					vecDatos.idChequera = NS.cmbChequeraPagMulti.getValue();
        		            						matDatos[0] = vecDatos;
        		                					var jsonDatosModificar = Ext.util.JSON.encode(matDatos);	
        		            						
        		                					mascara.show();
        		                					
        		                					ConsultaPropuestasAction.actualizaMultisociedad(''+NS.seleccionPropuestaGlobal.get('cveControl'), jsonStringDetalles
        		                																	,jsonDatosModificar, function(result, e){

        		                						mascara.hide();
        		                						
        		            							if(e.message=="Unable to connect to the server."){
        		            								Ext.Msg.alert('SET','Error de conexión al servidor');
        		            								return;
        		            							}
        		            							
        		            							if(result.error != null && result.error != '' && result.error != undefined) {
        		            								Ext.Msg.alert('SET',''+result.error);
        		            							}else{
        		            								if(result.msgUsuario != ''){
        		            									
        		            									Ext.Msg.alert('SET',''+result.msgUsuario);
        		            									
        		            									NS.cancelar();
        		            									//Ext.getCmp(PF + 'contFormaPago').setDisabled(true);
        		            									//Ext.getCmp(PF+'fieldMultiSociedad').setDisabled(true);
        		            									
        		            									mascara.show();
        		            									
        		            									var regSeleccionados = NS.gridMaestroPropuestas.getSelectionModel().getSelections();
        		            					        		
        		            									NS.limpiaCamposMultisociedad();
        		            									NS.gridDetalle.store.removeAll();
        		            				    				NS.gridDetalle.getView().refresh();
        		            				    				//NS.storeDetalle.load();
        		            				    				
        		            				    				NS.storeDetalle = new Ext.data.DirectStore({
    		            				    				  	 	paramsAsHash: false,
    		            				    				  	 	baseParams: {
    		            				    							idGrupoEmpresa:0,
    		            				    							idGrupoRubro:0,
    		            				    							cveControl:'',
    		            				    							idUsuario1:0,
    		            				    							idUsuario2:0,
    		            				    							idUsuario3:0
    		            				    						},
    		            				    						root: '',
    		            				    						paramOrder:['idGrupoEmpresa','idGrupoRubro','cveControl','idUsuario1','idUsuario2','idUsuario3'],
    		            				    						directFn: ConsultaPropuestasAction.consultarDetalle,
    		            				    						//autoLoad: {params:{start: 0, limit: 10}},
    		            				    						fields: [
    		            				    							{name: 'nomEmpresa'},//Grupo
    		            				    							{name: 'noEmpresa'},//Nom. Empresa
    		            				    							{name: 'noDocto'},//No. Docto
    		            				    							{name: 'nomProveedor'},//Proveedor
    		            				    							{name: 'importe'},//importe
    		            				    							{name: 'fecValorOriginalStr', type: 'date', dateFormat:'d/m/Y'},//fecha de vencimiento
    		            				    							{name: 'idDivisa'},//Divisa
    		            				    							{name: 'importeMn'},//ImporteMN
    		            				    							{name: 'descFormaPago'},//Forma Pago
    		            				    							{name: 'fecPropuestaStr', type: 'date', dateFormat:'d/m/Y'},//Fec Prop
    		            				    							{name: 'bancoPago'},//Banco de Pago
    		            				    							{name: 'idChequera'},//Chequera de Pago
    		            				    							{name: 'descGrupoCupo'},//Grupo de Rubros
    		            				    							{name: 'idRubro'},//Rubro
    		            				    							{name: 'importeOriginal'},//Importe Original
    		            				    							{name: 'idDivisaOriginal'},//DivisaOriginal
    		            				    							{name: 'beneficiario'},//Beneficiario
    		            				    							{name: 'noFolioDet'},//No. Folio Det
    		            				    							{name: 'concepto'},//Concepto
    		            				    							{name: 'idBancoBenef'},//Id Banco Benef
    		            				    							{name: 'idChequeraBenef'},//Id Chequera Benef
    		            				    							{name: 'noFactura'},//No. Factura
    		            				    							{name: 'diasInv'},//Dias Vto
    		            				    							{name: 'origenMov'},//Origen
    		            				    							{name: 'observacion'},
    		            				    							{name: 'noCliente'},//No. Persona
    		            				    							{name: 'noCheque'},//No. Cheque
    		            				    							{name: 'idBanco'},//No. Banco pagador
    		            				    							{name: 'usr1'},
    		            				    							{name: 'usr2'},
    		            				    							{name: 'color'},
    		            				    							{name: 'fecContabilizacionStr', type: 'date', dateFormat:'d/m/Y'}, //Fecha Contabilizacion
    		            				    							{name: 'fecOperacionStr', type: 'date', dateFormat:'d/m/Y'}, //Fecha Documento
    		            				    							{name: 'fecValorStr', type: 'date', dateFormat:'d/m/Y'}, //Fecha Vencimiento
    		            				    							{name: 'equivale'},
    		            				    							{name: 'idContable'},	//clase docto.
    		            				    							{name: 'invoiceType'},
    		            				    							{name: 'idClabe'},
    		            				    							{name: 'rfc'},
    		            				    							{name: 'referenciaCte'},
    		            				    							
    		            				    						],
    		            				    						listeners: {
    		            				    							load: function(s, records){
    		            				    								//var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeDetalle, msg:"Cargando 222..."});
    		            				    								
    		            				    								if(records.length==null || records.length<=0){
    		            				    									Ext.Msg.alert('SET','No existe detalle');
    		            				    									NS.registros = NS.gridDetalle.getSelectionModel().getSelections();
    		            				    								}else {
    		            				    									
    		            				    				          			var totalImporte=0;
    		            				    				          			var totalImporteDLS=0;
    		            				    				          			var sumaDetProp=0;
    		            				    				          			
    		            				    				          			var bandera = false;
    		            				    				          			var regSelMaestros = NS.gridMaestroPropuestas.getSelectionModel().getSelections();
    		            				    				          			
    		            				    				          			if(regSelMaestros.length > 0) {
    		            				    				          				if(regSelMaestros[0].get('user1') != '')
    		            				    				          					bandera = true;
    		            				    				          			}
    		            				    				          			if(bandera) {
    		            				    				          				for(var k=0; k<records.length; k++) {
    		            				    				          					
    		            				    				          					if(records[k].get('idDivisa') == 'DLS')
    		            				    				              					totalImporteDLS += records[k].get('importe');
    		            				    				              				else
    		            				    				              					totalImporte += records[k].get('importe');
    		            				    				              				
    		            				    				              				NS.sm.selectRow(k, true, false);
    		            				    				              				
    		            				    				              			}
    		            				    				          				
    		            				    				          				sumaDetProp = 0;
    		            				    				          				/*
    		            				    				          				var regTodos =NS.gridDetalle.store.data.items;
    		            				    				              			for(var k=0; k<regTodos.length; k++) {
    		            				    				              				sumaDetProp += regTodos[k].get('importe');
    		            				    				              			}*/
    		            				    				              			
    		            				    				          			}else {
    		            				    				          				//NS.sm.selectRange(0,records.length-1); //lo  comento para que no seleccione nada
    		            				    					          			
    		            				    				          				sumaDetProp = 0;
    		            				    				          				
    		            				    					          			//var regSelec=NS.gridDetalle.getSelectionModel().getSelections();
    		            				    					          			var regSelec=NS.gridDetalle.store.data.items; //todos los registros en lugar de seleccionarlos
    		            				    					          			for(var kk=0; kk<regSelec.length; kk++) {
    		            				    					          				if(regSelec[kk].get('idDivisa') == 'DLS')
    		            				    					          					totalImporteDLS += regSelec[kk].get('importe');
    		            				    					          				else
    		            				    					          					totalImporte += regSelec[kk].get('importe');
    		            				    					          			}
    		            				    					          			
    		            				    					          			/*for(var k=0; k < regSelec.length; k++) {
    		            				    					          				sumaDetProp += regSelec[k].get('importe');
    		            				    					          			}
    		            				    					          			*/
    		            				    				          			}
    		            				    				          			Ext.getCmp(PF+'totalMN').setValue(NS.formatNumber(Math.round((totalImporte)*100)/100 ));
    		            				    				          			Ext.getCmp(PF+'totalDLS').setValue(NS.formatNumber(Math.round((totalImporteDLS)*100)/100));

    		            				    					        		NS.registros = NS.gridDetalle.getSelectionModel().getSelections();
    		            				    					        		
    		            				    					        		//Deseleccionamos todo
    		            				    					        		NS.sm.selectRange(-1,-1);
    		            				    								}
    		            				    								
    		            				    								mascara.hide();
    		            				    							},
    		            				    							exception: function(mist){
    		            				    								Ext.Msg.alert('SET','Error en conexión al servidor');
    		            				    								mascara.hide();
    		            				    							}
    		            				    						}
    		            				    					}); 
    		            				    				 
        		            				    				if(NS.nominaRH){
        		            				    	    			NS.gridDetalle.reconfigure(NS.storeDetalle, NS.columnasDetalle);
        		            				    	    			habilitarBotonesDetalle(false);
        		            				    	    		}else if(NS.nominaTes && regSeleccionados[0].get('cveControl').indexOf('MAN') >= 0){
        		            				    	    			NS.gridDetalle.reconfigure(NS.storeDetalle, NS.columnasDetalleNom);
        		            				    	    			habilitarBotonesDetalle(false);
        		            				    	    		}else{
        		            				    	    			NS.gridDetalle.reconfigure(NS.storeDetalle, NS.columnasDetalle);
        		            				    	    			habilitarBotonesDetalle(true);
        		            				    	    		}
        		            				    				//NS.gridDetalle.reconfigure(NS.storeDetalle, NS.columnasDetalle);
	    		            				    				NS.gridPagingBar.bindStore(NS.storeDetalle, true);
	    		            				    				
	    		            				    				NS.storeDetalle.baseParams.idGrupoEmpresa=regSeleccionados[0].get('idGrupoFlujo');
	    		            					        		NS.storeDetalle.baseParams.idGrupoRubro=regSeleccionados[0].get('idGrupo');
	    		            					        		NS.storeDetalle.baseParams.cveControl=regSeleccionados[0].get('cveControl');
	    		            					        		NS.storeDetalle.baseParams.idUsuario1=regSeleccionados[0].get('usuarioUno');
	    		            					        		NS.storeDetalle.baseParams.idUsuario2=regSeleccionados[0].get('usuarioDos');
	    		            					        		//NS.storeDetalle.baseParams.idUsuario3=regSeleccionados[0].get('usuarioTres');
	    		            					        		
	    		            					        		//No perder el registro que carga los detalles, esto para sus modificaciones (Si es que se han seleccionado mas registros en el grid maestro)
	    		            					        		NS.seleccionPropuestaGlobal = regSeleccionados[0];
    		            				    				
        		            				    				NS.storeDetalle.load({
        		            				    				    params: {
        		            				    				        // specify params for the first page load if using paging
        		            				    				        start: 0,          
        		            				    				        limit: 500,
        		            				    				    }
        		            				    				});
        	        		                					
        		            				    				//POR PAGINACIÓN ES NECESARIO REALIZAR ESTA CONSULTA PARA SACAR EL TOTAL.
        		            									ConsultaPropuestasAction.sumaTotalDetalles(''+NS.seleccionPropuestaGlobal.get('cveControl'), function(resultado, e) {
        		            										if (resultado != '' && resultado != undefined && resultado != null){
        		            											//Ext.Msg.alert('SET', resultado);
        		            											Ext.getCmp(PF+'sumMN').setValue(NS.formatNumber(Math.round((resultado)*100)/100));
        		            										}else{
        		            											Ext.Msg.alert('SET', 'Error al obtener la suma total del detalle.');
        		            										}        		            										
        		            									});
        		            									
        		            								}else{
        		            									Ext.Msg.alert('SET','Error al actualizar multisociedades, favor de revisar información.');
        		            								}
        		            							}
        		            						});
        		                					
        		                				}
        		                				
        		                				NS.multisociedad = false;
        		                				return;
        		                			}
        		                			
        		                			var recSelectDetProp = NS.gridDetalle.getSelectionModel().getSelections();
        		                			var regSelectProp = NS.gridMaestroPropuestas.getSelectionModel().getSelections();
        		                			if(regSelectProp[0].get('user1') != '' || regSelectProp[0].get('user2') != '' || regSelectProp[0].get('user3') != ''){
        		                			Ext.Msg.confirm('SET', '¿Esta seguro de modificar una propuesta con autorizaciones?', function(btn){
        		                				if (btn === 'yes'){		
        		                					if(recSelectDetProp.length==1){
        	        		                			if(NS.cmbBancoPag.getValue() == '' && NS.cmbChequeraPag.getValue() == ''
        	        		                				&& NS.cmbBancoPagBenef.getValue() == '' &&    NS.cmbChequeraPagBenef.getValue() == ''
        	        		                				&&  NS.cmbFormaPago.getValue() == ''
        	        		                				&& Ext.getCmp(PF + 'dateNvaFecPago').getValue() == '' 
        	        		                				&& Ext.getCmp(PF + 'txtReferencia1').getValue() == ''
        	        		                				&& Ext.getCmp(PF + 'txtReferencia2').getValue() == ''
        	        		                					&& Ext.getCmp(PF + 'txtReferencia3').getValue() == ''){
        	        		                				
        	        		                				Ext.Msg.alert('SET','Realice algún cambio para poder modificar los registros.');
        	        		                				return;
        	        		                			}
                		                			}else{
                		                				if(NS.cmbBancoPag.getValue() == '' && NS.cmbChequeraPag.getValue() == ''
        	        		                				&& NS.cmbBancoPagBenef.getValue() == '' &&    NS.cmbChequeraPagBenef.getValue() == ''
        	        		                				&&  NS.cmbFormaPago.getValue() == ''
        	        		                				&& Ext.getCmp(PF + 'dateNvaFecPago').getValue() == ''){	
        	        		                				Ext.Msg.alert('SET','Realice algún cambio para poder modificar los registros.');
        	        		                				return;
        	        		                			}
                		                			}
                		                			//***** Valida que si tiene seleccionado un bancopag/benef tenga chequera. 	******/
                		                			if(NS.cmbBancoPag.getValue() != '' &&    NS.cmbChequeraPag.getValue() == ''){
                		                				Ext.Msg.alert('SET','Para modificar banco pagador es necesario seleccionar una chequera.');
                		                				return;
                		                			}
        		                				
        			                				if(NS.cmbBancoPagBenef.getValue() != '' &&    NS.cmbChequeraPagBenef.getValue() == ''){
        			                					Ext.Msg.alert('SET','Para modificar banco beneficiario es necesario seleccionar una chequera beneficiaria.');
        	    		                				return;
        			                				}
                		                			/*************************************************************************************/
                		                			if(NS.valida){
                		                				if(NS.cmbBancoPag.getValue() == '' || NS.cmbChequeraPag.getValue() == '' || NS.cmbBancoPagBenef.getValue() == '' || NS.cmbChequeraPagBenef.getValue() == ''){
                		                					Ext.Msg.alert('SET','Debe ingresar banco pagador y chequera pagadora, banco beneficiario y chequera beneficiaria.');
            	    		                				return;
                		                				}	
                		                			}
                		                			if(recSelectDetProp.length > 1) {
                		                				
                		                				/*if(NS.cmbFormaPago.getValue() != '' ){
                		                					Ext.Msg.alert('SET','Es necesario seleccionar solo un registro para modificar la forma de pago.');
                    		                				return;
                		                				}*/
                		                				
                		                				/*if(NS.cmbBancoPag.getValue() != '' &&    NS.cmbChequeraPag.getValue() != ''){
                		                					Ext.Msg.alert('SET','Es necesario seleccionar solo un registro para modificar la chequera pagadora.');
                    		                				return;
                		                				}*/
                		                				
                		                				/*if(NS.cmbBancoPagBenef.getValue() != '' &&    NS.cmbChequeraPagBenef.getValue() != ''){
                		                					Ext.Msg.alert('SET','Es necesario seleccionar solo un registro para modificar la chequera beneficiario');
                    		                				return;
                		                				}*/
                		                			
                		                				if(Ext.getCmp(PF + 'dateNvaFecPago').getValue() != ''){
                    		                				//Genera subpropuestas
                		                					NS.generarSubpropuestas();
                		                					return;
                		                				}
                		                				
                		                				NS.aceptarModificar();
                		                				
                		                			}else if(recSelectDetProp.length == 1) {
        	        		                			
                		                				if(Ext.getCmp(PF + 'dateNvaFecPago').getValue() != ''){
        	        		                				//Genera subpropuestas
        	    		                					NS.generarSubpropuestas();
        	    		                				}else{
                		                					//Modifica forma pago/ chequeras
                		                					NS.aceptarModificar();
                		                				}
                		                				
                		                			}
        		                				}
        		                			});
        		                			}else{
//        		                				if(recSelectDetProp.length==1){
//    	        		                			if(NS.cmbBancoPag.getValue() == '' && NS.cmbChequeraPag.getValue() == ''
//    	        		                				&& NS.cmbBancoPagBenef.getValue() == '' &&    NS.cmbChequeraPagBenef.getValue() == ''
//    	        		                				&&  NS.cmbFormaPago.getValue() == ''
//    	        		                				&& Ext.getCmp(PF + 'dateNvaFecPago').getValue() == '' 
//    	        		                				&& Ext.getCmp(PF + 'txtReferencia1').getValue() == ''
//    	        		                				&& Ext.getCmp(PF + 'txtReferencia2').getValue() == ''
//    	        		                				&& Ext.getCmp(PF + 'txtReferencia3').getValue() == ''){
//    	        		                				
//    	        		                				Ext.Msg.alert('SET','Realice algún cambio para poder modificar los registros.');
//    	        		                				return;
//    	        		                			}
//            		                			}else{
            		                				if(NS.cmbBancoPag.getValue() == '' && NS.cmbChequeraPag.getValue() == ''
    	        		                				&& NS.cmbBancoPagBenef.getValue() == '' &&    NS.cmbChequeraPagBenef.getValue() == ''
    	        		                				&&  NS.cmbFormaPago.getValue() == ''
    	        		                				&& Ext.getCmp(PF + 'dateNvaFecPago').getValue() == ''
    	        		                				&& Ext.getCmp(PF + 'txtReferencia1').getValue() == ''
    	    	        		                		&& Ext.getCmp(PF + 'txtReferencia2').getValue() == ''
    	    	        		                		&& Ext.getCmp(PF + 'txtReferencia3').getValue() == ''){	
    	        		                				Ext.Msg.alert('SET','Realice algún cambio para poder modificar los registros.');
    	        		                				return;
    	        		                			}
            		                			//}
            		                			//***** Valida que si tiene seleccionado un bancopag/benef tenga chequera. 	******/
            		                			if(NS.cmbBancoPag.getValue() != '' &&    NS.cmbChequeraPag.getValue() == ''){
            		                				Ext.Msg.alert('SET','Para modificar banco pagador es necesario seleccionar una chequera.');
            		                				return;
            		                			}
    		                				
    			                				if(NS.cmbBancoPagBenef.getValue() != '' &&    NS.cmbChequeraPagBenef.getValue() == ''){
    			                					Ext.Msg.alert('SET','Para modificar banco beneficiario es necesario seleccionar una chequera beneficiaria.');
    	    		                				return;
    			                				}
            		                			/*************************************************************************************/
            		                			if(NS.valida){
            		                				if(NS.cmbBancoPag.getValue() == '' || NS.cmbChequeraPag.getValue() == '' || NS.cmbBancoPagBenef.getValue() == '' || NS.cmbChequeraPagBenef.getValue() == ''){
            		                					Ext.Msg.alert('SET','Debe ingresar banco pagador y chequera pagadora, banco beneficiario y chequera beneficiaria.');
        	    		                				return;
            		                				}	
            		                			}
            		                			if(recSelectDetProp.length > 1) {
            		                				
            		                				/*if(NS.cmbFormaPago.getValue() != '' ){
            		                					Ext.Msg.alert('SET','Es necesario seleccionar solo un registro para modificar la forma de pago.');
                		                				return;
            		                				}*/
            		                				
            		                				/*if(NS.cmbBancoPag.getValue() != '' &&    NS.cmbChequeraPag.getValue() != ''){
            		                					Ext.Msg.alert('SET','Es necesario seleccionar solo un registro para modificar la chequera pagadora.');
                		                				return;
            		                				}*/
            		                				
            		                				/*if(NS.cmbBancoPagBenef.getValue() != '' &&    NS.cmbChequeraPagBenef.getValue() != ''){
            		                					Ext.Msg.alert('SET','Es necesario seleccionar solo un registro para modificar la chequera beneficiario');
                		                				return;
            		                				}*/
            		                			
            		                				if(Ext.getCmp(PF + 'dateNvaFecPago').getValue() != ''){
                		                				//Genera subpropuestas
            		                					NS.generarSubpropuestas();
            		                					return;
            		                				}
            		                				
            		                				NS.aceptarModificar();
            		                				
            		                			}else if(recSelectDetProp.length == 1) {
    	        		                			
            		                				if(Ext.getCmp(PF + 'dateNvaFecPago').getValue() != ''){
    	        		                				//Genera subpropuestas
    	    		                					NS.generarSubpropuestas();
    	    		                				}else{
            		                					//Modifica forma pago/ chequeras
            		                					NS.aceptarModificar();
            		                				}
            		                				
            		                			}
        		                			}
        		                		   }
        		                	}
                            	}
                            },
                            {
                                xtype: 'button',
                                text: 'Cancelar',
                                x: 870,
                                y: 135,
                                width: 80,
                                id: 'btnCancelar',
                                listeners: {
        		                	click: {
        		                		fn:function(e) {
                            				NS.cancelar();
        		                		}
        		                	}
                            	}
                            },
                            {
                        	   	xtype: 'fieldset',
                                title: 'Referencias',
                                id:PF+'fieldReferencias',
                                name:PF+'fieldReferencias',
                               	x: 160,
                                y: 90,
                                width: 700,
                                height: 85,
                                layout: 'absolute',
                                items: [
									{
									    xtype: 'label',
									    text: 'Referencia 1',
											x:10,
											y:0,
											
									},
									{
									    xtype: 'textfield',
									    id: PF+'txtReferencia1',
									    name: PF+'txtReferencia1',
									    x: 90,
									    y: 0,
									    maxLength: 35,
									    width: 230,
									    enableKeyEvents:true,
		                        		listeners:{
		                        			keypress:{
		                        	 			fn:function(caja, e) {
		                        	 				if(e.keyCode ==34){ 
		                        	 					//NS.buscar();
		                        	 				}
		                        	 			}
		                        	 		},
		                        	 		keydown:{
		                        	 			fn:function(caja, e) {
		                        	 				if(!caja.isValid()){
		                        	 					caja.setValue(caja.getValue().substring(0, 35));
		                        	 				}
		                        	 			}
		                        	 		},
		                        	 		keyup:{
		                        	 			fn:function(caja, e) {	 				
		                        	 				if(!caja.isValid()){
		                        	 					caja.setValue(caja.getValue().substring(0, 35));
		                        	 				}
		                        	 				
		                        	 			}
		                        	 		},
		                        		}
		                            
                        	
									    
									},
									{
									    xtype: 'label',
									    text: 'Referencia 2',
											x:350,
											y:0,
									},
									{
		                                xtype: 'textfield',
		                                x: 425,
		                                y: 0,
		                                maxLength: 35,
		                                width: 230,
		                                id: PF + 'txtReferencia2',
		                                name: PF+'txtReferencia2',
		                                enableKeyEvents:true,
		                        		listeners:{
		                        			keypress:{
		                        	 			fn:function(caja, e) {
		                        	 				if(e.keyCode ==34){ 
		                        	 					//NS.buscar();
		                        	 				}
		                        	 			}
		                        	 		},
		                        	 		keydown:{
		                        	 			fn:function(caja, e) {
		                        	 				if(!caja.isValid()){
		                        	 					caja.setValue(caja.getValue().substring(0, 35));
		                        	 				}
		                        	 			}
		                        	 		},
		                        	 		keyup:{
		                        	 			fn:function(caja, e) {	 				
		                        	 				if(!caja.isValid()){
		                        	 					caja.setValue(caja.getValue().substring(0, 35));
		                        	 				}
		                        	 				
		                        	 			}
		                        	 		},
		                        		}
		                            
                        	
		                            },
		                            {
									    xtype: 'label',
									    text: 'Referencia 3',
											x:10,
											y:30,
									},
									{
		                                xtype: 'textfield',
		                                x: 90,
		                                y: 30,
		                                width: 230,
		                                maxLength: 35,
		                                id: PF + 'txtReferencia3',
		                                name: PF+'txtReferencia3',
		                        		enableKeyEvents:true,
		                        		listeners:{
		                        			keypress:{
		                        	 			fn:function(caja, e) {
		                        	 				if(e.keyCode ==34){ 
		                        	 					//NS.buscar();
		                        	 				}
		                        	 			}
		                        	 		},
		                        	 		keydown:{
		                        	 			fn:function(caja, e) {
		                        	 				if(!caja.isValid()){
		                        	 					caja.setValue(caja.getValue().substring(0, 35));
		                        	 				}
		                        	 			}
		                        	 		},
		                        	 		keyup:{
		                        	 			fn:function(caja, e) {	 				
		                        	 				if(!caja.isValid()){
		                        	 					caja.setValue(caja.getValue().substring(0, 35));
		                        	 				}
		                        	 				
		                        	 			}
		                        	 		},
		                        		}
		                            }
                        		]
                           },
                            {
                        	   	xtype: 'fieldset',
                                title: 'MultiSociedad',
                                id:PF+'fieldMultiSociedad',
                                name:PF+'fieldMultiSociedad',
                               	x: 160,
                                y: 100,
                                hidden:true,
                                width: 700,
                                height: 60,
                                layout: 'absolute',
                                items: [
									{
									    xtype: 'textfield',
									    id: PF+'txtEmpresaMulti',
									    name: PF+'txtEmpresaMulti',
									    x: 10,
									    y: 0,
									    width: 40,
									    listeners:{
									    	change:{
									    		fn:function(caja, valor){
									    			var comboValue = BFwrk.Util.updateTextFieldToCombo(PF+'txtEmpresaMulti',NS.cmbEmpresaMulti.getId());
									    			NS.noEmpresaMulti = valor;
									    			
									    			var recSelectDetProp = NS.gridDetalle.getSelectionModel().getSelections();
													
													NS.sDivisa = recSelectDetProp[0].get('idDivisa');
													
													NS.storeBancoPagMulti.baseParams.idDivisa = NS.sDivisa;
													NS.storeBancoPagMulti.baseParams.noEmpresa = ''+valor;
													
									    			NS.storeBancoPagMulti.load();
									    		}
									    	}
									    }
									},
									NS.cmbEmpresaMulti,
									{
		                                xtype: 'textfield',
		                                x: 275,
		                                y: 0,
		                                width: 50,
		                                id: PF + 'txtBancoMulti'
		                            },
		                            NS.cmbBancoPagMulti,
		                            NS.cmbChequeraPagMulti
                        		]
                           }//fin field 
                            
                        ]
                    }
                ]
            }
        ]
	});
	
	
	/** 						Agregado EMS 28/09/15 										*
	 * CUANDO SE ABRE UNA PESTAÑA Y UNA VENTANA MODAL, SI SE CIERRA LA PESTAÑA				*
	 * LOS OBJETOS CREADOS DE LA VENTANA MODAL NO SE ELIMINAN, SI SE VUELVE ABRIR LA		*
	 * VENTANA MODAL DUPLICA N VECES LOS OBJETOS Y LA VENTANA NO FUNCIONAN LOS COMPONENTES 	**/
	
	function verificaComponentesCreados(){
		
		/**
		 * AGREGAR UN IF A ESTE METODO LOS COMPONENTES QUE SE CREEN EN VENTANAS MODALES 
		 * radios, combos.
		 */
		
		var compt; 
		
		/***** Ventana WinLogin y winModificacion *****/
		
		compt = Ext.getCmp(PF + 'txtPsw');
		if(compt != null || compt != undefined ){ compt.destroy(); }
		
		compt = Ext.getCmp(PF + 'fecModificacion');
		if(compt != null || compt != undefined ){ compt.destroy(); }
		
		
		/**************************/
		
		/*** Ventana Parametros ***/
		compt = Ext.getCmp(PF + 'optParametros');
		if(compt != null || compt != undefined ){ compt.destroy(); }
		/**************************/
		
		/*** Ventana Bloqueo de Proveedores ***/
		compt = Ext.getCmp(PF+'cmbEmpresasBloq');
		if(compt != null || compt != undefined ){ compt.destroy(); }
		
		compt = Ext.getCmp(PF+'cmbBeneficiarioBloq');
		if(compt != null || compt != undefined ){ compt.destroy(); }
		
		compt = Ext.getCmp(PF+'txtIdProveedorBloq');
		if(compt != null || compt != undefined ){ compt.destroy(); }
		
		compt = Ext.getCmp(PF + 'btnComboBenefBloq');
		if(compt != null || compt != undefined ){ compt.destroy(); }
		
		compt = Ext.getCmp(PF+'txtDocumentoBloq');
		if(compt != null || compt != undefined ){ compt.destroy(); }
		
		compt = Ext.getCmp(PF+'txtImporteBloq');
		if(compt != null || compt != undefined ){ compt.destroy(); }
		
		compt = Ext.getCmp(PF+'txtFechaBloq');
		if(compt != null || compt != undefined ){ compt.destroy(); }
		
		compt = Ext.getCmp(PF+'txtConceptoBloq');
		if(compt != null || compt != undefined ){ compt.destroy(); }
		
		compt = Ext.getCmp(PF+'btnBuscarBloq');
		if(compt != null || compt != undefined ){ compt.destroy(); }
		
		compt = Ext.getCmp(PF+'txtMotivoBloq');
		if(compt != null || compt != undefined ){ compt.destroy(); }
		
		compt = Ext.getCmp(PF+'chkBloqProveedor');
		if(compt != null || compt != undefined ){ compt.destroy(); }
		
		compt = Ext.getCmp(PF+'chkBloqDoc');
		if(compt != null || compt != undefined ){ compt.destroy(); }
		
		compt = Ext.getCmp(PF+'btnAceptarBloq');
		if(compt != null || compt != undefined ){ compt.destroy(); }
		
		compt = Ext.getCmp(PF+'btnDesbloquear');
		if(compt != null || compt != undefined ){ compt.destroy(); }
		
		compt = Ext.getCmp(PF+'btnBloq');
		if(compt != null || compt != undefined ){ compt.destroy(); }
		
		compt = Ext.getCmp(PF+'chkBloqSetExcel');
		if(compt != null || compt != undefined ){ compt.destroy(); }
		
		compt = Ext.getCmp(PF+'chkBloqSAPExcel');
		if(compt != null || compt != undefined ){ compt.destroy(); }
		
		compt = Ext.getCmp(PF+'btnExcelBloqueados');
		if(compt != null || compt != undefined ){ compt.destroy(); }
		
		compt = Ext.getCmp(PF+'cmbPeriodos');
		if(compt != null || compt != undefined ){ compt.destroy(); }
		
		/**************************************/
		
		
		/*** Ventana Pagos cruzados ***/
		
		compt = Ext.getCmp(PF + 'cmbBeneficiarioCruz');
		if(compt != null || compt != undefined ){ compt.destroy(); }
		
		compt = Ext.getCmp(PF + 'cmbBeneficiarioNvoPag');
		if(compt != null || compt != undefined ){ compt.destroy(); }
		
		compt = Ext.getCmp(PF + 'cmbDivisaOriginal');
		if(compt != null || compt != undefined ){ compt.destroy(); }
		
		compt = Ext.getCmp(PF + 'cmbDivisaPago');
		if(compt != null || compt != undefined ){ compt.destroy(); }
		
		compt = Ext.getCmp(PF + 'txtIdNuevoProvCruz');
		if(compt != null || compt != undefined ){ compt.destroy(); }
		
		compt = Ext.getCmp(PF + 'btnNuevoBenefCruz');
		if(compt != null || compt != undefined ){ compt.destroy(); }
		
		compt = Ext.getCmp(PF + 'txtIdDivisaOriginal');
		if(compt != null || compt != undefined ){ compt.destroy(); }
		
		compt = Ext.getCmp(PF + 'txtIdDivisaPago');
		if(compt != null || compt != undefined ){ compt.destroy(); }
		
		compt = Ext.getCmp(PF + 'btnAceptarNuevoProv');
		if(compt != null || compt != undefined ){ compt.destroy(); }
		
		compt = Ext.getCmp(PF + 'txtIdProveedorCruz');
		if(compt != null || compt != undefined ){ compt.destroy(); }
		
		compt = Ext.getCmp(PF + 'btnComboBenefCruz');
		if(compt != null || compt != undefined ){ compt.destroy(); }
		
		compt = Ext.getCmp(PF + 'btnNuevoPagoCruzado');
		if(compt != null || compt != undefined ){ compt.destroy(); }
		
		compt = Ext.getCmp(PF + 'btnEliminarPagoCruzado');
		if(compt != null || compt != undefined ){ compt.destroy(); }
		
	}
	
	 NS.validaAutorizacionBloqueados = function(){
		
		/************  VALIDACION BLOQUEO PROVEEDORES AUTORIZA *******************/
		var matEmpresas = new Array();
		var matProp = new Array();
		
		var recordsAutorizar=NS.gridMaestroPropuestas.getSelectionModel().getSelections();
		if(recordsAutorizar.length===0) {
			Ext.Msg.alert('SET','Es necesario seleccionar algun registro');
			return;
		}else {
			
			if(recordsAutorizar.length===1 && recordsAutorizar[0].get('estatus') == 'BLOQUEO SAP'){
				Ext.Msg.alert("SET", 'No se puede autorizar la propuesta porque tiene documento bloqueado de SAP.');
				habilitarBotones(true);
				return;
			}
			
			
			for(var i = 0; i < recordsAutorizar.length; i++){
				//Salta Registros transferencia sin chequera o banco beneficiario
				if(recordsAutorizar[i].get('color') == 'color:#C5B8D6'){
					continue;
				}
				
				var vecEmp = {};
				var vecProp = {};
				
				vecEmp.noEmpresa = recordsAutorizar[i].get('idGrupoFlujo');
				vecProp.cveControl = recordsAutorizar[i].get('cveControl');
				
				matEmpresas[i] = vecEmp;
				matProp[i] = vecProp;
			}
			
			var jSonEmpresas = Ext.util.JSON.encode(matEmpresas);
			var jSonPropuestas = Ext.util.JSON.encode(matProp);
			//Validar las chequeras pagadoras y beneficiarias.
			
			ConsultaPropuestasAction.validarBancoChequera(jSonPropuestas, function(result, e){
				
				if(e.message=="Unable to connect to the server."){
					Ext.Msg.alert('SET','Error de conexión al servidor');
					return;
				}
				console.log("respuesta de valida "+result);
				if(result != null && result != undefined && result.length != '') {
					
					if(result.error != ''){
						
						if(result.error == '1'){
							var concat = '';
							for(var i = 0; i<result.lista.length;i++){
          						concat += result.lista[i]+" "; 
          					}
							
							Ext.Msg.confirm('SET', 'Los registros con ID PROPUESTA ' + concat +' no tienen asignado Banco/Chequera pagadora'
									+' los cuales no se autorizaran, ¿Desea continuar?', function(btn) {
		          				if(btn == 'yes') {
		          					
		          					for(var i = 0; i<result.lista.length;i++){
		          						matrizBancoChequerasGlob[i] = result.lista[i];
		          						//alert(matrizBancoChequerasGlob[i]);
		          					}
		          					
		          					/******************************************************************************/
		          					NS.storeAutorizarProvBloq.baseParams.empresas=jSonEmpresas;
		          					NS.storeAutorizarProvBloq.baseParams.propuestas=jSonPropuestas;
		          				
		          					NS.storeAutorizarProvBloq.load();
		          					/******************************************************************************/
		          				
		          				}else{
		          					habilitarBotones(true);
		          					return;
		          				}
		          				
		          			});
							
						}else if(result.error == '2'){
							
							var concat = '';
							for(var i = 0; i<result.lista.length;i++){
          						concat += result.lista[i]+" "; 
          					}
							
							Ext.Msg.confirm('SET', 'Los registros con ID PROPUESTA ' + concat +' no tienen asignado Banco/Chequera beneficiaria'
									+' los cuales no se autorizarán, ¿Desea continuar?', function(btn) {
		          				if(btn == 'yes') {
		          					
		          					for(var i = 0; i<result.lista.length;i++){
		          						matrizBancoChequerasGlob[i] = result.lista[i]; 
		          					}
		          					
		          					/******************************************************************************/
		          					NS.storeAutorizarProvBloq.baseParams.empresas=jSonEmpresas;
		          					NS.storeAutorizarProvBloq.baseParams.propuestas=jSonPropuestas;
		          				
		          					NS.storeAutorizarProvBloq.load();
		          					/******************************************************************************/
		          					
		          				}else{
		          					return;
		          				}
		          				
		          			});
							
						}else if(result.error == '3'){
							
							var concat = '';
							for(var i = 0; i<result.lista.length;i++){
          						concat += result.lista[i]+" "; 
          					}
							
							Ext.Msg.confirm('SET', 'Los registros con ID PROPUESTA ' + concat +' no tienen asignado Banco/Chequera pagadora y'
									+' Banco/Chequera beneficiaria los cuales no se autorizaran, ¿Desea continuar?', function(btn) {
		          				if(btn == 'yes') {
		          					
		          					for(var i = 0; i<result.lista.length;i++){
		          						matrizBancoChequerasGlob[i] = result.lista[i]; 
		          						//alert(matrizBancoChequerasGlob[i]);
		          					}
		          					
		          					/******************************************************************************/
		          					NS.storeAutorizarProvBloq.baseParams.empresas=jSonEmpresas;
		          					NS.storeAutorizarProvBloq.baseParams.propuestas=jSonPropuestas;
		          				
		          					NS.storeAutorizarProvBloq.load();
		          					/******************************************************************************/
		          					
		          				}else{
		          					return;
		          				}
		          				
		          			});
							
						}//fin if result.error ==3
						
					}else{
						/******************************************************************************/
      					NS.storeAutorizarProvBloq.baseParams.empresas=jSonEmpresas;
      					NS.storeAutorizarProvBloq.baseParams.propuestas=jSonPropuestas;
      				
      					NS.storeAutorizarProvBloq.load();
      					
      					/******************************************************************************/
					} //fin else result.error != ''
				}
			});
			
		}
		
	}
	
	//Agregado EMS: 09/11/2015 - Genera subpropuestas
	NS.generarSubpropuestas = function(num,prefix){
	
		var seleccion = NS.gridDetalle.getSelectionModel().getSelections();
		var folios = '', noDoctos = '', empresa = ''; 
		var montoMaximo = 0; 
		
		mascara.show();
		
		for(var i=0; i < seleccion.length; i++){
			
			folios = folios + seleccion[i].get('noFolioDet') + ",";
			noDoctos = noDoctos + "'" + seleccion[i].get('noDocto') + "',";
			empresa = seleccion[i].get('noEmpresa');
            
			montoMaximo = montoMaximo + seleccion[i].get('importe');
		}
     
		//alert("Folios: " + folios + '\nnoDoctos: '+noDoctos + '\nempresa: '+ empresa + '\nMontoMaximo: ' + montoMaximo + '\nMarcados: '+ marcados);
		
		 var stencero = "";
		 if(NS.seleccionPropuestaGlobal.get('cupoTotal') - montoMaximo == 0){
			 stencero = "CUPO EN CERO:";
		 }
		 if(NS.seleccionPropuestaGlobal.get('cupoTotal') - montoMaximo < 0){
			 stencero = "CUPO EN NEGATIVO:";
		 }
		 
		 if(NS.seleccionPropuestaGlobal.get('cveControl') != '' || NS.seleccionPropuestaGlobal.get('cveControl') != null || NS.seleccionPropuestaGlobal.get('cveControl') != undefined){
			 var nvaProp = NS.seleccionPropuestaGlobal.get('cveControl') + '-1';
			 if(numCaracter(nvaProp)>1){
				 nvaProp = NS.seleccionPropuestaGlobal.get('cveControl');
			 }
		 }else{
			 mascara.hide();
			 Ext.Msg.alert('SET','La propuesta seleccionada no tiene Id.');
			 return; 
		 }
		 
         //Verificamos que no exista la nueva propuesta, si existe se va incrementando el consecutivo del final
		 ConsultaPropuestasAction.validaCvePropuesta(nvaProp, function(result, e){
			 
			 if(e.message=="Unable to connect to the server."){
					Ext.Msg.alert('SET','Error de conexión al servidor');
					mascara.hide();
					return;
				}
			 
			 if(result != null && result != '' && result != undefined) {								
					
				 if(result.error != ''){
					 Ext.Msg.alert('SET',result.error);
					 
					//Inserta en bitacora propuesta
						ConsultaPropuestasAction.insertaBitacoraPropuesta('subpropuesta', ''+NS.seleccionPropuestaGlobal.get('cveControl'), 
								'', 'Error al validar subpropuesta' , function(result, e){
							
							if(e.message=="Unable to connect to the server."){
								mascara.hide();
								Ext.Msg.alert('SET','Error de conexión al servidor');
								return;
							}
							
							if(result != null && result.length != '') {
								if(result.error != ''){
									Ext.Msg.alert('SET',''+result.error);
								}
							}
						});
					 mascara.hide();	
					 return;
				 }else if(result.mensaje != ''){
					 //result.mensaje tiene la nueva clave de la propuesta.
					 //inicia actualizacion
					 
					 nvaProp = result.mensaje;
					 var fecha = cambiarFecha(''+Ext.getCmp(PF + 'dateNvaFecPago').getValue());
					 
					 ConsultaPropuestasAction.insertaSubPropuesta(montoMaximo, nvaProp, NS.seleccionPropuestaGlobal.get('cveControl'), 
							 										fecha, 
							 										function(result, e){
						
						 if(e.message=="Unable to connect to the server."){
							 	mascara.hide();
								Ext.Msg.alert('SET','Error de conexión al servidor');
								return;
						}
						 
						 if(result != null && result != '' && result != undefined) {								
								
							 if(result.error != ''){
								 Ext.Msg.alert('SET',result.error);
								 
								//Inserta en bitacora propuesta
									ConsultaPropuestasAction.insertaBitacoraPropuesta('subpropuesta', ''+NS.seleccionPropuestaGlobal.get('cveControl'), 
											'', 'Error al insertar subpropuesta' , function(result, e){
										
										if(e.message=="Unable to connect to the server."){
											mascara.hide();
											Ext.Msg.alert('SET','Error de conexión al servidor');
											return;
										}
										
										if(result != null && result.length != '') {
											if(result.error != ''){
												Ext.Msg.alert('SET',''+result.error);
											}
										}
									});
								  mascara.hide();
								 return;
							 }else if(result.mensaje != ''){
								 
								 //var sDescModificado = "Se Creo Sub Propuesta " + nvaProp + " a partir de propuesta " + NS.seleccionPropuestaGlobal.get('cveControl');
								 
								 noDoctos = noDoctos.substring(0, noDoctos.length -1);
								 
								 ConsultaPropuestasAction.actualizaPropuesta(nvaProp, noDoctos, 
										 									NS.seleccionPropuestaGlobal.get('cveControl'),	
										 									fecha, function(result, e){
										
									 if(e.message=="Unable to connect to the server."){
										 	mascara.hide();
											Ext.Msg.alert('SET','Error de conexión al servidor');
											return;
										}
									 
									if(result != null && result != '' && result != undefined) {								
										 if(result.error != ''){
											 Ext.Msg.alert('SET',result.error);
											 
											//Inserta en bitacora propuesta
												ConsultaPropuestasAction.insertaBitacoraPropuesta('subpropuesta', ''+NS.seleccionPropuestaGlobal.get('cveControl'), 
														'', 'Error al actualizar propuesta' , function(result, e){
													
													if(e.message=="Unable to connect to the server."){
														mascara.hide();
														Ext.Msg.alert('SET','Error de conexión al servidor');
														return;
													}
													
													if(result != null && result.length != '') {
														if(result.error != ''){
															Ext.Msg.alert('SET',''+result.error);
														}
													}
												});
											 mascara.hide();
											 return;
										 }else if(result.mensaje != ''){
											 //sDescModificado = "Se Actualizan fecha propuesta,valor y clave del pago para " +nvaProp;
											 
											 //Actualiza montos de la propuesta original
											 ConsultaPropuestasAction.actualizaMontoPropuesta(montoMaximo, NS.seleccionPropuestaGlobal.get('cveControl'), 
													 										stencero, function(result, e){
												 if(e.message=="Unable to connect to the server."){
													 	mascara.hide();
														Ext.Msg.alert('SET','Error de conexión al servidor');
														return;
													}
												 
												if(result != null && result != '' && result != undefined) {								
														
													 if(result.error != ''){
														 Ext.Msg.alert('SET',result.error);
														 
														//Inserta en bitacora propuesta
															ConsultaPropuestasAction.insertaBitacoraPropuesta('subpropuesta', ''+NS.seleccionPropuestaGlobal.get('cveControl'), 
																	'', 'Error al actualizar montos en propuesta' , function(result, e){
																
																if(e.message=="Unable to connect to the server."){
																	mascara.hide();
																	Ext.Msg.alert('SET','Error de conexión al servidor');
																	return;
																}
																
																if(result != null && result.length != '') {
																	if(result.error != ''){
																		Ext.Msg.alert('SET',''+result.error);
																	}
																}
															});
														 mascara.hide();
														 return;
													 }else if(result.mensaje != ''){
														 //sDescModificado = "SE Actualizo Monto Maximo de propuesta por " + montoMaximo;
														 //Inserta bitacora_propuesta
														 Ext.Msg.alert('SET','Proceso terminado con éxito.');
														 
														//Inserta en bitacora propuesta
															ConsultaPropuestasAction.insertaBitacoraPropuesta('subpropuesta', ''+NS.seleccionPropuestaGlobal.get('cveControl'), 
																	''+NS.seleccionPropuestaGlobal.get('cveControl'),
																	nvaProp , function(result, e){
																
																if(e.message=="Unable to connect to the server."){
																	mascara.hide();
																	Ext.Msg.alert('SET','Error de conexión al servidor');
																	return;
																}
																
																if(result != null && result.length != '') {
																	if(result.error != ''){
																		Ext.Msg.alert('SET',''+result.error);
																		mascara.hide();
																	}
																}
															});
															
														 NS.limpiarTodo();
														 mascara.hide();
													 }
												}
												}); //Fin actualiza montos.
											 
										 }
									}
								}); //Fin actualiza propuesta
								 
							 }
						 }
					 }); //Fin inserta propuesta
				        
				 }
				
			 }
			 
		 }); //Fin validaCvePropiuesta
		 
		 //Si ya tiene guion
		// numCaracter(nvaProp);
		 
	}
	
	function numCaracter(cveControl){
		var numGuiones = 0;
		var posIni = cveControl.indexOf('-');
		
	    if(posIni > 0){
	    	numGuiones = numGuiones + 1
	    }
	        
	    for(var i = posIni+1; i <cveControl.length; i++){
	    	if(cveControl[i] == '-'){
	    		numGuiones = numGuiones + 1;
	    	}
	    }
	    return numGuiones;
	}
	
	function habilitarBotones(b){
		Ext.getCmp(PF + 'cmdAutorizar').setDisabled(!b);
		Ext.getCmp(PF + 'cmbQuitAut').setDisabled(!b);
		Ext.getCmp('btnModifProp').setDisabled(!b);
		//Ext.getCmp(PF + 'btnModificar').setDisabled(!b);
		//Ext.getCmp(PF + 'btnModificarPag').setDisabled(!b);
		Ext.getCmp(PF + 'btnEliminar').setDisabled(!b);
	}
	
	NS.habilitaDiversos = function habilitarBotones(b){
		
		Ext.getCmp(PF + 'txtBanco').setDisabled(!b);
		Ext.getCmp(PF + 'txtBancoBenef').setDisabled(!b);
		Ext.getCmp(PF + 'dateNvaFecPago').setDisabled(!b);
        NS.cmbFormaPago.setDisabled(!b)
        NS.cmbBancoPag.setDisabled(!b)
        NS.cmbChequeraPag.setDisabled(!b)
    	NS.cmbBancoPagBenef.setDisabled(!b)
        NS.cmbChequeraPagBenef.setDisabled(!b)
	}
	
	NS.validaDatosMultiSociedad = function(){
		
		if(Ext.getCmp(PF+'txtEmpresaMulti').getValue() == '' || Ext.getCmp(PF+'txtEmpresaMulti').getValue() == null
				|| Ext.getCmp(PF+'txtEmpresaMulti').getValue() == undefined){
			return false;
		}
		
		if(NS.cmbBancoPagMulti.getValue() == '' || NS.cmbBancoPagMulti.getValue() == null
				|| NS.cmbBancoPagMulti.getValue() == undefined){
			return false;
		}
		
		if(NS.cmbChequeraPagMulti.getValue() == '' || NS.cmbChequeraPagMulti.getValue() == null
				|| NS.cmbChequeraPagMulti.getValue() == undefined){
			return false;
		}
		
		return true;
	};
	
	NS.limpiaCamposMultisociedad = function(){
		//Limpia variables multisociedad
		Ext.getCmp(PF+'txtEmpresaMulti').setValue('');
		Ext.getCmp(PF+'txtBancoMulti').setValue('');
		NS.cmbEmpresaMulti.setValue('');
		NS.cmbBancoPagMulti.reset();
		NS.storeBancoPagMulti.removeAll();
		NS.cmbChequeraPagMulti.reset();
		NS.storeChequeraPagMulti.removeAll();
	};
	
	
    //	CREACIÓN DE VENTANA PARA EL SIMULADOR DE PROPUESTAS
	NS.storeColoresProp = new Ext.data.DirectStore( {
		paramsAsHash: false,
		baseParams: { },
		root: '',
		paramOrder: [],
		directFn: ConsultaPropuestasAction.obtenerColoresPropuesta,
		idProperty: 'color',
		fields: [
			{name: 'tipo' },
			{name: 'color'},
			{name: 'descripcionUno'},
			{name: 'descripcionDos'}
		],
		listeners: {
			load: function(s, records){
				//var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeAcreedoresExc, msg:"Cargando..."});
				mascara.hide();
			} 
		}
	});
	
	
	NS.selColoresProp = new Ext.grid.CheckboxSelectionModel({
		singleSelect: false
	});
	
	NS.colColoresProp = new Ext.grid.ColumnModel
 	([	
 	  	//NS.selColoresProp,
 	  	{	
 			header :'Color',
 			width :100,
 			sortable :true,
 			dataIndex :'', 
 			renderer: function (value, meta, record) {
 				meta.attr = 'style="background-color:'+record.get('color')+';"';
 	            return value;
 			}
 		},{
 			header :'Descripción',
 			width :450,
 			sortable :true,
 			dataIndex :'descripcionDos'
 		},{
 			header :'Tipo',
 			width : 100,
 			sortable :true,
 			dataIndex :'descripcionUno',
 		}
 	  ]);
	
	NS.gridColoresProp = new Ext.grid.EditorGridPanel ({
		store: NS.storeColoresProp,
		id: PF + 'gridColoresProp', 
		name: PF + 'gridColoresProp',
		cm: NS.colColoresProp,
		sm: NS.selColoresProp,
		width: 650,
		height:200,
		x: 0,
		y: 0,
		stripeRows: true,
		columnLines: true,
		listeners: {
			click: {
				fn: function(grid) {
					
				}
			}
		}
	});
	
	
	NS.storeColoresDet = new Ext.data.DirectStore( {
		paramsAsHash: false,
		baseParams: { },
		root: '',
		paramOrder: [],
		directFn: ConsultaPropuestasAction.obtenerColoresDetalle,
		idProperty: 'color',
		fields: [
			{name: 'tipo' },
			{name: 'color'},
			{name: 'descripcionUno'},
			{name: 'descripcionDos'}
		],
		listeners: {
			load: function(s, records){
				//var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeAcreedoresExc, msg:"Cargando..."});
				mascara.hide();
			} 
		}
	});
	
	NS.selColoresDet = new Ext.grid.CheckboxSelectionModel({
		singleSelect: false
	});
	
	NS.colColoresDet = new Ext.grid.ColumnModel
 	([	
 	  	//NS.selColoresDet,
 	  	{	
 			header :'Color',
 			width :100,
 			sortable :true,
 			dataIndex :'', 
 			renderer: function (value, meta, record) {
 				meta.attr = 'style="background-color:'+record.get('color')+';"';
 	            return value;
 			}
 		},{
 			header :'Descripción',
 			width :450,
 			sortable :true,
 			dataIndex :'descripcionDos'
 		},{
 			header :'Tipo',
 			width :100,
 			sortable :true,
 			dataIndex :'descripcionUno',
 		}
 	  ]);
	
	NS.gridColoresDet = new Ext.grid.EditorGridPanel ({
		store: NS.storeColoresDet,
		id: PF + 'gridColoresDet', 
		name: PF + 'gridColoresDet',
		cm: NS.colColoresDet,
		sm: NS.selColoresDet,
		width: 650,
		height:200,
		x: 0,
		y: 0,
		stripeRows: true,
		columnLines: true,
		listeners: {
			click: {
				fn: function(grid) {
					
				}
			}
		}
	});
	
    var winCatalogoColores = new Ext.Window({
		title: 'Catálogo de colores',
		modal: true,
		shadow: true,
		//closable: false,
		closeAction: 'hide',
		width: 700,
	   	height: 500,
	   	//x: 800,
	   	//y: 100,
	   	layout: 'absolute',
	   	plain: true,
	    bodyStyle: 'padding:10px;',
	    buttonAlign: 'center',
	    draggable: false,
	    resizable: false,
	   	items: [
	   	        {
	   	        	xtype: 'fieldset',
	                title: 'Colores Propuestas',
	                width: 680,
	                height: 220,
	                x: 10,
	                y: 10,
	                layout: 'absolute',
	                id: PF + 'fieldSimProp',
	                items: [
	                        NS.gridColoresProp
					]
	   	        },{
	   	        	xtype: 'fieldset',
	                title: 'Colores Detallado Propuestas',
	                width: 680,
	                height: 220,
	                x: 10,
	                y: 240,
	                layout: 'absolute',
	                id: PF + 'fieldSimDetProp',
	                items: [
	                        NS.gridColoresDet
					]
	   	        }  	       
	   	        ],
	   	        listeners:{
	   	        	show:{
	   	        		fn:function(){
	   	        			//	Limpiar componentes y remover stores de la ventana modal.
	   	        			NS.storeColoresProp.removeAll();
	   	        			NS.gridColoresProp.getView().refresh();
	   	        			NS.storeColoresDet.removeAll();
	   	        			NS.gridColoresDet.getView().refresh();
	   	        			
	   	        			NS.storeColoresProp.load();
	   	        			NS.storeColoresDet.load();
	   	        			mascara.show();
	   	        		}
	   	        	},
	   	        	hide:{
	   	        		fn:function(){
	   	        			mascara.hide();
	   	        		}
	   	        	}
	   	        }	
		});
    //FIN VENTANA CATÁLOGO COLORES.
    
    //FUNCION PARA El EVENTO DE CLICK EN LAS COLUMNAS DEL GRID MAESTRO PROPUESTAS
    NS.clickGridMaestroPropuestas = function(){
    	NS.cancelar();
			mascara.show();
			
		var regSeleccionados = NS.gridMaestroPropuestas.getSelectionModel().getSelections();
		var importeMN = 0;
		var importeDLS = 0;
		var importeEUR = 0;
		var importeOTR = 0;
		
		Ext.getCmp(PF + 'txtImporteProp').setValue(NS.formatNumber(Math.round((importeMN)*100)/100));
		Ext.getCmp(PF + 'txtImporteDLSProp').setValue(NS.formatNumber(Math.round((importeDLS)*100)/100));
		Ext.getCmp(PF + 'txtImporteEURProp').setValue(NS.formatNumber(Math.round((importeEUR)*100)/100));
		Ext.getCmp(PF + 'txtImporteOTRProp').setValue(NS.formatNumber(Math.round((importeOTR)*100)/100 ));
		
		if(regSeleccionados != null && regSeleccionados != undefined && regSeleccionados != '') {
			
			for(var i=0; i<regSeleccionados.length; i++) {
				
				if(regSeleccionados[i].get('concepto') != null && regSeleccionados[i].get('concepto') != undefined && regSeleccionados[i].get('concepto') != ''){
					
					if(regSeleccionados[i].get('concepto').indexOf('MN') > 0){
						importeMN += parseFloat(NS.unformatNumber('' + regSeleccionados[i].get('cupoTotal')));
						Ext.getCmp(PF + 'txtImporteProp').setValue(NS.formatNumber(Math.round((importeMN)*100)/100 ));
					}else if(regSeleccionados[i].get('concepto').indexOf('DLS') > 0){
						importeDLS += parseFloat(NS.unformatNumber('' + regSeleccionados[i].get('cupoTotal')));
						Ext.getCmp(PF + 'txtImporteDLSProp').setValue(NS.formatNumber(Math.round((importeDLS)*100)/100));
					}else if(regSeleccionados[i].get('concepto').indexOf('EUR') > 0){
						importeEUR += parseFloat(NS.unformatNumber('' + regSeleccionados[i].get('cupoTotal')));
						Ext.getCmp(PF + 'txtImporteEURProp').setValue(NS.formatNumber(Math.round((importeEUR)*100)/100 ));
					}else{
						importeOTR += parseFloat(NS.unformatNumber('' + regSeleccionados[i].get('cupoTotal')));
						Ext.getCmp(PF + 'txtImporteOTRProp').setValue(NS.formatNumber(Math.round((importeOTR)*100)/100 ));
					}
				}
					
			}
			
			NS.gridDetalle.store.removeAll();
			NS.gridDetalle.getView().refresh();
			 NS.storeDetalle = new Ext.data.DirectStore({
			  	 	paramsAsHash: false,
			  	 	baseParams: {
						idGrupoEmpresa:0, 
						idGrupoRubro:0,
						cveControl:'',
						idUsuario1:0,
						idUsuario2:0,
						idUsuario3:0
					},
					root: '',
					paramOrder:['idGrupoEmpresa','idGrupoRubro','cveControl','idUsuario1','idUsuario2','idUsuario3'],
					directFn: ConsultaPropuestasAction.consultarDetalle,
					//autoLoad: {params:{start: 0, limit: 10}},
					fields: [
						{name: 'nomEmpresa'},//Grupo
						{name: 'noEmpresa'},//Nom. Empresa
						{name: 'noDocto'},//No. Docto
						{name: 'nomProveedor'},//Proveedor
						{name: 'importe'},//importe
						{name: 'fecValorOriginalStr', type: 'date', dateFormat:'d/m/Y'},//fecha de vencimiento
						{name: 'idDivisa'},//Divisa
						{name: 'importeMn'},//ImporteMN
						{name: 'idFormaPago'},//ID Forma Pago
						{name: 'descFormaPago'},//Forma Pago
						{name: 'fecPropuestaStr', type: 'date', dateFormat:'d/m/Y'},//Fec Prop
						{name: 'bancoPago'},//Banco de Pago
						{name: 'idChequera'},//Chequera de Pago
						{name: 'descGrupoCupo'},//Grupo de Rubros
						{name: 'idRubro'},//Rubro
						{name: 'importeOriginal'},//Importe Original
						{name: 'idDivisaOriginal'},//DivisaOriginal
						{name: 'beneficiario'},//Beneficiario
						{name: 'noFolioDet'},//No. Folio Det
						{name: 'concepto'},//Concepto
						{name: 'idBancoBenef'},//Id Banco Benef
						{name: 'idChequeraBenef'},//Id Chequera Benef
						{name: 'noFactura'},//No. Factura
						{name: 'diasInv'},//Dias Vto
						{name: 'origenMov'},//Origen
						{name: 'observacion'},
						{name: 'noCliente'},//No. Persona
						{name: 'noCheque'},//No. Cheque
						{name: 'idBanco'},//No. Banco pagador
						{name: 'usr1'},
						{name: 'usr2'},
						{name: 'color'},
						{name: 'fecContabilizacionStr', type: 'date', dateFormat:'d/m/Y'}, //Fecha Contabilizacion
						{name: 'fecOperacionStr', type: 'date', dateFormat:'d/m/Y'}, //Fecha Documento
						{name: 'fecValorStr', type: 'date', dateFormat:'d/m/Y'}, //Fecha Vencimiento
						{name: 'equivale'},
						{name: 'idContable'},	//clase docto.
						{name: 'invoiceType'},
						{name: 'idClabe'},
						{name: 'rfc'},
						{name: 'referenciaCte'},
					],
					listeners: {
						load: function(s, records){
							//var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeDetalle, msg:"Cargando 222..."});
							
							if(records.length==null || records.length<=0){
								Ext.Msg.alert('SET','No existe detalle');
								NS.registros = NS.gridDetalle.getSelectionModel().getSelections();
							}else {
								console.log("entro al storeDetalle "+records[0].get('observacion'));
			          			var totalImporte=0;
			          			var totalImporteDLS=0;
			          			var sumaDetProp=0;
			          			
			          			var bandera = false;
			          			var regSelMaestros = NS.gridMaestroPropuestas.getSelectionModel().getSelections();
			          			
			          			if(regSelMaestros.length > 0) {
			          				if(regSelMaestros[0].get('user1') != '')
			          					bandera = true;
			          			}
			          			if(bandera) {
			          				for(var k=0; k<records.length; k++) {
			          					
			          					if(records[k].get('idDivisa') == 'DLS')
			              					totalImporteDLS += records[k].get('importe');
			              				else
			              					totalImporte += records[k].get('importe');
			              				
			              				NS.sm.selectRow(k, true, false);
			              				
			              			}
			          				
			          				sumaDetProp = 0;
			          				
			          				//var regTodos =NS.gridDetalle.store.data.items;
			              			//for(var k=0; k<regTodos.length; k++) {
			              		//		sumaDetProp += regTodos[k].get('importe');
			              		//	}
			              			
			          			}else {
			          				//NS.sm.selectRange(0,records.length-1); //lo  comento para que no seleccione nada
				          			
			          				sumaDetProp = 0;
			          				
				          			//var regSelec=NS.gridDetalle.getSelectionModel().getSelections();
				          			var regSelec=NS.gridDetalle.store.data.items; //todos los registros en lugar de seleccionarlos
				          			for(var kk=0; kk<regSelec.length; kk++) {
				          				if(regSelec[kk].get('idDivisa') == 'DLS')
				          					totalImporteDLS += regSelec[kk].get('importe');
				          				else
				          					totalImporte += regSelec[kk].get('importe');
				          			}
				          			
				          			//for(var k=0; k < regSelec.length; k++) {
				          			//	sumaDetProp += regSelec[k].get('importe');
				          			//}
				          			
			          			}
			          			Ext.getCmp(PF+'totalMN').setValue(NS.formatNumber(Math.round((totalImporte)*100)/100 ));
			          			Ext.getCmp(PF+'totalDLS').setValue(NS.formatNumber(Math.round((totalImporteDLS)*100)/100));

				        		NS.registros = NS.gridDetalle.getSelectionModel().getSelections();
				        		
				        		//Deseleccionamos todo
				        		NS.sm.selectRange(-1,-1);
							}
							
							mascara.hide();
						},
						exception: function(mist){
							Ext.Msg.alert('SET','Error en conexión al servidor');
							mascara.hide();
						}
					}
				}); 
			 //NS.gridDetalle.reconfigure(NS.storeDetalle, NS.columnasDetalle);
			 if(NS.nominaRH){
	    			NS.gridDetalle.reconfigure(NS.storeDetalle, NS.columnasDetalle);
	    			habilitarBotonesDetalle(false);
	    		}else if(NS.nominaTes && regSeleccionados[0].get('cveControl').indexOf('MAN') >= 0){
	    			NS.gridDetalle.reconfigure(NS.storeDetalle, NS.columnasDetalleNom);
	    			habilitarBotonesDetalle(false);
	    		}else{
	    			NS.gridDetalle.reconfigure(NS.storeDetalle, NS.columnasDetalle);
	    			habilitarBotonesDetalle(true);
	    		}
			NS.gridPagingBar.bindStore(NS.storeDetalle, true);
			
			NS.storeDetalle.baseParams.idGrupoEmpresa=regSeleccionados[0].get('idGrupoFlujo');
    		NS.storeDetalle.baseParams.idGrupoRubro=regSeleccionados[0].get('idGrupo');
    		NS.storeDetalle.baseParams.cveControl=regSeleccionados[0].get('cveControl');
    		NS.storeDetalle.baseParams.idUsuario1=regSeleccionados[0].get('usuarioUno');
    		NS.storeDetalle.baseParams.idUsuario2=regSeleccionados[0].get('usuarioDos');
    		NS.idDivisaCambio = regSeleccionados[0].get('divisa');
    		//NS.storeDetalle.baseParams.idUsuario3=regSeleccionados[0].get('usuarioTres');
    	//	console.log("cveControl "+NS.storeDetalle.baseParams.cveControl);
    		//No perder el registro que carga los detalles, esto para sus modificaciones (Si es que se han seleccionado mas registros en el grid maestro)
    		NS.seleccionPropuestaGlobal = regSeleccionados[0];
			
    		//NS.storeDetalle.load();
    		NS.storeDetalle.load({
			    params: {
			        // specify params for the first page load if using paging
			        start: 0,          
			        limit: 500,
			    }
			});
    		
    		//POR PAGINACIÓN ES NECESARIO REALIZAR ESTA CONSULTA PARA SACAR EL TOTAL.
			ConsultaPropuestasAction.sumaTotalDetalles(regSeleccionados[0].get('cveControl'), function(resultado, e) {
				if (resultado != '' && resultado != undefined && resultado != null){
					//Ext.Msg.alert('SET', resultado);
					Ext.getCmp(PF+'sumMN').setValue(NS.formatNumber(Math.round((resultado)*100)/100));
				}else{
					Ext.Msg.alert('SET', 'Error al obtener la suma total del detalle.');
				}
				
			});
			
    		NS.buscarNombre();
    		//Ext.getCmp(PF + 'btnModificar').setDisabled(false);
    		//Ext.getCmp(PF + 'btnModificarPag').setDisabled(false);
    		
    		//Azul pagadas y verde autorizadas
    		//if(regSeleccionados[0].get('color') == "color:#090DFA" || regSeleccionados[0].get('color') == "color:#04A861" ){
    			//|| regSeleccionados[0].get('color') == "color:#FDA920") 
    		//	Ext.getCmp(PF + 'btnModificar').setDisabled(true);
    		//	Ext.getCmp(PF + 'btnModificarPag').setDisabled(true);
    		//} 
    		if(regSeleccionados[0].get('color') == "color:#04A861"
    			|| ((NS.nominaRH || NS.nominaTes) 
    			&& regSeleccionados[0].get('cveControl').indexOf('MAN') >= 0)){ 
    			
    			Ext.getCmp(PF + 'btnModificar').setDisabled(true);
    			Ext.getCmp(PF + 'btnModificarPag').setDisabled(true);
    		}else{
    			Ext.getCmp(PF + 'btnModificar').setDisabled(false);
    			Ext.getCmp(PF + 'btnModificarPag').setDisabled(false);
    		} 	
			}else {
			NS.gridDetalle.store.removeAll();
			NS.gridDetalle.getView().refresh();
			Ext.getCmp(PF + 'txtImporteProp').setValue('0.00');
			Ext.getCmp(PF+'totalMN').setValue('0.00');
  			Ext.getCmp(PF+'sumMN').setValue('0.00');
  			Ext.getCmp(PF+'totalDLS').setValue('0.00');
		}
		
    };
    
    function verificaFacultadUsuario(){
    	
    	ConsultaPropuestasAction.verificaFacultadUsuario("VERNOMRH", function(resultado, e) {
			
			if(e.message=="Unable to connect to the server."){
				Ext.Msg.alert('SET','Error de conexión al servidor');
				return;
			}
			
			if (resultado != '' && resultado != null && resultado != undefined) {
				NS.nominaRH = true;
			}
			
		});
    	
    	
    	 ConsultaPropuestasAction.verificaFacultadUsuario("btnEliminaDetallePropuesta", function(resultado, e) {	
    		 if(e.message=="Unable to connect to the server."){			
    			 Ext.Msg.alert('SET','Error de conexión al servidor');			
    			             return;			
                    }			
                    			
                    if (resultado != '' && resultado != null && resultado != undefined) {			
                        Ext.getCmp(PF + 'btnEliminar').setDisabled(false);			
                    }else{			
                            Ext.getCmp(PF + 'btnEliminar').setDisabled(true);			
                    }			
                    			
            });
    	
    	ConsultaPropuestasAction.verificaFacultadUsuario("VERNOMTES", function(resultado, e) {
			
			if(e.message=="Unable to connect to the server."){
				Ext.Msg.alert('SET','Error de conexión al servidor');
				return;
			}
			
			if (resultado != '' && resultado != null && resultado != undefined) {
				NS.nominaTes = true;
			}
			
		});
    	
    }

	//Función para la llamada al reporte de propuestas autorizadas
	NS.imprimirReporte = function(){
		var regSelecMaestro = NS.gridMaestroPropuestas.getSelectionModel().getSelections();
		console.log("regSeleccionados "+regSelecMaestro.length);
	var posicion=0;
	//	for(var posicion=0; posicion<regSelecMaestro.length-1; posicion++){
	//		console.log("contador i inicial"+posicion);
	//		console.log("cveControl "+regSelecMaestro[posicion].get('cveControl'));
				ConsultaPropuestasAction.consultarDetalle(regSelecMaestro[posicion].get('idGrupoFlujo'),
				regSelecMaestro[posicion].get('idGrupo'),regSelecMaestro[posicion].get('cveControl'),
				regSelecMaestro[posicion].get('usuarioUno'),regSelecMaestro[posicion].get('usuarioDos'),0,function(resultado,e){
					//for(var j=0;j<resultado.length;resultado++){
					console.log("entro a consultarDetalle "+posicion);
					console.log("regSElecDetalle "+resultado.length);
					//		if(regSelecDetalle.length > 0) {
					console.log("empresa "+resultado[0].noEmpresa);
			
					if(resultado[0].noEmpresa>=2000 && resultado[0].noEmpresa<=2099){
						strParams = '?nomReporte=ReporteDetallePagoPropuestas';
						strParams += '&'+'nomParam1=idGrupoEmpresa';
						strParams += '&'+'valParam1=' + regSelecMaestro[posicion].get('idGrupoFlujo');
						strParams += '&'+'nomParam2=idGrupoRubro';
						strParams += '&'+'valParam2=' + regSelecMaestro[posicion].get('idGrupo');
						strParams += '&'+'nomParam3=cveControl';
						strParams += '&'+'valParam3=' + regSelecMaestro[posicion].get('cveControl');
						strParams += '&'+'nomParam4=idUsuario1';
						strParams += '&'+'valParam4=' + regSelecMaestro[posicion].get('usuarioUno');
						strParams += '&'+'nomParam5=idUsuario2';
						strParams += '&'+'valParam5=' + regSelecMaestro[posicion].get('usuarioDos');
						strParams += '&'+'nomParam6=idUsuario3';
						strParams += '&'+'valParam6=' + 0;
						strParams += '&'+'nomParam7=total';
						strParams += '&'+'valParam7=' + Ext.getCmp(PF+'totalMN').getValue();
						window.open("/SET/jsp/bfrmwrk/gnrators/genJasperPDF.jsp" + strParams);
	
					}
					return;
					//}	
				});
		//}

		
		
	};
    
    function habilitarBotonesDetalle(b){
		Ext.getCmp('btnMultiSociedad').setDisabled(!b);
		Ext.getCmp(PF + 'btnCheqNoagrupa').setDisabled(!b);
		Ext.getCmp(PF + 'btnEliminar').setDisabled(!b);
		Ext.getCmp(PF + 'btnModificarPag').setDisabled(!b);
		Ext.getCmp(PF + 'btnModificar').setDisabled(!b);
		Ext.getCmp(PF + 'btnImprimir').setDisabled(!b);
		
	}
    
	NS.ConsultaPropuestas.setSize(Ext.get(NS.tabContId).getWidth(), Ext.get(NS.tabContId).getHeight());
	
	//var obj = $("div [id*='contMovimientos'] div div div div div div")[1];
//staticCheck("div [id*='contMovimientos']",obj,2,".x-grid3-scroller",false);
});