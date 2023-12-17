import request from "../utils/request";
import {BASE_URL} from "./index";

export const getCanteenList = (query: any) => {
    return request({
        url: `${BASE_URL}/api/rest/canteen/list`,
        method: 'POST',
        data: JSON.stringify(query)
    });
};