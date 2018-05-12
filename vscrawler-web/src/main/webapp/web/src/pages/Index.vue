<style lang="less" scoped>
    .main {
        height: 100vh;
    }
    .header {
        display: flex;
        justify-content: space-between;
        align-items: center;
        height: .7rem;
        background: #fff;
        box-shadow: 0 1px 4px rgba(0, 21, 41, .08);
        padding: 0 .2rem;
        img {
            height: .4rem;
        }
    }
    .router {
        min-height: calc(~'100vh - .7rem');
        padding: .15rem .2rem;
        background: #f6f6f6;
    }
    .infos {
        display: flex;
        align-items: center;
        a {
            padding-right: .5rem;
            font-size: 14px;
            color: #555;
            &:hover {
                color: #03a9f4;
            }
        }
        .active {
            a {
                color: #03a9f4;
            }
        }
    }
</style>

<template>
    <div id="index" class="main">
        <header class="header">
            <a href=""><img src="../assets/logo.png" alt="logo"></a>
            <ul class="infos">
                <li :class="{'active': this.$route.path == '/list'}">
                    <a @click="() => this.$router.push('/list')">爬虫列表</a>
                </li>
                <li :class="{'active': this.$route.path == '/test'}">
                    <a @click="() => this.$router.push('/test')">抓取测试</a>
                </li>
                <li>
                    <Input v-model="search" icon="ios-search" placeholder="查询..." style="width: 200px"></Input>
                </li>
            </ul>
        </header>
        <section class="router">
            <router-view></router-view>
        </section>
    </div>
</template>

<script>
    import menu from '../config/menu'
    export default {
        name: 'index',
        data() {
            return {
                menus: menu,
                search: '',
                active: '',
                open: []
            }
        },
        created() {
            // 初始化侧边栏展示
            this.active = this.$route.path
            this.open = this.getOpen(this.active)
        },
        methods: {
            // 页面跳转
            route(name) {
                this.$router.push(name)
            },
            // 展开侧边栏二级菜单
            getOpen(key) {
                let res = []
                menu.map((m, i) => {
                    if (m.children) {
                        m.children.map((c) => {
                            if (c.route == key) {
                                res.push(i + '-' + i)
                            }
                        })
                    }
                })
                return res
            }
        }
    }
</script>

