import request from '@/utils/request'

// 查询告警配置列表
export function getListByObjectId(data) {
  return request({
    url: '/base/alert/config/list',
    method: 'get',
    params: data
  })
}

// 新建告警配置
export function addConfig(data) {
  return request({
    url: '/base/alert/config/add',
    method: 'post',
    data: data
  })
}

// 配置启用禁用状态修改
export function enableConfig(data) {
  return request({
    url: '/base/alert/config/enableConfig',
    method: 'put',
    data: data,
  })
}

// 应用告警配置
export function addApply(data) {
  return request({
    url: '/base/alert/config/apply',
    method: 'post',
    data: data
  })
}

// 添加设备后刷新配置缓存
export function loadAllCacheDevice() {
  return request({
    url: '/base/alert/config/loadDeviceCache',
    method: 'post'
  })
}
