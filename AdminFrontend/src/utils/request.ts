import axios, {AxiosInstance, AxiosError, AxiosResponse, AxiosRequestConfig} from 'axios';
import router from "../router";
import {useRouter} from "vue-router";

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
            const router = useRouter();
            localStorage.removeItem('ms_username');
            localStorage.removeItem('ms_user_id');
            localStorage.removeItem('ms_email');
            localStorage.removeItem('ms_avatar');
            localStorage.removeItem('ms_role');
            localStorage.removeItem('ms_keys');
            localStorage.removeItem('ms_todoList');
            localStorage.removeItem('ms_todoList_date_str');
            router.push('/login');
            return Promise.reject();
        }

        console.log(error);
        return Promise.reject();
    }
);

export default service;
