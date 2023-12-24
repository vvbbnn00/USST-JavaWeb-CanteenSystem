"use client";
import NavigationBar from "@/components/NavigationBar";
import BackButton from "@/components/common/BackButton";
import {Empty, message, Modal} from "antd";
import {Button, Skeleton} from "@nextui-org/react";
import {
    AlertOutlined,
    CheckCircleOutlined,
    ClockCircleOutlined, ExclamationCircleFilled,
    MessageOutlined,
} from "@ant-design/icons";
import useSWR from "swr";
import {fetchApiWithAuth} from "@/utils/api";
import {formatDateTime, formatDateTimeFromNow} from "@/utils/string";
import ImageLayout from "@/components/community/ImageLayout";
import CommentPanel from "@/app/complaint/[complaintId]/CommentPanel";
import {useEffect, useState} from "react";
import {getMe} from "@/utils/auth";

const {confirm} = Modal;

export default function ComplaintDetailPage({params}) {
    const [refresh, setRefresh] = useState(0);
    const [me, setMe] = useState();
    const {data, error, isLoading} = useSWR(
        `/api/rest/canteen/complaint/${params?.complaintId}?${refresh}`,
        (...args) => fetchApiWithAuth(...args).then(r => r.data)
    );
    const [messageApi, contextHolder] = message.useMessage();

    useEffect(() => {
        getMe().then(userData => {
            setMe(userData);
        });
    }, []);

    const onCloseComplaint = () => {
        confirm({
            title: '关闭投诉',
            icon: <ExclamationCircleFilled/>,
            content: '若您认为投诉的问题已经解决，或者您不再需要投诉，可以点击确认关闭投诉。一旦关闭，该投诉将无法被回复，您确定要关闭该投诉吗？',
            okType: 'danger',
            okText: '确认关闭',
            cancelText: '取消',
            autoFocusButton: 'cancel',
            onOk() {
                fetchApiWithAuth(`/api/rest/canteen/complaint/${params?.complaintId}`, {
                    method: 'PUT',
                    body: JSON.stringify({
                        status: 'finished'
                    })
                }).then(r => {
                    setRefresh(refresh + 1);
                }).catch(e => {
                    messageApi.open({
                        type: 'error',
                        content: e.message || "请求失败"
                    });
                })
            },
        });
    }


    return <>
        <NavigationBar/>
        {contextHolder}
        <main className={"flex justify-center flex-col items-center"}>
            <div className={"w-full lg:w-[768px] flex flex-wrap items-start"}>
                <div className={"bg-white m-5 rounded-md shadow-xl overflow-hidden pb-0 w-full"}>
                    {error && <div>
                        <div className={"flex flex-row items-center"}>
                            <BackButton/>
                            <div className={"flex-grow"}>
                                <h1 className={"text-lg font-bold truncate line-clamp-1 w-fit"}>
                                    出错了
                                </h1>
                            </div>
                        </div>
                        <Empty description={error?.message || "加载失败"} className={"mt-5 pb-10"}/>
                    </div>}
                    {!error && <div className={"flex flex-col"}>
                        <div className={"flex flex-row items-center"}>
                            <BackButton/>
                            <div className={"flex-grow"}>
                                {isLoading && <Skeleton className={"h-6 w-3/5 rounded-lg"}/>}
                                {!isLoading && <h1 className={"text-lg font-bold truncate line-clamp-1 w-fit"}>
                                    {data?.title}
                                </h1>}
                            </div>
                            <div className={"mr-5"}>
                                {data?.status !== 'finished' && me?.userId === data?.createdBy && <Button
                                    startContent={<CheckCircleOutlined/>}
                                    color={"success"}
                                    variant={"flat"}
                                    onClick={onCloseComplaint}
                                >
                                    关闭投诉
                                </Button>}
                            </div>
                        </div>
                    </div>}
                    {!error && <div className={"border-b flex-col flex gap-1 pb-5"}>
                        {isLoading && <Skeleton className={"h-10 w-[100px] rounded-full ml-5"}/>}
                        <div className={"pl-5 pr-5 text-lg font-bold w-fit"}>
                            {data?.status === 'pending' &&
                                <div className={"text-red-500 bg-red-100 p-2 pl-5 pr-5 rounded-full"}>
                                    <AlertOutlined/> 待回复
                                </div>}
                            {data?.status === 'processing' &&
                                <div className={"text-orange-500 bg-orange-100 p-2 pl-5 pr-5 rounded-full"}>
                                    <ClockCircleOutlined/> 处理中
                                </div>}
                            {data?.status === 'replied' &&
                                <div className={"text-green-500 bg-green-100 p-2 pl-5 pr-5 rounded-full"}>
                                    <MessageOutlined/> 已回复
                                </div>}
                            {data?.status === 'finished' &&
                                <div className={"text-gray-500 bg-gray-100 p-2 pl-5 pr-5 rounded-full"}>
                                    <CheckCircleOutlined/> 已关闭
                                </div>}
                        </div>
                        <div className={"flex flex-row items-center gap-2 p-5 pb-0"}>
                            <div className={"flex"}>
                                <span className={"text-gray-700 font-bold w-[80px]"}>最后更新</span>
                            </div>
                            {isLoading && <Skeleton className={"h-6 w-1/5 rounded-lg"}/>}
                            {!isLoading && <div className={"text-gray-500"}>
                                {formatDateTimeFromNow(data?.updatedAt)}
                            </div>}
                        </div>
                        <div className={"flex flex-row items-center gap-2 p-5 pb-0 pt-0"}>
                            <div className={"flex"}>
                                <span className={"text-gray-700 font-bold w-[80px]"}>创建时间</span>
                            </div>
                            {isLoading && <Skeleton className={"h-6 w-2/5 rounded-lg"}/>}
                            {!isLoading && <div className={"text-gray-500"}>
                                {formatDateTime(data?.createdAt)}
                            </div>}
                        </div>
                        <div className={"flex flex-row items-start gap-2 p-5 pt-0 pb-0"}>
                            <div className={"flex"}>
                                <span className={"text-gray-700 font-bold w-[80px]"}>投诉内容</span>
                            </div>
                            {isLoading && <Skeleton className={"h-6 w-4/5 rounded-lg"}/>}
                            {!isLoading && <div className={"text-gray-500"}>
                                <pre className={"font-sans text-wrap break-all"}>
                                    {data?.content}
                                </pre>
                            </div>}
                        </div>
                        {data?.imageInfoList && <div className={"p-5 pt-0"}>
                            <div className={"text-gray-700 font-bold pb-3"}>
                                相关图片
                            </div>
                            <ImageLayout imageInfoList={data?.imageInfoList} maxSlice={9}/>
                        </div>}
                    </div>}
                    {!isLoading && !error && <CommentPanel
                        data={data?.comments}
                        complaintId={data?.complaintId}
                        userId={data?.createdBy}
                        onReplied={() => {
                            setRefresh(refresh + 1);
                        }}/>
                    }
                </div>
            </div>
        </main>
    </>
}
