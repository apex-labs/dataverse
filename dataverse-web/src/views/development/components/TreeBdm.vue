<template>
  <div class="flex fdc">
    <div class="fxbw aic name">
      <h3 class="toe">{{ name }}</h3>
      <a-button v-if="showGroup" class="btn" type="text" @click="actionFn('add_group')"><span class="fs12">添加分组</span></a-button>
    </div>
    <div style="margin:0 8px 8px">
      <a-input-search v-if="showSearch" v-model:value="searchValue" placeholder="Search" allowClear />
    </div>
    <a-tree class="w100 flex1 ovh tree" :ref="(el: any) => setRef(el)" v-model:expandedKeys="expandedKeys"
      v-if="gData && gData.length > 0" v-model:selectedKeys="selectedKeys" :tree-data="gData"
      :field-names="{ children: 'children', title: 'title', key: 'key' }" :height="height"
      :auto-expand-parent="autoExpandParent" :load-data="onLoadData" @expand="onExpand" @select="selectNode">
      <template #switcherIcon="{ switcherCls }">
        <DownOutlined :class="switcherCls" />
      </template>
      <template #title="{ title, expanded, key, isLeaf, root, bdmJobId, dwLayer, engineType, userName }">
        <a-popover placement="rightBottom" overlayClassName="info" v-if="isLeaf" trigger="hover">
          <template #title>
            <div class="fxbw aic">
              <span class="tit">{{ title }}</span>
              <!-- <a-tag color="green">已加入调度</a-tag> -->
            </div>
          </template>
          <template #content>
            <p>引擎: {{ engineType }}</p>
            <p>创建人: {{ userName }}</p>
          </template>
          <div class="flex aic">
            <span class="mr5 flex aic">
              <SvgIcon :name="getEngineType(engineType)" style="color:#2A94F4" />
            </span>
            <div class="toe flex1 mr20">
              <a-input v-if="currentNode.isEdit && bdmJobId === currentNode.bdmJobId" v-model:value="jobName"
                placeholder="请输入" @blur="renameFn" @keypress.enter="(e: any) => e.target.blur()"></a-input>
              <template v-else>
                <span v-if="title.indexOf(searchValue) > -1">
                  {{ title.substr(0, title.indexOf(searchValue)) }}
                  <span class="strong">{{ searchValue }}</span>
                  {{ title.substr(title.indexOf(searchValue) + searchValue.length) }}
                </span>
                <span v-else>{{ title }}</span>
              </template>
            </div>
            <a-dropdown :trigger="['click', 'contextmenu']" placement="bottomRight" v-if="showGroup" @click.stop>
              <span class="h100 flex aic" style="display:block">
                <EllipsisOutlined class="mr10" />
              </span>
              <template #overlay>
                <a-menu @click="(e: any) => actionFn(e.key, key)">
                  <template v-if="isLeaf">
                    <a-menu-item key="rename">重命名</a-menu-item>
                    <!-- <a-menu-item key="delete_task">删除作业</a-menu-item> -->
                    <!-- <a-menu-item key="view_dependencies">查看任务依赖</a-menu-item> -->
                  </template>
                  <template v-else>
                    <a-menu-item key="add_group">添加子分组</a-menu-item>
                    <template v-if="!root">
                      <a-menu-item key="edit_group">编辑分组</a-menu-item>
                      <a-menu-item key="delete_group">删除分组</a-menu-item>
                      <a-menu-divider />
                      <a-menu-item key="add_task">添加作业</a-menu-item>
                    </template>
                  </template>
                </a-menu>
              </template>
            </a-dropdown>
          </div>
        </a-popover>

        <div class="flex aic" v-else>
          <span class="mr5 flex aic">
            <SvgIcon v-if="root" :name="dwLayer === 1 ? 'ODS' : dwLayer === 2 ? 'DW' : 'ADS'" style="color:#2A94F4" />
            <template v-else>
              <FolderOpenFilled v-if="expanded" style="color:#2A94F4" />
              <FolderFilled v-else style="color:#2A94F4" />
            </template>
          </span>
          <div class="toe flex1 mr20">
            <a-input v-if="currentNode.isEdit && bdmJobId === currentNode.bdmJobId" v-model:value="jobName"
              placeholder="请输入" @blur="renameFn" @keypress.enter="(e: any) => e.target.blur()"></a-input>
            <span v-else>{{ title }}</span>
          </div>
          <a-dropdown :trigger="['click', 'contextmenu']" placement="bottomRight" v-if="showGroup" @click.stop>
            <!-- <a-button class="mr10" type="text">
              <EllipsisOutlined />
            </a-button> -->
            <span class="h100 flex aic" style="display:block">
              <EllipsisOutlined class="mr10" />
            </span>
            <template #overlay>
              <a-menu @click="(e: any) => actionFn(e.key, key)">
                <template v-if="isLeaf">
                  <a-menu-item key="rename">重命名</a-menu-item>
                  <!-- <a-menu-item key="delete_task">删除作业</a-menu-item> -->
                  <!-- <a-menu-item key="view_dependencies">查看任务依赖</a-menu-item> -->
                </template>
                <template v-else>
                  <a-menu-item key="add_group">添加子分组</a-menu-item>
                  <template v-if="!root">
                    <a-menu-item key="edit_group">编辑分组</a-menu-item>
                    <a-menu-item key="delete_group">删除分组</a-menu-item>
                    <a-menu-divider />
                    <a-menu-item key="add_task">添加作业</a-menu-item>
                  </template>
                </template>
              </a-menu>
            </template>
          </a-dropdown>
        </div>
      </template>
    </a-tree>
    <Empty v-else />

    <!-- add/edit group -->
    <a-modal v-model:open="open" :title="isEditGroup ? '编辑分组' : '添加分组'" :mask-closable="false"
      :confirm-loading="confirmLoading" @ok="handleOk" @cancel="handleCancel">
      <a-form ref="groupFormRef" :model="groupForm" :rules="rules" v-bind="layout" autocomplete="off"
        :labelCol="{ span: 5 }">
        <a-form-item label="分组名称" name="bdmGroupName">
          <a-input v-model:value="groupForm.bdmGroupName" placeholder="请输入" />
        </a-form-item>
        <a-form-item label="所属数据域" name="dataRegionCode">
          <a-select v-model:value="groupForm.dataRegionCode" placeholder="请选择" :disabled="isEditGroup"
            @change="changeDatasourceType">
            <a-select-option :value="item.dataRegionCode" v-for="(item, i) in regionData" :key="'dt_' + i">{{
        item.regionName }}</a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item label="父类分组" name="parentBdmGroupCode">
          <a-tree-select v-model:value="groupForm.parentBdmGroupCode" show-search style="width: 100%"
            :dropdown-style="{ maxHeight: '400px', overflow: 'auto' }" placeholder="请选择" allow-clear
            :disabled="isEditGroup" tree-default-expand-all :tree-data="groupTreeData" :field-names="{
        children: 'children',
        value: 'bdmGroupCode',
      }" tree-node-filter-prop="bdmGroupName" @select="selectParent">
            <template #title="{ bdmGroupName }">
              {{ bdmGroupName }}
            </template>
          </a-tree-select>
        </a-form-item>
        <!-- <a-form-item label="分组层级" name="groupLevel">
          <a-select v-model:value="groupForm.groupLevel" placeholder="请选择" :disabled="isEditGroup">
            <a-select-option :value="0">0</a-select-option>
            <a-select-option :value="1">1</a-select-option>
            <a-select-option :value="2">2</a-select-option>
          </a-select>
        </a-form-item> -->
      </a-form>
    </a-modal>

    <a-modal v-model:open="isAddJob" title="添加作业" :mask-closable="false" @ok="addJobOk" @cancel="addJobCancel" centered>
      <a-form ref="jobFormRef" :model="jobForm" v-bind="layout" autocomplete="off" :labelCol="{ span: 5 }">
        <a-form-item label="任务名称" name="bdmJobName"
          :rules="[{ required: true, max: 20, message: '请填写长度在20以内的字符', trigger: ['blur', 'change'] }]">
          <a-input v-model:value="jobForm.bdmJobName" placeholder="请输入" />
        </a-form-item>
      </a-form>
    </a-modal>

    <a-modal v-model:open="isDelete" :title="`确定删除${currentNode.bdmGroupName || ''}吗？`" okType="danger"
      :confirm-loading="deleting" @ok="handleDeleteOk">
      <p>删除后无法恢复</p>
    </a-modal>
  </div>
