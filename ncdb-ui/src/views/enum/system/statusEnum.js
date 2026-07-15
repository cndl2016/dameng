import {createEnum} from '@/utils/enum'

let statusEnum = createEnum({
  ENABLE: ['0', '启用'],
  DISABLE: ['1', '停用'],
});
export default statusEnum;
