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

export const deleteVote = (id: number) => {
    return request({
        url: `${BASE_URL}/api/rest/vote/${id}`,
        method: 'DELETE'
    });
}

export const updateVote = (form: any) => {
    return request({
        url: `${BASE_URL}/api/rest/vote/${form.voteId}`,
        method: 'PUT',
        headers: {
            'Content-Type': 'application/json',
        },
        data: JSON.stringify(form)
    });
}

export const newVote = (createForm: any) => {
    return request({
        url: `${BASE_URL}/api/rest/vote`,
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        data: JSON.stringify(createForm)
    });

}

export const getVoteOptionsList = (voteId: number) => {
    return request({
        url: `${BASE_URL}/api/rest/vote/${voteId}`,
        method: 'GET',
    });
}

export const createVoteOption = (voteId: number, name: string) => {
    return request({
        url: `${BASE_URL}/api/rest/vote/${voteId}/option`,
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        data: JSON.stringify({
            name: name
        })
    });
}

export const deleteVoteOption = (voteId: number, optionId: number) => {
    return request({
        url: `${BASE_URL}/api/rest/vote/${voteId}/option/${optionId}`,
        method: 'DELETE'
    });
}

export const updateVoteOption = (voteId: number, optionId: number, name: string) => {
    return request({
        url: `${BASE_URL}/api/rest/vote/${voteId}/option/${optionId}`,
        method: 'PUT',
        headers: {
            'Content-Type': 'application/json',
        },
        data: JSON.stringify({
            name: name
        })
    });
}