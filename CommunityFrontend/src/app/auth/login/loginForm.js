"use client";
import {Button, Input} from "@nextui-org/react";
import {useFormStatus, useFormState} from 'react-dom'
import {login} from "@/utils/auth";


async function formLogin(_, formData) {
    const username = formData.get("username");
    const password = formData.get("password");
    const res = await login(username, password);
    if (res.error) {
        return {
            error: res.error
        }
    }
}


function SubmitButton() {
    const {pending} = useFormStatus();
    return <Button
        color={"primary"}
        className={"w-full mt-5"}
        type={"submit"}
        isLoading={pending}
    >登录</Button>
}


export default function LoginForm({setForm}) {

    const [state, formAction] = useFormState(formLogin);

    return <form className={"p-10"} name={"loginForm"} action={formAction}>
        <p className={"font-bold text-center text-2xl mb-10"}>登录</p>
        {state?.error && <div className={"text-red-400 mb-3 text-center bg-red-50 p-2 rounded-md"}>{state?.error}</div>}
        <Input
            type={"text"}
            placeholder={"请输入用户名"}
            label={"用户名"}
            className={"mb-3"}
            name={"username"}
            isRequired
        />
        <Input
            type={"password"}
            placeholder={"请输入密码"}
            label={"密码"}
            className={"mb-3"}
            name={"password"}
            isRequired
        />
        <SubmitButton/>
        <p className={"text-gray-500 text-base text-center mt-5"}>
            还未注册？<a className={"text-blue-500 cursor-pointer"} onClick={() => {
            setForm("register")
        }}>立即注册</a>
        </p>
    </form>
}
