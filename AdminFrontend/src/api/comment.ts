import request from "../utils/request";
import {BASE_URL} from "./index";

export const getCommentList = (query: any) => {
    return request({
        url: `${BASE_URL}/api/comment/list`,
        method: 'POST',
        data: JSON.stringify(query)
    });
}