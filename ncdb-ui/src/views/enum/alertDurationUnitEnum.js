import {createEnum} from '@/utils/enum'

let alertDurationUnitEnum = createEnum({
  H: ['h', '小时'],
  M: ['m', '分钟'],
  S: ['s', '秒']
});
export default alertDurationUnitEnum;