</template>

<script setup lang="ts">
import { ref, toRefs, reactive, computed, inject, watch, onMounted, onBeforeUnmount, nextTick } from 'vue'
import { useRouter } from 'vue-router'
import Empty from '@/components/Empty.vue'
import SvgIcon from '@/components/SvgIcon/index.vue'
import { message } from 'ant-design-vue'
import type { Tree, FormInstance } from 'ant-design-vue';
import type { Rule } from 'ant-design-vue/es/form';
import { treeBdmJob, addBdmJobGroup, editBdmJobGroup, deleteBdmJobGroup, addBdmJob, editBdmJob, checkJobAddSchedule } from '@/api/modules/develoment'
import { cloneDeep } from 'lodash-es'
import { useDvsStore } from '@/stores/modules/user'
const router = useRouter()

type TreeNode = typeof Tree.TreeNode

const regionData: any = inject('regionDataJob')
const props = defineProps({
  name: {
    type: String,
    default: '开发任务'
  },
  showName: {
    type: Boolean,
    default: true
  },
  showSearch: {
    type: Boolean,
    default: false
  },
  showGroup: {
    type: Boolean,
    default: false
  },
  selected: {
    type: Array,
    default: () => ([])
  },
})

const emit = defineEmits(['update:selected', 'add-task', 'remove-task', 'select'])

