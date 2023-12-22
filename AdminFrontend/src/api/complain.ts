import request from "../utils/request";
import {BASE_URL} from "./index";

export const getComplaintList = (query: any) => {
    if (query.status === '') {
        delete query.status;
    }

    return request({
        url: `${BASE_URL}/api/rest/canteen/complaint/list`,
        method: 'POST',
        data: JSON.stringify(query)
    });
};

export const complaintReply = (complaintId: number, content: string) => {
    return request({
        url: `${BASE_URL}/api/rest/canteen/complaint/${complaintId}/reply`,
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        data: JSON.stringify({
            content: content
        })
    });
}

export const shutComplaint = (form: any) => {
    // @ts-ignore
    return request({
        url: `${BASE_URL}/api/rest/canteen/complaint/${form.complaintId}`,
        method: 'PUT',
        data: JSON.stringify(form)
    });
}

export const getComplaintInfo = (complaintId: number) => {
    return request({
        url: `${BASE_URL}/api/rest/canteen/complaint/${complaintId}`,
        method: 'GET'
    });
}