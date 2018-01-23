/*
 * @author: Victor H. Tello
 * @since: 13/Feb/2012
 * 
 * ggonzalez 22/08/2017
 */
Ext.onReady(function() {
	var NS = Ext.namespace('apps.SET.Egresos.ConfirmacionCargoCta');
	NS.tabContId = apps.SET.tabContainerId;
	NS.idUsuario = apps.SET.iUserId;
	var PF = apps.SET.tabID + '.';
	Ext.Direct.addProvider(Ext.setapp.REMOTING_API);
	Ext.QuickTips.init();
	NS.fecHoy = BFwrk.Util.changeStringToDate(apps.SET.FEC_HOY);

	NS.noEmpresa = 0;
	NS.idBanco = 0;
	NS.idChequera = '';
	NS.noFolioDet = 0;
	NS.importePagos = 0;
	NS.totImportePagos = 0;
	NS.secuencia = '';
	NS.fecIni = '';
	NS.pbEjecutoPago = false;
	NS.diferenciaT=0;
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
	}
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
	NS.unformatNumber = function(num) {
		return num.replace(/(,)/g,''); 
	};
	NS.fechaInicial = function(fechaIni) {
		var fec = BFwrk.Util.sumDate(fechaIni, 'dia', -10);
		return BFwrk.Util.changeStringToDate(fec);
	};
	NS.fecIni = NS.fecHoy; //NS.fechaInicial(NS.fecHoy); 

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
		NS.cmbChequeras.reset();
		NS.storeBancos.removeAll();
		NS.storeChequeras.removeAll();

		if((valueCombo != null && valueCombo != undefined && valueCombo != '') || valueCombo == 0) {
			if(valueCombo == 0)
				Ext.getCmp(PF + 'txtEmpresa').setValue(valueCombo);

			NS.storeBancos.baseParams.noEmpresa = valueCombo;
			NS.storeBancos.load();
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
					if(caja.getValue() != null && caja.getValue() != undefined && caja.getValue() != '') {
						var valueCombo = BFwrk.Util.updateTextFieldToCombo(PF + 'txtBanco', NS.cmbBanco.getId());
						NS.cambiaChequera(valueCombo);
					}else
						NS.cambiaChequera('');
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
					if(combo.getValue() != null && combo.getValue() != '' && combo.getValue() != undefined)
						NS.cambiaChequera(combo.getValue());
				}
			}
		}
	});
	//Refresca el combo de chequeras segun el banco y la empresa seleccionados
	NS.cambiaChequera = function(valueCombo) {
		NS.cmbChequeras.reset();
		NS.storeChequeras.removeAll();

		if(valueCombo != null && valueCombo != undefined && valueCombo != '') {
			NS.storeChequeras.baseParams.idBanco = valueCombo;
			NS.storeChequeras.baseParams.noEmpresa = NS.noEmpresa;
			NS.storeChequeras.load();
			NS.idBanco = valueCombo;
		}else {
			Ext.getCmp(PF + 'txtBanco').reset();
			NS.cmbBanco.reset();
		}
	};
	//Etiqueta chequera
	NS.labChequera = new Ext.form.Label({
		text: 'Chequera:',
		x: 265,
		y: 50
	});
	//storeChequeras en base al banco seleccionado
	NS.storeChequeras = new Ext.data.DirectStore({
		paramsAsHash: false,
		root: '',
		baseParams:{idBanco: 0, noEmpresa: 0, idDivisa: ''},
		paramOrder: ['idBanco', 'noEmpresa', 'idDivisa'],
		directFn: ConfirmacionCargoCtaAction.llenaComboChequera,
		idProperty: 'id',
		fields: [
		         {name: 'id'},
		         {name: 'id'}
		         ],
		         listeners: {
		        	 load: function(s, records) {
		        		 var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeChequeras, msg:"Cargando..."});
		        		 if(records.length == null || records.length <= 0)
		        			 Ext.Msg.alert('SET', 'No existen chequeras para este banco');
		        	 }
		         }
	});
	//Combo Chequeras en base al banco
	NS.cmbChequeras = new Ext.form.ComboBox({
		store: NS.storeChequeras,
		id: PF + 'cmbChequeras',
		name: PF + 'cmbChequeras',
		x: 265,
		y: 65,
		width: 170,
		tabIndex: 4,
		typeAhead: true,
		selecOnFocus: true,
		mode: 'local',
		forceSelection: true,
		valueField: 'id',
		displayField: 'id',
		emptyText: 'Seleccione una chequera',
		triggerAction: 'all',
		value: '',
		listeners: {
			select: {
				fn:function(combo, valor){
					NS.idChequera = combo.getValue();
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
		value : NS.fecIni
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
		value: NS.fecHoy
	});
	//Contenedor de Busqueda
	NS.contBusqueda = new Ext.form.FieldSet({
		title: 'búsqueda',
		width: 985,
		height: 125,
		layout: 'absolute',
		items:[
		       NS.labEmpresa,
		       NS.txtEmpresa,
		       NS.cmbEmpresas,
		       NS.labBanco,
		       NS.txtBanco,
		       NS.cmbBanco,
		       NS.labChequera,
		       NS.cmbChequeras,
		       NS.labFechaIni,
		       NS.txtFechaIni,
		       NS.labFechaFin,
		       NS.txtFechaFin,
		       {
		    	   xtype: 'checkbox',
		    	   id: PF + 'chkRechazosNomina',
		    	   name: PF + 'chkRechazosNomina',
		    	   x: 500,
		    	   y: 60,
		    	   boxLabel: 'Mostrar Rechazos de Nominas'
		       },{
		    	   xtype: 'button',
		    	   text: 'Buscar',
		    	   x: 870,
		    	   y: 15,
		    	   width: 80,
		    	   height: 22,
		    	   listeners: {
		    		   click:{
		    			   fn:function(e){
		    				   NS.buscar();
		    			   }
		    		   }
		    	   }
		       }
		       ]
	});






	//Store Pagos donde se muestran los valores seleccionados
	NS.storePagos = new Ext.data.DirectStore({
		paramsAsHash: false,
		baseParams: {},
		root: '',
		paramOrder: ['fechaIni', 'fechaFin', 'noEmpresa', 'idBanco', 'idChequera'],
		directFn:  ConfirmacionCargoCtaAction.llenarMovtosPagos, 
		fields: [
		         {name: 'concepto'},
		         {name: 'importe'},
		         {name: 'noFolioDet'},
		         {name: 'idDivisa'},
		         {name: 'beneficiario'},
		         {name: 'noDocto'},
		         {name: 'noCliente'},
		         {name: 'noFactura'},
		         {name: 'fecValorOriginal'},
		         {name: 'noEmpresa'},
		         {name: 'idBanco'},
		         {name: 'idChequera'},
		         {name: 'origenMov'},
		         {name: 'cveControl'},

		         ],
		         listeners: {
		        	 load: function(s, records) {
		        		 var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storePagos, msg: "Buscando..."});

		        		 if(records.length == null || records.length <= 0)
		        			 Ext.Msg.alert('SET', 'No hay registros pendientes de confirmar!!');
		        	 }
		         }
	}); 
	//Para indicar el modo de seleccion de los registros en el Grid
	NS.seleccionPagos = new Ext.grid.CheckboxSelectionModel({
		singleSelect: false
	});
	//Columna de Seleccion  
	NS.columnasPagos = new Ext.grid.ColumnModel([
	                                             NS.seleccionPagos,
	                                             {header: 'Concepto', width: 80, dataIndex: 'concepto', sortable: true},
	                                             {header: 'Importe', width: 90, dataIndex: 'importe', sortable: true, css: 'text-align:right;', renderer: BFwrk.Util.rendererMoney},
	                                             {header: 'Folio', width: 70, dataIndex: 'noFolioDet', sortable: true},
	                                             {header: 'Divisa', width: 50, dataIndex: 'idDivisa', sortable: true},
	                                             {header: 'Beneficiario', width: 100, dataIndex: 'beneficiario', sortable: true},
	                                             {header: 'No. Docto', width: 70, dataIndex: 'noDocto', sortable: true},
	                                             {header: 'No. Cliente', width: 80, dataIndex: 'noCliente', sortable: true},
	                                             {header: 'No. Factura', width: 80, dataIndex: 'noFactura', sortable: true},
	                                             {header: 'Fec. Original', width: 90, dataIndex: 'fecValorOriginal', sortable: true},
	                                             {header: 'No. Empresa', width: 60, dataIndex: 'noEmpresa', sortable: true},
	                                             {header: 'Id. Banco', width: 60, dataIndex: 'idBanco', sortable: true},
	                                             {header: 'Chequera', width: 60, dataIndex: 'idChequera', sortable: true},
	                                             {header: '', width: 0, dataIndex: 'origenMov', sortable: true},
	                                             {header: 'Cve Control', width: 0, dataIndex: 'cveControl', sortable: true,hide:true}

	                                             ]);
	//Grid para mostrar los datos seleccionados
	NS.gridPagos = new Ext.grid.GridPanel({
		store: NS.storePagos,
		id: PF+ 'gridPagos',
		name: PF+ 'gridPagos',
		x: 5,
		cm: NS.columnasPagos,
		sm: NS.seleccionPagos,
		width: 480,
		height: 225,
		stripeRows: true,
		columnLines: true,
		title: 'Pagos',
		listeners: {
			click: {
				fn:function(grid) {
					var montoPag = 0;
					var montoCar = 0;
					var regSelec = NS.gridPagos.getSelectionModel().getSelections();

					Ext.getCmp(PF + 'txtRegistrosPag').setValue(regSelec.length);
					montoCar = NS.unformatNumber(Ext.getCmp(PF + 'txtMontoCar').getValue());

					for(var i=0; i<regSelec.length; i++) {
						montoPag = montoPag + regSelec[i].get('importe');
					}
					var importePag = (Math.round((montoPag)*100)/100);
					Ext.getCmp(PF + 'txtMontoPag').setValue(NS.formatNumber(importePag));
					var diferencia = (Math.round((montoPag - montoCar)*100)/100);
					Ext.getCmp(PF + 'txtDiferencia').setValue(NS.formatNumber(diferencia));
				}
			}
		}
	});

	//Store cargos donde se muestran los valores seleccionados
	NS.storeCargos = new Ext.data.DirectStore({
		paramsAsHash: false,
		baseParams: {},
		root: '',
		paramOrder: ['fechaIni', 'fechaFin', 'noEmpresa', 'idBanco', 'idChequera', 'importe', 'secuencia', 'todos', 'rechNom'],
		directFn: ConfirmacionCargoCtaAction.llenarMovtosCargos,
		//idProperty: 'noFolioDet', //identificador del store
		fields: [
		         {name: 'descripcion'},
		         {name: 'importe'},
		         {name: 'noFolioDet'},
		         {name: 'secuencia'},
		         {name: 'fecValor'},
		         {name: 'concepto'},
		         {name: 'id_cve_concepto'},
		         {name: 'noEmpresa'},
		         {name: 'idBanco'},
		         {name: 'idChequera'}
		       
		         ],
		         listeners: {
		        	 load: function(s, records){
		        		 var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeCargos, msg:"Buscando..."});

		        		 if(records.length > 0) {
		        			 NS.buscaCargos();
		        		 }
		        	 }
		         }
	});
	//Para indicar el modo de seleccion de los registros en el Grid
	NS.seleccionCargos = new Ext.grid.CheckboxSelectionModel({
		singleSelect: false
	});
	//Columna de Seleccion  
	NS.columnasCargos = new Ext.grid.ColumnModel([
	                                              NS.seleccionCargos,
	                                              {header: 'Descripcion', width: 100, dataIndex: 'descripcion', sortable: true},
	                                              {header: 'Importe', width: 90, dataIndex: 'importe', sortable: true, css: 'text-align:right;', renderer: BFwrk.Util.rendererMoney},
	                                              {header: 'Folio', width: 70, dataIndex: 'noFolioDet', sortable: true},
	                                              {header: 'Secuencia', width: 70, dataIndex: 'secuencia', sortable: true},
	                                              {header: 'Fec Original', width: 90, dataIndex: 'fecValor', sortable: true},
	                                              {header: 'Concepto', width: 100, dataIndex: 'concepto', sortable: true},
	                                              {header: 'Cve.concepto', width: 100, dataIndex: 'id_cve_concepto', sortable: true},
	                                              {header: 'No. Empresa', width: 60, dataIndex: 'noEmpresa', sortable: true},
	                                              {header: 'Id. Banco', width: 60, dataIndex: 'idBanco', sortable: true},
	                                              {header: 'Chequera', width: 60, dataIndex: 'idChequera', sortable: true}
	                                              ]);
	//Grid para mostrar los datos seleccionados
	NS.gridCargos = new Ext.grid.GridPanel({
		store: NS.storeCargos,
		id: 'gridCargos',
		x: 500,
		cm: NS.columnasCargos,
		sm: NS.seleccionCargos,
		width: 460,
		height: 225,
		stripeRows: true,
		columnLines: true,
		title: 'Cargos en Cuenta',
		listeners: {
			click: {
				fn:function(s, records) {
					var montoPag = 0;
					var montoCar = 0;
					var regSelec = NS.gridCargos.getSelectionModel().getSelections();

					Ext.getCmp(PF + 'txtRegistrosCar').setValue(regSelec.length);
					montoPag = NS.unformatNumber(Ext.getCmp(PF + 'txtMontoPag').getValue());

					for(var i=0; i<regSelec.length; i++) {
						montoCar = montoCar + regSelec[i].get('importe');
					}
					var importeCar = (Math.round((montoCar)*100)/100);
					Ext.getCmp(PF + 'txtMontoCar').setValue(NS.formatNumber(importeCar));
					var diferencia = (Math.round((montoPag - montoCar)*100)/100);
					Ext.getCmp(PF + 'txtDiferencia').setValue(NS.formatNumber(diferencia));
				}
			}
		}
	});


	//Etiqueta Registros Pagos
	NS.labRegistrosPag = new Ext.form.Label({
		text: 'Registros:',
		x: 10,
		y: 230
	});
	//Caja Registros Pagos
	NS.txtRegistrosPag = new Ext.form.TextField({
		id: PF + 'txtRegistrosPag',
		name:PF + 'txtRegistrosPag',
		x: 10,
		y: 245,
		width: 100,
		tabIndex: 0
	});
	//Etiqueta Monto Pagos
	NS.labMontoPag = new Ext.form.Label({
		text: 'Monto:',
		x: 150,
		y: 230
	});
	//Caja Monto Pagos
	NS.txtMontoPag = new Ext.form.TextField({
		id: PF + 'txtMontoPag',
		name:PF + 'txtMontoPag',
		x: 150,
		y: 245,
		width: 100,
		tabIndex: 0
	});
	//Etiqueta Registros Cargos
	NS.labRegistrosCar = new Ext.form.Label({
		text: 'Registros:',
		x: 500,
		y: 230
	});
	//Caja Registros Cargos
	NS.txtRegistrosCar = new Ext.form.TextField({
		id: PF + 'txtRegistrosCar',
		name:PF + 'txtRegistrosCar',
		x: 500,
		y: 245,
		width: 100,
		tabIndex: 0
	});
	//Etiqueta Monto Cargos
	NS.labMontoCar = new Ext.form.Label({
		text: 'Monto:',
		x: 640,
		y: 230
	});
	//Caja Monto Cargos
	NS.txtMontoCar = new Ext.form.TextField({
		id: PF + 'txtMontoCar',
		name:PF + 'txtMontoCar',
		x: 640,
		y: 245,
		width: 100,
		tabIndex: 0
	});
	//Contenedor Grids
	NS.contGrids = new Ext.form.FieldSet({
		y: 135,
		width: 985,
		height: 290,
		layout: 'absolute',
		items: [
		        NS.gridPagos,
		        NS.gridCargos,
		        NS.labRegistrosPag,
		        NS.txtRegistrosPag,
		        NS.labMontoPag,
		        NS.txtMontoPag,
		        NS.labRegistrosCar,
		        NS.txtRegistrosCar,
		        NS.labMontoCar,
		        NS.txtMontoCar,
		        {
			    	   xtype: 'checkbox',
			    	   id: PF + 'chkFinanExternos',
			    	   name: PF + 'chkFinanExternos',
			    	   x: 300,
			    	   y: 240,
			    	   boxLabel: 'Financiamientos Externos'
			       }
		        ]
	});
	//Etiqueta Diferencia
	NS.labDiferencia = new Ext.form.Label({
		text: 'Diferencia:',
		x: 20,
		y: 430
	});
	//Caja Diferencia
	NS.txtDiferencia = new Ext.form.TextField({
		id: PF + 'txtDiferencia',
		name:PF + 'txtDiferencia',
		x: 20,
		y: 445,
		width: 100,
		tabIndex: 0
	});
	//Etiqueta Permisible
	NS.labPermisible = new Ext.form.Label({
		text: 'Permisible:',
		x: 160,
		y: 430
	});
	//Caja Permisible
	NS.txtPermisible = new Ext.form.TextField({
		id: PF + 'txtPermisible',
		name:PF + 'txtPermisible',
		x: 160,
		y: 445,
		width: 100,
		tabIndex: 0
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
		       NS.contGrids,
		       NS.labDiferencia,
		       NS.txtDiferencia,
		       NS.labPermisible,
		       NS.txtPermisible,
		       {
		    	   xtype: 'button',
		    	   text: 'Parcializar',
		    	   x: 700,
		    	   y: 445,
		    	   width: 80,
		    	   height: 22,
		    	   listeners: {
		    		   click: {
		    			   fn: function(e) {
		    				   if(Ext.getCmp(PF+'chkFinanExternos').getValue()==true){
		    					   
		    					   var selPagos = NS.gridPagos.getSelectionModel().getSelections();
		    					   var selCargos = NS.gridCargos.getSelectionModel().getSelections();
		    					   if(selPagos.length<1){
		    						   Ext.Msg.alert("SET","Debe seleccionar Movimientos de Pagos");
		    					   }else{
		    						   if(selCargos.length<1){
			    						   Ext.Msg.alert("SET","Debe seleccionar un Movimiento de Cargos");
		    						   }else{
			    						   var cveControlAnt='';
			    						   var cveControl='';
			    						   for(var i=0;i<selPagos.length;i++){ 
				    						  if(i!=0){
				    							  if(selPagos[i].data.cveControl==cveControlAnt){
				    								  cveControlAnt=selPagos[i].data.cveControl;
				    								  
				    							  }else{
				    								  Ext.Msg.alert("SET","Las claves de control de los pagos deben ser las mismas");
				    								  return;
				    							  }
				    						  }else{
				    							  cveControlAnt=selPagos[i].data.cveControl;
				    						  }
				    					   }
			    						
			    						   var folioDet=selPagos[0].data.noFolioDet;
			    						   var secuencia=selCargos[0].data.secuencia;
			    						  
			    						   console.log("montoPago "+NS.txtMontoPag.getValue());
//										   console.log("montoCargo "+NS.txtMontoCar.getValue());
//										   console.log("folio "+folioDet);
//										   console.log("secuencia "+selCargos[0].data.secuencia);
//										   console.log("cve control "+cveControlAnt);
										   if(parseFloat(BFwrk.Util.unformatNumber(NS.txtMontoPag.getValue()))==parseFloat(BFwrk.Util.unformatNumber(NS.txtMontoCar.getValue()))){
											   return;
										   }else{
											   if(parseFloat(BFwrk.Util.unformatNumber(NS.txtMontoPag.getValue()))>parseFloat(BFwrk.Util.unformatNumber(NS.txtMontoCar.getValue()))){
												   Ext.Msg.alert("SET","La suma de los capitales debe ser menor al monto de cargo en cuenta");
											   }else{
												   
												   var interes=parseFloat(BFwrk.Util.unformatNumber(NS.txtMontoCar.getValue()))-parseFloat(BFwrk.Util.unformatNumber(NS.txtMontoPag.getValue()));
												   console.log("interes nuevoo "+interes.toFixed(2));
												   ConfirmacionCargoCtaAction.crearInteres(parseInt(folioDet),parseFloat(interes),parseInt(secuencia),cveControlAnt,function(mapResult,e){
													   if(mapResult.msgUsuario !== null
																&& mapResult.msgUsuario !== ''
																	&& mapResult.msgUsuario != undefined){
														   Ext.Msg.alert("Información SET",mapResult.msgUsuario);
														   NS.buscar();
													   }else{
														   Ext.Msg.alert("Información SET","No se pudo crear el Pago de Interes");
													   }										   
												   });
											   } 
										   }
										 
									
				    						   
			    					   }
		    					   }
		    				
			    				   }else{
			    					   NS.parcializa();
			    				   }
//		    					var selCargos = NS.gridCargos.getSelectionModel().getSelections();
//		    					var selPagos = NS.gridPagos.getSelectionModel().getSelections();
//		    					NS.diferenciaT=(Math.round((selPagos[0].get('importe')-selCargos[0].get('importe'))*100)/100);
//		    					console.log("dif "+NS.diferenciaT);
//								if(NS.diferenciaT<0){
//								NS.diferenciaT=BFwrk.Util.formatNumber(NS.diferenciaT*-1);
//							//	console.log("diferencia -"+NS.diferenciaT);
//								}else{
//									NS.diferenciaT=BFwrk.Util.formatNumber(NS.diferenciaT);
//									//console.log("diferencia "+NS.diferenciaT);
//								}
			    				   
			    			   }
		    		   }
		    	   }
		       },{
		    	   xtype: 'button',
		    	   text: 'Confirmar',
		    	   x: 790,
		    	   y: 445,
		    	   width: 80,
		    	   height: 22,
		    	   listeners: {
		    		   click: {
		    			   fn: function(e) {
		    				   NS.confirmar();
		    			   }
		    		   }
		    	   }
		       },{
		    	   xtype: 'button',
		    	   text: 'Limpiar',
		    	   x: 880,
		    	   y: 445,
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
	//Funcion para buscar los registros
	NS.buscar = function() {
		NS.pbEjecutoPago=false;
		if(NS.txtEmpresa.getValue() != '' && NS.cmbEmpresas != ''){
			Ext.getCmp(PF + 'chkFinanExternos').setValue(false);
			Ext.getCmp(PF + 'txtPermisible').setValue('0.00'); 
			NS.storePagos.removeAll();
			NS.gridPagos.getView().refresh();
			NS.storeCargos.removeAll();
			NS.gridCargos.getView().refresh();
			NS.storePagos.baseParams.fechaIni = cambiarFecha('' + Ext.getCmp(PF + 'txtFechaIni').getValue()); 
			NS.storePagos.baseParams.fechaFin = cambiarFecha('' + Ext.getCmp(PF + 'txtFechaFin').getValue()); 
			NS.storePagos.baseParams.noEmpresa = parseInt(Ext.getCmp(PF + 'txtEmpresa').getValue()); 
			if(Ext.getCmp(PF + 'txtBanco').getValue()==''){
				NS.storePagos.baseParams.idBanco = 0;
			}else{
				NS.storePagos.baseParams.idBanco = parseInt(Ext.getCmp(PF + 'txtBanco').getValue());
			}
			NS.storePagos.baseParams.idChequera = Ext.getCmp(PF + 'cmbChequeras').getValue();
			var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storePagos, msg:"Cargando..."});
			NS.storePagos.load();

			NS.storeCargos.baseParams.fechaIni = cambiarFecha('' + Ext.getCmp(PF + 'txtFechaIni').getValue()); 
			NS.storeCargos.baseParams.fechaFin = cambiarFecha('' + Ext.getCmp(PF + 'txtFechaFin').getValue()); 
			NS.storeCargos.baseParams.noEmpresa = parseInt(Ext.getCmp(PF + 'txtEmpresa').getValue());
			if(Ext.getCmp(PF + 'txtBanco').getValue()==''){
				NS.storeCargos.baseParams.idBanco = 0;
			}else{
				NS.storeCargos.baseParams.idBanco = parseInt(Ext.getCmp(PF + 'txtBanco').getValue());
			}
			NS.storeCargos.baseParams.idChequera = Ext.getCmp(PF + 'cmbChequeras').getValue();
			NS.storeCargos.baseParams.importe = 0;
			NS.storeCargos.baseParams.secuencia = '';
			NS.storeCargos.baseParams.todos = true;
			NS.storeCargos.baseParams.rechNom = Ext.getCmp(PF + 'chkRechazosNomina').getValue();
			var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeCargos, msg:"Cargando..."});
			NS.storeCargos.load();

		}else{
			Ext.Msg.alert('SET', 'Ingrese una empresa válida');
		}

	};

	NS.buscaCargos = function() {
		//NS.importePagos = 0;
		//NS.totImportePagos = 0;
		var importeCargos = 0;
		var recordsGridPagos = NS.storePagos.data.items;
		var recordsGridCargos = NS.storeCargos.data.items;
		var gridCargos = NS.gridCargos.getStore().recordType;
		var matrizPagos = new Array();
		var matrizCargos = new Array();
		if(recordsGridPagos.length > 0 && recordsGridCargos.length > 0 ) {
			for(var i=0; i<recordsGridPagos.length; i++) {
				for(var x=0; x<recordsGridCargos.length; x++) {
					if(recordsGridPagos[i].get('noEmpresa') == recordsGridCargos[x].get('noEmpresa') &&
							recordsGridPagos[i].get('idBanco') == recordsGridCargos[x].get('idBanco') &&
							recordsGridPagos[i].get('idChequera') == recordsGridCargos[x].get('idChequera') &&
							recordsGridPagos[i].get('importe') == recordsGridCargos[x].get('importe')) {

						var datos = new gridCargos({
							descripcion: recordsGridCargos[x].get('descripcion'),
							importe: recordsGridCargos[x].get('importe'),
							noFolioDet: recordsGridPagos[i].get('noFolioDet'),
							secuencia: recordsGridCargos[x].get('secuencia'),
							fecValor: recordsGridCargos[x].get('fecValor'),
							concepto: recordsGridCargos[x].get('concepto'),
							noEmpresa: recordsGridCargos[x].get('noEmpresa'),
							idBanco: recordsGridCargos[x].get('idBanco'),
							idChequera: recordsGridCargos[x].get('idChequera')
						});
						NS.storeCargos.remove(recordsGridCargos[x]);
						NS.gridCargos.stopEditing();
						NS.storeCargos.insert(x, datos);
						matrizPagos.push(i);
						matrizCargos.push(x);
						NS.gridCargos.getView().refresh();
						//break;
					}
				}
			}
		}
		NS.seleccionPagos.selectRows(matrizPagos);
		NS.seleccionCargos.selectRows(matrizCargos);
		var selCargos = NS.gridCargos.getSelectionModel().getSelections();
		var selPagos = NS.gridPagos.getSelectionModel().getSelections();
		var montoPag=0;
		var montoCar=0;
		for(var j=0; j<selPagos.length; j++) {
			montoPag = montoPag + selPagos[j].get('importe');
		}
		for(var a=0; a<selCargos.length; a++) {
			montoCar = montoCar + selCargos[a].get('importe');
		}
		var importePag = (Math.round((montoPag)*100)/100);
		var importeCar = (Math.round((montoCar)*100)/100);
		Ext.getCmp(PF + 'txtRegistrosPag').setValue(selPagos.length);
		Ext.getCmp(PF + 'txtMontoPag').setValue(NS.formatNumber(importePag));
		Ext.getCmp(PF + 'txtRegistrosCar').setValue(selCargos.length);
		Ext.getCmp(PF + 'txtMontoCar').setValue(NS.formatNumber(importeCar));
		var diferencia = (Math.round((importePag - importeCar)*100)/100);
		Ext.getCmp(PF + 'txtDiferencia').setValue(NS.formatNumber(diferencia));
	};
	//Función aceptar pagoParcial
	NS.aceptarPagoParcial=function(esCredito){
		var registros = NS.gridPagos.getSelectionModel().getSelections();
		var nuevoInteres=0,nuevoIva=0,importePago=0,saldoPendiente=0;
//		if(!esCredito){
//			console.log("es diferenet de credito");
//			folioDet=parseInt(BFwrk.Util.unformatNumber(Ext.getCmp(PF + 'txtfolioDet').getValue()));
//			importePago=parseFloat(BFwrk.Util.unformatNumber(Ext.getCmp(PF + 'txtImportePago').getValue()));
//			saldoPendiente=parseFloat(BFwrk.Util.unformatNumber(Ext.getCmp(PF + 'txtSaldoPendiente').getValue()));
//			if(Ext.getCmp(PF + 'txtImportePago').getValue() == ""||parseFloat(BFwrk.Util.unformatNumber(Ext.getCmp(PF + 'txtImportePago').getValue())==0)){
//				Ext.Msg.alert("SET","Debe capturar un importe.");
//				Ext.getCmp(PF + 'txtImportePago').focus();
//				return;
//			}else if(parseFloat(BFwrk.Util.unformatNumber(Ext.getCmp(PF + 'txtImporte').getValue()))<=parseFloat(BFwrk.Util.unformatNumber(Ext.getCmp(PF + 'txtImportePago').getValue()))&&registros[0].data.origenMov!='CRD'){
//				Ext.Msg.alert("SET","El importe a pagar es mayor al de la solicitud.");
//				Ext.getCmp(PF + 'txtImporte').focus();
//				Ext.getCmp(PF + 'txtImportePago').setValue("0.00");
//				return;
//			}else if(parseFloat(BFwrk.Util.unformatNumber(Ext.getCmp(PF + 'txtImportePago').getValue()))<=0){
//				Ext.Msg.alert("SET","No hay importe a pagar.");
//				Ext.getCmp(PF + 'txtImporte').focus();
//				return;
//			}
//		}else{
			var registros = NS.gridPagos.getSelectionModel().getSelections();
			folioDet=parseInt(BFwrk.Util.unformatNumber(Ext.getCmp(PF + 'txtfolioDetCrd').getValue()));
			console.log("folio det "+folioDet);
			if(registros.length>1){
				importePago=importePago;
				console.log("mas de un registro");
			}else{
				importePago=parseFloat(BFwrk.Util.unformatNumber(Ext.getCmp(PF + 'txtTotalPagoCrd').getValue()));
			}
			nuevoInteres=parseFloat(BFwrk.Util.unformatNumber(NS.txtInteresPagoCrd.getValue()));
			nuevoIva=parseFloat(BFwrk.Util.unformatNumber(NS.txtIvaPagoCrd.getValue()));
			console.log("importePago "+importePago);
			console.log("interes nuevo "+nuevoInteres);
//			if(parseFloat(BFwrk.Util.unformatNumber(NS.txtTotalPagoCrd.getValue()))==parseFloat(BFwrk.Util.unformatNumber(NS.txtCapitalCrd.getValue()))){
//				Ext.Msg.alert("SET","Ingrese un importe para iva o interes");
//				return;
//			}
			if(NS.txtInteresPagoCrd.getValue()==""){
				Ext.Msg.alert("SET","Ingrese el interes");
				NS.txtIvaPagoCrd.setValue("0.00");
				return;
			}else if(NS.txtIvaPagoCrd.getValue()==""){
				Ext.Msg.alert("SET","Ingrese el IVA");
				NS.txtIvaPagoCrd.setValue("0.00");
				return;
			}
	//	}
		BFwrk.Util.msgWait('Ejecutando pago parcial...', true);
		
		ConfirmacionCargoCtaAction.pagoParcial(importePago,folioDet,
				0,0,saldoPendiente,registros[0].data.origenMov,
				nuevoInteres,nuevoIva,
				function(mapResult,e) {
			BFwrk.Util.msgWait('Terminado...',false);
			if (mapResult.msgUsuario !== null
					&& mapResult.msgUsuario !== ''
						&& mapResult.msgUsuario != undefined) {
				for (var msg = 0; msg < mapResult.msgUsuario.length; msg++) {
					Ext.Msg.alert('Información SET',''+ mapResult.msgUsuario[msg],function(btn, text){
						if (btn == 'ok'){
							if(msg==mapResult.msgUsuario.length){
								if(mapResult.result==1){
									NS.pbEjecutoPago=true;
//									if(!esCredito)
								//		NS.winPagoParcial.hide();
									//else
									NS.winPagoParcialCrd.hide();
									NS.buscar();
									
									
								}
							}
						}
					});
				}
			}
		});
	}
	//Componentees winPagoParcial
	NS.lblFactura = new Ext.form.Label({
		text: 'Factura:',
		x: 0,
		y: 0
	});
	NS.lblFactura = new Ext.form.Label({
		text: 'Factura:',
		x: 5,
		y: 10
	});
	NS.txtFactura = new Ext.form.TextField({
		id : PF + 'txtFactura',
		name : PF + 'txtFactura',
		width : 180,
		disabled:true,
		x: 100,
		y: 5
	});
	NS.lbImpOriginal = new Ext.form.Label({
		text: 'Importe Original:',
		x: 5,
		y: 40
	});
	NS.txtImpOriginal = new Ext.form.TextField({
		id : PF + 'txtImpOriginal',
		name : PF + 'txtImpOriginal',
		width : 180,
		x: 100,
		y: 40,
		disabled:true,
		listeners:{
			change:{
				fn:function(){
					NS.txtImpOriginal.setValue(BFwrk.Util.formatNumber(NS.txtImpOriginal.getValue()));
				}
			}
		}
	});
	NS.lblImporte = new Ext.form.Label({
		text: 'Importe:',
		x: 5,
		y: 75
	});
	NS.txtImporte = new Ext.form.TextField({
		id : PF + 'txtImporte',
		name : PF + 'txtImporte',
		width : 180,
		x: 100,
		disabled:true,
		y: 75,
		listeners:{
			change:{
				fn:function(){
					NS.txtImporte.setValue(BFwrk.Util.formatNumber(NS.txtImporte.getValue()));
				}
			}
		}
	});
	NS.lblImportePago = new Ext.form.Label({
		text: 'Pago Parcial:',
		x: 5,
		y: 110
	});
	NS.txtImportePago = new Ext.form.TextField({
		id : PF + 'txtImportePago',
		name : PF + 'txtImportePago',
		width : 180,
		x: 100,
		y: 110,
		listeners:{
			change:{
				fn:function(){
					NS.txtImportePago.setValue(BFwrk.Util.formatNumber(NS.txtImportePago.getValue()));
					NS.txtSaldoPendiente.setValue(BFwrk.Util.formatNumber((parseFloat(BFwrk.Util.unformatNumber(NS.txtImporte.getValue()))-parseFloat(BFwrk.Util.unformatNumber(NS.txtImportePago.getValue()))).toFixed(2)));

				}
			}
		}
	});
	NS.lblSaldoPendiente = new Ext.form.Label({
		text: 'Saldo Restante:',
		x: 5,
		y: 145
	});
	NS.txtSaldoPendiente = new Ext.form.TextField({
		id : PF + 'txtSaldoPendiente',
		name : PF + 'txtSaldoPendiente',
		width : 180,
		x: 100,
		y: 145,
		disabled:true,
		listeners:{
			change:{
				fn:function(){
					NS.txtSaldoPendiente.setValue(BFwrk.Util.formatNumber(NS.txtSaldoPendiente.getValue()));
				}
			}
		}
	});
	NS.txtfolioDet = new Ext.form.TextField({
		id : PF + 'txtfolioDet',
		name : PF + 'txtfolioDet',
		width : 180,
		x: 100,
		y: 145,
		hidden:true
	});
	NS.txtFormaPago = new Ext.form.TextField({
		id : PF + 'txtFormaPago',
		name : PF + 'txtFormaPago',
		width : 180,
		x: 100,
		y: 145,
		hidden:true
	});
	//Ventana Pago parcial
	NS.winPagoParcial = new Ext.Window({
		title : 'Cargo en Cuenta Parcial',
		modal : true,
		shadow : true,
		closeAction : 'hide',
		width : 300,
		height : 250,
		layout : 'absolute',
		plain : true,
		bodyStyle : 'padding:10px;',
		buttonAlign : 'center',
		draggable : true,
		resizable : true,
		autoScroll : true,
		items : [ NS.lblFactura, 
		          NS.txtFactura,
		          NS.lbImpOriginal,
		          NS.txtImpOriginal,
		          NS.lblImporte,
		          NS.txtImporte,
		          NS.lblImportePago,
		          NS.txtImportePago,
		          NS.lblSaldoPendiente, 
		          NS.txtSaldoPendiente, 
		          NS.txtfolioDet,
		          NS.txtFormaPago
		          ],
		          buttons:[
		                   {
		                	   text: 'Aceptar',
		                	   handler: function(){
		                		
		                			   NS.aceptarPagoParcial(false); 
		                		   
		                		  
		                	   }
		                   },{
		                	   text: 'Cancelar',
		                	   name:PF+'cmdCancelar',
		                	   handler: function(){
		                		   NS.pbEjecutoPago = false;
		                		   NS.winPagoParcial.hide();
		                	   }
		                   },
		                   ],
		                   listeners : {
		                	   show : {
		                		   fn : function() {
		                		   }
		                	   },
		                	   hide : {
		                		   fn : function() {
		                			   NS.txtfolioDet.setValue("");
		                			   NS.txtFactura.setValue("");
		                			   NS.txtImporte.setValue("");
		                			   NS.txtImpOriginal.setValue("");
		                			   NS.txtImporte.setValue("");
		                			   NS.txtImportePago.setValue("");
		                			   NS.txtSaldoPendiente.setValue(""); 
		                		   }
		                	   }
		                   }
	});

	//Componentees winPagoParcialCrd
	NS.lblFacturaCrd = new Ext.form.Label({
		text: 'Factura:',
		x: 0,
		y: 0
	});
	NS.lblFacturaCrd = new Ext.form.Label({
		text: 'Factura:',
		x: 5,
		y: 10
	});
	NS.txtFacturaCrd = new Ext.form.TextField({
		id : PF + 'txtFacturaCrd',
		name : PF + 'txtFacturaCrd',
		width : 180,
		disabled:true,
		x: 100,
		y: 5
	});
	NS.lbImpOriginalCrd = new Ext.form.Label({
		text: 'Importe Original:',
		x: 5,
		y: 40
	});
	NS.txtImpOriginalCrd = new Ext.form.TextField({
		id : PF + 'txtImpOriginalCrd',
		name : PF + 'txtImpOriginalCrd',
		width : 180,
		x: 100,
		y: 40,
		disabled:true,
		listeners:{
			change:{
				fn:function(){
					NS.txtImpOriginalCrd.setValue(BFwrk.Util.formatNumber(NS.txtImpOriginalCrd.getValue()));
				}
			}
		}
	});
	NS.lblImporteCrd = new Ext.form.Label({
		text: 'Importe Total:',
		x: 5,
		y: 75
	});
	NS.txtImporteCrd = new Ext.form.TextField({
		id : PF + 'txtImporteCrd',
		name : PF + 'txtImporteCrd',
		width : 180,
		x: 100,
		disabled:true,
		y: 75,
		listeners:{
			change:{
				fn:function(){
					NS.txtImporteCrd.setValue(BFwrk.Util.formatNumber(NS.txtImporteCrd.getValue()));
				}
			}
		}
	});

	NS.txtfolioDetCrd = new Ext.form.TextField({
		id : PF + 'txtfolioDetCrd',
		name : PF + 'txtfolioDetCrd',
		width : 180,
		x: 100,
		y: 145,
		hidden:true
	});
	NS.txtFormaPagoCrd = new Ext.form.TextField({
		id : PF + 'txtFormaPagoCrd',
		name : PF + 'txtFormaPagoCrd',
		width : 180,
		x: 100,
		y: 145,
		hidden:true
	});
	NS.lblCapitalCrd = new Ext.form.Label({
		text: 'Capital:',
		x: 5,
		y: 0
	});
	NS.txtCapitalCrd = new Ext.form.TextField({
		id : PF + 'txtCapitalCrd',
		name : PF + 'txtCapitalCrd',
		width : 180,
		x: 92,
		y: 0,
		disabled:true,
		listeners:{
			change:{
				fn:function(){}
			}
		}
	});
	NS.lblInteresCrd = new Ext.form.Label({
		text: 'Interes:',
		x: 5,
		y: 35
	});
	NS.txtInteresCrd = new Ext.form.TextField({
		id : PF + 'txtInteresCrd',
		name : PF + 'txtInteresCrd',
		width : 180,
		x: 92,
		y: 35,
		disabled:true,
		listeners:{
			change:{
				fn:function(){}
			}
		}
	});
	NS.lblIvaCrd = new Ext.form.Label({
		text: 'OTRO:',
		x: 5,
		y: 70
	});
	NS.txtIvaCrd = new Ext.form.TextField({
		id : PF + 'txtIvaCrd',
		name : PF + 'txtIvaCrd',
		width : 180,
		x: 92,
		y: 70,
		disabled:true,
		listeners:{
			change:{
				fn:function(){}
			}
		}
	});

	NS.lblInteresPagoCrd = new Ext.form.Label({
		text: 'Interes:',
		x: 5,
		y: 0
	});
	NS.txtInteresPagoCrd = new Ext.form.TextField({
		id : PF + 'txtInteresPagoCrd',
		name : PF + 'txtInteresPagoCrd',
		width : 180,
		x: 92,
		y: 0,
		value:'0.00',
		listeners:{
			change:{
				fn:function(){
					if(NS.txtInteresPagoCrd.getValue()==""){
						Ext.Msg.alert("SET","Ingrese el interes");
						NS.txtIvaPagoCrd.setValue("0.00");
						return;
					}
//					NS.txtInteresPagoCrd.setValue(BFwrk.Util.formatNumber(NS.txtInteresPagoCrd.getValue()));
//					NS.txtTotalPagoCrd.setValue(BFwrk.Util.formatNumber((parseFloat(BFwrk.Util.unformatNumber(NS.txtIvaPagoCrd.getValue()))+
//							parseFloat(BFwrk.Util.unformatNumber(NS.txtInteresPagoCrd.getValue()))+
//							parseFloat(BFwrk.Util.unformatNumber(NS.txtCapitalCrd.getValue()))).toFixed(2)));
//					console.log("total pago egreso "+NS.txtTotalPagoCrd.getValue());
				}
			}
		}
	});
	NS.lblIvaPagoCrd = new Ext.form.Label({
		text: 'IVA:',
		x: 5,
		value:'0.00',
		y: 35
	});
	NS.txtIvaPagoCrd = new Ext.form.TextField({
		id : PF + 'txtIvaPagoCrd',
		name : PF + 'txtIvaPagoCrd',
		width : 180,
		x: 92,
		y: 35,
		value:'0.00',
		listeners:{
			change:{
				fn:function(){
					if(NS.txtIvaPagoCrd.getValue()==""){
						Ext.Msg.alert("SET","Ingrese el IVA");
						NS.txtIvaPagoCrd.setValue("0.00");
						return;
					}
					NS.txtIvaPagoCrd.setValue(BFwrk.Util.formatNumber(NS.txtIvaPagoCrd.getValue()));
					NS.txtTotalPagoCrd.setValue(BFwrk.Util.formatNumber((parseFloat(BFwrk.Util.unformatNumber(NS.txtIvaPagoCrd.getValue()))+
							parseFloat(BFwrk.Util.unformatNumber(NS.txtInteresPagoCrd.getValue()))+
							parseFloat(BFwrk.Util.unformatNumber(NS.txtCapitalCrd.getValue()))).toFixed(2)));

				}
			}
		}
	});
	NS.lblTotalPagoCrd = new Ext.form.Label({
		text: 'Total Egreso:',
		x: 5,
		y: 70
	});
	NS.txtTotalPagoCrd = new Ext.form.TextField({
		id : PF + 'txtTotalPagoCrd',
		name : PF + 'txtTotalPagoCrd',
		width : 180,
		x: 92,
		value:'0.00',
		y: 70,
		disabled:true,
		listeners:{
			change:{
				fn:function(){
					NS.txtTotalPagoCrd.setValue(BFwrk.Util.formatNumber(NS.txtTotalPagoCrd.getValue()));
					console.log("entro a total pago crd");
				}
			}
		}
	});

	//Ventana Pago parcial
	NS.winPagoParcialCrd = new Ext.Window({
		title : 'Cargo en Cuenta Parcial',
		modal : true,
		shadow : true,
		closeAction : 'hide',
		width : 320,
		height : 450,
		layout : 'absolute',
		plain : true,
		bodyStyle : 'padding:10px;',
		buttonAlign : 'center',
		draggable : true,
		resizable : true,
		autoScroll : true,
		items : [ NS.lblFacturaCrd, 
		          NS.txtFacturaCrd,
		          NS.lbImpOriginalCrd,
		          NS.txtImpOriginalCrd,
		          NS.lblImporteCrd,
		          NS.txtImporteCrd,
		          NS.txtfolioDetCrd,
		          NS.txtFormaPagoCrd, 
		          
		          {
			xtype : 'fieldset',
			title : 'Detalle de importes originales',
			id : PF + 'panelFinanc',
			name : PF + 'panelFinanc',
			x : 0,
			y : 100,
			width : 300,
			height : 130,
			layout : 'absolute',
			items : [
			         NS.lblCapitalCrd, 
			         NS.txtCapitalCrd, 
			         NS.lblInteresCrd, 
			         NS.txtInteresCrd,
			         NS.lblIvaCrd, 
			         NS.txtIvaCrd,]
		          },
		          {
		        	  xtype : 'fieldset',
		        	  title : 'Pago',
		        	  id : PF + 'panelFinanc1',
		        	  name : PF + 'panelFinanc1',
		        	  x : 0,
		        	  y : 240,
		        	  width : 300,
		        	  height : 130,
		        	  layout : 'absolute',
		        	  items : [
		        	           NS.lblInteresPagoCrd, 
		        	           NS.txtInteresPagoCrd,
		        	           NS.lblIvaPagoCrd, 
		        	           NS.txtIvaPagoCrd,
		        	           NS.lblTotalPagoCrd, 
		        	           NS.txtTotalPagoCrd,
		        	           ]
		          }
		          ],
		          buttons:[
		                   {
		                	   text: 'Aceptar',
		                	   handler: function(){
//		                		   console.log("antes aceptar "+ NS.txtInteresPagoCrd.getValue());
//		                		   var total=NS.txtImpOriginal.getValue()+NS.txtInteresPagoCrd.getValue();
//		                		   console.log("suma total "+total);
//		                		   if(NS.txtImpOriginal.getValue()==total){
//		                			   Ext.Msg.alert("SET","La suma del importe original y el intéres \n debe ser menor al importe del Banco");
//		                		   }else{
		                			   NS.aceptarPagoParcial(false); 
//		                		   }
		                		
		                	   }
		                   },{
		                	   text: 'Cancelar',
		                	   name:PF+'cmdCancelarCrd',
		                	   handler: function(){
		                		   NS.pbEjecutoPago = false;
		                		   NS.winPagoParcialCrd.hide();
		                	   }
		                   },
		                   ],
		                   listeners : {
		                	   show : {
		                		   fn : function() {
		                		   }
		                	   },
		                	   hide : {
		                		   fn : function() {
		                			   NS.txtfolioDetCrd.setValue("");
		                			   NS.txtFacturaCrd.setValue("");
		                			   NS.txtImporteCrd.setValue("");
		                			   NS.txtImpOriginalCrd.setValue("");
		                			   NS.txtSaldoPendiente.setValue(""); 
		                			   NS.txtInteresPagoCrd.setValue("");
		                			   NS.txtIvaPagoCrd.setValue("");
		                			   NS.txtTotalPagoCrd.setValue("");
		                			   NS.storeObtenerImportes.removeAll();
		                		   }
		                	   }
		                   }
	});

	NS.storeObtenerImportes = new Ext.data.DirectStore({
		paramsAsHash: false,
		root: '',
		baseParams:{
			folio: 0
		},
		paramOrder:['folio'],
		directFn: ConfirmacionCargoCtaAction.obtenerImporte, 
		fields: [
		         {name: 'interes'},
		         {name: 'iva'},
		         {name: 'capital'},
		         ],

	});
	//Función Parcializa
	NS.parcializa=function(){
		NS.pbEjecutoPago = false;
		pbCriteriosCorrectos = true;
		var band=false;

		/*if(Ext.getCmp(PF + 'cmbEmpresas').getValue() == ""&&Ext.getCmp(PF + 'cmbEmpresas').getValue() !=0){
			Ext.Msg.alert("SET","Tiene que seleccionar una empresa.");
			Ext.getCmp(PF + 'cmbEmpresas').focus();
			return;
		}
		else if(Ext.getCmp(PF + 'cmbBanco').getValue() == ""){
			Ext.Msg.alert("SET","Tiene que seleccionar un banco.");
			Ext.getCmp(PF + 'cmbBanco').focus();
			return;
		}
		else if(Ext.getCmp(PF + 'cmbChequeras').getValue() == ""){
			Ext.Msg.alert("SET","Tiene que seleccionar una chequera.");
			Ext.getCmp(PF + 'cmbChequeras').focus();
			return;
		}
		else*/ 
		if(NS.txtFechaIni.getValue() == ""||NS.txtFechaIni.getValue()==null){
			Ext.Msg.alert("SET","Tiene que indicar una fecha valida.");
			NS.txtfechaIni.focus();
			return;
		}
		console.log("datos devueltos por store "+NS.storePagos.data.items.length);
		if(NS.storePagos.data.items.length>0){
			var registros = NS.gridPagos.getSelectionModel().getSelections();
			var registrosCargos = NS.gridCargos.getSelectionModel().getSelections();
			if (registros.length<=0) {
				Ext.Msg.alert("SET","Selecciona el Movimiento a parcializar.");
				
			}
			else{
				if (registrosCargos.length<=0) {
					Ext.Msg.alert("SET","Selecciona un Cargo.");
				}else{
					console.log("origen mov "+registros[0].data.origenMov);
					if(registros[0].data.origenMov!="CRD"){
						if(registros.length>1){
							Ext.Msg.alert("SET","Debe seleccionar solo un registro de Pagos");
							return;
						}else{
							if(registros[0].data.importe>registrosCargos[0].data.importe){
								Ext.Msg.alert("El capital del pago debe ser menor al importe el Cargo");
								return;
							}else{
								//NS.txtfolioDet.setValue(registros[0].data.noFolioDet);
								//NS.txtFactura.setValue("CARGO EN CUENTA PARCIAL");
//								NS.txtImporte.setValue(BFwrk.Util.formatNumber(registros[0].data.importe));
//								NS.txtImpOriginal.setValue(BFwrk.Util.formatNumber(registros[0].data.importe));
//								NS.winPagoParcial.show();
								NS.txtfolioDetCrd.setValue(registros[0].data.noFolioDet);
								NS.txtFacturaCrd.setValue("CARGO EN CUENTA PARCIAL");
								NS.txtImporteCrd.setValue(BFwrk.Util.formatNumber(registrosCargos[0].data.importe));
								NS.txtImpOriginalCrd.setValue(BFwrk.Util.formatNumber(NS.txtMontoPag.getValue()));
								var interes=(Math.round((registrosCargos[0].data.importe-registros[0].data.importe)*100)/100);
								console.log("interes "+interes);
								if(interes<0){
									interes=interes*-1;
								}
								NS.txtCapitalCrd.setValue(BFwrk.Util.formatNumber(registros[0].data.importe));
								NS.txtInteresCrd.setValue(BFwrk.Util.formatNumber("0.00"));
								NS.txtIvaCrd.setValue("0.00");
								NS.txtInteresPagoCrd.setValue(BFwrk.Util.formatNumber(interes));
								NS.txtIvaPagoCrd.setValue("0.00");
								NS.txtTotalPagoCrd.setValue(BFwrk.Util.formatNumber(parseFloat(BFwrk.Util.unformatNumber(NS.txtInteresPagoCrd.getValue()))+parseFloat(BFwrk.Util.unformatNumber(NS.txtCapitalCrd.getValue()))));
								NS.winPagoParcialCrd.show();
							}
						}
					}
					else{
//					
//					if(registros.length>=2){
//						NS.txtfolioDetCrd.setValue(registros[0].data.noFolioDet);
//						NS.txtFacturaCrd.setValue("CARGO EN CUENTA PARCIAL");
//						NS.txtImporteCrd.setValue(BFwrk.Util.formatNumber(registros[0].data.importe));
//						NS.txtImpOriginalCrd.setValue(BFwrk.Util.formatNumber(registros[0].data.importe));
//						NS.txtImpOriginalCrd.setValue(BFwrk.Util.formatNumber(registros[0].data.importe));
//						NS.storeObtenerImportes.load({
//							params:{
//								folio:registros[0].data.noFolioDet
//							},
//							callback : function(records, operation,success) {
//								console.log("tamaño registros "+records.length);
//								if(records.length<=0){
//									Ext.Msg.alert('SET',"Error al obtener el detalle del movimiento.");
//									return;
//								}
//								else{
//									NS.txtCapitalCrd.setValue(BFwrk.Util.formatNumber(parseFloat(records[0].get('capital')).toFixed(2)));
//									NS.txtInteresCrd.setValue(BFwrk.Util.formatNumber(parseFloat(records[0].get('interes')).toFixed(2)));
//									NS.txtIvaCrd.setValue(BFwrk.Util.formatNumber(parseFloat(records[0].get('iva')).toFixed(2)));
//									NS.txtTotalPagoCrd.setValue(BFwrk.Util.formatNumber(parseFloat(records[0].get('capital')).toFixed(2)));
//									NS.txtInteresPagoCrd.setValue(interes);
//									NS.txtIvaPagoCrd.setValue(BFwrk.Util.formatNumber(registros[1].data.importe));
//									
//								}
//								NS.winPagoParcialCrd.show();
//							}
//						});
//					}else{
						var interes=0;
						var folioDetalle=0;
						for(var i=0;i<registros.length;i++){
							console.log("posicion "+i);
							console.log("importe "+registros[i].data.importe);
							console.log("concepto "+registros[i].data.concepto);
							var concepto=registros[i].data.concepto;
							concepto=concepto.substring(0,12);
							
							console.log("concepto busc "+concepto);
							if(concepto=='Pago Capital'){
								console.log("cap "+registros[i].data.importe);
								var capital=registros[i].data.importe;
							}else{
								if(concepto=='Pago Interes'){
									console.log("inte "+registros[i].data.importe);
									interes=registros[i].data.importe;
									folioDetalle=registros[i].data.noFolioDet;
								}
								
							}
						}
						if(capital>registrosCargos[0].data.importe){
							Ext.Msg.alert("El capital del pago debe ser menor al importe el Cargo");
							return;
						}else{
							console.log("folioDetalle "+folioDetalle);
							if(folioDetalle==0){
								Ext.Msg.alert("SET","Debe seleccionar un pago de interés");
							}else{
								
								NS.txtfolioDetCrd.setValue(folioDetalle);
								NS.txtFacturaCrd.setValue("CARGO EN CUENTA PARCIAL");
								NS.txtImporteCrd.setValue(BFwrk.Util.formatNumber(registrosCargos[0].data.importe));
								NS.txtImpOriginalCrd.setValue(BFwrk.Util.formatNumber(NS.txtMontoPag.getValue()));
								NS.storeObtenerImportes.load({
									params:{
										folio:registros[0].data.noFolioDet
									},
									callback : function(records, operation,success) {
										console.log("tamaño registros "+records.length);
										if(records.length<=0){
											Ext.Msg.alert('SET',"Error al obtener el detalle del movimiento.");
											return;
										}
										else{
											NS.txtCapitalCrd.setValue(BFwrk.Util.formatNumber(parseFloat(records[0].get('capital')).toFixed(2)));
											NS.txtInteresCrd.setValue(BFwrk.Util.formatNumber(parseFloat(records[0].get('interes')).toFixed(2)));
											NS.txtIvaCrd.setValue(BFwrk.Util.formatNumber(parseFloat(records[0].get('iva')).toFixed(2)));
											NS.txtTotalPagoCrd.setValue(BFwrk.Util.formatNumber(parseFloat(records[0].get('capital')).toFixed(2)));
											NS.txtInteresPagoCrd.setValue(interes);
											//NS.txtInteresPagoCrd.setValue(NS.txtInteresPagoCrd.getValue());
											NS.txtIvaPagoCrd.setValue("0.00");	
										}
										NS.winPagoParcialCrd.show();
									}
								});
								var totalSet=(Math.round((registrosCargos[0].data.importe-capital)*100)/100);
								console.log("total set "+totalSet);
								if(totalSet<0){
									totalSet=totalSet*-1;
									}
								NS.txtCapitalCrd.setValue(BFwrk.Util.formatNumber(capital));
								NS.txtInteresCrd.setValue(BFwrk.Util.formatNumber(interes));
								NS.txtIvaCrd.setValue("0.00");
								NS.txtInteresPagoCrd.setValue(BFwrk.Util.formatNumber(totalSet));
								//NS.txtInteresPagoCrd.setValue(NS.txtInteresPagoCrd.getValue());
								NS.txtIvaPagoCrd.setValue("0.00");
								NS.txtTotalPagoCrd.setValue(BFwrk.Util.formatNumber(parseFloat(BFwrk.Util.unformatNumber(NS.txtInteresPagoCrd.getValue()))+parseFloat(BFwrk.Util.unformatNumber(NS.txtCapitalCrd.getValue()))));
								NS.winPagoParcialCrd.show();
						//	}
							}
						}									
					}
				}
				}


		}



	}
	NS.limpiar = function() {
		Ext.getCmp(PF + 'cmbEmpresas').reset();
		Ext.getCmp(PF + 'txtBanco').reset();
		Ext.getCmp(PF + 'cmbBanco').reset();
		Ext.getCmp(PF + 'cmbChequeras').reset();
		NS.storeBancos.removeAll();
		NS.storeChequeras.removeAll();
		Ext.getCmp(PF + '').setValue(NS.fecIni);
		Ext.getCmp(PF + 'txtFechaFin').setValue(NS.fecHoy);
		Ext.getCmp(PF + 'chkRechazosNomina').setValue(false);
		NS.storePagos.removeAll();
		NS.gridPagos.getView().refresh();
		NS.storeCargos.removeAll();
		NS.gridCargos.getView().refresh();
		Ext.getCmp(PF + 'txtRegistrosPag').setValue('0');
		Ext.getCmp(PF + 'txtMontoPag').setValue('0.00');
		Ext.getCmp(PF + 'txtRegistrosCar').setValue('0');
		Ext.getCmp(PF + 'txtMontoCar').setValue('0.00');
		Ext.getCmp(PF + 'txtDiferencia').setValue('0.00');
		Ext.getCmp(PF + 'txtPermisible').setValue('0.00');
		Ext.getCmp(PF + 'txtEmpresa').setValue('0');
	};

	NS.confirmar = function() {
		var regSelecPagos = NS.gridPagos.getSelectionModel().getSelections();
		var regSelecCargos = NS.gridCargos.getSelectionModel().getSelections();

		var matRegConfPagos = new Array ();
		var matRegConfCargos = new Array ();

		for(var i=0; i<regSelecPagos.length; i++) {
			var regConfPagos = {};
			regConfPagos.noFolioDet = regSelecPagos[i].get('noFolioDet');
			regConfPagos.importe = regSelecPagos[i].get('importe');
			regConfPagos.fecValorOriginal = regSelecPagos[i].get('fecValorOriginal');

			matRegConfPagos[i] = regConfPagos;
		}
		for(var x=0; x<regSelecCargos.length; x++) {
			var regConfCargos = {};
			regConfCargos.noFolioDet = regSelecCargos[x].get('noFolioDet');
			regConfCargos.importe = regSelecCargos[x].get('importe');
			regConfCargos.secuencia = regSelecCargos[x].get('secuencia');

			matRegConfCargos[x] = regConfCargos;
		}
		var jSonString = Ext.util.JSON.encode(matRegConfPagos);
		var jSonString2 = Ext.util.JSON.encode(matRegConfCargos);
		var dDiferencia = Ext.getCmp(PF + 'txtDiferencia').getValue();
		var dPermisible = Ext.getCmp(PF + 'txtPermisible').getValue();

		dDiferencia = NS.unformatNumber((dDiferencia.substring(0, dDiferencia.indexOf('.') + 3))) * -1;
	//	console.log("diferencia "+dDiferencia);
		ConfirmacionCargoCtaAction.ejecutarConfirmacionCargoCta(jSonString, jSonString2, '' + dDiferencia,
				'' + dPermisible, function(res, e) {
	//		console.log("respuesta confirmador "+res.msgUsuario);
			if(res != null && res != undefined && res != '') {
				Ext.Msg.alert('SET', '' + res.msgUsuario + '!!');
				NS.buscar();
			}
		});
	};

	NS.contenedorCofirmacionCargo = new Ext.FormPanel( {
		title: 'confirmación de Cargos en Cuenta',
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
	NS.contenedorCofirmacionCargo.setSize(Ext.get(NS.tabContId).getWidth(), Ext.get(NS.tabContId).getHeight());
});
