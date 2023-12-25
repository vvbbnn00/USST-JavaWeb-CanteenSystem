"use client";
import NavigationBar from "@/components/NavigationBar";
import BackButton from "@/components/common/BackButton";
import {message} from "antd";
import {Button, Image, Input, Skeleton, Textarea} from "@nextui-org/react";
import {CloseOutlined} from "@ant-design/icons";
import EmojiButton from "@/components/common/EmojiButton";
import {useEffect, useRef, useState} from "react";
import {ImageIcon} from "@/components/icons/ImageIcon";
import {fetchApiWithAuth} from "@/utils/api";
import {useRouter, useSearchParams} from "next/navigation";
import {getUploadUrl, selectFile} from "@/utils/upload";
import CanteenSelect from "@/app/complaint/CanteenSelect";
import {useFormStatus} from "react-dom";

const SubmitButton = () => {
    const {pending} = useFormStatus();
    return <Button
        color={"primary"}
        className={"mt-2 rounded-full"}
        type={"submit"}
        isLoading={pending}
    >
        提交
    </Button>
}


export default function ComplaintCreatePage({}) {
    const searchParams = useSearchParams()
    const [files, setFiles] = useState([]);
    const [messageApi, contextHolder] = message.useMessage();
    const [tmpFiles, setTmpFiles] = useState([]);
    const router = useRouter();
    const contentRef = useRef(null);
    const [content, setContent] = useState("");
    const [canteenId, setCanteenId] = useState(searchParams.get("canteenId"));

    useEffect(() => {
        const content = searchParams.get("content");
        if (content) {
            setContent(content);
        }
    }, [searchParams]);

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

    const batchUpload = async (uploadFiles) => {
        try {
            const fileKeyList = [];
            for (let file of uploadFiles) {
                const {fileKey, url} = await getUploadUrl();
                fileKeyList.push(fileKey);
                await fetch(url, {
                    method: 'PUT',
                    body: await file.arrayBuffer()
                });
            }
            return fileKeyList;
        } catch (error) {
            console.error(error);
            return null;
        }
    }

    const doPublish = async (formData) => {
        const title = formData.get("title");
        const content = formData.get("content");
        let images = tmpFiles;

        if (!title || !content) {
            messageApi.open({
                type: 'error',
                content: '标题和内容不能为空'
            });
            return {};
        }

        if (!canteenId) {
            messageApi.open({
                type: 'error',
                content: '请选择食堂'
            });
            return {};
        }

        if (files.length > 0 && tmpFiles.length === 0) {
            images = await batchUpload(files);
            if (!images) {
                messageApi.open({
                    type: 'error',
                    content: "上传图片失败"
                });
                return {}
            }
            setTmpFiles(images);
        }

        try {
            const result = await fetchApiWithAuth(`/api/rest/canteen/${canteenId}/complaint`, {
                method: 'POST',
                body: JSON.stringify({
                    title, content, imageList: images
                })
            });
            messageApi.open({
                type: 'success',
                content: '发起投诉成功',
                duration: 2
            });
            setTmpFiles([]);
            router.replace(`/complaint/${result?.data?.complaintId}`);
        } catch (err) {
            messageApi.open({
                type: 'error',
                content: err.message || '请求失败'
            })
        }

        return {
            success: true
        }
    }


    const addFiles = async () => {
        try {
            const files = await selectFile(3);
            setFiles(files);
        } catch (e) {
            messageApi.open({
                type: "error",
                content: e
            })
        }
    }

    return <>
        <NavigationBar/>
        {contextHolder}
        <main className={"flex justify-center flex-col items-center"}>
            <div className={"w-full lg:w-[768px] flex flex-wrap items-start"}>
                <div className={"bg-white m-5 rounded-md shadow-xl overflow-hidden pb-0 w-full"}>
                    <div className={"flex flex-col"}>
                        <div className={"flex flex-row items-center"}>
                            <BackButton/>
                            <div className={"flex-grow"}>
                                <h1 className={"text-lg font-bold truncate line-clamp-1 w-fit"}>
                                    新建投诉
                                </h1>
                            </div>
                        </div>
                        <form className={"p-5 flex flex-col gap-2"} action={doPublish}>
                            <CanteenSelect
                                onChange={(value) => {
                                    setCanteenId(value);
                                }}
                                value={canteenId}
                            />
                            <Input
                                className={"w-full"}
                                placeholder={"请输入投诉标题"}
                                name={"title"}
                                label={"标题"}
                                isRequired={true}/>
                            <Textarea className={"w-full"}
                                      placeholder={"请输入投诉内容"}
                                      name={"content"}
                                      value={content}
                                      onChange={(e) => {
                                          setContent(e.target.value);
                                      }}
                                      isRequired={true}
                                      ref={contentRef}
                                      label={"投诉内容"}
                            />
                            <div className={"flex items-center justify-between"}>
                                <div className={"flex gap-2"}>
                                    <EmojiButton inputEmoji={inputEmoji}/>
                                    <div
                                        className={"hover:bg-blue-100 rounded-full p-2 text-blue-700 cursor-pointer transition-all"}
                                        onClick={addFiles}
                                    >
                                        <ImageIcon/>
                                    </div>
                                </div>
                                <SubmitButton/>
                            </div>
                            <div className={"flex flex-row gap-2 pt-2 flex-wrap"}>
                                {files?.map((file, index) => {
                                    return <div key={index} className={"relative"}>
                                        <Image src={URL.createObjectURL(file)} width={"150px"}
                                               className={"rounded-md object-cover w-[150px] h-[150px]"}/>
                                        <div
                                            className={"flex items-center justify-center absolute text-sm top-0 right-0 text-white rounded-r-md p-1.5 cursor-pointer z-10 bg-[rgba(0,0,0,0.5)] h-[30px] w-[30px]"}
                                            onClick={() => {
                                                setFiles(files.filter((_, i) => i !== index));
                                            }}
                                        >
                                            <CloseOutlined/>
                                        </div>
                                    </div>
                                })}
                            </div>
                        </form>
                    </div>
                </div>
            </div>
        </main>
    </>
}
