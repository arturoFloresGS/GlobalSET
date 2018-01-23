//@autor Cristian Garcia Garcia
Ext.onReady(function(){
	var NS = Ext.namespace('apps.SET.BancaElectronica.ImportaBancaElectronica');
	//variable para el tipo de movto
	NS.idUsuario =apps.SET.iUserId;
	//EL TAG ID DEL DIV DE RENDEREO SE OBTIENE DEL TAB AL QUE SE DE DIO CLICK
	NS.tabContId = apps.SET.tabContainerId;
	//NS.tabContId = 'contenedorImportaBancaElectronica';
	var PF = apps.SET.tabID+'.'; // Generar prefijo para id html
    Ext.Direct.addProvider(Ext.setapp.REMOTING_API);	
	Ext.Direct.addProvider(Ext.setapp.REMOTING_API);
	Ext.QuickTips.init(); 
	NS.fecHoy = BFwrk.Util.changeStringToDate(apps.SET.FEC_HOY);
	
	NS.sTipoMovto = 'I';
	
	//declaracion del checkbox del grid
	NS.sm = new Ext.grid.CheckboxSelectionModel({
		forceFit: false,
		fixed:true
	});

	function cambiarFecha(fecha)
	{
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
	
	function restarDias(fecha){
		fec = fecha.split("/");
        dt = new Date(parseInt(fec[2], 10), parseInt(
          fec[1], 10) - 1, parseInt(fec[0], 10));
        //dt.setMonth(dt.getMonth() + 1);
        dt.setDate(dt.getDate() - 5);
        console.log("fecha restada "+cambiarFecha(''+dt));
        return cambiarFecha(''+dt);
	}
	
	function obtenerValIni(valor)
	{
		var i=0;
		var valIni='';
		while(valor.charAt(i)!='a')
		{
			valIni+=valor.charAt(i);
			i++;
		}
		valIni.replace(",","");
		return valIni;
	}
	function obtenerValFin(valor)
	{
		var i=0;
		var valFin='';
		while(i<valor.length)
		{
			if(valor.charAt(i)=='a')
			{
				valFin=valor.substring(i+1,valor.length);
			}
			i++;
		}
		valFin.replace(",","");
		return valFin;
		
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
	NS.unformatNumber=function(num) {
		return num.replace(/(,)/g,'');
	};

		
	
	 //Datos para el store de criterio
	NS.datosCriterio= [
			['1', 'BANCO'],
			['2', 'CHEQUERA'],
			['3', 'MONTOS'],
			['4', 'FECHA VALOR'],
			['5', 'CONCEPTO']
					];
		
		// store de criterio
	 NS.storeCriterio = new Ext.data.SimpleStore({
		idProperty: 'idCriterio',
		fields: [
			{name: 'idCriterio'},
			{name: 'nombre'}
		]
		});
	NS.storeCriterio.loadData(NS.datosCriterio);
		//Store combo Bancos
    NS.storeBancos = new Ext.data.DirectStore({
		paramsAsHash: false,
		root: '',
		paramOrder:['pbInversion'],
		directFn: ImportaBancaElectronicaAction.obtenerBancos, 
		idProperty: 'idBanco', 
		fields: [
			 {name: 'idBanco'},
			 {name: 'descBanco'}
		],
		listeners:{
			load: function(s, records){
				var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeBancos, msg:"Cargando..."});
				if(records.length==null || records.length<=0)
				{
					Ext.Msg.alert('SET','No Existen bancos');
					Ext.getCmp(PF+'cmbCriterio').setDisabled(false);
				}
			}
		}
	}); 
	//Store Datos donde se muestran los valores seleccionados
	NS.storeDatos = new Ext.data.DirectStore({
		paramsAsHash: false,
		root: '',
		idProperty: 'criterio',//identificador del store
		fields: [
			{name: 'criterio'},
			{name: 'valor'}
		],
		listeners: {
			load: function(s, records){
				
			}
		}
	}); 
	//Grid para mostrar los datos seleccionados
		NS.gridDatos  = new Ext.grid.GridPanel({
		store : NS.storeDatos,
		columns : [ {
			id :'criterio',
			header :'Criterio',
			width :105,
			dataIndex :'criterio'
		}, {
			header :'Valor',
			width :147,
			dataIndex :'valor'
		} ],
		width: 260,
       	height: 110,
		x :0,
		y :0,
		title :'',
		listeners:{
			dblclick:{
				fn:function(grid){
					var records = NS.gridDatos.getSelectionModel().getSelections();
					for(var i=0;i<records.length;i++)
					{
						NS.gridDatos.store.remove(records[i]);
						NS.gridDatos.getView().refresh();
						Ext.getCmp(PF+'cmbCriterio').setDisabled(false);
					}	   
				}
			}
		}
	});
	//Store combo Chequeras
	    NS.storeChequeras = new Ext.data.DirectStore({
		paramsAsHash: false,
		baseParams:{
			noEmpresa:'',
			idBanco:'',
			pbTipoChequera:'false',
			pbInversion:'false'
		},
		root: '',
		paramOrder:['noEmpresa','idBanco','pbTipoChequera','pbInversion'],
		directFn: ImportaBancaElectronicaAction.obtenerChequeras, 
		idProperty: 'idChequera', 
		fields: [
			 {name: 'idChequera'},
			 {name: 'descChequera'}
		],
		listeners: {
			load: function(s, records){
				var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeChequeras, msg:"Cargando..."});
				if(records.length==null || records.length<=0)
				{
					Ext.Msg.alert('SET','No Existen chequeras');
					Ext.getCmp(PF+'cmbCriterio').setDisabled(false);
				}
			}
		}
	}); 
	//Store store Empresas
	    NS.storeEmpresas = new Ext.data.DirectStore({
		paramsAsHash: false,
		root: '',
		directFn: ImportaBancaElectronicaAction.obtenerEmpresas, 
		idProperty: 'noEmpresa', 
		fields: [
			{name: 'noEmpresa'},
			{name: 'nomEmpresa'}
		],
		listeners: {
			load: function(s, records){
				var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeEmpresas, msg:"Cargando..."});
				
				//Se agrega la opcion todas las empresas
	 			var recEmpresas = NS.storeEmpresas.recordType;	
				var todas = new recEmpresas({
					noEmpresa: 0,
					nomEmpresa: '************ TODAS ************'
		       	});
		   		NS.storeEmpresas.insert(0, todas);
			}
		}
	}); 
	NS.storeEmpresas.load();
	
	//Store Conceptos
	    NS.storeConceptos = new Ext.data.DirectStore({
		paramsAsHash: false,
		baseParams:{
			bancoUno:'',
			bancoDos:'',
			psTipoMovto:'I'
		},
		root: '',
		paramOrder:['bancoUno','bancoDos','psTipoMovto'],
		directFn: ImportaBancaElectronicaAction.obtenerConcepto, 
		idProperty: 'conceptoSet', 
		fields: [
			{name: 'conceptoSet'}
		],
		listeners: {
			load: function(s, records){
				var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeConceptos, msg:"Cargando..."});
				if(records.length==null || records.length<=0)
					Ext.Msg.alert('Información SET','No hay conceptos');
			}
		}
	}); 
	
		//Store para llenar el grid que muestra la consulta de movimientos
  	 NS.storeImportaBancaGrid = new Ext.data.DirectStore({
  	 	paramsAsHash: false,
  	 	baseParams: {
			optCapturados:'false',
			noEmpresa: 0,
			//chkTodas: 'false',
			tipoMovto:''+NS.sTipoMovto,
	        idUsuario:''+NS.idUsuario,
	        idBanco:'',
	        psChequera:'',
	        pdMontoIni:'',
	        pdMontoFin:'',
	        psFechaValorIni:'',
	        psFechaValorFin:'',
	        psConcepto:'',
	        lbTipoCh:'',
	        chkInversion:'false'
		},
		root: '',
		paramOrder:['optCapturados','noEmpresa','tipoMovto','idUsuario','idBanco','psChequera','pdMontoIni','pdMontoFin','psFechaValorIni','psFechaValorFin','psConcepto','lbTipoCh','chkInversion'],
		directFn: ImportaBancaElectronicaAction.llenarGridImportaBancaE,
		//idProperty: 'marca',
		fields: [
			{name: 'referencia'},
			{name: 'importe' },
			{name: 'fecValorString'},
			{name: 'sucursal'},
			{name: 'folioBanco'},
			{name: 'idChequera'},
			{name: 'descBanco'},
			{name: 'conceptoSet'},
			{name: 'noEmpresa'},
			{name: 'nomEmpresa'},
			{name: 'descConcepto'},
			{name: 'bBancaElect'},
			{name: 'transpasoDeposito'},
			{name: 'descCorresponde'},
			{name: 'corresponde'},
			{name: 'sIdentificador'},
			{name: 'sValores'},
			{name: 'noCliente'},
			{name: 'beneficiario'},
			{name: 'idDivision'},
			{name: 'importa'},
			{name: 'rechazo'},
			{name: 'liberaAut'},
		 	{name: 'idRubro'},
			{name: 'restoReferencia'},
			{name: 'idBanco'},
			{name: 'idUsuario'},
			{name: 'concepto2'},
			{name: 'cargoAbono'},
			{name: 'beneficiario'},
			{name: 'identificador'},
			{name: 'observacion'},
			{name: 'noCuentaEmp'},
			{name: 'secuencia'},
			{name: 'idCveConcepto'},
			{name: 'concepto2'},
			{name: 'descripcion'}
		],
		listeners: {
			load: function(s, records){
				var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeImportaBancaGrid, msg:"Cargando..."});
				if(records.length==null || records.length<=0)
				{
					Ext.Msg.alert('Información  SET','No existen Datos');
				}
				else
				{
          			var totalImporte=0;
          			NS.sm.selectRange(0,records.length-1);
          			var regSelec=NS.gridImportaBanca.getSelectionModel().getSelections();
          			
          			for(var k=0;k<regSelec.length;k=k+1)
          			{
     
          				if(NS.sTipoMovto=='A' && regSelec[k].get('cargoAbono')=='E'){
          					totalImporte=totalImporte-regSelec[k].get('importe');
          				}else{
          					totalImporte=totalImporte+regSelec[k].get('importe');
          				}
          				
          			}
					Ext.getCmp(PF+'totalRegistros').setValue(records.length);
					Ext.getCmp(PF+'totalImporte').setValue(NS.formatNumber(Math.round((totalImporte)*100)/100));   
          			
				}
			}
		}
	}); 
	//grid de datos
	 NS.gridImportaBanca = new Ext.grid.GridPanel({
        store : NS.storeImportaBancaGrid,
        typeAhead: true,
        id:'gridImportaBanca',
		//mode: 'local',
		mode: 'remote',
        cm: new Ext.grid.ColumnModel({
            defaults: {
                width: 120,
                value:true,
                sortable: true,
                forceFit: false,
                fixed:true
            },
            columns: [
                NS.sm,
                {
					header :'No. Empresa',
					width :70,
					sortable :true,
					dataIndex :'noEmpresa',
					forceFit: true,
                	fixed:false,
                	hidden: true
				},{
					header :'Empresa',
					width :150,
					sortable :true,
					dataIndex :'nomEmpresa',
					forceFit: true,
                	fixed:false
				},{
					header :'Banco',
					width :100,
					sortable :true,
					dataIndex :'descBanco',
					forceFit: true,
                	fixed:false
				},{
					header :'Chequera',
					width :80,
					sortable :true,
					dataIndex :'idChequera',
					forceFit: true,
                	fixed:false
				},{
					
					header :'Importe',
					width :90,
					sortable :true,
					dataIndex :'importe',
					css: 'text-align:right;',
					renderer: BFwrk.Util.rendererMoney,
					forceFit: true,
                	fixed:false
				},{
					header :'Fecha Valor',
					width :80,
					sortable :true,
					dataIndex :'fecValorString',
					forceFit: true,
                	fixed:false
				},{
					header :'Referencia',
					width :100,
					sortable :true,
					dataIndex :'referencia',
					forceFit: true,
                	fixed:false
				},{
					header: 'Sucursal',
					width: 100,
					sortable: true,
					dataIndex: 'sucursal',
					forceFit: true,
                	fixed: false,
                	hidden: true
				},{
					header :'Folio Banco',
					width :90,
					sortable :true,
					dataIndex :'folioBanco',
					forceFit: true,
                	fixed: false
				},{
					header :'Concepto',
					width :90,
					sortable :true,
					dataIndex :'conceptoSet',
					forceFit: true,
                	fixed:false
				},{
					header :'Concepto Bancario',
					width :150,
					sortable :true,
					dataIndex :'concepto2',
					forceFit: true,
                	fixed:false
				},{
					header :'Descripción Concepto',
					width :150,
					sortable :true,
					//dataIndex :'conceptoSet',
					dataIndex :'descripcion',
					forceFit: true,
                	fixed:false
				},{
					header :'Banca Electronica',
					width :100,
					sortable :true,
					dataIndex :'bBancaElect',
					forceFit: true,
                	fixed:false,
                	hidden: true
				},{
					header :'Traspaso Deposito',//inicia campos extras
					width :100,
					sortable :true,
					dataIndex :'transpasoDeposito',
					hidden: true,
					forceFit: true,
                	fixed:false
					
				},{
					header :'Desc Corresponde',
					width :100,
					sortable :true,
					dataIndex :'descCorresponde',
					hidden: true,
					forceFit: true,
                	fixed:false
				},{
					header :'Corresponde',
					width :100,
					sortable :true,
					dataIndex :'corresponde',
					forceFit: true,
                	fixed:false,
                	hidden: true
				},{
					header :'Identificador',
					width :100,
					sortable :true,
					dataIndex :'sIdentificador',
					hidden: true,
					forceFit: true,
                	fixed:false
				},{
					header :'Valores',
					width :100,
					sortable :true,
					dataIndex :'sValores',
					forceFit: true,
                	fixed:false,
                	hidden: true
				},{
					header :'No Cliente',
					width :100,
					sortable :true,
					dataIndex :'noCliente',
					hidden: true,
					forceFit: true,
                	fixed:false
				},{
					header :'Beneficiario',
					width :100,
					sortable :true,
					dataIndex :'beneficiario',
					hidden: true,
					forceFit: true,
                	fixed:false
				},{
					header :'Id Division',
					width :100,
					sortable :true,
					dataIndex :'idDivision',
					hidden: true,
					forceFit: true,
                	fixed:false
				},{
					header :'Importa',
					width :100,
					sortable :true,
					dataIndex :'importa',
					hidden: true,
					forceFit: true,
                	fixed:false
				},{
					header :'Rechazo',
					width :100,
					sortable :true,
					dataIndex :'rechazo',
					hidden: true,
					forceFit: true,
                	fixed:false
				},{
					header :'Libera AUT',
					width :100,
					sortable :true,
					dataIndex :'liberaAut',
					hidden: true,
					forceFit: true,
                	fixed:false
				},{
					header :'Res Ref',
					width :100,
					sortable :true,
					dataIndex :'restoReferencia',
					hidden: true,
					forceFit: true,
                	fixed:false
				},{
					header :'Id Rubro',
					width :100,
					sortable :true,
					dataIndex :'idRubro',
					hidden: true,
					forceFit: true,
                	fixed:false
				},{
					header :'Id Banco',
					width :100,
					sortable :true,
					dataIndex :'idBanco',
					hidden: true,
					forceFit: true,
                	fixed:false
				},{
					header :'Id Usuario',
					width :80,
					sortable :true,
					dataIndex :'idUsuario',
					hidden: true,
					forceFit: true,
                	fixed:false
				},{
					header :'Concepto 2',
					width :80,
					sortable :true,
					dataIndex :'concepto2',
					hidden: true,
					forceFit: true,
                	fixed:false
				},{
					header: 'CargoAbono',
					width: 80,
					sortable: true,
					dataIndex: 'cargoAbono',
					forceFit: true,
                	fixed: false
				},{
					header :'Beneficiario',
					width :80,
					sortable :true,
					dataIndex :'beneficiario',
					hidden: true
				},{
					header :'Identificador',
					width :80,
					sortable :true,
					dataIndex :'identificador',
					hidden: true,
					forceFit: true,
                	fixed:false
				},{
					header :'Observación',
					width :80,
					sortable :true,
					dataIndex :'observacion',
					hidden: true,
					forceFit: true,
                	fixed:false
				},{
					header :'No cuentaEmp',
					width :80,
					sortable :true,
					dataIndex :'noCuentaEmp',
					hidden: true,
					forceFit: true,
                	fixed:false
				},{
					header :'Secuencia',
					width :80,
					sortable :true,
					dataIndex :'secuencia',
					hidden: true,
					forceFit: true,
                	fixed:false
				}
				/*dtoRetornoGrid.setDescCorresponde(sDescCorresponde);
			                    dtoRetornoGrid.setCorresponde(sCorresponde);
			                    dtoRetornoGrid.setSIdentificador(sIdentificador);
			                    dtoRetornoGrid.setSValores(sValores);
			                    dtoRetornoGrid.setNoCliente(sNoCliente);
			                    dtoRetornoGrid.setBeneficiario(psBeneficiario);
			                    dtoRetornoGrid.setIdDivision(Integer.parseInt(psDivision));
			                    
			                    if(sImportaEspeciales!=null && sImportaEspeciales.equals("S"))
	                               dtoRetornoGrid.setImporta("S");
	                            else
	                               dtoRetornoGrid.setImporta(listRetorno.get(i).getImporta());
	                          
	                            dtoRetornoGrid.setBRechazo(listRetorno.get(i).getBRechazo());
	                            dtoRetornoGrid.setDescripcion(listRetorno.get(i).getDescripcion());
	                            dtoRetornoGrid.setLiberaAut(listRetorno.get(i).getLiberaAut());
	                            dtoRetornoGrid.setIdRubro(plIdRubro);//Solo aplica para Depositos CM
	                            dtoRetornoGrid.setRestoReferencia(psRestoRef);
			                   */
            ]
        }),
        sm: NS.sm,
        columnLines: true,
        width:1030,
        height:210,
        frame:true,
        listeners:{
        	click:{
        		fn:function(e){
        			var regSeleccionados = NS.gridImportaBanca.getSelectionModel().getSelections();
        			var regActuales=Ext.getCmp(PF+'totalRegistros').getValue();
        			var totalActual=Ext.getCmp(PF+'totalImporte').getValue();
        			var contImporte=0;
        			
	        		Ext.getCmp(PF+'totalRegistros').setValue(regSeleccionados.length);
	        				
        			for(var i=0;i<regSeleccionados.length;i++)
        			{
        				contImporte=contImporte+regSeleccionados[i].get('importe');
        			}
        				Ext.getCmp(PF+'totalImporte').setValue(NS.formatNumber(contImporte));
        		}
        	}
        }
    });
	//combo Criterio
	NS.cmbCriterio = new Ext.form.ComboBox({
		 store: NS.storeCriterio
		,name: PF+'cmbCriterio'
		,id: PF+'cmbCriterio'
		,typeAhead: true
		,mode: 'local'
		,minChars: 0
		,selecOnFocus: true
		,forceSelection: true
        ,x: 10
        ,y: 65
        ,width: 150
		,valueField:'idCriterio'
		,displayField:'nombre'
		,autocomplete: true
		,emptyText: 'Seleccione un criterio'
		,triggerAction: 'all'
		,value: ''
		,listeners:{
			select:{
				fn:function(combo, value) {
					var records=NS.storeCriterio.data.items;
					var tamGrid=(NS.storeDatos.data.items).length;
					for(var i=0;i<records.length;i=i+1)
						if(records[i].get('idCriterio')==combo.getValue())
							var nombreCriterio=records[i].get('nombre');
				    combo.setDisabled(true);
				    var datosClase = NS.gridDatos.getStore().recordType;
					if(combo.getValue()==1)
					{
						Ext.getCmp(PF+'cmbBancos').setVisible(true);
						Ext.getCmp(PF+'cmbBancos').setDisabled(false);
					
						Ext.getCmp(PF+'cmbConceptos').setVisible(false);
						Ext.getCmp(PF+'cmbChequeras').setVisible(false);
						Ext.getCmp(PF+'monto1').hide();
						Ext.getCmp(PF+'monto2').hide();
						Ext.getCmp(PF+'fecha1').hide();
						Ext.getCmp(PF+'fecha2').hide();	
						
						NS.storeBancos.baseParams.pbInversion='false';
						NS.storeBancos.load();		
						
						combo.setDisabled(true);
							
						var datos = new datosClase({
		                	criterio: 'BANCO',
							valor: ''
		            	});
                        NS.gridDatos.stopEditing();
	            		NS.storeDatos.insert(tamGrid, datos);
	            		NS.gridDatos.getView().refresh();
	            		
					}
					else if(combo.getValue()==2)
					{
						var indice=0;
						var i=0;
						var recordsDatGrid=NS.storeDatos.data.items;
						for(i=0;i<recordsDatGrid.length;i++)
						{
							if(recordsDatGrid[i].get('criterio')=='BANCO')
								indice=i;
						}
						if(recordsDatGrid[indice]!=undefined && (recordsDatGrid[indice].get('criterio'))=='BANCO')
						{
							for(i=0;i<recordsDatGrid.length;i++)
							{
								if(recordsDatGrid[i].get('criterio')=='CHEQUERA')
									indice=i;
							}
							if(recordsDatGrid[indice]!=undefined && (recordsDatGrid[indice].get('criterio'))=='CHEQUERA')
							{
								Ext.Msg.alert('información SET','Ya indicó la chequera necesita borrala antes');
		            			combo.setDisabled(false);
		            		}
		            		else
		            		{
		            			NS.storeChequeras.baseParams.pbTipoChequera='false';
								NS.storeChequeras.load();
								
								Ext.getCmp(PF+'cmbChequeras').setVisible(true);
								Ext.getCmp(PF+'cmbChequeras').setDisabled(false);
								
								Ext.getCmp(PF+'cmbConceptos').setVisible(false);
								Ext.getCmp(PF+'cmbBancos').setVisible(false);
								Ext.getCmp(PF+'monto1').hide();
								Ext.getCmp(PF+'monto2').hide();
								Ext.getCmp(PF+'fecha1').hide();
								Ext.getCmp(PF+'fecha2').hide();	
								combo.setDisabled(true);
								
								var datos = new datosClase({
		                			criterio: 'CHEQUERA',
									valor: ''
		            			});
	                        	NS.gridDatos.stopEditing();
		            			NS.storeDatos.insert(tamGrid, datos);
		            			NS.gridDatos.getView().refresh();
		            		}
						}
						else
						{
							Ext.Msg.alert('información SET','Debe elegir un banco');
							combo.setDisabled(false);
						}		
					}
					else if(combo.getValue()==3)
					{
						var indice=0;
						var i=0;
						var recordsDatGrid=NS.storeDatos.data.items;
						for(i=0;i<recordsDatGrid.length;i++)
						{
							if(recordsDatGrid[i].get('criterio')=='MONTOS')
								indice=i;
						}
						if(recordsDatGrid[indice]!=undefined && (recordsDatGrid[indice].get('criterio'))=='MONTOS')
						{
							Ext.Msg.alert('información SET','Ya indicó el monto necesita borralo antes');
							combo.setDisabled(false);
						}
						else if(recordsDatGrid[indice].get('criterio')=='MONTOS')
						{
							Ext.Msg.alert('información SET','Ya indicó el monto necesita borralo antes');
							combo.setDisabled(false);
						}
						else
						{
							Ext.getCmp(PF+'monto1').show();
							Ext.getCmp(PF+'monto2').show();
							Ext.getCmp(PF+'monto1').setDisabled(false);
							Ext.getCmp(PF+'monto2').setDisabled(false);
							
							Ext.getCmp(PF+'cmbConceptos').setVisible(false);
							Ext.getCmp(PF+'cmbBancos').setVisible(false);
							Ext.getCmp(PF+'fecha1').hide();
							Ext.getCmp(PF+'fecha2').hide();	
							Ext.getCmp(PF+'cmbChequeras').setVisible(false);
							
							var datos = new datosClase({
	                			criterio: 'MONTOS',
								valor: ''
	            			});
                        	NS.gridDatos.stopEditing();
	            			NS.storeDatos.insert(tamGrid, datos);
	            			NS.gridDatos.getView().refresh();
						}		
					}
					else if(combo.getValue()==4)
					{
						var indice=0;
						var i=0;
						var recordsDatGrid=NS.storeDatos.data.items;
					
						for(i=0;i<recordsDatGrid.length;i++)
						{
							if(recordsDatGrid[i].get('criterio')=='FECHA VALOR')
								indice=i;
						}
						if(recordsDatGrid[indice]!=undefined && (recordsDatGrid[indice].get('criterio'))=='FECHA VALOR')
						{
							Ext.Msg.alert('información SET','Ya indicó la fecha necesita borrarla antes');
						}
						else
						{
							Ext.getCmp(PF+'fecha1').show();
							Ext.getCmp(PF+'fecha2').show();
							Ext.getCmp(PF+'fecha1').setDisabled(false);
							Ext.getCmp(PF+'fecha2').setDisabled(false);
							
							Ext.getCmp(PF+'cmbBancos').setVisible(false);
							Ext.getCmp(PF+'cmbChequeras').setVisible(false);
							Ext.getCmp(PF+'cmbConceptos').setVisible(false);
							Ext.getCmp(PF+'monto1').hide();
							Ext.getCmp(PF+'monto2').hide();
							
							var datos = new datosClase({
	                			criterio: 'FECHA VALOR',
								valor: ''
	            			});
                        	NS.gridDatos.stopEditing();
	            			NS.storeDatos.insert(tamGrid, datos);
	            			NS.gridDatos.getView().refresh();
						}
					}
					else if(combo.getValue()==5)
					{
						
						var indice=0;
						var i=0;
						var recordsDatGrid=NS.storeDatos.data.items;
						for(i=0;i<recordsDatGrid.length;i++)
						{
							if(recordsDatGrid[i].get('criterio')=='BANCO')
								indice=i;
						}
						if(recordsDatGrid[indice]!=undefined && (recordsDatGrid[indice].get('criterio'))=='BANCO')
						{
							for(i=0;i<recordsDatGrid.length;i++)
							{
								if(recordsDatGrid[i].get('criterio')=='CONCEPTO')
									indice=i;
							}
							if(recordsDatGrid[indice]!=undefined && (recordsDatGrid[indice].get('criterio'))=='CONCEPTO')
							{
								Ext.Msg.alert('información SET','Ya indicó el concepto necesita borrarlo antes');
								combo.setDisabled(false);
		            		}
		            		else{
		            			var tipoMovto = Ext.getCmp(PF + 'optTipoMovtos').getValue();
		            			var iTipoMovto = parseInt(tipoMovto.getGroupValue());
		            			
		            			if(iTipoMovto == 0)
		            				NS.sTipoMovto = 'I';
		            			else if(iTipoMovto == 1)
		            				NS.sTipoMovto = 'E';
		            			else if(iTipoMovto == 2)
		            				NS.sTipoMovto = 'A';
		            			
								NS.storeConceptos.load();
								Ext.getCmp(PF+'cmbConceptos').setVisible(true);
								Ext.getCmp(PF+'cmbConceptos').setDisabled(false);
								
								Ext.getCmp(PF+'monto1').hide();
								Ext.getCmp(PF+'monto2').hide();
								Ext.getCmp(PF+'fecha1').hide();
								Ext.getCmp(PF+'fecha2').hide();								
								Ext.getCmp(PF+'cmbBancos').setVisible(false);
								Ext.getCmp(PF+'cmbChequeras').setVisible(false);
								
								var datos = new datosClase({
		                			criterio: 'CONCEPTO',
									valor: ''
		            			});
	                        	NS.gridDatos.stopEditing();
		            			NS.storeDatos.insert(tamGrid, datos);
		            			NS.gridDatos.getView().refresh();
		            		}
						}
						else
						{
							Ext.Msg.alert('información SET','Debe elegir un banco');
							combo.setDisabled(false);
						}
					}	
				}
			}
		}
	});	
	
		//combo Bancos
		NS.cmbBancos = new Ext.form.ComboBox({
		store: NS.storeBancos
		,name: PF+'cmbBancos'
		,id: PF+'cmbBancos'
		,typeAhead: true
		,mode: 'local'
		,minChars: 0
		,selecOnFocus: true
		,forceSelection: true
        ,x: 10
        ,y: 110
        ,width: 150
		,valueField:'idBanco'
		,displayField:'descBanco'
		,autocomplete: true
		,emptyText: 'Seleccione un banco'
		,triggerAction: 'all'
		,value: ''
		,visible: true
		,listeners:{
			select:{
				fn:function(combo, value) {
					var indice=0;
					var i=0;
					var datosClase = NS.gridDatos.getStore().recordType;
					var recordsDatGrid=NS.storeDatos.data.items;
					
						var banco=NS.storeBancos.getById(combo.getValue()).get('descBanco');
						NS.storeChequeras.baseParams.idBanco=''+combo.getValue();
						NS.storeConceptos.baseParams.bancoUno=combo.getValue();
						NS.storeConceptos.baseParams.bancoDos=0;
					 	
				    	for(i=0;i<recordsDatGrid.length;i++)
							{
								if(recordsDatGrid[i].get('criterio')=='BANCO')
								{
									indice=i;
									NS.storeDatos.remove(recordsDatGrid[indice]);
								}
							}
							
				    	var datos = new datosClase({
	                			criterio: 'BANCO',
								valor: banco
	            			});
                       	NS.gridDatos.stopEditing();
            			NS.storeDatos.insert(indice, datos);
            			NS.gridDatos.getView().refresh();
            			
						combo.setDisabled(true);
						Ext.getCmp(PF+'cmbCriterio').setDisabled(false);
				}
			}
		}
	});	

	//combo Chequeras
		NS.cmbChequeras = new Ext.form.ComboBox({
		store: NS.storeChequeras
		,name: PF+'cmbChequeras'
		,id: PF+'cmbChequeras'
		,typeAhead: true
		,mode: 'local'
		,minChars: 0
		,selecOnFocus: true
		,forceSelection: true
        ,x: 10
        ,y: 110
        ,width: 150
		,valueField:'idChequera'
		,displayField:'idChequera'
		,autocomplete: true
		,emptyText: 'Seleccione una chequera'
		,triggerAction: 'all'
		,value: ''
		,visible: false
		,listeners:{
			select:{
				fn:function(combo, value) {
						var indice=0;
						var i=0;
						var datosClase = NS.gridDatos.getStore().recordType;
						var recordsDatGrid=NS.storeDatos.data.items;				
						
						combo.setDisabled(true);
						Ext.getCmp(PF+'cmbCriterio').setDisabled(false);
						
						for(i=0;i<recordsDatGrid.length;i++)
						{
							if(recordsDatGrid[i].get('criterio')=='CHEQUERA')
							{
								indice=i;
								NS.storeDatos.remove(recordsDatGrid[indice]);
							}
						}
				    	var datos = new datosClase({
	                			criterio: 'CHEQUERA',
								valor: combo.getValue()
            			});
                       	NS.gridDatos.stopEditing();
	            		NS.storeDatos.insert(indice, datos);
            			NS.gridDatos.getView().refresh();
				}
			}
		}
	});	
	
	//combo Empresas
	NS.cmbEmpresas = new Ext.form.ComboBox({
		 store: NS.storeEmpresas
		,name: PF+'cmbEmpresas'
		,id: PF+'cmbEmpresas'
		,typeAhead: true
		,mode: 'local'
		,minChars: 0
		,selecOnFocus: true
		,forceSelection: true
        ,x: 10
        ,y: 20
        ,width: 250
		,valueField:'noEmpresa'
		,displayField:'nomEmpresa'
		,autocomplete: true
		,emptyText: 'Seleccione una empresa'
		,triggerAction: 'all'
		,value: ''
		,listeners:{
			select:{
				fn:function(combo, value) {
					NS.storeChequeras.baseParams.noEmpresa = '' + combo.getValue();
					NS.storeImportaBancaGrid.baseParams.noEmpresa = combo.getValue() == '' ? 0 : combo.getValue();
					NS.storeImportaBancaGrid.baseParams.idUsuario = NS.idUsuario;
				}
			}
		}
	});	
	
			//combo Comceptos
	NS.cmbConceptos = new Ext.form.ComboBox({
		 store: NS.storeConceptos
		,name: PF+'cmbConceptos'
		,id: PF+'cmbConceptos'
		,typeAhead: true
		,mode: 'local'
		,minChars: 0
		,selecOnFocus: true
		,forceSelection: true
      	,x: 10
        ,y: 110
        ,width: 150
		,valueField:'conceptoSet'
		,displayField:'conceptoSet'
		,autocomplete: true
		,emptyText: 'Seleccione un concepto'
		,triggerAction: 'all'
		,value: ''
		,visible: false
		,listeners:{
			select:{
				fn:function(combo, value) {
					var indice=0;
					var i=0;
					var datosClase = NS.gridDatos.getStore().recordType;
					var recordsDatGrid=NS.storeDatos.data.items;				
					
					combo.setDisabled(true);
					Ext.getCmp(PF+'cmbCriterio').setDisabled(false);
					
					for(i=0;i<recordsDatGrid.length;i++)
					{
						if(recordsDatGrid[i].get('criterio')=='CONCEPTO')
						{
							indice=i;
							NS.storeDatos.remove(recordsDatGrid[indice]);
						}
					}
			    	var datos = new datosClase({
                			criterio: 'CONCEPTO',
							valor: combo.getValue()
           			});
                    NS.gridDatos.stopEditing();
           			NS.storeDatos.insert(indice, datos);
           			NS.gridDatos.getView().refresh();
				}
			}
		}
	});
	
	NS.optTipoMovtos = new Ext.form.RadioGroup({
		id: PF + 'optTipoMovtos',
		name: PF + 'optTipoMovtos',
		x: 0,
		y: 0,
		columns: 3, //muestra los radiobuttons en tres columnas
		items: [
	          {boxLabel: 'Ingresos', name: 'optIEA', inputValue: 0, checked: true},  
	          {boxLabel: 'Egresos', name: 'optIEA', inputValue: 1},
	          {boxLabel: 'Ambos', name: 'optIEA', inputValue: 2}
	     ]
	});
	
	var winLogin = new Ext.Window({
								title: 'Importar Pendientes'
								,modal:true
								,shadow:true
								,width: 350
							   	,height:300
							   	,minWidth: 400
							   	,minHeight: 580
							   	,layout: 'fit'
							   	,plain:true
							    ,bodyStyle:'padding:20px;'
							   	,buttonAlign:'center'
							   	,closable: false
							   	,items: [
							   	{
							   	xtype: 'fieldset',
				                title: 'Rango de fechas a importar',
				                id:PF+'framePendiente',
				                name:PF+'framePendientes',
				                x: 10,
				                y: 10,
				                width: 750,
				                height: 10,
				                layout: 'absolute',
				                items: [
							   		{
			                   		xtype:'datefield',
			                   		format:'d/m/Y',
			                   		id:PF+'fechaUnoPendientes',
			                   		name:PF+'fechaUnoPendientes',
			                   		x:20,
			                   		y:10,
			                   		width:100,
			                   		hidden:false,
			                   		allowBlank: false,
			                   		blankText:'La fecha inicial es requerida',
			                   		listeners:{
			                   			change:{
			                   				fn:function(caja,valor){
													
			
								                   				}
								                   			}
								                   	}
								             }
								             ,{
			                   		xtype:'datefield',
			                   		format:'d/m/Y',
			                   		id:PF+'fechaDosPendientes',
			                   		name:PF+'fechaDosPendientes',
			                   		x:140,
			                   		y:10,
			                   		width:100,
			                   		hidden:false,
			                   		allowBlank: false,
			                   		blankText:'La fecha final es requerida',
			                   		listeners:{
			                   			change:{
			                   				fn:function(caja,valor){
			
								                   				}
								                   			}
								                   	}
								             }
								          ]
								        },
								        	{
							   	xtype: 'fieldset',
				                title: 'Banco',
				                id:PF+'framePendienteBancos',
				                name:PF+'framePendientesBancos',
				                x: 10,
				                y: 60,
				                width: 750,
				                height: 10,
				                layout: 'absolute',
				                items: [
							   		
									]
								}
							]
						   	,buttons: [{
						 	text: 'Ejecutar',
							handler: function(e) {
							 	winLogin.hide();
							 }
						     }]
						});
	
	NS.buscar = function() {
		var recordsDatosGrid = NS.storeDatos.data.items;
		
		if(recordsDatosGrid.length > 0)
		{
			for(var i=0;i<recordsDatosGrid.length;i=i+1)
			{
				if(recordsDatosGrid[i].get('criterio')==='BANCO')
				{
					var valorBanco=recordsDatosGrid[i].get('valor');
					if(valorBanco!='')
					{
						var recordsDataBanco=NS.storeBancos.data.items;
						for(var j=0;j<recordsDataBanco.length;j=j+1)
						{
							if(recordsDataBanco[j].get('descBanco')===valorBanco)
							{
								var idBanco=0;
								idBanco=recordsDataBanco[j].get('idBanco');
								NS.storeImportaBancaGrid.baseParams.idBanco=''+idBanco;
							}
						}
					}
					else{
						Ext.Msg.alert('Información SET','Debe agregar un valor a banco');
					}
				}
				else if(recordsDatosGrid[i].get('criterio')==='CHEQUERA')
				{
					var valorChequera=recordsDatosGrid[i].get('valor');
					if(valorChequera!='')
					{
						NS.storeImportaBancaGrid.baseParams.psChequera=''+valorChequera;
					}
					else{
						Ext.Msg.alert('Información SET','Debe agregar un valor a la chequera');
					}
				}
				else if(recordsDatosGrid[i].get('criterio')==='MONTOS')
				{
					var valorMontos=recordsDatosGrid[i].get('valor');
					if(valorMontos!='')
					{
						var ini=obtenerValIni(valorMontos);
						var fin=obtenerValFin(valorMontos);
						
						NS.storeImportaBancaGrid.baseParams.pdMontoIni=NS.unformatNumber(''+ini);
						NS.storeImportaBancaGrid.baseParams.pdMontoFin=NS.unformatNumber(''+fin);
						
					}
					else{
						Ext.Msg.alert('Información SET','Debe agregar un valor a montos');
					}
				}
				else if(recordsDatosGrid[i].get('criterio')==='FECHA VALOR')
				{
					var valorFecha=recordsDatosGrid[i].get('valor');
					if(valorFecha!=''){
						var ini=obtenerValIni(valorFecha);
						var fin=obtenerValFin(valorFecha);
						//alert('Val inicial'+ini);
						//alert('Val final'+fin);
						NS.storeImportaBancaGrid.baseParams.psFechaValorIni=''+ini.replace(/^\s*|\s*$/g,"");
						NS.storeImportaBancaGrid.baseParams.psFechaValorFin=''+fin.replace(/^\s*|\s*$/g,"");
						
					}
					else{
						Ext.Msg.alert('Información SET','Debe agregar un valor a la fecha');
					}
				}
				else if(recordsDatosGrid[i].get('criterio')==='CONCEPTO')
				{
					var valorConcepto=recordsDatosGrid[i].get('valor');
					if(valorConcepto!=''){
						 NS.storeImportaBancaGrid.baseParams.psConcepto=valorConcepto;
					}
					else{
						Ext.Msg.alert('Información SET','Debe agregar un valor al concepto');
					}
				}
			}
			var tipoMovto = Ext.getCmp(PF + 'optTipoMovtos').getValue();
			var iTipoMovto = parseInt(tipoMovto.getGroupValue());
			
			if(iTipoMovto == 0)
				NS.sTipoMovto = 'I';
			else if(iTipoMovto == 1)
				NS.sTipoMovto = 'E';
			else if(iTipoMovto == 2)
				NS.sTipoMovto = 'A';
			
			NS.storeImportaBancaGrid.baseParams.tipoMovto = NS.sTipoMovto;
			NS.storeImportaBancaGrid.load();
			NS.gridImportaBanca.getView().refresh();
		}
		else
		{
			Ext.Msg.alert('Información SET','Debe agregar datos para la búsqueda');
		}
	};
					
	
