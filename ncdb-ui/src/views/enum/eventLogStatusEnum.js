import {createEnum} from '@/utils/enum'
let eventLogStatusEnum = createEnum({
  SUCCESS: ['0','成功'],
  FAIL: ['1','失败'],
});

export default eventLogStatusEnum;
