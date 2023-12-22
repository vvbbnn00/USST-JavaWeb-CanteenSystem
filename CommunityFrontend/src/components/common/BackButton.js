"use client";
import {ArrowLeftOutlined} from "@ant-design/icons";
import {useRouter} from "next/navigation";

export default function BackButton() {
    const router = useRouter();
    return <div
        className={"rounded-full w-10 h-10 m-5 items-center justify-center flex text-lg hover:bg-gray-100 active:bg-gray-200 transition-background cursor-pointer"}
        onClick={() => router.back()}
    >
        <ArrowLeftOutlined/>
    </div>
}
