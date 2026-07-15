import request from '@/utils/request'

// 查询设备列表  cn 2024/07/10
export function getListDevice(data) {
  return request({
    url: '/device/getListDevice',
    method: 'get',
    params: data
  })
}

// 设备启用禁用状态修改 cn 2024/07/10
export function enableDevice(id, status) {
  const data = {
    id,
    status
  }
  return request({
    url: '/device/enableDevice',
    method: 'put',
    data: data
  })
}

// 删除设备 cn 2024/07/10
export function deleteDeviceById(id) {
  return request({
    url: '/device/' + id,
    method: 'delete'
  })
}

// 新增修改设备 cn 2024/07/10
export function saveOrUpdateDevice(data) {
  return request({
    url: '/device/saveOrUpdateDevice',
    method: 'post',
    data: data
  })
}

// 查询设备监控列表 cn 2024/07/11
export function getMonitorList(data) {
  return request({
    url: '/device/getMonitorList',
    method: 'post',
    data: data
  })
}

// 查询设备监控Echart数据 cn 2024/07/11
export function getMonitorDataForEchart(data) {
  return request({
    url: '/device/getMonitorDataForEchart',
    method: 'post',
    data: data
  })
}

// 测试连接
export function testPing(data) {
  return request({
    url: '/device/testPing',
    method: 'post',
    data: data
  })
}

// 批量保存设备
export function addDevices(data) {
  return request({
    url: '/device/addDevices',
    method: 'post',
    data: data
  })
}

// 获取在线设备ip列表
export function getDeviceIpList() {
  return request({
    url: '/device/getDeviceIpList',
    method: 'get',
  })
}

