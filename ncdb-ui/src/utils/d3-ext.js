/**
 * D3拓展方法封装处理
 * 陈宁 2025/02/12
*/

import html2canvas from 'html2canvas';

/**
 * 初始化一个渐变动画
 * parent：父级元素
 * gradientColors：定义一个颜色数组，用于构建渐变效果，包含了几种不同的颜色值（以十六进制颜色代码表示）,如["#108ee9","white","#108ee9"]
 * animateTime：动画持续时间，字符串类型，如"4s"
*/
export function initLinearGradientAnimate(parent, gradientColors, animateTime) {
  const animateObj = {
    linearGradientL2R: null,
    linearGradientR2L: null,
    linearGradientT2B: null,
    linearGradientB2T: null,
  };
  // 在父级元素中添加一个<defs>元素，通常用于定义可复用的图形元素、滤镜、渐变等
  const defs = parent.append("defs");
  // 定义一个函数，用于获取渐变颜色节点的偏移量，根据传入的当前数据项（d）和索引（i）来计算，
  // 偏移量是根据颜色数组长度来平均分配的，即每个颜色节点在渐变中的位置占比
  const getStopOffset = (d, i) => i / (gradientColors.length - 1);
  // 定义一个函数，用于获取渐变颜色节点的颜色值，这里直接返回传入的数据（也就是颜色数组中的颜色值）
  const getStopColor = (d) => d;

  // 在<defs>元素内添加一个从左到右（left to right）的线性渐变元素，并设置其ID为'linear-gradient-l2r'
  const linearGradientL2R = defs
    .append("linearGradient")
    .attr("id", "linear-gradient-l2r");
  // 定义从左到右线性渐变的属性，设置起始点坐标（x1,y1）和结束点坐标（x2,y2），以及扩展方法（spreadMethod）为'reflect'，
  // 这里表示在渐变范围外重复渐变效果来填充
  linearGradientL2R
    .attr("x1", "0%")
    .attr("y1", "0%")
    .attr("x2", "100%")
    .attr("y2", "0%")
    .attr("spreadMethod", "reflect");
  // 为从左到右的线性渐变元素添加颜色节点（stop），通过遍历gradientColors数组，
  // 为每个颜色创建一个<stop>元素，并设置其偏移量（offset）和颜色（stop-color）属性
  linearGradientL2R
    .selectAll(".stop")
    .data(gradientColors)
    .enter()
    .append("stop")
    .attr("offset", getStopOffset)
    .attr("stop-color", getStopColor);
  // 为从左到右的线性渐变元素添加一个动画效果，用于改变其x1坐标，
  // 让x1坐标的值从0%变化到200%，动画持续时间为2秒，并且无限重复（indefinite）
  linearGradientL2R
    .append("animate")
    .attr("attributeName", "x1")
    .attr("values", "0%;200%")
    .attr("dur", animateTime) // 动画持续时间默认是2秒
    .attr("repeatCount", "indefinite"); // 动画重复次数为无限次
  // 为从左到右的线性渐变元素添加一个动画效果，用于改变其x2坐标，
  // 让x2坐标的值从100%变化到300%，动画持续时间为2秒，并且无限重复
  linearGradientL2R
    .append("animate")
    .attr("attributeName", "x2")
    .attr("values", "100%;300%")
    .attr("dur", animateTime) // 动画持续时间默认是2秒
    .attr("repeatCount", "indefinite"); // 动画重复次数为无限次

  // 在<defs>元素内添加一个从右到左（right to left）的线性渐变元素，并设置其ID为'linear-gradient-r2l'
  const linearGradientR2L = defs
    .append("linearGradient")
    .attr("id", "linear-gradient-r2l");
  // 定义从右到左线性渐变的属性，设置起始点坐标（x1,y1）和结束点坐标（x2,y2），以及扩展方法（spreadMethod）为'reflect'
  linearGradientR2L
    .attr("x1", "0%")
    .attr("y1", "0%")
    .attr("x2", "100%")
    .attr("y2", "0%")
    .attr("spreadMethod", "reflect");
  // 为从右到左的线性渐变元素添加颜色节点（stop），通过遍历gradientColors数组，
  // 为每个颜色创建一个<stop>元素，并设置其偏移量（offset）和颜色（stop-color）属性
  linearGradientR2L
    .selectAll(".stop")
    .data(gradientColors)
    .enter()
    .append("stop")
    .attr("offset", getStopOffset)
    .attr("stop-color", getStopColor);
  // 为从右到左的线性渐变元素添加一个动画效果，用于改变其x1坐标，
  // 让x1坐标的值从100%变化到0%，动画持续时间为2秒，并且无限重复
  linearGradientR2L
    .append("animate")
    .attr("attributeName", "x1")
    .attr("values", "100%;0%")
    .attr("dur", animateTime) // 动画持续时间默认是2秒
    .attr("repeatCount", "indefinite"); // 动画重复次数为无限次
  // 为从右到左的线性渐变元素添加一个动画效果，用于改变其x2坐标，
  // 让x2坐标的值从200%变化到100%，动画持续时间为2秒，并且无限重复
  linearGradientR2L
    .append("animate")
    .attr("attributeName", "x2")
    .attr("values", "200%;100%")
    .attr("dur", animateTime) // 动画持续时间默认是2秒
    .attr("repeatCount", "indefinite"); // 动画重复次数为无限次

  // 在<defs>元素内添加一个从上到下（top to bottom）的线性渐变元素，并设置其ID为'linear-gradient-t2b'
  const linearGradientT2B = defs
    .append("linearGradient")
    .attr("id", "linear-gradient-t2b");
  linearGradientT2B
    .attr("x1", "0%")
    .attr("y1", "0%")
    .attr("x2", "0%")
    .attr("y2", "100%")
    .attr("spreadMethod", "reflect");
  // 为从上到下的线性渐变元素添加颜色节点（stop），通过遍历gradientColors数组，
  // 为每个颜色创建一个<stop>元素，并设置其偏移量（offset）和颜色（stop-color）属性
  linearGradientT2B
    .selectAll(".stop")
    .data(gradientColors)
    .enter()
    .append("stop")
    .attr("offset", getStopOffset)
    .attr("stop-color", getStopColor);
  // 为从上到下的线性渐变元素添加一个动画效果，用于改变其y1坐标，
  // 让y1坐标的值从0%变化到100%，动画持续时间为2秒，并且无限重复
  linearGradientT2B
    .append("animate")
    .attr("attributeName", "y1")
    .attr("values", "0%;100%")
    .attr("dur", animateTime) // 动画持续时间默认是2秒
    .attr("repeatCount", "indefinite"); // 动画重复次数为无限次
  // 为从上到下的线性渐变元素添加一个动画效果，用于改变其y2坐标，
  // 让y2坐标的值从100%变化到200%，动画持续时间为2秒，并且无限重复
  linearGradientT2B
    .append("animate")
    .attr("attributeName", "y2")
    .attr("values", "100%;200%")
    .attr("dur", animateTime) // 动画持续时间默认是2秒
    .attr("repeatCount", "indefinite"); // 动画重复次数为无限次

  // 在<defs>元素内添加一个从下到上（bottom to top）的线性渐变元素，并设置其ID为'linear-gradient-b2t'
  const linearGradientB2T = defs
    .append("linearGradient")
    .attr("id", "linear-gradient-b2t");
  linearGradientB2T
    .attr("x1", "0%")
    .attr("y1", "0%")
    .attr("x2", "0%")
    .attr("y2", "100%")
    .attr("spreadMethod", "reflect");
  // 为从下到上的线性渐变元素添加颜色节点（stop），通过遍历gradientColors数组，
  // 为每个颜色创建一个<stop>元素，并设置其偏移量（offset）和颜色（stop-color）属性
  linearGradientB2T
    .selectAll(".stop")
    .data(gradientColors)
    .enter()
    .append("stop")
    .attr("offset", getStopOffset)
    .attr("stop-color", getStopColor);
  // 为从下到上的线性渐变元素添加一个动画效果，用于改变其y1坐标，
  // 让y1坐标的值从100%变化到0%，动画持续时间为2秒，并且无限重复
  linearGradientB2T
    .append("animate")
    .attr("attributeName", "y1")
    .attr("values", "100%;0%")
    .attr("dur", animateTime) // 动画持续时间默认是2秒
    .attr("repeatCount", "indefinite"); // 动画重复次数为无限次
  // 为从下到上的线性渐变元素添加一个动画效果，用于改变其y2坐标，
  // 让y2坐标的值从200%变化到100%，动画持续时间为2秒，并且无限重复
  linearGradientB2T
    .append("animate")
    .attr("attributeName", "y2")
    .attr("values", "200%;100%")
    .attr("dur", animateTime) // 动画持续时间默认是2秒
    .attr("repeatCount", "indefinite"); // 动画重复次数为无限次

  animateObj.linearGradientL2R = linearGradientL2R;
  animateObj.linearGradientR2L = linearGradientR2L;
  animateObj.linearGradientT2B = linearGradientT2B;
  animateObj.linearGradientB2T = linearGradientB2T;

  return animateObj;
}

