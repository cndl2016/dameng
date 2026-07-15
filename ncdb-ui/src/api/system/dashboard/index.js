import request from '@/utils/request'

// 首页查询告警信息
export function listAlarm(data) {
  return request({
    url: '/system/dashboard/alarmLog',
    method: 'post',
    data: data
  })
}


// 首页获取设备top信息展示
export function getDeviceTopChart() {
  return request({
    url: '/system/dashboard/getDeviceTopChart',
    method: 'get'
  })
}