const dvsStore = useDvsStore()
const { env, dvsCode } = toRefs(dvsStore)

const getEngineType = (engineType: string | null) => {
  if (!engineType) return 'job-spark'
  else return `job-${engineType.toLocaleLowerCase()}`
}
const gData = ref<TreeNode[]>([]);
const autoExpandParent = ref<boolean>(true);
const tree = ref<any>(null)
const setRef = (el: HTMLElement) => {
  if (el) {
    tree.value = el
  }
}
const height = ref<number>(100)
const updateTreeHeight = () => {
  height.value = tree.value?.$el.nextSibling?.offsetHeight || 100
}
// 获取数据空间下数据域列表作为根目录
watch(
  () => regionData,
  () => {
    gData.value = regionData.value.map((e: any) => ({ dataRegionCode: e.dataRegionCode, title: e.regionName, key: 'root_' + e.dataRegionId, root: true, isLeaf: false, bdmGroupCode: null, dwLayer: e.dwLayer, env: e.env }))
  },
  { deep: true, immediate: true }
)
watch(
  () => gData,
  () => {
    nextTick(() => {
      updateTreeHeight()
    })
  },
  { deep: true, immediate: true }
)

onMounted(() => {
  window.addEventListener('resize', updateTreeHeight)
  updateTreeHeight()
})
onBeforeUnmount(() => {
  window.removeEventListener('resize', updateTreeHeight)
})
const expandedKeys = ref<any[]>([])
watch(
  dvsCode,
  () => {
    if (dvsCode.value) {
      expandedKeys.value = []
      autoExpandParent.value = false
      gData.value = []
      nextTick(() => {
        gData.value = regionData.value.map((e: any) => ({ dataRegionCode: e.dataRegionCode, title: e.regionName, key: 'root_' + e.dataRegionId, root: true, isLeaf: false, bdmGroupCode: null, dwLayer: e.dwLayer, env: e.env }))
      })
    }
  },
  { deep: true }
)
const selectedKeys = computed({
  get: () => props.selected,
  set: (val) => {
    emit('update:selected', val)
  }
})
const onExpand = (keys: string[]) => {
  expandedKeys.value = keys;
  autoExpandParent.value = false;
}
const selectNode = (keys: any, e: any) => {
  if (!e) return
  emit('select', e.node)
}

