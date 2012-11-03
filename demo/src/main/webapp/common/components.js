var treeMenuData = [];
var DataItem = function(config){
    this.url    = config.url;
    while (this.url.indexOf('.') >=0 ) {
        this.url = this.url.replace('.', '/');
    }
    this.url += '.jsf';
    if (config.mbsrc == '') {
        this.mbsrc  = 'common/source-notfound.html';
    }
    else {
        this.mbsrc  = 'common/resources/javaSource.jsp?file=' + config.mbsrc;
    }
    if (config.jspsrc == '') {
        this.jspsrc = 'common/resources/jspSource.jsp?file=' + config.url;
    }
    else {
        this.jspsrc = 'common/resources/jspSource.jsp?file=' + config.jspsrc;
    }
}
treeMenuData['ajaxAction']              = new DataItem({url: 'ajax.action', mbsrc: 'demo.DemoBean', jspsrc: 'ajax.action'});
treeMenuData['ajaxStatus']              = new DataItem({url: 'ajax.status', mbsrc: 'demo.DemoBean', jspsrc: 'ajax.status'});
treeMenuData['ajaxLogger']              = new DataItem({url: 'ajax.logger', mbsrc: 'demo.ColorBean', jspsrc: ''});
treeMenuData['ajaxScripter']            = new DataItem({url: 'ajax.scripter', mbsrc: 'demo.ScripterBean', jspsrc: ''});
treeMenuData['clientValidator']         = new DataItem({url: 'ajax.clientValidator', mbsrc: 'demo.ClientValidatorBean', jspsrc: ''});
treeMenuData['percentageProgress']      = new DataItem({url: 'ajax.progress.progress-a', mbsrc: 'demo.ProgressBean', jspsrc: ''});
treeMenuData['phaseProgress']           = new DataItem({url: 'ajax.progress.progress-b', mbsrc: 'demo.ProgressBean', jspsrc: ''});
treeMenuData['complexProgress']         = new DataItem({url: 'ajax.progress.progress-c', mbsrc: 'demo.ProgressBean', jspsrc: ''});
treeMenuData['simpleUpdater']           = new DataItem({url: 'ajax.updater.index', mbsrc: 'demo.UpdaterBean', jspsrc: 'ajax.updater.index:ajax.updater.simple'});
treeMenuData['complexUpdater']          = new DataItem({url: 'ajax.updater.index2', mbsrc: 'demo.UpdaterBean', jspsrc: 'ajax.updater.index2:ajax.updater.simple'});
treeMenuData['updaterNav']              = new DataItem({url: 'ajax.updater.partialNav', mbsrc: 'helloduke.UserBean', jspsrc: 'ajax.updater.partialNav:ajax.updater.greeting:ajax.updater.sameName'});
treeMenuData['ajaxUpdater']             = new DataItem({url: 'ajax.updater.ajaxupdater', mbsrc: 'demo.AjaxUpdaterBean', jspsrc: ''});

treeMenuData['absoluteLayout']          = new DataItem({url: 'layout.single.AbsoluteLayout', mbsrc: '', jspsrc: 'layout.single.AbsoluteLayout'});
treeMenuData['accordionLayout']         = new DataItem({url: 'layout.single.AccordionLayout', mbsrc: '', jspsrc: 'layout.single.AccordionLayout'});
treeMenuData['borderLayout']            = new DataItem({url: 'layout.single.BorderLayout', mbsrc: '', jspsrc: 'layout.single.BorderLayout'});
treeMenuData['cardLayout']              = new DataItem({url: 'layout.single.CardLayout', mbsrc: 'demo.layout.CardLayoutBean', jspsrc: 'layout.single.CardLayout'});
treeMenuData['columnLayout']            = new DataItem({url: 'layout.single.ColumnLayout', mbsrc: '', jspsrc: 'layout.single.ColumnLayout'});
treeMenuData['tabLayout']               = new DataItem({url: 'layout.single.TabLayout', mbsrc: '', jspsrc: 'layout.single.TabLayout'});
treeMenuData['tableLayout']             = new DataItem({url: 'layout.single.TableLayout', mbsrc: '', jspsrc: 'layout.single.TableLayout'});
treeMenuData['teamplateLayoutSimple']   = new DataItem({url: 'template.simple', mbsrc: '', jspsrc: 'template.simple1:template.simple2:template.simpleTemplate'});
treeMenuData['teamplateLayoutComplex']  = new DataItem({url: 'template.index', mbsrc: 'demo.TemplateBean', jspsrc: 'template.index:template.download:template.custom:template.templates.operamasksMain:template.templates.operamasksMain2:template.templates.operamasksMain3'});
treeMenuData['complexLayout']           = new DataItem({url: 'layout.complex.complexLayout', mbsrc: 'demo.layout.ComplexLayoutBean', jspsrc: 'layout.complex.complexLayout'});

