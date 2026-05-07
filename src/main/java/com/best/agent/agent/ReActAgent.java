package com.best.agent.agent;

/**
 *
 */
public abstract class ReActAgent extends BaseAgent {

    /**
     * 思考
     * @return
     */
    public abstract boolean think();

    /**
     * 执行
     * @return
     */
    public abstract String act();

    @Override
    public String step() {
        try {
            Boolean shouldAct = think();
            if (!shouldAct) {
                return "思考完成,不需要执行";
            }
            return act();
        }catch (
                Exception e
        ){
            e.printStackTrace();
            return "步骤执行完成: " + e.getMessage();
        }
    };

}
