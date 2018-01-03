layui.define(['jquery','element','form','layer','table','laydate'],function(exports){
	"use strict";
	var $ = layui.jquery,
	form = layui.form,
	layer = layui.layer,
	table = layui.table,
	laydate = layui.laydate,
	page = layui.page,
	Duang = function(){
		//记录当前页面代码渲染的数据表格对象
		//不支持html数据表格管理
		this.tables = {}
	};
	
	/**
	 * 初始化csrf token 参数 暂时是通过页面获取，以后可以考虑使用cookie
	 */
    Duang.prototype.getCsrfToken = function (){
 	   var data = {};
        var value = $('meta[name="_csrf"]').attr("content");
        var name = $('meta[name="_csrf_header"]').attr("content");
        data[name] = value;
        return data;
    }
    
    /**
	 * replace string: aaa{b} by {b:'--c'} to string: aaa--c
	 */
    Duang.prototype.stringReplaceByObject = function (string,obj) {
    	for(var p in obj) {
    		var type = typeof(obj[p]);
    		if((type==='string' || type==='number')) {
    			string = string.replace('{'+p+'}',obj[p]);
    		}
    	}
    	return string;
    };
    
    /**
     * 组合options
     */
    Duang.prototype.appendOptions = function (data,selectValue,value,text){
    	var options = '';
    	for(var item of data ) {
    		if(selectValue == item[value] ) {
        		options += '<option selected value="'+item[value]+'">'+item[text]+'</option>'
    		} else {
        		options += '<option value="'+item[value]+'">'+item[text]+'</option>'
    		}
    	}
    	return options;
    };
    
    /**
     * 自动渲染options
     * 属性参数：
     * data-option-value:选项值得字段
     * date-option-text:选项文本显示的字段
     * date-select-value: 默认选择的值
     * date-action: 选项数据异步请求地址
     * data-form-filter: 选项所在的表单filter
     * class: duang-select
     */
    Duang.prototype.autoRenderSelect = function(){
    	var _thisDuang = this;
    	$('.duang-select').each(function(){
    		var _thisSelect = $(this);
    		var initOptions = _thisSelect.html();
    		var optionValue = _thisSelect.data('option-value')||'id';
    		var optionText = _thisSelect.data('option-text')||'name';
    		var selectValue = _thisSelect.data('select-value');
    		var _action = _thisSelect.data('action');
    		if(_action){
    			$.get(_action,function(response){
    				if(response.code == '0') {
    					_thisSelect.html(initOptions + _thisDuang.appendOptions(response.data,selectValue,optionValue,optionText));
    					var filter = _thisSelect.data("form-filter");
    					if(filter) {
    						form.render('select',filter)
    					} else {
    						form.render('select');
    					}
    				}
    			});
    		}
    	});
    };
    
    /**
     * 两层树
     */
    Duang.prototype.treeRowFormat = function(data){
    	var parents = [];
    	for(var node of data ) {
    		if(node.pid == '0') {
    			node['child'] = [];
    			node['isChild'] = false;
    			parents[node.id] = node;
    		}
    	}
    	for(var node of data ) {
    		if(node.pid != '0') {
    			node.isChild = true;
    			parents[node.pid]['child'][node.id] = node;
    		}
    	}
    	var nodes = [];
    	for(var prop in parents ) {
    		var parent = parents[prop];
    		var child = parent['child'];
    		parent['child'] = null;
    		nodes.push(parent);
    		for(var p in child) {
    			var item = child[p];
    			nodes.push(item);
    		}
    	}
    	return nodes;
    };
    
	/**
	 * 动态渲染一个数据表格
	 * 替代 layui.table.render();
	 * 目标是把每次render的数据表格对象保存，方便下次使用
	 */
	Duang.prototype.table = function(options,beforeRenderCallback){
		var _thisDuang = this;
		if(options && options.id) {
			if(beforeRenderCallback && typeof(beforeRenderCallback) == 'function') {
				$.get(options.url,function(response){
					if(response.code == '0') {
						options.data = beforeRenderCallback.call(_thisDuang,response.data);
						options.url = null;
						_thisDuang.tables[options.id] = table.render(options);
					}
				})
			} else {
				_thisDuang.tables[options.id] = table.render(options);
			}
		}
	};
	
	/**
	 * 获取渲染的数据表格实例
	 */
	Duang.prototype.getTable = function(tableFilter){
		if(this.tables[tableFilter]) return this.tables[tableFilter];
		return null;
	}
  
	/**
	 * 数据表格搜索
	 * 表单必须是用form元素包围
	 * 属性参数：
	 * data-table-filter: 数据表格的lay-filter
	 * class: duang-search-btn 按钮的样式
	 */
	Duang.prototype.autoEnableSearchForm = function(){

    	var _thisDuang = this;
		//监听数据表格的搜索按钮
		$(document).on('click','.duang-search-btn',function(event){
			event.preventDefault();
			var _thisButton = $(this);
			var form = _thisButton.parents('form');
			var tableFilter = _thisButton.data('table-filter');
			var _thisTable = _thisDuang.getTable(tableFilter);
			//序列号表单
			var data = $(form).serializeArray();
			var where = {};
			//组装数据
			for(var item of data) {
				if(item.value !== '')
					where[item.name] = item.value;
			}
			_thisTable.reload({
				where: where
			});
		});
		
		
	}
	
	/**
	 * 统一打开iframe窗口的方法
	 * 属性参数：
	 * data-title: 窗口的标题
	 * date-width: 窗口的宽度（不带要单位）
	 * data-height: 窗口的高度（不带要单位）
	 * class: duang-open-window
	 */
	Duang.prototype.openWindow = function (_button,url){
		var title = _button.data("title")||'窗口';
		var width = _button.data("width")||500;
		var height = _button.data("height")||300;
		layer.open({
			area:[width+'px',height+'px'],
			title: title,
			fix: false, // 不固定
			maxmin: true,
			shadeClose: true,
			shade:0.4,
			type:2, // 0（信息框，默认）1（页面层）2（iframe层）3（加载层）4（tips层）
			content:url
		});
    };
    
    /**
     * 自动渲染日期选择控件
     * 需要属性参数：
     * data-type: year,month,date,datetime
     * class: duang-date
     */
    Duang.prototype.autoRenderDate = function(){
    	$('.duang-date').each(function(){
    		var _thisDate = $(this);
    		var type = _thisDate.data('type') || 'date';
    		laydate.render({
    			elem:_thisDate[0],
    			type:type
    		});
    	});
    };
    
    /**
	 * 自动开启批量删除的功能 属性参数： data-action: 删除请求的地址 data-table-filter:
	 * 影响的数据表格lay-filter class: duang-batch-delete-btn
	 */
    Duang.prototype.autoEnableBatchDelete = function(){
    	var _thisDuang = this;
    	// 监听批量删除按钮
    	$(document).on('click','.duang-batch-delete-btn',function(event){
    		event.preventDefault();
    		var _this  = $(this);
    		var _action = _this.data("action");
    		if(_action) {
    			var filter = _this.data("table-filter");
    			var checkStatus = table.checkStatus(filter);
    			var data = _thisDuang.getCsrfToken();
    	        data.ids = [];
    	        for(var item of checkStatus.data) {
    	        	data.ids.push(item.id);
    	        }
    			layer.confirm('确认要删除吗？', function(index) {
    			   $.post(_action,data,function(response){
    			       if(response.code == '0' ) {
    			    	 // 捉到所有被选中的，发异步进行删除
    			           layer.msg(response.msg, {
    			               icon : 1
    			           },function(){
    			        	   //删除成功数据表格中的数据条目的dom
    			        	   _thisDuang.removeTableSelectedRow(filter);
    			           });
    			           
    			       } else {
    			    	 // 捉到所有被选中的，发异步进行删除
    			           layer.msg(response.msg, {
    			               icon : 1
    			           });
    			       }
    			       
    			   });
    			});
    		}
    	});
    };
    
    /**
     * 自动开启刷新按钮
     * 属性参数：
     * data-table-filter: 影响的数据表格lay-filter
     * data-refresh: 可选参数，(true|false) 如果设置为true 重新定位当前页面刷新整个页面，反之，局部刷新数据表格
     * class: duang-refresh-btn
     */
    Duang.prototype.autoEnbleRefresh = function(){
    	var _thisDuang = this;
    	//监听数据表格刷新按钮
    	$(document).on('click','.duang-refresh-btn',function(event){
    		event.preventDefault();
    		var _thisButton = $(this);
    		var _tableFilter = _thisButton.data('table-filter');
    		if(_tableFilter) {
    			var _table = _thisDuang.getTable(_tableFilter);
    			_table.reload();
    		} else {
        		var _refresh = _thisButton.data('refresh');
        		if(_refresh) {
        			location.replace(location.href)
        		}
    		}
    	});
    };
    
    /**
     * 删除数据表格选中的行dom
     * @param filter 数据表格的lay-filter
     */
    Duang.prototype.removeTableSelectedRow = function (filter){
    	
    	var _thisTable = this.getTable(filter);
    	var layFilterIndex = 'LAY-table-'+_thisTable.config.index;
    	//找到table filter的索引
    	var tableContainer = $('div[lay-filter="'+layFilterIndex+'"]')
    	//查找选中的checkbox
    	tableContainer.find('input[name="layTableCheckbox"]:checked').each(function(){
    		//删除tr
    		 $(this).parents('tr').remove();
    	});
    	
    };
    
	/**
	 * 统一打开iframe窗口的方法
	 * 属性参数：
	 * data-title: 窗口的标题
	 * date-width: 窗口的宽度（不带要单位）
	 * data-height: 窗口的高度（不带要单位）
	 * class: duang-open-window
	 */
    Duang.prototype.autoEnableOpenWindow = function(){
    	var _thisDuang = this;
    	// 打开对话框
    	$(document).on('click','.duang-open-window',function(event){
    		event.preventDefault();
    		var _this = $(this);
    		var _action = _this.data("action");
    		if(_action) {
    			_thisDuang.openWindow(_this,_action);
    		}
    	});
    };
    
    Duang.prototype.auto = function(){
    	
    	var _thisDuang = this;
    	
    	//自动渲染select
    	this.autoRenderSelect();
    	this.autoRenderDate();
    	this.autoEnableSearchForm();
    	this.autoEnableBatchDelete();
    	this.autoEnbleRefresh();
    	this.autoEnableOpenWindow();
    	
    	// 监听数据表格的工具条（每条数据的编辑删除修改等操作）
    	table.on('tool', function(obj) {
    		var _button = $(this);
    		var _action = _button.data("action");
    		var data = obj.data;
    		if(_action) {
    			if(obj.event === 'edit') {
    				_thisDuang.openWindow(_button,_thisDuang.stringReplaceByObject(_action,obj.data));
    			} else if (obj.event === 'delete') {
    				layer.confirm('真的删除行么', function(index) {
    					var data = _thisDuang.getCsrfToken();
    					data.id = obj.data.id;
    					$.post(_action,data,function(response){
    						layer.alert(response.msg);
    						if(response.code == '0') {
    							obj.del();
    							layer.close(index);
    						}
    					});
    				});
    			}
    				
    		}
    		
    	});
    	
    	// 监听表单提交
    	form.on('submit(submit)', function(data){
    		$.post(data.action,data.field,function(response){
    			// success is 0
    			if (response.code == '0') {
    				layer.alert(response.msg, {icon: 6},function () {
    					// 获得frame索引
    					var index = parent.layer.getFrameIndex(window.name);
    					// 关闭当前frame
    					if(index != undefined) {
    						parent.layer.close(index);
    						
    					} else {
    						layer.close(layer.index);
    					}
    				}); 
    			} else {
    				layer.alert(response.msg, {icon: 6});
    			}
    		});
    		return false;
    	});
    	
  
    };
    
    var duang = new Duang();
    duang.auto();
    exports('duang',duang);
	
});