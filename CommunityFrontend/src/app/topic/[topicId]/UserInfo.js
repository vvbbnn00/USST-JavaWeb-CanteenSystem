import Link from "next/link";
import {Avatar, Skeleton, Tooltip} from "@nextui-org/react";
import {ClockCircleFilled, VerifiedOutlined} from "@ant-design/icons";
import {calcLevelColor} from "@/utils/level";
import {formatDateTime} from "@/utils/string";

export default function UserInfo({user, isLoading, dateTime}) {
    return <div className={"flex items-center gap-3"}>
        <Link href={"/user/" + user?.userId}>
            <Avatar
                src={user?.avatar}
                size={"lg"}
            />
        </Link>
        <div>
            <div className={"text-base font-bold flex items-center gap-2"}>
                <div className={"text-medium flex items-center"}>
                    {isLoading && <Skeleton className={"h-5 w-[100px] rounded-lg"}/>}
                    {user?.username}
                </div>
                {
                    user?.isVerified &&
                    <div className={"text-green-500 text-xl flex items-center"}>
                        <Tooltip content="认证用户">
                            <VerifiedOutlined/>
                        </Tooltip>
                    </div>
                }
                {
                    (user?.level !== undefined) &&
                    <div
                        className={`bg-${calcLevelColor(user?.level)}-100 text-${calcLevelColor(user?.level)}-500 font-bold w-fit pl-2.5 pr-2.5 pt-0.5 pb-0.5 rounded select-none`}>
                        Lv{user?.level}
                    </div>
                }
            </div>
            <div className={"text-sm text-gray-500 flex gap-1.5 items-center pt-1"}>
                <ClockCircleFilled/>
                {
                    isLoading && <Skeleton className={"h-3 w-[150px] rounded-lg"}/>
                }
                {
                    !isLoading && formatDateTime(dateTime)
                }
            </div>
        </div>
    </div>
}
