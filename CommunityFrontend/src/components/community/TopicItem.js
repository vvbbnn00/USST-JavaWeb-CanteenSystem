"use client";
import Link from "next/link";
import {Avatar, Chip} from "@nextui-org/react";
import {formatDateTimeFromNow} from "@/utils/string";
import ImageLayout from "@/components/community/ImageLayout";
import {CommentIcon} from "@/components/icons/CommentIcon";
import {LikeFilledIcon} from "@/components/icons/LikeFilledIcon";
import {LikeIcon} from "@/components/icons/LikeIcon";
import {fetchApiWithAuth} from "@/utils/api";
import {message} from "antd";
import {useRouter} from "next/navigation";

export default function TopicItem({topic, isHot = false, topicPage = 0, onLikeChange}) {
    const [messageApi, contextHolder] = message.useMessage();
    const router = useRouter();

    const toggleLike = () => {
        const likeStatus = topic?.isLiked;
        const requestMethod = likeStatus ? "DELETE" : "POST";
        fetchApiWithAuth(`/api/rest/topic/${topic?.topicId}/like`, {
            method: requestMethod
        }).catch(e => {
            messageApi.open({
                type: 'error',
                content: e.message || "操作失败"
            });
        }).finally(() => {
            if (onLikeChange) {
                onLikeChange(topicPage, topic?.topicId, !likeStatus);
            }
        });
    }

    return <div
        className={"flex flex-row hover:bg-gray-50 transition-background pb-3"}>
        {contextHolder}
        <div className={"p-5 w-fit flex items-start justify-center"}>
            <Link href={"/user/" + topic?.user?.userId}>
                <Avatar src={topic?.user?.avatar} size={"lg"}/>
            </Link>
        </div>
        <div className={"flex-col pt-5 pr-5 grow"}>
            <div className={"text-gray-500 text-base font-normal"}>
                {isHot && <Chip color="warning" variant={"flat"} className={"mr-1.5"}>热门</Chip>}
                <span
                    className={"break-all font-bold text-gray-700 text-lg"}>{topic?.title}</span>
                <Link href={"/user/" + topic?.user?.userId}>
                                            <span className={"text-gray-500 pl-1.5 font-normal"}>
                                                @{topic?.user?.username}
                                            </span>
                </Link>
                <span className={"pl-0.5 pr-0.5 text-lg"}>·</span>
                <span className={"text-gray-500 font-normal"}>
                                            {formatDateTimeFromNow(topic?.createdAt)}
                                        </span>
            </div>
            <div className={"text-gray-700 text-base pt-1 line-clamp-5"}>
                <pre className={"font-sans"}>
                    {topic?.content}
                </pre>
            </div>
            <div className={"pt-2"}>
                <Link href={"/topic/" + topic?.topicId}>
                                            <span
                                                className={"text-blue-500 text-base font-normal cursor-pointer hover:underline"}>
                                                查看详情
                                            </span>
                </Link>
            </div>
            {
                topic?.imageInfoList?.length > 0 &&
                <div className={"pt-2 pb-3"}>
                    <ImageLayout
                        imageInfoList={[...topic?.imageInfoList]}/>
                </div>
            }
            <div className={"flex flex-row items-center gap-5"}>
                <div
                    className={"text-gray-500 flex items-center gap-2 hover:bg-gray-100 rounded-full p-2 cursor-pointer transition-all active:bg-gray-200"}
                    onClick={() => {
                        router.push("/topic/" + topic?.topicId);
                    }}
                >
                    <CommentIcon/>
                    <span className={"select-none"}>
                        {topic?.commentCount}
                    </span>
                </div>
                <div
                    className={
                        "flex items-center gap-2 hover:bg-red-100 rounded-full p-2 cursor-pointer transition-all active:bg-red-200"
                        + (topic?.isLiked ? " text-red-500" : " text-gray-500")
                    }
                    onClick={toggleLike}
                >
                    {topic?.isLiked ? <LikeFilledIcon/> : <LikeIcon/>}
                    <span className={"select-none"}>{topic?.likeCount}</span>
                </div>
            </div>
        </div>
    </div>
}
