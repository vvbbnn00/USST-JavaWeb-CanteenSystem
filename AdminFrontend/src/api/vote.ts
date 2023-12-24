import request from "../utils/request";
import {BASE_URL} from "./index";

export const getVoteList = (query: any) => {
    if (query.isStarted === null) {
        delete query.isStarted;
    }
    return request({
        url: `${BASE_URL}/api/rest/vote/list`,
        method: 'POST',
        data: JSON.stringify(query)
    });
};