// search
const getParentKey = (
  key: string | number,
  tree: TreeNode[],
): string | number | undefined => {
  let parentKey;
  for (let i = 0; i < tree.length; i++) {
    const node = tree[i];
    if (node.children) {
      if (node.children.some((item: TreeNode) => item.key === key)) {
        parentKey = node.key;
      } else if (getParentKey(key, node.children)) {
        parentKey = getParentKey(key, node.children);
      }
    }
  }
  return parentKey;
};
const getAllNode = (
  tree: TreeNode[],
): any[] => {
  let r = [];
  for (let i = 0; i < tree.length; i++) {
    const node = tree[i];
    r.push(node)
    if (node.children) {
      r.push(...getAllNode(node.children))
    }
  }
  return r;
};
const allNode = computed(() => getAllNode(gData.value))
const searchValue = ref<string>('')
watch(searchValue, value => {
  if (value && value.length > 0) {
    const expandedList = allNode.value.filter((item: TreeNode) => item.title.toLocaleLowerCase().indexOf(value.toLocaleLowerCase()) > -1)
      .map((item: TreeNode) => {
        return getParentKey(item.key, gData.value)
      })
      .filter((item, i, self) => item && self.indexOf(item) === i)
    expandedKeys.value = expandedList
    autoExpandParent.value = true
  }
})

const initData = (nodes: any[], parentKey: any) => {
  return nodes.map(e => {
    e.key = parentKey + '-' + e.bdmJobGroupId
    e.isLeaf = false
    e.title = e.bdmGroupName
    if (e.children)
      e.children = initData(e.children, e.key)
    else e.children = []
    if (e.bdmJobVOList) e.bdmJobVOList.map((o: any) => {
      const obj = cloneDeep(o)
      obj.key = parentKey + '-job-' + o.bdmJobId
      obj.isLeaf = true
      obj.title = o.bdmJobName
      e.children.push(obj)
    })
    return e
  })
}
// 根据数据域获取其下分组与作业
const getBdmGroupList = (dataRegionCode: string) => {
  treeBdmJob({ dataRegionCode }).then((res: any) => {
    const list = res.data
    if (list) {
      const index = gData.value.findIndex((e: any) => e.dataRegionCode === dataRegionCode)
      gData.value[index].children = initData(list, gData.value[index].key)
    }
    setTimeout(() => {
      emit('select', cloneDeep(currentNode))
    }, 20);
  })
}
const onLoadData = (treeNode: TreeNode) => {
  return new Promise<void>(resolve => {
    if (!treeNode.dataRef.root) {
      resolve();
      return;
    }
    const { dataRegionCode } = treeNode.dataRef
    treeBdmJob({ dataRegionCode }).then((res: any) => {
      const list = res.data
      if (list) {
        const index = gData.value.findIndex((e: any) => e.dataRegionCode === dataRegionCode)
        gData.value[index].children = initData(list, treeNode.dataRef.key)
      }
    }).finally(() => {
      resolve();
    })
  });
};
defineExpose({ getBdmGroupList }) // 暴露给父组件调用

// rename
const jobName = ref<string>('')
const renameFn = () => {
  const { bdmGroupCode, bdmJobCode, bdmJobId, dataRegionCode, title } = cloneDeep(currentNode)
  if (title === jobName.value) {
    currentNode.isEdit = false
    return
  }
  const params = {
    bdmGroupCode, bdmJobCode, bdmJobId, bdmJobName: jobName.value, dvsCode: dvsCode.value, dataRegionCode, env: env.value
  }
  editBdmJob(params).then((res: any) => {
    if (res.responseStatus === 200) {
      currentNode.isEdit = false
      getBdmGroupList(dataRegionCode)
    } else {
      message.error(res.errorMsg)
    }
  }).catch(() => {
    currentNode.isEdit = false
  })
}

