// cn 2024-10-12
import request from '@/utils/request'

// 查询任务日志列表
export function listJobLog(query) {
  return request({
    url: '/job/joblog/list',
    method: 'get',
    params: query
  })
}

// 清空任务日志
export function cleanJobLog(data) {
  return request({
    url: '/job/joblog/clearLog',
    method: 'put',
    data: data
  })
}
