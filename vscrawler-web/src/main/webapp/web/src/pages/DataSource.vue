<template>
<section>
    <!-- 搜索表单 -->
    <Form :label-width="120" style="border-bottom: 1px solid #f0f0f0;margin-bottom: 15px;">
        <Row>
            <Col span="12">
            <FormItem label="数据源名称">
                <Input v-model="info.dataSource" placeholder="数据源名称" style="width:300px;"></Input>
            </FormItem>
            </Col>
            <Col span="12">
            <FormItem label="数据源类型">
                <Input v-model="info.dataType" placeholder="数据源类型" style="width:300px;"></Input>
            </FormItem>
            </Col>
            <Col span="12">
            <FormItem label="状态">
                <Select v-model="info.status" style="width:300px">
                    <Option value="-1">全部</Option>
                    <Option value="0">启用</Option>
                    <Option value="1">停用</Option>
                </Select>
            </FormItem>
            </Col>
            <Col span="12">
            <FormItem>
                <Button type="primary" @click="getDataSourceList">搜索</Button>
                <Button type="primary" @click="clearSearch">清空</Button>
            </FormItem>
            </Col>
        </Row>
    </Form>
    <!-- 操作按钮 -->
    <div class="content-title">
        <p></p>
        <p>
            <Button type="primary" icon="android-refresh" @click="getDataSourceList">刷新</Button>
            <Button type="primary" icon="android-add" @click="() => operate('add')">新增</Button>
        </p>
    </div>
    <!-- 表格 -->
    <c-table :columns="columns" :datas="datas"></c-table>
    <!-- 数据源 -->
    <Modal v-model="modal.show">
        <p slot="header">
            <span>{{modal.type}}</span>
        </p>
        <Form ref="modal" :model="modal.data" :rules="modal.rule" :label-width="120">
            <FormItem label="数据源名称" prop="name">
                <Input v-model="modal.data.name" placeholder="请输入数据源名称" :readonly="modal.type == '数据源配置详情'"></Input>
            </FormItem>
            <FormItem label="数据源描述" prop="description">
                <Input v-model="modal.data.description" placeholder="请输入数据源描述" :readonly="modal.type == '数据源配置详情'"></Input>
            </FormItem>
            <FormItem label="数据源类型" prop="dataTypeId">
                <Select v-model="modal.data.dataTypeId" filterable placeholder="请选择数据类型" :disabled="modal.type == '数据源配置详情'">
                    <Option v-for="item in dataTypes" :value="item.id" :key="item.id">{{ item.name }}</Option>
                </Select>
            </FormItem>
            <FormItem label="是否为默认数据源" prop="isDefault">
                <RadioGroup v-model="modal.data.isDefault" :disabled="modal.type == '数据源配置详情'">
                    <Radio label="1">是</Radio>
                    <Radio label="0">否</Radio>
                </RadioGroup>
            </FormItem>
            <FormItem label="权重(0-100)" prop="weight">
                <InputNumber v-model="modal.data.weight" :min="0" :max="100" :readonly="modal.type == '数据源配置详情'"></InputNumber>
            </FormItem>
        </Form>
        <p slot="footer" style="text-align: center" v-show="modal.type != '数据源配置详情'">
            <Button type="primary" @click="handleSubmit('modal')">保存</Button>
            <Button type="ghost" @click="handleReset('modal')" style="margin-left: 8px">清空</Button>
        </p>
    </Modal>
    <!-- 展示所有关联api -->
    <Modal v-model="modalApi.show">
        <p slot="header">
            <span>查看API</span>
        </p>
        <Form :label-width="80">
            <FormItem label="关联API">
                <Button type="primary" @click="apiData.show = true">新增API</Button>
                <c-table border :columns="modalApi.column" :datas="modalApi.datas" style="margin-top:5px;"></c-table>
            </FormItem>
        </Form>
        <p slot="footer"></p>
    </Modal>
    <Modal v-model="apiData.show" width="800" :mask-closable="false" scrollable class-name="modal-no-footer">
        <p slot="header">
            <span>API</span>
        </p>
        <div v-if="apiData.show">
            <data-api :save-api="saveApi" :source-id="modalApi.sourceId" :api="apiData"></data-api>
        </div>
        <p slot="footer" style="text-align: center; height: 0px">
            <!-- <Button type="primary" @click="handleSubmit('modal')">保存</Button>
            <Button type="ghost" @click="handleReset('modal')" style="margin-left: 8px">清空</Button> -->
        </p>
    </Modal>
</section>
</template>

