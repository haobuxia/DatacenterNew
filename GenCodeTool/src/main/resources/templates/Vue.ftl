<template>
    <div class="resource">
        <!-- 搜索 -->
        <el-form inline :model="viewSearch.data" label-position="right" label-width="80px">
            <el-form-item label="搜索对象">
                <el-input v-model="viewSearch.data.${firstEnityName?uncap_first}" placeholder="输入名称/说明关键词"></el-input>
            </el-form-item>
            <el-form-item>
                <el-button type="primary" @click="viewSearch.show=true">更多搜索</el-button>
                <el-button type="primary" @click="handleSearch()">搜索</el-button>
                <el-button type="danger" @click="handleAdd()">创建</el-button>
            </el-form-item>
        </el-form>
        <!-- 表格 -->
        <div class="viewTableClass">
            <el-table :data="viewTable.data" border :header-cell-style="{background:'#f9fafc'}">
            <#if model_column?exists>
                <#list model_column as model>
                    <el-table-column prop="${model.changeColumnName?uncap_first}" <#if model.columnType = 'DATE'>:formatter="dateFormat" </#if>  label="${model.columnComment}" width="150">${model.columnComment}</el-table-column>
                </#list>
            </#if>
                <el-table-column label="操作" width="300">
                    <template slot-scope="scope">
                        <el-button type="primary" size="small" @click="handleEdit(scope.row,scope.$index)">修改</el-button>
                        <el-button type="danger" size="small" @click="handleDelet(scope.row,scope.$index)">删除</el-button>
                    </template>
                </el-table-column>
            </el-table>

        </div>
        <!-- 分页 -->
        <el-pagination class="pageArea" @size-change="handleSizeChange" @current-change="handleCurrentChange"
                       :current-page="viewTable.pageInfo.page" :page-sizes="[100, 200, 300, 400]"
                       :page-size="viewTable.pageInfo.pageSize" layout="total, sizes, prev, pager, next, jumper"
                       :total="400">
        </el-pagination>
        <!-- 对话框区 开始 -->
        <!-- 更多搜索 -->
        <el-dialog title="搜索数据对象" :visible.sync="viewSearch.show" width="30%" id="viewSearch">
            <el-form :model="viewSearch.data" label-position="right" label-width="120px">
            <#if model_column?exists>
                <#assign i = 0>
                <#list model_column as model>
                    <#if model.columnType = 'VARCHAR' || model.columnType = 'TEXT'|| model.columnType = 'text'|| model.columnType = 'varchar' || model.columnType = 'INTEGER' || model.columnType = 'INT' || model.columnType = 'tinyint' || model.columnType = 'TINYINT'|| model.columnType = 'FLOAT'||model.columnType = 'float'>
                        <el-form-item label="${model.columnComment}" prop="${model.changeColumnName?uncap_first}">
                            <el-input v-model="viewSearch.data.${model.changeColumnName?uncap_first}" placeholder="请输入${model.columnComment}"></el-input>
                        </el-form-item>
                    <#elseif model.columnType ='DATETIME'||model.columnType = 'TIMESTAMP'|| model.columnType = 'datatime'|| model.columnType = 'timestamp'|| model.columnType = 'DATE'|| model.columnType = 'date'>
                        <el-form-item label="${model.columnComment}" prop="${model.changeColumnName?uncap_first}">
                            <el-date-picker v-model="viewSearch.data.${model.changeColumnName?uncap_first}" align="right" type="date" placeholder="选择日期">
                            </el-date-picker>
                        </el-form-item>
                    </#if>
                </#list>
            </#if>
            </el-form>
            <div slot="footer" class="dialog-footer">
                <el-button type="danger" @click="viewSearch.show = false">取消</el-button>
                <el-button type="primary" @click="handleSearch()">搜索</el-button>
            </div>
        </el-dialog>
        <!-- 新增 -->
        <el-dialog title="创建数据对象" :visible.sync="viewAdd.show" width="30%" id="viewAdd">
            <el-form :model="viewAdd.data" label-position="right" label-width="120px">
            <#if model_column?exists>
                <#assign i = 0>
                <#list model_column as model>
                    <#if model.columnType = 'VARCHAR' || model.columnType = 'TEXT'|| model.columnType = 'text'|| model.columnType = 'varchar' || model.columnType = 'INTEGER' || model.columnType = 'INT' || model.columnType = 'tinyint' || model.columnType = 'TINYINT'|| model.columnType = 'FLOAT'||model.columnType = 'float'>
                        <el-form-item label="${model.columnComment}" prop="${model.changeColumnName?uncap_first}">
                            <el-input v-model="viewAdd.data.${model.changeColumnName?uncap_first}" placeholder="请输入${model.columnComment}"></el-input>
                        </el-form-item>
                    <#elseif model.columnType ='DATETIME'||model.columnType = 'TIMESTAMP'|| model.columnType = 'datatime'|| model.columnType = 'timestamp'|| model.columnType = 'DATE'|| model.columnType = 'date'>
                        <el-form-item label="${model.columnComment}" prop="${model.changeColumnName?uncap_first}">
                            <el-date-picker v-model="viewAdd.data.${model.changeColumnName?uncap_first}" align="right" type="date" placeholder="选择日期"></el-date-picker>
                        </el-form-item>
                    </#if>
                </#list>
            </#if>
            </el-form>
            <div slot="footer" class="dialog-footer">
                <el-button type="danger" @click="viewAdd.show = false">取消</el-button>
                <el-button type="primary" @click="addSingle()">确定</el-button>
            </div>
        </el-dialog>
        <!-- 修改 -->
        <el-dialog title="修改数据集属性" :visible.sync="viewEdit.show" width="30%" id="viewEdit">
            <el-form :model="viewEdit.data" label-position="right" label-width="120px">
            <#if model_column?exists>
                <#assign i = 0>
                <#list model_column as model>
                    <#if model.columnType = 'VARCHAR' || model.columnType = 'TEXT'|| model.columnType = 'text'|| model.columnType = 'varchar' || model.columnType = 'INTEGER' || model.columnType = 'INT' || model.columnType = 'tinyint' || model.columnType = 'TINYINT'|| model.columnType = 'FLOAT'||model.columnType = 'float'>
                        <el-form-item label="${model.columnComment}" prop="${model.changeColumnName?uncap_first}">
                            <el-input v-model="viewEdit.data.${model.changeColumnName?uncap_first}" placeholder="请输入${model.columnComment}"></el-input>
                        </el-form-item>
                    <#elseif model.columnType ='DATETIME'||model.columnType = 'TIMESTAMP'|| model.columnType = 'datatime'|| model.columnType = 'timestamp'|| model.columnType = 'DATE'|| model.columnType = 'date'>
                        <el-form-item label="${model.columnComment}" prop="${model.changeColumnName?uncap_first}">
                            <el-date-picker v-model="viewEdit.data.${model.changeColumnName?uncap_first}" align="right" type="date" placeholder="选择日期"></el-date-picker>
                        </el-form-item>
                    </#if>
                </#list>
            </#if>
            </el-form>
            <div slot="footer" class="dialog-footer">
                <el-button type="danger" @click="viewEdit.show = false">取消</el-button>
                <el-button type="primary" @click="editSingle()">确定</el-button>
            </div>
        </el-dialog>
        <!-- 删除 -->
        <el-dialog title="删除" :visible.sync="viewDelet.show" width="30%" id="viewDelet">
            <div class="modal-body">
                您确定删除要删除该项吗?
            </div>
            <div slot="footer" class="dialog-footer">
                <el-button type="danger" @click="viewDelet.show = false">取消</el-button>
                <el-button type="primary" @click="deletSingle()">确定</el-button>
            </div>
        </el-dialog>
        <!-- 对话框 结束 -->

    </div>
