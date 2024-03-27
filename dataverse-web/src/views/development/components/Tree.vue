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
      <template
        #title="{ title, expanded, key, isLeaf, root, groupLevel, etlJobId, dwLayer, datasourceCode, userName, engineType }">
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
              <SvgIcon :name="getSourceType(datasourceCode)" style="color:#2A94F4" />
            </span>
            <div class="toe flex1 mr20">
              <a-input v-if="currentNode.isEdit && etlJobId === currentNode.etlJobId" v-model:value="jobName"
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
                    <a-menu-item key="add_group" v-if="groupLevel < 3">添加子分组</a-menu-item>
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
            <a-input v-if="currentNode.isEdit && etlJobId === currentNode.etlJobId" v-model:value="jobName"
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
                  <a-menu-item key="add_group" v-if="groupLevel < 3">添加子分组</a-menu-item>
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
    <a-modal v-model:open="open" :title="isEditGroup ? '编辑分组' : '添加分组'" :mask-closable="false" centered
      :confirm-loading="confirmLoading" @ok="handleOk" @cancel="handleCancel">
      <a-form ref="groupFormRef" name="custom-validation" :model="groupForm" :rules="rules" v-bind="layout"
        autocomplete="off" :labelCol="{ span: 5 }">
        <a-form-item label="分组名称" name="etlGroupName">
          <a-input v-model:value="groupForm.etlGroupName" placeholder="请输入" />
        </a-form-item>
        <a-form-item label="所属数据域" name="dataRegionCode">
          <a-select v-model:value="groupForm.dataRegionCode" placeholder="请选择" :disabled="isEditGroup"
            @change="changeDatasourceType">
            <a-select-option :value="item.dataRegionCode" v-for="(item, i) in regionData" :key="'dt_' + i">{{
        item.regionName }}</a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item label="父类分组" name="parentEtlGroupCode">
          <a-tree-select v-model:value="groupForm.parentEtlGroupCode" show-search style="width: 100%"
            :dropdown-style="{ maxHeight: '400px', overflow: 'auto' }" placeholder="请选择" allow-clear
            :disabled="isEditGroup" tree-default-expand-all :tree-data="groupTreeData" :field-names="{
        children: 'children',
        value: 'etlGroupCode',
      }" tree-node-filter-prop="etlGroupName" @select="selectParent">
            <template #title="{ etlGroupName }">
              {{ etlGroupName }}
            </template>
          </a-tree-select>
        </a-form-item>
        <!-- <a-form-item label="分组层级" name="groupLevel">
          <a-select v-model:value="groupForm.groupLevel" placeholder="请选择" :disabled="isEditGroup">
            <a-select-option :value="1">1</a-select-option>
            <a-select-option :value="2">2</a-select-option>
            <a-select-option :value="3">3</a-select-option>
          </a-select>
        </a-form-item> -->
      </a-form>
    </a-modal>

    <a-modal v-model:open="isDelete" :title="`确定删除${currentNode.etlGroupName || ''}吗？`" okType="danger"
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
import { treeEtlJob, addEtlJobGroup, editEtlJobGroup, deleteEtlJobGroup, updateEtlJobName, checkJobAddSchedule } from '@/api/modules/develoment'
import { cloneDeep } from 'lodash-es'
import { getIconNameByDatasourceType } from '@/utils/constant'
import { useDvsStore } from '@/stores/modules/user'

const dvsStore = useDvsStore()
const { dvsCode } = toRefs(dvsStore)

const router = useRouter()

const dwLayerDetailEnum: any = [
  {
    "value": 11,
    "desc": "ODS"
  },
  {
    "value": 21,
    "desc": "DWD"
  },
  {
    "value": 22,
    "desc": "DWS"
  },
  {
    "value": 23,
    "desc": "DIM"
  },
  {
    "value": 31,
    "desc": "MASTER"
  },
  {
    "value": 32,
    "desc": "MODEL"
  },
  {
    "value": 33,
    "desc": "LABEL"
  },
  {
    "value": 34,
    "desc": "DM"
  }
]
const getDwLayerDetail = (v: string) => {
  const obj = dwLayerDetailEnum.find((e: any) => e.desc === v)
  return obj?.value || 11
}

