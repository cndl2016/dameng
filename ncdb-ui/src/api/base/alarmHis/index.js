import request from '@/utils/request'

  export function getListAlarmHis(data) {
    return request({
      url: '/base/sysMessage/getPageList',
      method: 'get',
      params: data
    })
  }
