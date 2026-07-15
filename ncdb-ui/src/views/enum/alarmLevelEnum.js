import {createEnum} from '@/utils/enum'
let alarmLevelEnum = createEnum({
  NORMAL: ['0','提醒'],
  SERIOUS: ['1','严重'],
});

export default alarmLevelEnum;