//#region 弧线路径
// 根据起点、终点、半径比例生成弧线路径
export function arcPath(a, b, r_frac) {
  // a: 起点坐标
  // b: 终点坐标
  // r_frac: 弧的半径，以起点和终点之间距离的一半为基准的分数形式表示，1表示生成半圆弧形，该数值越接近0，弧形越扁平，0是无效值
  let c = getCenter(a, b, r_frac);
  let r = dist(c, a);

  let aAngle = Math.atan2(a[1] - c[1], a[0] - c[0]);
  let bAngle = Math.atan2(b[1] - c[1], b[0] - c[0]);

  // 如果起点角度大于终点角度，将终点角度加上2 * Math.PI，确保角度范围正确
  if (aAngle > bAngle) {
    bAngle += 2 * Math.PI;
  }

  // 构建路径字符串，先移动到起始点
  let pathStr = `M${a[0]},${a[1]}`;
  // 添加弧线绘制命令，格式为 A rx ry x-axis-rotation large-arc-flag sweep-flag x y
  // 这里简化一些参数，假设x轴旋转角度为0，对于半圆等情况，根据角度差值判断large-arc-flag和sweep-flag
  let largeArcFlag = Math.abs(bAngle - aAngle) >= Math.PI ? 1 : 0;
  let sweepFlag = bAngle - aAngle >= 0 ? 1 : 0;
  pathStr += `A${r} ${r} 0 ${largeArcFlag} ${sweepFlag} ${b[0]} ${b[1]}`;

  return pathStr;
}

