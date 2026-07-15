import {createEnum} from '@/utils/enum'
let safeStatusTypeEnum = createEnum({
  SAFE: ["0",'安全'],
  LOW_RISK: ["1",'低风险'],
  HIGH_RISK: ["2",'高风险']
});

export default safeStatusTypeEnum;