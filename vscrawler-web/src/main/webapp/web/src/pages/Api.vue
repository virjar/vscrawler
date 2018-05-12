<style lang="less" scoped>
    .bodys {
        background: #fff;
        padding: .15rem;
        .title {
            padding-bottom: .1rem;
            border-bottom: 1px solid #eee;
            font-size: .14rem;
        }
        .content {
            padding: .1rem 0;
        }
        .config {
            display: flex;
            padding: .1rem 0;
        }
    }
    .tests {
        display: flex;
        .flex1 {
            flex: 1;
        }
    }
    .icon-central {
        display: flex;
        align-items: center;
        height: 100%;
    }
</style>

<template>
    <section id="test" class="bodys">
        <Row :gutter="20">
            <Col :sm="12" :xs="24">
            <h5 class="title">请求配置</h5>
            <div class="config">
                <Input v-model="infos.url">
                <Select v-model="infos.method" slot="prepend" style="width: .8rem">
                    <Option value="GET">GET</Option>
                    <Option value="POST">POST</Option>
                </Select>
                </Input>
                <Button type="primary" style="margin-left: 5px" @click="apiTest">测试</Button>
                <Button type="primary" style="margin-left: 5px" @click="clear">清空</Button>
            </div>
            <Tabs value="params">
                <TabPane label="UrlParams" name="params">
                    <div class="component-table">
                        <Table size="small" :columns="urlParams.columns" :data="infos.urlParamDatas"></Table>
                    </div>
                </TabPane>
                <TabPane label="Headers" name="header">
                    <div class="component-table">
                        <Table :columns="header.columns" :data="infos.headerDatas"></Table>
                    </div>
                </TabPane>
                <TabPane label="Body" name="body">
                    <RadioGroup v-model="infos.postType" style="margin: -10px 0 10px 0">
                        <Radio label="0">form-data</Radio>
                        <Radio label="1">row-json</Radio>
                        <Radio label="2">row-xml</Radio>
                    </RadioGroup>
                    <div class="component-table">
                        <Table v-if="infos.postType == 0" :columns="formData.columns" :data="infos.formDatas"></Table>
                    </div>
                    <Input v-if="infos.postType != 0" v-model="infos.postParameter" type="textarea" :rows="4"></Input>
                </TabPane>
            </Tabs>
            </Col>
            <Col :sm="12" :xs="24">
            <h5 class="title">返回结果</h5>
            <Tabs value="body" :animated="false">
                <TabPane label="Body" name="body">
                    <Input v-model="response" type="textarea" readonly :rows="8"></Input>
                </TabPane>
            </Tabs>
            </Col>
        </Row>
    </section>
</template>

