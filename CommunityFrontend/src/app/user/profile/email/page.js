"use client";
import NavigationBar from "@/components/NavigationBar";
import useSWR from "swr";
import {fetchApiWithAuth} from "@/utils/api";
import {Button, Input, Spinner} from "@nextui-org/react";
import {useFormStatus} from "react-dom";
import {message} from "antd";
import {useState} from "react";
import BackButton from "@/components/common/BackButton";
import Link from "next/link";

const SubmitButton = () => {
    const {pending} = useFormStatus();

    return <Button
        color={"primary"}
        className={"w-full mt-5"}
        type={"submit"}
        isLoading={pending}
    >提交</Button>
}


export default function UserEmail() {
    const [refresh, setRefresh] = useState(0);
    const [messageApi, contextHolder] = message.useMessage();

    const {data, isLoading, error} = useSWR(
        `/api/rest/user/me?${refresh}`,
        (...args) => fetchApiWithAuth(...args).then(r => r.data)
    );

    const doChange = (formData) => {
        const email = formData.get("email");

        fetchApiWithAuth(`/api/rest/user/me`, {
            method: "PUT",
            body: JSON.stringify({email})
        }).then(r => {
            messageApi.open({
                type: 'success',
                content: '修改成功'
            });
        }).catch(e => {
            messageApi.open({
                type: 'error',
                content: e.message || "请求失败"
            });
        })
    }

    return (<>
        <main className={"min-h-[100vh]"}>
            {contextHolder}
            <NavigationBar/>
            <div className={"flex items-center justify-center"}>
                <div className={"w-full lg:w-[500px] bg-white m-5 rounded-md shadow-xl overflow-hidden"}>
                    <div className={"flex w-full items-center justify-center relative"}>
                        <div className={"h-full flex items-center absolute left-1"}>
                            <BackButton/>
                        </div>
                        <h1 className={"text-center font-bold text-gray-900 text-2xl flex items-center p-5"}>修改邮箱</h1>
                    </div>
                    <p className={"p-5 text-blue-600 text-sm text bg-blue-50 m-5 rounded-lg"}>
                        用户邮箱可以用于用户头像的获取。如果您已经在Gravatar官网上注册并上传头像，您可以使用相同的邮箱登录本站，即可显示您的头像。<br/>
                        <Link href={"https://cn.gravatar.com/"} className={"underline"}>点击此处前往Gravatar官网</Link>
                    </p>

                    {isLoading &&
                        <div className={"p-5 text-center"}>
                            <Spinner color={"default"}/>
                        </div>
                    }

                    {data &&
                        <form className={"p-5"} action={doChange}>
                            <Input label={"用户邮箱"}
                                   placeholder={"请输入用户邮箱"}
                                   defaultValue={data?.email}
                                   className={"mb-3"}
                                   name={"email"}
                                   isRequired={true}/>
                            <SubmitButton/>
                        </form>
                    }
                </div>
            </div>
        </main>
    </>)
}