treeMenuData['textField']              = new DataItem({url: 'form.single.textField', mbsrc: 'demo.form.single.TextFieldBean', jspsrc: 'form.single.textField'});
treeMenuData['textArea']               = new DataItem({url: 'form.single.textArea', mbsrc: 'demo.form.single.TextAreaBean', jspsrc: 'form.single.textArea'});
treeMenuData['dateField']              = new DataItem({url: 'form.single.dateField', mbsrc: 'demo.form.single.DateFieldBean', jspsrc: 'form.single.dateField'});
treeMenuData['numberField']            = new DataItem({url: 'form.single.numberField', mbsrc: 'demo.form.single.NumberFieldBean', jspsrc: 'form.single.numberField'});
treeMenuData['combo']                  = new DataItem({url: 'form.single.combo', mbsrc: 'demo.form.single.ComboBean', jspsrc: 'form.single.combo'});
treeMenuData['timeField']              = new DataItem({url: 'form.single.timeField', mbsrc: 'demo.form.single.TimeFieldBean', jspsrc: 'form.single.timeField'});
treeMenuData['checkBox']               = new DataItem({url: 'form.single.checkBox', mbsrc: 'demo.form.single.CheckBoxBean', jspsrc: 'form.single.checkBox'});
treeMenuData['checkBoxGroup']          = new DataItem({url: 'form.single.checkBoxGroup', mbsrc: 'demo.form.single.CheckBoxGroupBean', jspsrc: 'form.single.checkBoxGroup'});
treeMenuData['radioGroup']             = new DataItem({url: 'form.single.radioGroup', mbsrc: 'demo.form.single.RadioGroupBean', jspsrc: 'form.single.radioGroup'});
treeMenuData['simpleHtmlEditor']       = new DataItem({url: 'form.single.simpleHtmlEditor', mbsrc: 'demo.form.single.SimpleHtmlEditorBean', jspsrc: 'form.single.simpleHtmlEditor'});
treeMenuData['complexForm']            = new DataItem({url: 'form.complex.complexForm', mbsrc: 'demo.form.complex.ComplexFormBean', jspsrc: 'form.complex.complexForm'});

treeMenuData['datePicker']              = new DataItem({url: 'menu.date-picker', mbsrc: 'demo.MenuBean', jspsrc: ''});
treeMenuData['calcNumberField']         = new DataItem({url: 'form.calcNumberField', mbsrc: 'demo.DemoBean', jspsrc: ''});
treeMenuData['dynamicImage']            = new DataItem({url: 'image.test', mbsrc: 'demo.ImageBean', jspsrc: ''});
treeMenuData['filteredList']            = new DataItem({url: 'form.country', mbsrc: 'demo.CountryBean', jspsrc: ''});
treeMenuData['validateB']               = new DataItem({url: 'form.validate-b', mbsrc: 'demo.DemoBean', jspsrc: ''});
treeMenuData['validate']                = new DataItem({url: 'form.validate', mbsrc: 'demo.DemoBean', jspsrc: ''});

