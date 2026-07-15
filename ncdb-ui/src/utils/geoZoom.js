// 引用：https://github.com/vasturiano/d3-geo-zoom
import { select as d3Select, pointers as d3Pointers } from 'd3-selection';
import { zoom as d3Zoom } from 'd3-zoom';
import versor from "versor";
import Kapsule from 'kapsule';

// 定义 Kapsule 的配置
export default Kapsule({
  props: {
    // 当 projection 属性发生变化时的处理函数
    projection: {
      onChange(projection, state) {
        // 如果有 projection，则设置 unityScale 为 projection 的 scale，否则为 1
        state.unityScale = projection ? projection.scale() : 1;
      }
    },
    // 设置 scaleExtent 属性的默认值和变化时的处理函数
    scaleExtent: {
      default: [0.1, 1e3],
      // 如果 state.zoom 存在，则更新其 scaleExtent
      onChange(extent, state) { state.zoom && state.zoom.scaleExtent(extent) }
    },
    // 设置 northUp 属性的默认值为 false
    northUp: { default: false },
    // 设置 onMove 属性的默认值为一个空函数
    onMove: { defaultVal: () => {} }
  },

  // 初始化函数，接收节点元素和状态对象
  init(nodeEl, state) {
    // 使用 d3Zoom 创建一个缩放对象，并设置 scaleExtent 和绑定事件
    d3Select(nodeEl).call(state.zoom = d3Zoom()
      .scaleExtent(state.scaleExtent)
      .on('start', zoomStarted)
      .on('zoom', zoomed)
    );

    let v0, r0, q0;

    // 缩放开始事件处理函数
    function zoomStarted(ev) {
      // 如果没有 projection，则直接返回
      if (!state.projection) return;

      // 将投影反转后的坐标转换为 versor 的笛卡尔坐标并存储
      v0 = versor.cartesian(state.projection.invert(getPointerCoords(ev)));
      r0 = state.projection.rotate();
      // 将旋转角度转换为 versor 对象并存储
      q0 = versor(r0);
    }

    // 缩放事件处理函数

    function zoomed(ev) {
      // 如果没有 projection，则直接返回
      if (!state.projection) return;

      // 计算新的缩放比例
      const scale = ev.transform.k * state.unityScale;
      // 更新投影的缩放比例
      state.projection.scale(scale);

      // 计算新的 versor 对象和旋转角度
      const v1 = versor.cartesian(state.projection.rotate(r0).invert(getPointerCoords(ev))),
        q1 = versor.multiply(q0, versor.delta(v0, v1)),
        rotation = versor.rotation(q1);

      // 如果 northUp 为 true，则设置 Z 轴旋转角度为 0
      if (state.northUp) {
        rotation[2] = 0; // Don't rotate on Z axis
      }

      // 更新投影的旋转角度
      state.projection.rotate(rotation);

      // 调用 onMove 回调函数，传递缩放比例和旋转角度
      state.onMove({ scale, rotation });
    }

    // 获取指针坐标的函数
    function getPointerCoords(zoomEv) {
      // 定义一个函数用于计算一组值的平均值
      const avg = vals => vals.reduce((agg, v) => agg + v, 0) / vals.length;

      // 获取指针坐标数组
      const pointers = d3Pointers(zoomEv, nodeEl);

      // 如果有多个指针，则计算它们的中心点坐标
      return (pointers && pointers.length > 1)
        ? [0, 1].map(idx => avg(pointers.map(t => t[idx]))) // calc centroid of all points if multi-touch
        : pointers.length
          ? pointers[0] // single point click
          : [undefined, undefined];
    }
  }
});