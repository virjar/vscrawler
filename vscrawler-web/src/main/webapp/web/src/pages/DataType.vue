<template>
<section>
	<div class="content-title">
		<p>
			<Input v-model="query" @on-enter="getDataTypeList" placeholder="关键词查询..." style="width:300px;"></Input>
			<Button type="primary" icon="search" @click="getDataTypeList"></Button>
		</p>
		<p>
			<Button type="primary" icon="android-refresh" @click="getDataTypeList">刷新</Button>
			<Button type="primary" icon="android-add" @click="() => operate('add')">新增</Button>
		</p>
	</div>
	<c-table :columns="columns" :datas="datas"></c-table>
	<Modal v-model="modal.show" :width="modal.editable ? 900 : 750">
		<p slot="header">
			<span>{{modal.type}}</span>
		</p>
		<Form ref="modal" :model="modal.data" :rules="modal.rule" :show-message="modal.editable" :label-width="110">
			<div style="display: flex">
				<div style="flex: 1.185">
					<FormItem label="数据类型名称" prop="name">
						<Input v-model="modal.data.name" :readonly="!modal.editable" placeholder="请输入数据类型名称"></Input>
					</FormItem>
					<FormItem label="是否缓存" prop="cache">
						<RadioGroup v-model="modal.data.cache">
							<Radio label="1" :disabled="!modal.editable">
								<span>不缓存</span>
							</Radio>
							<Radio label="0" :disabled="!modal.editable">
								<span>设定缓存时间</span>
							</Radio>
							<Radio label="-1" :disabled="!modal.editable">
								<span>永久缓存</span>
							</Radio>
						</RadioGroup>
					</FormItem>
					<FormItem label="标准输出key" prop="outPutKey">
						<Input v-model="modal.data.outPutKey" :readonly="!modal.editable" placeholder="请输入标准输出key"></Input>
					</FormItem>
				</div>
				<div style="flex: 1">
					<FormItem label="数据类型描述" prop="description">
						<Input v-model="modal.data.description" :readonly="!modal.editable" placeholder="请输入数据类型描述"></Input>
					</FormItem>
					<FormItem label="缓存时间(天)" v-show="modal.data.cache == '0'" prop="cacheTime">
						<InputNumber v-model="modal.data.cacheTime" :readonly="!modal.editable" placeholder="缓存天数" :min="1" :max="30"></InputNumber>
					</FormItem>
				</div>
			</div>
			<FormItem label="标准输出格式(json)" prop="outPutJson">
				<div style="display: flex">
					<Input style="flex: 1" v-model="modal.data.outPutJson" :readonly="!modal.editable" type="textarea" @on-keydown.tab="defaultTab" :autosize="{minRows: 18 ,maxRows: 18}" placeholder="请输入标准格式"></Input>
					<span style="flex: 0.1" v-if="modal.editable"></span>
					<div style="flex: 1; height: 385px" v-if="modal.editable">
						<vue-json-editor v-model="json" :show-btns="false"></vue-json-editor>
					</div>
				</div>
			</FormItem>
		</Form>
		<p slot="footer" v-show="modal.editable" style="text-align: center">
			<Button type="primary" @click="handleSubmit('modal')">保存</Button>
			<Button type="ghost" @click="handleReset('modal')" style="margin-left: 8px">清空</Button>
		</p>
	</Modal>
	<Modal v-model="dataSource.show" width="600">
		<p slot="header">
			<span>数据源详情</span>
		</p>
		<c-table :columns="dataSource.columns" :datas="dataSource.datas"></c-table>
		<p slot="footer" style="text-align: center"></p>
	</Modal>
</section>
</template>