// add/edit group
interface FormState {
  dataRegionCode: string | null
  bdmGroupCode: string | null
  bdmGroupName: string | null
  bdmJobGroupId: number | null
  groupLevel: number | null
  parentBdmGroupCode: string | null
}
const groupFormRef = ref<InstanceType<typeof FormInstance>>();
const groupForm = reactive<FormState>({
  dataRegionCode: null,
  bdmGroupCode: '',
  bdmJobGroupId: null,
  bdmGroupName: '',
  groupLevel: 1,
  parentBdmGroupCode: null
});
const jobFormRef = ref<InstanceType<typeof FormInstance>>();
const jobForm = reactive({
  bdmJobName: ''
});

const checkName = async (_rule: Rule, value: string) => {
  if (!value) {
    return Promise.reject('请填写分组名称')
  }
  if (value.length <= 20) {
    return Promise.resolve()
  } else {
    return Promise.reject('请填写长度在20以内的字符')
  }
}
const rules: Record<string, Rule[]> = {
  dataRegionCode: [{ required: true, message: '请选择数据源类型', trigger: ['blur', 'change'] }],
  bdmGroupName: [{ required: true, validator: checkName, trigger: ['blur', 'change'] }],
  groupLevel: [{ required: true, message: '请选择分组层级', trigger: ['blur', 'change'] }],
  parentBdmGroupCode: [{ required: false, message: '请选择父类分组', trigger: ['blur', 'change'] }],
};
const layout = {
  labelCol: { span: 4 },
  wrapperCol: { span: 14 },
};
const isAddJob = ref(false)
const open = ref<boolean>(false);
const confirmLoading = ref<boolean>(false);
const handleCancel = () => {
  Object.assign(groupForm, {
    dataRegionCode: null,
    bdmGroupCode: '',
    bdmJobGroupId: null,
    bdmGroupName: '',
    groupLevel: 1,
    parentBdmGroupCode: null
  })
  groupFormRef.value.clearValidate()
  confirmLoading.value = false;
};
const handleOk = () => {
  confirmLoading.value = true;
  const { dataRegionCode } = cloneDeep(groupForm)
  groupFormRef.value.validate().then(() => {
    const action = isEditGroup.value ? editBdmJobGroup : addBdmJobGroup
    action(groupForm).then((res: any) => {
      if (res.responseStatus === 200) {
        open.value = false;
        Object.assign(groupForm, {
          dataRegionCode: null,
          bdmGroupCode: '',
          bdmJobGroupId: null,
          bdmGroupName: '',
          groupLevel: 1,
          parentBdmGroupCode: null
        })
        getBdmGroupList(dataRegionCode)
      } else {
        message.error(res.errorMsg)
      }
    })
  }).catch((err: any) => {
    console.log('err', err);
    confirmLoading.value = false;
  }).finally(() => {
    confirmLoading.value = false;
  })
};
const groupTreeData = ref<any[]>([])
const getGroupTreeData = (data: any[]) => {
  return data.map((o: any) => {
    if (o.children) o.children = getGroupTreeData(o.children)
    o.isLeaf = !o.children || o.children.length === 0
    o.key = o.bdmGroupCode  // treeselect组件key,value必须相同或者只存在一个，也可以删除 delete o.key
    return o
  }).filter(o => o.groupLevel < 3)
}
const changeDatasourceType = (v: any) => {
  const item = allNode.value.find((e: any) => e.dataRegionCode === v)
  groupTreeData.value = item && item.children ? getGroupTreeData(cloneDeep(item.children)) : []
  groupForm.groupLevel = isEditGroup.value ? item.groupLevel : (item.groupLevel + 1) || 1
}
const selectParent = (value: string, node: any) => {
  groupForm.groupLevel = node.groupLevel + 1
}
// when action,current node
const isEditGroup = ref(false)
let currentNode = reactive<any>({})
// file tree action
const showModal = (data?: any) => {
  if (data) {
    const { dataRegionCode, parentBdmGroupCode, bdmGroupCode, groupLevel, bdmGroupName, bdmJobGroupId } = data
    changeDatasourceType(dataRegionCode)
    Object.assign(groupForm, { dataRegionCode, parentBdmGroupCode: bdmGroupCode })
    if (isEditGroup.value) Object.assign(groupForm, { groupLevel: groupLevel || 1, bdmGroupName, bdmJobGroupId, parentBdmGroupCode, bdmGroupCode })
  }
  open.value = true;
}
const actionFn = (type: any, nodeKey?: any) => {
  if (nodeKey) {
    const node = allNode.value.find(e => e.key === nodeKey)
    currentNode = Object.assign(currentNode, { ...node })
  }
  isEditGroup.value = false
  switch (type) {
    case 'add_group':
      if (nodeKey) showModal(currentNode)
      else showModal()
      break;
    case 'edit_group':
      isEditGroup.value = true
      if (nodeKey) showModal(currentNode)
      else showModal()
      break;
    case 'delete_group':
      isDelete.value = true
      break;
    case 'add_task':
      jobForm.bdmJobName = currentNode.bdmGroupName + '作业'
      isAddJob.value = true
      break;
    case 'rename': {
      currentNode.isEdit = true
      jobName.value = currentNode.title + ''
      break;
    }
    case 'delete_task': {
      deleteJobFn(nodeKey)
      break;
    }
    case 'view_dependencies': {
      const href = router.resolve({
        path: '/development/schedule/detail',
        query: {
          jobCode: currentNode.bdmJobCode
        }
      }).href
      window.open(href, "_blank")
      break;
    }

    default:
      break;
  }
}

