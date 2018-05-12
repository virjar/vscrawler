export default [
  {
    route: "/dashboard",
    name: "dashboard",
    icon: "home"
  },
  {
    name: "表单页",
    icon: "ios-copy-outline",
    children: [
      {
        route: "/form/base",
        name: "基础表单",
        icon: "compose"
      },
      {
        route: "/form/step",
        name: "分布表单",
        icon: "printer"
      },
      {
        route: "/form/advance",
        name: "高级表单",
        icon: "map"
      }
    ]
  },
  {
    name: "列表页",
    icon: "ios-list-outline",
    children: [
      {
        route: "/list/query",
        name: "查询表格",
        icon: "ios-photos-outline"
      },
      {
        route: "/list/base",
        name: "标准列表",
        icon: "ios-barcode-outline"
      },
      {
        route: "/list/card",
        name: "卡片列表",
        icon: "ios-crop"
      },
      {
        route: "/list/search",
        name: "搜索列表",
        icon: "ios-ionic-outline"
      }
    ]
  },
  {
    name: "详情页",
    icon: "ios-world-outline",
    children: [
      {
        route: "/detail/base",
        name: "基础详情页",
        icon: "ios-drag"
      },
      {
        route: "/detail/advance",
        name: "高级详情页",
        icon: "ios-grid-view-outline"
      }
    ]
  },
  {
    name: "结果页",
    icon: "clipboard",
    children: [
      {
        route: "/result/success",
        name: "成功",
        icon: "ios-checkmark-outline"
      },
      {
        route: "/result/fail",
        name: "失败",
        icon: "ios-close-outline"
      }
    ]
  },
  {
    name: "异常页",
    icon: "ios-information-outline",
    children: [
      {
        route: "/error/e403",
        name: "403",
        icon: "ios-help-outline"
      },
      {
        route: "/error/e404",
        name: "404",
        icon: "ios-help-outline"
      },
      {
        route: "/error/e500",
        name: "500",
        icon: "ios-help-outline"
      }
    ]
  }
];
