<template>
    <div id="cTable" class="component-table">
        <Table ref="table" :row-class-name="rowClass" :columns="columns" :data="table"></Table>
        <Page v-if="page > 10" :current="pagger" show-total :total="page" size="small" @on-change="changePage"></Page>
    </div>
</template>

<script>
    export default {
        name: 'cTable',
        props: ['columns', 'datas', 'query', 'rowClass', 'onSelectionChange', 'onRowClick', 'onExpand'],
        data() {
            return {
                /**
                 * 页码
                 * @type {Number}
                 */
                pagger: 1
            }
        },
        computed: {
            /**
             * 计算属性
             * 通过customer和输入的query过滤项生成表格原始数据
             * @return {[type]} [description]
             */
            data() {
                return this.query ? this.filters.filterQuery(this.datas, this.query) : this.datas
            },
            /**
             * 计算属性
             * 通过data和pagger生成表格显示数据
             * @return {[type]} [description]
             */
            table() {
                return this.data.filter((item, index) => {
                    return index >= ((this.pagger - 1) * 10) && index < this.pagger * 10
                })
            },
            /**
             * 计算属性
             * 通过原始数据长度生成总页数
             * @return {[type]} [description]
             */
            page() {
                this.pagger = 1
                return this.data.length
            }
        },
        methods: {
            /**
             * 改变页码引发的回调函数
             * @param  {[type]} page [description]
             * @return {[type]}      [description]
             */
            changePage(page) {
                return this.pagger = page
            },
            formatDate(date, fmt) {
                if (!date) {
                    return '-'
                }
                return this.filters.formatDate(date, fmt)
            },
            emptyTo(value) {
                return this.filters.emptyTo(value)
            },
            /**
             * 表格方法都通过$emit传递给父组件
             * @param  {[type]} fun  [description]
             * @param  {[type]} data [description]
             * @return {[type]}      [description]
             */
            func(fun, data) {
                this.$emit(fun, data)
            }
        }
    }
</script>