type TreeNode = typeof Tree.TreeNode

const sourceData: any = inject('sourceData')
const getSourceType = (sourceCode: string | null) => {
  const source = sourceData.value.find((e: any) => e.datasourceCode === sourceCode)
  const sourceType = source?.datasourceTypeId || 1
  return getIconNameByDatasourceType(sourceType)
}
const regionData: any = inject('regionDataJob')
const props = defineProps({
  name: {
    type: String,
    default: '集成任务'
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
    default: () => []
  }
})

const emit = defineEmits(['update:selected', 'add-task', 'remove-task', 'select'])

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
    gData.value = regionData.value.map((e: any) => ({ dataRegionCode: e.dataRegionCode, title: e.regionName, key: 'root_' + e.dataRegionId, root: true, isLeaf: false, etlGroupCode: null, dwLayer: e.dwLayer, dwLayerDetail: getDwLayerDetail(e.dwLayerDetail), env: e.env, groupLevel: 0 }))
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
        gData.value = regionData.value.map((e: any) => ({ dataRegionCode: e.dataRegionCode, title: e.regionName, key: 'root_' + e.dataRegionId, root: true, isLeaf: false, etlGroupCode: null, dwLayer: e.dwLayer, dwLayerDetail: getDwLayerDetail(e.dwLayerDetail), env: e.env, groupLevel: 0 }))
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
  expandedKeys.value = keys
  autoExpandParent.value = false
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

