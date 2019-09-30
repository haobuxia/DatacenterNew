<template>
    <div class="template-ipage">
        <el-form
                ref="searchForm"
                :inline="true"
                label-width="80px"
                label-position="right"
                onsubmit="return false"
                class="group-btns"
        >
            <el-form-item>
                <el-button
                        type="primary"
                        size="small"
                        @click="handleSearch"
                >查询</el-button>
            </el-form-item>
            <el-form-item>
                <el-button
                        type="primary"
                        size="small"
                        @click="showAddModal"
                >新增</el-button>
            </el-form-item>
        </el-form>
        <el-table
                :data="tableData"
                border
                stripe
                class="tableData"
                height="calc(100vh - 200px)"
        >
            <el-table-column
                    v-for="col in columns"
                    :key="col.id"
                    :prop="col.prop"
                    :label="col.label"
                    :width="col.width"
            >
            </el-table-column>
            <el-table-column
                    label="操作"
                    width="150"
            >
                <template slot-scope="scope">
                    <el-button
                            type="primary"
                            size="mini"
                            @click="handleEdit(scope.row)"
                    >修改</el-button>
                    <el-button
                            type="danger"
                            size="mini"
                            @click="handleDelete(scope.row)"
                    >删除</el-button>
                </template>
            </el-table-column>
        </el-table>
        <!-- 分页器 -->
        <div style="margin-top:15px;">
            <el-pagination
                    align="center"
                    :current-page="pageInfo.page"
                    :page-sizes="[20,50,100,200,500]"
                    :page-size="pageInfo.pageSize"
                    layout="total, sizes, prev, pager, next, jumper"
                    :total="pageInfo.total"
                    @size-change="handleSizeChange"
                    @current-change="handleCurrentChange"
            >
            </el-pagination>
        </div>

        <!-- 弹窗 -->
        <el-dialog
                :title="formShowtype==='add'?'添加':(formShowtype==='edit'?'修改':'查询')"
                :visible.sync="addFormShow"
        >
            <el-form
                    ref="addForm"
                    :model="addForm"
                    :rules="formShowtype!=='search'?rules:{}"
                    label-width="100px"
                    size="mini"
            >
                <!--替换  elements start -->
                <#if model_column?exists>
                    <#assign a = 0>
                    <#list model_column as model>
                        <#if model.type != '字典' && (model.columnType = 'VARCHAR' || model.columnType = 'TEXT'|| model.columnType = 'text'|| model.columnType = 'varchar' || model.columnType = 'int' || model.columnType = 'INT' || model.columnType = 'tinyint' || model.columnType = 'TINYINT'|| model.columnType = 'FLOAT'||model.columnType = 'float')>
                            <el-form-item
                                    label="${model.columnComment}"
                                    prop="${model.changeColumnName}"
                            >
                                <el-input v-model="addForm.${model.changeColumnName}"></el-input>
                            </el-form-item>
                        <#elseif model.columnType ='DATETIME'||model.columnType = 'TIMESTAMP'|| model.columnType = 'datatime'|| model.columnType = 'timestamp'|| model.columnType = 'DATE'|| model.columnType = 'date'>
                            <el-form-item label="${model.columnComment}" prop="${model.changeColumnName}">
                                <el-date-picker v-model="addForm.${model.changeColumnName}" align="right" type="date" placeholder="选择日期"></el-date-picker>
                            </el-form-item>
                        <#elseif model.type = '字典'>
                        </#if>
                    </#list>
                </#if>
                <!--替换  elements end -->
                <el-form-item>
                    <el-button
                            v-show="formShowtype === 'add'"
                            type="primary"
                            @click="() => addSubmit()"
                    >添加</el-button>
                    <el-button
                            v-show="formShowtype === 'edit'"
                            type="primary"
                            @click="() => editSubmit()"
                    >修改</el-button>
                    <el-button
                            v-show="formShowtype === 'search'"
                            type="primary"
                            @click="() => searchSubmit()"
                    >查询</el-button>
                    <el-button @click="addFormShow = false">取消</el-button>
                </el-form-item>
            </el-form>

        </el-dialog>
    </div>
