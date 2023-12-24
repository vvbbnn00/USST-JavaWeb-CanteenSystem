"use client";
import NavigationBar from "@/components/NavigationBar";
import useSWR from "swr";
import {fetchApiWithAuth} from "@/utils/api";
import {Button, Input, Spinner} from "@nextui-org/react";
import {useFormStatus} from "react-dom";
import {message, Modal} from "antd";
import {useState} from "react";
import BackButton from "@/components/common/BackButton";

const {confirm} = Modal;

const SubmitButton = () => {
    const {pending} = useFormStatus();

    return <Button
        color={"primary"}
        className={"w-full mt-5"}
        type={"submit"}
        isLoading={pending}
    >提交</Button>
}


export default function UserVerify() {
    const [refresh, setRefresh] = useState(0);
    const [messageApi, contextHolder] = message.useMessage();

    const {data, isLoading, error} = useSWR(
        `/api/rest/user/me?${refresh}`,
        (...args) => fetchApiWithAuth(...args).then(r => r.data)
    );

    const doVerify = (formData) => {
        const employeeId = formData.get("employeeId");
        const name = formData.get("name");

        confirm({
            title: '确认提交',
            content: '提交后将无法更改，确认提交认证信息吗？',
            okText: '确定',
            okType: 'danger',
            cancelText: '取消',
            onOk() {
                fetchApiWithAuth(`/api/rest/user/me/verify`, {
                    method: "POST",
                    body: JSON.stringify({employeeId, name})
                }).then(r => {
                    setRefresh(refresh + 1);
                }).catch(e => {
                    messageApi.open({
                        type: 'error',
                        content: e.message || "认证失败"
                    });
                })
            }
        });
    }

    return (<>
        <main className={"min-h-[100vh]"}>
            {contextHolder}
            <NavigationBar/>
            <div className={"flex items-center justify-center"}>
                <div className={"w-full lg:w-[500px] bg-white m-5 rounded-md shadow-xl overflow-hidden"}>
                    <div className={"flex w-full items-center justify-center relative"}>
                        <div className={"h-full flex items-center absolute left-1"}>
                            <BackButton />
                        </div>
                        <h1 className={"text-center font-bold text-gray-900 text-2xl flex items-center p-5"}>用户认证</h1>
                    </div>
                    <p className={"p-5 text-yellow-600 text-sm text bg-yellow-50 m-5 rounded-lg"}>
                        完成用户认证，可以一次性获得<b>20</b>点积分奖励，认证后您的发言权重将会提升，获得更多的关注。<br/>
                        <b>每个用户只能认证一次，认证后不可更改。</b>
                    </p>

                    {isLoading &&
                        <div className={"p-5 text-center"}>
                            <Spinner color={"default"}/>
                        </div>
                    }

                    {data?.isVerified === false &&
                        <form className={"p-5"} action={doVerify}>
                            <Input label={"学号"}
                                   placeholder={"请输入学号"}
                                   className={"mb-3"}
                                   name={"employeeId"}
                                   isRequired={true}/>
                            <Input label={"姓名"}
                                   placeholder={"请输入姓名"}
                                   className={"mb-3"}
                                   name={"name"}
                                   isRequired={true}/>

                            <SubmitButton/>
                        </form>
                    }

                    {data?.isVerified === true &&
                        <div className={"p-5"}>
                            <Input label={"学号"}
                                   placeholder={"请输入学号"}
                                   className={"mb-3"}
                                   name={"employeeId"}
                                   isRequired={true}
                                   disabled={true}
                                   defaultValue={data?.employeeId}/>
                            <Input label={"姓名"}
                                   placeholder={"请输入姓名"}
                                   className={"mb-3"}
                                   name={"name"}
                                   isRequired={true}
                                   disabled={true}
                                   defaultValue={data?.name}/>
                            <div
                                className={"bg-green-50 text-green-500 text-center p-3 pt-2.5 pb-2.5 rounded-lg mt-5"}
                            >
                                已认证
                            </div>
                        </div>
                    }
                </div>
            </div>
        </main>
    </>)
}
