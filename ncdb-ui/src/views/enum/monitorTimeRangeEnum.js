// 告警阈值类型枚举
import {createEnum} from '@/utils/enum'

let monitorTimeRangeEnum = createEnum({
  ONE_HOUR_AGO: ["0", '1小时前'],
  ONE_DAY_AGO: ["1", '最近1天'],
  THREE_DAY_AGO: ["2", '最近3天'],
  SEVEN_DAY_AGO: ["3", '最近7天']
});
export default monitorTimeRangeEnum;
