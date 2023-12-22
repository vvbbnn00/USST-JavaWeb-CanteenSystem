import {Image, Button, Textarea, Input} from "@nextui-org/react";
import {CameraIcon} from "@/components/icons/CameraIcon";
import {useState} from "react";
import {CloseCircleOutlined, CloseOutlined} from "@ant-design/icons";


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


export default function PublishForm({user}) {
    const [files, setFiles] = useState([]);

    const addFiles = async () => {
        try {
            const files = await selectFile(9);
            setFiles(files);
        } catch (e) {
            console.log(e);
        }
    }

    return (
        <div className={"bg-white rounded-md p-5 w-full shadow-xl"}>
            <div className={"flex flex-row"}>
                <div className={"mr-2 mt-2 rounded-full overflow-hidden h-[56px] w-[56px] bg-gray-100"}>
                    <Image src={user?.avatar} width={"56px"} height={"56px"} className={"rounded-full"}/>
                </div>
                <div className={"grow"}>
                    <div className={"mb-3"}>
                        <Input
                            name={"title"}
                            placeholder={"取一个标题吧"}
                            className={"w-full mb-2"}
                            size={"sm"}
                        />
                        <Textarea
                            name={"content"}
                            className={"w-full"}
                            placeholder={"有什么新鲜事想告诉大家？"}
                        />
                    </div>
                    <div className={"flex flex-row justify-between pl-1 pr-1"}>
                        <div
                            className={"hover:bg-blue-100 rounded-full p-2 text-blue-700 cursor-pointer transition-all"}
                            onClick={addFiles}
                        >
                            <CameraIcon/>
                        </div>
                        <div>
                            <Button
                                color={"primary"}
                                className={"w-full rounded-full"}
                            >发布</Button>
                        </div>
                    </div>
                    <div className={"flex flex-row gap-2 pt-2"}>
                        {files?.map((file, index) => {
                            return <div key={index} className={"relative"}>
                                <Image src={URL.createObjectURL(file)} width={"150px"}
                                       className={"rounded-md object-cover w-[150px] h-[150px]"}/>
                                <div
                                    className={"absolute text-sm top-0 right-0 text-white rounded-r-md p-1.5 cursor-pointer z-10 bg-[rgba(0,0,0,0.5)]"}
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
