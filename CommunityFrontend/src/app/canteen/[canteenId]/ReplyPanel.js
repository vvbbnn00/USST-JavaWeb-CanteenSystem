"use client";
import {Avatar, Button} from "@nextui-org/react";
import {useState} from "react";
import {formatDateTimeFromNow} from "@/utils/string";

export default function ReplyPanel({replies}) {
    const [showReply, setShowReply] = useState(false);

    return <>
        <div
            className={"w-fit text-gray-500 text-base font-normal cursor-pointer hover:text-gray-700"}
            onClick={() => setShowReply(!showReply)}
        >
            回复 ({replies?.length})
        </div>
        {showReply && <div>
            {replies?.map(reply => {
                return <div className={"flex flex-row"} key={reply?.replyId}>
                    <div className={"pl-0 p-5 w-fit flex items-start justify-center"}>
                        <Avatar src={reply?.user?.avatar} size={"lg"}/>
                    </div>
                    <div className={"flex-col pt-5 pr-5 grow"}>
                        <div className={"text-gray-500 text-base font-normal"}>
                            <span
                                className={"break-all font-bold text-gray-700 text-lg"}>{reply?.user?.username}</span>
                            <span className={"pl-0.5 pr-0.5 text-lg"}>·</span>
                            <span className={"text-gray-500 font-normal"}>
                                {formatDateTimeFromNow(reply?.createdAt)}
                            </span>
                        </div>
                        <div className={"text-gray-700 text-base pt-1 line-clamp-5"}>
                            <pre className={"font-sans text-wrap break-all"}>
                                {reply?.content}
                            </pre>
                        </div>
                    </div>
                </div>
            })}
        </div>}
    </>
}
