"use client";
import {Avatar, Button, Chip, Textarea} from "@nextui-org/react";
import {useEffect, useRef, useState} from "react";
import {getMe} from "@/utils/auth";
import {fetchApiWithAuth} from "@/utils/api";
import Link from "next/link";
import {formatDateTimeFromNow} from "@/utils/string";
import {Empty, message} from "antd";
import {useFormStatus} from "react-dom";
import EmojiButton from "@/components/common/EmojiButton";

const SubmitButton = () => {
    const {pending} = useFormStatus();
    return <Button
        color={"primary"}
        className={"mt-2 rounded-full"}
        type={"submit"}
        isLoading={pending}
    >
        回复
    </Button>
}


export default function CommentPanel({complaintId, data, onReplied, userId}) {
    const [user, setUser] = useState();
    const [messageApi, contextHolder] = message.useMessage();
    const textareaRef = useRef();
    const [commentList, setCommentList] = useState([]);
    const [me, setMe] = useState();

    useEffect(() => {
        getMe().then(userData => {
            setMe(userData);
        });
    }, []);

    useEffect(() => {
        if (data?.length > 0) {
            setCommentList(data?.sort((a, b) => {
                return new Date(b?.createdAt) - new Date(a?.createdAt);
            }));
        }
    }, [data]);

    useEffect(() => {
        getMe().then(userData => {
            setUser(userData);
        });
    }, []);

    async function doCommentTopic(formData) {
        let content = formData.get("content");
        if (!content) {
            messageApi.open({
                type: 'error',
                content: '评论内容不能为空'
            });
            return;
        }
        content = content.trim();
        fetchApiWithAuth(`/api/rest/canteen/complaint/${complaintId}/reply`, {
            method: "POST",
            body: JSON.stringify({content})
        }).then(r => {
            messageApi.open({
                type: 'success',
                content: '评论成功'
            });
            textareaRef.current.reset();
            setContent("");
            onReplied();
        }).catch(e => {
            messageApi.open({
                type: 'error',
                content: e.message || "评论失败"
            });
        });
    }

    const contentRef = useRef();
    const [content, setContent] = useState("");

    const inputEmoji = (emoji) => {
        if (!emoji) return;
        const content = contentRef.current;
        if (!content) return;
        const start = content.selectionStart;
        const end = content.selectionEnd;
        setContent(content.value.substring(0, start) + emoji + content.value.substring(end));
        content.focus();
        content.selectionStart = start + emoji.length;
        content.selectionEnd = start + emoji.length;
    }


    return <div>
        {contextHolder}
        {me?.userId === userId && <>
            <div className={"flex pl-5 pr-5"}>
                <div className={"pl-2 pt-5"}>
                    <Avatar
                        src={user?.avatar}
                        size={"lg"}
                        className={"pointer-events-none select-none"}
                    />
                </div>
                <div className={"flex-grow p-5"}>
                    <form action={doCommentTopic} ref={textareaRef}>
                        <Textarea className={"w-full"}
                                  placeholder={"回复该投诉内容"}
                                  name={"content"}
                                  value={content}
                                  onChange={(e) => {
                                      setContent(e.target.value);
                                  }}
                                  isRequired={true}
                                  ref={contentRef}
                        />
                        <div className={"flex items-center justify-between"}>
                            <EmojiButton inputEmoji={inputEmoji}/>
                            <SubmitButton/>
                        </div>
                    </form>
                </div>
            </div>
            <div className={"border-b border-gray-200"}/>
        </>}
        <div className={"pl-5 pr-5 font-bold pt-5"}>
            投诉追踪
        </div>
        <div className={"p-5 pt-3 flex flex-col gap-3"}>
            {commentList?.length === 0 && <div className={"w-full"}>
                <Empty description={"暂无追踪记录"}/>
            </div>}
            {
                commentList?.map(comment => {
                    return <div className={"flex flex-row gap-5 border-b border-gray-200"} key={comment?.commentId}>
                        <div className={"pl-2 pt-2"}>
                            <Link href={"/user/" + comment?.user?.userId}>
                                <Avatar
                                    src={comment?.user?.avatar}
                                    size={"lg"}
                                />
                            </Link>
                        </div>
                        <div className={"flex-grow pb-5"}>
                            <div className={"text-gray-500 text-base font-normal flex mt-1"}>
                                <Link href={"/user/" + comment?.user?.userId} className={"flex items-center"}>
                                    <span className={"text-gray-500 font-bold flex items-center"}>
                                       @{comment?.user?.username} ({userId === comment?.user?.userId ? "客户" : "食堂管理员"})
                                    </span>
                                </Link>
                                <span className={"pl-0.5 pr-0.5 text-lg flex items-center"}>·</span>
                                <span className={"text-gray-500 font-normal flex items-center"}>
                                    {formatDateTimeFromNow(comment?.createdAt)}
                                </span>
                            </div>
                            <div className={"text-gray-700 text-base line-clamp-5"}>
                                <pre className={"font-sans text-wrap break-all"}>
                                    {comment?.content}
                                </pre>
                            </div>
                        </div>
                    </div>
                })
            }
        </div>
    </div>
}
