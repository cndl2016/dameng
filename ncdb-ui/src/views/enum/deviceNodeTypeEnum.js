import {createEnum} from '@/utils/enum'
let deviceNodeTypeEnum = createEnum({
  AREA: ['0','地域节点'],
  ROOM: ['1','机房节点'],
});

export default deviceNodeTypeEnum;
