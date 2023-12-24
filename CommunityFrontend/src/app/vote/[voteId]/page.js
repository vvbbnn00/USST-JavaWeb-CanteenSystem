"use client";

import NavigationBar from "@/components/NavigationBar";
import useSWR from "swr";
import {fetchApiWithAuth} from "@/utils/api";
import BackButton from "@/components/common/BackButton";
import {formatDateTime} from "@/utils/string";
import {useEffect, useState} from "react";
import {CheckCircleFilled, CheckCircleOutlined} from "@ant-design/icons";
import {Empty, message} from "antd";
import {Spinner} from "@nextui-org/react";

export default function VoteDetailPage({params}) {
    const [refresh, setRefresh] = useState(0);
    const [messageApi, contextHolder] = message.useMessage();
    const {data, error, isLoading} = useSWR(
        `/api/rest/vote/${params?.voteId}`,
        (...args) => fetchApiWithAuth(...args)
    );

    const {data: userResult} = useSWR(
        `/api/rest/vote/${params?.voteId}/result?${refresh}`,
        (...args) => fetchApiWithAuth(...args).then(r => r.data)
    );

    const [selectedOption, setSelectedOption] = useState();

    useEffect(() => {
        if (userResult) {
            setSelectedOption(userResult?.voteOptionId);
        }
    }, [userResult]);

    const submitVote = () => {
        fetchApiWithAuth(`/api/rest/vote/${params?.voteId}/result/${selectedOption}`, {
            method: 'POST',
        }).then(() => {
            messageApi.open({
                type: 'success',
                content: '投票成功'
            });
        }).catch(err => {
            messageApi.open({
                type: 'error',
                content: err.message || '请求失败'
            })
        }).finally(() => {
            setRefresh(refresh + 1);
        });
    }

    return <>
        <NavigationBar/>
        {contextHolder}
        <main className={"flex justify-center items-center h-screen absolute top-0 w-screen"}>
            <div
                className={"w-full flex-col bg-white rounded-md shadow-xl overflow-hidden pb-0 m-5 max-w-[600px] transition-all"}>
                <div className={"w-full flex items-center border-b"}>
                    <div className={"w-[0px] z-10"}>
                        <BackButton/>
                    </div>
                    <h1 className={"font-bold text-center flex-grow text-lg"}>
                        {isLoading && "加载中..."}
                        {!isLoading && error && "加载失败"}
                        {data?.data?.voteName}
                    </h1>
                </div>
                {isLoading && <div className={"flex flex-col gap-2 w-full p-5 pt-10 pb-10"}>
                    <Spinner color={"default"} className={"m-auto"}/>
                </div>}
                {!isLoading && error && <div className={"flex flex-col gap-2 w-full p-5 pt-10 pb-10"}>
                    <Empty description={error?.message || "加载失败"}/>
                </div>}
                {!isLoading && !error && <div>
                    <div className={"flex flex-col p-5 w-full"}>
                        <div className={"flex flex-col"}>
                            <span className={"font-bold"}>投票描述</span>
                            <span className={"text-gray-500"}>
                            <pre className={"font-sans break-all text-wrap"}>
                                {data?.data?.voteIntro}
                            </pre>
                        </span>
                            <div className={"flex flex-row gap-2 mt-2"}>
                                <span className={"font-bold"}>投票状态</span>
                                <span className={"text-gray-500"}>
                                {data?.data?.isStarted ? "正在投票" : "投票已结束"}
                            </span>
                            </div>
                            <div className={"flex flex-row gap-2"}>
                                <span className={"font-bold"}>投票时间</span>
                                <span className={"text-gray-500"}>
                                {formatDateTime(data?.data?.startTime)} ~ {formatDateTime(data?.data?.endTime)}
                            </span>
                            </div>
                        </div>
                    </div>
                    <div className={"flex flex-col p-5 w-full pt-0 relative"}>
                        <div className={"flex flex-col"}>
                            <div className={"flex flex-col gap-2 mt-2"}>
                                {data?.list?.map((item, index) => {
                                    return <div
                                        className={"flex flex-row gap-2 border rounded-full p-2 border-gray-200 shadow items-center cursor-pointer hover:bg-gray-100 transition-all"}
                                        key={item?.voteOptionId}
                                        onClick={() => {
                                            if (data?.data?.isStarted && !userResult) {
                                                setSelectedOption(item?.voteOptionId);
                                            }
                                        }}
                                    >
                                        <div
                                            className={"flex items-center justify-center rounded-full text-gray-500 text-large w-5 h-5"}>
                                            {
                                                selectedOption === item?.voteOptionId ?
                                                    <CheckCircleFilled/> :
                                                    <CheckCircleOutlined/>
                                            }
                                        </div>
                                        <span className={"text-gray-500 ml-1"}>
                                        {item?.name}
                                    </span>
                                    </div>
                                })}

                                {data?.data?.isStarted &&
                                    <div className={"w-full flex items-center justify-center mt-3"}>
                                        {!userResult &&
                                            <button
                                                className={"bg-black text-white rounded-full w-fit pl-10 pr-10 p-2 text-center cursor-pointer select-none hover:bg-gray-700 transition-all active:bg-gray-900"}
                                                onClick={submitVote}
                                            >
                                                提交
                                            </button>}
                                        {userResult &&
                                            <button
                                                className={"bg-gray-200 text-gray-500 rounded-full w-fit pl-10 pr-10 p-2 text-center select-none cursor-auto"}>
                                                您已投票
                                            </button>}
                                    </div>}
                            </div>
                        </div>
                    </div>
                </div>}
            </div>
        </main>
    </>
}
