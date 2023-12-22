"use client";
import NavigationBar from "@/components/NavigationBar";
import {ExclamationCircleFilled, MoreOutlined} from "@ant-design/icons";
import {useRouter} from "next/navigation";
import {
    Dropdown,
    DropdownItem,
    DropdownMenu,
    DropdownTrigger,
    Skeleton
} from "@nextui-org/react";
import useSWR from "swr";
import {fetchApiWithAuth} from "@/utils/api";
import {useEffect, useState} from "react";
import {getMe} from "@/utils/auth";
import ImageLayout from "@/components/community/ImageLayout";
import BottomToolbox from "@/app/topic/[topicId]/BottomToolbox";
import CommentPanel from "@/app/topic/[topicId]/CommentPanel";
import UserInfo from "@/app/topic/[topicId]/UserInfo";
import {Empty, Modal} from "antd";
import BackButton from "@/components/common/BackButton";

const {confirm} = Modal;

export default function TopicDetailPage({params}) {
    const router = useRouter();
    const {data, isLoading, error} = useSWR(
        `/api/rest/topic/${params?.topicId}`,
        (...args) => fetchApiWithAuth(...args).then(r => r.data)
    );

    const [user, setUser] = useState();

    useEffect(() => {
        getMe().then(userData => {
            setUser(userData);
        });
    }, []);

    const showDeleteConfirm = () => {
        confirm({
            title: '确认删除',
            icon: <ExclamationCircleFilled/>,
            content: '删除后将无法恢复，确认删除该话题吗？',
            okText: '确定',
            okType: 'danger',
            cancelText: '取消',
            onOk() {
                fetchApiWithAuth(`/api/rest/topic/${params?.topicId}`, {
                    method: "DELETE"
                }).then(r => {
                    router.back();
                })
            },
            onCancel() {
            },
        });
    }

    return <>
        <NavigationBar/>
        <main className={"flex justify-center min-h-[100vh]"}>
            <div className={"w-full lg:w-[768px] bg-white m-5 rounded-md shadow-xl overflow-hidden"}>
                {error && <div>
                    <div className={"flex flex-row items-center"}>
                        <BackButton/>
                        <div className={"flex-grow"}>
                            <h1 className={"text-lg font-bold truncate line-clamp-1 w-fit"}>
                                出错了
                            </h1>
                        </div>
                    </div>
                    <Empty description={error?.message || "加载失败"} className={"mt-5"}/>
                </div>}
                {!error && <div className={"flex flex-col"}>
                    <div className={"flex flex-row items-center"}>
                        <BackButton/>
                        <div className={"flex-grow"}>
                            {isLoading && <Skeleton className={"h-10 w-3/5 rounded-lg"}/>}
                            <h1 className={"text-lg font-bold truncate line-clamp-1 w-fit"}>
                                {data?.title}
                            </h1>
                        </div>
                    </div>
                    <div className={"flex justify-between"}>
                        <div className={"pl-5"}>
                            <UserInfo user={data?.user} isLoading={isLoading} dateTime={data?.createdAt}/>
                        </div>
                        <div className={"pr-5"}>
                            {data?.user && <Dropdown>
                                <DropdownTrigger>
                                    <div
                                        className={"text-gray-500 text-2xl hover:bg-blue-100 hover:text-blue-500 rounded-full w-10 h-10 flex items-center justify-center cursor-pointer active:bg-blue-200 transition-background"}>
                                        <MoreOutlined rotate={90}/>
                                    </div>
                                </DropdownTrigger>
                                <DropdownMenu aria-label="Static Actions">
                                    <DropdownItem key="message">私聊该用户</DropdownItem>
                                    {(user?.userId === data?.user?.userId || user?.role === 'admin') &&
                                        <DropdownItem
                                            key="delete" className="text-danger" color="danger"
                                            onClick={showDeleteConfirm}
                                        >
                                            删除
                                        </DropdownItem>
                                    }
                                </DropdownMenu>
                            </Dropdown>}
                        </div>
                    </div>
                    <div className={"p-5"}>
                        <pre className={"text-gray-800 font-sans"}>
                            {isLoading && <div className={"flex gap-2 flex-col"}>
                                <Skeleton className={"h-5 w-[300px] rounded-lg"}/>
                                <Skeleton className={"h-5 w-[300px] rounded-lg"}/>
                                <Skeleton className={"h-5 w-[200px] rounded-lg"}/>
                            </div>}
                            {data?.content}
                        </pre>
                    </div>
                    {(data?.imageInfoList && data?.imageInfoList.length > 0) &&
                        <div className={"pl-5 pr-5 pb-5"}>
                            <ImageLayout imageInfoList={data?.imageInfoList}/>
                        </div>
                    }
                    <BottomToolbox data={data}/>
                    <CommentPanel topicId={params?.topicId}/>
                </div>}
            </div>
        </main>
    </>
}
