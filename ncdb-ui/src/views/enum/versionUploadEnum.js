import {createEnum} from '@/utils/enum'
let versionCheckEnum = createEnum({
  UPLOAD_FAIL: ['0','上传失败'],
  UPLOAD_ING: ['1','上传中'],
  UPLOAD_SUCCESS: ['2','上传成功'],
});

export default versionCheckEnum;
