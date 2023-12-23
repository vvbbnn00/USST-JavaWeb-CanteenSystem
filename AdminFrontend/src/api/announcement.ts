import request from "../utils/request";
import {BASE_URL} from "./index";

export const getAnnouncementList = (query: any) => {
    return request({
        url: `${BASE_URL}/api/rest/canteen/announcement/list`,
        method: 'POST',
        data: JSON.stringify(query)
    });
}
