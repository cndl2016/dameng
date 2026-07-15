import request from '@/utils/request'

// 查询采集列表
export function getAgentConfigPage(data) {
  return request({
    url: '/base/agent/getAgentConfigPage',
    method: 'get',
    params: data
  })
}

// 修改采集频率
export function updateAgentConfig(data) {
  return request({
    url: '/base/agent/update',
    method: 'post',
    data: data
  })
}
