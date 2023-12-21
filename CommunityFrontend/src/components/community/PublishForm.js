import {Avatar, Button, Textarea} from "@nextui-org/react";
import {CameraIcon} from "@/components/icons/CameraIcon";

export default function PublishForm({user}) {
    return (
        <div className={"bg-white rounded-md p-5 w-full shadow-xl"}>
            <div className={"flex flex-row"}>
                <div className={"pr-2 pt-2"}>
                    <Avatar src={user?.avatar} size={"lg"} className={"mb-5"}/>
                </div>
                <div className={"w-full"}>
                    <div className={"mb-3"}>
                        <Textarea
                            className={"w-full"}
                            placeholder={"有什么新鲜事想告诉大家？"}
                        />
                    </div>
                    <div className={"flex flex-row justify-between pl-1 pr-1"}>
                        <div
                            className={"hover:bg-blue-100 rounded-full p-2 text-blue-700 cursor-pointer transition-all"}>
                            <CameraIcon/>
                        </div>
                        <div>
                            <Button
                                color={"primary"}
                                className={"w-full rounded-full"}
                            >发布</Button>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    )
}
