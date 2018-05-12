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
    }
</style>

<template>
    <section class="bodys">
        <h5 class="title">爬虫列表</h5>
        <div class="content">
            <Input v-model="query" placeholder="关键词查询..." style="width: 3rem;" icon="android-search"></Input>
            <Button type="primary" icon="android-refresh" @click="getCrawler">刷新</Button>
            <Button type="primary" icon="android-add" @click="() => operate()">新增</Button>
        </div>
        <c-table :columns="columns" :datas="datas" :query="query"></c-table>
        <Modal v-model="modal.show">
            <p slot="header">
                <span>新增爬虫</span>
            </p>
            <Upload ref="upload" multiple type="drag" name="file" accept="application/java-archive" action="//jsonplaceholder.typicode.com/posts/">
                <div style="padding: 20px 0">
                    <Icon type="ios-cloud-upload" size="52" style="color: #3399ff"></Icon>
                    <p>选择/拖拽 jar 文件上传</p>
                </div>
            </Upload>
            <p slot="footer" style="text-align: center">
                <Button type="primary" @click="finishUpload">完成</Button>
            </p>
        </Modal>
    </section>
</template>

<script>
    import cTable from '../components/table'
    import cTest from './Test'
    export default {
        components: {
            cTable,
        },
        data() {
            return {
                query: '',
                columns: [{
                    title: '爬虫名',
                    width: 180,
                    key: 'crawlerName'
                }, {
                    title: '活跃Thread',
                    width: 140,
                    key: 'activeThreadNumber'
                }, {
                    title: '活跃Session',
                    width: 200,
                    key: 'activeSessionNumber'
                }, {
                    title: '状态',
                    width: 120,
                    render: (h, params) => {
                        return h('span', [
                            h('span', {
                                style: {
                                    color: this.getStatusColor(params.row.status),
                                    'margin-right': '5px',
                                    'font-size': '17px'
                                }
                            }, '●'),
                            h('span', {
                                style: {
                                    color: this.getStatusColor(params.row.status)
                                }
                            }, params.row.status),
                        ])
                    }
                }, {
                    title: '操作',
                    width: 200,
                    render: (h, params) => {
                        let btnType
                        let btnText
                        switch (params.row.status) {
                            case '初始化':
                                btnType = {
                                    type: 'primary'
                                }
                                btnText = '启用'
                                break
                            case '已停止':
                                btnType = {
                                    type: 'primary'
                                }
                                btnText = '启用'
                                break
                            case '运行中':
                                btnType = {
                                    type: 'error'
                                }
                                btnText = '停用'
                                break
                            case '启动中':
                                btnType = {
                                    type: 'info',
                                    disabled: true,
                                    loading: true
                                }
                                btnText = '爬取中...'
                                break
                            default:
                                break
                        }
                        return h('span', [
                            h('Poptip', {
                                props: {
                                    title: 'jar地址',
                                    content: params.row.jarPath || '-',
                                    trigger: 'hover'
                                }
                            }, [h('Button', {
                                props: {
                                    type: 'info',
                                    size: 'small',
                                    icon: 'briefcase'
                                },
                            }, 'jar包')]),
                            h('Button', {
                                style: {
                                    margin: '0 5px'
                                },
                                props: {
                                    type: 'primary',
                                    size: 'small',
                                    icon: 'upload'
                                },
                                on: {
                                    click: () => {
                                        this.$router.push({
                                            path: '/test',
                                            query: {
                                                name: params.row.crawlerName
                                            }
                                        })
                                    }
                                }
                            }, '测试'),
                            h('Poptip', {
                                on: {
                                    'on-ok': () => {
                                        if (btnText == '启用') {
                                            this.service.getStart(params.row.crawlerName).then(res => {
                                                if (res.status == 0) {
                                                    this.getCrawler()
                                                    this.$Message.success('启用成功', 2)
                                                } else {
                                                    this.$Message.error(res.message, 2)
                                                }
                                            })
                                        } else if (btnText == '停用') {
                                            this.service.getStop(params.row.crawlerName).then(res => {
                                                if (res.status == 0) {
                                                    this.getCrawler()
                                                    this.$Message.success('停用成功', 2)
                                                } else {
                                                    this.$Message.error(res.message, 2)
                                                }
                                            })
                                        }
                                    },
                                    'on-cancel': () => {
                                        console.log('cancel')
                                    }
                                },
                                props: {
                                    confirm: true,
                                    title: `确认${btnText}?`
                                }
                            }, [h('Button', {
                                props: {
                                    ...btnType,
                                    size: 'small',
                                    icon: 'flag'
                                },
                            }, btnText)])
                        ])
                    }
                }],
                datas: [],
                modal: {
                    show: false
                }
            }
        },
        mounted() {
            this.getCrawler()
        },
        methods: {
            // 获取爬虫列表
            getCrawler() {
                this.datas = []
                this.service.getCrawler().then((res) => {
                    if (res.status == 0) {
                        this.datas = res.data
                        if (this.datas.length == 0) {
                            this.$Message.info('暂无数据')
                        }
                    } else {
                        this.$Message.error(res.message, 2)
                    }
                })
            },
            // 模态框
            operate(type, params) {
                this.$refs.upload.clearFiles()
                this.modal.show = true
            },
            // 模态框完成事件
            finishUpload() {
                this.modal.show = false
                this.getCrawler()
            },
            // 获取 status 颜色
            getStatusColor(status) {
                switch (status) {
                    case '初始化':
                        return 'gray'
                    case '运行中':
                        return 'green'
                    case '已停止':
                        return 'red'
                    case '启动中':
                        return 'gray'
                    default:
                        return 'gray'
                }
            }
        }
    }
</script>
