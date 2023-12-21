import request from "../utils/request";
import {BASE_URL} from "./index";

export const getItemList = (query: any) => {
    return request({
        url: `${BASE_URL}/api/rest/canteen/item/list`,
        method: 'POST',
        data: JSON.stringify(query)
    });
};

export const deleteItem = (itemId: number) => {
    return request({
        url: `${BASE_URL}/api/rest/canteen/item/${itemId}`,
        method: 'DELETE'
    });
}