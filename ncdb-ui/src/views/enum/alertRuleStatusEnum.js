import {createEnum} from '@/utils/enum'

let alertRuleStatusEnum = createEnum({
  ENABLE: ['yes', '启用'],
  DISABLE: ['no', '停用'],
});
export default alertRuleStatusEnum;
