<template>
  <a-drawer v-model:open="drawerVisible" title="布局设置" size="300px">
    <!-- 布局切换 -->
    <a-divider class="divider" orientation="center">
      <icon><NotificationOutlined /></icon>
      布局切换
    </a-divider>
    <div class="layout-box mb30">
      <a-tooltip effect="dark" title="纵向" placement="top" :mouse-enter-delay="200">
        <div :class="['layout-item layout-vertical', { 'is-active': layout == 'vertical' }]" @click="setLayout('vertical')">
          <div class="layout-dark"></div>
          <div class="layout-container">
            <div class="layout-light"></div>
            <div class="layout-content"></div>
          </div>
          <icon v-if="layout == 'vertical'">
            <CheckCircleFilled />
          </icon>
        </div>
      </a-tooltip>
      <a-tooltip effect="dark" title="经典" placement="top" :mouse-enter-delay="200">
        <div :class="['layout-item layout-classic', { 'is-active': layout == 'classic' }]" @click="setLayout('classic')">
          <div class="layout-dark"></div>
          <div class="layout-container">
            <div class="layout-light"></div>
            <div class="layout-content"></div>
          </div>
          <icon v-if="layout == 'classic'">
            <CheckCircleFilled />
          </icon>
        </div>
      </a-tooltip>
      <a-tooltip effect="dark" title="横向" placement="top" :mouse-enter-delay="200">
        <div :class="['layout-item layout-transverse', { 'is-active': layout == 'transverse' }]" @click="setLayout('transverse')">
          <div class="layout-dark"></div>
          <div class="layout-content"></div>
          <icon v-if="layout == 'transverse'">
            <CheckCircleFilled />
          </icon>
        </div>
      </a-tooltip>
      <a-tooltip effect="dark" title="分栏" placement="top" :mouse-enter-delay="200">
        <div :class="['layout-item layout-columns', { 'is-active': layout == 'columns' }]" @click="setLayout('columns')">
          <div class="layout-dark"></div>
          <div class="layout-light"></div>
          <div class="layout-content"></div>
          <icon v-if="layout == 'columns'">
            <CheckCircleFilled />
          </icon>
        </div>
      </a-tooltip>
    </div>

    <!-- 全局主题 -->
    <a-divider class="divider" orientation="center">
      <icon><HeartFilled /></icon>
      全局主题
    </a-divider>
    <!-- <div class="theme-item">
      <span>主题颜色</span>
      <el-color-picker v-model="primary" :predefine="colorList" @change="changePrimary" />
    </div> -->
    <div class="theme-item">
      <span>暗黑模式</span>
      <SwitchDark />
    </div>
    <div class="theme-item">
      <span>灰色模式</span>
      <a-switch v-model:checked="isGrey" @change="changeGreyOrWeak('grey', !!$event)" />
    </div>
    <div class="theme-item">
      <span>色弱模式</span>
      <a-switch v-model:checked="isWeak" @change="changeGreyOrWeak('weak', !!$event)" />
    </div>
    <div class="theme-item mb40">
      <span>
        侧边栏反转色
        <a-tooltip effect="dark" title="该属性目前只在纵向、经典布局模式下生效" placement="top">
          <icon><QuestionCircleFilled /></icon>
        </a-tooltip>
      </span>
      <a-switch v-model:checked="asideInverted" :disabled="!['vertical', 'classic'].includes(layout)" @change="setAsideTheme" />
    </div>

    <!-- 界面设置 -->
    <a-divider class="divider" orientation="center">
      <icon><SettingFilled /></icon>
      界面设置
    </a-divider>
    <div class="theme-item">
      <span>折叠菜单</span>
      <a-switch v-model:checked="isCollapse" />
    </div>
    <div class="theme-item">
      <span>面包屑</span>
      <a-switch v-model:checked="breadcrumb" />
    </div>
    <div class="theme-item">
      <span>面包屑图标</span>
      <a-switch v-model:checked="breadcrumbIcon" />
    </div>
    <div class="theme-item">
      <span>标签栏</span>
      <a-switch v-model:checked="tabs" />
    </div>
    <div class="theme-item">
      <span>标签栏图标</span>
      <a-switch v-model:checked="tabsIcon" />
    </div>
    <div class="theme-item">
      <span>页脚</span>
      <a-switch v-model:checked="footer" />
    </div>
  </a-drawer>
</template>

<script setup lang="ts">
import { ref } from "vue";
import { storeToRefs } from "pinia";
import { useTheme } from "@/hooks/useTheme";
import { useGlobalStore } from "@/stores/modules/global";
import { LayoutType } from "@/stores/interface";
// import { DEFAULT_PRIMARY } from "@/config";
import mittBus from "@/utils/mittBus";
import SwitchDark from "@/components/SwitchDark/index.vue";
import Icon, {
  NotificationOutlined,
  CheckCircleFilled,
  HeartFilled,
  QuestionCircleFilled,
  SettingFilled
} from "@ant-design/icons-vue";

const { changeGreyOrWeak, setAsideTheme } = useTheme(); // changePrimary

const globalStore = useGlobalStore();
const { layout, isGrey, isWeak, asideInverted, isCollapse, breadcrumb, breadcrumbIcon, tabs, tabsIcon, footer } =
  storeToRefs(globalStore); // primary

// 预定义主题颜色
// const colorList = [
//   DEFAULT_PRIMARY,
//   "#daa96e",
//   "#0c819f",
//   "#409eff",
//   "#27ae60",
//   "#ff5c93",
//   "#e74c3c",
//   "#fd726d",
//   "#f39c12",
//   "#9b59b6"
// ];

// 设置布局方式
const setLayout = (val: LayoutType) => {
  globalStore.setGlobalState("layout", val);
  setAsideTheme();
};

// 打开主题设置
const drawerVisible = ref(false);
mittBus.on("openThemeDrawer", () => (drawerVisible.value = true));
</script>

<style scoped lang="less">
@import "./index.less";
</style>
