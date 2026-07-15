import useUserStore from '@/store/modules/user'

export function checkPermission(value){
  const all_permission = "*:*:*";
  const permissions = useUserStore().permissions;
  if (value && value instanceof Array && value.length > 0) {
    const permissionFlag = value
    return permissions.some(permission => {
      return all_permission === permission || permissionFlag.includes(permission)
    })
  } else {
    return false;
  }
}
