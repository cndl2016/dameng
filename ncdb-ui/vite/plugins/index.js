import vue from '@vitejs/plugin-vue'

import createAutoImport from './auto-import'
import createSvgIcon from './svg-icon'
import createCompression from './compression'
import createSetupExtend from './setup-extend'
import {viteStaticCopy} from "vite-plugin-static-copy";

export default function createVitePlugins(viteEnv, isBuild = false) {
    const vitePlugins = [vue(),
    viteStaticCopy({
        targets:[
            // {
            //     src: 'resource/js/*',
            //     dest: 'resource/js'
            // }
        ]
    })
    ]
    vitePlugins.push(createAutoImport())
	vitePlugins.push(createSetupExtend())
    vitePlugins.push(createSvgIcon(isBuild))
	isBuild && vitePlugins.push(...createCompression(viteEnv))
    return vitePlugins
}
