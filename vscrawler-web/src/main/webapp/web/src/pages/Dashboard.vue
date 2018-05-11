<template>
    <div id="dashboard">
        <div id="chart" style="width: 100%; height: 400px;"></div>
    </div>
</template>
<script>
import echarts from "echarts"
export default {
    name: 'dashboard',
    data() {
        return {
        }
    },
    mounted() {
        this.paint()
    },
    methods: {
        paint() {
            let chart = echarts.init(document.getElementById('chart'))
            chart.clear()
            chart.setOption({
                tooltip: {
                    trigger: 'axis',
                    axisPointer: {            // 坐标轴指示器，坐标轴触发有效
                        type: 'shadow'        // 默认为直线，可选为：'line' | 'shadow'
                    }
                },
                legend: {
                    y: 'bottom',
                    data: ['本周命中', '上周命中', '本周运行', '上周运行']
                },
                grid: {
                    top: '5%',
                    left: '0%',
                    right: '4%',
                    bottom: '15%',
                    containLabel: true
                },
                xAxis: [
                    {
                        type: 'category',
                        data: ['客群划分', '客户准入', '资金流动'],
                        axisLabel: {
                            interval: 0,
                            rotate: 40
                        },
                    }
                ],
                yAxis: [{
                    type: 'value',
                }],
                series: [
                    {
                        name: '本周命中',
                        type: 'bar',
                        barMaxWidth: 20,
                        stack: 'left',
                        data: [100, 90, 80]
                    },
                    {
                        name: '上周命中',
                        type: 'bar',
                        barMaxWidth: 20,
                        stack: 'left',
                        data: [50, 48, 46]
                    },
                    {
                        name: '本周运行',
                        type: 'bar',
                        barMaxWidth: 20,
                        stack: 'right',
                        data: [50, 48, 46]
                    },
                    {
                        name: '上周运行',
                        type: 'bar',
                        barMaxWidth: 20,
                        stack: 'right',
                        data: [50, 48, 46]
                    }
                ]
            })

            window.onresize = () => {
                chart.resize()
            }
        }
    }
}
</script>