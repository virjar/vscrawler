<style>
.icon-central {
    display: flex;
    align-items: center;
    height: 100%;
}
</style>

<template>
<div id="dataApi" class="api-modal-body">
    <div class="content-title">
        <div class="content-title">
            <span style="width: 100px; margin-top: 5px">API名称</span>
            <Input v-model="apiData.requestName" :readonly="!api.editable"></Input>
        </div>
        <!-- <Button style="margin: 0 0 0 5px">返回</Button> -->
    </div>
    <section>
        <div class="section-title">
            请求配置
        </div>
        <div class="content-title">
            <Input v-model="apiData.url" :readonly="!api.editable">
            <Select v-model="apiData.httpMethod" slot="prepend" style="width: 80px">
                    <Option value="GET">GET</Option>
                    <Option value="POST">POST</Option>
                </Select>
            </Input>
            <Button style="margin: 0 0 0 5px" @click="apiTest">测试</Button>
        </div>
        <Tabs value="params">
            <TabPane label="UrlParams" name="params">
                <Table :columns="apiData.urlParameter.columns" size="small" :data="apiData.urlParameter.data"></Table>
            </TabPane>
            <TabPane label="Headers" name="header">
                <Table :columns="apiData.header.columns" :data="apiData.header.data"></Table>
            </TabPane>
            <TabPane label="Body" name="body">
                <RadioGroup v-model="apiData.postType" style="margin: -10px 0 10px 0">
                    <Radio label="0">form-data</Radio>
                    <Radio label="1">row-json</Radio>
                    <Radio label="2">row-xml</Radio>
                </RadioGroup>
                <Table v-if="apiData.postType == 0" :columns="apiData.formData.columns" :data="apiData.formData.data"></Table>
                <Input v-if="apiData.postType != 0" :readonly="!api.editable" v-model="apiData.postParameter" type="textarea" :rows="4"></Input>
            </TabPane>
        </Tabs>
    </section>
    <section class="section-style">
        <div class="section-title">
            返回配置
        </div>
        <Tabs value="body" :animated="false">
            <TabPane label="Body" name="body">
                <Input v-model="response.response" type="textarea" readonly :rows="8"></Input>
            </TabPane>
            <TabPane label="标准化转换配置" name="trans">
                <div class="json-box">
                    <Row type="flex" align="middle">
                        <Col span="10">
                        <pre>
                        {{'\n'+JSON.stringify(this.jsonObj, null, '    ')}}
                    </pre>
                        </Col>
                        <Col span="4">
                        <div>
                            <Icon size='50' type="arrow-right-c"></Icon>
                        </div>
                        </Col>
                        <Col span="10">
                        <div class="json-format">
                            <Tree :data="treeData" :render="renderContent"></Tree>
                        </div>
                        </Col>
                    </Row>
                </div>
            </TabPane>
        </Tabs>
    </section>
    <Modal v-model="modal.show" title="值映射" :transfer="false">
        <Table :columns="api.editable ? columns : readonlyColumns" :data="modal.currentData.valueTrans"></Table>
        <div class="radio-label">
            <span>其它默认转换为: </span>
            <RadioGroup v-model="modal.currentData.rawOutput">
                <Radio label="0">原值</Radio>
                <Radio label="1">指定值</Radio>
            </RadioGroup>
            <Input v-if="modal.currentData.rawOutput == 1" v-model="modal.currentData.defaultValue" placeholder="请输入指定值"></Input>
        </div>
        <div slot="footer">
        </div>
    </Modal>
    <div>
        <p style="text-align: center; margin-top: 20px">
            <Button type="primary" @click="saveApiPre">保存</Button>
            <Button type="ghost" @click="handleReset('modal')" style="margin-left: 8px">清空</Button>
        </p>
    </div>
</div>
</template>

