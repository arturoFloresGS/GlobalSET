//GGonzález
Ext.onReady(function() {
	var NS = Ext.namespace('apps.SET.Financiamiento.CreditoBancario');
	NS.tabContId = apps.SET.tabContainerId;
	NS.idUsuario = apps.SET.iUserId;
	var vsTipoMenu='';//tipo de menú financiamientos
	var PF = apps.SET.tabID + '.';
	Ext.Direct.addProvider(Ext.setapp.REMOTING_API);
	Ext.QuickTips.init();
	var vsMenu='';
	NS.GI_ID_EMPRESA = apps.SET.NO_EMPRESA;
	NS.GI_NOM_EMPRESA = apps.SET.NOM_EMPRESA;
	NS.fecHoy = apps.SET.FEC_HOY;
	NS.USR = apps.SET.NOMBRE + '  ' + apps.SET.APELLIDO_PATERNO + '  '
	+ apps.SET.APELLIDO_MATERNO;
	//Cuando carga la pantalla

	NS.storeConfiguraSetTodos = new  Ext.data.DirectStore({
		paramsAsHash: false,
		root: '',
		directFn: AltaFinanciamientoAction.consultarConfiguraSetTodos,
		idProperty: 'id',
		fields: [
		         {name: 'id'},
		         {name: 'valorConfiguraSet'}
		         ],
	});
	//load de la pantalla
	//carga de store
	NS.storeConfiguraSetTodos.load({
		callback:function(records){
			NS.proyecto=records[0].get('valorConfiguraSet');
			if(NS.proyecto=='CIE'){
				if (vsTipoMenu== ''||vsTipoMenu == "B")
					NS.chkConcentrado.setVisible(true);
			}
			vsMenu=vsTipoMenu;
			if(NS.proyecto=='CIE'&&NS.chkConcentrado.getValue()&&(vsTipoMenu == "" || vsTipoMenu == "B")){
				vsMenu = "', 'B";
			}
		}
	});
	//Componentes pantalla
	NS.lblUsuario = new Ext.form.Label({
		text : 'Usuario:',
		x : 100,
		y : 3
	});
	NS.txtUsuario = new Ext.form.TextField({
		id : PF + 'txtUsuario',
		name : PF + 'txtUsuario',
		value : NS.USR,
		x : 170,
		y : 0,
		readOnly : true,
		width : 300
	});
	NS.lblNota = new Ext.form.Label({
		text : '*Para editar el valor divisa, dar doble click sobre la celda.',
		x : 0,
		y : 220
	});
	// Empresa
	NS.lblEmpresa = new Ext.form.Label({
		text : 'Empresa:',
		x : 60,
		y : 3
	});
	NS.txtEmpresa = new Ext.form.TextField({
		id : PF + 'txtEmpresa',
		name : PF + 'txtEmpresa',
		value : '0',
		x : 120,
		y : 0,
		width : 70,
		listeners : {
			change : {
				fn : function(caja, valor) {
					var comboValue = BFwrk.Util
					.updateTextFieldToCombo(PF+ 'txtEmpresa',NS.cmbEmpresa.getId());
				}
			}
		}
	});
	NS.storeCmbEmpresa = new Ext.data.DirectStore({
		paramsAsHash : false,
		root : '',
		baseParams : {
			plUsuario : parseInt(NS.idUsuario),
			psMenu : ''
		},
		paramOrder : [ 'plUsuario', 'psMenu' ],
		directFn : ReportePasivosFAction.obtenerEmpresas,
		idProperty : 'id',
		fields : [ {
			name : 'id'
		}, {
			name : 'descripcion'
		} ],
		listeners : {
			load : function(s, records) {
				var recordsStoreEmpresas = NS.storeCmbEmpresa.recordType;	
				var todos = new recordsStoreEmpresas({
					id: '0',
					descripcion: '****TODOS****'
				});
				NS.storeCmbEmpresa.insert(0,todos);
				NS.cmbEmpresa.setValue('0');
				NS.cmbEmpresa.setRawValue('****TODOS****');
			}
		}
	});
	var myMask = new Ext.LoadMask(Ext.getBody(), {
		store : NS.storeCmbEmpresa,
		msg : "Cargando..."
	});
	NS.storeCmbEmpresa.load();
	NS.cmbEmpresa = new Ext.form.ComboBox(
			{
				store : NS.storeCmbEmpresa,
				name : PF + 'cmbEmpresa',
				id : PF + 'cmbEmpresa',
				typeAhead : true,
				mode : 'local',
				minChars : 0,
				selecOnFocus : true,
				forceSelection : true,
				x : 195,
				y : 0,
				width : 320,
				valueField : 'id',
				displayField : 'descripcion',
				autocomplete : true,
				emptyText : 'Seleccione una empresa',
				triggerAction : 'all',
				value : '',
				listeners : {
					select : {
						fn : function(combo, valor) {
							var comboValue = BFwrk.Util
							.updateComboToTextField(PF+ 'txtEmpresa',NS.cmbEmpresa.getId());
						}
					}
				}
			});
	NS.chkConcentrado = new Ext.form.Checkbox({
		name : PF + 'chkConcentrado',
		id : 'chkConcentrado',
		x : 450,
		y : 40,
		hidden:true,
		boxLabel: 'Consolidado',
		listeners : {
			check : {
				fn : function(combo, valor) {
					var myMask = new Ext.LoadMask(Ext.getBody(), {
						store : NS.storeCmbEmpresa,
						msg : "Cargando..."
					});
					NS.storeCmbEmpresa.load();
					var myMask = new Ext.LoadMask(Ext.getBody(), {
						store : NS.storeGridDivisa,
						msg : "Cargando..."
					});
					NS.storeGridDivisa.load();
				}
			}
		}

	});
	//grid
	NS.columnaSeleccion = new Ext.grid.CheckboxSelectionModel ({
		singleSelect: false
	});
	NS.columnasDivisa = new Ext.grid.ColumnModel([
	                                              NS.columnaSeleccion,
	                                              {header: 'Id Divisa', width: 0, dataIndex: 'idDivisa', sortable: true,hidden : true, hideable : false,},
	                                              {header: 'Divisa', width: 180, dataIndex: 'divisa', sortable: true},
	                                              {header : 'Valor Divisa',dataIndex : 'valorDivisa',editable : true,sortable : true,flex : 10, width:100,
	                                            	  editor: new Ext.form.TextField({
	                                            		  xtype : 'textfield',
	                                            		  width:100,
	                                            		  allowBlank: false,
	                                            		  value:''
	                                            	  })
	                                              }
	                                              ]);
	NS.storeGridDivisa = new Ext.data.DirectStore({
		paramsAsHash : false,
		root : '',
		baseParams : {
			plUsuario : parseInt(NS.idUsuario),
			psMenu : ''
		},
		paramOrder : [ 'plUsuario', 'psMenu' ],
		directFn : ReportePasivosFAction.obtenerDivisaCreditos,
		fields : [ {
			name : 'idDivisa'
		}, {
			name : 'divisa'
		},{
			name : 'valorDivisa'
		}
		],
		listeners : {
			load : function(s, records) {
				/*if(records.length<=0)
					Ext.Msg.alert('SET','No existen divisas para el reporte');*/
				vsMenu=vsTipoMenu;
				if(NS.proyecto=='CIE'&&NS.chkConcentrado.getValue()&&(vsTipoMenu == "" || vsTipoMenu == "B")){
					vsMenu = "', 'B";
				}
			}
		}
	});
	var myMask = new Ext.LoadMask(Ext.getBody(), {
		store : NS.storeGridDivisa,
		msg : "Cargando..."
	});
	NS.storeGridDivisa.load();
	NS.gridDivisa = new Ext.grid.EditorGridPanel({
		store: NS.storeGridDivisa,
		id: 'gridGastos',
		cm: NS.columnasGastos,
		width: 320,
		height: 140,
		frame : true,
		x: 125,
		y:70,
		stripeRows: true,
		columnLines: true,
		cm: NS.columnasDivisa,
		sm: NS.columnaSeleccion,
	});
	NS.storePasivosFinancieros = new Ext.data.DirectStore({
		paramsAsHash : false,
		root : '',
		baseParams : {
			noEmpresa:0,
			json:''
		},
		paramOrder : ['noEmpresa','json'],
		directFn : ReportePasivosFAction.obtenerPasivosFinancieros,
		fields : [ {
			name : 'noBanco'
		}, {
			name : 'banco'
		},  {
			name : 'grupo'
		}, {
			name : 'importe'
		}, {
			name : 'tasaRef'
		}, {
			name : 'valorTasa'
		}, {
			name : 'divisa'
		}, {
			name : 'valorTasa'
		}, {
			name : 'inicio'
		}, {
			name : 'plazo'
		}, {
			name : 'vencimiento'
		}, {
			name : 'montoCortoPlazo'
		}, {
			name : 'interes'
		}, {
			name : 'montoLargoPlazo'
		},{
			name : 'periodicidadPagos'
		},{
			name : 'lineaAutorizada'
		},{
			name : 'lineaDisponible'
		},{
			name : 'linea'
		},{
			name : 'color'
		},{
			name : 'destinoRecurso'
		},{
			name:'idTipoFinanciamiento'
		},{
			name : 'garantias'
		},{
			name : 'avales'
		},{
			name : 'descripcionAval'
		},

		],
		listeners : {
			load : function(s, records) {
				if(records.length<=0){
					Ext.Msg.alert('SET','No existen datos para la consulta');
					NS.winReporte.hide();
					return;
				}
			}
		}
	});
	NS.gridDatos = new Ext.grid.GridPanel({
		store : NS.storePasivosFinancieros,
		id : PF + 'gridDatos',
		name : PF + 'gridDatos',
		frame : true,
		autoScroll : true,
		autowidth : true,
		height : 500,
		x : 0,
		y : 0,
		cm : new Ext.grid.ColumnModel({
			defaults : {
				width : 120,
				value : true,
				sortable : true
			},
			columns : [
			           {
			        	   id : 'noBanco',
			        	   header : '',
			        	   width : 0,
			        	   dataIndex : 'noBanco',
			        	   direction : 'ASC',
			        	   hidden : true,
			        	   hideable : false,
			           },{
			        	   id : 'banco',
			        	   header : 'Banco',
			        	   width : 350,
			        	   dataIndex : 'banco',
			        	   direction : 'ASC',
			        	   renderer: function (value, meta, record) {
			        		   meta.attr = 'style=' + record.get('color');
			        		   return value;
			        	   }
			           },
			           {
			        	   header :'',
			        	   width : 0,
			        	   dataIndex : 'grupo',
			        	   hidden : true,
			        	   hideable : false,
			        	   renderer: function (value, meta, record) {
			        		   meta.attr = 'style=' + record.get('color');
			        		   return value;
			        	   }
			           },
			           {
			        	   header : 'Importe',
			        	   width : 130,
			        	   dataIndex : 'importe',
			        	   align :'right', 
			        	   renderer: function (value, meta, record) {
			        		   meta.attr = 'style=' + record.get('color');
			        		   return value;
			        	   }

			           },
			           {
			        	   header : 'Tasa Ref.',
			        	   width : 100,
			        	   dataIndex : 'tasaRef',
			        	   renderer: function (value, meta, record) {
			        		   meta.attr = 'style=' + record.get('color');
			        		   return value;
			        	   }
			           },
			           {
			        	   header : '',
			        	   width : 0,
			        	   dataIndex : 'divisa',
			        	   hidden : true,
			        	   hideable : false,

			           },
			           {
			        	   header : 'Valor Tasa',
			        	   width : 100,
			        	   dataIndex : 'valorTasa',
			        	   align :'right'
			           },
			           {
			        	   header : 'Inicio',
			        	   width : 120,
			        	   dataIndex : 'inicio',
			           }, {
			        	   header : 'Plazo',
			        	   width : 100,
			        	   dataIndex : 'plazo',
			           }, {
			        	   header : 'Vencimiento',
			        	   width : 100,
			        	   dataIndex : 'vencimiento',
			           }, {
			        	   header : 'Monto C/Plazo',
			        	   width : 100,
			        	   dataIndex : 'montoCortoPlazo',
			        	   align :'right',
			        	   renderer: function (value, meta, record) {
			        		   meta.attr = 'style=' + record.get('color');
			        		   return Ext.util.Format.number(
			        				   value, '0,0.00');
			        	   }
			           }, {
			        	   header : '',
			        	   width :0,
			        	   dataIndex : 'interes',
			        	   hidden : true,
			        	   hideable : false,

			           }, {
			        	   header : 'Monto L/Plazo',
			        	   width : 100,
			        	   align :'right',
			        	   dataIndex : 'montoLargoPlazo',
			        	   renderer: function (value, meta, record) {
			        		   meta.attr = 'style=' + record.get('color');
			        		   return Ext.util.Format.number(
			        				   value, '0,0.00');
			        	   }

			           }, {
			        	   header : 'Periodicidad Pagos',
			        	   width : 150,
			        	   dataIndex : 'periodicidadPagos',

			           },
			           {
			        	   header : 'Línea Autorizada',
			        	   width : 120,
			        	   align :'right',
			        	   dataIndex : 'lineaAutorizada',
			        	   renderer: function (value, meta, record) {
			        		   meta.attr = 'style=' + record.get('color');
			        		   return Ext.util.Format.number(
			        				   value, '0,0.00');
			        	   }
			           },
			           {
			        	   header : 'Línea Disponible',
			        	   width : 120,
			        	   align :'right',
			        	   dataIndex : 'lineaDisponible',
			        	   renderer: function (value, meta, record) {
			        		   meta.attr = 'style=' + record.get('color');
			        		   return Ext.util.Format.number(
			        				   value, '0,0.00');
			        	   }
			           },
			           {
			        	   header : '',
			        	   width : 100,
			        	   dataIndex : 'linea',
			        	   hidden : true,
			        	   hideable : false,
			           },
			           {
			        	   header : 'Tipo Uso',
			        	   width : 300,
			        	   dataIndex : 'idTipoFinanciamiento',
			           },
			           {
			        	   header : 'Uso Específico',
			        	   width : 160,
			        	   dataIndex : 'destinoRecurso',
			           },
			           {
			        	   header : 'Garantías',
			        	   width : 450,
			        	   dataIndex : 'garantias',
			           },
			           {
			        	   header : 'Nombre del Aval / Obligado',
			        	   width : 300,
			        	   dataIndex : 'avales',
			        	   renderer: function (value, metaData) {
			        		   var resultado="";
			        		   var array=value.split("--");
			        		   for(var i=0;i<array.length;i++){
			        			   resultado+=array[i] + '</br>';
			        		   }
			        		   return resultado;
			        	   }
			           },
			           {
			        	   header : 'Descripción Aval / Obligado',
			        	   width : 150,
			        	   dataIndex : 'descripcionAval',
			        	   renderer: function (value, metaData) {
			        		   var resultado="";
			        		   var array=value.split("--");
			        		   for(var i=0;i<array.length;i++){
			        			   resultado+=array[i] + '</br>';
			        		   }
			        		   return resultado;
			        	   }
			           },
			           ]
		}),
		viewConfig : {
			getRowClass : function(record, index) {
			}
		},
		listeners : {
			click : {
				fn : function(grid) {
				}
			},
			dblclick : {
				fn : function(grid) {}
			}
		}
	});
	NS.generaExcel=function(){
		if(NS.storePasivosFinancieros.getCount()<=0){
			Ext.Msg.alert("SET","No existen registros.");
			return;
		}
		else{
			var matrizPasivos = new Array();
			for (var i = 0; i < NS.storePasivosFinancieros.data.length; i++) {
				var registro=NS.storePasivosFinancieros.getAt(i);
				var registroPasivos= {};
				registroPasivos.banco = registro.data.banco;
				registroPasivos.importe = registro.data.importe;
				registroPasivos.importe = registro.data.importe;
				registroPasivos.tasaRef = registro.data.tasaRef;
				registroPasivos.valorTasa = registro.data.valorTasa;
				registroPasivos.inicio = registro.data.inicio;
				registroPasivos.plazo = registro.data.plazo;
				registroPasivos.vencimiento = registro.data.vencimiento;
				registroPasivos.montoCortoPlazo =  Ext.util.Format.number(registro.data.montoCortoPlazo, '0,0.00');
				registroPasivos.montoLargoPlazo = Ext.util.Format.number(registro.data.montoLargoPlazo, '0,0.00');
				registroPasivos.periodicidadPagos = registro.data.periodicidadPagos;
				registroPasivos.lineaAutorizada = Ext.util.Format.number(registro.data.lineaAutorizada, '0,0.00');
				registroPasivos.lineaDisponible = Ext.util.Format.number(registro.data.lineaDisponible, '0,0.00');
				registroPasivos.idTipoFinanciamiento = registro.data.idTipoFinanciamiento;
				registroPasivos.destinoRecurso = registro.data.destinoRecurso;
				registroPasivos.garantias = registro.data.garantias;
				registroPasivos.avales = registro.data.avales+"__"+registro.data.descripcionAval;
				
				matrizPasivos[i] = registroPasivos;
			}
			var jsonStringPasivos= Ext.util.JSON.encode(matrizPasivos);
			var forma = NS.formPasivos.getForm();
			Ext.getCmp('hdJasonP').setValue(jsonStringPasivos);
			forma.url = '/SET/jsp/bfrmwrk/gnrators/abreExcel.jsp';
			forma.standardSubmit = true;
			forma.method = 'GET';
			forma.target = '_blank';
			var el = forma.getEl().dom;
			var target = document.createAttribute("target");
			target.nodeValue = "_blank";
			el.setAttributeNode(target);
			forma.submit({target: '_blank'});
		}
	}
	NS.formPasivos = new Ext.form.FormPanel({
		title : '',
		autowidth : true,
		height : 550,
		layout : 'absolute',
		id : PF + 'formPasivos',
		name : PF + 'formPasivos',
		autoScroll : true,
		items : [

		         NS.gridDatos,
		         {
		        	 xtype: 'textfield',
		        	 x:0,
		        	 y:0,
		        	 hidden:true,
		        	 name:'hdJasonP',
		        	 id:'hdJasonP',
		        	 value: ''
		         },
		         {
		        	 xtype: 'textfield',
		        	 x:0,
		        	 y:0,
		        	 hidden:true,
		        	 name: 'nomReporte',
		        	 id: 'nomReporte',
		        	 value: 'excelPasivosFinancieros'
		         },

		         ],
		         buttons:[
		                  /*   {
		      	                    	text: 'Resumen',
		      	                    	handler: function(){

		      	                    	}
		      	                    },*/{
		      	                    	text: 'Exportar Excel',
		      	                    	handler: function(){
		      	                    		NS.generaExcel();
		      	                    	}
		      	                    },

		      	                    ],
	});



	//Inicio ventana reporte
	NS.winReporte = new Ext.Window(
			{
				title : 'Pasivos Financieros (Créditos bancarios)',
				modal : true,
				shadow : true,
				closeAction : 'hide',
				width : 1200,
				height : 600,
				layout : 'absolute',
				plain : true,
				bodyStyle : 'padding:10px;',
				buttonAlign : 'center',
				draggable : true,
				resizable : true,
				autoScroll : true,
				items : [NS.formPasivos],
				listeners : {
					show : {
						fn : function() {
							if(NS.proyecto=='CIE'&&NS.chkConcentrado.getValue()&&(vsTipoMenu == "" || vsTipoMenu == "B")){
								vsMenu = "', 'B";
							}
							var myMask = new Ext.LoadMask(Ext.getBody(), {
								store : NS.storePasivosFinancieros,
								msg : "Cargando..."
							});
							var matriz = new Array();
							for (var i = 0; i < NS.storeGridDivisa.data.length; i++) {
								var record=NS.storeGridDivisa.getAt(i);
								var registros = {};
								registros.idDivisa = record.data.idDivisa;
								registros.descDivisa = record.data.descDivisa;
								registros.valorDivisa = record.data.valorDivisa;
								matriz[i] = registros;
							}
							var jsonString = Ext.util.JSON
							.encode(matriz);
							NS.storePasivosFinancieros.baseParams.json=jsonString;
							NS.storePasivosFinancieros.baseParams.noEmpresa=parseInt(NS.txtEmpresa.getValue());
							NS.storePasivosFinancieros.load();
						}
					},
					hide : {
						fn : function() {}
					}
				}
			});

	//Fin ventana reporte
	NS.limpiar=function(){
		var myMask = new Ext.LoadMask(Ext.getBody(), {
			store : NS.storeGridDivisa,
			msg : "Cargando..."
		});
		NS.storeGridDivisa.load();
	}
	NS.ejecutar=function(){
		for (var i = 0; i < NS.storeGridDivisa.data.length; i++) {
			var record=NS.storeGridDivisa.getAt(i);
			if(record.data.valorDivisa==''||record.data.valorDivisa==0){
				Ext.Msg.alert('SET','Debe digitar todos los valores de los tipos de cambio de la pantalla');
				return;
			}
		}
		NS.winReporte.show();
	}
	NS.reportePasivosF = new Ext.form.FormPanel({
		title : 'Reporte pasivos Financieros (Créditos Bancarios)',
		width : 650,
		height : 500,
		padding : 10,
		layout : 'absolute',
		id : PF + 'reportePasivosF',
		name : PF + 'reportePasivosF',
		renderTo : NS.tabContId,
		frame : true,
		autoScroll : true,
		items : [
		         {
		        	 xtype : 'fieldset',
		        	 title : '',
		        	 id : PF + 'panelEmpresa',
		        	 name : PF + 'panelEmpresa',
		        	 x : 0,
		        	 y : 0,
		        	 width : 600,
		        	 height : 44,
		        	 layout : 'absolute',
		        	 items : [ NS.lblUsuario, NS.txtUsuario ]
		         },
		         {
		        	 xtype : 'fieldset',
		        	 title : 'Tipos de Cambio Especiales',
		        	 id : PF + 'paneTiposDeCambio',
		        	 name : PF + 'paneTiposDeCambio',
		        	 x : 0,
		        	 y : 50,
		        	 width : 600,
		        	 height : 330,
		        	 layout : 'absolute',
		        	 items : [ NS.lblEmpresa,
		        	           NS.txtEmpresa,NS.cmbEmpresa,NS.chkConcentrado,NS.gridDivisa,NS.lblNota],
		        	           buttons:[
		        	                    {
		        	                    	text: 'Ejecutar',
		        	                    	handler: function(){
		        	                    		NS.ejecutar();
		        	                    	}
		        	                    },{
		        	                    	text: 'Limpiar',
		        	                    	handler: function(){
		        	                    		NS.limpiar();
		        	                    	}
		        	                    }
		        	                    ]
		         }
		         ]
	});
	NS.reportePasivosF.setSize(Ext.get(NS.tabContId).getWidth(), Ext.get(NS.tabContId)
			.getHeight());
});