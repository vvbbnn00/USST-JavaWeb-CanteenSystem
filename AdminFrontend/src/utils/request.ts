import axios, {AxiosInstance, AxiosError, AxiosResponse, AxiosRequestConfig} from 'axios';
import codetabs from "md-editor-v3/lib/types/MdEditor/layouts/Content/markdownIt/codetabs";

const service: AxiosInstance = axios.create({
    timeout: 5000,
    headers: {
        'Content-Type': 'application/json',
    },
    withCredentials: true
});

service.interceptors.request.use(
    (config: AxiosRequestConfig) => {
        const token = localStorage.getItem('ms_token');
        if (token) {
            // @ts-ignore
            config.headers['Authorization'] = token;
        }
        return config;
    },
    (error: AxiosError) => {
        console.log(error);
        return Promise.reject();
    }
);

service.interceptors.response.use(
    (response: AxiosResponse) => {
        if (response.status === 200) {
            if (response.data.code !== 200) {
                return Promise.reject();
            }
            return response;
        } else {
            Promise.reject();
        }
    },
    (error: AxiosError) => {
        // @ts-ignore
        if (error?.response?.data?.code === 401) {
            localStorage.removeItem('ms_username');
            localStorage.removeItem('ms_token');
            location.reload();
            return Promise.reject();
        }

        console.log(error);
        return Promise.reject();
    }
);

export default service;
