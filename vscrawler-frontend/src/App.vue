<style>
    #app {
        height: 100%;
    }
</style>

<template>
    <div id="app">
        <router-view></router-view>
    </div>
</template>

<script>
    export default {
        name: 'app',
        created() {
            // 路由跳转事件
            this.$router.beforeEach((to, from, next) => {
                console.log('app to', to)
                next()
            })
        }
    }
</script>
