﻿//@Autor Cristian 
Ext.onReady(function(){
	Ext.QuickTips.init();
	Ext.Direct.addProvider(Ext.setapp.REMOTING_API);	
	var NS = Ext.namespace('apps.SET.Consultas.ConsultaDeMovimientos');
	var PF = apps.SET.tabID + '.';
	NS.tabContId = apps.SET.tabContainerId;	 
	NS.idUsuario = apps.SET.iUserId;
	var psRevividor = "N";
	NS.fecHoy = BFwrk.Util.changeStringToDate(apps.SET.FEC_HOY);
	NS.sHora = "";	
	NS.noEmpresa = "";
	NS.nomEmpresa = "";
	var sName="";
	NS.resultado= "";
	NS.GI_USUARIO = apps.SET.iUserId;
	NS.myMask = new Ext.LoadMask(Ext.getBody(), {msg:"Buscando ..."});
	
    NS.dato= new Ext.form.TextField({
		id: PF + 'dato',
        name: PF + 'dato',
    	value:'',
        x: 120,
        y: 60,
        width: 430, 
    	emptyText: '',
    });

	
	/********************************************************
	 * BOTONES FACULTATIVOS
	 */
	
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
		for(var i=0;i<12;i=i+1)
		{
			if(mesArreglo[i]===mesDate)
			{
			var mes=i+1;
				if(mes<10)
				var mes='0'+mes;
			}
		}		
		var fechaString=''+dia+'/'+mes+'/'+anio;
		return fechaString;		
	}
	
	NS.btnDescompensar = apps.SET.btnFacultativo( new Ext.Button({
		text: 'Eliminar',
        x: 575,
        y: 217,
        width: 70,
        id:  'BeCbtnRegresar',
        name: 'BeCbtnRegresar',
        disabled : true,
        listeners: {
        	click : {
        		fn : function(e){
        			var validar= NS.validarDescompensar();
        			if(validar!='')
        				BFwrk.Util.msgShow(validar, 'WARNING');
        			else{	
        				if(NS.resultado != ''){
        					NS.validar3200();
        				}else{
	        				Ext.Msg.show({
	        					title: 'Información SET',
	        					msg: '¿Esta seguro de Eliminar los movimientos seleccionados?',
	        						buttons: {
	        							yes: true,
	        							no: true,
	        							cancel: false
	        						},
	        						icon: Ext.MessageBox.QUESTION,
	        						fn: function(btn) {
	        							if(btn === 'yes')
	        								NS.regresarMovto();
	        						}
	        				});
        				}
        			}
        		}
        	}
        }
	}));
	
	NS.btnEliminar = apps.SET.btnFacultativo( new Ext.Button({
		text: 'Regresar',
        x: 670,
        y: 217,
        width: 70,
        id: 'BeCbtnEliminar',
        name : 'BeCbtnEliminar',
        disabled : true,
        listeners : {
        	click: {
        		fn : function(e){
        			var recGridConsMovi = NS.gridConsultaMovimientos.getSelectionModel().getSelections();
        			if(recGridConsMovi.length <= 0){
        				BFwrk.Util.msgShow('Debe seleccionar minimo un registro', 'WARNING');
        			}else{
        				var mensaje=NS.validarMovimientoEliminar();
        				if(mensaje==''){
        					Ext.Msg.show({
        						title: 'Información SET',
        						msg: '¿Esta seguro de regresar el movimiento seleccionado?',
        						buttons: {
        								yes: true,
        								no: true,
        								cancel: false
        						},
        						icon: Ext.MessageBox.QUESTION,
        						fn: function(btn) {
        							if(btn == 'yes')
        								NS.eliminarMovto();
        							}
        						});
        				}else
        					BFwrk.Util.msgShow(mensaje, 'WARNING');
        			}
        			
        		}
        	}	
        }
	}));
	
	NS.chkDetalladoFondeo= new Ext.form.Checkbox({
		id:PF+ 'chkDetalladoFondeo',
		name :PF + 'chkDetalladoFondeo',
		boxLabel: 'Detallado ',
		x: 0,
		y : 25,
	});
	/*Se agrega componente para motivos */
	/********************************************************
	 * 					Combo Motivos						*
	 *******************************************************/
	NS.lbMotivo = new Ext.form.Label({
		text: 'Motivo: ',
        x: 220,
        y: 220	
	});
	
	NS.storeMotivos = new Ext.data.DirectStore({
		paramsAsHash: false,
		baseParams:{},
		root: '',
		paramOrder:[],
		directFn: ChequesTransitoAction.llenarComboMotivos, 
		idProperty: 'id', 
		fields: [
			 {name: 'id'},
			 {name: 'descripcion'},
		],
		listeners: {
			load: function(s, records){				
				if(records.length==null || records.length<=0){
					Ext.Msg.alert('SET','No existen motivos');
				}
			},
			exception:function(misc) {
				Ext.Msg.alert('SET', 'Error al cargar los datos, verificar la conexión');
			}
		}
	});
	NS.storeMotivos.load();
	
	NS.cmbMotivos = new Ext.form.ComboBox({
		store: NS.storeMotivos
		,name: PF+'cmbMotivos'
		,id: PF+'cmbMotivos'
		,typeAhead: true
		,mode: 'local'
		,minChars: 0
		,selecOnFocus: true
		,forceSelection: true
		,x: 280
        ,y: 216
        ,width: 170
        ,valueField:'id'
    	,displayField:'descripcion'
    	,emptyText: 'Seleccione un motivo'
		,triggerAction: 'all'
		,value: ''
		,visible: false
		,autocomplete:true
		,fieldLabel:'Motivos'
	});
	 /*********Fin combo motivos *******/
	
	/*NS.cmbGraficas = new Ext.form.ComboBox({
		store:['Grafica Empresa/Movs','Grafica Banco/Importe','Grafica For.Pago/Movs','Grafica Estatus/Movs','Graficar Ing/Egr'],
		name: PF+'cmbGraficas',
		id: PF+'cmbGraficas',
		typeAhead: true,
		mode: 'local',
		minChars: 0,
		selecOnFocus: true,
		forceSelection: true,
        x: 0,
        y: 0,
        width: 190,
		valueField:'idGrafica',
		displayField:'descripcion',
		autocomplete: true,
		emptyText: 'Seleccione tipo de Gráfica',
		triggerAction: 'all',
		value: '',
		hidden:true,
		listeners:{
			select:{
				fn:function(combo, value) {

				    combo.setDisabled(true);
				    
					if(combo.getValue() == 'Grafica Empresa/Movs')
					{
						//Grafica Empresa/Movs
						Ext.getCmp('panel3').show();
						Ext.getCmp('panel2').show();
						Ext.getCmp('panel1').show();
						Ext.getCmp('panel1').update('<img src="'+window.location.protocol+"//"+window.location.host+"/graficaSet/"+apps.SET.iUserId+'EmpresaNumMovs'+sName+'PC.jpg" border="0"/>');
						Ext.getCmp('panel2').update('<img src="'+window.location.protocol+"//"+window.location.host+"/graficaSet/"+apps.SET.iUserId+'EmpresaNumMovs'+sName+'BG.jpg" border="0"/>');
						Ext.getCmp('panel3').update('<img src="'+window.location.protocol+"//"+window.location.host+"/graficaSet/"+apps.SET.iUserId+'EmpresaNumMovs'+sName+'LG.jpg" border="0"/>');
						Ext.Msg.alert('SET', 'Graficas listas');
						
					}
					else if(combo.getValue() == 'Grafica Banco/Importe')
					{
						//Grafica Banco/Importe
						Ext.getCmp('panel3').show();
						Ext.getCmp('panel2').show();
						Ext.getCmp('panel1').show();
					    Ext.getCmp('panel1').update('<img src="'+window.location.protocol+"//"+window.location.host+"/graficaSet/"+apps.SET.iUserId+'BancosImporte'+sName+'PC.jpg" border="0"/>');
						Ext.getCmp('panel2').update('<img src="'+window.location.protocol+"//"+window.location.host+"/graficaSet/"+apps.SET.iUserId+'BancosImporte'+sName+'BG.jpg" border="0"/>');
						Ext.getCmp('panel3').update('<img src="'+window.location.protocol+"//"+window.location.host+"/graficaSet/"+apps.SET.iUserId+'BancosImporte'+sName+'LG.jpg" border="0"/>');
						Ext.Msg.alert('SET', 'Graficas listas');
					}
					else if(combo.getValue() == 'Grafica For.Pago/Movs')
					{
						//Grafica For.Pago/Movs
						Ext.getCmp('panel3').show();
					    Ext.getCmp('panel2').show();
					    Ext.getCmp('panel1').show();
					    Ext.getCmp('panel1').update('<img src="'+window.location.protocol+"//"+window.location.host+"/graficaSet/"+apps.SET.iUserId+'FormaPagoNumMovs'+sName+'PC.jpg" border="0"/>');
						Ext.getCmp('panel2').update('<img src="'+window.location.protocol+"//"+window.location.host+"/graficaSet/"+apps.SET.iUserId+'FormaPagoNumMovs'+sName+'BG.jpg" border="0"/>');
						Ext.getCmp('panel3').update('<img src="'+window.location.protocol+"//"+window.location.host+"/graficaSet/"+apps.SET.iUserId+'FormaPagoNumMovs'+sName+'LG.jpg" border="0"/>');
						Ext.Msg.alert('SET', 'Graficas listas');
			
					}
					else if(combo.getValue()== 'Grafica Estatus/Movs')
					{
						//Grafica Estatus/Movs
						Ext.getCmp('panel3').show();
					    Ext.getCmp('panel2').show();
					    Ext.getCmp('panel1').show();
					    Ext.getCmp('panel1').update('<img src="'+window.location.protocol+"//"+window.location.host+"/graficaSet/"+apps.SET.iUserId+'EstatusNumMovs'+sName+'PC.jpg" border="0"/>');
					    Ext.getCmp('panel2').update('<img src="'+window.location.protocol+'//'+window.location.host+'/graficaSet/back.png" border="0"/>');
					    Ext.getCmp('panel3').update('<img src="'+window.location.protocol+'//'+window.location.host+'/graficaSet/back.png" border="0"/>');
					    Ext.Msg.alert('SET', 'Graficas listas');

					}
					else if(combo.getValue()== 'Graficar Ing/Egr')
					{
						//Graficar Ing/Egr
						NS.graficaEspecial();

					}	
					
				}
			}
		},
		items:{
			
		}
	});
	Ext.getCmp(PF+'cmbGraficas').setDisabled(true);*/
	//Se agrega combo de empresas 

	
	/************************************************************************
	 * 							COMBO EMPRESAS
	 ************************************************************************/
		
	NS.storeLlenaEmpresa = new Ext.data.DirectStore({
		paramsAsHash: true,
		root: '',
		directFn: GlobalAction.llenarComboEmpresasUsuario,
		fields:
		[
		    
		    {name: 'id'},
		 	{name: 'descripcion'}
		 	
		],
		idProperty: 'id',
		listeners:
		{
			load: function(s, records){
				var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeLlenaEmpresa, msg: "Cargando..."});
				if (records.length == null || records.length <= 0){
					Ext.Msg.alert('SET', 'No tiene empresas asignadas');
				}
				//Se agrega la opcion de todas las empresas
				var recordsStoreEmpresas = NS.storeLlenaEmpresa.recordType;
				var todas = new recordsStoreEmpresas({
					id: 0,
					descripcion: '***************TODAS***************'
				});
				NS.storeLlenaEmpresa.insert(0, todas);
				NS.myMask.hide();
					
			},
			exception:function(misc) {
				NS.myMask.hide();
				Ext.Msg.alert('SET', 'Error al cargar los datos, verificar la conexión');
			}
		}
	}); 
    NS.myMask.show();
	NS.storeLlenaEmpresa.load();
	
	NS.txtNoEmpresa = new Ext.form.TextField({
		id: PF + 'txtNoEmpresa',
        name:PF + 'txtNoEmpresa',
        x: 0,
		y: 15,
		width: 50,
        tabIndex: 0,
        value:'0',
        listeners: {
			change: {
				fn: function(caja,valor) {
					if (caja.getValue() !== '' && caja.getValue() !== undefined && caja.getValue() !== null){
						BFwrk.Util.updateTextFieldToCombo(PF + 'txtNoEmpresa', NS.cmbEmpresa.getId());
						idEmpresa = parseInt(caja.getValue());
						NS.noEmpresa = caja.getValue();
						if(NS.cmbEmpresa.getRawValue()=='***************TODAS***************')
							caja.setValue('0');
					}
					else{
						NS.cmbEmpresa.reset();
						idEmpresa = 0;
						NS.noEmpresa = 0;
					}
                }
			}
    	}
    });

	NS.cmbEmpresa = new Ext.form.ComboBox({
		store: NS.storeLlenaEmpresa,
		id: PF + 'cmbEmpresa',
		name: PF + 'cmbEmpresa',
		x: 60,
		y: 15,
		width: 320,
		typeAhead: true,
		mode: 'local',
		forceSelection: true,
		valueField: 'id',
		displayField: 'descripcion',
		triggerAction: 'all',
		value:'',
		visible: false,
		autocomplete:true,
		selectOnFocus:true,
		listeners:{
			select:{
				fn:function(combo, valor) {
					 NS.limpiarCombos();
			 		 NS.valoresIniciales();
					BFwrk.Util.updateComboToTextField(PF + 'txtNoEmpresa', NS.cmbEmpresa.getId());
					
				}
			}
		}
	});
	/**********************************************/
	
	NS.inicializarVariables = function(){
		NS.iIdBanco = 0;
		NS.iIdBeneficiario = 0;
		NS.iIdCaja = 0;
		NS.sIdChequera = '';
		NS.sConcepto = '';
		NS.sIdDivisa = '';
		NS.iIdDivision = 0;
		NS.sEstatus= '';
		NS.iFolMovi = 0;
		NS.iIdFormaPago = 0;
		NS.sCvePropuesta = '';
		NS.uMontoIni = 0;
		NS.uMontoFin = 0;
		NS.iNoCheque = 0;
		NS.sNoDocto = '';
		NS.iNoFactura = 0;
		NS.sOrigen = '';
		NS.iTipoOperacion = 0;
		NS.sValorCustodia = '';
		NS.sFecIni = '';
		NS.sFecFin = '';
		NS.bOptEmp = true;
		NS.bOptTipoMovto = true;
		NS.bSolCan = false;
		NS.parametroBus = '';
		NS.sBeneficiario = '';
		NS.pooHeaders= '';
	};
	
	NS.inicializarVariables();
	
	NS.limpiar = function()
	{
		NS.inicializarVariables();
		NS.storeDatos.removeAll();
		NS.gridDatos.getView().refresh();
		NS.storeConsultaMovimientos.removeAll();
		NS.gridConsultaMovimientos.getView().refresh();
		NS.ConsultaDeMovimientos.getForm().reset();
		//Ext.getCmp(PF+'cmbGraficas').setDisabled(true);
	};
	
	NS.limpiarCombos = function()
	{
		NS.cmbBanco.reset();
		NS.cmbCajas.reset();
		NS.cmbChequeras.reset();
		NS.cmbDivisas.reset();
		NS.cmbDivisiones.reset();
		NS.cmbEstatus.reset();
		NS.cmbFormaPago.reset();
		NS.cmbOrigen.reset();
		NS.cmbTipoMovto.reset();
		NS.cmbValorCustodia.reset();
		NS.cmbBeneficiario.reset();
		NS.storeDatos.removeAll();
		NS.gridDatos.getView().refresh();	
		
	};
		
	NS.sm = new Ext.grid.CheckboxSelectionModel({
		singleSelect : false
	});
	//Inicia declaracion de stores para los combos de criterios de búsqueda
	
	//Llamada a GlobalAction para obtener la hora actual
	NS.obtenerHora = function(){
		GlobalAction.obtenerHoraActualFormato12(function(result, e){
			if(result.substring(result.length - 1) === '1')
			{
				NS.sHora = result.substring(0,5) + ' PM';
				return NS.sHora;
			}
			else
			{
				NS.sHora = result.substring(0,5) + ' AM';
				return NS.sHora;
			}
		});
	};	
	
	NS.obtenerHora();
	//Store Datos donde se agregan los valores seleccionados
	NS.storeDatos = new Ext.data.DirectStore({
		paramsAsHash: false,
		root: '',
		idProperty: 'criterio',//identificador del store
		fields: [
					{name: 'id'},
					{name: 'criterio'},
					{name: 'valor'}
				],
		listeners: {
			load: function(s, records){
				




		
				}
			}
	}); 
	
	//Grid para mostrar los datos seleccionados como parametros de búsqueda
	NS.gridDatos  = new Ext.grid.GridPanel({
		store : NS.storeDatos,
		columns : 
			[ 
				{
					id :'id',
					header :'Id',
					width :10,
					dataIndex :'id',
					hidden: true
				},{
					header :'Criterio',
					width :120,
					dataIndex :'criterio'
				}, {
					header :'Valor',
					width :150,
					dataIndex :'valor'
				} 
			],
		columnLines: true,
		frame:true,
        autoScroll: true,
		width: 320,
       	height: 160,
		x :420,
		y :0,
		title :'',
		listeners:{

			//ESTO ESTABA ANTES//    dblclick:{
			click:{	
			
				fn:function(grid){
					var records = NS.gridDatos.getSelectionModel().getSelections();	
					for(var i=0;i<records.length;i = i + 1)
					{
						if(records[i].get('criterio') == 'FECHA VALOR')
						{
							BFwrk.Util.msgShow('No puede eliminar la fecha', 'INFO');
							return;
						}
						NS.gridDatos.store.remove(records[i]);
						NS.gridDatos.getView().refresh();
						Ext.getCmp(PF+'cmbCriterio').setDisabled(false);
						
						

					}
				}
			}
		}
	});																																																																																																																																									
	
	
	//Esta Función permite mostrar u ocultar componentes del formulario
	NS.mostrarOcultar = function(bMostrar){
		Ext.getCmp(PF + 'cmbValorCustodia').setVisible(bMostrar);
		//Ext.getCmp(PF + 'cmbTipoMovto').setVisible(bMostrar);
		Ext.getCmp(PF + 'cmbOrigen').setVisible(bMostrar);
		Ext.getCmp(PF + 'cmbFormaPago').setVisible(bMostrar);
		Ext.getCmp(PF + 'cmbEstatus').setVisible(bMostrar);
		//Ext.getCmp(PF + 'cmbDivisiones').setVisible(bMostrar);
		Ext.getCmp(PF + 'cmbDivisas').setVisible(bMostrar);
		Ext.getCmp(PF + 'cmbChequeras').setVisible(bMostrar);
		//Ext.getCmp(PF + 'cmbCajas').setVisible(bMostrar);
		Ext.getCmp(PF + 'cmbBanco').setVisible(bMostrar);
		Ext.getCmp(PF + 'txtConcepto').setVisible(bMostrar);
		//Ext.getCmp(PF + 'txtFolioMovi').setVisible(bMostrar);
		Ext.getCmp(PF + 'txtCvePropuesta').setVisible(bMostrar);
		Ext.getCmp(PF + 'txtMontoIni').setVisible(bMostrar);
		Ext.getCmp(PF + 'txtMontoFin').setVisible(bMostrar);
		Ext.getCmp(PF + 'txtNoCheque').setVisible(bMostrar);
		Ext.getCmp(PF + 'txtNoDocto').setVisible(bMostrar);
		Ext.getCmp(PF + 'txtNoFactura').setVisible(bMostrar);
		Ext.getCmp(PF + 'btnComboBenef').setVisible(bMostrar);
		//Ext.getCmp(PF + 'cmbBeneficiario').setVisible(bMostrar);
		Ext.getCmp(PF + 'txtFolioCompensacion').setVisible(bMostrar);
		
	};
	
	NS.datosCriterio = [
		//['0','FECHA VALOR'],
		['1','BANCO'],
		//['2','BENEFICIARIO'],
		//['3','CAJA'],
		['4','CHEQUERA'],
		['5','CONCEPTO'],
		['6','DIVISA'],
		//['7','DIVISION'],
		['8','ESTATUS'],
	//	['9','FOLIO MOVIMIENTO'],
		['10','FORMA DE PAGO'],
		['11','CVE PROPUESTA'],
		['12','MONTOS'],
		['13','NO. CHEQUE'],
		['14','NO. DOCUMENTO'],
		['15','NO. FACTURA'],
		['16','ORIGEN'],
		//['17','TIPO MOVIMIENTO'],
		//['18','VALOR CUSTODIA']
		['19','FOLIO COMPENSACION'],
	];
	
	NS.storeCriterio  = new Ext.data.SimpleStore({
		idProperty: 'idCriterio',
		fields:
		[
			{name: 'idCriterio'},
			{name: 'descripcion'}
		]
	});
	
	NS.storeCriterio.loadData(NS.datosCriterio);
	
	//Combo que contiene los criterios de busqueda
	NS.cmbCriterio = new Ext.form.ComboBox({
		store: NS.storeCriterio,
		name: PF+'cmbCriterio',
		id: PF+'cmbCriterio',
		typeAhead: true,
		mode: 'local',
		minChars: 0,
		selecOnFocus: true,
		forceSelection: true,
        x: 50,
        y: 90,
        width: 190,
		valueField:'idCriterio',
		displayField:'descripcion',
		autocomplete: true,
		emptyText: 'Seleccione un criterio',
		triggerAction: 'all',
		value: '',
		listeners:{
			select:{
				fn:function(combo, value) {

				    combo.setDisabled(true);
				    
					if(combo.getValue() == 1)
					{
						//BANCO
						NS.storeBanco.baseParams.idEmpresa = parseInt( Ext.getCmp(PF + 'txtNoEmpresa').getValue());
						NS.storeBanco.load();
						NS.mostrarOcultar(false);
						Ext.getCmp(PF + 'cmbBanco').setDisabled(false);
						Ext.getCmp(PF + 'cmbBanco').setVisible(true);
						
						NS.agregarCriterioValor ('BANCO', Ext.getCmp(PF + 'cmbBanco').getId(), 'criterio');
						
					}
					else if(combo.getValue() == 2)
					{
						//BENEFICIARIO
						NS.mostrarOcultar(false);
						Ext.getCmp(PF + 'cmbBeneficiario').setDisabled(false);
						Ext.getCmp(PF + 'cmbBeneficiario').setVisible(true);
						Ext.getCmp(PF + 'btnComboBenef').setVisible(true);
						
						NS.agregarCriterioValor ('BENEFICIARIO', Ext.getCmp(PF + 'cmbBeneficiario').getId(), 'criterio');
					}
					else if(combo.getValue() == 3)
					{
						//CAJA
						NS.storeCajas.load();
						NS.mostrarOcultar(false);
						Ext.getCmp(PF + 'cmbCajas').setDisabled(false);
						Ext.getCmp(PF + 'cmbCajas').setVisible(true);
						
						NS.agregarCriterioValor('CAJA', Ext.getCmp(PF + 'cmbCajas').getId(), 'criterio');
								
					}
					else if(combo.getValue()== 4)
					{
						//CHEQUERA
						NS.storeChequeras.baseParams.iIdEmpresa = parseInt( Ext.getCmp(PF + 'txtNoEmpresa').getValue());
						NS.storeChequeras.load();
						NS.mostrarOcultar(false);
						Ext.getCmp(PF + 'cmbChequeras').setDisabled(false);
						Ext.getCmp(PF + 'cmbChequeras').setVisible(true);
						
						NS.agregarCriterioValor ('CHEQUERA', Ext.getCmp(PF + 'cmbChequeras').getId(), 'criterio');
					}
					else if(combo.getValue()==5)
					{
						//CONCEPTO
						NS.mostrarOcultar(false);
						Ext.getCmp(PF + 'txtConcepto').setDisabled(false);
						Ext.getCmp(PF + 'txtConcepto').setVisible(true);
						
						NS.agregarCriterioValor('CONCEPTO', Ext.getCmp(PF + 'txtConcepto').getId(), 'criterio');
						
					}	
					else if(combo.getValue() == 6)
					{
						//DIVISA
						NS.storeDivisas.load();
						NS.mostrarOcultar(false);
						Ext.getCmp(PF + 'cmbDivisas').setDisabled(false);
						Ext.getCmp(PF + 'cmbDivisas').setVisible(true);
									
						NS.agregarCriterioValor ('DIVISA', Ext.getCmp(PF + 'cmbDivisas').getId(), 'criterio');
					}
					else if(combo.getValue() == 7)
					{
						//DIVISION
						NS.storeDivisiones.load();
						NS.mostrarOcultar(false);
						Ext.getCmp(PF + 'cmbDivisiones').setDisabled(false);
						Ext.getCmp(PF + 'cmbDivisiones').setVisible(true);
						
						NS.agregarCriterioValor ('DIVISION', Ext.getCmp(PF + 'cmbDivisiones').getId(), 'criterio');
					}
					else if(combo.getValue() == 8)
					{
						//ESTATUS
						NS.storeEstatus.load();
						NS.mostrarOcultar(false);
						Ext.getCmp(PF + 'cmbEstatus').setDisabled(false);
						Ext.getCmp(PF + 'cmbEstatus').setVisible(true);
						
						NS.agregarCriterioValor ('ESTATUS', Ext.getCmp(PF + 'cmbEstatus').getId(), 'criterio');
					}
					else if(combo.getValue() == 9)
					{
						//FOLIO MOVIMIENTO
						NS.mostrarOcultar(false);
						Ext.getCmp(PF + 'txtFolioMovi').setDisabled(false);
						Ext.getCmp(PF + 'txtFolioMovi').setVisible(true);
						
						NS.agregarCriterioValor ('FOLIO MOVIMIENTO', Ext.getCmp(PF + 'txtFolioMovi').getId(), 'criterio');
					}
					else if(combo.getValue() == 10)
					{
						//FORMA DE PAGO
						NS.storeFormaPago.load();
						NS.mostrarOcultar(false);
						Ext.getCmp(PF + 'cmbFormaPago').setDisabled(false);
						Ext.getCmp(PF + 'cmbFormaPago').setVisible(true);
						
						NS.agregarCriterioValor ('FORMA DE PAGO', Ext.getCmp(PF + 'cmbFormaPago').getId(), 'criterio');
					}
					else if(combo.getValue() == 11)
					{
						//CVE PROPUESTA
						NS.mostrarOcultar(false);
						Ext.getCmp(PF + 'txtCvePropuesta').setDisabled(false);
						Ext.getCmp(PF + 'txtCvePropuesta').setVisible(true);
						
						 NS.agregarCriterioValor ('CVE PROPUESTA', Ext.getCmp(PF + 'txtCvePropuesta').getId(), 'criterio');
					}
					else if(combo.getValue() == 12)
					{
						//MONTOS
						NS.mostrarOcultar(false);
						Ext.getCmp(PF + 'txtMontoIni').setDisabled(false);
						Ext.getCmp(PF + 'txtMontoIni').setVisible(true);
						Ext.getCmp(PF + 'txtMontoFin').setDisabled(false);
						Ext.getCmp(PF + 'txtMontoFin').setVisible(true);
						
						NS.agregarCriterioValor ('MONTOS', Ext.getCmp(PF + 'txtMontoIni').getId(), 'criterio');
					}
					else if(combo.getValue() == 13)
					{
						//NO. CHEQUE
						NS.mostrarOcultar(false);
						Ext.getCmp(PF + 'txtNoCheque').setDisabled(false);
						Ext.getCmp(PF + 'txtNoCheque').setVisible(true);
						
						 NS.agregarCriterioValor ('NO. CHEQUE', Ext.getCmp(PF + 'txtNoCheque').getId(), 'criterio');
					}
					else if(combo.getValue() == 14)
					{
						//NO. DOCUMENTO
						NS.mostrarOcultar(false);
						Ext.getCmp(PF + 'txtNoDocto').setDisabled(false);
						Ext.getCmp(PF + 'txtNoDocto').setVisible(true);
						
						NS.agregarCriterioValor ('NO. DOCUMENTO', Ext.getCmp(PF + 'txtNoDocto').getId(), 'criterio');
					}
					else if(combo.getValue() == 15)
					{
						//NO. FACTURA
						NS.mostrarOcultar(false);
						Ext.getCmp(PF + 'txtNoFactura').setDisabled(false);
						Ext.getCmp(PF + 'txtNoFactura').setVisible(true);
						
						NS.agregarCriterioValor ('NO. FACTURA', Ext.getCmp(PF + 'txtNoFactura').getId(), 'criterio');						
						 
					}
					else if(combo.getValue() == 16)
					{
						//ORIGEN
						NS.storeOrigen.load();
						NS.mostrarOcultar(false);
						Ext.getCmp(PF + 'cmbOrigen').setDisabled(false);
						Ext.getCmp(PF + 'cmbOrigen').setVisible(true);
						
						NS.agregarCriterioValor ('ORIGEN', Ext.getCmp(PF + 'cmbOrigen').getId(), 'criterio');
					}
					else if(combo.getValue() == 17)
					{
						//TIPO MOVIMIENTO
						NS.storeTipoMovimiento.load();
						NS.mostrarOcultar(false);
						Ext.getCmp(PF + 'cmbTipoMovto').setDisabled(false);
						Ext.getCmp(PF + 'cmbTipoMovto').setVisible(true);
						
						NS.agregarCriterioValor ('TIPO MOVIMIENTO', Ext.getCmp(PF + 'cmbTipoMovto').getId(), 'criterio');
					}
					else if(combo.getValue() == 18)
					{
						//VALOR CUSTODIA
						NS.storeValorCustodia.load();
						NS.mostrarOcultar(false);
						Ext.getCmp(PF + 'cmbValorCustodia').setDisabled(false);
						Ext.getCmp(PF + 'cmbValorCustodia').setVisible(true);
						NS.agregarCriterioValor ('VALOR CUSTODIA', Ext.getCmp(PF + 'cmbValorCustodia').getId(), 'criterio');
					}
					else if(combo.getValue()==19)
					{
						//FOLIO COMPENSACION
						NS.mostrarOcultar(false);
						Ext.getCmp(PF + 'txtFolioCompensacion').setDisabled(false);
						Ext.getCmp(PF + 'txtFolioCompensacion').setVisible(true);
						
						NS.agregarCriterioValor('FOLIOCOMPENSACION', Ext.getCmp(PF + 'txtFolioCompensacion').getId(), 'criterio');
						
					}
				}
			}
		}
	});//Termina cmbCriterio
	
	
	NS.agregarCriterioValor = function(sValor, oIdElemento, sTipoAgregado){
		
		var indice = 0;
		var i = 0;
		var recordsDatGrid = NS.storeDatos.data.items;
		var sValorAnterior = '';
		
		for(i = 0; i < recordsDatGrid.length; i = i + 1)
		{
			if(recordsDatGrid[i].get('criterio') == sValor)
			{
				indice = i;
				sValorAnterior = recordsDatGrid[i].get('valor');
			}
		}
		
		if(sTipoAgregado == 'criterio')
		{
			if(recordsDatGrid.length > 0)
			{
				if((indice > 0 || (recordsDatGrid[0].get('criterio') == sValor)) && sValorAnterior !== '')
				{
					Ext.Msg.alert('información SET','Ya indicó el criterio, necesita borralo antes');
					Ext.getCmp(oIdElemento).setDisabled(false);
					return;
				}
				else
					NS.agregarValorGridDatos(sValor, oIdElemento, 0, sTipoAgregado);
			}
			else
				NS.agregarValorGridDatos(sValor, oIdElemento, 0, sTipoAgregado);
		}
		else if(sTipoAgregado == 'valor')
		{	
			NS.storeDatos.remove(recordsDatGrid[indice]);
			NS.agregarValorGridDatos(sValor, oIdElemento, indice, sTipoAgregado, sValorAnterior);
		}
		
	};
	
	NS.agregarValorGridDatos = function(sValor, oIdElemento, indice, sTipoAgregado, sValorAnterior){
		var valorCombo = Ext.getCmp(oIdElemento).getValue();
		var tamGrid = indice <= 0 ? (NS.storeDatos.data.items).length : indice;
		var datosClase = NS.gridDatos.getStore().recordType;
		var valorAsignado = "";
		var valorAgregado = "";	
		
		if(sTipoAgregado == 'valor')
		{
			Ext.getCmp(PF + 'cmbCriterio').setDisabled(false);
			if (sValor == 'FECHA VALOR')
				Ext.getCmp(oIdElemento).setDisabled(false);
			else
				Ext.getCmp(oIdElemento).setDisabled(true);
		}
		
		if (sValor == 'FECHA VALOR'){
			valorAsignado =  BFwrk.Util.changeDateToString('' + Ext.getCmp(PF + 'txtFechaInicial').getValue()) + ' a ' + BFwrk.Util.changeDateToString('' + Ext.getCmp(PF + 'txtFechaFinal').getValue());
			valorAgregado = "";
		}
		else{
			valorAsignado = sValorAnterior !== undefined && sValorAnterior !== '' ? sValorAnterior + ' a ' : '';
			valorAgregado = sTipoAgregado == 'criterio' ? '' : $('input[id*="'+ oIdElemento +'"]').val()//sCadenaValor
		}
		
			var datos = new datosClase({
				id: valorCombo,
               	criterio: sValor,
				valor: valorAsignado   +  valorAgregado
           	});
            NS.gridDatos.stopEditing();
       		NS.storeDatos.insert(tamGrid, datos);
       		NS.gridDatos.getView().refresh();
       			
	};
	
	//Store para el combo de banco
	NS.storeBanco = new Ext.data.DirectStore({
		paramsAsHash: false,
		root: '',
		baseParams:{idEmpresa: 0},
		paramOrder:['idEmpresa'],
		directFn: ConsultasAction.llenarComboBanco, 
		idProperty: 'id', 
		fields: [
			{name: 'id'},
			{name: 'descripcion'}
		],
		listeners: {
			load: function(s, records){
				var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeBanco, msg:"Cargando..."});
				if(records.length == null || records.length<=0)
					Ext.Msg.alert('Información SET','No existen bancos');
			}
		}	
	}); 
	
	
	//combo para mostrar los bancos asignados a una empresa
	NS.cmbBanco = new Ext.form.ComboBox({
		store: NS.storeBanco,
		name: PF+'cmbBanco',
		id: PF+'cmbBanco',
		typeAhead: true,
		mode: 'local',
		minChars: 0,
		selecOnFocus: true,
		forceSelection: true,
        x: 50,
        y: 125,
        width: 190,
		valueField:'id',
		displayField:'descripcion',
		autocomplete: true,
		emptyText: 'Seleccione un banco',
		triggerAction: 'all',
		value: '',
		disabled: true,
		hidden: true,
		ctCls: 'ConsMoviCmbBanco',
		listeners:{
			select:{
				fn:function(combo, value) {
					NS.agregarCriterioValor('BANCO', combo.getId(), 'valor');
					NS.storeChequeras.baseParams.iIdBanco = combo.getValue();
				}
			}
		}
	});	//Termina cmbBanco
	 
	//Store para el combo de cajas
	NS.storeCajas = new Ext.data.DirectStore({
		paramsAsHash: false,
		root: '',
		directFn: ConsultasAction.llenarComboCajas, 
		idProperty: 'id', 
		fields: [
			{name: 'id'},
			{name: 'descripcion'}
		],
		listeners: {
			load: function(s, records){
				var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeCajas, msg:"Cargando..."});
				if(records.length==null || records.length<=0)
					Ext.Msg.alert('Información SET','No existen cajas asignadas al usuario');
					
			}
		}
	}); 
	
	
	//combo para mostrar los proveedores
	NS.cmbCajas = new Ext.form.ComboBox({
		store: NS.storeCajas,
		name: PF + 'cmbCajas',
		id: PF + 'cmbCajas',
		typeAhead: true,
		mode: 'local',
		minChars: 0,
		selecOnFocus: true,
		forceSelection: true,
        x: 50,
        y: 125,
        width: 190,
		valueField:'id',
		displayField:'descripcion',
		autocomplete: true,
		emptyText: 'Seleccione una caja',
		triggerAction: 'all',
		value: '',
		disabled: true,
		hidden: true,
		listeners:{
			select:{
				fn:function(combo, value) {
					NS.agregarCriterioValor('CAJA', combo.getId(), 'valor');
				}
			}
		}
	});	//Termina cmbCajas
	
	
	//store para el combo de chequeras
	NS.storeChequeras = new Ext.data.DirectStore({
		paramsAsHash: false,
		baseParams : {
			iIdBanco : 0,
			iIdEmpresa: 0
		},
		paramOrder : ['iIdBanco','iIdEmpresa'],
		root: '',
		directFn: ConsultasAction.llenarComboChequeras, 
		idProperty: 'descripcion', 
		fields: [
			{name: 'descripcion'},
			{name: 'descripcion'}
		],
		listeners: {
			load: function(s, records){
				var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeChequeras, msg:"Cargando..."});
				if(records.length==null || records.length<=0)
					Ext.Msg.alert('Información SET','No existen chequeras para este banco');
					
			}
		}
	}); 
	
	
	//combo para mostrar chequeras asignadas a un banco
	NS.cmbChequeras = new Ext.form.ComboBox({
		store: NS.storeChequeras,
		name: PF + 'cmbChequeras',
		id: PF + 'cmbChequeras',
		typeAhead: true,
		mode: 'local',
		minChars: 0,
		selecOnFocus: true,
		forceSelection: true,
        x: 50,
        y: 125,
        width: 190,
		valueField:'descripcion',
		displayField:'descripcion',
		autocomplete: true,
		emptyText: 'Seleccione una chequera',
		triggerAction: 'all',
		value: '',
		disabled: true,
		hidden: true,
		listeners:{
			select:{
				fn:function(combo, value) {
					NS.agregarCriterioValor('CHEQUERA', combo.getId(), 'valor');
				}
			}
		}
	});	//Termina cmbChequeras
	
	//store para el combo de divisas
	NS.storeDivisas = new Ext.data.DirectStore({
		paramsAsHash: false,
		baseParams : {
			sCondicion : ''
		},
		paramOrder : ['sCondicion'],
		root: '',
		directFn: GlobalAction.llenarComboDivisas, 
		idProperty: 'idDivisa', 
		fields: [
			{name: 'idDivisa'},
			{name: 'descDivisa'},
		],
		listeners: {
			load: function(s, records){
				var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeDivisas, msg:"Cargando..."});
				if(records.length==null || records.length<=0)
					Ext.Msg.alert('Información SET','No se encontraron divisas...');
					
			}
		}
	}); 
	
	
	//combo para mostrar las divisas 
	NS.cmbDivisas = new Ext.form.ComboBox({
		store: NS.storeDivisas,
		name: PF + 'cmbDivisas',
		id: PF + 'cmbDivisas',
		typeAhead: true,
		mode: 'local',
		minChars: 0,
		selecOnFocus: true,
		forceSelection: true,
        x: 50,
        y: 125,
        width: 190,
		valueField:'idDivisa',
		displayField:'descDivisa',
		autocomplete: true,
		emptyText: 'Seleccione una divisa',
		triggerAction: 'all',
		value: '',
		disabled: true,
		hidden: true,
		listeners:{
			select:{
				fn:function(combo, value) {
					NS.agregarCriterioValor('DIVISA', combo.getId(), 'valor');
				}
			}
		}
	});	//Termina cmbDivisas
	
	//store para el combo de divisiones
	NS.storeDivisiones = new Ext.data.DirectStore({
		paramsAsHash: false,
		baseParams : {
			bEmpActual : true
		},
		paramOrder : ['bEmpActual'],
		root: '',
		directFn: ConsultasAction.llenarComboDivisiones, 
		idProperty: 'id', 
		fields: [
			{name: 'id'},
			{name: 'descripcion'},
		],
		listeners: {
			load: function(s, records){
				var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeDivisiones, msg:"Cargando..."});
				if(records.length==null || records.length<=0)
					Ext.Msg.alert('Información SET','No se encontraron divisiones');
					
			}
		}
	}); 
	
	
	//combo para mostrar las divisas 
	NS.cmbDivisiones = new Ext.form.ComboBox({
		store: NS.storeDivisiones,
		name: PF + 'cmbDivisiones',
		id: PF + 'cmbDivisiones',
		typeAhead: true,
		mode: 'local',
		minChars: 0,
		selecOnFocus: true,
		forceSelection: true,
        x: 50,
        y: 125,
        width: 190,
		valueField:'id',
		displayField:'descripcion',
		autocomplete: true,
		emptyText: 'Seleccione una division',
		triggerAction: 'all',
		value: '',
		disabled: true,
		hidden: true,
		listeners:{
			select:{
				fn:function(combo, value) {
					NS.agregarCriterioValor('DIVISION', combo.getId(), 'valor');
				}
			}
		}
	});	//Termina cmbDivisiones
	
	//store para el combo de estatus
	NS.storeEstatus = new Ext.data.DirectStore({
		paramsAsHash: false,
		root: '',
		directFn: ConsultasAction.llenarComboEstatus, 
		idProperty: 'campoUno', 
		fields: [
			{name: 'campoUno'},
			{name: 'descripcion'},
		],
		listeners: {
			load: function(s, records){
				var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeEstatus, msg:"Cargando..."});
				if(records.length==null || records.length<=0)
					Ext.Msg.alert('Información SET','No se encontraron estatus de movimiento');
					
			}
		}
	}); 
	
	//combo para mostrar los estatus de los movimientos
	NS.cmbEstatus = new Ext.form.ComboBox({
		store: NS.storeEstatus,
		name: PF + 'cmbEstatus',
		id: PF + 'cmbEstatus',
		typeAhead: true,
		mode: 'local',
		minChars: 0,
		selecOnFocus: true,
		forceSelection: true,
        x: 50,
        y: 125,
        width: 190,
		valueField:'campoUno',
		displayField:'descripcion',
		autocomplete: true,
		emptyText: 'Seleccione un estatus',
		triggerAction: 'all',
		value: '',
		disabled: true,
		hidden: true,
		listeners:{
			select:{
				fn:function(combo, value) {
					NS.agregarCriterioValor('ESTATUS', combo.getId(), 'valor');
				}
			}
		}
	});	//Termina cmbEstatus
	
	//store para el combo forma de pago
	NS.storeFormaPago = new Ext.data.DirectStore({
		paramsAsHash: false,
		baseParams:{
			fieldOne: 'id_forma_pago',
			fieldTwo: 'desc_forma_pago',
			table: 'cat_forma_pago',
			con: '',
			order: '',
		},
		paramOrder:['fieldOne','fieldTwo','table','con','order'],
		root: '',
		directFn: GlobalAction.llenarComboGeneral, 
		idProperty: 'id', 
		fields: [
			{name: 'id'},
			{name: 'descripcion'},
		],
		listeners: {
			load: function(s, records){
				var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeFormaPago, msg:"Cargando..."});
				if(records.length==null || records.length<=0)
					Ext.Msg.alert('Información SET','No se encontraron formas de pago');
					
			}
		}
	}); 
	
	//combo para mostrar los tipos de forma de pago
	NS.cmbFormaPago = new Ext.form.ComboBox({
		store: NS.storeFormaPago,
		name: PF + 'cmbFormaPago',
		id: PF + 'cmbFormaPago',
		typeAhead: true,
		mode: 'local',
		minChars: 0,
		selecOnFocus: true,
		forceSelection: true,
        x: 50,
        y: 125,
        width: 190,
		valueField:'id',
		displayField:'descripcion',
		autocomplete: true,
		emptyText: 'Seleccione forma de pago',
		triggerAction: 'all',
		value: '',
		disabled: true,
		hidden: true,
		listeners:{
			select:{
				fn:function(combo, value) {
					NS.agregarCriterioValor('FORMA DE PAGO', combo.getId(), 'valor');
				}
			}
		}
	});	//Termina cmbFormaPago
	
	
	//store para el combo de origen
	NS.storeOrigen = new Ext.data.DirectStore({
		paramsAsHash: false,
		root: '',
		directFn: ConsultasAction.llenarComboOrigen, 
		idProperty: 'campoUno', 
		fields: [
			{name: 'campoUno'},
			{name: 'descripcion'},
		],
		listeners: {
			load: function(s, records){
				var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeOrigen, msg:"Cargando..."});
				if(records.length==null || records.length<=0)
					Ext.Msg.alert('Información SET','No se encontraron origenes');
					
			}
		}
	}); 
	
	//combo para mostrar los tipos de origen 
	NS.cmbOrigen = new Ext.form.ComboBox({
		store: NS.storeOrigen,
		name: PF + 'cmbOrigen',
		id: PF + 'cmbOrigen',
		typeAhead: true,
		mode: 'local',
		minChars: 0,
		selecOnFocus: true,
		forceSelection: true,
        x: 50,
        y: 125,
        width: 190,
		valueField:'campoUno',
		displayField:'descripcion',
		autocomplete: true,
		emptyText: 'Seleccione un origen',
		triggerAction: 'all',
		value: '',
		disabled: true,
		hidden: true,
		listeners:{
			select:{
				fn:function(combo, value) {
					NS.agregarCriterioValor('ORIGEN', combo.getId(), 'valor');
				}
			}
		}
	});	//Termina cmbOrigen
	
	//store para el combo tipo de movimiento
	NS.storeTipoMovimiento = new Ext.data.DirectStore({
		paramsAsHash: false,
		root: '',
		directFn: ConsultasAction.llenarComboTipoOperacion, 
		idProperty: 'id', 
		fields: [
			{name: 'id'},
			{name: 'descripcion'},
		],
		listeners: {
			load: function(s, records){
				var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeTipoMovimiento, msg:"Cargando..."});
				if(records.length==null || records.length<=0)
					Ext.Msg.alert('Información SET','No se encontraron tipos de operación');
			}
		}
	}); 
	
	//combo para mostrar los tipos de operacion 
	NS.cmbTipoMovto = new Ext.form.ComboBox({
		store: NS.storeTipoMovimiento,
		name: PF + 'cmbTipoMovto',
		id: PF + 'cmbTipoMovto',
		typeAhead: true,
		mode: 'local',
		minChars: 0,
		selecOnFocus: true,
		forceSelection: true,
        x: 50,
        y: 125,
        width: 190,
		valueField:'id',
		displayField:'descripcion',
		autocomplete: true,
		emptyText: 'Seleccione tipo movimiento',
		triggerAction: 'all',
		value: '',
		disabled: true,
		hidden: true,
		listeners:{
			select:{
				fn:function(combo, value) {
					NS.agregarCriterioValor('TIPO MOVIMIENTO', combo.getId(), 'valor');
				}
			}
		}
	});	//Termina cmbTipoMovto
	
	//store para el combo valor custodia
	NS.storeValorCustodia = new Ext.data.DirectStore({
		paramsAsHash: false,
		baseParams : {
			bEmpActual : false,
		},
		paramOrder : ['bEmpActual'],
		root: '',
		directFn: ConsultasAction.llenarComboValorCustodia, 
		idProperty: 'descripcion', 
		fields: [
			{name: 'descripcion'},
		],
		listeners: {
			load: function(s, records){
				var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeValorCustodia, msg:"Cargando..."});
				if(records.length==null || records.length<=0)
					Ext.Msg.alert('Información SET','No se encontraron datos para valor custodia');
					
			}
		}
	}); 
	
	//combo para mostrar las chequeras de valor custodia
	NS.cmbValorCustodia = new Ext.form.ComboBox({
		store: NS.storeValorCustodia,
		name: PF + 'cmbValorCustodia',
		id: PF + 'cmbValorCustodia',
		typeAhead: true,
		mode: 'local',
		minChars: 0,
		selecOnFocus: true,
		forceSelection: true,
        x: 50,
        y: 125,
        width: 190,
		valueField:'descripcion',
		displayField:'descripcion',
		autocomplete: true,
		emptyText: 'Seleccione valor custodia',
		triggerAction: 'all',
		value: '',
		disabled: true,
		hidden: true,
		listeners:{
			select:{
				fn:function(combo, value) {
					NS.agregarCriterioValor('VALOR CUSTODIA', combo.getId(), 'valor');
				}
			}
		}
	});	//Termina cmbValorCustodia
	
	//Este combo carga los datos dependiendo la(s) letra(s) que se tecleen en el combo y se da clik en el boton
	//store  Beneficiario 
    NS.storeBeneficiario = new Ext.data.DirectStore({
		paramsAsHash: false,
		baseParams:{
			prefijoProv : '',
			iNoProv : 0,
			noEmpresa: 0
		},
		root: '',
		paramOrder:['prefijoProv','iNoProv', 'noEmpresa'],
		root: '',
		directFn: ConsultasAction.llenarComboProveedores, 
		idProperty: 'id', 
		fields: [
			 {name: 'id'},
			 {name: 'descripcion'},
		],
		listeners: {
			load: function(s, records){
				var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeBeneficiario, msg:"Cargando..."});
				if(records.length==null || records.length<=0)
				{
					Ext.Msg.alert('SET','No Existen Beneficiarios con ese nombre');
				}
			}
		}
	}); 
	
	//combo para el beneficiario
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
		,hideTrigger: true
        ,pageSize: 10
      	,x: 50
        ,y: 125
        ,width: 170
        ,tabIndex: 4
		,valueField:'id'
		,displayField:'descripcion'
		,autocomplete: true
		,emptyText: 'Escriba prefijo...'
		,triggerAction: 'all'
		,value: ''
		,hidden: true
		,editable: true
		,listeners:{
			select:{
				fn:function(combo, valor) {
					var id = combo.getValue();
					sBeneficiario = NS.storeBeneficiario.getById(id).get('descripcion');
				 	NS.agregarCriterioValor('BENEFICIARIO', combo.getId(), 'valor');
				}
			},
				beforequery: function(qe){
					NS.parametroBus = qe.combo.getRawValue();
				}
		}
	});
	
	
	NS.limpiarParamsGrid = function()
	{
		NS.storeConsultaMovimientos.baseParams.iIdBanco = 0;
		NS.storeConsultaMovimientos.baseParams.sBeneficiario = '';
		NS.storeConsultaMovimientos.baseParams.iIdBeneficiario = 0;
		NS.storeConsultaMovimientos.baseParams.iIdCaja = 0;
	    NS.storeConsultaMovimientos.baseParams.sIdChequera = '';
		NS.storeConsultaMovimientos.baseParams.sConcepto = '';
		NS.storeConsultaMovimientos.baseParams.sIdDivisa = '';
		NS.storeConsultaMovimientos.baseParams.iIdDivision = 0;
		NS.storeConsultaMovimientos.baseParams.sEstatus= '';
		NS.storeConsultaMovimientos.baseParams.iFolMovi = 0;
		NS.storeConsultaMovimientos.baseParams.iIdFormaPago = 0;
		NS.storeConsultaMovimientos.baseParams.sCvePropuesta= '';
		NS.storeConsultaMovimientos.baseParams.uMontoIni = 0;
		NS.storeConsultaMovimientos.baseParams.uMontoFin = 0;
		NS.storeConsultaMovimientos.baseParams.iNoCheque = 0;
		NS.storeConsultaMovimientos.baseParams.sNoDocto = '';
		NS.storeConsultaMovimientos.baseParams.iNoFactura = 0;
		NS.storeConsultaMovimientos.baseParams.sOrigen = '';
		NS.storeConsultaMovimientos.baseParams.iTipoOperacion= 0;
		NS.storeConsultaMovimientos.baseParams.sValorCustodia = '';
		NS.storeConsultaMovimientos.baseParams.sFecIni = '';
		NS.storeConsultaMovimientos.baseParams.sFecFin = '';
		NS.storeConsultaMovimientos.baseParams.noEmpresa = 0
		//NS.storeConsultaMovimientos.baseParams.bOptEmp = true;
		//NS.storeConsultaMovimientos.baseParams.bOptTipoMovto = true;
		//NS.storeConsultaMovimientos.baseParams.bSolCan = false;
	};
	//Store para llenar el gridConsultaMoviemientos
  	 NS.storeConsultaMovimientos = new Ext.data.DirectStore({
  	 	paramsAsHash: false,
  	 	baseParams: {
			iIdBanco : 0,
			iIdBeneficiario : 0,
			sBeneficiario: '',
			iIdCaja : 0,
			sIdChequera : '',
			sConcepto : '',
			sIdDivisa : '',
			iIdDivision : 0,
			sEstatus: '',
			iFolMovi : 0,
			iIdFormaPago : 0,
			sCvePropuesta: '',
			uMontoIni : 0,
			uMontoFin : 0,
			iNoCheque : 0,
			sNoDocto : '',
			iNoFactura : 0,
			sOrigen : '',
			iTipoOperacion: 0,
			sValorCustodia : '',
			sFecIni : '',
			sFecFin : '',
			bOptEmp : true,
			noEmpresa: 0,
			bOptTipoMovto : true,
			bSolCan : false,
			pooHeaders: '',
		},
		root: '',
		paramOrder:[
					'iIdBanco',
					'iIdBeneficiario',
					'sBeneficiario',
					'iIdCaja',
					'sIdChequera',
					'sConcepto',
					'sIdDivisa',
					'iIdDivision',
					'sEstatus',
					'iFolMovi',
					'iIdFormaPago',
					'sCvePropuesta',
					'uMontoIni',
					'uMontoFin',
					'iNoCheque',
					'sNoDocto',
					'iNoFactura',
					'sOrigen',
					'iTipoOperacion',
					'sValorCustodia',
					'sFecIni',
					'sFecFin',
					'bOptEmp',
					'noEmpresa',
					'bOptTipoMovto',
					'bSolCan',
					'pooHeaders'
			],
		directFn: ConsultasAction.consultarMovimientos,
		fields:
		[
			{name : 'importeOriginal'},
			{name : 'idTipoMovto'},
			{name : 'idEstatusCb'},
			{name : 'noFolioDet'},
			{name : 'tipoCambio'},
			{name : 'noCuenta'},
			{name : 'noDocto'},
			{name : 'noEmpresa'}, 
			{name : 'idEstatusMov'},
			{name : 'noFolioMov'},
			{name : 'fecValor'},
			{name : 'fecValorOriginal'},
			{name : 'fecAlta'},
			{name : 'importe'},
			{name : 'idDivisa'},
			{name : 'division'},
			{name : 'referencia'},
			{name : 'idFormaPago'},
			{name : 'poHeaders'},
			{name : 'noCheque'},
			{name : 'idBanco'},
			{name : 'idBancoBenef'},
			{name : 'idChequeraBenef'},
			{name : 'beneficiario'},
			{name : 'idChequera'},
			{name : 'importeDesglosado'},
			{name : 'folioRef'},
			{name : 'valorTasa'},
			{name : 'loteEntrada'},
			{name : 'grupoPago'},
			{name : 'loteSalida'},
			{name : 'idTipoDocto'},
			{name : 'concepto'},
			{name : 'idCaja'},
			{name : 'idLeyenda'},
			{name : 'fecRecalculo'},
			{name : 'noCliente'},
			{name : 'origenMov'},
			{name : 'solicita'},
			{name : 'autoriza'},
			{name : 'observacion'},
			{name : 'bSalvoBuenCobro'},
			{name : 'fecConfTrans'},
			{name : 'bEntregado'},
			{name : 'fecEntregado'},
			
			{name : 'descBancoBenef'},
			//desc_tipo_operacion
			{name : 'idTipoOperacion'},
			{name : 'idCveOperacion'},
			{name : 'descCveOperacion'},
			{name : 'descFormaPago'},
			{name : 'descCaja'},
			{name : 'descEstatus'},
			{name : 'noFactura'},
			{name : 'tipoCancelacion'},
			{name : 'nomEmpresa'},
			{name : 'codBloqueo'},
			{name : 'origenMov'},
			{name : 'cveControl'},
			{name : 'origen'},
			{name : 'cPeriodo'},
			{name : 'poHeaders'}, 
			
			{name: 'descBanco'},
			{name: 'idRubro'},
			{name: 'idGrupo'},
			{name: 'descRubro'},
			{name: 'descGrupo'},
			{name: 'fecExportacion'},
			
			{name: 'equivalePersona'},
			{name: 'nomProveedor'},
			{name: 'clabe'},
			
			{name: 'fecPropuestaStr'},
			{name: 'peridoDescompensar'},
			{name: 'fechaContabilizacion'},
			{name: 'comentario2'}
		],
		listeners: {
			load: function(s, records){
				
				var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeEnvioTransGrid, msg:"Buscando..."});
				NS.myMask.hide();
				Ext.getCmp(PF + 'btnBuscar').setDisabled(false);
				if(records.length==null || records.length<=0)
				{
					/*Ext.getCmp(PF + 'cmdGrafica1').setDisabled(true);
					Ext.getCmp(PF + 'cmdGrafica2').setDisabled(true);
					Ext.getCmp(PF + 'cmdGrafica3').setDisabled(true);
					Ext.getCmp(PF + 'cmdGrafica4').setDisabled(true);
 					*/
					//Ext.getCmp('panel1').update('<img src="'+window.location.protocol+'//'+window.location.host+'/graficaSet/back.png" border="0"/>');
					//Ext.getCmp('panel2').update('<img src="'+window.location.protocol+'//'+window.location.host+'/graficaSet/back.png" border="0"/>');
					//Ext.getCmp('panel3').update('<img src="'+window.location.protocol+'//'+window.location.host+'/graficaSet/back.png" border="0"/>');
					Ext.Msg.alert('Información  SET','No se encontraron datos con los parametros de búsqueda');
					return;
				}
				else
				{
					Ext.getCmp(PF + 'txtTotalReg').setValue(records.length);
					Ext.getCmp(PF + 'btnExportar').setDisabled(false);
					Ext.getCmp('BeCbtnEliminar').setDisabled(false);
					Ext.getCmp('BeCbtnRegresar').setDisabled(false);
					Ext.getCmp(PF + 'btnComprobante').setDisabled(false);
	    			Ext.getCmp(PF + 'btnImprimir').setDisabled(false);
    			//	Ext.getCmp(PF+'cmbGraficas').setDisabled(false);
	    			
	    			//Graficas
	    			var noEmp = NS.txtNoEmpresa.getValue()
/*	    			if(noEmp==0)
	    			{
	    				Ext.getCmp(PF + 'cmdGrafica1').setDisabled(false);
	    			}
	    			else
	    			{
	    				Ext.getCmp(PF + 'cmdGrafica1').setDisabled(true);
	    			}
    				Ext.getCmp(PF + 'cmdGrafica2').setDisabled(false);
    				Ext.getCmp(PF + 'cmdGrafica3').setDisabled(false);
    				Ext.getCmp(PF + 'cmdGrafica4').setDisabled(false);
*/    				
    				var sAux1="", sAux2="";
					sAux1 = NS.sFecIni.replace("/", "");
					sAux1 = sAux1.replace("/", "");
					sAux2 = NS.sFecFin.replace("/", "");
					sAux2 = sAux2.replace("/", "");
    				sName = noEmp+""+NS.iIdBanco + NS.sOrigen+sAux1+sAux2;
    				
				}
			}
		}
	}); 

  	 //Store para grafica total I/E
  	/* NS.storeGraficaMovimientos = new Ext.data.DirectStore({
  	 	paramsAsHash: false,
  	 	baseParams: {
			noEmpresa: 0,
			sFecIni : '',
			sFecFin : ''
		},
		root: '',
		paramOrder:[
					'noEmpresa',
					'sFecIni',
					'sFecFin'
			],
		directFn: ConsultasAction.graficarTotalMovimientos,
		fields: 
		[
		],
		listeners: {
			load: function(s, records){
				
				var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeGraficaMovimientos, msg:"Graficando..."});
				
				if(records.length==null || records.length<=0)
				{
					Ext.Msg.alert('Información  SET','No se encontraron datos a graficar con los parametros de búsqueda');
					return;
				}
				else
				{
					var sAux1 = NS.sFecIni.replace("/", "");
					sAux1 = sAux1.replace("/", "");
					var sAux2 = NS.sFecFin.replace("/", "");
					sAux2 = sAux2.replace("/", "");
					
					var sNameT = Ext.getCmp(PF + 'txtNoEmpresa').getValue()+""+sAux1+sAux2;
					
					Ext.getCmp('panel3').show();
					Ext.getCmp('panel2').show();
					Ext.getCmp('panel1').show();
                	Ext.getCmp('panel1').update('<img src="'+window.location.protocol+"//"+window.location.host+"/graficaSet/"+apps.SET.iUserId+'MovsTotales'+sNameT+'PC.jpg" border="0"/>');
		        	Ext.getCmp('panel2').update('<img src="'+window.location.protocol+'//'+window.location.host+'/graficaSet/'+apps.SET.iUserId+'MovsTotales'+sNameT+'BG.jpg" border="0"/>');
		        	Ext.getCmp('panel3').update('<img src="'+window.location.protocol+'//'+window.location.host+'/graficaSet/'+apps.SET.iUserId+'MovsTotales'+sNameT+'LG.jpg" border="0"/>');
					Ext.Msg.alert('SET', 'Graficas listas');
				}
			}
		}
	});*/

	//Grid principal donde se mostraran los registros de movimiento
	NS.gridConsultaMovimientos = new Ext.grid.GridPanel({
		id: PF + 'gridConsultaMovimientos',
		name: PF + 'gridConsultaMovimientos',
        store : NS.storeConsultaMovimientos,
        cm: new Ext.grid.ColumnModel({
        	defaults: 
	        	{
		            width: 120,
		            value:true,
		            sortable: true
	       		},
            columns: [
                NS.sm,
                {
					header: 'No. Factura', 
					width: 80, 
					dataIndex: 'noFactura'
				},{
               		header: 'No. Docto.', 
               		width: 80, 
               		dataIndex: 'noDocto',
               		hidden: true
              	},
				{
					header: 'Importe', 
					width: 100, 
					dataIndex: 'importe',
					css: 'text-align:right;',
					renderer: BFwrk.Util.rendererMoney
				},
				{
					header: 'Divisa', 
					width: 50, 
					dataIndex: 'idDivisa'
				},
				{
					header: 'Tipo Mov.', 
					width: 55, 
					dataIndex: 'idTipoMovto',
					hidden: true
				},
				{
					header: 'Estatus', 
					width: 80, 
					dataIndex: 'descEstatus'
				},
				{
					header: 'Fecha Venc.', 
					width: 100, 
					dataIndex: 'fecValor'
				},
				{
					header: 'Fecha Original', 
					width: 100, 
					dataIndex: 'fecValorOriginal',
					hidden: true
				},
				{
					 header: 'Fecha Contabilización',
					 width:100,
					 dataIndex:'fechaContabilizacion'
				},
				{
					header: 'No. Proveedor', 
					width: 100, 
					dataIndex: 'noCliente',
					hidden: true
				},
				{
					header: 'Beneficiario', 
					width: 200, 
					dataIndex: 'beneficiario'
				},
				{
					header: 'Concepto', 
					width: 200, 
					dataIndex: 'concepto'
				},
				{
					header: 'Cve. operación', 
					width: 100, 
					dataIndex: 'descCveOperacion'
				},
				{
					header: 'Banco', 
					width: 50, 
					dataIndex: 'idBanco',
					hidden: true
				},
				{ //14
					 header: 'Desc Banco',
					 width:50,
					 dataIndex:'descBanco',
					 hidden: true
				 },
				{
					header: 'Chequera', 
					width: 100, 
					dataIndex: 'idChequera'
				},
				{	
					header: 'Desc banco Benef', 
					width: 100, 
					dataIndex: 'descBancoBenef'
				},
				{
					header: 'Chequera Benef', 
					width: 100, 
					dataIndex: 'idChequeraBenef'
				},{
					header: 'Forma de Pago', 
					width: 100, 
					dataIndex: 'descFormaPago'
				},{
					header: 'Caja', 
					width: 100, 
					dataIndex: 'descCaja',
					hidden: true
				},{
					header: 'Folio', 
					width: 80, 
					dataIndex: 'noFolioDet'
				},{
					header: 'Origen', 
					width: 80, 
					dataIndex: 'origen',
					hidden: true
				},{
					header: 'Tipo operación', 
					width: 120, 
					dataIndex: 'idTipoOperacion',
					hidden: true
				},{
					header: 'Referencia', 
					width: 100, 
					dataIndex: 'referencia'
				},{
					header: 'No. Cheque', 
					width: 100, 
					dataIndex: 'noCheque'
				},{
					header: 'Fecha confirmación', 
					width: 100, 
					dataIndex: 'fecConfTrans'
				},{
					header: 'Entregado', 
					width: 100, 
					dataIndex: 'bEntregado',
					hidden: true
				},{
					header: 'Tipo de Cancelaci\u00f3n', 
					width: 100, 
					dataIndex: 'tipoCancelacion',
					hidden: true
				},{
					header: 'Fec. entregado', 
					width: 100, 
					dataIndex: 'fecEntregado',
					hidden: true
				},{
					header: 'Origen', 
					width: 100, 
					dataIndex: 'origenMov'
				},{
					header: 'No. Empresa', 
					width: 100, 
					dataIndex: 'noEmpresa',
					hidden: true
				},{
					header: 'Empresa', 
					width: 150, 
					dataIndex: 'nomEmpresa'
				},{
					header: 'Id Estatus Cb', 
					width: 100, 
					dataIndex: 'idEstatusCb',
					hidden : true
				},{
					header: 'Subtotal Divisa', 
					width: 100, 
					dataIndex: '',
					hidden: true
				},{
					header: 'Divisa', 
					width: 100, 
					dataIndex: 'idDivisa',
					hidden: true
				},{
					header: 'Divisi\u00f3n', 
					width: 100, 
					dataIndex: 'division',
					hidden: true
				},{
					header: 'Importe Original', 
					width: 100, 
					dataIndex: 'importeOriginal',
					css: 'text-align:right;',
					renderer: BFwrk.Util.rendererMoney
				},{
					header: 'Tipo de Pago', 
					width: 100, 
					dataIndex: 'poHeaders',
					hidden: true
				},{
					header: 'Fecha Alta', 
					width: 100, 
					dataIndex: 'fecAlta',
					hidden: true
				},{
					header: 'Cve. Control', 
					width: 100, 
					dataIndex: 'cveControl'
				},{ // 40
					header: 'Bloqueado', 
					width: 100, 
					dataIndex: 'codBloqueo',
					hidden: true
				},{
					header: 'IdEstatusMov', 
					width: 100, 
					dataIndex: 'idEstatusMov',
					hidden : true
				},{
					header: 'Lote Entrada', 
					width: 100, 
					dataIndex: 'loteEntrada',
					hidden : true
				},{
					header: 'Lote Salida', 
					width: 100, 
					dataIndex: 'loteSalida',
					hidden : true
				},{
					header: 'Id Banco', 
					width: 100, 
					dataIndex: 'idBanco',
					hidden : true
				},{
					header: 'Id Banco Benef', 
					width: 100, 
					dataIndex: 'idBancoBenef',
					hidden : true
				},{
					header: 'Cve. operación', 
					width: 100, 
					dataIndex: 'idCveOperacion',
					hidden : true
				},{
					header: 'No. Cuenta', 
					width: 100, 
					dataIndex: 'noCuenta',
					hidden : true
				},{
					header: 'Id Forma de Pago', 
					width: 100, 
					dataIndex: 'idFormaPago',
					hidden : true
				},{
					header: 'Tipo Cambio', 
					width: 100, 
					dataIndex: 'tipoCambio',
					hidden : true
				},{
					header: 'Origen', 
					width: 100, 
					dataIndex: 'origenMov',
					hidden : true
				},{
					header: 'Solicita', 
					width: 100, 
					dataIndex: 'solicita',
					hidden : true
				},{
					header: 'Autoriza', 
					width: 100, 
					dataIndex: 'autoriza',
					hidden : true
				},{
					header: 'Observación', 
					width: 100, 
					dataIndex: 'observacion',
					hidden : true
				},{
					header: 'SB Cobro', 
					width: 100, 
					dataIndex: 'bSalvoBuenCobro',
					hidden : true
				},{
					header: 'Estatus', 
					width: 100, 
					dataIndex: 'idEstatusCb',
					hidden : true
				},{
					header: 'Importe Desglosado', 
					width: 100,
					css: 'text-align:right;',
					dataIndex: 'importeDesglosado',
					hidden : true
				},{
					header: 'Folio Ref', 
					width: 100, 
					dataIndex: 'folioRef',
					hidden : true
				},{
					header: 'Id Caja', 
					width: 100, 
					dataIndex: 'idCaja',
					hidden : true
				},
				{
					header: 'Periodo', 
					width: 100, 
					dataIndex: 'cPeriodo',
					hidden : true
				},
				{
					header: 'Poliza SAP', 
					width: 100, 
					dataIndex: 'poHeaders',
					hidden : true
				}
				,{ //61
					 header: 'Id grupo',
					 width:50,
					 dataIndex:'idGrupo',
					 hidden: true
				 },
				 {
					 header: 'Desc grupo',
					 width:50,
					 dataIndex:'descGrupo',
					 hidden: true
				 },
				 {
					 header: 'Id rubro',
					 width:50,
					 dataIndex:'idRubro',
					 hidden: true
				 },
				 {
					 header: 'Desc rubro',
					 width:50,
					 dataIndex:'descRubro',
					 hidden: true
				 },
				 {
					 header: 'Comentario 2',
					 width:150,
					 dataIndex:'comentario2',
					 hidden: false
				 }
            ]
        })
        ,viewConfig: 
        {
        	getRowClass: function(record, rowIndex){
        		if(record.get('idEstatusMov') === 'N'
        			&& record.get('idTipoOperacion') === 3000)
        		{
        			return 'row-font-color-red';
        		}
        	}
        },
        sm: NS.sm,
        columnLines: true,
        x:0,
        y:0,
        width:1000,
        height:210,
       // frame:true,
        listeners:{
        	click:{
        		fn:function(e){
       				NS.dato.setValue(NS.gridConsultaMovimientos.getSelectionModel().getSelections()[0].get('noFolioDet'));
        			//Cambios Prueba edgar
        			
		        //	var regSelec = NS.gridConsultaMovimientos.getSelectionModel().getSelections();	
					
		        	//if(regSelec.length > 0) {
			        	//if(regSelec[0].get('idFormaPago') == 3) { //se elimina la condicion de forma de pago
							//Ext.getCmp(PF + 'btnRegresar').setDisabled(false);
						//}else{
						//	Ext.getCmp(PF + 'btnRegresar').setDisabled(true);
						//}
		        	//}
        		}
        	}
        }
    });
    
	//Funciona para graficar totales de Ingresos/Egresos
	/*NS.graficaEspecial = function(){
		var datosBusqueda = NS.storeDatos.data.items;
		
		if(Ext.getCmp(PF + 'txtNoEmpresa').getValue()!="")
    	{
    		if (Ext.getCmp(PF + 'txtNoEmpresa').getValue() !== 0)
    			NS.storeGraficaMovimientos.baseParams.noEmpresa = parseInt(Ext.getCmp(PF + 'txtNoEmpresa').getValue());
    		else
    			NS.storeGraficaMovimientos.baseParams.noEmpresa = 0;
    		
    		for(var i=0; i<datosBusqueda.length; i=i+1)
    		{
	    		if(datosBusqueda[i].get('criterio') == 'FECHA VALOR')
				{
					if(Ext.getCmp(PF + 'txtFechaInicial').getValue() == '' || Ext.getCmp(PF + 'txtFechaFinal').getValue() == '')
					{
						BFwrk.Util.msgShow('Las fechas son necesarias', 'INFO');
						return;
					}
					NS.storeGraficaMovimientos.baseParams.sFecIni = BFwrk.Util.changeDateToString('' + Ext.getCmp(PF + 'txtFechaInicial').getValue());
					NS.storeGraficaMovimientos.baseParams.sFecFin = BFwrk.Util.changeDateToString('' + Ext.getCmp(PF + 'txtFechaFinal').getValue());
					NS.sFecIni = BFwrk.Util.changeDateToString('' + Ext.getCmp(PF + 'txtFechaInicial').getValue());
					NS.sFecFin = BFwrk.Util.changeDateToString('' + Ext.getCmp(PF + 'txtFechaFinal').getValue());    				
				}
    		}
    		NS.storeGraficaMovimientos.load();
    	}
		else
		{
			BFwrk.Util.msgShow('Debe seleccionar una empresa o todas y un rango de fechas', 'INFO');
		}
	};*/
	
    //Funcion para realizar la busqueda de movimientos
    NS.buscar = function(){
    	var datosBusqueda = NS.storeDatos.data.items;
    	NS.limpiarParamsGrid();
    	NS.myMask.show();
		Ext.getCmp(PF + 'btnBuscar').setDisabled(true);
		var contCriterio=0;
		
    	if(datosBusqueda.length>0 && Ext.getCmp(PF + 'txtNoEmpresa').getValue()!="")
    	{
    		if (Ext.getCmp(PF + 'txtNoEmpresa').getValue() !== 0)
    			NS.storeConsultaMovimientos.baseParams.noEmpresa = parseInt(Ext.getCmp(PF + 'txtNoEmpresa').getValue());
    		else
    			NS.storeConsultaMovimientos.baseParams.noEmpresa = 0;
    		
    		for(var i = 0; i < datosBusqueda.length; i = i + 1)
    		{
    			if(datosBusqueda[i].get('criterio') == 'FECHA VALOR')
    			{
    				if(Ext.getCmp(PF + 'txtFechaInicial').getValue() == '' || Ext.getCmp(PF + 'txtFechaFinal').getValue() == '')
    				{
    					BFwrk.Util.msgShow('Las fechas son necesarias', 'INFO');
    					NS.myMask.hide();
    					Ext.getCmp(PF + 'btnBuscar').setDisabled(false);
    					return;
    				}
    				NS.storeConsultaMovimientos.baseParams.sFecIni = BFwrk.Util.changeDateToString('' + Ext.getCmp(PF + 'txtFechaInicial').getValue());
    				NS.storeConsultaMovimientos.baseParams.sFecFin = BFwrk.Util.changeDateToString('' + Ext.getCmp(PF + 'txtFechaFinal').getValue());
    				NS.sFecIni = BFwrk.Util.changeDateToString('' + Ext.getCmp(PF + 'txtFechaInicial').getValue());
    				NS.sFecFin = BFwrk.Util.changeDateToString('' + Ext.getCmp(PF + 'txtFechaFinal').getValue());    				
    			}
    			else if(datosBusqueda[i].get('criterio') == 'BANCO')
    			{    			
    				if(datosBusqueda[i].get('id') !== '')
    				{
    					NS.storeConsultaMovimientos.baseParams.iIdBanco = parseInt(datosBusqueda[i].get('id'));
    					NS.iIdBanco = parseInt(datosBusqueda[i].get('id'));
    					contCriterio++;
    				}
    				else
    				{
    					BFwrk.Util.msgShow('Debe dar un valor a BANCO', 'INFO');
    					NS.myMask.hide();
    					Ext.getCmp(PF + 'btnBuscar').setDisabled(false);
    					return;
    				}
    			}
    			/*else if(datosBusqueda[i].get('criterio') == 'BENEFICIARIO')
    			{
    				if(datosBusqueda[i].get('id') !== '')
    				{
    					NS.storeConsultaMovimientos.baseParams.sBeneficiario = sBeneficiario;
						NS.storeConsultaMovimientos.baseParams.iIdBeneficiario = parseInt(datosBusqueda[i].get('id'));
						NS.iIdBeneficiario = parseInt(datosBusqueda[i].get('id'));
    				}
    				else
    				{
						BFwrk.Util.msgShow('Debe dar un valor a BENEFICIARIO', 'INFO');    				
    					Ext.getCmp(PF + 'btnBuscar').setDisabled(false);
    					return;
    				}
    			}*/
    			/*else if(datosBusqueda[i].get('criterio') == 'CAJA'){
    				if(datosBusqueda[i].get('id') !== '')
    				{
						NS.storeConsultaMovimientos.baseParams.iIdCaja = parseInt(datosBusqueda[i].get('id'));
						NS.iIdCaja = parseInt(datosBusqueda[i].get('id'));
    				}
    				else
    				{
						BFwrk.Util.msgShow('Debe dar un valor a CAJA', 'INFO');
						Ext.getCmp(PF + 'btnBuscar').setDisabled(false);    					
    					return;
    				}
    			}*/
    			else if(datosBusqueda[i].get('criterio') == 'CHEQUERA')
    			{
    				if(datosBusqueda[i].get('id') !== '')
    				{
						NS.storeConsultaMovimientos.baseParams.sIdChequera = datosBusqueda[i].get('id');
						NS.sIdChequera = datosBusqueda[i].get('id');  
						contCriterio++;
    				}
    				else
    				{
    					BFwrk.Util.msgShow('Debe dar un valor a CHEQUERA', 'INFO');
    					NS.myMask.hide();
    					Ext.getCmp(PF + 'btnBuscar').setDisabled(false);
    					return;
    				}
    			}
    			else if(datosBusqueda[i].get('criterio') == 'CONCEPTO')
    			{
    				if(datosBusqueda[i].get('id') !== '')
    				{
						NS.storeConsultaMovimientos.baseParams.sConcepto = datosBusqueda[i].get('id');
						NS.sConcepto = datosBusqueda[i].get('id'); 
						contCriterio++;
    				}
    				else
    				{
    					BFwrk.Util.msgShow('Debe dar un valor a CONCEPTO', 'INFO');
    					NS.myMask.hide();
    					Ext.getCmp(PF + 'btnBuscar').setDisabled(false);
    					return;
    				}
    			}
    			else if(datosBusqueda[i].get('criterio') == 'DIVISA')
    			{
    				if(datosBusqueda[i].get('id') !== '')
    				{
    					NS.storeConsultaMovimientos.baseParams.sIdDivisa = datosBusqueda[i].get('id');
    					NS.sIdDivisa = datosBusqueda[i].get('id');
    					contCriterio++;
    				}
    				else
    				{
    					BFwrk.Util.msgShow('Debe dar un valor a DIVISA', 'INFO');
    					NS.myMask.hide();
    					Ext.getCmp(PF + 'btnBuscar').setDisabled(false);		
			    		return;
    				}
    			}
    			/*else if(datosBusqueda[i].get('criterio') == 'DIVISION')
    			{
    				if(datosBusqueda[i].get('id') !== '')
    				{
						NS.storeConsultaMovimientos.baseParams.iIdDivision = parseInt(datosBusqueda[i].get('id'));
						NS.iIdDivision = parseInt(datosBusqueda[i].get('id'));    				
    				}
    				else
    				{
						BFwrk.Util.msgShow('Debe dar un valor a DIVISION', 'INFO');
						Ext.getCmp(PF + 'btnBuscar').setDisabled(false);
						return;    				
    				}
    			}*/
    			else if(datosBusqueda[i].get('criterio') == 'ESTATUS')
    			{
    				if(datosBusqueda[i].get('id') !== '')
    				{
						NS.storeConsultaMovimientos.baseParams.sEstatus = datosBusqueda[i].get('id');
						NS.sEstatus = datosBusqueda[i].get('id');  
						contCriterio++;
    				}
    				else
    				{
						BFwrk.Util.msgShow('Debe dar un valor a ESTATUS', 'INFO');   
						NS.myMask.hide();
						Ext.getCmp(PF + 'btnBuscar').setDisabled(false);
						return; 				
    				}
    			}
    			/*else if(datosBusqueda[i].get('criterio') == 'FOLIO MOVIMIENTO')
    			{
    				if(datosBusqueda[i].get('id') !== '')
    				{
						NS.storeConsultaMovimientos.baseParams.iFolMovi = parseInt(datosBusqueda[i].get('id'));
						NS.iFolMovi = parseInt(datosBusqueda[i].get('id'));    				
    				}
    				else
    				{
						BFwrk.Util.msgShow('Debe dar un valor a FOLIO MOVIMIENTO', 'INFO');    
						Ext.getCmp(PF + 'btnBuscar').setDisabled(false);
						return;				
    				}
    			}*/
    			else if(datosBusqueda[i].get('criterio') == 'FORMA DE PAGO')
    			{
    				if(datosBusqueda[i].get('id') !== '')
    				{
						NS.storeConsultaMovimientos.baseParams.iIdFormaPago = parseInt(datosBusqueda[i].get('id'));
						NS.iIdFormaPago = parseInt(datosBusqueda[i].get('id')); 
						contCriterio++;
    				}
    				else
    				{
						BFwrk.Util.msgShow('Debe dar un valor a FORMA DE PAGO', 'INFO');
						NS.myMask.hide();
						Ext.getCmp(PF + 'btnBuscar').setDisabled(false);
						return;    				
    				}
    			}
    			else if(datosBusqueda[i].get('criterio') == 'CVE PROPUESTA')
    			{
    				if(datosBusqueda[i].get('id') !== '')
    				{
						NS.storeConsultaMovimientos.baseParams.sCvePropuesta = datosBusqueda[i].get('id');
						NS.sCvePropuesta = datosBusqueda[i].get('id');  
						contCriterio++;
    				}
    				else
    				{
						BFwrk.Util.msgShow('Debe dar un valor a CVE PROPUESTA', 'INFO');
						NS.myMask.hide();
						Ext.getCmp(PF + 'btnBuscar').setDisabled(false);
						return;    				
    				}
    			}
    			else if(datosBusqueda[i].get('criterio') == 'MONTOS')
    			{
    				if(datosBusqueda[i].get('id') !== '')
    				{
    					var fAux = Ext.getCmp(PF + 'txtMontoIni').getValue();
    					if(fAux.indexOf(",")!=-1) fAux = fAux.replace(/,/g, '');
    					
    					NS.storeConsultaMovimientos.baseParams.uMontoIni = parseFloat(fAux);
    					NS.uMontoIni = parseFloat(fAux);
    					
    					fAux = Ext.getCmp(PF + 'txtMontoFin').getValue();
    					if(fAux.indexOf(",")!=-1) fAux = fAux.replace(/,/g, '');
    					
    					NS.storeConsultaMovimientos.baseParams.uMontoFin = parseFloat(fAux);
    					NS.uMontoFin = parseFloat(fAux);
    					
    					contCriterio++;
    				}
    				else
    				{
    					BFwrk.Util.msgShow('Debe dar un valor a MONTOS', 'INFO');
    					NS.myMask.hide();
    					Ext.getCmp(PF + 'btnBuscar').setDisabled(false);
    					return;
    				}
    			}
    			else if(datosBusqueda[i].get('criterio') == 'NO. CHEQUE')
    			{
    				if(datosBusqueda[i].get('id') !== '')
    				{
    					NS.storeConsultaMovimientos.baseParams.iNoCheque = parseInt(datosBusqueda[i].get('id'));
    					NS.iNoCheque = parseInt(datosBusqueda[i].get('id'));
    					contCriterio++;
    				}
    				else
    				{
    					BFwrk.Util.msgShow('Debe dar un valor a NO. CHEQUE', 'INFO');
    					NS.myMask.hide();
    					Ext.getCmp(PF + 'btnBuscar').setDisabled(false);
    					return;
    				}
    			}
    			else if(datosBusqueda[i].get('criterio') == 'NO. DOCUMENTO')
    			{
    				if(datosBusqueda[i].get('id') !== '')
    				{
    					NS.storeConsultaMovimientos.baseParams.sNoDocto = datosBusqueda[i].get('id');
    					NS.sNoDocto = datosBusqueda[i].get('id');
    					contCriterio++;
    				}
    				else
    				{
    					BFwrk.Util.msgShow('Debe dar un valor a NO. DOCUMENTO', 'INFO');
    					NS.myMask.hide();
    					Ext.getCmp(PF + 'btnBuscar').setDisabled(false);
    				
    					return;
    				}
    			}
    			else if(datosBusqueda[i].get('criterio') == 'NO. FACTURA')
    			{
    				if(datosBusqueda[i].get('id') !== '')
    				{
    					NS.storeConsultaMovimientos.baseParams.iNoFactura = parseInt(datosBusqueda[i].get('id'));
    					NS.iNoFactura = parseInt(datosBusqueda[i].get('id'));
    					contCriterio++;
    				}
    				else
    				{
    					BFwrk.Util.msgShow('Debe dar un valor a NO. FACTURA', 'INFO');
    					NS.myMask.hide();
    					Ext.getCmp(PF + 'btnBuscar').setDisabled(false);
    					return;
    				}
    			}
    			else if(datosBusqueda[i].get('criterio') == 'ORIGEN')
    			{
    				if(datosBusqueda[i].get('id') !== '')
    				{
    					NS.storeConsultaMovimientos.baseParams.sOrigen = datosBusqueda[i].get('id');
    					NS.sOrigen = datosBusqueda[i].get('id');
    					contCriterio++;
    				}
    				else
    				{
    					BFwrk.Util.msgShow('Debe dar un valor a ORIGEN', 'INFO');
    					NS.myMask.hide();
    					Ext.getCmp(PF + 'btnBuscar').setDisabled(false);
    					return;
    				}
    			}
    			/*else if(datosBusqueda[i].get('criterio') == 'TIPO MOVIMIENTO')
    			{
    				if(datosBusqueda[i].get('id') !== '')
    				{
    					NS.storeConsultaMovimientos.baseParams.iTipoOperacion = parseInt(datosBusqueda[i].get('id'));
    					NS.iTipoOperacion = parseInt(datosBusqueda[i].get('id'));
    				}
    				else
    				{
    					BFwrk.Util.msgShow('Debe dar un valor a TIPO MOVIMIENTO', 'INFO');
    					Ext.getCmp(PF + 'btnBuscar').setDisabled(false);
    					return;
    				}
    			}*/
    			else if(datosBusqueda[i].get('criterio') == 'VALOR CUSTODIA')
    			{
    				if(datosBusqueda[i].get('id') !== '')
    				{	    					
    					NS.storeConsultaMovimientos.baseParams.sValorCustodia = datosBusqueda[i].get('id');
    					NS.sValorCustodia = datosBusqueda[i].get('id');
    					contCriterio++;
    				}
    				else
    				{
    					BFwrk.Util.msgShow('Debe dar un valor a VALOR CUSTODIA', 'INFO');
    					NS.myMask.hide();
    					Ext.getCmp(PF + 'btnBuscar').setDisabled(false);
    					return;
    				}
    			}
    			else if(datosBusqueda[i].get('criterio') == 'FOLIOCOMPENSACION')
    			{
    				if(datosBusqueda[i].get('id') !== '')
    				{
						NS.storeConsultaMovimientos.baseParams.pooHeaders = datosBusqueda[i].get('id');
						NS.pooHeaders=  datosBusqueda[i].get('id');
						contCriterio++;
    				}
    				else
    				{
    					BFwrk.Util.msgShow('Debe ingresar un folio', 'INFO');
    					NS.myMask.hide();
    					Ext.getCmp(PF + 'btnBuscar').setDisabled(false);
    					return;
    				}
    			}
    		}//Termina For
    		
    		if(contCriterio !=0 )
    			NS.storeConsultaMovimientos.load();
    		else{
    			BFwrk.Util.msgShow('Debe seleccionar por lo menos un criterio de busqueda.  ', 'INFO');
    			Ext.getCmp(PF + 'btnBuscar').setDisabled(false);
				NS.myMask.hide();
				return;
    		}	
    	}
    	else
    	{
    		BFwrk.Util.msgShow('Debe agregar datos para la búsqueda', 'INFO');
    		NS.myMask.hide();
    		Ext.getCmp(PF + 'btnBuscar').setDisabled(false);
    	}
    };
    
    /*
     * FUNCIONES DESCOMPENSAR
     */
    NS.validar3200 = function(){
	    ConsultasAction.validar3200(NS.resultado,function(result, e){
			  if(result == '' || result == '<br>'){
		    	Ext.Msg.confirm( 'SET', 'Algunos registros seleccionados seran alterados, desea continuar. ' , function(btn){
					if(btn=='yes'){
						NS.regresarMovto();
					}
				 }); 
			  }else{
				  Ext.Msg.alert('SET', result);
			  }
			  NS.resultado="";
		});
	};	
	
    NS.validarDescompensar = function(){
    	var mensaje="";
    	var registrosSeleccionados = NS.gridConsultaMovimientos.getSelectionModel().getSelections();
		
    	//Validar si esta seleccionado minimo un registro
		if(registrosSeleccionados.length <= 0){
			mensaje= 'Debe seleccionar minimo un registro';
			return mensaje;
		}
		//Validar si selecciono un motivo
		if(NS.cmbMotivos.getRawValue()==''){
			mensaje= 'Debe seleccionar un motivo';
			return mensaje;
		}	
		
		//Validar condiciones para la descompensacion
		
		var patEstatusCb = /A|M|C/;
		var patEstatusMovtoCancel = /X|Y|Z|Q/; 
		
		for(var i = 0; i < registrosSeleccionados.length; i = i +1){
			j = i;
			if (registrosSeleccionados[i].get('idTipoOperacion') != 3000
				&& registrosSeleccionados[i].get('idTipoOperacion') != 1018 
				&& registrosSeleccionados[i].get('idTipoOperacion') != 3101 ){
				if (registrosSeleccionados[i].get('idEstatusMov') != 'P' && registrosSeleccionados[i].get('idFormaPago') != 5) 
					return 'Operación sin caso de cancelación , <br> En el registro ' + (j + 1);
			} else {
				if ((registrosSeleccionados[i].get('cveControl')!=null || registrosSeleccionados[i].get('cveControl')!='') && registrosSeleccionados[i].get('idTipoOperacion') != 3000){
					mensaje = "¡Operación sin caso de cancelación! <br> El movimiento " + j+1 + " se encuentra en la propuesta:  " + registrosSeleccionados[i].get('cveControl');
					return mensaje;
			    }
				if (registrosSeleccionados[i].get('idTipoOperacion') == 3101 || registrosSeleccionados[i].get('idTipoOperacion') == 1018){
					if(registrosSeleccionados[i].get('fecExportacion') == null || registrosSeleccionados[i].get('fecExportacion')== ''){
						if(registrosSeleccionados[i].get('idEstatusMov')!='A'){
							mensaje ='Operación sin caso de cancelación , <br> Error en el registro: '+ j+1 ;
							return mensaje;
						}
					}else{
						mensaje ='Operación sin caso de cancelación , <br> En el registro '+ j+1 ;
						return mensaje;
					}
				}
				
				//VERIFICA SI ESTA CONCILIADO
				if(patEstatusCb.test(registrosSeleccionados[i].get('idEstatusCb'))){
					mensaje ='No se puede cancelar un movimiento conciliado, <br> Error en el registro: '+ j+1 ;
					return mensaje;
				}
				
				//SI ES UN TRASPASO DE INGRESO SE CANCELA EN CONTROL DE ARCHIVOS
				if((registrosSeleccionados[i].get('idTipoOperacion') >= 3700 && registrosSeleccionados[i].get('idTipoOperacion') <= 3709)
					||(registrosSeleccionados[i].get('idTipoOperacion') >= 3800 && registrosSeleccionados[i].get('idTipoOperacion') <= 3818) 
					&& registrosSeleccionados[i].get('idTipoMovto') == 'I')
				{
					mensaje ='¡Los traspasos se cancelan en control de archivos (Banca Electrónica) !' ;
					return mensaje;													
				}																							

	            if(registrosSeleccionados[i].get('idTipoOperacion') == 3000 
	            	&& (registrosSeleccionados[i].get('idEstatusMov') == 'C' || registrosSeleccionados[i].get('idEstatusMov') == 'N')){
	            	if(registrosSeleccionados[i].get('cveControl') != null && registrosSeleccionados[i].get('cveControl') != ''){
	            		mensaje ='No puede cancelar la solicitud. <br> Error en el registro  ' + j+1;
						return mensaje;
	            	}
	            }
	           
	            if(registrosSeleccionados[i].get('idEstatusMov').match(patEstatusMovtoCancel)){
	            	mensaje ='No puede cancelar un movimiento cancelado. <br> Error en el registro ' + j+1;
					return mensaje;
	            }
	            
	            if(registrosSeleccionados[i].get('idEstatusMov') == 'Q'){
	           		mensaje ='No se puede cancelar una solicitud de traspaso aplicada. <br> Error en el registro: ' + j+1;
					return mensaje;
	            }
	            
	            if(registrosSeleccionados[i].get('idEstatusMov') == 'T'
	            	&& registrosSeleccionados[i].get('idFormaPago') == 3
	            	&& registrosSeleccionados[i].get('idTipoOperacion') == 3200){
            		mensaje ='¡Las transferencias se cancelan en control de archivos (Banca Electrónica) !. <br> Error en el registro: ' + j+1;
					return mensaje;
            	}
	            
            	if(registrosSeleccionados[i].get('idTipoOperacion') == 5000
            		|| registrosSeleccionados[i].get('idTipoOperacion') == 5100
            		|| registrosSeleccionados[i].get('idTipoOperacion') == 5104){
            		mensaje ='¡Los movimientos de la caja se cancelan en el arqueo de caja!. <br>  Error en el registro: ' + j+1;
					return mensaje;
            	}
			}
			return mensaje;
		}
   }
    /*
     * FUNCIONES ELIMINAR
     */
    
    NS.validarMovimientoEliminar = function(){
    	var mensaje="";
    	var registrosSeleccionados = NS.gridConsultaMovimientos.getSelectionModel().getSelections();
		
    	//VALIDAR QUE SE ENCUENTREN REGISTROS SELECCIONADOS
		if(registrosSeleccionados.length <= 0){
			mensaje="Debe seleccionar minimo un registro.";
			return mensaje;
		}
		
		//VALIDAR QUE SE ENCUENTRE UN MOTIVO SELECCIONADO
		if(NS.cmbMotivos.getRawValue()==''){
			
			mensaje= "Debe seleccionar un motivo";
			return mensaje;
		}
		
		var patEstatusCb = /A|M|C/;
		var patEstatusMovtoCancel = /X|Y|Z/; 
		//VALIDAR QUE LOS MOVIMIENTOS SE PUEDAN ELIMINAR
		for(var i = 0; i < registrosSeleccionados.length; i = i +1){
			j = i;
			if (registrosSeleccionados[i].get('idTipoOperacion') != 3000
				&& registrosSeleccionados[i].get('idTipoOperacion') != 1018 
				&& registrosSeleccionados[i].get('idTipoOperacion') != 3101 ){
			
				
				if (registrosSeleccionados[i].get('idEstatusMov') != 'P'  && registrosSeleccionados[i].get('idEstatusCb') != 'P' && registrosSeleccionados[i].get('idEstatusCb') != 'N') 
					return 'Operación sin caso de cancelación , <br> En el registro ***' + (j + 1);
			} else {
				if ((registrosSeleccionados[i].get('cveControl')!=null || registrosSeleccionados[i].get('cveControl')!='') && registrosSeleccionados[i].get('idTipoOperacion') != 3000){
					mensaje = "¡Operación sin caso de cancelación! <br> El movimiento " + j+1 + " se encuentra en la propuesta:  " + registrosSeleccionados[i].get('cveControl');
					return mensaje;
			    }
				if (registrosSeleccionados[i].get('idTipoOperacion') == 3101 || registrosSeleccionados[i].get('idTipoOperacion') == 1018){
					if(registrosSeleccionados[i].get('fecExportacion') == null || registrosSeleccionados[i].get('fecExportacion')== ''){
						if(registrosSeleccionados[i].get('idEstatusMov')!='A'){
							mensaje ='Operación sin caso de cancelación , <br> Error en el registro: '+ j+1 ;
							return mensaje;
						}
					}else{
						mensaje ='Operación sin caso de cancelación , <br> En el registro '+ j+1 ;
						return mensaje;
					}
				}
				
				//VERIFICA SI ESTA CONCILIADO
				if(patEstatusCb.test(registrosSeleccionados[i].get('idEstatusCb'))){
					mensaje ='No se puede cancelar un movimiento conciliado, <br> Error en el registro: '+ j+1 ;
					return mensaje;
				}
				//SI ES UN TRASPASO DE INGRESO SE CANCELA EN CONTROL DE ARCHIVOS
				if((registrosSeleccionados[i].get('idTipoOperacion') >= 3700 && registrosSeleccionados[i].get('idTipoOperacion') <= 3709)
					||(registrosSeleccionados[i].get('idTipoOperacion') >= 3800 && registrosSeleccionados[i].get('idTipoOperacion') <= 3818) 
					&& registrosSeleccionados[i].get('idTipoMovto') == 'I')
				{
					mensaje ='¡Los traspasos se cancelan en control de archivos (Banca Electrónica) !' ;
					return mensaje;													
				}																							

	            if(registrosSeleccionados[i].get('idTipoOperacion') == 3000 
	            	&& (registrosSeleccionados[i].get('idEstatusMov') == 'C' || registrosSeleccionados[i].get('idEstatusMov') == 'N')){
	            	if(registrosSeleccionados[i].get('cveControl') != null && registrosSeleccionados[i].get('cveControl') != ''){
	            		mensaje ='No puede cancelar la solicitud. <br> Error en el registro  ' + j+1;
						return mensaje;
	            	}
	            }
	           
	            if(registrosSeleccionados[i].get('idEstatusMov').match(patEstatusMovtoCancel)){
	            	mensaje ='No puede cancelar un movimiento cancelado. <br> Error en el registro ' + j+1;
					return mensaje;
	            }
	            
	            if(registrosSeleccionados[i].get('idEstatusMov') == 'Q'){
	           		mensaje ='No se puede cancelar una solicitud de traspaso aplicada. <br> Error en el registro: ' + j+1;
					return mensaje;
	            }
	            
	            if(registrosSeleccionados[i].get('idEstatusMov') == 'T'
	            	&& registrosSeleccionados[i].get('idFormaPago') == 3
	            	&& registrosSeleccionados[i].get('idTipoOperacion') == 3200){
            		mensaje ='¡Las transferencias se cancelan en control de archivos (Banca Electrónica) !. <br> Error en el registro: ' + j+1;
					return mensaje;
            	}
	            
            	if(registrosSeleccionados[i].get('idTipoOperacion') == 5000
            		|| registrosSeleccionados[i].get('idTipoOperacion') == 5100
            		|| registrosSeleccionados[i].get('idTipoOperacion') == 5104){
            		mensaje ='¡Los movimientos de la caja se cancelan en el arqueo de caja!. <br>  Error en el registro: ' + j+1;
					return mensaje;
            	}
			}
			return mensaje;
		}
 }
    
    NS.regresarMovto = function(){
    	Ext.MessageBox.show({
		       title : 'Información SET',
		       msg : 'Cancelando Documentos, espere por favor...',
		       width : 300,
		       wait : true,
		       progress:true,
		       waitConfig : {interval:200}
	   		});
    	var recGridConsMovi = NS.gridConsultaMovimientos.getSelectionModel().getSelections();
		var matRegMovto = new Array();
		var matBitaChe = new Array();
		var psRevividor = "R";
		for(var i = 0; i < recGridConsMovi.length; i = i + 1){	
			
			
			arrayMovto = {};
			arrayMovto.noFolioDet = recGridConsMovi[i].get('noFolioDet');
			arrayMovto.idTipoOperacion = recGridConsMovi[i].get('idTipoOperacion');
			arrayMovto.idEstatusMov = recGridConsMovi[i].get('idEstatusMov');
			arrayMovto.psOrigenMov = recGridConsMovi[i].get('origenMov');
			arrayMovto.idFormaPago = recGridConsMovi[i].get('idFormaPago');
			arrayMovto.bEntregado = recGridConsMovi[i].get('bEntregado');
			arrayMovto.idTipoMovto = recGridConsMovi[i].get('idTipoMovto');
			arrayMovto.importe = recGridConsMovi[i].get('importe');
			arrayMovto.noEmpresa = recGridConsMovi[i].get('noEmpresa');
			arrayMovto.noCuenta = recGridConsMovi[i].get('noCuenta');
			arrayMovto.idChequera = recGridConsMovi[i].get('idChequera');
			arrayMovto.idBanco = recGridConsMovi[i].get('idBanco');
			arrayMovto.noDocto = recGridConsMovi[i].get('noDocto');
			arrayMovto.lote = recGridConsMovi[i].get('loteEntrada');
			arrayMovto.bSalvoBuenCobro = recGridConsMovi[i].get('bSalvoBuenCobro');													
			arrayMovto.fecConfTrans = recGridConsMovi[i].get('fecConfTrans') == null ? '' : recGridConsMovi[i].get('fecConfTrans');
			arrayMovto.idDivisa = recGridConsMovi[i].get('idDivisa');
			arrayMovto.iLote = '0';
			arrayMovto.psRevividor = 'N';
			if(Ext.getCmp(PF + 'optEgreso').getValue()==true){
				arrayMovto.psTipoCancelacion = 'R';
			}else if(Ext.getCmp(PF + 'optIngreso').getValue()==true){
				arrayMovto.psTipoCancelacion = 'C';
			}
			arrayMovto.observacion = NS.cmbMotivos.getRawValue();
			NS.periodo = recGridConsMovi[i].get('fecValor');
			NS.periodo = NS.periodo.substring(6, NS.periodo.length-12);
			arrayMovto.periodo = NS.periodo;
			arrayMovto.poHeaders = recGridConsMovi[i].get('poHeaders');
			arrayMovto.beneficiario = recGridConsMovi[i].get('beneficiario');
			arrayMovto.peridoDescompensar = recGridConsMovi[i].get('peridoDescompensar');
			arrayMovto.fecPropuestaStr = recGridConsMovi[i].get('fecPropuestaStr');
			arrayMovto.origen = recGridConsMovi[i].get('origen');
			matRegMovto[i] = arrayMovto;
			
			//Datos para bitacora_cheques
			arrayBita = {};
			arrayBita.noFolioDet = recGridConsMovi[i].get('noFolioDet');
			arrayBita.noEmpresa = recGridConsMovi[i].get('noEmpresa');
			arrayBita.idBanco = recGridConsMovi[i].get('idBanco');
			arrayBita.idChequera = recGridConsMovi[i].get('idChequera');
			arrayBita.noCheque = recGridConsMovi[i].get('noCheque');
			arrayBita.idEstatus = 'C';
			arrayBita.causa = '12';
			
			matBitaChe[i] = arrayBita;
		 
		}
		var jsonRegMovto = Ext.util.JSON.encode(matRegMovto);
		var jsonBitaChe = Ext.util.JSON.encode(matBitaChe);
		
		ConsultasAction.ejecutarRevividor(jsonRegMovto, jsonBitaChe, 'ConsultaDeMovimientos', function(response, e){
			if(response != undefined && response != null && response != ''){
				//BFwrk.Util.msgShow('' + response.msgUsuario, 'INFO');
				Ext.Msg.alert('SET', response);
				NS.buscar();
				//NS.limpiar();
				//NS.valoresIniciales();
			}else
				Ext.Msg.alert('SET', response);
		});
		NS.cmbMotivos.reset();
		//NS.buscar();
		
	};
    
    NS.eliminarMovto = function(){
 Ext.MessageBox.show({
		       title : 'Información SET',
		       msg : 'Regresando Documentos, espere por favor...',
		       width : 300,
		       wait : true,
		       progress:true,
		       waitConfig : {interval:200}
	   		});
		var matRegMovto = new Array();
		var matBitaChe = new Array();
		var recGridConsMovi = NS.gridConsultaMovimientos.getSelectionModel().getSelections();
		for(var i = 0; i < recGridConsMovi.length; i = i +1){
			 //Datos para revividor
            arrayMovto = {};
			arrayMovto.noFolioDet = recGridConsMovi[i].get('noFolioDet');
			arrayMovto.idTipoOperacion = recGridConsMovi[i].get('idTipoOperacion');
			arrayMovto.idEstatusMov = recGridConsMovi[i].get('idEstatusMov');
			arrayMovto.psOrigenMov = recGridConsMovi[i].get('origenMov');
			arrayMovto.idFormaPago = recGridConsMovi[i].get('idFormaPago');
			arrayMovto.bEntregado = recGridConsMovi[i].get('bEntregado');
			arrayMovto.idTipoMovto = recGridConsMovi[i].get('idTipoMovto');
			arrayMovto.importe = recGridConsMovi[i].get('importe');
			arrayMovto.noEmpresa = recGridConsMovi[i].get('noEmpresa');
			arrayMovto.noCuenta = recGridConsMovi[i].get('noCuenta');
			arrayMovto.idChequera = recGridConsMovi[i].get('idChequera');
			arrayMovto.idBanco = recGridConsMovi[i].get('idBanco');
			arrayMovto.noDocto = recGridConsMovi[i].get('noDocto');
			arrayMovto.lote = recGridConsMovi[i].get('loteEntrada');
			arrayMovto.bSalvoBuenCobro = recGridConsMovi[i].get('bSalvoBuenCobro');													
			arrayMovto.fecConfTrans = recGridConsMovi[i].get('fecConfTrans') == null ? '' : recGridConsMovi[i].get('fecConfTrans'); 
			arrayMovto.idDivisa = recGridConsMovi[i].get('idDivisa');
			arrayMovto.iLote = '0';
			arrayMovto.psRevividor = 'N';
			arrayMovto.psTipoCancelacion = 'C';
			arrayMovto.noFolioRef = recGridConsMovi[i].get('folioRef');
			arrayMovto.observacion = NS.cmbMotivos.getRawValue();
			arrayMovto.periodo=recGridConsMovi[i].get('cPeriodo');
			arrayMovto.poHeaders = recGridConsMovi[i].get('poHeaders');
			arrayMovto.beneficiario = recGridConsMovi[i].get('beneficiario');
			arrayMovto.origen = recGridConsMovi[i].get('origen');
			matRegMovto[i] = arrayMovto;
			
			//Datos para bitacora_cheques
			arrayBita = {};
			arrayBita.noFolioDet = recGridConsMovi[i].get('noFolioDet');
			arrayBita.noEmpresa = recGridConsMovi[i].get('noEmpresa');
			arrayBita.idBanco = recGridConsMovi[i].get('idBanco');
			arrayBita.idChequera = recGridConsMovi[i].get('idChequera');
			arrayBita.noCheque = recGridConsMovi[i].get('noCheque');
			arrayBita.idEstatus = 'C';
			arrayBita.causa = '13';
			
			matBitaChe[i] = arrayBita;
				
		} //Fin del for  
			
		var jsonRegMovto = Ext.util.JSON.encode(matRegMovto);
		var jsonBitaChe = Ext.util.JSON.encode(matBitaChe);
			
		//ConsultasAction.cancelarMovimientos(jsonRegMovto, jsonBitaChe, 'ConsultaDeMovimientos', function(response, e){
		ConsultasAction.ejecutarRevividor(jsonRegMovto, jsonBitaChe, 'ConsultaDeMovimientos', function(response, e){
    		if(response != undefined && response != null && response != ''){
					Ext.Msg.alert('SET', response);
					//NS.limpiar();
					//NS.valoresIniciales();
					NS.buscar();
    		}else
				Ext.Msg.alert('SET', 'Ocurrio un error');		
			NS.cmbMotivos.reset();
		});
		
	}
    
    NS.exportarDatosExcel = function(){
    	var recordsMovimientos = NS.storeConsultaMovimientos.data.items;
    	var matrizDatos = new Array();
    	var iNoColumns = 0;
    	var c = 0;
    	
    	for(var i = 0; i < recordsMovimientos.length; i++) {
    		var array = {};
    		array.C0 = i < 0 ? NS.gridConsultaMovimientos.getColumnModel().getColumnHeader(1) : recordsMovimientos[i].get('noDocto');
    		array.C1 = i < 0 ? NS.gridConsultaMovimientos.getColumnModel().getColumnHeader(2) : recordsMovimientos[i].get('importe');
    		array.C2 = i < 0 ? NS.gridConsultaMovimientos.getColumnModel().getColumnHeader(3) : recordsMovimientos[i].get('idDivisa');
    		array.C3 = i < 0 ? NS.gridConsultaMovimientos.getColumnModel().getColumnHeader(4) : recordsMovimientos[i].get('idTipoMovto');
    		array.C4 = i < 0 ? NS.gridConsultaMovimientos.getColumnModel().getColumnHeader(5) : recordsMovimientos[i].get('descEstatus');
    		array.C5 = i < 0 ? NS.gridConsultaMovimientos.getColumnModel().getColumnHeader(6) : recordsMovimientos[i].get('fecValor');
    		array.C6 = i < 0 ? NS.gridConsultaMovimientos.getColumnModel().getColumnHeader(7) : recordsMovimientos[i].get('fecValorOriginal');
    		array.C7 = i < 0 ? NS.gridConsultaMovimientos.getColumnModel().getColumnHeader(8) : recordsMovimientos[i].get('noCliente');
    		array.C8 = i < 0 ? NS.gridConsultaMovimientos.getColumnModel().getColumnHeader(9) : recordsMovimientos[i].get('beneficiario');
    		array.C9 = i < 0 ? NS.gridConsultaMovimientos.getColumnModel().getColumnHeader(10) : recordsMovimientos[i].get('concepto');
    		array.C10 = i < 0 ? NS.gridConsultaMovimientos.getColumnModel().getColumnHeader(11) : recordsMovimientos[i].get('descCveOperacion');
    		array.C11 = i < 0 ? NS.gridConsultaMovimientos.getColumnModel().getColumnHeader(12) : recordsMovimientos[i].get('idBanco');
    		array.C12 = i < 0 ? NS.gridConsultaMovimientos.getColumnModel().getColumnHeader(13) : recordsMovimientos[i].get('idChequera');
    		array.C13 = i < 0 ? NS.gridConsultaMovimientos.getColumnModel().getColumnHeader(14) : recordsMovimientos[i].get('descBancoBenef');
    		array.C14 = i < 0 ? NS.gridConsultaMovimientos.getColumnModel().getColumnHeader(15) : recordsMovimientos[i].get('idChequeraBenef');
    		array.C15 = i < 0 ? NS.gridConsultaMovimientos.getColumnModel().getColumnHeader(16) : recordsMovimientos[i].get('descFormaPago');
    		array.C16 = i < 0 ? NS.gridConsultaMovimientos.getColumnModel().getColumnHeader(17) : recordsMovimientos[i].get('descCaja');
    		array.C17 = i < 0 ? NS.gridConsultaMovimientos.getColumnModel().getColumnHeader(18) : recordsMovimientos[i].get('noFolioDet');
    		array.C18 = i < 0 ? NS.gridConsultaMovimientos.getColumnModel().getColumnHeader(19) : recordsMovimientos[i].get('origen');
    		array.C19 = i < 0 ? NS.gridConsultaMovimientos.getColumnModel().getColumnHeader(20) : recordsMovimientos[i].get('idTipoOperacion');
    		array.C20 = i < 0 ? NS.gridConsultaMovimientos.getColumnModel().getColumnHeader(21) : recordsMovimientos[i].get('referencia');
    		array.C21 = i < 0 ? NS.gridConsultaMovimientos.getColumnModel().getColumnHeader(22) : recordsMovimientos[i].get('noCheque');
    		array.C22 = i < 0 ? NS.gridConsultaMovimientos.getColumnModel().getColumnHeader(23) : recordsMovimientos[i].get('fecConfTrans');
    		array.C23 = i < 0 ? NS.gridConsultaMovimientos.getColumnModel().getColumnHeader(24) : recordsMovimientos[i].get('bEntregado');
    		array.C24 = i < 0 ? NS.gridConsultaMovimientos.getColumnModel().getColumnHeader(25) : recordsMovimientos[i].get('tipoCancelacion');
    		array.C25 = i < 0 ? NS.gridConsultaMovimientos.getColumnModel().getColumnHeader(26) : recordsMovimientos[i].get('fecEntregado');
    		array.C26 = i < 0 ? NS.gridConsultaMovimientos.getColumnModel().getColumnHeader(27) : recordsMovimientos[i].get('origenMov');
    		array.C27 = i < 0 ? NS.gridConsultaMovimientos.getColumnModel().getColumnHeader(28) : recordsMovimientos[i].get('noEmpresa');
    		array.C28 = i < 0 ? NS.gridConsultaMovimientos.getColumnModel().getColumnHeader(29) : recordsMovimientos[i].get('nomEmpresa');
    		array.C29 = i < 0 ? NS.gridConsultaMovimientos.getColumnModel().getColumnHeader(30) : recordsMovimientos[i].get('idEstatusCb');
    		array.C30 = i < 0 ? NS.gridConsultaMovimientos.getColumnModel().getColumnHeader(31) : recordsMovimientos[i].get('noFactura');
    		//array.C31 = "Vac";//i < 0 ? NS.gridConsultaMovimientos.getColumnModel().getColumnHeader(32) : recordsMovimientos[i].get('');
    		array.C31 = i < 0 ? NS.gridConsultaMovimientos.getColumnModel().getColumnHeader(33) : recordsMovimientos[i].get('idDivisa'); //divisa
    		array.C32 = i < 0 ? NS.gridConsultaMovimientos.getColumnModel().getColumnHeader(34) : recordsMovimientos[i].get('division');
    		array.C33 = i < 0 ? NS.gridConsultaMovimientos.getColumnModel().getColumnHeader(35) : recordsMovimientos[i].get('importeOriginal');
    		//array.C35 = i < 0 ? NS.gridConsultaMovimientos.getColumnModel().getColumnHeader(36) : recordsMovimientos[i].get('poHeaders');
    		array.C34 = i < 0 ? NS.gridConsultaMovimientos.getColumnModel().getColumnHeader(37) : recordsMovimientos[i].get('fecAlta');
    		array.C35 = i < 0 ? NS.gridConsultaMovimientos.getColumnModel().getColumnHeader(38) : recordsMovimientos[i].get('cveControl');
    		array.C36 = i < 0 ? NS.gridConsultaMovimientos.getColumnModel().getColumnHeader(39) : recordsMovimientos[i].get('codBloqueo');
    		/*array.C55 = i < 0 ? NS.gridConsultaMovimientos.getColumnModel().getColumnHeader(56) : recordsMovimientos[i].get('idEstatusMov');
    		array.C15 = i < 0 ? NS.gridConsultaMovimientos.getColumnModel().getColumnHeader(16) : recordsMovimientos[i].get('loteEntrada');
    		array.C16 = i < 0 ? NS.gridConsultaMovimientos.getColumnModel().getColumnHeader(17) : recordsMovimientos[i].get('loteSalida');
    		array.C17 = i < 0 ? NS.gridConsultaMovimientos.getColumnModel().getColumnHeader(18) : recordsMovimientos[i].get('idBanco');
    		array.C18 = i < 0 ? NS.gridConsultaMovimientos.getColumnModel().getColumnHeader(19) : recordsMovimientos[i].get('idBancoBenef');
    		array.C19 = i < 0 ? NS.gridConsultaMovimientos.getColumnModel().getColumnHeader(20) : recordsMovimientos[i].get('idCveOperacion');
    		array.C20 = i < 0 ? NS.gridConsultaMovimientos.getColumnModel().getColumnHeader(21) : recordsMovimientos[i].get('noCuenta');
    		array.C21 = i < 0 ? NS.gridConsultaMovimientos.getColumnModel().getColumnHeader(22) : recordsMovimientos[i].get('idFormaPago');
    		array.C23 = i < 0 ? NS.gridConsultaMovimientos.getColumnModel().getColumnHeader(24) : recordsMovimientos[i].get('tipoCambio');
    		array.C24 = i < 0 ? NS.gridConsultaMovimientos.getColumnModel().getColumnHeader(25) : recordsMovimientos[i].get('origenMov');
    		array.C25 = i < 0 ? NS.gridConsultaMovimientos.getColumnModel().getColumnHeader(26) : recordsMovimientos[i].get('solicita');
    		array.C26 = i < 0 ? NS.gridConsultaMovimientos.getColumnModel().getColumnHeader(27) : recordsMovimientos[i].get('autoriza');
    		array.C27 = i < 0 ? NS.gridConsultaMovimientos.getColumnModel().getColumnHeader(28) : recordsMovimientos[i].get('observacion');
    		array.C28 = i < 0 ? NS.gridConsultaMovimientos.getColumnModel().getColumnHeader(29) : recordsMovimientos[i].get('bSalvoBuenCobro');
    		array.C29 = i < 0 ? NS.gridConsultaMovimientos.getColumnModel().getColumnHeader(30) : recordsMovimientos[i].get('idEstatusCb');
    		array.C31 = i < 0 ? NS.gridConsultaMovimientos.getColumnModel().getColumnHeader(32) : recordsMovimientos[i].get('importeDesglosado');
    		array.C32 = i < 0 ? NS.gridConsultaMovimientos.getColumnModel().getColumnHeader(33) : recordsMovimientos[i].get('folioRef');
    		array.C33 = i < 0 ? NS.gridConsultaMovimientos.getColumnModel().getColumnHeader(34) : recordsMovimientos[i].get('idCaja');*/
    		array.C37 = i < 0 ? NS.gridConsultaMovimientos.getColumnModel().getColumnHeader(61) : recordsMovimientos[i].get('idGrupo');
    		array.C38 = i < 0 ? NS.gridConsultaMovimientos.getColumnModel().getColumnHeader(63) : recordsMovimientos[i].get('idRubro');
    		array.C39 = i < 0 ? NS.gridConsultaMovimientos.getColumnModel().getColumnHeader(62) : recordsMovimientos[i].get('descGrupo');
    		array.C40 = i < 0 ? NS.gridConsultaMovimientos.getColumnModel().getColumnHeader(64) : recordsMovimientos[i].get('descRubro');
    		array.C41 = i < 0 ? NS.gridConsultaMovimientos.getColumnModel().getColumnHeader(14) : recordsMovimientos[i].get('descBanco');
    		array.C42 = i < 0 ? NS.gridConsultaMovimientos.getColumnModel().getColumnHeader(65) : recordsMovimientos[i].get('fechaContabilizacion');
    		
    		
    		matrizDatos[c] = array;
    		c++;
    	}
    	var jsonMoviExport = Ext.util.JSON.encode(matrizDatos);
    	iNoColumns = parseInt(NS.gridConsultaMovimientos.getColumnModel().getColumnCount()) - 21;
    	
    	ConsultasAction.exportarDatosExcel(jsonMoviExport, iNoColumns, "ConsultaDeMovimientos", function(res,e) {
    		if (res != null && res != undefined && res == "") {
				Ext.Msg.alert('SET', "Error al generar el archivo");
			} else {
				strParams = '?nomReporte=ConsultaDeMovimientos';
				strParams += '&'+'nomParam1=nomArchivo';
				strParams += '&'+'valParam1='+res;
				window.open("/SET/jsp/bfrmwrk/gnrators/abreExcel.jsp"+strParams);
			}
    	 });
    };
    
    //Declaracion de la funcion para el comprobante
	NS.imprimirComprobante = function(){
		var recordsGridConsMovto = NS.gridConsultaMovimientos.getSelectionModel().getSelections();
		var iTipoOperacion = recordsGridConsMovto[0].get('idTipoOperacion');
		var iFormaPago = recordsGridConsMovto[0].get('idFormaPago');
		var sEstatusMov = recordsGridConsMovto[0].get('idEstatusMov');
		var bEntregado = recordsGridConsMovto[0].get('bEntregado');
		
		if((iTipoOperacion == 3200) && ((iFormaPago == 3 && (sEstatusMov == 'K' || sEstatusMov == 'T'))
			|| (iFormaPago == 1 && (sEstatusMov == 'I' || sEstatusMov == 'R') && bEntregado == 'S')))
			
			//If (iTipoOperacion = 3200) And ((iFormaPago = 3 And (sStatusMov = "K" Or sStatusMov = "T")) 
			//Or (iFormaPago = 1 And (sStatusMov = "I" Or sStatusMov = "R") And sB_entregado = "S")) Then
		{
			var strParams = '';
			strParams = '?nomReporte=ComprobanteMovto';
			
			strParams += '&'+'nomParam1=beneficiario';
			strParams += '&'+'valParam1=' + recordsGridConsMovto[0].get('equivalePersona');
			strParams += '&'+'nomParam2=banco';
			strParams += '&'+'valParam2=' + recordsGridConsMovto[0].get('descBancoBenef');
			strParams += '&'+'nomParam3=chequera';
			strParams += '&'+'valParam3=' + recordsGridConsMovto[0].get('idChequeraBenef');
			strParams += '&'+'nomParam4=refBancaria';
			strParams += '&'+'valParam4=' + recordsGridConsMovto[0].get('noFolioDet');
			strParams += '&'+'nomParam5=folioBanco';
			if(recordsGridConsMovto[0].get('idFormaPago') == 1)
			{
				strParams += '&'+'valParam5=' + recordsGridConsMovto[0].get('noCheque');
			}
			else
			{
				if(recordsGridConsMovto[0].get('referencia') != '')
					strParams += '&'+'valParam5=' + recordsGridConsMovto[0].get('referencia');
				else
					strParams += '&'+'valParam5=' + recordsGridConsMovto[0].get('noFolioDet');
			}
			strParams += '&'+'nomParam6=formaPago';
			strParams += '&'+'valParam6=' + recordsGridConsMovto[0].get('descFormaPago');
			strParams += '&'+'nomParam7=divisa';
			strParams += '&'+'valParam7=' + recordsGridConsMovto[0].get('idDivisa');
			strParams += '&'+'nomParam8=importeOriginal';
			
			var importeOriginal=NS.cambiarFormato(recordsGridConsMovto[0].get('importeOriginal').toFixed(2), '$');
			strParams += '&'+'valParam8=' + importeOriginal ;
			strParams += '&'+'nomParam9=importePagado';
			//strParams += '&'+'valParam9=' + recordsGridConsMovto[0].get('importeOriginal');//Verificar
			strParams += '&'+'valParam9=' + importeOriginal;//Verificar
			strParams += '&'+'nomParam10=sucursal';
			strParams += '&'+'valParam10=' + '0';//Cambiar
			strParams += '&'+'nomParam11=noDocto';
			strParams += '&'+'valParam11=' + recordsGridConsMovto[0].get('noDocto')+' ';
			strParams += '&'+'nomParam12=fechaPago';
			strParams += '&'+'valParam12=' + recordsGridConsMovto[0].get('fecValor').substring(0,11);
			strParams += '&'+'nomParam13=fechaHoy';
			strParams += '&'+'valParam13=' + apps.SET.FEC_HOY;
			strParams += '&'+'nomParam14=hora';
			strParams += '&'+'valParam14=' + NS.sHora;
			strParams += '&'+'nomParam15=nom_benef';
			strParams += '&'+'valParam15=' + recordsGridConsMovto[0].get('nomProveedor');
			strParams += '&'+'nomParam16=clabe';
			strParams += '&'+'valParam16='  + recordsGridConsMovto[0].get('clabe');
			window.open("/SET/jsp/bfrmwrk/gnrators/genJasperPDF.jsp" + strParams);
		}
		else
		{
			if(iFormaPago == 1)
				BFwrk.Util.msgShow('El cheque no ha sido impreso', 'INFO');
			else if(iFormaPago == 3)
				BFwrk.Util.msgShow('La transferencia no ha sido confirmada', 'INFO');
		}
	};
	//Termina declaracion imprimir
	
	//Función para la llamada al reporte de movimientos
	NS.imprimirReporte = function(){
		var sFechaHoy = '';
		var strParams = '';
		strParams = '?nomReporte=ReporteConsultaMovimientos';
		
		sFechaHoy = apps.SET.FEC_HOY;
		 
		strParams += '&'+'nomParam1=iIdBanco';
		strParams += '&'+'valParam1=' + NS.iIdBanco;
		strParams += '&'+'nomParam2=iIdBeneficiario';
		strParams += '&'+'valParam2=' + NS.iIdBeneficiario;
		strParams += '&'+'nomParam3=iIdCaja';
		strParams += '&'+'valParam3=' + NS.iIdCaja;
		strParams += '&'+'nomParam4=sIdChequera';
		strParams += '&'+'valParam4=' + NS.sIdChequera;
		strParams += '&'+'nomParam5=sConcepto';
		strParams += '&'+'valParam5=' + NS.sConcepto;
		strParams += '&'+'nomParam6=sIdDivisa';
		strParams += '&'+'valParam6=' + NS.sIdDivisa;
		strParams += '&'+'nomParam7=iIdDivision';
		strParams += '&'+'valParam7=' + NS.iIdDivision;
		strParams += '&'+'nomParam8=sEstatus';
		strParams += '&'+'valParam8=' + NS.sEstatus;
		strParams += '&'+'nomParam9=iFolMovi';
		strParams += '&'+'valParam9=' + NS.iFolMovi;
		strParams += '&'+'nomParam10=iIdFormaPago';
		strParams += '&'+'valParam10=' + NS.iIdFormaPago;
		strParams += '&'+'nomParam11=sCvePropuesta';
		strParams += '&'+'valParam11=' + NS.sCvePropuesta;
		strParams += '&'+'nomParam12=uMontoIni';
		strParams += '&'+'valParam12=' + NS.uMontoIni;
		strParams += '&'+'nomParam13=uMontoFin';
		strParams += '&'+'valParam13=' + NS.uMontoFin;
		strParams += '&'+'nomParam14=iNoCheque';
		strParams += '&'+'valParam14=' + NS.iNoCheque;
		strParams += '&'+'nomParam15=sNoDocto';
		strParams += '&'+'valParam15=' + NS.sNoDocto;
		strParams += '&'+'nomParam16=iNoFactura';
		strParams += '&'+'valParam16=' + NS.iNoFactura;
		strParams += '&'+'nomParam17=sOrigen';
		strParams += '&'+'valParam17=' + NS.sOrigen;
		strParams += '&'+'nomParam18=iTipoOperacion';
		strParams += '&'+'valParam18=' + NS.iTipoOperacion;
		strParams += '&'+'nomParam19=sValorCustodia';
		strParams += '&'+'valParam19=' + NS.sValorCustodia;
		strParams += '&'+'nomParam20=sFecIni';
		strParams += '&'+'valParam20=' + NS.sFecIni;
		strParams += '&'+'nomParam21=sFecFin';
		strParams += '&'+'valParam21=' + NS.sFecFin;
		strParams += '&'+'nomParam22=bOptEmp';
		strParams += '&'+'valParam22=' + NS.bOptEmp;
		strParams += '&'+'nomParam23=bOptTipoMovto';
		strParams += '&'+'valParam23=' + NS.bOptTipoMovto;
		strParams += '&'+'nomParam24=bSolCan';
		strParams += '&'+'valParam24=' + NS.bSolCan;
		//Parametros extra para el reporte
		strParams += '&'+'nomParam25=fechaHoy';
		strParams += '&'+'valParam25=' + sFechaHoy.substring(0,2) + ' DE ' + BFwrk.Util.getNameMonth(parseInt(sFechaHoy.substring(3,5))) + ' DE ' + sFechaHoy.substring(6,10);
		strParams += '&'+'nomParam26=nomEmpresa';
		strParams += '&'+'valParam26=' + NS.nomEmpresa;
		strParams += '&'+'nomParam27=noEmpresa';
		strParams += '&'+'valParam27=' + NS.noEmpresa;
		//strParams += '&'+'valParam27=' + NS.noEmpresa;
		
		window.open("/SET/jsp/bfrmwrk/gnrators/genJasperPDF.jsp" + strParams);
	};
	
	////////////////////////////////////////////////////////////////////////////////////////////
	//SE AGREGA EL COMBO DE EMPRESA 
	NS.lblNoEmpresaImportar = new Ext.form.Label({
		text: 'Empresa:',
		x: 0,
		y: 0
	});
	
	NS.txtNoEmpresaImportar = new Ext.form.TextField({
		id: PF + 'txtNoEmpresaImportar',
        name:PF + 'txtNoEmpresaImportar',
        x: 0,
		y: 15,
		width: 50,
        tabIndex: 0,
        value:'',
        listeners: {
			change: {
				fn: function(caja,valor) {
					if (caja.getValue() !== '' && caja.getValue() !== undefined && caja.getValue() !== null){
						BFwrk.Util.updateTextFieldToCombo(PF + 'txtNoEmpresaImportar', NS.cmbEmpresaImportar.getId());
						idEmpresa = parseInt(caja.getValue());
						NS.noEmpresa = caja.getValue();
						if(NS.cmbEmpresa.getRawValue()=='***************TODAS***************')
							caja.setValue('0');
					}
					else{
						NS.cmbEmpresa.reset();
						idEmpresa = 0;
						NS.noEmpresa = 0;
					}
                }
			}
    	}
    });
	
	NS.storeLlenaEmpresaImportar = new Ext.data.DirectStore({
		paramsAsHash: true,
		root: '',
		directFn: GlobalAction.llenarComboEmpresasUsuario,
		fields:
		[
		    
		    {name: 'id'},
		 	{name: 'descripcion'}
		 	
		],
		idProperty: 'id',
		listeners:
		{
			load: function(s, records){
				var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeLlenaEmpresaImportar, msg: "Cargando..."});
				if (records.length == null || records.length <= 0){
					Ext.Msg.alert('SET', 'No tiene empresas asignadas');
				}
				//Se agrega la opcion de todas las empresas
				var recordsStoreEmpresas = NS.storeLlenaEmpresaImportar.recordType;
				var todas = new recordsStoreEmpresas({
					id: 0,
					descripcion: '***************TODAS***************'
				});
				NS.storeLlenaEmpresaImportar.insert(0, todas);
				NS.myMask.hide();
					
			},
			exception:function(misc) {
				NS.myMask.hide();
				Ext.Msg.alert('SET', 'Error al cargar los datos, verificar la conexión');
			}
		}
	}); 
	
	NS.cmbEmpresaImportar = new Ext.form.ComboBox({
		store: NS.storeLlenaEmpresaImportar,
		id: PF + 'cmbEmpresaImportar',
		name: PF + 'cmbEmpresaImportar',
		x: 60,
		y: 15,
		width: 300,
		typeAhead: true,
		mode: 'local',
		forceSelection: true,
		valueField: 'id',
		emptyText: 'Empresa Importación',
		displayField: 'descripcion',
		triggerAction: 'all',
		value:'',
		visible: false,
		autocomplete:true,
		selectOnFocus:true,
		listeners:{
			select:{
				fn:function(combo, valor) {
			 		 BFwrk.Util.updateComboToTextField(PF + 'txtNoEmpresaImportar', NS.cmbEmpresaImportar.getId());
					
				}
			}
		}
	});
	
	NS.lblFechasImportar = new Ext.form.Label({
		text: 'Rango de Fechas:',
		x: 0,
		y: 50
	});
	
	NS.txtFechaInicialImportar = new Ext.form.DateField({
		id: PF + 'txtFechaInicialImportar',
		name: PF + 'txtFechaInicialImportar',
		format:'d/m/Y',
		x: 0,
		y: 70,
		width: 100,
		value: apps.SET.FEC_HOY
     });
	
	NS.txtFechaFinalImportar = new Ext.form.DateField({
		id: PF + 'txtFechaFinalImportar',
		name: PF + 'txtFechaFinalImportar',
		format:'d/m/Y',
		x: 125,
		y: 70,
		width: 100,
		value: apps.SET.FEC_HOY,
		listeners:{
        	change:{
        		fn:function(caja, valor){	
					var fechaUno=NS.txtFechaInicialImportar.getValue();
					var valIni='';
					if(fechaUno!='')
					{
						valIni=cambiarFecha(''+fechaUno);
					}
					var valFin='';
					if(valor!='')
					{
						valFin=cambiarFecha(''+valor);
					}
					if(fechaUno>valor)
					{
						Ext.Msg.alert('SET','La fecha inicial debe ser menor a la final');
					}
					
        		}
				}
			}
	});
	
	NS.lblEstatusImportar = new Ext.form.Label({
		text: 'Estatus:',
		x: 0,
		y: 110
	});
	
	NS.datosImportar = [
	            ['T','EN PROCESO DE IMPORTACIÓN'],
	            ['I','IMPORTADO'],
	            ['R','RECHAZADO']
	 ];
	            	
	 NS.storeEstatusImportar  = new Ext.data.SimpleStore({
	      idProperty: 'idEstatusImportar',
	      fields:
	      [
	            			{name: 'idEstatusImportar'},
	            			{name: 'descripcionImportar'}
	         ]
	});
	NS.storeEstatusImportar.loadData(NS.datosImportar);
	
	NS.cmbEstatusImportar = new Ext.form.ComboBox ({
		store: NS.storeEstatusImportar,
		id: PF + 'cmbEstatusImportar',
		name: PF + 'cmbEstatusImportar',
		x: 0,
		y: 130,
		width: 180,
		typeAhead: true,
		mode: 'local',
		selecOnFocus: true,
		forceSelection: true,
		valueField: 'idEstatusImportar',
		displayField: 'descripcionImportar',
		autocomplete: true,
		emptyText: 'Estatus de Importación',
		triggerAction: 'all',
		value: '',
		visible: false,
		listeners: {
			select: {
				fn: function(combo, valor) {
				
				}
			}
		}
	});	
	
	NS.optTipoReporte = new Ext.form.RadioGroup ({
		id: PF + 'optTipoReporte',
		name: PF + 'optTipoReporte',
		x: 0,
		y: 0,
		columns: 3, //muestra los radiobuttons en dos columnas
		items: [
	          {boxLabel: 'Cuentas por Pagar', name: 'optActualTipoReporte', inputValue: 0, checked: true},  
	          {boxLabel: 'Datos Bancarios', name: 'optActualTipoReporte', inputValue: 1},
	          {boxLabel: 'Cuentas por Cobrar', name: 'optActualTipoReporte', inputValue: 2}
	    ]
	});
	
	NS.panelRadioButtonTipoReporte = new Ext.form.FieldSet ({
		title: 'Tipo Excel',
		x: 0,
		y: 170,
		width: 375,
		height: 80,
		layout: 'absolute',
		items: [
		     NS.optTipoReporte
		]
	});
	
	NS.panelImportaciones = new Ext.form.FieldSet ({
		title: 'Criterios',
		x: 15,
		y: 0,
		width: 400,
		height: 320,
		layout: 'absolute',
		items: [
		     NS.lblNoEmpresaImportar,
             NS.txtNoEmpresaImportar,
             NS.cmbEmpresaImportar,
             NS.lblFechasImportar,
             NS.txtFechaFinalImportar,
             NS.txtFechaInicialImportar,
             NS.lblEstatusImportar,
             NS.cmbEstatusImportar,
             NS.panelRadioButtonTipoReporte
		],
	});
	
	var winImportaciones = new Ext.Window({
		title: 'Excel Importaciones',
		modal: true,
		shadow: true,
		//closable: false,
		closeAction: 'hide',
		width: 450,
	   	height: 400,
	   	layout: 'absolute',
	   	plain: true,
	   	resizable:false,
	   	draggable:false,
	    bodyStyle: 'padding:10px;',
	    buttonAlign: 'center',
	    buttons:[
			      {
			    	text:'Aceptar',
			    	handler:function(){
			    		var valueTR = Ext.getCmp(PF+'optTipoReporte').getValue();
			    		NS.estatusTipoReporte = valueTR.getGroupValue();
			    		var noEmpresa = NS.txtNoEmpresaImportar.getValue();
			            var fecIni = NS.txtFechaInicialImportar.getValue();
			            var fecFin = NS.txtFechaFinalImportar.getValue();
			            var estatus = NS.cmbEstatusImportar.getValue();
			            if(noEmpresa!=null && noEmpresa !='' && fecIni!='' && fecFin!=''){
			            	fecIni = cambiarFecha('' + fecIni);
			            	fecFin = cambiarFecha('' + fecFin);
			            	
			            	parametros='?nomReporte=excelImportaciones';
			            	parametros+='&nomParam1=noEmpresa';
							parametros+='&valParam1='+noEmpresa;
							parametros+='&nomParam2=fecIni';
							parametros+='&valParam2='+fecIni;
							parametros+='&nomParam3=fecFin';
							parametros+='&valParam3='+fecFin;
							parametros+='&nomParam4=estatus';
							parametros+='&valParam4='+estatus;
							parametros+='&nomParam5=tipoReporte';
							parametros+='&valParam5='+NS.estatusTipoReporte;
							window.open("/SET/jsp/bfrmwrk/gnrators/abreExcel.jsp"+parametros);
			            }else{
			            	Ext.Msg.alert('SET','Ingrese una empresa y un rango de fechas válido');
			            }
			    		return;
			    	}
			      },
			      
			      {
				    	text:'Limpiar',
				    	handler:function(){
				    		NS.limpiarBotonVentanaImportar();
				    		NS.storeLlenaEmpresaImportar.load();
				    	}
				      },		      
	    ],
	   	items: [
	   	     NS.panelImportaciones
	   	     ],
   	        	listeners:{
   	        		show:{
   	        			fn:function(){
   	        				NS.limpiarVentanaImportar();
   	        				var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeLlenaComboEmpresasImportar, msg:"Cargando..."});
   	        				NS.storeLlenaEmpresaImportar.load();
   	        				
   	        			}
   	        		}
   	        	}	
		});
	
	NS.limpiarVentanaImportar = function(){
		 NS.storeLlenaEmpresaImportar.removeAll();
         NS.txtNoEmpresaImportar.setValue('');
         NS.cmbEmpresaImportar.reset();
         NS.txtFechaFinalImportar.reset();
         NS.txtFechaInicialImportar.reset();
         NS.cmbEstatusImportar.reset();
         NS.optTipoReporte.reset();
         
	}
	
	NS.limpiarBotonVentanaImportar = function(){
		NS.storeLlenaEmpresaImportar.removeAll();
        NS.txtNoEmpresaImportar.setValue('');
        NS.cmbEmpresaImportar.reset();
        NS.txtFechaFinalImportar.reset();
        NS.txtFechaInicialImportar.reset();
        NS.cmbEstatusImportar.reset();
        NS.optTipoReporte.reset();
	}
	//////////////////////////////////////////////////////////////////////////////
	
	//Termina Función del reporte de movimientos
	NS.ConsultaDeMovimientos = new Ext.form.FormPanel({
    title: 'Consulta de Movimientos',
    width: 831,
    autoScroll: true,
    height: 566,
    padding: 10,
    layout: 'absolute',
    id: PF + 'ConsultaDeMovimientos',
    name: PF + 'ConsultaDeMovimientos',
    renderTo : NS.tabContId,
    frame: true,
    //autoScroll: true,
        items : [
            {
                xtype: 'fieldset',
                title: '',
                width: 1045,
                height: 500,
                x: 5,
                y: 0,
               // autoScroll: true,
                layout: 'absolute',
                id: 'fSetPrincipal',
                items: [
                    {
                        xtype: 'fieldset',
                        title: 'Criterios de busqueda',
                        x: 0,
                        y: 0,
                        height: 195,
                        layout: 'absolute',
                        width: 1020,
                        id: 'fSetBusqueda',
                        items: [
                            {
                                xtype: 'label',
                                text: 'Empresa:'
                            },
                            NS.txtNoEmpresa,
                            NS.cmbEmpresa,
                            /*{
                                xtype: 'textfield',
                                x: 0,
                                y: 20,
                                width: 240,
                                id: PF + 'txtNomEmpresa',
                                name: PF + 'txtNomEmpresa',
                                readOnly: true,
                                value: apps.SET.NOM_EMPRESA
                            },
                            {
                                xtype: 'fieldset',
                                title: 'Empresa',
                                x: 270,
                                y: 0,
                                width: 150,
                                height: 60,
                                layout: 'absolute',
                                id: 'fSetEmpresa',
                                items: [
                                    {
                                        xtype: 'radio',
                                        x: 0,
                                        y: -2,
                                        boxLabel: 'Actual',
                                        name: PF + 'optActual',
                                        id: PF + 'optActual',
                                        checked: true,
                                        listeners : {
                                        	check : {
                                        		fn : function(opt, valor)
                                        		{
                                        			if(valor)
                                        			{
                                        				Ext.getCmp(PF + 'optActual').setValue(true);
                                        				Ext.getCmp(PF + 'optTodas').setValue(false);
                                        				NS.storeConsultaMovimientos.baseParams.bOptEmp = true;
                                        				NS.bOptEmp = true;
                                        			}
                                        					
                                        		}
                                        	}
                                        } 
                                    },
                                    {
                                        xtype: 'radio',
                                        x: 70,
                                        y: 0,
                                        boxLabel: 'Todas',
                                        name: PF + 'optTodas',
                                        id: PF + 'optTodas',
                                        listeners : {
                                        	check : {
                                        		fn : function(opt, valor)
                                        		{
                                        			if(valor)
                                        			{
                                        				Ext.getCmp(PF + 'optTodas').setValue(true);
                                        				Ext.getCmp(PF + 'optActual').setValue(false);
                                        				NS.storeConsultaMovimientos.baseParams.bOptEmp = false;
                                        				NS.bOptEmp = false;
                                        			}
                                        					
                                        		}
                                        	}
                                        } 
                                    }
                                ]
                            },*/
                            
                            {
                                xtype: 'fieldset',
                                title: 'Origen Movimiento',
                                x:255,
                                y:45,
                                width: 160,
                                height: 115,
                                layout: 'absolute',
                                id: 'fSetOrigenMovimiento',
                                items: [
                                    {
                                        xtype: 'radio',
                                        x: 0,
                                        y: 0,
                                        boxLabel: 'Egreso',
                                        name: PF + 'optEgreso',
                                        id: PF + 'optEgreso',
                                        checked: true,
                                        listeners : {
                                        	check : {
                                        		fn : function(opt, valor)
                                        		{
                                        			if(valor)
                                        			{
                                        				Ext.getCmp(PF + 'optEgreso').setValue(true);
                                        				Ext.getCmp(PF + 'optIngreso').setValue(false);
                                        				NS.storeConsultaMovimientos.baseParams.bOptTipoMovto = true;
                                        				NS.bOptTipoMovto = true;
                                        			}
                                        		}
                                        	}
                                        } 
                                    },
                                    {
                                        xtype: 'radio',
                                        x: 0,
                                        y: 30,
                                        boxLabel: 'Ingreso',
                                        name: PF + 'optIngreso',
                                        id: PF + 'optIngreso',
                                        listeners : 
                                        {
                                        	check : 
                                        	{
                                        		fn : function(opt, valor)
                                        		{
                                        			if(valor)
                                        			{
                                        				Ext.getCmp(PF + 'optIngreso').setValue(true);
                                        				Ext.getCmp(PF + 'optEgreso').setValue(false);
                                        				NS.storeConsultaMovimientos.baseParams.bOptTipoMovto = false;
                                        				NS.bOptTipoMovto = false;
                                        			}
                                        		}
                                        	}
                                        } 
                                    },
                                    {
                                        xtype: 'checkbox',
                                        x: 0,
                                        y: 60,
                                        boxLabel: 'Solicitudes Canceladas',
                                        name: 'chkSolCan',
                                        id: 'chkSolCan',
                                        listeners : 
                                        {
                                        	check : {
                                        		fn : function(opt, valor)
                                        		{
                                        			if(valor)
                                        				NS.storeConsultaMovimientos.baseParams.bSolCan = true;
                                        			else
                                        				NS.storeConsultaMovimientos.baseParams.bSolCan = false;
                                        		}
                                        	}
                                        } 
                                    }
                                ]
                            },
                            {
                                xtype: 'label',
                                text: 'Fecha:',
                                x: 0,
                                y: 45
                            },
                            {
                                xtype: 'label',
                                text: 'Contable',
                                x: 0,
                                y: 60
                            },
                            {
                                xtype: 'datefield',
                                format: 'd/m/Y',
                                x: 50,
                                y: 50,
                                width: 90,
                                name: PF + 'txtFechaInicial',
                                id: PF + 'txtFechaInicial',
                                value: apps.SET.FEC_HOY,
                                listeners : {
                                	change : {
                                		fn : function(box, val)
                                		{
                                			NS.agregarCriterioValor('FECHA VALOR', box.getId(), 'valor');
                                		}
                                	}
                                }
                            },
                            {
                                xtype: 'datefield',
                                format: 'd/m/Y',
                                x: 150,
                                y: 50,
                                width: 90,
                                name: PF + 'txtFechaFinal',
                                id: PF + 'txtFechaFinal',
                                value: apps.SET.FEC_HOY,
                                listeners : 
                                {
                                	change : 
                                	{
                                		fn : function(box, val)
                                		{	
                                			var fechaIni = Ext.getCmp(PF + 'txtFechaInicial').getValue();
                                			if(fechaIni > box.getValue())
                                			{
                                				BFwrk.Util.msgShow('La fecha inicial no puede ser mayor', 'INFO');
                                				return;
                                			}
                                			NS.agregarCriterioValor('FECHA VALOR', box.getId(), 'valor');
                                		}
                                	}
                                }
                            },
                            {
                                xtype: 'label',
                                text: 'Criterio:',
                                x: 0,
                                y: 90
                            },
                            	NS.cmbCriterio,
                            	NS.cmbCajas,
                            	NS.cmbBanco,
                            	NS.cmbChequeras,
                            	NS.cmbDivisas,
                            	NS.cmbDivisiones,
                            	NS.cmbEstatus,
                            	NS.cmbFormaPago,
                            	NS.cmbOrigen,
                            	NS.cmbTipoMovto,
                            	NS.cmbValorCustodia,
                            	NS.cmbBeneficiario,
                            {
	                        xtype: 'button',
	                        x: 230,
	                        y: 125,
	                        width: 20,
	                        height: 22,
	                        id: PF + 'btnComboBenef',
	                        name: PF + 'btnComboBenef',
	                        hidden : true,
	                        icon: 'img/icons/lupita.png',
	                        listeners:{
	                        	click:{
	                        		fn:function(e){
	                        				if(NS.parametroBus === '')
	                        				{
	                        					Ext.Msg.alert('Información SET','Es necesario agregar una letra o nombre');
	                        				}
	                        				else
	                        				{
	                        					NS.storeBeneficiario.baseParams.prefijoProv = NS.parametroBus;
	                        					NS.storeBeneficiario.baseParams.noEmpresa = parseInt(Ext.getCmp(PF + 'txtNoEmpresa').getValue());
	                        					NS.storeBeneficiario.load();
	                        				}
	                       				}
	                   				}
			               		}
		               		},
                            //Inician cajas de texto para los criterios que las requieran
                            {
                            	xtype : 'uppertextfield',
                            	x: 50,
						        y: 125,
						        width: 190,
						        name: PF + 'txtConcepto',
						        id: PF + 'txtConcepto',
						        hidden: true,
						        listeners: 
						        	{
						        	change: 
						        		{
						        		fn: function(caja, valor)
						        		{
						        			NS.agregarCriterioValor('CONCEPTO', caja.getId(), 'valor');
						        		}
						        	}
						        }
                            },
                            {
                            	xtype : 'uppertextfield',
                            	x: 50,
						        y: 125,
						        width: 190,
						        name: PF + 'txtFolioCompensacion',
						        id: PF + 'txtFolioCompensacion',
						        hidden: true,
						        listeners: 
						        	{
						        	change: 
						        		{
						        		fn: function(caja, valor)
						        		{
						        			NS.agregarCriterioValor('FOLIOCOMPENSACION', caja.getId(), 'valor');
						        		}
						        	}
						        }
                            },
                            {
                            	xtype : 'numberfield',
                            	x: 50,
						        y: 125,
						        width: 70,
						        name: PF + 'txtFolioMovi',
						        id: PF + 'txtFolioMovi',
						        hidden: true,
						        listeners: 
						        	{
						        	change: 
						        		{
						        		fn: function(caja, valor)
						        		{
						        			NS.agregarCriterioValor('FOLIO MOVIMIENTO', caja.getId(), 'valor');
						        		}
						        	}
						        }
                            },
                            {
                            	xtype : 'textfield',
                            	x: 50,
						        y: 125,
						        width: 190,
						        name: PF + 'txtCvePropuesta',
						        id: PF + 'txtCvePropuesta',
						        hidden: true,
						        listeners: 
						        	{
						        	change: 
						        		{
						        		fn: function(caja, valor)
						        		{
						        			NS.agregarCriterioValor('CVE PROPUESTA', caja.getId(), 'valor');
						        		}
						        	}
						        }
                            },
                            {
                            	xtype : 'textfield',
                            	x: 50,
						        y: 125,
						        width: 90,
						        name: PF + 'txtMontoIni',
						        id: PF + 'txtMontoIni',
						        hidden: true,
						        listeners: 
						        	{
						        	change: 
						        		{
						        		fn: function(caja, valor)
						        		{
						        			Ext.getCmp(PF + 'txtMontoIni').setValue(BFwrk.Util.formatNumber(caja.getValue()));
						        			NS.agregarCriterioValor('MONTOS', caja.getId(), 'valor');
						        		}
						        	}
						        }
                            },
                            {
                            	xtype : 'textfield',
                            	x: 150,
						        y: 125,
						        width: 90,
						        name: PF + 'txtMontoFin',
						        id: PF + 'txtMontoFin',
						        hidden: true,
						        listeners: 
						        	{
						        	change: 
						        		{
						        		fn: function(caja, valor)
						        		{
						        			Ext.getCmp(PF + 'txtMontoFin').setValue(BFwrk.Util.formatNumber(caja.getValue()));
						        			NS.agregarCriterioValor('MONTOS', caja.getId(), 'valor');
						        		}
						        	}
						        }
                            },
                            {
                            	xtype : 'textfield',
                            	x: 50,
						        y: 125,
						        width: 70,
						        name: PF + 'txtNoCheque',
						        id: PF + 'txtNoCheque',
						        hidden: true,
						        listeners: 
						        	{
						        	change: 
						        		{
						        		fn: function(caja, valor)
						        		{
						        			NS.agregarCriterioValor('NO. CHEQUE', caja.getId(), 'valor');
						        		}
						        	}
						        }
                            },
                            {
                            	xtype : 'textfield',
                            	x: 50,
						        y: 125,
						        width: 100,
						        name: PF + 'txtNoDocto',
						        id: PF + 'txtNoDocto',
						        hidden: true,
						        listeners: 
						        	{
						        	change: 
						        		{
						        		fn: function(caja, valor)
						        		{
						        			NS.agregarCriterioValor('NO. DOCUMENTO', caja.getId(), 'valor');
						        		}
						        	}
						        }
                            },
                            {
                            	xtype : 'textfield',
                            	x: 50,
						        y: 125,
						        width: 70,
						        name: PF + 'txtNoFactura',
						        id: PF + 'txtNoFactura',
						        hidden: true,
						        listeners: 
						        	{
						        	change: 
						        		{
						        		fn: function(caja, valor)
						        		{
						        			NS.agregarCriterioValor('NO. FACTURA', caja.getId(), 'valor');
						        		}
						        	}
						        }
                            },
                            {
                                xtype: 'label',
                                text: 'Valor:',
                                x: 0,
                                y: 125
                            },
                            NS.gridDatos,
                            {
                                xtype: 'button',
                                text: 'Buscar movimientos',
                                x: 750,
                                y: 130,
                                width: 90,
                                name : PF + 'btnBuscar',
                                id : PF + 'btnBuscar',
                                listeners : {
                                	click : {
                                		fn : function(e)
                                		{
                                			NS.buscar();
                                		}
                                	}
                                }
                            },
                            {
		                        xtype: 'button',
		                        text: 'Ver PDF',
	                            x: 860,
	                            y: 130,
	                            width: 60,
	   	                        id: PF + 'btnPdf',
		                        name: PF + 'btnPdf',
		                        disabled: false,
		                        listeners:
		                        {
		                        	click:
		                        	{
		                        		fn: function(btn){
		                        			var parametros='';
		                        		    var dato=NS.dato.getValue();
		                        		    parseInt(dato);
		                        		    
		                        		    if(dato==""){
		                        		    	Ext.Msg.alert('SET', "Seleccione un movimiento");
		                        		    }
		                        		    else{
		                        		    	ConsultasAction.leerRuta(dato,function(res){
			                        				var cadena=res;
			                        				var inicio = 16;
			                        			    var fin    = 20;
			                        			    var subCadena = cadena.substring(inicio, fin);
			                        			    
			                        			    //alert(res);
			                        			    
			                        				if (res != null && res != undefined && res == "") {
		                        						Ext.Msg.alert('SET', "No hay ruta establecida");
			                        				}
			                        				else if(res.indexOf("Error:")>=0)
			                        			    	Ext.Msg.alert('SET', res.substring(res.indexOf(":")+1, res.length));
			                        				else{
			                        					if(subCadena=="null"){
			                        						Ext.Msg.alert('SET', "No hay ruta establecida");
			                        					}
			                        					else{
			                        						//alert("Se abre el PDF: " + res);
			                        						var win = window.open(res, '_blank');
			                        						win.focus();
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
                                text: 'Limpiar',
                                x: 924,
                                y: 130,
                                width: 70,
                                id : PF + 'btnLimpiar',
                                name : PF + 'btnLimpiar',
                                listeners : {
                                	click : {
                                		fn : function(e)
                                		{
                                			NS.limpiar();
                                			NS.valoresIniciales();
                                		}
                                	}
                                }
                            },
                           /* {
                                xtype: 'fieldset',
                                title: 'Graficas',
                                x: 735,
                                y: 120,
                                width: 215,
                                height: 70,
                                layout: 'absolute',
                                id: 'fSetGraficas',
                                hidden:true,
                                items: [
										NS.cmbGraficas
										]
                            },*/
                            //Agregado el 09 de feb del 2015
                            {
                                xtype: 'fieldset',
                                title: 'FONDEO DEL DIA',
                                x: 755,
                                y: 20,
                                width: 215,
                                height: 80,
                                layout: 'absolute',
                                id: 'fSetFondeo',
                                //hidden:true,
                                items: [
                                        NS.chkDetalladoFondeo,
										{
										 xtype: 'button',
									     text: 'Generar reporte',
									     x: 0,
								   	     y: 0,
									     width: 190,
										 id: PF +'btnFondeo',
										 name: PF + 'btnFondeo',
										 //disabled : true,
										 listeners:{
										    click:{
										    	fn: function(e){
										    		
										    		ConsultasAction.retornaUsuario( function(result, e) {
										    		
										    			if(result != null && result != undefined && result != '') {
										    				
										    				NS.fInicio = BFwrk.Util.changeDateToString('' + Ext.getCmp(PF + 'txtFechaInicial').getValue());
												    		NS.fFin = BFwrk.Util.changeDateToString('' + Ext.getCmp(PF + 'txtFechaFinal').getValue());  
										 					
										 					if(NS.tipoPersona !=""){
										 						parametros='?nomReporte=CMreporteFondeo';
									 							parametros+='&nomParam1=fInicio';
									 							parametros+='&valParam1='+NS.fInicio ;
									 							parametros+='&nomParam2=fFin';
									 							parametros+='&valParam2='+NS.fFin ;
									 							parametros+='&nomParam3=bandera';
									 							parametros+='&valParam3='+NS.chkDetalladoFondeo.getValue();
									 							parametros+='&nomParam4=usuario';
									 							parametros+='&valParam4='+result;
									 							window.open("/SET/jsp/bfrmwrk/gnrators/abreExcel.jsp"+parametros);
										 					}else{
										 						Ext.Msg.alert('SET'," Debe seleccionar un rango de fechas.");
										 						
										 					}
										    			}else{
										    				Ext.Msg.alert('SET',"Error al obtener al usuario");
										    			}
										    		
											    		
											    				
										    		});	
										    	}
										    }
										 }
										}
									   ]
                            },
                        ]
                    },
                    {
                        xtype: 'fieldset',
                        title: 'Movimientos',
                        x: 0,
                        y: 200,
                        layout: 'absolute',
                        height: 275,
                        width: 1020,
                        id: 'fSetMovimientos',
                        items: [
                        	NS.gridConsultaMovimientos,
                        	NS.cmbMotivos,
                        	NS.lbMotivo,
                        	{
                                xtype: 'label',
                                text: 'Total de registros:',
                                x: 10,
                                y: 220
                            },
                            {
                                xtype: 'textfield',
                                x: 110,
                                y: 216,
                                width: 90,
                                id: PF + 'txtTotalReg',
                                name : PF + 'txtTotalReg'
                            },
                            {
                                xtype: 'button',
                                text: 'Importaciones',
                                x: 485,
                                y: 217,
                                width: 70,
                                id: PF +'btnImportaciones',
                                name: PF + 'btnImportaciones',
                                disabled : false,
                                listeners:{
                                	click:{
                                		fn: function(e){
                                			winImportaciones.show();
                                		}
                                	}
                                }
                            },
                            {
                                xtype: 'button',
                                text: 'Comprobante',
                                x: 750,
                                y: 217,
                                width: 70,
                                id: PF +'btnComprobante',
                                name: PF + 'btnComprobante',
                                disabled : true,
                                listeners:{
                                	click:{
                                		fn: function(e){
                                			var recordsGridConsMovto = NS.gridConsultaMovimientos.getSelectionModel().getSelections();
		
											if(recordsGridConsMovto.length == 0)
											{
												BFwrk.Util.msgShow('Debe seleccionar un movimiento', 'INFO');
												return;
											}
											
                                			Ext.Msg.show({
											title: 'Información SET',
											msg: '¿Esta seguro de obtener el comprobante de este movimiento?',
												buttons: {
													yes: true,
													no: true,
													cancel: false
												},
												icon: Ext.MessageBox.QUESTION,
												fn: function(btn) {
													if(btn === 'yes')
														NS.imprimirComprobante();
													else if(btn === 'no')
														return;
													else
														return;
												}
											});
                                		}
                                	}
                                }
                            },
                           // {
                              //  xtype: 'button',
                                //    }
                            
                            /*            
            If Trim(msfDatos.TextMatrix(i, LI_C_ID_ESTATUS_MOV)) Like "[T]" And _
                Trim(msfDatos.TextMatrix(i, LI_C_ID_FORMA_PAGO)) = 3 And _
                Trim(msfDatos.TextMatrix(i, LI_C_ID_TIPO_OPERACION)) = 3200 Then
                Screen.MousePointer = 0
                MsgBox "¡Las transferencias se cancelan en control de archivos (Banca Electrónica) !", vbExclamation, "SET"
                quitar_color (i)
                Exit Sub
            End If
            
            If Val(msfDatos.TextMatrix(i, LI_C_ID_TIPO_OPERACION)) = 5000 _
                Or Val(msfDatos.TextMatrix(i, LI_C_ID_TIPO_OPERACION)) = 5100 Or _
                Val(msfDatos.TextMatrix(i, LI_C_ID_TIPO_OPERACION)) = 5104 Then
                Screen.MousePointer = 0
                MsgBox "¡Los movimientos de la caja se cancelan en el arqueo de caja!", vbExclamation, "SET"
                quitar_color (i)
                Exit Sub
            End If
            
            ps_tipo_cancelacion = "C"
            ps_estatus = msfDatos.TextMatrix(msfDatos.row, LI_C_ID_ESTATUS_MOV)
            psOrigen = msfDatos.TextMatrix(msfDatos.row, LI_C_ORIGEN)
            ps_estatus_mov = "P"
            pi_secuencia = 1
            
            'Nueva Llamada para optimizacion
            
            
                plresp = gobjVarGlobal.revividor(GI_USUARIO, gobjVarGlobal.PasswordBD, "frmconmovi", GI_USUARIO, _
                        ps_revividor, Trim(msfDatos.TextMatrix(msfDatos.row, LI_C_NO_FOLIO_DET)), _
                        Trim(msfDatos.TextMatrix(msfDatos.row, LI_C_ID_TIPO_OPERACION)), _
                        ps_tipo_cancelacion, _
                        Trim(msfDatos.TextMatrix(msfDatos.row, LI_C_ID_ESTATUS_MOV)), _
                        IIf(Trim(msfDatos.TextMatrix(msfDatos.row, LI_C_ORIGEN_MOV)) = "CXP", "SOI", Trim(msfDatos.TextMatrix(msfDatos.row, LI_C_ORIGEN_MOV))), _
                        Trim(msfDatos.TextMatrix(msfDatos.row, LI_C_ID_FORMA_PAGO)), _
                        Trim(msfDatos.TextMatrix(msfDatos.row, LI_C_PS_ENTREGADO)), _
                        Trim(msfDatos.TextMatrix(msfDatos.row, LI_C_ID_TIPO_MOVTO)), _
                        Val(Format(msfDatos.TextMatrix(msfDatos.row, LI_C_IMPORTE), "")), _
                        Trim(msfDatos.TextMatrix(msfDatos.row, LI_C_NO_EMPRESA)), _
                        Val(Trim(msfDatos.TextMatrix(msfDatos.row, LI_C_NO_CUENTA))), _
                        Trim(msfDatos.TextMatrix(msfDatos.row, LI_C_ID_CHEQUERA)), _
                        Trim(msfDatos.TextMatrix(msfDatos.row, LI_C_ID_BANCO)), _
                        Str(GI_USUARIO), _
                        Val(Trim(msfDatos.TextMatrix(msfDatos.row, LI_C_NO_DOCTO))), _
                        Val(Trim(msfDatos.TextMatrix(msfDatos.row, LI_C_LOTE_ENTRADA))), _
                        Trim(msfDatos.TextMatrix(msfDatos.row, LI_C_B_SALVO_BUEN_COBRO)), _
                        Trim(msfDatos.TextMatrix(msfDatos.row, LI_C_FEC_CONF_TRANS)), _
                        Trim(msfDatos.TextMatrix(msfDatos.row, LI_C_ID_DIVISA)), _
                        ps_resultado)
                If plresp <> 0 Then
                    Screen.MousePointer = 0
                    If plresp = 2000 Then
                        MsgBox "¡ Este Movimiento no se puede cancelar !", vbInformation, "SET"
                    Else
                        ps_resultado = Replace(ps_resultado, "|", " ")
                        MsgBox "Error en revividor  # " & ps_resultado, vbCritical, "SET"
                    End If
                    Exit Sub
                End If
                If rstdatos.RecordCount > 0 Then
                    Call gobjSQL.funSacar_movimiento_archivo(Trim(msfDatos.TextMatrix(msfDatos.row, LI_C_NO_FOLIO_DET)))
                End If
            rstdatos.Close
            Set rstdatos = Nothing
            
            If Me.msfDatos.TextMatrix(i, LI_C_DESC_ESTATUS) <> "IMPORTADO" Then
            ''''''''''''''''''''''''''''''''''''''''''''''''''''''''tabla'''''''''''''''''''''''''''''''''''
            'Query el cual se usa para insertar el movimiento en la  bitacora_cheques
                lc_Afectados = gobjSQL.FunSQLInsert_bitacora_cheques(Trim(Val(Me.msfDatos.TextMatrix(i, LI_C_NO_FOLIO_DET))), _
                                        Trim(Val(Me.msfDatos.TextMatrix(i, LI_C_NO_EMPRESA))), _
                                        Trim(Val(msfDatos.TextMatrix(i, LI_C_ID_BANCO))), _
                                        Trim(msfDatos.TextMatrix(i, LI_C_ID_CHEQUERA)), _
                                        Trim(Val(msfDatos.TextMatrix(i, LI_C_NO_CHEQUE))), "C", 13)
            '''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''
            End If
            ' Si se cancela el movimiento pago de cheque busca la solictud y la elimina de solicitud che
            Select Case ps_tipo_cancelacion
                Case "C"
                    msfDatos.TextMatrix(i, LI_C_TIPO_CANCELACION) = "CANCELADO"
                Case "O"
                    msfDatos.TextMatrix(i, LI_C_TIPO_CANCELACION) = "EXP. CANCELADO"
            End Select
            
            msfDatos.TextMatrix(i, LI_C_DESC_ESTATUS) = "CANCELADO"
            msfDatos.TextMatrix(i, LI_C_ID_ESTATUS_MOV) = "X"
            
            quitar_color (i)
        End If
    Next i
    Screen.MousePointer = 0
    MsgBox "Datos Cancelados", vbInformation, "SET"
    If optmov(0).Value Then 'Egresos
        If mfCancelaEgr = True Or consolicitud = True Then
            Cmdcancelar.Enabled = True
        Else
            Cmdcancelar.Enabled = False
        End If
        If mfRegresaEgr Then
            cmdregresar.Enabled = True
        Else
            cmdregresar.Enabled = False
        End If
        
    ElseIf optmov(1).Value Then 'Ingresos
        If mfCancelaIng Then
            Cmdcancelar.Enabled = True
        Else
            Cmdcancelar.Enabled = False
        End If
        cmdregresar.Enabled = False
    End If
                             */
                            NS.btnEliminar,
                            NS.btnDescompensar,
                        	//NS.btnEliminar
                           // {
                              //  xtype: 'button',
                                
                            //},
                            {
                                xtype: 'button',
                                text: 'Exportar',
                                x: 920,
                                y: 217,
                                width: 70,
                                id: PF + 'btnExportar',
                                name : PF + 'btnExportar',
                                disabled : true,
                                listeners : {
                                	click : {
                                		fn : function(btn)
                                		{
                                			//NS.winFile.show();
											//var form = file.find('form');
											//var input = form.find('input');
                                			//window.document.execCommand('saveas');
                                			//$('#' + NS.tabContId).file()
                                			Ext.Msg.show({
											title: 'Información SET',
											msg: '¿Esta seguro de exportar los datos a excel?',
												buttons: {
													yes: true,
													no: true,
													cancel: false
												},
												icon: Ext.MessageBox.QUESTION,
												fn: function(btn) {
													if(btn === 'yes')
														NS.exportarDatosExcel();
													else if(btn === 'no')
														return;
													else
														return;
												}
											});
                                		}
                                	}
                                }
                            }
                        ]
                    },
                    
                   /* {
                        xtype: 'checkbox',
                        x: 290,
                        y: 400,
                        hidden: true,
                        boxLabel: 'Mostrar subtotales por chequeras'
                    },*/
                    {
                        xtype: 'button',
                        text: 'Imprimir',
                        x: 845,
                        y: 440,
                        width: 70,
                        id : PF + 'btnImprimir',
                        name : PF + 'btnImprimir',
                        disabled : true,
                        listeners : {
                        	click : {
                        		fn : function(e)
                        		{
                        			Ext.Msg.show({
											title: 'Información SET',
											msg: '¿Esta seguro de generar el reporte?',
												buttons: {
													yes: true,
													no: true,
													cancel: false
												},
												icon: Ext.MessageBox.QUESTION,
												fn: function(btn) {
													if(btn === 'yes'){
														NS.noEmpresa = Ext.getCmp(PF + 'txtNoEmpresa').getValue();
														
														if (NS.noEmpresa > 0){
															NS.nomEmpresa = NS.storeLlenaEmpresa.getById(NS.noEmpresa).get('descripcion');
															NS.noEmpresa = Ext.getCmp(PF + 'txtNoEmpresa').getValue() + "";
														}
														else
															NS.nomEmpresa = apps.SET.NOM_EMPRESA;
															
														NS.imprimirReporte();
													}													
													else if(btn === 'no')
														return;
													else
														return;
												}
											});
                        		}
                        	}
                        }
                    }
                ]
            }
        ]
	});
	
	
	NS.cambiarFormato = function(num,prefix){
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
	
	//Se agregan aqui valores iniciales para la interfaz
	NS.valoresIniciales = function()
	{
		NS.agregarCriterioValor('FECHA VALOR', Ext.getCmp(PF + 'txtMontoIni').getId(), 'criterio');
		NS.agregarCriterioValor('FECHA VALOR', Ext.getCmp(PF + 'txtFechaInicial').getId(), 'valor');
		NS.agregarCriterioValor('FECHA VALOR', Ext.getCmp(PF + 'txtFechaFinal').getId(), 'valor');
		Ext.getCmp(PF + 'txtFechaInicial').setDisabled(false);
		Ext.getCmp(PF + 'txtFechaFinal').setDisabled(false);
	};
	NS.valoresIniciales();
	//Termina valores iniciales para la interfaz
 
	/*$('table[id*="btnExportar"] tr td[class="x-btn-mc"] em button').file().choose(function(e, input){
                                				alert('Elige la ruta' + input.val());
                                			});*/
	NS.ConsultaDeMovimientos.setSize(Ext.get(NS.tabContId).getWidth(), Ext.get(NS.tabContId).getHeight());
	//staticCheck("div[id*='gridConsultaMovimientos']","div[id*='gridConsultaMovimientos']",8,".x-grid3-scroller",false);
	
});