"use client";
import {Image, Button, Textarea, Input} from "@nextui-org/react";
import {useRef, useState} from "react";
import {CloseOutlined} from "@ant-design/icons";
import {message} from 'antd';
import {fetchApiWithAuth} from "@/utils/api";
import {useFormStatus} from "react-dom";
import {useRouter} from 'next/navigation'
import EmojiButton from "@/components/common/EmojiButton";
import {ImageIcon} from "@/components/icons/ImageIcon";

const selectFile = (maxFileCount) => {
    const SUPPORT_FILE_TYPES = ["image/png", "image/jpeg", "image/gif", "image/webp"];
    const fileInput = document.createElement("input");
    fileInput.type = "file";
    fileInput.multiple = true;
    fileInput.accept = SUPPORT_FILE_TYPES.join(",");
    fileInput.click();
    return new Promise((resolve, reject) => {
        fileInput.onchange = (e) => {
            const files = e.target.files;
            if (files.length > maxFileCount) {
                reject("文件数量超过上限");
            }
            console.log(files);
            resolve(Array.from(files));
        }
    })
}


const getUploadUrl = () => {
    return fetchApiWithAuth("/api/rest/image/upload", {
        method: "POST"
    });
}

const SubmitButton = () => {
    const {pending} = useFormStatus();

    return <Button
        color={"primary"}
        className={"w-full rounded-full"}
        isLoading={pending}
        type={"submit"}
    >发布</Button>
}

export default function PublishForm({user}) {
    const [files, setFiles] = useState([]);
    const [messageApi, contextHolder] = message.useMessage();
    const [tmpFiles, setTmpFiles] = useState([]);
    const router = useRouter();
    const contentRef = useRef(null);
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
            const result = await fetchApiWithAuth("/api/rest/topic", {
                method: 'POST',
                body: JSON.stringify({
                    title, content, images
                })
            });
            messageApi.open({
                type: 'success',
                content: '发布成功',
                duration: 2
            });
            setTmpFiles([]);
            router.push(`/topic/${result?.data?.topicId}`);
        } catch (err) {
            messageApi.open({
                type: 'error',
                content: err.message || '发布失败'
            })
        }

        return {
            success: true
        }
    }


    const addFiles = async () => {
        try {
            const files = await selectFile(9);
            setFiles(files);
        } catch (e) {
            messageApi.open({
                type: "error",
                content: e
            })
        }
    }

    return (
        <div className={"bg-white rounded-md p-5 w-full shadow-xl"}>
            {contextHolder}
            <div className={"flex flex-row"}>
                <div className={"mr-5 mt-2 rounded-full overflow-hidden h-[56px] w-[56px] bg-gray-100"}>
                    <Image src={user?.avatar} width={"56px"} height={"56px"} className={"rounded-full"}/>
                </div>
                <div className={"grow"}>
                    <form action={doPublish}>
                        <div className={"mb-3"}>
                            <Input
                                name={"title"}
                                placeholder={"取一个标题吧"}
                                className={"w-full mb-2"}
                                size={"sm"}
                                isRequired={true}
                            />
                            <Textarea
                                name={"content"}
                                className={"w-full"}
                                placeholder={"有什么新鲜事想告诉大家？"}
                                isRequired={true}
                                ref={contentRef}
                                onChange={(e) => {
                                    setContent(e.target.value);
                                }}
                                value={content}
                            />
                        </div>
                        <div className={"flex flex-row justify-between pl-1 pr-1"}>
                            <div className={"flex gap-1"}>
                                <EmojiButton inputEmoji={inputEmoji}/>

                                <div
                                    className={"hover:bg-blue-100 rounded-full p-2 text-blue-700 cursor-pointer transition-all"}
                                    onClick={addFiles}
                                >
                                    <ImageIcon/>
                                </div>
                            </div>
                            <div>
                                <SubmitButton/>
                            </div>
                        </div>
                    </form>
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
                </div>
            </div>
        </div>
    )
}
