import request from "../utils/request";
import {BASE_URL} from "./index";

export const getTopicList = (query: any) => {
    return request({
        url: `${BASE_URL}/api/rest/topic/list`,
        method: 'POST',
        data: JSON.stringify(query)
    });
}

export const deleteTopic = (topicId: number) => {
    return request({
        url: `${BASE_URL}/api/rest/topic/${topicId}`,
        method: 'DELETE'
    });
}

export const getTopicCommentList = (topicId: number) => {
    return request({
        url: `${BASE_URL}/api/rest/topic/${topicId}/comment`,
        method: 'GET',
    });
}

export const deleteTopicComment = (topicId: number, commentId: number) => {
    return request({
        url: `${BASE_URL}/api/rest/topic/${topicId}/comment/${commentId}`,
        method: 'DELETE'
    });
}