<script>
import cTable from "../components/table";
import dataApi from "./DataApi"
export default {
    components: {
        cTable,
        dataApi
    },
    data() {
        return {
            apiData: {
                show: false,
                apiId: '',
                editiable: true
            },
            info: {
                dataSource: '',
                dataType: '',
                status: '-1'
            },
            columns: [{
                    title: "数据源名称",
                    width: 130,
                    key: "name"
                },
                {
                    title: "数据源描述",
                    width: 130,
                    key: "description"
                },
                {
                    title: "数据源类型",
                    width: 130,
                    key: "data_type_name"
                },
                {
                    title: "数据源权重",
                    width: 120,
                    key: "weight"
                },
                {
                    title: "是否默认",
                    width: 100,
                    render: (h, params) => {
                        return h('span', params.row.is_default == '0' ? '否' : '是')
                    }
                },
                {
                    title: "关联API",
                    width: 100,
                    key: "dataSourceApiDtoList",
                    render: (h, params) => {
                        return h("span", [
                            h(
                                "a", {
                                    on: {
                                        click: () => {
                                            this.modalApi.sourceId = params.row.id;
                                            this.modalApi.datas = params.row.data_source_api_list
                                            this.modalApi.show = true;
                                        }
                                    }
                                },
                                params.row.data_source_api_list.length
                            )
                        ]);
                    }
                },
                // {
                //     title: "状态",
                //     width: 80,
                //     key: "status"
                // },
                {
                    title: "操作",
                    width: 220,
                    render: (h, params) => {
                        return h("span", [
                            h("a", {
                                on: {
                                    click: () => {
                                        this.operate('show', params.row)
                                    }
                                }
                            }, "查看"),
                            h("span", " | "),
                            h(
                                "a", {
                                    on: {
                                        click: () => {
                                            this.operate('edit', params.row)
                                        }
                                    }
                                },
                                "修改"
                            ),
                            h("span", " | "),
                            h(
                                "Poptip", {
                                    on: {
                                        "on-ok": () => {
                                            this.doDelete(params.row);
                                        },
                                        "on-cancel": () => {
                                            console.log("cancel");
                                        }
                                    },
                                    props: {
                                        confirm: true,
                                        title: "确认删除" + params.row.name
                                    }
                                }, [h("a", "删除")]
                            )
                        ]);
                    }
                }
            ],
            datas: [],
            dataTypes: [],
            modal: {
                show: false,
                type: '',
                data: {
                    name: '',
                    weight: 0,
                    description: '',
                    dataTypeId: '',
                    isDefault: 0
                },
                rule: {
                    name: [{
                        required: true,
                        message: '请输入名称',
                        trigger: 'blur'
                    }],
                    description: [{
                        required: true,
                        message: '请输入描述',
                        trigger: 'blur'
                    }],
                    dataTypeId: [{
                        required: true,
                        type: 'number',
                        message: '请选择数据类型',
                        trigger: 'change'
                    }],
                    weight: [{
                        required: true,
                        message: '请输入权重',
                        type: 'number',
                        trigger: 'blur'
                    }],
                    isDefault: [{
                        required: true,
                        message: '请选择是否默认',
                        trigger: 'change'
                    }]
                }
            },
            modalApi: {
                show: false,
                sourceId: '',
                column: [{
                        title: "API名称",
                        width: 120,
                        key: "request_name"
                    },
                    {
                        title: "执行顺序",
                        width: 100,
                        key: "order"
                    },
                    {
                        title: "操作",
                        width: 150,
                        align: 'center',
                        render: (h, params) => {
                            return h("span", [
                                h("a", {
                                    on: {
                                        click: () => {
                                            this.apiData.apiId = params.row.id
                                            this.apiData.editable = false;
                                            this.apiData.show = true
                                        }
                                    }
                                }, "查看"),
                                h("span", "|"),
                                h("a", {
                                    on: {
                                        click: () => {
                                            this.apiData.apiId = params.row.id
                                            this.apiData.editable = true;
                                            this.apiData.show = true
                                        }
                                    }
                                }, "修改"),
                                h("span", "|"),
                                h("Poptip", {
                                    on: {
                                        "on-ok": () => {
                                            this.postDeleteApi(params.row.sourceApiId);
                                        },
                                        "on-cancel": () => {
                                            console.log("cancel");
                                        }
                                    },
                                    props: {
                                        confirm: true,
                                        title: "确认删除" + params.row.name
                                    }
                                }, [h("a", "删除")])
                            ]);
                        }
                    }
                ],
                datas: []
            },
        };
    },
    mounted() {
        this.getDataSourceList();
        this.getDataTypeList();
    },
    methods: {
        saveApi(data) {
            this.service.postInsertDataSourceApi(data).then((response) => {
                if (response.response_code == '00') {
                    console.log(response)
                } else {
                    this.$Notice.error({
                        title: '发生错误',
                        desc: response.response_msg
                    })
                }
            })
        },
        // 查询数据源类型列表
        getDataTypeList() {
            this.service.getDataTypeList('').then((response) => {
                if (response.response_code == '00') {
                    this.dataTypes = response.content
                } else {
                    this.$Notice.error({
                        title: '发生错误',
                        desc: response.response_msg
                    })
                }
            })
        },
        // 获取数据源信息
        getDataSourceList() {
            let data = {
                dataSource: this.info.dataSource,
                dataType: this.info.dataType,
                status: this.info.status == '-1' ? '' : this.info.status,
            }
            this.service.getDataSourceList(data).then(response => {
                if (response.response_code == "00") {
                    this.datas = response.content;
                    if (this.datas.length == 0) {
                        this.$Message.info('暂无数据')
                    }
                } else {
                    this.$Notice.error({
                        title: "发生错误",
                        desc: response.response_msg
                    });
                }
            });
        },
        // 清空
        clearSearch() {
            this.info = {
                dataSource: '',
                dataType: '',
                status: '-1'
            }
        },
        // 模态框
        operate(type, params) {
            let types = {
                add: '新增数据源配置',
                show: '数据源配置详情',
                edit: '编辑数据源配置'
            }

            this.handleReset('modal')
            
            this.modal.type = types[type]
            this.modal.show = true
            this.modal.data = {
                name: '',
                weight: 0,
                description: '',
                dataTypeId: '',
                isDefault: '0'
            }
            if (type == 'add') {
                this.handleReset('modal')
            } else {
                this.modal.data = JSON.parse(JSON.stringify(params))
                this.dataTypes.map((i) => {
                    if (i.name == this.modal.data.data_type_name) {
                        this.modal.data.dataTypeId = i.id
                    }
                })
                this.modal.data.weight = Number(this.modal.data.weight)
                this.modal.data.isDefault = this.modal.data.is_default
            }
        },
        handleSubmit(name) {
            this.$refs[name].validate(valid => {
                if (valid) {

                    let data = {
                        name: this.modal.data.name,
                        weight: this.modal.data.weight,
                        description: this.modal.data.description,
                        dataTypeId: this.modal.data.dataTypeId,
                        isDefault: this.modal.data.isDefault
                    }

                    if (this.modal.type == '新增数据源配置') {
                        this.service.postInsertDataSource(data).then(response => {
                            if (response.response_code == '00') {
                                this.getDataSourceList()
                                this.modal.show = false
                                this.$Message.success('新增成功')
                            } else {
                                this.$Notice.error({
                                    title: '发生错误',
                                    desc: response.response_msg
                                })
                            }
                        })
                    } else {
                        data.id = this.modal.data.id
                        this.service.postUpdateDataSource(data).then(response => {
                            if (response.response_code == '00') {
                                this.getDataSourceList()
                                this.modal.show = false
                                this.$Message.success('修改成功')
                            } else {
                                this.$Notice.error({
                                    title: '发生错误',
                                    desc: response.response_msg
                                })
                            }
                        })
                    }
                } else {
                    this.$Message.error('请按照要求填写信息!');
                }
            });
        },
        handleReset(name) {
            this.$refs[name].resetFields();
        },
        getSourceApi() {
            this.service.getDataSourceApi().then(response => {
                if (response.response_code == "00") {
                    this.data1 = response.content;
                    this.formValidate.requestName =
                        response.content.requestName;
                    this.formValidate.url = response.content.url;
                } else {
                    this.$Notice.error({
                        title: "发生错误",
                        desc: response.response_msg
                    });
                }
            });
        },
        getAllSource() {
            this.service.getDataSourceList().then(response => {
                if (response.response_code == "00") {
                    this.datas = response.content;
                } else {
                    this.$Notice.error({
                        title: "发生错误",
                        desc: response.response_msg
                    });
                }
            });
        },
        doDelete(params) {
            this.service.getDeleteDataSource(params.id).then(response => {
                if (response.response_code == "00") {
                    this.$Message.success("删除成功！", 4);
                    this.getDataSourceList();
                } else {
                    this.$Notice.error({
                        title: "发生错误",
                        desc: response.response_msg
                    });
                }
            });
        },
        // 通过id删除关联api
        postDeleteDataSourceApi(id) {
            this.service.postDeleteDataSourceApi({
                sourceApiId: id
            }).then((response) => {
                if (response.response_code == '00') {
                    this.modalApi.datas.filter((i) => {
                        return i.sourceApiId != id
                    })
                    this.getDataSourceList()
                    this.$Message.success('删除成功！', 2)
                } else {
                    this.$Notice.error({
                        title: '发生错误',
                        desc: response.response_msg
                    })
                }
            })
        }
    }
};
</script>