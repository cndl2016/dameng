import {createEnum} from '@/utils/enum'
let deviceConnEnum = createEnum({
  SUCCESS_CONN: ['0','正常'],
  FAIL_CONN: ['1','异常'],
});

export default deviceConnEnum;
