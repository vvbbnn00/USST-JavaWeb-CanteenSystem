import request from "../utils/request";
import {BASE_URL} from "./index";

export const getCanteenList = (query: any) => {
    return request({
        url: `${BASE_URL}/api/rest/canteen/list`,
        method: 'POST',
        data: JSON.stringify(query)
    });
};

export const deleteCanteen = (id: number) => {
    return request({
        url: `${BASE_URL}/api/rest/canteen/${id}`,
        method: 'DELETE'
    });
}

export const updateCanteen = (form: any) => {
    return request({
        url: `${BASE_URL}/api/rest/canteen/${form.canteenId}`,
        method: 'PUT',
        headers: {
            'Content-Type': 'application/json',
        },
        data: JSON.stringify(form)
    });
}

export const newCanteen = (createForm: any) => {
    return request({
        url: `${BASE_URL}/api/rest/canteen`,
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        data: JSON.stringify(createForm)
    });
}