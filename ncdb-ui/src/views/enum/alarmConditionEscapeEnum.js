// 告警阈值条件判断转义枚举
import {createEnum} from '@/utils/enum'

let alarmConditionEscapeEnum = createEnum({
  EQ: ['eq', '= '],
  NE: ['ne', '!= '],
  GT: ['gt', '> '],
  GE: ['ge', '>= '],
  LT: ['lt', '< '],
  LE: ['le', '<= '],
  AND: ['and', '∩ '],
  OR: ['or', '∪ ']
});
export default alarmConditionEscapeEnum;
