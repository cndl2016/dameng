import request from '@/utils/request'

// 查询告警通知消息记录列表
export function getNoticeMessagePage(data) {
  return request({
    url: '/base/alert/notice/message/list',
    method: 'get',
    params: data
  })
}
