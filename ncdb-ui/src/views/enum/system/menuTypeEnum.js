//菜单类型枚举 wys 2024/10/31
import {createEnum} from '@/utils/enum'

let menuTypeEnum = createEnum({
  CATALOG: ['M', '目录'],
  MENU: ['C', '菜单'],
  BUTTON: ['F', '按钮'],
});
export default menuTypeEnum;
