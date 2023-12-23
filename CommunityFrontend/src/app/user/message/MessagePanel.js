"use client";

import {useEffect, useRef, useState} from "react";
import {fetchApiWithAuth} from "@/utils/api";
import {Avatar, ScrollShadow} from "@nextui-org/react";
import useSWR from "swr";
import {getMe} from "@/utils/auth";
import {formatDateTime} from "@/utils/string";
import {SendIcon} from "@/components/icons/SendIcon";
import EmojiButton from "@/components/common/EmojiButton";


export default function MessagePanel({user}) {
    const [userInfo, setUserInfo] = useState();
    const [me, setMe] = useState();
    const [refresh, setRefresh] = useState(0);
    const {data: recentMessages} = useSWR(
        userInfo ? `/api/rest/user/message/target/${userInfo?.userId}?${refresh}` : null,
        (...args) => fetchApiWithAuth(...args).then(r => r.list),
        {
            refreshInterval: 5000
        }
    )
    const contentRef = useRef();
    const scrollRef = useRef();

    useEffect(() => {
        fetchApiWithAuth(`/api/rest/user/${user?.userId}`).then(r => {
            setUserInfo(r.data);
        });
    }, [user]);

    useEffect(() => {
        getMe().then(r => {
            setMe(r);
        })
    }, []);

    useEffect(() => {
        scrollRef.current && scrollRef.current.scrollTo(0, scrollRef.current.scrollHeight);
    }, [recentMessages]);

    const inputEmoji = (emoji) => {
        // 在光标位置插入图标
        const input = contentRef.current;
        const start = input?.selectionStart;
        const end = input?.selectionEnd;
        const content = input?.value;
        input.value = content.substring(0, start) + emoji + content.substring(end);
        input?.focus();
        input.selectionStart = start + emoji.length;
        input.selectionEnd = start + emoji.length;
    }

    const MyMessage = ({message}) => {
        return <div className={"w-full"}>
            <div className={"flex justify-end"}>
                <div className={"bg-blue-500 text-white rounded-lg p-2.5 rounded-br-none"}>
                    <pre className={"font-sans"}>{message?.content}</pre>
                </div>
                <Avatar src={me?.avatar} alt={me?.username} className={"ml-2"}/>
            </div>
            <div className={"flex justify-end text-gray-500 text-sm mt-0.5"}>
                {formatDateTime(message?.createdAt)}
            </div>
        </div>
    }


    const TargetMessage = ({message}) => {
        return <div className={"w-full"}>
            <div className={"flex"}>
                <Avatar src={userInfo?.avatar} alt={userInfo?.username} className={"mr-2"}/>
                <div className={"bg-gray-100 rounded-lg p-2.5 rounded-bl-none"}>
                    <pre className={"font-sans"}>{message?.content}</pre>
                </div>
            </div>
            <div className={"flex text-gray-500 text-sm mt-0.5"}>
                {formatDateTime(message?.createdAt)}
            </div>
        </div>
    }

    const sendMessage = () => {
        const content = contentRef.current?.value?.trim();
        if (!content) {
            return;
        }
        fetchApiWithAuth(`/api/rest/user/message/target/${userInfo?.userId}`, {
            method: "POST",
            body: JSON.stringify({
                content: content,
            })
        }).then(r => {
            contentRef.current.value = "";
            setRefresh(refresh + 1);
        });
    }

    return <div className={"w-full h-full flex flex-col justify-between"}>
        <div className={"p-4 text-center border-b font-bold text-xl"}>
            {userInfo?.username}
        </div>
        <div className={"flex-grow flex-col p-5 pt-2 overflow-scroll overflow-x-hidden"} ref={scrollRef}>
            {recentMessages?.toReversed()?.map(message => {
                return <div key={message?.messageId} className={"mt-3"}>
                    {(message?.fromUserId === me?.userId) ? <MyMessage message={message}/> :
                        <TargetMessage message={message}/>}
                </div>
            })}
        </div>
        <div className={"border-t"}/>
        <div className={"m-5 mt-0"}>
            <div className={"flex items-center mt-2.5 mb-2.5"}>
                <EmojiButton inputEmoji={inputEmoji}/>
            </div>
            <div className={"flex items-center gap-2 p-5 bg-gray-100  rounded-xl"}>
            <textarea
                className={"flex-grow rounded-lg p-0 bg-gray-100 outline-none h-24 resize-none"}
                ref={contentRef}
                name={"content"}
                placeholder={"创建一条私信"}
            ></textarea>
                <div
                    className={"text-blue-500 rounded-full p-2.5 cursor-pointer hover:bg-blue-100 transition-background active:bg-blue-200 flex items-center justify-center"}
                    onClick={sendMessage}
                >
                    <SendIcon/>
                </div>
            </div>
        </div>
    </div>
}
