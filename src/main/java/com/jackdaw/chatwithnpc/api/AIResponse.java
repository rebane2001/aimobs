package com.jackdaw.chatwithnpc.api;

import com.jackdaw.chatwithnpc.npc.Actions;

public interface AIResponse {
    /**
     * 获取AI的回复。
     * @param prompt AI的提示信息。
     * @return AI的回复。
     * @throws Exception 如果获取AI回复时出现错误。
     */
    String getAIResponse(String prompt) throws Exception;

    /**
     * 提取AI关于动作的回复。
     * <p>
     * <b>TODO: 在该版本暂不更新。</b>
     * @param response AI的回复。
     * @return 动作回复。
     */
    Actions getActions(String response);
}
