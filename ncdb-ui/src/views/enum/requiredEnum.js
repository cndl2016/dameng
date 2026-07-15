// 输入框是否必填枚举
import {createEnum} from '@/utils/enum'

let requiredEnum = createEnum({
  REQUIRED: ['1','必填'],
  NOT_REQUIRED: ['0','非必填'],
});

export default requiredEnum;