const initData = (nodes: any[], parent: any) => {
  return nodes.map(e => {
    const { key, dataRegionCode, dwLayer, dwLayerDetail, env } = parent
    e.key = key + '-' + e.etlGroupId
    e.dataRegionCode = dataRegionCode
    e.dwLayer = dwLayer
    e.dwLayerDetail = getDwLayerDetail(dwLayerDetail)
    e.isLeaf = false
    e.title = e.etlGroupName
    e.dataRegionCode = dataRegionCode
    e.env = env
    if (e.children)
      e.children = initData(e.children, e)
    else e.children = []
    if (e.etlJobVOList) e.etlJobVOList.map((o: any) => {
      const obj = cloneDeep(o)
      obj.key = key + '-job-' + o.etlJobId
      obj.isLeaf = true
      obj.title = o.etlJobName
      obj.dataRegionCode = dataRegionCode
      obj.dwLayer = dwLayer
      obj.dwLayerDetail = getDwLayerDetail(dwLayerDetail)
      obj.env = env
      e.children.push(obj)
    })
    return e
  })
}
// 根据数据域获取其下分组与作业
const getEtlGroupList = (dataRegionCode: string, env: number) => {
  treeEtlJob({ dataRegionCode, env }).then((res: any) => {
    const list = res.data.children
    if (list) {
      const index = gData.value.findIndex((e: any) => e.dataRegionCode === dataRegionCode)
      gData.value[index].children = initData(list, gData.value[index])
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
    const { dataRegionCode, env } = treeNode.dataRef
    treeEtlJob({ dataRegionCode, env }).then((res: any) => {
      const list = res.data.children
      if (list) {
        const index = gData.value.findIndex((e: any) => e.dataRegionCode === dataRegionCode)
        gData.value[index].children = initData(list, treeNode.dataRef)
      }
    }).finally(() => {
      resolve();
    })
  });
};
defineExpose({ getEtlGroupList }) // 暴露给父组件调用

// add/edit group
interface FormState {
  env: number | null
  dataRegionCode: string | null
  etlGroupCode: string | null
  etlGroupName: string | null
  etlGroupId: number | null
  groupLevel: number | null
  parentEtlGroupCode: string | null
}
const groupFormRef = ref<InstanceType<typeof FormInstance>>();
const groupForm = reactive<FormState>({
  dataRegionCode: null,
  etlGroupCode: '',
  etlGroupId: null,
  etlGroupName: '',
  groupLevel: 1,
  env: 0,
  parentEtlGroupCode: null
});

// rename
const jobName = ref<string>('')
const renameFn = () => {
  const { dataRegionCode, env, etlJobId, title } = cloneDeep(currentNode)
  if (title === jobName.value) {
    currentNode.isEdit = false
    return
  }
  const params = { etlJobId, etlJobName: jobName.value }
  updateEtlJobName(params).then((res: any) => {
    if (res.responseStatus === 200) {
      currentNode.isEdit = false;
      getEtlGroupList(dataRegionCode, env)
    } else {
      message.error(res.errorMsg)
    }
  })
}

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
  etlGroupName: [{ required: true, validator: checkName, trigger: ['blur', 'change'] }],
  groupLevel: [{ required: true, message: '请选择分组层级', trigger: ['blur', 'change'] }],
  parentEtlGroupCode: [{ required: false, message: '请选择父类分组', trigger: ['blur', 'change'] }],
};
const layout = {
  labelCol: { span: 4 },
  wrapperCol: { span: 14 },
};
const open = ref<boolean>(false);
const confirmLoading = ref<boolean>(false);
const handleCancel = () => {
  Object.assign(groupForm, {
    dataRegionCode: null,
    etlGroupCode: '',
    etlGroupId: null,
    etlGroupName: '',
    groupLevel: 1,
    parentEtlGroupCode: null
  })
  groupFormRef.value.clearValidate()
  confirmLoading.value = false;
};
const handleOk = () => {
  confirmLoading.value = true
  const { dataRegionCode, env } = cloneDeep(groupForm)
  groupFormRef.value.validate().then(() => {
    const action = isEditGroup.value ? editEtlJobGroup : addEtlJobGroup
    action(groupForm).then((res: any) => {
      if (res.responseStatus === 200) {
        open.value = false;
        Object.assign(groupForm, {
          dataRegionCode: null,
          etlGroupCode: '',
          etlGroupId: null,
          etlGroupName: '',
          groupLevel: 1,
          env: 0,
          parentEtlGroupCode: null
        })
        getEtlGroupList(dataRegionCode, env)
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
    o.key = o.etlGroupCode  // treeselect组件key,value必须相同或者只存在一个，也可以删除 delete o.key
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
    const { dataRegionCode, env, parentEtlGroupCode, etlGroupCode, groupLevel, etlGroupName, etlGroupId } = data
    changeDatasourceType(dataRegionCode)
    Object.assign(groupForm, { dataRegionCode, env, parentEtlGroupCode: etlGroupCode, groupLevel: groupLevel + 1 })
    if (isEditGroup.value) Object.assign(groupForm, { groupLevel: groupLevel || 1, etlGroupName, etlGroupId, parentEtlGroupCode, etlGroupCode })
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
      if (currentNode) {
        const task = { ...currentNode, key: currentNode.key + Date.now(), etlJobId: null, etlJobName: currentNode.etlGroupName + '作业', isLeaf: true }
        emit('add-task', task)
      }
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
          jobCode: currentNode.etlJobCode
        }
      }).href
      window.open(href, "_blank")
      break;
    }

    default:
      break;
  }
}

// 删除分组
const isDelete = ref(false)
const deleting = ref(false)
const handleDeleteOk = () => {
  deleting.value = true
  const { etlGroupId, dataRegionCode, env } = currentNode
  deleteEtlJobGroup({ etlJobGroupId: etlGroupId }).then((res: any) => {
    if (res.responseStatus === 200) {
      message.success(res.msg)
      getEtlGroupList(dataRegionCode, env)
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
  const { bdmGroupId, bdmJobId, dataRegionCode, env, etlJobCode } = currentNode
  const params = { env, jobCode: etlJobCode }
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
