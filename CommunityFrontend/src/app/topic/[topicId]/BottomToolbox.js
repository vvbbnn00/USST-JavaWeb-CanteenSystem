"use client";


import {CommentIcon} from "@/components/icons/CommentIcon";
import {LikeFilledIcon} from "@/components/icons/LikeFilledIcon";
import {LikeIcon} from "@/components/icons/LikeIcon";
import {message} from "antd";
import {fetchApiWithAuth} from "@/utils/api";
import {useEffect, useState} from "react";

export default function BottomToolbox({data}) {
    const [messageApi, contextHolder] = message.useMessage();
    const [likeStatus, setLikeStatus] = useState(data?.isLiked);
    const [likeCount, setLikeCount] = useState(data?.likeCount);

    useEffect(() => {
        setLikeStatus(data?.isLiked);
        setLikeCount(data?.likeCount);
    }, [data?.isLiked, data?.likeCount]);

    const toggleLike = () => {
        const requestMethod = likeStatus ? "DELETE" : "POST";
        fetchApiWithAuth(`/api/rest/topic/${data?.topicId}/like`, {
            method: requestMethod
        }).catch(e => {
            messageApi.open({
                type: 'error',
                content: e.message || "操作失败"
            });
        }).then(r => {
            setLikeStatus(!likeStatus);
            setLikeCount(likeCount + (likeStatus ? -1 : 1));
        })
    }

    return <div
        className={"border-t border-b border-gray-200 flex flex-row justify-between items-center p-2.5 pl-5 pr-5"}>
        {contextHolder}
        <div className={"flex flex-row items-center gap-5"}>
            <div
                className={"text-gray-500 flex items-center gap-2 hover:bg-gray-100 rounded-full p-2 cursor-pointer transition-all active:bg-gray-200"}>
                <CommentIcon/> <span
                className={"select-none"}>{data?.commentCount}</span>
            </div>
            <div
                className={
                    "flex items-center gap-2 hover:bg-red-100 rounded-full p-2 cursor-pointer transition-all active:bg-red-200"
                    + (likeStatus ? " text-red-500" : " text-gray-500")
                }
                onClick={toggleLike}
            >
                {likeStatus ? <LikeFilledIcon/> : <LikeIcon/>}
                <span className={"select-none"}>{likeCount}</span>
            </div>
        </div>
    </div>
}
