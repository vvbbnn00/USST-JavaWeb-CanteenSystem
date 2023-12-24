"use client";
import NavigationBar from "@/components/NavigationBar";
import {fetchApiWithAuth} from "@/utils/api";
import {Button, Input} from "@nextui-org/react";
import {useFormStatus} from "react-dom";
import {message} from "antd";
import BackButton from "@/components/common/BackButton";
import {logout} from "@/utils/auth";


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
    const [messageApi, contextHolder] = message.useMessage();

    const doChange = (formData) => {
        const oldPassword = formData.get("oldPassword");
        const newPassword = formData.get("newPassword");
        const confirmNewPassword = formData.get("confirmNewPassword");

        if (newPassword !== confirmNewPassword) {
            messageApi.open({
                type: 'error',
                content: '两次输入的密码不一致'
            });
            return;
        }

        fetchApiWithAuth(`/api/rest/user/me/password`, {
            method: "PUT",
            body: JSON.stringify({
                oldPassword,
                password: newPassword,
                confirmPassword: confirmNewPassword
            })
        }).then(r => {
            messageApi.open({
                type: 'success',
                content: '修改成功，请重新登录'
            });
            logout();
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
                        <h1 className={"text-center font-bold text-gray-900 text-2xl flex items-center p-5"}>修改密码</h1>
                    </div>
                    <form className={"p-5"} action={doChange}>
                        <Input label={"原密码"}
                               placeholder={"请输入原密码"}
                               className={"mb-3"}
                               name={"oldPassword"}
                               type={"password"}
                               isRequired={true}/>
                        <Input label={"新密码"}
                               placeholder={"请输入新密码"}
                               className={"mb-3"}
                               name={"newPassword"}
                               type={"password"}
                               isRequired={true}/>
                        <Input label={"确认新密码"}
                               placeholder={"请再次输入新密码"}
                               className={"mb-3"}
                               name={"confirmNewPassword"}
                               type={"password"}
                               isRequired={true}/>
                        <SubmitButton/>
                    </form>
                </div>
            </div>
        </main>
    </>)
}
