"use client"

import NavigationBar from "@/components/navbar";
import {Button, Input, Navbar, NavbarBrand} from "@nextui-org/react";
import Image from "next/image";

export default function Login() {
    return (<>
            <Navbar>
                <NavbarBrand>
                    <Image src={"/logo.png"} alt={"食堂点评交流社区"} width={48} height={48}
                           className={"rounded-xl mr-2.5"}/>
                    <p className="font-bold text-inherit">食堂点评交流社区</p>
                </NavbarBrand>
            </Navbar>
            <main className="flex justify-between h-full pt-10">
                <div className={"flex-1"}>
                    <h1>
                        上海理工大学 食堂点评交流社区
                    </h1>
                </div>
                <div className={"flex-1"}>
                    <h1 className={"font-bold text-center text-2xl mb-5"}>登录</h1>
                    <form>
                        <Input name={"username"} label={"用户名"} className={"mb-5"}></Input>
                        <Input name={"password"} label={"密码"} type={"password"} className={"mb-5"}></Input>
                        <Button
                            type={"submit"}
                            color={"primary"}
                            className={"w-full"}
                        >登录</Button>
                    </form>
                </div>
            </main>
        </>
    )
}