import request from '@/utils/request'

// 设备节点树查询 cn 2024/07/10
export function getDeviceNodeTree(data) {
  return request({
    url: '/device/deviceNode/getDeviceNodeTree',
    method: 'post',
    data: data
  })
}

// 新增/编辑设备节点 cn 2024/07/10
export function saveOrUpdateDeviceNode(data) {
  return request({
    url: '/device/deviceNode/saveOrUpdateDeviceNode',
    method: 'post',
    data: data
  })
}

// 删除设备节点 cn 2024/07/10
export function deleteDeviceNode(id) {
  return request({
    url: '/device/deviceNode/deleteDeviceNode/' + id,
    method: 'delete'
  })
}

// 设备监控节点树 cn 2024/07/11
export function getDeviceMonitorTree(data) {
  return request({
    url: '/device/deviceNode/getDeviceMonitorTree',
    method: 'post',
    data: data
  })
}
