// plugins/echarts.ts
import VueECharts from 'vue-echarts'
import { use } from 'echarts/core'
import { BarChart, PieChart } from 'echarts/charts'
import { TitleComponent, TooltipComponent, LegendComponent, GridComponent } from 'echarts/components'
import { CanvasRenderer } from 'echarts/renderers'

use([BarChart, PieChart, TitleComponent, TooltipComponent, LegendComponent, GridComponent, CanvasRenderer])

export default defineNuxtPlugin((nuxtApp) => {
  // Register globally so <v-chart> doesn't conflict with Vuetify
  nuxtApp.vueApp.component('VueECharts', VueECharts)
})