<script>
    export default {
        name: 'test',
        data() {
            return {
                infos: {
                    url: '',
                    method: 'GET',
                    urlParamDatas: [],
                    headerDatas: [{
                        key: '',
                        value: ''
                    }],
                    formDatas: [{
                        key: '',
                        value: ''
                    }],
                    postType: '0',
                    postParameter: ''
                },
                urlParams: {
                    columns: [{
                        title: 'key',
                        align: 'center',
                        render: (h, params) => {
                            return h('Input', {
                                props: {
                                    value: this.infos.urlParamDatas[params.index].key
                                },
                                on: {
                                    'on-blur': (e) => {
                                        this.infos.urlParamDatas[params.index].key = e.target.value
                                    }
                                }
                            })
                        }
                    }, {
                        title: 'value',
                        align: 'center',
                        render: (h, params) => {
                            return h('Input', {
                                props: {
                                    value: this.infos.urlParamDatas[params.index].value
                                },
                                on: {
                                    'on-blur': (e) => {
                                        this.infos.urlParamDatas[params.index].value = e.target.value
                                    }
                                }
                            })
                        }
                    }, {
                        title: ' ',
                        align: 'center',
                        render: (h, params) => {
                            return h('span', [
                                h('a', {
                                    style: {
                                        padding: '5px'
                                    },
                                    on: {
                                        click: () => {
                                            this.infos.urlParamDatas.splice(params.index + 1, 0, {
                                                key: '',
                                                value: ''
                                            })
                                        }
                                    }
                                }, [
                                    h('Icon', {
                                        props: {
                                            type: 'plus-circled',
                                            size: 16
                                        }
                                    })
                                ]),
                                h('a', {
                                    style: {
                                        padding: '5px'
                                    },
                                    on: {
                                        click: () => {
                                            this.infos.urlParamDatas.splice(params.index, 1)
                                        }
                                    }
                                }, [
                                    h('Icon', {
                                        props: {
                                            type: 'minus-circled',
                                            size: 16
                                        }
                                    })
                                ]),
                            ])
                        }
                    }]
                },
                header: {
                    columns: [{
                        title: '配置项',
                        align: 'center',
                        render: (h, params) => {
                            return h('Input', {
                                props: {
                                    value: this.infos.headerDatas[params.index].key
                                },
                                on: {
                                    'on-blur': (e) => {
                                        this.infos.headerDatas[params.index].key = e.target.value
                                    }
                                }
                            })
                        }
                    }, {
                        title: '参数',
                        align: 'center',
                        render: (h, params) => {
                            return h('Input', {
                                props: {
                                    value: this.infos.headerDatas[params.index].value
                                },
                                on: {
                                    'on-blur': (e) => {
                                        this.infos.headerDatas[params.index].value = e.target.value
                                    }
                                }
                            })
                        }
                    }, {
                        title: ' ',
                        align: 'center',
                        render: (h, params) => {
                            return h('span', [
                                h('a', {
                                    style: {
                                        padding: '5px'
                                    },
                                    on: {
                                        click: () => {
                                            this.infos.headerDatas.splice(params.index + 1, 0, {
                                                key: '',
                                                value: ''
                                            })
                                        }
                                    }
                                }, [
                                    h('Icon', {
                                        props: {
                                            type: 'plus-circled',
                                            size: 16
                                        }
                                    })
                                ]),
                                h('a', {
                                    style: {
                                        padding: '5px'
                                    },
                                    on: {
                                        click: () => {
                                            this.infos.headerDatas.splice(params.index, 1)
                                        }
                                    }
                                }, [
                                    h('Icon', {
                                        props: {
                                            type: 'minus-circled',
                                            size: 16
                                        }
                                    })
                                ]),
                            ])
                        }
                    }]
                },
                formData: {
                    columns: [{
                        title: '配置项',
                        align: 'center',
                        render: (h, params) => {
                            return h('Input', {
                                props: {
                                    value: this.infos.formDatas[params.index].key
                                },
                                on: {
                                    'on-blur': (e) => {
                                        this.infos.formDatas[params.index].key = e.target.value
                                    }
                                }
                            })
                        }
                    }, {
                        title: '参数',
                        align: 'center',
                        render: (h, params) => {
                            return h('Input', {
                                props: {
                                    value: this.infos.formDatas[params.index].value
                                },
                                on: {
                                    'on-blur': (e) => {
                                        this.infos.formDatas[params.index].value = e.target.value
                                    }
                                }
                            })
                        }
                    }, {
                        title: ' ',
                        align: 'center',
                        render: (h, params) => {
                            return h('span', [
                                h('a', {
                                    style: {
                                        padding: '5px'
                                    },
                                    on: {
                                        click: () => {
                                            this.infos.formDatas.splice(params.index + 1, 0, {
                                                key: '',
                                                value: ''
                                            })
                                        }
                                    }
                                }, [
                                    h('Icon', {
                                        props: {
                                            type: 'plus-circled',
                                            size: 16
                                        }
                                    })
                                ]),
                                h('a', {
                                    style: {
                                        padding: '5px'
                                    },
                                    on: {
                                        click: () => {
                                            this.infos.formDatas.splice(params.index, 1)
                                        }
                                    }
                                }, [
                                    h('Icon', {
                                        props: {
                                            type: 'minus-circled',
                                            size: 16
                                        }
                                    })
                                ]),
                            ])
                        }
                    }]
                },
                response: ''
            }
        },
        methods: {
            apiTest() {
                let data = {
                    crawlerName: '',
                    crawlerVersion: '',
                    queryPage: '',
                    param: ''
                }
                this.service.postGrad(data).then(res => {
                    if (res.status == 0) {
                        this.$Message.success('爬虫测试成功', 2)
                    } else {
                        this.$Message.error(res.message, 2)
                    }
                })
            },
            clear() {}
        }
    }
</script>
