//域管理同步任务启用状态枚举 wys 2024/10/31
import {createEnum} from '@/utils/enum'

let syncEnableEnum = createEnum({
  DISABLE: ['0', '关闭'],
  ENABLE: ['1', '开启'],
});
export default syncEnableEnum;
