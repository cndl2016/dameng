import { createVNode, render } from "vue";
import customMessage from "@/components/customMessage";

// 创建dom
const div = document.createElement('div');
div.setAttribute('class', 'custom-message-container');
document.body.appendChild(div);

let timer = null;
export default {
  // 消息提示
  message({text, buttonMsg, buttonFunction}) {
    const vnode = createVNode(customMessage, {text, buttonMsg, buttonFunction});
    render(vnode, div);
    clearTimeout(timer);
    timer = setTimeout(() =>{
      render(null, div);
    }, 3500)
  },

  // 关闭消息提示
  closeMessage() {
    render(null, div)
  }
}