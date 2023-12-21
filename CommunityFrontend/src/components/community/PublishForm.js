import {Image, Button, Textarea} from "@nextui-org/react";
import {CameraIcon} from "@/components/icons/CameraIcon";

export default function PublishForm({user}) {
    return (
        <div className={"bg-white rounded-md p-5 w-full shadow-xl"}>
            <div className={"flex flex-row"}>
                <div className={"mr-2 mt-2 rounded-full overflow-hidden h-[56px] w-[56px] bg-gray-100"}>
                    <Image src={user?.avatar} width={"56px"} height={"56px"} className={"rounded-full"}/>
                </div>
                <div className={"grow"}>
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