treeMenuData['hello']                   = new DataItem({url: 'binding.greeting', mbsrc: 'demo.binding.HelloBean:demo.binding.HelloAction:demo.binding.HelloValidation', jspsrc: ''});
treeMenuData['calc']                    = new DataItem({url: 'binding.calc', mbsrc: 'demo.binding.CalcBean', jspsrc: ''});
treeMenuData['click']                   = new DataItem({url: 'binding.click', mbsrc: 'demo.binding.ClickBean', jspsrc: ''});
treeMenuData['dynamicModel']            = new DataItem({url: 'binding.select-model', mbsrc: 'demo.binding.ModelSelection:demo.binding.AbstractModel:demo.binding.Model1:demo.binding.Model2', jspsrc: ''});
treeMenuData['UIDataBinding']           = new DataItem({url: 'binding.stock', mbsrc: 'demo.binding.StockModel:demo.binding.Quote', jspsrc: ''});
treeMenuData['comboBinding']            = new DataItem({url: 'binding.selectItems', mbsrc: 'demo.binding.SelectItemsBean', jspsrc: ''});
treeMenuData['stateless']               = new DataItem({url: 'ejb.hello', mbsrc: 'demo.HelloEJB:demo.Hello:demo.DemoBean', jspsrc: ''});
treeMenuData['stateful']                = new DataItem({url: 'ejb.calc', mbsrc: 'demo.CalcEJB:demo.Calc', jspsrc: ''});
treeMenuData['viewdata']                = new DataItem({url: 'ejb.statelesscalc', mbsrc: 'demo.StatelessCalcEJB:demo.StatelessCalc', jspsrc: ''});
treeMenuData['simpleDataGrid']          = new DataItem({url: 'grid.simpleDataGrid', mbsrc: 'demo.grid.SimpleDataGridBean', jspsrc: ''});
treeMenuData['stockQuote']              = new DataItem({url: 'grid.stock-quote', mbsrc: 'demo.StockBean', jspsrc: ''});
treeMenuData['mutiSelectionGrid']       = new DataItem({url: 'grid.multiSelectionGrid', mbsrc: 'demo.MultiSelectionGridBean', jspsrc: ''});
treeMenuData['editGrid']                = new DataItem({url: 'grid.edit-grid', mbsrc: 'demo.grid.EditGridBean:demo.grid.GridDataModel', jspsrc: ''});
treeMenuData['pagedStockQuote']         = new DataItem({url: 'grid.paged-stock-quote', mbsrc: 'demo.PagedStockBean', jspsrc: ''});
treeMenuData['staticTree']              = new DataItem({url: 'tree.tree-a', mbsrc: 'demo.StaticNodesTreeBean', jspsrc: ''});
treeMenuData['dynamicTree']             = new DataItem({url: 'tree.tree-b', mbsrc: 'demo.DynamicNodesTreeBean:demo.WindowsExplorer:demo.File', jspsrc: ''});
treeMenuData['checkTree']               = new DataItem({url: 'tree.tree-c', mbsrc: 'demo.CheckNodesTreeBean', jspsrc: ''});
treeMenuData['simpleCheckTree']         = new DataItem({url: 'tree.tree-g', mbsrc: 'demo.SimpleCheckNodesTreeBean', jspsrc: ''});
treeMenuData['treeServerApi1-nonajax']  = new DataItem({url: 'tree.tree-d', mbsrc: 'demo.ServerSideTreeBean:demo.WindowsExplorer:demo.File', jspsrc: ''});
treeMenuData['treeServerApi1-ajax']     = new DataItem({url: 'tree.tree-e', mbsrc: 'demo.ServerSideTreeBean:demo.WindowsExplorer:demo.File', jspsrc: ''});
treeMenuData['treeServerApi2']          = new DataItem({url: 'tree.tree-f', mbsrc: 'demo.ServerSideTreeBean2', jspsrc: ''});
treeMenuData['IoVCTree']                = new DataItem({url: 'binding.checkNodesTree2', mbsrc: 'demo.binding.CheckNodesTree2Bean', jspsrc: ''});
treeMenuData['dataView']                = new DataItem({url: 'view.test', mbsrc: '', jspsrc: ''});
treeMenuData['foreach']                 = new DataItem({url: 'view.foreach', mbsrc: '', jspsrc: ''});
treeMenuData['dynamicMenu']             = new DataItem({url: 'view.dynamicMenu', mbsrc: 'demo.DynamicMenuBean', jspsrc: ''});
treeMenuData['graph']                   = new DataItem({url: 'graph.main', mbsrc: '', jspsrc: ''});
treeMenuData['menu']                    = new DataItem({url: 'menu.menu', mbsrc: 'demo.MenuBean', jspsrc: ''});
treeMenuData['toolbar']                 = new DataItem({url: 'menu.toolbar', mbsrc: 'demo.ToolBarBean', jspsrc: ''});
treeMenuData['dynamicToolbar']                 = new DataItem({url: 'menu.dynamicToolbar', mbsrc: 'demo.DynamicToolBarBean', jspsrc: ''});
treeMenuData['paging']                  = new DataItem({url: 'view.paging', mbsrc: '', jspsrc: ''});
treeMenuData['simpleDialog']            = new DataItem({url: 'dialog.dialog', mbsrc: 'demo.DialogBean', jspsrc: ''});
treeMenuData['gridDialog']              = new DataItem({url: 'dialog.grid', mbsrc: 'demo.DialogEditBean', jspsrc: 'dialog.grid:dialog.edit'});
treeMenuData['fileUpload']              = new DataItem({url: 'fileupload.fileuploadprogress', mbsrc: 'demo.FileUploadProgressBean', jspsrc: ''});
treeMenuData['boxes']                   = new DataItem({url: 'box.boxes', mbsrc: '', jspsrc: ''});
