package com.weishu.upf.dynamic_proxy_hook.app2.static_proxy;

import com.weishu.upf.dynamic_proxy_hook.app2.Shopping;

/**
 * @author weishu
 * @date 16/1/28
 */
public class ProxyShopping implements Shopping {

    Shopping base;

    ProxyShopping(Shopping base) {
        this.base = base;
    }

    @Override
    public Object[] doShopping(long money) {

        // 先黑点钱(修改输入参数)
        long readCost = (long) (money * 0.5);

        System.out.println(String.format("花了%s块钱", readCost));

        // 帮忙买东西
        Object[] things = base.doShopping(readCost);

        // 偷梁换柱(修改返回值)
        if (things != null && things.length > 1) {
            things[0] = "被掉包的东西!!";
        }

        return things;
    }
}
