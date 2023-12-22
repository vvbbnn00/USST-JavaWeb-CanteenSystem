"use client"
import NavigationBar from "@/components/NavigationBar";
import {Button, Skeleton} from "@nextui-org/react";
import store from "@/utils/store";
import {useEffect, useState} from "react";
import PublishForm from "@/components/community/PublishForm";
import {fetchApi} from "@/utils/api";
import CanteenRankList from "@/components/canteen/CanteenRankList";
import {ClockCircleFilled, FireFilled} from "@ant-design/icons";
import TopicList from "@/components/community/TopicList";
import LatestVote from "@/components/vote/LatestVote";


export default function Home() {
    const [user, setUser] = useState();
    const [sort, setSort] = useState("createdAt");


    useEffect(() => {
        const meUserData = store.session.get("meUserData");
        if (meUserData) {
            setUser(meUserData);
            return;
        }
        fetchApi("/api/rest/user/me").then(userData => {
            const {data} = userData;
            store.session.set("meUserData", data);
            setUser(data);
        });
    }, []);

    const toggleSort = () => {
        setSort(sort === "compValue" ? "createdAt" : "compValue");
    }

    return (
        <>
            <NavigationBar/>
            <main className={"max-w-[1200px] mx-auto"}>
                <div className="flex justify-center p-5 flex-row gap-5 flex-wrap lg:flex-nowrap">
                    <div className={"flex flex-col items-center lg:w-2/3 w-full"}>
                        <PublishForm user={user}/>
                        <div className={"bg-white mt-5 w-full rounded shadow-xl pb-5"}>
                            <div
                                className={"flex flex-row items-center justify-between p-5 font-bold text-lg text-gray-700 pb-5 border-b border-gray-200"}>
                                <div>社区话题</div>
                                <div>
                                    <Button
                                        size={"sm"}
                                        startContent={sort === "compValue" ? <FireFilled/> : <ClockCircleFilled/>}
                                        onClick={toggleSort}
                                    >
                                        {
                                            sort === "compValue" ? "按热度" : "按时间"
                                        }
                                    </Button>
                                </div>
                            </div>
                            <TopicList sort={sort}/>
                        </div>
                    </div>
                    <div className={"grow flex flex-col gap-3"}>
                        <LatestVote/>
                        <CanteenRankList/>

                    </div>
                </div>
            </main>
        </>
    )
}
