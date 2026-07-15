// cn 2024-10-12
import request from '@/utils/request'

// 查询任务列表
export function listJob(query) {
  return request({
    url: '/job/jobinfo/list',
    method: 'get',
    params: query
  })
}

// 启动任务
export function startJob(data) {
  return request({
    url: '/job/jobinfo/start',
    method: 'put',
    data: data
  })
}

// 停止任务
export function stopJob(data) {
  return request({
    url: '/job/jobinfo/stop',
    method: 'put',
    data: data
  })
}

// 删除任务
export function delJob(jobId) {
  return request({
    url: '/job/jobinfo/' + jobId,
    method: 'delete'
  })
}

// 立刻执行一次
export function triggerJob(data) {
  return request({
    url: '/job/jobinfo/trigger',
    method: 'put',
    data: data
  })
}

// 新增/修改任务
export function addOrUpdateJob(data) {
  return request({
    url: '/job/jobinfo/addOrUpdate',
    method: 'put',
    data: data
  })
}