const addJobOk = () => {
  jobFormRef.value.validate().then(() => {
    if (currentNode) {
      const { bdmGroupCode, dataRegionCode } = currentNode
      const params = {
        bdmGroupCode, bdmJobName: jobForm.bdmJobName, dvsCode: dvsCode.value, dataRegionCode, env: env.value,
        // bdmScriptParam: {
        //   bdmScript: '', env: env.value, engineType: 1, version: 1
        // }
      }
      addBdmJob(params)
        .then((res: any) => {
          if (res.responseStatus === 200) {
            message.success(res.msg)
            getBdmGroupList(dataRegionCode)
          } else {
            message.error(res.errorMsg)
          }
        }).finally(() => { isAddJob.value = false })
      // const task = { ...currentNode, key: currentNode.key + Date.now(), bdmJobId: null, bdmJobName: jobForm.bdmJobName, isLeaf: true }
      // isAddJob.value = false
      // emit('add-task', task)
    }
  })
}
const addJobCancel = () => {
  isAddJob.value = false
}

// 删除分组
const isDelete = ref(false)
const deleting = ref(false)
const handleDeleteOk = () => {
  deleting.value = true
  const { bdmJobGroupId, dataRegionCode } = currentNode
  deleteBdmJobGroup({ bdmJobGroupId }).then((res: any) => {
    if (res.responseStatus === 200) {
      message.success(res.msg)
      getBdmGroupList(dataRegionCode)
    } else {
      message.error(res.errorMsg)
    }
  }).finally(() => {
    deleting.value = false
    isDelete.value = false
  })
}
// 删除作业
const deleteJobFn = (nodeKey: any) => {
  console.log(currentNode);
  const { bdmGroupId, bdmJobId, dataRegionCode, env, bdmJobCode } = currentNode
  const params = { env, jobCode: bdmJobCode }
  checkJobAddSchedule(params).then((res: any) => {
    if (res.responseStatus === 200) {
      if (res.data) {
        message.error('已被加入调度任务，若要删除，请先从调度任务中删除此节点')
      } else {
        const index = allNode.value.findIndex((e: any) => e.key === nodeKey)
        if (index >= 0) allNode.value.splice(index, 1)
      }
    }
  })
}
</script>

<style scoped lang="less">
.name {
  padding: 4px 10px;

  .btn {
    height: 32px;
  }
}

.toolbar {
  height: 40px;
  padding: 12px 6px;

  .btn {
    margin: 0 6px;
    cursor: pointer;
  }
}
</style>

<style>
.info.ant-popover {
  .ant-popover-inner {
    padding: 5px 10px;
  }

  .ant-popover-title {
    padding: 0 0 5px;
    margin-bottom: 0;
    border-bottom: 1px solid var(--border-color);
  }

  .ant-popover-inner-content {
    padding: 5px;
    line-height: 1.8;
  }
}
</style>