</template>

<script>
    import axios from "@/libs/axios";
    import api from "@/api/data-model/data";
    import {Message} from 'element-ui';
    import { formatDate } from '../../../utils/dateUtil.js'

    export default {
        name: "${table_name}",
        data() {
            return {
                //服务器地址
                base_url: "http://127.0.0.1:19062",
                //页面表格
                viewTable: {
                    //dom元素id
                    domId: "",
                    //样式表类名
                    className: "",
                    pageInfo: {
                        //当前页码
                        page: 1,
                        //数据总数量
                        count: 0,
                        //每页包含数据量
                        pageSize: 50
                    },
                    //数据
                    data: []
                },
                //接口信息 *need
                //数据搜索功能
                viewSearch: {
                    data: {
                        <#if model_column?exists>
                            <#list model_column as model>
                            ${model.columnName}:""<#if model_has_next>,</#if>
                            </#list>
                        </#if>
              },
            show: false
        },
            //数据新增功能
            viewAdd: {
                data: {},
                show: false
            },
            //数据修改功能
            viewEdit: {
                index: "",
                data:{},
                show:false
            },
            //数据删除功能
            viewDelet: {
                show: false,
                item:{},
                index: "",
                data:{}
            }
        }
    },
    created()
    {
        this.getTableData(this.viewSearch.data);
    },
    methods: {
            dateFormat: function(row, column){
                var date = row[column.property];
                if (date === undefined) {
                    return "";
                }
                var date = new Date(date);
                return formatDate(date, 'yyyy-MM-dd');
            },
        //表格操作
        /**
         * @function () handleEdit(item)
         * @description 表格 修改弹窗
         * @param {Object} item 表格一行数据
         * @param {String} index 数据第几项
         */
        handleEdit(item, index)
        {
            console.log("edit", item, index);
            this.viewEdit.data = Object.assign({}, item);
            this.viewEdit.old = Object.assign({}, item);
            this.viewEdit.index = index;
            this.viewEdit.show = true;
        }  ,
        /**
         * @function () editSingle()
         * @description 修改单项
         */
        editSingle()
        {
            axios.post(this.base_url+'/${table_name?uncap_first}/update', this.viewEdit.data)
                    .then(res => {
                if(res.data.success)
            {
                Message.success("修改成功");
                this.$set(this.viewTable.data, this.viewEdit.index, this.viewEdit.data);
            }
        else
            {
                Message.error('修改失败。')
            }
        }).
            catch(err => {Message.error('网络错误，修改失败。')
        })
            this.viewEdit.show = false;
        }  ,
        /**
         * @function () handleDelet(item)
         * @description 表格 修改数据
         * @param {Object} item 表格一行数据
         */
        handleDelet(item, index)
        {
            console.log("delet", item);
            this.viewDelet.item = item;
            this.viewDelet.data = Object.assign({}, item);
            this.viewDelet.index = index;
            this.viewDelet.show = true;
        },
        /**
         * @function () deleteSingle()
         * @description 删除单项
         */
        deletSingle()
        {
            axios.post(this.base_url+'/${table_name?uncap_first}/delete', this.viewDelet.data)
                    .then(res => {
                if(res.data.success){
                Message.success("删除成功");
                this.getTableData(this.viewSearch.data)
                }
                else{
                    Message.error('删除失败')
                }
        })
    .catch(err => {Message.error('网络错误，删除失败')})
    this.viewDelet.show = false;
    },

    /**
     * @function () handleDelet(item)
     * @description 表格 修改数据
     * @param {Object} item 表格一行数据
     */
    handleAdd(item)
    {
        console.log("添加新成员");
        this.viewAdd.show = true;
    } ,
    /**
     * @function () addeSingle()
     * @description 添加单项
     */
    addSingle()
    {
        var params = Object.assign({}, this.viewAdd.data);
        axios.post(this.base_url+'/${table_name?uncap_first}/add', params)
                .then(res => {
            if(res.data.success)
        {
            Message.success("添加成功");
            this.viewTable.data.push(params);
        }
    else
        {
            Message.error('添加失败。')
        }
    }).
        catch(err => {Message.error('添加失败，网络错误。')
    })
        for (var i in this.viewAdd.data) {
            this.viewAdd.data[i] = "";
        }
        this.viewAdd.show = false;
    },
    // 搜索 得到表格数据
    handleSearch()
    {
        this.getTableData(this.viewSearch.data);
    },
    getTableData(params)
    {
        axios.post(this.base_url+'/${table_name?uncap_first}/list', params).then(res => {
            if(res.data.success)
        {
            this.viewTable.data = res.data.data
        }
    else
        {
            Message.error('查询表格数据错误' + res.message)
        }
    }).
        catch(err => {Message.error('查询表格数据出错，网络错误。')
    }) ;
    },
    //分页
    handleSizeChange(val)
    {
        this.viewTable.pageInfo.page = 1;
        this.viewTable.pageInfo.pageSize = val;
        handleSearch();
    },
    handleCurrentChange(val)
    {
        handleSearch();
    }
    }
    }
</script>
