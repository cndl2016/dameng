--开发环境功能sql

SET IDENTITY_INSERT "SYS_MENU" ON;
INSERT INTO SYS_MENU(MENU_ID,MENU_NAME,PARENT_ID,ORDER_NUM,PATH,COMPONENT,QUERY,IS_FRAME,IS_CACHE,MENU_TYPE,VISIBLE,STATUS,PERMS,ICON,CREATE_BY,CREATE_TIME,UPDATE_BY,UPDATE_TIME,REMARK)
VALUES
    (102, '菜单管理', 1, 30, 'menu', 'system/menu/index', '', 1, 0, 'C', '0', '0', 'system:menu:list', 'tree-table', NULL, NULL, NULL, NULL, ''),
    (105, '字典管理', 1, 60, 'dict', 'system/dict/index', '', 1, 0, 'C', '0', '0', 'system:dict:list', 'dict', NULL, NULL, NULL, NULL, ''),
    (106, '参数设置', 1, 70, 'config', 'system/config/index', '', 1, 0, 'C', '0', '0', 'system:config:list', 'edit', NULL, NULL, NULL, NULL, ''),
    (1014, '菜单新增', 102, 20, '', '', '', 1, 0, 'F', '0', '0', 'system:menu:add', '#', NULL, NULL, NULL, NULL, ''),
    (1015, '菜单修改', 102, 30, '', '', '', 1, 0, 'F', '0', '0', 'system:menu:edit', '#', NULL, NULL, NULL, NULL, ''),
    (1016, '菜单删除', 102, 40, '', '', '', 1, 0, 'F', '0', '0', 'system:menu:remove', '#', NULL, NULL, NULL, NULL, ''),
    (1026, '刷新缓存', 105, 10, '#', '', '', 1, 0, 'F', '0', '0', 'system:dict:refresh', '#', NULL, NULL, NULL, NULL, ''),
    (1027, '字典新增', 105, 20, '#', '', '', 1, 0, 'F', '0', '0', 'system:dict:add', '#', NULL, NULL, NULL, NULL, ''),
    (1028, '字典修改', 105, 30, '#', '', '', 1, 0, 'F', '0', '0', 'system:dict:edit', '#', NULL, NULL, NULL, NULL, ''),
    (1029, '字典删除', 105, 40, '#', '', '', 1, 0, 'F', '0', '0', 'system:dict:remove', '#', NULL, NULL, NULL, NULL, ''),
    (1030, '字典导出', 105, 50, '#', '', '', 1, 0, 'F', '0', '0', 'system:dict:export', '#', NULL, NULL, NULL, NULL, ''),
    (1031, '刷新缓存', 106, 10, '#', '', '', 1, 0, 'F', '0', '0', 'system:config:refresh', '#', NULL, NULL, NULL, NULL, ''),
    (1032, '参数新增', 106, 20, '#', '', '', 1, 0, 'F', '0', '0', 'system:config:add', '#', NULL, NULL, NULL, NULL, ''),
    (1033, '参数修改', 106, 30, '#', '', '', 1, 0, 'F', '0', '0', 'system:config:edit', '#', NULL, NULL, NULL, NULL, ''),
    (1034, '参数删除', 106, 40, '#', '', '', 1, 0, 'F', '0', '0', 'system:config:remove', '#', NULL, NULL, NULL, NULL, ''),
    (1035, '参数导出', 106, 50, '#', '', '', 1, 0, 'F', '0', '0', 'system:config:export', '#', NULL, NULL, NULL, NULL, '');
SET IDENTITY_INSERT "SYS_MENU" OFF;
commit;