<script>
export default {
    name: 'dataApi',
    props: ['saveApi', 'sourceId', 'api'],
    data() {
        return {
            modal: {
                show: false,
                currentData: {},
            },
            currentPath: '',
            apiData: {
                id: '',
                sourceId: '',
                httpMethod: 'GET',
                header: this.getColumnsAndData('header'),
                urlParameter: this.getColumnsAndData('urlParameter'),
                url: '',
                requestName: '',
                postParameter: '',
                formData: this.getColumnsAndData('formData'),
                postType: '0',
                sourceId: '',
                transJson: '',
            },
            jsonObj: {
                name: '',
                age: '',
                gender: '',
                weight: '',
                key1: '',
                key2: '',
                key3: '',
                key4: '',
                key5: '',
                son: {
                    name: '',
                    age: ''
                }
            },
            data: [{}],
            readonlyColumns: [{
                title: '源',
                align: 'center',
                key: 'rawValue'
            }, {
                title: ' ',
                align: 'center',
                width: '70',
                render: (h, params) => {
                    return h('span', [
                        h('Icon', {
                            props: {
                                type: 'arrow-right-c',
                                size: 30
                            }
                        })
                    ])
                }
            }, {
                title: '标准',
                align: 'center',
                key: 'value'
            }],
            columns: [{
                title: '源',
                align: 'center',
                render: (h, params) => {
                    let valueTrans = this.modal.currentData.valueTrans
                    return h('Input', {
                        props: {
                            value: params.row.rawValue,
                            placeholder: '请输入源值'
                        },
                        on: {
                            'on-blur': (event) => {
                                console.log(event)
                                valueTrans[params.row._index].rawValue = event.target.value
                            }
                        }
                    })
                }
            }, {
                title: ' ',
                align: 'center',
                width: '70',
                render: (h, params) => {
                    return h('span', [
                        h('Icon', {
                            props: {
                                type: 'arrow-right-c',
                                size: 30
                            }
                        })
                    ])
                }
            }, {
                title: '标准',
                align: 'center',
                render: (h, params) => {
                    let valueTrans = this.modal.currentData.valueTrans
                    return h('Input', {
                        props: {
                            value: params.row.value,
                            placeholder: '请输入标准值'
                        },
                        on: {
                            'on-blur': (event) => {
                                valueTrans[params.row._index].value = event.target.value
                            }
                        }
                    })
                }
            }, {
                title: '操作',
                align: 'center',
                width: '100',
                render: (h, params) => {
                    let valueTrans = this.modal.currentData.valueTrans
                    return h('span', [
                        h('a', {
                            on: {
                                click: () => {
                                    valueTrans.push({
                                        rawValue: '',
                                        value: ''
                                    })
                                }
                            },
                            style: {
                                display: params.row._index == valueTrans.length - 1 ? '' : 'none'
                            }
                        }, [
                            h('Icon', {
                                props: {
                                    type: 'ios-plus-outline',
                                    size: 25
                                }
                            }),
                        ]),
                        h('span', {
                            style: {
                                display: valueTrans.length == 1 ? 'none' : ''
                            }
                        }, [
                            h('span', {
                                style: {
                                    margin: '0 2px'
                                }
                            }, ' '),
                            h('a', {
                                on: {
                                    click: () => {
                                        valueTrans.splice(params.row._index, 1)
                                    }
                                }
                            }, [
                                h('Icon', {
                                    props: {
                                        type: 'ios-minus-outline',
                                        size: 25
                                    }
                                })
                            ]),
                        ])
                    ])
                }
            }],
            treeData: [{
                title: 'json',
                expand: true,
                render: (h, {
                    root,
                    node,
                    data
                }) => {
                    return h('span', {
                        style: {
                            display: 'inline-block',
                            width: '100%'
                        }
                    }, [
                        h('span', [
                            h('span', data.title),
                            h('span', ' : '),
                        ])
                    ]);
                },
                children: []
            }],
            buttonProps: {
                type: 'ghost',
                size: 'small',
            },
            response: {}
        }
    },
    mounted() {
        this.treeData[0].children = this.appendChildren(this.jsonObj)
        this.getApi()
    },
    computed: {
        doSaveApi() {
            return this.saveApi || (() => {})
        },
        headerItem() {
            let header = {}
            this.apiData.header.data.forEach((value) => {
                header[value.key] = value.value
            })
            return header
        },
        urlParameterItem() {
            let urlParameter = {}
            this.apiData.urlParameter.data.forEach((value) => {
                urlParameter[value.key] = value.value
            })
            return urlParameter
        },
        formDataItem() {
            let formData = {}
            this.apiData.formData.data.forEach((value) => {
                formData[value.key] = value.value
            })
            return formData
        }
    },
    methods: {
        getApi() {
            this.service.getDataSourceApi(this.api.apiId).then((response) => {
                if (response.response_code == '00') {
                    let content = response.content
                    let apiData = this.apiData
                    apiData.httpMethod = content.httpMethod
                    apiData.postType = content.postType.toString()
                    apiData.requestName = content.requestName
                    apiData.id = content.id
                    apiData.sourceId = content.sourceId
                    apiData.url = content.url
                } else {
                    this.$Notice.error({
                        title: '发生错误',
                        desc: response.response_msg
                    })
                }
            })
        },
        saveApiPre() {
            let transJson = []
            transJson = this.getTransJson(this.treeData[0].children, transJson)
            let apiData = this.apiData
            let data = {
                httpMethod: apiData.httpMethod,
                header: '',
                // header: JSON.stringify(this.headerItem),
                urlParameter: '',
                // urlParameter: JSON.stringify(this.urlParameterItem),
                url: apiData.url,
                requestName: apiData.requestName,
                postParameter: apiData.postType == 0 ? '' : apiData.postParameter,
                // postParameter: apiData.postType == 0 ? JSON.stringify(this.formDataItem) : apiData.postParameter,
                postType: apiData.postType,
                transJson: JSON.stringify(transJson),
                sourceId: this.sourceId
            }
            this.doSaveApi(data)
        },
        apiTest() {
            let apiData = this.apiData
            let data = {
                httpMethod: apiData.httpMethod,
                header: JSON.stringify(this.headerItem),
                urlParameter: JSON.stringify(this.urlParameterItem),
                url: apiData.url,
                requestName: apiData.requestName,
                postParameter: apiData.postType == 0 ? JSON.stringify(this.formDataItem) : apiData.postParameter,
                postType: apiData.postType
            }
            this.service.postTestDataSourceApi(data).then((response) => {
                if (response.response_code == '00') {
                    this.response = response.content
                } else {
                    this.$Notice.error({
                        title: '发生错误',
                        desc: response.response_msg
                    })
                }
            })
        },
        renderReadOnlyColumns() {
            return [{
                title: 'Key',
                align: 'center',
                key: 'key'
            }, {
                title: 'Value',
                align: 'center',
                key: 'value'
            }, {
                title: '描述',
                align: 'center',
                key: 'des'
            }]
        },
        getColumnsAndData(name) {
            return {
                columns: this.api.editable ? this.renderColumns(name) : this.renderReadOnlyColumns(),
                data: this.api.editable ? [{
                    key: '',
                    value: '',
                    des: ''
                }] : []
            }
        },
        renderColumns(name) {
            return [{
                title: '操作',
                align: 'center',
                width: '100',
                render: (h, params) => {
                    let data = this.apiData[name].data
                    return h('span', [
                        h('a', {
                            on: {
                                click: () => {
                                    data.push({
                                        key: '',
                                        value: '',
                                        des: ''
                                    })
                                }
                            },
                            style: {
                                display: params.row._index == data.length - 1 ? '' : 'none'
                            }
                        }, [
                            h('Icon', {
                                props: {
                                    type: 'ios-plus-outline',
                                    size: 25
                                }
                            }),
                        ]),
                        h('span', {
                            style: {
                                display: data.length == 1 ? 'none' : ''
                            }
                        }, [
                            h('span', {
                                style: {
                                    margin: '0 2px'
                                }
                            }, ' '),
                            h('a', {
                                on: {
                                    click: () => {
                                        data.splice(params.row._index, 1)
                                    }
                                }
                            }, [
                                h('Icon', {
                                    props: {
                                        type: 'ios-minus-outline',
                                        size: 25
                                    }
                                })
                            ]),
                        ])
                    ])
                }
            }, {
                title: 'Key',
                align: 'center',
                render: (h, params) => {
                    let data = this.apiData[name].data
                    return h('Input', {
                        props: {
                            value: params.row.key,
                            placeholder: '请输入key'
                        },
                        on: {
                            'on-blur': (event) => {
                                data[params.row._index].key = event.target.value
                            }
                        }
                    })
                }
            }, {
                title: 'Value',
                align: 'center',
                render: (h, params) => {
                    let data = this.apiData[name].data
                    return h('Input', {
                        props: {
                            value: params.row.value,
                            placeholder: '请输入value'
                        },
                        on: {
                            'on-blur': (event) => {
                                data[params.row._index].value = event.target.value
                            }
                        }
                    })
                }
            }, {
                title: '描述',
                align: 'center',
                render: (h, params) => {
                    let data = this.apiData[name].data
                    return h('Input', {
                        props: {
                            value: params.row.des,
                            placeholder: '请输入描述'
                        },
                        on: {
                            'on-blur': (event) => {
                                data[params.row._index].des = event.target.value
                            }
                        }
                    })
                }
            }]
        },
        renderContent(h, {
            root,
            node,
            data
        }) {
            return h('span', {
                style: {
                    display: 'inline-block',
                    width: '100%'
                }
            }, [
                h('span', [
                    h('a', {
                        on: {
                            click: () => {
                                this.showValueTrans(data)
                            }
                        },
                        style: {
                            color: data.valueTrans[0].rawValue != '' ? 'purple' : ''
                        }
                    }, data.title),
                    h('span', ' : '),
                    h('Poptip', {
                        props: {
                            placement: 'right-end',
                            width: 200,
                            trigger: 'hover',
                            // confirm: true
                            transfer: true
                        },
                        on: {
                            'on-popper-show': () => {
                                this.currentPath = data.path
                            }
                        }
                    }, [
                        h('Icon', {
                            props: {
                                type: 'code-working',
                                size: 18,
                            },
                            style: {
                                color: 'green',
                                position: 'relative',
                                left: '5px',
                                top: '3px',
                                display: data.children || data.keyName ? 'none' : ''
                            }
                        }),
                        h('span', {
                            style: {
                                display: data.children || data.keyName ? '' : 'none',
                                color: 'orange',
                                fontSize: '14px'
                            }
                        }, data.keyName),
                        h('div', {
                            slot: 'content',
                        }, [
                            h('Tree', {
                                props: {
                                    data: this.renderKeys(h, data),
                                    render: (h, params) => {
                                        return this.renderKeyContent(h, params, data)
                                    }
                                }
                            })
                        ])
                    ])
                ]),
            ]);
        },
        renderKeyContent(h, {
            root,
            node,
            data
        }, fromData) {
            return h('span', {
                style: {
                    display: 'inline-block',
                    width: '100%'
                }
            }, [
                h('span', [
                    h('a', {
                        on: {
                            click: this.api.editable ? () => {
                                this.setTargetKey(data, fromData)
                            } : () => {}
                        },
                        style: {
                            color: data.path == fromData.keyPath ? 'orange' : ''
                        }
                    }, data.title)
                ]),
            ]);
        },
        showValueTrans(data) {
            this.modal.show = true
            this.modal.currentData = this.findCurrentData(data.path)
        },
        renderKeys(h, nodeData) {
            let treeNodes = [{}]
            let renderRoot = (h, {
                root,
                node,
                data
            }) => {
                return h('span', {
                    style: {
                        display: 'inline-block',
                        width: '100%'
                    }
                }, [
                    h('span', [
                        h('span', data.title),
                        h('span', ':'),
                    ])
                ]);
            }
            treeNodes[0] = {
                title: 'keys',
                expand: true,
                render: renderRoot,
                children: this.appendChildren(this.jsonObj)
            }
            return treeNodes
        },
        setTargetKey(data, fromData) {
            let currentData = this.findCurrentData(fromData.path)
            currentData.keyName = data.title.toString()
            currentData.keyPath = data.path.toString()
        },
        findCurrentData(path) {
            let paths = path.split('.')
            let currentChildren = this.treeData[0].children
            let currentData = this.treeData.children
            for (let path of paths) {
                for (let child of currentChildren) {
                    if (child.title == path) {
                        currentData = child
                        if (child.children) {
                            currentChildren = child.children
                        }
                        break
                    }
                }
            }
            return currentData
        },
        appendChildren(data, nodeData) {
            let childrenNodes = []
            for (let [key, value] of Object.entries(data)) {
                let node = {
                    title: key,
                    expand: true,
                    path: nodeData ? `${nodeData.path}.${key}` : `${key}`,
                    keyName: '',
                    keyPath: '',
                    defaultValue: '',
                    rawOutput: '0',
                    valueTrans: [{
                        rawValue: '',
                        value: ''
                    }]
                }
                if (typeof value == 'object') {
                    node['children'] = this.appendChildren(value, node)
                }
                childrenNodes.push(node)
            }
            return childrenNodes
        },
        getTransJson(children, transJson) {
            children.forEach((item) => {
                if (item.keyPath && item.path) {
                    const data = {
                        sourcePath: '$.' + item.keyPath,
                        targetPath: '$.' + item.path,
                        rawOutput: item.rawOutput,
                        defaultValue: item.defaultValue,
                        valueTrans: item.valueTrans
                    }
                    transJson.push(data)
                }
                if (item.children) {
                    this.getTransJson(item.children, transJson)
                }
            })
            return transJson
        }
    }
}
</script>
