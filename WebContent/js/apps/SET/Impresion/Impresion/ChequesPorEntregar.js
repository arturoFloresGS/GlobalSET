/**
 * Luis Alfredo Serrato Montes de Oca
 * 16/12/2015
 */


Ext.onReady(function (){
	
	var NS = Ext.namespace('apps.SET.Impresion.Impresion.ChequesPorEntregar');
	NS.tabContId = apps.SET.tabContainerId;
	NS.idUsuario = apps.SET.iUserId;
	NS.fecHoy = BFwrk.Util.changeStringToDate(apps.SET.FEC_HOY);
	var PF = apps.SET.tabID + '.';
	Ext.Direct.addProvider(Ext.setapp.REMOTING_API);
	Ext.QuickTips.init();
	NS.GI_ID_EMPRESA = apps.SET.NO_EMPRESA;
	NS.opcionesSol='solicitantes';
	
	/*
	 * LABEL'S/TEXTBOX'S
	 */
	

	Ext.override(Ext.grid.GridView, {
	    getColumnWidth : function(col){
	        var w = this.cm.getColumnWidth(col);
	        if(typeof w == 'number'){
	            return (Ext.isBorderBox || Ext.isSafari3 ? w : (w-this.borderWidth > 0 ? w-this.borderWidth:0)) + 'px';
	        }
	        return w;
	    }
	});
	
	NS.empresaLbl = new Ext.form.Label({
		text: 'Empresa',
		x:0,
		y:10
	});
	 
	 NS.empresaTxt = new Ext.form.TextField({
		 width: 50,
		 id: PF + 'empresaTxt',
		 name: PF + 'empresaTxt',
		 x: 50,
		 y: 10,
		 blankText : 'Este campo es requerido',
		 listeners:
		 {
			 change:
			 {
				 fn: function(caja, valor)
				 {
					 BFwrk.Util.updateTextFieldToCombo(PF + 'empresaTxt', NS.cmbEmpresa.getId());
		 			NS.storeBanco.baseParams.idEmpresa=''+(valor);
		 			var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeBanco, msg: "Cargando..."});
		 			NS.storeBanco.load();
		 			//NS.txtBanco.setValue("");
					NS.cmbBanco.reset();
				 }
			 }
		 }
	 });
	 
	 NS.criterioLbl = new Ext.form.Label({
		 text: 'Criterio',
		 x:0,
		 y:55
	 });
	
	 
	 NS.valorLbl = new Ext.form.Label({
		 text: 'Valor',
		 x:0,
		 y:100
	 });
	 
	 NS.proveedorLbl = new Ext.form.Label({
         text: 'Proveedor:',
         x: 0,
         y: 140
     });
	 

	 NS.importeUno = new Ext.form.NumberField({
		   	width: 100,
		   	id: PF + 'importeUno',
			name: PF + 'importeUno',
			x: 105,
			y: 100,
			hidden: true,
			listeners:{
            	change:{
            		fn:function(caja, valor){
            			var indice=0;
						var i=0;
						var datosClase = NS.gridDatos.getStore().recordType;
						var recordsDatGrid=NS.storeDatos.data.items;				
						
						for(i=0;i<recordsDatGrid.length;i++)
						{
							if(recordsDatGrid[i].get('criterio')=='IMPORTES')
							{
								indice=i;
								NS.storeDatos.remove(recordsDatGrid[indice]);
							}
						}
				    	var datos = new datosClase({
	                			criterio: 'IMPORTES',
								valor: NS.cambiarFormato(valor)
	           			});
	                    NS.gridDatos.stopEditing();
	           			NS.storeDatos.insert(indice, datos);
	           			NS.gridDatos.getView().refresh();
        			}
            	}
            }	 	
	});
	 
	 NS.importeDos = new Ext.form.NumberField({
		   	width: 100,
		   	id: PF + 'importeDos',
			name: PF + 'importeDos',
			x: 215,
			y: 100,
			hidden: true,
			listeners:{
            	change:{
            		fn:function(caja, valor){
            			var indice=0;
						var i=0;
						var datosClase = NS.gridDatos.getStore().recordType;
						var recordsDatGrid=NS.storeDatos.data.items;		
						var valorUno=NS.cambiarFormato(NS.importeUno.getValue());
						if(valorUno!='')
							valorUno+=' a '
						for(i=0;i<recordsDatGrid.length;i++)
						{
							if(recordsDatGrid[i].get('criterio')=='IMPORTES')
							{
								indice=i;
								NS.storeDatos.remove(recordsDatGrid[indice]);
							}
						}
						if(parseInt(valorUno) > parseInt(valor))
						{
							Ext.Msg.alert('SET','El importe dos debe ser mayor o igual al importe uno');
							NS.importeUno.setValue('');
							NS.importeDos.setValue('');
							return;
						}
				    	var datos = new datosClase({
	                			criterio: 'IMPORTES',
								valor: valorUno + NS.cambiarFormato(valor)
	           			});
	                    NS.gridDatos.stopEditing();
	           			NS.storeDatos.insert(indice, datos);
	           			NS.gridDatos.getView().refresh();
	           			NS.cmbCriterio.setDisabled(false);
            		}
            	}
            }
	});
	 
	 
	 NS.clavePropuestaTxt ={
			xtype: 'uppertextfield',
			width: 210,
		   	id: PF + 'clavePropuestaTxt',
			name: PF + 'clavePropuestaTxt',
			x: 105,
			y: 100,
			
			hidden: true,
			listeners:{
            	change:{
            		fn:function(caja, valor){
            			var indice=0;
						var i=0;
						var datosClase = NS.gridDatos.getStore().recordType;
						var recordsDatGrid=NS.storeDatos.data.items;				
						
						for(i=0;i<recordsDatGrid.length;i++)
						{
							if(recordsDatGrid[i].get('criterio')=='CLAVE CONTROL')
							{
								indice=i;
								NS.storeDatos.remove(recordsDatGrid[indice]);
							}
						}
				    	var datos = new datosClase({
	                			criterio: 'CLAVE CONTROL',
								valor: valor
	           			});
				    	NS.cmbCriterio.setDisabled(false);
	                    NS.gridDatos.stopEditing();
	           			NS.storeDatos.insert(indice, datos);
	           			NS.gridDatos.getView().refresh();
            		}
            	}
            }
        
	};
	 
	 
	 NS.numeroChequeTxt = new Ext.form.NumberField({
				width: 210,
			   	id: PF + 'numeroChequeTxt',
				name: PF + 'numeroChequeTxt',
				x: 105,
				y: 100,
				hidden: true,
				listeners:{
	            	change:{
	            		fn:function(caja, valor){
	            			var indice=0;
							var i=0;
							var datosClase = NS.gridDatos.getStore().recordType;
							var recordsDatGrid=NS.storeDatos.data.items;				
							
							for(i=0;i<recordsDatGrid.length;i++)
							{
								if(recordsDatGrid[i].get('criterio')=='NO. CHEQUE')
								{
									indice=i;
									NS.storeDatos.remove(recordsDatGrid[indice]);
								}
							}
					    	var datos = new datosClase({
		                			criterio: 'NO. CHEQUE',
									valor: valor
		           			});
					    	NS.cmbCriterio.setDisabled(false);
		                    NS.gridDatos.stopEditing();
		           			NS.storeDatos.insert(indice, datos);
		           			NS.gridDatos.getView().refresh();
	            		}
	            	}
	            }
	        
		});
	 //Aqui comienza proveedor
	 //store proveedor
		NS.storeProveedor = new Ext.data.DirectStore({
			paramsAsHash: false,
			root: '',
			baseParams: {texto : ''},
			paramOrder: ['texto'],
			directFn: ChequesPorEntregarAction.consultarProveedores, 
			idProperty: 'id', 
			fields: [
				 {name: 'id'},
				 {name: 'descripcion'}
			],
			listeners: {
				load: function(s, records){
					//var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeProveedor, msg:"Cargando..."});
					if(records.length==null || records.length<=0)
					{
						Ext.Msg.alert('SET','No Existen proveedores con ese nombre');
						Ext.getCmp(PF + 'txtProveedor').reset();
						NS.cmbProveedor.reset();
					}
				}
			}
		}); 
		
		//combo proveedor
		//NS.storeProveedor.load();
		NS.cmbProveedor = new Ext.form.ComboBox({
			store: NS.storeProveedor
			,name: PF+'cmbProveedor'
			,id: PF+'cmbProveedor'
			,typeAhead: true
			,mode: 'local'
			,minChars: 0
			,selecOnFocus: true
			,forceSelection: true
	        ,x: 105
	        ,y: 140
	        ,width: 210
			,valueField:'id'
			,displayField:'descripcion'
			,autocomplete: true
			,emptyText: 'Seleccione un proveedor'
			,triggerAction: 'all'
			,value: ''
			,visible: false
			,listeners:{
				select:{ fn: function(combo, valor) {
						//linea cambia combo
						BFwrk.Util.updateComboToTextField(PF+'txtProveedor',NS.cmbProveedor.getId());
						//NS.accionarCmbDivisa(combo.getValue());
					}
				},
				beforequery: function(qe){
					NS.parametroBus=qe.combo.getRawValue();
				}
			}
		});
		
		//Este store se carga segun la clave tecleada en la caja de texto
		NS.storeUnicoBeneficiario = new Ext.data.DirectStore({
			paramsAsHash: false,
			baseParams:{
				campoUno:'no_persona',
				campoDos:'',
				tabla:'persona',
				condicion:'',
				orden:'',
				registroUnico:false
			},
			root: '',
			paramOrder:['campoUno','campoDos','tabla','condicion','orden','registroUnico'],
			root: '',
			directFn: ChequesPorEntregarAction.llenarComboBeneficiario, 
			idProperty: 'id', 
			fields: [
				 {name: 'id'},
				 {name: 'descripcion'},
			],
			listeners: {
				load: function(s, records){
					//var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeUnicoBeneficiario, msg:"Cargando..."});
					if(records.length==null || records.length<=0)
					{
						Ext.Msg.alert('SET','No Existe el beneficiario con esa clave');
						Ext.getCmp(PF + 'txtProveedor').reset();
						NS.cmbProveedor.reset();
						return;
					}
					else{
						var reg = NS.storeUnicoBeneficiario.data.items;
						Ext.getCmp(NS.cmbProveedor.getId()).setValue(reg[0].get('descripcion'));
						NS.accionarUnicoBeneficiario(reg[0].get('id'));
					}
				}
			}
		}); 
		
		NS.accionarUnicoBeneficiario = function(valueCombo){
	 		NS.idPersona=valueCombo;
	 		NS.nomPersona=NS.storeUnicoBeneficiario.getById(valueCombo).get('descripcion');
	 	}
		
	 	NS.accionarBeneficiario = function(valueCombo){
	 		NS.idPersona=valueCombo;
	 		NS.nomPersona=NS.storeProveedor.getById(valueCombo).get('descripcion');
	 	}
	 	
	 
	 
	 NS.txtProveedor={
         xtype: 'textfield',
         id: PF+'txtProveedor',
         name: PF+'txtProveedor',
         x: 50,
         y: 140,
         width: 50,
         listeners:{
         	change:{
         		fn:function(caja, valor){
         			//linea cambia combo
         			/*var comboValue = BFwrk.Util.updateTextFieldToCombo(PF+'txtProveedor',NS.cmbProveedor.getId());
         			if(!comboValue && Ext.getCmp(PF+'txtProveedor').getValue()!='') {
         				Ext.getCmp(PF+'txtProveedor').setValue('');
         			}*/
         			
         			if(
         				caja.getValue() != null && caja.getValue() != undefined && caja.getValue() != '') {
         				NS.storeUnicoBeneficiario.baseParams.registroUnico=true;
         				NS.storeUnicoBeneficiario.baseParams.condicion=''+caja.getValue();
         				var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeUnicoBeneficiario, msg:"Cargando Proveedor..."});
         				NS.storeUnicoBeneficiario.load();
        				}else {
        					NS.cmbProveedor.reset();
        					NS.storeUnicoBeneficiario.removeAll();
        				}
         			
         		}
         	}
         }
     }
	 
	 NS.cmdProveedor={
         xtype: 'button',
         text: '...',
         x: 320,
         y: 140,
         width: 20,
         id: PF+'cmdProveedor',
         name: PF+'cmdProveedor',
         listeners:{
         	click:{
         		fn:function(e){
         			
         			  if(NS.parametroBus==='' || NS.parametroBus.length < 4) {
							  Ext.Msg.alert('SET','Es necesario escribir un mínimo de 4 caracteres para la búsqueda');
						  }else{
							  NS.storeProveedor.baseParams.texto=NS.parametroBus;
							  var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeProveedor, msg:"Cargando Proveedores..."});
							  NS.storeProveedor.load();
						  }
         		}
         	}
         }
     }
	 
	 NS.idChequeraTxt = new Ext.form.TextField({
		   	width: 200,
		   	id: PF + 'idChequeraTxt',
			name: PF + 'idChequeraTxt',
			x: 105,
			y: 100,
			hidden: true
	});
	 
	 NS.bancoTxt = new Ext.form.TextField({
		   	width: 200,
		   	id: PF + 'bancoTxt',
			name: PF + 'bancoTxt',
			x: 105,
			y: 100,
			hidden: true
	});
	 
	 NS.proveedorTxt = new Ext.form.TextField({
		   	width: 200,
		   	id: PF + 'proveedorTxt',
			name: PF + 'proveedorTxt',
			x: 105,
			y: 100,
			hidden: true
	});
	 
	 NS.mTotalMnLbl = new Ext.form.Label({
		 text: 'Monto Total MN:',
		 x:750,
		 y:235
	 });
	 
	 NS.mTotalMnNmb = new Ext.form.TextField({
		   	width: 100,
		   	id: PF + 'mTotalMnNmb',
			name: PF + 'mTotalMnNmb',
			x: 840,
			y: 235,
			value: '$0.00',
			readOnly: true,
			style: {
				textAlign: 'right',
	        }
			
	});
	 
	 NS.mTotalDlsLbl = new Ext.form.Label({
		 text: 'Monto Total DLS:',
		 x:750,
		 y:270
	 });
	 
	 NS.mTotalDlsNmb = new Ext.form.TextField({
		   	width: 100,
		   	id: PF + 'mTotalDlsNmb',
			name: PF + 'mTotalDlsNmb',
			x: 840,
			y: 265,
			value: '$0.00',
			readOnly: true,
			style: {
				textAlign: 'right',
	        }
			
	});
	 
	 NS.rTotalMnNmb = new Ext.form.NumberField({
		   	width: 50,
		   	id: PF + 'rTotalMnNmb',
			name: PF + 'rTotalMnNmb',
			x: 680,
			y: 235,
			value: 0,
			readOnly: true
			
	});
	 
	 NS.rTotalMnLbl = new Ext.form.Label({
		 text: 'Total de Registros MN:',
		 x:565,
		 y:235
	 });
	 
	 NS.rTotalDlsNmb = new Ext.form.NumberField({
		   	width: 50,
		   	id: PF + 'rTotalDlsNmb',
			name: PF + 'rTotalDlsNmb',
			x: 680,
			y: 265,
			value: 0,
			readOnly: true
			
	});
	 
	 NS.rTotalDlsLbl = new Ext.form.Label({
		 text: 'Total de Registros DLS:',
		 x:565,
		 y:270
	 });
	 
	 NS.solicitanteLbl = new Ext.form.Label({
		 text: 'Solicitante:',
		 x:0,
		 y:235
	 });
	 
	
	 NS.identificacionLbl = new Ext.form.Label({
		 text: 'Identificacion:',
		 x:0,
		 y:270
	 });
	
	 NS.identificacionTxt = new Ext.form.TextField({
		   	width: 200,
		   	id: PF + 'identificacionTxt',
			name: PF + 'identificacionTxt',
			x: 75,
			y: 270,
			readOnly: true
			
	});
	 
	 NS.fechaEntregaLbl = new Ext.form.Label({
		 text: 'Fecha Entrega:',
		 x:0,
		 y:305
	 }); 
	 
	 /*
	  * DATEFIELD'S
	  */
	 
		NS.txtFechaInicial = new Ext.form.DateField({
			id: PF + 'txtFechaInicial',
			name: PF + 'txtFechaInicial',
			format:'d/m/Y',
			x: 105,
			y: 100,
			width: 100,
			hidden: true,
			listeners:{
            	change:{
            		fn:function(caja, valor){
            			var indice=0;
						var i=0;
						var datosClase = NS.gridDatos.getStore().recordType;
						var recordsDatGrid=NS.storeDatos.data.items;
						for(i=0;i<recordsDatGrid.length;i++)
						{
							if(recordsDatGrid[i].get('criterio')=='FECHAS')
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
	                			criterio: 'FECHAS',
								valor: valorFecha
	           			});
	                    NS.gridDatos.stopEditing();
	           			NS.storeDatos.insert(indice, datos);
	           			NS.gridDatos.getView().refresh();
            		}
            			}
						}
				});
		NS.txtFechaFinal = new Ext.form.DateField({
			id: PF + 'txtFechaFinal',
			name: PF + 'txtFechaFinal',
			format:'d/m/Y',
			x: 215,
			y: 100,
			width: 100,
			hidden: true,
			listeners:{
            	change:{
            		fn:function(caja, valor){
            			var indice=0;
						var i=0;
						var datosClase = NS.gridDatos.getStore().recordType;
						var recordsDatGrid=NS.storeDatos.data.items;	
						var fechaUno=NS.txtFechaInicial.getValue();
						var valIni='';
						if(fechaUno!='')
						{
							valIni=cambiarFecha(''+fechaUno);
						}
						if(valIni!='')
						{
							valIni+=' a ';
						}			
						NS.cmbCriterio.setDisabled(false);
						for(i=0;i<recordsDatGrid.length;i++)
						{
							if(recordsDatGrid[i].get('criterio')=='FECHAS')
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
							Ext.Msg.alert('SET','La fecha inicial debe ser menor a la final');
						}
						else
						{
					    	var datos = new datosClase({
		                			criterio: 'FECHAS',
									valor: valIni + valFin
		           			});
		                    NS.gridDatos.stopEditing();
		           			NS.storeDatos.insert(indice, datos);
		           			NS.gridDatos.getView().refresh();   		
    			
            		}
					}
				}
			}
			});
		
		NS.txtFechaEntrega = new Ext.form.DateField({
			id: PF + 'txtFechaEntrega',
			name: PF + 'txtFechaEntrega',
			format:'d/m/Y',
			x: 75,
			y: 305,
			width: 100,
			value: apps.SET.FEC_HOY
			
		});
	 
		
	/*
	 * FUNCTION'S
	 */	
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
		NS.manejoDeCriterios = function(criterio){
			var datosClase = NS.gridDatos.getStore().recordType;
			var dato = NS.storeDatos.data.items; 
			alert(criterio);
			if(criterio == 'CLAVE CONTROL'){
				for(var i=0; i < dato.length; i++){
					if(dato[i].get('criterio') == 'CLAVE CONTROL'){
						NS.storeDatos.remove(dato[i]);
					}
				}
				
				//
				//NS.clavePropuestaTxt.setVisible(true);
				Ext.getCmp(PF+'clavePropuestaTxt').show();
				NS.txtFechaInicial.setVisible(false);
				NS.txtFechaFinal.setVisible(false);
				NS.importeUno.setVisible(false);
			    NS.importeDos.setVisible(false);
			    NS.numeroChequeTxt.setVisible(false);
			    NS.idChequeraTxt.setVisible(false);
			    NS.bancoTxt.setVisible(false);
			    NS.proveedorTxt.setVisible(false);
			    //
				
				var datos = new datosClase({
                	criterio: 'CLAVE CONTROL',
					valor: NS.clavePropuestaTxt.getValue()
            	});
				NS.storeDatos.insert(dato.length, datos);
        		NS.gridDatos.getView().refresh();
        		NS.clavePropuestaTxt.setDisabled(true);
				
			}else if(criterio == 'FECHAS'){
				for(var i=0; i < dato.length; i++){
					if(dato[i].get('criterio') == 'FECHAS'){
						NS.storeDatos.remove(dato[i]);
					}
				}
				
				//
				NS.txtFechaInicial.setVisible(true);
				NS.txtFechaFinal.setVisible(true);
				NS.importeUno.setVisible(false);
			    NS.importeDos.setVisible(false);
			    NS.numeroChequeTxt.setVisible(false);
			    NS.clavePropuestaTxt.setVisible(false);
			    NS.idChequeraTxt.setVisible(false);
			    NS.bancoTxt.setVisible(false);
			    NS.proveedorTxt.setVisible(false);
				//
				
				/*var datos = new datosClase({
                	criterio: 'FECHAS',
					valor:NS.cambiarFecha('' + NS.txtFechaInicial.getValue())
            	});*/
				NS.storeDatos.insert(dato.length, datos);
        		NS.gridDatos.getView().refresh();
        		NS.txtFechaInicial.setDisabled(true);
        		NS.txtFechaFinal.setDisabled(true);
			}else if(criterio == 'IMPORTES'){
				for(var i=0; i < dato.length; i++){
					if(dato[i].get('criterio') == 'IMPORTES'){
						NS.storeDatos.remove(dato[i]);
					}
				}
				//
				NS.importeUno.setVisible(true);
			    NS.importeDos.setVisible(true);
			    NS.txtFechaInicial.setVisible(false);
				NS.txtFechaFinal.setVisible(false);
				NS.clavePropuestaTxt.setVisible(false);
				NS.numeroChequeTxt.setVisible(false);
				NS.idChequeraTxt.setVisible(false);
				NS.bancoTxt.setVisible(false);
				NS.proveedorTxt.setVisible(false);
				//
				
				var datos = new datosClase({
                	criterio: 'IMPORTES',
					valor: '$' + NS.importeUno.getValue() + " >< $" + NS.importeDos.getValue()
            	});
				NS.storeDatos.insert(dato.length, datos);
        		NS.gridDatos.getView().refresh();
        		NS.importeUno.setDisabled(true);
        		NS.importeDos.setDisabled(true);
			}else if(criterio == 'NUM_CHEQUE'){
				for(var i = 0; i < dato.length; i++){
					if(dato[i].get('criterio') == 'NUM_CHEQUE'){
						NS.storeDatos.remove(dato[i]);
					}
				}
				//
				NS.numeroChequeTxt.setVisible(true);
				NS.txtFechaInicial.setVisible(false);
				NS.txtFechaFinal.setVisible(false);
				NS.importeUno.setVisible(false);
			    NS.importeDos.setVisible(false);
			    NS.clavePropuestaTxt.setVisible(false);
			    NS.idChequeraTxt.setVisible(false);
			    NS.bancoTxt.setVisible(false);
			    NS.proveedorTxt.setVisible(false);
				//
				var datos = new datosClase({
					criterio: 'NUM_CHEQUE',
					valor: NS.numeroChequeTxt.getValue()
				});
				NS.storeDatos.insert(dato.length, datos);
        		NS.gridDatos.getView().refresh();
        		NS.numeroChequeTxt.setDisabled(true);
			}else if(criterio == 'CHEQUERA'){
				for(var i = 0; i < dato.length; i++){
					if(dato[i].get('criterio') == 'CHEQUERA'){
						NS.storeDatos.remove(dato[i]);
					}
				}
				//
				NS.idChequeraTxt.setVisible(true);
				NS.txtFechaInicial.setVisible(false);
				NS.txtFechaFinal.setVisible(false);
				NS.importeUno.setVisible(false);
			    NS.importeDos.setVisible(false);
			    NS.clavePropuestaTxt.setVisible(false);
			    NS.numeroChequeTxt.setVisible(false);
			    NS.bancoTxt.setVisible(false);
			    NS.proveedorTxt.setVisible(false);
			    //
				var datos = new datosClase({
					criterio: 'CHEQUERA',
					valor: NS.idChequeraTxt.getValue()
				});
				NS.storeDatos.insert(dato.length, datos);
        		NS.gridDatos.getView().refresh();
        		NS.idChequeraTxt.setDisabled(true);
			}else if(criterio == 'BANCO'){
				for(var i = 0; i < dato.length; i++){
					if(dato[i].get('criterio') == 'BANCO'){
						NS.storeDatos.remove(dato[i]);
					}
				}
				//
				NS.bancoTxt.setVisible(true);
				NS.idChequeraTxt.setVisible(false);
				NS.txtFechaInicial.setVisible(false);
				NS.txtFechaFinal.setVisible(false);
				NS.importeUno.setVisible(false);
			    NS.importeDos.setVisible(false);
			    NS.clavePropuestaTxt.setVisible(false);
			    NS.numeroChequeTxt.setVisible(false);
			    NS.proveedorTxt.setVisible(false);
				//
				var datos = new datosClase({
					criterio: 'BANCO',
					valor: NS.bancoTxt.getValue()
				});
				NS.storeDatos.insert(dato.length, datos);
        		NS.gridDatos.getView().refresh();
				NS.bancoTxt.setDisabled(true);
			}else if(criterio == 'PROVEEDOR'){
				for(var i = 0; i < dato.length; i++){
					if(dato[i].get('criterio') == 'PROVEEDOR'){
						NS.storeDatos.remove(dato[i]);
					}
				}
				//
				NS.proveedorTxt.setVisible(true);
				NS.bancoTxt.setVisible(false);
				NS.idChequeraTxt.setVisible(false);
				NS.txtFechaInicial.setVisible(false);
				NS.txtFechaFinal.setVisible(false);
				NS.importeUno.setVisible(false);
			    NS.importeDos.setVisible(false);
			    NS.clavePropuestaTxt.setVisible(false);
			    NS.numeroChequeTxt.setVisible(false);
				//
				var datos = new datosClase({
					criterio: 'PROVEEDOR',
					valor: NS.proveedorTxt.getValue()
				});
				NS.storeDatos.insert(dato.length, datos);
        		NS.gridDatos.getView().refresh();
				NS.proveedorTxt.setDisabled(true);
			}
		}
		
		NS.calcularRenglones = function(){
			var seleccionados =  NS.gridDatosCheques.getSelectionModel().getSelections();
			var importeMn = 0;
			var seleccionMn = 0;
			var importeDls = 0;
			var seleccionDls = 0;
			
			for(var i = 0; i < seleccionados.length; i++){
				if(seleccionados[i].get('divisa') == 'PESOS'){
					importeMn = importeMn + seleccionados[i].get('importe');
					seleccionMn++;
				}else{
					importeDls = importeDls + seleccionados[i].get('importe');
					seleccionDls++;
				}
					
			}
			
			NS.mTotalMnNmb.setValue(NS.cambiarFormato(importeMn.toFixed(2), '$'));
 	       	NS.mTotalDlsNmb.setValue(NS.cambiarFormato(importeDls.toFixed(2), '$'));
 	       	NS.rTotalMnNmb.setValue(seleccionMn);
 	       	NS.rTotalDlsNmb.setValue(seleccionDls);
 	  	   
			
		}
		//Quitar formato a las cantidades
		NS.unformatNumber=function(num) {
			return num.replace(/(,)/g,'');
		};
		
		//Formato de un numero a monetario
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
		}
		
		NS.limpiarTodo = function(){
			NS.empresaTxt.setValue('');
			NS.cmbEmpresa.reset();
			NS.importeUno.setValue('');
			NS.importeDos.setValue('');
			NS.cmbCriterio.reset();
			NS.cmbCriterio.setDisabled(false);
			//NS.clavePropuestaTxt.setValue('');
			Ext.getCmp(PF+'clavePropuestaTxt').setValue('');
			Ext.getCmp(PF+'numeroChequeTxt').setValue('');
			NS.txtFechaInicial.setValue('');
			NS.txtFechaFinal.setValue('');
			//NS.numeroChequeTxt.setValue('');
			NS.idChequeraTxt.setValue('');
			NS.bancoTxt.setValue('');
			NS.proveedorTxt.setValue('');
			//NS.txtProveedor.setValue('');
			Ext.getCmp(PF+'txtProveedor').setValue('');
			NS.txtFechaEntrega.setValue(apps.SET.FEC_HOY);
			NS.importeUno.setVisible(false);
		    NS.importeDos.setVisible(false);
		    //NS.clavePropuestaTxt.setVisible(false);
		    Ext.getCmp(PF+'clavePropuestaTxt').setVisible(false);
		    NS.txtFechaInicial.setVisible(false);
			NS.txtFechaFinal.setVisible(false);
			Ext.getCmp(PF+'numeroChequeTxt').setVisible(false);
			//NS.numeroChequeTxt.setVisible(false);
			NS.idChequeraTxt.setVisible(false);
			
			NS.cmbChequera.setVisible(false);
			NS.cmbChequera.reset();
			NS.cmbBanco.setVisible(false);
			NS.cmbBanco.reset();
			NS.proveedorTxt.setValue('');
			NS.cmbProveedor.reset();
			
			NS.bancoTxt.setVisible(false);
			NS.proveedorTxt.setVisible(false);
			//NS.clavePropuestaTxt.setDisabled(false);
			NS.importeUno.setDisabled(false);
			NS.importeDos.setDisabled(false);
			NS.txtFechaInicial.setDisabled(false);
			NS.txtFechaFinal.setDisabled(false);
			//NS.numeroChequeTxt.setDisabled(false);
			NS.idChequeraTxt.setDisabled(false);
			NS.bancoTxt.setDisabled(false);
			NS.proveedorTxt.setDisabled(false);
			NS.cmbSolicitantes.reset();
			NS.identificacionTxt.setValue('');
			NS.mTotalMnNmb.setValue('$0.00');
			NS.mTotalDlsNmb.setValue('$0.00');
			NS.rTotalMnNmb.setValue(0);
			NS.rTotalDlsNmb.setValue(0);
			//Aqui
			NS.storeProveedor.removeAll();
			NS.storeUnicoBeneficiario.removeAll();
			//NS.storeLlenarComboEmpresa.removeAll();
			//NS.storeCriterio.removeAll();
			NS.storeBanco.removeAll();
			NS.storeChequera.removeAll();
			//Aqui
			NS.storeDatos.removeAll();
			NS.gridDatos.getView().refresh();
			NS.storeLlenarGridCheques.removeAll();
			NS.gridDatosCheques.getView().refresh();
		}
		
		NS.limpiarBusqueda = function(){
			//NS.empresaTxt.setValue('');
			//NS.cmbEmpresa.reset();
			NS.importeUno.setValue('');
			NS.importeDos.setValue('');
			NS.cmbCriterio.reset();
			NS.cmbCriterio.setDisabled(false);
			//NS.clavePropuestaTxt.setValue('');
			Ext.getCmp(PF+'clavePropuestaTxt').setValue('');
			Ext.getCmp(PF+'numeroChequeTxt').setValue('');
			NS.txtFechaInicial.setValue('');
			NS.txtFechaFinal.setValue('');
			//NS.numeroChequeTxt.setValue('');
			NS.idChequeraTxt.setValue('');
			NS.bancoTxt.setValue('');
			//NS.txtProveedor.setValue('');
			Ext.getCmp(PF+'txtProveedor').setValue('');
			//NS.txtFechaEntrega.setValue(apps.SET.FEC_HOY);
			NS.importeUno.setVisible(false);
		    NS.importeDos.setVisible(false);
		    //NS.clavePropuestaTxt.setVisible(false);
		    Ext.getCmp(PF+'clavePropuestaTxt').setVisible(false);
		    NS.txtFechaInicial.setVisible(false);
			NS.txtFechaFinal.setVisible(false);
			Ext.getCmp(PF+'numeroChequeTxt').setVisible(false);
			//NS.numeroChequeTxt.setVisible(false);
			NS.idChequeraTxt.setVisible(false);
			
			NS.cmbChequera.setVisible(false);
			NS.cmbChequera.reset();
			NS.cmbBanco.setVisible(false);
			NS.cmbBanco.reset();
			NS.proveedorTxt.setValue('');
			NS.cmbProveedor.reset();
			
			NS.bancoTxt.setVisible(false);
			NS.proveedorTxt.setVisible(false);
			//NS.clavePropuestaTxt.setDisabled(false);
			NS.importeUno.setDisabled(false);
			NS.importeDos.setDisabled(false);
			NS.txtFechaInicial.setDisabled(false);
			NS.txtFechaFinal.setDisabled(false);
			//NS.numeroChequeTxt.setDisabled(false);
			NS.idChequeraTxt.setDisabled(false);
			NS.bancoTxt.setDisabled(false);
			NS.proveedorTxt.setDisabled(false);
			//NS.cmbSolicitantes.reset();
			//NS.identificacionTxt.setValue('');
			//NS.mTotalMnNmb.setValue('$0.00');
			//NS.mTotalDlsNmb.setValue('$0.00');
			//NS.rTotalMnNmb.setValue(0);
			//NS.rTotalDlsNmb.setValue(0);
			//Aqui
			NS.storeProveedor.removeAll();
			NS.storeUnicoBeneficiario.removeAll();
			//NS.storeLlenarComboEmpresa.removeAll();
			//NS.storeCriterio.removeAll();
			//NS.storeBanco.removeAll();
			NS.storeChequera.removeAll();
			//Aqui
			NS.storeDatos.removeAll();
			NS.gridDatos.getView().refresh();
			NS.storeLlenarGridCheques.removeAll();
			NS.gridDatosCheques.getView().refresh();
		}
			
			
	 /*
	  * COMBO'S
	  */
	
	 
	 NS.storeLlenarComboEmpresa = new Ext.data.DirectStore({
			paramsAsHash: true,
			root: '',
			directFn: ChequesPorEntregarAction.llenaEmpresa,
			fields:
				[
				 	{name: 'idEmpresa'},
				 	{name: 'descEmpresa'}
				],
			idProperty: 'id',
			listeners:
			{
				load: function(s, records)
				{
					if(records.length == null || records.length <= 0){
						Ext.Msg.alert('SET', 'No hay empresas que mostrar');
					}else{
						
					}
						
				}
			}

		});
	    var myMask = new Ext.LoadMask(Ext.getBody(), {store:NS.storeLlenarComboEmpresa, msg: "Cargando Empresas ..."});
		NS.storeLlenarComboEmpresa.load();
		
		NS.cmbEmpresa = new Ext.form.ComboBox({
			store: NS.storeLlenarComboEmpresa,
			id: PF + 'cmbEmpresa',
			name: PF + 'cmbEmpresa',
			width: 210,
			x: 105,
			y: 10,
			typeAhead: true,
			mode: 'local',
			selecOnFocus: true,
			forceSelection: true,
			valueField: 'idEmpresa',
			displayField: 'descEmpresa',
			autocomplete: true,
			emptyText: 'Seleccione Empresa',
			triggerAction: 'all',
			listeners:{
				select:{
					fn: function(combo, valor)
					{
						BFwrk.Util.updateComboToTextField(PF + 'empresaTxt', NS.cmbEmpresa.getId());
						//NS.limpiarTodo();
						var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeBanco, msg: "Cargando..."});
						NS.storeBanco.baseParams.idEmpresa=''+NS.cmbEmpresa.getValue();
						NS.storeBanco.load();
						//NS.cmbBanco.reset();
						NS.limpiarBusqueda();
					}
				}
				
			}
		});
		
	 
		/*NS.datosComboCriterio = [
           ['FECHAS', 'FECHAS'],
           ['IMPORTES', 'IMPORTES'],
           ['CLAVE CONTROL', 'CLAVE CONTROL'],
           ['NUM_CHEQUE', 'NUM_CHEQUE'],
           ['CHEQUERA', 'CHEQUERA'],
           ['BANCO', 'BANCO'],
           ['PROVEEDOR', 'PROVEEDOR']
		];*/	       		
		                        	
		/*NS.storeCriterio = new Ext.data.SimpleStore({
			idProperty: 'idCriterio',
			fields: [
			         {name: 'idCriterio'},
			         {name: 'descCriterio'}
			         ]
		});
		NS.storeCriterio.loadData(NS.datosComboCriterio);
		*/
		//data criterios
		NS.dataCriterio = [
						  [ '0', 'BANCO' ]
					     ,[ '1', 'CHEQUERA' ]
					     //,[ '2', 'EMPRESA' ]
					     //,[ '3', 'CAJA DE PAGO' ]
					     //,[ '4', 'LOTE' ]
					    //,[ '5', 'CENTRO DE COSTOS' ]
					    //,[ '6', 'SOLICITUDES' ]
					     ,[ '7', 'FECHAS' ]
					     ,[ '8', 'CLAVE CONTROL' ]
					     //,[ '9', 'PROVEEDOR' ]    
					     ,[ '10', 'IMPORTES' ]
					     ,[ '11', 'NO. CHEQUE' ]
						  ];
	  	NS.storeCriterio = new Ext.data.SimpleStore( {
			idProperty: 'idCriterio',
			fields : [
				{name :'idCriterio'},
				{name :'descripcion'}
		 ]
		});
		NS.storeCriterio.loadData(NS.dataCriterio);
		
		//combo criterios
		NS.cmbCriterio = new Ext.form.ComboBox({
			 store: NS.storeCriterio
			,name: PF+'cmbCriterio'
			,id: PF+'cmbCriterio'
			,typeAhead: true
			,mode: 'local'
			,minChars: 0
			,selecOnFocus: true
			,forceSelection: true
			,x: 105
			,y: 50
			,width:210
			,valueField:'idCriterio'
			,displayField:'descripcion'
			,autocomplete: true
			,emptyText: 'Seleccione un criterio'
			,triggerAction: 'all'
			,value: ''
			,listeners:{
				select:{
					fn:function(combo, value) {
						/*if(Ext.getCmp(PF+'txtEmpresa').getValue() != '')
						{
							NS.storeDivision.baseParams.condicion = 'no_empresa = ' + Ext.getCmp(PF+'txtEmpresa').getValue();
							NS.storeDivision.load();
						}*/
						//alert("entro");
						//combo.setDisabled(true);
						var tamGrid=(NS.storeDatos.data.items).length;
						//alert(tamGrid);
						var datosClase = NS.gridDatos.getStore().recordType;
						
						if(combo.getValue() == 0)
						{
							//alert("entro2");
							//NS.bancoTxt.setVisible(true);
							NS.cmbBanco.setVisible(true);
							NS.cmbChequera.setVisible(false);
							//Ext.getCmp(PF+'cmbBanco').show();
		    				//NS.idChequeraTxt.setVisible(false);
							Ext.getCmp(PF+'clavePropuestaTxt').hide();
		    				NS.txtFechaInicial.setVisible(false);
		    				NS.txtFechaFinal.setVisible(false);
		    				NS.importeUno.setVisible(false);
		    			    NS.importeDos.setVisible(false);
		    			    //NS.clavePropuestaTxt.setVisible(false);
		    			    Ext.getCmp(PF+'numeroChequeTxt').hide();
		    			    //NS.numeroChequeTxt.setVisible(false);
		    			    NS.proveedorTxt.setVisible(false);
		    			    
							var indice=0;
							var i=0;
							var recordsDatGrid=NS.storeDatos.data.items;
							var tamGrid=(NS.storeDatos.data.items).length;
							
							//if(tamGrid<=1){
							for(i=0;i<recordsDatGrid.length;i++)
							{		
								if(recordsDatGrid[i].get('criterio')=='BANCO'){
									indice=i;
								
								}
							}
							//}else{
								//Ext.Msg.alert('SET','Ya se indico el Banco');
								//return;
							//}
							
							if(indice>0){
								Ext.Msg.alert('SET','Ya indicó BANCO necesita borralo antes');
								combo.setDisabled(false);
								return;
							}else{
								combo.setDisabled(true);
								var datos = new datosClase({
				                	criterio: 'BANCO',
									valor: ''
				            	});
		                        NS.gridDatos.stopEditing();
			            		NS.storeDatos.insert(tamGrid, datos);
			            		NS.gridDatos.getView().refresh();
							}
							
						}else if(combo.getValue() == 1){
							NS.cmbChequera.setVisible(true);
							NS.cmbBanco.setVisible(false);
							//NS.idChequeraTxt.setVisible(true);
		    				NS.txtFechaInicial.setVisible(false);
		    				NS.txtFechaFinal.setVisible(false);
		    				NS.importeUno.setVisible(false);
		    			    NS.importeDos.setVisible(false);
		    			    Ext.getCmp(PF+'clavePropuestaTxt').hide();
		    			    //NS.clavePropuestaTxt.setVisible(false);
		    			    Ext.getCmp(PF+'numeroChequeTxt').hide();
		    			    //NS..setVisible(false);
		    			    //NS.bancoTxt.setVisible(false);
		    			    NS.proveedorTxt.setVisible(false);
		    			    
							
							var indice=0;
							var i=0;
							var recordsDatGrid=NS.storeDatos.data.items;
							for(i=0;i<recordsDatGrid.length;i++){
								if(recordsDatGrid[i].get('criterio')=='BANCO')
									indice=i;
							}
							
							if(recordsDatGrid[indice]!=undefined && (recordsDatGrid[indice].get('criterio'))=='BANCO'){
								
								for(i=0;i<recordsDatGrid.length;i++){
								
									if(recordsDatGrid[i].get('criterio')=='CHEQUERA')
										indice=i;
								}
								
								if(indice>0 && recordsDatGrid[i].get('criterio')=='CHEQUERA'){
									Ext.Msg.alert('SET','Ya indicó CHEQUERA necesita borrala antes');
									combo.setDisabled(false);
									return;
								}else{
								
									combo.setDisabled(true);
									var datos = new datosClase({
					                	criterio: 'CHEQUERA',
										valor: ''
					            	});
			                        NS.gridDatos.stopEditing();
				            		NS.storeDatos.insert(tamGrid, datos);
				            		NS.gridDatos.getView().refresh();
				            	
								}
							}else{
								Ext.Msg.alert('SET','Primero debe escoger un Banco');
								//combo.setDisabled(false);
								NS.cmbChequera.setVisible(false);
							}
						}else if(combo.getValue() == 2){
							
						}else if(combo.getValue() == 3){	
							Ext.getCmp(PF+'cmbChequera').hide();
							Ext.getCmp(PF+'cmbBanco').hide();
							Ext.getCmp(PF+'cmbCriterioEmpresa').hide();
							Ext.getCmp(PF+'cmbCaja').show();
							//Ext.getCmp(PF+'cmbDivision').hide();
							Ext.getCmp(PF+'txtFechaIni').hide();
							Ext.getCmp(PF+'txtFechaFin').hide();
							Ext.getCmp(PF+'txtImporteInicial').hide();
							Ext.getCmp(PF+'txtImporteFinal').hide();
							Ext.getCmp(PF+'txtCentroCosto').hide();
							Ext.getCmp(PF+'txtLote').hide();
							Ext.getCmp(PF+'txtDocto').hide();
							Ext.getCmp(PF+'txtSolicitudIni').hide();
							Ext.getCmp(PF+'txtSolicitudFin').hide();
							Ext.getCmp(PF+'txtTipoEgreso').hide();
							
							var indice=0;
							var i=0;
							var recordsDatGrid=NS.storeDatos.data.items;
							for(i=0;i<recordsDatGrid.length;i++){
								if(recordsDatGrid[i].get('criterio')=='CAJA DE PAGO')
									indice=i;
							}
							
							if(indice>0){
								Ext.Msg.alert('SET','Ya indicó CAJA DE PAGO necesita borrala antes');
								combo.setDisabled(false);
								return;
							}else{
								combo.setDisabled(true);
								var datos = new datosClase({
				                	criterio: 'CAJA DE PAGO',
									valor: ''
				            	});
		                        NS.gridDatos.stopEditing();
			            		NS.storeDatos.insert(tamGrid, datos);
			            		NS.gridDatos.getView().refresh();
							}
						}else if(combo.getValue() == 4){
							Ext.getCmp(PF+'cmbBanco').hide();
							Ext.getCmp(PF+'cmbChequera').hide();
							Ext.getCmp(PF+'cmbCriterioEmpresa').hide();
							Ext.getCmp(PF+'cmbCaja').hide();
							//Ext.getCmp(PF+'cmbDivision').hide();
							Ext.getCmp(PF+'txtFechaIni').hide();
							Ext.getCmp(PF+'txtFechaFin').hide();
							Ext.getCmp(PF+'txtImporteInicial').hide();
							Ext.getCmp(PF+'txtImporteFinal').hide();
							Ext.getCmp(PF+'txtCentroCosto').hide();
							Ext.getCmp(PF+'txtLote').show();
							Ext.getCmp(PF+'txtDocto').hide();
							Ext.getCmp(PF+'txtSolicitudIni').hide();
							Ext.getCmp(PF+'txtSolicitudFin').hide();
							Ext.getCmp(PF+'txtTipoEgreso').hide();
							
							var indice=0;
							var i=0;
							var recordsDatGrid=NS.storeDatos.data.items;
							
							for(i=0;i<recordsDatGrid.length;i++){
								if(recordsDatGrid[i].get('criterio')=='LOTE')
									indice=i;
							}
							
							if(indice>0){
								Ext.Msg.alert('SET','Ya indicó LOTE necesita borralo antes');
								combo.setDisabled(false);
								return;
							}else{
								combo.setDisabled(true);
								var datos = new datosClase({
				                	criterio: 'LOTE',
									valor: ''
				            	});
		                        NS.gridDatos.stopEditing();
			            		NS.storeDatos.insert(tamGrid, datos);
			            		NS.gridDatos.getView().refresh();
							}
						}else if(combo.getValue() == 5){
							Ext.getCmp(PF+'cmbBanco').hide();
							Ext.getCmp(PF+'cmbChequera').hide();
							Ext.getCmp(PF+'cmbCriterioEmpresa').hide();
							Ext.getCmp(PF+'cmbCaja').hide();
							//Ext.getCmp(PF+'cmbDivision').hide();
							Ext.getCmp(PF+'txtFechaIni').hide();
							Ext.getCmp(PF+'txtFechaFin').hide();
							Ext.getCmp(PF+'txtImporteInicial').hide();
							Ext.getCmp(PF+'txtImporteFinal').hide();
							Ext.getCmp(PF+'txtCentroCosto').show();
							Ext.getCmp(PF+'txtLote').hide();
							Ext.getCmp(PF+'txtDocto').hide();
							Ext.getCmp(PF+'txtSolicitudIni').hide();
							Ext.getCmp(PF+'txtSolicitudFin').hide();
							Ext.getCmp(PF+'txtTipoEgreso').hide();
							
							var indice=0;
							var i=0;
							var recordsDatGrid=NS.storeDatos.data.items;
							for(i=0;i<recordsDatGrid.length;i++){
								if(recordsDatGrid[i].get('criterio')=='CENTRO DE COSTOS')
									indice=i;
							}
							
							if(indice>0){
								Ext.Msg.alert('SET','Ya indicó CENTRO DE COSTOS necesita borralo antes');
								combo.setDisabled(false);
								return;
							}else{
								combo.setDisabled(true);
								var datos = new datosClase({
				                	criterio: 'CENTRO DE COSTOS',
									valor: ''
				            	});
		                        NS.gridDatos.stopEditing();
			            		NS.storeDatos.insert(tamGrid, datos);
			            		NS.gridDatos.getView().refresh();
							}
						}else if(combo.getValue() == 6){	
							Ext.getCmp(PF+'cmbBanco').hide();
							Ext.getCmp(PF+'cmbChequera').hide();
							Ext.getCmp(PF+'cmbCriterioEmpresa').hide();
							Ext.getCmp(PF+'cmbCaja').hide();
							//Ext.getCmp(PF+'cmbDivision').hide();
							Ext.getCmp(PF+'txtFechaIni').hide();
							Ext.getCmp(PF+'txtFechaFin').hide();
							Ext.getCmp(PF+'txtImporteInicial').hide();
							Ext.getCmp(PF+'txtImporteFinal').hide();
							Ext.getCmp(PF+'txtCentroCosto').hide();
							Ext.getCmp(PF+'txtLote').hide();
							Ext.getCmp(PF+'txtDocto').hide();
							Ext.getCmp(PF+'txtSolicitudIni').show();
							Ext.getCmp(PF+'txtSolicitudFin').show();
							Ext.getCmp(PF+'txtTipoEgreso').hide();
							
							var indice=0;
							var i=0;
							var recordsDatGrid=NS.storeDatos.data.items;
							for(i=0;i<recordsDatGrid.length;i++){
								if(recordsDatGrid[i].get('criterio')=='SOLICITUDES')
									indice=i;
							}
							
							if(indice>0){
								Ext.Msg.alert('SET','Ya indicó SOLICITUDES necesita borralo antes');
								combo.setDisabled(false);
								return;
							}else{
								combo.setDisabled(true);
								var datos = new datosClase({
				                	criterio: 'SOLICITUDES',
									valor: ''
				            	});
		                        NS.gridDatos.stopEditing();
			            		NS.storeDatos.insert(tamGrid, datos);
			            		NS.gridDatos.getView().refresh();
							}
						}else if(combo.getValue() == 7){
							NS.txtFechaInicial.setVisible(true);
		    				NS.txtFechaFinal.setVisible(true);
		    				NS.importeUno.setVisible(false);
		    			    NS.importeDos.setVisible(false);
		    			    //NS.numeroChequeTxt.setVisible(false);
		    			    Ext.getCmp(PF+'numeroChequeTxt').hide();
		    			    Ext.getCmp(PF+'clavePropuestaTxt').hide();
		    			    //NS.clavePropuestaTxt.setVisible(false);
		    			    NS.cmbChequera.setVisible(false);
		    			    NS.cmbBanco.setVisible(false);
		    			    NS.proveedorTxt.setVisible(false);
		    			    
		    			    var indice=0;
							var i=0;
							var recordsDatGrid=NS.storeDatos.data.items;
							for(i=0;i<recordsDatGrid.length;i++){
								if(recordsDatGrid[i].get('criterio')=='FECHAS')
									indice=i;
							}
							
							if(indice>0){
								Ext.Msg.alert('SET','Ya indicó FECHAS necesita borralas antes');
								combo.setDisabled(false);
								return;
							}else{
								combo.setDisabled(true);
								var datos = new datosClase({
				                	criterio: 'FECHAS',
									valor: ''
				            	});
		                        NS.gridDatos.stopEditing();
			            		NS.storeDatos.insert(tamGrid, datos);
			            		NS.gridDatos.getView().refresh();
			            		
							}
							
						}else if(combo.getValue() == 8){
							Ext.getCmp(PF+'clavePropuestaTxt').show();
							//NS.clavePropuestaTxt.setVisible(true);
		    				NS.txtFechaInicial.setVisible(false);
		    				NS.txtFechaFinal.setVisible(false);
		    				NS.importeUno.setVisible(false);
		    			    NS.importeDos.setVisible(false);
		    			    Ext.getCmp(PF+'numeroChequeTxt').hide();
		    			    //NS.numeroChequeTxt.setVisible(false);
		    			    NS.cmbChequera.setVisible(false);
		    			    NS.cmbBanco.setVisible(false);
		    			    NS.proveedorTxt.setVisible(false);
		    			    
							var indice=0;
							var i=0;
							var recordsDatGrid=NS.storeDatos.data.items;
							for(i=0;i<recordsDatGrid.length;i++){
								if(recordsDatGrid[i].get('criterio')=='CLAVE CONTROL')
									indice=i;
							}
							
							if(indice>0){
								Ext.Msg.alert('SET','Ya indicó CLAVE CONTROL necesita borralo antes');
								combo.setDisabled(false);
								return;
							}else{
								combo.setDisabled(true);
								var datos = new datosClase({
				                	criterio: 'CLAVE CONTROL',
									valor: ''
				            	});
		                        NS.gridDatos.stopEditing();
			            		NS.storeDatos.insert(tamGrid, datos);
			            		NS.gridDatos.getView().refresh();
							}
							
						}
						else if(combo.getValue() == 10){
							NS.importeUno.setVisible(true);
		    			    NS.importeDos.setVisible(true);
		    			    NS.cmbBanco.setVisible(false);
		    			    NS.cmbChequera.setVisible(false);
		    			    NS.txtFechaInicial.setVisible(false);
		    				NS.txtFechaFinal.setVisible(false);
		    				//NS.clavePropuestaTxt.setVisible(false);
		    				Ext.getCmp(PF+'clavePropuestaTxt').hide();
		    				//NS.numeroChequeTxt.setVisible(false);
		    				Ext.getCmp(PF+'numeroChequeTxt').hide();
		    			
							var indice=0;
							var i=0;
							var recordsDatGrid=NS.storeDatos.data.items;
							for(i=0;i<recordsDatGrid.length;i++){
								if(recordsDatGrid[i].get('criterio')=='IMPORTES')
									indice=i;
							}
							
							if(indice>0){
								Ext.Msg.alert('SET','Ya indicó IMPORTE necesita borralo antes');
								combo.setDisabled(false);
								return;
							}else{
								combo.setDisabled(true);
								var datos = new datosClase({
				                	criterio: 'IMPORTES',
									valor: ''
				            	});
		                        NS.gridDatos.stopEditing();
			            		NS.storeDatos.insert(tamGrid, datos);
			            		NS.gridDatos.getView().refresh();
							}
						}else if(combo.getValue() == 11){
							//alert("Entro, No Cheque.");
							Ext.getCmp(PF+'numeroChequeTxt').show();
							NS.importeUno.setVisible(false);
		    			    NS.importeDos.setVisible(false);
		    			    NS.cmbBanco.setVisible(false);
		    			    NS.cmbChequera.setVisible(false);
		    			    NS.txtFechaInicial.setVisible(false);
		    				NS.txtFechaFinal.setVisible(false);
		    				//NS.clavePropuestaTxt.setVisible(false);
		    				Ext.getCmp(PF+'clavePropuestaTxt').hide();
		    				//NS.numeroChequeTxt.setVisible(false);
							
							var indice=0;
							var i=0;
							var recordsDatGrid=NS.storeDatos.data.items;
							for(i=0;i<recordsDatGrid.length;i++){
								if(recordsDatGrid[i].get('criterio')=='NO. CHEQUE')
									indice=i;
							}
							
							
							if(indice>0){
								Ext.Msg.alert('SET','Ya indicó NO. Cheque necesita borralo antes');
								combo.setDisabled(false);
								return;
							}else{
								combo.setDisabled(true);
								var datos = new datosClase({
				                	criterio: 'NO. CHEQUE',
									valor: ''
				            	});
		                        NS.gridDatos.stopEditing();
			            		NS.storeDatos.insert(tamGrid, datos);
			            		NS.gridDatos.getView().refresh();
							}
						}
					}
				}
			}
		});
		

		
		/*NS.cmbCriterio = new Ext.form.ComboBox({
			store: NS.storeCriterio,
			id: PF + 'cmbCriterio',
			name: PF + 'cmbTipoImpresion',
			width: 210,
			x: 105,
			y: 55,
			typeAhead: true,
			mode: 'local',
			selecOnFocus: true,
			forceSelection: true,
			valueField: 'idCriterio',
			displayField: 'descCriterio',
		    autocomplete: true,
		    emptyText: 'SELECCIONE CRITERIO',
		    triggerAction: 'all',
		    listeners:{
		    	select:{
		    		fn: function(combo, valor)
		    		{
		    		var criterio = NS.cmbCriterio.getValue();
			    		   alert("entro");
		    			   if(valor != ''){
		    				   alert("entro if");
			    			   NS.manejoDeCriterios(criterio);
			    		   }else{
			    			   Ext.Msg.alert('SET', 'Debe seleccionar un criterio');
			    		   }
		    			/*if(combo.getValue() == 'FECHAS'){
		    				NS.txtFechaInicial.setVisible(true);
		    				NS.txtFechaFinal.setVisible(true);
		    				NS.importeUno.setVisible(false);
		    			    NS.importeDos.setVisible(false);
		    			    NS.numeroChequeTxt.setVisible(false);
		    			    NS.clavePropuestaTxt.setVisible(false);
		    			    NS.idChequeraTxt.setVisible(false);
		    			    NS.bancoTxt.setVisible(false);
		    			    NS.proveedorTxt.setVisible(false);
		    			}else if(combo.getValue() == 'IMPORTES'){
		    		
		    				NS.importeUno.setVisible(true);
		    			    NS.importeDos.setVisible(true);
		    			    NS.txtFechaInicial.setVisible(false);
		    				NS.txtFechaFinal.setVisible(false);
		    				NS.clavePropuestaTxt.setVisible(false);
		    				NS.numeroChequeTxt.setVisible(false);
		    				NS.idChequeraTxt.setVisible(false);
		    				NS.bancoTxt.setVisible(false);
		    				NS.proveedorTxt.setVisible(false);
		    			}else if(combo.getValue() == 'CLAVE CONTROL'){
		    				NS.clavePropuestaTxt.setVisible(true);
		    				NS.txtFechaInicial.setVisible(false);
		    				NS.txtFechaFinal.setVisible(false);
		    				NS.importeUno.setVisible(false);
		    			    NS.importeDos.setVisible(false);
		    			    NS.numeroChequeTxt.setVisible(false);
		    			    NS.idChequeraTxt.setVisible(false);
		    			    NS.bancoTxt.setVisible(false);
		    			    NS.proveedorTxt.setVisible(false);
		    			}else if(combo.getValue() == 'NUM_CHEQUE'){
		    				NS.numeroChequeTxt.setVisible(true);
		    				NS.txtFechaInicial.setVisible(false);
		    				NS.txtFechaFinal.setVisible(false);
		    				NS.importeUno.setVisible(false);
		    			    NS.importeDos.setVisible(false);
		    			    NS.clavePropuestaTxt.setVisible(false);
		    			    NS.idChequeraTxt.setVisible(false);
		    			    NS.bancoTxt.setVisible(false);
		    			    NS.proveedorTxt.setVisible(false);
		    			}else if(combo.getValue() == 'CHEQUERA'){
		    				NS.idChequeraTxt.setVisible(true);
		    				NS.txtFechaInicial.setVisible(false);
		    				NS.txtFechaFinal.setVisible(false);
		    				NS.importeUno.setVisible(false);
		    			    NS.importeDos.setVisible(false);
		    			    NS.clavePropuestaTxt.setVisible(false);
		    			    NS.numeroChequeTxt.setVisible(false);
		    			    NS.bancoTxt.setVisible(false);
		    			    NS.proveedorTxt.setVisible(false);
		    			}else if(combo.getValue() == 'BANCO'){
		    				NS.bancoTxt.setVisible(true);
		    				NS.idChequeraTxt.setVisible(false);
		    				NS.txtFechaInicial.setVisible(false);
		    				NS.txtFechaFinal.setVisible(false);
		    				NS.importeUno.setVisible(false);
		    			    NS.importeDos.setVisible(false);
		    			    NS.clavePropuestaTxt.setVisible(false);
		    			    NS.numeroChequeTxt.setVisible(false);
		    			    NS.proveedorTxt.setVisible(false);
		    			}else if(combo.getValue() == 'PROVEEDOR'){
		    				NS.proveedorTxt.setVisible(true);
		    				NS.bancoTxt.setVisible(false);
		    				NS.idChequeraTxt.setVisible(false);
		    				NS.txtFechaInicial.setVisible(false);
		    				NS.txtFechaFinal.setVisible(false);
		    				NS.importeUno.setVisible(false);
		    			    NS.importeDos.setVisible(false);
		    			    NS.clavePropuestaTxt.setVisible(false);
		    			    NS.numeroChequeTxt.setVisible(false);
		    			}*/
		    			
		//    		}
		//    	}
		//    }
		//});
		
		//combos de los criterios
		//store banco
		NS.storeBanco = new Ext.data.DirectStore({
			paramsAsHash: false,
			baseParams: {idEmpresa:'0'},
			paramOrder: ['idEmpresa'],
			directFn: ChequesPorEntregarAction.llenaBanco,
			idProperty: 'idBanco',
			fields: [
				 {name: 'idBanco'},
		 	     {name: 'descBanco'}
			],
			listeners: {
				load: function(s, records){
					if (records.length == null || records.length <= 0){
						Ext.Msg.alert('SET', 'No existen bancos');
						NS.empresaTxt.setValue('');
						NS.cmbEmpresa.reset();
					}
				}
			}
		});
		
		//store chequera
		
			
			//directFn: GlobalAction.llenarComboGeneral, 
			//idProperty: 'descripcion', 
			/*fields: [
				 {name: 'descripcion'},
				 {name: 'descripcion'}
			],
			listeners: {
				load: function(s, records){
					
				}
			}*/

		NS.storeChequera = new Ext.data.DirectStore({
			paramsAsHash: false,
			root: '',
			baseParams: {idEmpresa:'0',idBanco:'0'},
			paramOrder: ['idEmpresa','idBanco'],
			idProperty: 'descChequera',
			directFn: ChequesPorEntregarAction.llenaChequera,
			idProperty: 'idChequera',
			fields:   
			[
			 	{name: 'idChequera'},
			 	{name: 'descChequera'}
			],
			listeners: {
				load: function(s, records) {
					
					if (records.length == null || records.length <= 0){
						Ext.Msg.alert('SET', 'No existen chequeras');
						NS.cmbBanco.reset();
					}
				}
			}
		});
		
		
		//NS.storeBanco.load();
		NS.cmbBanco = new Ext.form.ComboBox({
			store: NS.storeBanco
			,name: PF+'cmbBanco'
			,id: PF+'cmbBanco'
			,typeAhead: true
			,mode: 'local'
			,minChars: 0
			,selecOnFocus: true
			,forceSelection: true
	        ,x: 105
	        ,y: 100
	        ,width: 210
			,valueField:'idBanco'
			,displayField:'descBanco'
			,autocomplete: true
			,emptyText: 'Seleccione un banco'
			,triggerAction: 'all'
			,value: ''
			,hidden: true
			,listeners:{
				select:{
					fn: 
					function(combo, valor) {
						if(NS.empresaTxt.getValue!=''){
							NS.accionarCmbBanco(combo.getValue());
							var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeChequera, msg: "Cargando Chequeras..."});
							NS.storeChequera.baseParams.idEmpresa=''+NS.empresaTxt.getValue();
							NS.storeChequera.baseParams.idBanco=''+NS.cmbBanco.getValue();
							NS.storeChequera.load();
							NS.cmbChequera.reset();
							}else{
								return Ext.Msg.alert('SET',"No se ha seleccionado una empresa"); 
							}
					}
				}
			  }
		});
		
		//NS.storeChequera.load();
		//combo chequera
		NS.cmbChequera = new Ext.form.ComboBox({
			store: NS.storeChequera
			,name: PF+'cmbChequera'
			,id: PF+'cmbChequera'
			,typeAhead: true
			,mode: 'local'
			,minChars: 0
			,selecOnFocus: true
			,forceSelection: true
	        ,x: 105
	        ,y: 100
	        ,width: 210
			,valueField:'idChequera'
			,displayField:'descChequera'
			,autocomplete: true
			,emptyText: 'Seleccione una chequera'
			,triggerAction: 'all'
			,value: ''
			,hidden: true
			,listeners:{
				select:{
					fn: 
					function(combo, valor) {
						NS.accionarCmbChequera(combo.getValue());
					}
				}
			}
		});
	
		
		
		NS.accionarCmbBanco = function(comboValor){
			//Ext.getCmp(PF+'cmbChequera').reset();
			NS.cmbChequera.reset();
			if(NS.empresaTxt.getValue() == ''){
				NS.storeChequera.baseParams.condicion = 'id_banco = '+ comboValor +' AND no_empresa = ' + NS.GI_ID_EMPRESA;
			}else{
				NS.storeChequera.baseParams.condicion = 'id_banco = '+ comboValor +' AND no_empresa = "'+ NS.empresaTxt.getValue()+'" ';
			}
			//NS.storeChequera.load();
			
			var banco = NS.storeBanco.getById(comboValor).get('descBanco');
			
			var datosClase = NS.gridDatos.getStore().recordType;
			var indice=0;
			var i=0;
			var recordsDatGrid=NS.storeDatos.data.items;
			
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
	  		NS.cmbCriterio.setDisabled(false);
	  		
	  		//Ext.getCmp(PF+'cmbCriterio').setDisabled(false);
		}
		
		
		NS.accionarCmbChequera = function(comboValor){
			var chequera = NS.storeChequera.getById(comboValor).get('descChequera');
			var datosClase = NS.gridDatos.getStore().recordType;
			var indice=0;
			var i=0;
			var recordsDatGrid=NS.storeDatos.data.items;
			
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
				valor: chequera
	   		});
	        NS.gridDatos.stopEditing();
	  		NS.storeDatos.insert(indice, datos);
	  		NS.gridDatos.getView().refresh();
	  		NS.cmbCriterio.setDisabled(false);
	  		//Ext.getCmp(PF+'cmbCriterio').setDisabled(false);
		}

		
		///////////////////////////////////////////////////////////////////////////
		
		NS.storeLlenarComboSolicitantes = new Ext.data.DirectStore({
			paramsAsHash: true,
			root: '',
			baseParams: {tipoSol: ''+NS.opcionesSol},
			paramOrder: ['tipoSol'],
			directFn: ChequesPorEntregarAction.obtenerSolicitantes,
			fields:
				[
				 {name: 'idPersona'},
				 {name: 'nombre'}
				 
				 ],
				 idProperty: 'idPersona',
				 listeners:
				 {
					 load: function(s, records)
					 {
						 var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeLlenarComboSolicitantes, msg: "Cargando Datos ..."});
						 if(records.length == null || records.length <= 0){
							 Ext.Msg.alert('SET', 'No hay solicitantes que mostrar');
						 }else{
						 }
					 }
				 }
		});
		var myMask = new Ext.LoadMask(Ext.getBody(), {store:NS.storeLlenarComboSolicitantes, msg: "Cargando Solicitantes ..."});
		NS.storeLlenarComboSolicitantes.load();
		
		NS.cmbSolicitantes = new Ext.form.ComboBox({
			store: NS.storeLlenarComboSolicitantes,
			id: PF + 'cmbSolicitantes',
			name: PF + 'cmbSolicitantes',
			width: 200,
			x: 75,
			y: 235,
			typeAhead: true,
			mode: 'local',
			selecOnFocus: true,
			forceSelection: true,
			valueField: 'idPersona',
			displayField: 'nombre',
			autocomplete: true,
			emptyText: 'SELECCIONE SOLICITANTE',
			triggerAction: 'all',
			listeners:{
				select:{
					fn:function(combo, valor){
						BFwrk.Util.updateComboToTextField(PF + 'identificacionTxt', NS.cmbSolicitantes.getId());
					}
				}
				
			}
		});
		
		NS.optOpcionesSol = new Ext.form.RadioGroup({
			id: PF + 'optOpcionesSol',
			name: PF + 'optOpcionesSol',
			x: 300,
			y: 235,
			columns: 1,
			items:
			[
			 	{boxLabel: 'Solicitante', name: 'optOpcionesSol', inputValue: 0, checked: true,
			 		listeners: {
			 				check: {
			 					fn: function(opt, valor) {
			 						if (valor == true) {
			 							var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeLlenarComboSolicitantes, msg: "Cargando Solicitantes ..."});
			 							NS.opcionesSol = 'solicitantes';
			 							NS.storeLlenarComboSolicitantes.baseParams.tipoSol = ''+NS.opcionesSol;
			 							NS.storeLlenarComboSolicitantes.load();
			 							NS.cmbSolicitantes.reset();
			 							NS.identificacionTxt.setValue('');
		 								
			 						}
			 							
			 					}
			 				}
			 			}
			 	},
			 	{boxLabel: 'Solicitante Alterno', name: 'optOpcionesSol', inputValue: 1 ,
			 		listeners: {
			 				check: {
			 					fn: function(opt, valor) {
			 						if (valor == true) {
			 							var myMask = new Ext.LoadMask(Ext.getBody(), {store: NS.storeLlenarComboSolicitantes, msg: "Cargando Solicitantes ..."});
			 							NS.opcionesSol = 'alternos';
			 							NS.storeLlenarComboSolicitantes.baseParams.tipoSol = ''+NS.opcionesSol;
			 							NS.storeLlenarComboSolicitantes.load();
			 							NS.cmbSolicitantes.reset();
			 							NS.identificacionTxt.setValue('');
			 						}
			 						
			 						
			 						
			 					}
			 					
			 				}
			 			}
			 	},
			 	
			 	
			]
		});
	/*
	 * GRID'S
	 */	
		
		
		
		NS.storeLlenarGridCheques = new Ext.data.DirectStore({
			paramsAsHash: true,
			baseParams: {json: '',entregado: 'false'},
			paramOrder: ['json','entregado'],
			root: '',
			directFn: ChequesPorEntregarAction.obtenerCheques,
			fields:
			[

				{name: 'noEmpresa'},
			 	{name: 'nomEmpresa'},
			    {name: 'noProveedor'},
			    {name: 'nombreProveedor'},
			    {name: 'idBanco'},
			 	{name: 'nombreBanco'},
			 	{name: 'idChequera'},
			 	{name: 'noCheque'},
			 	{name: 'importe'},
			 	{name: 'fecImprime'},
			 	{name: 'divisa'},
			 	{name: 'entregado'},
			 	{name: 'fecEntregado'},
			 	{name: 'usuEntregado'},
			 	{name: 'poHeaders'},
			 	{name: 'fecPropuesta'},
			],
			listeners:
			{
				load: function(s, records)
				{
					if(records.length == null || records.length <= 0){
						Ext.Msg.alert('SET', 'No hay datos que mostrar');
					}
					NS.calcularRenglones();
				}
			}
		});
		
		
		NS.seleccionGridCheques = new Ext.grid.CheckboxSelectionModel({
			singleSelect: false,
			listeners:{
				rowselect: {
					fn:function(r, rowIndex){
						NS.calcularRenglones();
						
					}
				},
				rowdeselect: {
					fn:function(r, rowIndex){
						NS.calcularRenglones();
					}
				}
			}
		});  
		
		
		NS.columnasDatosCheques = new Ext.grid.ColumnModel([
          NS.seleccionGridCheques,
          {header: 'NO. EMPRESA', width: 100, dataIndex:'noEmpresa',sortable: true, hidden:true},
		  {header: 'EMPRESA', width: 200, dataIndex: 'nomEmpresa',sortable: true},
		  {header: 'NO. PROVEEDOR', width: 100, dataIndex: 'noProveedor',sortable: true},	
		  {header: 'NOMBRE PROVEEDOR', width: 200, dataIndex: 'nombreProveedor',sortable: true},
		  {header: 'ID. BANCO', width: 100, dataIndex: 'idBanco',sortable: true},
		  {header: 'BANCO', width: 150, dataIndex: 'nombreBanco',sortable: true,hidden:true},
		  {header: 'CHEQUERA', width: 100, dataIndex: 'idChequera',sortable: true},
		  {header: 'NO. CHEQUE', width: 80, dataIndex: 'noCheque',sortable: true},       
		  {header: 'IMPORTE', width: 120, dataIndex: 'importe', renderer: BFwrk.Util.rendererMoney ,align:'right',sortable: true},
		  {header: 'FECHA IMPRESIÓN', width: 120, dataIndex: 'fecImprime',sortable: true},  
		  {header: 'DIVISA', width: 120, dataIndex: 'divisa',sortable: true},
		  {header: 'ENTREGADO', width: 80, dataIndex: 'entregado',sortable: true},
		  {header: 'FECHA ENTREGA', width: 120, dataIndex: 'fecEntregado',sortable: true},
		  {header: 'USUARIO ENTREGA/OBSERVACION', width: 150, dataIndex: 'usuEntregado', sortable: true},
		  {header: 'POLIZA DE COMPENSACIÓN', width: 150, dataIndex: 'poHeaders', sortable: true, hidden:true},
		  {header: 'FECHA PROPUESTA', width: 150, dataIndex: 'fecPropuesta', sortable: true, hidden:true}
		]);
		
		NS.gridDatosCheques = new Ext.grid.GridPanel({
			store: NS.storeLlenarGridCheques,
			id: PF + 'gridDatosCheques',
			x: 0,
			y: 5,
			cm: NS.columnasDatosCheques,
			sm: NS.seleccionGridCheques,
			lockColumns: 3, 
			width: 960,
			height: 200,
			border: false,
			stripeRows: true,
			listeners:{
				rowdblClick:{
					fn:function(grid){
						
					}
				}
			}
	      
		});
				
		NS.storeDatos = new Ext.data.DirectStore({
			paramsAsHash: false,
			root: '',
			idProperty: 'criterio',//identificador del store
			fields: [
				{name: 'criterio'},
				{name: 'valor'}
			]
		});
		
		//grid criterios
		NS.gridDatos  = new Ext.grid.GridPanel({
			store : NS.storeDatos,
			id: PF+'gridDatos',
			name: PF+'gridDatos',
			width: 275,
	       	height: 145,
			x :390,
			y :0,
			frame: true,
			title :'',
			columns : [ 
			{
				id :'criterio',
				header :'Criterio',
				width :120,
				dataIndex :'criterio'
			}, 
			{
				header :'Valor',
				width :135,
				dataIndex :'valor'
			} ],
			listeners:{
				dblclick:{
					fn:function(grid){
						var records = NS.gridDatos.getSelectionModel().getSelections();
						var recordsDatGrid=NS.storeDatos.data.items;
						var tamGrid=(NS.storeDatos.data.items).length;
						for(var i=0;i<records.length;i++){
							if(records[i].get('criterio')=='BANCO' && tamGrid>1 && recordsDatGrid[i].get('criterio')=='CHEQUERA'){	
										Ext.Msg.alert('SET', 'No se puede eliminar el Banco');
										return;
								
									//Ext.Msg.alert('SET', 'Ya se agrego un Banco');
							}else{
								if(records[i].get('criterio')=='BANCO'){
									NS.cmbBanco.reset();
									NS.cmbBanco.setVisible(false);
								}else if(records[i].get('criterio')=='FECHAS'){
									NS.txtFechaInicial.setValue('');
									NS.txtFechaInicial.setVisible(false);
									NS.txtFechaFinal.setValue('');
									NS.txtFechaFinal.setVisible(false);
								}else if(records[i].get('criterio')=='CLAVE CONTROL'){
									Ext.getCmp(PF+'clavePropuestaTxt').setValue('');
									Ext.getCmp(PF+'clavePropuestaTxt').hide();
								}else if(records[i].get('criterio')=='IMPORTES'){
									NS.importeUno.setValue('');
									NS.importeDos.setValue('');
									NS.importeUno.setVisible(false);
									NS.importeDos.setVisible(false);
								}else if(records[i].get('criterio')=='NO. CHEQUE'){
									Ext.getCmp(PF+'numeroChequeTxt').setValue('');
									Ext.getCmp(PF+'numeroChequeTxt').hide();
								}else if(records[i].get('criterio')=='CHEQUERA'){
									NS.cmbChequera.reset();
									NS.cmbChequera.setVisible(false);
								}
									NS.gridDatos.store.remove(records[i]);
									NS.gridDatos.getView().refresh();
									NS.cmbCriterio.setDisabled(false);
									NS.cmbCriterio.reset();
									
									
									
									
							}
								
							//Ext.getCmp(PF+'cmbCriterio').setDisabled(false);
						}
					}
				}
			}
		});
		
		/*NS.gridDatos  = new Ext.grid.GridPanel({
			store : NS.storeDatos,
			id: PF+'gridDatos',
			name: PF+'gridDatos',
			width: 375,
	       	height: 130,
			x :380,
			y :0,
			frame: true,
			title :'',
			columns : [ 
			{
				id :'criterio',
				header :'Criterio',
				width :170,
				dataIndex :'criterio'
			}, 
			{
				header :'Valor',
				width :185,
				dataIndex :'valor'
			} ],
			listeners:{
				rowdblClick:{
					fn:function(grid, index){
						var renglon = grid.getSelectionModel().getSelected();
						
						switch(renglon.get('criterio')){
							case 'CLAVE CONTROL':
							
								NS.clavePropuestaTxt.setVisible(true);
			    				NS.txtFechaInicial.setVisible(false);
			    				NS.txtFechaFinal.setVisible(false);
			    				NS.importeUno.setVisible(false);
			    			    NS.importeDos.setVisible(false);
			    			    NS.numeroChequeTxt.setVisible(false);
			    			    NS.idChequeraTxt.setVisible(false);
			    			    NS.bancoTxt.setVisible(false);
			    			    NS.proveedorTxt.setVisible(false);
			    			    
			            		NS.clavePropuestaTxt.setDisabled(false);
			            		NS.clavePropuestaTxt.setValue('');
								break;
							case 'IMPORTES':
								
								NS.importeUno.setVisible(true);
			    			    NS.importeDos.setVisible(true);
			    			    NS.txtFechaInicial.setVisible(false);
			    				NS.txtFechaFinal.setVisible(false);
			    				NS.clavePropuestaTxt.setVisible(false);
			    				NS.numeroChequeTxt.setVisible(false);
			    				NS.idChequeraTxt.setVisible(false);
			    				NS.bancoTxt.setVisible(false);
			    				NS.proveedorTxt.setVisible(false);
								
			    				NS.importeUno.setDisabled(false);
			    				NS.importeDos.setDisabled(false);
			    				NS.importeUno.setValue('');
			    				NS.importeDos.setValue('');
								break;
							case 'FECHAS':
								NS.txtFechaInicial.setVisible(true);
			    				NS.txtFechaFinal.setVisible(true);
			    				NS.importeUno.setVisible(false);
			    			    NS.importeDos.setVisible(false);
			    			    NS.clavePropuestaTxt.setVisible(false);
			    			    NS.numeroChequeTxt.setVisible(false);
			    			    NS.idChequeraTxt.setVisible(false);
			    			    NS.bancoTxt.setVisible(false);
			    			    NS.proveedorTxt.setVisible(false);
			    			    
			    			    NS.txtFechaInicial.setDisabled(false);
			    				NS.txtFechaFinal.setDisabled(false);
			    				NS.txtFechaInicial.setValue('');
			    				NS.txtFechaFinal.setValue('');
								break;
							case 'NUM_CHEQUE':
								NS.numeroChequeTxt.setVisible(true);
								NS.txtFechaInicial.setVisible(false);
			    				NS.txtFechaFinal.setVisible(false);
								NS.importeUno.setVisible(false);
			    			    NS.importeDos.setVisible(false);
			    			    NS.clavePropuestaTxt.setVisible(false);
			    			    NS.idChequeraTxt.setVisible(false);
			    			    NS.bancoTxt.setVisible(false);
			    			    NS.proveedorTxt.setVisible(false);
			    			    
			    			    NS.numeroChequeTxt.setDisabled(false);
			    			    NS.numeroChequeTxt.setValue('');
								break; 
							case 'CHEQUERA':
								NS.idChequeraTxt.setVisible(true);
								NS.numeroChequeTxt.setVisible(false);
								NS.txtFechaInicial.setVisible(false);
			    				NS.txtFechaFinal.setVisible(false);
								NS.importeUno.setVisible(false);
			    			    NS.importeDos.setVisible(false);
			    			    NS.clavePropuestaTxt.setVisible(false);
			    			    NS.bancoTxt.setVisible(false);
			    			    NS.proveedorTxt.setVisible(false);
			    			    
			    			    NS.idChequeraTxt.setDisabled(false);
			    			    NS.idChequeraTxt.setValue('');
								break;
							case 'BANCO':
								NS.bancoTxt.setVisible(true);
								NS.idChequeraTxt.setVisible(false);
								NS.numeroChequeTxt.setVisible(false);
								NS.txtFechaInicial.setVisible(false);
			    				NS.txtFechaFinal.setVisible(false);
								NS.importeUno.setVisible(false);
			    			    NS.importeDos.setVisible(false);
			    			    NS.clavePropuestaTxt.setVisible(false);
			    			    NS.proveedorTxt.setVisible(false);
								
			    			    NS.bancoTxt.setDisabled(false);
			    			    NS.bancoTxt.setValue('');
								break;
							case 'PROVEEDOR':
								NS.proveedorTxt.setVisible(true);
								NS.bancoTxt.setVisible(false);
								NS.idChequeraTxt.setVisible(false);
								NS.numeroChequeTxt.setVisible(false);
								NS.txtFechaInicial.setVisible(false);
			    				NS.txtFechaFinal.setVisible(false);
								NS.importeUno.setVisible(false);
			    			    NS.importeDos.setVisible(false);
			    			    NS.clavePropuestaTxt.setVisible(false);
			    			    
			    			    NS.proveedorTxt.setDisabled(false);
			    			    NS.proveedorTxt.setValue('');
								break;
						
						}
						NS.cmbCriterio.setValue(renglon.get('criterio'));
						NS.storeDatos.remove(renglon);
						NS.gridDatos.getView().refresh();
						

					}
				}
			}
			           
		});*/
		
		
	 
	 /*
	  * PANEL'S
	  */
	 
	 
	
    NS.PanelContenedorCheques = new Ext.form.FieldSet({
    	title: 'CHEQUES',
    	id: PF + 'PanelContenedorCheques',
    	layout: 'absolute',
    	width: 978,
    	height: 400,
    	x: 0,
    	y: 220,
    	items:[NS.gridDatosCheques,
    	       NS.mTotalMnLbl,
    	       NS.mTotalMnNmb,
    	       NS.mTotalDlsLbl,
    	       NS.mTotalDlsNmb,
    	       NS.rTotalMnNmb,
    	  	   NS.rTotalDlsNmb,
    	  	   NS.rTotalMnLbl,
    	  	   NS.rTotalDlsLbl,
    	  	   NS.cmbSolicitantes,
    	  	   NS.solicitanteLbl,
    	  	   NS.identificacionLbl,
    	  	   NS.identificacionTxt,
    	  	   NS.fechaEntregaLbl,
    	  	   NS.optOpcionesSol,
    	  	   NS.txtFechaEntrega    	  	   
    	],
    	buttons:[
    	         {
    	        	 text: 'Ejecutar',
    	        	 handler: function(){
    	        		 NS.datos = NS.gridDatosCheques.getSelectionModel().getSelections();
    	        		 if(NS.datos.length > 0){
    	        			 if(NS.cmbSolicitantes.getValue() != ''){
    	        				 if(NS.txtFechaEntrega.getValue() != ''){
			    	        		 NS.poHeaders = '';
			    	        		 NS.idChequera = '';
			    	        		 NS.fecPropuesta = '';
			    	        		 //NS.noEmpresa = NS.gridDatosCheques.getSelectionModel().getSelections()[0].get('noEmpresa');
			    	        		 //NS.idBanco = NS.gridDatosCheques.getSelectionModel().getSelections()[0].get('idBanco');
			    	        		 for(var i = 0; i < NS.datos.length; i ++){
			    	        			 NS.poHeaders = NS.poHeaders + NS.datos[i].get('poHeaders');
			    	        			 //NS.idChequera = NS.idChequera + NS.datos[i].get('idChequera');
			    	        			 //NS.fecPropuesta = NS.fecPropuesta + NS.datos[i].get('fecPropuesta');
				        				 if(i < NS.datos.length - 1){
				        					 NS.poHeaders = NS.poHeaders + ',';
				        					 //NS.idChequera = NS.idChequera + ',';
				        					 //NS.fecPropuesta = NS.fecPropuesta + ',';
				        				 }
				        			 }
				        			 //NS.poHeaders = NS.poHeaders + '';
				        			 //NS.idChequera = NS.idChequera + '';
				        			 //NS.fecPropuesta = NS.fecPropuesta + '';
				        			 var jsonDatos = '[{"poHeaders":"' + NS.poHeaders + '","nombre":"' + NS.cmbSolicitantes.getRawValue() + 
				        			 	'","fecha":"' + cambiarFecha(''+NS.txtFechaEntrega.getValue())+'"}]';	 
				        			 ChequesPorEntregarAction.enviarDatos(jsonDatos, function(resultado, e){
				        				 Ext.Msg.alert('SET', resultado);
				        				 //NS.storeLlenarGridCheques.reload();
				        				 NS.limpiarTodo();
				        			 });
    	        				 }else{
    	        					 Ext.Msg.alert('SET', 'Debe seleccionar una fecha entrega');
    	        				 }
    	        			 }else{
    	        				 Ext.Msg.alert('SET', 'Debe seleccionar un solicitante');
    	        			 }
    	        		 }else{
    	        			 Ext.Msg.alert('SET', 'No se han seleccionado registros');
    	        		 }
    	        	 }
    	         },
    	         {
    	        	 text: 'Excel',
    	        	 handler: function(){
    	        		 var datosSeleccion = NS.gridDatosCheques.getSelectionModel().getSelections();
    	        		 if(datosSeleccion.length > 0){
    	        			 var matrizDatos = new Array();
    	        			 for(var i = 0; i < datosSeleccion.length; i++){
    	        				var vecDatos = {};
    	        				vecDatos.nomEmpresa = datosSeleccion[i].get('nomEmpresa');
    	        				vecDatos.noProveedor = datosSeleccion[i].get('noProveedor');
    	        				vecDatos.nombreProveedor = datosSeleccion[i].get('nombreProveedor');
    	        				vecDatos.nombreBanco = datosSeleccion[i].get('nombreBanco');
    	        				vecDatos.idChequera = datosSeleccion[i].get('idChequera');
    	        				vecDatos.noCheque = datosSeleccion[i].get('noCheque');
    	        				vecDatos.importe = datosSeleccion[i].get('importe');
    	        				vecDatos.fecImprime = datosSeleccion[i].get('fecImprime');
    	        				vecDatos.divisa = datosSeleccion[i].get('divisa');
    	        				vecDatos.entregado = datosSeleccion[i].get('entregado');
    	        				vecDatos.fecEntregado = datosSeleccion[i].get('fecEntregado');
    	        				vecDatos.usuEntregado = datosSeleccion[i].get('usuEntregado');
    	        				matrizDatos[i] = vecDatos; 
    	        			 }
    	        			 
    	        			 var json = Ext.util.JSON.encode(matrizDatos);
    	        			 ChequesPorEntregarAction.exportarExcel(json, function(res, e){
    	        				 if (res != null && res != undefined && res == "") {
    	        						Ext.Msg.alert('SET', "Error al generar el archivo");
    	        					}else {
    	        						strParams = '?nomReporte=chequesPorEntregar';
    	        						strParams += '&'+'nomParam1=nomArchivo';
    	        						strParams += '&'+'valParam1='+res;
    	        						window.open("/SET/jsp/bfrmwrk/gnrators/abreExcel.jsp"+strParams);
    	        					}

    	        			 });
    	        			
    	        		 }else{
    	        			 Ext.Msg.alert('SET', 'No se han seleccionado registros');
    	        		 }
    	        	 }
    	         },
    	         {
    	        	 text: 'Limpiar',
    	        	 handler: function(){
    	        		 NS.limpiarTodo();
    	        	 }
    	         }
	    ]       
    });
		
	
	NS.PanelContenedorCriteriosBusqueda = new Ext.form.FieldSet({
		title: 'BUSQUEDA',
		id: PF + 'PanelContenedorCriteriosBusqueda',
		layout: 'absolute',
		width: 978,
		height: 210,
		x: 0,
		y: 0,
		items:[
		       NS.empresaLbl,
		       NS.empresaTxt,
		       NS.cmbEmpresa,
		       NS.criterioLbl,
		       NS.cmbCriterio,
		       NS.valorLbl,
		       NS.txtFechaInicial,
		       NS.txtFechaFinal,
		       NS.importeUno,
		       NS.importeDos,
		       NS.clavePropuestaTxt,
		       NS.numeroChequeTxt,
		       NS.cmbChequera,
		       NS.idChequeraTxt,
		       NS.bancoTxt,
		       NS.cmbBanco,
		       NS.proveedorTxt,
		       NS.gridDatos,
		       NS.proveedorLbl,
		       NS.txtProveedor,
		       NS.cmbProveedor,
		       NS.cmdProveedor,
               {
                   xtype: 'checkbox',
                   id: PF + 'chkEntregados',
                   x: 770,
                   y: 55,
                   boxLabel: 'Cheques Entregados',
                   listeners:{
                   check:{
                   		fn:function(checkBox,valor){
                       		if(valor==true) {
                    				NS.storeLlenarGridCheques.baseParams.entregado = 'true';

                       		}else{
                    			  	NS.storeLlenarGridCheques.baseParams.entregado = 'false';
	                        }
                   		}
                   }
                 }
               },
		       
		       /*{
		    	   xtype: 'button',
		    	   text: 'Agregar',
		    	   x: 0,
		    	   y: 175,
		    	   width: 80,
		    	   height: 22,
		    	   handler: function(){
		    		   var criterio = NS.cmbCriterio.getValue();
		    		   
		    		   if(criterio != ''){
		    			   NS.manejoDeCriterios(criterio);
		    			   
		    		   }else{
		    			   Ext.Msg.alert('SET', 'Debe seleccionar un criterio');
		    		   }

		    	   }
		       },*/
		       
		       {
		    	   xtype: 'button',
		    	   text: 'Buscar',
		    	   x: 800,
		    	   y: 135,
		    	   width: 80,
		    	   height: 22,
		    	   handler: function(){ 
		    		   if(NS.cmbEmpresa.getValue()!="" && NS.cmbBanco.getValue()!=""){
		    		   var json = '[{';
		    		   if(NS.cmbEmpresa.getValue() != "" && NS.empresaTxt.getValue() !=""){
		    			   json = json + '"empresa":"' + NS.empresaTxt.getValue() + '",'; 
		    		   }else{
		    			   json = json + '"empresa":"",';
		    		   }if(NS.txtFechaInicial.getValue() != '' && NS.txtFechaFinal.getValue() != ''){
		    			   var valorFecIni=cambiarFecha(''+NS.txtFechaInicial.getValue());
		    			   var valorFecFin=cambiarFecha(''+NS.txtFechaFinal.getValue());
		    			   json = json + '"fInicial":"' + valorFecIni + '","fFinal":"' + valorFecFin + '",';
		    		   }else{
		    			   json = json + '"fInicial":"","fFinal":"",';
		    		   }if(NS.importeUno.getValue() != '' && NS.importeDos.getValue() != ''){
		    			   json = json + '"iUno":"' + NS.importeUno.getValue() + '","iDos":"' + NS.importeDos.getValue() + '",'; 
		    		   }else{
		    			   json = json + '"iUno":"","iDos":"",';
		    		   }if(Ext.getCmp(PF+'clavePropuestaTxt').getValue() != ''){
		    			   json = json + '"clave":"' + Ext.getCmp(PF+'clavePropuestaTxt').getValue() + '",'; 
		    		   }else{
		    			   json = json + '"clave":"",';
		    		   }if(Ext.getCmp(PF+'numeroChequeTxt').getValue() != ''){
		    			   json = json + '"noCheque":"' + Ext.getCmp(PF+'numeroChequeTxt').getValue() + '",';  
		    		   }else{
		    			   json = json + '"noCheque":"",';
		    		   }if(NS.cmbChequera.getValue() != ''){
		    			   json = json + '"chequera":"' + NS.cmbChequera.getValue() + '",'; 
		    		   }else{
		    			   json = json + '"chequera":"",';
		    		   }if(NS.cmbBanco.getValue() != ''){
		    			   json = json + '"banco":"' + NS.cmbBanco.getValue() + '",';  
		    		   }else{
		    			   json = json + '"banco":"",';
		    		   }if(NS.cmbProveedor.getValue() != ''){
		    			   json = json + '"proveedor":"' + NS.cmbProveedor.getValue() + '"';
		    		   }else{
		    			   json = json + '"proveedor":""';
		    		   }
		    		   
		    		   json = json + '}]';
		    		   NS.storeLlenarGridCheques.baseParams.json = json;
		    		   var myMask = new Ext.LoadMask(Ext.getBody(), {msg: "Cargando Datos...",store: NS.storeLlenarGridCheques});
		    		   NS.storeLlenarGridCheques.load();
		    		   }else{
		    			  return  Ext.Msg.alert('SET','Debes elegir una empresa y un banco');
		    		   }
		    		   //NS.limpiarTodo();
		    	   }
		       }
		       
		],
		buttons:[

		]
	
	});
	
	
	
	NS.PanelContenedorGeneral = new Ext.form.FieldSet({
		id: PF + 'PanelContenedorGeneral',
		layout: 'absolute',
		width: 1010,
		height: 650,
		items: [
		        NS.PanelContenedorCriteriosBusqueda,
		        NS.PanelContenedorCheques
		]
	});
	
	
	NS.CPEntregar = new Ext.form.FormPanel({
		title: 'Cheques Por Entregar',
		width: 1010,
		height: 800,
		frame: true,
		padding: 10,
		autoScroll: true,
		layout: 'absolute',
		id: PF + 'CPEntregar',
		name: PF + 'CPEntregar',
		renderTo: NS.tabContId,
		html : '<img src="img/formas/Fondo_Form.png" style="height:100%;width:100%;" alt="fondo" align="middle">',
		items: [
		        	NS.PanelContenedorGeneral
		        	
		        ]
	});
	
	NS.CPEntregar.setSize(Ext.get(NS.tabContId).getWidth(), Ext.get(NS.tabContId).getHeight());

	
});