import request from '@/utils/request'

// 通知方式保存
export function addNotice(data) {
  return request({
    url: '/base/alarmMessage/add',
    method: 'post',
    data: data
  })
}

// 获取所有通知方式
export function getAll() {
  return request({
    url: '/base/alarmMessage/getAll',
    method: 'post'
  })
}