// 根据起点、终点和半径比例计算弧的圆心坐标
function getCenter(a, b, frac) {
  let c = getP3(a, b, frac);
  let b1 = yIntercept(a, b);
  let b2 = yIntercept(a, c);
  let m1 = inverseSlope(a, b);
  let m2 = inverseSlope(a, c);

  // 找到两条垂直平分线的交点
  // 也就是求解m1 * x + b2 = m2 * x + b2 中的x
  let x = (b2 - b1) / (m1 - m2);
  // 将x代入其中一个线性方程来获取y值
  let y = m1 * x + b1;

  return [x, y];
}

// 计算两点之间的距离
function dist(a, b) {
  return Math.sqrt(Math.pow(a[0] - b[0], 2) + Math.pow(a[1] - b[1], 2));
}

// 计算两点的中点坐标
function midpoint(a, b) {
  return [(a[0] + b[0]) / 2, (a[1] + b[1]) / 2];
}

// 根据起点、终点和半径比例获取第三个点（用于后续计算圆心等相关操作）
function getP3(a, b, frac) {
  let mid = midpoint(a, b);
  let m = inverseSlope(a, b);
  // 检查B点是否在A点下方
  let bLower = b[1] < a[1] ? -1 : 1;

  // 从中点沿着斜率方向的距离：介于0和两点间距离的一半之间
  let d = 0.5 * dist(a, b) * frac;

  let x = d / Math.sqrt(1 + Math.pow(m, 2));
  let y = m * x;
  return [bLower * x + mid[0], bLower * y + mid[1]];
  // return [mid[0] + d, mid[1] - (d * (b[0] - a[0])) / (b[1] - a[1])];
}

// 计算从点A到点B连线的斜率
function slope(a, b) {
  return (b[1] - a[1]) / (b[0] - a[0]);
}

// 计算从点A到点B连线的垂直平分线的斜率（即原斜率的负倒数）
function inverseSlope(a, b) {
  return -1 * (1 / slope(a, b));
}

// 计算从点A到点B连线的垂直平分线的y轴截距
function yIntercept(a, b) {
  let m = inverseSlope(a, b);
  let p = midpoint(a, b);
  let x = p[0];
  let y = p[1];
  return y - m * x;
}
//#endregion

// 将对象下载为csv文件
export function downloadCSV(data) {
  // 提取对象的键作为 CSV 文件的表头
  const headers = Object.keys(data[0]);
  // 将表头用逗号连接成字符串
  let csvContent = headers.join(',') + '\n';
  // 遍历对象数组，将每个对象的值转换为 CSV 行
  data.forEach(item => {
    const row = headers.map(header => item[header]).join(',');
    csvContent += row + '\n';
  });
  // 创建 Blob 对象，指定 MIME 类型为 text/csv
  const blob = new Blob([csvContent], { type: 'text/csv' });
  // 创建一个临时的 URL 来表示 Blob 对象
  const url = URL.createObjectURL(blob);
  // 创建一个 a 标签用于下载
  const a = document.createElement('a');
  a.href = url;
  // 设置下载的文件名
  a.download = 'data.csv';
  // 模拟点击 a 标签触发下载
  a.click();
  // 释放临时 URL
  URL.revokeObjectURL(url);
}

// 导出为图片
export async function downloadImage(obj, type, name) {
  if (obj) {
    const canvas = await html2canvas(obj);
    const imageData = canvas.toDataURL(type);
    const link = document.createElement('a');
    link.href = imageData;
    link.download = name;
    link.click();
  }
};