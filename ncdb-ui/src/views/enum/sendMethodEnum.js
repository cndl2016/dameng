import {createEnum} from '@/utils/enum'
let sendMethodEnum = createEnum({
  MAIL: ["0",'邮箱'],
  LARK: ["1",'飞书'],
  DING: ["2",'钉钉'],
  WECOM: ["3",'企业微信'],
  SMS: ["4",'短信'],
  WEOA: ["5",'微信公众号'],
});

export default sendMethodEnum;
