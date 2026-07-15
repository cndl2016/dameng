import {createEnum} from '@/utils/enum'
let responseCodeCheckEnum = createEnum({
  SUCCESS_CODE: [200,'执行成功'],
  FAIL_CODE: [601,'执行失败']
});

export default responseCodeCheckEnum;
