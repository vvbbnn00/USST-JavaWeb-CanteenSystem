"use client"

import {Button, Input} from "@nextui-org/react";

export default function RegisterForm({setForm}) {

    return <form className={"p-10"} name={"registerForm"}>
        <p className={"font-bold text-center text-2xl mb-10"}>注册</p>
        <Input
            type={"text"}
            label={"用户名"}
            placeholder={"用户名由字母、数字、下划线组成，2-20位"}
            className={"mb-3"}
            name={"username"}
            isRequired={true}
        />
        <Input
            type={"password"}
            label={"密码"}
            placeholder={"密码需包含字母和数字，6-20位"}
            className={"mb-3"}
            name={"password"}
            isRequired={true}
        />
        <Input
            type={"password"}
            label={"确认密码"}
            placeholder={"确认密码需与密码一致"}
            className={"mb-3"}
            name={"confirmPassword"}
            isRequired={true}
        />
        <Input
            type={"text"}
            label={"邮箱"}
            placeholder={"邮箱将决定您的头像，可在Gravatar注册"}
            className={"mb-3"}
            name={"email"}
            isRequired={true}
        />
        <Button color={"primary"} className={"w-full mt-5"} type={"submit"}>注册</Button>
        <p className={"text-gray-500 text-base text-center mt-5"}>
            已有帐号？<a className={"text-blue-500 cursor-pointer"} onClick={() => {
            setForm("login")
        }}>立即登录</a>
        </p>
    </form>
}