NS.contenedorImportaBancaElectronica=new Ext.FormPanel({
    title: 'Importa Banca Electrónica',
    width: 772,
    height: 574,
    padding: 10,
    frame: true,
    autoScroll: true,
    layout: 'absolute',
    id: PF + 'importaBanca',
    name: PF+ 'importaBanca',
    renderTo: NS.tabContId,
        items :[
            {
                xtype: 'fieldset',
                title: 'búsqueda',
                id:PF+'frameBusqueda',
                name:PF+'frameBusqueda',
                x: 10,
                y: 0,
                width: 1050,
                height: 175,
                layout: 'absolute',
                items: [
                    {
                        xtype: 'label',
                        text: 'Criterio:',
                        id:PF+'lblCriterio',
                        name:PF+'lblCriterio',
                        x: 10,
                        y: 50
                    },
                    {
                        xtype: 'label',
                        text: 'Valor:',
                        id:PF+'lblValor',
                        name:PF+'lblValor',
                        x: 10,
                        y: 95
                    },
                    {
                        xtype: 'fieldset',
                        title: '',
                        id:PF+'contArchCap',
                        name:PF+'contArchCap',
                        x: 620,
                        y: 80,
                        width: 240,
                        height: 40,
                        layout: 'absolute',
                        items: [new Ext.form.RadioGroup({
	                           	vertical: false,
	                           	id:PF+'optArchCap',
	                           	name:PF+'optArchCap',
	                           	items:[
		                            {
		                                boxLabel: 'Archivos B.E.',
		                                name:PF+'id-2',
                                        checked:true,
                                        listeners:{
                                           		check:{
                                           			fn:function(opt, valor)
                                           			{
                                           				if(valor==true)
                                           				NS.storeImportaBancaGrid.baseParams.optCapturados='false';
                                           			}
                                           		}
                                           	}
		                            },
		                            {
		                               boxLabel: 'Capturados',
		                               name:PF+'id-2',
		                               listeners:{
                                           		check:{
                                           			fn:function(opt, valor)
                                           			{
                                           				if(valor==true)
                                           				NS.storeImportaBancaGrid.baseParams.optCapturados=''+valor;
                                           			}
                                           		}
                                           	}
		                            }
	                            ]
	                          })
                        ]
                    },
                    {
                        xtype: 'fieldset',
                        id:PF+'contTipoMovto',
                        name:PF+'contTipoMovto',                        
                        title: '',
                        x: 620,
                        y: 20,
                        width: 240,
                        height: 40,
                        layout: 'absolute',
                        items: [NS.optTipoMovtos/*,
                                new Ext.form.RadioGroup(
                        			{
                                    vertical: false,
                                    id:PF+'optIngEgr',
                                    name:PF+'optIngEgr',									                           
                                    items: [
                                            {
                                             boxLabel: 'Ingresos', 
                                             name:PF+'id-1', 
                                             inputValue: 1,
                                             checked:true,
                                             listeners:{
                                           		check:{
                                           			fn:function(opt, valor)
                                           			{
                                           				if(valor==true)
                                           				{
                                           					NS.storeConceptos.baseParams.psTipoMovto='I';
                                           					NS.storeImportaBancaGrid.baseParams.tipoMovto='I';
                                           					NS.tipoMovto='I';
                                           				}
                                           			}
                                           		}
                                           	}
                                            },{
                                             boxLabel: 'Egresos',
                                             name: PF+'id-1',
                                             inputValue: 2,
                                             listeners:{
                                           		check:{
                                           			fn:function(opt, valor)
                                           			{
                                           				if(valor==true)
                                           				{
                                           					NS.storeConceptos.baseParams.psTipoMovto='E';
                                           					NS.storeImportaBancaGrid.baseParams.tipoMovto='E';		
                                           					NS.tipoMovto='E';
                                           				}
                                           			}
                                           		}
                                           	}
                                            },{
                                             boxLabel: 'Ambos', 
                                             name: PF+'id-1',
                                             inputValue: 3, 
                                             listeners:{
                                           		check:{
                                           			fn:function(opt, valor)
                                           			{
                                           				if(valor==true)
                                           				{
                                           					NS.storeConceptos.baseParams.psTipoMovto='A';
                                           					NS.storeImportaBancaGrid.baseParams.tipoMovto='A';
                                           					NS.tipoMovto='A';
                                           				}
                                           			}
                                           		}
                                           	}
                                            }
                                    ]    

                            })*/
                        ]
                    },
                    {
                        xtype: 'container',
                        x: 310,
                        y: 20,
                        width: 280,
                        height: 145,
                        items: [
                               NS.gridDatos
                        ]
                    },
                   NS.cmbCriterio,
                   NS.cmbBancos,
                   NS.cmbConceptos,
                   NS.cmbChequeras,
                   
                   	{
                   	   xtype: 'numberfield',
                   	   id:PF+'monto1',
                   	   name:PF+'monto1',
                       x: 10,
                       y: 110,
                       width: 100,
                       hidden: true,
                       allowBlank: false,
                       blankText:'El monto uno es requerido',
                       listeners:{
                       		change:{
								fn:function(caja,valor) {
									var indice=0;
									var i=0;
									var datosClase = NS.gridDatos.getStore().recordType;
									var recordsDatGrid=NS.storeDatos.data.items;				
									
									//Ext.getCmp('cmbCriterio').setDisabled(false);
									
									for(i=0;i<recordsDatGrid.length;i++)
									{
										if(recordsDatGrid[i].get('criterio')=='MONTOS')
										{
											indice=i;
											NS.storeDatos.remove(recordsDatGrid[indice]);
										}
									}
							    	var datos = new datosClase({
				                			criterio: 'MONTOS',
											valor: NS.formatNumber(valor)
				           			});
				                    NS.gridDatos.stopEditing();
				           			NS.storeDatos.insert(indice, datos);
				           			NS.gridDatos.getView().refresh();
								}
                       		}
                       	}
                   	},
                   	{
                   		xtype:'numberfield',
                   		id:PF+'monto2',
                   		name:PF+'monto2',
                   		x:120,
                   		y:110,
                   		width: 100,
                   		hidden: true,
                   		allowBlank: false,
                   		blankText:'El monto dos es requerido',
                   		listeners:{
                   			change:{
                   				fn:function(caja, valor)
                   				{
                   					var indice=0;
									var i=0;
									var datosClase = NS.gridDatos.getStore().recordType;
									var recordsDatGrid=NS.storeDatos.data.items;		
									var valorUno = NS.formatNumber(Ext.getCmp(PF+'monto1').getValue());
									if(valorUno!='')
										valorUno+=' a '
									//Ext.getCmp('cmbCriterio').setDisabled(false);
									
									for(i=0;i<recordsDatGrid.length;i++)
									{
										if(recordsDatGrid[i].get('criterio')=='MONTOS')
										{
											indice=i;
											NS.storeDatos.remove(recordsDatGrid[indice]);
										}
									}
							    	var datos = new datosClase({
				                			criterio: 'MONTOS',
											valor: valorUno+NS.formatNumber(valor)
				           			});
				                    NS.gridDatos.stopEditing();
				           			NS.storeDatos.insert(indice, datos);
				           			NS.gridDatos.getView().refresh();
				           			
                   					if(valor!='')
                   					{
                   						Ext.getCmp(PF+'cmbCriterio').setDisabled(false);
                   						Ext.getCmp(PF+'monto1').setDisabled(true);
                   						caja.setDisabled(true);
                   					}
                   				}
                   			}
                   		}
                   	},
                   	{
                   		xtype:'datefield',
                   		format:'d/m/Y',
                   		id:PF+'fecha1',
                   		name:PF+'fecha1',
                   		x:10,
                   		y:110,
                   		width:100,
                   		hidden:true,
                   		allowBlank: false,
                   		blankText:'La fecha inicial es requerida',
                   		listeners:{
                   			change:{
                   				fn:function(caja,valor){
                   					var indice=0;
									var i=0;
									var datosClase = NS.gridDatos.getStore().recordType;
									var recordsDatGrid=NS.storeDatos.data.items;				
									for(i=0;i<recordsDatGrid.length;i++)
									{
										if(recordsDatGrid[i].get('criterio')=='FECHA VALOR')
										{
											indice=i;
											NS.storeDatos.remove(recordsDatGrid[indice]);
										}
									}
									var valorFecha='';
									if(valor!='')
									{
										valorFecha=cambiarFecha(''+valor);
									}
							    	var datos = new datosClase({
				                			criterio: 'FECHA VALOR',
											valor: valorFecha
				           			});
				                    NS.gridDatos.stopEditing();
				           			NS.storeDatos.insert(indice, datos);
				           			NS.gridDatos.getView().refresh();
                   				}
                   			}
                   		}
                   	},
                   	{
                   		xtype:'datefield',
                   		format:'d/m/Y',
                   		id:PF+'fecha2',
                   		name:PF+'fecha2',
                   		x:120,
                   		y:110,
                   		width:100,
                   		hidden:true,
                   		allowBlank: false,
                   		blankText:'La fecha final es requerida',
						listeners:{
                   			change:{
                   				fn:function(caja,valor){
                   					var indice=0;
									var i=0;
									var datosClase = NS.gridDatos.getStore().recordType;
									var recordsDatGrid=NS.storeDatos.data.items;	
									var fechaUno=Ext.getCmp(PF+'fecha1').getValue();
									var valIni='';
									if(fechaUno!='')
									{
										valIni=cambiarFecha(''+fechaUno);
									}
									if(valIni!='')
									{
										valIni+=' a ';
									}			
									Ext.getCmp(PF+'cmbCriterio').setDisabled(false);
									for(i=0;i<recordsDatGrid.length;i++)
									{
										if(recordsDatGrid[i].get('criterio')=='FECHA VALOR')
										{
											indice=i;
											NS.storeDatos.remove(recordsDatGrid[indice]);
										}
									}
									var valFin='';
									if(valor!='')
									{
										valFin=cambiarFecha(''+valor);
									}
									if(fechaUno>valor)
									{
										Ext.Msg.alert('información SET','La fecha inicial debe ser menor a la final');
									}
									else
									{
								    	var datos = new datosClase({
					                			criterio: 'FECHA VALOR',
												valor: valIni+valFin
					           			});
					                    NS.gridDatos.stopEditing();
					           			NS.storeDatos.insert(indice, datos);
					           			NS.gridDatos.getView().refresh();
                   					}
                   				}
                   			}
                   		}          		
                   	},
                    {
                        xtype: 'button',
                        text: 'Buscar',
                        x: 920,
                        y: 20,
                        width: 80,
                        listeners:{
                        	click:{
                        		fn:function(e){
                        			NS.buscar();
                        		}
                        	}
                        }
                    },
                   NS.cmbEmpresas,
                    {
                        xtype: 'label',
                        text: 'Empresa:',
                        id:PF+'lblEmpresa',
                        name:PF+'lblEmpresa',
                        x: 10,
                        y: 5
                    },
                    {
                        xtype: 'checkbox',
                        id:PF+'todas',
                        name:PF+'todas',
                        x: 370,
                        y: 10,
                        boxLabel: 'Todas',
                        hidden: true,
                        listeners:{
                        	check:{
                        		fn:function(checkBox,valor)
                        		{
                        		if(valor==true)
                     				NS.storeImportaBancaGrid.baseParams.chkTodas=''+valor;
	                        	}
                        	}
                        }
                    },
                     {
                        xtype: 'checkbox',
                        id:PF+'pbInversion',
                        name:PF+'pbInversion',
                        x: 10,
                        y: 140,
                        boxLabel: 'Chequeras de Inversión',
                        hidden: true,
                        listeners:{
                        	check:{
                        		fn:function(checkBox,valor)
                        		{
                        			if(valor==true)
                        			{
	                     				NS.storeChequeras.baseParams.pbInversion=''+valor;
	                     				NS.storeBancos.baseParams.pbInversion=''+valor;
	                     				NS.storeImportaBancaGrid.baseParams.chkInversion=''+valor;
                        			}
                        		}
                        	}
                        }
                    }
                ]
            },
            {
                xtype: 'fieldset',
                title: 'Depósitos Banca Electrónica',
                id:PF+'depBancaElectronica',
                name:PF+'depBancaElectronica',
                x: 10,
                y: 180,
                width: 1050,
                height: 280,
                layout: 'absolute',
                items: [
                NS.gridImportaBanca,
                    {
                        xtype: 'textfield',
                        id:PF+'totalRegistros',
                        name:PF+'totalRegistros',
                        x: 85,
                        y: 220,
                        width: 50
                    },
                    {
                        xtype: 'textfield',
                        id:PF+'totalImporte',
                        name:PF+'totalImporte',
                        x: 220,
                        y: 220,
                        width: 110
                    },
                    {
                        xtype: 'label',
                        text: 'Total Registros:',
                        id:PF+'lblTotalRegistros',
                        name:PF+'lblTotalRegistros',
                        x: 5,
                        y: 225
                    },
                    {
                        xtype: 'label',
                        id:PF+'lblMontoTotal',
                        name:PF+'lblMontoTotal',
                        text: 'Monto Total:',
                        x: 155,
                        y: 225
                    }
                ]
            },
            {
                xtype: 'button',
                text: 'Ejecutar',
                id:PF+'btnEjecutar',
                name:PF+'btnEjecutar',
                x: 680,
                y: 470,
                width: 80,
                listeners: {
                	click: {
                		fn:function(e){
            				var matriz = new Array (); 
            				var registrosSelec = NS.gridImportaBanca.getSelectionModel().getSelections();
            				
            				if(registrosSelec.length == 0) {
            					Ext.Msg.alert('SET','Es necesario seleccionar algun registro!!');
            					return;
            				}
            				
            				Ext.MessageBox.show({
						       title : 'SET',
						       msg : 'Importando, espere por favor...',
						       width : 300,
						       wait : true,
						       progress:true,
						       waitConfig : {interval:200}//,
						       //icon :'lupita'
					           //icon :'forma_alta', //custom class in msg-box.html
					           ///animateTarget : 'mb7'
					   		});
            				
            				for(var i=0; i<registrosSelec.length; i++) {
            					if(registrosSelec[i].get('transpasoDeposito')=='S') {
            						Ext.Msg.confirm('Información SET','Este movimiento puede ser un traspaso o un depósito '
	            						+'Banco: '+registrosSelec[i].get('descBanco')+' Chequera: '+registrosSelec[i].get('idChequera')
	            						+' Concepto: '+registrosSelec[i].get('concepto')+' Importe: '+registrosSelec[i].get('importe')
	            						+' ¿Desea importar el movimiento?',function(btn) {  
		            					
            							if(btn == 'yes') {
		            						
		            					}
            						});
            					}
            					var reg = {};
            					
            					reg.referencia = registrosSelec[i].get('referencia');
            					reg.importe = registrosSelec[i].get('importe');
            					reg.fecValorString = registrosSelec[i].get('fecValorString');
            					
            					reg.sucursal = registrosSelec[i].get('sucursal');
            					reg.folioBanco = registrosSelec[i].get('folioBanco');
            					reg.idChequera = registrosSelec[i].get('idChequera');
            					reg.descBanco = registrosSelec[i].get('descBanco');
            					
            					reg.concepto = registrosSelec[i].get('concepto2');
            					reg.noEmpresa = registrosSelec[i].get('noEmpresa');
            					reg.nomEmpresa = registrosSelec[i].get('nomEmpresa');
            					reg.descConcepto = registrosSelec[i].get('conceptoSet');
            					reg.bBancaElect = registrosSelec[i].get('bBancaElect');
            					reg.transpasoDeposito = registrosSelec[i].get('transpasoDeposito');
            					reg.descCorresponde= registrosSelec[i].get('descCorresponde');
            					reg.corresponde=registrosSelec[i].get('corresponde');
            					reg.sIdentificador=registrosSelec[i].get('sIdentificador');
            					reg.sValores=registrosSelec[i].get('sValores');
							 	reg.noCliente=registrosSelec[i].get('noCliente');
								reg.beneficiario=registrosSelec[i].get('beneficiario');
								reg.idDivision=registrosSelec[i].get('idDivision');
								reg.importa=registrosSelec[i].get('importa');
								reg.rechazo=registrosSelec[i].get('rechazo');
								reg.liberaAut=registrosSelec[i].get('liberaAut');
								reg.idRubro=registrosSelec[i].get('idRubro');
								reg.restoReferencia=registrosSelec[i].get('restoReferencia');
								reg.idBanco=registrosSelec[i].get('idBanco');
								reg.fecValorString=registrosSelec[i].get('fecValorString');
								reg.idUsuario=registrosSelec[i].get('idUsuario');
								reg.noCuentaEmp=registrosSelec[i].get('noCuentaEmp');
								reg.secuencia=registrosSelec[i].get('secuencia');
								reg.descripcion = registrosSelec[i].get('descripcion');
								reg.idCveConcepto = registrosSelec[i].get('idCveConcepto');
								reg.cargoAbono = registrosSelec[i].get('cargoAbono');
								
								matriz[i] = reg; 
                			}
                			var jsonString = Ext.util.JSON.encode(matriz);
							
            				ImportaBancaElectronicaAction.obtenerBarridoGrid(jsonString, NS.sTipoMovto, function(result, e) {
								if(result.msgUsuario != null  &&  result.msgUsuario != '') {
									Ext.Msg.alert('SET', '' + result.msgUsuario);
								}else if(result.regImportados != null && result.regImportados != '') {
									Ext.Msg.alert('SET','Importados: ' + result.regImportados);
									NS.gridImportaBanca.getView().refresh();
									NS.buscar();
								}
							});
	               		}
               		}
                }
            },
            {
                xtype: 'button',
                text: 'Limpiar',
                id:PF+'btnLimpiar',
                name:PF+'btnLimpiar',
                x: 770,
                y: 470,
                width: 80,
                listeners:{
                	click:{
                		fn:function(e){
                		
                			NS.storeImportaBancaGrid.baseParams.optCapturados='false';
							NS.storeImportaBancaGrid.baseParams.noEmpresa = 0;
							//NS.storeImportaBancaGrid.baseParams.chkTodas= 'false';
							NS.storeImportaBancaGrid.baseParams.tipoMovto='I';
					        NS.storeImportaBancaGrid.baseParams.idUsuario='';
					        NS.storeImportaBancaGrid.baseParams.idBanco='';
					        NS.storeImportaBancaGrid.setBaseParam('psChequera','');
					        NS.storeImportaBancaGrid.baseParams.pdMontoIni='';
					        NS.storeImportaBancaGrid.baseParams.pdMontoFin='';
					        NS.storeImportaBancaGrid.baseParams.psFechaValorIni='';
					        NS.storeImportaBancaGrid.baseParams.psFechaValorFin='';
					        NS.storeImportaBancaGrid.baseParams.psConcepto='';
					        NS.storeImportaBancaGrid.baseParams.lbTipoCh='';
					        NS.storeImportaBancaGrid.baseParams.chkInversion='false';
                		
                			NS.contenedorImportaBancaElectronica.getForm().reset();
                			NS.gridDatos.store.removeAll();
							NS.gridDatos.getView().refresh();
							NS.gridImportaBanca.store.removeAll();
							NS.gridImportaBanca.getView().refresh();
							
							NS.storeBancos.removeAll();
							NS.storeChequeras.removeAll();
							NS.storeImportaBancaGrid.removeAll();
							NS.storeConceptos.removeAll();
							
							//Mostrar nuevamente los criterios
							Ext.getCmp(PF+'cmbConceptos').setVisible(true);
							Ext.getCmp(PF+'cmbConceptos').setDisabled(false);
                		}
                	}
                }
            },
            {
                xtype: 'button',
                id:PF+'btnImprimir',
                name:PF+'btnImprimir',
                text: 'Imprimir',
                x: 860,
                y: 470,
                width: 80
            },
            {
                xtype: 'button',
                id:PF+'btnEliminar',
                name:PF+'btnEliminar',
                text: 'Eliminar',
                x: 950,
                y: 470,
                width: 80,
                listeners:{
                	click:{
                		fn:function(e){
                			var matrizElimi= new Array();
                			var recordsEliminar=NS.gridImportaBanca.getSelectionModel().getSelections();
                			var regElimi={};
                			if(recordsEliminar.length===0)
                			{
                				Ext.Msg.alert('Información SET','Es necesario seleccionar algun registro');
                			}
                			else
                			{
	                		   Ext.Msg.confirm('Información SET','¿Esta seguro de cancelar '+recordsEliminar.length+' movimientos?',function(btn){  
	                		   	if(btn === 'yes')		
	                		   	{
		                			for (var inc=0; inc<recordsEliminar.length;inc=inc+1)
		                			{
		                				regElimi.secuencia=recordsEliminar[inc].get('secuencia');
		                				matrizElimi[inc]=regElimi;
		                			}
		                			
		                			var jsonString = Ext.util.JSON.encode(matrizElimi);	
										ImportaBancaElectronicaAction.cancelarMovimientos(jsonString, function(result, e){
											if(result===0)
											{
												Ext.Msg.alert('Información SET','No se cancelaron los movimientos');
											}
											else
											{
												Ext.Msg.alert('Información SET','Se cancelaron '+ result+ ' movimientos');
												
												for (var c=0; c<recordsEliminar.length;c=c+1)
					                			{
					                				NS.gridImportaBanca.store.remove(recordsEliminar[c]);
					                				NS.gridImportaBanca.getView().refresh();
					                				matrizElimi[inc]=regElimi;
					                			}
											}
										});
								 }
                			  })
                			}
                		}
                	}
                }
            }/*,
            {
                xtype: 'button',
                id:PF+'btnImpPendientes',
                name:PF+'btnImpPendientes',
                text: 'Importar Pendientes',
                x: 660,
                y: 510,
                listeners:{
                	click:{
                		fn:function(e){
                		
                			winLogin.show("hello-win");
	                			 
                		}
                	}
                }
            }*/
        ]
        
});

NS.valoresIniciales = function()
{
	var tamGrid=(NS.storeDatos.data.items).length;
	var datosClase = NS.gridDatos.getStore().recordType;
	var fecha=cambiarFecha(''+NS.fecHoy);
	
	   var datos = new datosClase({
		criterio: 'FECHA VALOR',
			valor: restarDias(fecha)+' a '+restarDias(fecha)
	});
	NS.gridDatos.stopEditing();
	NS.storeDatos.insert(tamGrid, datos);
	NS.gridDatos.getView().refresh();
};
NS.valoresIniciales();

NS.contenedorImportaBancaElectronica.setSize(Ext.get(NS.tabContId).getWidth(), Ext.get(NS.tabContId).getHeight());
//staticCheck("#gridImportaBanca  div div[class='x-panel-ml']","#gridImportaBanca  div div[class='x-panel-ml']",8,".x-grid3-scroller",false);
});