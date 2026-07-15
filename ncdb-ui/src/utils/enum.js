export  function createEnum(enumMap){
  const valueMap = {};
  const descMap = {};
  const descForValueMap = {};
  for(const key of Object.keys(enumMap)){
    const [value, desc] = enumMap[key];
    valueMap[key] =value
    descMap[value] = desc
    descForValueMap[desc] = value
  }
  return {
    ...valueMap,
    getDesc(key) {
      return (enumMap[key] && enumMap[key][1]) || '无'
    },
    getDescFromValue(value) {
      return descMap[value] || '';
    },
    getValueFromDesc(desc) {
      return descForValueMap[desc] || '无';
    },
  }
}