<script>
import cTable from '../components/table'
import vueJsonEditor from 'vue-json-editor'
import jsonlint from 'jsonlint'
export default {
	components: {
		cTable,
		vueJsonEditor
	},
	data() {
		// 初始化json验证
		let validateJson = (rule, value, callback) => {
			try {
				let result = jsonlint.parse(value)
				callback()
			} catch (err) {
				let message = err.message.split('\n')
				callback(new Error(message[0] + message[1]))
			}
		}
		let formatJson = (rule, value, callback) => {
			try {
				this.modal.data.outPutJson = JSON.stringify(JSON.parse(this.modal.data.outPutJson), null, '\t')
				callback()
			} catch (err) {}
		}
		return {
			query: '',
			columns: [{
				title: '数据类型名',
				width: 200,
				key: 'name'
			}, {
				title: '数据类型描述',
				width: 200,
				key: 'description'
			}, {
				title: '缓存时间(天)',
				width: 120,
				render: (h, params) => {
					let cache
					if (params.row.cache == 0) {
						cache = this.filters.emptyTo(params.row.cache_time)
					} else if (params.row.cache == -1) {
						cache = '永久缓存'
					} else {
						cache = '不缓存'
					}
					return h('span', cache)
				}
			}, {
				title: '关联数据源',
				width: 140,
				render: (h, params) => {
					return h('a', {
						on: {
							click: () => {
								this.getDataSourceList(params.row)
							}
						}
					}, params.row.dataSourceCount)
				}
			}, {
				title: '操作',
				width: 250,
				render: (h, params) => {
					return h('span', [
						h('a', {
							on: {
								click: () => {
									this.operate('show', params.row)
								}
							}
						}, '查看'),
						h('span', ' | '),
						h('a', {
							on: {
								click: () => {
									this.operate('edit', params.row)
								}
							}
						}, '修改'),
						h('span', ' | '),
						h('Poptip', {
							on: {
								'on-ok': () => {
									this.doDelete(params.row)
								},
								'on-cancel': () => {
									return
								}
							},
							props: {
								confirm: true,
								title: '确认删除'
							}
						}, [h('a', '删除')])
					])
				}
			}],
			datas: [],
			// 操作模态框
			modal: {
				show: false,
				editable: true,
				type: '',
				data: {
					name: '',
					description: '',
					cache: '',
					cacheTime: 1,
					outPutJson: '',
					outPutKey: '',
					outPutJson: ''
				},
				rule: {
					name: [{
						required: true,
						message: '请输入数据类型名称',
						trigger: 'blur'
					}],
					description: [{
						required: true,
						message: '请输入数据类型描述',
						trigger: 'blur'
					}],
					cache: [{
						required: true,
						message: '请选择缓存信息',
						trigger: 'change'
					}],
					outPutKey: [{
						required: true,
						message: '请输入标准输出key',
						trigger: 'blur'
					}],
					outPutJson: [{
						required: true,
						message: '请输入标准 json 格式',
						trigger: 'blur'
					}, {
						validator: validateJson,
						trigger: "change"
					}, {
						validator: formatJson,
						trigger: "blur"
					}]
				}
			},
			dataSource: {
				show: false,
				columns: [{
					title: '数据源名称',
					width: 150,
					key: 'name'
				}, {
					title: '权重',
					width: 100,
					key: 'weight'
				}, {
					title: '默认数据源',
					width: 150,
					key: 'name'
				}],
				datas: []
			},
		}
	},
	mounted() {
		this.getDataTypeList()
	},
	// 计算属性
	// 管理json对应关系，json和jsonedit联动
	computed: {
		json: {
			get: function() {
				try {
					let result = jsonlint.parse(this.modal.data.outPutJson)
					// console.log(result)
					return result
				} catch (err) {
					return {}
				}
			},
			set: function(v) {
				// console.log(v)
				this.modal.data.outPutJson = JSON.stringify(v, null, "\t")
			}
		}
	},
	methods: {
		// 获取数据类型列表
		getDataTypeList() {
			this.service.getDataTypeList(this.query).then((response) => {
				if (response.response_code == '00') {
					this.datas = response.content
				} else {
					this.$Notice.error({
						title: '发生错误',
						desc: response.response_msg
					})
				}
			})
		},
		// 通过数据类型id获取关联数据源
		getDataSourceList(params) {
			this.dataSource.show = true
			this.service.getDataTypeSourceList(params.id).then((response) => {
				if (response.response_code == '00') {
					this.dataSource.datas = response.content
				} else {
					this.$Notice.error({
						title: '发生错误',
						desc: response.response_msg
					})
				}
			})
		},
		// textarea可输入tab
		defaultTab(e) {
			// 阻止默认切换元素的行为
			if (e && e.preventDefault) {
				e.preventDefault()
			} else {
				window.event.returnValue = false
			}
			var s = e.target.selectionStart;
			e.target.value = e.target.value.substring(0, e.target.selectionStart) + "\t" + e.target.value.substring(e.target.selectionEnd);
			e.target.selectionEnd = s + 1;
		},
		// 操作，打开操作模态框
		operate(type, params) {
			let types = {
				add: '新增数据类型',
				show: '数据类型详情',
				edit: '编辑数据类型'
			}
			this.modal.editable = type != 'show'
			this.modal.type = types[type]
			this.modal.show = true
			if (type == 'add') {
				this.modal.data = {
					name: '',
					description: '',
					cache: '',
					cacheTime: 1,
					outPutJson: '',
					outPutKey: ''
				}
				this.handleReset('modal')
			} else {
				this.service.getDataType(params.id).then((response) => {
					if (response.response_code == '00') {
						let content = response.content
						let data = this.modal.data
						data.id = content.id
						data.name = content.name
						data.description = content.description
						data.cache = '' + content.cache
						data.cacheTime = content.cache_time
						data.outPutKey = content.out_put_key
						try {
							data.outPutJson = JSON.stringify(JSON.parse(content.out_put_json), null, '\t')
						} catch (err) {

						}
					} else {
						this.$Notice.error({
							title: '发生错误',
							desc: response.response_msg
						})
					}
				})
			}
		},
		// 提交保存
		handleSubmit(name) {
			this.$refs[name].validate((valid) => {
				if (valid) {
					let item = this.modal.data
					let data = {
						name: item.name,
						description: item.description,
						cache: item.cache,
						cacheTime: item.cacheTime,
						outPutJson: {},
						outPutKey: item.outPutKey
					}
					data.outPutJson = JSON.stringify(JSON.parse(item.outPutJson))

					if (this.modal.type == '编辑数据类型') {
						data.id = this.modal.data.id
						this.service.postUpdateDataType(data).then((response) => {
							this.modal.show = false
							if (response.response_code == '00') {
								this.$Message.success('修改成功!', 4)
								this.getDataTypeList()
							} else {
								this.$Notice.error({
									title: '发生错误',
									desc: response.response_msg
								})
							}
						})
					} else {
						this.service.postInsertDataType(data).then((response) => {
							this.modal.show = false
							if (response.response_code == '00') {
								this.$Message.success('添加成功!', 4)
								this.getDataTypeList()
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
			})
		},
		// 重置输入
		handleReset(name) {
			this.$refs[name].resetFields();
		},
		// 删除数据类型
		doDelete(params) {
			this.service.getDeleteDataType(params.id).then((response) => {
				if (response.response_code == '00') {
					this.$Message.success('删除成功！', 4)
					this.getDataTypeList()
				} else {
					this.$Notice.error({
						title: '发生错误',
						desc: response.response_msg
					})
				}
			})
		}
	}
}
</script>
