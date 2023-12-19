import request from "../utils/request";
import {BASE_URL} from "./index";

export const getCommentList = () => {
    return request({
        url: `${BASE_URL}/api/rest/topic/list`,
        method: 'POST',
    });
}