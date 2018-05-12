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
    .ivu-form-item-error-tip {
        display: block;
        font-size: .12rem;
    }
</style>

<template>
    <section id="test" class="bodys">
        <Row :gutter="20">
            <Col :sm="12" :xs="24">
            <Tabs value="body" :animated="false">
                <TabPane label="请求配置" name="body">
                    <Form>
                        <FormItem :error="error">
                            <Input v-model="info" @on-blur="getInput" type="textarea" :rows="8"></Input>
                        </FormItem>
                    </Form>
                </TabPane>
                <Button type="primary" style="margin-top: 6px" slot="extra" size="small" icon="gear-a" @click="doTest" :loading="loading">测试</Button>
                <Button type="primary" style="margin-top: 6px;margin-left: 6px;" slot="extra" size="small" icon="trash-a" @click="doClear">清空</Button>
            </Tabs>
            </Col>
            <Col :sm="12" :xs="24">
            <Tabs value="body" :animated="false">
                <TabPane label="返回结果" name="body">
                    <Input v-model="response" type="textarea" readonly :rows="8"></Input>
                </TabPane>
            </Tabs>
            </Col>
        </Row>
    </section>
</template>

<script>
    import jsonlint from 'jsonlint'
    export default {
        name: 'test',
        data() {
            return {
                info: '',
                error: '',
                response: '',
                loading: false
            }
        },
        created() {
            if (this.$route.query.name) {
                this.info = JSON.stringify(JSON.parse(`{"crawlerName":"${this.$route.query.name}"}`), null, 4)
            }
        },
        methods: {
            // blur 进行输入校验
            getInput() {
                this.error = ''
                try {
                    this.info = JSON.stringify(JSON.parse(this.info), null, 4)
                } catch (e) {
                    this.error = e.message
                }
            },
            // 测试
            doTest() {
                this.error = ''
                try {
                    let data = jsonlint.parse(this.info)
                    this.loading = true
                    this.service.postGrad(data).then(res => {
                        this.loading = false
                        if (res.status == 0) {
                            this.$Message.success('爬虫测试成功', 2)
                        } else {
                            this.$Message.error(res.message || '出错了！', 2)
                        }
                    })
                } catch (e) {
                    this.error = e.message
                    this.$Message.error(e.message || '出错了！', 2)
                }
            },
            // 清空
            doClear() {
                this.error = ''
                this.info = ''
                this.response = ''
            }
        }
    }
</script>
