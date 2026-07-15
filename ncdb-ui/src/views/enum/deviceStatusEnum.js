import {createEnum} from '@/utils/enum'
let deviceStatusEnum = createEnum({
  SUCCESS_CONN: ['0','运行中'],
  FAIL_CONN: ['1','未启用'],
});

export default deviceStatusEnum;