</template>
<script>
    import { mapGetters } from 'vuex'
    import { objDeepCopy } from '@/utils/utils.js'
    <!--替换  单表Id start -->
    const dataObjectId = ${model_column[0].resId}
    <!--替换  单表Id end -->
    export default {
        name: 'RoleAuth',
        data() {
            return {
                formShowtype: 'add',
                addFormShow: false,
                <!--替换  单表属性列表 form start -->
                addForm: {
                    <#if model_column?exists>
                        <#list model_column as model>
                        ${model.changeColumnName}:""<#if model_has_next>,</#if>
                        </#list>
                    </#if>
                },
                <!--替换  单表属性列表 form end -->
                // 提交用 搜索条件
                condition: [],
                // 表格单页数据
                tableData: [],
                <!--替换  单表属性列表 column start -->
                columns: [
                    <#if model_column?exists>
                        <#list model_column as model>
                            { id: ${model_index}, label: '${model.columnComment}', prop: '${model.changeColumnName}', width: ''}<#if model_has_next>,</#if>
                        </#list>
                    </#if>
                ],
                <!--替换  单表属性列表 column end -->
                // 表单验证
                valid: false,
                <!--替换  表单验证 rules start -->
                rules: {
                    <#if model_column?exists>
                        <#list model_column as model>
                            <#if model.isNull=="false">
                            ${model.changeColumnName}: [{ required: true, message: '必填', trigger: 'blur' }]<#if model_has_next>,</#if>
                            </#if>
                        </#list>
                    </#if>
                },
                <!--替换  表单验证 rules start -->
                // 分页
                pageInfo: {
                    pageSize: 0,
                    page: 0,
                    total: 0
                },
                notice: {}
            }
        },
        computed: {
                ...mapGetters(['token'])
    },
    created() {
        this.getTableData(this.condition, this.pageInfo)
    },
    methods: {
        // 重置验证
        resetForm() {
            if (this.$refs['addForm']) {
                this.$refs['addForm'].clearValidate()
            }
        },
        // 搜索
        searchSubmit() {
            this.condition = []
            Object.keys(this.addForm).forEach(item => {
                if (this.addForm[item]) {
                this.condition.push({
                    key: item,
                    condition: 'like',
                    value: this.addForm[item]
                })
            }
        })
            this.pageInfo.page = 1
            this.getTableData(this.condition, this.pageInfo)
        },
        // 添加
        addSubmit() {
            this.$refs['addForm'].validate(valid => {
                if (valid) {
                    this.notice = this.$notify({
                        type: 'warning',
                        message: '添加中...',
                        showClose: true,
                        duration: 0,
                        offset: 30
                    })
                    this.$store
                            .dispatch('api/add', {
                                dataObjectId: dataObjectId,
                                data: objDeepCopy(this.addForm)
                            })
                            .then(res => {
                        this.notice.close()
                    if (res.success) {
                        this.$message({
                            type: 'success',
                            message: '添加成功',
                            duration: 1000,
                            offset: 30
                        })
                        this.getTableData(this.condition, this.pageInfo)
                    } else {
                        this.$notify({
                            type: 'danger',
                            message: '添加失败',
                            duration: 1000,
                            offset: 30
                        })
                    }
                })
                .catch(() => {
                        this.notice.close()
                })
                    this.addFormShow = false
                    for (const index in this.addForm) {
                        this.addForm[index] = ''
                    }
                } else {
                    this.$message('表单验证未通过')
                return false
            }
        })
        },
        // 修改
        editSubmit() {
            this.$refs['addForm'].validate(valid => {
                if (valid) {
                    this.notice = this.$notify({
                        type: 'warning',
                        message: '修改中...',
                        showClose: true,
                        duration: 0,
                        offset: 30
                    })

                    const o = objDeepCopy(this.addForm)
                    this.$store
                            .dispatch('api/update', {
                                dataObjectId: dataObjectId,
                                condition: [
                                    {
                                        condition: 'equals',
                                        key: 'rid',
                                        value: o.rid
                                    }
                                ],
                                data: o
                            })
                            .then(res => {
                        this.notice.close()
                    if (res.success) {
                        this.$message({
                            type: 'success',
                            message: '修改成功',
                            duration: 1000,
                            offset: 30
                        })
                        this.getTableData(this.condition, this.pageInfo)
                        this.addFormShow = false
                    } else {
                        this.$notify({
                            type: 'danger',
                            message: '修改失败',
                            duration: 1000,
                            offset: 30
                        })
                    }
                })
                .catch(() => {
                        this.notice.close()
                })
                } else {
                    this.$message('表单验证未通过')
                return false
            }
        })
        },
        // 删除
        deleteSubmit(row) {
            this.notice = this.$notify({
                type: 'warning',
                message: '删除中...',
                showClose: true,
                duration: 0,
                offset: 30
            })

            this.$store
                    .dispatch('api/delete', {
                        dataObjectId: dataObjectId,
                        condition: [
                            {
                                condition: 'equals',
                                key: 'rid',
                                value: row.rid
                            }
                        ]
                    })
                    .then(res => {
                this.notice.close()
            if (res.success) {
                this.$message({
                    type: 'success',
                    message: '删除成功',
                    duration: 1000,
                    offset: 30
                })
                this.getTableData(this.condition, this.pageInfo)
            } else {
                this.$notify({
                    type: 'danger',
                    message: '删除失败',
                    duration: 1000,
                    offset: 30
                })
            }
        })
        .catch(() => {
                this.notice.close()
        })
        },
        // 添加窗口显示
        showAddModal() {
            this.resetForm()
            if (this.formShowtype !== 'add') {
                this.formShowtype = 'add'
                this.$nextTick(() => {
                    for (const index in this.addForm) {
                    this.addForm[index] = ''
                }
            })
            }
            this.addFormShow = true
        },
        // 搜索 显示
        handleSearch() {
            this.resetForm()
            if (this.formShowtype !== 'search') {
                this.formShowtype = 'search'
                this.$nextTick(() => {
                    for (const index in this.addForm) {
                    this.addForm[index] = ''
                }
            })
            }
            this.addFormShow = true
        },
        //  表格 操作 点击修改
        handleEdit(row) {
            this.resetForm()
            const o = objDeepCopy(row)
            this.formShowtype = 'edit'
            for (const index in this.addForm) {
                this.addForm[index] = o[index]
            }
            this.$nextTick(() => {
                this.addFormShow = true
        })
        },
        //  表格 操作 点击删除
        handleDelete(row) {
            this.$confirm('确定要删除吗?', '确认信息', {
                distinguishCancelAndClose: true,
                confirmButtonText: '确定',
                cancelButtonText: '取消',
                type: 'warning'
            }).then(() => {
                this.deleteSubmit(row)
        })
        },
        // 获取表格数据
        getTableData(condition, pageInfo) {
            this.$store
                    .dispatch('api/view', {
                        dataObjectId: dataObjectId,
                        condition,
                        pageInfo
                    })
                    .then(response => {
                if (typeof response.data === 'object') {
                this.tableData = response.data.rtnData
                this.pageInfo = response.data.pageInfo
                        ? response.data.pageInfo
                        : {
                            page: this.pageInfo.page,
                            pageSize: this.pageInfo.pageSize,
                            total: this.pageInfo.total
                        }
            } else {
                this.tableData = []
                this.pageInfo.page = 1
                this.pageInfo.total = 0
                this.$message({
                    message: response.message,
                    type: 'error'
                })
            }
        })
        .catch(() => {
                this.$message({
                message: '查询表格操作失败',
                type: 'error'
            })
        })
        },
        // 分页
        handleSizeChange(val) {
            this.pageInfo.page = 1
            this.pageInfo.pageSize = val
            this.getTableData(this.condition, this.pageInfo)
        },
        handleCurrentChange(val) {
            this.pageInfo.page = val
            this.getTableData(this.condition, this.pageInfo)
        }
    }
    }
</script>
<style lang="scss" scoped>
    .template-ipage {
        height: calc(100vh - 50px);
        padding: 10px 0;
        overflow-y: auto;
    }
    .group-btns {
        margin-left: 30px;
    }
    .tableData {
        width: 98%;
        margin: 0 auto;
    }
</style>
